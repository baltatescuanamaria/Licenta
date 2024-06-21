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

class ProductsActivity : AppCompatActivity() {
        private lateinit var storage: FirebaseStorage
        private lateinit var storageReference: StorageReference
        private var isActivityActive = false

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.your_products)

                val homeBtn: ImageButton = findViewById(R.id.home)
                val productsBtn: ImageButton = findViewById(R.id.products)
                val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
                val profileBtn: ImageButton = findViewById(R.id.profile)
                val addItemBtn: Button = findViewById(R.id.addItem)
                val mainPage: LinearLayout = findViewById(R.id.mainLayout)

                storage = FirebaseStorage.getInstance()
                storageReference = storage.reference

                val db = FirebaseFirestore.getInstance()
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val documentName = "user_${userId}"
                val maximumLength = 15
                val userDocRef = db.collection("users").document(documentName).collection("products")

                isActivityActive = true

                userDocRef.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                                val productName = document.getString("product_name")
                                val price = document.getString("price")
                                val imageUrl = document.getString("image_url")
                                val url = document.getString("key")

                                val layoutInflater = LayoutInflater.from(this)
                                val productLayout = layoutInflater.inflate(R.layout.product_layout, null)
                                val productNameBtn: LinearLayout = productLayout.findViewById(R.id.productButton)
                                val productNameField: TextView = productLayout.findViewById(R.id.numeProdus)
                                val priceTextField: TextView = productLayout.findViewById(R.id.price)
                                val productImageField: ImageView = productLayout.findViewById(R.id.picturePlace)

                                if (productName != null) {
                                        if (productName.length > maximumLength) {
                                                productNameField.text = productName.substring(0, maximumLength) + "..."
                                        } else {
                                                productNameField.text = productName
                                        }
                                        priceTextField.text = "$price lei"
                                        mainPage.addView(productLayout)

                                        productNameBtn.setOnClickListener {
                                                val intent = Intent(this, EditProductActivity::class.java)
                                                intent.putExtra("PRODUCT_NAME", productName)
                                                intent.putExtra("URL", url)
                                                startActivity(intent)
                                                //finish()
                                        }
                                }

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
                        }
                }

                val backBtn: ImageButton = findViewById(R.id.back_button)
                backBtn.setOnClickListener {
                        val intent = Intent(this, HomescreenActivity::class.java)
                        startActivity(intent)
                        //finish()
                }

                addItemBtn.setOnClickListener {
                        val intent = Intent(this, AddProductActivity::class.java)
                        startActivity(intent)
                        //finish()
                }

                homeBtn.setOnClickListener {
                        val intent = Intent(this, HomescreenActivity::class.java)
                        startActivity(intent)
                        //finish()
                }

                productsBtn.setOnClickListener {
                        val intent = Intent(this, ProductsActivity::class.java)
                        startActivity(intent)
                        //finish()
                }

                wishlistBtn.setOnClickListener {
                        val intent = Intent(this, WishlistActivity::class.java)
                        startActivity(intent)
                        //finish()
                }

                profileBtn.setOnClickListener {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        //finish()
                }
        }

        override fun onDestroy() {
                super.onDestroy()
                isActivityActive = false
        }
}
