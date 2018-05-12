package com.wistron.swpc.android.WiTMJ.listener;

import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;

/**
 * Created by WH1604025 on 2016/4/21.
 */
public interface onFragmentListener<E>  {
     void onStartFragment(Class newClass);

     void onStartFragmentWithData(Class newClass,E data);
}
