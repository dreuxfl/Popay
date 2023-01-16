package com.popay

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.popay.entities.Product


private var baseUrl : String? = null
private var cartList : ArrayList<Product> = ArrayList()

class CartDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_details)
        baseUrl = this.getString(R.string.baseUrl)
        val cartListRecyclerView = findViewById<RecyclerView>(R.id.cart_detail_list)

        cartListRecyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreferences: SharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null).toString()

        var cartId = ""
        val bundle = intent.extras
        if (bundle != null) {
            Log.d("Last Bug", bundle.getInt("cartId").toString())
            cartId = bundle.getInt("cartId").toString()
        }

        val queue = Volley.newRequestQueue(this)
        val arrayRequest : JsonArrayRequest = object : JsonArrayRequest(
            Method.GET,
            "$baseUrl/cart_products/${cartId}",
            null,
            { response ->
                try{
                    cartList = arrayListOf()
                    for(i in 0 until response.length()){
                        val cartItemProduct = response.getJSONObject(i).getJSONObject("product")
                        val cartItemId = cartItemProduct.getInt("id")
                        val cartItemName = cartItemProduct.getString("caption")
                        val cartItemPrice = cartItemProduct.getDouble("price")
                        val cartItemStock = response.getJSONObject(i).getInt("quantity")
                        cartList.add(Product(cartItemId, cartItemName, cartItemPrice, cartItemStock))
                    }
                    cartListRecyclerView.adapter = CartDetailsAdapter(cartList)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Fatal error, call dev", Toast.LENGTH_LONG).show()
                }
            },
            {
                println("ERRR ${it}")
            },

            ) {
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(arrayRequest)
    }


}