package com.example.shopapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.shopapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fullScreen()
        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)

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

    override fun onClick(v: View?){
        if(v !=null){
            when(v.id){
                R.id.tv_forgot_password -> {
                    val intent=Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    logInRegisteredUser()
                }

                R.id.tv_register -> {
                    val intent=Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }


            }
        }
    }

    private fun validateLoginDetails():Boolean{ //WE MUST CHANGE THE CONDITIONS IN THIS FUNCTION
        return when{
            TextUtils.isEmpty(et_email_login.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser(){

        if(validateLoginDetails()){

            //Show the progress dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            //Get the text from editText and trim the space
            val email= et_email_login.text.toString().trim { it <= ' '}
            val password= et_password.text.toString().trim { it <= ' '}

            //Login using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task->

                hideProgressDialog()

                if (task.isSuccessful){
                    showErrorSnackBar("You are logged in successfully", false)
                    //Now we should send user to MainActivity
                }
                else{
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }

            }
        }
    }
}