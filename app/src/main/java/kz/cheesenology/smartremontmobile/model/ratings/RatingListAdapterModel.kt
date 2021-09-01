package kz.cheesenology.smartremontmobile.model.ratings

data class RatingListAdapterModel(
    var ratingDetailID: Int,
    var roleID: Int,
    var detailName: String,
    var detailCode: String,
    var detailWeight: Int,
    var isChecked: Boolean? = false,
    var stepCheckedID: Int? = 0,
    var stepList: MutableList<RatingStepListModel>?
)