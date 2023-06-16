package com.example.tolong.ui.call.emergency

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tolong.databinding.ActivityEmergencyBinding
import com.example.tolong.helper.rotateBitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class EmergencyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmergencyBinding
    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myFile = reduceFileImage(intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File)
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )

        binding.ivGambarKejadian.setImageBitmap(rotatedBitmap)

        val modelFile = FileUtil.loadMappedFile(this, "model.tflite")
        val options = Interpreter.Options()
        interpreter = Interpreter(modelFile, options)

        val inputShape = interpreter.getInputTensor(0).shape()
        val inputDataType = interpreter.getInputTensor(0).dataType()
        val inputBuffer = TensorBuffer.createFixedSize(inputShape, inputDataType)
        val tensorImage = TensorImage(inputDataType)

        val bitmap = BitmapFactory.decodeFile(myFile.path)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        tensorImage.load(resizedBitmap)
        tensorImage.buffer.rewind()

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputShape[1].toInt(), inputShape[2].toInt(), ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
        val normalizedImage = imageProcessor.process(tensorImage)
        inputBuffer.loadBuffer(normalizedImage.buffer)

        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputImageBuffer = TensorBuffer.createFixedSize(outputShape, interpreter.getOutputTensor(0).dataType())
        interpreter.run(inputBuffer.buffer, outputImageBuffer.buffer)

        val outputText = outputImageBuffer.floatArray.joinToString(", ")

        val outputArray = outputText.split(", ")
        val outputFloat = outputArray.map { it.toFloat() }
        val outputMax = outputFloat.maxOrNull()
        val outoutMaxIndex = outputFloat.indexOf(outputMax)
        val array = listOf("Kecelakaan", "Lalu lintas padat", "Kebakaran", "Lalu lintas sepi")
        val result = array[outoutMaxIndex]

        binding.tvResultDeteksi.text = result

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

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}