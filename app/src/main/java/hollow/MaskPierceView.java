package hollow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.yy.asm.demo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Time:2021/2/4 16:16
 * Author:
 * Description: 使用 PorterDuff.Mode.SRC_OUT 方式实现镂空效果
 */
public class MaskPierceView extends View {
    private static final String TAG = "MaskPierceView";

    private Bitmap mSrcRectBg;

    private List<Bitmap> mDstBitmap = new ArrayList<>();

    private int mScreenWidth;   // 屏幕的宽
    private int mScreenHeight;  // 屏幕的高


    public MaskPierceView(Context context) {
        this(context, null);
    }

    public MaskPierceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (mScreenWidth == 0) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaskPierceView);
        int color = typedArray.getColor(R.styleable.MaskPierceView_maskColor, Color.GRAY);
        typedArray.recycle();

        mSrcRectBg = makeSrcRect(color);

    }


    public void addHollowAnchor(Point point, int width, int height, int radio) {
        Bitmap bm = Bitmap.createBitmap(mScreenWidth, mScreenWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        RectF rectF = new RectF();
        rectF.left = point.x;
        rectF.top = point.y;
        rectF.right = point.x + width;
        rectF.bottom = point.y + height;
        canvas.drawRoundRect(rectF, radio, radio, paint);
        mDstBitmap.add(bm);

    }



    public void invalid() {
        invalidate();
    }

    private void layoutView(View v, int width, int height) {
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        canvas.saveLayer(0, 0, mScreenWidth, mScreenHeight, null);
        //DST bitmap 使用 SRC_OUT 模式，挖空背景层
        for (Bitmap bitmap : mDstBitmap) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        //背景层
        paint.setAlpha(255);
        canvas.drawBitmap(mSrcRectBg, 0, 0, paint);
        paint.setXfermode(null);
    }


    /**
     * 创建默认遮罩层形状
     *
     * @param color
     * @return
     */
    private Bitmap makeSrcRect(int color) {
        Bitmap bm = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvcs.drawRect(new RectF(0, 0, mScreenWidth, mScreenHeight), paint);
        return bm;
    }


}