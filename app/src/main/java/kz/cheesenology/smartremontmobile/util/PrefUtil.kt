package kz.cheesenology.smartremontmobile.util

import android.content.Context
import android.content.SharedPreferences
import kz.cheesenology.smartremontmobile.SmartRemontApplication

object PrefUtils {
    private val PREF_NAME = "kz.cheesenology.okk_preferences"

    val prefs: SharedPreferences
        get() =
            SmartRemontApplication.instance!!.applicationComponent.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    val editor: SharedPreferences.Editor
        get() = prefs.edit()
}
