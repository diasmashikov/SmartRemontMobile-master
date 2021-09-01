package kz.cheesenology.smartremontmobile.view.camerax


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.*
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.google.common.util.concurrent.ListenableFuture
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.databinding.ActivityCameraPhotoReportBinding
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.ImageFilesRotatorAndResizer
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject


@SuppressLint("RestrictedApi")
class CameraPhotoReportActivity : MvpAppCompatActivity(), CameraPhotoReportView {
    @Inject
    lateinit var presenter: CameraPhotoReportPresenter

    @InjectPresenter
    lateinit var moxyPresenter: CameraPhotoReportPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    private lateinit var binding: ActivityCameraPhotoReportBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView
    private lateinit var imagePreview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var videoCapture: VideoCapture
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo

    private val executor = Executors.newSingleThreadExecutor()
    private var imageRotated: Bitmap? = null
    private var fileRotated: File? = null

    private val REQUEST_CODE_PERMISSIONS = 999
    private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    )

    companion object {
        private const val PHOTO = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@CameraPhotoReportActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityCameraPhotoReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        previewView = binding.previewView
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        previewView.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        presenter.setIntentData(
                intent!!.getIntExtra("remont_id", 0),
                intent!!.getIntExtra("stage_id", 0)
        )

        if (allPermissionsGranted()) {
            previewView.post {
                startCamera()
            }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.cameraCaptureButton.setOnClickListener {
            takePicture()
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    PHOTO -> {
                        binding.cameraCaptureButton.setOnClickListener {
                            takePicture()
                        }
                    }
                }
            }

        })
        binding.cameraTorchButton.setOnClickListener {
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


                        var imageBitmapa = image.image?.toBitmap()!!
                        var text_to_write = AppConstant.getCurrentDateFullWithoutSeconds() + " --- ${
                            intent.getStringExtra(
                                    "address"
                            )
                        }"



                        if (Build.VERSION.SDK_INT > 23) {
                            var rotatedImage = ImageFilesRotatorAndResizer.rotateImage(imageBitmapa, 90)
                            var bitmap_resized = rotatedImage?.let { ImageFilesRotatorAndResizer.newresizeBitmap(it, 1100, 1100) }
                            //rotatedImage?.let { ImageFilesRotatorAndResizer.getResizedBitmap(it, 1400, 1400) }


                            /*
                            imageRotated = textAsBitmap(
                                    bitmap_resized!!,
                                    AppConstant.getCurrentDateFullWithoutSeconds() + " --- ${
                                        intent.getStringExtra(
                                                "address"
                                        )
                                    }"
                            )

                             */

                            imageRotated = waterMark(bitmap_resized!!, text_to_write)



                            file!!.writeBitmap(imageRotated!!, Bitmap.CompressFormat.JPEG, 100)
                            //imageRotated = bitmap_resized?.let { ImageFilesRotatorAndResizer.addStampToImage(it) }


                            fileRotated = ImageFilesRotatorAndResizer.bitmapToFile(applicationContext, imageRotated, fileName)
                        } else {
                            var bitmap_resized = imageBitmapa?.let { ImageFilesRotatorAndResizer.newresizeBitmap(it, 1100, 1100) }
                            imageRotated = waterMark(bitmap_resized!!, text_to_write)
                            file!!.writeBitmap(imageRotated!!, Bitmap.CompressFormat.JPEG, 100)
                            fileRotated = ImageFilesRotatorAndResizer.bitmapToFile(applicationContext, imageRotated, fileName)
                        }


                        if (fileRotated != null) {
                            presenter.addNewPhotoReportPhoto(fileName, fileRotated!!.absolutePath, "photo", intent!!.getIntExtra("chat_message_id_key", 0))
                        }
                        vibrate()
                        val msg = "Фотография сохранена: ${file.absolutePath}"
                        image.close()
                        previewView.post {
                            Toast.makeText(this@CameraPhotoReportActivity, msg, Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        val msg = "Ошибка, фото не сохранено: ${exception.message}"
                        previewView.post {
                            Toast.makeText(this@CameraPhotoReportActivity, msg, Toast.LENGTH_LONG).show()
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

    fun waterMark(src: Bitmap, watermark: String?): Bitmap? {
        //get source image width and height
        val w = src.width
        val h = src.height
        val result = Bitmap.createBitmap(w, h, src.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0.toFloat(), 0.toFloat(), null)
        val paint = Paint()
        val fm: Paint.FontMetrics = Paint.FontMetrics()
        paint.color = Color.BLACK
        paint.getFontMetrics(fm)
        val margin = 50
        val horizontalSpacing = 24
        val verticalSpacing = 36
        val x = horizontalSpacing //(bitmap.getWidth() - bounds.width()) / 2;

        val y: Int = src.getHeight() - verticalSpacing
        //(50 - margin)
        //50 + fm.top - margin
       //10 + fm.bottom + margin
        canvas.drawRect(x.toFloat() - 100, y.toFloat() + 100,
                1200.toFloat(), 1400.toFloat(), paint)
        paint.color = Color.WHITE

        var testTextSize: Float = 12f;
        val bounds = Rect()
        paint.getTextBounds(watermark, 0, watermark!!.length, bounds)

        // Calculate the desired size as a proportion of our testTextSize.

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize: Float = testTextSize * w / bounds.width()
        paint.textSize = desiredTextSize

        canvas.drawText(watermark!!, x.toFloat(), y.toFloat(), paint)
        return result
    }



    fun textAsBitmap(bitmap: Bitmap, text: String?): Bitmap? {

        val paint = Paint()
        val mTextPaintOutline = Paint()
        val scaledSize = resources.getDimensionPixelSize(R.dimen.watermark_text_size)
        val scale = resources.displayMetrics.density
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val bounds = Rect()
        paint.getTextBounds(text, 0, text!!.length, bounds)
        paint.textSize = scaledSize.toFloat()
        val x: Int = canvas.width - bounds.width()
        val y: Int = canvas.height - bounds.height() / 2
        canvas.drawText(text, 0f, y.toFloat(), paint)
        paint.style = Paint.Style.FILL
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
        Toast.makeText(this@CameraPhotoReportActivity, this, Toast.LENGTH_SHORT).show()
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
        videoCapture = VideoCapture.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
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
                            imageCapture,
                            videoCapture
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
                binding.cameraTorchButton.setImageDrawable(
                        ContextCompat.getDrawable(
                                this,
                                kz.cheesenology.smartremontmobile.R.drawable.ic_flash_on_24dp
                        )
                )
            } else {
                binding.cameraTorchButton.setImageDrawable(
                        ContextCompat.getDrawable(
                                this,
                                kz.cheesenology.smartremontmobile.R.drawable.ic_flash_off_24dp
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
