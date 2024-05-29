package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

class ProductActivity : AppCompatActivity() {
    private lateinit var idSeller: String
    var name: String = ""
    var price: String = ""
    var quantity: String = ""
    var locationCity: String = ""
    var locationCountry: String = ""
    var surnameOwner: String = ""
    var nameOwner: String = ""
    var description: String = ""
    var packageType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_page)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val backButton: ImageButton = findViewById(R.id.back_button)

        val nameField: TextView = findViewById(R.id.name_produs)
        val priceField: TextView = findViewById(R.id.pret)
        val descriptionField: TextView = findViewById(R.id.description)
        val sellerField: TextView = findViewById(R.id.name_seller)
        val locationField: TextView = findViewById(R.id.location)
        val quantityAvailable: TextView = findViewById(R.id.titleAvailableQuantity)

        val nameProduct = intent.getStringExtra("PRODUCT_NAME")
        val userId = intent.getStringExtra("USERID")
        val db = FirebaseFirestore.getInstance()
        val productsDocRef = db.collection("products")

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

                    nameField.text = productName
                    locationField.text = "$locationCity, $locationCountry"
                    priceField.text = "$price lei/ $packageType"
                    sellerField.text = "$nameOwner, $surnameOwner"
                    descriptionField.text = description
                    quantityAvailable.text = quantity
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }

        val wantedQuantity: EditText = findViewById(R.id.wantedQuantity)
        val addItem: Button = findViewById(R.id.addItem)
        addItem.setOnClickListener {
            val q = wantedQuantity.text.toString()
            val name = nameField.text.toString()
            if (q.isEmpty() || q.toInt() > quantity.toInt()) {
                wantedQuantity.error = "Error"
            } else {
                addItemToCart(name, sellerField.text.toString(), price, q, idSeller, locationCity, locationCountry)
                val intent = Intent(this, HomescreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val addToWishlist: Button = findViewById(R.id.addToWishlist)
        addToWishlist.setOnClickListener {
            val nameValue = nameField.text.toString()
            val sellerValue = sellerField.text.toString()
            val q = wantedQuantity.text.toString()
            if (q.isEmpty() || q.toInt() > quantity.toInt()) {
                wantedQuantity.error = "Error"
            } else {
                addItemToWishlist(idSeller, nameValue, sellerValue, q, description, packageType, locationCity, locationCountry, price)
                val intent = Intent(this, HomescreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val contact: Button = findViewById(R.id.button_contact)
        contact.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("OWNER_ID", idSeller)
            startActivity(intent)
            finish()
        }

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

    private fun addItemToWishlist(idSeller: String, nameValue: String, sellerValue: String, quantity: String, description: String, packageType: String, locationCity: String, locationCountry: String, price: String) {
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
            "price" to price
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("wishlist").document(nameValue)
            .set(productInput)
            .addOnSuccessListener {
                Log.d(Logging.TAG, "Added with success")
                Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()
                val newIntent = Intent(this, HomescreenActivity::class.java)
                startActivity(newIntent)
                finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            .addOnFailureListener { e ->
                Log.w(Logging.TAG, "Error adding document", e)
                Toast.makeText(this, "An error occurred while registering the information :(", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addItemToCart(nameValue: String, sellerValue: String, priceValue: String, quantity: String, idSeller: String, locationCity: String, locationCountry: String) {
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "seller" to sellerValue,
            "price" to priceValue,
            "quantity" to quantity,
            "idSeller" to idSeller,
            "city" to locationCity,
            "country" to locationCountry,
            "description" to description,
            "package" to packageType
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("cart").document("${nameValue}")
            .set(productInput)
            .addOnSuccessListener {
                Log.d(Logging.TAG, "Added with success")
                Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()
                val newIntent = Intent(this, HomescreenActivity::class.java)
                startActivity(newIntent)
                finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
    }
}