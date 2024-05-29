package com.example.myapp;
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomescreenActivity : AppCompatActivity() {
    val categoriesList = mutableSetOf<String>()
    private lateinit var idSeller:String
    var name:String = ""
    var price:String = ""
    var quantity:String=""
    var locationCity: String=""
    var locationCountry: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen)

        val homeBtn: ImageButton = findViewById(R.id.home)
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

        val userProductsRef = currentUserId?.let {
            db.collection("users").document("user_$currentUserId")
                .collection("wishlist").get().addOnSuccessListener { documents->
                for (document in documents) {
                    val category = document.getString("category")
                    if (category != null) {
                        categoriesList.add(category)
                    }
                }
                }
        }
        db.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val name = document.getString("product_name").toString()
                    val price = document.getString("price").toString()
                    val locationCity = document.getString("city").toString()
                    val locationCountry = document.getString("country").toString()
                    val idSeller = document.getString("userId").toString()
                    val category = document.getString("category")
                    if (idSeller != currentUserId && category != null && categoriesList.contains(category)) {
                        val layoutInflater = LayoutInflater.from(this)
                        val productLayout =
                            layoutInflater.inflate(R.layout.homescreen_product_layout, null)
                        val productButton: LinearLayout =
                            productLayout.findViewById(R.id.product)
                        val productNameSpace: TextView =
                            productLayout.findViewById(R.id.numeProdus)
                        val locationText: TextView = productLayout.findViewById(R.id.location)
                        productNameSpace.text = name
                        locationText.text = "$locationCountry, $locationCity"
                        mainPage.addView(productLayout)
                        //selectBtn.text = name
                        productButton.setOnClickListener {
                            val intent = Intent(this, ProductActivity::class.java)
                            intent.putExtra("PRODUCT_NAME", name)
                            intent.putExtra("USERID", idSeller)
                            startActivity(intent)
                            finish()
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                        }
                    }
                    }
                }
                        .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting products", exception)
                }

        val secondPage: LinearLayout = findViewById(R.id.arrayProductsRecAdded)
        var city = ""
        var country = ""

        val userProductsRef2 = currentUserId?.let {
            db.collection("users").document("user_$currentUserId").get().addOnSuccessListener { data->
                        country = data.getString("country").toString()
                        city = data.getString("city").toString()
                    }
                }

        val userDocRef = db.collection("products")
        userDocRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                var productCount = 0
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                val category = document.getString("category")

                if (idSeller != currentUserId && locationCity == city && productCount < 15) {
                    Log.d("Firestore", "Adding product: $name")
                    val layoutInflater = LayoutInflater.from(this)
                    val productLayout =
                        layoutInflater.inflate(R.layout.homescreen_product_layout, null)
                    val productButton: LinearLayout =
                        productLayout.findViewById(R.id.product)
                    val productNameSpace: TextView =
                        productLayout.findViewById(R.id.numeProdus)
                    val locationText: TextView = productLayout.findViewById(R.id.location)
                    productNameSpace.text = name
                    locationText.text = "$locationCountry, $locationCity"
                    secondPage.addView(productLayout)

                    //selectBtn.text = name
                    productButton.setOnClickListener {
                        val intent = Intent(this, ProductActivity::class.java)
                        intent.putExtra("PRODUCT_NAME", name)
                        intent.putExtra("USERID", idSeller)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    }
                    productCount++
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
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