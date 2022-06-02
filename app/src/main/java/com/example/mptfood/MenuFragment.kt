package com.example.mptfood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import com.example.mptfood.Models.Product
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MenuFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl(ServiceBuilder.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiServices::class.java)
        api.fetchAllProducts().enqueue(object : retrofit2.Callback<List<Product>> {
            override fun onFailure(call: retrofit2.Call<List<Product>>, e: Throwable) {
                Log.e("Error", e.toString())
            }

            override fun onResponse(
                call: retrofit2.Call<List<Product>>,
                response: retrofit2.Response<List<Product>>
            ) {
                activity?.runOnUiThread {
                    showData(response.body()!!)
                }
                progressBar.visibility = View.INVISIBLE
            }

        })
    }

    private fun showData(products: List<Product>) {
        recyclerView.layoutManager =LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ItemAdapter(context, products) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("OBJECT_INTENT", it.ID)
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val menuView: View = inflater.inflate(R.layout.fragment_menu, container, false)
        recyclerView = menuView.findViewById(R.id.Menu)
        progressBar = menuView.findViewById(R.id.ProgressBar)
        progressBar.visibility = View.VISIBLE
        return menuView
    }

}