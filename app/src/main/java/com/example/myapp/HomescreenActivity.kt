package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class HomescreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val settingsBtn: ImageButton = findViewById(R.id.settings)

        val mainPage: LinearLayout = findViewById(R.id.mainLayout)
        val db = FirebaseFirestore.getInstance()
        db.collection("users").get().addOnSuccessListener { users ->
            for (user in users) {
                val userId = user.id
                val userProductsRef = db.collection("users").document(userId).collection("products")

                userProductsRef.get().addOnSuccessListener { documents ->
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
                            val intent = Intent(this, ProductActivity::class.java)
                            intent.putExtra("PRODUCT_NAME", productName)
                            startActivity(intent)
                            finish()
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting products", exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting users", exception)
        }

        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        messagesBtn.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
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
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }
}