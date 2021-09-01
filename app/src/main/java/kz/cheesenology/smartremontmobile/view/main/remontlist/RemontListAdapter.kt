package kz.cheesenology.smartremontmobile.view.main.remontlist

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_remont_list_constraint.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.model.RemontListDBModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.toEmptyIfNull
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


typealias RemontAdapterModel = RemontListDBModel

class RenovationListAdapter : RecyclerView.Adapter<RenovationListAdapter.Holder>(), Filterable {

    private var _data: MutableList<RemontAdapterModel> = mutableListOf()
    private var searchList: MutableList<RemontAdapterModel> = mutableListOf()
    private val _singleData: RemontAdapterModel? = null
    private var _callback: (RemontAdapterModel) -> Unit = { }
    private var mCallback: Callback? = null
    var remontListViewListener: RemontListView? = null

    var isBig: Boolean = false

    init {
        isBig = AppConstant.isBIG()
    }

    var data: List<RemontAdapterModel>
        get() = _data
        set(value) {

            /*val diffUtilCallback = RemontListDiffUtil(_data, value)
            val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
            searchList = value.toMutableList()
            _data = value.toMutableList()
            diffResult.dispatchUpdatesTo(this)*/

            searchList = value.toMutableList()
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }


    var singleData: RemontAdapterModel
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_remont_list_constraint, parent, false)
        Log.e("ONCREATE VIEWHOLDER", " TRUE")
        return Holder(itemView)
    }

    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit = with(_data[position]) {
        Log.e("onBindViewHolder", " TRUE")
        holder.apply {

            if( okkPhotoReportSendDate != null ){
                val currentDateTime = LocalDateTime.now()
                var dateNow = currentDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)).substring(2, 4).toInt()
                var dateLastSentPhotoReport = okkPhotoReportSendDate?.substring(0, 2)?.toInt()
                var date = dateNow - dateLastSentPhotoReport!!
                if(date >= 7){
                    val color = ContextCompat.getColor(itemView.context, R.color.red_400)
                    tvPhotoReportData.setTextColor(color)
                }

                when(date){

                    1 -> tvPhotoReportData.text = " Последний фотоотчёт был сделан: " + date.toString() + " день назад"
                    2 -> tvPhotoReportData.text = " Последний фотоотчёт был сделан: " + date.toString() + " дня назад"
                    3 -> tvPhotoReportData.text = " Последний фотоотчёт был сделан: " + date.toString() + " дня назад"
                    4 -> tvPhotoReportData.text = " Последний фотоотчёт был сделан: " + date.toString() + " дня назад"
                    else -> tvPhotoReportData.text = " Последний фотоотчёт был сделан: " + date.toString() + " дней назад"

                }

            }


            tvTitle.text = "Клиент: $clientName"
            tvWorkGroup.text = "Подрядчик: $contractorName"
            tvStatusDate.text = "$okkStatusText - $remontDateBegin"





            tvManager.text =
                "Менеджер: ${managerFio.toEmptyIfNull()}   ${managerPhone.toEmptyIfNull()}"

            tvLastChange.text = fio

            if (internalMaster.isNullOrEmpty())
                tvInternalMaster.text = "ИТР не назначен"
            else
                tvInternalMaster.text = "ИТР: $internalMaster $internalMasterPhone"

            when (isBig) {
                true -> {
                    tvDefectCnt.visibility = View.INVISIBLE
                    tvDefectAcceptCnt.visibility = View.INVISIBLE
                }
                else -> {
                    tvDefectCnt.visibility = View.VISIBLE
                    tvDefectAcceptCnt.visibility = View.VISIBLE
                }
            }

            tvConstructorLink.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            tvConstructorLink.setOnClickListener {
                mCallback!!.runContructorLink(constructorUrl)
            }

            /*btnRemontAccept.setOnClickListener {
                mCallback!!.acceptRemont(this@with)
            }

            btnRemontCancel.setOnClickListener {
                mCallback!!.cancelRemont(this@with)
            }

            btnRemontAccept.visibility = View.GONE
            btnRemontCancel.visibility = View.GONE*/

            when (sendStatus) {
                1 -> {
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_done_green_24dp,
                            0
                    )
                    tvErrorText.visibility = View.GONE
                }
                2 -> {
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_clear_red_36dp,
                            0
                    )
                    tvErrorText.visibility = View.VISIBLE
                    tvErrorText.text = errorText
                }
                else -> {
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    tvErrorText.visibility = View.GONE
                }
            }

            //Статус
            when (okkStatus) {
                1 -> {
                    tvStatusDate.setTextColor(Color.BLACK)
                    //btnRemontAccept.visibility = View.VISIBLE
                    //btnRemontCancel.visibility = View.VISIBLE
                }
                2 -> tvStatusDate.setTextColor(Color.parseColor("#1B5E20"))
                else -> tvStatusDate.setTextColor(Color.RED)
            }

            //Активный этап
            when (activeStageName) {
                null -> tvCurrentStage.text = "Текущий этап: -"
                "null" -> tvCurrentStage.text = "Текущий этап: -"
                else -> {
                    if (stageStatusName != null)
                        tvCurrentStage.text = "Текущий этап: $activeStageName\n${stageStatusName}"
                    else
                        tvCurrentStage.text = "Текущий этап: $activeStageName"
                }
            }

            //Цвет активного статуса
            when (stageStatusID) {
                -1 -> tvCurrentStage.setTextColor(Color.BLACK)
                1 -> tvCurrentStage.setTextColor(Color.parseColor("#FFB300"))
                2 -> tvCurrentStage.setTextColor(Color.RED)
                3 -> tvCurrentStage.setTextColor(Color.parseColor("#A0522D"))
                4 -> tvCurrentStage.setTextColor(Color.parseColor("#1B5E20"))
                else -> {
                    tvCurrentStage.setTextColor(Color.BLACK)
                }
            }

            //Количество дефектов по этому ремонту
            when {
                defectCnt > 0 && defectAcceptCnt > 0 -> {
                    tvDefectCnt.text = defectCnt.toString()
                    tvDefectAcceptCnt.text = defectAcceptCnt.toString()
                }
                defectCnt > 0 && defectAcceptCnt == 0 -> {
                    tvDefectAcceptCnt.text = ""
                    tvDefectCnt.text = """$defectCnt"""
                }
                defectCnt == 0 && defectAcceptCnt > 0 -> {
                    tvDefectCnt.text = ""
                    tvDefectAcceptCnt.text = """$defectAcceptCnt"""
                }
                else -> {
                    tvDefectCnt.text = ""
                    tvDefectAcceptCnt.text = ""
                }
            }


            when {
                address != null -> tvAddress.text = address
                else -> tvAddress.text = "адрес не указан"
            }


            if (activeStageID!! >= 5 && stageStatusID == 4) {
                btnRating.visibility = View.VISIBLE
                if (ratingRemontID != null) {
                    btnRating.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_check_box_green_36dp,
                            0
                    )
                } else {
                    btnRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            } else {
                btnRating.visibility = View.GONE
            }

            btnRating.setOnClickListener {
                mCallback!!.onRatingClick(this@with)
            }

            itemView.setOnClickListener {
                mCallback!!.onClick(this@with)
            }

            tvTitle.setOnTouchListener { _, event ->
                if (event.action == 1 && !tvTitle.hasSelection()) {
                    mCallback!!.onClick(this@with)
                }
                false
            }
            tvAddress.setOnTouchListener { _, event ->
                if (event.action == 1 && !tvAddress.hasSelection()) {
                    mCallback!!.onClick(this@with)
                }
                false
            }
            tvWorkGroup.setOnTouchListener { _, event ->
                if (event.action == 1 && !tvWorkGroup.hasSelection()) {
                    mCallback!!.onClick(this@with)
                }
                false
            }
            btnOptions.setOnClickListener {
                val popup = PopupMenu(itemView.context, holder.btnOptions)
                popup.inflate(R.menu.menu_remont_list_item)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_remont_item_call_contractor -> {
                            mCallback?.onContractorPhoneClick(contractorPhone)
                            true
                        }
                        R.id.menu_remont_item_call_internal_master -> {
                            mCallback?.onInternalMasterCall(internalMasterPhone)
                            true
                        }
                        R.id.menu_remont_item_show_remont_project -> {
                            mCallback?.showRemontProject(this@with)
                            true
                        }
                        else -> false
                    }
                }
                //displaying the popup
                //displaying the popup
                popup.show()
            }
        }
    }

    fun setCallback(callback: (RemontAdapterModel) -> Unit) {
        _callback = callback
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: RemontAdapterModel)
        fun menuClick(valueItem: RemontAdapterModel)
        fun acceptRemont(mode: RemontAdapterModel)
        fun cancelRemont(mode: RemontAdapterModel)
        fun onRatingClick(remontListDBModel: RemontAdapterModel)
        fun onContractorPhoneClick(contractorPhone: String?)
        fun showRemontProject(remontListDBModel: RemontAdapterModel)
        fun onInternalMasterCall(internalMasterPhone: String?)
        fun runContructorLink(constructorUrl: String?)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTitle = view.tvItemRemontListNum
        var tvManager = view.tvItemRemontListOkkName
        var tvConstructorLink = view.tvItemRemontListConstructorLink
        val tvAddress = view.tvItemRemontListAddress
        val tvWorkGroup = view.tvItemRemontListResidenceAcceptDate
        val tvInternalMaster = view.tvItemRemontListProrab
        val tvStatusDate = view.tvItemRemontListStatus
        val tvCurrentStage = view.tvItemRenovationCurrentStage
        val tvLastChange = view.tvItemRenovationLastChange
        val tvErrorText = view.tvItemRenovationErrorText
        val tvDefectCnt = view.tvItemRemontListDefectNum
        val tvDefectAcceptCnt = view.tvRemontListDefectAcceptInfo
        val btnRating = view.btnItemRemontListRating
        val btnOptions = view.tvRemontListOptions
        val tvPhotoReportData = view.tvItemRemontListDaysAfterPhotoReport
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    _data = searchList.toMutableList()
                } else {
                    val filteredList = ArrayList<RemontAdapterModel>()
                    for (row in searchList) {
                        if (row.remontID.toString().toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        } else if (row.address != null) {
                            if (row.address!!.toLowerCase().contains(charString.toLowerCase()))
                                filteredList.add(row)
                        }
                    }
                    _data = filteredList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = _data
                return filterResults
            }

            override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: Filter.FilterResults
            ) {
                if (filterResults.values != null) {
                    _data = filterResults.values as ArrayList<RemontAdapterModel>
                    notifyDataSetChanged()
                } else {
                    Log.e("Empty List", " ")
                }
            }
        }
    }
}
