package com.example.myapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.inappmessaging.internal.Logging.TAG
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class EditProfileActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUrl: Uri? = null
    private var nameValue: String = " "
    private var rndmUUID: String = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit)

        val surnameField: EditText = findViewById(R.id.changeSurname)
        val nameField: EditText = findViewById(R.id.changeName)
        val usernameField: EditText = findViewById(R.id.changeUsername)
        val cityField: EditText = findViewById(R.id.changeCity)
        val countryField: EditText = findViewById(R.id.changeCountry)
        val descriptionField: EditText = findViewById(R.id.changeDescription)
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
                    val description = document.getString("description")
                    val imageUrl = document.getString("image_url")


                    nameField.setText(name)
                    surnameField.setText(surname)
                    usernameField.setText(username)
                    cityField.setText(city)
                    countryField.setText(country)
                    descriptionField.setText(description)

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
                    Log.d(TAG, "Document does not exist")
                }
            }

        val changeBtn: Button = findViewById(R.id.changePicture)
        changeBtn.setOnClickListener {
            openGallery()
        }


        val saveBtn: Button = findViewById(R.id.save)

        saveBtn.setOnClickListener{
            val surnameValue = surnameField.text.toString()
            val nameValue = nameField.text.toString()
            val usernameValue = usernameField.text.toString()
            val cityValue = cityField.text.toString()
            val countryValue = countryField.text.toString()
            val descriptionValue = descriptionField.text.toString()
            val imageValue = productImageField.imageAlpha.toString()

            val database = Firebase.firestore
            val updateInfo = hashMapOf<String, Any>()

            if (usernameValue.isNotEmpty()) {
                updateInfo["username"] = usernameValue
            }
            if (nameValue.isNotEmpty()) {
                updateInfo["name"] = nameValue
            }
            if (surnameValue.isNotEmpty()) {
                updateInfo["surname"] = surnameValue
            }
            if (cityValue.isNotEmpty()) {
                updateInfo["city"] = cityValue
            }
            if (countryValue.isNotEmpty()) {
                updateInfo["country"] = countryValue
            }
            if (descriptionValue.isNotEmpty()) {
                updateInfo["description"] = descriptionValue
            }
            if (imageValue.isNotEmpty()) {
                uploadImage()
                updateInfo["image_url"] = rndmUUID
            }

            val documentName = "user_${userId}"
            val doc = database.collection("users").document(documentName)

            doc.update(updateInfo)
                .addOnSuccessListener {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "An error occurred while updating the information :(", Toast.LENGTH_SHORT).show()
                }

        }
        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val backBtn: ImageButton = findViewById(R.id.back_button)
        backBtn.setOnClickListener {
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

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select an image"), AddProductActivity.PICK_IMAGE_REQUEST
        )
    }

    private fun uploadImage() {
        if (imageUrl != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.show()

            rndmUUID = UUID.randomUUID().toString()
            val ref = storageReference.child("images/$rndmUUID")

            ref.putFile(imageUrl!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagePlace: ImageView = findViewById(R.id.picturePlace)
        if (requestCode == AddProductActivity.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let {
                val inputStream = contentResolver.openInputStream(selectedImage)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imagePlace.setImageBitmap(bitmap)
                imageUrl = selectedImage
            }
        }
    }
}