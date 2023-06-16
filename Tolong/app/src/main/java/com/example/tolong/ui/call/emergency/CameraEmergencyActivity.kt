package com.example.tolong.ui.call.emergency

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.tolong.databinding.ActivityCameraEmergencyBinding
import com.example.tolong.helper.createFile
import com.example.tolong.helper.uriToFile

class CameraEmergencyActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityCameraEmergencyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initGallery()

        binding.captureImage.setOnClickListener {
            captureImage()
        }
        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        startCamera()
    }

    private fun initGallery() {
        openGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.i("TEST_GALERY", "Galeri berhasil dipilih")

                val selectedImg: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImg, this@CameraEmergencyActivity)

                val intent = Intent(this@CameraEmergencyActivity, EmergencyActivity::class.java)
                intent.putExtra(EmergencyActivity.EXTRA_PHOTO_RESULT, myFile)
                intent.putExtra(EmergencyActivity.EXTRA_CAMERA_MODE, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)

                this@CameraEmergencyActivity.finish()
                startActivity(intent)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        openGalleryLauncher.launch(chooser)
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraEmergencyActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    val intent = Intent(this@CameraEmergencyActivity, EmergencyActivity::class.java)
                    intent.putExtra(EmergencyActivity.EXTRA_PHOTO_RESULT, photoFile)
                    intent.putExtra(
                        EmergencyActivity.EXTRA_CAMERA_MODE,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    this@CameraEmergencyActivity.finish()
                    startActivity(intent)
                }
            }
        )


    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(480, 720))
                .build()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture, imageAnalysis
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraEmergencyActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }
}