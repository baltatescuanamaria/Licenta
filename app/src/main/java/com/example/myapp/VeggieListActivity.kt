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
        var county: String = ""

        db.collection("users").document("user_$currentUserId").get().addOnSuccessListener { document->
            county = document.getString("country").toString()
        }
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        val userDocRef = db.collection("products")

        isActivityActive = true

        userDocRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
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

                if (userIdOwner != currentUserId && category == "Vegetable" && locationCountry == county) {
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
                        //finish()
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }

        val thirdPage: LinearLayout = findViewById(R.id.arrayProductssameSeller)


        val userDocRef2 = db.collection("users").document("user_$currentUserId").collection("reccs3")
        userDocRef2.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                val category = document.getString("category")
                val packageType = document.getString("package").toString()
                val imageUrl = document.getString("image_url").toString()

                if(category == "Vegetable") {
                    val layoutInflater = LayoutInflater.from(this)
                    val productLayout =
                        layoutInflater.inflate(R.layout.homescreen_product_layout3, null)
                    val productButton: LinearLayout = productLayout.findViewById(R.id.product)
                    val productNameSpace: TextView = productLayout.findViewById(R.id.numeProdus)
                    val locationText: TextView = productLayout.findViewById(R.id.location)
                    val priceText: TextView = productLayout.findViewById(R.id.price)
                    val productImageField: ImageView = productLayout.findViewById(R.id.picturePlace)

                    productNameSpace.text = name
                    locationText.text = "$locationCountry, $locationCity"
                    priceText.text = "$price lei/$packageType"
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
                    thirdPage.addView(productLayout)

                    //selectBtn.text = name
                    productButton.setOnClickListener {
                        val intent = Intent(this, ProductActivity::class.java)
                        intent.putExtra("PRODUCT_NAME", name)
                        intent.putExtra("USERID", idSeller)
                        startActivity(intent)
                        //finish()
                    }
                }

            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }


        backBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            //finish()
        }

        homeBtn.setOnClickListener{
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
}