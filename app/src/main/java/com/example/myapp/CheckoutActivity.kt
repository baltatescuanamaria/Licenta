package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_checkout)

        val checkout: Button = findViewById(R.id.checkout)
        checkout.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }
}