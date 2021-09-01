package kz.cheesenology.smartremontmobile.di

import dagger.Subcomponent
import kz.cheesenology.smartremontmobile.domain.work.SyncNetworkWork
import kz.cheesenology.smartremontmobile.notifications.OkkFirebaseService
import kz.cheesenology.smartremontmobile.view.auth.AuthActivity
import kz.cheesenology.smartremontmobile.view.camerax.CameraDefectListActivity
import kz.cheesenology.smartremontmobile.view.camerax.CameraPhotoReportActivity
import kz.cheesenology.smartremontmobile.view.main.chat.ChatActivity
import kz.cheesenology.smartremontmobile.view.main.checkaccept.AcceptCheckActivity
import kz.cheesenology.smartremontmobile.view.main.clientphoto.ClientPhotoActivity
import kz.cheesenology.smartremontmobile.view.main.defectlist.DefectListActivity
import kz.cheesenology.smartremontmobile.view.main.notification.ServerNotificationActivity
import kz.cheesenology.smartremontmobile.view.main.photoreport.PhotoReportActivity
import kz.cheesenology.smartremontmobile.view.main.ratings.RatingsActivity
import kz.cheesenology.smartremontmobile.view.main.remontlist.RemontListActivity
import kz.cheesenology.smartremontmobile.view.main.send.SendStatsFragment
import kz.cheesenology.smartremontmobile.view.main.stages.StagesActivity
import kz.cheesenology.smartremontmobile.view.main.webview.WebViewDefaultActivity
import kz.cheesenology.smartremontmobile.view.request.checklist.RequestCheckListActivity
import kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus.RequestCheckStatusListActivity
import kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto.RequestCheckListPhotoFixActivity
import kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto.camera.CameraXRequestDefectActivity
import kz.cheesenology.smartremontmobile.view.request.requestlist.RequestListActivity
import kz.cheesenology.smartremontmobile.view.stagestatushistory.StageStatusHistoryFragment
import kz.cheesenology.smartremontmobile.work.SyncWork


@DatabaseScope
@Subcomponent(modules = [RoomModule::class])
interface DatabaseComponent {

    fun inject(authActivity: AuthActivity)
    fun inject(syncWork: SyncWork)
    fun inject(remontListActivity: RemontListActivity)
    fun inject(cameraPhotoReportActivity: CameraPhotoReportActivity)
    fun inject(stageStatusHistoryFragment: StageStatusHistoryFragment)
    fun inject(okkFirebaseService: OkkFirebaseService)
    fun inject(sendStatsFragment: SendStatsFragment)
    fun inject(serverNotificationActivity: ServerNotificationActivity)
    fun inject(stagesActivity: StagesActivity)
    fun inject(webViewDefaultActivity: WebViewDefaultActivity)
    fun inject(clientPhotoActivity: ClientPhotoActivity)
    fun inject(acceptCheckActivity: AcceptCheckActivity)
    fun inject(defectListActivity: DefectListActivity)
    fun inject(photoReportActivity: PhotoReportActivity)
    fun inject(cameraDefectListActivity: CameraDefectListActivity)
    fun inject(chatActivity: ChatActivity)
    fun inject(ratingsActivity: RatingsActivity)
    fun inject(syncNetworkWork: SyncNetworkWork)
    fun inject(requestListActivity: RequestListActivity)
    fun inject(requestCheckListActivity: RequestCheckListActivity)
    fun inject(requestCheckStatusListActivity: RequestCheckStatusListActivity)
    fun inject(cameraXRequestDefectActivity: CameraXRequestDefectActivity)
    fun inject(requestCheckListPhotoFixActivity: RequestCheckListPhotoFixActivity)
}