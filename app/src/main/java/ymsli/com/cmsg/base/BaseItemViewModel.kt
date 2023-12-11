package ymsli.com.cmsg.base

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.smsapp.utils.rx.SchedulerProvider

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author VE00YM129
 * @date   Feb 03, 2022
 * Copyright (c) 2022, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * BaseItemViewModel : This abstract class is the base item view model of all the
 *                  item view model of recycler view, contains common code to all
 *                   view models of item of recycler view
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */

abstract class BaseItemViewModel<T : Any>(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper,smsAppRepository) {

    /* contains data to be populated in recycler view */
    val data: MutableLiveData<T> = MutableLiveData()

    fun onManualCleared() = onCleared()

    /** updates data populated in recycler view list */
    fun updateData(data: T) {
        this.data.postValue(data)
    }
}