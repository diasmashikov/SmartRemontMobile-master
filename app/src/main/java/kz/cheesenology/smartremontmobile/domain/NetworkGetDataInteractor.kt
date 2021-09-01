package kz.cheesenology.smartremontmobile.domain

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.AppDatabase
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
import kz.cheesenology.smartremontmobile.data.rating.RatingDetailDao
import kz.cheesenology.smartremontmobile.data.rating.RatingDetailEntity
import kz.cheesenology.smartremontmobile.data.rating.comment.RatingCommentDao
import kz.cheesenology.smartremontmobile.data.rating.comment.RatingCommentEntity
import kz.cheesenology.smartremontmobile.data.rating.remont.RatingRemontDao
import kz.cheesenology.smartremontmobile.data.rating.remont.RatingRemontEntity
import kz.cheesenology.smartremontmobile.data.rating.step.RatingStepDao
import kz.cheesenology.smartremontmobile.data.rating.step.RatingStepEntity
import kz.cheesenology.smartremontmobile.data.remont.RemontDao
import kz.cheesenology.smartremontmobile.data.remont.RemontListDao
import kz.cheesenology.smartremontmobile.data.remont.RemontListEntity
import kz.cheesenology.smartremontmobile.data.remontroom.RemontRoomDao
import kz.cheesenology.smartremontmobile.data.remontroom.RemontRoomEntity
import kz.cheesenology.smartremontmobile.data.remontstatus.RemontStatusDao
import kz.cheesenology.smartremontmobile.data.remontstatus.RemontStatusEntity
import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeDao
import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeEntity
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
import kz.cheesenology.smartremontmobile.model.catalog.CatalogResponseModel
import kz.cheesenology.smartremontmobile.model.fulllist.FullListDataModel
import kz.cheesenology.smartremontmobile.network.NetworkApi
import javax.inject.Inject

class NetworkGetDataInteractor @Inject constructor(
    val networkApi: NetworkApi,
    val remontDao: RemontDao,
    val stageDao: StageDao,
    val reportTypeDao: ReportTypeDao,
    val standartDao: StandartPhotoDao,
    val roomDao: RoomDao,
    val checkListDao: CheckListDao,
    val remontStatusDao: RemontStatusDao,
    val stageStatusDao: StageStatusDao,
    val checkListHistoryDao: CheckListHistoryDao,
    val remontListDao: RemontListDao,
    val remontCheckListDao: RemontCheckListDao,
    val userDefectMediaDao: UserDefectMediaDao,
    val remontRoomDao: RemontRoomDao,
    val chatFileDao: StageChatFileDao,
    val chatDao: StageChatDao,
    val groupChatDao: GroupChatDao,
    val stageStatusHistoryDao: StageStatusHistoryDao,
    val ratingRemontDao: RatingRemontDao,
    val ratingDetailDao: RatingDetailDao,
    val ratingCommentDao: RatingCommentDao,
    val ratingStepDao: RatingStepDao,
    val appDatabase: AppDatabase
) {
    //Данные по ремонтам
    fun setRemontDataToDB(data: FullListDataModel): Completable {
        return Completable.merge {
            val defectList: ArrayList<UserDefectMediaEntity> = arrayListOf()
            val def = data.value.defectList
            def?.forEach {
                defectList.add(
                    UserDefectMediaEntity(
                        remontID = it.remont_id,
                        checkListID = it.check_list_id,
                        fileUrl = it.file_url,
                        fileType = it.file_type,
                        fileName = it.file_name,
                        audioName = it.audio_name,
                        audioUrl = it.audio_url,
                        comment = it.comments,
                        defectStatus = it.is_accepted,
                        dateCreate = it.date_create,
                        isForSend = 0,
                        stage_id = it.stage_id!!
                    )
                )
            }

            val historyList: ArrayList<CheckListHistoryEntity> = arrayListOf()
            val his = data.value.checkListHis
            if (his != null) {
                for (list in his) {
                    historyList.add(
                        CheckListHistoryEntity(
                            list.remontCheckListHistId!!,
                            list.remontID!!,
                            list.checkListID!!,
                            list.roomID!!,
                            list.defectCnt,
                            list.isAccepted,
                            list.description,
                            list.dateCreate
                        )
                    )
                }
            }

            val remontCheckList: ArrayList<RemontCheckListEntity> = arrayListOf()
            val rem = data.value.checkList
            if (rem != null) {
                for (list in rem) {
                    remontCheckList.add(
                        RemontCheckListEntity(
                            remontID = list.remontId,
                            checkListID = list.checkListId,
                            checkListPID = list.checkListPid,
                            checkName = list.checkName,
                            norm = list.norm,
                            isRoom = list.isRoom,
                            stageID = list.stageId,
                            isActive = list.isActive,
                            roomID = list.roomId,
                            audioInfo = list.audioInfo,
                            audioName = list.audioName,
                            isAudioForSend = 0,
                            defectCnt = list.defectCnt,
                            description = list.description,
                            isAccepted = list.isAccepted,
                            isForSend = 0
                        )
                    )
                }
            }

            val remontList: ArrayList<RemontListEntity> = arrayListOf()
            val remList = data.value.remontList
            if (remList != null) {
                for (list in remList) {
                    remontList.add(
                        RemontListEntity(
                            remontID = list.remontId!!,
                            clientRequestID = list.clientRequestId,
                            remontStatusID = list.remontStatusId,
                            info = list.info,
                            okkStatus = list.okkStatus,
                            isOKKStatusChange = 0,
                            okkEmployeeID = list.okkEmployeeId,
                            okkSendDate = list.okkSendDate,
                            okkAnswerDate = list.okkAnswerDate,
                            contractorSendDate =  list.contractorSendDate,
                            contractorAnswerDate = list.contractorAnswerDate,
                            address = list.address,
                            remontDateBegin = list.remontDateBegin,
                            price = list.price,
                            okkStatusText = list.okkStatusText,
                            contractorName = list.contractorName,
                            clientName = list.clientName,
                            statusName = list.statusName,
                            activeStageName = list.activeStageName,
                            activeStageID = list.activeStageId,
                            activeStageStatusName = list.activeStageStatusName,
                            fio =  list.fio,
                            stageStatusID = list.stageStatusId,
                            stageStatusComment = null,
                            stageStatusDesc = "0",
                            sendStatus = 0,
                            errorText = null,
                            isStageForSend = 0,
                            constructorUrl = list.constructorUrl,
                            planirovkaImage =  list.planirovkaImage,
                            planirovkaImageUrl =  list.planirovkaImageURL,
                            managerFIO =  list.managerFIO,
                            managerPhone =  list.managerPhone,
                            contractorPhone =  list.contractorPhone,
                            projectRemontName =  list.projectRemontName,
                            internalMaster = list.internalMaster,
                            internalMasterPhone = list.internalMasterPhone
                        )
                    )
                }
            }

            val remontRoomList: ArrayList<RemontRoomEntity> = arrayListOf()
            val remontRoom = data.value.remontRoomList
            if (remontRoom != null) {
                for (list in remontRoom) {
                    remontRoomList.add(
                        RemontRoomEntity(
                            roomID = list.roomID,
                            remontID = list.remontID
                        )
                    )
                }
            }

            //CHAT MESSAGE
            val chatList: ArrayList<StageChatEntity> = arrayListOf()
            val chatItem = data.value.groupChatList
            if (chatItem != null) {
                for (list in chatItem) {
                    chatList.add(
                        StageChatEntity(
                            stageChatID = list.stage_chat_id,
                            groupChatID = list.group_chat_id!!,
                            employeeID = list.employee_id,
                            client_id = list.client_id,
                            dateChat = list.date_chat,
                            message = list.message,
                            fio = list.chat_fio,
                            remontID = list.remont_id
                        )
                    )
                }
            }
            //CHAT FILE
            val chatFileList: ArrayList<StageChatFileEntity> = arrayListOf()
            val chatFileItem = data.value.stageChatFileList
            if (chatFileItem != null) {
                for (list in chatFileItem) {
                    chatFileList.add(
                        StageChatFileEntity(
                            stageChatFileID = list.stage_chat_file_id,
                            stageChatID = list.stage_chat_id,
                            file_name = list.file_name,
                            file_ext = list.file_ext,
                            file_url = list.file_url,
                                chatMessageID = 1
                        )
                    )
                }
            }
            //STAGE STATUS HISTORY
            val stageStatusHistList: ArrayList<StageStatusHistoryEntity> = arrayListOf()
            val stageStatusHistItem = data.value.stageStatusHistList
            if (stageStatusHistItem != null) {
                for (list in stageStatusHistItem) {
                    stageStatusHistList.add(
                        StageStatusHistoryEntity(
                            remontID = list.remont_id,
                            stageID = list.stage_id,
                            stageStatusID = list.stage_status_id,
                            dateCreate = list.date_create!!,
                            fio = list.fio,
                            comment = list.comments,
                            remarkName = list.remarkName
                        )
                    )
                }
            }

            val ratingDetailList: ArrayList<RatingDetailEntity> = arrayListOf()
            val ratingDetail = data.value.ratingDetailList
            ratingDetail?.forEach { item ->
                ratingDetailList.add(
                    RatingDetailEntity(
                        ratingDetailID = item.rt_detail_id!!,
                        roleID = item.rt_role_id!!,
                        detailName = item.rt_detail_name!!,
                        detailCode = item.rt_detail_code!!,
                        detailWeight = item.rt_detail_weight!!
                    )
                )
            }

            val ratingStepList: ArrayList<RatingStepEntity> = arrayListOf()
            val ratingStep = data.value.ratingStepList
            ratingStep?.forEach { item ->
                ratingStepList.add(
                    RatingStepEntity(
                        ratingStepID = item.rt_step_id!!,
                        ratingDetailID = item.rt_detail_id!!,
                        stepName = item.rt_step_name!!,
                        stepOrder = item.rt_step_order!!
                    )
                )
            }

            val ratingRemontList: ArrayList<RatingRemontEntity> = arrayListOf()
            val ratingRemont = data.value.ratingRemontList
            ratingRemont?.forEach { item ->
                ratingRemontList.add(
                    RatingRemontEntity(
                        ratingRemontID = item.rt_remont_id!!,
                        remontID = item.remont_id!!,
                        stepID = item.rt_step_id!!,
                        contractorID = item.contractor_id!!,
                        isEdit = item.is_edit!!
                    )
                )
            }

            val ratingCommentList: ArrayList<RatingCommentEntity> = arrayListOf()
            val ratingComment = data.value.ratingCommentList
            ratingComment?.forEach { item ->
                ratingCommentList.add(
                    RatingCommentEntity(
                        ratingCommentID = item.rt_remont_comment_id!!,
                        remont_id = item.remont_id!!,
                        roleID = item.rt_role_id!!,
                        comments = item.comments
                    )
                )
            }

            //DELETE ALL DATA FROM DB except catalogs
            clearAllData()

            appDatabase.runInTransaction {
                checkListHistoryDao.insertAll(historyList)
                remontCheckListDao.insertAll(remontCheckList)
                remontListDao.insertAll(remontList)
                userDefectMediaDao.insertAll(defectList)
                remontRoomDao.insertAll(remontRoomList)
                chatDao.insertAll(chatList)
                chatFileDao.insertAll(chatFileList)
                stageStatusHistoryDao.insertAll(stageStatusHistList)

                ratingCommentDao.insertAll(ratingCommentList)
                ratingDetailDao.insertAll(ratingDetailList)
                ratingRemontDao.insertAll(ratingRemontList)
                ratingStepDao.insertAll(ratingStepList)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    //Занесение справочников
    @SuppressLint("CheckResult")
    fun setCatalogsInfo(data: CatalogResponseModel): Completable {
        return Completable.merge {
            val stageList: ArrayList<StageEntity> = arrayListOf()
            val stage = data.value.stageList
            if (stage != null) {
                for (list in stage) {
                    stageList.add(
                        StageEntity(
                            list.stageId!!,
                            list.stageName!!,
                            list.stageCode!!,
                            list.stageOrderNum!!,
                            list.stageShortName!!
                        )
                    )
                }
            }

            val standartList: ArrayList<StandartPhotoEntity> = arrayListOf()
            val standart = data.value.standartList
            if (standart != null) {
                for (list in standart) {
                    standartList.add(
                        StandartPhotoEntity(
                            list.checkListStandartId!!,
                            list.checkListId!!,
                            list.photoUrl!!,
                            list.photoComment!!,
                            list.photoName!!,
                            list.isGood!!
                        )
                    )
                }
            }

            val roomList: ArrayList<RoomEntity> = arrayListOf()
            val room = data.value.roomList
            if (room != null) {
                for (item in room) {
                    roomList.add(
                        RoomEntity(
                            item.roomId,
                            item.roomName,
                            item.roomCode,
                            item.orderNum,
                            item.isFictive
                        )
                    )
                }
            }

            val checkList: ArrayList<CheckListEntity> = arrayListOf()
            val check = data.value.checkList
            if (check != null) {
                for (item in check) {
                    checkList.add(
                        CheckListEntity(
                            item.checkListId,
                            item.checkListPid,
                            item.stageId!!,
                            item.checkName!!,
                            item.norm,
                            item.isRoom!!,
                            item.isActive!!
                        )
                    )
                }
            }

            val remontStatusList: ArrayList<RemontStatusEntity> = arrayListOf()
            val remont = data.value.remontStatusList
            if (remont != null) {
                for (item in remont) {
                    remontStatusList.add(
                        RemontStatusEntity(
                            item.remontStatusId!!,
                            item.statusName,
                            item.statusCode
                        )
                    )
                }
            }

            val stageStatusList: ArrayList<StageStatusEntity> = arrayListOf()
            val stageStatus = data.value.stageStatusList
            if (stageStatus != null) {
                for (item in stageStatus) {
                    stageStatusList.add(
                        StageStatusEntity(
                            item.stageStatusId!!,
                            item.statusName,
                            item.statusCode,
                            item.what
                        )
                    )
                }
            }

            val groupChatList: ArrayList<GroupChatEntity> = arrayListOf()
            val groupChat = data.value.groupChatList
            if (groupChat != null) {
                for (item in groupChat) {
                    groupChatList.add(
                        GroupChatEntity(
                            groupChatID = item.groupChatID!!,
                            groupChatName = item.groupChatName,
                            groupChatCode = item.groupChatCode,
                            groupChatOrderNum = item.groupChatOrderNum,
                            groupChatShortName = item.groupChatShortName,
                            stageID = item.stageID
                        )
                    )
                }
            }

            val reportTypeList: ArrayList<ReportTypeEntity> = arrayListOf()
            val reportType = data.value.reportTypeList
            reportType?.forEach { item ->
                reportTypeList.add(
                    ReportTypeEntity(
                        reportTypeID = item.reportTypeID!!,
                        reportTypeCode = item.reportTypeCode!!,
                        reportTypeName = item.reportTypeName!!
                    )
                )
            }

            stageDao.delete()
            standartDao.delete()
            roomDao.delete()
            checkListDao.delete()
            remontStatusDao.delete()
            stageStatusDao.delete()
            groupChatDao.delete()
            reportTypeDao.delete()

            appDatabase.runInTransaction {
                stageDao.insertAll(stageList)
                standartDao.insertAll(standartList)
                roomDao.insertAll(roomList)
                checkListDao.insertAll(checkList)
                remontStatusDao.insertAll(remontStatusList)
                stageStatusDao.insertAll(stageStatusList)
                groupChatDao.insertAll(groupChatList)
                reportTypeDao.insertAll(reportTypeList)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    private fun clearAllData() {
        ratingRemontDao.delete()
        ratingCommentDao.delete()
        checkListHistoryDao.delete()
        stageStatusHistoryDao.delete()
        remontCheckListDao.delete()
        remontListDao.delete()
        userDefectMediaDao.delete()
        remontRoomDao.delete()
        chatDao.delete()
        chatFileDao.delete()
    }
}