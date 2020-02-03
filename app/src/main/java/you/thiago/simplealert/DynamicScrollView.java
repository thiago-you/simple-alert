package you.thiago.simplealert;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

@SuppressWarnings({"unused"})
public class DynamicScrollView extends ScrollView {

    private int maxHeight;

    public DynamicScrollView(Context context) {
        super(context);
    }

    public DynamicScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public DynamicScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DynamicScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.DynamicScrollView);

            try {
                maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.DynamicScrollView_maxHeight, (int) getResources().getDimension(R.dimen.default_scroll_max_height));
            } finally {
                styledAttrs.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
