package kz.cheesenology.smartremontmobile.domain

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.AppDatabase
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
import kz.cheesenology.smartremontmobile.model.request.Value
import javax.inject.Inject

class NetworkSetRequestData @Inject constructor(
    val requestListDao: RequestListDao,
    val requestCheckListDao: RequestCheckListDao,
    val requestCheckListHistoryDao: RequestCheckListHistoryDao,
    val requestCheckAcceptDao: RequestCheckAcceptDao,
    val requestcheckPhotoDao: CheckRequestPhotoDao,
    val appDatabase: AppDatabase
) {

    fun setRequestDataToDB(data: Value): Completable {
        return Completable.merge {
            val requestList: ArrayList<RequestListEntity> = arrayListOf()
            val request = data.client_request_list
            request?.forEach {
                requestList.add(
                    RequestListEntity(
                        client_request_id = it!!.client_request_id,
                        remont_id = it.remont_id,
                        resident_name = it.resident_name,
                        resident_id = it.resident_id,
                        flat_num = it.flat_num,
                        flat_list_name = it.flat_list_name,
                        flat_list_url = it.flat_list_url,
                        manager_project_phone = it.manager_project_phone,
                        manager_project_id = it.manager_project_id,
                        manager_project_name = it.manager_project_name,
                        okk_id = it.okk_id,
                        okk_name = it.okk_name,
                        okk_date = it.okk_date,
                        resident_delivery_date = it.resident_delivery_date,
                        last_planned_date = it.last_planned_date,
                        is_draft_accept = it.is_draft_accept,
                        draft_status = it.draft_status,
                        okk_check_date = it.okk_check_date
                    )
                )
            }

            val requestHistoryList: ArrayList<RequestCheckListHistoryEntity> = arrayListOf()
            val history = data.draft_check_history_list
            history?.forEach {
                requestHistoryList.add(
                    RequestCheckListHistoryEntity(
                        client_request_draft_check_history_id = it!!.client_request_draft_check_history_id,
                        client_request_id = it.client_request_id,
                        employee_id = it.employee_id,
                        check_date = it.check_date,
                        draft_defect_file_name = it.file_name,
                        draft_defect_file_url = it.file_url,
                        okk_comment = it.okk_comment,
                        draft_status = it.draft_status,
                        client_request_document_id = it.client_request_document_id,
                        okk_fio = it.okk_fio,
                        is_for_send = 0,
                        okk_check_date = it.okk_check_date
                    )
                )
            }

            val requestCheckList: ArrayList<RequestCheckListEntity> = arrayListOf()
            val check = data.draft_check_list
            check?.forEach {
                requestCheckList.add(
                    RequestCheckListEntity(
                        draft_check_list_id = it!!.draft_check_list_id,
                        draft_check_list_pid = it.draft_check_list_pid,
                        draft_check_list_name = it.draft_check_list_name,
                        is_active = it.is_active
                    )
                )
            }

            val requestCheckAcceptList: ArrayList<RequestCheckAcceptEntity> = arrayListOf()
            val checkAccept = data.client_request_draft_check
            checkAccept?.forEach {
                requestCheckAcceptList.add(
                    RequestCheckAcceptEntity(
                        client_request_check_id = it!!.client_request_check_id,
                        client_request_id = it.client_request_id,
                        draft_check_list_id = it.draft_check_list_id,
                        employee_id = it.employee_id,
                        date_create = it.date_create,
                        is_accepted = it.is_accepted
                        /*content_url = it.content_url,
                        content_type = it.content_type,
                        file_name = it.file_name,
                        comments = it.comments*/
                    )
                )
            }

            val requestCheckPhotoList: ArrayList<CheckRequestPhotoEntitiy> = arrayListOf()
            val checkPhoto = data.client_request_check_file
            checkPhoto?.forEach {
                requestCheckPhotoList.add(
                    CheckRequestPhotoEntitiy(
                        requestCheckPhotoID = it!!.client_request_check_file_id,
                        requestCheckListID = it.client_request_check_id,
                        clientRequestID = it.client_request_id,
                        draft_check_list_id = it.draft_check_list_id,
                        requestCheckPhotoName = it.file_name,
                        requestCheckPhotoUrl = it.content_url
                    )
                )
            }


            requestCheckListDao.delete()
            requestListDao.delete()
            requestCheckListHistoryDao.delete()
            requestCheckAcceptDao.delete()
            requestcheckPhotoDao.delete()

            appDatabase.runInTransaction {
                requestCheckListDao.insertAll(requestCheckList)
                requestListDao.insertAll(requestList)
                requestcheckPhotoDao.insertAll(requestCheckPhotoList)
                requestCheckListHistoryDao.insertAll(requestHistoryList)
                requestCheckAcceptDao.insertAll(requestCheckAcceptList)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }
}