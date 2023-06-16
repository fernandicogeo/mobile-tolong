package com.example.tolong.ui.call

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tolong.databinding.ActivityPoliceCallBinding
import com.example.tolong.ui.MainActivity

class PoliceCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPoliceCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoliceCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnYa.setOnClickListener {
            val phoneNumber = "110"
            val dialNumberIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(dialNumberIntent)
        }

        binding.btnNo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}