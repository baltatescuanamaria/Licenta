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

class ProductWishlistActivity : AppCompatActivity() {
    lateinit var name:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_wishlist_page)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val backButton: ImageButton = findViewById(R.id.back_button)

        val nameField: TextView = findViewById(R.id.name_produs)
        val priceField: TextView = findViewById(R.id.pret)
        val descriptionField: TextView = findViewById(R.id.description)
        val sellerField: TextView = findViewById(R.id.name_seller)
        val locationField: TextView = findViewById(R.id.location)

        val nameProduct  = intent.getStringExtra("PRODUCT_NAME")
        val idOwner = intent.getStringExtra("ID_OWNER")
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${idOwner}"
        val userDocRef = db.collection("users").document(documentName).collection("products").document("product_${nameProduct}")
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                name = document.getString("product_name").toString()
                val price  = document.getString("price")
                val quantity = document.getString("quantity")
                val description = document.getString("description")


                nameField.text = name
                priceField.text = "${price} lei"
                descriptionField.text = description
            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }
        val userDocRef2 = db.collection("users").document(documentName)
        userDocRef2.get().addOnSuccessListener { document->
            if (document != null && document.exists()) {
                val locationCity = document.getString("city")
                val locationCountry = document.getString("country")
                val seller = document.getString("name")

                locationField.text = "${locationCountry}, ${locationCity}"
                sellerField.text = seller


            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }

        val addItem: Button = findViewById(R.id.addItem)
        addItem.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            val nameValue = nameField.text.toString()
            val sellerValue = sellerField.text.toString()
            val priceValue = priceField.text.toString()
            addItemToCart(nameValue, sellerValue, priceValue)
        }

        val deleteBtn:Button = findViewById(R.id.removeToWishlist)
        deleteBtn.setOnClickListener {
                val userDocRef3 = db.collection("users").document("user_${userId}").collection("wishlist").document("wishlist_${name}")
                userDocRef3.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted with succes", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Error in deleting the product", Toast.LENGTH_SHORT).show()
                }
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

        messagesBtn.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
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

    private fun addItemToCart(nameValue: String, sellerValue: String, priceValue: String){
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "seller" to sellerValue,
            "price" to priceValue
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("cart").document("cart_${nameValue}")
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

}