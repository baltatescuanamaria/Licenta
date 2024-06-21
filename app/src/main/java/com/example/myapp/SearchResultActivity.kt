package com.example.myapp;
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
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SearchResultActivity : AppCompatActivity() {
    var location: String = ""
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var isActivityActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result)

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        //val addItemBtn: Button = findViewById(R.id.addItem)
        //val selectBtn: Button = findViewById(R.id.buttonProduct)
        //val picturePlace: ImageView = findViewById(R.id.picturePlace)
        val mainPage: LinearLayout = findViewById(R.id.mainLayout)
        //val maximumLength = 15
        val db = FirebaseFirestore.getInstance()
        //val currentUser = FirebaseAuth.getInstance().currentUser
        //val currentUserId = currentUser?.uid

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        isActivityActive = true

        val search = intent.getStringExtra("SEARCH").toString()

        val userProductsRef = db.collection("products")
        userProductsRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val productName = document.getString("product_name").toString()
                val surname = document.getString("surname").toString()
                val name = document.getString("name").toString()
                val username = document.getString("username").toString()
                val category = document.getString("category").toString()

                if (productName.uppercase().contains(search) || surname.uppercase().contains(search) || name.uppercase().contains(search) || username.uppercase().contains(search) || category.uppercase().contains(search)) {
                    val userIdOwner = document.getString("idSeller").toString()
                    val locationCity = document.getString("city").toString()
                    val locationCountry = document.getString("country").toString()
                    val price = document.getString("price").toString()
                    val imageUrl = document.getString("image_url")

                    location = "$locationCountry, $locationCity"
                    val layoutInflater = LayoutInflater.from(this)
                    val productLayout = layoutInflater.inflate(
                        R.layout.product_layout_wishlist,
                        null
                    )
                    val productNameBtn: LinearLayout =
                        productLayout.findViewById(R.id.productButton)
                    val productNameField: TextView =
                        productLayout.findViewById(R.id.numeProdus)
                    val priceTextField: TextView =
                        productLayout.findViewById(R.id.price)
                    val locationTextField: TextView =
                        productLayout.findViewById(R.id.location)
                    val productImageField: ImageView =
                        productLayout.findViewById(R.id.picturePlace)

                    productNameField.text = productName
                    priceTextField.text = "$price lei"
                    locationTextField.text =
                        "$locationCountry, $locationCity"
                    mainPage.addView(productLayout)

                    productNameBtn.setOnClickListener {
                        val intent = Intent(
                            this,
                            ProductWishlistActivity::class.java
                        )
                        intent.putExtra("PRODUCT_NAME", productName)
                        intent.putExtra("USERID", userIdOwner)
                        startActivity(intent)
                        //finish()
                    }


                    if (imageUrl != null) {
                        val storageRef =
                            storage.reference.child("images/$imageUrl")
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            if (isActivityActive) {
                                Glide.with(this)
                                    .load(uri)
                                    .into(productImageField)
                            }
                        }.addOnFailureListener {
                            Log.e(
                                "Firebase Storage",
                                "Error getting image URL",
                                it
                            )
                        }
                    }
                }
            }
            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting products", exception)
            }

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

                val backButton: ImageButton = findViewById(R.id.back_button)
                backButton.setOnClickListener {
                    val intent = Intent(this, HomescreenActivity::class.java)
                    startActivity(intent)
                    finish()
                }


                homeBtn.setOnClickListener {
                    val intent = Intent(this, HomescreenActivity::class.java)
                    startActivity(intent)
                    finish()
                }


                productsBtn.setOnClickListener {
                    val intent = Intent(this, ProductsActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                wishlistBtn.setOnClickListener {
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                profileBtn.setOnClickListener {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
