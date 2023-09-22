package com.example.minotes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    lateinit var loginCardView: CardView
    lateinit var edtEmail:EditText
    lateinit var edtPsw:EditText
    lateinit var txtSignup:TextView
    lateinit var loginBtn:Button
    lateinit var firebaseDatabase: FirebaseDatabase
   private lateinit var auth:FirebaseAuth
   private lateinit var googleSignInClient:GoogleSignInClient


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginCardView = findViewById(R.id.loginCardView)
        edtEmail = findViewById(R.id.edtEmail)
        edtPsw = findViewById(R.id.edtPsw)
        txtSignup = findViewById(R.id.txtSignup)
        loginBtn = findViewById(R.id.loginBtn)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        txtSignup.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }

        loginBtn.setOnClickListener {

            var email = edtEmail.text.toString()
            var psw = edtPsw.text.toString()

            if (email.isNotBlank() && psw.isNotBlank()){

                auth.signInWithEmailAndPassword(email,psw).addOnCompleteListener {task->
                    if (task.isSuccessful){
                        Toast.makeText(this,"Login Successfully",Toast.LENGTH_SHORT).show()
                        var i = Intent(this,MainActivity::class.java)
                        startActivity(i)
                        this.finish()
                    }else{
                        Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }

                }

            }else{
                Toast.makeText(this,"Enter Details",Toast.LENGTH_SHORT).show()
            }

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        loginCardView.setOnClickListener {
            signInGoogle()
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            this.finish()
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){

                var gsoDetails = gsoDetails(account.displayName,account.email)
                firebaseDatabase.getReference("Email").child(auth.currentUser!!.uid).child("User Details").setValue(gsoDetails)

                val intent : Intent = Intent(this , MainActivity::class.java)
                intent.putExtra("email" , account.email)
                intent.putExtra("name" , account.displayName)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }


    }

    data class gsoDetails(
        var name:String? = null,
        var email:String? = null,
        var psw:String? = null
    )
}