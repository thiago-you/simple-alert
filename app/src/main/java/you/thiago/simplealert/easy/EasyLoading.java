package you.thiago.simplealert.easy;

import android.content.Context;
import you.thiago.simplealert.Loading;

/**
 * Loading facade
 */
public class EasyLoading {

    private EasyLoading() {
        // ... private enclosing constructor
    }

    public static Loading build(Context context, int messageRes) {
        return new Loading.Dialog(context).setMessage(messageRes);
    }

    public static Loading build(Context context, String message) {
        return new Loading.Dialog(context).setMessage(message);
    }

    public static Loading buildDialog(Context context, int messageRes) {
        return new Loading.Dialog(context).setMessage(messageRes);
    }

    public static Loading buildDialog(Context context, String message) {
        return new Loading.Dialog(context).setMessage(message);
    }

    public static Loading buildSpinner(Context context) {
        return new Loading.Spinner(context);
    }

    public static Loading buildSpinner(Context context, int messageRes) {
        return new Loading.Spinner(context).setMessage(messageRes);
    }

    public static Loading buildSpinner(Context context, String message) {
        return new Loading.Spinner(context).setMessage(message);
    }

    public static Loading show(Context context, int messageRes) {
        return EasyLoading.build(context, messageRes).show();
    }

    public static Loading show(Context context, String message) {
        return EasyLoading.build(context, message).show();
    }

    public static Loading showDialog(Context context, int messageRes) {
        return EasyLoading.buildDialog(context, messageRes).show();
    }

    public static Loading showDialog(Context context, String message) {
        return EasyLoading.buildDialog(context, message).show();
    }

    public static Loading showSpinner(Context context) {
        return EasyLoading.buildSpinner(context).show();
    }

    public static Loading showSpinner(Context context, int messageRes) {
        return EasyLoading.buildSpinner(context, messageRes).show();
    }

    public static Loading showSpinner(Context context, String message) {
        return EasyLoading.buildSpinner(context, message).show();
    }
}
