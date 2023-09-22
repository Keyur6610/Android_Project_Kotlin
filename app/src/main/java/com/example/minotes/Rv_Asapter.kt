package com.example.minotes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class Rv_Asapter(var userList: ArrayList<user>,var context: Context):
    RecyclerView.Adapter<Rv_Asapter.MyViewHolder>() {

    lateinit var databaseReference: DatabaseReference
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    inner class MyViewHolder(item:View):RecyclerView.ViewHolder(item){
        var userTittle = item.findViewById<TextView>(R.id.userTittle)
        var userDetails = item.findViewById<TextView>(R.id.userDetails)
        var rv_cardView = item.findViewById<CardView>(R.id.rv_cardView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.rv_raw,parent,false)
        databaseReference = FirebaseDatabase.getInstance().getReference("Email")
        firebaseFirestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()


        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentPosition = userList[position]
        holder.userTittle.text = currentPosition.Tittle
        holder.userDetails.text = currentPosition.Details

        holder.rv_cardView.setOnClickListener(View.OnClickListener {
            var i = Intent(context,UpdateDeleteActivity::class.java)
            i.putExtra("tittle",currentPosition.Tittle.toString())
            i.putExtra("details",currentPosition.Details.toString())
            i.putExtra("id",currentPosition.id.toString())
            context.startActivity(i)

        })

        holder.rv_cardView.setOnLongClickListener(View.OnLongClickListener {

            var alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Delete")
            alertDialog.setMessage("Do you want to delete?")
            alertDialog.setIcon(R.drawable.baseline_delete_24)

            alertDialog.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->

                databaseReference.child(auth.currentUser!!.uid).child("Notes").child(currentPosition.id.toString()).removeValue().addOnSuccessListener {
                    Toast.makeText(context,"Delete Successfully",Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                    }
              //  databaseReference.child(currentPosition.id.toString()).removeValue()
              /*  firebaseFirestore.collection(auth.currentUser!!.uid.toString()).document().delete()
                    .addOnSuccessListener {
                    Toast.makeText(context,"Delete Successfully",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                } */

            })

            alertDialog.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

            })

            alertDialog.show()

            true
        })

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            holder.userDetails.setTextColor(R.color.white)
        }

    }

    fun searchListFun(searchList: java.util.ArrayList<user>) {
        userList = searchList
        notifyDataSetChanged()
    }
}