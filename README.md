# TimberDialog
[![](https://jitpack.io/v/antonygolovin/timberdialog.svg)](https://jitpack.io/#antonygolovin/timberdialog)

Simple dialog which lets to check the logs from [Timber](https://github.com/JakeWharton/timber). Logs can be shared as `.log` file.

Based on the dialog from [u2020](https://github.com/JakeWharton/u2020) app.

![Demo](art/demo.gif)

## Add dependency
Add it in your root `build.gradle`:
``` xml
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}
```

Add the dependency:
``` xml
dependencies {
    debugImplementation 'com.github.antonygolovin:timberdialog:0.1'
}
```

## Plant a tree
Use `LumberYard` class:
``` kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        LumberYard.getInstance(this).let {
            it.cleanUp()
            Timber.plant(it.tree())
        }
    }
}
```

## Edit app's AndroidManifest.xml
Add provider inside `<application>` node:
``` xml
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
    </provider>
```

Add `res/xml/file_paths.xml:`
``` xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path
        name="logs"
        path="/" />
</paths>
```