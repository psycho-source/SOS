package com.tarun.sos.utils

import android.content.Context

const val MISC_SHARED_PREFS = "misc_shared_prefs"

fun isFirstStart(context: Context?): Boolean {
    val sharedPreferences =
        context?.getSharedPreferences(MISC_SHARED_PREFS, Context.MODE_PRIVATE) ?: return true
    return sharedPreferences.getBoolean("isFirstStart", true)
}

fun setFirstStart(context: Context?, isFirstStart: Boolean) {
    val sharedPreferences =
        context?.getSharedPreferences(MISC_SHARED_PREFS, Context.MODE_PRIVATE) ?: return
    with(sharedPreferences.edit()) {
        putBoolean("isFirstStart", isFirstStart)
        apply()
    }
}