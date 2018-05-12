package com.wistron.swpc.android.WiTMJ;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.wistron.swpc.android.WiTMJ.navigation.NavigationFragment;
import com.wistron.swpc.android.WiTMJ.train.TrainFragment;
import com.wistron.swpc.android.WiTMJ.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WH1604025 on 2016/6/12.
 */
public class ManagerFragment {
    List<Fragment> fragmentStack = new ArrayList<Fragment>();

    private static ManagerFragment instance;

    private ManagerFragment() {
    }

    // 单例模式中获取唯一的MyApplication实例
    public  static ManagerFragment getInstance() {
        if (instance == null) {
            synchronized (ManagerFragment.class) {
                if (instance == null) {
                    instance = new ManagerFragment();
                }
            }
        }
        return instance;
    }

    // 添加fragment到容器中
    public void addfragment(Fragment fragment) {
        if(fragmentStack ==null){
            fragmentStack=new ArrayList<Fragment>();
        }
        if(!fragmentStack.contains(fragment)) {
            fragmentStack.add(fragment);
        }
    }

    public void finishFragment(FragmentManager fm,Fragment fragment){
        if(fragmentStack !=null){
            fm.beginTransaction().remove(fragment).commit();
        }
    }

    public int size(){
        return fragmentStack.size();
    }


    // 遍历所有fragment并finish
    public void finishAllfragment(FragmentManager fm) {
        L.i(ManagerFragment.class,"fragmentList size:"+fragmentStack.size());
        for (Fragment fragment : fragmentStack) {
            if(fragment instanceof HomeFragment) continue;
            fm.beginTransaction().remove(fragment).commit();
            L.i(ManagerFragment.class,fragment.getClass()+"");
        }
        fragmentStack.clear();
    }

    //
    public void finishTrainAndNavigationFragment(FragmentManager fm){
        L.i(ManagerFragment.class,"fragmentList size:"+fragmentStack.size());
        for (Fragment fragment : fragmentStack) {
            if(fragment instanceof TrainFragment || fragment instanceof NavigationFragment){
                fm.beginTransaction().remove(fragment).commit();
                continue;
            }

        }
    }
}
