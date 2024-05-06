package uz.fido.universaldigital.ui.utils.home_utils

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.robinhood.ticker.TickerUtils
import com.scwang.smartrefresh.header.BezierCircleHeader
import io.paperdb.Paper
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.products.MenuHomeFragment
import uz.fido.universaldigital.widgets.total_balance.TotalBalanceWidget
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun MenuHomeFragment.loadProfileImage() {
    if (Paper.book().read(Const.PAPER_USER_PHOTO_PATH, "").isNotEmpty()) {
        Glide.with(requireContext())
            .load(Paper.book().read(Const.PAPER_USER_PHOTO_PATH, ""))
            .error(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    if (Paper.book().read(Const.FIRST_NAME, "").isEmpty() && Paper.book().read(Const.LAST_NAME, "").isEmpty()) {
                        binding.userAvatar.setImageResource(R.drawable.ic_profile_image_empty)
                    }
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return true
                }
            }).into(binding.userAvatar)
    }
}

@SuppressLint("SetTextI18n")
fun MenuHomeFragment.setUserDetails() {
    val fullName = Paper.book().read(Const.PAPER_CLIENT_FULL_NAME, "")
    val clientName = Paper.book().read(Const.FIRST_NAME, "")
    val clientSurname = Paper.book().read(Const.LAST_NAME, "")
    val clientPhone = Format.phoneFormat(Paper.book().read(Const.PAPER_CLIENT_PHONE, ""))
    val clientPhotoPath = Paper.book().read(Const.PAPER_USER_PHOTO_PATH, "")
    binding.userName.text = fullName.trim().ifEmpty { clientPhone }
    if (clientName.trim().isNotEmpty() && clientSurname.trim().isNotEmpty()) {
        binding.tvShortName.visibility = View.VISIBLE
        binding.tvShortName.text = clientName.first().toString() + clientSurname.first().toString()
    } else if (clientPhotoPath.isEmpty()) {
        binding.tvShortName.visibility = View.INVISIBLE
        binding.userAvatar.visibility = View.VISIBLE
        binding.userAvatar.load(R.drawable.ic_profile_image_empty)
    }
}


fun MenuHomeFragment.setUpTickerView() {
    binding.balance.apply {
        setCharacterLists(TickerUtils.provideNumberList())
        typeface = ResourcesCompat.getFont(requireContext(), uz.fido.utils.R.font.inter_bold)
    }
}

fun loadCardsFromPaper(): ArrayList<CardResponse> {
    return try {
        Paper.book().read(Const.PAPER_CLIENT_CARDS, ArrayList())
    } catch (e: Exception) {
        Paper.book().write(Const.PAPER_CLIENT_CARDS, ArrayList<CardResponse>())
        ArrayList()
    }
}

fun getCardsWithBalanceVisibility(): ArrayList<CardResponse> {
    return try {
        Paper.book().read(Const.PAPER_CARDS_WITH_BALANCE_VIS, ArrayList())
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

fun MenuHomeFragment.initBalanceWidget(userBalance: String) {
    val ids = AppWidgetManager.getInstance(requireContext())
        .getAppWidgetIds(ComponentName(requireContext(), TotalBalanceWidget::class.java))
    TotalBalanceWidget().onUpdate(
        requireContext(), AppWidgetManager.getInstance(requireContext()), ids
    )
    val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    val updatedText =
        requireContext().getString(R.string.updated_at) + " " + dateFormat.format(Date())
    Paper.book().write(Const.TOTAL_BALANCE_UPDATED_AT, updatedText)
    Paper.book().write(Const.TOTAL_BALANCE, userBalance)
}

