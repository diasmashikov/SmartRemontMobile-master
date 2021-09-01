package kz.cheesenology.smartremontmobile

import android.app.Activity
import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.liulishuo.filedownloader.FileDownloader
import io.reactivex.plugins.RxJavaPlugins
import kz.cheesenology.smartremontmobile.di.*
import kz.cheesenology.smartremontmobile.notifications.OkkFirebaseService
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.data.StringFormat
import org.acra.sender.HttpSender


@AcraCore(buildConfigClass = BuildConfig::class, reportFormat = StringFormat.JSON)
@AcraHttpSender(
    uri = "https://test.smart-remont.kz/rest/crash",
    httpMethod = HttpSender.Method.POST
)
class SmartRemontApplication : Application() {

    lateinit var databaseComponent: DatabaseComponent
    lateinit var applicationComponent: ApplicationComponent

    fun get(): SmartRemontApplication? {
        return instance
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }

    override fun onCreate() {
        super.onCreate()

        //SoLoader.init(this, false)
        Stetho.initializeWithDefaults(this)

        instance = this

        FirebaseApp.initializeApp(this)
        Fresco.initialize(this)
        FileDownloader.setup(applicationContext)

        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(applicationContext))
            //.roomModule(RoomModule(applicationContext))
            .build()

        /*if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(DatabasesFlipperPlugin(this))
            client.start()
        }*/
        //WorkManager.getInstance().cancelAllWork()

        RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
            ACRA.getErrorReporter().handleException(it)
        }
    }

    fun plusDatabaseComponent(dbName: String): DatabaseComponent {
        databaseComponent =
            applicationComponent.plusDatabaseModule(RoomModule(dbName + "_" + BuildConfig.SERVER_NAME))
        return databaseComponent
    }

    companion object {
        operator fun get(activity: Activity): SmartRemontApplication {
            return activity.application as? SmartRemontApplication ?: throw IllegalStateException()
        }

        operator fun get(okkFirebaseService: OkkFirebaseService): SmartRemontApplication {
            return okkFirebaseService.application as? SmartRemontApplication
                ?: throw IllegalStateException()
        }

        var databaseComponent: DatabaseComponent? = null
        var instance: SmartRemontApplication? = null
    }
}