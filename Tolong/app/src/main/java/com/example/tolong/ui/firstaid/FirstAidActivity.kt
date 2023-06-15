package com.example.tolong.ui.firstaid

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.tolong.R
import com.example.tolong.databinding.ActivityFirstAidBinding
import com.example.tolong.helper.rotateBitmap
import com.example.tolong.ui.MainActivity
import com.example.tolong.ui.nearby.NearbyActivity
import com.example.tolong.ui.profile.ProfileActivity
import com.example.tolong.ui.setting.SettingActivity
import com.example.tolong.viewmodel.ViewModelFactoryAuth
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Float.max
import java.nio.ByteBuffer

class FirstAidActivity : AppCompatActivity() {
    private lateinit var factory: ViewModelFactoryAuth
    private lateinit var binding: ActivityFirstAidBinding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var interpreter: Interpreter

    private var lat: Double? = 0.0
    private var lon: Double? = 0.0

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstAidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myFile = reduceFileImage(intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File)
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )

        binding.ivGambarLuka.setImageBitmap(rotatedBitmap)
        bottomNav()

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        // Memuat model TFLite dari file di direktori "assets"
        val modelFile = FileUtil.loadMappedFile(this, "model_quant.tflite")
        val options = Interpreter.Options()
        interpreter = Interpreter(modelFile, options)

        // Mendapatkan dimensi input model TFLite
        val inputShape = interpreter.getInputTensor(0).shape()
        val inputDataType = interpreter.getInputTensor(0).dataType()
        val inputBuffer = TensorBuffer.createFixedSize(inputShape, inputDataType)
        val tensorImage = TensorImage(inputDataType)

        // Mengkonversi gambar menjadi Bitmap
        val bitmap = BitmapFactory.decodeFile(myFile.path)

        // Mendapatkan gambar sebagai TensorImage
        tensorImage.load(bitmap)
        tensorImage.buffer.rewind()

        // Melakukan pra-pemrosesan gambar
        val resizeOp = ResizeOp(inputShape[1].toInt(), inputShape[2].toInt(), ResizeOp.ResizeMethod.BILINEAR)
        val resizedImage = resizeOp.apply(tensorImage)
        resizedImage.buffer.rewind()
        inputBuffer.loadBuffer(resizedImage.buffer)

        // Menjalankan inferensi pada model TFLite
        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputImageBuffer = TensorBuffer.createFixedSize(outputShape, interpreter.getOutputTensor(0).dataType())
        interpreter.run(inputBuffer.buffer, outputImageBuffer.buffer)

        // Mendapatkan output hasil inferensi sebagai array float
        val outputText = outputImageBuffer.floatArray.joinToString(", ")

        val outputArray = outputText.split(", ")
        var resultTipe: String = ""

        when (max(outputArray[0].toFloat(), max(outputArray[1].toFloat(), max(outputArray[2].toFloat(), max(outputArray[3].toFloat(), max(outputArray[4].toFloat(), max(outputArray[5].toFloat(), outputArray[6].toFloat()))))))) {
            outputArray[0].toFloat() -> resultTipe = "Lecet"
            outputArray[1].toFloat() -> resultTipe = "Memar"
            outputArray[2].toFloat() -> resultTipe = "Luka bakar"
            outputArray[3].toFloat() -> resultTipe = "Luka sobek"
            outputArray[4].toFloat() -> resultTipe = "Kuku tumbuh ke dalam"
            outputArray[5].toFloat() -> resultTipe = "Laserasi"
            outputArray[6].toFloat() -> resultTipe = "Luka tusuk"
        }

        binding.tvResultTipeLuka.text = resultTipe
        binding.tvResultPenanggulangan.text = outputText
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
    fun bottomNav() {
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.first_aid -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                }
                R.id.nearby -> {
                    getMyLocation()
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                }
            }
        }
        binding.bottomNavigationView.setSelectedItemId(R.id.first_aid)
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation.lastLocation.addOnSuccessListener {
                if (it != null) {
                    lat = it.latitude
                    lon = it.longitude
                    val intentNearby = Intent(this, NearbyActivity::class.java)

                    intentNearby.putExtra(NearbyActivity.EXTRA_LAT, lat)
                    intentNearby.putExtra(NearbyActivity.EXTRA_LON, lon)

                    Log.d("MainActivity", "Lat: $lat dan Lon: $lon")
                    startActivity(intentNearby)

                } else {
                    showDialog("2")
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showDialog(isSuccess: String) {
        if (isSuccess == "2") {
            AlertDialog.Builder(this).apply {
                setTitle("Lokasi tidak ditemukan!!")
                setMessage("Silakan coba lagi.")
                create()
                show()
            }
        } else if (isSuccess == "3") {
            AlertDialog.Builder(this).apply {
                setTitle("Upload foto gagal!!")
                setMessage("Silakan coba lagi.")
                create()
                show()
            }
        } else if (isSuccess == "4") {
            AlertDialog.Builder(this).apply {
                setTitle("Permission tidak diberikan!!")
                setMessage("Anda harus memberikan permission untuk menggunakan fitur ini.")
                create()
                show()
            }
        }
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}