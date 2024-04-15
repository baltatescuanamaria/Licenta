package com.example.myapp;

import android.app.Activity;
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ProductsActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_products)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val addItemBtn: Button = findViewById(R.id.addItem)
        val selectBtn: Button = findViewById(R.id.cardView)

        addItemBtn.setOnClickListener {
        val intent = Intent(this, AddProductActivity::class.java)
        startActivity(intent)
        finish()
        }

        selectBtn.setOnClickListener {
        val intent = Intent(this, EditProductActivity::class.java)
        startActivity(intent)
        finish()
        }

        homeBtn.setOnClickListener{
        val intent = Intent(this, HomescreenActivity::class.java)
        startActivity(intent)
        finish()
        }

        messagesBtn.setOnClickListener {
        val intent = Intent(this, MessageActivity::class.java)
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
