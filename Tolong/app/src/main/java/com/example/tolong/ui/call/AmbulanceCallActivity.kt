package com.example.tolong.ui.call

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tolong.R
import com.example.tolong.databinding.ActivityAmbulanceCallBinding
import com.example.tolong.databinding.ActivityMainBinding
import com.example.tolong.ui.MainActivity

class AmbulanceCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAmbulanceCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmbulanceCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnYa.setOnClickListener {
            val phoneNumber = "118"
            val dialNumberIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(dialNumberIntent)
        }

        binding.btnNo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}