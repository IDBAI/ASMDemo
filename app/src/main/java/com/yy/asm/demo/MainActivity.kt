package com.yy.asm.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import simple.SlingDemoActivity

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

        findViewById<Button>(R.id.click_report).setOnClickListener {
        }
        findViewById<Button>(R.id.goto_sling).setOnClickListener {
            startActivity(Intent(this, SlingDemoActivity::class.java))
        }
    }
}