package kz.cheesenology.smartremontmobile.di

import android.content.Context
import dagger.Component
import kz.cheesenology.smartremontmobile.view.auth.AuthActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, RetrofitModule::class])
interface ApplicationComponent {

    //APP
    fun getContext(): Context

    fun plusDatabaseModule(roomModule: RoomModule): DatabaseComponent
    fun inject(authPresenter: AuthActivity)

    //PRESENTER INJECT
    /* fun inject(authPresenter: AuthPresenter)
     //fun inject(authPresenter: RemontListPresenter)
     fun inject(stagesPresenter: StagesPresenter)
     fun inject(checkPresenter: CheckPresenter)
     fun inject(clientPhotoPresenter: ClientPhotoPresenter)
     fun inject(sendStatsPresenter: SendStatsPresenter)*/
}