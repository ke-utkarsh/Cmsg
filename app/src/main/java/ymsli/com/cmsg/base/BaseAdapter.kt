package ymsli.com.cmsg.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author VE00YM129
 * @date   January 10, 2022
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * BaseAdapter : This abstract class is the base adapter of all the RV adapters
 *                  and contains common to provide in recycler view adapters
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */
abstract class BaseAdapter<T : Any, VH : BaseItemViewHolder<T, out BaseItemViewModel<T>>>(
    parentLifecycle: Lifecycle,
    data: ArrayList<T>
) : RecyclerView.Adapter<VH>() {

    var dataList: ArrayList<T>

    private var recyclerView: RecyclerView? = null

    init {
        //handles RV lifecycle upon view destruction
        parentLifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onParentDestroy() {
                recyclerView?.run {
                    for (i in 0 until childCount) {
                        getChildAt(i)?.let {
                            (getChildViewHolder(it) as BaseItemViewHolder<*, *>)
                                .run {
                                    onDestroy()
                                    viewModel.onManualCleared()
                                }
                        }
                    }
                }
            }

            //handles RV lifecycle upon view stop
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onParentStop() {
                recyclerView?.run {
                    for (i in 0 until childCount) {
                        getChildAt(i)?.let {
                            (getChildViewHolder(it) as BaseItemViewHolder<*, *>).onStop()
                        }
                    }
                }
            }

            //handles RV lifecycle upon view start
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onParentStart() {
                recyclerView?.run {
                    if (layoutManager is LinearLayoutManager) {
                        val first = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        val last = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        if (first in 0..last)
                            for (i in first..last) {
                                findViewHolderForAdapterPosition(i)?.let {
                                    (it as BaseItemViewHolder<*, *>).onStart()
                                }
                            }
                    }
                }
            }
        })

        this.dataList = data
    }

    /** handles recycler view attachment in the activity/fragment */
    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        holder.onStart()
    }

    /** handles recycler view detachment in the activity/fragment */
    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        holder.onStop()
    }

    /** handles recycler view attachment in the activity/fragment */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    /** handles recycler view detachment in the activity/fragment */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(dataList[position],position)
    }

    /** loads data in adapter of recycler view */
    open fun loadData(data: ArrayList<T>) {
        this.dataList = data
        notifyDataSetChanged()
    }

    /** clears data in adapter of recycler view */
    open fun clearData(){
        this.dataList.clear()
        notifyDataSetChanged()
    }
}