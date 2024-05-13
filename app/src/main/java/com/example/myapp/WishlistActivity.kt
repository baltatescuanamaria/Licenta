package com.example.myapp;
import android.annotation.SuppressLint
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

class WishlistActivity : AppCompatActivity() {
        @SuppressLint("SetTextI18n")
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.wishlist)

                val homeBtn: ImageButton = findViewById(R.id.home)
                val messagesBtn: ImageButton = findViewById(R.id.message)
                val productsBtn: ImageButton = findViewById(R.id.products)
                val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
                val profileBtn: ImageButton = findViewById(R.id.profile)
                //val addItemBtn: Button = findViewById(R.id.addItem)
                //val selectBtn: Button = findViewById(R.id.buttonProduct)
                //val picturePlace: ImageView = findViewById(R.id.picturePlace)
                val mainPage: LinearLayout = findViewById(R.id.mainLayout)
                val maximumLength = 15
                val db = FirebaseFirestore.getInstance()
                db.collection("users").get().addOnSuccessListener { users ->
                        for (user in users) {
                                val userId = user.id
                                val userProductsRef = db.collection("users").document(userId)
                                        .collection("wishlist")
                                userProductsRef.get().addOnSuccessListener { documents ->
                                        for (document in documents) {
                                                val productName = document.getString("product_name")
                                                val userIdOwner = document.getString("idSeller")
                                                val price = document.getString("price")
                                                var location: String = ""
                                                /*val quantity = document.getString("quantity")
                                                val description = document.getString("description")
                                                val picture = document.getString("imageUrl")*/

                                                val ownerData = db.collection("users")
                                                        .document("user_$userIdOwner").get()
                                                        .addOnSuccessListener { document ->
                                                                if (document != null && document.exists()) {
                                                                        val name =
                                                                                document.getString("name")
                                                                        val surname =
                                                                                document.getString("surname")
                                                                        val username =
                                                                                document.getString("username")
                                                                        val city =
                                                                                document.getString("city")
                                                                        val country =
                                                                                document.getString("country")
                                                                        location = "$country, $city"
                                                                }
                                                                val layoutInflater =
                                                                        LayoutInflater.from(this)
                                                                val productLayout =
                                                                        layoutInflater.inflate(
                                                                                R.layout.product_layout_wishlist,
                                                                                null
                                                                        )
                                                                val productNameButton: LinearLayout =
                                                                        productLayout.findViewById(R.id.product)
                                                                val productNameSpace: TextView =
                                                                        productLayout.findViewById(R.id.numeProdus)
                                                                val priceText: TextView =
                                                                        productLayout.findViewById(R.id.price)
                                                                val locationText: TextView =
                                                                        productLayout.findViewById(R.id.location)
                                                                if (productName != null) {
                                                                        if (productName.length > maximumLength) {
                                                                                productNameSpace.text =
                                                                                        productName.substring(
                                                                                                0,
                                                                                                maximumLength
                                                                                        ) + "..."
                                                                        } else if (location.length > maximumLength) {
                                                                                locationText.text =
                                                                                        location.substring(
                                                                                                0,
                                                                                                maximumLength
                                                                                        ) + "..."
                                                                        } else {
                                                                                productNameSpace.text =
                                                                                        productName
                                                                                locationText.text =
                                                                                        location

                                                                        }
                                                                }
                                                                priceText.text = "$price lei"
                                                                mainPage.addView(productLayout)

                                                                //selectBtn.text = name
                                                                productNameButton.setOnClickListener {
                                                                        val intent = Intent(
                                                                                this,
                                                                                ProductWishlistActivity::class.java
                                                                        )
                                                                        intent.putExtra(
                                                                                "PRODUCT_NAME",
                                                                                productName
                                                                        )
                                                                        intent.putExtra(
                                                                                "ID_OWNER",
                                                                                userIdOwner
                                                                        )
                                                                        startActivity(intent)
                                                                        finish()
                                                                        overridePendingTransition(
                                                                                R.anim.slide_in,
                                                                                R.anim.slide_out
                                                                        )
                                                                }
                                                        }
                                        }
                                }
                                val backButton: ImageButton = findViewById(R.id.back_button)
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
        }
}