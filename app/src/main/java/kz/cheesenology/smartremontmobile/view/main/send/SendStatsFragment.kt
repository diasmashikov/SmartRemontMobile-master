package kz.cheesenology.smartremontmobile.view.main.send


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_send_stats.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.check.SendStatsModel
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class SendStatsFragment(val remontList: List<Int>) : MvpAppCompatDialogFragment(), SendStatsView {


    @Inject
    lateinit var presenter: SendStatsPresenter
    @InjectPresenter
    lateinit var moxyPresenter: SendStatsPresenter
    @ProvidePresenter
    fun providePresenter() = presenter

    private lateinit var progressDialog: ProgressDialog

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[activity!!].databaseComponent?.inject(this)
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        presenter.setRemontIDList(remontList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Получение данных...")

        btnDialogSendData.setOnClickListener {
            presenter.sendAllOfflineWorkData()
        }

        btnDialogClose.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun showToast(toString: String) {
        Toast.makeText(context, toString, Toast.LENGTH_SHORT).show()
    }

    override fun closeDialog() {
        dialog?.dismiss()
    }

    override fun showDialog(s: String) {
        if (!progressDialog.isShowing) {
            progressDialog.setTitle(s)
            progressDialog.show()
        }
    }

    override fun dismissDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

    @SuppressLint("SetTextI18n")
    override fun setStats(it: SendStatsModel?) {
        tvDialogSendText.text = """
            Статусы этапов: ${it!!.remontListCnt}
            Дефекты: ${it.photoCnt}
        """.trimIndent()
        tvDialogSendText.setTextColor(Color.RED)
        btnDialogSendData.visibility = View.VISIBLE
    }

    override fun setEmptySendText() {
        tvDialogSendText.text = "Нет данных для отправки"
        btnDialogSendData.visibility = View.GONE
    }
}
