package com.example.myapp;
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

class OrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orders_list)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        //val selectBtn: Button = findViewById(R.id.buttonProduct)
        //val picturePlace: ImageView = findViewById(R.id.picturePlace)
        val mainPage: LinearLayout = findViewById(R.id.mainLayout)

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val maximumLength = 15
        val userDocRef = db.collection("users").document(documentName).collection("orders")
        userDocRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val productName = document.getString("product_name")
                //val price  = document.getString("price")
                val buyerId = document.getString("userId")
                val quantity = document.getString("quantity")
                /*val description = document.getString("description")
                val picture = document.getString("imageUrl")*/


                val layoutInflater = LayoutInflater.from(this)
                val productLayout = layoutInflater.inflate(R.layout.product_layout, null)
                val productNameButton: LinearLayout = productLayout.findViewById(R.id.product)
                val productNameSpace: TextView = productLayout.findViewById(R.id.numeProdus)
                val priceText: TextView = productLayout.findViewById(R.id.price)

                if (productName != null) {
                    if (productName.length > maximumLength) {
                        productNameSpace.text =
                            productName.substring(0, maximumLength) + "..."
                    } else {
                        productNameSpace.text = productName
                    }
                    priceText.text = "Quantity wanted: $quantity"
                    mainPage.addView(productLayout)

                    //selectBtn.text = name
                    productNameButton.setOnClickListener {
                        val intent = Intent(this, OrderDetailsActivity::class.java)
                        intent.putExtra("PRODUCT_NAME", productName)
                        intent.putExtra("USERID", buyerId)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    }
                }
            }
        }
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
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
