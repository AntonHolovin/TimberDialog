# TimberDialog

Simple dialog which lets to check the logs from [Timber](https://github.com/JakeWharton/timber). Logs can be shared as `.log` file.

![Demo] (art/demo.gif)

### Plant a tree
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

### Add provider to AndroidManifest.xml to support API 24+
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