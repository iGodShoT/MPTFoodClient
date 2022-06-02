package com.example.mptfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import com.example.mptfood.Models.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class   SplashScreenActivity : AppCompatActivity() {

    private val handler = Handler()


    var productList : ArrayList<Product> = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        allowAccess()

    }


    private fun allowAccess() {

        val retrofit = Retrofit.Builder()
            .baseUrl(ServiceBuilder.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiServices::class.java)
        api.fetchAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, e: Throwable) {
                Log.e("Error", e.toString())
            }

            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                for (i in 0..response.body()!!.size - 1){
                    //d("as","tel : ${response.body()!![i].user_Surname}")
                    productList.add(
                        Product(
                            response.body()!![i].ID,
                            response.body()!![i].Name,
                            response.body()!![i].Price,
                            response.body()!![i].QuantityAvailable,
                            response.body()!![i].Image,
                        )
                    )
                }

            }

        })
    }
    private val runnable = Runnable {
        if (!isFinishing){
            //Paper.book("cart").destroy()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
            Toast.makeText(this, "Добро пожаловать", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 2000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}