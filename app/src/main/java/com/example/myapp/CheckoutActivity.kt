package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_checkout)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        var priceTotalText: TextView = findViewById(R.id.pretTotal)
        val checkoutBtn: Button = findViewById(R.id.checkout)

        var priceTotal = 0

        val mainPage: LinearLayout = findViewById(R.id.mainLayout)
        val maximumLength = 15
        val db = FirebaseFirestore.getInstance()
        db.collection("users").get().addOnSuccessListener { users ->
            for (user in users) {
                val userId = user.id
                val userProductsRef = db.collection("users").document(userId)
                    .collection("cart")
                userProductsRef.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val productName = document.getString("product_name")
                        val userIdOwner = document.getString("idSeller")
                        val price = document.getString("price")
                        if (price != null) {
                            priceTotal += price.toIntOrNull() ?: 0
                        }
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
                                priceTotalText.text = priceTotal.toString()

                                //selectBtn.text = name
                                productNameButton.setOnClickListener {
                                    val intent = Intent(
                                        this, ProductCartActivity::class.java)
                                    intent.putExtra("PRODUCT_NAME", productName)
                                    intent.putExtra("ID_OWNER", userIdOwner)
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


                val checkout: Button = findViewById(R.id.checkout)
                checkout.setOnClickListener {
                    val intent = Intent(this, HomescreenActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }

                val backButton: ImageButton = findViewById(R.id.back_button)
                backButton.setOnClickListener {
                    val intent = Intent(this, HomescreenActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }

                checkoutBtn.setOnClickListener {
                    val intent = Intent(this, CartActivity::class.java)
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