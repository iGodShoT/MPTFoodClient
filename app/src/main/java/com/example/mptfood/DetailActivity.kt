package com.example.mptfood

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import com.example.mptfood.Models.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    var num : Int = 1
    lateinit var product : Product
    var ID : Int = 0
    lateinit var title : TextView
    lateinit var product_img : ImageView
    lateinit var product_price : TextView
    lateinit var add_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        title = findViewById(R.id.ProductName)
        //val description = findViewById<TextView>(R.id.ProductDescription)
        product_img = findViewById(R.id.ProductImage)
        product_price = findViewById(R.id.Price)
        add_button = findViewById(R.id.AddToCart)

        ID = intent.getIntExtra("OBJECT_INTENT", 0).toInt()
        GetProduct()


        Observable.create(ObservableOnSubscribe<MutableList<CartItem>> {

            add_button.setOnClickListener { view ->

                val item = CartItem(product)
                ShoppingCart.addItem(item, num)
                Toast.makeText(this, "${product?.Name} добавлен в корзину", Toast.LENGTH_LONG).show()
                it.onNext(ShoppingCart.getCart())

            }
        }).subscribe{ cart ->
            var quantity = CartItem(product).Quantity

            cart.forEach{cartItem ->
                quantity += num
            }

        }.dispose()
    }

    fun addProd(view: View){
        if (num < 99){
            num++
        }
        setText()
        countPrice()
    }

    private fun countPrice() {
        Price.setText((num * product!!.Price).toString())
    }

    fun remProd(view: View) {
        if (num > 1){
            num--
        }
        setText()
        countPrice()
    }

    private fun setText() = Quantity.setText(num.toString()+"")

    fun GetProduct(){
        var reqBuild = ServiceBuilder.buildService(ApiServices::class.java)
        var reqCall = reqBuild.getProductById(ID)
        reqCall.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful){
                    product = response.body()!!
                    setInfo()
                }

            }

            override fun onFailure(call: Call<Product>, t: Throwable) { }
        })

    }

    private fun setInfo() {
        title.text = product?.Name
        product_price.text = product?.Price.toString()
        val imageBytes = Base64.decode(product?.Image, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        product_img.setImageBitmap(decodedImage)
    }

}