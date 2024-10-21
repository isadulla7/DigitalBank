package uz.fido.utils.libs.smart_refresh.refresh_layout.wrapper;

import android.annotation.SuppressLint;
import android.view.View;

import uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshHeader;
import uz.fido.utils.libs.smart_refresh.refresh_layout.simple.SimpleComponent;

/**
 * 刷新头部包装
 * Created by scwang on 2017/5/26.
 */
@SuppressLint("ViewConstructor")
public class RefreshHeaderWrapper extends SimpleComponent implements RefreshHeader {

    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }

}
