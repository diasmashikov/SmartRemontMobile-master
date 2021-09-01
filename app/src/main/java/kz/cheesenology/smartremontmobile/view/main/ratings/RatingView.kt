package kz.cheesenology.smartremontmobile.view.main.ratings

import kz.cheesenology.smartremontmobile.model.ratings.RatingListAdapterModel
import moxy.MvpView

interface RatingView : MvpView {
    fun setViewData(list: MutableList<RatingListAdapterModel>)
    fun showToast(s: String)
    fun successPutRatings(s: String)

}