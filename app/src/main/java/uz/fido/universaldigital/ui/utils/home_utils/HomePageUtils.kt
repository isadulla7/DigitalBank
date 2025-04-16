package uz.fido.universaldigital.ui.utils.home_utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.robinhood.ticker.TickerUtils
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.products.MenuHomeFragment
import uz.fido.universaldigital.ui.fragments.products.new_design.MenuNewHomeFragment
import uz.fido.universaldigital.widgets.total_balance.TotalBalanceWidget
import uz.fido.utils.const.Const
import uz.fido.utils.libs.smart_refresh.smart.BezierCircleHeader
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.user.deleteFromPaper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun MenuHomeFragment.loadProfileImage() {
    if (getFromSecureStore(Const.PAPER_USER_PHOTO_PATH).isNotEmpty()) {
        Picasso.get()
            .load(getFromSecureStore(Const.PAPER_USER_PHOTO_PATH))
            .placeholder(R.drawable.ic_profile_image_empty)
            .error(R.drawable.ic_profile_image_empty)
            .into(binding.userAvatar)
    } else {
        binding.userAvatar.load(R.drawable.ic_profile_image_empty)
    }
}


fun MenuNewHomeFragment.loadProfileImage() {
    if (getFromSecureStore(Const.PAPER_USER_PHOTO_PATH).isNotEmpty()) {
        Picasso.get()
            .load(getFromSecureStore(Const.PAPER_USER_PHOTO_PATH))
            .placeholder(R.drawable.ic_profile_image_empty)
            .error(R.drawable.ic_profile_image_empty)
            .into(binding.userAvatar)
    } else {
        binding.userAvatar.load(R.drawable.ic_profile_image_empty)
    }
}

fun MenuHomeFragment.setUserDetails() {
    val fullName = getFromSecureStore(Const.PAPER_CLIENT_FULL_NAME)
    val clientPhone = Format.phoneFormat(getFromSecureStore(Const.PAPER_CLIENT_PHONE))
    binding.userName.text = fullName.trim().ifEmpty { clientPhone }
}

fun MenuNewHomeFragment.setUserDetails() {
    val fullName = getFromSecureStore(Const.PAPER_CLIENT_FULL_NAME)
    val clientPhone = Format.phoneFormat(getFromSecureStore(Const.PAPER_CLIENT_PHONE))
    binding.userName.text = fullName.trim().ifEmpty { clientPhone }
}


fun MenuHomeFragment.setUpTickerView() {
    binding.balance.apply {
        setCharacterLists(TickerUtils.provideNumberList())
        typeface = ResourcesCompat.getFont(requireContext(), uz.fido.utils.R.font.inter_bold)
    }
}

fun loadCardsFromPaper(): ArrayList<CardResponse> {
    return try {
        Paper.book().read(Const.PAPER_CLIENT_CARDS, ArrayList()) ?: arrayListOf()
    } catch (e: Exception) {
        Paper.book().write(Const.PAPER_CLIENT_CARDS, ArrayList<CardResponse>())
        ArrayList()
    }
}

fun saveUserCardsSecure(cards: List<CardResponse>) {
    try {
        val json = Gson().toJson(cards)
        saveToSecureStore(Const.PAPER_CLIENT_CARDS, json)
        deleteFromPaper(Const.PAPER_CLIENT_CARDS)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getUserCardsFromSecureStore(): ArrayList<CardResponse> {
    try {
        val type = object : TypeToken<ArrayList<CardResponse>>() {}.type
        return Gson().fromJson(getFromSecureStore(Const.PAPER_CLIENT_CARDS), type) ?: loadCardsFromPaper()
    } catch (e: Exception) {
        return loadCardsFromPaper()
    }
}

fun getCardsWithBalanceVisibility(): ArrayList<CardResponse> {
    return try {
        Paper.book().read(Const.PAPER_CARDS_WITH_BALANCE_VIS, ArrayList()) ?: arrayListOf()
    } catch (e: Exception) {
        Paper.book().write(Const.PAPER_CARDS_WITH_BALANCE_VIS, ArrayList<CardResponse>())
        ArrayList()
    }
}

fun MenuHomeFragment.initRefreshLayout() {
    binding.refreshLayout.apply {
        setRefreshHeader(BezierCircleHeader(requireContext()))
            .setEnableNestedScroll(true)
        setOnRefreshListener {
            getCardList(it)
            fetchCurrencyRates()
        }
        setOnClickListener {
            binding.nestedScrollView.scrollTo(0, 0)
            binding.refreshLayout.autoRefresh()
        }
    }
}

fun MenuNewHomeFragment.initRefreshLayout() {
    binding.refreshLayout.apply {
        setRefreshHeader(BezierCircleHeader(requireContext()))
            .setEnableNestedScroll(true)
        setOnRefreshListener {
            getCardList(it)
            fetchMyHouseList()
        }
        setOnClickListener {
            binding.nestedScrollView.scrollTo(0, 0)
            binding.refreshLayout.autoRefresh()
        }
    }
}

fun MenuHomeFragment.initBalanceWidget(userBalance: String) {
    val ids = AppWidgetManager.getInstance(requireContext())
        .getAppWidgetIds(ComponentName(requireContext(), TotalBalanceWidget::class.java))
    TotalBalanceWidget().onUpdate(
        requireContext(), AppWidgetManager.getInstance(requireContext()), ids
    )
    val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    val updatedText =
        requireContext().getString(R.string.updated_at) + " " + dateFormat.format(Date())
    saveToSecureStore(Const.TOTAL_BALANCE_UPDATED_AT, updatedText)
    saveToSecureStore(Const.TOTAL_BALANCE, userBalance)
}

