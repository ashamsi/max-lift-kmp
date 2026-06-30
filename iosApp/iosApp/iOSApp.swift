import SwiftUI
import Shared
import GoogleMobileAds
import os.log

private let adLog = Logger(subsystem: Bundle.main.bundleIdentifier ?? "com.arturshamsi.maxlift", category: "MaxLiftAds")

/// Bridges AdMob `BannerViewDelegate` callbacks to the shared Compose layer so it can
/// react to load/failure events (e.g. collapse the banner when an ad fails).
///
/// A delegate instance is created per banner and retained via an associated object on the
/// banner itself (see `AdBannerLoader`), so its callbacks stay valid for the banner's life.
private final class AdBannerDelegate: NSObject, BannerViewDelegate {
    private let listener: AdBannerListener

    init(listener: AdBannerListener) {
        self.listener = listener
    }

    func bannerViewDidReceiveAd(_ bannerView: BannerView) {
        adLog.info("Banner loaded. unit=\(bannerView.adUnitID ?? "nil", privacy: .public)")
        listener.onAdLoaded()
    }

    func bannerView(_ bannerView: BannerView, didFailToReceiveAdWithError error: Error) {
        adLog.error(
            "Banner failed. unit=\(bannerView.adUnitID ?? "nil", privacy: .public), error=\(error.localizedDescription, privacy: .public)"
        )
        listener.onAdFailedToLoad()
    }
}

private enum AdBannerLoader {
    private static var isSdkStarted = false
    private static var pendingLoad: (() -> Void)?
    private static var delegateKey: UInt8 = 0

    static func configureFactory() {
        AdViewFactory().createAdView = { listener in
            let banner = BannerView(adSize: AdSizeBanner)

            #if DEBUG
            let isDebug = true
            #else
            let isDebug = false
            #endif

            let adUnitId = AdConfig.shared.getBannerAdUnitId(platformType: .ios, isDebug: isDebug)
            banner.adUnitID = adUnitId

            let delegate = AdBannerDelegate(listener: listener)
            banner.delegate = delegate
            // Keep the delegate alive for as long as the banner exists.
            objc_setAssociatedObject(banner, &delegateKey, delegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)

            adLog.info("Requesting banner. isDebug=\(isDebug, privacy: .public), adUnitId=\(adUnitId, privacy: .public)")

            scheduleLoad(for: banner)
            return banner
        }
    }

    static func startSdk() {
        MobileAds.shared.start { _ in
            isSdkStarted = true
            adLog.info("Mobile Ads SDK started")
            pendingLoad?()
            pendingLoad = nil
        }
    }

    private static func scheduleLoad(for banner: BannerView) {
        let load = { loadBanner(banner) }
        if isSdkStarted {
            load()
        } else {
            pendingLoad = load
        }
    }

    private static func loadBanner(_ banner: BannerView) {
        func attemptLoad(retry: Bool) {
            if let rootVC = rootViewController() {
                banner.rootViewController = rootVC
                banner.load(Request())
            } else if retry {
                adLog.warning("rootViewController nil; retrying banner load on next run loop")
                DispatchQueue.main.async {
                    attemptLoad(retry: false)
                }
            } else {
                adLog.error("rootViewController still nil; banner load skipped")
            }
        }
        attemptLoad(retry: true)
    }

    private static func rootViewController() -> UIViewController? {
        let scenes = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
        let window = scenes.flatMap(\.windows).first(where: \.isKeyWindow)
            ?? scenes.flatMap(\.windows).first
        var root = window?.rootViewController
        while let presented = root?.presentedViewController {
            root = presented
        }
        return root
    }
}

@main
struct iOSApp: App {
    init() {
        AdBannerLoader.configureFactory()
        AdBannerLoader.startSdk()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
