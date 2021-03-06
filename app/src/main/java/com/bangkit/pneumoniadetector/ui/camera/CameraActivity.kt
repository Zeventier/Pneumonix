package com.bangkit.pneumoniadetector.ui.camera


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.databinding.ActivityCameraBinding
import com.bangkit.pneumoniadetector.tools.FilePhotoTools
import com.bangkit.pneumoniadetector.ui.preview.PreviewActivity
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var isFlashLightOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // set btn_back and btn_flash icon color to black
        binding.btnBack.apply {
            setIconTintResource(R.color.white)
            setOnClickListener { finish() }
        }
        binding.btnFlash.setIconTintResource(R.color.white)

        binding.btnFromGallery.setOnClickListener { takeFromGallery() }
        binding.btnTakePhoto.setOnClickListener { takePhoto() }

    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    // method for preparing camera
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetResolution(Size(binding.viewFinder.measuredWidth, binding.viewFinder.measuredHeight))
                .build()
                .also{
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(binding.viewFinder.measuredWidth, binding.viewFinder.measuredHeight))
                .build()

            try{
                cameraProvider.unbindAll()
                val cam = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

                binding.btnFlash.setOnClickListener {
                    if( cam.cameraInfo.hasFlashUnit() ){
                        cam.cameraControl.enableTorch(!isFlashLightOn)
                        binding.btnFlash.setIconTintResource(if (isFlashLightOn) R.color.white_2 else R.color.red_1)
                        isFlashLightOn = !isFlashLightOn
                    }
                    else
                        Toast.makeText(
                            this@CameraActivity,
                            "Your phone doesn't have a flashlight feature",
                            Toast.LENGTH_SHORT
                        ).show()

                }
            }
            catch(exc: Exception){
                Toast.makeText(this@CameraActivity, getString(R.string.failed_to_show_camera), Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // method for clicking btn_take_photo that will take a photo
    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val photoFile = FilePhotoTools.createFile(application)

        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity,"Gagal mengambil gambar.", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    goToPreviewActivity(photoFile)
                }
            }
        )

    }

    // A method for going to previewActivity with a file sent to it
    private fun goToPreviewActivity(photoFile: File) {
        val intent = Intent(this@CameraActivity, PreviewActivity::class.java).apply {
            putExtra(PreviewActivity.EXTRA_PICTURE, photoFile)
            putExtra(PreviewActivity.EXTRA_IS_PICTURE, true)
        }
        startActivity(intent)
    }

    // A method for taking picture from gallery
    private fun takeFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){  result ->
        if(result.resultCode == RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            val photoFile = FilePhotoTools.uriToFile(selectedImg, this@CameraActivity)
            goToPreviewActivity(photoFile)
        }
    }
}