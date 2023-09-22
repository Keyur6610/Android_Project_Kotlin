package com.example.minotes

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UpdateDeleteActivity : AppCompatActivity() {

    private lateinit var edtTittle: EditText
    private lateinit var edtDetails: EditText
    private lateinit var tickBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var databaseReference: DatabaseReference
    private  lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var  auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete)

        edtTittle = findViewById(R.id.edtTittle)
        edtDetails = findViewById(R.id.edtDetails)
        tickBtn = findViewById(R.id.tickBtn)
        backBtn = findViewById(R.id.backBtn)
        databaseReference = FirebaseDatabase.getInstance().getReference("Email")
        firebaseFirestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()


        var getTittle = intent.getStringExtra("tittle")
        var getDetails = intent.getStringExtra("details")
        var getId = intent.getStringExtra("id")

       edtTittle.setText(getTittle)
        edtDetails.setText(getDetails)

        tickBtn.setOnClickListener {

            if (edtTittle.text.toString().isNotBlank() || edtDetails.text.toString().isNotBlank()){

                var Tittle = edtTittle.text.toString()
                var Details = edtDetails.text.toString()

                var userData = hashMapOf(
                    "tittle" to Tittle,
                    "details" to Details
                )

                /*  var updateData =  firebaseFirestore.collection(auth.currentUser!!.uid.toString()).document().update(userData as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Update Successfully",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            this.finish()
                    }
                        .addOnFailureListener {
                            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                        } */


                // var id = databaseReference.push().key!!
                var data = user(getId,Tittle,Details)
                databaseReference.child(auth.currentUser!!.uid).child("Notes").child(getId.toString()).setValue(data)
                    .addOnCompleteListener {
                        Toast.makeText(this,"Update Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        this.finish()
                    }

                    .addOnFailureListener {
                        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                    }

            }


        }

     /*   deleteBtn.setOnClickListener {

            databaseReference.child(getId.toString()).removeValue()

            Toast.makeText(this@UpdateDeleteActivity,"Delete Successfully",Toast.LENGTH_SHORT).show()

            startActivity(Intent(this,MainActivity::class.java))
            finish()

        }*/

        backBtn.setOnClickListener {
            onBackPressed()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}