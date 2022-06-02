package com.example.mptfood

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import com.example.mptfood.Models.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS


class CartFragment : Fragment() {

    private lateinit var totalPriceTV: TextView
    private lateinit var cartRv : RecyclerView
    private lateinit var nextBtn : Button
    private lateinit var dltBtn : ImageButton
    private lateinit var adapter: ShoppingCartAdapter
    private lateinit var crtFrg : CartFragment
    private lateinit var cartView: View
    private lateinit var data : MutableList<CartItem>
    private var userByEmail = Client()
    private var emailOfUser : String? = ""
    lateinit var mGoogleSignInClient : GoogleSignInClient
    var lastOrder : Order = Order()
    var totalPrice: Double? = 0.0
    var dateChoose : String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)
        emailOfUser = account!!.email

        data = ShoppingCart.getCart() as MutableList<CartItem>

        adapter = ShoppingCartAdapter(context,data){index->deleteItem(index)}
        adapter.notifyDataSetChanged()
        cartRv.adapter = adapter
        cartRv.layoutManager = LinearLayoutManager(activity)

        Paper.init(context)
        nextBtn.setOnClickListener {
            val count = ShoppingCart.getShoppingCartSize()

            if (count < 1){
                Toast.makeText(context, "Ваша корзина пуста", Toast.LENGTH_LONG).show()
            }else if (count >= 1){
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_time, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setTitle("Выберите время заказа")
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                val nowBtn = mDialogView.findViewById<Button>(R.id.now_time)
                val chooseBtn = mDialogView.findViewById<Button>(R.id.choose_time)
                val closeBtn = mDialogView.findViewById<AppCompatButton>(R.id.close_dialog)
                closeBtn.setOnClickListener {
                    mAlertDialog.dismiss()
                }
                nowBtn.setOnClickListener {
                    getByEmail(true)
                }

                val c = Calendar.getInstance()




                chooseBtn.setOnClickListener {

                    val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        c.set(Calendar.YEAR, year)
                        c.set(Calendar.MONTH, monthOfYear)
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        if (c.time <= Calendar.getInstance().time){
                            Toast.makeText(context, "Некорректная дата", Toast.LENGTH_SHORT).show()
                        }else {
                            val myFormat = "yyyy-MM-dd'T'HH:mm:ss" // mention the format you need
                            val sdf = SimpleDateFormat(myFormat, Locale.ROOT)
                            dateChoose = sdf.format(c.time)
                            getByEmail(false)
                            mAlertDialog.dismiss()
                        }
                    }

                    DatePickerDialog(context!!, dateSetListener,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show()
                    }
            }
        }

        Thread(Runnable {
            var i=0;
            while(i<Int.MAX_VALUE){
                i++
                activity?.runOnUiThread(java.lang.Runnable {
                    countPrice()
                })
                Thread.sleep(1000)
            }
        }).start()
        countPrice()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Paper.init(context)
        cartView = inflater.inflate(R.layout.fragment_cart, container, false)
        crtFrg = CartFragment()
        val cartItem = inflater.inflate(R.layout.cart_items,container,false)
        var googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context!!, googleSignInOption)
        totalPriceTV = cartView.findViewById<TextView>(R.id.Total)
        cartRv = cartView.findViewById<RecyclerView>(R.id.Cart)
        nextBtn = cartView.findViewById<Button>(R.id.Confirm)
        dltBtn = cartItem.findViewById<ImageButton>(R.id.Delete)
        return cartView
    }

    fun deleteItem(index: Int){
        data.removeAt(index)
        adapter.setItem(data)
    }

    fun countPrice(){
        totalPrice = ShoppingCart.getCart()
                .fold(0.toDouble()) {acc, cartItem -> acc + cartItem.Quantity.times(cartItem.Product!!.Price!!.toDouble()) }
        totalPriceTV.text   = "Сумма заказа: $totalPrice руб."
    }

    fun getByEmail(now: Boolean){
        var servBuilder = ServiceBuilder.buildService(ApiServices::class.java)
        var requsetCall = servBuilder.getByEmail(emailOfUser!!)
        requsetCall.enqueue(object : Callback<Client>{
            override fun onResponse(call: Call<Client>, response: Response<Client>) {
                   if (response.isSuccessful){
                       //d("user3", "${response.body()!!.ID} ${response.body()!!.Email}")
                       userByEmail = response.body()!!
                       //d("user1", "${userByEmail.Email.toString()} ${userByEmail.ID}")
                       if (now) {
                           Add(true)
                       }else{
                           Add(false)
                       }
                   }
            }

            override fun onFailure(call: Call<Client>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    fun Add(now : Boolean){
        if (android.os.Build.VERSION.SDK_INT >= 26) {
           if (now){
               d("user2", "${userByEmail.Email.toString()} ${userByEmail.ID}")
               var neworder : Order = Order(0,userByEmail.ID!!, 2, 1, LocalDate.now().toString() ,totalPrice!!)
               val servBuild = ServiceBuilder.buildService(ApiServices::class.java)
               val requestCall = servBuild.addOrder(neworder)
               requestCall.enqueue(object : Callback<Order>{
                   override fun onResponse(call: Call<Order>, response: Response<Order>) {
                       if (response.isSuccessful){
                           getLastOrder(response.body()!!.ID)
                       }
                   }
                   override fun onFailure(call: Call<Order>, t: Throwable) {
                       Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_LONG).show()
                   }
               })
           }else{
               var order : Order = Order(0,userByEmail.ID!!, 2, 1, dateChoose ,totalPrice!!)
               val servBuild = ServiceBuilder.buildService(ApiServices::class.java)
               val requestCall = servBuild.addOrder(order)
               requestCall.enqueue(object : Callback<Order>{
                   override fun onResponse(call: Call<Order>, response: Response<Order>) {
                       if (response.isSuccessful){
                           getLastOrder(response.body()!!.ID)
                       }
                   }
                   override fun onFailure(call: Call<Order>, t: Throwable) {
                       Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_LONG).show()
                   }
               })
           }
        }
    }

    fun getLastOrder(id : Int){
        var servBuilder = ServiceBuilder.buildService(ApiServices::class.java)
        var requsetCall = servBuilder.getOrderLast()
        requsetCall.enqueue(object : Callback<Order>{


            override fun onResponse(call: Call<Order>, response: Response<Order>) {
                if (response.isSuccessful){
                    lastOrder = response.body()!!
                    addOrderProducts()
                }
            }

            override fun onFailure(call: Call<Order>, t: Throwable) {
                Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_LONG).show()
            }

        })
    }

    fun addOrderProducts(){
        var count = 0
        var listProd = ShoppingCart.getCart()
        for (i in listProd){
            var ordProd = OrderContent(0,lastOrder.ID, i.Product!!.ID, i.Quantity)
            var servBuild = ServiceBuilder.buildService(ApiServices::class.java)
            var requsetCall = servBuild.addOrderProduct(ordProd)
            requsetCall.enqueue(object : Callback<OrderContent>{
                override fun onResponse(
                    call: Call<OrderContent>,
                    response: Response<OrderContent>) {
                    count++
                    Toast.makeText(context, "Заказ успешно оформлен", Toast.LENGTH_LONG).show()
                    Paper.book("cart").destroy()
                    startActivity(Intent(context, MainActivity::class.java))
                }
                override fun onFailure(call: Call<OrderContent>, t: Throwable) {
                    Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

}