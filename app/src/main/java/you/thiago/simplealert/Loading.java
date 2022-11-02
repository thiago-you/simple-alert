package you.thiago.simplealert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Locale;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class Loading {

    public static final int LENGTH_SHORT = 4000;
    public static final int LENGTH_LONG = 7000;

    private Context context;
    protected SimpleAlert dialog;

    private String message;
    private String loadingMessage;

    private boolean enableProgress;
    private boolean autoUpdateMessage;

    private Thread progressThread;
    private MessageUpdate messageUpdate;

    protected boolean isSleeper = false;

    /**
     * Private instance
     */
    private Loading() {
        // ...
    }

    private Loading(Context context) {
        this.context = context;
        dialog = new SimpleAlert(context, SimpleAlert.STYLE_LOADING).setMessage(R.string.loading);
    }

    public Loading setMessage(int stringRes) {
        return setMessage(context.getString(stringRes));
    }

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
        enableProgress = false;
        return this;
    }

    public Loading enableProgress() {
        enableProgress = true;
        dialog.showSpinnerProgress();
        return this;
    }

    public Loading disableAutoUpdate() {
        autoUpdateMessage = false;
        return this;
    }

    public Loading enableAutoUpdate() {
        autoUpdateMessage = true;
        return this;
    }

    public Loading initProgress(int actualProgress) {
        startProgressThread(this, actualProgress, 100);
        return this;
    }

    public Loading setDelay(int milliDelay) {
        return initProgress(getProgressFromDelay(milliDelay));
    }

    public int getProgressFromDelay(int milliDelay) {
        if (milliDelay >= 10000) {
            return 0;
        }
        if (milliDelay <= 0) {
            return 90;
        }

        double maxProgress = 100.00;
        double progress = (maxProgress - ((((double) milliDelay) / 10000) * maxProgress));

        if (progress <= 0) {
            progress = 90;
        }

        return (int) progress;
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
        dialog.setOnCancelListener(cancelListener);
        return this;
    }

    public Loading setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        return setDismissListener(99, dismissListener);
    }

    public Loading setDismissListener(int fromProgress, DialogInterface.OnDismissListener dismissListener) {
        updateProgress(fromProgress, 100);

        dialog.setDismissListener(dismissListener);
        return this;
    }

    public Loading setFutureDismissListener(DialogInterface.OnDismissListener dismissListener) {
        dialog.setDismissListener(dismissListener);
        return this;
    }

    public Loading preventDismiss() {
        dialog.preventDismiss();
        return this;
    }

    public Loading setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public Loading setLightMode() {
        dialog.setLightMode();
        return this;
    }

    public Loading setDarkMode() {
        dialog.setDarkMode();
        return this;
    }

    public Loading setTextColor(int colorId) {
        dialog.setTextColor(colorId);
        return this;
    }

    public Loading setBackgroundColor(int color) {
        dialog.setBackgroundColor(color);
        return this;
    }

    public Loading setBackground(Drawable background) {
        dialog.setBackground(background);
        return this;
    }

    public Loading setDismissTimeout(int dismissTime) {
        dialog.setDismissTimeout(dismissTime);
        return this;
    }

    public Loading setMessageUpdate(MessageUpdate messageUpdate) {
        this.messageUpdate = messageUpdate;
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
        String message = this.message != null ? this.message : "";

        if (messageUpdate != null) {
            message = messageUpdate.getMessage(progress);
        } else if (autoUpdateMessage) {
            if (progress >= 100) {
                message = context.getString(R.string.finishing_status);
            } else if (progress >= 90) {
                message = context.getString(R.string.saving_status);
            }  else if (progress >= 80) {
                message = context.getString(R.string.processing_return_status);
            } else if (progress >= 30) {
                message = context.getString(R.string.sending_status);
            } else if (progress >= 10) {
                message = context.getString(R.string.processing_status);
            } else if (progress >= 0) {
                message = context.getString(R.string.initial_status);
            }
        }

        if (this instanceof Spinner) {
            setMessage(message);

            if (enableProgress) {
                dialog.setProgress(progress + "%");
            }
        } else {
            if (enableProgress) {
                if (loadingMessage == null) {
                    loadingMessage = message;
                }

                setMessage(String.format(Locale.getDefault(), "%d%% %s", progress, loadingMessage));
            } else {
                setMessage(message);
            }
        }
    }
    /**
     * Stop Thread to update progress
     */
    public void stopProgressThread() {
        if (progressThread != null) {
            progressThread.interrupt();
        }
    }

    /**
     * Thread to update progress
     */
    private void startProgressThread(final Loading loading, final int actualProgress, final int maxProgress) {
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

                            loading.setProgress(i);

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

        progressThread.start();
    }

    /**
     * Loading with dialog style
     */
    public static class Dialog extends Loading {
        public Dialog(Context context) {
            super(context);

            dialog = new SimpleAlert(context, SimpleAlert.STYLE_LOADING)
                    .setLightMode()
                    .preventDismiss()
                    .setMessage(R.string.loading);
        }

        public Dialog(Context context, int delay) {
            super(context);

            dialog = new SimpleAlert(context, SimpleAlert.STYLE_LOADING)
                    .setLightMode()
                    .preventDismiss()
                    .setMessage(R.string.loading);

            setDelay(delay);
        }
    }

    /**
     * Loading with Spinner style
     */
    public static class Spinner extends Loading {
        public Spinner(Context context) {
            super(context);

            dialog = new SimpleAlert(context, SimpleAlert.STYLE_SPINNER_LOADING)
                    .setDarkMode()
                    .preventDismiss();
        }

        public Spinner(Context context, int delay) {
            super(context);

            dialog = new SimpleAlert(context, SimpleAlert.STYLE_SPINNER_LOADING)
                    .setDarkMode()
                    .preventDismiss();

            setDelay(delay);
        }
    }

    /**
     * Loading invisible with only progress thread
     */
    public static class Sleeper extends Loading {

        private Runnable action;

        public Sleeper(Context context) {
            super(context);
            isSleeper = true;
        }

        public Sleeper init(int progress) {
            initProgress(progress);
            return this;
        }

        public Sleeper setAction(@Nullable Runnable action) {
            this.action = action;
            return this;
        }

        @Override
        public void dismiss() {
            if (dialog.getContext() instanceof Activity) {
                ((Activity) dialog.getContext()).runOnUiThread(action);
            } else {
                new Handler(Looper.getMainLooper()).post(action);
            }
        }
    }

    public interface MessageUpdate {
        String getMessage(int progress);
    }
}
