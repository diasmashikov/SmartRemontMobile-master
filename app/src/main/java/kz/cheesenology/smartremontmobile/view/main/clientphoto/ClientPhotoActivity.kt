package kz.cheesenology.smartremontmobile.view.main.clientphoto

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_client_photo.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.util.color
import kz.cheesenology.smartremontmobile.util.spannable
import kz.cheesenology.smartremontmobile.view.main.camera.PhotoPreviewActivity
import kz.cheesenology.smartremontmobile.view.main.clientphoto.expand.PhotoChildModel
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class ClientPhotoActivity : MvpAppCompatActivity(), ClientPhotoView, BaseClientPhotoAdapter.OnItemClickListener {

    @Inject
    lateinit var presenter: ClientPhotoPresenter

    @InjectPresenter
    lateinit var moxyPresenter: ClientPhotoPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    var REQUEST_IMAGE_CAPTURE = 1003
    var mCurrentPhotoPath: String? = null

    private var cameraAdapter: ClientPhotoAdapter by Delegates.notNull()
    private var clientPhotoAdapter: BaseClientPhotoAdapter by Delegates.notNull()

    var myList: ArrayList<PhotoChildModel> = ArrayList()
    var dialogAddPhoto: AlertDialog? = null

    var selectedRoom: String? = null
    var movieComparator: Comparator<PhotoChildModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@ClientPhotoActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_photo)

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        title = "Фото для клиента"

        presenter.setIntentData(intent!!.getIntExtra("remont_id", 0))

        /*btnClientCapturePhoto.setOnClickListener {
            presenter.captureImage(packageManager, getExternalFilesDir(Environment.DIRECTORY_PICTURES), spinner.selectedItem.toString())
        }*/

        myList.add(PhotoChildModel(
                false,
                "http://smremont.cheesenology.kz/documents/standart_photo/standart_photo_check_list_id_405b4de577147794.56160242.png",
                null,
                null,
                "Кухня"
        ))
        myList.add(PhotoChildModel(
                false,
                "http://smremont.cheesenology.kz/documents/standart_photo/standart_photo_check_list_id_405b4de577147794.56160242.png",
                null,
                null,
                "Спальня"
        ))
        myList.add(PhotoChildModel(
                false,
                "http://smremont.cheesenology.kz/documents/standart_photo/standart_photo_check_list_id_405b4de577147794.56160242.png",
                null,
                null,
                "Туалет"
        ))
        myList.add(PhotoChildModel(
                false,
                "http://smremont.cheesenology.kz/documents/standart_photo/standart_photo_check_list_id_405b4de577147794.56160242.png",
                null,
                null,
                "Кухня"
        ))
        myList.add(PhotoChildModel(
                false,
                "http://smremont.cheesenology.kz/documents/standart_photo/standart_photo_check_list_id_405b4de577147794.56160242.png",
                null,
                null,
                "Спальня"
        ))
        myList.add(PhotoChildModel(
                false,
                "http://smremont.cheesenology.kz/documents/standart_photo/standart_photo_check_list_id_405b4de577147794.56160242.png",
                null,
                null,
                "Кухня"
        ))

        var gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        rvClientPhoto.layoutManager = gridLayoutManager
        movieComparator = Comparator { o1: PhotoChildModel, o2: PhotoChildModel -> o1.roomName!!.compareTo(o2.roomName!!) }
        Collections.sort(myList, movieComparator)
        clientPhotoAdapter = ClientSectionAdapter(myList)
        clientPhotoAdapter.setGridLayoutManager(gridLayoutManager)
        clientPhotoAdapter.setOnItemClickListener(this@ClientPhotoActivity)
        rvClientPhoto.adapter = clientPhotoAdapter

        fabAddClientPhoto.setOnClickListener {
            addPhotoDialog()
        }
    }

    private fun addPhotoDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_client_photo, null)
        val spinner = dialogView.findViewById(R.id.spinnerClientRooms) as Spinner
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                clientPhotoAdapter.photoList
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = aa

        val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(spannable { color(Color.parseColor("#1B5E20"), "Выберите комнату: ") })
                .setPositiveButton("Добавить", null)
                .setNegativeButton("Отменить", null)

        dialogAddPhoto = builder.show()
        dialogAddPhoto!!.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            selectedRoom = spinner.selectedItem.toString()
            presenter.captureImage(packageManager, getExternalFilesDir(Environment.DIRECTORY_PICTURES), spinner.selectedItem.toString())
        }
    }

    override fun onItemClicked(model: PhotoChildModel, mImgPathList: List<String>, position: Int) {
        var intent = Intent(this@ClientPhotoActivity, PhotoPreviewActivity::class.java)
        intent.putStringArrayListExtra("path_list", ArrayList(mImgPathList) as ArrayList<String>?)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    override fun deleteServerItem(model: PhotoChildModel) {
        //presenter.deletePhotoFromServer()
    }

    override fun deleteUserItem(model: PhotoChildModel) {
        val file = File(model.imgPath)

        if (file.exists()) {
            file.delete()
        }

        val index = myList.indexOf(model)
        myList.remove(model)
        clientPhotoAdapter.notifyItemRemovedAtPosition(index)
    }

    override fun onSubheaderClicked(position: Int) {
        clientPhotoAdapter.getSectionIndex(position)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun startCameraIntent(takePictureIntent: Intent, photoFile: File, selectRoom: String) {
        dialogAddPhoto!!.dismiss()
        val photoURI = FileProvider.getUriForFile(this,
                "kz.cheesenology.android.fileprovider",
                photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentPhotoPath),
                    128, 128)
            presenter.scaleBitmapResult(windowManager, mCurrentPhotoPath, thumbImage, selectedRoom.toString())
            Toast.makeText(this@ClientPhotoActivity, selectedRoom.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun setImagePath(absolutePath: String?) {
        mCurrentPhotoPath = absolutePath
    }

    override fun setCapturedPhoto(sBitmap: Bitmap, lBitmap: Bitmap?, mCurrentPhotoPath: String?, selectRoom: String) {
        //cameraAdapter.singleData = CameraListModel(true, sBitmap!!, lBitmap!!, mCurrentPhotoPath!!, "", -1)
        var model = PhotoChildModel(
                true,
                null,
                lBitmap,
                mCurrentPhotoPath,
                selectedRoom)

        for (i in myList.indices) {
            if (movieComparator!!.compare(myList[i], model) >= 0) {
                myList.add(i, model)
                clientPhotoAdapter.notifyItemInsertedAtPosition(i)
                return
            }
        }
        myList.add(myList.size, model)
        clientPhotoAdapter.notifyItemInsertedAtPosition(myList.size - 1)
    }
}
