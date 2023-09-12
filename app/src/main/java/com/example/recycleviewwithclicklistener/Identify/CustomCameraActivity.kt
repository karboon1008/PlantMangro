package com.example.recycleviewwithclicklistener.Identify

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.ZoomControls
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recycleviewwithclicklistener.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CustomCameraActivity : AppCompatActivity() {

    private var initialFingerSpacing = 0f
    private var zoomLevel = 1f
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_camera)

        val viewFinder = findViewById<PreviewView>(R.id.viewFinder)
        viewFinder.setOnTouchListener { _, event ->
            handleZoomTouchEvent(event)
            return@setOnTouchListener true
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        findViewById<Button>(R.id.camera_capture_button).setOnClickListener {
            takePhoto()
        }

        findViewById<Button>(R.id.snaptips).setOnClickListener{
            // Create a layout inflater to inflate the dialog layout
            val inflater = LayoutInflater.from(this)
            val dialogLayout = inflater.inflate(R.layout.popout, null)

            // Create an alert dialog with the dialog layout
            val builder = AlertDialog.Builder(this)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Got it!") { dialog, which ->
                // Do something when the "OK" button is clicked
                dialog.dismiss()
            }
            builder.show()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }


    private fun handleZoomTouchEvent(event: MotionEvent) {
        val camera = camera ?: return
        val cameraControl = camera.cameraControl
        val maxZoom = camera.cameraInfo.zoomState.value?.maxZoomRatio ?: 1f

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount >= 2){
                    initialFingerSpacing = getFingerSpacing(event)
                    zoomLevel = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if(event.pointerCount >= 2){
                    val newFingerSpacing = getFingerSpacing(event)
                    if (newFingerSpacing > initialFingerSpacing) {
                        if (zoomLevel < maxZoom) {
                            zoomLevel += 0.05f // Increase zoom level
                        }
                    } else if (newFingerSpacing < initialFingerSpacing) {
                        if (zoomLevel > 1f) {
                            zoomLevel -= 0.05f // Decrease zoom level
                        }
                    }

                    val newZoomRatio = zoomLevel.coerceIn(1f, maxZoom)
                    cameraControl.setZoomRatio(newZoomRatio)
                }
            }
        }
    }

    private fun getFingerSpacing(event: MotionEvent): Float {
        if(event.pointerCount <2) return 0f

        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt((x * x + y * y).toDouble()).toFloat()
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PlantMangro")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    val savedUri = output.savedUri ?: return

                    val intent = Intent(this@CustomCameraActivity, result_activity::class.java)
                    intent.putExtra("image", savedUri.toString())
                    startActivity(intent)
                }

            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // Define the desired output image size
        val width = 1080
        val height = 1920

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val viewFinder = findViewById<PreviewView>(R.id.viewFinder)
            val preview = Preview.Builder().build().also{it.setSurfaceProvider(viewFinder.surfaceProvider)}
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(width, height)).build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "PlantMangro"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


}