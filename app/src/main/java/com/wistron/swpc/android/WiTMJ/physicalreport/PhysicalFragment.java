package com.wistron.swpc.android.WiTMJ.physicalreport;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.bean.ReportData;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.navigation.NavigationDetailActivity;
import com.wistron.swpc.android.WiTMJ.physicalreport.adapter.PhysicalDmwYearAdapter;
import com.wistron.swpc.android.WiTMJ.physicalreport.adapter.RecordsAdapter;
import com.wistron.swpc.android.WiTMJ.train.TrainingActivity;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhysicalFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener {
    private String TAG = "PhysicalFragment";
    private RecordsDao mRecordsDao;
    private LineChart mChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private View mRootView;
    private TextView mDateType;
    private LinearLayout mDateLayout;
    private LinearLayout mWeekLayout;
    private LinearLayout mMonthLayout;
    private LinearLayout mYearLayout;
    private ListView mDataLv;
    private TextView mPopTv;
    private ImageView mPopIv;
    private ListView mDwmyearLv;
    private PopupWindow mPopupWindow;
    private LinearLayout dayTypeLayout;
    private RecordsAdapter mRecordsAdapter;
    private RadioButton mDistanceRBtn;
    private RadioButton mSpeedRBtn;
    private RadioButton mCaloriesRBtn;
    private Button mPreBtn;
    private Button mNextBtn;
    private String mStr[];
    private List<Records> mRecordList = new ArrayList<Records>();
    private int radioType;
    private int dateType;
    private int xAxisType;

    private Calendar calndar = Calendar.getInstance();
    private int year ;/*点击pre/nextbtn时的所在年*/
    private int month ;/*点击pre/nextbtn时的所在月*/
    private int whichWeek ;
    private int numWeekOfMonth ;/*一个月最大周数*/
    private long sevenDayOfWeek;/*获取一周七天，初始值为0：表示获取当前天所在周的7天*/
    private int fiveyear;/*获取当前5年，初始值为0：表示获取当前年所在前后5年*/

    ArrayList<String> currDayList = new ArrayList<String>();
    ArrayList<String> realList = new ArrayList<String>();
    ReportData realReportData[];

    private TextView dayTV1,dayTV2,dayTV3,dayTV4,dayTV5,dayTV6,dayTV7;
    private TextView weekTV1,weekTV2,weekTV3,weekTV4,weekTV5;
    private TextView monthTV1,monthTV2,monthTV3,monthTV4,monthTV5,monthTV6;
    private TextView monthTV7,monthTV8,monthTV9,monthTV10,monthTV11,monthTV12;
    private TextView yearTv1,yearTv2,yearTv3,yearTv4,yearTv5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        LogUtils.d(TAG,"--onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_physical, container, false);
        initView();
        initData();
        initEvent();
        LogUtils.d(TAG,"--onCreateView");
        return mRootView;
    }

    @Override
    public void initView() {
        mRecordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
        mChart = (LineChart) mRootView.findViewById(R.id.chart1);
        leftAxis = mChart.getAxisLeft();
        rightAxis = mChart.getAxisRight();
        dayTypeLayout = (LinearLayout) mRootView.findViewById(R.id.dayTypeLayout);
        dayTypeLayout.setOnClickListener(this);
        mPopupWindow = new PopupWindow(getActivity());
        mDateType = (TextView) mRootView.findViewById(R.id.dateType);
        mDataLv = (ListView) mRootView.findViewById(R.id.dataLv);
        mDateLayout = (LinearLayout) mRootView.findViewById(R.id.dayLayout);
        mWeekLayout = (LinearLayout) mRootView.findViewById(R.id.weekLayout);
        mMonthLayout = (LinearLayout) mRootView.findViewById(R.id.monthLayout);
        mYearLayout = (LinearLayout) mRootView.findViewById(R.id.yearLayout);
        mPopTv = (TextView) mRootView.findViewById(R.id.popTv);
        mPopIv = (ImageView) mRootView.findViewById(R.id.popIv);
        mPopIv.setOnClickListener(this);
        mDistanceRBtn = (RadioButton) mRootView.findViewById(R.id.distanceRdioBtn);
        mSpeedRBtn = (RadioButton) mRootView.findViewById(R.id.speeedRdioBtn);
        mCaloriesRBtn = (RadioButton) mRootView.findViewById(R.id.caloriesRdioBtn);
        mDistanceRBtn.setOnClickListener(this);
        mSpeedRBtn.setOnClickListener(this);
        mCaloriesRBtn.setOnClickListener(this);
        mPreBtn = (Button) mRootView.findViewById(R.id.preBtn);
        mNextBtn= (Button) mRootView.findViewById(R.id.nextBtn);
        mPreBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        dayTV1 = (TextView) mRootView.findViewById(R.id.day1);
        dayTV2 = (TextView) mRootView.findViewById(R.id.day2);
        dayTV3 = (TextView) mRootView.findViewById(R.id.day3);
        dayTV4 = (TextView) mRootView.findViewById(R.id.day4);
        dayTV5 = (TextView) mRootView.findViewById(R.id.day5);
        dayTV6 = (TextView) mRootView.findViewById(R.id.day6);
        dayTV7 = (TextView) mRootView.findViewById(R.id.day7);

        weekTV1 = (TextView) mRootView.findViewById(R.id.week1);
        weekTV2 = (TextView) mRootView.findViewById(R.id.week2);
        weekTV3 = (TextView) mRootView.findViewById(R.id.week3);
        weekTV4 = (TextView) mRootView.findViewById(R.id.week4);
        weekTV5 = (TextView) mRootView.findViewById(R.id.week5);

        monthTV1 = (TextView) mRootView.findViewById(R.id.month1);
        monthTV2 = (TextView) mRootView.findViewById(R.id.month2);
        monthTV3 = (TextView) mRootView.findViewById(R.id.month3);
        monthTV4 = (TextView) mRootView.findViewById(R.id.month4);
        monthTV5 = (TextView) mRootView.findViewById(R.id.month5);
        monthTV6 = (TextView) mRootView.findViewById(R.id.month6);
        monthTV7 = (TextView) mRootView.findViewById(R.id.month7);
        monthTV8 = (TextView) mRootView.findViewById(R.id.month8);
        monthTV9 = (TextView) mRootView.findViewById(R.id.month9);
        monthTV10 = (TextView) mRootView.findViewById(R.id.month10);
        monthTV11 = (TextView) mRootView.findViewById(R.id.month11);
        monthTV12 = (TextView) mRootView.findViewById(R.id.month12);

        yearTv1 = (TextView) mRootView.findViewById(R.id.year1);
        yearTv2 = (TextView) mRootView.findViewById(R.id.year2);
        yearTv3 = (TextView) mRootView.findViewById(R.id.year3);
        yearTv4 = (TextView) mRootView.findViewById(R.id.year4);
        yearTv5 = (TextView) mRootView.findViewById(R.id.year5);
        /*注册监听事件*/
        dayTV1.setOnClickListener(this);dayTV2.setOnClickListener(this);
        dayTV3.setOnClickListener(this);dayTV4.setOnClickListener(this);
        dayTV5.setOnClickListener(this);dayTV6.setOnClickListener(this);
        dayTV7.setOnClickListener(this);

        weekTV1.setOnClickListener(this);weekTV2.setOnClickListener(this);
        weekTV3.setOnClickListener(this);weekTV4.setOnClickListener(this);
        weekTV5.setOnClickListener(this);

        monthTV1.setOnClickListener(this);monthTV2.setOnClickListener(this);
        monthTV3.setOnClickListener(this);monthTV4.setOnClickListener(this);
        monthTV5.setOnClickListener(this);monthTV6.setOnClickListener(this);
        monthTV7.setOnClickListener(this);monthTV8.setOnClickListener(this);
        monthTV9.setOnClickListener(this);monthTV10.setOnClickListener(this);
        monthTV11.setOnClickListener(this);monthTV12.setOnClickListener(this);

        yearTv1.setOnClickListener(this);yearTv2.setOnClickListener(this);
        yearTv3.setOnClickListener(this);yearTv4.setOnClickListener(this);
        yearTv5.setOnClickListener(this);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void initData() {
        calndar.setTimeZone(TimeZone.getTimeZone("GMT"));
        year = calndar.get(Calendar.YEAR);
        month =calndar.get(Calendar.MONTH)+1;
        currDayList = (ArrayList<String>) CommonUtil.getCurDaysInweek(0);
        whichWeek = calndar.get(Calendar.WEEK_OF_MONTH);
        numWeekOfMonth = calndar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        sevenDayOfWeek = 0;/*为0表示获取这周的七天，-1：上周，1：这周*/
        fiveyear = 0;/*为0表示获取当前5年，-1：前5年，1：后5年*/
        dateType = Constants.DATE_DAY;
        radioType = Constants.RADIO_DISTANCE;
        mStr = getActivity().getResources().getStringArray(R.array.date_type);
        mDwmyearLv = new ListView(getActivity());
        PhysicalDmwYearAdapter popAdapter = new PhysicalDmwYearAdapter(getActivity(),mStr);
        mDwmyearLv.setAdapter(popAdapter);
        mDwmyearLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mDateType.setText("Day");
                        mPopTv.setText("Day");
                        dateType = Constants.DATE_DAY;
                        /*每次点击事件都需获取当前的时间段来绘制图表*/
                        initDayLayout();
                        break;
                    case 1:
                        mDateType.setText("Week");
                        mPopTv.setText("Week");
                        dateType = Constants.DATE_WEEK;
                        initWeekLayout();
                        break;

                    case 2:
                        mDateType.setText("Month");
                        mPopTv.setText("Month");
                        dateType = Constants.DATE_MONTH;
                        initMonthLayout();
                        break;

                    case 3:
                        mDateType.setText("Year");
                        mPopTv.setText("Year");
                        dateType = Constants.DATE_YEAR;
                        initYearLayout();
                        break;
                }
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        mDataLv.setOnItemClickListener(this);
        getRecordsFromServer();
        if (dateType == Constants.DATE_DAY && radioType == Constants.RADIO_DISTANCE){
            initCurrDaysOfWeek();
        }
    }

    private void getRecordsFromServer() {
        String act= PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN,"");
        TmjConnection connection = TmjClient.create();
        /*========zlb 时间段具体值===========*/
        Call<List<Records>> call = connection.getRecords(act,"2016-01-01","2020-01-01");
        call.enqueue(new Callback<List<Records>>() {
            @Override
            public void onResponse(Call<List<Records>> call, Response<List<Records>> response) {
                hideProgress();
                if(response.isSuccessful()){
                    mRecordsDao.deleteAll();
                    for (int i = 0 ;i<response.body().size();i++){
                        Records record = response.body().get(i);
                        mRecordsDao.insertOrReplace(record);
                    }
                    LogUtils.d(TAG," mRecordsDao "+mRecordsDao.queryBuilder().list());
                    LogUtils.d(TAG,"Successful====="+response.code() + " " +response.message());
                    List<Records> records = mRecordsDao.queryBuilder().list();
                    LogUtils.d(TAG,"records.size() "+records.size() +" mRecordList.size() "+mRecordList.size());
                    /*mRecordList =records;
                    mRecordsAdapter = new RecordsAdapter(getActivity(),mRecordList);
                    mRecordsAdapter.notifyDataSetChanged();
                    mDataLv.setAdapter(mRecordsAdapter);*/
                }else{
                    LogUtils.d(TAG,"Failure====== "+response.code() + " " +response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Records>> call, Throwable t) {
                LogUtils.d(TAG,"Failure===== "+t.getMessage());
            }
        });
    }

    private void initDayLayout() {
        mDateLayout.setVisibility(View.VISIBLE);
        mWeekLayout.setVisibility(View.GONE);
        mMonthLayout.setVisibility(View.GONE);
        mYearLayout.setVisibility(View.GONE);
        initCurrDaysOfWeek();
    }

    private void initWeekLayout() {
        mDateLayout.setVisibility(View.GONE);
        mWeekLayout.setVisibility(View.VISIBLE);
        mMonthLayout.setVisibility(View.GONE);
        mYearLayout.setVisibility(View.GONE);
        initCurrWeekOfMonth();
    }

    private void initMonthLayout() {
        mDateLayout.setVisibility(View.GONE);
        mWeekLayout.setVisibility(View.GONE);
        mMonthLayout.setVisibility(View.VISIBLE);
        mYearLayout.setVisibility(View.GONE);
        initCurrMonthOfYear();

    }
    private void initYearLayout() {
        mDateLayout.setVisibility(View.GONE);
        mWeekLayout.setVisibility(View.GONE);
        mMonthLayout.setVisibility(View.GONE);
        mYearLayout.setVisibility(View.VISIBLE);
        initCurrFiveYears();
    }

    private void initDaysOfWeek(long sevenDayOfWeek) {
        ArrayList<String> dayList = (ArrayList<String>) CommonUtil.getCurDaysInweek(sevenDayOfWeek);
        dayTV1.setText(dayList.get(0).substring(5,dayList.get(0).length()));
        dayTV2.setText(dayList.get(1).substring(5,dayList.get(1).length()));
        dayTV3.setText(dayList.get(2).substring(5,dayList.get(2).length()));
        dayTV4.setText(dayList.get(3).substring(5,dayList.get(3).length()));
        dayTV5.setText(dayList.get(4).substring(5,dayList.get(4).length()));
        dayTV6.setText(dayList.get(5).substring(5,dayList.get(5).length()));
        dayTV7.setText(dayList.get(6).substring(5,dayList.get(6).length()));
        dayTV1.setTag(R.id.tag_day,dayList.get(0));
        dayTV2.setTag(R.id.tag_day,dayList.get(1));
        dayTV3.setTag(R.id.tag_day,dayList.get(2));
        dayTV4.setTag(R.id.tag_day,dayList.get(3));
        dayTV5.setTag(R.id.tag_day,dayList.get(4));
        dayTV6.setTag(R.id.tag_day,dayList.get(5));
        dayTV7.setTag(R.id.tag_day,dayList.get(6));
        realList = dayList;
        refreshChart(realList,dateType,radioType);
    }
    private void initWeekOfMonth(int year,int month) {
        LogUtils.d(TAG,"year ="+year);
        LogUtils.d(TAG,"month ="+month);
        ArrayList<String> weekList = (ArrayList<String>) CommonUtil.getWeeksOfMonth(year,month);
        String str1[] = weekList.get(0).split("~");
        String bgnStr1 = str1[0].substring(5,str1[0].length());
        String endStr1 = str1[1].substring(5,str1[1].length());
        weekTV1.setText(bgnStr1+"~\n"+endStr1);

        String str2[] = weekList.get(1).split("~");
        String bgnStr2 = str2[0].substring(5,str2[0].length());
        String endStr2 = str2[1].substring(5,str2[1].length());
        weekTV2.setText(bgnStr2+"~\n"+endStr2);

        String str3[] = weekList.get(2).split("~");
        String bgnStr3 = str3[0].substring(5,str3[0].length());
        String endStr3 = str3[1].substring(5,str3[1].length());
        weekTV3.setText(bgnStr3+"~\n"+endStr3);

        String str4[] = weekList.get(3).split("~");
        String bgnStr4 = str4[0].substring(5,str4[0].length());
        String endStr4 = str4[1].substring(5,str4[1].length());
        weekTV4.setText(bgnStr4+"~\n"+endStr4);

        if (weekList.size()>4){
            weekTV5.setVisibility(View.VISIBLE);
            String str5[] = weekList.get(4).split("~");
            String bgnStr5 = str5[0].substring(5,str5[0].length());
            String endStr5 = str5[1].substring(5,str5[1].length());
            weekTV5.setText(bgnStr5+"~\n"+endStr5);
            weekTV5.setTag(R.id.tag_week,weekList.get(4));
        }
        weekTV1.setTag(R.id.tag_week,weekList.get(0));
        weekTV2.setTag(R.id.tag_week,weekList.get(1));
        weekTV3.setTag(R.id.tag_week,weekList.get(2));
        weekTV4.setTag(R.id.tag_week,weekList.get(3));
        realList = weekList;
        refreshChart(realList,dateType,radioType);
    }
    private void initMonthOfYear(int year) {
        monthTV1.setTag(R.id.tag_month,"01");
        monthTV2.setTag(R.id.tag_month,"02");
        monthTV3.setTag(R.id.tag_month,"03");
        monthTV4.setTag(R.id.tag_month,"04");
        monthTV5.setTag(R.id.tag_month,"05");
        monthTV6.setTag(R.id.tag_month,"06");
        monthTV7.setTag(R.id.tag_month,"07");
        monthTV8.setTag(R.id.tag_month,"08");
        monthTV9.setTag(R.id.tag_month,"09");
        monthTV10.setTag(R.id.tag_month,"10");
        monthTV11.setTag(R.id.tag_month,"11");
        monthTV12.setTag(R.id.tag_month,"12");
        ReportData reportData[] = getMonthRecordsByDate(String.valueOf(year)+"-%");
        realReportData = reportData;
        refreshChartMonthYear(realReportData,dateType,radioType);
    }

/*    private void initFiveYears() {
*//*        ArrayList<String> yearList = (ArrayList<String>) CommonUtil.getFiveYear(year);
        yearTv1.setText(yearList.get(0));
        yearTv2.setText(yearList.get(1));
        yearTv3.setText(yearList.get(2));
        yearTv4.setText(yearList.get(3));
        yearTv5.setText(yearList.get(4));
        yearTv1.setTag(R.id.tag_year,yearList.get(0));
        yearTv2.setTag(R.id.tag_year,yearList.get(1));
        yearTv3.setTag(R.id.tag_year,yearList.get(2));
        yearTv4.setTag(R.id.tag_year,yearList.get(3));
        yearTv4.setTag(R.id.tag_year,yearList.get(3));
        yearTv5.setTag(R.id.tag_year,yearList.get(4));*//*
        yearTv1.setTag(R.id.tag_year,"2016");
        yearTv2.setTag(R.id.tag_year,"2017");
        yearTv3.setTag(R.id.tag_year,"2018");
        yearTv4.setTag(R.id.tag_year,"2019");
        yearTv5.setTag(R.id.tag_year,"2020");
        refreshChart(realList,dateType,radioType);
    }*/

    private void initCurrDaysOfWeek() {
        currDayList = (ArrayList<String>) CommonUtil.getCurDaysInweek(0);
        dayTV1.setText(currDayList.get(0).substring(5,currDayList.get(0).length()));
        dayTV2.setText(currDayList.get(1).substring(5,currDayList.get(1).length()));
        dayTV3.setText(currDayList.get(2).substring(5,currDayList.get(2).length()));
        dayTV4.setText(currDayList.get(3).substring(5,currDayList.get(3).length()));
        dayTV5.setText(currDayList.get(4).substring(5,currDayList.get(4).length()));
        dayTV6.setText(currDayList.get(5).substring(5,currDayList.get(5).length()));
        dayTV7.setText(currDayList.get(6).substring(5,currDayList.get(6).length()));
        dayTV1.setTag(R.id.tag_day,currDayList.get(0));
        dayTV2.setTag(R.id.tag_day,currDayList.get(1));
        dayTV3.setTag(R.id.tag_day,currDayList.get(2));
        dayTV4.setTag(R.id.tag_day,currDayList.get(3));
        dayTV5.setTag(R.id.tag_day,currDayList.get(4));
        dayTV6.setTag(R.id.tag_day,currDayList.get(5));
        dayTV7.setTag(R.id.tag_day,currDayList.get(6));
        realList = currDayList;
        refreshChart(realList,dateType,radioType);
        removeListviewData();
    }

    private void initCurrWeekOfMonth() {
        year = calndar.get(Calendar.YEAR);
        month =calndar.get(Calendar.MONTH)+1;
        ArrayList<String> weekList = (ArrayList<String>) CommonUtil.getWeeksOfMonth(year,month);
        LogUtils.d(TAG,"weekList=="+weekList);
        String str1[] = weekList.get(0).split("~");
        String bgnStr1 = str1[0].substring(5,str1[0].length());
        String endStr1 = str1[1].substring(5,str1[1].length());
        weekTV1.setText(bgnStr1+"~\n"+endStr1);
        String str2[] = weekList.get(1).split("~");
        String bgnStr2 = str2[0].substring(5,str2[0].length());
        String endStr2 = str2[1].substring(5,str2[1].length());
        weekTV2.setText(bgnStr2+"~\n"+endStr2);
        String str3[] = weekList.get(2).split("~");
        String bgnStr3 = str3[0].substring(5,str3[0].length());
        String endStr3 = str3[1].substring(5,str3[1].length());
        weekTV3.setText(bgnStr3+"~\n"+endStr3);
        String str4[] = weekList.get(3).split("~");
        String bgnStr4 = str4[0].substring(5,str4[0].length());
        String endStr4 = str4[1].substring(5,str4[1].length());
        weekTV4.setText(bgnStr4+"~\n"+endStr4);
        if (weekList.size()>4){
            weekTV5.setVisibility(View.VISIBLE);
            String str5[] = weekList.get(4).split("~");
            String bgnStr5 = str5[0].substring(5,str5[0].length());
            String endStr5 = str5[1].substring(5,str5[1].length());
            weekTV5.setText(bgnStr5+"~\n"+endStr5);
            weekTV5.setTag(R.id.tag_week,weekList.get(4));
        }
        weekTV1.setTag(R.id.tag_week,weekList.get(0));
        weekTV2.setTag(R.id.tag_week,weekList.get(1));
        weekTV3.setTag(R.id.tag_week,weekList.get(2));
        weekTV4.setTag(R.id.tag_week,weekList.get(3));
        realList = weekList ;
        refreshChart(realList,dateType,radioType);
        removeListviewData();
    }

    private void initCurrMonthOfYear() {
        year = calndar.get(Calendar.YEAR);
        month =calndar.get(Calendar.MONTH)+1;
        monthTV1.setTag(R.id.tag_month,"01");
        monthTV2.setTag(R.id.tag_month,"02");
        monthTV3.setTag(R.id.tag_month,"03");
        monthTV4.setTag(R.id.tag_month,"04");
        monthTV5.setTag(R.id.tag_month,"05");
        monthTV6.setTag(R.id.tag_month,"06");
        monthTV7.setTag(R.id.tag_month,"07");
        monthTV8.setTag(R.id.tag_month,"08");
        monthTV9.setTag(R.id.tag_month,"09");
        monthTV10.setTag(R.id.tag_month,"10");
        monthTV11.setTag(R.id.tag_month,"11");
        monthTV12.setTag(R.id.tag_month,"12");
        ReportData reportData[] = getMonthRecordsByDate(String.valueOf(year)+"-%");
        realReportData = reportData;
        refreshChartMonthYear(realReportData,dateType,radioType);
        removeListviewData();
    }

    private void initCurrFiveYears() {
/*        ArrayList<String> yearList = (ArrayList<String>) CommonUtil.getFiveYear(0);
        yearTv1.setText(yearList.get(0));
        yearTv2.setText(yearList.get(1));
        yearTv3.setText(yearList.get(2));
        yearTv4.setText(yearList.get(3));
        yearTv5.setText(yearList.get(4));
        yearTv1.setTag(R.id.tag_year,yearList.get(0));
        yearTv2.setTag(R.id.tag_year,yearList.get(1));
        yearTv3.setTag(R.id.tag_year,yearList.get(2));
        yearTv4.setTag(R.id.tag_year,yearList.get(3));
        yearTv5.setTag(R.id.tag_year,yearList.get(4));*/
        yearTv1.setTag(R.id.tag_year,"2016");
        yearTv2.setTag(R.id.tag_year,"2017");
        yearTv3.setTag(R.id.tag_year,"2018");
        yearTv4.setTag(R.id.tag_year,"2019");
        yearTv5.setTag(R.id.tag_year,"2020");
        ReportData reportData[] = getYearRecordsByDate();
        realReportData = reportData;
        refreshChartMonthYear(realReportData,dateType,radioType);
        removeListviewData();
    }

    private void initChart() {
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setPinchZoom(true);
        mChart.animateX(2500);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);// zlb chart description shape
        l.setEnabled(false);//zlb：description at the bottom of chart don't display
        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);//zlb :desc position: center/left
        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);//zlb set xaxis text color
        xAxis.setDrawAxisLine(false);//zlb
        xAxis.setDrawGridLines(true);//zlb draw vertical grid line
        xAxis.enableGridDashedLine(10f,10f,10f);//vertical line are dashed
        xAxis.setSpaceBetweenLabels(1);// zlb --
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawAxisLine(false);//zlb false:dashed line true:solid line
        leftAxis.setDrawGridLines(true);//zlb draw horizontal grid line
        leftAxis.enableGridDashedLine(10f, 10f, 10f);//zlb: horizontal line are dashed
        leftAxis.setDrawZeroLine(false);//zlb :horizontal grid line below are not showed
        rightAxis.setTypeface(tf);
        rightAxis.setTextColor(R.color.rdio2);
        rightAxis.setDrawAxisLine(false);//zlb false:dashed line true:solid line
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setLabelCount(4, false);//zlb: show num of right side YAxis displayed
        rightAxis.setDrawLabels(false);//don't show right side YAxis
    }

    private LineDataSet getDataSet(LineDataSet set){
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(0f);//default 3f
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawCircleHole(true);
        set.setDrawValues(true);// false: zlb don't show value
        // create a data object with the datasets
        return  set;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("TAG ","--onResume");
        getRecordsFromServer();
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d(TAG,"--onStart");
        getRecordsFromServer();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG,"--onActivityCreated");
        getRecordsFromServer();
    }

    @Override
    public void onClick(View v) {
        if(v.getTag(R.id.tag_day)!=null){
          String day = (String) v.getTag(R.id.tag_day);
            refreshListView(day,dateType);
            return;
        }else if (v.getTag(R.id.tag_week)!=null){
            String week = (String) v.getTag(R.id.tag_week);
            refreshListView(week,dateType);
            return;
        }else if (v.getTag(R.id.tag_month)!=null){
            String month = (String) v.getTag(R.id.tag_month);
            refreshListView(month,dateType);
            return;
        }else if (v.getTag(R.id.tag_year)!=null){
            String year = (String) v.getTag(R.id.tag_year);
            refreshListView(year,dateType);
            return;
        }
        switch (v.getId()){
            case R.id.dayTypeLayout:
                showPopWindow(0,0);
                break;
            case R.id.popIv:
                showPopWindow(0,0);
                break;
            case R.id.distanceRdioBtn:
                radioType = Constants.RADIO_DISTANCE;
                if (dateType == Constants.DATE_DAY || dateType == Constants.DATE_WEEK){
                    refreshChart(realList,dateType,radioType);
                }else if (dateType == Constants.DATE_MONTH || dateType == Constants.DATE_YEAR){
                    refreshChartMonthYear(realReportData,dateType,radioType);
                }
                break;
            case R.id.speeedRdioBtn:
                radioType = Constants.RADIO_SPEED;
                if (dateType == Constants.DATE_DAY || dateType == Constants.DATE_WEEK){
                    refreshChart(realList,dateType,radioType);
                }else if (dateType == Constants.DATE_MONTH || dateType == Constants.DATE_YEAR){
                    refreshChartMonthYear(realReportData,dateType,radioType);
                }
                break;
            case R.id.caloriesRdioBtn:
                radioType = Constants.RADIO_CALORIES;
                if (dateType == Constants.DATE_DAY || dateType == Constants.DATE_WEEK){
                    refreshChart(realList,dateType,radioType);
                }else if (dateType == Constants.DATE_MONTH || dateType == Constants.DATE_YEAR){
                    refreshChartMonthYear(realReportData,dateType,radioType);
                }
                break;
            case R.id.preBtn:
                if (dateType == Constants.DATE_DAY){
                    sevenDayOfWeek = sevenDayOfWeek -1;
                    initDaysOfWeek(sevenDayOfWeek);
                    realList = (ArrayList<String>) CommonUtil.getCurDaysInweek(sevenDayOfWeek);
                    refreshChart(realList,dateType,radioType);
                }else if(dateType == Constants.DATE_WEEK){
                    month = month -1;
                    if (month<1){
                        year = year -1;
                        month = 12;
                    }
                    initWeekOfMonth(year,month);
                    realList = (ArrayList<String>) CommonUtil.getWeeksOfMonth(year,month);
                    refreshChart(realList,dateType,radioType);
                }else if(dateType == Constants.DATE_MONTH) {
                    year = year - 1;
                    initMonthOfYear(year);
                }/*else if(dateType == Constants.DATE_YEAR) {
                    //fiveyear = fiveyear - 1;
                    initFiveYears();
                }*/
                removeListviewData();
                break;
            case R.id.nextBtn:
                if (dateType == Constants.DATE_DAY){
                    sevenDayOfWeek = sevenDayOfWeek+1;
                    initDaysOfWeek(sevenDayOfWeek);
                    realList = (ArrayList<String>) CommonUtil.getCurDaysInweek(sevenDayOfWeek);
                    refreshChart(realList,dateType,radioType);
                }else if(dateType == Constants.DATE_WEEK){
                    month = month +1;
                    if (month>12){
                        year = year+1;
                        month = 1;
                    }
                    initWeekOfMonth(year,month);
                    realList = (ArrayList<String>) CommonUtil.getWeeksOfMonth(year,month);
                    refreshChart(realList,dateType,radioType);
                }else if(dateType == Constants.DATE_MONTH) {
                    year = year + 1;
                    initMonthOfYear(year);
                }
                removeListviewData();
                break;
            default:
                break;
        }

    }

    private void refreshListView(String str,int dateType) {
        List<Records> newRecords =null;
        if (dateType == Constants.DATE_DAY){
            String newStr = CommonUtil.getStr1ToStr2(str);
            newRecords =  mRecordsDao.queryBuilder().where(RecordsDao.Properties.End_time.like(newStr+"%")).list();
        }else if (dateType == Constants.DATE_WEEK){
            String s[] = str.split("~");
            String srtDate = CommonUtil.getWeekDate(s[0]);
            String endDate = CommonUtil.getWeekDate(s[1]);
            newRecords = mRecordsDao.queryBuilder().where(RecordsDao.Properties.End_time.between(srtDate,endDate)).list();
        }else if (dateType == Constants.DATE_MONTH){
            String date = year+"-"+str;
            LogUtils.d(TAG,"==refreshListView==DATE_MONTH="+date);
            newRecords = mRecordsDao.queryBuilder().where(RecordsDao.Properties.End_time.like(date+"%")).list();
        }else if (dateType == Constants.DATE_YEAR){
            newRecords = mRecordsDao.queryBuilder().where(RecordsDao.Properties.End_time.like(str+"%")).list();
        }
        mRecordList = newRecords;
        mRecordsAdapter = new RecordsAdapter(getActivity(),mRecordList);
        mDataLv.setAdapter(mRecordsAdapter);
        mRecordsAdapter.notifyDataSetChanged();
    }

    private void removeListviewData(){
        mRecordList.clear();
        mRecordsAdapter = new RecordsAdapter(getActivity(),mRecordList);
        mDataLv.setAdapter(mRecordsAdapter);
        mRecordsAdapter.notifyDataSetChanged();
    }


    private  ReportData[] getDayRecordsByDate(List<String> dayList){
        ReportData[]  list =new ReportData[dayList.size()];
        String srtDate =  CommonUtil.getStrToServerStr(dayList.get(0));
        String endDate =  CommonUtil.getStrToServerStr(dayList.get(dayList.size()-1));
        LogUtils.d(TAG,"===srtDate==="+srtDate);
        LogUtils.d(TAG,"===endDate==="+endDate);
        List<Records> recordsList =  mRecordsDao.queryBuilder().where(RecordsDao.Properties.End_time.between(srtDate,endDate)).list();
        for(int j=0;j<dayList.size();j++){
            float sum = 0f;
            for (int i=0;i<recordsList.size();i++){
                if(recordsList.get(i).getEnd_time().startsWith(CommonUtil.getStr1ToStr2(dayList.get(j)))){
                    ReportData rd = list[j];
                   if(rd==null){
                       rd =new ReportData();
                       list[j]=rd;
                   }
                    double dis = CommonUtil.getDistance(Double.valueOf(recordsList.get(i).getDistance()));
                    float disf = (float) dis;
                    double cal = CommonUtil.getCalories(Double.valueOf(recordsList.get(i).getCalories()));
                    float calf = (float) cal;
                    double spd = CommonUtil.getSpeed(Double.valueOf(recordsList.get(i).getAvg_speed()));
                    float spdf = (float) spd;

                    list[j].setDistance(rd.getDistance()+disf);
                    list[j].setCal(rd.getCal()+calf);
                    sum = sum +spdf;
                }
            }
            if (list[j]!=null){
                list[j].setSpeed(sum/=recordsList.size());
            }
        }

        return list;
    }

    private  ReportData[] getWeekRecordsByDate(List<String> weekList){
        ReportData[]  list =new ReportData[weekList.size()];
        String bgnStr =  weekList.get(0).split("~")[0];
        String endStr ="";
        if (weekList.size()>4){
            endStr =  weekList.get(4).split("~")[1];
        }else {
            endStr =  weekList.get(3).split("~")[1];
        }
        String srtDate = CommonUtil.getWeekDate(bgnStr);
        String endDate = CommonUtil.getWeekDate(endStr);
        List<Records> recordsList =  mRecordsDao.queryBuilder().where(RecordsDao.Properties.End_time.between(srtDate,endDate)).list();
        for(int j=0;j<weekList.size();j++){
            float sum = 0f;
            for (int i=0;i<recordsList.size();i++){
                String str1 = CommonUtil.getUnfmtStrToStr(weekList.get(j).split("~")[0]);
                String str2 = CommonUtil.getUnfmtStrToStr(weekList.get(j).split("~")[1]);
                String str = CommonUtil.getWeekRecordsStr(recordsList.get(i).getEnd_time());
                if(str.compareTo(str1)>=0 && str.compareTo(str2)<=0){
                    ReportData rd = list[j];
                    if(rd==null){
                        rd =new ReportData();
                        list[j]=rd;
                    }

                    double dis = CommonUtil.getDistance(Double.valueOf(recordsList.get(i).getDistance()));
                    float disf = (float) dis;
                    double cal = CommonUtil.getCalories(Double.valueOf(recordsList.get(i).getCalories()));
                    float calf = (float) cal;
                    double spd = CommonUtil.getSpeed(Double.valueOf(recordsList.get(i).getAvg_speed()));
                    float spdf = (float) spd;

                    list[j].setDistance(rd.getDistance()+disf);
                    list[j].setCal(rd.getCal()+calf);
                    sum = sum +spdf;
                }
            }
            if (list[j]!=null){
                list[j].setSpeed(sum/=recordsList.size());
            }
        }
        return list;
    }

    private  ReportData[] getMonthRecordsByDate(String conditionYear){//  conditionYear ex: "2016-%"
        ReportData[] list =new ReportData[12];
       Cursor c = mRecordsDao.getDatabase().rawQuery("select sum(distance) distance ,sum(calories)  calories ,(sum(avg_speed)/count(*)) as  speed ,month1 from (" +
                "select  substr(end_time,6,2) as month1,* from records where end_time like ? ) a group by month1 order by month1",new String[]{conditionYear});
        while (c.moveToNext()) {
            ReportData rd = new ReportData();
            String distance = c.getString(c.getColumnIndex("distance"));
            String calories = c.getString(c.getColumnIndex("calories"));
            String speed = c.getString(c.getColumnIndex("speed"));
            String month1 = c.getString(c.getColumnIndex("month1"));//month1的值是 01 02 03...12
            double dis = CommonUtil.getDistance(Double.valueOf(distance));
            float disf = (float) dis;
            double cal = CommonUtil.getCalories(Double.valueOf(calories));
            float calf = (float) cal;
            double spd = CommonUtil.getSpeed(Double.valueOf(speed));
            float spdf = (float) spd;
            rd.setCal(calf);
            rd.setDistance(disf);
            rd.setSpeed(spdf);
            rd.setMonth(month1);
            //LogUtils.i("db", "_id=>" + _id + ", name=>" + name + ", age=>" + age);
            list[Integer.valueOf(month1)-1] = rd;
        }
        c.close();
        LogUtils.i("db", "getMonthRecordsByDate"+list.toString());
        return list;
    }

    private  ReportData[] getYearRecordsByDate(){
        ReportData[] list =new ReportData[12];
        Cursor c = mRecordsDao.getDatabase().rawQuery("select sum(distance) distance ,sum(calories)  calories ,(sum(avg_speed)/count(*)) as  speed ,year1 from (" +
                "select  substr(end_time,1,4) as year1,* from records where year1 in ('2016','2017','2018','2019','2020')) a group by year1 order by year1",null);
        while (c.moveToNext()) {
            ReportData rd = new ReportData();
            String distance = c.getString(c.getColumnIndex("distance"));
            String calories = c.getString(c.getColumnIndex("calories"));
            String speed = c.getString(c.getColumnIndex("speed"));
            String year1 = c.getString(c.getColumnIndex("year1"));//month1的值是 01 02 03...12

            double dis = CommonUtil.getDistance(Double.valueOf(distance));
            float disf = (float) dis;
            double cal = CommonUtil.getCalories(Double.valueOf(calories));
            float calf = (float) cal;
            double spd = CommonUtil.getSpeed(Double.valueOf(speed));
            float spdf = (float) spd;
            rd.setCal(calf);
            rd.setDistance(disf);
            rd.setSpeed(spdf);
            rd.setYear(year1);
            list[Integer.valueOf(year1)-2016] = rd;
        }
        c.close();
        LogUtils.i("db", "getMonthRecordsByDate"+list.toString());
        return list;
    }

    /*refreshChart of day/week*/
    private void refreshChart(List<String> list,int dateType,int radioType){
        /* x轴y轴改变及每个点数据值改变*/
        initChart();
        ReportData[] reportDatas;
        if (dateType == Constants.DATE_MONTH){
            reportDatas = new ReportData[12];
        }else if(dateType == Constants.DATE_YEAR){
            reportDatas = new ReportData[5];
        }else{
            reportDatas = new ReportData[list.size()];
        }
        if (dateType == Constants.DATE_DAY){
            reportDatas = getDayRecordsByDate(list);
            xAxisType = Constants.XASIS_DAY_SEVEN;
        }else if(dateType == Constants.DATE_WEEK){
            reportDatas = getWeekRecordsByDate(list);
            if (numWeekOfMonth>=5){
                xAxisType = Constants.XASIS_WEEK_FIVE;
            }else {
                xAxisType = Constants.XASIS_WEEK_FOUR;
            }
        }
        refreshChartBase(reportDatas,radioType);
    }

    /*refreshChart of month/year*/
    private void refreshChartMonthYear(ReportData[] reportDatas,int dateType,int radioType){
        initChart();
/*        if (dateType == Constants.DATE_MONTH){
            reportDatas = new ReportData[12];
        }else if(dateType == Constants.DATE_YEAR){
            reportDatas = new ReportData[5];
        }*/
         if (dateType == Constants.DATE_MONTH){
             reportDatas = new ReportData[12];
             reportDatas = getMonthRecordsByDate(year+"-%");
            xAxisType = Constants.XASIS_MONTH_TWELVE;
        }else if (dateType == Constants.DATE_YEAR){
             reportDatas = new ReportData[5];
             xAxisType = Constants.XASIS_YEAR_FIVE;
            reportDatas = getYearRecordsByDate();
        }
        refreshChartBase(reportDatas,radioType);
    }


    private void refreshChartBase(ReportData reportDatas[],int radioType){
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 1; i <= xAxisType; i++) {
            xVals.add((i) + "");
        }
        for (int i = 0; i < xAxisType; i++) {
            if (radioType == Constants.RADIO_DISTANCE){
                if (reportDatas[i]!=null){
                    float val = reportDatas[i].getDistance();
                    yVals.add(new Entry(val, i));
                }else {
                    yVals.add(new Entry(0f, i));
                }
            }else if (radioType == Constants.RADIO_SPEED){
                if (reportDatas[i]!=null){
                    float val = reportDatas[i].getSpeed();
                    yVals.add(new Entry(val, i));
                }else {
                    yVals.add(new Entry(0f, i));
                }

            }else if (radioType == Constants.RADIO_CALORIES){
                if (reportDatas[i]!=null){
                    float val = reportDatas[i].getCal();
                    yVals.add(new Entry(val, i));
                }else {
                    yVals.add(new Entry(0f, i));
                }
            }
        }
        if (leftAxis.getAxisMaxValue()==0.0f && rightAxis.getAxisMaxValue()==0.0f){
            leftAxis.setAxisMinValue(0);
            leftAxis.setAxisMaxValue(100);
            rightAxis.setAxisMinValue(0);
            rightAxis.setAxisMaxValue(100);
        }
        LineDataSet set = new LineDataSet(yVals, "");
        if (radioType == Constants.RADIO_DISTANCE){
            set.setColor(Color.rgb(Constants.DISTANCE_RGB_RED,Constants.DISTANCE_RGB_GREEN,Constants.DISTANCE_RGB_BLUE));
        }else if (radioType == Constants.RADIO_SPEED){
            set.setColor(Color.rgb(Constants.SPEED_RGB_RED,Constants.SPEED_RGB_GREEN,Constants.SPEED_RGB_BLUE));
        }else if (radioType == Constants.RADIO_CALORIES){
            set.setColor(Color.rgb(Constants.CALORIES_RGB_RED,Constants.CALORIES_RGB_GREEN,Constants.CALORIES_RGB_BLUE));
        }
        set = getDataSet(set);
        dataSets.add(set);
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);
        mChart.setData(data);
    }

    private void showPopWindow(int xoff,int yoff) {
        if (mPopupWindow == null)
            return;
        //location获得控件的位置
        int[] location = new int[2];
        if (mPopTv != null){
            mPopTv.getLocationOnScreen(location);//控件在屏幕的位置
        }
        mPopupWindow.setAnimationStyle(R.style.AppTheme);
        int width = dayTypeLayout.getMeasuredWidth();
        mPopupWindow.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(width);
        Resources resources = getActivity().getResources();
        Drawable d = resources.getDrawable(R.color.setting_bg);
        mPopupWindow.setBackgroundDrawable(d);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(mDwmyearLv);
        mPopupWindow.showAsDropDown(mPopTv, xoff, yoff, Gravity.CENTER);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*此时是服务器的原始数据，未转换*/
        Records record = mRecordList.get(position);
        Intent i = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("startTime",record.getStart_time());
        bundle.putString("speed",record.getAvg_speed());
        bundle.putString("distance",record.getDistance());
        bundle.putString("duration",record.getDuration());
        bundle.putString("calories",record.getCalories());
        i.putExtras(bundle);
        if (mRecordList.get(position).getTag() != null) {
            int tagId = mRecordList.get(position).getTag();
            if (tagId == Constants.TAG_NAVIGATION) {
                i.putExtra(Constants.TAG_NAVIGATION_TO_DETAIL, Constants.TAG_NAVIGATION);
                i.setClass(getActivity(), NavigationDetailActivity.class);
                startActivity(i);
            } else if (tagId == Constants.TAG_TRAINING) {
                i.putExtra(Constants.TAG_TRAINING_TO_DETAIL, Constants.TAG_TRAINING);
                i.setClass(getActivity(), TrainingActivity.class);
                startActivity(i);
            } else if (tagId == Constants.TAG_INSTANT) {
                i.putExtra(Constants.TAG_INSTANT_TO_DETAIL, Constants.TAG_INSTANT);
                i.setClass(getActivity(), CompetitionDetailActivity.class);
                startActivity(i);
            }
        }
    }
}
