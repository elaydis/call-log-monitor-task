package io.github.elaydis.calllogmonitor.utils

import android.os.Build

inline fun oreo(code: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        code()
    }
}

inline fun nougat(code: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        code()
    }
}

inline fun marshmallowOnly(code: () -> Unit) {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
        code()
    }
}