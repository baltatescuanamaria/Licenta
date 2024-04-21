package com.example.myapp;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

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
        //val selectBtn: Button = findViewById(R.id.buttonProduct)
        //val picturePlace: ImageView = findViewById(R.id.picturePlace)
        val mainPage: LinearLayout = findViewById(R.id.mainLayout)

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName).collection("products")
        userDocRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                        val productName = document.getString("product_name")
                        /*val price  = document.getString("price")
                        val quantity = document.getString("quantity")
                        val description = document.getString("description")
                        val picture = document.getString("imageUrl")*/

                        val layoutInflater = LayoutInflater.from(this)
                        val productLayout = layoutInflater.inflate(R.layout.product_layout, null)
                        val productNameButton: Button = productLayout.findViewById(R.id.buttonProduct)
                        productNameButton.text = productName
                        mainPage.addView(productLayout)

                        //selectBtn.text = name
                        productNameButton.setOnClickListener {
                                val intent = Intent(this, EditProductActivity::class.java)
                                intent.putExtra("PRODUCT_NAME", productName)
                                startActivity(intent)
                                finish()
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                        }
                }
        }
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener{
             val intent = Intent(this, HomescreenActivity::class.java)
             startActivity(intent)
             finish()
             overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }


        addItemBtn.setOnClickListener {
        val intent = Intent(this, AddProductActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        homeBtn.setOnClickListener{
        val intent = Intent(this, HomescreenActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        messagesBtn.setOnClickListener {
        val intent = Intent(this, MessageActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        productsBtn.setOnClickListener {
        val intent = Intent(this, ProductsActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        wishlistBtn.setOnClickListener {
        val intent = Intent(this, WishlistActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        profileBtn.setOnClickListener {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
        }

        }
