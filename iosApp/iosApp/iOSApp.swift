import SwiftUI
import Shared
import GoogleMobileAds

@main
struct iOSApp: App {
    init() {
        MobileAds.shared.start(completionHandler: nil)

        AdViewFactory().createAdView = {
            let banner = BannerView(adSize: AdSizeBanner)
            
            // Determine if we are in debug mode
            let isDebug: Bool
            #if DEBUG
            isDebug = true
            #else
            isDebug = false
            #endif
            
            // Use the shared logic from Kotlin
            banner.adUnitID = AdConfig.shared.getBannerAdUnitId(platformType: .ios, isDebug: isDebug)

            // Helper to find the root view controller for ad clicks
            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let rootVC = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController {
                banner.rootViewController = rootVC
            }

            banner.load(Request())
            return banner
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
