package kz.cheesenology.smartremontmobile.view.main.expand

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.mindorks.placeholderview.annotations.*
import com.mindorks.placeholderview.annotations.expand.ChildPosition
import com.mindorks.placeholderview.annotations.expand.ParentPosition
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModelNew
import kz.cheesenology.smartremontmobile.view.main.stages.ChildClickInterface


@Layout(R.layout.child_view)
class ChildView(var context: Context, var info: CheckListChildModelNew) {

    var callback: ChildClickInterface = context as ChildClickInterface

    @JvmField
    @ParentPosition
    var parentPosition: Int = 0

    @JvmField
    @ChildPosition
    var childPosition: Int = 0

    @JvmField
    @Position
    var globalPosition: Int = 0

    @View(R.id.tvChildTitle)
    @JvmField
    var titleTxt: TextView? = null

    @View(R.id.llChildStr)
    @JvmField
    var llChild: LinearLayout? = null

    @View(R.id.tvChildNorm)
    @JvmField
    var normTxt: TextView? = null

    @View(R.id.tvChildDefectNum)
    @JvmField
    var defectTxt: TextView? = null


    @LongClick(R.id.llChildStr)
    fun onLongClick() {
        //Toast.makeText(context,"Toast long click", Toast.LENGTH_LONG).show()
        callback.onChildLongClick()
    }

    @Click(R.id.llChildStr)
    fun onClick() {
        //Toast.makeText(context,"Toast long click", Toast.LENGTH_LONG).show()
        callback.onChildClick(info, globalPosition)
    }

    fun setTitle(isAccept: Int?, defectCnt: Int?, norm: String?, title: String?) {
        info.isAccepted = isAccept
        info.checkName = title
        info.defectCnt = defectCnt
        info.norm = norm

        titleTxt!!.text = title
        normTxt!!.text = norm
        if (defectCnt != null)
            if (defectCnt > 0)
                defectTxt!!.text = defectCnt.toString()

        if (info.norm != null && info.norm != "null")
            normTxt!!.text = info.norm.toString()
        if (info.norm == null || "null" == info.norm)
            normTxt!!.text = ""
        if (defectCnt == null)
            defectTxt!!.text = ""
        if (defectCnt == 0)
            defectTxt!!.text = ""


        when (isAccept) {
            //no status
            -1 -> {
                titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            //DEFECT
            0 -> titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_red_24dp, 0, 0, 0)
            //OK
            1 -> titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_beenhere_green_24dp, 0, 0, 0)
            //NOT USED
            2 -> titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_red_24dp, 0, 0, 0)
            else -> {
                titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Resolve
    fun onResolved() {
        titleTxt!!.text = info.checkName
        if (info.norm != null && info.norm != "null")
            normTxt!!.text = info.norm.toString()
        else
            normTxt!!.text = ""

        if (info.defectCnt != null)
            if (info.defectCnt!! > 0) {
                titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_red_24dp, 0, 0, 0)
                defectTxt!!.text = info.defectCnt.toString()
            } else {
                titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                defectTxt!!.text = ""
            }
        /*when (info.isAccepted) {
            //no status
            -1 -> {
                titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            }
            //DEFECT
            0 -> titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_red_24dp, 0, 0, 0)
            //OK
            1 -> titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_beenhere_green_24dp, 0, 0, 0)
            //NOT USED
            2 -> titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_red_24dp, 0, 0, 0)
            else -> {
                titleTxt!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }*/
    }

}