package app.c14220170.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class addTask : AppCompatActivity() {

    val db = Firebase.firestore
    private var editPage = false
    private var oriTaskName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _btnBack = findViewById<Button>(R.id.btnBack)
        _btnBack.setOnClickListener {
            val intent = Intent(this@addTask, MainActivity::class.java)
            startActivity(intent)
        }

        val _title = findViewById<EditText>(R.id.editTextTitle)
        val _desc = findViewById<EditText>(R.id.editTextDesc)
        val _startDate = findViewById<EditText>(R.id.editTextDate)
        val _btnSave = findViewById<Button>(R.id.btnSave)

        _btnSave.setOnClickListener {
            Log.d("ButtonClick", "Add Task button clicked")
            if (editPage){
                UpdateData(db,oriTaskName.toString(), _title.text.toString(), _desc.text.toString(), _startDate.text.toString())
            }
            AddData(db, _title.text.toString(), _desc.text.toString(), _startDate.text.toString())
        }

        val intent = intent.getParcelableExtra("task", task::class.java)
        if (intent != null){
            editPage = true
            _btnSave.text = "Update"
            oriTaskName = intent.title

            _title.setText(intent.title)
            _desc.setText(intent.desc)
            _startDate.setText(intent.startDate)
        }



    }

    private fun AddData(db: FirebaseFirestore, taskName: String, taskDesc: String, taskStartDate: String){
        val newData = task(taskName, taskDesc, taskStartDate, "Start")

        db.collection("taskList")
            .document(newData.title)
            .set(newData)
            .addOnSuccessListener {
                Log.d("Firebase", "Data added successfully")
                val intent = Intent(this@addTask, MainActivity::class.java)
                startActivity(intent)
                if (editPage){
                    Toast.makeText(this@addTask, "Task Successfully Updated", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@addTask, "Task Successfully Added", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
                Toast.makeText(this@addTask, "Error Adding Task", Toast.LENGTH_LONG).show()
            }

    }

    private fun UpdateData(db: FirebaseFirestore, originalTaskName: String, taskName: String, taskDesc: String, taskStartDate: String){
        db.collection("taskList")
            .document(originalTaskName)
            .delete()
            .addOnSuccessListener {
                AddData(db, taskName, taskDesc, taskStartDate)
            }
            .addOnFailureListener {
                Toast.makeText(this@addTask, "Error Updating Task", Toast.LENGTH_LONG).show()
            }
    }
}