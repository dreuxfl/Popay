package com.popay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.popay.entities.Product
import org.json.JSONObject

class ProductPopup: AppCompatActivity() {


    private var popupText = ""
    private var popupBtnPlus = ""
    private var popupBtnMoins = ""
    private var popupButton = ""
    private var darkStatusBar = false
    private var popUpproduct: Product? = null
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.confirm_product_popup)

        val sharedPreferences: SharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null).toString()

        val productName = findViewById<TextView>(R.id.popup_window_productname)
        val description = findViewById<TextView>(R.id.popup_window_productDesc)
        val price = findViewById<TextView>(R.id.text_price)
        val quantite = findViewById<EditText>(R.id.edit_text_quantite_value)
        val plus = findViewById<ImageButton>(R.id.button_plus)
        val moins = findViewById<ImageButton>(R.id.button_minus)
        val confirmBtn = findViewById<Button>(R.id.popup_window_button)
        val viewWithBorder = findViewById<CardView>(R.id.popup_window_view_with_border)
        val viewBackground = findViewById<ConstraintLayout>(R.id.popup_window_background)

        val bundle = intent.extras
        if (bundle != null) {
            popUpproduct = bundle.getSerializable("product") as Product
        }

        plus.setOnClickListener{
            popUpproduct!!.quantity++
            popupText = popUpproduct!!.quantity.toString()
            val newPrice = popUpproduct!!.price * popUpproduct!!.quantity
            quantite.setText(popupText)
            price.text = newPrice.toString() + " €"
        }


        moins.setOnClickListener{
            if(popUpproduct!!.quantity > 1){
                popUpproduct!!.quantity -= 1
                popupText = popUpproduct!!.quantity.toString()
                val newPrice = popUpproduct!!.price * popUpproduct!!.quantity
                quantite.setText(popupText)
                price.text = newPrice.toString() + " €"
            }
        }

        productName.text = popUpproduct!!.name
        quantite.setText(popUpproduct!!.quantity.toString())
        description.text = popUpproduct!!.description
        price.text = popUpproduct!!.price.toString() + " €"

        confirmBtn.setOnClickListener{
            val queue = Volley.newRequestQueue(this)

            val urlPostProduct = "http://10.136.76.77:8080/api/cart_product/${popUpproduct!!.id}"
            val params = HashMap<String, Double>()
            params["quantity"] = popUpproduct!!.quantity.toDouble()
            val postProduct : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, urlPostProduct, JSONObject((params as Map<*, *>?)!!), { response1 ->
                    if (response1.getBoolean("success")) {
                        Toast.makeText(this, "Product Posted", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Payment failed", Toast.LENGTH_LONG).show()
                    }
                }, {
                    Toast.makeText(this, "Payment failed", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params2: MutableMap<String, String> = HashMap()
                    params2["Authorization"] = "Bearer $token"
                    return params2
                }
            }

            val urlCreateCart = "http://10.136.76.77:8080/api/cart"
            val postCart : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, urlCreateCart, JSONObject(), { response1 ->
                    Log.d("RESPONSE GET CART",response1.toString())
                }, {

                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params2: MutableMap<String, String> = HashMap()
                    params2["Authorization"] = "Bearer $token"
                    return params2
                }
            }

            val urlCheckCart = "http://10.136.76.77:8080/api/cart"
            val getCurrentCart : JsonObjectRequest = object : JsonObjectRequest(
                Method.GET, urlCheckCart, JSONObject(), { response1 ->


                }, {
                    queue.add(postCart)


                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params2: MutableMap<String, String> = HashMap()
                    params2["Authorization"] = "Bearer $token"
                    return params2
                }
            }
            queue.add(getCurrentCart)
            queue.add(postProduct)
            finish()

        }

        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        val alpha = 100
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            viewBackground.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
        viewWithBorder.alpha = 0f
        viewWithBorder.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()


    }

    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val viewBackground = findViewById<CardView>(R.id.popup_window_background)
        val viewWithBorder = findViewById<CardView>(R.id.popup_window_view_with_border)
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            viewBackground.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        viewWithBorder.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

}