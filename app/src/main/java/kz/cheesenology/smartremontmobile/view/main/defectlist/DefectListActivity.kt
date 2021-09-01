package kz.cheesenology.smartremontmobile.view.main.defectlist

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devlomi.record_view.OnRecordListener
import com.devlomi.record_view.RecordButton
import com.devlomi.record_view.RecordView
import com.github.piasy.rxandroidaudio.AudioRecorder
import kotlinx.android.synthetic.main.activity_defect_list.*
import kotlinx.android.synthetic.main.view_audio_player.view.*
import kz.cheesenology.smartremontmobile.PhotoDefectEditActivity
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.rooms.RoomEntity
import kz.cheesenology.smartremontmobile.model.CheckListDefectSelectModel
import kz.cheesenology.smartremontmobile.model.DefectListModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.view.camerax.CameraDefectListActivity
import kz.cheesenology.smartremontmobile.view.main.camera.PhotoPreviewActivity
import kz.cheesenology.smartremontmobile.view.main.camera.VideoPlayActivity
import kz.cheesenology.smartremontmobile.view.main.send.SendStatsFragment
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import nl.changer.audiowife.AudioWife
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates

class DefectListActivity : MvpAppCompatActivity(), DefectListView {

    @Inject
    lateinit var presenter: DefectListPresenter

    @InjectPresenter
    lateinit var moxyPresenter: DefectListPresenter

    @ProvidePresenter
    fun providePresenter() = presenter


    private var adapter: DefectListAdapter by Delegates.notNull()
    val layoutManager: LinearLayoutManager = LinearLayoutManager(this)

    var mAudioRecorder = AudioRecorder.getInstance()
    var mAudioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@DefectListActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_defect_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Дефекты"

        presenter.setIntentData(
            intent!!.getIntExtra("remont_id", 0),
            intent!!.getIntExtra("active_stage_id", 0)
        )

        adapter = DefectListAdapter()
        rvDefectList.adapter = adapter
        rvDefectList.layoutManager = layoutManager
        rvDefectList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setCallback(object : DefectListAdapter.Callback {
            override fun shareDefects(arrayList: java.util.ArrayList<T>) {
                val uriList: ArrayList<Uri> = ArrayList()
                arrayList.forEach {
                    try {
                        val file = File(AppConstant.FULL_MEDIA_PHOTO_PATH, it.fileName!!)
                        if (file.exists()) {
                            val uri = FileProvider.getUriForFile(
                                this@DefectListActivity,
                                "$packageName.fileprovider",
                                file
                            )
                            uriList.add(uri)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                if (!uriList.isNullOrEmpty()) {
                    sendShareIntent(uriList)
                }
            }

            override fun acceptDefectList(arrayList: java.util.ArrayList<T>) {
                val builder = AlertDialog.Builder(this@DefectListActivity)
                builder.setTitle("Принять")
                builder.setMessage("Вы действительно хотите принять выделенные дефекты?")
                builder.setPositiveButton("Да") { dialog, which ->
                    presenter.acceptDefects(arrayList)
                    adapter.closeActionMode()
                }
                builder.setNegativeButton("Нет") { dialog, which ->
                }
                builder.show()
            }

            override fun onFileClick(defectListModel: T) {
                presenter.showMediaItem(defectListModel)
            }

            override fun deleteSelectedList(arrayList: MutableList<T>) {
                val builder = AlertDialog.Builder(this@DefectListActivity)
                builder.setTitle("Удалить")
                builder.setMessage("Вы действительно хотите удалить выделенные дефекты?")
                builder.setPositiveButton("Да") { dialog, which ->
                    presenter.deleteDefects(arrayList as java.util.ArrayList<T>)
                    adapter.closeActionMode()
                }
                builder.setNegativeButton("Нет") { dialog, which ->
                }
                builder.show()
            }

            override fun setDefectInfoForList(selectedList: MutableList<T>) {
                presenter.setMultiSelectDefects(selectedList)
            }

            override fun setCheckListIDForCheckList(
                checkListID: Int?,
                remontCheckListPhotoID: Int?
            ) {
                presenter.setCheckListID(remontCheckListPhotoID, checkListID)
            }

            override fun onClick(model: T) {
                presenter.preDefectSet(model)
            }

            override fun setRoomIDForCheckList(roomID: Int, remontCheckListPhotoID: Int?) {
                presenter.getCheckListsByRoom(roomID, remontCheckListPhotoID)
            }
        })

        btnAddDefectsPhoto.setOnClickListener {
            val intent = Intent(this@DefectListActivity, CameraDefectListActivity::class.java)
            intent.putExtra("remont_id", getIntent().getIntExtra("remont_id", 0))
            intent.putExtra("stage_id", getIntent().getIntExtra("active_stage_id", 0))
            intent.putExtra("camera_mode", "photo")
            intent.putExtra("address", getIntent().getStringExtra("address"))
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()

        adapter.notifyDataSetChanged()
    }

    private fun sendShareIntent(uriList: java.util.ArrayList<Uri>) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.type = "image/*"
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_defect_list, menu)
        return true
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_defect_video -> {
            val intent = Intent(this@DefectListActivity, CameraDefectListActivity::class.java)
            intent.putExtra("remont_id", getIntent().getIntExtra("remont_id", 0))
            intent.putExtra("stage_id", getIntent().getIntExtra("active_stage_id", 0))
            intent.putExtra("camera_mode", "video")
            startActivity(intent)
            true
        }
        R.id.menu_defect_send_data -> {
            presenter.showSendStatsDialog()
            true
        }
        R.id.menu_defect_attach_file -> {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )

            val adb = AlertDialog.Builder(this)
            val items = arrayOf<CharSequence>("Добавить фото", "Добавить видео")
            adb.setSingleChoiceItems(items, 0, object : DialogInterface.OnClickListener {
                override fun onClick(d: DialogInterface?, n: Int) {
                    when(n) {
                        0 -> {
                            intent.type = "image/*"
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            startActivityForResult(intent, 1001)
                        }
                        1 -> {
                            intent.type = "video/*"
                            startActivityForResult(intent, 1002)
                        }
                    }
                }
            })
            adb.setNegativeButton("Отмена", null)
            adb.setTitle("")
            adb.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 || requestCode == 1002) {
            if (resultCode != Activity.RESULT_OK) {
                return
            }

            var type: String? = null
            when (requestCode) {
                1001 -> {
                    type = "photo"
                }
                1002 -> {
                    type = "video"
                }
            }

            val uriPathList: ArrayList<String> = ArrayList()
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    val imagePath = getPathFromURI(imageUri)
                    if (imagePath != null)
                        uriPathList.add(imagePath)
                }
                presenter.setAttachFromFile(uriPathList, type)
            } else if (data?.data != null) {
                val imageUri = data.data
                val imagePath = getPathFromURI(imageUri!!)
                if (imagePath != null)
                    uriPathList.add(imagePath)

                presenter.setAttachFromFile(uriPathList, type)
            }
        }
    }

    private fun getPathFromURI(contentUri: Uri): String? {
        var res: String? = ""
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (cursor?.moveToFirst()!!) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(columnIndex)
        }
        cursor.close()
        return res
    }

    override fun navigateToSendDialog(remontID: Int) {
        val dialog = SendStatsFragment(listOf(remontID))
        dialog.show(supportFragmentManager, "statistic")
    }

    override fun showItemPhoto(defectListModel: T) {
        //val intent = Intent(this@DefectListActivity, PhotoPreviewActivity::class.java)
        val intent = Intent(this@DefectListActivity, PhotoDefectEditActivity::class.java)
        intent.putStringArrayListExtra("path_list", arrayListOf(defectListModel.fileName))
        intent.putExtra("file", defectListModel.fileName)
        intent.putExtra("list_status", AppConstant.PHOTO_STATUS_USER)
        intent.putExtra("position", 0)
        startActivity(intent)
    }

    override fun showItemVideo(defectListModel: T) {
        val intent = Intent(this@DefectListActivity, VideoPlayActivity::class.java)
        intent.putExtra("video_path", defectListModel.fileName)
        startActivity(intent)
    }

    override fun showDefectInfoDialog(
        model: MutableList<T>,
        roomList: List<RoomEntity>?,
        checkList: List<CheckListDefectSelectModel>?
    ) {

        val list: ArrayList<T> = model as ArrayList<T>
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_defect_info, null)

        //var roomValue: RoomEntity? = null
        var checkListValue: CheckListDefectSelectModel? = null

        //Edit Text comment
        val etComment = dialogView.findViewById(R.id.tvDialogDefectInfoComment) as EditText
        etComment.imeOptions = EditorInfo.IME_ACTION_DONE

        //ROOM SPINNER
        /*val spinner = dialogView.findViewById(R.id.spinnerDialogDefectRoom) as Spinner
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, roomList!!.toMutableList())
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = aa*/

        //CHECK LIST AUTO TEXT
        val textCheckList =
            dialogView.findViewById(R.id.tvDialogDefectInfoCheckList) as AutoCompleteTextView
        textCheckList.imeOptions = EditorInfo.IME_ACTION_DONE
        val checkListAdapter = ArrayAdapter(
            this,
            R.layout.drop_down_multiline,
            R.id.drop_down_item,
            checkList!!.toMutableList()
        )
        textCheckList.setAdapter(checkListAdapter)

        textCheckList.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                textCheckList.showDropDown()
            }
        }

        textCheckList.setOnClickListener {
            if (textCheckList.text.isNullOrEmpty()) {
                textCheckList.showDropDown()
            }
        }
        textCheckList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.applicationWindowToken, 0)
                val selectedItem = parent.getItemAtPosition(position) as CheckListDefectSelectModel
                checkListValue = selectedItem
            }

        //AUDIO RECORD
        if (list.size == 1) {
            val recordButton = dialogView.findViewById(R.id.recordButtonDefect) as RecordButton
            val recordView = dialogView.findViewById(R.id.recordViewDefect) as RecordView
            recordButton.setRecordView(recordView)
            recordView.cancelBounds = 8F
            recordView.setSmallMicColor(Color.parseColor("#c2185b"))
            recordView.setLessThanSecondAllowed(false)
            recordView.setSlideToCancelText("Отмена")
            recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0)
            recordView.setOnRecordListener(object : OnRecordListener {
                override fun onStart() {
                    Log.d("RecordView", "onStart")
                    startAudioRecord()
                }

                override fun onCancel() {
                    Log.d("RecordView", "onCancel")
                }

                override fun onFinish(recordTime: Long) {
                    Log.d("RecordView", "onFinish")
                    stopRecord(dialogView, list[0].defectID)
                    Log.d("RecordTime", recordTime.toString())
                }

                override fun onLessThanSecond() {
                    Log.d("RecordView", "onLessThanSecond")
                    Toast.makeText(
                        this@DefectListActivity,
                        "Аудиозапись слишком короткая",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            recordView.setOnBasketAnimationEndListener {
                Log.d(
                    "RecordView",
                    "Basket Animation Finished"
                )
            }
        } else {
            val recordButton = dialogView.findViewById(R.id.recordButtonDefect) as RecordButton
            val recordView = dialogView.findViewById(R.id.recordViewDefect) as RecordView
            recordButton.visibility = View.GONE
            recordView.visibility = View.GONE
        }
        //DIALOG
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Подтвердить", null)
            .setNegativeButton("Назад", null)
        val dialogSetDefectInfo: AlertDialog?
        dialogSetDefectInfo = builder.show()
        dialogSetDefectInfo!!.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                //roomValue = spinner.getItemAtPosition(spinner.selectedItemPosition) as RoomEntity
                if (list.size == 1) {
                    val item = list[0]
                    if (item.defectStatus != 1) {
                        presenter.setDefectPhotoInfo(
                            item.defectID!!,
                            checkListValue,
                            etComment.text.toString(),
                            item.remontID,
                            mAudioFile
                        )

                        dialogSetDefectInfo.dismiss()
                    } else {
                        Toast.makeText(
                            this@DefectListActivity,
                            "Нельзя редактировать этот статус",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    var check: Boolean = true
                    list.forEach {
                        if (it.defectStatus == 1)
                            check = false
                    }
                    if (check) {
                        presenter.setDefectListInfo(
                            list,
                            checkListValue,
                            etComment.text.toString(),
                            mAudioFile
                        )
                        dialogSetDefectInfo.dismiss()
                    } else {
                        Toast.makeText(
                            this@DefectListActivity,
                            "Один из выбранных дефектов невозможно отредактировать",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        //EDIT VALUES
        if (list.size == 1) {
            val item = list[0]
            textCheckList.setText(item.checkName)
            etComment.setText(item.comment)

            val audio = File(AppConstant.FULL_MEDIA_AUDIO_PATH + item.audioName)
            if (audio.exists())
                initAudioRecord(Uri.fromFile(audio), audio.name, dialogView, item.defectID)
            else if (!item.audioName.isNullOrEmpty()) {
                initAudioRecord(
                    Uri.parse(AppConstant.getServerName() + item.audioUrl),
                    audio.name,
                    dialogView,
                    item.defectID
                )
            }
            //SET EDIT VALUES
            /*if (item.roomID != null) {
                roomList.forEachIndexed { index, roomEntity ->
                    if (roomEntity.roomID == item.roomID) {
                        roomValue = roomEntity
                        spinner.setSelection(index)
                    }
                }
            }*/

            if (item.checkListID != null) {
                checkList.forEach {
                    if (it.checkListID == item.checkListID)
                        checkListValue = it
                }
            }
        }
    }

    private fun stopRecord(dialogView: View, defectID: Int?) {
        mAudioRecorder.stopRecord()
        initAudioRecord(Uri.fromFile(mAudioFile), mAudioFile!!.name, dialogView, defectID)
        //presenter.updateAudioRecord(mAudioFile)
    }

    private fun initAudioRecord(uri: Uri?, audioName: String, dialogView: View, defectID: Int?) {
        dialogView.llAudioPlayer.visibility = View.VISIBLE
        dialogView.tvAudioTitle.text = audioName

        dialogView.btnClearRecordedAudio.setOnClickListener {
            presenter.deleteAudio(defectID)
            dialogView.llAudioPlayer.visibility = View.GONE
        }

        AudioWife.getInstance()
            .init(this@DefectListActivity, uri)
            .setPlayView(dialogView.btnPlayAudio)
            .setPauseView(dialogView.btnPauseAudio)
            .setSeekBar(dialogView.sbAudioPlayer)
            .setRuntimeView(dialogView.tvAudioRunTime)
            .setTotalTimeView(dialogView.tvAudioTotalTime)

        AudioWife.getInstance().addOnCompletionListener {
            //toast("Воспроизведение окончено").show()
            // do you stuff.
        }

        AudioWife.getInstance().addOnPlayClickListener {
            //toast("Воспроизведение").show()
            // get-set-go. Lets dance.
        }

        AudioWife.getInstance().addOnPauseClickListener {
            Toast.makeText(this@DefectListActivity, "Пауза", Toast.LENGTH_SHORT).show()
        }

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


    override fun setDefectList(it: List<DefectListModel>) {
        adapter.data = it
    }

}