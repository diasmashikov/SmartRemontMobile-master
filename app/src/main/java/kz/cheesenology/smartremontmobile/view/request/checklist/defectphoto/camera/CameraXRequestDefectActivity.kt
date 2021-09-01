package kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_request_check_list_photo_fix.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.databinding.ActivityCameraDefectListBinding
import kz.cheesenology.smartremontmobile.databinding.ActivityCameraXRequestDefectBinding
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.view.request.checklist.RequestCheckListPresenter
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Provider

class CameraXRequestDefectActivity : MvpAppCompatActivity(), CameraXRequestDefectView {

    var checkID : Int? = null
    var clientRequestID : Int? = null
    var draftCheckID : Int? = null

    @Inject
    lateinit var presenterProvider: Provider<CameraXRequestDefectPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    private lateinit var binding: ActivityCameraXRequestDefectBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView
    private lateinit var imagePreview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo


    private val executor = Executors.newSingleThreadExecutor()

    private val REQUEST_CODE_PERMISSIONS = 10035
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    companion object {
        private const val PHOTO = 0
        private const val VIDEO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@CameraXRequestDefectActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXRequestDefectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = binding.previewViewRequest
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        previewView.implementationMode = PreviewView.ImplementationMode.PERFORMANCE

        supportActionBar?.hide()

        checkID = intent.getIntExtra("check_id", 0)
        clientRequestID = intent.getIntExtra("client_request_id", 0)
        draftCheckID = intent.getIntExtra("draft_check_id", 0)
        presenter.setCheckID(checkID!!, clientRequestID!!, draftCheckID!!)

        if (allPermissionsGranted()) {
            previewView.post {
                startCamera()
            }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            Toast.makeText(
                this@CameraXRequestDefectActivity,
                "Нет разрешения на использование камеры",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.cameraCaptureButtonRequest.setOnClickListener {
            takePicture()
        }
        binding.cameraTorchButtonRequest.setOnClickListener {
            toggleTorch()
        }
    }

    private fun toggleTorch() {
        if (cameraInfo.torchState.value == TorchState.ON) {
            cameraControl.enableTorch(false)
        } else {
            cameraControl.enableTorch(true)
        }
    }

    private fun takePicture() {
        var mob = Environment.getExternalStorageDirectory().absoluteFile
        mob = File(mob, AppConstant.MEDIA_USER_PHOTO_PATH)
        if (!mob.exists()) {
            mob.mkdirs()
        }

        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(
            AppConstant.FULL_MEDIA_PHOTO_PATH +
                    fileName
        )

        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {
                    val resultImage: Bitmap? = textAsBitmap(
                        image.image?.toBitmap()!!,
                        AppConstant.getCurrentDateFullWithoutSeconds() + " --- ${
                            intent.getStringExtra(
                                "address"
                            )
                        }"
                    )
                    file.writeBitmap(resultImage!!, Bitmap.CompressFormat.JPEG, 100)
                    presenter.addNewPhoto(fileName, file.absolutePath, "photo")
                    vibrate()
                    val msg = "Фотография сохранена: ${file.absolutePath}"
                    previewView.post {
                        Toast.makeText(this@CameraXRequestDefectActivity, msg, Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    val msg = "Ошибка, фото не сохранено: ${exception.message}"
                    previewView.post {
                        Toast.makeText(this@CameraXRequestDefectActivity, msg, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        )
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    fun textAsBitmap(bitmap: Bitmap, text: String?): Bitmap? {
        val paint = Paint()
        val scaledSize = resources.getDimensionPixelSize(R.dimen.watermark_text_size)
        val scale = resources.displayMetrics.density
        paint.color = Color.WHITE
        val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val bounds = Rect()
        paint.getTextBounds(text, 0, text!!.length, bounds)
        paint.textSize = scaledSize.toFloat()
        val x: Int = canvas.width - bounds.width()
        val y: Int = canvas.height - bounds.height() / 2
        canvas.drawText(text, 0f, y.toFloat(), paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.BLACK
        canvas.drawText(text, 0f, y.toFloat(), paint)
        return mutableBitmap
    }

    fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            //deprecated in API 26
            v.vibrate(100)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                previewView.post { startCamera() }
            } else {
                "Разрешение на чтение файлов и использование камеры НЕ ВЫДАНЫ".toast()
                finish()
            }
        }
    }

    fun String.toast() {
        Toast.makeText(this@CameraXRequestDefectActivity, this, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        imagePreview = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(previewView.display.rotation)
        }.build()
        imagePreview.setSurfaceProvider(previewView.surfaceProvider)

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            setFlashMode(ImageCapture.FLASH_MODE_AUTO)
        }.build()

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera =
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    imagePreview,
                    imageCapture
                )
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
            setTorchStateObserver()
            setZoomStateObserver()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setTorchStateObserver() {
        cameraInfo.torchState.observe(this, Observer { state ->
            if (state == TorchState.ON) {
                binding.cameraTorchButtonRequest.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_flash_on_24dp
                    )
                )
            } else {
                binding.cameraTorchButtonRequest.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_flash_off_24dp
                    )
                )
            }
        })
    }

    private fun setZoomStateObserver() {
        cameraInfo.zoomState.observe(this, Observer { state ->
            // state.linearZoom
            // state.zoomRatio
            // state.maxZoomRatio
            // state.minZoomRatio
            Log.d("ZOOM: ", "${state.linearZoom}")
        })
    }
}