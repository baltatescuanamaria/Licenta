package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViticultureListActivity : AppCompatActivity() {
    private lateinit var idSeller:String
    val categoriesList = mutableSetOf<String>()
    var name:String = ""
    var price:String = ""
    var quantity:String=""
    var locationCity: String=""
    var locationCountry: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viticulture_list)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val backButton: ImageButton = findViewById(R.id.back_button)

        val mainPage: LinearLayout = findViewById(R.id.arrayProductsRecAdded)

        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val userDocRef = db.collection("products")
        userDocRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                var productCount = 0
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                val category = document.getString("category")

                if (idSeller != currentUserId && category == "Viticulture" && productCount < 15) {
                    Log.d("Firestore", "Adding product: $name")
                    val layoutInflater = LayoutInflater.from(this)
                    val productLayout =
                        layoutInflater.inflate(R.layout.homescreen_product_layout, null)
                    val productButton: LinearLayout =
                        productLayout.findViewById(R.id.product)
                    val productNameSpace: TextView =
                        productLayout.findViewById(R.id.numeProdus)
                    val locationText: TextView = productLayout.findViewById(R.id.location)
                    productNameSpace.text = name
                    locationText.text = "$locationCountry, $locationCity"
                    mainPage.addView(productLayout)

                    //selectBtn.text = name
                    productButton.setOnClickListener {
                        val intent = Intent(this, ProductActivity::class.java)
                        intent.putExtra("PRODUCT_NAME", name)
                        intent.putExtra("USERID", idSeller)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    }
                    productCount++
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }

        backButton.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
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