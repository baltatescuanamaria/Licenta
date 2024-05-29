package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class FinishOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finish_order)

        val placeOrder: Button = findViewById(R.id.goBack)
        placeOrder.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)

        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        productsBtn.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            finish()
        }

        wishlistBtn.setOnClickListener {
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)
            finish()
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}