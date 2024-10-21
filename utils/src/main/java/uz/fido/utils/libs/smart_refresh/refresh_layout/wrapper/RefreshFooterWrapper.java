package uz.fido.utils.libs.smart_refresh.refresh_layout.wrapper;

import android.annotation.SuppressLint;
import android.view.View;

import uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshFooter;
import uz.fido.utils.libs.smart_refresh.refresh_layout.simple.SimpleComponent;

/**
 * 刷新底部包装
 * Created by scwang on 2017/5/26.
 */
@SuppressLint("ViewConstructor")
public class RefreshFooterWrapper extends SimpleComponent implements RefreshFooter {

    public RefreshFooterWrapper(View wrapper) {
        super(wrapper);
    }

}
