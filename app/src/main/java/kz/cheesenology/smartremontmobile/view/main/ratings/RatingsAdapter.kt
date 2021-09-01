package kz.cheesenology.smartremontmobile.view.main.ratings

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_ratings.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.model.ratings.RatingListAdapterModel

typealias rT = RatingListAdapterModel

class RatingsAdapter : RecyclerView.Adapter<RatingsAdapter.ViewHolder>() {
    private var _data: MutableList<rT> = mutableListOf()
    private val _singleData: rT? = null
    private var mCallback: Callback? = null

    lateinit var parentContext: Context

    var data: List<rT>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: rT
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ratings, parent, false)
        parentContext = parent.context
        return ViewHolder(itemView)
    }

    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int): Unit =
        with(_data[position]) {
            holder.apply {
                tvDetailName.text = detailName

                chipGroup.isSingleSelection = true
                stepList?.forEach { item ->
                    val chip = Chip(parentContext)
                    chip.text = item.stepName
                    chip.isClickable = true
                    chip.isCheckable = true
                    chip.setOnClickListener {
                        stepCheckedID = item.ratingStepID
                    }
                    chipGroup.addView(chip)
                    if (item.ratingRemontID != null) {
                        chip.isChecked = true
                        isChecked = true
                    }
                }

                chipGroup.setOnCheckedChangeListener { group, checkedId ->
                    isChecked = checkedId != -1
                }
            }
        }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDetailName = view.tvItemRatingStepName
        val chipGroup = view.rgItemRatings
    }
}