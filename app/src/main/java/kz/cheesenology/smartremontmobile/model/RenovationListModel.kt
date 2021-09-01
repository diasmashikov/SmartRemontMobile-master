package kz.cheesenology.smartremontmobile.model

data class RenovationListModel(
        var client: String,
        var workGroup: String,
        var statusDate: String,
        var currentStage: String,
        var changeName: String
)