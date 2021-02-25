package com.example.shopapp.ui.i.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.shopapp.R
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.utils.Constants
import com.example.shopapp.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()
        //hideProgressDialog()
        tv_edit.setOnClickListener {
            val intent=Intent(this@SettingsActivity, UserProfileActivity::class.java)
            startActivity(intent)

            //intent.putExtra(Constants.EXTRA_USERS_DETAILS, mUserDetails)

        }

        btn_logout.setOnClickListener { v: View ->

            if (v != null) {
                when (v.id) {
                    R.id.btn_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }

        ll_address.setOnClickListener {
            val intent= Intent(this, AddressListActivity::class.java )
            startActivity(intent)
        }
    }

    private fun setupActionBar() {

        //We are creating the back arrow
        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
        hideProgressDialog()
    }

    fun userDetailsSuccess(user: User) {
        hideProgressDialog()
        /*
         GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)
         tv_name.text= "${user.firstName} ${user.lastName}"
         tv_gender.text=user.gender
         tv_email.text = user.email
         tv_mobile_number.text="${user.mobile}" */
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()

    }
}
