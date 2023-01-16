package com.popay

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.popay.databinding.FragmentHomeBinding
import com.popay.entities.Cart
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreditFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    private lateinit var cartsListRecyclerView: RecyclerView
    private var baseUrl : String? = null
    private var carts : ArrayList<Cart> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        baseUrl = context?.getString(R.string.baseUrl)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        cartsListRecyclerView = binding!!.cartList
        cartsListRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCarts()
    }


    private fun getCarts() {
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)
        val queue = Volley.newRequestQueue(context)

        val arrayRequest : JsonArrayRequest = object : JsonArrayRequest(
            Method.GET,
            "$baseUrl/carts",
            null,
            { response ->
                try{
                    carts = arrayListOf()
                    for(i in 0 until response.length()){
                        val cartId = response.getJSONObject(i).getInt("id")
                        val cartPrice = response.getJSONObject(i).getDouble("totalAmount")
                        val dateString = response.getJSONObject(i).getString("paymentDate").format()
                        println(dateString)
                        val paymentDate = LocalDateTime.parse(dateString)
                        carts.add(Cart(cartId, paymentDate, cartPrice))
                    }
                    cartsListRecyclerView.adapter = CartsHistoryAdapter(carts)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Fatal error, call dev", Toast.LENGTH_LONG).show()
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