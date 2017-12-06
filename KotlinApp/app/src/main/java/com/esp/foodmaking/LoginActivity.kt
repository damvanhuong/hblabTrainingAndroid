package com.esp.foodmaking

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import java.util.*

class LoginActivity : AppCompatActivity(), FacebookCallback<LoginResult> {

    val callbackManager: CallbackManager = CallbackManager.Factory.create()
    override fun onSuccess(result: LoginResult?) {
        val params = Bundle()
        params.putString("fields", "id,name,cover,picture.type(large)")
        val request = GraphRequest(result?.accessToken, "me", params, HttpMethod.GET, object : GraphRequest.Callback {
            override fun onCompleted(response: GraphResponse?) {
                val data = response?.jsonObject
                if (data != null) {
                    val id = data.getString("id")
                    val name = data.getString("name")
                    val avatar = data.getJSONObject("picture")?.getJSONObject("data")?.getString("url")
                    val cover = data.getJSONObject("cover")?.getString("source")
                    User.initUser(id!!, name!!, avatar!!, cover!!)
                    Auth.saveUser(applicationContext, User.instance!! )
                    finish()
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
                }
            }
        })
        request.executeAsync()
    }

    override fun onError(error: FacebookException?) {
        toast("Connect failed")
    }

    override fun onCancel() {
        toast("Cancel")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun loginFacebook(view: View) {
        val loginManager: LoginManager = LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, this)
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }
}
