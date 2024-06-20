package com.example.myapp
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileActivity : AppCompatActivity() {
    private var imageUrl: String = ""
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val editBtn: Button = findViewById(R.id.edit)
        editBtn.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val nameField: TextView = findViewById(R.id.name)
        val locationField: TextView = findViewById(R.id.loc)
        val phoneNumberField: TextView = findViewById(R.id.PhoneNumber)
        val descriptionField: TextView = findViewById(R.id.description)
        val productImageField: ImageView = findViewById(R.id.picturePlace)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName)
        userDocRef.get().addOnSuccessListener { document->
            if (document != null && document.exists()) {
                val name = document.getString("name")
                val surname = document.getString("surname")
                val username = document.getString("username")
                val city = document.getString("city")
                val country = document.getString("country")
                val phoneNumber = document.getString("phoneNumber")
                val description = document.getString("description")
                imageUrl = document.getString("image_url").toString()


                nameField.text = "${name} ${surname}"
                locationField.text = "${city}, ${country}"
                phoneNumberField.text = phoneNumber
                descriptionField.text = description
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

            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val logoutBtn: Button = findViewById(R.id.logout)
        val ordersBtn: Button = findViewById(R.id.orders)

        logoutBtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
        }
        ordersBtn.setOnClickListener{
            val intent = Intent(this, OrdersActivity::class.java)
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

