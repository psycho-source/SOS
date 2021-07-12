package com.tarun.sos.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

fun isPermissionGranted(
    grantPermissions: Array<String>, grantResults: IntArray,
    permission: String
): Boolean {
    for (i in grantPermissions.indices) {
        if (permission == grantPermissions[i]) {
            return grantResults[i] == PackageManager.PERMISSION_GRANTED
        }
    }
    return false
}

fun requestPermission(
    activity: AppCompatActivity, requestId: Int,
    permission: String, finishActivity: Boolean
) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(permission),
        requestId
    )
}

fun requestPermissions(
    activity: AppCompatActivity, requestId: Int,
    permissions: Array<String>
) {
    ActivityCompat.requestPermissions(
        activity,
        permissions,
        requestId
    )
}