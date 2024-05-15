package com.example.myapp
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging


class ProductActivity : AppCompatActivity() {
    private lateinit var idSeller:String
    var name:String = ""
    var price:String = ""
    var quantity:String=""
    var locationCity: String=""
    var locationCountry: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_page)

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
        val quantityAvailable: TextView = findViewById(R.id.titleAvailableQuantity)

        val wantedQuantity: EditText = findViewById(R.id.wantedQuantity)


        val nameProduct  = intent.getStringExtra("PRODUCT_NAME")
        val userId = intent.getStringExtra("USERID")
        val db = FirebaseFirestore.getInstance()
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName).collection("products").document("product_${nameProduct}")
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                name = document.getString("product_name").toString()
                price  = document.getString("price").toString()
                quantity = document.getString("quantity").toString()
                val description = document.getString("description")
                val pack = document.getString("package")


                nameField.text = name
                priceField.text = "$price lei/$pack"
                descriptionField.text = description
                quantityAvailable.text = quantity
            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }
        val userDocRef2 = db.collection("users").document(documentName)
        userDocRef2.get().addOnSuccessListener { document->
            if (document != null && document.exists()) {
                locationCity = document.getString("city").toString()
                locationCountry = document.getString("country").toString()
                val sellerName = document.getString("name")
                val sellerSurname = document.getString("surname")
                idSeller = document.getString("userId").toString()

                locationField.text = "${locationCountry}, ${locationCity}"
                sellerField.text = "${sellerName} ${sellerSurname}"


            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }

        val addItem: Button = findViewById(R.id.addItem)
        addItem.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
            val q = wantedQuantity.text.toString()
            if(q.isEmpty() || q.toInt() > quantity.toInt()){
                wantedQuantity.setError("Error")
            } else {
                val nameValue = name
                val sellerValue = sellerField.text.toString()
                val priceValue = price
                addItemToCart(nameValue, sellerValue, priceValue, q, idSeller, locationCity, locationCountry)
            }
        }

        val addToWishlist: Button = findViewById(R.id.addToWishlist)
        addToWishlist.setOnClickListener{
            val nameValue = nameField.text.toString()
            val sellerValue = sellerField.text.toString()
            val q = wantedQuantity.text.toString()
            if(q.isEmpty() || q.toInt() > quantity.toInt()){
                wantedQuantity.setError("Error")
            } else {
                addItemToWishlist(idSeller, nameValue, sellerValue, q)
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

    private fun addItemToWishlist(idSeller:String, nameValue: String, sellerValue: String, quantity: String){
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "seller" to sellerValue,
            "idSeller" to idSeller,
            "wanted_quantity" to quantity
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("wishlist").document("wishlist_${nameValue}")
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
    private fun addItemToCart(nameValue: String, sellerValue: String, priceValue: String, idSeller: String, quantity:String, locationCity: String, locationCountry: String){
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "seller" to sellerValue,
            "price" to priceValue,
            "quantity" to quantity,
            "idSeller" to idSeller,
            "city" to locationCity,
            "country" to locationCountry
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