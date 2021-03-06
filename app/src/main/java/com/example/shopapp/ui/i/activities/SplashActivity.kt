package com.example.shopapp.ui.i.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.shopapp.R

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fullScreen()
        moveMainActivity()

    }

    private fun moveMainActivity() {
        //Move to the MainActivity after 2500 milliseconds. Posible cambiar con la forma de MoureDev
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                startActivity(Intent(this, LoginActivity::class.java))
                finish() //call this when your activity is done and should be close
            },
            1500 // milliseconds
        )
    }
}
