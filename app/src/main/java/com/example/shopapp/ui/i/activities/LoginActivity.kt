package com.example.shopapp.ui.i.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.shopapp.R
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.User
import com.example.shopapp.utils.Constants
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

                if (task.isSuccessful){
                   FirestoreClass().getUserDetails(this@LoginActivity)
                }
                else{
                    hideProgressDialog()
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }

            }
        }
    }

    fun userLoggedInSuccess(user: User){

        hideProgressDialog()

        if(user.profileCompleted==0){
            val intent=Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.putExtra(Constants.EXTRA_USERS_DETAILS, user)
            startActivity(intent)
        }
        else{
            val intent=Intent(this@LoginActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        finish()

    }
}