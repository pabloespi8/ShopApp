package com.example.shopapp.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.shopapp.R
import com.google.android.material.snackbar.Snackbar

 open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        fullScreen()
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        //WE HAVE ALREADY CREATE THIS FUNCION THAT CREATE A ERROR-SUCCESS BAR
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarError))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarSuccess))
        }
        snackBar.show()
    }


    private fun fullScreen() {
        //Si estamos en Android R utilizamos una forma para establecer la pantalla completa, sino, la forma vieja
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}