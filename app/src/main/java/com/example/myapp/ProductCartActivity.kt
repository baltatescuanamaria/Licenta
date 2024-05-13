package com.example.myapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

class ProductCartActivity : AppCompatActivity() {
    private lateinit var idSeller:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_page_cart)

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
        val userId = intent.getStringExtra("USERID")
        val db = FirebaseFirestore.getInstance()
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document("user_${userId}").collection("products").document("product_$nameProduct")
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val name = document.getString("product_name")
                val price  = document.getString("price")
                val quantity = document.getString("quantity")
                val description = document.getString("description")


                nameField.text = name
                priceField.text = price
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
                val sellerName = document.getString("name")
                val sellerSurname = document.getString("surname")
                idSeller = document.getString("userId").toString()

                locationField.text = "${locationCountry}, ${locationCity}"
                sellerField.text = "${sellerName} ${sellerSurname}"


            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }

        backButton.setOnClickListener{
            val intent = Intent(this, CheckoutActivity::class.java)
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
}