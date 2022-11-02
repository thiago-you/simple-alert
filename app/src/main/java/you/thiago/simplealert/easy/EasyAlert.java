package you.thiago.simplealert.easy;

import android.content.Context;

import you.thiago.simplealert.SimpleAlert;

/**
 * Alert collection facade
 */
public class EasyAlert {

    private EasyAlert() {
        // ... private enclosing constructor
    }

    public static class Default extends EasyAlertConfig {
        public Default(Context context) {
            super(new SimpleAlert(context).setType(SimpleAlert.LIGHT));
        }
    }

    public static class Light extends EasyAlertConfig {
        public Light(Context context) {
            super(new SimpleAlert(context).setType(SimpleAlert.LIGHT));
        }
    }

    public static class Dark extends EasyAlertConfig {
        public Dark(Context context) {
            super(new SimpleAlert(context).setType(SimpleAlert.DARK));
        }
    }

    public static class Success extends EasyAlertConfig {
        public Success(Context context) {
            super(new SimpleAlert(context).setType(SimpleAlert.SUCCESS));
        }
    }

    public static class Info extends EasyAlertConfig {
        public Info(Context context) {
            super(new SimpleAlert(context).setType(SimpleAlert.INFO));
        }
    }

    public static class Warning extends EasyAlertConfig {
        public Warning(Context context) {
            super(new SimpleAlert(context).setType(SimpleAlert.WARNING));
        }
    }

    public static class Error extends EasyAlertConfig {
        public Error(Context context) {
            super(new SimpleAlert(context).setType(SimpleAlert.ERROR));
        }
    }

    public static class System extends EasyAlertConfig {
        public System(Context context) {
            super(new SimpleAlert(context, SimpleAlert.STYLE_SYSTEM));
        }
    }
}
