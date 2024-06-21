package com.example.myapp
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toUpperCase
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Locale

class HomescreenActivity : AppCompatActivity() {
    private val categoriesList = mutableSetOf<String>()
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var name:String = ""
    var price:String = ""
    var quantity:String=""
    var locationCity: String=""
    var locationCountry: String=""
    private var productCount = 0

    @SuppressLint("CutPasteId")
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
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val editText = findViewById<EditText>(R.id.search)

        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputText = editText.text.toString().uppercase()
                val intent = Intent(this, SearchResultActivity::class.java)
                intent.putExtra("SEARCH", inputText)

                startActivity(intent)
                return@setOnEditorActionListener true
            }
            false
        }


        val userDocRef1 = db.collection("users").document("user_$currentUserId").collection("wishlist")
        userDocRef1.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                val category = document.getString("category")
                val packageType = document.getString("package").toString()
                val imageUrl = document.getString("image_url").toString()

                val prod1 = db.collection("products")
                prod1.get().addOnSuccessListener { products ->
                    for (product in products) {
                        val categoryProd = product.getString("category").toString()
                        val countyProd = product.getString("country").toString()
                        val idProd = product.getString("userId").toString()

                        if (categoryProd == category && countyProd == locationCountry && idProd != currentUserId) {
                            val layoutInflater = LayoutInflater.from(this)
                            val productLayout =
                                layoutInflater.inflate(R.layout.homescreen_product_layout, null)
                            val productButton: LinearLayout =
                                productLayout.findViewById(R.id.product)
                            val productNameSpace: TextView =
                                productLayout.findViewById(R.id.numeProdus)
                            val locationText: TextView = productLayout.findViewById(R.id.location)
                            val priceText: TextView = productLayout.findViewById(R.id.price)
                            val productImageField: ImageView =
                                productLayout.findViewById(R.id.picturePlace)

                            productNameSpace.text = name
                            locationText.text = "$locationCountry, $locationCity"
                            priceText.text = "$price lei/$packageType"
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
                            mainPage.addView(productLayout)

                            productButton.setOnClickListener {
                                val intent = Intent(this, ProductActivity::class.java)
                                intent.putExtra("PRODUCT_NAME", name)
                                intent.putExtra("USERID", idProd)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }

        val secondPage: LinearLayout = findViewById(R.id.arrayProductsRecAdded)


        val userDocRef = db.collection("users").document("user_$currentUserId").collection("reccs2")
        userDocRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                //val category = document.getString("category")
                val packageType = document.getString("package").toString()
                val imageUrl = document.getString("image_url").toString()


                val layoutInflater = LayoutInflater.from(this)
                val productLayout = layoutInflater.inflate(R.layout.homescreen_product_layout2, null)
                val productButton: LinearLayout = productLayout.findViewById(R.id.product)
                val productNameSpace: TextView = productLayout.findViewById(R.id.numeProdus)
                val locationText: TextView = productLayout.findViewById(R.id.location)
                val priceText: TextView = productLayout.findViewById(R.id.price)
                val productImageField: ImageView = productLayout.findViewById(R.id.picturePlace)

                    productNameSpace.text = name
                    locationText.text = "$locationCountry, $locationCity"
                    priceText.text = "$price lei/$packageType"
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
                    secondPage.addView(productLayout)

                    productButton.setOnClickListener {
                        val intent = Intent(this, ProductActivity::class.java)
                        intent.putExtra("PRODUCT_NAME", name)
                        intent.putExtra("USERID", idSeller)
                        startActivity(intent)
                        //finish()
                    }


            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }

        val thirdPage: LinearLayout = findViewById(R.id.arrayProductssameSeller)


        val userDocRef2 = db.collection("users").document("user_$currentUserId").collection("reccs3")
        userDocRef2.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                //val category = document.getString("category")
                val packageType = document.getString("package").toString()
                val imageUrl = document.getString("image_url").toString()


                val layoutInflater = LayoutInflater.from(this)
                val productLayout = layoutInflater.inflate(R.layout.homescreen_product_layout3, null)
                val productButton: LinearLayout = productLayout.findViewById(R.id.product)
                val productNameSpace: TextView = productLayout.findViewById(R.id.numeProdus)
                val locationText: TextView = productLayout.findViewById(R.id.location)
                val priceText: TextView = productLayout.findViewById(R.id.price)
                val productImageField: ImageView = productLayout.findViewById(R.id.picturePlace)

                productNameSpace.text = name
                locationText.text = "$locationCountry, $locationCity"
                priceText.text = "$price lei/$packageType"
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
                thirdPage.addView(productLayout)

                productButton.setOnClickListener {
                    val intent = Intent(this, ProductActivity::class.java)
                    intent.putExtra("PRODUCT_NAME", name)
                    intent.putExtra("USERID", idSeller)
                    startActivity(intent)
                    //finish()
                }


            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }

        val forthPage: LinearLayout = findViewById(R.id.arrayProductsyourArea)


        val userDocRef4 = db.collection("users").document("user_$currentUserId").collection("wishlist")
        userDocRef4.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val name = document.getString("product_name").toString()
                val price = document.getString("price").toString()
                val locationCity = document.getString("city").toString()
                val locationCountry = document.getString("country").toString()
                val idSeller = document.getString("userId").toString()
                val category = document.getString("category")
                val packageType = document.getString("package").toString()
                val imageUrl = document.getString("image_url").toString()

                val prod1 = db.collection("products")
                prod1.get().addOnSuccessListener { products ->
                    for (product in products) {
                        val categoryProd = product.getString("category").toString()
                        val countyProd = product.getString("country").toString()
                        val idProd = product.getString("userId").toString()

                        if (categoryProd != category && countyProd == locationCountry && idProd != currentUserId) {
                            val layoutInflater = LayoutInflater.from(this)
                            val productLayout =
                                layoutInflater.inflate(R.layout.homescreen_product_layout, null)
                            val productButton: LinearLayout =
                                productLayout.findViewById(R.id.product)
                            val productNameSpace: TextView =
                                productLayout.findViewById(R.id.numeProdus)
                            val locationText: TextView = productLayout.findViewById(R.id.location)
                            val priceText: TextView = productLayout.findViewById(R.id.price)
                            val productImageField: ImageView =
                                productLayout.findViewById(R.id.picturePlace)

                            productNameSpace.text = name
                            locationText.text = "$locationCountry, $locationCity"
                            priceText.text = "$price lei/$packageType"
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
                            forthPage.addView(productLayout)

                            productButton.setOnClickListener {
                                val intent = Intent(this, ProductActivity::class.java)
                                intent.putExtra("PRODUCT_NAME", name)
                                intent.putExtra("USERID", idProd)
                                startActivity(intent)

                            }
                        }
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting products", exception)
        }

        fruitsBtn.setOnClickListener{
            val intent = Intent(this, FruitsListActivity::class.java)
            startActivity(intent)
            //finish()
        }

        veggiesBtn.setOnClickListener{
            val intent = Intent(this, VeggieListActivity::class.java)
            startActivity(intent)
            //finish()
        }

        animalBtn.setOnClickListener{
            val intent = Intent(this, AnimalListActivity::class.java)
            startActivity(intent)
            //finish()
        }

        beekeepingBtn.setOnClickListener{
            val intent = Intent(this, BeekeepingListActivity::class.java)
            startActivity(intent)
            //finish()
        }

        viticultureBtn.setOnClickListener{
            val intent = Intent(this, ViticultureListActivity::class.java)
            startActivity(intent)
            //finish()
        }

        preservedBtn.setOnClickListener{
            val intent = Intent(this, PreservedListActivity::class.java)
            startActivity(intent)
            //finish()
        }

        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            //finish()
        }

        productsBtn.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            //finish()
        }

        wishlistBtn.setOnClickListener {
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)
            //finish()
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            //finish()
        }
        cartBtn.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
            //finish()
        }
    }
}