package kz.cheesenology.smartremontmobile.view.fragmentpreview

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_request_check_status_list.*
import kotlinx.android.synthetic.main.fragment_request_check_status_list_view_pager.*
import kotlinx.android.synthetic.main.item_image_list.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.view.main.photoreport.PhotoReportView
import kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus.DialogDraftPhotoViewPager
import kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus.RequestCheckStatusListView
import java.io.File
import java.util.*


class PhotoPreviewFragment(
    val listOfPhotoPaths: MutableList<String?>,
    picture_position: Int,
    val dialogBitmapList: MutableList<Bitmap?>,
    val exit_type: Int,
    val rotatedBitmapToFiles: MutableList<File?>
) : Fragment() {

    var mCallback: OnCallbackReceived? = null

    var listOfPhotoPathsFragment = dialogBitmapList
    var image_position = picture_position
    var requestCheckStatusListViewListener: RequestCheckStatusListView? = null
    var photoReportViewListener: PhotoReportView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(
            R.layout.fragment_request_check_status_list_view_pager,
            container,
            false
        )
        retainInstance = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //var rotatedBitmapToFiles: MutableList<File?> = arrayListOf()


        //mCallback?.Update(image_position)

       //val number_first_to_move = listOfPhotoPathsFragment[0]
        //listOfPhotoPathsFragment[0] = listOfPhotoPathsFragment[image_position]
       // listOfPhotoPathsFragment[image_position] = number_first_to_move





        view_pager.adapter?.notifyDataSetChanged()
        view_pager.visibility = View.VISIBLE
        /*
        for((index, bitmap) in dialogBitmapList.withIndex())
        {
            var rotatedBitmapToFile =
                listOfPhotoPaths[index]?.let {
                    ImageFilesRotatorAndResizer.bitmapToFile(
                        activity!!.applicationContext,
                        bitmap,
                        it
                    )
                }

            rotatedBitmapToFiles.add(rotatedBitmapToFile)


        }


         */

        view_pager.adapter = DialogDraftPhotoViewPager(listOfPhotoPaths, rotatedBitmapToFiles, dialogBitmapList)
        view_pager.setOffscreenPageLimit(1)
        view_pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        view_pager.doOnLayout {
            view_pager.setCurrentItem(image_position, false)
        }




        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (view_pager.visibility == View.VISIBLE && exit_type == 1) {
                    view_pager.visibility = View.GONE
                    btnCloseViewPager2.visibility = View.GONE
                    requestCheckStatusListViewListener?.closeViewPagerFromDialog(image_position)
                } else if (view_pager.visibility == View.VISIBLE && exit_type == 0) {
                    view_pager.visibility = View.GONE
                    btnCloseViewPager2.visibility = View.GONE
                    requestCheckStatusListViewListener?.closeViewPagerFromList(image_position)
                    //activity!!.getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else if(view_pager.visibility == View.VISIBLE && exit_type == 3)
                {
                    view_pager.visibility = View.GONE
                    btnCloseViewPager2.visibility = View.GONE
                    photoReportViewListener?.closeViewPager(image_position)
                }
                else {
                    if (isEnabled) {
                        isEnabled = false
                        activity!!.onBackPressed()
                    }
                }


            }
        })

        if(exit_type == 1)
        {
            btnCloseViewPager2.setOnClickListener {
                view_pager.visibility = View.GONE
                btnCloseViewPager2.visibility = View.GONE
                requestCheckStatusListViewListener?.closeViewPagerFromDialog(image_position)
            }
        }
        else if(exit_type == 3)
        {
            btnCloseViewPager2.setOnClickListener {
                view_pager.visibility = View.GONE
                btnCloseViewPager2.visibility = View.GONE
                photoReportViewListener?.closeViewPager(image_position)
            }
        }
        else
        {
            btnCloseViewPager2.setOnClickListener {
                view_pager.visibility = View.GONE
                btnCloseViewPager2.visibility = View.GONE
                requestCheckStatusListViewListener?.closeViewPagerFromList(image_position)
            }
        }




    }
    interface OnCallbackReceived {
        fun Update(image_position: Int)
    }


     override fun onAttach(activity: Activity) {
        super.onAttach(activity);

        try {
            mCallback = activity as OnCallbackReceived
        } catch (e: ClassCastException) {

        }
    }



    // You can Call the event from fragment as mentioned below
    // mCallback is the activity context.


}