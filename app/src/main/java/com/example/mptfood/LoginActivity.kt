package com.example.mptfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.*
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import com.example.mptfood.Models.Client
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import io.paperdb.Paper
import kotlinx.coroutines.handleCoroutineException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    var RC_SIGN_IN = 0
    lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Paper.init(this@LoginActivity)
        loginBtn = findViewById(R.id.registrBtn)

        var googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()


        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        loginBtn.setOnClickListener{
            signIn()
        }

    }

    private fun signIn() {
        var signInIntent : Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            var task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            d("asdasdasd", task.result.email.toString())
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            if (task.result.email!!.contains("@mpt.ru")){
                var account : GoogleSignInAccount = task.getResult(ApiException::class.java)
                var client = Client()
                var servBuild = ServiceBuilder.buildService(ApiServices::class.java)
                var requestCall = servBuild.getByEmail(task.result.email!!)
                requestCall.enqueue(object : Callback<Client>{
                    override fun onResponse(call: Call<Client>, response: Response<Client>) {
                        if (response.isSuccessful){
                            client = response.body()!!
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }else{
                            var newClient = Client(0, account.familyName, account.givenName, account.email)
                            var servBuild = ServiceBuilder.buildService(ApiServices::class.java)
                            var requestCall = servBuild.addClient(newClient)
                            requestCall.enqueue(object : Callback<Client>{
                                override fun onResponse(
                                    call: Call<Client>,
                                    response: Response<Client>
                                ) {
                                    if (response.isSuccessful){
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    }
                                }

                                override fun onFailure(call: Call<Client>, t: Throwable) {
                                    mGoogleSignInClient.signOut()
                                    Toast.makeText(this@LoginActivity, "Ошибка со стороны сервера", Toast.LENGTH_LONG).show()
                                }

                            })
                        }
                    }

                    override fun onFailure(call: Call<Client>, t: Throwable) {
                        mGoogleSignInClient.signOut()
                        Toast.makeText(this@LoginActivity, "Ошибка со стороны сервера", Toast.LENGTH_LONG).show()
                    }

                })
            }else{
                    Toast.makeText(this, "Авторизуйтесь с помощью почты МПТ", Toast.LENGTH_LONG).show()
                    mGoogleSignInClient.signOut()
            }
        }catch (e : ApiException){
            Log.e("Google err", e.getStatusCode().toString())
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        var account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
        super.onStart()
    }

}