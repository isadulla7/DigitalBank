package uz.fido.utils.libs.smart_refresh.refresh_layout.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshHeader;
import uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshLayout;


/**
 * 默认Header创建器
 * Created by scwang on 2018/1/26.
 */
public interface DefaultRefreshHeaderCreator {
    @NonNull
    RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout);
}
