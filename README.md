# Simple Alert
An easy and useful alert dialog. The library has a lot of simple and custom features, such as actions, callback, logs, loadings and can even prevent user from dismissing the dialog or clicking away.

Loadings (dialog or spinner) can implements a dismiss callback listener, that will execute only after the dialog dismiss.

Alerts can use normal, clean and system style and show cancel button when needed. All texts, titles, button titles and actions can be customized. Layout can be overridden to use your own requirements.

### SAMPLE
![info alert](sample/alert-5.jpg?raw=true "Info Alert") ![error alert](sample/alert-7.jpg?raw=true "Error Alert")

![dialog loading](sample/alert-2.jpg?raw=true "Dialog Loading") ![spinner loading](sample/alert-6.jpg?raw=true "Spinner Loading")

More samples can be found into /sample folder.

### USAGE DEMO
    // Show simple alert:
    new SimpleAlert(context)
        .setMessage("Hi, i'm a simple and pretty alert!")
        .show();

    // show dialog loading
    new Loading.Dialog(MainActivity.this)
        .setStaticMessage("Loading...")
        .updateProgress(0, 100)
        .show();

    // show spinner loading
    new Loading.Spinner(MainActivity.this)
        .setStaticMessage("Loading...")
        .updateProgress(0, 100)
        .show();

    // show logs and confirm listener
    new SimpleAlert(MainActivity.this)
        .setType(SimpleAlert.ERROR)
        .setMessage("Hi, i'm showing error into a pretty way!")
        .setExtras(Log.getStackTraceString(e)) // or your log string
        .setConfirmClickListener(new SimpleAlert.OnSimpleAlertClickListener() {
            @Override
            public void onClick(SimpleAlert simpleAlert) {
                // your code
                simpleAlert.dismiss();
            }
        })
        .show();

### Requirements
    Min SDK Version >= 19

### Import library from Jitpack
    - Add Jitpack repository into you project (build.gradle):

        allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
        }

    - Add library implementation into build.gradle (Module:app)

        dependencies {
            ...
            implementation 'com.github.thiago-you:simple-alert:Tag'
        }

    - Sync build.gradle and build your project

See [Jitpack](https://jitpack.io/docs/) docs for more info.

### Download Library
Follow these steps to import the library into your project:

    - Download the library
    - Go to you project under "File" -> "New" -> "Import Module"
    - In build.gradle, import library as "implementation project(':simple-alert')"
    - Sync build.gradle and build your project