package com.example.mptfood

import android.content.Context
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mptfood.Models.ContentClass

class ProductInOrderAdapter(var context: Context, var listProd : ArrayList<ContentClass>) : RecyclerView.Adapter<ProductInOrderAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        var textName = view.findViewById<TextView>(R.id.prod_name)
        var textPrice = view.findViewById<TextView>(R.id.prod_price)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.product_inside_order_history, parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textName.text = listProd[position].product.Name
        holder.textPrice.text = "${listProd[position].quantity} X ${listProd[position].product.Price} ₽"
        d("asdasdasd", listProd[position].product.Name)
    }

    override fun getItemCount(): Int = listProd.size


}