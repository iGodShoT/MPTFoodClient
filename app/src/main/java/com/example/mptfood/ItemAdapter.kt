package com.example.mptfood

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mptfood.Models.Product

class ItemAdapter(private val context: Context?, private val products: List<Product>, val listener: (Product) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ImageViewHolder>(){
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodTitle = view.findViewById<TextView>(R.id.ProductTitle)
        val foodPrice = view.findViewById<TextView>(R.id.ProductPrice)
        val imageView = view.findViewById<ImageView>(R.id.Catalog)

        fun bindView(food: Product, listener: (Product) -> Unit) {
            foodTitle.text = food.Name
            foodPrice.text = food.Price.toString()
            itemView.setOnClickListener { listener(food) }
            val imageBytes = Base64.decode(food.Image, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imageView.setImageBitmap(decodedImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
            ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_list_item, parent, false))

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindView(products[position], listener)
    }


}