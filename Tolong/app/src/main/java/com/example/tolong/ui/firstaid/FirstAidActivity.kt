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
        var resultPenanggulangan: String = ""

        when (max(outputArray[0].toFloat(), max(outputArray[1].toFloat(), max(outputArray[2].toFloat(), max(outputArray[3].toFloat(), max(outputArray[4].toFloat(), max(outputArray[5].toFloat(), outputArray[6].toFloat()))))))) {
            outputArray[0].toFloat() -> {
                resultTipe = "Lecet"
                resultPenanggulangan = "1. Bersihkan luka dengan bahan yang tidak berserat atau kasa steril, dan gunakan antiseptik seperti Betadine. Jika terdapat kotoran yang tertanam, Savlon dapat digunakan karena mengandung antiseptik dan surfaktan untuk membantu menghilangkan kotoran. Bilas luka setelah lima menit dengan saline steril atau air keran yang mengalir., 2. Jangan menggosok kotoran yang tertanam, karena ini dapat membuat luka lebih parah lagi., 3. Tutupi luka yang telah dibersihkan dengan pembalut steril antilengket yang sesuai. , 4. Ganti balutan sesuai dengan instruksi pabriknya (beberapa mungkin dibiarkan selama beberapa hari hingga seminggu). Jika Anda mengoleskan kembali antiseptik, bersihkan setelah lima menit dan ganti lukanya, sementara Anda membawa orang tersebut ke perawatan medis."
            }
            outputArray[1].toFloat() -> {
                resultTipe = "Memar"
                resultPenanggulangan = "1. Tinggikan area yang memar di atas ketinggian jantung, jika memungkinkan., 2.Oleskan kompres es yang dibungkus dengan handuk tipis. Biarkan selama 20 menit. Ulangi beberapa kali selama satu atau dua hari setelah cedera. Ini membantu mengurangi pembengkakan dan rasa sakit., 3. Jika area yang memar membengkak, pasang perban elastis di sekelilingnya, tetapi jangan terlalu kencang, saat Anda membawa orang tersebut ke perawatan medis."
            }
            outputArray[2].toFloat() -> {
                resultTipe = "Luka bakar"
                resultPenanggulangan = "1. Dinginkan luka bakar dengan air bersih mengalir selama minimal 10 menit, idealnya 20 menit., 2. Akses layanan medis darurat (EMS) jika luka bakar besar, dalam atau dekat dengan wajah, mulut atau tenggorokan atau area genital, atau jika itu adalah hasil dari produk kimia, listrik atau api., 3. Selama tidak menempel di kulit, lepaskan semua pakaian dan perhiasan di atau dekat kulit yang terbakar., 4. Setelah dingin, tutupi dengan penutup balutan yang mempertahankan kelembapan, mengikuti kontur luka dengan mudah dan tidak lengket (misalnya, hidrogel). 5. Jika perlu, tutupi luka bakar dengan kain basah atau bungkus plastik saat Anda membawa orang tersebut ke perawatan medis."
            }
            outputArray[3].toFloat() -> {
                resultTipe = "Luka sobek"
                resultPenanggulangan = "1. Jika luka mengeluarkan banyak darah, berikan tekanan pada luka untuk menghentikan pendarahan. Ikuti langkah-langkah untuk pendarahan. Setelah pendarahan berhenti, selesaikan langkah selanjutnya yang tercantum di sini., 2. Bersihkan abrasi atau luka dengan air minum (bersih), sebaiknya suam-suam kuku dan dari keran untuk mengalirkan air bertekanan. Jika tidak tersedia air bersih, gunakan disinfektan untuk membersihkan luka., 3. Gunakan kompres steril untuk menghilangkan kotoran yang tertinggal di luka. Saat menggunakan disinfektan untuk membersihkan luka, ganti kompres secara teratur., 4. Keringkan area sekitar luka dan tutupi luka itu sendiri dengan pembalut seperti plester, hidrogel, film plastik atau hidrokoloid. Jika Anda tidak memiliki akses ke pembalut seperti itu, gunakan plester yang menempel, saat Anda membawa orang tersebut ke perawatan medis."
            }
            outputArray[4].toFloat() -> {
                resultTipe = "Kuku tumbuh ke dalam"
                resultPenanggulangan = "1. Larutkan satu atau dua sendok makan garam dapur biasa ke dalam wadah es krim berisi air hangat suam-suam kuku. Rendam jari kaki Anda yang sakit selama 5-10 menit dalam air asin yang hangat (bukan panas). Anda dapat mengulangi perendaman beberapa kali sehari jika diinginkan., 2. Selanjutnya, tepuk-tepuk area tersebut dengan lembut hingga kering dan beri beberapa tetes Betadine (atau losion antiseptik lainnya seperti Savlon) ke area yang sakit dan tutupi dengan bandaid atau bahan lainnya. balutan bersih., 3. Jangan mencoba menggali atau memotong sendiri kuku yang tumbuh ke dalam. Ini akan memperburuk iritasi dan meningkatkan risiko infeksi, sementara Anda membawa orang tersebut ke perawatan medis."
            }
            outputArray[5].toFloat() -> {
                resultTipe = "Laserasi"
                resultPenanggulangan = "1. Jika luka mengeluarkan banyak darah, berikan tekanan pada luka untuk menghentikan pendarahan. Ikuti langkah-langkah untuk pendarahan. Setelah pendarahan berhenti, selesaikan langkah selanjutnya yang tercantum di sini., 2. Bersihkan abrasi atau luka dengan air minum (bersih), sebaiknya suam-suam kuku dan dari keran untuk mengalirkan air bertekanan. Jika tidak tersedia air bersih, gunakan disinfektan untuk membersihkan luka., 3. Gunakan kompres steril untuk menghilangkan kotoran yang tertinggal di luka. Saat menggunakan disinfektan untuk membersihkan luka, ganti kompres secara teratur., 4. Keringkan area sekitar luka dan tutupi luka itu sendiri dengan pembalut seperti plester, hidrogel, film plastik atau hidrokoloid. Jika Anda tidak memiliki akses ke pembalut seperti itu, gunakan plester yang menempel, saat Anda membawa orang tersebut ke perawatan medis."
            }
            outputArray[6].toFloat() -> {
                resultTipe = "Luka tusuk"
                resultPenanggulangan = "1. Hentikan pendarahan. Berikan tekanan lembut dengan perban atau kain bersih., 2. Bersihkan luka. Bilas luka dengan air jernih selama 5 sampai 10 menit. Jika ada kotoran atau kotoran yang tertinggal di luka, gunakan waslap untuk menggosoknya dengan lembut. Temui dokter jika Anda tidak dapat menghilangkan semua kotoran atau kotoran., 3. Oleskan antibiotik. Oleskan selapis tipis krim atau salep antibiotik (Neosporin, Polysporin). Selama dua hari pertama, cuci kembali area tersebut dan aplikasikan kembali antibiotik saat Anda mengganti balutan., 4. Tutupi luka. Perban membantu menjaga kebersihan luka., 5. Jika perlu, tutupi luka bakar dengan kain basah atau bungkus plastik saat Anda membawa orang tersebut ke perawatan medis."
            }
        }

        binding.tvResultTipeLuka.text = resultTipe
        binding.tvResultPenanggulangan.text = resultPenanggulangan
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