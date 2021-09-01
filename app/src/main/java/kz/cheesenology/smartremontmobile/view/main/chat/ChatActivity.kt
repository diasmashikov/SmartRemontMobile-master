package kz.cheesenology.smartremontmobile.view.main.chat

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat_actiivty.*
import kz.cheesenology.brigadierapp.view.main.remont.remontlist.remontmenu.chatlist.chat.ChatPhotoType
import kz.cheesenology.brigadierapp.view.main.remont.remontlist.remontmenu.chatlist.chat.ChatPresenter
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.model.ChatMessageListModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.RealPathUtil
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import kotlin.properties.Delegates


class ChatActivity : MvpAppCompatActivity(), ChatView {

    @Inject
    lateinit var presenter: ChatPresenter

    @InjectPresenter
    lateinit var moxyPresenter: ChatPresenter

    @ProvidePresenter
    fun providePresenter() = presenter
    private var adapter: ChatAdapter by Delegates.notNull()

    val filesAttachList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@ChatActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_actiivty)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent!!.getStringExtra("stage_name")

        if (title != "Внутренний чат") {
            layout_chatbox.visibility = View.GONE
        }

        presenter.setIntentData(
            intent!!.getIntExtra("remont_id", 0),
            intent!!.getIntExtra("group_chat_id", 0)
        )

        adapter = ChatAdapter(this@ChatActivity)
        rvChat.adapter = adapter
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setCallback(object : ChatAdapter.Callback {
            override fun onFilesClick(model: ChatPhotoType) {
                val req =
                    DownloadManager.Request(Uri.parse(AppConstant.getServerName() + model.file_url))
                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true)
                    .setTitle(model.file_name)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDescription("")
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        model.file_name
                    )
                val downloadManager: DownloadManager =
                    getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(req)
            }

            override fun onClick(model: ChatType) {
                presenter.checkForFiles(model)
            }
        })

        btnChatAttachFile.setOnClickListener {
            fileSelector()
        }

        btnSendMessage.setOnClickListener {
            if (!etChatMessage.text.isNullOrEmpty())
                presenter.addMessage(etChatMessage.text.toString())
        }
    }

    override fun clearInput() {
        etChatMessage.setText("")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showEmptyView() {
        rvChat.visibility = View.GONE
        tvChatEmptyView.visibility = View.VISIBLE
    }

    override fun setList(it: MutableList<ChatMessageListModel>) {
        tvChatEmptyView.visibility = View.GONE
        rvChat.visibility = View.VISIBLE
        adapter.data = it
        rvChat.scrollToPosition(rvChat.adapter!!.itemCount - 1)
    }

    private fun fileSelector() {
        lateinit var dialog: AlertDialog
        val array = arrayOf("Добавить фото", "Добавить файл")
        val builder = AlertDialog.Builder(this@ChatActivity)
        builder.setSingleChoiceItems(array, -1) { _, which ->
            when (array[which]) {
                "Добавить фото" -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.putExtra("return-data", true)
                    startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        3001
                    )
                }
                "Добавить видео" -> {
                    val intent = Intent()
                    intent.type = "video/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.putExtra("return-data", true)
                    startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        3002
                    )
                }
                "Добавить файл" -> {
                    val intent = Intent()
                    intent.type = "*/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.putExtra("return-data", true)
                    startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        3003
                    )
                }
            }
            dialog.dismiss()
        }
        dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            3001 -> if (resultCode == Activity.RESULT_OK) {
                /*val filePath = data!!.data!!.path
                Toast.makeText(activity, filePath, Toast.LENGTH_LONG).show()*/
                val type = "photo"

                val uriPathList: ArrayList<String> = ArrayList()
                if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        val imagePath = AppConstant.getUriFilePath(imageUri)
                        if (imagePath != null)
                            uriPathList.add(imagePath)
                    }
                    presenter.setAttachFromFile(
                        uriPathList,
                        type,
                        etChatMessage.text.toString()
                    )
                } else if (data?.data != null) {
                    val imageUri = data.data
                    val imagePath = AppConstant.getUriFilePath(imageUri)
                    if (imagePath != null)
                        uriPathList.add(imagePath)

                    presenter.setAttachFromFile(
                        uriPathList,
                        type,
                        etChatMessage.text.toString()
                    )
                }
            }
            3003 -> {
                val type = "file"

                val uriPathList: ArrayList<String> = ArrayList()
                if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val fileUri = data.clipData!!.getItemAt(i).uri
                        val filePath = AppConstant.getUriFilePath(fileUri)
                        if (filePath != null)
                            uriPathList.add(filePath)
                    }
                    presenter.setAttachFromFile(
                        uriPathList,
                        type,
                        etChatMessage.text.toString()
                    )
                } else if (data?.data != null) {
                    val fileUri = data.data
                    val filePath = RealPathUtil.getRealPath(this@ChatActivity, fileUri!!)
                    if (filePath != null)
                        uriPathList.add(filePath)

                    presenter.setAttachFromFile(
                        uriPathList,
                        type,
                        etChatMessage.text.toString()
                    )
                }
            }
        }
    }
}
