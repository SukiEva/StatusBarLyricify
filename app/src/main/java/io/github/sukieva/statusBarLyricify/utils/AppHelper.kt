package io.github.sukieva.statusBarLyricify.utils

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import io.github.sukieva.statusBarLyricify.MyApp

fun String.toast(duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(MyApp.context, this, duration).show()

fun browse(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    MyApp.context.startActivity(intent)
}