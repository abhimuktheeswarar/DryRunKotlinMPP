Pod::Spec.new do |spec|
    spec.name                     = 'dryrun'
    spec.version                  = '1.0.2-RC'
    spec.homepage                 = 'https://github.com/abhimuktheeswarar/DryRunKotlinMPP'
    spec.source                   = { :git => "https://github.com/abhimuktheeswarar/DryRunKotlinMPP.git", :tag => "v#{spec.version}" }
    spec.authors                  = 'Abhi Muktheeswarar'
    spec.license                  = 'The Apache Software License, Version 2.0'
    spec.summary                  = 'DryRunKotlinMPP Kotlin/Native module CocoaPods'
    spec.vendored_frameworks      = 'Volumes/Code/Kotlin/DryRunKotlinMPP/dryrun/xcframework/dryrun.xcframework'
    spec.static_framework         = true
    spec.libraries                = "c++"
    spec.module_name              = "#{spec.name}_umbrella"

end