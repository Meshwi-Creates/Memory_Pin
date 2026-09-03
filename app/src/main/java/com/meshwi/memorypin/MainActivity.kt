package com.meshwi.memorypin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnCreateMemory = findViewById<Button>(R.id.btnCreateMemory)

        btnCreateMemory.setOnClickListener {
            val intent = Intent(this, CreateMemoryActivity::class.java)
            startActivity(intent)
        }
    }
}