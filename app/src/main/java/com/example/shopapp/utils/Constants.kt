package com.example.shopapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS: String= "users"
    const val MYSHOPPAL_PREFERENCES:String= "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME:String="logged_in_username"
    const val EXTRA_USERS_DETAILS:String= "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE:Int= 2
    const val PICK_IMAGE_REQUEST_CODE=1

    const val MALE:String="Male"
    const val FEMALE:String="Female"

    const val MOBILE:String="mobile"
    const val GENDER:String="gender"

    const val USER_PROFILE_IMAGE:String="User_Profile_Image"
    const val IMAGE:String="image"
    const val COMPLETE_PROFILE:String= "profileCompleted"


    fun showImageChooser(activity: Activity){
        //An intent for launching the image selection of phone storage
        val galleryIntent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity:Activity, uri: Uri?):String? {

        /*
        MimeTypeMap: two way map that maps MIME-types to file extensions and vice versa

        GetSingleton(): Get the singleton instance of MimeTypeMap

        getExtensionFromMimeType: Return the registered extension for the given MIME type.

        contentResolver.getType: Return the MIME type of the given content URL.
         */

        //Obtiene la extension (jpg, png) de la imagen que tenemos en nuestro dispositivo
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}