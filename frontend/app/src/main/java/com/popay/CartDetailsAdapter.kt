package com.popay

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.popay.entities.Product
import org.json.JSONObject


class CartDetailsAdapter(private val CartItems: ArrayList<Product>) :
    RecyclerView.Adapter<CartDetailsAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartItemCaption: TextView = view.findViewById(R.id.cart_item_detail_caption)
        val cartItemPrice: TextView = view.findViewById(R.id.cart_item_detail_price)
        val cartItemQuantity: TextView = view.findViewById(R.id.cart_item_detail_quantity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val cartItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_detail, parent, false)
        return ViewHolder(cartItemView)

    }


    override fun getItemCount(): Int {
        return CartItems.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cartItemCaption.text = CartItems[position].name
        holder.cartItemPrice.text = String.format("%.2f", (CartItems[position].price * CartItems[position].quantity)) + "€"
        holder.cartItemQuantity.text = CartItems[position].quantity.toString()
    }
}