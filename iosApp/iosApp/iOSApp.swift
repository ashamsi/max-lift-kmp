import SwiftUI
import Shared
import GoogleMobileAds
import os.log

private let adLog = Logger(subsystem: Bundle.main.bundleIdentifier ?? "com.arturshamsi.maxlift", category: "MaxLiftAds")

/// Retained for the lifetime of the app so BannerViewDelegate callbacks stay valid.
private final class AdBannerDelegate: NSObject, BannerViewDelegate {
    func bannerViewDidReceiveAd(_ bannerView: BannerView) {
        adLog.info("Banner loaded. unit=\(bannerView.adUnitID ?? "nil", privacy: .public)")
    }

    func bannerView(_ bannerView: BannerView, didFailToReceiveAdWithError error: Error) {
        adLog.error(
            "Banner failed. unit=\(bannerView.adUnitID ?? "nil", privacy: .public), error=\(error.localizedDescription, privacy: .public)"
        )
    }
}

private enum AdBannerLoader {
    private static let delegate = AdBannerDelegate()
    private static var isSdkStarted = false
    private static var pendingLoad: (() -> Void)?

    static func configureFactory() {
        AdViewFactory().createAdView = {
            let banner = BannerView(adSize: AdSizeBanner)

            #if DEBUG
            let isDebug = true
            #else
            let isDebug = false
            #endif

            let adUnitId = AdConfig.shared.getBannerAdUnitId(platformType: .ios, isDebug: isDebug)
            banner.adUnitID = adUnitId
            banner.delegate = delegate

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
