package com.example.mqttkotlinsample

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment

private const val TAG = "HiveMQ"

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    requireContext().toast(message)
}