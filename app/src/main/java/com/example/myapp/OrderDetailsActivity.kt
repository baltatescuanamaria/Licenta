package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderDetailsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_details)

        val backBtn: ImageButton = findViewById(R.id.back_button)
        val acceptBtn: Button = findViewById(R.id.acceptOrder)
        val rejectBtn: Button = findViewById(R.id.rejectOrder)

        val buyerNameField: TextView = findViewById(R.id.username)
        val buyerPhoneField: TextView = findViewById(R.id.phoneNumberBuyer)
        val buyerLocation: TextView = findViewById(R.id.location)
        val quantityField: TextView = findViewById(R.id.titleAvailableQuantity)
        val totalPriceField: TextView = findViewById(R.id.priceValue)
        val productNameField: TextView = findViewById(R.id.name_produs)

        val currentProductName = intent.getStringExtra("PRODUCT_NAME")
        val currentBuyerId = intent.getStringExtra("USERID")

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName).collection("orders")
        userDocRef.get().addOnSuccessListener { document ->
            for (doc in document){
                val buyerId = doc.getString("userId")
                val productName = doc.getString("product_name")

                if(currentProductName == productName && currentBuyerId == buyerId){
                    val name = doc.getString("name")
                    val surname = doc.getString("surname")
                    val address = doc.getString("address")
                    val city = doc.getString("city")
                    val country = doc.getString("country")
                    val phoneNumber = doc.getString("phone_number")
                    val quantity = doc.getString("quantity")
                    val price = doc.getString("priceTotal")

                    buyerNameField.text = "$name $surname"
                    buyerLocation.text = "$country, $city, $address"
                    buyerPhoneField.text = "$phoneNumber"
                    quantityField.text = "$quantity"
                    totalPriceField.text = "$price"
                    productNameField.text = "$productName"
                }
            }
        }

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)

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

        backBtn.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }


        rejectBtn.setOnClickListener{
                userDocRef.document("$currentProductName").delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomescreenActivity::class.java)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error in deleting the product", Toast.LENGTH_SHORT).show()
                    }
        }

        acceptBtn.setOnClickListener {
            val userDocRef2 = db.collection("users").document(documentName).collection("products").document("$currentProductName")
            userDocRef.document("$currentProductName").delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomescreenActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error in deleting the product", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
