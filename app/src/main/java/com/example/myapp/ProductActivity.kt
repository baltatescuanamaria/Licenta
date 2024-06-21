package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging
import com.google.firebase.inappmessaging.internal.Logging.TAG
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class ProductActivity : AppCompatActivity() {
    //var idSeller: String = ""
    var name: String = ""
    private var key: String = ""
    var price: String = ""
    var quantity: String = ""
    var category: String = ""
    var locationCity: String = ""
    var locationCountry: String = ""
    private var surnameOwner: String = ""
    private var nameOwner: String = ""
    var description: String = ""
    private var packageType: String = ""
    private var imageUrl: String = ""
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_page)

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
        val quantityAvailableField: TextView = findViewById(R.id.titleAvailableQuantity)
        val productImageField: ImageView = findViewById(R.id.picturePlace)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val nameProduct = intent.getStringExtra("PRODUCT_NAME")
        val userId = intent.getStringExtra("USERID")
        val db = FirebaseFirestore.getInstance()
        val productsDocRef = db.collection("products")
        var idSeller: String = ""

        productsDocRef.get().addOnSuccessListener { documents ->
            for (doc in documents) {
                val productName = doc.getString("product_name").toString()
                idSeller = doc.getString("userId").toString()

                if (productName == nameProduct && userId == idSeller) {

                    locationCity = doc.getString("city").toString()
                    locationCountry = doc.getString("country").toString()
                    price = doc.getString("price").toString()
                    surnameOwner = doc.getString("surname").toString()
                    nameOwner = doc.getString("name").toString()
                    description = doc.getString("description").toString()
                    packageType = doc.getString("package").toString()
                    quantity = doc.getString("quantity").toString()
                    imageUrl = doc.getString("image_url").toString()
                    category = doc.getString("category").toString()
                    key = doc.get("key").toString()

                    nameField.text = productName
                    locationField.text = "$locationCity, $locationCountry"
                    priceField.text = "$price lei/ $packageType"
                    sellerField.text = "$nameOwner, $surnameOwner"
                    descriptionField.text = description
                    quantityAvailableField.text = quantity

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
            Log.e("Firestore", "Error getting products", exception)
        }

        val contactBtn: Button = findViewById(R.id.button_contact)
        contactBtn.setOnClickListener {
            val intent = Intent(this, ProfileContactActivity::class.java)
            intent.putExtra("OWNER_ID", userId)
            startActivity(intent)
        }

        val wantedQuantityField: EditText = findViewById(R.id.wantedQuantity)
        val addItemBtn: Button = findViewById(R.id.addItem)
        addItemBtn.setOnClickListener {
            val q = wantedQuantityField.text.toString()
            val name = nameField.text.toString()
            if (q.isEmpty() || q.toInt() > quantity.toInt()) {
                wantedQuantityField.error = "Error"
            } else {
                if (userId != null) {
                    addItemToCart(key, name, sellerField.text.toString(), price, q, userId, locationCity, locationCountry, category)
                }
                val intent = Intent(this, HomescreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val addToWishlistBtn: Button = findViewById(R.id.addToWishlist)
        addToWishlistBtn.setOnClickListener {
            val nameValue = nameField.text.toString()
            val sellerValue = sellerField.text.toString()
            val q = wantedQuantityField.text.toString()
            if (q.isEmpty() || q.toInt() > quantity.toInt()) {
                wantedQuantityField.error = "Error"
            } else {
                if (userId != null) {
                    addItemToWishlist(key, userId, nameValue, sellerValue, q, description, packageType, locationCity, locationCountry, price, category)
                }
                val intent = Intent(this, HomescreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        backBtn.setOnClickListener {
            //val intent = Intent(this, HomescreenActivity::class.java)
            //startActivity(intent)
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

    private fun addItemToWishlist(key: String, idSeller: String, nameValue: String, sellerValue: String, quantity: String, description: String, packageType: String, locationCity: String, locationCountry: String, price: String, category: String) {
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "seller" to sellerValue,
            "idSeller" to idSeller,
            "wanted_quantity" to quantity,
            "description" to description,
            "package" to packageType,
            "city" to locationCity,
            "country" to locationCountry,
            "price" to price,
            "category" to category,
            "image_url" to imageUrl,
            "key" to key.toInt()
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"


        db.collection("users")
            .document(documentName).collection("wishlist").document(key)
            .set(productInput)
            .addOnSuccessListener {
                Log.d(Logging.TAG, "Added with success")
                Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()

                db.collection("products").whereEqualTo("userId", idSeller).get()
                    .addOnSuccessListener { products ->
                        for (product in products) {
                            val productData = product.data
                            val key = product.get("key").toString()
                            db.collection("users").document(documentName).collection("reccs3").document(key).set(productData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Product added to reccs3 with success")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding product to reccs3", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error getting products from the same seller", e)
                    }

                val newIntent = Intent(this, HomescreenActivity::class.java)
                startActivity(newIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(Logging.TAG, "Error adding document", e)
                Toast.makeText(this, "An error occurred while registering the information :(", Toast.LENGTH_SHORT).show()
            }
    }


    private fun addItemToCart(key: String, nameValue: String, sellerValue: String, priceValue: String, quantity: String, idSeller: String, locationCity: String, locationCountry: String, category: String) {
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "seller" to sellerValue,
            "price" to priceValue,
            "quantity" to quantity,
            "idSeller" to idSeller,
            "city" to locationCity,
            "country" to locationCountry,
            "category" to category,
            "description" to description,
            "package" to packageType,
            "image_url" to imageUrl,
            "key" to key
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("cart").document(key)
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