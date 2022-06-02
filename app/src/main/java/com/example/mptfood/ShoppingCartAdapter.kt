package com.example.mptfood

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mptfood.R
import com.example.mptfood.Models.ShoppingCart
import com.example.mptfood.Models.CartItem
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.cart_items.view.*


class ShoppingCartAdapter(var cart: Context?, var cartItem: List<CartItem>, val onClickDelete: (Int) -> Unit) :
    RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {

    private var listData = cartItem


   inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(cartItem: CartItem, index : Int){

            itemView.ProductName.text = cartItem.Product!!.Name
            itemView.ProductPrice.text = cartItem.Product!!.Price.toString()
            var num = cartItem.Quantity
            itemView.txtNumbers_cart.text = num.toString()
            itemView.ProductPrice.text = (num*cartItem!!.Product!!.Price).toString()
            if (cartItem!!.Product!!.Image != null){
                val imageBytes = Base64.decode(cartItem!!.Product!!.Image, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                itemView.ProductImage.setImageBitmap(decodedImage)
            }
            d("as", "${num}")
            Observable.create(ObservableOnSubscribe<MutableList<CartItem>> {


                itemView.Delete.setOnClickListener { view ->
                    d("as", "qw ${cartItem.Product!!.Name}")
                    val item = CartItem(cartItem.Product)
                    ShoppingCart.removeItem(item)
                    Toast.makeText(itemView.context, "${cartItem.Product!!.Name} удален", Toast.LENGTH_LONG).show()
                    onClickDelete(index)
                    it.onNext(ShoppingCart.getCart())

                }

                fun countPrice() {
                    itemView.ProductPrice.setText((num * cartItem.Product!!.Price).toString())
                }

                fun setText() {
                    itemView.txtNumbers_cart.setText(num.toString()+"")
                    cartItem.Quantity = num
                }
                itemView.Minus.setOnClickListener {view ->
                    val item = CartItem(cartItem.Product)
                    if (num > 1){
                        itemView.Plus.isEnabled = true
                        num--
                        countPrice()
                        setText()
                        ShoppingCart.removeCount(item)
                    }
                    else if (num == 1){
                        itemView.Minus.isEnabled = false
                    }

                    it.onNext(ShoppingCart.getCart())
                    notifyDataSetChanged()
                }
                itemView.Plus.setOnClickListener { view ->
                    val item = CartItem(cartItem.Product)
                    if (num < 99){
                        itemView.Minus.isEnabled = true
                        num++
                        setText()
                        countPrice()
                        ShoppingCart.addCount(item)
                    } else if (num == 99){
                        itemView.Plus.isEnabled = false
                    }

                    it.onNext(ShoppingCart.getCart())
                    notifyDataSetChanged()
                }
            }).subscribe()


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(cart).inflate(R.layout.cart_items, parent,false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ShoppingCartAdapter.ViewHolder, position: Int) {
        holder.bindItem(listData[position], position)

    }

    fun setItem(items: List<CartItem>){
        listData = items
        notifyDataSetChanged()
    }

}