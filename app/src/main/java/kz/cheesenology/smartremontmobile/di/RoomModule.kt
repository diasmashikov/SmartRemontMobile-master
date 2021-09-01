package kz.cheesenology.smartremontmobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import kz.cheesenology.smartremontmobile.data.AppDatabase
import kz.cheesenology.smartremontmobile.data.chat.StageChatDao
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileDao
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryDao
import kz.cheesenology.smartremontmobile.data.check.list.CheckListDao
import kz.cheesenology.smartremontmobile.data.groupchat.GroupChatDao
import kz.cheesenology.smartremontmobile.data.notification.NotificationDao
import kz.cheesenology.smartremontmobile.data.rating.RatingDetailDao
import kz.cheesenology.smartremontmobile.data.rating.comment.RatingCommentDao
import kz.cheesenology.smartremontmobile.data.rating.remont.RatingRemontDao
import kz.cheesenology.smartremontmobile.data.rating.step.RatingStepDao
import kz.cheesenology.smartremontmobile.data.remont.RemontDao
import kz.cheesenology.smartremontmobile.data.remont.RemontListDao
import kz.cheesenology.smartremontmobile.data.remontroom.RemontRoomDao
import kz.cheesenology.smartremontmobile.data.remontstatus.RemontStatusDao
import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeDao
import kz.cheesenology.smartremontmobile.data.request.RequestDao
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkaccept.RequestCheckAcceptDao
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryDao
import kz.cheesenology.smartremontmobile.data.requestlist.checklist.RequestCheckListDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoDao
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomDao
import kz.cheesenology.smartremontmobile.data.stage.StageDao
import kz.cheesenology.smartremontmobile.data.stagestatus.StageStatusDao
import kz.cheesenology.smartremontmobile.data.stagestatushist.StageStatusHistoryDao
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao


@Module
class RoomModule(val dbName: String) {

    @DatabaseScope
    @Provides
    internal fun providesRoomDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java,
            dbName
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @DatabaseScope
    @Provides
    internal fun provideRemontDao(appDatabase: AppDatabase): RemontDao {
        return appDatabase.remontDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideStageDao(appDatabase: AppDatabase): StageDao {
        return appDatabase.stageDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRoomDao(appDatabase: AppDatabase): RoomDao {
        return appDatabase.roomDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideStandartDao(appDatabase: AppDatabase): StandartPhotoDao {
        return appDatabase.standartDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideStageStatusDao(appDatabase: AppDatabase): StageStatusDao {
        return appDatabase.stageStatusDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRemontStatusDao(appDatabase: AppDatabase): RemontStatusDao {
        return appDatabase.remontStatusDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideCheckListDao(appDatabase: AppDatabase): CheckListDao {
        return appDatabase.checkListDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRemontListDao(appDatabase: AppDatabase): RemontListDao {
        return appDatabase.remontListDao()
    }

    @DatabaseScope
    @Provides
    internal fun providePhotoDao(appDatabase: AppDatabase): UserDefectMediaDao {
        return appDatabase.photoDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRemontCheckListDao(appDatabase: AppDatabase): RemontCheckListDao {
        return appDatabase.remontCheckListDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideCheckListHistoryDao(appDatabase: AppDatabase): CheckListHistoryDao {
        return appDatabase.checkListHistoryDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRemontRoomDao(appDatabase: AppDatabase): RemontRoomDao {
        return appDatabase.remontRoomDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideChatDao(appDatabase: AppDatabase): StageChatDao {
        return appDatabase.stageChatDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideChatFileDao(appDatabase: AppDatabase): StageChatFileDao {
        return appDatabase.stageChatFileDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideStageStatusHistoryDao(appDatabase: AppDatabase): StageStatusHistoryDao {
        return appDatabase.stageStatusHistoryDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideGroupChatDao(appDatabase: AppDatabase): GroupChatDao {
        return appDatabase.groupChatDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideReportTypeDao(appDatabase: AppDatabase): ReportTypeDao {
        return appDatabase.reportTypeDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRatingDetailDao(appDatabase: AppDatabase): RatingDetailDao {
        return appDatabase.ratingDetailDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRatingStepDao(appDatabase: AppDatabase): RatingStepDao {
        return appDatabase.ratingStepDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRatingRemontDao(appDatabase: AppDatabase): RatingRemontDao {
        return appDatabase.ratingRemontDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRatingCommentDao(appDatabase: AppDatabase): RatingCommentDao {
        return appDatabase.ratingCommentDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRequestDao(appDatabase: AppDatabase): RequestDao {
        return appDatabase.requestDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRequestCheckListDao(appDatabase: AppDatabase): RequestCheckListDao {
        return appDatabase.requestCheckListDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRequestListDao(appDatabase: AppDatabase): RequestListDao {
        return appDatabase.requestListDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRequestCheckListHistoryDao(appDatabase: AppDatabase): RequestCheckListHistoryDao {
        return appDatabase.requestCheckListHistoryDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRequestCheckAcceptDao(appDatabase: AppDatabase): RequestCheckAcceptDao {
        return appDatabase.requestCheckAcceptDao()
    }

    @DatabaseScope
    @Provides
    internal fun provideRequestCheckPhotoDao(appDatabase: AppDatabase): CheckRequestPhotoDao {
        return appDatabase.requestCheckPhotoDao()
    }

    @DatabaseScope
    @Provides
    internal fun providePhotoDraftStatusDao(appDatabase: AppDatabase): PhotoDraftStatusDao {
        return appDatabase.photoDraftStatusDao()
    }
}