package com.popay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popay.databinding.FragmentHomeBinding
import com.popay.entities.Product

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    private lateinit var cartListRecyclerView: RecyclerView
    private  var cartList : ArrayList<Product> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        cartListRecyclerView = binding!!.cartList
        cartListRecyclerView.layoutManager = LinearLayoutManager(context)
      //  cartListRecyclerView.setHasFixedSize(true)

            cartList.add(Product(0, "maxence", 1.00, "Very cringe boy", 1))
        cartList.add(Product(1, "corki", 10.00, "No lore", 5))
        cartList.add(Product(2, "kitto katto", 3.99, "キットカット", 13))
        cartListRecyclerView.adapter = CartAdapter(cartList)

    //    cartList = arrayListOf()
Log.e("ttt","t")

        binding!!.scanBtn.setOnClickListener {
            val intent = Intent (activity, QRCodeScannerActivity::class.java)
            activity?.startActivity(intent)
        }

        binding!!.nfcBtn.setOnClickListener {
            val intent = Intent (activity, NfcReaderActivity::class.java)
            activity?.startActivity(intent)
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  getUserData()


    }

    private fun getUserData() {
        cartList.plus(Product(0, "maxence", 1.00, "Very cringe boy", 1))
        cartList.plus(Product(1, "corki", 10.00, "No lore", 5))
        cartList.plus(Product(2, "kitto katto", 3.99, "キットカット", 13))
        cartListRecyclerView.layoutManager = LinearLayoutManager(context)

        cartListRecyclerView.adapter = CartAdapter(cartList)
        cartListRecyclerView.adapter!!.notifyDataSetChanged()
    }
}
