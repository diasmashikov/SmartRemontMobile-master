package kz.cheesenology.smartremontmobile.view.auth

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_auth.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.util.PrefUtils
import kz.cheesenology.smartremontmobile.util.apkdownload.Download
import kz.cheesenology.smartremontmobile.util.apkdownload.DownloadService
import kz.cheesenology.smartremontmobile.view.main.notification.ServerNotificationActivity
import kz.cheesenology.smartremontmobile.view.main.remontlist.RemontListActivity
import kz.cheesenology.smartremontmobile.view.selectwork.SelectWorkActivity
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import permissions.dispatcher.*
import javax.inject.Inject


@RuntimePermissions
class AuthActivity : MvpAppCompatActivity(), AuthView {

    @Inject
    lateinit var presenter: AuthPresenter

    @InjectPresenter
    lateinit var moxyPresenter: AuthPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    lateinit var progressDialog: ProgressDialog

    var pdDownload: ProgressDialog? = null

    var isNotification: Boolean = false

    val GET_UNKNOWN_APP_SOURCES = 2001
    val INSTALL_PACKAGES_REQUESTCODE = 3001
    val DOWNLOAD_PROGRESS = "download_progress"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@AuthActivity].applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        title = "Smart Remont"

        showPermissionWithPermissionCheck()

        pdDownload = ProgressDialog(this@AuthActivity)
        //download progress receiver
        registerReceiver()

        isNotification = intent.getBooleanExtra("notification_str", false)

        //onNewIntent(intent)

        /*val manufacturer = "xiaomi"
        if (manufacturer.equals(android.os.Build.MANUFACTURER, ignoreCase = true)) {
            val intent = Intent()
            intent.component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            startActivity(intent)
        }*/

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Авторизация...")

        btnAuthLogin.setOnClickListener {
            presenter.authUser(
                etAuthLogin.text.toString().trim(),
                etAuthPassword.text.toString().trim()
            )
        }

        etAuthLogin.addTextChangedListener(AuthTextWatcher(etAuthLogin))
        etAuthPassword.addTextChangedListener(AuthTextWatcher(etAuthPassword))

        cbSaveAuth.setOnCheckedChangeListener { buttonView, isChecked ->
            PrefUtils.editor.putBoolean("is_auth", isChecked).apply()
            if (!isChecked) {
                PrefUtils.editor.putString("auth_login", null).apply()
                PrefUtils.editor.putString("auth_password", null).apply()
            }
        }

        btnDownloadPhotos.setOnClickListener {
            val downloadmanager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            presenter.downloadTest(downloadmanager)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_auth, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_auth_download_apk -> {
                presenter.apkUpdateCheck()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.getBooleanExtra("notification_str", false)) {
            isNotification = true
            checkAuthState()
        }
    }

    override fun setVersion(s: String) {
        tvVerion.text = s
    }

    override fun checkAuthState() {
        val isAuth = PrefUtils.prefs.getBoolean("is_auth", false)
        if (isAuth) {
            val user = PrefUtils.prefs.getString("auth_login", "")
            val password = PrefUtils.prefs.getString("auth_password", "")

            if (user!!.isNotEmpty() && password!!.isNotEmpty()) {
                PrefUtils.editor.putBoolean("is_auth", true).apply()
                etAuthLogin.setText(user)
                etAuthPassword.setText(password)
                cbSaveAuth.isChecked = true
                presenter.authUser(user.trim(), password.trim())
            }
        } else {
            PrefUtils.editor.putBoolean("is_auth", false).apply()
        }
    }

    private inner class AuthTextWatcher(private val view: View) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                etAuthLogin.id -> presenter.validateLogin(etAuthLogin.text.toString().trim())
                etAuthPassword.id -> presenter.validatePassword(
                    etAuthPassword.text.toString().trim()
                )
            }
        }
    }

    override fun setLoginLayoutError(s: String) {
        tlAuthLogin.error = s
        requestFocus(etAuthLogin)
    }

    override fun setPasswordLayoutError(s: String) {
        tlAuthPassword.error = s
        requestFocus(tlAuthPassword)
    }

    override fun disableLoginLayoutError() {
        tlAuthLogin.isErrorEnabled = false
    }

    override fun disablePasswordLayoutError() {
        tlAuthPassword.isErrorEnabled = false
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun showAuthError(errMsg: String) {
        PrefUtils.editor.putString("auth_login", null).apply()
        PrefUtils.editor.putString("auth_password", null).apply()
        Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
    }

    override fun navigateToMainMenu(s: String) {
        if (cbSaveAuth.isChecked) {
            PrefUtils.editor.putString("auth_login", etAuthLogin.text.toString()).apply()
            PrefUtils.editor.putString("auth_password", etAuthPassword.text.toString()).apply()
        }
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
        val nav = Intent(applicationContext, SelectWorkActivity::class.java)
        nav.putExtra("status", isNotification)
        startActivity(nav)
    }

    override fun navigateToNotificationScreen() {
        startActivity(Intent(applicationContext, SelectWorkActivity::class.java))
    }

    override fun showToast(s: String) {
        Toast.makeText(this@AuthActivity, s, Toast.LENGTH_LONG).show()
    }

    override fun showDialog() {
        if (!progressDialog.isShowing)
            progressDialog.show()
    }

    override fun dismissDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }


    override fun prepareForInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val result = packageManager.canRequestPackageInstalls()
            if (result) {
                installApk()
            } else {
                // request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES),
                    INSTALL_PACKAGES_REQUESTCODE
                )
            }
        } else {
            installApk()
        }
    }

    private fun installApk() {
        pdDownload?.setMessage("Скачивание")
        pdDownload?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        if (pdDownload != null) {
            if (pdDownload!!.isShowing)
                pdDownload!!.dismiss()
            else
                pdDownload!!.show()
        }

        val intent = Intent(this, DownloadService::class.java)
        intent.putExtra("version", "1")
        startService(intent)
    }

    override fun onDestroy() {
        if (pdDownload != null && pdDownload!!.isShowing)
            pdDownload!!.dismiss()
        super.onDestroy()
    }

    private fun registerReceiver() {
        val bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DOWNLOAD_PROGRESS)
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DOWNLOAD_PROGRESS) {
                val download: Download =
                    intent.getParcelableExtra<Parcelable>("download") as Download
                pdDownload?.setProgress(download.progress)
                Log.e("progress: ", download.progress.toString() + " ")
                if (download.progress === 100) {
                    pdDownload?.setMessage("Приложение загружено")
                } else {
                    pdDownload?.setMessage(
                        String.format(
                            "Загружено (%d/%d) MB",
                            download.currentFileSize,
                            download.totalFileSize
                        )
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GET_UNKNOWN_APP_SOURCES -> prepareForInstall()

            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            INSTALL_PACKAGES_REQUESTCODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApk()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES)
            }
        }

        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    )
    fun showPermission() {
    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    )
    fun showRationaleForPermission(request: PermissionRequest) {

    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    )
    fun onPermissionDenied() {
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    )
    fun onPermissionNeverAskAgain() {

    }
}
