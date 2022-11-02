package you.thiago.simplealert;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused WeakerAccess UnusedReturnValue")
public class SimpleAlert implements View.OnClickListener {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int LIGHT = 4;
    public static final int DARK = 5;

    public static final int STYLE_MINIMALIST = 1;
    public static final int STYLE_SYSTEM = 2;
    public static final int STYLE_LOADING = 3;
    public static final int STYLE_SPINNER_LOADING = 4;

    public static final int LENGTH_SHORT = 4000;
    public static final int LENGTH_LONG = 7000;

    /**
     * Dialog component
     */
    private Dialog dialog;

    private final Context context;

    private OnSimpleAlertClickListener confirmClickListener;
    private OnSimpleAlertClickListener cancelClickListener;

    private DialogInterface.OnCancelListener cancelListener;

    private String title, btnConfirmTitle, btnCancelTitle;
    private final int alertStyle;
    private int alertType, dismissTime;
    private boolean cancelOnTouchOutside, autoDismiss, showCancelButton, showProgress;

    private ImageView titleIcon;
    private Button btnConfirm, btnCancel, btnShowExtras;

    protected View dialogView, lytTitle, viewDivider;
    protected TextView txtTitle, txtMessage, txtExtras, txtProgress;

    private boolean isConfirmBtnSizeUpdated, isCancelBtnSizeUpdated;

    /**
     * Click listener
     */
    public interface OnSimpleAlertClickListener {
        void onClick(SimpleAlert simpleAlert);
    }

    public SimpleAlert(Context context) {
        this(context, SimpleAlert.STYLE_MINIMALIST);
    }

    public SimpleAlert(Context context, int alertStyle) {
        this.context = context;
        this.alertStyle = alertStyle;
        alertType = SimpleAlert.LIGHT;

        runOnUi(this::setupComponent);
    }

    /**
     * Setup dialog component
     */
    private void setupComponent() {
        dialog = new Dialog(context, R.style.AlertDialog);
        dialog.setCanceledOnTouchOutside(false);

        if (alertStyle == SimpleAlert.STYLE_LOADING || alertStyle == SimpleAlert.STYLE_SPINNER_LOADING) {
            if (alertStyle == SimpleAlert.STYLE_SPINNER_LOADING) {
                dialog.setContentView(R.layout.alert_loading_spinner);
                txtProgress = dialog.findViewById(R.id.txtProgress);
            } else {
                dialog.setContentView(R.layout.alert_loading);
            }

            dialog.setCancelable(false);
        } else {
            if (alertStyle == SimpleAlert.STYLE_SYSTEM) {
                dialog.setContentView(R.layout.alert_system);
            } else {
                dialog.setContentView(R.layout.alert);
                lytTitle = dialog.findViewById(R.id.lytTitle);
            }

            dialog.setCancelable(true);

            titleIcon = dialog.findViewById(R.id.imgIcon);
            txtTitle = dialog.findViewById(R.id.txtTitle);
            txtExtras = dialog.findViewById(R.id.txtExtras);
            viewDivider = dialog.findViewById(R.id.viewDivider);
            btnConfirm = dialog.findViewById(R.id.btnConfirm);
            btnCancel = dialog.findViewById(R.id.btnCancel);
            btnShowExtras = dialog.findViewById(R.id.btnShowExtras);

            btnConfirm.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnShowExtras.setOnClickListener(this);
        }

        dialogView = dialog.findViewById(R.id.alertDialog);
        txtMessage = dialog.findViewById(R.id.txtMessage);
    }

    /**
     * Config component behavior
     */
    private void configComponent() {
        if (alertStyle == SimpleAlert.STYLE_LOADING || alertStyle == SimpleAlert.STYLE_SPINNER_LOADING) {
            if (cancelListener != null) {
                dialog.setCancelable(true);
            }

            if (alertStyle == SimpleAlert.STYLE_SPINNER_LOADING) {
                if (showProgress) {
                    txtProgress.setVisibility(View.VISIBLE);
                } else {
                    txtProgress.setVisibility(View.GONE);
                }
            }
        } else {
            if (alertStyle == SimpleAlert.STYLE_SYSTEM) {
                configSystemAlertStyle();
            } else {
                configMinimalistAlertStyle();
            }

            if (!showCancelButton && btnCancelTitle == null) {
                btnCancel.setVisibility(View.GONE);
            }

            if (btnConfirmTitle == null) {
                if (alertType == SimpleAlert.ERROR && cancelClickListener == null) {
                    setBtnConfirmTitle(context.getString(R.string.default_confirm_error_btn_title));
                }
            }

            if (cancelClickListener != null) {
                btnCancel.setVisibility(View.VISIBLE);
            }

            if (autoDismiss) {
                if (btnConfirmTitle == null) {
                    btnConfirm.setVisibility(View.GONE);
                }
                if (btnCancelTitle != null) {
                    btnCancel.setVisibility(View.GONE);
                }
            }

            if (btnConfirm.getVisibility() != View.VISIBLE && btnCancel.getVisibility() != View.VISIBLE) {
                dialogView.setOnClickListener(v -> dialog.dismiss());
            }
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
    }

    public Context getContext() {
        return context;
    }

    public SimpleAlert setTitle(int stringRes) {
        return setTitle(context.getString(stringRes));
    }

    public SimpleAlert setTitle(@Nullable String title) {
        this.title = title;

        runOnUi(() -> {
            if (txtTitle != null) {
                txtTitle.setText(title);
            }
        });

        return this;
    }

    public SimpleAlert setMessage(int stringRes) {
        return setMessage(context.getString(stringRes));
    }

    public SimpleAlert setMessage(@Nullable String message) {
        runOnUi(() -> {
            if (txtMessage != null) {
                txtMessage.setText(message);
            }
        });

        return this;
    }

    public SimpleAlert setBtnConfirmTitle(int stringRes) {
        return setBtnConfirmTitle(context.getString(stringRes));
    }

    public SimpleAlert setBtnConfirmTitle(String btnConfirmTitle) {
        this.btnConfirmTitle = btnConfirmTitle;

        runOnUi(() -> {
            if (btnConfirm != null) {
                btnConfirm.setText(btnConfirmTitle);
                updateButtonTextSize();
            }
        });

        return this;
    }

    public SimpleAlert setBtnCancelTitle(int stringRes) {
        return setBtnCancelTitle(context.getString(stringRes));
    }

    public SimpleAlert setBtnCancelTitle(String btnCancelTitle) {
        this.btnCancelTitle = btnCancelTitle;

        runOnUi(() -> {
            if (btnCancel != null) {
                btnCancel.setText(btnCancelTitle);
                btnCancel.setVisibility(View.VISIBLE);
                updateButtonTextSize();
            }
        });

        return this;
    }

    public SimpleAlert setProgress(String progress) {
        runOnUi(() -> {
            if (txtProgress != null) {
                txtProgress.setText(progress);
            }
        });

        return this;
    }

    public SimpleAlert setExtras(String extra) {
        runOnUi(() -> {
            if (txtExtras != null && extra != null && extra.length() > 0) {
                txtExtras.setText(extra);

                btnShowExtras.setVisibility(View.VISIBLE);
                btnShowExtras.setText(HtmlCompat.fromHtml(context.getString(R.string.fmt_btn_show_extras), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
        });

        return this;
    }

    public SimpleAlert setErrorStack(String extra) {
        runOnUi(() -> {
            if (txtExtras != null && extra != null && extra.length() > 0) {
                setType(SimpleAlert.ERROR);
                txtExtras.setText(extra);

                btnShowExtras.setVisibility(View.VISIBLE);
                btnShowExtras.setText(HtmlCompat.fromHtml(context.getString(R.string.fmt_btn_show_extras), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
        });

        return this;
    }

    public SimpleAlert showCancel() {
        showCancelButton = true;
        return this;
    }

    public SimpleAlert setConfirmClickListener(@Nullable final OnSimpleAlertClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
        return this;
    }

    public SimpleAlert setCancelClickListener(@Nullable final OnSimpleAlertClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;

        runOnUi(() -> {
            if (btnCancel != null) {
                if (cancelClickListener != null) {
                    btnCancel.setVisibility(View.VISIBLE);
                } else {
                    btnCancel.setVisibility(View.GONE);
                }
            }
        });

        return this;
    }

    public SimpleAlert setRequiredConfirm(@Nullable final Common.OnClickListener confirmListener) {
        if (confirmListener == null) {
            return this;
        }

        this.preventDismiss();

        return setConfirmClickListener(dialog -> {
            confirmListener.onClick();

            dialog.dismiss();
        });
    }

    public SimpleAlert setConfirmListener(@Nullable final Common.OnClickListener confirmListener) {
        return setConfirmClickListener(dialog -> {
            if (confirmListener != null) {
                confirmListener.onClick();
            }

            dialog.dismiss();
        });
    }

    public SimpleAlert setCancelListener(@Nullable final Common.OnClickListener cancelListener) {
        return setCancelClickListener(dialog -> {
            if (cancelListener != null) {
                cancelListener.onClick();
            }

            dialog.dismiss();
        });
    }

    public SimpleAlert setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;

        runOnUi(() -> dialog.setOnCancelListener(cancelListener));

        return this;
    }

    public SimpleAlert setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        runOnUi(() -> dialog.setOnDismissListener(dismissListener));
        return this;
    }

    public SimpleAlert setType(int alertType) {
        this.alertType = alertType;
        return this;
    }

    public SimpleAlert setCanceledOnTouchOutside(boolean cancel) {
        runOnUi(() -> {
            dialog.setCanceledOnTouchOutside(cancel);

            if (cancel) {
                dialog.setCancelable(true);
            }
        });

        return this;
    }

    public SimpleAlert setCancelable(boolean cancel) {
        runOnUi(() -> dialog.setCancelable(cancel));
        return this;
    }

    public SimpleAlert preventDismiss() {
        return setCancelable(false);
    }

    public SimpleAlert setLightMode() {
        runOnUi(() -> {
            if (txtTitle != null) {
                txtTitle.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
            }

            if (txtMessage != null) {
                txtMessage.setTextColor(ContextCompat.getColor(context, R.color.text));
            }

            if (txtProgress != null) {
                txtProgress.setTextColor(ContextCompat.getColor(context, R.color.text));
            }

            if (alertStyle == SimpleAlert.STYLE_LOADING && dialogView != null) {
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_light));
            }
        });

        return this;
    }

    public SimpleAlert setDarkMode() {
        runOnUi(() -> {
            if (txtTitle != null) {
                txtTitle.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }

            if (txtMessage != null) {
                txtMessage.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }

            if (txtProgress != null) {
                txtProgress.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }

            if (alertStyle == SimpleAlert.STYLE_LOADING && dialogView != null) {
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_dark));
            }
        });

        return this;
    }

    public SimpleAlert setTextColor(int colorId) {
        runOnUi(() -> {
            if (txtMessage != null) {
                txtMessage.setTextColor(colorId);
            }

            if (txtProgress != null) {
                txtProgress.setTextColor(colorId);
            }
        });

        return this;
    }

    public SimpleAlert setBackgroundColor(int color) {
        runOnUi(() -> {
            if (dialogView != null) {
                dialogView.setBackgroundColor(ContextCompat.getColor(context, color));
            }
        });

        return this;
    }

    public SimpleAlert setBackground(Drawable background) {
        runOnUi(() -> {
            if (dialogView != null) {
                dialogView.setBackground(background);
            }
        });

        return this;
    }

    protected SimpleAlert showSpinnerProgress() {
        showProgress = true;
        return this;
    }

    private void updateButtonTextSize() {
        boolean updateSize = btnConfirmTitle != null && btnConfirmTitle.length() > 18;

        if (btnCancelTitle != null && btnCancelTitle.length() > 18) {
            updateSize = true;
        }

        if (updateSize) {
            if (!isConfirmBtnSizeUpdated && btnConfirm != null) {
                isConfirmBtnSizeUpdated = true;
                btnConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }

            if (!isCancelBtnSizeUpdated && btnCancel != null) {
                isCancelBtnSizeUpdated = true;
                btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
        }
    }

    private void configMinimalistAlertStyle() {
        switch (alertType) {
            case SimpleAlert.SUCCESS: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_success_32dp));
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_success));
                break;
            }
            case SimpleAlert.ERROR: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_error_32dp));
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_error));
                break;
            }
            case SimpleAlert.INFO: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_info_32dp));
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_info));
                break;
            }
            case SimpleAlert.WARNING: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_warning_32dp));
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_warning));
                break;
            }
            case SimpleAlert.LIGHT: {
                titleIcon.setImageDrawable(null);
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_light));
                break;
            }
            case SimpleAlert.DARK: {
                titleIcon.setImageDrawable(null);
                dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_dark));
                break;
            }
        }

        if (alertType == SimpleAlert.LIGHT) {
            if (txtTitle != null) {
                txtTitle.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
            }
            if (btnShowExtras != null) {
                btnShowExtras.setTextColor(ContextCompat.getColor(context, R.color.text));
            }
            if (btnConfirm != null) {
                btnConfirm.setTextColor(ContextCompat.getColor(context, R.color.alert_btn));
            }
            if (btnCancel != null) {
                btnCancel.setTextColor(ContextCompat.getColor(context, R.color.alert_btn));
            }
        } else {
            if (txtTitle != null) {
                txtTitle.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }
            if (txtMessage != null) {
                txtMessage.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }
            if (txtExtras != null) {
                txtExtras.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }
            if (viewDivider != null) {
                viewDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.text_white));
            }
            if (btnShowExtras != null) {
                btnShowExtras.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }
            if (btnConfirm != null) {
                btnConfirm.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }
            if (btnCancel != null) {
                btnCancel.setTextColor(ContextCompat.getColor(context, R.color.text_white));
            }
        }

        if (title == null) {
            if (alertType == SimpleAlert.LIGHT || alertType == SimpleAlert.DARK) {
                title = context.getString(R.string.attention);

                if (titleIcon != null) {
                    titleIcon.setVisibility(View.GONE);
                }
            } else if (alertType == SimpleAlert.SUCCESS) {
                title = context.getString(R.string.success);
            } else {
                title = context.getString(R.string.attention);
            }
        }

        if (txtTitle != null) {
            txtTitle.setText(title);
        }
    }

    private void configSystemAlertStyle() {
        switch (alertType) {
            case SimpleAlert.SUCCESS: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_success_system_48dp));
                break;
            }
            case SimpleAlert.ERROR: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_error_system_48dp));
                break;
            }
            case SimpleAlert.INFO: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_info_system_48dp));
                break;
            }
            case SimpleAlert.WARNING: {
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_warning_system_48dp));
                break;
            }
        }

        if (btnConfirm != null) {
            btnConfirm.setTextColor(ContextCompat.getColor(context, R.color.alert_system_primary));
        }
        if (btnCancel != null) {
            btnCancel.setTextColor(ContextCompat.getColor(context, R.color.alert_system_primary));
        }
        if (btnShowExtras != null) {
            btnShowExtras.setTextColor(ContextCompat.getColor(context, R.color.alert_system_primary));
        }
    }

    @Override
    public void onClick(View view) {
        runOnUi(() -> {
            if (view.getId() == R.id.btnCancel) {
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(this);
                } else {
                    dialog.dismiss();
                }
            } else if (view.getId() == R.id.btnConfirm) {
                if (confirmClickListener != null) {
                    confirmClickListener.onClick(this);
                } else {
                    dialog.dismiss();
                }
            } else if (view.getId() == R.id.btnShowExtras) {
                if (txtExtras != null) {
                    if (txtExtras.getVisibility() == View.VISIBLE) {
                        viewDivider.setVisibility(View.GONE);
                        txtExtras.setVisibility(View.GONE);
                        btnShowExtras.setText(HtmlCompat.fromHtml(context.getString(R.string.fmt_btn_show_extras), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    } else {
                        viewDivider.setVisibility(View.VISIBLE);
                        txtExtras.setVisibility(View.VISIBLE);
                        btnShowExtras.setText(HtmlCompat.fromHtml(context.getString(R.string.fmt_btn_hide_extras), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    }
                }
            }
        });
    }

    public SimpleAlert setDismissTimeout(int dismissTime) {
        this.dismissTime = dismissTime;
        autoDismiss = true;

        return this;
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public void show() {
        runOnUi(() -> {
            configComponent();

            if (autoDismiss) {
                dialog.setCanceledOnTouchOutside(true);
                new Handler(Looper.getMainLooper()).postDelayed(() -> dialog.dismiss(), dismissTime);
            }

            Window window = dialog.getWindow();

            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            dialog.show();
        });
    }

    public void dismiss() {
        runOnUi(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Run code action on UI thread
     */
    private void runOnUi(Runnable action) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(action);
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }

    /**
     * Show all alerts style including loading
     */
    public static class Demo {

        private final Context context;

        private List<Integer> styles = new ArrayList<>() {
            {
                add(SimpleAlert.LIGHT);
                add(SimpleAlert.DARK);
                add(SimpleAlert.SUCCESS);
                add(SimpleAlert.INFO);
                add(SimpleAlert.WARNING);
                add(SimpleAlert.ERROR);
            }
        };

        private Demo(Context context) {
            this.context = context;
        }

        public static void start(Context context) {
            Demo showcase = new Demo(context);
            showcase.showAlerts();
        }

        private void showAlerts() {
            new SimpleAlert(context)
                    .setType(styles.get(0))
                    .showCancel()
                    .setMessage(context.getString(R.string.alert_demo_text))
                    .setConfirmClickListener((dialog) -> {
                        styles = styles.subList(1, styles.size());

                        if (styles.size() > 0) {
                            showAlerts();
                        } else {
                            new SimpleAlert(context, SimpleAlert.STYLE_SYSTEM)
                                    .showCancel()
                                    .setMessage(context.getString(R.string.alert_demo_text))
                                    .setConfirmClickListener(alert -> {
                                        showLoadings();
                                        alert.dismiss();
                                    })
                                    .show();
                        }

                        dialog.dismiss();
                    })
                    .show();
        }

        private void showLoadings() {
            final Loading loading1 = new Loading.Dialog(context).setDismissTimeout(Loading.LENGTH_SHORT);
            final Loading loading2 = new Loading.Dialog(context).setDarkMode().setDismissTimeout(Loading.LENGTH_SHORT);
            final Loading loading3 = new Loading.Spinner(context).setDismissTimeout(Loading.LENGTH_SHORT);

            loading1.setFutureDismissListener(loading -> loading2.show());
            loading2.setFutureDismissListener(loading -> loading3.show());
            loading3.setFutureDismissListener(DialogInterface::dismiss);

            loading1.show();
        }
    }
}
