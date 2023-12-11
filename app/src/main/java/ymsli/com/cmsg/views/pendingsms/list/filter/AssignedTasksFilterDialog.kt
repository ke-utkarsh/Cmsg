package ymsli.com.couriemate.views.tasklist.filter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ymsli.com.cmsg.R
import ymsli.com.cmsg.common.FilterModel

/**
 * Project Name : Couriemate
 * @company YMSLI
 * @author  Balraj Singh (VE00YM023)
 * @date   26/02/2020
 * Copyright (c) 2020, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -------------------------------------------------------------------------------------
 * AssignedTasksFilterDialog : BottomSheetDialogFragment implementation specifically for
 *                             Assigned tasks tab
 * -------------------------------------------------------------------------------------
 * Revision History
 * -------------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -------------------------------------------------------------------------------------
 */
class AssignedTasksFilterDialog: BottomSheetDialogFragment() {

    lateinit var layout: View
        private set
    private lateinit var actor: Actor
    private lateinit var backgroundButtonPressed: Drawable
    private lateinit var button: Button
    private lateinit var text: TextView

    private val blueColor = Color.parseColor("#2079E5")

    /** Maintain the current as well as the previous state of this filter dialog */
    private var currentState  = FilterModel.getDefaultState()
    private var previousState = FilterModel.getDefaultState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundButtonPressed = resources.getDrawable(R.drawable.shape_filter_btns_pressed)
    }

    /**
     * When this dialog is attached to any activity, then use the context as an implementation
     * of Actor interface, which will be used for communication between activity and
     * this dialog.
     *
     * @author Balraj VE00YM023
     */

    override fun onAttach(context: Context) {
        actor = context as Actor
        super.onAttach(context)
    }


    /**
     * When view is created set up the layout for appropriate filter and then
     * init the UI based on the previous state of this filter dialog.
     * @author Balraj VE00YM023
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        layout = inflater.inflate(R.layout.layout_filter_dialog_assigned_tasks, container, false) as View
     //   initButtonsAndTexts(layout)
        previousState = actor.getAssignedTasksFilterPreviousState()
        currentState = previousState.clone()
        text.text = actor.getCompanyAddress()
        setDialogViewBasedOnPreviousState(previousState)
        return layout
    }

    /**
     * Init the state of filter buttons and texts.
     * @author Balraj VE00YM023
     */
    private fun initButtonsAndTexts(layout: View){
//        button = layout.findViewById(R.id.btn_filter_dialog_couriemate_address)
//        text   = layout.findViewById(R.id.text_filter_dialog_couriemate_address)
//        button.setOnClickListener{btn -> filterButtonOnClick(btn as Button, 0)}
//        layout.findViewById<Button>(R.id.btn_filter_dialog_clear).setOnClickListener{resetClearState()}
//        layout.findViewById<Button>(R.id.btn_filter_dialog_apply).setOnClickListener{actor.applyAssignedTasksFilter(currentState)}
//        layout.findViewById<Button>(R.id.btn_filter_dialog_close).setOnClickListener{this.dismiss()}
    }

    /**
     * Configure the dialog UI based on the previous state.
     * if any filter was applied then highlight the corresponding filter buttons and texts.
     * @author Balraj VE00YM023
     */
    private fun setDialogViewBasedOnPreviousState(previousState: FilterModel){
        /* If Some applyReturnListFilter was set then reset that view */
        if(previousState.selectedBtnIndex != -1){
            setButtonColor(button, blueColor, blueColor)
            setTextColor(text, Color.parseColor("#000000"))
            setClearState()
        }
        else{
//            layout.findViewById<Button>(R.id.btn_filter_dialog_clear).visibility = View.GONE
//            var applyBtn = layout.findViewById<Button>(R.id.btn_filter_dialog_apply)
//            applyBtn.isClickable = false
//            applyBtn.setBackgroundColor(Color.parseColor("#AAAAAA"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(currentState.selectedBtnIndex != -1){
            setButtonColor(button, Color.parseColor("#EEEEEE"), Color.parseColor("#AAAAAA"))
        }
    }

    /**
     * When any filter button is clicked change its color and the color of corresponding text.
     * also reset the color of other buttons and texts.
     * update the currentState to include current selectedIndex.
     * @author Balraj VE00YM023
     */
    private fun filterButtonOnClick(btn: Button, index: Int){
        setButtonColor(btn, Color.parseColor("#2079E5"), Color.parseColor("#2079E5"))
        setTextColor(text,  Color.parseColor("#000000"))
        currentState.selectedBtnIndex = index
        setClearState()
    }

    /**
     * Sets the color of filter button.
     * @author Balraj VE00YM023
     */
    private fun setButtonColor(btn: Button, borderColor: Int, iconTint: Int){
        var bg = btn.background.mutate() as GradientDrawable
        bg.setStroke(4, borderColor)
        var icon = btn.compoundDrawables[1] as VectorDrawable
        icon.setTint(iconTint)
    }

    private fun setTextColor(tv: TextView, color: Int){
        tv.setTextColor(color)
    }

    /**
     * Show the clear button on the header of FilterDialog.
     * @author Balraj VE00YM023
     */
    private fun setClearState() {
//        layout.findViewById<Button>(R.id.btn_filter_dialog_clear).visibility = View.VISIBLE
//        setApplyButtonStatus()
    }

    private fun setApplyButtonStatus(){
//        var applyBtn = layout.findViewById<Button>(R.id.btn_filter_dialog_apply)
//        if(currentState.selectedBtnIndex == previousState.selectedBtnIndex){
//            applyBtn.isClickable = false
//            applyBtn.setBackgroundColor(Color.parseColor("#AAAAAA"))
//            applyBtn.setTextColor(Color.parseColor("#DDDDDD"))
//        }
//        else{
//            applyBtn.isClickable = true
//            applyBtn.setBackgroundColor(Color.parseColor("#2079E5"))
//            applyBtn.setTextColor(Color.parseColor("#FFFFFF"))
//        }
    }

    /**
     * Reset the clear filter button to default state(hidden).
     * @author Balraj VE00YM023
     */
    private fun resetClearState(){
//        layout.findViewById<Button>(R.id.btn_filter_dialog_clear).visibility = View.GONE
        resetAddressButton()
        setApplyButtonStatusOnClear()
    }

    private fun resetAddressButton(){
        if(currentState.selectedBtnIndex!=-1){
            setButtonColor(button, Color.parseColor("#EEEEEE"), Color.parseColor("#AAAAAA"))
            setTextColor(text, Color.parseColor("#8a000000"))
            currentState.selectedBtnIndex = -1
        }
    }

    /**
     * Set the color as well as the state of apply button based on the current state
     * and previous state. if current state and previous state are equal then disable the
     * apply button as there is no change in the state.
     * @author Balraj VE00YM023
     */
    private fun setApplyButtonStatusOnClear(){
//        var applyBtn = layout.findViewById<Button>(R.id.btn_filter_dialog_apply)
//        if(currentState.equals(previousState)){
//            applyBtn.isClickable = false
//            applyBtn.setBackgroundColor(Color.parseColor("#AAAAAA"))
//            applyBtn.setTextColor(Color.parseColor("#DDDDDD"))
//        }
//        else{
//            applyBtn.isClickable = true
//            applyBtn.setBackgroundColor(Color.parseColor("#2079E5"))
//            applyBtn.setTextColor(Color.parseColor("#FFFFFF"))
//        }
    }


    /**
     * User of this filter dialog must implement this interface in order to provide a
     * communication channel.
     * @author Balraj VE00YM023
     */
    interface Actor{
        fun applyAssignedTasksFilter(predicate: FilterModel)
        fun getAssignedTasksFilterPreviousState(): FilterModel
        fun getCompanyAddress(): String
    }
}