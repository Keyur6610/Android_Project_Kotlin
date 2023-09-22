package com.example.minotes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.core.view.MenuCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firestore.v1.DocumentChange
import com.google.rpc.context.AttributeContext.Resource
import com.squareup.picasso.Picasso
import java.util.Collections

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var txtLoading:ProgressBar
    lateinit var adapter: Rv_Asapter
    lateinit var userList: ArrayList<user>
    lateinit var addImg:ImageView
    lateinit var searchView: SearchView
    lateinit var myToolbar: androidx.appcompat.widget.Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView:NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var databaseReference: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var googleSignInClient : GoogleSignInClient



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView =findViewById(R.id.recyclerView)
        txtLoading = findViewById(R.id.txtLoading)
        addImg = findViewById(R.id.addImg)
        searchView = findViewById(R.id.searchView)
        userList = ArrayList<user>()
        myToolbar = findViewById(R.id.myToolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        var count = 1

        var nav = navigationView.getHeaderView(0)
        var navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header)


      var txtName = navHeaderView.findViewById<TextView>(R.id.txtName)
      var txtEmail = navHeaderView.findViewById<TextView>(R.id.txtEmail)
       var profileImg = navHeaderView.findViewById<ImageView>(R.id.profileImg)

        databaseReference = FirebaseDatabase.getInstance().getReference("Email")
        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()


        setSupportActionBar(myToolbar)
       // myToolbar.inflateMenu(R.menu.option_menu)

        databaseReference.child(auth.currentUser!!.uid).child("User Details").addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var name = snapshot.child("name").getValue(String::class.java)
                var email = snapshot.child("email").getValue(String::class.java)
                var img = snapshot.child("Image").getValue(String::class.java)

               txtName.text = name
                txtEmail.text = email

                Picasso.get().load(img).placeholder(R.drawable.man_1_).into(profileImg)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
            }

        })

       toggle = ActionBarDrawerToggle(this,drawerLayout,myToolbar,R.string.Open,R.string.Close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = GridLayoutManager(this,2)
        recyclerView.setHasFixedSize(true)


        adapter = Rv_Asapter(userList,this@MainActivity)


        txtLoading.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

     /*   firebaseFirestore.collection(auth.currentUser!!.uid.toString()).addSnapshotListener(object : EventListener<QuerySnapshot>{

            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null)
                {
                    Log.e("Firestore error", error.message.toString())
                    return
                }

                for (dc in value!!.documents){
                        userList.add(dc.toObject<user>(user::class.java)!!)

                }

                recyclerView.adapter = adapter
               adapter.notifyDataSetChanged()
                txtLoading.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

        }) */

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (snapshot.exists()){

                    for (userData in snapshot.child(auth.currentUser!!.uid).child("Notes").children){

                        var data = userData.getValue(user::class.java)
                        userList.add(data!!)

                    }

                    adapter = Rv_Asapter(userList,this@MainActivity)
                    recyclerView.adapter = adapter

                    txtLoading.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
            }

        })


        addImg.setOnClickListener(View.OnClickListener {

            var i = Intent(this,UserActivity::class.java)
            startActivity(i)

        })

        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item: MenuItem ->

            when(item.itemId){
                R.id.account -> startActivity(Intent(this,ProfileActivity::class.java))
                R.id.signout -> {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    googleSignInClient = GoogleSignIn.getClient(this , gso)
                    googleSignInClient.signOut()

                    auth.signOut()
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
                R.id.Settings->{

                     startActivity(Intent(this,SettingsActivity::class.java))

                   /* if (count%2 == 0){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }*/

                }

            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        })



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchFun(p0)
                return true
            }
        })

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
           txtName.setTextColor(resources.getColor(R.color.white))
           txtEmail.setTextColor(resources.getColor(R.color.white))
        }else{
            txtName.setTextColor(resources.getColor(R.color.black))
            txtEmail.setTextColor(resources.getColor(R.color.black))
        }

    }

    private fun searchFun(p0: String?) {

        var searchList = ArrayList<user>()

        for (i in userList){

            if (p0 != null) {
                if (i.Tittle?.toLowerCase()?.contains(p0.toLowerCase())  == true || i.Details?.toLowerCase()
                    ?.contains(p0.toLowerCase()) == true){
                    searchList.add(i)

                }
            }

        }
        adapter.searchListFun(searchList)

    }

    /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout ->{


            }
            R.id.userAccount -> {
                startActivity(Intent(this,ProfileActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            moveTaskToBack(true)
        }


    }


}