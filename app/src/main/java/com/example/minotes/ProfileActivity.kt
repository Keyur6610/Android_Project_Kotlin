package com.example.minotes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    lateinit var txtName:TextView
    lateinit var txtEmail:TextView
    lateinit var profile_pic_id:ImageView
    lateinit var backBtn:ImageButton
    lateinit var editImg:Button
    lateinit var deleteBtn:Button
    lateinit var  auth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var firebaseDatabase: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmail)
        profile_pic_id = findViewById(R.id.prfile_pic_id)
        backBtn = findViewById(R.id.backBtn)
        editImg = findViewById(R.id.editImage)
        deleteBtn = findViewById(R.id.deleteBtn)
        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Email")


        var imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){it->
            if (it.resultCode == Activity.RESULT_OK){

                if (it.data != null){

                    var ref = Firebase.storage.reference.child("Profile Image")
                    ref.putFile(it.data!!.data!!).addOnSuccessListener {

                        Toast.makeText(this@ProfileActivity,"Image Upload",Toast.LENGTH_SHORT).show()

                        ref.downloadUrl.addOnSuccessListener {

                            firebaseDatabase.child(auth.currentUser!!.uid).child("User Details").child("Image").setValue(it.toString())

                        }
                            .addOnFailureListener {
                                Toast.makeText(this@ProfileActivity,"Image Not Fetch",Toast.LENGTH_SHORT).show()
                            }

                    }
                        .addOnFailureListener {
                            Toast.makeText(this@ProfileActivity,"Image Not Upload",Toast.LENGTH_SHORT).show()
                        }

                }

            }
        }


        editImg.setOnClickListener {
            var intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }

        deleteBtn.setOnClickListener {
            var alertDialogue = AlertDialog.Builder(this)
            alertDialogue.setTitle("Delete Account")
            alertDialogue.setMessage("Do you want to delete your account permanently?")
            alertDialogue.setIcon(R.drawable.baseline_delete_24)
            alertDialogue.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->

                firebaseDatabase.child(auth.currentUser!!.uid).removeValue().addOnSuccessListener {
                    auth.currentUser!!.delete().addOnCompleteListener {task->
                        if (task.isSuccessful){
                            Toast.makeText(this,"Account Delete",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,LoginActivity::class.java))
                            this.finish()
                        }
                    }
                        .addOnFailureListener {
                            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                        }

                }
                    .addOnFailureListener {
                        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                    }

            })
            alertDialogue.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

            })

            alertDialogue.show()

        }

        firebaseDatabase.child(auth.currentUser!!.uid).child("User Details")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var name = snapshot.child("name").getValue(String::class.java)
                    var email = snapshot.child("email").getValue(String::class.java)
                    var img = snapshot.child("Image").getValue(String::class.java).toString()

                    txtName.text = name
                    txtEmail.text = email

                    Picasso.get().load(img).placeholder(R.drawable.man_1_).into(profile_pic_id)
                   // Toast.makeText(this@ProfileActivity,"Success",Toast.LENGTH_SHORT).show()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity,"Error",Toast.LENGTH_SHORT).show()
                }

            })


      /*  firebaseFirestore.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document != null){
                    var name = document.data!!["name"].toString()
                    var email = document.data!!["email"].toString()
                    var psw = document.data!!["password"].toString()

                    txtName.text = name
                    txtEmail.text = email
                    txtPsw.text = psw

                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
            } */





        backBtn.setOnClickListener {
            onBackPressed()
        }

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            txtName.setTextColor(resources.getColor(R.color.white))
            txtEmail.setTextColor(resources.getColor(R.color.white))
            editImg.setTextColor(resources.getColor(R.color.white))
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}