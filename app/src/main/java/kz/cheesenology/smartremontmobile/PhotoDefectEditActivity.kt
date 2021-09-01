package kz.cheesenology.smartremontmobile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import kotlinx.android.synthetic.main.activity_photo_defect_edit.*
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File


class PhotoDefectEditActivity : AppCompatActivity() {

    var mPhotoEditor: PhotoEditor? = null

    var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_defect_edit)

        imageFile = File(AppConstant.FULL_MEDIA_PHOTO_PATH, intent.getStringExtra("file"))

        photoEditorView.source.setImageURI(Uri.fromFile(imageFile))

        mPhotoEditor = PhotoEditor.Builder(this, photoEditorView)
            .setPinchTextScalable(true)
            .build()

        mPhotoEditor!!.setBrushDrawingMode(true)
        mPhotoEditor!!.brushSize = 4f
        mPhotoEditor!!.brushColor = Color.RED
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_photo_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_photo_edit_redo -> {
                mPhotoEditor?.redo()
                true
            }
            R.id.menu_photo_edit_undo -> {
                mPhotoEditor?.undo()
                true
            }
            R.id.menu_photo_edit_save -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mPhotoEditor!!.saveAsFile(imageFile?.absolutePath!!, object : OnSaveListener {
                        override fun onSuccess(imagePath: String) {
                            Log.e("PhotoEditor", "Image Saved Successfully")
                            showToast("Изменения сохранены")
                            onBackPressed()
                        }

                        override fun onFailure(exception: Exception) {
                            Log.e("PhotoEditor", "Failed to save Image")
                        }
                    })
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showToast(s: String) {
        Toast.makeText(this@PhotoDefectEditActivity, s, Toast.LENGTH_SHORT).show()
    }
}