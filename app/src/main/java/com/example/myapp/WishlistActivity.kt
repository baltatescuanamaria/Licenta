package com.example.myapp;
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

class WishlistActivity : AppCompatActivity() {
        var location: String = ""
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.wishlist)

                val homeBtn: ImageButton = findViewById(R.id.home)
                val productsBtn: ImageButton = findViewById(R.id.products)
                val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
                val profileBtn: ImageButton = findViewById(R.id.profile)
                //val addItemBtn: Button = findViewById(R.id.addItem)
                //val selectBtn: Button = findViewById(R.id.buttonProduct)
                //val picturePlace: ImageView = findViewById(R.id.picturePlace)
                val mainPage: LinearLayout = findViewById(R.id.mainLayout)
                val maximumLength = 15
                val db = FirebaseFirestore.getInstance()
                val currentUser = FirebaseAuth.getInstance().currentUser
                val currentUserId = currentUser?.uid
                db.collection("users").get().addOnSuccessListener { users ->
                        for (user in users) {
                                val userId = user.id
                                val userProductsRef = db.collection("users").document(userId)
                                        .collection("wishlist")
                                userProductsRef.get().addOnSuccessListener { documents ->
                                        for (document in documents) {
                                                val productName = document.getString("product_name").toString()
                                                val userIdOwner = document.getString("idSeller").toString()
                                                val locationCity = document.getString("city").toString()
                                                val locationCountry = document.getString("country").toString()
                                                val price = document.getString("price").toString()
                                                location = "$locationCountry, $locationCity"
                                                val layoutInflater = LayoutInflater.from(this)
                                                val productLayout = layoutInflater.inflate(R.layout.product_layout_wishlist, null)
                                                val productNameButton: LinearLayout = productLayout.findViewById(R.id.product)
                                                val productNameSpace: TextView = productLayout.findViewById(R.id.numeProdus)
                                                val priceText: TextView = productLayout.findViewById(R.id.price)
                                                val locationText: TextView = productLayout.findViewById(R.id.location)

                                                productNameSpace.text = productName
                                                priceText.text = "$price lei"
                                                locationText.text = "$locationCountry, $locationCity"
                                                mainPage.addView(productLayout)

                                                                //selectBtn.text = name
                                                productNameButton.setOnClickListener {
                                                     val intent = Intent(this, ProductWishlistActivity::class.java)
                                                     intent.putExtra("PRODUCT_NAME", productName)
                                                     intent.putExtra("USERID", userIdOwner)
                                                     startActivity(intent)
                                                     finish()
                                                     overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                                                }
                                                }
                                }.addOnFailureListener { exception ->
                                Log.e("Firestore", "Error getting products", exception)
                        }
                                val backButton: ImageButton = findViewById(R.id.back_button)
                                backButton.setOnClickListener {
                                        val intent = Intent(this, HomescreenActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                                }


                                homeBtn.setOnClickListener {
                                        val intent = Intent(this, HomescreenActivity::class.java)
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
        }
}