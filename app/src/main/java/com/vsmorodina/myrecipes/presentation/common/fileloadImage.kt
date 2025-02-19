package com.vsmorodina.myrecipes.presentation.common

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageUri")
fun loadImage(imageView: ImageView, imageUri: Uri?) {
    if (imageUri != null) {
        imageView.setImageURI(imageUri)
    } else {
        imageView.setImageDrawable(null)
    }
}