package com.example.myapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_part1)

        auth = FirebaseAuth.getInstance()
        val nextBtn: Button = findViewById(R.id.next_btn)
        val nameField: EditText = findViewById(R.id.name_input)
        val surnameField: EditText = findViewById(R.id.surname_input)
        val usernameField: EditText = findViewById(R.id.username_input)
        val emailField: EditText = findViewById(R.id.email_input)
        val passwordField: EditText = findViewById(R.id.password_input)
        val verifyPasswordField: EditText = findViewById(R.id.verify_password_input)
        /*val nameError: TextView = findViewById(R.id.nameError)
        val surnameError: TextView = findViewById(R.id.surnameError)
        val usernameError: TextView = findViewById(R.id.usernameError)
        val emailError: TextView = findViewById(R.id.emailError)
        val passwordError: TextView = findViewById(R.id.passwordError)
        val verifyPasswordError: TextView = findViewById(R.id.verifyPasswordError)
        */
        val backBtn: ImageButton = findViewById(R.id.back_button)
        backBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        nextBtn.setOnClickListener{
            val nameValue = nameField.text.toString()
            val surnameValue = surnameField.text.toString()
            val usernameValue = usernameField.text.toString()
            val emailValue = emailField.text.toString()
            val passwordValue = passwordField.text.toString()
            val verifyPasswordValue = verifyPasswordField.text.toString()

            var hasError = false

            if (nameValue.isEmpty()) {
                nameField.error = "Input required"
                hasError = true
            }

            if (surnameValue.isEmpty()) {
                surnameField.error = "Input required"
                hasError = true
            }
            //TODO: de verificat ca nu username nu este deja in uz (sa vad cum sa salvez datele in Firebase)
            if (usernameValue.isEmpty()) {
                usernameField.error = "Input required"
                hasError = true
            }

            if (emailValue.isEmpty()){
                emailField.error = "Input required"
                hasError = true
            } else {

                val email = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

                if (!email.matches(emailValue)) {
                    emailField.error = "Incorrect email address"
                    hasError = true
                } else {

                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(emailValue)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val result = task.result?.signInMethods
                                if (!result.isNullOrEmpty()) {
                                    emailField.error = "Email already in use"
                                    hasError = true
                                }
                            } else {
                                println("Error")
                            }
                        }
                }
            }

            if (passwordValue.isEmpty()) {
                passwordField.error = "Input required"
                hasError = true
            }

            if(passwordValue.length < 6) {
                passwordField.error = "The password has to have at least 6 characters"
                hasError = true
            }

            val uppercase = Regex("[A-Z]").findAll(passwordValue).count()
            val digits = Regex("[0-9]").findAll(passwordValue).count()
            val symbols = Regex("[^a-zA-Z0-9]").findAll(passwordValue).count()

            if (uppercase == 0 && (digits == 0 || symbols == 0)) {
                passwordField.error = "Password has to have at lest one uppercase letter, one symbol or one number"
                hasError = true
            }

            if (verifyPasswordValue.isEmpty()) {
                verifyPasswordField.error = "Input required"
                hasError = true
            }

            if(verifyPasswordValue != passwordValue){
                verifyPasswordField.error = "Different password inputs"
                hasError = true
            }

            if(!hasError) {
                registerUser(nameValue, surnameValue, usernameValue, emailValue, passwordValue)
            }

        }
    }

    private fun registerUser(nameValue:String, surnameValue: String, usernameValue:String, emailValue: String, passwordValue:String) {
            val intent = Intent(this, RegisterActivity4::class.java)
            intent.putExtra("NAME_KEY", nameValue)
            intent.putExtra("SURNAME_KEY", surnameValue)
            intent.putExtra("USERNAME_KEY", usernameValue)
            intent.putExtra("EMAIL_KEY", emailValue)
            intent.putExtra("PASSWORD_KEY", passwordValue)

            startActivity(intent)
            finish()
    }
}
