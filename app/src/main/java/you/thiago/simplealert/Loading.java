package you.thiago.simplealert;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Locale;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class Loading {

    protected Activity activity;
    protected SimpleAlert dialog;

    protected String message;
    private String staticMessage;
    private boolean disableProgress;

    private Thread progressThread;

    /**
     * Private instance
     */
    private Loading() {

    }

    private Loading(Activity activity) {
        this.activity = activity;

        /* create alert instance */
        dialog = new SimpleAlert(activity, SimpleAlert.STYLE_LOADING).setMessage(activity.getString(R.string.loading));
    }

    /**
     * Set a static dialog message
     */
    public Loading setStaticMessage(String message) {
        if (message != null && (this.message == null || !this.message.equals(message))) {
            dialog.setMessage(message);
        }

        staticMessage = message;
        return this;
    }

    /**
     * Update dialog message if has not static msg set
     */
    public Loading setMessage(String message) {
        if (message != null && (this.message == null || !this.message.equals(message))) {
            dialog.setMessage(message);
        }

        this.message = message;
        return this;
    }

    private void setProgress(int progress) {
        updateLoadingMessage(progress);
    }

    public Loading disableProgress() {
        disableProgress = true;
        dialog.hideSpinnerProgress();
        return this;
    }

    public Loading updateProgress(int actualProgress, int maxProgress) {
        startProgressThread(this, actualProgress, maxProgress);
        return this;
    }

    public Loading updateProgress(Integer... progress) {
        if (progress.length > 1) {
            startProgressThread(this, progress[0], progress[1]);
        } else {
            startProgressThread(this, progress[0], progress[0]);
        }

        return this;
    }

    public Loading setCancelListener(DialogInterface.OnCancelListener cancelListener) {
        dialog.setCancelListener(cancelListener);
        return this;
    }

    public Loading setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        /* set finish progress */
        updateProgress(99, 100);

        dialog.setDismissListener(dismissListener);
        return this;
    }

    public Loading setFutureDismissListener(DialogInterface.OnDismissListener dismissListener) {
        dialog.setDismissListener(dismissListener);
        return this;
    }

    public Loading setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public Loading setLightColor() {
        dialog.setLightColor();
        return this;
    }

    public Loading setDarkColor() {
        dialog.setDarkColor();
        return this;
    }

    public Loading setTextColor(int colorId) {
        dialog.setTextColor(colorId);
        return this;
    }

    public Loading setBackgroundColor(Drawable background) {
        dialog.setBackgroundColor(background);
        return this;
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public Loading show() {
        dialog.show();
        return this;
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void updateLoadingMessage(int progress) {
        String message = "";
        if (staticMessage != null) {
            message = staticMessage;
        } else {
            if (progress >= 100) {
                message = activity.getString(R.string.finishing_status);
            } else if (progress >= 90) {
                message = activity.getString(R.string.saving_status);
            }  else if (progress >= 80) {
                message = activity.getString(R.string.processing_return_status);
            } else if (progress >= 30) {
                message = activity.getString(R.string.sending_status);
            } else if (progress >= 10) {
                message = activity.getString(R.string.processing_status);
            } else if (progress >= 0) {
                message = activity.getString(R.string.initial_status);
            }
        }

        if (this instanceof Spinner) {
            setMessage(message);

            if (!disableProgress) {
                dialog.setProgress(progress + "%");
            }
        } else {
            if (disableProgress) {
                setMessage(message);
            } else {
                setMessage(String.format(Locale.getDefault(), "%d%% %s", progress, message));
            }
        }
    }

    /**
     * Thread to update progress
     */
    private void startProgressThread(final Loading loading, final int actualProgress, final int maxProgress) {
        /* stop old thread */
        if (progressThread != null) {
            progressThread.interrupt();
        }

        /* create runnable thread to update progress */
        progressThread = new Thread() {
            @Override
            public void run() {
                if (actualProgress >= maxProgress) {
                    loading.dismiss();
                } else {
                    for (int i = actualProgress; i <= maxProgress; i++) {
                        try {
                            if (Thread.interrupted()) {
                                return;
                            }

                            /* update dialog progress in UI Thread */
                            final int progress = i;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loading.setProgress(progress);
                                }
                            });

                            /* wait for interface animations */
                            Thread.sleep(100);

                            if (i >= 100) {
                                loading.dismiss();
                                return;
                            }
                        } catch (InterruptedException e) {
                            return;
                        } catch (Exception e) {
                            loading.dismiss();
                            Log.e(getClass().getSimpleName(), e.getMessage(), e);
                        }
                    }
                }
            }
        };

        /* run thread */
        progressThread.start();
    }

    /**
     * Loading with dialog style
     */
    public static class Dialog extends Loading {

        public Dialog(Activity activity) {
            super(activity);

            /* create loading instance */
            dialog = new SimpleAlert(activity, SimpleAlert.STYLE_LOADING).setMessage(activity.getString(R.string.loading));
        }
    }

    /**
     * Loading with Spinner style
     */
    public static class Spinner extends Loading {

        private int progress;

        public Spinner(Activity activity) {
            super(activity);

            /* create loading instance */
            dialog = new SimpleAlert(activity, SimpleAlert.STYLE_SPINNER_LOADING);

            /* config default spinner color to light */
            dialog.setLightColor();
        }
    }
}
