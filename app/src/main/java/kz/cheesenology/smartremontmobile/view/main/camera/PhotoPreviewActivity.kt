package kz.cheesenology.smartremontmobile.view.main.camera

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_photo_preview.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File


class PhotoPreviewActivity : AppCompatActivity() {

    private var sImgPathList: MutableList<String>? = null
    private var sPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_photo_preview)

        sPosition = intent!!.getIntExtra("position", 0)
        val STATUS = intent!!.getStringExtra("list_status")
        sImgPathList = intent.getStringArrayListExtra("path_list").toMutableList()

        viewPager.adapter = SamplePagerAdapter(this, sImgPathList, STATUS)
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(this).clearMemory()
    }

    override fun onResume() {
        super.onResume()
        viewPager.currentItem = sPosition
    }

    class SamplePagerAdapter(context: Context, sImgPathList: MutableList<String>?, var status: String) : androidx.viewpager.widget.PagerAdapter() {

        var mContext = context
        private var mImgPathlList = sImgPathList

        override fun getCount(): Int {
            return when {
                //mDrawables.size != 0 -> mDrawables!!.size
                mImgPathlList!!.size != 0 -> mImgPathlList!!.size
                else -> 0
            }
        }

        @SuppressLint("CheckResult")
        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)

            if (mImgPathlList != null && mImgPathlList!!.isNotEmpty()) {
                var imageFile: File? = null
                when (status) {
                    AppConstant.PHOTO_STATUS_USER -> imageFile = File(AppConstant.FULL_MEDIA_PHOTO_PATH, mImgPathlList!![position])
                    AppConstant.PHOTO_STATUS_STANDART -> imageFile = File(AppConstant.FULL_MEDIA_STANDART_PATH, mImgPathlList!![position])
                    AppConstant.PHOTO_STATUS_PLANIROVKA -> imageFile = File(AppConstant.FULL_MEDIA_STANDART_PATH, mImgPathlList!![position])
                }

                if (imageFile!!.exists()) {
                    GlideApp.with(mContext)
                            .load(imageFile)
                            .apply(RequestOptions()
                                    .fitCenter()
                                    .format(DecodeFormat.PREFER_ARGB_8888)
                                    .override(Target.SIZE_ORIGINAL))
                            .fitCenter()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photoView)
                    //photoView.setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))
                } else {
                    Log.e("log,", mImgPathlList!![position])
                    /*GlideApp.with(mContext)
                            .load(mImgPathlList!![position])
                            .skipMemoryCache(true)
                            .into(photoView)*/
                    Log.e("log:   ", AppConstant.getServerName() + mImgPathlList!![position])
                    GlideApp.with(mContext)
                            .load(AppConstant.getServerName() + mImgPathlList!![position])
                            .apply(RequestOptions()
                                    .fitCenter()
                                    .format(DecodeFormat.PREFER_ARGB_8888)
                                    .override(Target.SIZE_ORIGINAL))
                            .fitCenter()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photoView)
                }
            }

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
}
