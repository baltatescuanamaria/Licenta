package com.example.myapp
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val editBtn: Button = findViewById(R.id.edit)
        editBtn.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        val nameField: TextView = findViewById(R.id.name)
        val locationField: TextView = findViewById(R.id.loc)

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
                nameField.text = "${name} ${surname}"
                locationField.text = "${city}, ${country}"
            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val settingsBtn: ImageButton = findViewById(R.id.settings)

        settingsBtn.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
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

