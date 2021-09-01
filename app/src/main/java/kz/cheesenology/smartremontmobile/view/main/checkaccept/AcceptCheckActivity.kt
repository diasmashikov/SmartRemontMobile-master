package kz.cheesenology.smartremontmobile.view.main.checkaccept

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.github.piasy.rxandroidaudio.AudioRecorder
import kotlinx.android.synthetic.main.activity_accept_check.*
import kotlinx.android.synthetic.main.view_add_photo.*
import kotlinx.android.synthetic.main.view_audio_player.*
import kotlinx.android.synthetic.main.view_correct_standart.*
import kotlinx.android.synthetic.main.view_wrong_standart.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListEntity
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryEntity
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoEntity
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaEntity
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.model.check.UploadResult
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.view.main.camera.PhotoPreviewActivity
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import nl.changer.audiowife.AudioWife
import permissions.dispatcher.*
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates


@RuntimePermissions
class AcceptCheckActivity : MvpAppCompatActivity(), CheckView, View.OnClickListener {

    var TAG = "audio record"

    val REQUEST_IMAGE_CAPTURE = 1001

    var mCurrentPhotoPath: String? = null
    var mCurrentPhotoName: String? = null
    var mAudioFile: File? = null
    var checkStatus: Int? = null
    var isAudioFromServer: Boolean = false
    var audioUrl: String? = null
    var userInteraction = false

    var clientName: String = ""
    var statusText: String = ""
    var checkName: String = ""
    var normText: String = ""


    @Inject
    lateinit var presenter: CheckPresenter

    @InjectPresenter
    lateinit var moxyPresenter: CheckPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    var mAudioRecorder = AudioRecorder.getInstance()

    private var cameraAdapter: CameraListAdapter by Delegates.notNull()
    private var historyAdapter: HistoryListAdapter by Delegates.notNull()
    private var standartGoodAdapter: StandartGoodAdapter by Delegates.notNull()
    private var standartWeakAdapter: StandartWeakAdapter by Delegates.notNull()

    private lateinit var progressDialog: ProgressDialog

    var REMONT_STATUS_ID = 0
    var STAGE_STATUS_ID = 0
    var ACTIVE_STAGE_ID = 0
    var CURRENT_STAGE_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@AcceptCheckActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_check)

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        startAudioWithPermissionCheck()

        presenter.setIntentData(
            intent!!.getIntExtra("remont_id", 0),
            intent!!.getIntExtra("check_list_id", 0),
            intent!!.getIntExtra("remont_check_list_id", 0),
            intent!!.getIntExtra("stage_status", 0),
            intent!!.getIntExtra("active_stage_id", 0),
            intent!!.getIntExtra("current_stage_id", 0)
        )

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Получение данных...")

        REMONT_STATUS_ID = intent!!.getIntExtra("remont_status", 0)
        STAGE_STATUS_ID = intent!!.getIntExtra("stage_status", 0)
        ACTIVE_STAGE_ID = intent!!.getIntExtra("active_stage_id", 0)
        CURRENT_STAGE_ID = intent!!.getIntExtra("current_stage_id", 0)

        setTitleBlock()

        checkStageStatusElements()

        //BUTTON TAKE PHOTO
        btnCheckCapturePhoto.setOnClickListener {
            presenter.captureImage(
                packageManager,
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
        }

        rvCheckListPhotos.isNestedScrollingEnabled = false
        rvStandartListCorrect.isNestedScrollingEnabled = false
        rvStandartListWrong.isNestedScrollingEnabled = false

        //PHOTO LIST ADAPTER
        cameraAdapter = CameraListAdapter(this@AcceptCheckActivity)
        rvCheckListPhotos.adapter = cameraAdapter
        val gridLayout = androidx.recyclerview.widget.GridLayoutManager(this@AcceptCheckActivity, 3)
        rvCheckListPhotos.layoutManager = gridLayout
        cameraAdapter.setCallback(object : CameraListAdapter.Callback {
            override fun onPhotoClick(position: Int, pathList: List<String>) {
                startPhotoPreview(position, pathList, AppConstant.PHOTO_STATUS_USER)
            }

            override fun onPhotoDelete(position: Int, remontCheckListPhotoID: Int?) {
                presenter.deletePhotoFromDB(remontCheckListPhotoID)
            }

            override fun onClick(
                model: List<Bitmap>,
                position: Int,
                urlList: List<String>?,
                pathList: List<String>?
            ) {
                //startPhotoPreview(model, position, urlList, pathList)
            }
        })

        //History LIST ADAPTER
        /*historyAdapter = HistoryListAdapter()
        rvHistoryList.adapter = historyAdapter
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvHistoryList.layoutManager = layoutManager*/

        //GOOD STANDARD ADAPTER
        standartGoodAdapter = StandartGoodAdapter()
        rvStandartListCorrect.adapter = standartGoodAdapter
        val layoutManager1 = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        rvStandartListCorrect.layoutManager = layoutManager1
        standartGoodAdapter.setCallback(object : StandartGoodAdapter.Callback {
            override fun onClick(position: Int, urlList: List<String>) {
                startPhotoPreview(position, urlList, AppConstant.PHOTO_STATUS_STANDART)
            }
        })

        //WEAK STANDARD ADAPTER
        standartWeakAdapter = StandartWeakAdapter()
        rvStandartListWrong.adapter = standartWeakAdapter
        val layoutManager2 = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        rvStandartListWrong.layoutManager = layoutManager2
        standartWeakAdapter.setCallback(object : StandartWeakAdapter.Callback {
            override fun onClick(position: Int, urlList: List<String>) {
                startPhotoPreview(position, urlList, AppConstant.PHOTO_STATUS_STANDART)
            }
        })
    }

    private fun checkStageStatusElements() {


    }

    private fun startPhotoPreview(position: Int, pathList: List<String>?, status: String) {
        val intent = Intent(this@AcceptCheckActivity, PhotoPreviewActivity::class.java)
        intent.putStringArrayListExtra("path_list", pathList as ArrayList<String>?)
        intent.putExtra("list_status", status)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun setTitleBlock() {
        clientName = intent!!.getStringExtra("client_name")
        statusText = intent!!.getStringExtra("status_text")
        checkName = intent!!.getStringExtra("check_name")
        normText = intent!!.getStringExtra("norm")

        title = checkName

        tvAcceptCheckTitle.text = """
            $checkName
        """.trimIndent()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        userInteraction = true
    }

    override fun hideAudioPlayer() {
        llAudioPlayer.visibility = View.GONE
    }

    override fun showToast(s: String) {
        Toast.makeText(this@AcceptCheckActivity, s, Toast.LENGTH_SHORT).show()
    }

    override fun setResultData(value: UploadResult) {
        val mIntent = Intent()
        mIntent.putExtra("global_position", intent!!.getIntExtra("global_position", 0))
        mIntent.putExtra("is_accepted", value.isAccepted)
        mIntent.putExtra("check_name", checkName)
        mIntent.putExtra("defect_cnt", value.defectCnt)
        mIntent.putExtra("norm", value.norm)
        setResult(Activity.RESULT_OK, mIntent)

        userInteraction = false
        onBackPressed()
    }

    override fun setCheckInfo(info: RemontCheckListEntity) {
        when (info.isAccepted) {
            0 -> {
                /*//DEFECTS
                cbDefect.isChecked = true
                if (info.description != null)
                    etDefectComment.setText(info.description.toString())
                if (info.defectCnt != null)
                    etDefectNumber.setText(info.defectCnt.toString())*/
            }
            1 -> {
                //ACCEPT
                cbAccept.isChecked = true
            }
            2 -> {
                //CANCELLED
                cbCancel.isChecked = true
            }
        }
    }

    override fun setPhotos(it: List<UserDefectMediaEntity>) {
        /*for (item in it) {
            cameraAdapter.singleData = CameraListModel(true, null, null, item.fileName, null, item.defectID)
        }*/
        cameraAdapter.data = it
    }

    override fun setHistory(history: List<CheckListHistoryEntity>) {
        //historyAdapter.data = history
    }

    override fun setGoodStandart(goodList: List<StandartPhotoEntity>) {
        standartGoodAdapter.data = goodList
    }

    override fun setWeakStandart(weakList: List<StandartPhotoEntity>) {
        standartWeakAdapter.data = weakList
    }

    override fun setUserRecordedAudio(audioName: String?) {
        val file = File(AppConstant.FULL_MEDIA_AUDIO_PATH, audioName)
        mAudioFile = file
        initAudioRecord(Uri.fromFile(file), audioName)
    }

    override fun setImagePath(absolutePath: String?) {
        mCurrentPhotoPath = absolutePath
    }

    override fun setCapturedPhoto(sBitmap: Bitmap?, lBitmap: Bitmap?, mCurrentPhotoPath: String?) {
        val file = File(mCurrentPhotoPath)
        if (file.exists())
        //cameraAdapter.singleData = CameraListModel(true, sBitmap!!, lBitmap!!, mCurrentPhotoPath!!, "", -1)
        else
            Toast.makeText(
                this@AcceptCheckActivity,
                "Ошибка. Фотография не найдена",
                Toast.LENGTH_SHORT
            ).show()
    }

    override fun showDialog(s: String) {
        if (!progressDialog.isShowing) {
            progressDialog.setTitle(s)
            progressDialog.show()
        }
    }

    override fun dismissDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioWife.getInstance().release()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlideApp.get(this@AcceptCheckActivity).trimMemory(level)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_check, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_check_accept -> {
                if (REMONT_STATUS_ID in AppConstant.STATUS_REMONT_ACTIVE) {
                    if (AppConstant.isWorkWithCheckListAccepted(CURRENT_STAGE_ID, ACTIVE_STAGE_ID, STAGE_STATUS_ID)) {
                        if (radioGroupCheckStatus.checkedRadioButtonId == -1) {
                            toast("Выберите статус").show()
                        } else {

                            if (checkStatus == AppConstant.checkAccept || checkStatus == AppConstant.checkCancel) {
                                showAcceptStatusDialog()
                            } else if (checkStatus == AppConstant.checkDefect) {
                                if (etDefectNumber.text.isEmpty()) {
                                    etDefectNumber.requestFocus()
                                    etDefectNumber.error = "Укажите количество дефектов"
                                    toast("Укажите количество дефектов").show()
                                } else {
                                    when {
                                        etDefectNumber.text.toString().toInt() < 1 -> etDefectNumber.error = "Недопустимое количество"
                                        etDefectNumber.text.toString().toInt() > 99 -> etDefectNumber.error = "Недопустимое количество"
                                        else -> showAcceptStatusDialog()
                                    }
                                }
                            }

                        }
                    } else {
                        showToast("Этап неактивен")
                    }
                } else {
                    showToast("Ремонт неактивен")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    private fun showAcceptStatusDialog() {
        /* alert("Подтвердите ваш выбор: ") {
             yesButton {

             }
             noButton { }
         }.show()*/

        val num = etDefectNumber.text.toString()
        if (num.isEmpty()) {
            presenter.updateCheckStatus(
                checkStatus,
                etDefectComment.text.toString(),
                0
            )
        } else {
            presenter.updateCheckStatus(
                checkStatus,
                etDefectComment.text.toString(),
                num.toInt()
            )
        }

    }

    override fun onClick(v: View?) {
        when (v) {

        }
    }

    override fun startCameraIntent(
        takePictureIntent: Intent,
        photoFile: File,
        absolutePath: String
    ) {
        mCurrentPhotoPath = absolutePath
        mCurrentPhotoName = photoFile.name

        val photoURI = FileProvider.getUriForFile(
            applicationContext,
            applicationContext.packageName + ".fileprovider",
            photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            galleryAddPic(mCurrentPhotoPath)

            val thumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(mCurrentPhotoPath),
                128, 128
            )
            presenter.insertAndScale(
                windowManager,
                mCurrentPhotoPath,
                thumbImage,
                mCurrentPhotoName
            )
            if (mCurrentPhotoPath == null || mCurrentPhotoName == null) {
                Toast.makeText(this@AcceptCheckActivity, "Произошла ошибка формирования фотографии", Toast.LENGTH_SHORT).show()
            }
            mCurrentPhotoPath = null
            mCurrentPhotoName = null
        }
    }

    private fun galleryAddPic(path: String?) {
        //Добавление фото в медиа хранилище?!
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, path)
        values.put(MediaStore.Images.Media.DESCRIPTION, "SmartRemont photo")
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.BUCKET_ID, path.hashCode())
        values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "SmartRemont")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.MediaColumns.DATA, path)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        //Скан медиафайлов
        MediaScannerConnection.scanFile(
            this@AcceptCheckActivity,
            arrayOf(path), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(path)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }

    private fun initAudioRecord(uri: Uri, audioName: String?) {
        llAudioPlayer.visibility = View.VISIBLE
        tvAudioTitle.text = audioName!!

        AudioWife.getInstance()
            .init(this@AcceptCheckActivity, uri)
            .setPlayView(btnPlayAudio)
            .setPauseView(btnPauseAudio)
            .setSeekBar(sbAudioPlayer)
            .setRuntimeView(tvAudioRunTime)
            .setTotalTimeView(tvAudioTotalTime)

        AudioWife.getInstance().addOnCompletionListener {
            //toast("Воспроизведение окончено").show()
            // do you stuff.
        }

        AudioWife.getInstance().addOnPlayClickListener {
            //toast("Воспроизведение").show()
            // get-set-go. Lets dance.
        }

        AudioWife.getInstance().addOnPauseClickListener {
            Toast.makeText(this@AcceptCheckActivity, "Пауза", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecord() {
        mAudioRecorder.stopRecord()
        //TODO как обработать перезапись аудио?
        initAudioRecord(Uri.fromFile(mAudioFile), mAudioFile!!.name)
        presenter.updateAudioRecord(mAudioFile)
    }

    private fun startAudioRecord() {
        var mob = Environment.getExternalStorageDirectory().absoluteFile
        mob = File(mob, AppConstant.MEDIA_USER_AUDIO_PATH)
        if (!mob.exists()) {
            mob.mkdirs()
        }
        mAudioFile = File(AppConstant.FULL_MEDIA_AUDIO_PATH + System.nanoTime() + ".file.m4a")
        mAudioRecorder.prepareRecord(
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
            192000, 192000, mAudioFile
        )
        mAudioRecorder.startRecord()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated method
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun startAudio() {
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForAudio(request: PermissionRequest) {
        Toast.makeText(this@AcceptCheckActivity, "Вы запретили пользоваться камерой", Toast.LENGTH_SHORT).show()
    }

    @OnPermissionDenied(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onAudioDenied() {
        Toast.makeText(this, "Вы отказались от камеры", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onAudioNeverAskAgain() {
        Toast.makeText(this, "Вам запрещено пользоваться приложением", Toast.LENGTH_SHORT).show()
    }
}
