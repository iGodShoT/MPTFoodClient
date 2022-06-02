package com.example.mptfood.Models

import android.content.Context
import android.util.Log.d
import io.paperdb.Paper

class ShoppingCart {


    companion object{

        fun addItem(cartItem: CartItem, quantityOf : Int){

            val cart = getCart()
            val targetItem = cart.singleOrNull{ it.Product!!.ID == cartItem.Product!!.ID}

            if (targetItem == null)
            {
                cartItem.Quantity = quantityOf
                cart.add(cartItem)
            }
            else
                targetItem.Quantity += quantityOf

            saveCart(cart)
        }

        fun removeItem(cartItem: CartItem){
            val cart = getCart()

            val targetItem = cart.singleOrNull{ it.Product!!.ID == cartItem.Product!!.ID}

            if (targetItem != null)
                cart.remove(targetItem)

            saveCart(cart)
        }

        fun addCount(cartItem: CartItem){
            val cart = getCart()
            val targetItem = cart.singleOrNull{ it.Product!!.ID == cartItem.Product!!.ID}

            if (targetItem !=null)
                targetItem.Quantity++

            saveCart(cart)
        }

        fun removeCount(cartItem: CartItem){
            val cart = getCart()

            val targetItem = cart.singleOrNull{ it.Product!!.ID == cartItem.Product!!.ID}
            if (targetItem !=null)
            {
                targetItem.Quantity--
                d("as", "qq: ${targetItem.Quantity}")
            }
            saveCart(cart)
        }

        fun saveCart(cart: MutableList<CartItem>) {
            Paper.book("cart").write("cart", cart)
        }

        fun getCart() : MutableList<CartItem> {
            return Paper.book("cart").read("cart", mutableListOf())
        }


        fun getShoppingCartSize() : Int {
            var cartSize = 0
            getCart().forEach {
                cartSize += it.Quantity
            }
            return cartSize
        }

    }

}