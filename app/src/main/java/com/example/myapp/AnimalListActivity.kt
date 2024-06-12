package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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

class AnimalListActivity : AppCompatActivity() {
    var name:String = ""
    var price:String = ""
    var quantity:String=""
    var locationCity: String=""
    var locationCountry: String=""
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.animal_list)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val backBtn: ImageButton = findViewById(R.id.back_button)

        val mainPage: LinearLayout = findViewById(R.id.arrayProductsRecAdded)

        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
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
                val packageType = document.getString("package")
                //val description = document.getString("description")
                val imageUrl = document.getString("image_url")

                if (idSeller != currentUserId && category == "Animal Products" && productCount < 15) {
                    Log.d("Firestore", "Adding product: $name")
                    val layoutInflater = LayoutInflater.from(this)
                    val productLayout =
                        layoutInflater.inflate(R.layout.homescreen_product_layout, null)
                    val productBtn: LinearLayout =
                        productLayout.findViewById(R.id.product)
                    val productNameField: TextView =
                        productLayout.findViewById(R.id.numeProdus)
                    val locationTextField: TextView = productLayout.findViewById(R.id.location)
                    val priceTextField: TextView = productLayout.findViewById(R.id.price)
                    val productImageField: ImageView = productLayout.findViewById(R.id.picturePlace)

                    productNameField.text = name
                    locationTextField.text = "$locationCountry, $locationCity"
                    priceTextField.text = "$price lei/$packageType"

                    imageUrl?.let {
                        val storageRef = storage.reference.child("images/$it")
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(this)
                                .load(uri)
                                .into(productImageField)
                        }.addOnFailureListener {
                            Log.e("Firebase Storage", "Error getting image URL", it)
                        }
                    }
                    mainPage.addView(productLayout)

                    productBtn.setOnClickListener {
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

        backBtn.setOnClickListener{
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