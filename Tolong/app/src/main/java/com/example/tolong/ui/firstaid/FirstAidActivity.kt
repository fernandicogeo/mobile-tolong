package com.example.tolong.ui.firstaid

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.tolong.R
import com.example.tolong.databinding.ActivityFirstAidBinding
import com.example.tolong.helper.rotateBitmap
import com.example.tolong.repository.ResultCondition
import com.example.tolong.ui.MainActivity
import com.example.tolong.ui.nearby.NearbyActivity
import com.example.tolong.ui.profile.ProfileActivity
import com.example.tolong.ui.setting.SettingActivity
import com.example.tolong.viewmodel.FirstAidViewModel
import com.example.tolong.viewmodel.ViewModelFactoryModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FirstAidActivity : AppCompatActivity() {
    private val firstAidViewModel: FirstAidViewModel by viewModels { factory }
    private lateinit var factory: ViewModelFactoryModel
    private lateinit var binding: ActivityFirstAidBinding
    private lateinit var fusedLocation: FusedLocationProviderClient

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

        factory = ViewModelFactoryModel.getInstanceModel(binding.root.context)

        val myFile = reduceFileImage(intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File)
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )

        binding.ivGambarLuka.setImageBitmap(rotatedBitmap)
        bottomNav()

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        firstAidViewModel.uploadImage(myFile).observe(this) {
            if (it != null) {
                when (it) {
                    is ResultCondition.LoadingState -> {
                        Toast.makeText(
                            this,
                            "Sedang loading..",
                            Toast.LENGTH_SHORT
                        ).show()
                    } is ResultCondition.ErrorState -> {
                    Toast.makeText(
                        this,
                        "Gagal memproses gambar. Coba lagi.",
                        Toast.LENGTH_SHORT
                    ).show()
                    } is ResultCondition.SuccessState -> {
                        result(it.data.prediction)
                    }
                }
            }
        }
    }

    private fun result(result: String) {
        var resultTipe: String = ""
        var resultPenanggulangan: String = ""

        when (result) {
            "Abrasions" -> {
                resultTipe = "Lecet"
                resultPenanggulangan = "1. Bersihkan luka dengan bahan yang tidak berserat atau kasa steril, dan gunakan antiseptik seperti Betadine. Jika terdapat kotoran yang tertanam, Savlon dapat digunakan karena mengandung antiseptik dan surfaktan untuk membantu menghilangkan kotoran. Bilas luka setelah lima menit dengan saline steril atau air keran yang mengalir., 2. Jangan menggosok kotoran yang tertanam, karena ini dapat membuat luka lebih parah lagi., 3. Tutupi luka yang telah dibersihkan dengan pembalut steril antilengket yang sesuai. , 4. Ganti balutan sesuai dengan instruksi pabriknya (beberapa mungkin dibiarkan selama beberapa hari hingga seminggu). Jika Anda mengoleskan kembali antiseptik, bersihkan setelah lima menit dan ganti lukanya, sementara Anda membawa orang tersebut ke perawatan medis."
            }
            "Bruises" -> {
                resultTipe = "Memar"
                resultPenanggulangan = "1. Tinggikan area yang memar di atas ketinggian jantung, jika memungkinkan., 2.Oleskan kompres es yang dibungkus dengan handuk tipis. Biarkan selama 20 menit. Ulangi beberapa kali selama satu atau dua hari setelah cedera. Ini membantu mengurangi pembengkakan dan rasa sakit., 3. Jika area yang memar membengkak, pasang perban elastis di sekelilingnya, tetapi jangan terlalu kencang, saat Anda membawa orang tersebut ke perawatan medis."
            }
            "Burns" -> {
                resultTipe = "Luka bakar"
                resultPenanggulangan = "1. Dinginkan luka bakar dengan air bersih mengalir selama minimal 10 menit, idealnya 20 menit., 2. Akses layanan medis darurat (EMS) jika luka bakar besar, dalam atau dekat dengan wajah, mulut atau tenggorokan atau area genital, atau jika itu adalah hasil dari produk kimia, listrik atau api., 3. Selama tidak menempel di kulit, lepaskan semua pakaian dan perhiasan di atau dekat kulit yang terbakar., 4. Setelah dingin, tutupi dengan penutup balutan yang mempertahankan kelembapan, mengikuti kontur luka dengan mudah dan tidak lengket (misalnya, hidrogel). 5. Jika perlu, tutupi luka bakar dengan kain basah atau bungkus plastik saat Anda membawa orang tersebut ke perawatan medis."
            }
            "Cut" -> {
                resultTipe = "Luka sobek"
                resultPenanggulangan = "1. Jika luka mengeluarkan banyak darah, berikan tekanan pada luka untuk menghentikan pendarahan. Ikuti langkah-langkah untuk pendarahan. Setelah pendarahan berhenti, selesaikan langkah selanjutnya yang tercantum di sini., 2. Bersihkan abrasi atau luka dengan air minum (bersih), sebaiknya suam-suam kuku dan dari keran untuk mengalirkan air bertekanan. Jika tidak tersedia air bersih, gunakan disinfektan untuk membersihkan luka., 3. Gunakan kompres steril untuk menghilangkan kotoran yang tertinggal di luka. Saat menggunakan disinfektan untuk membersihkan luka, ganti kompres secara teratur., 4. Keringkan area sekitar luka dan tutupi luka itu sendiri dengan pembalut seperti plester, hidrogel, film plastik atau hidrokoloid. Jika Anda tidak memiliki akses ke pembalut seperti itu, gunakan plester yang menempel, saat Anda membawa orang tersebut ke perawatan medis."
            }
            "Ingrown Nails" -> {
                resultTipe = "Kuku tumbuh ke dalam"
                resultPenanggulangan = "1. Larutkan satu atau dua sendok makan garam dapur biasa ke dalam wadah es krim berisi air hangat suam-suam kuku. Rendam jari kaki Anda yang sakit selama 5-10 menit dalam air asin yang hangat (bukan panas). Anda dapat mengulangi perendaman beberapa kali sehari jika diinginkan., 2. Selanjutnya, tepuk-tepuk area tersebut dengan lembut hingga kering dan beri beberapa tetes Betadine (atau losion antiseptik lainnya seperti Savlon) ke area yang sakit dan tutupi dengan bandaid atau bahan lainnya. balutan bersih., 3. Jangan mencoba menggali atau memotong sendiri kuku yang tumbuh ke dalam. Ini akan memperburuk iritasi dan meningkatkan risiko infeksi, sementara Anda membawa orang tersebut ke perawatan medis."
            }
            "Laceration" -> {
                resultTipe = "Laserasi"
                resultPenanggulangan = "1. Jika luka mengeluarkan banyak darah, berikan tekanan pada luka untuk menghentikan pendarahan. Ikuti langkah-langkah untuk pendarahan. Setelah pendarahan berhenti, selesaikan langkah selanjutnya yang tercantum di sini., 2. Bersihkan abrasi atau luka dengan air minum (bersih), sebaiknya suam-suam kuku dan dari keran untuk mengalirkan air bertekanan. Jika tidak tersedia air bersih, gunakan disinfektan untuk membersihkan luka., 3. Gunakan kompres steril untuk menghilangkan kotoran yang tertinggal di luka. Saat menggunakan disinfektan untuk membersihkan luka, ganti kompres secara teratur., 4. Keringkan area sekitar luka dan tutupi luka itu sendiri dengan pembalut seperti plester, hidrogel, film plastik atau hidrokoloid. Jika Anda tidak memiliki akses ke pembalut seperti itu, gunakan plester yang menempel, saat Anda membawa orang tersebut ke perawatan medis."
            }
            "Stab_wound" -> {
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