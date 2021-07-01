package com.msabhi.dryrun


//import platform.UIKit.UIDevice
import platform.Foundation.NSUUID

actual class Platform actual constructor() {
    actual val platform: String =
        NSUUID().UUIDString()  //UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}