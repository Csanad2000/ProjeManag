package com.csanad.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.csanad.projemanag.R
import com.csanad.projemanag.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val typeFace:Typeface=Typeface.createFromAsset(assets,"carbon bl.ttf")
        tvAppName.typeface=typeFace

        Handler().postDelayed({
            var currentUserId=FirestoreClass().getCurrentUserId()

            if (currentUserId.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        },2500)
    }
}