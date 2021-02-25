package com.example.shopapp.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.shopapp.R
import java.io.IOException

class GlideLoader(val context:Context) {

    fun loadUserPicture(image: Any, imageView: ImageView){
        try{
            //Load the user image in the imageView
            Glide.with(context).load(image)//Uri of the image
                .centerCrop() //Scale type of the image
                .placeholder(R.drawable.image) //default image if our image failed to load
                .into(imageView)//the view in which the image will be loaded
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun loadProductPicture(image: Any, imageView: ImageView){
        try{
            //Load the user image in the imageView
            Glide.with(context).load(image)//Uri of the image
                .centerCrop() //Scale type of the image
                .into(imageView)//the view in which the image will be loaded
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }

}