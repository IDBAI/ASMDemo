package simple

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.yy.asm.demo.R

/**
 *
 * @ProjectName: ASMDemo
 * @Author: yangshun
 * @CreateDate: 2020/9/22 6:04 PM
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/9/22 6:04 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
class SlingDemoActivity : AppCompatActivity() {
    lateinit var seatSecond: View
    lateinit var root: FrameLayout

    val SIZE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sling)

        findViewById<Button>(R.id.click_show_sling).setOnClickListener {
            val imageView = ImageView(this.applicationContext)
            imageView.setImageResource(R.drawable.ic_launcher_background)
            val layoutParams = FrameLayout.LayoutParams(SIZE, SIZE)
            imageView.layoutParams = layoutParams
            root.addView(imageView)
            start(imageView)
        }


        seatSecond = findViewById(R.id.seat_second)
        root = findViewById(R.id.root)
    }


    private fun start(targetView: View) {

        val startX = targetView.x
        val startY = targetView.y

        val screenSize = ScreenUtils.getScreenSize(this)
        val measuredWidth = targetView.measuredWidth
        val measuredHeight = targetView.measuredHeight

        Log.i("TAG", "measuredWidth :$measuredWidth")
        Log.i("TAG", "measuredHeight :$measuredHeight")
        val centerX = (screenSize[0] / 2.0f) - (SIZE * 1.0f) / 2.0f
        val centerY = (screenSize[1] / 2.0f) - (SIZE * 1.0f) / 2.0f


        val ofFloatX = ObjectAnimator.ofFloat(targetView, "translationX", startX, centerX)
        val ofFloatY = ObjectAnimator.ofFloat(targetView, "translationY", startY, centerY)
        val scaleX = ObjectAnimator.ofFloat(targetView, "scaleX", 1.0f, 1.5f)
        val scaleY = ObjectAnimator.ofFloat(targetView, "scaleY", 1.0f, 1.5f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(ofFloatX, ofFloatY, scaleX, scaleY)
        animatorSet.duration = 800
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.start()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                end(targetView)
            }
        })
    }


    private fun end(targetView: View) {

        val endX = seatSecond.x
        val endY = seatSecond.y

        val centerX = targetView.x
        val centerY = targetView.y

        val ofFloatX = ObjectAnimator.ofFloat(targetView, "translationX", centerX, endX)
        val ofFloatY = ObjectAnimator.ofFloat(targetView, "translationY", centerY, endY)
        val scaleX = ObjectAnimator.ofFloat(targetView, "scaleX", 1.5f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(targetView, "scaleY", 1.5f, 1.0f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(ofFloatX, ofFloatY, scaleX, scaleY)
        animatorSet.duration = 800
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.start()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                root.removeView(targetView)
            }
        })
    }


}