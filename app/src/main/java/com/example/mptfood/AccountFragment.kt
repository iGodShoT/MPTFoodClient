package com.example.mptfood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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


class AccountFragment : Fragment() {

    private lateinit var exitBtn : Button
    private lateinit var nameSurnam : TextView
    private lateinit var emailText : TextView
    private lateinit var recyclerView : RecyclerView
    lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var adapterOrd : OrderAdapter
    var listOrders : ArrayList<Order> = ArrayList<Order>()
    var listContent : ArrayList<ContentClass> = ArrayList()

    private lateinit var account : GoogleSignInAccount
    var client : Client = Client()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Paper.init(context)
        getUser()



        account = GoogleSignIn.getLastSignedInAccount(context)!!
        if (account != null) {
            nameSurnam.setText(account.displayName.toString())
            emailText.setText(account.email.toString())
        }
        exitBtn.setOnClickListener {


            signOut()

        }


    }


   override fun onResume() {
        adapterOrd.onUpdate(listOrders, listContent)
        super.onResume()
    }

    private fun getOrders(){
        var reqBuild = ServiceBuilder.buildService(ApiServices::class.java)
        var reqCall = reqBuild.fetchAllOrders(client.ID!!)
        reqCall.enqueue(object : Callback<List<Order>>
        {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful){
                    for (i in 0..response.body()!!.size-1){
                        listOrders.add(response.body()!![i])
                    }
                    getContent()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getContent(){
        for (i in 0..listOrders.size-1){
            var orderId : Int = listOrders[i].ID
            var reqBuild = ServiceBuilder.buildService(ApiServices::class.java)
            var reqCall = reqBuild.getOrderProductByOrder(listOrders[i].ID)
            reqCall.enqueue(object : Callback<List<OrderContent>>{
                override fun onResponse(
                    call: Call<List<OrderContent>>,
                    response2: Response<List<OrderContent>>) {
                    for (i in 0..response2.body()!!.size - 1){
                        var quantity = response2.body()!![i].Quantity
                        var product : Product
                        var requsetBuilder = ServiceBuilder.buildService(ApiServices::class.java)
                        var reqCall = requsetBuilder.getProductById(response2.body()!![i].ProductID)
                        reqCall.enqueue(object : Callback<Product>{
                            override fun onResponse(
                                call: Call<Product>,
                                response3: Response<Product>
                            ) {
                                product = response3.body()!!
                                listContent.add(
                                    ContentClass(
                                    product, quantity, orderId
                                    )
                                )
                                Log.d("qqqqqqqqq", listContent.last().product.Name)
                                addToRecycler()
                            }


                            override fun onFailure(call: Call<Product>, t: Throwable) {
                                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
                            }

                        })


                    }
                }

                override fun onFailure(call: Call<List<OrderContent>>, t: Throwable) {
                    Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    private fun getUser(){
        var requestBuild = ServiceBuilder.buildService(ApiServices::class.java)
        var requsetCall = requestBuild.getByEmail(account.email.toString())
        requsetCall.enqueue(object : Callback<Client>{
            override fun onResponse(call: Call<Client>, response: Response<Client>) {
                if (response.isSuccessful){
                    client = response.body()!!
                    getOrders()
                }else{
                    Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Client>, t: Throwable) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addToRecycler(){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapterOrd
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener {
                Toast.makeText(context, "Вы вышли", Toast.LENGTH_LONG).show()
                startActivity(Intent(context, SplashScreenActivity::class.java))
                activity!!.finish()
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val accountView: View = inflater.inflate(R.layout.fragment_account, container, false)
        exitBtn = accountView.findViewById<Button>(R.id.exitBtn)
        nameSurnam = accountView.findViewById<TextView>(R.id.nameSurname)
        emailText = accountView.findViewById<TextView>(R.id.email)
        recyclerView = accountView.findViewById(R.id.OrderRecycler)
        var googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        adapterOrd = OrderAdapter(context!!, listOrders, listContent)
        mGoogleSignInClient = GoogleSignIn.getClient(context!!, googleSignInOption)
        account = GoogleSignIn.getLastSignedInAccount(context)!!
        return accountView
    }

}
