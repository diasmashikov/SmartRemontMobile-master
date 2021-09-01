package kz.cheesenology.smartremontmobile.data

import androidx.room.Database
import androidx.room.RoomDatabase
import kz.cheesenology.smartremontmobile.data.chat.StageChatDao
import kz.cheesenology.smartremontmobile.data.chat.StageChatEntity
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileDao
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListEntity
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryDao
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryEntity
import kz.cheesenology.smartremontmobile.data.check.list.CheckListDao
import kz.cheesenology.smartremontmobile.data.check.list.CheckListEntity
import kz.cheesenology.smartremontmobile.data.groupchat.GroupChatDao
import kz.cheesenology.smartremontmobile.data.groupchat.GroupChatEntity
import kz.cheesenology.smartremontmobile.data.notification.NotificationDao
import kz.cheesenology.smartremontmobile.data.notification.NotificationEntity
import kz.cheesenology.smartremontmobile.data.rating.RatingDetailDao
import kz.cheesenology.smartremontmobile.data.rating.RatingDetailEntity
import kz.cheesenology.smartremontmobile.data.rating.comment.RatingCommentDao
import kz.cheesenology.smartremontmobile.data.rating.comment.RatingCommentEntity
import kz.cheesenology.smartremontmobile.data.rating.remont.RatingRemontDao
import kz.cheesenology.smartremontmobile.data.rating.remont.RatingRemontEntity
import kz.cheesenology.smartremontmobile.data.rating.step.RatingStepDao
import kz.cheesenology.smartremontmobile.data.rating.step.RatingStepEntity
import kz.cheesenology.smartremontmobile.data.remont.RemontDao
import kz.cheesenology.smartremontmobile.data.remont.RemontEntity
import kz.cheesenology.smartremontmobile.data.remont.RemontListDao
import kz.cheesenology.smartremontmobile.data.remont.RemontListEntity
import kz.cheesenology.smartremontmobile.data.remontroom.RemontRoomDao
import kz.cheesenology.smartremontmobile.data.remontroom.RemontRoomEntity
import kz.cheesenology.smartremontmobile.data.remontstatus.RemontStatusDao
import kz.cheesenology.smartremontmobile.data.remontstatus.RemontStatusEntity
import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeDao
import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeEntity
import kz.cheesenology.smartremontmobile.data.request.RequestDao
import kz.cheesenology.smartremontmobile.data.request.RequestEntity
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListDao
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListEntity
import kz.cheesenology.smartremontmobile.data.requestlist.checkaccept.RequestCheckAcceptDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkaccept.RequestCheckAcceptEntity
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryDao
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryEntity
import kz.cheesenology.smartremontmobile.data.requestlist.checklist.RequestCheckListDao
import kz.cheesenology.smartremontmobile.data.requestlist.checklist.RequestCheckListEntity
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoEntitiy
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusDao
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusEntity
import kz.cheesenology.smartremontmobile.data.rooms.RoomDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomEntity
import kz.cheesenology.smartremontmobile.data.stage.StageDao
import kz.cheesenology.smartremontmobile.data.stage.StageEntity
import kz.cheesenology.smartremontmobile.data.stagestatus.StageStatusDao
import kz.cheesenology.smartremontmobile.data.stagestatus.StageStatusEntity
import kz.cheesenology.smartremontmobile.data.stagestatushist.StageStatusHistoryDao
import kz.cheesenology.smartremontmobile.data.stagestatushist.StageStatusHistoryEntity
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoDao
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoEntity
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaEntity

@Database(
    entities = [
        (TaskListEntity::class),
        (RemontEntity::class),
        (RoomEntity::class),
        (StageEntity::class),
        (StandartPhotoEntity::class),
        (CheckListEntity::class),
        (RemontStatusEntity::class),
        (StageStatusEntity::class),
        (RemontCheckListEntity::class),
        (CheckListHistoryEntity::class),
        (RemontListEntity::class),
        (UserDefectMediaEntity::class),
        (RemontRoomEntity::class),
        (StageChatEntity::class),
        (StageChatFileEntity::class),
        (StageStatusHistoryEntity::class),
        (NotificationEntity::class),
        (GroupChatEntity::class),
        (ReportTypeEntity::class),
        (RatingDetailEntity::class),
        (RatingStepEntity::class),
        (RatingRemontEntity::class),
        (RatingCommentEntity::class),
        (RequestEntity::class),
        (RequestCheckListEntity::class),
        (RequestListEntity::class),
        (RequestCheckListHistoryEntity::class),
        (RequestCheckAcceptEntity::class),
        (CheckRequestPhotoEntitiy::class),
        (PhotoDraftStatusEntity::class)
    ], version = 147, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remontDao(): RemontDao
    abstract fun stageDao(): StageDao
    abstract fun roomDao(): RoomDao
    abstract fun standartDao(): StandartPhotoDao
    abstract fun stageStatusDao(): StageStatusDao
    abstract fun remontStatusDao(): RemontStatusDao
    abstract fun checkListDao(): CheckListDao
    abstract fun remontCheckListDao(): RemontCheckListDao
    abstract fun checkListHistoryDao(): CheckListHistoryDao
    abstract fun remontListDao(): RemontListDao
    abstract fun photoDao(): UserDefectMediaDao
    abstract fun stageChatDao(): StageChatDao
    abstract fun stageChatFileDao(): StageChatFileDao
    abstract fun remontRoomDao(): RemontRoomDao
    abstract fun stageStatusHistoryDao(): StageStatusHistoryDao
    abstract fun notificationDao(): NotificationDao
    abstract fun groupChatDao(): GroupChatDao
    abstract fun reportTypeDao(): ReportTypeDao
    abstract fun ratingRemontDao(): RatingRemontDao
    abstract fun ratingDetailDao(): RatingDetailDao
    abstract fun ratingStepDao(): RatingStepDao
    abstract fun ratingCommentDao(): RatingCommentDao
    abstract fun requestDao(): RequestDao
    abstract fun requestCheckListDao(): RequestCheckListDao
    abstract fun requestListDao(): RequestListDao
    abstract fun requestCheckListHistoryDao(): RequestCheckListHistoryDao
    abstract fun requestCheckAcceptDao(): RequestCheckAcceptDao
    abstract fun requestCheckPhotoDao(): CheckRequestPhotoDao
    abstract fun photoDraftStatusDao(): PhotoDraftStatusDao
}