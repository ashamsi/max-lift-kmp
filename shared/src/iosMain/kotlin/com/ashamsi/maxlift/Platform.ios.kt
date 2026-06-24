package com.ashamsi.maxlift

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val type: PlatformType = PlatformType.IOS
    override val isDebug: Boolean = NSBundle.mainBundle.bundleIdentifier?.endsWith(".debug") == true || 
                                     NSBundle.mainBundle.pathForResource("DEBUG", "txt") != null
}

actual fun getPlatform(): Platform = IOSPlatform()