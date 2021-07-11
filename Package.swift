// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "dryrun",
    platforms: [.iOS("11"), .watchOS("4"), .tvOS("11"), .macOS("10.13"),],
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