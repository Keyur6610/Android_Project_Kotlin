package com.example.minotes

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    lateinit var edtName:EditText
    lateinit var edtEmail:EditText
    lateinit var edtPsw:EditText
    lateinit var edtNumber:EditText
    lateinit var signupBtn:Button
    lateinit var auth: FirebaseAuth
    lateinit var firebaseFireStore:FirebaseFirestore
    lateinit var firebaseDatabase: DatabaseReference


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPsw = findViewById(R.id.edtPsw)
        signupBtn = findViewById(R.id.signupBtn)
        auth = FirebaseAuth.getInstance()
       firebaseFireStore = FirebaseFirestore.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Email")


        signupBtn.setOnClickListener {

            if (edtName.text.toString().isNotBlank() && edtEmail.text.toString().isNotBlank() && edtPsw.text.toString().isNotBlank()){

                var name = edtName.text.toString()
                var email = edtEmail.text.toString()
                var psw = edtPsw.text.toString()

                var userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "password" to psw

                )

                var userModel = user(name,email,psw)
                var userDetails = userDetails(name,email,psw)
                var users = firebaseFireStore.collection("users")
                var q = users.whereEqualTo("email",email).get()
                    .addOnSuccessListener {task->
                        if (task.isEmpty){
                            auth.createUserWithEmailAndPassword(email,psw).addOnCompleteListener {task->
                                if(task.isSuccessful){

                                   // users.document(auth.currentUser!!.uid).set(userData)
                                    firebaseDatabase.child(auth.currentUser!!.uid).child("User Details").setValue(userDetails)
                                    Toast.makeText(this,"Sign up Successfully",Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@SignupActivity,MainActivity::class.java))
                                    this.finish()


                                }else{
                                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(this,"user already registered",Toast.LENGTH_SHORT).show()
                        }

                    }

            }else{
                Toast.makeText(this,"Enter Details",Toast.LENGTH_SHORT).show()
            }

        }

    }

    data class userDetails(
        var name:String? = null,
        var email:String? = null,
        var password:String? = null
    )

}