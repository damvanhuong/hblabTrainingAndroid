package com.esp.foodmaking

import android.content.Context

class Auth {
    companion object {
        fun saveUser(context: Context, user: User) {
            context.getSharedPreferences(Const.APP_NAME, Context.MODE_PRIVATE).edit()
                    .putString(Const.USER_ID, user.id)
                    .putString(Const.USER_NAME, user.name)
                    .putString(Const.USER_AVATAR, user.avatarUrl)
                    .putString(Const.USER_COVER, user.coverUrl)
                    .apply()
        }

        fun removeUser(context: Context) {
            context.getSharedPreferences(Const.APP_NAME, Context.MODE_PRIVATE).edit()
                    .remove(Const.USER_ID)
                    .remove(Const.USER_NAME)
                    .remove(Const.USER_AVATAR)
                    .remove(Const.USER_COVER)
                    .apply()
        }

        fun getUser(context: Context) : User?{
            val sharedPrefs = context.getSharedPreferences(Const.APP_NAME, Context.MODE_PRIVATE)
            val id = sharedPrefs.getString(Const.USER_ID, null)
            val name = sharedPrefs.getString(Const.USER_NAME, null)
            val cover = sharedPrefs.getString(Const.USER_COVER, null)
            val avatar = sharedPrefs.getString(Const.USER_AVATAR, null)
            return if (id == null || name == null) {
                null
            } else {
                User(id, name, avatar, cover)
            }
        }
    }
}