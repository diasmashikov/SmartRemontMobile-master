package kz.cheesenology.smartremontmobile.view.main.stages

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.stage.StageEntity


class CustomSpinnerAdapter(context: Context, resourceId: Int,
                           private val objects: List<StageEntity>,
                           val activeStageID: Int) : ArrayAdapter<StageEntity>(context, resourceId, objects) {

    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    private fun getCustomView(position: Int, parent: ViewGroup): View {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.item_spinner, parent, false)
        val label = row.findViewById(R.id.tvItemSpinner) as TextView
        label.text = objects[position].stageShortName

        val activePos = activeStageID - 1
        if (position == activePos) {
            label.setTextColor(Color.parseColor("#1B5E20"))
        }

        return row
    }

}