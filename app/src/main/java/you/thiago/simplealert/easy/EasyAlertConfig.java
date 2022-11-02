package you.thiago.simplealert.easy;

import you.thiago.simplealert.Common;
import you.thiago.simplealert.SimpleAlert;

/**
 * Easy Alert shared config
 */
public class EasyAlertConfig {

    protected final SimpleAlert simpleAlert;

    public EasyAlertConfig(SimpleAlert simpleAlert) {
        this.simpleAlert = simpleAlert;
    }

    public SimpleAlert build(int messageRes) {
        return simpleAlert.setMessage(messageRes);
    }

    public SimpleAlert build(String message) {
        return simpleAlert.setMessage(message);
    }

    public SimpleAlert build(int titleRes, int messageRes) {
        return simpleAlert.setTitle(titleRes).setMessage(messageRes);
    }

    public SimpleAlert build(String title, String message) {
        return simpleAlert.setTitle(title).setMessage(message);
    }

    public SimpleAlert build(int messageRes, Common.OnClickListener listener) {
        return simpleAlert.setMessage(messageRes).setConfirmListener(listener);
    }

    public SimpleAlert build(String message, Common.OnClickListener listener) {
        return simpleAlert.setMessage(message).setConfirmListener(listener);
    }

    public SimpleAlert build(int titleRes, int messageRes, Common.OnClickListener listener) {
        return simpleAlert.setTitle(titleRes).setMessage(messageRes).setConfirmListener(listener);
    }

    public SimpleAlert build(String title, String message, Common.OnClickListener listener) {
        return simpleAlert.setTitle(title).setMessage(message).setConfirmListener(listener);
    }

    public SimpleAlert show(int messageRes) {
        build(messageRes).show();
        return simpleAlert;
    }

    public SimpleAlert show(String message) {
        build(message).show();
        return simpleAlert;
    }

    public SimpleAlert show(int titleRes, int messageRes) {
        build(titleRes, messageRes).show();
        return simpleAlert;
    }

    public SimpleAlert show(String title, String message) {
        build(title, message).show();
        return simpleAlert;
    }

    public SimpleAlert show(int messageRes, Common.OnClickListener listener) {
        build(messageRes, listener).show();
        return simpleAlert;
    }

    public SimpleAlert show(String message, Common.OnClickListener listener) {
        build(message, listener).show();
        return simpleAlert;
    }

    public SimpleAlert show(int titleRes, int messageRes, Common.OnClickListener listener) {
        build(titleRes, messageRes, listener).show();
        return simpleAlert;
    }

    public SimpleAlert show(String title, String message, Common.OnClickListener listener) {
        build(title, message, listener).show();
        return simpleAlert;
    }
}
