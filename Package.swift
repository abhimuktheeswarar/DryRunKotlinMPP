// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "dryrun",
    platforms: [.iOS(.v11), [.watchOS(.v4), .tvOS(.v11), .macOS(.v10.13)],
    products: [
        .library(
            name: "dryrun",
            targets: ["dryrun"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "dryrun",
            path: "dryrun/xcframework/dryrun.xcframework"
        ),
    ]
)
