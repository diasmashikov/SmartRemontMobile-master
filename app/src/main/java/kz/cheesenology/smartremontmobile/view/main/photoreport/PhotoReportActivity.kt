package kz.cheesenology.smartremontmobile.view.main.photoreport

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_defect_list.*
import kotlinx.android.synthetic.main.activity_photo_report.*
import kotlinx.android.synthetic.main.activity_request_check_status_list.*
import kotlinx.android.synthetic.main.activity_stages.*
import kotlinx.android.synthetic.main.fragment_request_check_status_list_view_pager.*
import kotlinx.android.synthetic.main.view_audio_player.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity
import kz.cheesenology.smartremontmobile.model.ChatMessageModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.view.camerax.CameraPhotoReportActivity
import kz.cheesenology.smartremontmobile.view.fragmentpreview.PhotoPreviewFragment
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates


class PhotoReportActivity : MvpAppCompatActivity(), PhotoReportView {

    @Inject
    lateinit var presenter: PhotoReportPresenter

    @InjectPresenter
    lateinit var moxyPresenter: PhotoReportPresenter

    @ProvidePresenter
    fun providePresenter() = presenter


    private var adapter: PhotoReportAdapter by Delegates.notNull()

    private var showPhotoReportsDialog: AlertDialog? = null
    var dialogPhotosListAdapter: DialogPhotoReportPhotosAdapter by Delegates.notNull()

    var messageChatID: Long? = null
    var lastPosition: Int? = null
    var stage_id: Int? = null
    var datePhotoReportChat: String? = null

    val layoutManager: LinearLayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@PhotoReportActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_report)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Фотоотчет"

        val remont_id = intent!!.getIntExtra("remont_id", 0)
        stage_id = intent!!.getIntExtra("active_stage_id", 0)

        presenter.photoReportViewListener = this

        presenter.setIntentData(
                remont_id,
                stage_id!!
        )




        adapter = PhotoReportAdapter()
        rvPhotoReportList.adapter = adapter
        rvPhotoReportList.layoutManager = layoutManager
        rvPhotoReportList.addItemDecoration(
                DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                )
        )

        // so this method collects all the pictures we have made as a list
        adapter.setCallback(object : PhotoReportAdapter.Callback {


            override fun onFileClicked(chatMessageModel: T, chatMessageID: Int?, rvPositionItem: Int, comment: String?, dateChat: String?, remontID: Int) {
                lastPosition = rvPositionItem
                messageChatID = chatMessageID!!.toLong()
                datePhotoReportChat = dateChat
                if (chatMessageID != null) {
                    presenter.getPhotoFiles(chatMessageID, comment)
                    if (dateChat != null) {
                        presenter.updatePhotoReportDateSend(dateChat, remontID)
                    }

                }
            }

            override fun deletePhotoReport(chatMessageID: Int?) {
                if (chatMessageID != null) {
                    presenter.getPhotoFilesToDelete(chatMessageID)
                    presenter.deletePhotoReportFiles(chatMessageID)
                    presenter.deletePhotoReport(chatMessageID)
                }
            }

        })

        btnAddPhotoReport.setOnClickListener {
            lastPosition = null
            presenter.savePhotoReport()

            val intent = Intent(this@PhotoReportActivity, CameraPhotoReportActivity::class.java)
            intent.putExtra("remont_id", getIntent().getIntExtra("remont_id", 0))
            intent.putExtra("stage_id", getIntent().getIntExtra("active_stage_id", 0))
            intent.putExtra("camera_mode", "photo")
            intent.putExtra("address", getIntent().getStringExtra("address"))
            messageChatID?.let { it1 -> intent.putExtra("chat_message_id_key", it1.toInt()) }
            startActivity(intent)
        }


    }

    override fun deletePhotoFiles(list: List<StageChatFileEntity>) {
        for (item in list) {
            File(AppConstant.FULL_MEDIA_PHOTO_PATH + item.file_name).delete()
        }
    }


    override fun showPhotosDialog(list: List<StageChatFileEntity>, comment: String?) {


        var array_paths: MutableList<String?> = arrayListOf()
        var array_files_to_bitmaps: MutableList<File?> = arrayListOf()
        var hashMapaOfActiveStages: HashMap<Int, String> = hashMapOf(3 to "Раскидка кабелей, разводка канализации и сантехники",
                4 to "Выравнивание стен, потолков, экопол, плитка, галтели, покарска потолка",
                5 to "Ламинат, обои, двери, розетки, плинтус , санфаянс")
        for (photopath in list) {

            var file_path = AppConstant.FULL_MEDIA_PHOTO_PATH + photopath.file_name
            array_paths.add(file_path)
            var file_to_bitmapa = File(file_path)
            if (file_to_bitmapa != null) {
                array_files_to_bitmaps.add(file_to_bitmapa)
            }


        }


        var dialog_naming = "Просмотр фотографий"

        supportActionBar?.hide()

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(dialog_naming)
        val dialogView = layoutInflater.inflate(R.layout.dialog_photo_report, null)

        val etComment = dialogView.findViewById<EditText>(R.id.etDialogPhotoReport)

        val rvTakenPictures = dialogView.findViewById<RecyclerView>(R.id.rvPhotoReportPhotos)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(dialogView)

        dialogPhotosListAdapter = DialogPhotoReportPhotosAdapter(array_files_to_bitmaps, array_paths)
        if (comment == "Фотоотчёт") {

        } else {

            when (stage_id) {
                3 -> {
                    val sub_string = comment?.substring(72)
                    val new_comment = comment?.replace(comment, sub_string!!)
                    etComment.setText(new_comment)
                }
                4 -> {
                    val sub_string = comment?.substring(91)
                    val new_comment = comment?.replace(comment, sub_string!!)
                    etComment.setText(new_comment)
                }
                5 -> {
                    val sub_string = comment?.substring(70)
                    val new_comment = comment?.replace(comment, sub_string!!)
                    etComment.setText(new_comment)
                }
            }

        }


        //dialogPhotoListAdapter.requestCheckStatusListViewListener = this

        dialogPhotosListAdapter.photoReportViewListener = this

        rvTakenPictures.adapter = dialogPhotosListAdapter
        val layoutManager = GridLayoutManager(
                this@PhotoReportActivity,
                3,
                LinearLayoutManager.VERTICAL,
                false
        )
        rvTakenPictures.setHasFixedSize(true)
        rvTakenPictures.layoutManager = layoutManager


        dialogBuilder.setCancelable(true)

        dialogBuilder.setNegativeButton("Отмена", null)
        dialogBuilder.setPositiveButton("Подтвердить", null)

        showPhotoReportsDialog = dialogBuilder.show()


        showPhotoReportsDialog!!.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener {
                    showPhotoReportsDialog!!.dismiss()
                    supportActionBar?.show()
                }

        showPhotoReportsDialog!!.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    var stage = hashMapaOfActiveStages[stage_id]

                    presenter.updateMessage("Фотоотчёт | Этап: $stage " + "\n" + "\n" + etComment.text.toString(), messageChatID!!.toInt())
                    showPhotoReportsDialog!!.dismiss()
                    supportActionBar?.show()

                }

        showPhotoReportsDialog!!.setOnDismissListener {
            if(flPhotoReportViewPager.visibility == View.VISIBLE){

            }else{
                supportActionBar?.show()
            }

        }


    }


    override fun showViewPager(
            picture_position: Int,
            files_to_bitmaps: MutableList<File?>,
            imagesPath: MutableList<String?>
    ) {

        var bitmaps: MutableList<Bitmap?> = arrayListOf()


        btnAddPhotoReport.visibility = View.GONE
        rvPhotoReportList.visibility = View.GONE


        val fragmentViewPager = PhotoPreviewFragment(
                imagesPath,
                picture_position, bitmaps, 3, files_to_bitmaps
        )

        fragmentViewPager.photoReportViewListener = this
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        showPhotoReportsDialog?.dismiss()

        // creates a viewpager
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flPhotoReportViewPager, fragmentViewPager)
            commit()
        }


        flPhotoReportViewPager.visibility = View.VISIBLE


        /*
        Timer().schedule(object : TimerTask() {

            override fun run() {
                this@RequestCheckStatusListActivity.runOnUiThread(Runnable {

                    btnRequestCheckAccept.visibility = View.GONE
                    btnRequestCheckReject.visibility = View.GONE
                    rvRequestCheckHistory.visibility = View.GONE
                    flViewPagerView.visibility = View.VISIBLE
                })
            }


        }, 300)

         */

    }


    override fun closeViewPager(image_position: Int) {


        rvPhotoReportList.visibility = View.VISIBLE
        btnAddPhotoReport.visibility = View.VISIBLE

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportActionBar?.show()

        flPhotoReportViewPager.visibility = View.GONE
        //view_pager.visibility = View.GONE
        //btnCloseViewPager2.visibility = View.GONE


        if (lastPosition != null) {
            Handler().postDelayed(Runnable {
                rvPhotoReportList.findViewHolderForAdapterPosition(lastPosition!!)?.itemView!!.performClick()
            }, 1)
        }


    }


    override fun showToast(s: String) {
        Toast.makeText(this@PhotoReportActivity, s, Toast.LENGTH_SHORT).show()
    }

    override fun getChatMessageID(chatMessageID: Long) {
        messageChatID = chatMessageID
    }


    override fun onRestart() {
        super.onRestart()


        /*adapter.notifyDataSetChanged()
        Handler().postDelayed(Runnable { rvPhotoReportList.
        findViewHolderForAdapterPosition(adapter.itemCount - 1)?.
        itemView!!.
        findViewById<ImageView>(R.id.ivItemPhotoReportListImage).
        performClick() }, 1)

*/

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode != Activity.RESULT_OK) {
                return
            }

        }

    }


    override fun setPhotoReportList(it: List<ChatMessageModel>, chat_ID: Long) {
        adapter.data = it


    }

}