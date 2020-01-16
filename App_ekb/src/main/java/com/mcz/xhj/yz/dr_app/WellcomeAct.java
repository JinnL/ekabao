package com.mcz.xhj.yz.dr_app;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mcz.xhj.R;
import com.umeng.analytics.MobclickAgent;
import com.mcz.xhj.yz.dr_application.LocalApplication;
import com.mcz.xhj.yz.dr_service.UpdateService;
import com.mcz.xhj.yz.dr_urlconfig.UrlConfig;
import com.mcz.xhj.yz.dr_util.LogUtils;
import com.mcz.xhj.yz.dr_util.stringCut;
import com.mcz.xhj.yz.dr_view.ButtonProgressBar;
import com.mcz.xhj.yz.dr_view.ToastMaker;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import okhttp3.Call;

public class WellcomeAct extends BaseActivity {
    @BindView(R.id.vp_wellcome)
    @Nullable
    ViewPager vp_wellcome;
    private SharedPreferences preferences;
    private boolean flag; //第一次安装程序
    private boolean isSucces = false;
    public static boolean isForeground = false;

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.logo_act;
    }

    String apkDownload = UrlConfig.APKDOWNLOAD;

    @Override
    protected void initParams() {
        if (!isTaskRoot()) {
            // Android launched another instance of the root activity into an existing task
            //  so just quietly finish and go away, dropping the user back into the activity
            //  at the top of the stack (ie: the last state of this task)
            finish();
            return;
        }

        if (LocalApplication.getInstance().channelName != null && !"".equalsIgnoreCase(LocalApplication.getInstance().channelName)) {
            if (LocalApplication.getInstance().channelName.equalsIgnoreCase("alibaba")) {
                apkDownload = UrlConfig.APKDOWNLOAD1;
            } else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("360")) {
                apkDownload = UrlConfig.APKDOWNLOAD360;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("xiaomi")) {
                apkDownload = UrlConfig.APKDOWNLOADXIAOMI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("huawei")) {
                apkDownload = UrlConfig.APKDOWNLOADHUAWEI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("oppo")) {
                apkDownload = UrlConfig.APKDOWNLOADOPPO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("vivo")) {
                apkDownload = UrlConfig.APKDOWNLOADVIVO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("qq")) {
                apkDownload = UrlConfig.APKDOWNLOADYINGYONGBAO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("lenovo")) {
                apkDownload = UrlConfig.APKDOWNLOADLENOVO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("anzhi")) {
                apkDownload = UrlConfig.APKDOWNLOADANZHI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("mumayi")) {
                apkDownload = UrlConfig.APKDOWNLOADMUMAYI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("yingyonghui")) {
                apkDownload = UrlConfig.APKDOWNLOAD11;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("meizu")) {
                apkDownload = UrlConfig.APKDOWNLOADMEIZU;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("letv")) {
                apkDownload = UrlConfig.APKDOWNLOADLETV;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("smartisan")) {
                apkDownload = UrlConfig.APKDOWNLOADCHUIZI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("jinli")) {
                apkDownload = UrlConfig.APKDOWNLOAD15;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("sougou")) {
                apkDownload = UrlConfig.APKDOWNLOADSOUGOU;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("cool")) {
                apkDownload = UrlConfig.APKDOWNLOAD17;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("sansung")) {
                apkDownload = UrlConfig.APKDOWNLOADSANXING;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("baidu")) {
                apkDownload = UrlConfig.APKDOWNLOADBAIDU;
            }


            /*if (LocalApplication.getInstance().channelName.equalsIgnoreCase("1")) {
                apkDownload = UrlConfig.APKDOWNLOAD1;
            } else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("2")) {
                apkDownload = UrlConfig.APKDOWNLOAD360;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("3")) {
                apkDownload = UrlConfig.APKDOWNLOADXIAOMI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("4")) {
                apkDownload = UrlConfig.APKDOWNLOADHUAWEI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("5")) {
                apkDownload = UrlConfig.APKDOWNLOADOPPO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("6")) {
                apkDownload = UrlConfig.APKDOWNLOADVIVO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("7")) {
                apkDownload = UrlConfig.APKDOWNLOADYINGYONGBAO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("8")) {
                apkDownload = UrlConfig.APKDOWNLOADLENOVO;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("9")) {
                apkDownload = UrlConfig.APKDOWNLOADANZHI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("10")) {
                apkDownload = UrlConfig.APKDOWNLOADMUMAYI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("11")) {
                apkDownload = UrlConfig.APKDOWNLOAD11;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("12")) {
                apkDownload = UrlConfig.APKDOWNLOADMEIZU;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("13")) {
                apkDownload = UrlConfig.APKDOWNLOADLETV;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("14")) {
                apkDownload = UrlConfig.APKDOWNLOADCHUIZI;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("15")) {
                apkDownload = UrlConfig.APKDOWNLOAD15;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("16")) {
                apkDownload = UrlConfig.APKDOWNLOADSOUGOU;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("17")) {
                apkDownload = UrlConfig.APKDOWNLOAD17;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("18")) {
                apkDownload = UrlConfig.APKDOWNLOADSANXING;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("dandanzhuan")) {
                apkDownload = UrlConfig.APKDOWNLOADDANDANZHUAN;
            }else if (LocalApplication.getInstance().channelName.equalsIgnoreCase("tangzhuan")) {
                apkDownload = UrlConfig.APKDOWNLOADTANGZHUAN;
            } else if(LocalApplication.getInstance().channelName.equalsIgnoreCase("shihai")){
                apkDownload = UrlConfig.APKDOWNLOADSHIHAI;
            }*/
        }

        preferences = LocalApplication.getInstance().sharereferences;
        flag = preferences.getBoolean("FirstLog", true);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        if (!preferences.getString("uid", "").equalsIgnoreCase("")) {
            LogUtils.i("--->POSTTIME：str:"+str+", systime:"+preferences.getString("systime", ""));
            if (!str.equalsIgnoreCase(preferences.getString("systime", ""))) {
                postTime();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("systime", str);
                editor.commit();
            }
        }
        getReNewAl();

        createFile(getResources().getString(R.string.app_name));
    }

    private void postTime() {
        LogUtils.i("--->POSTTIME：uid:"+preferences.getString("uid", "")+", token:"+preferences.getString("token", ""));
        OkHttpUtils
                .post()
                .url(UrlConfig.POSTTIME)
                .addParams("uid", preferences.getString("uid", ""))
                .addParams("token", preferences.getString("token", ""))
                .addParam("version", UrlConfig.version)
                .addParam("channel", "2").build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        LogUtils.i("--->POSTTIME："+response);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        // TODO Auto-generated method stub
                    }
                });
    }

    boolean updating = false;

    @Override
    public void onButtonClicked(Dialog dialog, int position, Object tag) {
        if (position == 0) {//dialog只有一个按钮
            if (tag.equals("updatesingle")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
                        return;
                    } else {

                        final ButtonProgressBar bpb = (ButtonProgressBar) dialog.findViewById(R.id.bpb);
                        bpb.setVisibility(View.VISIBLE);
                        bpb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!updating) {
                                    updating = true;
                                    downloadFile(bpb, apkDownload);
                                }
                            }
                        });
//                        ToastMaker.showShortToast("有重大更新，请等待下载完毕！");
                    }
                } else {
//					DialogMaker.setUpdate();
                    // 开启更新服务UpdateService
//					Intent updateIntent = new Intent(WellcomeAct.this, UpdateService.class).putExtra("update", UrlConfig.APKDOWNLOAD);
//					startService(updateIntent);
                    final ButtonProgressBar bpb = (ButtonProgressBar) dialog.findViewById(R.id.bpb);
                    bpb.setVisibility(View.VISIBLE);
                    bpb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!updating) {
                                updating = true;
                                downloadFile(bpb, apkDownload);
                            }
                        }
                    });
                    ToastMaker.showShortToast("有重大更新，请等待后台下载完毕！");
                }
            } else if (tag.equals("update")) {
                Go();
            }
//			else{
//				Go();
//			}
        } else if (position == 1) {//dialog有两个按钮
            if (tag.equals("update")) {
                LogUtils.i("---->普通更新。。。");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                        return;
                    } else {
                        LogUtils.i("---->安装更新包"+apkDownload);
                        // 开启更新服务UpdateService
                        Intent updateIntent = new Intent(WellcomeAct.this, UpdateService.class).putExtra("update", apkDownload);
                        startService(updateIntent);
                        ToastMaker.showShortToast("已转至后台下载");
                        Go();
                    }
                } else {
                    // 开启更新服务UpdateService
                    Intent updateIntent = new Intent(WellcomeAct.this, UpdateService.class).putExtra("update", apkDownload);
                    startService(updateIntent);
//					dialog.findViewById(R.id.tv_update).setVisibility(View.VISIBLE);
                    ToastMaker.showShortToast("已转至后台下载");
                    Go();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 开启更新服务UpdateService
                    Intent updateIntent = new Intent(WellcomeAct.this, UpdateService.class).putExtra("update", apkDownload);
                    startService(updateIntent);
                    ToastMaker.showShortToast("已转至后台下载");
                    Go();
                } else {
                    ToastMaker.showLongToast("请先同意授权");
                    finish();
                }
            }
        } else if (requestCode == 112) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//					DialogMaker.setUpdate();
                    // 开启更新服务UpdateService
//					Intent updateIntent = new Intent(WellcomeAct.this, UpdateService.class).putExtra("update", UrlConfig.APKDOWNLOAD);
//					startService(updateIntent);
//					dialog.findViewById(R.id.tv_update).setVisibility(View.VISIBLE);
                    final ButtonProgressBar bpb = (ButtonProgressBar) dialog.findViewById(R.id.bpb);
                    bpb.setVisibility(View.VISIBLE);
                    bpb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!updating) {
                                updating = true;
                                downloadFile(bpb, apkDownload);
                            }
                        }
                    });
//                    ToastMaker.showShortToast("有重大更新，请等待下载完毕！");
                } else {
                    ToastMaker.showLongToast("请先同意授权");
                    finish();
                }
            }
        }
    }

    private void Go() {

        isSucces = false;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isSucces) {
//					OkHttpUtils.post().url(UrlConfig.RENEWAL)
//							.addParam("version", UrlConfig.version)
//							.addParam("channel", "2").build().cancel();
                    OkHttpUtils.getInstance().cancelTag(this);
                    if (!flag) {
                        skipToMain();
                    } else {
                        //打开引导页
                        startActivity(new Intent(WellcomeAct.this, LogAct.class)
                                .putExtra("imgUrl", "").putExtra("location", "")
                                .putExtra("title", ""));
                        finish();
                    }
                }
            }
        }, 1000);
    }

    // 更新2.0
    private String isForceUpdate = null;
    private String version_renewal = null;
    private String content = "发现新版本,是否更新";
    private Dialog dialog;
    private long mPressedTime = 0;
    private boolean isUpdate = true;
    private String serverVersion, maxVersion;// 服务器版本 ,最新版本号
    private String updateUrl;

    private void getReNewAl() {
        OkHttpUtils.post().url(UrlConfig.RENEWAL)
                .addParam("version", UrlConfig.version)
                .addParam("channel", "2").build()
                .connTimeOut(10000).execute(new StringCallback() {

            @Override
            public void onResponse(String response) {
                LogUtils.i("--->版本更新renewal："+response);
                JSONObject obj = JSON.parseObject(response);
                if (obj.getBoolean("success")) {
                    isUpdate = false;
                    JSONObject map = obj.getJSONObject("map");
                    maxVersion = map.getString("maxVersion");
//                    maxVersion = "2.0.1";
                    if (map.getString("isMaintenance") != null) {
                        if (!"".equalsIgnoreCase(map.getString("isMaintenance"))) {
                            startActivity(new Intent(WellcomeAct.this, WebViewActivity.class)
                                    .putExtra("URL", map.getString("isMaintenance"))
                                    .putExtra("TITLE", "系统维护"));
                            finish();
                            return;
                        }
                    }
                    JSONObject sysAppRenewal = map.getJSONObject("sysAppRenewal");
                    if (sysAppRenewal != null) {
                        isForceUpdate = sysAppRenewal.getString("isForceUpdate");
                        version_renewal = sysAppRenewal.getString("releaseVersion");
//                        version_renewal = "2.0.0";
                        content = sysAppRenewal.getString("content");
                    }
                    if (maxVersion != null && !maxVersion.equalsIgnoreCase("")) {
                        if (stringCut.compareVersion(maxVersion, LocalApplication.localVersion) > 0) {
                            if (sysAppRenewal != null && !isForceUpdate.equalsIgnoreCase("") && isForceUpdate != null && !version_renewal.equalsIgnoreCase("") && version_renewal != null) {
                                if ("1".equals(isForceUpdate)) {//1是强制更新0是非强制
                                    LogUtils.i("--->isForceUpdate强制更新！");
                                    if (stringCut.compareVersion(version_renewal, LocalApplication.localVersion) > 0) {
                                        LogUtils.i("--->强制更新！！");
                                        dialog = showAlertDialog("重大更新", stringCut.HuanHang_Cut(content), new String[]{"立即更新"}, false, false, "updatesingle");
                                        final ButtonProgressBar bpb = (ButtonProgressBar) dialog.findViewById(R.id.bpb);
                                        bpb.setVisibility(View.VISIBLE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (ContextCompat.checkSelfPermission(WellcomeAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                //申请权限
                                                ActivityCompat.requestPermissions(WellcomeAct.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
                                                return;
                                            } else {
                                                bpb.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (!updating) {
                                                            updating = true;
                                                            downloadFile(bpb, apkDownload);
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            bpb.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (!updating) {
                                                        updating = true;
                                                        downloadFile(bpb, apkDownload);
                                                    }
                                                    ToastMaker.showShortToast("有重大更新！请等待下载完成哟！");
                                                }
                                            });
                                        }
//														isUpdate = true;
                                    } else {
                                        dialog = showAlertDialog("版本更新", stringCut.HuanHang_Cut(content), new String[]{"取消", "立即更新"}, true, true, "update");
                                    }
                                } else {
                                    dialog = showAlertDialog("版本更新", stringCut.HuanHang_Cut(content), new String[]{"取消", "立即更新"}, true, true, "update");
                                }
                            } else {
                                dialog = showAlertDialog("版本更新", stringCut.HuanHang_Cut(content), new String[]{"取消", "立即更新"}, true, true, "update");
                            }
                        } else {
                            Go();
                        }
                    }
                } else {

                    if(obj.getString("errorCode").equals("9999")){
                        ToastMaker.showShortToast("系统错误");
                        Go();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e) {
                LogUtils.i("--->获取版本更新renewal失败！");
                ToastMaker.showShortToast("网络异常");
                Go();
            }
        });
    }

    /**
     * @return void
     * @Description: 自动安装
     */
    private void InstallationAPK() {
        LogUtils.i("---->安装更新包");
        // 下载完成，点击安装
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
            LogUtils.i("---->安装更新包SDK_INT >= 25");
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", updateFile);//在AndroidManifest中的android:authorities值
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            startActivity(install);
        } else {
            LogUtils.i("---->安装更新包else");
            Uri uri = Uri.fromFile(updateFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        }

    }

    public static File updateDir = null;
    public static File updateFile = null;
    /***********保存升级APK的目录***********/
    public static final String KonkaApplication = "konkaUpdateApplication";

    public static boolean isCreateFileSucess;

    /**
     * 方法描述：createFile方法
     */
    public static void createFile(String app_name) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            isCreateFileSucess = true;

            updateDir = new File(Environment.getExternalStorageDirectory() + "/" + KonkaApplication + "/");
            updateFile = new File(updateDir + "/" + app_name + ".apk");

            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    isCreateFileSucess = false;
                    e.printStackTrace();
                }
            }

        } else {
            isCreateFileSucess = false;
        }
    }

    /**
     * 获取广告页信息
     */
    public void startAdvertisement() {
        OkHttpUtils.post().url(UrlConfig.STARTADVERTISEMENT)
                .tag(this)
                .addParam("version", UrlConfig.version)
                .addParam("channel", "2").build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.i("--->启动页广告startAdvertisement："+response);
                        isSucces = true;
                        JSONObject obj = JSON.parseObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject map = obj.getJSONObject("map");
                            if (map.size() > 0) {
                                JSONObject sysBanner = map.getJSONObject("sysBanner");
                                final String imgUrl = sysBanner.getString("imgUrl");
                                final String location = sysBanner.getString("location");
                                final String title = sysBanner.getString("title");
                                startActivity(new Intent(WellcomeAct.this, AdverActivity.class)
                                        .putExtra("imgUrl", imgUrl)
                                        .putExtra("location", location)
                                        .putExtra("title", title));
                                finish();
                            } else {
                                startActivity(new Intent(WellcomeAct.this, MainActivity.class));
                                finish();
                            }
                        } else {
                            startActivity(new Intent(WellcomeAct.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        LogUtils.i("--->启动页广告startAdvertisement获取失败！");
                        isSucces = true;
                        ToastMaker.showShortToast("网络异常");
                        startActivity(new Intent(WellcomeAct.this, MainActivity.class));
                        finish();
                    }
                });
    }

    public void skipToMain() {
        //获取广告页信息
        startAdvertisement();

        //startActivity(new Intent(WellcomeAct.this, MainActivity.class));
       // finish();
    }

    public void downloadFile(final ButtonProgressBar bpb, String url) {

        LogUtils.e("apk更新的url："+url);
        bpb.startLoader();
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory() + "/konkaUpdateApplication/", "/" + getResources().getString(R.string.app_name) + ".apk")//
                {

                    @Override
                    public void inProgress(float progress) {
                        bpb.setProgress((int) (100 * progress));
                        Log.e("e", "inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        bpb.stopLoader();
                        ToastMaker.showLongToast("请检查网络");
                        Log.e("e", "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File response) {
                        bpb.stopLoader();
                        InstallationAPK();
                        Log.e("e", "onResponse :" + response.getAbsolutePath());
                    }

                });
    }

}
