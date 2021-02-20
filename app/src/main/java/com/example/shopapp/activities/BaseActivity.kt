package com.example.shopapp.activities

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.shopapp.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_progress.*


open class BaseActivity : AppCompatActivity() {

     private lateinit var mProgressDialog: Dialog

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

    fun fullScreen() {
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

    fun showProgressDialog(text:String){
         
         mProgressDialog=Dialog(this)
         
         //Set the screen content from a layout resource
         //The resource will be inflated, adding all top-level views to the screen
         
         mProgressDialog.setContentView(R.layout.dialog_progress)
         mProgressDialog.tv_progress_text.text = text
         mProgressDialog.setCancelable(false)
         mProgressDialog.setCanceledOnTouchOutside(false)
         mProgressDialog.show()
     }
    
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}