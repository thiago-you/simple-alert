package you.thiago.simplealert;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

@SuppressWarnings("unused WeakerAccess UnusedReturnValue")
public class SimpleAlert implements View.OnClickListener {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;

    public static final int STYLE_CLEAN = 1;
    public static final int STYLE_MINIMALIST = 2;
    public static final int STYLE_SYSTEM = 3;
    public static final int STYLE_LOADING = 4;
    public static final int STYLE_SPINNER_LOADING = 5;

    public static final int LENGTH_SHORT = 4000;
    public static final int LENGTH_LONG = 7000;

    /**
     * Dialog component
     */
    private Dialog dialog;

    private Context context;
    private OnSimpleAlertClickListener confirmClickListener;
    private OnSimpleAlertClickListener cancelClickListener;
    private DialogInterface.OnCancelListener cancelListener;

    private String title, btnConfirmTitle;
    private int alertType, alertStyle, dismissTime;
    private boolean cancelOnTouchOutside, autoDismiss, showCancelButton, hideProgress;

    private ImageView titleIcon;
    private Button btnConfirm, btnCancel, btnShowExtras;

    protected View dialogView, lytTitle, viewDivider;
    protected TextView txtTitle, txtMessage, txtExtras, txtProgress;

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

        /* set default alert type */
        alertType = SimpleAlert.SUCCESS;

        /* init component */
        initComponent();
    }

    /**
     * Initialize component
     */
    private void initComponent() {
        /* create component */
        dialog = new Dialog(context, R.style.AlertDialog);

        /* set cancel config */
        dialog.setCanceledOnTouchOutside(false);

        /* set alert layout */
        if (alertStyle == SimpleAlert.STYLE_LOADING || alertStyle == SimpleAlert.STYLE_SPINNER_LOADING) {
            if (alertStyle == SimpleAlert.STYLE_SPINNER_LOADING) {
                dialog.setContentView(R.layout.alert_loading_spinner);
                txtProgress = dialog.findViewById(R.id.txtProgress);
            } else {
                dialog.setContentView(R.layout.alert_loading);
            }

            /* prevent loading cancel */
            dialog.setCancelable(false);
        } else {
            if (alertStyle == SimpleAlert.STYLE_SYSTEM) {
                dialog.setContentView(R.layout.alert_system);
            } else {
                dialog.setContentView(R.layout.alert);

                /* init unique views */
                lytTitle = dialog.findViewById(R.id.lytTitle);
            }

            /* set cancel config */
            dialog.setCancelable(true);

            /* init interface */
            titleIcon = dialog.findViewById(R.id.imgIcon);
            txtTitle = dialog.findViewById(R.id.txtTitle);
            txtExtras = dialog.findViewById(R.id.txtExtras);
            viewDivider = dialog.findViewById(R.id.viewDivider);
            btnConfirm = dialog.findViewById(R.id.btnConfirm);
            btnCancel = dialog.findViewById(R.id.btnCancel);
            btnShowExtras = dialog.findViewById(R.id.btnShowExtras);

            /* set default listeners */
            btnConfirm.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnShowExtras.setOnClickListener(this);
        }

        /* get root view */
        dialogView = dialog.findViewById(R.id.alertDialog);

        /* init common attrs */
        txtMessage = dialog.findViewById(R.id.txtMessage);
    }

    /**
     * Config component behavior
     */
    private void configComponent() {
        /* config alert styles */
        if (alertStyle == SimpleAlert.STYLE_LOADING || alertStyle == SimpleAlert.STYLE_SPINNER_LOADING) {
            if (cancelListener != null) {
                dialog.setCancelable(true);
            }
            if (alertStyle == SimpleAlert.STYLE_SPINNER_LOADING && hideProgress) {
                txtProgress.setVisibility(View.GONE);
            }
        } else {
            /* config alert style */
            if (alertStyle == SimpleAlert.STYLE_SYSTEM) {
                configSystemAlertStyle();
            } else {
                configAlertStyle();
            }

            /* remove o cancel button */
            if (!showCancelButton) {
                btnCancel.setVisibility(View.GONE);
            }

            if (btnConfirmTitle == null) {
                if (alertType == SimpleAlert.ERROR && cancelClickListener == null) {
                    setBtnConfirmTitle(context.getString(R.string.default_confirm_error_btn_title));
                }
            }

            /* config cancel btn */
            if (cancelClickListener != null) {
                btnCancel.setVisibility(View.VISIBLE);
            }

            /* hide buttons */
            if (autoDismiss) {
                btnConfirm.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            }

            /* set default dismiss click */
            if (btnConfirm.getVisibility() != View.VISIBLE && btnCancel.getVisibility() != View.VISIBLE) {
                dialogView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }

        /* config dialog animation */
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
    }

    public SimpleAlert setTitle(String title) {
        this.title = title;

        if (txtTitle != null) {
            txtTitle.setText(title);
        }

        return this;
    }

    public SimpleAlert setMessage(String message) {

        if (txtMessage != null) {
            txtMessage.setText(message);
        }

        return this;
    }

    public SimpleAlert setProgress(String progress) {

        if (txtProgress != null) {
            txtProgress.setText(progress);
        }

        return this;
    }

    public SimpleAlert setExtras(String extra) {
        if (txtExtras != null && extra != null && extra.length() > 0) {
            txtExtras.setText(extra);

            /* show btn */
            btnShowExtras.setVisibility(View.VISIBLE);
            btnShowExtras.setText(Html.fromHtml(context.getString(R.string.fmt_btn_show_extras)));
        }

        return this;
    }

    public SimpleAlert setBtnConfirmTitle(String btnConfirmTitle) {
        this.btnConfirmTitle = btnConfirmTitle;

        if (btnConfirm != null) {
            btnConfirm.setText(btnConfirmTitle);
        }

        return this;
    }

    public SimpleAlert setBtnCancelTitle(String btnCancelTitle) {
        showCancelButton = true;

        if (btnCancel != null) {
            btnCancel.setText(btnCancelTitle);
            btnCancel.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public SimpleAlert showCancel() {
        showCancelButton = true;
        return this;
    }

    public SimpleAlert setConfirmClickListener(@Nullable OnSimpleAlertClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
        return this;
    }

    public SimpleAlert setCancelClickListener(@Nullable OnSimpleAlertClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;

        if (btnCancel != null) {
            if (cancelClickListener != null) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }
        }

        return this;
    }

    public SimpleAlert setCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        dialog.setOnCancelListener(cancelListener);

        return this;
    }

    public SimpleAlert setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        dialog.setOnDismissListener(dismissListener);
        return this;
    }

    public SimpleAlert setType(int alertType) {
        this.alertType = alertType;
        return this;
    }

    public SimpleAlert setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        if (cancel) {
            dialog.setCancelable(true);
        }

        return this;
    }

    public SimpleAlert setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public SimpleAlert setLightColor() {
        if (txtMessage != null) {
            txtMessage.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
        }
        if (txtProgress != null) {
            txtProgress.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
        }

        if (alertStyle == SimpleAlert.STYLE_LOADING && dialogView != null) {
            dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_default_dark));
        }

        return this;
    }

    public SimpleAlert setDarkColor() {
        if (txtMessage != null) {
            txtMessage.setTextColor(ContextCompat.getColor(context, R.color.textColor));
        }
        if (txtProgress != null) {
            txtProgress.setTextColor(ContextCompat.getColor(context, R.color.textColor));
        }

        if (alertStyle == SimpleAlert.STYLE_LOADING && dialogView != null) {
            dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_default));
        }

        return this;
    }

    public SimpleAlert setTextColor(int colorId) {
        if (txtMessage != null) {
            txtMessage.setTextColor(colorId);
        }
        if (txtProgress != null) {
            txtProgress.setTextColor(colorId);
        }

        return this;
    }

    public SimpleAlert setBackgroundColor(Drawable background) {
        if (dialogView != null) {
            dialogView.setBackground(background);
        }

        return this;
    }

    protected SimpleAlert hideSpinnerProgress() {
        hideProgress = true;
        return this;
    }

    private void configAlertStyle() {
        if (alertStyle == SimpleAlert.STYLE_MINIMALIST) {
            /* set root view background color */
            switch (alertType) {
                case SimpleAlert.SUCCESS: {
                    dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_success));
                    break;
                }
                case SimpleAlert.ERROR: {
                    dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_error));
                    break;
                }
                case SimpleAlert.INFO: {
                    dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_info));
                    break;
                }
                case SimpleAlert.WARNING: {
                    dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_warning));
                    break;
                }
            }

            /* change text colors */
            if (txtMessage != null) {
                txtMessage.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
            }
            if (txtExtras != null) {
                txtExtras.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
            }
            if (viewDivider != null) {
                viewDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.textWhite));
            }
            if (btnShowExtras != null) {
                btnShowExtras.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
            }
            if (btnConfirm != null) {
                btnConfirm.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
            }
            if (btnCancel != null) {
                btnCancel.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
            }
        } else {
            /* set root view default background */
            dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.alert_default));

            /* change text colors */
            if (txtMessage != null) {
                txtMessage.setTextColor(ContextCompat.getColor(context, R.color.textColor));
            }
            if (txtExtras != null) {
                txtExtras.setTextColor(ContextCompat.getColor(context, R.color.textColor));
            }
            if (viewDivider != null) {
                viewDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.textColor));
            }
            if (btnConfirm != null) {
                btnConfirm.setTextColor(ContextCompat.getColor(context, R.color.alert_btn_confirm));
            }
            if (btnCancel != null) {
                btnCancel.setTextColor(ContextCompat.getColor(context, R.color.alert_btn_cancel));
            }
            if (btnShowExtras != null) {
                btnShowExtras.setTextColor(ContextCompat.getColor(context, R.color.alert_btn_confirm));
            }
        }

        /* config default title */
        if (title == null) {
            if (alertType == SimpleAlert.SUCCESS) {
                title = context.getString(R.string.success);
            } else {
                title = context.getString(R.string.attention);
            }
        }

        /* config title color */
        switch (alertType) {
            case SimpleAlert.SUCCESS: {
                lytTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.alert_success_bg));
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_success_32dp));
                break;
            }
            case SimpleAlert.ERROR: {
                lytTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.alert_error_bg));
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_error_32dp));
                break;
            }
            case SimpleAlert.INFO: {
                lytTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.alert_info_bg));
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_info_32dp));
                break;
            }
            case SimpleAlert.WARNING: {
                lytTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.alert_warning_bg));
                titleIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_warning_32dp));
                break;
            }
        }
    }

    private void configSystemAlertStyle() {
        /* config title icon */
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
                    btnShowExtras.setText(Html.fromHtml(context.getString(R.string.fmt_btn_show_extras)));
                } else {
                    viewDivider.setVisibility(View.VISIBLE);
                    txtExtras.setVisibility(View.VISIBLE);
                    btnShowExtras.setText(Html.fromHtml(context.getString(R.string.fmt_btn_hide_extras)));
                }
            }
        }
    }

    public SimpleAlert setDismissTimeout(int dismissTime) {
        this.dismissTime = dismissTime;
        autoDismiss = true;

        return this;
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void show() {
        /* config component behavior */
        configComponent();

        if (autoDismiss) {
            dialog.setCanceledOnTouchOutside(true);

            /* create dismiss timer */
            final Handler handler  = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, dismissTime);
        }

        /* set match parent size */
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.show();
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
