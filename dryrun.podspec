Pod::Spec.new do |spec|
    spec.name                     = 'dryrun'
    spec.version                  = '1.0.6-RC'
    spec.homepage                 = 'https://github.com/abhimuktheeswarar/DryRunKotlinMPP'
    spec.source                   = { :git => "https://github.com/abhimuktheeswarar/DryRunKotlinMPP.git", :tag => "v#{spec.version}" }
    spec.authors                  = 'Abhi Muktheeswarar'
    spec.license                  = 'The Apache Software License, Version 2.0'
    spec.summary                  = 'DryRunKotlinMPP Kotlin/Native module CocoaPods'
    spec.vendored_frameworks      = 'dryrun/xcframework/dryrun.xcframework'
    spec.static_framework         = true
    spec.libraries                = "c++"
    spec.module_name              = "#{spec.name}_umbrella"

    spec.platforms           	= { :ios => '11', :watchos => '4', :tvos => '11', :osx => '10.13' }

    spec.pod_target_xcconfig 	= { 'ONLY_ACTIVE_ARCH' => 'YES' }

end