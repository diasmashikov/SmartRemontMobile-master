package kz.cheesenology.smartremontmobile.view.auth

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.BuildConfig
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import org.json.JSONObject
import javax.inject.Inject


@InjectViewState
class AuthPresenter @Inject constructor(var networkApi: NetworkApi) : MvpPresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.checkAuthState()

        viewState.setVersion("Версия: " + BuildConfig.VERSION_NAME + "(${BuildConfig.SERVER_NAME})")
    }

    @SuppressLint("CheckResult")
    fun authUser(login: String, password: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                val token = task.result?.token
                val id = task.result?.id
                PrefUtils.editor.putString("fcm_token", token.toString()).apply()
                Log.e("TOKEN: ", token.toString())
                Log.e("ID: ", id.toString())
                if (validateLayoutAuth(login, password)) {
                    //Start REST or Offline auth
                    val jsonString = JSONObject()
                        .put("login", login)
                        .put("password", password)

                    Log.e("auth: ", jsonString.toString())

                    networkApi.signInPostUrl(login, password, token, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { viewState.showDialog() }
                        .doOnTerminate { viewState.dismissDialog() }
                        .subscribe({
                            Log.e("response: ", it.body().toString())
                            if (it.isSuccessful) {
                                if (it.body()!!.result.status) {
                                    //Save user for offline work
                                    PrefUtils.editor.putString("login", login).apply()
                                    PrefUtils.editor.putString("password", password).apply()
                                    PrefUtils.editor.putString("fio", it.body()!!.value.fio).apply()
                                    PrefUtils.editor.putString("okk_id", it.body()!!.value.okk_id.toString()).apply()
                                    PrefUtils.editor.putString("okk_name", it.body()!!.value.okk_name).apply()

                                    SmartRemontApplication.instance!!.plusDatabaseComponent(login)

                                    viewState.navigateToMainMenu("Вход онлайн")
                                } else {
                                    viewState.showAuthError(it.body()!!.result.errMsg)
                                }
                            } else {
                                viewState.showAuthError("Ошибка подключения")
                            }
                        }, {
                            it.printStackTrace()
                            //viewState.showAuthError(it.message.toString())
                             val pLogin = PrefUtils.prefs.getString("login", null)
                             val pPass = PrefUtils.prefs.getString("password", null)

                             if (
                                     pLogin == login && pPass == password
                             ) {
                                 SmartRemontApplication.instance!!.plusDatabaseComponent(login)
                                 viewState.navigateToMainMenu("Авторизация оффлайн")
                             } else {
                                 viewState.showAuthError(it.message.toString())
                             }
                        })
                }
                //val token = PrefUtils.prefs.getString("fcm_token", null)

            })


    }

    private fun validateLayoutAuth(login: String, password: String): Boolean {
        return when {
            login.isEmpty() -> {
                viewState.setLoginLayoutError("Введите логин")
                false
            }
            password.isEmpty() -> {
                viewState.setPasswordLayoutError("Введите пароль")
                false
            }
            else -> {
                viewState.disableLoginLayoutError()
                viewState.disablePasswordLayoutError()
                true
            }
        }
    }

    fun validateLogin(login: String) {
        if (login.isEmpty()) {
            viewState.setLoginLayoutError("Введите логин")
        } else {
            viewState.disableLoginLayoutError()
        }
    }

    fun validatePassword(password: String) {
        if (password.isEmpty()) {
            viewState.setPasswordLayoutError("Введите пароль")
        } else {
            viewState.disablePasswordLayoutError()
        }
    }

    fun downloadTest(downloadmanager: DownloadManager) {
        val uri = Uri.parse("http://commonsware.com/misc/test.mp4")
        val request = DownloadManager.Request(uri)
        request.setTitle("My File")
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "test.mp4"
        )
        downloadmanager.enqueue(request)
    }

    @SuppressLint("CheckResult")
    fun apkUpdateCheck() {
        val id = BuildConfig.VERSION_NAME
        networkApi.checkApkUpdate()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.version != null) {
                        if (id.replace(".", "").toInt() < it.body()!!.version!!.toInt()) {
                            viewState.showToast("Требуется обновление")
                            viewState.prepareForInstall()
                        } else {
                            viewState.showToast("Обновление не требуется. У вас установлена последняя версия")
                        }
                    }
                }
            }, {
                it.printStackTrace()
            })
    }
}