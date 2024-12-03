package app.c14220170.tasklist

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class adapterRecView(private val listTask: ArrayList<task>) : RecyclerView.Adapter<adapterRecView.ListViewHolder>() {

    val db = Firebase.firestore

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _title = itemView.findViewById<TextView>(R.id.taskName)
        var _desc = itemView.findViewById<TextView>(R.id.taskDesc)
        var _startDate = itemView.findViewById<TextView>(R.id.taskStartDate)
        var _btnHapus = itemView.findViewById<TextView>(R.id.btnHapus)
        var _btnEdit = itemView.findViewById<TextView>(R.id.btnEdit)
        var _btnStartEnd = itemView.findViewById<TextView>(R.id.btnStartEnd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycle, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTask.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val task = listTask[position]
        holder._title.setText(task.title)
        holder._desc.setText(task.desc)
        holder._startDate.setText(task.startDate)

        when (task.status) {
            "Start" -> {
                holder._btnStartEnd.text = "Start"
                holder._btnStartEnd.setBackgroundColor(Color.parseColor("#0096FF"))
            }

            "Ongoing" -> {
                holder._btnStartEnd.text = "Ongoing"
                holder._btnStartEnd.setBackgroundColor(Color.parseColor("#FFC000"))
            }

            else -> {
                holder._btnStartEnd.text = "Completed"
                holder._btnStartEnd.setBackgroundColor(Color.parseColor("#50C878"))
            }
        }

        holder._btnStartEnd.setOnClickListener {
            if (task.status == "Completed") {
                return@setOnClickListener
            }

            val newStatus = when (task.status) {
                "Start" -> "Ongoing"
                else -> "Completed"
            }

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirm Status")
                .setMessage("Are you sure you want to change the status to $newStatus?")
                .setPositiveButton("Yes") { dialog, _ ->
                    db.collection("taskList")
                        .whereEqualTo("title",task.title)
                        .whereEqualTo("description", task.desc)
                        .whereEqualTo("startDate", task.startDate)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty){
                                val documentId = documents.documents[0].id
                                db.collection("taskList")
                                    .document(documentId)
                                    .update("status", newStatus)
                                    .addOnSuccessListener {
                                        task.status = newStatus
                                        notifyItemChanged(position)
                                        Log.d("Firebase", "Status updated to $newStatus")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d("Firebase", "Error updating document: ${e.message}")
                                    }
                            }
                            else{
                                Log.d("Firebase", "Document not found")
                            }
                        }
                        .addOnFailureListener{e ->
                            Log.d("Firebase", "Error getting documents: ${e.message}")
                        }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
        holder._btnEdit.setOnClickListener{
            val intent = Intent(holder.itemView.context, addTask::class.java).apply {
                putExtra("task", task)
            }
            holder.itemView.context.startActivity(intent)
        }
        holder._btnHapus.setOnClickListener{
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes") { dialog, _ ->
                    db.collection("taskList")
                        .whereEqualTo("title",task.title)
                        .whereEqualTo("description", task.desc)
                        .whereEqualTo("startDate", task.startDate)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val documentId = documents.documents[0].id
                                db.collection("taskList")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener {
                                        listTask.removeAt(position)
                                        notifyItemRemoved(position)
                                        notifyItemRangeChanged(position, listTask.size)
                                        Log.d("Firebase", "Task deleted successfully")
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Task Successfully Deleted",
                                            Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener{ e ->
                                        Log.d("Firebase", "Error deleting document: ${e.message}")
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Error Deleting Task",
                                            Toast.LENGTH_LONG).show()
                                    }
                            }
                            else{
                                Log.d("Firebase", "Document not found")
                            }
                        }
                        .addOnFailureListener{ e ->
                            Log.d("Firebase", "Error getting documents: ${e.message}")
                        }
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
}