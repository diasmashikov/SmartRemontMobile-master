package kz.cheesenology.smartremontmobile.view.stagestatushistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_stage_status_history.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.model.StageStatusHistoryListModel
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import kotlin.properties.Delegates

class StageStatusHistoryFragment(val remontID: Int) : MvpAppCompatDialogFragment(), StageStatusHistoryView {

    private var adapter: StageStatusHistoryAdapter by Delegates.notNull()
    @Inject
    lateinit var presenter: StageStatusHistoryPresenter
    @InjectPresenter
    lateinit var moxyPresenter: StageStatusHistoryPresenter

    @ProvidePresenter
    fun providePresenter() = presenter


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[activity!!].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stage_status_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setRemontID(remontID)
        adapter = StageStatusHistoryAdapter()
        rvStageStatusHistory.adapter = adapter
        val layoutManager = LinearLayoutManager(activity!!)
        rvStageStatusHistory.layoutManager = layoutManager
        rvStageStatusHistory.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL))
    }

    override fun setList(it: List<StageStatusHistoryListModel>) {
        adapter.data = it
    }
}