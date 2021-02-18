package com.example.shopapp.firestore

import android.app.Activity
import android.util.Log
import com.example.shopapp.activities.LoginActivity
import com.example.shopapp.activities.RegisterActivity
import com.example.shopapp.models.User
import com.example.shopapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        //Creation de database
        /*
        The "users" is collection name. If the collection is already created then it will not create the same again.
        Document ID for users fields. Here the document it is the User ID.
        userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of repplacing the fields.
        sirve para unir el user con la autenticación de firebase y el set para que se le pueda añadir luego gender o image
         */
        mFireStore.collection(Constants.USERS).document(userInfo.id).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                //Here call a function of base activity for transferring the result to it
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user", e)

            }
    }

    fun getCurrentUserID(): String {

        //An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentUserId if it is not null or else it will be blank
        var currentUserID = ""

        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity:Activity){

        //Here we pass the collection name from which we wants the data
        mFireStore.collection(Constants.USERS)
            //The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName, document.toString())

                //Here we have received the document snapshot which is converted into the User Data model object
                val user= document.toObject(User::class.java)!!

                //START
                when (activity){
                    is LoginActivity -> {
                        //Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                }
                //END
            }
            .addOnFailureListener{ e->
                when (activity){

                }
            }
    }
}