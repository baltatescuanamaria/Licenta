package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProductWishlistActivity : AppCompatActivity() {
    private lateinit var idSeller: String
    var name: String = ""
    var price: String = ""
    private var key: String = ""
    var quantity: String = ""
    var locationCity: String = ""
    var locationCountry: String = ""
    private var sellerName: String = ""
    var description: String = ""
    private var packageType: String = ""
    var category: String = ""
    private var imageUrl: String = ""
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_wishlist_page)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val backBtn: ImageButton = findViewById(R.id.back_button)

        val nameField: TextView = findViewById(R.id.name_produs)
        val priceField: TextView = findViewById(R.id.pret)
        val descriptionField: TextView = findViewById(R.id.description)
        val sellerField: TextView = findViewById(R.id.name_seller)
        val locationField: TextView = findViewById(R.id.location)
        val productImageField: ImageView = findViewById(R.id.picturePlace)

        val nameProduct = intent.getStringExtra("PRODUCT_NAME")
        val userId = intent.getStringExtra("USERID")
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val productsDocRef =
            db.collection("users").document("user_$currentUserId").collection("wishlist")

        productsDocRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val productName = document.getString("product_name").toString()
                idSeller = document.getString("idSeller").toString()
                Log.d(Logging.TAG, "Checking user: ${idSeller}, county: $userId")

                if (productName == nameProduct && userId == idSeller) {
                    Log.d(Logging.TAG, "Checking user: ${idSeller}, county: $userId")
                    locationCity = document.getString("city").toString()
                    locationCountry = document.getString("country").toString()
                    price = document.getString("price").toString()
                    sellerName = document.getString("seller").toString()
                    description = document.getString("description").toString()
                    packageType = document.getString("package").toString()
                    quantity = document.getString("wanted_quantity").toString()
                    category = document.getString("category").toString()
                    imageUrl = document.getString("image_url").toString()
                    key = document.get("key").toString()

                    nameField.text = productName
                    locationField.text = "$locationCity, $locationCountry"
                    priceField.text = "$price lei/$packageType"
                    sellerField.text = sellerName
                    descriptionField.text = description

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
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting product", exception)
        }

        val addItemBtn: Button = findViewById(R.id.addItem)
        addItemBtn.setOnClickListener {
            val nameValue = nameField.text.toString()
            val sellerValue = sellerField.text.toString()
            val priceValue = priceField.text.toString()
            addItemToCart(
                nameValue,
                sellerValue,
                price,
                quantity,
                idSeller,
                locationCity,
                locationCountry,
                category
            )
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
        }

        val deleteBtn: Button = findViewById(R.id.removeToWishlist)
        deleteBtn.setOnClickListener {
            db.collection("users").document("user_$currentUserId").collection("wishlist")
                .document(key)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error deleting the product", Toast.LENGTH_SHORT).show()
                }

            db.collection("users").document("user_$currentUserId").collection("reccs3")
                .document(key)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error deleting the product", Toast.LENGTH_SHORT).show()
                }

        }


        val contactBtn: Button = findViewById(R.id.button_contact)
        contactBtn.setOnClickListener {
            val intent = Intent(this, ProfileContactActivity::class.java)
            intent.putExtra("OWNER_ID", userId)
            startActivity(intent)
            finish()
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)
            finish()
        }

        homeBtn.setOnClickListener {
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

    private fun addItemToCart(nameValue: String, sellerValue: String, priceValue: String, quantity: String, idSeller: String, locationCity: String, locationCountry: String, category: String) {
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "seller" to sellerValue,
            "idSeller" to idSeller,
            "quantity" to quantity,
            "description" to description,
            "package" to packageType,
            "city" to locationCity,
            "country" to locationCountry,
            "price" to price,
            "category" to category,
            "image_url" to imageUrl
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("cart").document(nameValue)
            .set(productInput)
            .addOnSuccessListener {
                Log.d(Logging.TAG, "Added with success")
                Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()
                val newIntent = Intent(this, HomescreenActivity::class.java)
                startActivity(newIntent)
                finish()
            }
    }
}
