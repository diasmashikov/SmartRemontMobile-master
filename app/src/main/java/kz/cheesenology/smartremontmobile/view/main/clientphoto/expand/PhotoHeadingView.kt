package kz.cheesenology.smartremontmobile.view.main.clientphoto.expand

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.expand.*
import kz.cheesenology.smartremontmobile.R

@Parent
@Layout(R.layout.header_view)
class PhotoHeadingView(var mContext: Context, var mHeading: String) {
    
    @Toggle(R.id.toggleView)
    @View(R.id.toggleView)
    @JvmField
    var toggleView: LinearLayout? = null

    @View(R.id.tvHeaderTitle)
    @JvmField
    var headingTxt: TextView? = null

    @View(R.id.toggleIconHeader)
    @JvmField
    var toggleIcon: ImageView? = null

    @ParentPosition
    @JvmField
    var mParentPosition: Int = 0

    @Resolve
    fun onResolved() {
        if (toggleIcon != null)
            toggleIcon!!.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp))
        if (headingTxt != null)
            headingTxt!!.text = mHeading
    }

    @Expand
    fun onExpand() {
        if (toggleIcon != null)
            toggleIcon!!.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp))
    }

    @Collapse
    fun onCollapse() {
        if (toggleIcon != null)
            toggleIcon!!.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp))
    }
}