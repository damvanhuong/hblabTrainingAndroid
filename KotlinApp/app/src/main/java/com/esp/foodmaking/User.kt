package com.esp.foodmaking

class User(var id: String, var name: String, var avatarUrl : String, var coverUrl: String) {

    companion object {
        var instance : User? = null

        fun initUser(id: String, name: String, avatarUrl : String, coverUrl: String) {
            instance = User(id, name, avatarUrl, coverUrl)
        }
    }
}
