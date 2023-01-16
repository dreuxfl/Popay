package com.popay

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.popay.entities.Cart
import java.time.format.DateTimeFormatter


class CartsHistoryAdapter(private val CartItems: ArrayList<Cart>) :
    RecyclerView.Adapter<CartsHistoryAdapter.ViewHolder>(){


    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val cartId: TextView = view.findViewById(R.id.cart_id)
        val cartPaymentDate: TextView = view.findViewById(R.id.cart_payment_date)
        val cartPrice: TextView = view.findViewById(R.id.cart_price)
        val cartPaymentTime: TextView = view.findViewById(R.id.cart_payment_time)
        val details: TextView = view.findViewById(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            val cartItemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_history_item, parent, false)
            return ViewHolder(cartItemView)

        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cartId.text = CartItems[position].id.toString()
            holder.cartPaymentDate.text = CartItems[position].date.toLocalDate().toString()
            holder.cartPaymentTime.text = CartItems[position].date.toLocalTime().format(
                DateTimeFormatter.ofPattern("HH:mm")).toString()
            holder.cartPrice.text = String.format("%.2f", CartItems[position].price) + "€"

            holder.details.setOnClickListener {
                val intent = Intent(holder.itemView.context, CartDetails::class.java)
                intent.putExtra("cartId", CartItems[position].id)
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return CartItems.size
        }
}