package com.example.shopapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextViewBold(context: Context, attrs: AttributeSet): AppCompatTextView (context, attrs){

    //Creating our own TextView
    init {
        applyFont()
    }

    private fun applyFont() {

        //Establecer un tipo de fuente
        val typeface: Typeface= Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(typeface)
    }

}