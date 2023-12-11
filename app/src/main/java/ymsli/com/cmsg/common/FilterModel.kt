package ymsli.com.cmsg.common

import java.sql.Timestamp


/**
 * @constructor
 * @param selectedBtnIndex index of the selected button in ReturnTaskListFilterDialog, -1 if no button is selected
 * @param taskStatusId status id to be be used for applyReturnListFilter, can be one of TaskStatus enum's statusId
 * @param toDate
 */
data class FilterModel(var selectedBtnIndex: Int, var taskStatusId: Int?,
                       var toDate: Timestamp?, var fromDate: Timestamp?){
    fun clone(): FilterModel {
        return FilterModel(
            this.selectedBtnIndex,
            this.taskStatusId,
            this.toDate,
            this.fromDate
        )
    }

    fun equals(o: FilterModel): Boolean{
        var selectedBtnPredicate = this.selectedBtnIndex == o.selectedBtnIndex
        var fromDatePredicate: Boolean = if((this.fromDate == null) && (o.fromDate == null)) true
        else if((this.fromDate == null) && (o.fromDate != null)) false
        else if((this.fromDate != null) && (o.fromDate == null)) false
        else (this.fromDate!!.equals(o.fromDate))

        var toDatePredicate: Boolean = if((this.toDate == null) && (o.toDate == null)) true
        else if((this.toDate == null) && (o.toDate != null)) false
        else if((this.toDate != null) && (o.toDate == null)) false
        else (this.toDate!!.equals(o.toDate))

        return selectedBtnPredicate && fromDatePredicate && toDatePredicate
    }

    fun isSet(): Boolean{
        return (selectedBtnIndex != -1 || taskStatusId != -1 || toDate != null || fromDate != null)
    }

    fun isNotSet(): Boolean{
        return (selectedBtnIndex == -1 && taskStatusId == -1 && toDate == null && fromDate == null)
    }

    companion object{
        fun getDefaultState(): FilterModel{
            return FilterModel(selectedBtnIndex = -1, taskStatusId = -1, toDate = null, fromDate = null)
        }
    }
}