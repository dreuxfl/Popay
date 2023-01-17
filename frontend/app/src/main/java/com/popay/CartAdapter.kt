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


class CartAdapter(private val CartItems: ArrayList<Product>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>(){

    private var baseUrl : String? = null

    private var listener: UpdateTotalQuantityListener? = null

    fun setUpdateTotalQuantityListener(listener: UpdateTotalQuantityListener) {
        this.listener = listener
    }


    interface UpdateTotalQuantityListener {
        fun onTotalQuantityUpdate(totalQuantity: Double)
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartItemCaption: TextView = view.findViewById(R.id.cart_item_caption)
        val cartItemPrice: TextView = view.findViewById(R.id.cart_item_price)
        val cartItemQuantity: TextView = view.findViewById(R.id.cart_item_quantity)
        val addBtn: AppCompatImageButton = view.findViewById(R.id.cart_item_plus)
        val minusBtn: AppCompatImageButton  = view.findViewById(R.id.cart_item_minus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val cartItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return ViewHolder(cartItemView)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        baseUrl = holder.itemView.context.getString(R.string.baseUrl)

        val sharedPreferences: SharedPreferences? = holder.itemView.context.getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)
        val queue = Volley.newRequestQueue(holder.itemView.context)

        holder.cartItemCaption.text = CartItems[position].name
        holder.cartItemPrice.text = String.format("%.2f", (CartItems[position].price * CartItems[position].quantity)) + "€"
        holder.cartItemQuantity.text = CartItems[position].quantity.toString()

        holder.addBtn.setOnClickListener {
            val url = baseUrl + "/cart_product/${CartItems[position].id}"

            val params = HashMap<String, Int>()
            params["quantity"] = 1

            val addQuantityRequest = object : JsonObjectRequest(
                Method.POST, url, JSONObject((params as Map<*, *>?)!!),
                { response ->
                    CartItems[position].quantity += 1
                    holder.cartItemQuantity.text = CartItems[position].quantity.toString()
                    holder.cartItemPrice.text = String.format("%.2f", (CartItems[position].price * CartItems[position].quantity)) + "€"
                    var totalQuantity = 0.0
                    for (product in CartItems) {
                        totalQuantity += product.price * product.quantity
                    }
                    listener?.onTotalQuantityUpdate(totalQuantity)
                },
                {
                    println("ERRR ${it}")
                },

                ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params1: MutableMap<String, String> = HashMap()
                    params1["Authorization"] = "Bearer $token"
                    return params1
                }
            }
            queue.add(addQuantityRequest)
        }

        holder.minusBtn.setOnClickListener {
            val url = baseUrl + "/cart_product/${CartItems[position].id}"

            val params = HashMap<String, Int>()
            params["quantity"] = -1
            val addQuantityRequest = @SuppressLint("SetTextI18n")
            object : JsonObjectRequest(
                Method.POST, url, JSONObject((params as Map<*, *>?)!!),
                { response ->
                    CartItems[position].quantity -= 1
                    holder.cartItemQuantity.text = CartItems[position].quantity.toString()
                    holder.cartItemPrice.text = String.format("%.2f", (CartItems[position].price * CartItems[position].quantity)) + "€"
                    var totalQuantity = 0.0
                    for (product in CartItems) {
                        totalQuantity += product.price * product.quantity
                    }
                    listener?.onTotalQuantityUpdate(totalQuantity)
                },
                {
                    println("ERRR ${it}")
                },

                ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params1: MutableMap<String, String> = HashMap()
                    params1["Authorization"] = "Bearer $token"
                    return params1
                }
            }
            queue.add(addQuantityRequest)
        }

    }
    override fun getItemCount(): Int {
        return CartItems.size
    }

    fun getTotalAmount(): Double {
        var totalAmount = 0.0
        for (item in CartItems) {
            totalAmount += item.price * item.quantity
        }
        return totalAmount
    }
}