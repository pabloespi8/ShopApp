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
    const val FIRST_NAME:String="firstName"
    const val LAST_NAME:String="lastName"
    const val MOBILE:String="mobile"
    const val GENDER:String="gender"

    const val USER_PROFILE_IMAGE:String="User_Profile_Image"
    const val IMAGE:String="image"
    const val COMPLETE_PROFILE:String= "profileCompleted"

    // Firebase Constants
    // This is used for the collection name for USERS.

    const val PRODUCTS: String = "products"
    const val CART_ITEMS: String = "cart_items"
    const val ADDRESSES: String = "addresses"
    const val ORDERS: String = "orders"
    const val SOLD_PRODUCTS: String = "sold_products"


    // Intent extra constants.
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_MY_ORDER_DETAILS: String = "extra_my_order_details"
    const val EXTRA_SOLD_PRODUCT_DETAILS: String = "extra_sold_product_details"

    // A unique code of image selection from Phone Storage.
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121
    const val DEFAULT_CART_QUANTITY: String = "1"

    // Firebase database field names
    const val USER_ID: String = "user_id"
    const val PRODUCT_ID: String = "product_id"
    const val PRODUCT_IMAGE: String = "Product_Image"
    const val CART_QUANTITY: String = "cart_quantity"
    const val STOCK_QUANTITY: String = "stock_quantity"
    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"


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