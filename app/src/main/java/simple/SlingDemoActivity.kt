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
import android.view.ViewPropertyAnimator
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
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
            layoutParams.leftMargin = 300
            layoutParams.topMargin = 500
            imageView.layoutParams = layoutParams

            imageView.layout(300, 500, 0, 0)
            root.addView(imageView)
            start(imageView)
        }


        seatSecond = findViewById(R.id.seat_second)
        root = findViewById(R.id.root)
    }


    private fun start(targetView: View) {

        val startX = targetView.x
        val startY = targetView.y
        val left = targetView.left
        val top = targetView.top


        val screenSize = ScreenUtils.getScreenSize(this)
        val measuredWidth = targetView.measuredWidth
        val measuredHeight = targetView.measuredHeight

        Log.i("TAG", "left :$left")
        Log.i("TAG", "top :$top")
        Log.i("TAG", "startX :$startX")
        Log.i("TAG", "startY :$startY")
        Log.i("TAG", "measuredWidth :$measuredWidth")
        Log.i("TAG", "measuredHeight :$measuredHeight")
        val centerX = (screenSize[0] / 2.0f) - (SIZE * 1.0f) / 2.0f
        val centerY = (screenSize[1] / 2.0f) - (SIZE * 1.0f) / 2.0f


        val animate = targetView.animate()
        animate.translationXBy(centerX - startX)
            .translationYBy(centerY - startY)
            .scaleXBy(0.5f)
            .scaleYBy(0.5f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(800)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    end(targetView)
                }
            })
            .start()

    }


    private fun end(targetView: View) {

        val endX = seatSecond.x
        val endY = seatSecond.y

        val centerX = targetView.x
        val centerY = targetView.y

        Log.i("TAGEND", "endX $endX")//900
        Log.i("TAGEND", "endY $endY")
        Log.i("TAGEND", "centerX $centerX")//490
        Log.i("TAGEND", "centerY $centerY")


        val animate = targetView.animate()
        animate.translationXBy(endX - centerX)
            .translationYBy(endY - centerY)
            .scaleXBy(-0.5f)
            .scaleYBy(-0.5f)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(800)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    root.removeView(targetView)
                }
            })
            .start()

    }


}