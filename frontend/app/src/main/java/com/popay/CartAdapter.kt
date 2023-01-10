package com.popay

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.popay.entities.Product


class CartAdapter(private val CartItems: ArrayList<Product>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartItemCaption: TextView = view.findViewById(R.id.cart_item_caption)
        val cartItemPrice: TextView = view.findViewById(R.id.cart_item_price)
        val cartItemDesc: TextView = view.findViewById(R.id.cart_item_description)
        val cartItemQuantity: TextView = view.findViewById(R.id.cart_item_quantity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val cartItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return ViewHolder(cartItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.cartItemCaption.text = CartItems[position].name
        holder.cartItemPrice.text = CartItems[position].price.toString()
        holder.cartItemDesc.text = CartItems[position].description
        holder.cartItemQuantity.text = CartItems[position].quantity.toString()
    }

    override fun getItemCount(): Int {
        Log.e("ttttttfffft",CartItems.size.toString())

        return CartItems.size
    }
}