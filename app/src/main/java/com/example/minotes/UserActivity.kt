package com.example.minotes


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserActivity : AppCompatActivity() {
   private lateinit var edtTittle: EditText
    private lateinit var edtDetails: EditText
    private lateinit var tickBtn: ImageButton
    lateinit var backBtn:ImageButton
    lateinit var userLayout:ConstraintLayout
    private lateinit var databaseReference: DatabaseReference
    private  lateinit var firebaseFirestore: FirebaseFirestore
    private  lateinit var auth: FirebaseAuth


    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        edtTittle = findViewById(R.id.edtTittle)
        edtDetails = findViewById(R.id.edtDetails)
        tickBtn = findViewById(R.id.tickBtn)
        backBtn = findViewById(R.id.backBtn)
        userLayout = findViewById(R.id.userLayout)
       // databaseReference = FirebaseDatabase.getInstance().getReference("usernotes")
        databaseReference = FirebaseDatabase.getInstance().getReference("Email")
        firebaseFirestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()



       // edtTittle.setTextSize(10F)

        backBtn.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })


       tickBtn.setOnClickListener {

           if (edtTittle.text.toString().isNotBlank() || edtDetails.text.toString().isNotBlank()){

               var tittle = edtTittle.text.toString()
               var details = edtDetails.text.toString()


               var notesData = hashMapOf(
                   "tittle" to tittle,
                   "details" to details
               )

               /*   var users = firebaseFirestore.collection(auth.currentUser!!.uid.toString()).add(notesData)
                      .addOnSuccessListener {
                          Toast.makeText(this,"Note Created Successfully", Toast.LENGTH_SHORT).show()
                          startActivity(Intent(this, MainActivity::class.java))
                          finish()
                      }
                          .addOnFailureListener {
                              Toast.makeText(this,"fail to add note", Toast.LENGTH_SHORT).show()
                          }*/


               var userId = databaseReference.push().key!!
               var userData = user(userId,tittle,details)

               databaseReference.child(auth.currentUser!!.uid).child("Notes").child(userId).setValue(userData).addOnCompleteListener {
                   Toast.makeText(this,"Note Created Successfully", Toast.LENGTH_SHORT).show()
                   startActivity(Intent(this, MainActivity::class.java))
                   finish()
               }
                   .addOnFailureListener {
                       Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
                   }

               /*   if (edtTittle.text.toString().isNotBlank() || edtDetails.text.toString().isNotBlank()){

                      var tittle = edtTittle.text.toString()
                      var details = edtDetails.text.toString()

                      var id = databaseReference.push().key!!
                      var userModel = user(id,tittle,details)
                      databaseReference.child(id).setValue(userModel)

                          .addOnCompleteListener {
                              edtTittle.text.clear()
                              edtDetails.text.clear()
                              Toast.makeText(this,"Note Created Successfully", Toast.LENGTH_SHORT).show()
                              startActivity(Intent(this, MainActivity::class.java))
                              finish()
                          }

                          .addOnFailureListener {
                              Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
                          }

                  }else{

                  } */


           }

      }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}