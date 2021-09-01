package kz.cheesenology.smartremontmobile.view.main.stages

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_stages.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.check.list.CheckListEntity
import kz.cheesenology.smartremontmobile.data.groupchat.GroupChatEntity
import kz.cheesenology.smartremontmobile.data.rooms.RoomEntity
import kz.cheesenology.smartremontmobile.data.stage.StageEntity
import kz.cheesenology.smartremontmobile.model.Rooms
import kz.cheesenology.smartremontmobile.model.StageStatusHistSingleModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModelNew
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.PrefUtils
import kz.cheesenology.smartremontmobile.util.color
import kz.cheesenology.smartremontmobile.util.spannable
import kz.cheesenology.smartremontmobile.view.main.camera.PhotoPreviewActivity
import kz.cheesenology.smartremontmobile.view.main.chat.ChatActivity
import kz.cheesenology.smartremontmobile.view.main.checkaccept.AcceptCheckActivity
import kz.cheesenology.smartremontmobile.view.main.defectlist.DefectListActivity
import kz.cheesenology.smartremontmobile.view.main.expand.ChildView
import kz.cheesenology.smartremontmobile.view.main.expand.HeadingView
import kz.cheesenology.smartremontmobile.view.main.photoreport.PhotoReportActivity
import kz.cheesenology.smartremontmobile.view.main.ratings.RatingsActivity
import kz.cheesenology.smartremontmobile.view.stagestatushistory.StageStatusHistoryFragment
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import permissions.dispatcher.*
import javax.inject.Inject


@RuntimePermissions
class StagesActivity : MvpAppCompatActivity(), StagesView, ChildClickInterface {

    @Inject
    lateinit var presenter: StagesPresenter

    @InjectPresenter
    lateinit var moxyPresenter: StagesPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    var REMONT_STATUS_ID = 0
    var STAGE_STATUS_ID = 0

    private lateinit var progressDialog: ProgressDialog

    lateinit var stagesList: List<StageEntity>
    lateinit var roomList: List<RoomEntity>
    var chatStageList: List<GroupChatEntity>? = arrayListOf()

    var currentStage: Int = 0
    var activeStageID: Int = 0
    var currentRoom: Int = 1
    var clientName: String = ""
    var statusText: String = ""

    var dialogChangeStage: AlertDialog? = null

    var userInteraction: Boolean = false

    var menuStage: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@StagesActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stages)

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter.setIntentData(
            intent!!.getIntExtra("remont_id", 0),
            intent!!.getIntExtra("active_stage_id", 0),
            intent!!.getIntExtra("stage_status", 0),
            intent!!.getIntExtra("remont_status", 0)
        )

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Обработка данных...")

        showCameraWithPermissionCheck()

        STAGE_STATUS_ID = intent!!.getIntExtra("stage_status", 0)
        REMONT_STATUS_ID = intent!!.getIntExtra("remont_status", 0)

        currentStage = intent!!.getIntExtra("active_stage_id", 0)
        activeStageID = intent!!.getIntExtra("active_stage_id", 0)
        clientName = intent!!.getStringExtra("client_name")
        statusText = intent!!.getStringExtra("address")
        title = "#" + intent!!.getIntExtra("remont_id", 0).toString()

        tvStagesTitle.text = statusText

        spinnerStages?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (userInteraction) {
                    currentStage = stagesList[position].stageID
                    presenter.getCheckListByActiveStageAndRoom(currentStage)
                    userInteraction = false
                }
            }
        }

        spinnerRooms?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (userInteraction) {
                    currentRoom = roomList[position].roomID
                    presenter.getCheckListByActiveStageAndRoom(currentStage)
                    userInteraction = false
                }
            }
        }

        btnAcceptAllStagesWithoutStatus.setOnClickListener {
            if (REMONT_STATUS_ID in AppConstant.STATUS_REMONT_ACTIVE) {
                if (STAGE_STATUS_ID in AppConstant.STATUS_STAGE_ACTIVE) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("")
                    builder.setMessage("Подтвердите принятие всех чек-листов по текущему этапу, где статус не был указан")

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        presenter.acceptAllStagesWithoutStatus()
                    }

                    builder.setNegativeButton(android.R.string.no) { dialog, which ->

                    }

                    builder.show()
                } else {
                    Toast.makeText(this@StagesActivity, "Этап неактивен", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@StagesActivity, "Ремонт не активен", Toast.LENGTH_SHORT).show()
            }
        }

        /*btnShowPlanirovka.setOnClickListener {
            presenter.setPlanirovkaImage()
        }*/

        btnMakePhotoReport.setOnClickListener {
            val intentPhotoReport = Intent(this@StagesActivity, PhotoReportActivity::class.java)
            intentPhotoReport.putExtra("remont_id", intent.getIntExtra("remont_id", 0))
            if (currentStage == 0) {
                currentStage = intent!!.getIntExtra("active_stage_id", 0)
            }
            intentPhotoReport.putExtra("active_stage_id", currentStage)
            intentPhotoReport.putExtra("address", intent!!.getStringExtra("address"))
            intentPhotoReport.putExtra("stage_chat_id", intent!!, )
            startActivity(intentPhotoReport)
        }
    }

    override fun setChatStagesList(it: List<GroupChatEntity>?) {
        chatStageList = it

    }

    override fun onRestart() {
        super.onRestart()
        presenter.getCheckListByActiveStageAndRoom(currentStage)
    }

    override fun navigateToChat(groupChatID: Int, remontID: Int, stageName: String) {
        val intent = Intent(this@StagesActivity, ChatActivity::class.java)
        intent.putExtra("group_chat_id", groupChatID)
        intent.putExtra("remont_id", remontID)
        intent.putExtra("stage_name", stageName)
        startActivity(intent)
    }

    override fun showPlanirovka(it: String?) {
        val intent = Intent(this@StagesActivity, PhotoPreviewActivity::class.java)
        intent.putStringArrayListExtra("path_list", arrayListOf(it) as ArrayList<String>?)
        intent.putExtra("list_status", AppConstant.PHOTO_STATUS_PLANIROVKA)
        intent.putExtra("position", 0)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun setStageStatus(stageNameByID: StageStatusHistSingleModel) {
        if (stageNameByID.dateCreate == null) {
            stageNameByID.dateCreate = ""
        }
        tvStageStatusText.text = """${stageNameByID.statusName} - ${stageNameByID.dateCreate}"""
    }

    override fun clearStageStatusText() {
        tvStageStatusText.text = ""
    }

    override fun acceptStatus(info: CheckListChildModel, globalPosition: Int) {
        presenter.acceptSelectedCheckList(info, globalPosition, currentStage, currentRoom)
    }

    override fun cancelStatus(info: CheckListChildModel, globalPosition: Int) {
        presenter.sendCheckListStatusWithData(
            info,
            AppConstant.checkCancel,
            currentRoom,
            globalPosition
        )
    }

    override fun updateChildOnPosition(
        globalPosition: Int,
        isAccepted: Int,
        defectCnt: Int?,
        norm: String?,
        checkName: String?
    ) {
        (expandableView.getViewResolverAtPosition(globalPosition) as ChildView)
            .setTitle(isAccepted, defectCnt, norm, checkName)
    }

    override fun updateListAfterFullAccept() {
        presenter.getCheckListByActiveStageAndRoom(currentStage)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun setChangeStageStatusVisibility(visible: Boolean) {
        if (menuStage != null) {
            menuStage!!.findItem(R.id.menu_stage_change_status).isVisible = visible
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        if (AppConstant.isBIG()) {
            inflater.inflate(R.menu.menu_stage_big, menu)
        } else {
            inflater.inflate(R.menu.menu_stage, menu)
        }

        menuStage = menu!!
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_stage_change_status -> {
                presenter.checkStatusChange(REMONT_STATUS_ID, STAGE_STATUS_ID, currentStage)
            }
            R.id.menu_stage_remont_chat -> {

                val charList: List<CharSequence> = chatStageList!!.map { it.groupChatName!! }
                lateinit var dialog: AlertDialog
                val builder = AlertDialog.Builder(this@StagesActivity)
                builder.setSingleChoiceItems(
                    charList.toTypedArray(),
                    0,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(d: DialogInterface?, n: Int) {
                            chatStageList?.forEach {
                                if (it.groupChatName == charList[n]) {
                                    presenter.navigatToChat(it)
                                }
                            }
                        }
                    })
                dialog = builder.create()
                dialog.show()

            }
            R.id.menu_stage_defects -> {
                val intentDefect = Intent(this@StagesActivity, DefectListActivity::class.java)
                intentDefect.putExtra("remont_id", intent.getIntExtra("remont_id", 0))
                if (currentStage == 0) {
                    currentStage = intent!!.getIntExtra("active_stage_id", 0)
                }
                intentDefect.putExtra("active_stage_id", currentStage)
                intentDefect.putExtra("address", intent!!.getStringExtra("address"))
                startActivity(intentDefect)
            }

            R.id.menu_stage_history -> {
                val dialog = StageStatusHistoryFragment(intent!!.getIntExtra("remont_id", 0))
                dialog.show(supportFragmentManager, "statistic")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun changeStageStatusDialog() {
        // Inflates the dialog with custom view
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_change_stage_status, null)
        val spinnerStatus = dialogView.findViewById(R.id.spinnerStageStatusChange) as Spinner
        val spinnerDesc = dialogView.findViewById(R.id.spinnerStageDescription) as Spinner
        val statusText = dialogView.findViewById(R.id.etStageStatusChangeComment) as EditText
        val aa = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayOf("Имеются замечания", "Этап завершен")
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = aa

        val bb = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayOf("ОКК проверено", "Сдан клиенту без ОКК", "Этап завершен с замечаниями", "Ложный вызов")
        )
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDesc.adapter = bb

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(spannable { color(Color.parseColor("#1B5E20"), "Изменение статуса этапа: ") })
            .setMessage(spinnerStages.selectedItem.toString())
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена", null)
        dialogChangeStage = builder.show()
        dialogChangeStage!!.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            Toast.makeText(this@StagesActivity, spinnerStatus.selectedItem.toString(), Toast.LENGTH_SHORT).show()

            if (spinnerDesc.selectedItem.toString() == "Ложный вызов" && statusText.text.isNullOrEmpty()) {
                statusText.error = "Добавьте комментарий"
            } else {
                presenter.sendChangeStageStatus(
                    spinnerStatus.selectedItem.toString(),
                    statusText.text.toString(),
                    spinnerDesc.selectedItem.toString()
                )
            }
        }
    }

    override fun dismissStateChangeDialog() {
        if (dialogChangeStage!!.isShowing) {
            dialogChangeStage!!.dismiss()
            //presenter.getStagesList(currentStage, true, currentRoom)
            finish()
        }
    }

    override fun navigateToRatings(remontID: Int) {
        finish()
        val intent = Intent(this@StagesActivity, RatingsActivity::class.java)
        intent.putExtra("remont_id", remontID)
        startActivity(intent)
    }

    override fun showToast(errMsg: String) {
        Toast.makeText(this@StagesActivity, errMsg, Toast.LENGTH_LONG).show()
    }

    override fun showGetListFromServerError(errMsg: String) {
        Toast.makeText(this@StagesActivity, errMsg, Toast.LENGTH_SHORT).show()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        userInteraction = true
    }

    override fun clearExpandableList() {
        expandableView.removeAllViews()
    }

    override fun setExpandableHeading(item: CheckListEntity, it: List<CheckListChildModelNew>) {
        expandableView.addView(HeadingView(this, item.checkName))
        for (subItem in it) {
            expandableView.addView(ChildView(this, subItem))
        }
    }

    override fun navigateToCheckListNew(info: CheckListChildModelNew, globalPosition: Int) {
        val intent = Intent(this@StagesActivity, AcceptCheckActivity::class.java)
        intent.putExtra("remont_id", getIntent().getIntExtra("remont_id", 0))
        intent.putExtra("stage_status", getIntent().getIntExtra("stage_status", 0))
        intent.putExtra("check_list_id", info.checkListID)
        intent.putExtra("active_stage_id", activeStageID)
        intent.putExtra("current_stage_id", currentStage)
        intent.putExtra("remont_status", getIntent().getIntExtra("remont_status", 0))
        intent.putExtra("global_position", globalPosition)
        intent.putExtra("client_name", clientName)
        intent.putExtra("status_text", statusText)
        intent.putExtra("check_name", info.checkName)
        intent.putExtra("norm", info.norm.toString())
        startActivityForResult(intent, 202)
    }

    override fun navigateToCheckList(
        info: CheckListChildModel,
        globalPosition: Int,
        remontCheckListID: Int
    ) {
        val intent = Intent(this@StagesActivity, AcceptCheckActivity::class.java)
        intent.putExtra("remont_id", getIntent().getIntExtra("remont_id", 0))
        intent.putExtra("stage_status", getIntent().getIntExtra("stage_status", 0))
        intent.putExtra("check_list_id", info.checkListID)
        intent.putExtra("active_stage_id", activeStageID)
        intent.putExtra("current_stage_id", currentStage)
        intent.putExtra("remont_status", getIntent().getIntExtra("remont_status", 0))
        intent.putExtra("room_id", currentRoom)
        intent.putExtra("remont_check_list_id", remontCheckListID)
        intent.putExtra("global_position", globalPosition)
        intent.putExtra("client_name", clientName)
        intent.putExtra("status_text", statusText)
        intent.putExtra("check_name", info.checkName)
        intent.putExtra("room_name", spinnerRooms.selectedItem.toString())
        intent.putExtra("norm", info.norm.toString())

        if (spinnerRooms.selectedItem is Rooms) {
            intent.putExtra("room_id", (spinnerRooms.selectedItem as Rooms).room_id!!)
        }
        startActivityForResult(intent, 202)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 202) {
            if (resultCode == Activity.RESULT_OK)
                updateChildOnPosition(
                    data!!.getIntExtra("global_position", 0),
                    data.getIntExtra("is_accepted", 0),
                    data.getIntExtra("defect_cnt", 0),
                    data.getStringExtra("norm"),
                    data.getStringExtra("check_name")
                )
        }
    }

    override fun onChildClick(info: CheckListChildModelNew, globalPosition: Int) {
        presenter.checkNewCheckList(info, globalPosition, currentStage, currentRoom)
    }

    override fun setFullStages(stages: List<StageEntity>, activeStageID: Int) {
        val spinnerArrayAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            stages,
            activeStageID
        )
        spinnerStages.adapter = spinnerArrayAdapter
        spinnerStages.setSelection(activeStageID - 1)
        stagesList = stages
    }

    override fun setRooms(rooms: List<RoomEntity>) {
        val spinnerArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            rooms
        )
        spinnerRooms.adapter = spinnerArrayAdapter

        roomList = rooms
    }

    override fun closeStageScreen() {
        onBackPressed()
    }

    override fun onChildLongClick() {
        /*val list = listOf("Посмотреть стандарты", "Посмотреть историю")
        selector("", list) { _, i ->
            when {
                list[i] == "Посмотреть стандарты" -> {

                }
                list[i] == "Посмотреть историю" -> {

                }
            }
        }*/
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

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showCamera() {
    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showRationaleForCamera(request: PermissionRequest) {
        Toast.makeText(this@StagesActivity, "Вы отказались от камеры", Toast.LENGTH_SHORT).show()
    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onCameraDenied() {
        Toast.makeText(this, "Вы отказались от камеры", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onCameraNeverAskAgain() {
        Toast.makeText(this, "Вам запрещено пользоваться приложением", Toast.LENGTH_SHORT).show()
    }

    override fun showDialog() {
        if (!progressDialog.isShowing)
            progressDialog.show()
    }

    override fun dismissDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

}
