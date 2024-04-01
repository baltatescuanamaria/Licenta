package com.example.myapp;

import android.app.Activity;
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class WishlistActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wishlist)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)

        homeBtn.setOnClickListener{
        val intent = Intent(this, HomescreenActivity::class.java)
        startActivity(intent)
        finish()
        }

        messagesBtn.setOnClickListener {
        val intent = Intent(this, MessageListActivity::class.java)
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
