package com.yy.asm.demo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bamboo.cashlib.DoReport

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
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val person = Person()
        person.talk()
        person.doSomething()

        findViewById<TextView>(R.id.click_me).setOnClickListener{
            DoReport.report("MainActivity -> 上报信息")
        }
    }
}