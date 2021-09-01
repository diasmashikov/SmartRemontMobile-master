import kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus.*



import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_request_check_status_list.*
import kotlinx.android.synthetic.main.dialog_accept_draft_status.*
import kotlinx.android.synthetic.main.fragment_request_check_status_list_view_pager.*
import kotlinx.android.synthetic.main.item_accept_draft_photo.*
import kotlinx.android.synthetic.main.item_request_draft_history.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListEntity
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryEntity
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusEntity
import kz.cheesenology.smartremontmobile.util.ImageFilesRotatorAndResizer
import kz.cheesenology.smartremontmobile.util.PrefUtils
import kz.cheesenology.smartremontmobile.view.fragmentpreview.PhotoPreviewFragment
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.properties.Delegates


class RequestCheckStatusListActivity : MvpAppCompatActivity(), RequestCheckStatusListView, PhotoPreviewFragment.OnCallbackReceived {

    // androidx.appcompat.widget.SearchView.OnQueryTextListener

    @Inject
    lateinit var presenterProvider: Provider<RequestCheckStatusListPresenter>
    lateinit var progressDialog: ProgressDialog
    private val presenter by moxyPresenter { presenterProvider.get() }

    private var adapter: RequestCheckStatusListAdapter by Delegates.notNull()
    private var searchTestAdapter: searchTestAdapter by Delegates.notNull()

    private var acceptRequestDialog: AlertDialog? = null
    private var rejectRequestDialog: AlertDialog? = null
    private var showPicturesDialog: AlertDialog? = null

    var ivDialogRejectPhoto: ImageView? = null

    var dialogBitmapList: MutableList<Bitmap?> = mutableListOf()
    var dialogPhotoPathList: MutableList<String?> = arrayListOf()
    var dialogBitmapListTemp: MutableList<Bitmap?> = mutableListOf()
    var dialogPhotoPathListTemp: MutableList<String?> = arrayListOf()
    var rotatedBitmapToFiles: MutableList<File?> = mutableListOf()
    var dialogPhotoPathBufferList: MutableList<String?> = arrayListOf()
    var dialogPhotoBitmapBufferList: MutableList<Bitmap?> = mutableListOf()

    var currentPhotoPath: String? = null
    var exit_type: Int = 1
    var load_sign: Int = 1
    var dialog_type: String = ""

    var draftID: Int? = null
    var et_okk_comment: String? = ""
    var dialogPhotoListAdapter: DialogPhotoDraftStatusAdapter by Delegates.notNull()
    var vpPicturePosition: Int? = null

    val REQUEST_IMAGE_CAPTURE = 1010

    var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@RequestCheckStatusListActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_check_status_list)

        title = "Статус ЧО"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.setIntentData(intent.getIntExtra("client_request_id", 0))

        adapter = RequestCheckStatusListAdapter()
        rvRequestCheckHistory.adapter = adapter
        val layoutManager = LinearLayoutManager(this@RequestCheckStatusListActivity)
        rvRequestCheckHistory.layoutManager = layoutManager
        rvRequestCheckHistory.addItemDecoration(
            DividerItemDecoration(
                this@RequestCheckStatusListActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        adapter.setCallback(object : RequestCheckStatusListAdapter.Callback {
            override fun onClick(requestListEntity: requestCheckType) {

            }
        })

        adapter.requestCheckStatusListAdapterViewListener = this

        /*

        //TestingSectioOfSearch

        searchTestAdapter = searchTestAdapter()
        rvSearchTest.adapter = searchTestAdapter
        val layoutManagerSearch = LinearLayoutManager(this@RequestCheckStatusListActivity)
        rvSearchTest.layoutManager = layoutManagerSearch
        presenter.getData()

        //searchTestAdapter.requestCheckStatusListViewListener = this


        btnRequestCheckReject.setOnClickListener {
            showRequestRejectDialog()
        }

        btnRequestCheckAccept.setOnClickListener {
            showRequestAcceptDialog()
        }

        rvRequestCheckHistory.visibility = View.GONE
        btnRequestCheckReject.visibility = View.GONE
        btnRequestCheckAccept.visibility = View.GONE

         */

        btnRequestCheckReject.setOnClickListener {
            showRequestRejectDialog()
        }

        btnRequestCheckAccept.setOnClickListener {
            showRequestAcceptDialog()
        }





    }

    override fun getDataFromDB(list: List<RequestListEntity>) {

        searchTestAdapter.data = list

    }

    private fun showRequestRejectDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Имеются замечания")

        val dialogView = layoutInflater.inflate(R.layout.dialog_reject_draft, null)
        dialogBuilder.setView(dialogView)

        val btnAnotherCall = dialogView.findViewById<Button>(R.id.btnSetFutureDateCall)
        val etComment = dialogView.findViewById<EditText>(R.id.etDialogRejectDraft)
        ivDialogRejectPhoto = dialogView.findViewById<ImageView>(R.id.ivDialogRejectDraft)
        etComment.hint = "Комментарий"

        var selectedDate: String? = null

        btnAnotherCall.setOnClickListener {
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val month: Int = cal.get(Calendar.MONTH)
            val year: Int = cal.get(Calendar.YEAR)
            var picker = DatePickerDialog(
                this, { view, year, monthOfYear, dayOfMonth ->

                    val dayStr = when {
                        dayOfMonth < 10 -> {
                            "0${dayOfMonth}"
                        }
                        else -> {
                            dayOfMonth.toString()
                        }
                    }

                    var month = monthOfYear + 1
                    val monthStr = when {
                        month < 10 -> {
                            "0${month}"
                        }
                        else -> {
                            month.toString()
                        }
                    }

                    selectedDate = "$dayStr.$monthStr.$year"

                    btnAnotherCall.text = "$dayStr.$monthStr.$year"

                },
                year,
                month,
                day
            )
            picker.datePicker.minDate = Date().time
            picker.show()
        }

        ivDialogRejectPhoto?.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            applicationContext.packageName + ".fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }

        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton("Подтвердить", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        rejectRequestDialog = dialogBuilder.show()

        rejectRequestDialog!!.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                if (currentPhotoPath != null) {
                    if (selectedDate.isNullOrEmpty()) {
                        showToast("Не выбрана дата повторного выбора")
                    } else {
                        presenter.rejectRequest(
                            currentPhotoPath,
                            etComment.text.toString(),
                            selectedDate
                        )
                    }
                } else {
                    showToast("Прикрепите дефектный акт")
                }
            }

        rejectRequestDialog!!.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                closeRejectDialog()
            }
    }

    override fun showToast(s: String) {
        Toast.makeText(this@RequestCheckStatusListActivity, s, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {

        var mob = Environment.getExternalStorageDirectory().absoluteFile
        mob = File(mob, "/DCIM/SmartRemont/Photo/")
        if (!mob.exists()) {
            mob.mkdirs()
        }

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            mob
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var bitmaped_image = ImageFilesRotatorAndResizer.handleSamplingAndRotationBitmap(
            applicationContext, File(
                currentPhotoPath
            ).toUri()
        )
        dialogBitmapList.add(bitmaped_image)
        dialogBitmapListTemp.add(bitmaped_image)
        if(acceptRequestDialog != null)
        {
            dialogPhotoListAdapter.notifyDataSetChanged()
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val thumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(currentPhotoPath),
                128, 128
            )
            ivDialogRejectPhoto?.setImageBitmap(thumbImage)
        }

        io.reactivex.Observable.fromArray(bitmaped_image)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ bitmapa ->
                var rotatedBitmapToFile =
                    dialogPhotoPathList[dialogPhotoPathList.size - 1]?.let {
                        ImageFilesRotatorAndResizer.bitmapToFile(
                            applicationContext,
                            bitmapa,
                            it
                        )
                    }
                rotatedBitmapToFiles.add(rotatedBitmapToFile)
            }

        /*


         */
    }

    override fun closeViewPagerFromDialog(image_position: Int) {

        vpPicturePosition = image_position
        rvRequestCheckHistory.visibility = View.VISIBLE

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportActionBar?.show()

        flViewPagerView.visibility = View.GONE
        view_pager.visibility = View.GONE
        btnCloseViewPager2.visibility = View.GONE

        btnRequestCheckAccept.performClick()
        btnRequestCheckAccept.visibility = View.VISIBLE
        btnRequestCheckReject.visibility = View.VISIBLE
    }

    override fun closeViewPagerFromList(image_position: Int)
    {
        rvRequestCheckHistory.visibility = View.VISIBLE

        flViewPagerView.visibility = View.GONE
        view_pager.visibility = View.GONE
        btnCloseViewPager2.visibility = View.GONE

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportActionBar?.show()


        btnRequestCheckAccept.visibility = View.VISIBLE
        btnRequestCheckReject.visibility = View.VISIBLE
        rvRequestCheckHistory.visibility = View.VISIBLE

        getPhotoUrls(draftID, et_okk_comment)
    }

    override fun getPhotoUrls(clientRequestDraftCheckHistoryId: Int?, okk_comment: String?) {
        draftID = clientRequestDraftCheckHistoryId
        et_okk_comment = okk_comment
        presenter.getPhotoUrlsFromDB(clientRequestDraftCheckHistoryId)
    }

    override fun showPicturesDialog(PhotoURLs: List<PhotoDraftStatusEntity>){

        var dialog_naming = "Просмотр фотографий"

        supportActionBar?.hide()

        exit_type = 0

        dialog_type = dialog_naming

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(dialog_naming)
        val dialogView = layoutInflater.inflate(R.layout.dialog_accept_draft_status, null)

        val btnMakePhoto = dialogView.findViewById<Button>(R.id.btnMakePhoto)
        val etComment = dialogView.findViewById<EditText>(R.id.etDialogAcceptDraft)

        btnMakePhoto.visibility = View.GONE
        etComment.setText(et_okk_comment)

        val rvTakenPictures = dialogView.findViewById<RecyclerView>(R.id.rvAcceptPhotos)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(dialogView)



        if(!dialogPhotoPathList.isEmpty() && !dialogBitmapList.isEmpty() && load_sign == 1)
        {

            load_sign = 0
            dialogPhotoPathBufferList = dialogPhotoPathList
            dialogPhotoBitmapBufferList = dialogBitmapList

           // showToast(dialogPhotoPathBufferList.toString() + "BOL" + "$load_sign")

            //dialogPhotoPathList.clear()
            //dialogBitmapList.clear()
        }
        else if(!dialogPhotoPathList.isEmpty() && !dialogBitmapList.isEmpty() && load_sign == 0)
        {

            load_sign = 0
            //dialogPhotoPathBufferList = dialogPhotoPathList
            //dialogPhotoBitmapBufferList = dialogBitmapList
            //showToast(dialogPhotoPathBufferList.toString() +"SOL" + "$load_sign")
        }

        if(dialogPhotoPathList.size < PhotoURLs.size && load_sign == 1) {
            for(photoURL in PhotoURLs)
            {
                var bitmapFile = photoURL.photo_url?.let { ImageFilesRotatorAndResizer.handleSamplingAndRotationBitmap(
                    applicationContext, File(
                        it
                    ).toUri()
                ) }
                dialogPhotoPathList.add(photoURL.photo_url)
                dialogBitmapList.add(bitmapFile)

                dialogPhotoPathBufferList = dialogPhotoPathList
                dialogPhotoBitmapBufferList = dialogBitmapList



            }


            load_sign = 0

            dialogPhotoListAdapter = DialogPhotoDraftStatusAdapter(
                dialogBitmapList,
                dialogPhotoPathList
            )

        }
        else if(load_sign == 0){


            //showToast(dialogPhotoBitmapBufferList.toString() + "KOL")
           dialogPhotoListAdapter = DialogPhotoDraftStatusAdapter(dialogPhotoBitmapBufferList,
               dialogPhotoPathBufferList)
        }
        else
        {

        }

        dialogPhotoListAdapter.requestCheckStatusListViewListener = this

        rvTakenPictures.adapter = dialogPhotoListAdapter
        val layoutManager = GridLayoutManager(
            this@RequestCheckStatusListActivity,
            3,
            LinearLayoutManager.VERTICAL,
            false
        )
        rvTakenPictures.setHasFixedSize(true)
        rvTakenPictures.layoutManager = layoutManager

        dialogBuilder.setNegativeButton("Отмена", null)

        showPicturesDialog = dialogBuilder.show()

        showPicturesDialog!!.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener {
                    showPicturesDialog!!.dismiss()
                    supportActionBar?.show()
                    if(load_sign == 1)
                    {
                        dialogPhotoPathList.clear()
                        dialogBitmapList.clear()
                        dialogBitmapList = mutableListOf()
                        dialogPhotoPathList = arrayListOf()
                    }
                    else
                    {

                    }
                    dialogPhotoListAdapter = DialogPhotoDraftStatusAdapter(
                        dialogBitmapListTemp,
                        dialogPhotoPathListTemp,
                    )
                    dialogPhotoListAdapter.notifyDataSetChanged()
                }

        /*
       if(rotatedBitmapToFiles.isEmpty())
       {


           var loadingObservable = io.reactivex.Observable.fromArray(dialogBitmapList)
               .subscribeOn(Schedulers.newThread())
               .observeOn(AndroidSchedulers.mainThread())
               loadingObservable.subscribe{object: Subscriber<Boolean>{

                   override fun onSubscribe(s: Subscription?) {
                       for((index, bitmap) in it.withIndex()){
                           var rotatedBitmapToFile =
                               dialogPhotoPathList[index]?.let {
                                   ImageFilesRotatorAndResizer.bitmapToFile(
                                       applicationContext,
                                       bitmap,
                                       it
                                   )
                               }
                           rotatedBitmapToFiles.add(rotatedBitmapToFile)
                       }
                   }

                   override fun onNext(t: Boolean?) {


                   }

                   override fun onError(t: Throwable?) {
                   }

                   override fun onComplete() {
                       pbShowPicturesDialog.visibility = View.GONE
                       tvOfPbShowPicturesDialog.visibility = View.GONE
                   }

               }
               }



       }
       else
       {
           pbShowPicturesDialog.visibility = View.GONE
           tvOfPbShowPicturesDialog.visibility = View.GONE
       }

            */

        // рабочий метод, но onComplete не работает
        if(rotatedBitmapToFiles.isEmpty())
        {
            /*
            io.reactivex.Observable.fromArray(dialogBitmapList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete{}
                .subscribe{ bitmapList ->
                    for((index, bitmap) in bitmapList.withIndex())
                    {
                        var rotatedBitmapToFile =
                            dialogPhotoPathList[index]?.let {
                                ImageFilesRotatorAndResizer.bitmapToFile(
                                    applicationContext,
                                    bitmap,
                                    it
                                )
                            }
                        rotatedBitmapToFiles.add(rotatedBitmapToFile)
                    }

                }

             */


            for(path in dialogPhotoPathList)
            {

                rotatedBitmapToFiles.add(File(path))
            }
            showToast("KARAZHOL")

        }
        else
        {
            //showToast(dialogPhotoPathList[0].toString())
        }




    }

    private fun showRequestAcceptDialog() {
        val dialog_naming = "Принятие ЧО"
        exit_type = 1
        dialog_type = dialog_naming
        supportActionBar?.hide()

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(dialog_naming)


        val dialogView = layoutInflater.inflate(R.layout.dialog_accept_draft_status, null)
        val btnMakePhoto = dialogView.findViewById<Button>(R.id.btnMakePhoto)
        val etComment = dialogView.findViewById<EditText>(R.id.etDialogAcceptDraft)

        val rvTakenPictures = dialogView.findViewById<RecyclerView>(R.id.rvAcceptPhotos)

        etComment.hint = "Комментарий"

        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(dialogView)

       // dialogBitmapList = dialogBitmapListTemp
        // dialogPhotoPathList = dialogPhotoPathListTemp

        dialogPhotoListAdapter = DialogPhotoDraftStatusAdapter(dialogBitmapListTemp, dialogPhotoPathListTemp)

        dialogPhotoListAdapter.requestCheckStatusListViewListener = this

        rvTakenPictures.adapter = dialogPhotoListAdapter
        val layoutManager = GridLayoutManager(
            this@RequestCheckStatusListActivity,
            3,
            LinearLayoutManager.VERTICAL,
            false
        )
        rvTakenPictures.setHasFixedSize(true)
        rvTakenPictures.layoutManager = layoutManager

        btnMakePhoto?.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        null
                    }

                    dialogPhotoPathList.add(currentPhotoPath)
                    dialogPhotoPathListTemp.add(currentPhotoPath)

                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            applicationContext.packageName + ".fileprovider",
                            it
                        )

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }

                }

            }

            //showToast(listOfPhotoPaths.toString())
        }

        /*btnRequestDeletePicture.setOnClickListener {
            deletePictureFromViewPager(view_pager.currentItem)
        }*/




        dialogBuilder.setPositiveButton("Подтвердить", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        acceptRequestDialog = dialogBuilder.show()

        acceptRequestDialog!!.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                if (currentPhotoPath != null) {
                    presenter.acceptRequest(etComment.text.toString(), dialogPhotoPathListTemp)
                    dialogPhotoPathListTemp = arrayListOf()
                    dialogBitmapListTemp = arrayListOf()
                    supportActionBar?.show()
                } else {
                    showToast("Прикрепите фото договора")
                }
            }

        acceptRequestDialog!!.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                closeAccpeptDialog()
                supportActionBar?.show()


                /*
                dialogPhotoPathList.clear()
                dialogBitmapList.clear()
                dialogBitmapList = mutableListOf()
                dialogPhotoPathList = arrayListOf()
                dialogPhotoListAdapter = DialogPhotoDraftStatusAdapter(dialogBitmapListTemp, dialogPhotoPathListTemp)
                dialogPhotoListAdapter.notifyDataSetChanged()

                 */
            }


    }


    override fun showViewPager(
        picture_position: Int,
        bitmaps: MutableList<Bitmap?>,
        imagesPath: MutableList<String?>
    ) {

        if(dialog_type == "Принятие ЧО")
        {
            val fragmentViewPager = PhotoPreviewFragment(
                imagesPath,
                picture_position, bitmaps, exit_type, rotatedBitmapToFiles
            )

            fragmentViewPager.requestCheckStatusListViewListener = this
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            closeAccpeptDialog()
            showPicturesDialog?.dismiss()

            // creates a viewpager
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flViewPagerView, fragmentViewPager)
                commit()
            }

            flViewPagerView.visibility = View.INVISIBLE

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



        }
        else if(dialog_type == "Просмотр фотографий")
        {

            val fragmentViewPager = PhotoPreviewFragment(
                dialogPhotoPathList,
                picture_position, dialogBitmapList, exit_type, rotatedBitmapToFiles
            )
            fragmentViewPager.requestCheckStatusListViewListener = this

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            closeAccpeptDialog()
            showPicturesDialog?.dismiss()



            // creates a viewpager
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flViewPagerView, fragmentViewPager)
                commit()

            }


            flViewPagerView.visibility = View.INVISIBLE

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






        }




    }

    override fun closeAccpeptDialog() {
        if(flViewPagerView.visibility == View.VISIBLE)
        {

        }
        else
        {
            supportActionBar?.hide()
        }
        acceptRequestDialog?.dismiss()
    }

    override fun closeRejectDialog() {
        rejectRequestDialog?.dismiss()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()

        /*
        when (flViewPagerView.visibility) {
            View.VISIBLE -> {
                vpPicturePosition?.let { closeViewPager(it) }
            }
            View.GONE -> {
                super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }

         */



        //if(flViewPagerView.visibility == View.VISIBLE && exit_type == 1){


           // vpPicturePosition?.let { closeViewPagerFromDialog(it) }

           


        // } // && searchView.isIconified == true
        //if (flViewPagerView.visibility == View.VISIBLE && exit_type == 0)
        //{


            //vpPicturePosition?.let { closeViewPagerFromList(it) }
            //btnCloseViewPager2.visibility = View.GONE
          //  showPicturesDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.performClick()




        //}
        //if(flViewPagerView.visibility == View.GONE){

        //}
       //  else if(!searchView.isIconified() && flViewPagerView.visibility == View.GONE){
        //            searchView.setIconified(true)
        //        }
      // else {
        //    super.onBackPressed()
        //}



    }

    override fun deleteFilePath(filePath: String?, position: Int) {
        dialogPhotoPathListTemp.remove(filePath)
        dialogBitmapListTemp.removeAt(position)

        showToast(dialogPhotoPathListTemp.size.toString())
    }

    override fun setData(it: List<RequestCheckListHistoryEntity>?) {
        adapter.data = it!!
    }

    override fun Update(image_position: Int) {
        vpPicturePosition = image_position

    }

}