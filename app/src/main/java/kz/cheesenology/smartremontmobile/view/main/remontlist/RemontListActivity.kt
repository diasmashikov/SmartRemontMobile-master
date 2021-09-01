package kz.cheesenology.smartremontmobile.view.main.remontlist

import android.app.DownloadManager
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.actionitembadge.library.ActionItemBadge
import kotlinx.android.synthetic.main.activity_renovation_list.*
import kotlinx.android.synthetic.main.dialog_filter_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.chat.StageChatEntity
import kz.cheesenology.smartremontmobile.model.RemontListDBModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.view.main.clientphoto.ClientPhotoActivity
import kz.cheesenology.smartremontmobile.view.main.notification.ServerNotificationActivity
import kz.cheesenology.smartremontmobile.view.main.ratings.RatingsActivity
import kz.cheesenology.smartremontmobile.view.main.send.SendStatsFragment
import kz.cheesenology.smartremontmobile.view.main.stages.StagesActivity
import kz.cheesenology.smartremontmobile.view.main.webview.WebViewDefaultActivity
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates


class RemontListActivity : MvpAppCompatActivity(), RemontListView {

    private var localMenu: Menu? = null

    @Inject
    lateinit var presenter: RemontListPresenter

    @InjectPresenter
    lateinit var moxyPresenter: RemontListPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    private var adapter: RenovationListAdapter by Delegates.notNull()

    var selectedList = mutableListOf<Int>()

    private lateinit var progressDialog: ProgressDialog

    lateinit var searchView: SearchView
    private var queryTextListener: SearchView.OnQueryTextListener? = null

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@RemontListActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renovation_list)

        title = "Ремонты"

        presenter.setIntentInfo(intent.getIntExtra("active_stage_id", 0))

        if (intent.getBooleanExtra("status", false)) {
            //Автоматический запрос данных если нажал на уведомление
            //presenter.checkBeforeGetFullDataList()

            //Переход на экран уведомлений
            //startActivity(Intent(this@RemontListActivity, ServerNotificationActivity::class.java))
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Получение данных...")

        adapter = RenovationListAdapter()
        rvRenovationList.adapter = adapter
        rvRenovationList.layoutManager = LinearLayoutManager(baseContext)
        rvRenovationList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setCallback(object : RenovationListAdapter.Callback {
            override fun acceptRemont(mode: RemontAdapterModel) {
                presenter.acceptRemontStatus(mode.remontID, "1")
            }

            override fun cancelRemont(mode: RemontAdapterModel) {
                presenter.acceptRemontStatus(mode.remontID, "0")
            }

            override fun onRatingClick(remontListDBModel: RemontAdapterModel) {
                val intent = Intent(this@RemontListActivity, RatingsActivity::class.java)
                intent.putExtra("remont_id", remontListDBModel.remontID)
                startActivity(intent)
            }

            override fun onContractorPhoneClick(contractorPhone: String?) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contractorPhone"))
                startActivity(intent)
            }

            override fun onInternalMasterCall(internalMasterPhone: String?) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$internalMasterPhone"))
                startActivity(intent)
            }

            override fun runContructorLink(constructorUrl: String?) {
                if (constructorUrl.isNullOrEmpty()) {
                    showToast("Ссылка на конструктор отсутствует!")
                } else {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(AppConstant.getServerName() + constructorUrl)
                        )
                    startActivity(browserIntent)
                }
            }

            override fun showRemontProject(remontListDBModel: RemontAdapterModel) {
                if (remontListDBModel.projectRemontName.isNullOrEmpty()) {
                    Toast.makeText(
                        this@RemontListActivity,
                        "Проект по этому ремонту доступен только из чата",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val downloadedFile =
                        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath.toString() + "/" + remontListDBModel.projectRemontName)
                    if (downloadedFile.exists()) {
                        Toast.makeText(this@RemontListActivity, "Открытие файла", Toast.LENGTH_SHORT)
                            .show()
                        showPdf(downloadedFile)
                    } else {
                        val req =
                            DownloadManager.Request(Uri.parse(AppConstant.getProjectFileUrl() + remontListDBModel.remontID))
                        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(true)
                            .setTitle(remontListDBModel.projectRemontName)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDescription("")
                            .setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                remontListDBModel.projectRemontName
                            )
                        val downloadManager: DownloadManager =
                            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(req)
                    }
                }
            }



            override fun menuClick(valueItem: RemontAdapterModel) {
                val intent = Intent(this@RemontListActivity, ClientPhotoActivity::class.java)
                intent.putExtra("remont_id", valueItem.remontID)
                startActivity(intent)
                //startActivity(Intent(this@RemontListActivity, ClientPhotoActivity::class.java))
            }

            override fun onClick(model: RemontAdapterModel) {
                when {
                    //model.okkStatus == 1 -> showAcceptRemontDialog(model)
                    model.okkStatus == 3 -> Toast.makeText(this@RemontListActivity, "Ремонт был отключен", Toast.LENGTH_SHORT).show()
                    else -> {
                        val intent = Intent(this@RemontListActivity, StagesActivity::class.java)
                        intent.putExtra("remont_id", model.remontID)
                        intent.putExtra("active_stage_id", model.activeStageID)
                        intent.putExtra("client_name", model.clientName)
                        intent.putExtra("stage_status", model.stageStatusID)
                        intent.putExtra("remont_status", model.remontStatusID)
                        intent.putExtra(
                            "status_text",
                            model.okkStatusText + " " + model.remontDateBegin
                        )
                        intent.putExtra("address", model.address)
                        //intent.putExtra("stage_chat_id", model.)
                        startActivity(intent)
                    }
                }
            }
        })

        adapter.remontListViewListener = this
    }

    fun showPdf(downloadedFile: File) {
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        val ext: String = downloadedFile.name.substring(downloadedFile.name.lastIndexOf(".") + 1)
        val type: String? = mime.getMimeTypeFromExtension(ext)
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri: Uri = FileProvider.getUriForFile(
                    baseContext,
                    "kz.cheesenology.smartremontmobile.fileprovider",
                    downloadedFile
                )
                intent.setDataAndType(contentUri, type)
            } else {
                intent.setDataAndType(Uri.fromFile(downloadedFile), type)
            }
            startActivityForResult(intent, 20003)
        } catch (anfe: ActivityNotFoundException) {
            Toast.makeText(
                baseContext,
                "Нет приложения для просмотра этого типа файлов",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRestart() {
        super.onRestart()
        presenter.checkNotifications(true)
    }

    override fun onResume() {
        super.onResume()
        presenter.showRemontList()
        presenter.apkUpdateCheck()
    }

    override fun needsUpdate(b: Boolean) {
        if (b) {
            viewUpdateStatus.visibility = View.VISIBLE
        } else {
            viewUpdateStatus.visibility = View.GONE
        }
    }


    override fun setNotificationCount(i: Int) {
        if (i > 0) {
            ActionItemBadge.update(
                this, localMenu?.findItem(R.id.menu_remont_notifications),
                getDrawable(R.drawable.ic_notifications_white_24dp),
                ActionItemBadge.BadgeStyles.RED,
                i
            )
        } else {
            //ActionItemBadge.hide(localMenu?.findItem(R.id.menu_remont_notifications))
            ActionItemBadge.update(
                this, localMenu?.findItem(R.id.menu_remont_notifications),
                getDrawable(R.drawable.ic_notifications_white_24dp),
                ActionItemBadge.BadgeStyles.GREY,
                i
            )
        }
    }

    override fun navigateToNotificationActivity() {
        startActivity(Intent(this@RemontListActivity, ServerNotificationActivity::class.java))
    }

    override fun showToast(s: String) {
        Toast.makeText(this@RemontListActivity, s, Toast.LENGTH_SHORT).show()
    }

    override fun refreshList() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_remont, menu)

        localMenu = menu
        //Заглушка
        presenter.checkNotifications(true)

        val searchItem = menu!!.findItem(R.id.menu_remont_list_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
            searchView.isIconified = false
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    Log.i("onQueryTextChange", newText)
                    adapter.filter.filter(newText)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("onQueryTextSubmit", query)
                    adapter.filter.filter(query)
                    return false
                }
            }
            searchView.setOnQueryTextListener(queryTextListener)

            searchView.setOnCloseListener {
                //searchView.restoreDefaultFocus()
                false
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_remont_list_search -> {
                searchView.requestFocus()
                searchView.requestFocusFromTouch()
            }
            R.id.menu_remont_notifications -> {
                startActivity(
                    Intent(
                        this@RemontListActivity,
                        ServerNotificationActivity::class.java
                    )
                )
            }
            R.id.menu_remont_get_catalog -> {
                presenter.getCatalogsFromServer()
            }
            R.id.menu_remont_get_list -> {
                presenter.checkBeforeGetFullDataList()
            }
            R.id.menu_remont_send_data -> {
                presenter.checkBeforeNavigateSend()
            }
            R.id.menu_remont_filter -> {
                presenter.showFilterDialog()
            }
            R.id.menu_remont_report -> {
                val intent = Intent(this@RemontListActivity, WebViewDefaultActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun updateFilterSelectedItems(status1: Int, status2: Int, status3: Int, status4: Int) {
        selectedList = mutableListOf(status1, status2, status3, status4)
    }

    private fun showRemontFilterDialog() {
        val dialogBuilder = AlertDialog.Builder(this@RemontListActivity)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter_list, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setPositiveButton("Фильтровать") { dialog, id ->
            dialogView.rgRemontStatus.setOnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                presenter.changeFilterPref("REMONT", radio.id)
            }
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun showStatusFilterDialog(
        statusRemont: Int,
        stage: Int,
        statusOkk: Int,
        stageStatus: Int
    ) {
        val dialogBuilder = AlertDialog.Builder(this@RemontListActivity)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter_list, null)
        dialogBuilder.setView(dialogView)


        //remont status
        when (statusRemont) {
            0 -> dialogView.rbFilterRemontStatusAll.isChecked = true
            1 -> dialogView.rbFilterRemontStatusStart.isChecked = true
            2 -> dialogView.rbFilterRemontStatusFinish.isChecked = true
            4 -> dialogView.rbFilterRemontStatusWarranty.isChecked = true
        }

        //okk status
        when (statusOkk) {
            0 -> dialogView.rbFilterOkkStatusAll.isChecked = true
            1 -> dialogView.rbFilterOkkStatusNotAccepted.isChecked = true
            2 -> dialogView.rbFilterOkkStatusAccept.isChecked = true
            3 -> dialogView.rbFilterOkkStatusCanceled.isChecked = true
        }

        //stage
        when (stage) {
            0 -> dialogView.rbFilterStageStatusAll.isChecked = true
            -1 -> dialogView.rbFilterStageStatusWithoutStage.isChecked = true
            1 -> dialogView.rbFilterStageStatusChernovayaOtdelka.isChecked = true
            2 -> dialogView.rbFilterStageStatusRemontProject.isChecked = true
            3 -> dialogView.rbFilterStageStatusElectricityBath.isChecked = true
            4 -> dialogView.rbFilterStageStatusWallsFloor.isChecked = true
            5 -> dialogView.rbFilterStageStatusLaminatOboi.isChecked = true
            6 -> dialogView.rbFilterStageStatusClientAccept.isChecked = true
        }

        //stage status
        when (stageStatus) {
            0 -> dialogView.rbStageStatusAll.isChecked = true
            -1 -> dialogView.rbStageStatusNewStage.isChecked = true
            1 -> dialogView.rbStageStatusStageReadyAccept.isChecked = true
            2 -> dialogView.rbStageStatusStageProblem.isChecked = true
            3 -> dialogView.rbStageStatusStageProblemFixed.isChecked = true
            4 -> dialogView.rbStageStatusStageFinished.isChecked = true
            5 -> dialogView.rbStageStatusStageProblemAndFixAll.isChecked = true
        }

        dialogBuilder.setPositiveButton("Фильтровать") { dialog, id ->

            when (dialogView.rgRemontStatus.checkedRadioButtonId) {
                dialogView.rbFilterRemontStatusAll.id -> {
                    presenter.changeFilterStatusPref("filter_remont", 0)
                }
                dialogView.rbFilterRemontStatusStart.id -> {
                    presenter.changeFilterStatusPref("filter_remont", 1)
                }
                dialogView.rbFilterRemontStatusFinish.id -> {
                    presenter.changeFilterStatusPref("filter_remont", 2)
                }
                dialogView.rbFilterRemontStatusWarranty.id -> {
                    presenter.changeFilterStatusPref("filter_remont", 4)
                }
            }

            when (dialogView.rgOkkStatus.checkedRadioButtonId) {
                dialogView.rbFilterOkkStatusAll.id -> {
                    presenter.changeFilterStatusPref("filter_okk", 0)
                }
                dialogView.rbFilterOkkStatusNotAccepted.id -> {
                    presenter.changeFilterStatusPref("filter_okk", 1)
                }
                dialogView.rbFilterOkkStatusAccept.id -> {
                    presenter.changeFilterStatusPref("filter_okk", 2)
                }
                dialogView.rbFilterOkkStatusCanceled.id -> {
                    presenter.changeFilterStatusPref("filter_okk", 3)
                }
            }

            when (dialogView.rgStage.checkedRadioButtonId) {
                dialogView.rbFilterStageStatusAll.id -> {
                    presenter.changeFilterStatusPref("filter_stage", 0)
                }
                dialogView.rbFilterStageStatusWithoutStage.id -> {
                    presenter.changeFilterStatusPref("filter_stage", -1)
                }
                dialogView.rbFilterStageStatusChernovayaOtdelka.id -> {
                    presenter.changeFilterStatusPref("filter_stage", 1)
                }
                dialogView.rbFilterStageStatusRemontProject.id -> {
                    presenter.changeFilterStatusPref("filter_stage", 2)
                }
                dialogView.rbFilterStageStatusElectricityBath.id -> {
                    presenter.changeFilterStatusPref("filter_stage", 3)
                }
                dialogView.rbFilterStageStatusWallsFloor.id -> {
                    presenter.changeFilterStatusPref("filter_stage", 4)
                }
                dialogView.rbFilterStageStatusLaminatOboi.id -> {
                    presenter.changeFilterStatusPref("filter_stage", 5)
                }
                dialogView.rbFilterStageStatusClientAccept.id -> {
                    presenter.changeFilterStatusPref("filter_stage", 6)
                }
            }

            when (dialogView.rgStageStatus.checkedRadioButtonId) {
                dialogView.rbStageStatusAll.id -> {
                    presenter.changeFilterStatusPref("filter_stage_status", 0)
                }
                dialogView.rbStageStatusNewStage.id -> {
                    presenter.changeFilterStatusPref("filter_stage_status", -1)
                }
                dialogView.rbStageStatusStageReadyAccept.id -> {
                    presenter.changeFilterStatusPref("filter_stage_status", 1)
                }
                dialogView.rbStageStatusStageProblem.id -> {
                    presenter.changeFilterStatusPref("filter_stage_status", 2)
                }
                dialogView.rbStageStatusStageProblemFixed.id -> {
                    presenter.changeFilterStatusPref("filter_stage_status", 3)
                }
                dialogView.rbStageStatusStageFinished.id -> {
                    presenter.changeFilterStatusPref("filter_stage_status", 4)
                }
                dialogView.rbStageStatusStageProblemAndFixAll.id -> {
                    presenter.changeFilterStatusPref("filter_stage_status", 5)
                }
            }

            presenter.showRemontList()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun showStatsFragment(fullListID: List<Int>) {
        val dialog = SendStatsFragment(fullListID)
        dialog.show(supportFragmentManager, "statistic")
    }

    override fun setListData(value: List<RemontListDBModel>) {
        if (!value.isNullOrEmpty()) {
            tvEmptyRemontList.visibility = View.GONE
            rvRenovationList.visibility = View.VISIBLE
            adapter.data = value
        } else {
            rvRenovationList.visibility = View.GONE
            tvEmptyRemontList.visibility = View.VISIBLE
            adapter.clearData()
            rvRenovationList.refreshDrawableState()
        }
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
}
