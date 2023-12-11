package ymsli.com.cmsg.views.pendingsms.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SubscriptionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ymsli.com.cmsg.R
import ymsli.com.cmsg.adapter.MessageListAdapter
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.common.SendSMSService
import ymsli.com.cmsg.database.entity.LocalSentMessageEntity
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.databinding.ActivityPendingListBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.utils.*
import ymsli.com.cmsg.views.messagedetail.MessageDetailsActivity
import ymsli.com.couriemate.views.tasklist.drag.SimpleItemTouchHelperCallback
import ymsli.com.couriemate.views.tasklist.swipe.SwipeHandler
import java.io.Serializable
import java.sql.Timestamp


class PendingMsgListActivity : BaseActivity<PendingMsgListViewModel, ActivityPendingListBinding>(),
    SearchView.OnQueryTextListener, MessageListAdapter.SelectedMessagesListener {
    private var allMessageSelected: Boolean = false
    override fun provideViewBinding() = ActivityPendingListBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var messageListAdapter: MessageListAdapter

    lateinit var swipeHandler: SwipeHandler
    private lateinit var mItemTouchHelper: ItemTouchHelper
    lateinit var messageEntityList: ArrayList<MessageEntity>
    lateinit var filteredList: ArrayList<MessageEntity>
    var longTouchSelectionMessageList: ArrayList<MessageEntity> = ArrayList()

    private var isReadOnly: Boolean = false
    private var messageType: Int = MessageTypeEnum.PENDING.messageType
    private var providerType: String? = null
    private var allowCheckBoxSelection: Boolean = true
    private lateinit var globalMenu: Menu

    /** variable to keep track of drag drop mode */
    private var dragDropMode = false
    private lateinit var selectionHandler: SelectionHandler


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) globalMenu = menu
        alterToolbar(menu, allMessageSelected)
        return super.onCreateOptionsMenu(menu)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarTopPendingList)
        isReadOnly = intent.getBooleanExtra(Constants.READ_ONLY_MESSAGES_INTENT, false)
        messageType =
            intent.getIntExtra(Constants.MESSAGE_TYPE_INTENT, MessageTypeEnum.PENDING.messageType)
        providerType = intent.getStringExtra(Constants.PROVIDER_TYPE_INTENT)
        val isLocalSentIntent = intent.getBooleanExtra(Constants.LOCAL_SENT_INTENT, false)

        setProperToolbar()
        supportActionBar?.setDisplayShowHomeEnabled(true)
        viewModel.changeSelectionCount.value = longTouchSelectionMessageList.size;
        binding.cbSelectAll.setOnCheckedChangeListener { buttonView, isChecked -> // change border of all messages and toolbar icons too
            if (binding.spinnerMsgFilter.selectedItemPosition == 0) {
                setupMessagesList(isChecked, messageEntityList)
                alterMessageSelectionState(isChecked)
                longTouchSelectionMessageList = messageEntityList
                viewModel.changeSelectionCount.value = longTouchSelectionMessageList.size
            } else {
                if (filteredList.isNotEmpty()) {
                    setupMessagesList(isChecked, filteredList)
                    alterMessageSelectionState(isChecked)
                    longTouchSelectionMessageList = messageEntityList
                    viewModel.changeSelectionCount.value = longTouchSelectionMessageList.size
                } else {
                    longTouchSelectionMessageList.clear()
                    viewModel.changeSelectionCount.value = 0
                }
            }
            if (!binding.cbSelectAll.isChecked) {
                viewModel.changeSelectionCount.value = 0
            }
        }
        //setupMessagesList(false,null)

        if (providerType.isNullOrBlank()) {
            if (isLocalSentIntent) {
                viewModel.getSentMessagesOffline().observe(this) {
                    if (it != null && it.isNotEmpty()) {
                        // convert to corresponding POJO
                        messageEntityList =
                            convertToMessageEntity(it).toCollection(ArrayList())//it.toCollection(ArrayList())
                        if (messageType == MessageTypeEnum.SENT_API.messageType ||
                            messageType == MessageTypeEnum.DELIVERED.messageType
                        ) {
                            messageEntityList = (messageEntityList.filter {
                                it.messageStatus.equals(
                                    MessageStatusEnum.SENT.status,
                                    true
                                )
                            }).toCollection(ArrayList())
                        }
                        setupMessagesList(false, null)
                        setupFilter()
                    } else {
                        // show that no pending messages
                        handleNoMessageAck(true)
                    }
                }
            } else {
                viewModel.getPendingMessages(MessageStatusEnum.PENDING.status, messageType)
                    .observe(this) {
                        if (it != null && it.isNotEmpty()) {
                            messageEntityList = it.toCollection(ArrayList())
                            if (messageType == MessageTypeEnum.SENT_API.messageType || messageType == MessageTypeEnum.DELIVERED.messageType) {
                                messageEntityList = (messageEntityList.filter {
                                    it.messageStatus.equals(
                                        MessageStatusEnum.SENT.status,
                                        true
                                    )
                                }).toCollection(ArrayList())
                            }
                            Log.i("ListMessage", messageEntityList.size.toString())
                            setupMessagesList(false, messageEntityList)
                            setupFilter()
                            setupMultipleSelectionHandler()
                        } else {
                            // show that no pending messages
                            handleNoMessageAck(true)
                        }
                    }
            }
        } else {
            viewModel.getMessagesOfProvider(MessageTypeEnum.SENT_API.messageType, providerType!!)
                .observe(this) {
                    Log.d("List", "Here")
                    if (it != null && it.isNotEmpty()) {
                        messageEntityList = it.toCollection(ArrayList())
                        if (messageType == MessageTypeEnum.SENT_API.messageType) {
                            messageEntityList = (messageEntityList.filter {
                                it.messageStatus.equals(
                                    MessageStatusEnum.SENT.status,
                                    true
                                )
                            }).toCollection(ArrayList())
                        } else if (messageType == MessageTypeEnum.NOT_DELIVERED.messageType) {
                            messageEntityList = (messageEntityList.filterNot {
                                it.messageStatus.equals(
                                    MessageStatusEnum.SENT.status,
                                    true
                                )
                            }).toCollection(ArrayList())
                        }
                        Log.i("ListMessage4", messageEntityList.size.toString())
                        setupMessagesList(false, messageEntityList)
                        setupFilter()
                    } else {
                        // show that no pending messages
                        handleNoMessageAck(true)
                    }
                }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.messageString.observe(this) {
            it.getIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.changeSelectionCount.observe(this) {
            if (it != 0) {
                supportActionBar?.subtitle = resources.getString(R.string.selection_count) + it
                //supportActionBar?.title=resources.getString(R.string.selection_count)+it
            } else {
                supportActionBar?.subtitle = null
                //setProperToolbar()
            }

        }
    }

    private fun setupFilter() {
        binding.spinnerMsgFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // check position and populate filtered list
                    when (position) {
                        FilterDateEnum.ALL.code -> {
                            setupMessagesList(false, messageEntityList)
                        }

                        FilterDateEnum.TODAY.code -> {
                            filteredList = Utils.filterMessageEntityByTime(
                                messageEntityList,
                                FilterDateEnum.TODAY.code
                            )
                            setupMessagesList(false, filteredList)
                        }

                        FilterDateEnum.YESTERDAY.code -> {
                            filteredList = Utils.filterMessageEntityByTime(
                                messageEntityList,
                                FilterDateEnum.YESTERDAY.code
                            )
                            setupMessagesList(false, filteredList)
                        }

                        FilterDateEnum.OLDER.code -> {
                            filteredList = Utils.filterMessageEntityByTime(
                                messageEntityList,
                                FilterDateEnum.OLDER.code
                            )
                            setupMessagesList(false, filteredList)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    /**
     * loads message list in the
     * recycler view
     * @param showSelected if true, then border is green
     */
    private fun setupMessagesList(
        showSelected: Boolean,
        messagesList: java.util.ArrayList<MessageEntity>?
    ) {
        if (!(this::messageEntityList.isInitialized)) return
        if (messagesList == null) {
            handleNoMessageAck(true)
            return
        }
        handleNoMessageAck(false)

        linearLayoutManager = LinearLayoutManager(this)
        val messageItems = Utils.getMessageItems(ArrayList(messagesList))
        messageListAdapter = MessageListAdapter(messageItems, true, this, showSelected)
        binding.recyclerPendingMsgList.adapter = messageListAdapter
        binding.recyclerPendingMsgList.layoutManager = linearLayoutManager
        binding.recyclerPendingMsgList.setHasFixedSize(true)

        //set list item click listeners
        val callback = SimpleItemTouchHelperCallback(messageListAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        if (messagesList != null) {
            if (messagesList.size > 0) {
                //messageAdapter.loadData(messageEntityList.toCollection(ArrayList()))
                handleNoMessageAck(false)
                if (!isReadOnly) setupListItemSwipeHandler()
            } else handleNoMessageAck(true)
        } else handleNoMessageAck(true)
    }

    /**
     * shows user no message available
     * OR
     * hides the empty ack message
     */
    private fun handleNoMessageAck(noMessage: Boolean) {
        if (noMessage) {
            // show no message available
            binding.recyclerPendingMsgList.visibility = View.GONE
            binding.tvNoMsg.visibility = View.VISIBLE
            binding.cbSelectAll.isEnabled = false
        } else {
            binding.recyclerPendingMsgList.visibility = View.VISIBLE
            binding.tvNoMsg.visibility = View.GONE
            if (allowCheckBoxSelection) binding.cbSelectAll.isEnabled = true
        }
    }

    /**
     * Configures the swipe handler for the swipe to pickup and swipe to call actions on
     * list items.
     *
     * ACTIONS:
     * LEFT SWIPE:   Send the SMS
     * RIGHT SWIPE:  Delete the message
     */
    private fun setupListItemSwipeHandler() {
        swipeHandler = SwipeHandler(this, binding.recyclerPendingMsgList, messageListAdapter)
        swipeHandler.leftSwipeAction = object : SwipeHandler.SwipeAction {
            override fun perform(task: MessageEntity) {
                Log.d("left", "swipe")
                // send the corresponding SMS
                if (viewModel.isInternetConnected()) {
                    sendSMS(task)
                } else {
                    Toast.makeText(
                        applicationContext,
                        Constants.NO_NETWORK_AVAILABLE,
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
        swipeHandler.rightSwipeAction = object : SwipeHandler.SwipeAction {
            override fun perform(task: MessageEntity) {
                Log.d("right", "swipe")
                // remove the message from list and update the list
                if (viewModel.isInternetConnected()) {
                    deleteMessage(task)
                } else {
                    messageListAdapter.notifyDataSetChanged()
                    Toast.makeText(
                        applicationContext,
                        Constants.NO_NETWORK_AVAILABLE,
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }

    /**
     * when the user selects all the
     * messages and wants to send
     * them in one shot, then new toolbar
     * should be loaded
     * This also reverts the toolbar
     * stage to default state
     */
    private fun alterToolbar(menu: Menu?, bulkSMS: Boolean) {
        if (bulkSMS) menuInflater.inflate(R.menu.bulk_sens_sms, menu)
        else {
            menu?.clear()
            menuInflater.inflate(R.menu.menu_msg_list, menu)
            setupSearchView(menu)
        }
    }

    /**
     * When fragment stops and either multiple selection mode is active
     * or task rearrange feature is active then we invalidate options menu
     * so that other fragments get correct toolbar options.
     */
    override fun onStop() {
        super.onStop()
        if (this::selectionHandler.isInitialized && (selectionHandler.selectionMode || dragDropMode)) {
            invalidateOptionsMenu()
            //setupToolbarForInteractiveModeOff()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.logout) {
            logout()
        } else if (item.itemId == R.id.refresh) {
            // check internet and then refresh data
            if (viewModel.isInternetConnected()) {
                // clean local DB & pull pending messages again
                Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tPending List Refresh Called")
                viewModel.cleanMessageOfType(MessageTypeEnum.PENDING.messageType)
                viewModel.getPendingMessages()
                viewModel.getSentMessages()
            } else {
                viewModel.messageString.postValue(Event(resources.getString(R.string.network_connection_error)))
            }
        } else if (item.itemId == R.id.send_bulk_toolbar) {
            if (viewModel.isInternetConnected()) {
                supportActionBar?.subtitle = null
                showSnackbarWithDuration(longTouchSelectionMessageList)
                // sendBulkSMS(longTouchSelectionMessageList)

            } else {
                viewModel.messageString.postValue(Event(resources.getString(R.string.network_connection_error)))
            }


        } else if (item.itemId == R.id.delete_toolbar) {
            // ask for user confirmation before deleting messages
            if (viewModel.isInternetConnected()) {
                showDeleteConfirmation()
            } else {
                viewModel.messageString.postValue(Event(resources.getString(R.string.network_connection_error)))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSnackbarWithDuration(longTouchSelectionMessageList: ArrayList<MessageEntity>) {
        val customSnackbarView = LayoutInflater.from(this).inflate(R.layout.custome_snakebar, null)
        val snackbar = Snackbar.make(binding.root, "", 4000)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        if (longTouchSelectionMessageList.size > 500) {
            customSnackbarView.findViewById<TextView>(R.id.snackbar_title).text =
                "At a time Maximum 500 messages can be sent"
            customSnackbarView.findViewById<TextView>(R.id.snackbar_text).text =
                "Sending 500 out of ${longTouchSelectionMessageList.size} messages"

        } else {
            customSnackbarView.findViewById<TextView>(R.id.snackbar_title).text = "Sending Message"
            customSnackbarView.findViewById<TextView>(R.id.snackbar_text).text =
                "Sending ${longTouchSelectionMessageList.size} messages"
        }
        supportActionBar?.subtitle = null
        // Add the custom layout to the Snackbar
        snackbarLayout.addView(customSnackbarView, 0)
        // Show the Snackbar
        snackbar.show()
        // Dismiss the Snackbar after the specified duration
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            snackbar.dismiss()
            sendBulkSMS(longTouchSelectionMessageList)
        }, 4000)
    }

    private fun showSnackBarForSendingMessage(longTouchSelectionMessageList: java.util.ArrayList<MessageEntity>) {
        /* var message = ""
         if (longTouchSelectionMessageList.size > 500) {
             message = "Sending 500 out of ${longTouchSelectionMessageList.size} messages."
         } else {
             message = "Sending ${longTouchSelectionMessageList.size} messages?"
         }
         val labelLogout = "OK"
         val titleDialog = getString(R.string.confirmation)
         val actionCancel = {}
         val actionOk = {
             if (viewModel.isInternetConnected()) {
                 supportActionBar?.subtitle = null
                 sendBulkSMS(longTouchSelectionMessageList)
             } else {
                 viewModel.messageString.postValue(Event(resources.getString(R.string.network_connection_error)))
             }
             alterToolbar(globalMenu, false)
         }
         showConfirmationDialog(
             message,
             labelLogout,
             actionCancel,
             actionOk,
             titleDialog,
             R.drawable.ic_error
         )*/
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendBulkSMS(longTouchSelectionMessageList: ArrayList<MessageEntity>) {
        var subscriptionManager = getSystemService(SubscriptionManager::class.java)
        val simList: ArrayList<PhoneSIMEntity> = ArrayList()
        simList.clear()
        val subscription = subscriptionManager.activeSubscriptionInfoList
        val sim1 = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0)
        val sim2 = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1)
        if (sim1 != null || sim2 != null) {
            for (i in subscription.indices) {
                val simInfo = PhoneSIMEntity(
                    slot = subscription[i].simSlotIndex + 1,
                    carrierName = subscription[i].carrierName.toString(),
                    subscriptionId = subscription[i].subscriptionId
                )
                simList.add(simInfo)
            }
            viewModel.storeSIMInfo(simList.toTypedArray())
            //Send SMS, When Sim is available
            sendBulkMessages(longTouchSelectionMessageList)
        } else {
            //Clear the Sim List, When no sim available.
            simList.clear()
            //Update the empty sim array.
            viewModel.storeSIMInfo(simList.toTypedArray())
            //Show dialog to user that Sim is not available
            Utils.showNoSimDialog(this@PendingMsgListActivity)
        }
    }


    private fun showDeleteConfirmation() {
        val message = getString(R.string.message_delete_confirm)
        val labelLogout = getString(R.string.confirm)
        val titleDialog = getString(R.string.confirmation)
        val actionCancel = {}
        val actionOk = {
            deleteBulkMessages(longTouchSelectionMessageList)
            alterToolbar(globalMenu, false)
        }
        showConfirmationDialog(
            message,
            labelLogout,
            actionCancel,
            actionOk,
            titleDialog,
            R.drawable.ic_error
        )
    }

    private fun sendBulkMessages(messages: ArrayList<MessageEntity>) {
        // show progress and start sending messages
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t***Send bulk SMS triggered***")
        val smsIntent = Intent(this, SendSMSService::class.java)
        smsIntent.putExtra("SMS_LIST", messages as Serializable)
        Log.i("messageType1", messageType.toString())
        smsIntent.putExtra("MESSAGE_TYPE", messageType)
        smsIntent.putExtra("PROVIDER_TYPE", providerType)
        startService(smsIntent)
    }

    private fun deleteBulkMessages(messages: ArrayList<MessageEntity>) {
        // show progress and start deleting messages
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t***Delete bulk SMS triggered***")
        viewModel.showProgress.postValue(Event(true))
        viewModel.deleteMessage(messages)
    }

    /**
     * Configures the call backs for multiple selection mode on recycler view.
     * when multiple selection mode is on, toolbar's menu is updated to show a single icon which
     * is used to perform pickup of multiple items and item swipe function is disabled.
     *
     * when multiple selection is off, toolbar's menu is restored to the default state and
     * item swipe function is enabled.
     */
    private fun setupMultipleSelectionHandler() {
        if (!(this::messageListAdapter.isInitialized)) return
        selectionHandler = messageListAdapter.selectionHandler
        selectionHandler.observer = object : SelectionHandler.Observer {
            override fun onSelectionModeOn() {
                //activity?.invalidateOptionsMenu()
                //(activity as MainActivity).setupToolbarForInteractiveModeOn(Constants.EMPTY_STRING)
                swipeHandler.disableSwipe()
                //enablePullToRefresh(false)
            }

            override fun onSelectionModeOff() {
                /* activity?.invalidateOptionsMenu()
                 (activity as MainActivity).setupToolbarForInteractiveModeOff()*/
                swipeHandler.enableSwipe()
                //enablePullToRefresh(true)
            }
        }
    }

    override fun getSelectedMessages(messages: List<MessageEntity>) {
        Log.d("Selected messages", "here")
        // check if list is empty
        // restore toolbar to default state
        // If list is not empty
        // alter the state of the toolbar, disable filter
        longTouchSelectionMessageList = messages.toCollection(ArrayList())
        if (!binding.cbSelectAll.isChecked) viewModel.changeSelectionCount.value =
            longTouchSelectionMessageList.size
        alterMessageSelectionState(!messages.isEmpty())
    }

    override fun startMessageDetail(message: MessageEntity) {
        val detailIntent = Intent(this, MessageDetailsActivity::class.java)
        detailIntent.putExtra(Constants.READ_ONLY_MESSAGES_INTENT, isReadOnly)
        detailIntent.putExtra(Constants.MESSAGE_DETAIL_INTENT, message)
        startActivity(detailIntent)
    }

    private fun alterMessageSelectionState(isChecked: Boolean) {
        if (this::swipeHandler.isInitialized) {
            allMessageSelected = isChecked
            binding.spinnerMsgFilter.isEnabled = !isChecked
            invalidateOptionsMenu()
            if (!isChecked) swipeHandler.enableSwipe()
            else swipeHandler.disableSwipe()
        }
    }

    /**
     * Enables the drag drop mode,
     * by disabling the multiple selection feature and configuring
     * the toolbar for drag drop mode.
     */
    private fun enableDragDropDownMode() {
        dragDropMode = true
        selectionHandler.isActive = false
        invalidateOptionsMenu()
    }

    /**
     * Disables the drop drop mode by enabling the multi selection
     * mode and configuring the toolbar for normal operation mode.
     */
    private fun disableDragDropDownMode() {
        dragDropMode = false
        if (this::selectionHandler.isInitialized) selectionHandler.isActive = true
        //enableDrag(false)
        invalidateOptionsMenu()
        //(activity as MainActivity).setupToolbarForInteractiveModeOff()
    }

    //region Search view configuration functions
    override fun onQueryTextSubmit(query: String?): Boolean = true

    override fun onQueryTextChange(newText: String?): Boolean {
        if (this::messageEntityList.isInitialized && !messageEntityList.isEmpty()) applySearchFilter(
            newText
        )
        return true
    }

    var searchView: androidx.appcompat.widget.SearchView? = null

    /**
     * Setup the search view to provide the search feature in toolbar.
     */
    private fun setupSearchView(menu: Menu?) {
        searchView = menu?.findItem(R.id.search_task_list_toolbar)!!.actionView as SearchView?
        searchView?.setOnQueryTextListener(this)
        searchView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                Log.d("detached", "view")
            }

            override fun onViewAttachedToWindow(v: View?) {
                Log.d("attached", "view")
            }
        })
    }

    /**
     * Apply the search filter by filtering the data list based on the search criteria
     * and then loading the filtered list in the recycler view.
     */
    private fun applySearchFilter(newText: String?) {
        if (!newText.isNullOrEmpty()) {
            if (messageEntityList != null && messageEntityList.isNotEmpty()) {
                val filteredTasks = messageEntityList.filter {
                    it.orderNo.toString()
                        .contains(newText, true) ?: false || it.receiverName?.contains(
                        newText,
                        true
                    ) ?: false || it.receiverMobileNo?.contains(newText, true) ?: false
                }
                val messageItems = Utils.getMessageItems(ArrayList(filteredTasks))
                messageListAdapter.messageItems = messageItems
                messageListAdapter.notifyDataSetChanged()
            }
        } else {
            val messageItems = Utils.getMessageItems(ArrayList(messageEntityList))
            messageListAdapter.messageItems = messageItems
            messageListAdapter.notifyDataSetChanged()
        }
    }

    //endregion Search view configuration functions

    /**
     * sets appropriate title and
     * corresponding menu items
     */
    private fun setProperToolbar() {
        when (messageType) {
            MessageTypeEnum.PENDING.messageType -> {
                supportActionBar?.title = resources.getString(R.string.pending_messages_label)
                binding.tvListType.visibility = View.GONE
            }

            MessageTypeEnum.SENT_OFFLINE.messageType -> {
                supportActionBar?.title = resources.getString(R.string.delivered)
                binding.tvListType.visibility = View.VISIBLE
                removeSelectionHandler()
            }

            MessageTypeEnum.SENT_API.messageType -> {
                supportActionBar?.title = resources.getString(R.string.delivered)
                binding.tvListType.visibility = View.VISIBLE
                removeSelectionHandler()
            }

            MessageTypeEnum.DELIVERED.messageType -> {
                binding.tvListType.text = resources.getString(R.string.delivered)
                binding.tvListType.visibility = View.VISIBLE
                removeSelectionHandler()
            }

            MessageTypeEnum.NOT_DELIVERED.messageType -> {
                binding.tvListType.text = resources.getString(R.string.not_delivered)
                binding.tvListType.visibility = View.VISIBLE
            }
        }
        if (!providerType.isNullOrBlank()) supportActionBar?.title = providerType
    }

    /**
     * removes features like selecting messages
     * swipe message items to right or left
     */
    private fun removeSelectionHandler() {
        allowCheckBoxSelection = false
        disableDragDropDownMode()
        binding.cvSelectAll.visibility = View.GONE
    }

    private fun convertToMessageEntity(localMessages: List<LocalSentMessageEntity>): List<MessageEntity> {
        val messageEntityList: ArrayList<MessageEntity> = ArrayList()
        for (messageEntity in localMessages) {
            val message = MessageEntity(
                messageBody = messageEntity.messageBody,
                messageStatus = messageEntity.messageStatus,
                orderId = messageEntity.orderId,
                receiverMobileNo = messageEntity.receiverMobileNo,
                receiverName = messageEntity.receiverName,
                serviceProvider = messageEntity.serviceProvider,
                smsId = messageEntity.smsId,
                subject = messageEntity.subject,
                updateBy = messageEntity.updateBy,
                updatedOn = messageEntity.updatedOn,
                createdOn = messageEntity.createdOn,
                messageType = messageEntity.messageType,
                orderNo = messageEntity.orderNo
            )
            messageEntityList.add(message)
        }
        return messageEntityList
    }
}