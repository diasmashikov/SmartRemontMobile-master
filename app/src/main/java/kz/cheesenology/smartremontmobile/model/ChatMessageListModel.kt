package kz.cheesenology.smartremontmobile.model

import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity

data class ChatMessageListModel(
        var stageChatID: Int?,
        var stageID: Int,
        var fio: String?,
        var dateChat: String?,
        var message: String?,
        var remontID: Int?,
        var requestStatusID: Int?,
        var requestError: String?,
        var photoList: List<StageChatFileEntity>,
        var filesList: List<StageChatFileEntity>
)