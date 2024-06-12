package com.example.myapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class AddImageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUrl: Uri? = null
    private var rndmUUID: String = " "

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_image)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            openGallery()
        }

        val nextAction: Button = findViewById(R.id.next_btn)
        nextAction.setOnClickListener {
            uploadImage()
        }

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity4::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun uploadImage() {
        if (imageUrl != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading...")
            progressDialog.show()

            rndmUUID = UUID.randomUUID().toString()

            val ref = storageReference.child("images/$rndmUUID")
            ref.putFile(imageUrl!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()

                        val imageDownloadUrl = uri.toString()
                        val db = FirebaseFirestore.getInstance()
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        val documentName = "user_$userId"

                        val userImage = hashMapOf(
                            "image_url" to rndmUUID
                        )

                        db.collection("users").document(documentName)
                            .set(userImage, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Image URL added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                            }

                        val intent = Intent(this, HomescreenActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagePlace: ImageView = findViewById(R.id.picturePlace)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
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
