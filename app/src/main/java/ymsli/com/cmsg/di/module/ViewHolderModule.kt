package ymsli.com.cmsg.di.module

import androidx.lifecycle.LifecycleRegistry
import dagger.Module
import dagger.Provides
import ymsli.com.cmsg.base.BaseItemViewHolder
import ymsli.com.cmsg.di.ViewModelScope

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author VE00YM129
 * @date   Feb 03, 2022
 * Copyright (c) 2022, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ViewHolderModule : This is the view holder module of dagger2 framework. This is
 *                      responsible for providing objects with @Inject annotation
 *                      in the view holder.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */
@Module
class ViewHolderModule(private val viewHolder: BaseItemViewHolder<*, *>)  {

    @Provides
    @ViewModelScope
    fun provideLifecycleRegistry(): LifecycleRegistry = LifecycleRegistry(viewHolder)
}