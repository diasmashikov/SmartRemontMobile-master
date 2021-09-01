package kz.cheesenology.smartremontmobile.network

import io.reactivex.Flowable
import io.reactivex.Observable
import kz.cheesenology.smartremontmobile.model.*
import kz.cheesenology.smartremontmobile.model.catalog.CatalogResponseModel
import kz.cheesenology.smartremontmobile.model.check.UploadCheckDataModel
import kz.cheesenology.smartremontmobile.model.fulllist.FullListDataModel
import kz.cheesenology.smartremontmobile.model.request.JSONRequestListModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface NetworkApi {

    @FormUrlEncoded
    @POST("/rest/login")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun signInPostUrl(
        @Field("login") login: String,
        @Field("password") pswd: String,
        @Field("token") token: String?,
        @Field("token_id") id: String?
    ): Observable<Response<ResultAuthModel>>

    @FormUrlEncoded
    @POST("/rest/change-remont-okk-status")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun acceptRemontStatus(
        @Field("login") login: String,
        @Field("password") pswd: String,
        @Field("remont_id") remontId: Int?,
        @Field("is_accept") accept: String
    ): Flowable<Response<FullListDataModel>>

    @FormUrlEncoded
    @POST("/rest/del-remont-check-list-audio/check_list_id/{check_list_id}/remont_id/{remont_id}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun deleteAudioFromServer(
        @Field("login") login: String,
        @Field("password") pswd: String,
        @Path("check_list_id") remont_check_list_id: Int,
        @Path("remont_id") remontID: Int
    ): Flowable<Response<DefaultResponseModel>>

    @FormUrlEncoded
    @POST("/rest/push-notification-list")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getNotificationList(
        @Field("login") login: String,
        @Field("password") password: String
    ): Flowable<Response<NotificationListModel>>

    @FormUrlEncoded
    @POST("/rest/offline-read-remont")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getFullDataList(
        @Field("login") login: String,
        @Field("password") pswd: String
    ): Observable<Response<FullListDataModel>>

    @FormUrlEncoded
    @POST("/rest/offline-read-info")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getCatalogs(
        @Field("login") login: String,
        @Field("password") pswd: String
    ): Flowable<Response<CatalogResponseModel>>

    @Multipart
    @POST("/rest/check-list-edit/login/{login}/password/{password}")
    fun uploadTaskCloseData(
        @Part image: List<MultipartBody.Part>,
        @Part audio: List<MultipartBody.Part>,
        @Part("info") json: RequestBody,
        @Path("login") login: String,
        @Path("password") password: String
    ): Flowable<Response<UploadCheckDataModel>>

    @Multipart
    @POST("/rest/check-list-processing/login/{login}/password/{password}")
    fun sendCheckListProcessing(
        @Path("login") login: String?,
        @Path("password") password: String?,
        @Part("info") sendJSON: RequestBody,
        @Part photoList: ArrayList<MultipartBody.Part>,
        @Part audioList: ArrayList<MultipartBody.Part>,
        @Part videoList: ArrayList<MultipartBody.Part>
    ): Flowable<Response<SendDataResultModel>>

    @Multipart
    @POST("/rest/check-list-edit/login/{login}/password/{password}")
    fun uploadTaskCloseDataOut(
        @Part("info") json: RequestBody,
        @Path("login") login: String,
        @Path("password") password: String
    ): Flowable<Response<UploadCheckDataModel>>

    @FormUrlEncoded
    @POST("/rest/del-remont-check-list-photo/defect_id/{defect_id}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun deletePhotoFromServer(
        @Field("login") login: String,
        @Field("password") pswd: String,
        @Path("defect_id") defect_id: Int
    ): Flowable<Response<DefaultResponseModel>>

    @GET("/rest/get-app-last-version/what/OKK")
    fun checkApkUpdate(): Flowable<Response<NetApkCheckModel>>

    @Streaming
    @GET
    fun downloadApk(@Url sURL: String): Call<ResponseBody>

    @Multipart
    @POST(
        "/restokk/request-from-mobile/" +
                "login/{login}/" +
                "password/{password}/" +
                "mobile_id/{mobile_id}/" +
                "random_num/{random_num}/" +
                "request_type_code/UPD_REMONT_RATING/"
    )
    fun sendRatings(
        @Path("login") login: String?,
        @Path("password") password: String?,
        @Path("mobile_id") mobileID: String?,
        @Path("random_num") randomNum: Int?,
        @Part("info") requestBody: RequestBody?
    ): Observable<Response<JsonSyncModel>>

    @Multipart
    @POST(
        "/restokk/request-from-mobile/" +
                "login/{login}/" +
                "password/{password}/" +
                "mobile_id/{mobile_id}/" +
                "random_num/{random_num}/" +
                "request_type_code/CHAT_SEND_OKK/"
    )
    fun sendMessage(
        @Path("login") login: String?,
        @Path("password") password: String?,
        @Path("mobile_id") mobileID: String?,
        @Path("random_num") randomNum: Int?,
        @Part("info") requestBody: RequestBody?
    ): Observable<Response<JsonSyncModel>>

    @FormUrlEncoded
    @POST("/rest/offline-read-client-request/")
   /* @POST("/restokk/request-from-mobile/" +
            "login/{login}/" +
            "password/{password}/" +
            "mobile_id/{mobile_id}/" +
            "random_num/{random_num}/" +
            "request_type_code/CHAT_SEND_OKK/")*/
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getRequestList(
        @Field("login") login: String?,
        @Field("password") password: String?
    ): Observable<Response<JSONRequestListModel>>

    @Multipart
    @POST("/restokk/request-from-mobile/" +
            "login/{login}/" +
            "password/{password}/" +
            "mobile_id/{mobile_id}/" +
            "random_num/{random_num}/" +
            "request_type_code/DRAFT_CHECK_LIST_PROCESSING/")
    fun sendRequestListToServer(
        @Path("login") login: String?,
        @Path("password") password: String?,
        @Path("mobile_id") mobile_id: String?,
        @Path("random_num") random_num: Int?,
        @Part("info") sendJSON: RequestBody,
        @Part photoList: ArrayList<MultipartBody.Part>,
        @Part defectPhoto: ArrayList<MultipartBody.Part>
    ): Flowable<Response<JsonSyncModel>>

}