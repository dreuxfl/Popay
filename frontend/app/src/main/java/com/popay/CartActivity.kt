package com.popay

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.auth0.android.jwt.JWT
import com.popay.entities.User
import org.json.JSONObject
import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class CartActivity : AppCompatActivity() {
    private var baseUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        baseUrl = getString(R.string.baseUrl)

        val sharedPreferences: SharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val queue = Volley.newRequestQueue(this)

        val stringRequest :  JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            "$baseUrl/cart",
            null,
            { response ->
                if(response.has("token")){

                

                }else{
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()
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
        queue.add(stringRequest)

    }
}