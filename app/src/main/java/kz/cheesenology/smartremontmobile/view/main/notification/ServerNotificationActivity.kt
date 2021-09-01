package kz.cheesenology.smartremontmobile.view.main.notification

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_server_notification.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.notification.NotificationEntity
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import kotlin.properties.Delegates

class ServerNotificationActivity : MvpAppCompatActivity(), ServerNotificationView {

    @Inject
    lateinit var presenter: ServerNotificationPresenter
    @InjectPresenter
    lateinit var moxyPresenter: ServerNotificationPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    private var adapter: ServerNotificationAdapter by Delegates.notNull()
    val layoutManager: LinearLayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@ServerNotificationActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_notification)

        title = "Уведомления"

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = ServerNotificationAdapter()
        rvNotificationList.adapter = adapter
        rvNotificationList.layoutManager = layoutManager
        rvNotificationList.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getBooleanExtra("notification_str", false)!!) {
            presenter.updateReadStatus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_notification, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_notification_refresh -> {
                presenter.getNotificationList()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setNotificationList(data: List<NotificationEntity>) {
        adapter.data = data
        showToast("Уведомления обновлены")
    }

    override fun showToast(s: String) {
        Toast.makeText(this@ServerNotificationActivity, s, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
