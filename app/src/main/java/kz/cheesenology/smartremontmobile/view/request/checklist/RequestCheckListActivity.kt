package kz.cheesenology.smartremontmobile.view.request.checklist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_request_check_list.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.model.RequestCheckListRoomModel
import kz.cheesenology.smartremontmobile.model.RequestCheckListSectionModel
import kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus.RequestCheckStatusListActivity
import kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto.RequestCheckListPhotoFixActivity
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.properties.Delegates

class RequestCheckListActivity : MvpAppCompatActivity(), RequestCheckListView {

    @Inject
    lateinit var presenterProvider: Provider<RequestCheckListPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    private var adapter: RequestCheckListAdapter by Delegates.notNull()

    var clientRequestID: Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@RequestCheckListActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_check_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        clientRequestID = intent.getIntExtra("client_request_id", 0)
        title = clientRequestID.toString()
        presenter.setRequestID(clientRequestID!!)


        adapter = RequestCheckListAdapter()
        rvRequestCheckList.adapter = adapter
        val layoutManager = LinearLayoutManager(this@RequestCheckListActivity)
        rvRequestCheckList.layoutManager = layoutManager
        rvRequestCheckList.addItemDecoration(
            DividerItemDecoration(
                this@RequestCheckListActivity,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setCallback(object : RequestCheckListAdapter.Callback {
            override fun onClick(model: RequestCheckListRoomModel) {
                val intent = Intent(
                    this@RequestCheckListActivity,
                    RequestCheckListPhotoFixActivity::class.java
                )
                intent.putExtra("check_id", model.draft_check_list_id)
                intent.putExtra("client_request_id", clientRequestID)
                intent.putExtra("draft_check_id", model.draft_check_list_id)
                startActivity(intent)
            }

            override fun onSwitchChange(
                requestCheckListSectionModel: CheckListType,
                checked: Boolean
            ) {
                presenter.changeCheckAcceptStatus(requestCheckListSectionModel.row!!, checked)
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_request_check_status, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_request_check_status -> {
                val intent = Intent(
                    this@RequestCheckListActivity,
                    RequestCheckStatusListActivity::class.java
                )
                intent.putExtra("client_request_id", clientRequestID)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setData(list: MutableList<RequestCheckListSectionModel>) {
        adapter.data = list
    }
}