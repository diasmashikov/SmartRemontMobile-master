package kz.cheesenology.smartremontmobile.view.main.ratings

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_ratings.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.model.ratings.RatingSectionModel

typealias ratingT = RatingSectionModel

class RatingsSectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _data: MutableList<ratingT> = mutableListOf()
    private val _singleData: ratingT? = null
    private var mCallback: Callback? = null

    lateinit var parentContext: Context

    var data: List<ratingT>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: ratingT
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val v = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            parentContext = parent.context
            HeaderHolder(v)
        } else {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ratings, parent, false)
            parentContext = parent.context
            ViewHolder(v)
        }
    }

    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        val item = _data[position]
        return if (!item.isRow) {
            0
        } else {
            1
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int): Unit =
        with(_data[position]) {
            val item = _data[position]
            if (item.isRow) {
                val holder1 = holder as ViewHolder
                holder1.apply {
                    tvStepName.text = item.row?.stepName

                    for (i in 1..10) {
                        val chip = Chip(parentContext)
                        chip.text = "stepName"
                        chip.isClickable = true
                        chip.isCheckable = true
                        radioGroup.addView(chip)
                    }
                }
            } else {
                val h = holder as HeaderHolder
                h.tvHeader.typeface = Typeface.DEFAULT_BOLD
                h.tvHeader.text = item.section!!
            }
        }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = itemView.findViewById(android.R.id.text1)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStepName = view.tvItemRatingStepName
        val radioGroup = view.rgItemRatings
    }

}
