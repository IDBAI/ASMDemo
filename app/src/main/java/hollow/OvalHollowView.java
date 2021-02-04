package hollow;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Time:2021/2/4 15:23
 * Author:
 * Description:
 */
public class OvalHollowView extends HollowView {
    public OvalHollowView(Context context) {
        super(context);
    }

    public OvalHollowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public OvalHollowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OvalHollowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected Path makePath() {
        Path path = new Path();
        path.addOval(this.getPaddingAdjustedRectF(), Path.Direction.CCW);
        return path;
    }
}