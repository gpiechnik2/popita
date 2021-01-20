package com.example.popitaapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.popitaapp.BuildConfig
import com.example.popitaapp.R
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login_choices.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class Login_choices : AppCompatActivity() {

    //google provider
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    //facebook provider
    lateinit var callbackManager: CallbackManager
    private val FB_SIGN_IN = 64206

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_choices)

        //back button
        var back_btn = findViewById(R.id.back_btn) as Button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }

        //email choice button
        var email_btn = findViewById(R.id.email_btn) as Button
        email_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, Login_with_email::class.java)
            startActivity(intent);
        }

        //facebook provider choice button

        callbackManager = CallbackManager.Factory.create()

        facebook_btn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        }

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d("TAG", "Success Login")
                // Get User's Info
                getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)

            }

            override fun onCancel() {
                Toast.makeText(this@Login_choices, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@Login_choices, exception.message, Toast.LENGTH_LONG).show()
            }
        })


        //google provider choice button

        val idToken = getString(R.string.web_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("$idToken")
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        google_btn.setOnClickListener {
            signInByGoogle()
        }
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(token,
            "/$userId/",
            parameters,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject

                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }

                val access_token = token?.token.toString()

                //Log.i("FACEBOOK ACCES TOKEN: ", token.toString())

                // Facebook Id
                if (jsonObject.has("id")) {
                    val facebookId = jsonObject.getString("id")
                    Log.i("Facebook Id: ", facebookId.toString())

                    validateAccesToken(access_token, facebookId)

                } else {
                    Log.i("Facebook Id: ", "Not exists")
                }


            }).executeAsync()
    }

    private fun validateAccesToken(access_token: String, facebookId: String) {

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/auth/facebook/token/login/"

        val jsonObject = JSONObject()
        jsonObject.put("access_token", access_token)
        jsonObject.put("facebookId", facebookId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@Login_choices.runOnUiThread(Runnable {
                    Toast.makeText(this@Login_choices, "Creating account failed.", Toast.LENGTH_SHORT).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)
                    val auth_token = jsonObject.get("token")

                    //instance and save method
                    val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("auth_token", auth_token.toString())
                    editor.commit()

                    //on-production only
                    //val authorization_token = sharedPreference.getString("auth_token", null)
                    //println(authorization_token)

                    this@Login_choices.runOnUiThread(Runnable {
                        Toast.makeText(this@Login_choices, "Successful log in.", Toast.LENGTH_SHORT).show()
                    })

                    //room activity
                    val intent = Intent(this@Login_choices, RoomActivity::class.java)
                    startActivity(intent);

                }

                else if (response.code == 400) {
                    this@Login_choices.runOnUiThread(Runnable {
                        Toast.makeText(this@Login_choices, "Unable to log in with provided credentials.", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        })
    }

    private fun signInByGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
                signInIntent, RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //facebook provider
        if (requestCode == FB_SIGN_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

        //google provider
        if (requestCode == RC_SIGN_IN) {
            val task =
                    GoogleSignIn.getSignedInAccountFromIntent(data)

            handleSignInResult(task)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                    ApiException::class.java
            )

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

            //Perform generating auth token on server side
            loginWithGoogleToken(googleIdToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                    "failed code=", e.statusCode.toString()
            )
        }
    }

    fun loginWithGoogleToken(googleIdToken: String) {
        val ip = getString(R.string.server_ip)
        val url = "http://$ip/auth/google/token/login/"

        val jsonObject = JSONObject()
        jsonObject.put("id_token", googleIdToken)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@Login_choices.runOnUiThread(Runnable {
                    Toast.makeText(this@Login_choices, "Creating account failed.", Toast.LENGTH_SHORT).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)
                    val auth_token = jsonObject.get("token")

                    //instance and save method
                    val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("auth_token", auth_token.toString())
                    editor.commit()

                    //on-production only
                    //val authorization_token = sharedPreference.getString("auth_token", null)
                    //println(authorization_token)

                    this@Login_choices.runOnUiThread(Runnable {
                        Toast.makeText(this@Login_choices, "Successful log in.", Toast.LENGTH_SHORT).show()
                    })

                    //room activity
                    val intent = Intent(this@Login_choices, RoomActivity::class.java)
                    startActivity(intent);

                }

                else if (response.code == 400) {
                    this@Login_choices.runOnUiThread(Runnable {
                        Toast.makeText(this@Login_choices, "Unable to log in with provided credentials.", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        })
    }
}