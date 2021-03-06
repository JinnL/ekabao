package com.ekabao.oil.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekabao.oil.ui.fragment.DiscoverFragment;
import com.ekabao.oil.ui.fragment.OilFragment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.ekabao.oil.R;
import com.ekabao.oil.bean.FriendBean;
import com.ekabao.oil.bean.Mainpopupwindow;
import com.ekabao.oil.global.LocalApplication;
import com.ekabao.oil.http.UrlConfig;
import com.ekabao.oil.http.okhttp.OkHttpUtils;
import com.ekabao.oil.http.okhttp.callback.StringCallback;
import com.ekabao.oil.ui.fragment.FindFragment;
import com.ekabao.oil.ui.fragment.HomeFragment;
import com.ekabao.oil.ui.fragment.HomeMallFragment;
import com.ekabao.oil.ui.fragment.InvestFragment;
import com.ekabao.oil.ui.fragment.PersonFragment;
import com.ekabao.oil.ui.fragment.ProfitFragment;
import com.ekabao.oil.ui.fragment.SafeFragment;
import com.ekabao.oil.ui.fragment.WelfareFragment;
import com.ekabao.oil.ui.view.ToastMaker;
import com.ekabao.oil.util.LogUtils;
import com.ekabao.oil.util.StatusBarUtil;
import com.ekabao.oil.util.SystemUtil;
import com.nie.ngallerylibrary.GalleryViewPager;
import com.nie.ngallerylibrary.ScalePageTransformer;
import com.nie.ngallerylibrary.adater.MyPageradapter;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class MainActivity extends BaseActivity {

    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.tv_invest)
    TextView tvInvest;
    @BindView(R.id.img_activity)
    ImageView imgActivity;
    @BindView(R.id.tv_find)
    TextView tvFind;
    @BindView(R.id.tv_person)
    TextView tvPerson;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.main_red_circle)
    View mainRedCircle;
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }*/

    public HomeFragment home = null; //首页
    public InvestFragment invest = null; //出借

    public SafeFragment safe = null; //安全保障
    private HomeMallFragment homemallFragment;

    public ProfitFragment profitFrag = null;//返利 邀请好友

    public FindFragment findFragment = null;//发现
    public DiscoverFragment discoverFragment = null;//商城


    public PersonFragment person = null; //个人中心板块


    private WelfareFragment welfareFragment; //福利

    private OilFragment oilFragment;//加油


    private SharedPreferences preferences = LocalApplication.sharereferences;
    public static Boolean isHome = false, isHomeChecked = false,
            isLoginPsw = false, isInvestChecked = false, isInvest = false,
            isLog = false, isMore = false, hasNavigationBar = false, isActivity = false, isExit = false, isPersonChecked = false;
    private InvestFragment frag;
    private String btnUrl;//中间按钮图片url
    private String btnUrl2;//悬浮按钮图片url
    private List<FriendBean> list;
    private FragmentTransaction transaction;
    private long curMillis;
    private int day;


    private List<FriendBean> list3;

    private int id; //悬浮框的
    private String location;
    private String imgUrl;
    private String title;

    private int pid; //油卡套餐
    private int money;
    private int month;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        home = HomeFragment.instance();
        invest = InvestFragment.instance();
        //profitFrag = ProfitFragment.instance();
        safe = SafeFragment.instance();

        person = PersonFragment.instance();

        //findFragment = FindFragment.instance();

        homemallFragment = HomeMallFragment.instance();

        // welfareFragment = WelfareFragment.newInstance();

        oilFragment = OilFragment.newInstance();

        discoverFragment = DiscoverFragment.newInstance();

        //为了修改状态栏的文字颜色和背景
        StatusBarUtil.StatusBarLightMode(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (popupWindow2 != null) {
            popupWindow2.dismiss();
            handler.removeCallbacks(runnable);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected synchronized void onDestroy() {
        super.onDestroy();
        if (popupWindow2 != null) {
            popupWindow2.dismiss();
            handler.removeCallbacks(runnable);
        }
        if (window_float != null) {
            window_float.dismiss();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initParams() {
        //记录上一次打开APP的时间。用来判断是否弹出底部中间的活动弹框
        Calendar calendar = Calendar.getInstance();
        curMillis = calendar.getTimeInMillis();
        day = time2DayOfYear(curMillis);

        //LogUtils.i("day：" + day + ", last_day: " + preferences.getInt("day", 0));

        String deviceBrand = SystemUtil.getDeviceBrand();
        String systemModel = SystemUtil.getSystemModel();
        LogUtils.i("手机产商：" + deviceBrand + " ,手机型号：" + systemModel);


        LocalApplication.getInstance().setMainActivity(this);

        setCheckedFram(1);

        //设置沉浸式状态栏,目前这里不需要 pop_main 是根布局
       /* if (checkDeviceHasNavigationBar(MainActivity.this)) {
            pop_main.setFitsSystemWindows(true);
        }*/

        String location = getIntent().getStringExtra("location");

        String title = getIntent().getStringExtra("title");

        LogUtils.i("location：" + location + ", title: " + title);


        if (location != null && title != null) {

            startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                    .putExtra("BANNER", "toujisong")
                    .putExtra("URL", location + "&app=true")
                    .putExtra("TITLE", title));
        }
        //手势密码界面
        if (LocalApplication.getInstance().sharereferences.getBoolean("login", false)) {
            if (LocalApplication.getInstance().sharereferences.getBoolean("loginshoushi", false)) {
                startActivity(new Intent(MainActivity.this, GestureEditActivity.class).putExtra("flag", "wellcome"));
            }
        }
        //根据活动改变底部导航图标
        // getData2();
        pushRegisterId();

        getData3();

    }

    public InvestFragment getFrag() {
        return frag;
    }

    public void setFrag(InvestFragment frag) {
        this.frag = frag;
    }


    /**
     * 显示悬浮框
     */
    private View pop_float;
    private PopupWindow window_float;

    public void showFloatPopupWindow(String imgurl) {
        // 加载布局
        pop_float = LayoutInflater.from(this).inflate(R.layout.new_pop_float, null);
        // 找到布局的控件
        ImageView img_float = (ImageView) pop_float.findViewById(R.id.img_float);

        LogUtils.e("imgUrl" + imgUrl);
        Glide.with(this)
                // .load(list3.get(0).getAppPic())
                .load(imgurl)
                .into(img_float);
        //Picasso.with(MainActivity.this).load(list3.get(0).getAppPic()).into(img_float);

        // 实例化popupWindow
        window_float = new PopupWindow(pop_float, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        // 控制键盘是否可以获得焦点
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        window_float.setOutsideTouchable(false);

        window_float.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //popupWindow.setFocusable(true);
        window_float.setFocusable(true);
        backgroundAlpha(0.5f);

        pop_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundAlpha(1f);
                window_float.dismiss();
            }
        });
        window_float.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
                // et_yzm.setText("");
            }
        });
        img_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastMaker.showShortToast("悬浮框");
                //list3.get(0).getAppUrl() + "?app=true"
                //http://a.app.qq.com/o/simple.jsp?pkgname=com.middleman.ymc
              /*  if (list3.get(0).getStatus() == 1) {
                    startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                            .putExtra("URL", list3.get(0).getAppUrl() + "?app=true")
                            .putExtra("TITLE", list3.get(0).getTitle())
                            .putExtra("AFID", list3.get(0).getId() + ""));
                }*/

                if (id != 0) {
                    startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                            .putExtra("URL", location + "?app=true")
                            .putExtra("TITLE", title)
                            .putExtra("AFID", id));
                }


            }
        });
        // LogUtils.e("显示悬浮框" + list3.size());

        window_float.showAtLocation(pop_float, Gravity.BOTTOM, 0, 0);
        // window_float.showAtLocation(pop_float, Gravity.CENTER, 0, 0);
        /*img_float.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;
            int mScreenX = 0, mScreenY = 0;
            int dx, dy;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = (int) event.getRawX() - lastX + mScreenX;
                        dy = lastY - (int) event.getRawY() + mScreenY;
                        window_float.update(dx, dy, -1, -1);
                        break;
                    case MotionEvent.ACTION_UP:
                        mScreenX = dx;
                        mScreenY = dy;
                        break;
                }
                return true;
            }
        });*/


        //window_float.showAsDropDown();

    }

    @Override
    public void onButtonClicked(Dialog dialog, int position, Object tag) {

        if (position == 0) {
            ToastMaker.showShortToast("可以在安全中心-手势密码 中进行修改");
        }
        if (position == 1) {
            startActivity(new Intent(MainActivity.this, GestureEditActivity.class));
        }
    }

    // 获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        LogUtils.i("hasNavigationBar：" + hasNavigationBar);
        return hasNavigationBar;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }


    /**
     * 根据活动改变底部导航图标（"type", "2"）
     */
    private void getData2() {
        OkHttpUtils.post()
                .url(UrlConfig.ACTIVITYLIST)
                .addParams("uid", preferences.getString("uid", ""))
                .addParams("status", "1")
                .addParams("type", "2")
                .addParams("pageOn", "1")
                .addParams("pageSize", "200")
                .addParams("version", UrlConfig.version)
                .addParams("channel", "2")
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String result) {
                LogUtils.i("--->底部中间按钮的活动：" + result);
              /*  JSONObject obj = JSON.parseObject(result);
                if (obj.getBoolean("success")) {
                    JSONObject objmap = obj.getJSONObject("map");
                    JSONObject objinfo = objmap.getJSONObject("Page");
                    btnUrl = objmap.getString("btnUrl");
                    JSONArray objrows = objinfo.getJSONArray("rows");
                    list = JSON.parseArray(objrows.toJSONString(), FriendBean.class);
                    // TODO: 2018/3/29  底部中间按钮的活动

                    if (list.size() > 0) {
                        initImgUrl(list);
                        //显示底部中间按钮
                        imgActivity.setVisibility(View.VISIBLE);
                        Picasso.with(MainActivity.this).load(btnUrl).into(imgActivity);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (day == preferences.getInt("day", 0)) {
                                    return;//在一天之内打开app，只显示一次弹框
                                } else if (isPersonChecked || isExit) {
                                    return;//防止退出登录时，登录弹框和它重叠
                                } else {
                                    LogUtils.i("--->底部中间按钮的活动又出来啦！");
                                    showPopupWindowActivity();
                                }
                            }
                        }, 1000);

                    } else {
                        imgActivity.setVisibility(View.GONE);
                    }

                } else if ("9998".equals(obj.getString("errorCode"))) {

                } else {
                    ToastMaker.showShortToast("服务器异常");
                }*/
            }

            @Override
            public void onError(Call call, Exception e) {
                LogUtils.i("--->进行中的活动：获取失败！");
                ToastMaker.showShortToast("请检查网络");
            }
        });
    }


    /**
     * 根据活动是否显示悬浮框（"type", "3"）
     */
    private void getData3() {
        //UrlConfig.ACTIVITYLIST
        OkHttpUtils.post()

                .url(UrlConfig.startPopup)
                //.addParams("uid", preferences.getString("uid", ""))
                // .addParams("status", "1")
                // .addParams("type", "3")
                //  .addParams("pageOn", "1")
                // .addParams("pageSize", "200")
                .addParams("version", UrlConfig.version)
                .addParams("channel", "2")
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String result) {
                LogUtils.e("--->显示悬浮框的活动：" + result);

                JSONObject obj = JSON.parseObject(result);
                if (obj.getBoolean("success")) {

                    JSONObject objmap = obj.getJSONObject("map");

                    if (objmap.getJSONArray("sysBanner") != null) {

                        JSONArray sysBanner = objmap.getJSONArray("sysBanner");

                        if (sysBanner.size() != 0) {
                            List<Mainpopupwindow> mainpopupwindows = JSON.parseArray(sysBanner.toJSONString(), Mainpopupwindow.class);

                            Mainpopupwindow mainpopupwindow = mainpopupwindows.get(0);

                            imgUrl = mainpopupwindow.getImgUrl();

                            title = mainpopupwindow.getTitle();

                            id = mainpopupwindow.getId();

                            location = mainpopupwindow.getLocation();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    LogUtils.e("--->显示悬浮框的活动：showFloatPopupWindow");
                                    showFloatPopupWindow(imgUrl);
                                }
                            }, 1000);
                        }
                    }


                 /*   JSONObject objinfo = objmap.getJSONObject("Page");
                    btnUrl2 = objmap.getString("btnUrl");
                    JSONArray objrows = objinfo.getJSONArray("rows");
                    list3 = JSON.parseArray(objrows.toJSONString(), FriendBean.class);


                    if (list3.size() > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                showFloatPopupWindow();
                            }
                        }, 1000);
                    }*/

                } else if ("9998".equals(obj.getString("errorCode"))) {

                } else {
                    ToastMaker.showShortToast("服务器异常");
                }
            }

            @Override
            public void onError(Call call, Exception e) {
                LogUtils.i("--->进行中的活动：获取失败！");
                ToastMaker.showShortToast("请检查网络");
            }
        });
    }


    @OnClick({R.id.tv_home, R.id.tv_invest, R.id.img_activity, R.id.tv_find, R.id.tv_person})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_home:
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                resetTabState();
                setTabState(tvHome, R.drawable.icon_main_home_press, 0xFF333333);//设置Tab状态
                switchFragment(0);//切换Fragment
                break;
            case R.id.tv_invest:
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                resetTabState();
                setTabState(tvInvest, R.drawable.icon_main_fund_press, 0xFF333333);
                switchFragment(1);
                break;
            case R.id.img_activity:  //变为安全了

                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                //  resetTabState();
                //setTabState(tvInvest, R.drawable.icon_main_fund_press, 0xFF333333);


                switchFragment(4);
                resetTabState();

                //弹框处理
                // showPopupWindowActivity();

                break;
            case R.id.tv_find:
               /* if (preferences.getString("uid", "").equalsIgnoreCase("")) {
                    if (popupWindow2 != null) {
                        popupWindow2.dismiss();
                        handler.removeCallbacks(runnable);
                    }
                    LogUtils.e("去登录");
                    //showPopupWindowLogin();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class)
                            .putExtra("point", "personFrag"));

                } else {}*/

                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                resetTabState();
                setTabState(tvFind, R.drawable.icon_main_more_press, 0xFF333333);
                switchFragment(2);

                break;
            case R.id.tv_person:
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                resetTabState();
                setTabState(tvPerson, R.drawable.icon_main_account_press, 0xFF333333);
                switchFragment(3);
                isPersonChecked = true;
                isExit = false;


                break;
        }
    }

    /**
     * switch the fragment Fragment切换
     *
     * @param i id
     */
    public void switchFragment(int i) {
        transaction = getSupportFragmentManager().beginTransaction();
        switch (i) {
            case 0:
               /* if (home == null) {
                    home = HomeFragment.instance();
                    LogUtils.e("switchFragment");
                }
                transaction.replace(R.id.fragment_content, home);*/

                if (homemallFragment == null) {
                    homemallFragment = HomeMallFragment.instance();
                }
                transaction.replace(R.id.fragment_content, homemallFragment);



                break;
            case 1:
              /*  if (welfareFragment == null) {
                    welfareFragment = WelfareFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, welfareFragment);*/


                if (oilFragment == null) {

                    oilFragment = OilFragment.newInstance(pid);
                }

             /*   if (pid!=0){
                    oilFragment.setOilPackageFragment(pid, money,month);
                }*/

                transaction.replace(R.id.fragment_content, oilFragment);


                break;
            case 2:
                if (discoverFragment == null) {
                    discoverFragment = DiscoverFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, discoverFragment);
                /*if (findFragment == null) {
                    findFragment = FindFragment.instance();
                }
                transaction.replace(R.id.fragment_content, findFragment);*/
                break;
            case 3:
                if (person == null) {
                    person = PersonFragment.instance();
                }
                transaction.replace(R.id.fragment_content, person);
                break;

            case 4: //新增的 中间的安全
                if (safe == null) {
                    safe = SafeFragment.instance();
                }
                transaction.replace(R.id.fragment_content, safe);

                break;


            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    //设置首页跳转各个fragment
    public void setCheckedFram(int checkedFram) {
        resetTabState();
        switch (checkedFram) {
            case 1:
                setTabState(tvHome, R.drawable.icon_main_home_press, 0xFF333333);//设置Tab状态
                switchFragment(0);
                break;
            case 2:
                setTabState(tvInvest, R.drawable.icon_main_fund_press, 0xFF333333);
                switchFragment(1);
                break;
            case 3:
                setTabState(tvFind, R.drawable.icon_main_more_press, 0xFF333333);
                switchFragment(2);
                break;
            case 4:
                setTabState(tvPerson, R.drawable.icon_main_account_press, 0xFF333333);
                switchFragment(3);
                break;
        }
    }

    /*重置底部按钮的颜色状态
    * 0xFF999999
    * */
    public void resetTabState() {
        setTabState(tvHome, R.drawable.icon_main_home, 0xFF999999);
        setTabState(tvInvest, R.drawable.icon_main_fund, 0xFF999999);
        setTabState(tvFind, R.drawable.icon_main_more, 0xFF999999);
        setTabState(tvPerson, R.drawable.icon_main_account, 0xFF999999);
    }

    /*设置底部按钮的颜色状态*/
    private void setTabState(TextView textView, int image, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, image, 0, 0);//Call requires API level 17
        }
        LogUtils.e(color + "color");
        textView.setTextColor(color);
    }

    private View layout;
    private PopupWindow popupWindow;

    public void showPopupWindowLogin() {
        // 加载布局
        layout = LayoutInflater.from(this).inflate(R.layout.new_pop_person_login, null);
        // 找到布局的控件
        // 实例化popupWindow
        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        TextView bt_login = (TextView) (layout).findViewById(R.id.bt_login);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bt_login.getLayoutParams();
        lp.setMargins(20, 10, 20, 30);
        bt_login.setLayoutParams(lp);
        // 控制键盘是否可以获得焦点
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setFocusable(true);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return false;
            }
        });
//		// 设置popupWindow弹出窗体的背景
//		WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 监听
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1f);
//            }
//        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, LoginActivity.class).putExtra("point", "personFrag"));
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private View layout2;
    private PopupWindow popupWindow2;
    private int size;
    private List<Object> imgList;

    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            size++;
            Log.i("Banner--->size：", size + "");
            mViewPager.setCurrentItem(size);
            if (size == imgList.size()) {
                size = 0;
                mViewPager.setCurrentItem(0);
                handler.postDelayed(this, 1500);
            } else {
                handler.postDelayed(this, 1500);
            }
        }
    };

    private void initImgUrl(List<FriendBean> list) {
        imgList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            imgList.add(list.get(i).getAppPic());
        }
    }

    private GalleryViewPager mViewPager;
    private SimpleAdapter mPagerAdapter;

    private void showPopupWindowActivity() {
        // 加载布局
        layout2 = LayoutInflater.from(this).inflate(R.layout.pop_home_activity, null);
        mViewPager = (GalleryViewPager) layout2.findViewById(R.id.viewpager);
        mViewPager.setPageTransformer(true, new ScalePageTransformer());

        mPagerAdapter = new SimpleAdapter(MainActivity.this);
        mViewPager.setAdapter(mPagerAdapter);

        //设置OffscreenPageLimit
        mViewPager.setOffscreenPageLimit(Math.min(list.size(), 5));
        mPagerAdapter.addAll(list);

        mViewPager.setCurrentItem(0);
        handler.postDelayed(runnable, 1500);

        // 实例化popupWindow
        popupWindow2 = new PopupWindow(layout2, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        // 控制键盘是否可以获得焦点
        popupWindow2.setBackgroundDrawable(new BitmapDrawable());
        popupWindow2.setOutsideTouchable(true);
        popupWindow2.setFocusable(true);
        layout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow2.dismiss();
                handler.removeCallbacks(runnable);
                return false;
            }
        });

        popupWindow2.showAtLocation(layout2, Gravity.CENTER, 0, 0);
    }

    public class SimpleAdapter extends MyPageradapter {

        private final List<FriendBean> mList;
        private final Context mContext;

        public SimpleAdapter(Context context) {
            mList = new ArrayList<>();
            mContext = context;
        }

        public void addAll(List<FriendBean> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            ImageView imageView = null;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setTag(position);
            //Picasso.with(mContext).load(mList.get(position).getAppPic()).into(imageView);
            Glide.with(MainActivity.this).load(mList.get(position).getAppPic()).placeholder(R.drawable.bg_home_banner_fail)
                    .error(R.drawable.bg_home_banner_fail).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((mViewPager.getCurrentItem()) == position) {
                        if (mList.get(0).getStatus() == 1) {
                            startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                                    .putExtra("URL", mList.get(0).getAppUrl() + "?app=true")
                                    .putExtra("TITLE", mList.get(0).getTitle())
                                    .putExtra("AFID", mList.get(0).getId() + ""));
                        }
                    }

                }
            });
//            }

            return imageView;
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //将对应的秒值转成一年中的第几天：DAY_OF_YEAR
    private int time2DayOfYear(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        return dayOfYear;
    }

    private long mPressedTime = 0;

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();// 获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {// 比较两次按键时间差
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else {// 退出程序
            //中间底部的活动弹框，每天只弹出一次。记录上一次退出的日期
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("day", day);
            editor.commit();
            this.finish();
            System.exit(0);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     **/

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }


    /**
     * 设置消息的小红点 的显示隐藏
     */
    public void setMainRedCircle(boolean isRead) {
        if (!isRead) {
            mainRedCircle.setVisibility(View.VISIBLE);
        } else {
            mainRedCircle.setVisibility(View.GONE);
        }
    }

    /**
     * 设置油卡套餐
     */
    public void setOilFragment(int Pid, int Money,int Month) {


        pid=Pid;
        money=Money;
        month=Month;

        LogUtils.e(Pid+"设置油卡套餐0000"+month);
        if (oilFragment != null) {
            oilFragment.setOilPackageFragment(pid, money,month);
            LogUtils.e("设置油卡套餐11111");
        }
    }


}
