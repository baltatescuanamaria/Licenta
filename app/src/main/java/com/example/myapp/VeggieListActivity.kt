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

class VeggieListActivity : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var isActivityActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        var location: String = ""
        super.onCreate(savedInstanceState)
        setContentView(R.layout.veggie_list)

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

        isActivityActive = true

        userDocRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                var productCount = 0
                val productName = document.getString("product_name")
                val userIdOwner = document.getString("userId")
                val locationCity = document.getString("city")
                val locationCountry = document.getString("country")
                location = "$locationCountry, $locationCity"
                val category = document.getString("category")
                val price  = document.getString("price")
                val packageType = document.getString("package")
                //val description = document.getString("description")
                val imageUrl = document.getString("image_url")

                if (userIdOwner != currentUserId && category == "Vegetable" && productCount <15) {
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

                    productNameField.text = productName
                    locationTextField.text = location
                    priceTextField.text = "$price lei/$packageType"

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

                    //selectBtn.text = name
                    productBtn.setOnClickListener {
                        val intent = Intent(this, ProductActivity::class.java)
                        intent.putExtra("PRODUCT_NAME", productName)
                        intent.putExtra("USERID", userIdOwner)
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