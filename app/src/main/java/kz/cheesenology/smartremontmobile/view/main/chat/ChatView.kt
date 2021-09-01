package kz.cheesenology.smartremontmobile.view.main.chat

import kz.cheesenology.smartremontmobile.model.ChatMessageListModel
import moxy.MvpView


interface ChatView : MvpView {
    fun setList(it: MutableList<ChatMessageListModel>)
    fun showEmptyView()
    fun clearInput()

}
