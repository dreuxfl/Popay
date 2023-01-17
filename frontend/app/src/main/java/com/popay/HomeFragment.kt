package com.popay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.popay.databinding.FragmentHomeBinding
import com.popay.entities.Product
import org.json.JSONObject

class HomeFragment : Fragment(), CartAdapter.UpdateTotalQuantityListener {

    private var binding: FragmentHomeBinding? = null

    private lateinit var cartListRecyclerView: RecyclerView
    private val token : SharedPreferences? = null
    private var baseUrl : String? = null
    private var cartList : ArrayList<Product> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        baseUrl = context?.getString(R.string.baseUrl)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val placeholder = binding!!.PlaceHolder

        cartListRecyclerView = binding!!.cartList


        cartListRecyclerView.layoutManager = LinearLayoutManager(context)
        if (cartList.isEmpty()) {
            placeholder.visibility = View.VISIBLE
        } else {
            placeholder.visibility = View.GONE
        }



        binding!!.scanBtn.setOnClickListener {
            val intent = Intent (activity, QRCodeScannerActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }


        binding!!.nfcBtn.setOnClickListener {
            val intent = Intent (activity, NfcReaderActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }

        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
    }


    private fun getUserData() {
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)
        val queue = Volley.newRequestQueue(context)

        val arrayRequest : JsonArrayRequest = @SuppressLint("SetTextI18n")
        object : JsonArrayRequest(
            Method.GET,
            "$baseUrl/cart_products",
            null,
            { response ->
                try{
                    cartList = arrayListOf()

                    if(response.length() > 0) {
                        binding!!.cartItemTotalQuantityValue.text = String.format(
                            "%.2f",
                            response.getJSONObject(0).getJSONObject("cart").getDouble("totalAmount")
                        ) + "€"
                    } else {
                        binding!!.cartItemTotalQuantityValue.text = "0.00€"
                    }
                    for(i in 0 until response.length()){
                        val cartItemProduct = response.getJSONObject(i).getJSONObject("product")
                        val cartItemId = cartItemProduct.getInt("id")
                        val cartItemName = cartItemProduct.getString("caption")
                        val cartItemPrice = cartItemProduct.getDouble("price")
                        val cartItemStock = response.getJSONObject(i).getInt("quantity")
                        cartList.add(Product(cartItemId, cartItemName, cartItemPrice, cartItemStock))
                    }
                    cartListRecyclerView.layoutManager = LinearLayoutManager(context)
                    if (cartList.isEmpty()) {
                        binding!!.PlaceHolder.visibility = View.VISIBLE
                    } else {
                        binding!!.PlaceHolder.visibility = View.GONE
                    }

                    val adapter = CartAdapter(cartList)
                    adapter.setUpdateTotalQuantityListener(this)
                    cartListRecyclerView.adapter = adapter

                } catch (e: Exception) {
                    binding!!.cartItemTotalQuantityValue.text = "0.00€"
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


        val urlCreateCart = "$baseUrl/cart"
        val postCart : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, urlCreateCart, JSONObject(), { response1 ->
                queue.add(arrayRequest)
                Log.d("RESPONSE POST CART",response1.toString())
            }, {
                Log.d("ERROR POST CART", it.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val params2: MutableMap<String, String> = HashMap()
                params2["Authorization"] = "Bearer $token"
                return params2
            }
        }

        val urlCheckCart = "$baseUrl/cart"
        val getCurrentCart : JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, urlCheckCart, null, { response1 ->
                queue.add(arrayRequest)
                Log.d("RESPONSE GET CART",response1.toString())
            }, {
                queue.add(postCart)
                queue.add(arrayRequest)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val params2: MutableMap<String, String> = HashMap()
                params2["Authorization"] = "Bearer $token"
                return params2
            }
        }
        queue.add(getCurrentCart)



    }

    override fun onTotalQuantityUpdate(totalQuantity: Double) {
        binding?.cartItemTotalQuantityValue?.text = String.format("%.2f", totalQuantity) + "€"
    }
}
