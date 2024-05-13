package com.example.myapp;
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.AnimalListActivity
import com.example.myapp.BeekeepingListActivity
import com.example.myapp.CheckoutActivity
import com.example.myapp.FruitsListActivity
import com.example.myapp.MessageListActivity
import com.example.myapp.PreservedListActivity
import com.example.myapp.ProductActivity
import com.example.myapp.ProductsActivity
import com.example.myapp.ProfileActivity
import com.example.myapp.R
import com.example.myapp.VeggieListActivity
import com.example.myapp.ViticultureListActivity
import com.example.myapp.WishlistActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomescreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val cartBtn: ImageButton = findViewById(R.id.cart)

        val fruitsBtn : ImageButton = findViewById(R.id.fruits)
        val veggiesBtn : ImageButton = findViewById(R.id.veggies)
        val animalBtn : ImageButton = findViewById(R.id.animal)
        val beekeepingBtn : ImageButton = findViewById(R.id.beekeeping)
        val viticultureBtn : ImageButton = findViewById(R.id.viticulture)
        val preservedBtn : ImageButton = findViewById(R.id.foodPers)

        val mainPage: LinearLayout = findViewById(R.id.arrayProducts)
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid
        db.collection("users").get().addOnSuccessListener { users ->
            for (user in users) {
                val userId = user.id
                val city  = user.getString("city")
                val country  = user.getString("country")
                val location = "$city, $country"
                val userProductsRef = db.collection("users").document(userId).collection("products")
                userProductsRef.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val productName = document.getString("product_name")
                        val userIdOwner = document.getString("userId")
                        val category = document.getString("category")
                        /*val quantity = document.getString("quantity")
                        val description = document.getString("description")
                        val picture = document.getString("imageUrl")*/
                        if (userIdOwner != currentUserId) {
                            val layoutInflater = LayoutInflater.from(this)
                            val productLayout =
                                layoutInflater.inflate(R.layout.homescreen_product_layout, null)
                            val productButton: LinearLayout =
                                productLayout.findViewById(R.id.product)
                            val productNameSpace: TextView =
                                productLayout.findViewById(R.id.numeProdus)
                            val locationText: TextView = productLayout.findViewById(R.id.location)
                            productNameSpace.text = productName
                            locationText.text = location
                            mainPage.addView(productLayout)
                            //selectBtn.text = name
                            productButton.setOnClickListener {
                                val intent = Intent(this, ProductActivity::class.java)
                                intent.putExtra("PRODUCT_NAME", productName)
                                intent.putExtra("USERID", userIdOwner)
                                startActivity(intent)
                                finish()
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                            }
                        }
                            }
                        }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting products", exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting users", exception)
        }

        fruitsBtn.setOnClickListener{
            val intent = Intent(this, FruitsListActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        veggiesBtn.setOnClickListener{
            val intent = Intent(this, VeggieListActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        animalBtn.setOnClickListener{
            val intent = Intent(this, AnimalListActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        beekeepingBtn.setOnClickListener{
            val intent = Intent(this, BeekeepingListActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        viticultureBtn.setOnClickListener{
            val intent = Intent(this, ViticultureListActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        preservedBtn.setOnClickListener{
            val intent = Intent(this, PreservedListActivity::class.java)
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
        cartBtn.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }
}