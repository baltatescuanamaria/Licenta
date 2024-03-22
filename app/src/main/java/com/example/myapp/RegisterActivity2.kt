package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity2 : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_part2)

        val login: Button = findViewById(R.id.next_btn)
        login.setOnClickListener{
            val intent = Intent(this, RegisterActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }
}