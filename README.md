# Building Hover Tester for use with your own Hover account:

1. On your Hover dashboard create a new app. Name it whatever you want, but the package name must be `com.hover.tester.X` where X is your choice, probably your organization name or similar.
2. Import this project into Android Studio. Open the app level build.gradle file. Under the `demo` productFlavor change `applicationIdSuffix` from "demo" to X from step 1. (Leave everything else as "demo")
3. Open the `app/src/demo/Android Manifest.xml` and change the value of the Hover ApiKey meta-data to the API Key from your Hover dashboard for the `com.hover.tester.X` app you created in step 1.
4. Find the Build Variants panel in Android Studio and choose "demoRelease", then you can run it on your testing phone or build the apk.

If everything is correct then once permissions have been granted in the app, the "Add integration" button should show up and clicking it will show any actions you have created in the Hover dashboard.
