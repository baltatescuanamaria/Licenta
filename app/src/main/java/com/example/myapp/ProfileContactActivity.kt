package com.example.myapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileContactActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_contact)

        val nameField: TextView = findViewById(R.id.name)
        val locationField: TextView = findViewById(R.id.loc)
        val phoneNumberField: TextView = findViewById(R.id.PhoneNumber)
        val descriptionField: TextView = findViewById(R.id.description)
        val backBtn: ImageButton = findViewById(R.id.back_button)

        val db = FirebaseFirestore.getInstance()
        val userId = intent.getStringExtra("OWNER_ID")
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName)
        userDocRef.get().addOnSuccessListener { document->
            if (document != null && document.exists()) {
                val name = document.getString("name")
                val surname = document.getString("surname")
                val username = document.getString("username")
                val city = document.getString("city")
                val country = document.getString("country")
                val phoneNumber = document.getString("phoneNumber")
                val description = document.getString("description")

                nameField.text = "${name} ${surname}"
                locationField.text = "${city}, ${country}"
                phoneNumberField.text = phoneNumber
                descriptionField.text = description

            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val mainPage: LinearLayout = findViewById(R.id.arrayProducts)

        db.collection("users").document(documentName).collection("products").get().addOnSuccessListener { documents->
            for (document in documents) {
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                val category = document.getString("category")
                val packageType = document.getString("package").toString()
                val imageUrl = document.getString("image_url").toString()
                val layoutInflater = LayoutInflater.from(this)
                val productLayout = layoutInflater.inflate(R.layout.homescreen_product_layout,null)
                val productBtn: LinearLayout = productLayout.findViewById(R.id.product)
                val productNameField: TextView = productLayout.findViewById(R.id.numeProdus)
                val locationTextField: TextView = productLayout.findViewById(R.id.location)
                val priceField: TextView = productLayout.findViewById(R.id.price)
                val productImageField: ImageView = productLayout.findViewById(R.id.picturePlace)

                    productNameField.text = name
                    locationTextField.text = "$locationCountry, $locationCity"
                    priceField.text = "$price lei/$packageType"
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
                        //finish()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting products", exception)

        }

        backBtn.setOnClickListener{
            finish()
        }


        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
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

