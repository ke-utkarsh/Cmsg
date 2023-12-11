package ymsli.com.cmsg.views.userengagement

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author  Sushant Somani
 * @date   Feb 04, 2022
 * Copyright (c) 2022, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 *  * ProgressFragment : This class is responsilbe for showing progress bar which
 *                      which is not a generic android progress bar but an animation.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ymsli.com.cmsg.R

class ProgressFragment: DialogFragment() {

    companion object{
        const val TAG = "ProgressFragment"
        fun newInstance(): ProgressFragment {
            val args = Bundle()
            val fragment =
                ProgressFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.progress_layout, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}