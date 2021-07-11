// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "Dryrun",
    platforms: [.iOS("11"), .watchOS("4"), .tvOS("11"), .macOS("10.13"),],
    products: [
        .library(
            name: "Dryrun",
            targets: ["dryrun"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "Dryrun",
            path: "dryrun/xcframework/dryrun.xcframework"
        ),
    ]
)
