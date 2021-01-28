package simple

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.ViewConfiguration
import android.view.WindowManager

/**
 * Time:2021/1/28 6:34 PM
 * Author:
 * Description:
 */
object ScreenUtils {

    /**
     * 屏幕宽高 0 width, 1 height, 2 widthDP, 3 heightDP
     *
     * @return
     */
    fun getScreenSize(activity: Activity): IntArray {
        val result = IntArray(4)
        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay?.getMetrics(outMetrics)
        result[0] = outMetrics.widthPixels
        result[1] =
            outMetrics.heightPixels - getStatusBarHeightInner(activity) - getNavigationBarHeight(
                activity
            )
        val density = outMetrics.density
        result[2] = (outMetrics.widthPixels / density).toInt()
        result[3] =
            ((outMetrics.heightPixels - getStatusBarHeightInner(activity)) / density).toInt()
        return result
    }


    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    private fun getStatusBarHeightInner(activity: Activity): Int {
        var statusBarHeight = 0
        statusBarHeight = try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field = c.getField("status_bar_height")
            val x = field[obj].toString().toInt()
            activity.resources.getDimensionPixelSize(x)
        } catch (e: Exception) {
            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)
            rect.top
        }
        return statusBarHeight
    }


    /**
     * 获取虚拟按键栏高度
     *
     * @return
     */
    private fun getNavigationBarHeight(activity: Activity): Int {
        var result = 0
        if (hasNavBar(activity)) {
            val res: Resources = activity.resources
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }


    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    private fun hasNavBar(context: Context): Boolean {
        val res = context.resources
        val resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (resourceId != 0) {
            var hasNav = res.getBoolean(resourceId)
            // check override flag
            val sNavBarOverride: String = getNavBarOverride()
            if ("1" == sNavBarOverride) {
                hasNav = false
            } else if ("0" == sNavBarOverride) {
                hasNav = true
            }
            hasNav
        } else { // fallback
            !ViewConfiguration.get(context).hasPermanentMenuKey()
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private fun getNavBarOverride(): String {
        var sNavBarOverride: String = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                val c = Class.forName("android.os.SystemProperties")
                val m =
                    c.getDeclaredMethod("get", String::class.java)
                m.isAccessible = true
                sNavBarOverride = m.invoke(null, "qemu.hw.mainkeys") as String
            } catch (e: Throwable) {
            }
        }
        return sNavBarOverride
    }

}