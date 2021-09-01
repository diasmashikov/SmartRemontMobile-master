package kz.cheesenology.smartremontmobile.view.main.webview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Pair
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_web_view_default.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeEntity
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class WebViewDefaultActivity : MvpAppCompatActivity(), WebViewDefaultView {

    @Inject
    lateinit var presenter: WebViewPresenter

    @InjectPresenter
    lateinit var moxyPresenter: WebViewPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    lateinit var spinner: Spinner
    lateinit var adapter: ArrayAdapter<ReportTypeEntity>

    val builder: MaterialDatePicker.Builder<Pair<Long, Long>> =
        MaterialDatePicker.Builder.dateRangePicker()
    var picker: MaterialDatePicker<Pair<Long, Long>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_default)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        spinner = findViewById(R.id.spinnerWebViewReport)

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = spinner.selectedItem as ReportTypeEntity
                presenter.loadUrL(item.reportTypeCode)
            }
        }

        //WEB
        webViewDefault.settings.javaScriptEnabled = true
        webViewDefault.settings.loadWithOverviewMode = true
        webViewDefault.settings.useWideViewPort = true
        webViewDefault.settings.domStorageEnabled = true
        webViewDefault.setInitialScale(1)
        webViewDefault.settings.setSupportZoom(true)
        webViewDefault.settings.builtInZoomControls = true
        webViewDefault.settings.displayZoomControls = false
        webViewDefault.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webViewDefault.isScrollbarFadingEnabled = false

        val constraintsBuilder = CalendarConstraints.Builder()
        builder.setCalendarConstraints(constraintsBuilder.build())
        builder.setTitleText("Выберите даты")
        picker = builder.build()

        webViewDefault.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                setProgressBarVisibility(View.VISIBLE)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                setProgressBarVisibility(View.GONE)
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                setProgressBarVisibility(View.GONE)
                Toast.makeText(this@WebViewDefaultActivity, description, Toast.LENGTH_SHORT).show()
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView,
                req: WebResourceRequest,
                rerr: WebResourceError
            ) {
                setProgressBarVisibility(View.GONE)
                onReceivedError(
                    view,
                    rerr.errorCode,
                    rerr.description.toString(),
                    req.url.toString()
                )
            }
        }
    }

    private fun setProgressBarVisibility(visibility: Int) {
        if (pbWebView != null) {
            pbWebView.visibility = visibility
        }
    }

    override fun setSpinnerData(it: MutableList<ReportTypeEntity>?) {
        adapter = ArrayAdapter(
            applicationContext,
            R.layout.item_spinner_web_report,
            it!!
        )
        spinner.adapter = adapter
    }

    @SuppressLint("CheckResult")
    override fun setURL(url: String, postData: ByteArray) {
        /*Completable.fromAction {

        }.subscribeOn(Schedulers.single())*/
        webViewDefault.postUrl(url, postData)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_web_view, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_web_calendar -> {
                picker?.show(supportFragmentManager, picker.toString())
                picker?.addOnPositiveButtonClickListener {
                    val sdf = SimpleDateFormat("dd.MM.yyyy")
                    val date1 = Date(it.first!!)
                    val date2 = Date(it.second!!)
                    val code = spinner.selectedItem as ReportTypeEntity
                    presenter.setFilterDates(
                        sdf.format(date1),
                        sdf.format(date2),
                        code.reportTypeCode
                    )
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
