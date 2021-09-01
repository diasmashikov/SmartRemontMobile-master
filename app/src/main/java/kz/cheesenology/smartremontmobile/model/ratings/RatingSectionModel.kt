package kz.cheesenology.smartremontmobile.model.ratings

class RatingSectionModel {
    var row: RatingListModel? = null
        private set
    var section: String? = null
        private set
    var isRow: Boolean = false
        private set

    companion object {
        fun createRow(row: RatingListModel): RatingSectionModel {
            val ret = RatingSectionModel()
            ret.row = row
            ret.isRow = true
            return ret
        }

        fun createSection(section: String?): RatingSectionModel {
            val ret = RatingSectionModel()
            ret.section = section
            ret.isRow = false
            return ret
        }
    }
}