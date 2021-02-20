package com.example.shopapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.shopapp.activities.LoginActivity
import com.example.shopapp.activities.RegisterActivity
import com.example.shopapp.activities.UserProfileActivity
import com.example.shopapp.models.User
import com.example.shopapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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
        mFireStore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
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
        //We can get with this function the ID of the current user
        //An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""

        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        //Here we pass the collection name from which we wants the data
        mFireStore.collection(Constants.USERS)
            //The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //Here we have received the document snapshot which is converted into the User Data model object
                val user =
                    document.toObject(User::class.java)!! //convertimos lo que recibimos del data base en un objeto user para trabajar con él

                //Creating and instance from sharedpreferences
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MYSHOPPAL_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                //KEY: looged_in_username - VALUE: firstName + lastName

                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()

                //START
                when (activity) {
                    is LoginActivity -> {
                        //Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                }
                //END
            }
            .addOnFailureListener { e ->
                when (activity) {

                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHasMap: HashMap<String, Any>) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHasMap)
            .addOnSuccessListener {

                when (activity) {
                    is UserProfileActivity -> {
                        //Hide the progress dialog if there is any error. And print the error in log
                        activity.userProfileUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        //Hide the progress dialog if there is any error. And print the error in log
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while updating the user details", e)
            }

    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        //Le estamos poniendo el nombre de la constante USER_PROFILE_IMAGE, despues un numero que es el tiempo en milisegundos que pasa
        // y despues el nombre de la extensión que tenga
        //Por tanto se quedaría así: User_Profile_Image123456765432.jpg
        val sRef: StorageReference = FirebaseStorage.getInstance().reference
            .child(
                Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot->
            //The image upload is success
            Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            //Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener{ uri->
                Log.e("Downloadable Image URL", uri.toString())
                when(activity){
                    is UserProfileActivity ->{
                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }
        }
            .addOnFailureListener { exception->
                //Hide the progress dialog if there is any error. And print the error in log

                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, exception.message, exception )
            }

    }
}