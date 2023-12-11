package ymsli.com.cmsg.di.component

import dagger.Component
import ymsli.com.cmsg.di.ViewModelScope
import ymsli.com.cmsg.di.module.ViewHolderModule
import ymsli.com.cmsg.viewholder.MessageViewHolder
import ymsli.com.cmsg.viewholder.SentMessageDashboardViewHolder

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author VE00YM129
 * @date   Jan 10, 2022
 * Copyright (c) 2022, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ViewHolderComponent : This is the viewholder component of dagger2 framework. This is
 *                      responsible for injecting objects in the viewholder of RV.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */
@ViewModelScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ViewHolderModule::class]
)
interface ViewHolderComponent {
    fun inject(messageViewHolder: MessageViewHolder)

    fun inject(sentMessageDashboardViewHolder: SentMessageDashboardViewHolder)
}