package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class CheckoutActivity : AppCompatActivity() {
    private var priceTotal = 0
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var isActivityActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_checkout)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val priceTotalTextField: TextView = findViewById(R.id.pretTotal)
        val checkoutBtn: Button = findViewById(R.id.checkout)

        val mainPage: LinearLayout = findViewById(R.id.mainLayout)
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        isActivityActive = true

        if (currentUserId != null) {
            val userProductsRef = db.collection("users").document("user_$currentUserId").collection("cart")
            userProductsRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val productName = document.getString("product_name")
                    val userIdOwner = document.getString("idSeller")
                    val price = document.getString("price")
                    val quantity = document.getString("quantity")
                    if (price != null && quantity != null) {
                        priceTotal += price.toInt() * quantity.toInt()
                    }
                    val city = document.getString("city").toString()
                    val country = document.getString("country").toString()
                    val packageType = document.getString("package").toString()
                    val imageUrl = document.getString("image_url")

                    val layoutInflater = LayoutInflater.from(this)
                    val productLayout = layoutInflater.inflate(R.layout.product_layout_wishlist, null)
                    val productNameBtn: LinearLayout = productLayout.findViewById(R.id.productButton)
                    val productNameField: TextView = productLayout.findViewById(R.id.numeProdus)
                    val locationField: TextView = productLayout.findViewById(R.id.location)
                    val priceTextField: TextView = productLayout.findViewById(R.id.price)
                    val productImageField: ImageView = productLayout.findViewById(R.id.picturePlace)

                    productNameField.text = productName
                    priceTextField.text = "$price lei/$packageType"
                    locationField.text = "$country, $city"

                    if (imageUrl != null) {
                        val storageRef = storage.reference.child("images/$imageUrl")
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            if (isActivityActive) {
                                Glide.with(this)
                                    .load(uri)
                                    .into(productImageField)
                            }
                        }.addOnFailureListener {
                            Log.e("Firebase Storage", "Error getting image URL", it)
                        }
                    }
                    mainPage.addView(productLayout)

                    priceTotalTextField.text = "$priceTotal lei"

                    productNameBtn.setOnClickListener {
                        val intent = Intent(this, ProductCartActivity::class.java)
                        intent.putExtra("PRODUCT_NAME", productName)
                        intent.putExtra("USERID", userIdOwner)
                        intent.putExtra("PRICE", priceTotal)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    }
                }
            }
        }

        val backBtn: ImageButton = findViewById(R.id.back_button)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        checkoutBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("TOTAL", priceTotal)
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
