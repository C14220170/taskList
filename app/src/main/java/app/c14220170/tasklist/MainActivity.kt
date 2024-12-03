package app.c14220170.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var DataTaskList = ArrayList<task>()
    lateinit var taskAdapter : adapterRecView
    lateinit var _rvTask : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _rvTask = findViewById<RecyclerView>(R.id.rvTask)
        _rvTask.layoutManager = LinearLayoutManager(this)
        taskAdapter = adapterRecView(DataTaskList)
        _rvTask.adapter = taskAdapter
        ReadData(db)

        val _addBtn = findViewById<Button>(R.id.addBtn)
        _addBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, addTask::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        ReadData(db)
    }

    private fun ReadData(db: FirebaseFirestore){
        db.collection("taskList")
            .get()
            .addOnSuccessListener {
                result ->
                DataTaskList.clear()
                for (document in result){
                    val readData = task(
                        document.data.get("title").toString(),
                        document.data.get("desc").toString(),
                        document.data.get("startDate").toString(),
                        document.data.get("status").toString()
                    )
                    DataTaskList.add(readData)
                    }
                taskAdapter.notifyDataSetChanged()
                }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }
}