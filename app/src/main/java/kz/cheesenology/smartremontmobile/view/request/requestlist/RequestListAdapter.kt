package kz.cheesenology.smartremontmobile.view.request.requestlist

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_request_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListEntity
import kz.cheesenology.smartremontmobile.util.PrefUtils


typealias requestListType = RequestListEntity

class RequestListAdapter : RecyclerView.Adapter<RequestListAdapter.Holder>(), Filterable  {

    private var _data: MutableList<requestListType> = mutableListOf()
    private val _singleData: requestListType? = null
    private var mCallback: Callback? = null
    var requestListViewListener: RequestListView? = null
    // Этот лист предназначен для копирования основной даты нашего адаптера
    private var initialData: MutableList<requestListType> = mutableListOf()

    var data: List<requestListType>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: requestListType
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        // initialData берет на себя изначальную инфу всего _data,
        // чтобы после показывать ее, если search не используется
        initialData = ArrayList(_data)

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_request_list, parent, false)
        return Holder(itemView)
    }

    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit = with(_data[position]) {
        holder.apply {
            tvRequestNumber.text = client_request_id.toString()
            tvAddress.text = resident_name
            tvFlatNum.text = "КВ: " + flat_num.toString()
            tvProrab.text = manager_project_name
            tvItemResidentAcceptDate.text = "Дата назначения ОКК: $okk_date"
            tvStatus.text = draft_status
            tvOkkName.text = PrefUtils.prefs.getString("fio", "")
            tvFlatLink.text = "Квартирный листок"
            tvFlatLink.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            tvProrabPhone.text = manager_project_phone
            tvProrabPhone.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            if (okk_check_date.isNullOrEmpty()) {
                tvOkkCheckDate.visibility = View.GONE
            } else {
                tvOkkCheckDate.visibility = View.VISIBLE
                tvOkkCheckDate.text = "Дата повтр. проверки: " + okk_check_date.toString()
            }

            tvProrabPhone.setOnClickListener {

            }

            tvFlatLink.setOnClickListener {
                mCallback!!.runFlatLink(flat_list_url)
            }

            itemView.setOnClickListener {
                mCallback!!.onClick(this@with)
            }
        }
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(requestListEntity: requestListType)
        fun runFlatLink(flatListUrl: String?)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRequestNumber = view.tvItemRequestListNum
        val tvDefectNum = view.tvItemRequestListDefectNum
        val tvOptions = view.tvRequestListOptions
        val tvFlatLink = view.tvItemRequestFlatLink
        val tvProrabPhone = view.tvItemRequestListProrabPhone
        val tvOkkName = view.tvItemRequestListOkkName
        val tvAddress = view.tvItemRequestListAddress
        val tvFlatNum = view.tvItemRequestListFlatNum
        val tvProrab = view.tvItemRequestListProrab
        val tvItemResidentAcceptDate = view.tvItemRequestListResidenceAcceptDate
        val tvStatus = view.tvItemRequestListStatus
        val tvOkkCheckDate = view.tvItemRequestOkkCheckDate
    }


    // Эта функция вызывается в главного активити, чтобы исполнять фильтрацию основываясь на написанных данных
    override fun getFilter(): Filter? {
        return Filter
    }

    // Тут происходит процесс фильтрации
    private val Filter: Filter = object : Filter() {


        // Алгоритм фильтрации
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<requestListType> = ArrayList()

            // Если поисковик пуст, то он показывает всю информацию от initialData
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(initialData)
            }
            // Когда поисковик заполнятся, то filterPattern берет на себя написанное значение и добавляет в filteredList itemы,
            // которые имеют похожие значения
            else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                for (pieceOfData in initialData) {
                    if (pieceOfData.resident_name?.toLowerCase()!!.contains(filterPattern) ||
                            pieceOfData.flat_num?.toLowerCase()!!.contains(filterPattern) ||
                            pieceOfData.client_request_id.toString() == filterPattern) {
                        filteredList.add(pieceOfData)
                    }
                }
            }
            // метод FilterResults забирает обновленные itemы, которые нужно показать, чтобы потом передать на показ
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        // метод publishResults показывает обновленный лист с results
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            _data.clear()
            _data.addAll(results.values as List<requestListType>)
            notifyDataSetChanged()
        }
    }



    }


