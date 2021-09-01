package kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_request_check_list_photo_fix.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoEntitiy
import kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto.camera.CameraXRequestDefectActivity
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.properties.Delegates

class RequestCheckListPhotoFixActivity : MvpAppCompatActivity(), RequestCheckListPhotoFixView {
    @Inject
    lateinit var presenterProvider: Provider<RequestCheckListPhotoFixPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    var checkID: Int? = null
    var clientRequestID: Int? = null
    var draftCheckID: Int? = null

    private var adapter: RequestCheckListPhotoFixAdapter by Delegates.notNull()

    var commentDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@RequestCheckListPhotoFixActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_check_list_photo_fix)

        title = "Фото по чек-листу"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkID = intent.getIntExtra("check_id", 0)
        clientRequestID = intent.getIntExtra("client_request_id", 0)
        draftCheckID = intent.getIntExtra("draft_check_id", 0)
        presenter.setCheckID(checkID!!, draftCheckID!!, clientRequestID!!)

        btnRequestCheckListAddPhoto.setOnClickListener {
            val intent = Intent(
                this@RequestCheckListPhotoFixActivity,
                CameraXRequestDefectActivity::class.java
            )
            intent.putExtra("check_id", checkID)
            intent.putExtra("client_request_id", clientRequestID)
            intent.putExtra("draft_check_id", draftCheckID)
            startActivity(intent)
        }

        adapter = RequestCheckListPhotoFixAdapter()
        rvRequestCheckListPhotoFix.adapter = adapter
        val layoutManager = LinearLayoutManager(this@RequestCheckListPhotoFixActivity)
        rvRequestCheckListPhotoFix.layoutManager = layoutManager
        rvRequestCheckListPhotoFix.addItemDecoration(
            DividerItemDecoration(
                this@RequestCheckListPhotoFixActivity,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setCallback(object : RequestCheckListPhotoFixAdapter.Callback {
            override fun onClick(model: T) {
                //presenter.checkComment(model)
                showCommentDialog(model)
            }
        })
    }

    fun showCommentDialog(model: T) {
        val dialogBuilder = AlertDialog.Builder(this@RequestCheckListPhotoFixActivity)
        dialogBuilder.setMessage("Принятие ЧО")
        val comment = EditText(this@RequestCheckListPhotoFixActivity)
        comment.hint = "Комментарий"

        if (!model.comment.isNullOrEmpty()) {
            comment.setText(model.comment)
        }

        val layout = LinearLayout(this@RequestCheckListPhotoFixActivity)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(comment)

        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(layout)
        dialogBuilder.setPositiveButton("Принять", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        commentDialog = dialogBuilder.show()

        commentDialog!!.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                if (!comment.text.isNullOrEmpty())
                    presenter.setComment(model.requestCheckPhotoID!!, comment.text.toString())
                else {
                    comment.error = "Комментарий не может быть пустым"
                }
            }

        commentDialog!!.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                closeCommentDialog()
            }
    }

    override fun closeCommentDialog() {
        commentDialog?.dismiss()
    }

    override fun setData(it: List<CheckRequestPhotoEntitiy>?) {
        adapter.data = it!!
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}