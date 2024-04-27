package uz.fido.universaldigital.ui.fragments.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.fido.network.data.utility.Status
import uz.fido.network.di.SocketClient
import uz.fido.network.domain.datasource.services.SocketInterface
import uz.fido.network.domain.model.chat.EditMessageRequest
import uz.fido.network.domain.model.chat.MessageHistory
import uz.fido.network.domain.model.chat.MessageHistoryResponse
import uz.fido.network.domain.model.chat.ReceiveMessagesRequest
import uz.fido.network.domain.model.chat.RoomListResponse
import uz.fido.network.domain.model.chat.SendMessageRequest
import uz.fido.network.domain.model.chat.SendMessageResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMenuChatBinding
import uz.fido.universaldigital.ui.fragments.chat.adapter.ChatAdapter
import uz.fido.universaldigital.ui.fragments.payment.templates.adapter.PaymentTemplatesAdapter
import uz.fido.universaldigital.ui.utils.extensions.formatStringToHtml
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MenuChatFragment : BaseFragment<FragmentMenuChatBinding, MenuChatViewModel>(
    FragmentMenuChatBinding::inflate, MenuChatViewModel::class.java
), BaseInterface {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var shimmerAdapter: PaymentTemplatesAdapter
    private lateinit var mStorage: StorageReference

    private var storageReference: StorageReference? = null
    private var messageHistory: MessageHistory? = null
    private var chatAdapter: ChatAdapter? = null
    private var storage: FirebaseStorage? = null
    private var messagePosition: Int? = null
    private var roomImgName: String? = null
    private var roomName: String? = null
    private var roomId: String? = null

    private var list = ArrayList<MessageHistory>()
    private var operation = 0

    companion object {
        const val OPERATION_SEND_MSG = 0
        const val OPERATION_EDIT = 1
        const val OPERATION_REMOVE = 2
        const val OPERATION_CLEAR_ALL = 3
        const val CHAT_OPERATION = "operation"
    }

    enum class MessageTypes(id: Int) {
        TYPE_MESSAGE(1), TYPE_IMAGE(2);

        var typeId: Int = 1

        init {
            this.typeId = id
        }
    }

    enum class MessageStates(id: Int) {
        STATE_DELETED(3), STATE_DELETED_ALL(4);

        var stateId: Int = 0

        init {
            this.stateId = id
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.getString(CHAT_OPERATION) != null) {
                operation = it.getString(CHAT_OPERATION)!!.toInt()
                messageHistory = it.serializable("messageHistory") as MessageHistory?
            }
        }
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mStorage = FirebaseStorage.getInstance().getReference("Uploads/" + getClientId())
        shimmerAdapter = PaymentTemplatesAdapter(this)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        fetchRoomList()
        sendTestRequest()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.imageSend.setOnClickListener {
            if (binding.editMessage.text.toString().trim().isNotEmpty()) {
                binding.imageSend.isEnabled = false
                if (operation != 0) {
                    messageOperationRequest()
                } else {
                    val data: ByteArray = binding.editMessage.text.toString().toByteArray()
                    val base64String = Base64.encodeToString(data, Base64.DEFAULT)
                    sendMessageRequest(formatStringToHtml(binding.editMessage.text.toString()))
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            layoutManager = linearLayoutManager
            scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                }
            }
            addOnScrollListener(scrollListener)

            list = ArrayList()
            chatAdapter = ChatAdapter(this@MenuChatFragment, list)
            adapter = chatAdapter
        }
        binding.editMessage.addTextChangedListener {
            binding.imageSend.isEnabled = it!!.toString().isNotEmpty()
        }
        binding.shimmerView.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            adapter = shimmerAdapter
        }
    }

    private fun receiveMessagesRequest(roomId: String) {
        showSkeleton(binding.shimmerView, shimmerAdapter, R.layout.shimmer_item_chat, 5)
        binding.imageSend.isEnabled = false
        val request = ReceiveMessagesRequest(
            room_id = roomId
        )
        viewModel.receiveMessages(getClientToken(), request).observe(viewLifecycleOwner) {
            stopAnimation()
            if (isVisible) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.shimmerView.visibility = View.GONE
                }, 500)
            }
            if (it.status == Status.SUCCESS) {
                val response = it.data as MessageHistoryResponse
                response.msg_list?.let { messageHistory ->
                    if (messageHistory.isNotEmpty()) {
                        list.addAll(messageHistory)
                        updateAdapter()
                    }
                }
            }
        }
    }

    private fun sendMessageRequest(
        msg: String? = null, sendMessageRequest: SendMessageRequest? = null
    ) {
        animateRefreshButton()
        var request = SendMessageRequest(
            msg_text = msg ?: "",
            msg_object = "",
            msg_type_id = MessageTypes.TYPE_MESSAGE.typeId.toString(),
            ref_msg_id = "",
            ref_user_id = "",
            room_id = roomId!!
        )
        if (sendMessageRequest != null) {
            request = sendMessageRequest
        }
        viewModel.sendMessage(getClientToken(), request).observe(viewLifecycleOwner) {
            stopAnimation()
            when (it.status) {
                Status.SUCCESS -> {
                    val sendMessageResponse = it.data as SendMessageResponse
                    checkForList(sendMessageResponse)
                    clearEditTextMessage()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun editMessageRequest() {
        animateRefreshButton()
        val item = list[messagePosition!!]
        val message = binding.editMessage.text.toString()
        val editMessageRequest = EditMessageRequest(
            msg_id = item.msg_id!!, msg_text = message, room_id = roomId!!
        )
        viewModel.editMessage(getClientToken(), editMessageRequest).observe(viewLifecycleOwner) {
            stopAnimation()
            when (it.status) {
                Status.SUCCESS -> {
                    list.forEach { model ->
                        if (model.msg_id == item.msg_id) {
                            model.msg_text = message
                            model.state_id = "e"
                        }
                    }
                    chatAdapter?.notifyDataSetChanged()
                    clearEditTextMessage()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun removeMessageRequest() {
        animateRefreshButton()
        val item = list[messagePosition!!]
        val message = binding.editMessage.text.toString()
        val editMessageRequest = EditMessageRequest(
            msg_id = item.msg_id!!, msg_text = message, room_id = roomId!!
        )
        viewModel.removeMessage(getClientToken(), editMessageRequest).observe(viewLifecycleOwner) {
            stopAnimation()
            when (it.status) {
                Status.SUCCESS -> {
                    list.removeAt(messagePosition!!)
                    chatAdapter?.notifyItemRemoved(messagePosition!!)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun clearAllMessagesRequest() {
        animateRefreshButton()
        val editMessageRequest = EditMessageRequest(
            room_id = roomId!!, msg_state_id = MessageStates.STATE_DELETED_ALL.stateId.toString()
        )
        viewModel.clearRoomHistory(getClientToken(), editMessageRequest)
            .observe(viewLifecycleOwner) {
                stopAnimation()
                when (it.status) {
                    Status.SUCCESS -> {
                        list.clear()
                        chatAdapter?.notifyDataSetChanged()
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
    }

    private fun clearEditTextMessage() {
        binding.imageSend.isEnabled = true
//        binding.editMessage.hideKeyboard()
        binding.editMessage.setText("")
        operation = OPERATION_SEND_MSG
    }

    private fun messageOperationRequest() {
        when (operation) {
            OPERATION_EDIT -> {
                editMessageRequest()
            }

            OPERATION_REMOVE -> {
                removeMessageRequest()
            }

            OPERATION_CLEAR_ALL -> {
                clearAllMessagesRequest()
            }
        }

        operation = OPERATION_SEND_MSG
    }

    private fun checkForList(sendMessageResponse: SendMessageResponse) {
        var isExist = false
        list.forEach {
            if (it.msg_id.toString() == sendMessageResponse.msg_id) {
                isExist = true
                it.msg_text = sendMessageResponse.msg_text
                it.created_date = sendMessageResponse.created_date
            }
        }
        if (!isExist) {
            list.add(
                0, MessageHistory(
                    msg_text = sendMessageResponse.msg_text,
                    msg_type_id = sendMessageResponse.msg_type_id,
                    msg_id = sendMessageResponse.msg_id,
                    msg_object_id = sendMessageResponse.msg_object_id,
                    ref_msg_id = sendMessageResponse.ref_msg_id,
                    is_mine = sendMessageResponse.created_user_id == getClientId(),
                    created_date = sendMessageResponse.created_date,
                    created_user_id = getClientId(),
                    ref_msg_text = sendMessageResponse.ref_msg_text,
                    ref_user_id = sendMessageResponse.ref_user_id,
                    owner_user_id = sendMessageResponse.owner_user_id,
                    owner_user_name = sendMessageResponse.owner_user_name,
                    ref_user_name = sendMessageResponse.ref_user_name
                )
            )
            chatAdapter?.notifyItemInserted(0)
            scrollRecycler()
        } else {
            chatAdapter?.notifyDataSetChanged()
        }
    }

    private fun scrollRecycler() {
        try {
            Handler(Looper.getMainLooper()).postDelayed(
                { binding.recyclerView.scrollToPosition(0) }, 10
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun animateRefreshButton() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun stopAnimation() {
        binding.progress.visibility = View.GONE
    }

    private fun checkForMessageHolder(list: ArrayList<MessageHistory>): ArrayList<MessageHistory> {
        val clientId = getClientId()
        var oldDate: String? = null
        val newList = ArrayList<MessageHistory>()
        val sortedList = ArrayList<MessageHistory>()
        list.forEach { model ->
            if (model.msg_id != null) {
                model.is_mine = model.created_user_id == clientId
                sortedList.add(model)
            }
        }
        sortedList.sortByDescending { it.msg_id!!.toInt() }
        sortedList.reverse()
        sortedList.forEach { model ->
            val date = Format.formatChatDateToDay(model.created_date!!)
            if (oldDate != null && oldDate == date) {
                newList.add(model)
            } else {
                newList.add(
                    MessageHistory(
                        created_user_id = "", is_header = true, created_date = date
                    )
                )
                newList.add(model)
            }
            oldDate = date
        }
        newList.reverse()
        return newList
    }

    private fun updateAdapter() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            chatAdapter?.updateChatListItems(checkForMessageHolder(list))
            withContext(Dispatchers.Main) {
                scrollRecycler()
            }
        }
    }

    private fun fetchRoomList() {
        viewModel.fetchRoomList(getClientToken()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val roomListResponse = it.data as RoomListResponse
                    val roomModel = roomListResponse.room_list[0]
                    roomId = roomModel.room_id.toString()
                    roomName = roomModel.room_name.toString()
                    roomImgName = roomModel.default_img_name.toString()
//                    binding.textUserName.text = roomName
                    receiveMessagesRequest(roomId.toString())
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun sendTestRequest() {
        val socketClient = SocketClient.retrofitService()
        socketClient.socketTest(getClientId(), requireContext().getDeviceIds()).enqueue(object :
            Callback<SocketInterface.BgTaskResponse> {
            override fun onResponse(
                call: Call<SocketInterface.BgTaskResponse>,
                response1: Response<SocketInterface.BgTaskResponse>
            ) {
                try {
                    val response = response1.body()
                    if (response?.data != null && response.data.size > 0) {
                        response.data.forEach {
                            if (it.method != null) {
                                when (it.method) {
                                    "USER_MESSAGE", "USER_MESSAGE_EDITED", "USER_IS_TYPING", "USER_MESSAGE_DELETED" -> {
                                        val sendMessageResponse = Gson().fromJson(
                                            it.responses,
                                            SendMessageResponse::class.java
                                        )
                                        checkForList(sendMessageResponse)
                                    }
                                }
                            }
                        }
                    }
                    sendTestRequest()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SocketInterface.BgTaskResponse>, t: Throwable) {
                try {
                    sendTestRequest()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

}