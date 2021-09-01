package kz.cheesenology.smartremontmobile.view.request.requestlist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_request_list.*
import kotlinx.android.synthetic.main.dialog_filter_request_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListEntity
import kz.cheesenology.smartremontmobile.model.RequestSendCntModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.view.request.checklist.RequestCheckListActivity
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.properties.Delegates

class RequestListActivity : MvpAppCompatActivity(), RequestListView {

    @Inject
    lateinit var presenterProvider: Provider<RequestListPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    private var adapter: RequestListAdapter by Delegates.notNull()

    private var sendDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@RequestListActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_list)

        title = "Список заявок"

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = RequestListAdapter()
        rvRequestList.adapter = adapter
        val layoutManager = LinearLayoutManager(this@RequestListActivity)
        rvRequestList.layoutManager = layoutManager
        rvRequestList.addItemDecoration(
            DividerItemDecoration(
                this@RequestListActivity,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.requestListViewListener = this

        adapter.setCallback(object : RequestListAdapter.Callback {
            override fun onClick(requestListEntity: requestListType) {
                val intent = Intent(this@RequestListActivity, RequestCheckListActivity::class.java)
                intent.putExtra("client_request_id", requestListEntity.client_request_id)
                startActivity(intent)
            }

            override fun runFlatLink(flatListUrl: String?) {
                if (flatListUrl != null) {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(AppConstant.getServerName() + flatListUrl)
                        )
                    startActivity(browserIntent)
                } else {
                    showToast("Квартирный листок отсутствует")
                }
            }
        })

        val search = findViewById<SearchView>(R.id.searchWidget)
        search.setOnClickListener {
            search.setIconified(false)
        }
        search?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                adapter.getFilter()?.filter(newText)

                return false
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_request_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_request_get_data -> {
                presenter.getRequestDataFromServer()
            }
            R.id.menu_request_send_data -> {
                presenter.checkSendData()
            }
            R.id.menu_request_filter -> {
                presenter.showFilter()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showPB() {
        pbRequestList.visibility = View.VISIBLE
    }

    override fun dismissPB() {
        pbRequestList.visibility = View.GONE
    }

    override fun setRequestList(it: List<RequestListEntity>?) {
        adapter.data = it!!
        if (it != null) {
            if (it.size > 0) {
                title = "Список заявок (${it.size})"
            } else
                title = "Список заявок"
        }
    }

    override fun showToast(s: String?) {
        Toast.makeText(this@RequestListActivity, s, Toast.LENGTH_SHORT).show()
    }

    override fun showRequestFilterList(filterStatus: Int) {
        val dialogBuilder = AlertDialog.Builder(this@RequestListActivity)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter_request_list, null)
        dialogBuilder.setView(dialogView)

        when (filterStatus) {
            1 -> dialogView.rbFilterRequestStatusAll.isChecked = true
            0 -> dialogView.rbFilterRequestStatusUnverified.isChecked = true
            2 -> dialogView.rbFilterRequestStatusRemarks.isChecked = true
        }

        dialogBuilder.setPositiveButton("Фильтровать") { dialog, id ->

            when (dialogView.rgRequestStatus.checkedRadioButtonId) {
                dialogView.rbFilterRequestStatusUnverified.id -> {
                    presenter.changeFilterRequestStatusPref( 0)
                }
                dialogView.rbFilterRequestStatusRemarks.id -> {
                    presenter.changeFilterRequestStatusPref( 2)
                }
                dialogView.rbFilterRequestStatusAll.id -> {
                    presenter.changeFilterRequestStatusPref( 1)
                }
            }
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun showSendDialog(count: RequestSendCntModel) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Отправка данных по ЧО")
        val text = TextView(this)

        val str1 = "Статусы ЧО: ${count.draft_check_cnt}"
        val str2 = "Статусы чек-листов: ${count.check_list_cnt}"
        val str3 = "Фотографии по чек-листам: ${count.check_list_photo_cnt}"
        text.text = """
            Количество данные на отправку:
            $str1
            $str2
            $str3
        """.trimIndent()

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(text)

        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(layout)
        dialogBuilder.setPositiveButton("Отправить", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        sendDialog = dialogBuilder.show()

        sendDialog!!.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                presenter.sendDataToServer()
            }

        sendDialog!!.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                sendDialog?.dismiss()
            }
    }

    override fun closeSendDialog() {
        sendDialog?.dismiss()
    }
}