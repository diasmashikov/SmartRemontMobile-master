package kz.cheesenology.smartremontmobile.model

import kz.cheesenology.smartremontmobile.data.check.list.CheckListEntity
import kz.cheesenology.smartremontmobile.data.requestlist.checklist.RequestCheckListEntity

class RequestCheckListSectionModel {
    var row: RequestCheckListRoomModel? = null
        private set
    var section: String? = null
        private set
    var isRow: Boolean = false
        private set

    companion object {

        fun createRow(row: RequestCheckListRoomModel): RequestCheckListSectionModel {
            val ret = RequestCheckListSectionModel()
            ret.row = row
            ret.isRow = true
            return ret
        }

        fun createSection(section: String): RequestCheckListSectionModel {
            val ret = RequestCheckListSectionModel()
            ret.section = section
            ret.isRow = false
            return ret
        }
    }
}