package uz.fido.utils.libs.smart_refresh.refresh_layout.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshFooter;
import uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshLayout;

/**
 * 默认Footer创建器
 * Created by scwang on 2018/1/26.
 */
public interface DefaultRefreshFooterCreator {
    @NonNull
    RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout);
}
