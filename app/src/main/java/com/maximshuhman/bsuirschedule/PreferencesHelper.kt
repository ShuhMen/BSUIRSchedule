package com.maximshuhman.bsuirschedule

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PreferenceHelper {


    val OPENED_GROUP = "OPENED_GROUP"
    val TYPE = "TYPE"


    fun defaultPreference(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.openedGroup
        get() = getInt(OPENED_GROUP, 0)
        set(value) {
            editMe {
                it.putInt(OPENED_GROUP, value)
            }
        }

    var SharedPreferences.openedType
        get() = getInt(TYPE, 0)
        set(value) {
            editMe {
                it.putInt(TYPE, value)
            }
        }

    /* var SharedPreferences.clearValues
         get() = { }
         set(value) {
             editMe {
                 it.clear()
             }
         }*/
}