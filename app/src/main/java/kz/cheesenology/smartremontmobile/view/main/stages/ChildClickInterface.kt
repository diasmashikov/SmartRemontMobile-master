package kz.cheesenology.smartremontmobile.view.main.stages

import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModelNew

interface ChildClickInterface {
    fun onChildLongClick()
    fun onChildClick(info: CheckListChildModelNew, globalPosition: Int)
    fun acceptStatus(info: CheckListChildModel, globalPosition: Int)
    fun cancelStatus(info: CheckListChildModel, globalPosition: Int)
}
