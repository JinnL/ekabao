package com.ekabao.oil.ui.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ekabao.oil.R;
import com.ekabao.oil.bean.OilOrderDetailBean;
import com.ekabao.oil.global.LocalApplication;
import com.ekabao.oil.ui.activity.me.MallOrderDetailsActivity;
import com.ekabao.oil.ui.activity.me.MyOilCardBuyDetailsActivity;
import com.ekabao.oil.ui.activity.me.MyOrderDetailsActivity;
import com.ekabao.oil.util.Arith;
import com.ekabao.oil.util.DisplayUtil;
import com.ekabao.oil.util.JStringKit;
import com.ekabao.oil.util.LogUtils;
import com.ekabao.oil.util.StringCut;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.socialize.utils.ContextUtil.getPackageName;


/**
 * 对app的所有对话框进行管理
 *
 * @author blue
 */
@SuppressLint("InflateParams")
public class DialogMaker {

    public interface DialogCallBack {
        /**
         * 对话框按钮点击回调
         *
         * @param position 点击Button的索引.
         * @param tag
         */
        public void onButtonClicked(Dialog dialog, int position, Object tag);

        /**
         * 当对话框消失的时候回调
         *
         * @param tag
         */
        public void onCancelDialog(Dialog dialog, Object tag);
    }

    public static final String BIRTHDAY_FORMAT = "%04d-%02d-%02d";

    /**
     * 创建一个通用的alert对话框
     *
     * @return 显示并返回对话框
     */

    public static void setUpdate() {
        tv_update.setVisibility(View.VISIBLE);
    }

    private static TextView tv_update;

    public static Dialog showCommonAlertDialog(Context context, String title, String msg, String[] btns, final DialogCallBack callBack, boolean isCanCancelabel, final boolean isDismissAfterClickBtn, final Object tag) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_alert_common_layout, null);
        TextView contentTv = (TextView) contentView.findViewById(R.id.dialog_content_tv);
        TextView titleTv = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        tv_update = (TextView) contentView.findViewById(R.id.tv_update);
        OnClickListener lis = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != callBack) {
                    callBack.onButtonClicked(dialog, (Integer) v.getTag(), tag);
                }
                if (isDismissAfterClickBtn) {
                    dialog.dismiss();
                }
            }

        };
        if (null != btns) {
            int len = btns.length;
            if (len > 0) {
                LinearLayout btnLayout = (LinearLayout) contentView.findViewById(R.id.btn_layout);
                Button btn;
                View LineView;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, DisplayUtil.dip2px(context, 44));
                params.weight = 1.0f;
                for (int i = 0; i < len; i++) {
                    btn = new Button(context);
                    btn.setText(btns[i]);
                    btn.setTag(i);
                    btn.setSingleLine(true);
                    btn.setEllipsize(TruncateAt.END);
                    btn.setOnClickListener(lis);
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.dip2px(context, 16));
                    btn.setTextColor(0xffa0a0a0);
                    btn.setGravity(Gravity.CENTER);
                    if (0 == i && 1 == len) {
                        btn.setBackgroundResource(R.drawable.bg_base);
                        btn.setTextColor(0xffffffff);
                    } else if (0 == i) {
                        btn.setBackgroundResource(R.drawable.alert_left_btn_selector);
                    } else if (i > 0 && i < len - 1) {
                        btn.setBackgroundResource(R.drawable.alert_mid_btn_selector);
                    } else if (len - 1 == i) {
                        btn.setBackgroundResource(R.drawable.alert_right_btn_selector);
                        btn.setTextColor(Color.parseColor("#ec5c59"));
                    }
                    btn.setPadding(10, 10, 10, 10);
                    btnLayout.addView(btn, params);
                    LineView = new View(context);
                    LineView.setBackgroundColor(Color.parseColor("#b2b2b2"));
                    if (i < len - 1 && len > 1) {
                        btnLayout.addView(LineView, new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
                    }
                }
            }

        }

        if (JStringKit.isNotBlank(title)) {
            setDialogTextViewContent(context, title, titleTv);
            titleTv.setVisibility(View.VISIBLE);
        } else if (JStringKit.isNotBlank(msg)) {
            // 若标题为空 而内容不为空 内容按标题格式显示
            title = msg;
            msg = null;
            setDialogTextViewContent(context, title, titleTv);
            titleTv.setVisibility(View.VISIBLE);
        } else {
            contentTv.setVisibility(View.GONE);
            titleTv.setVisibility(View.GONE);
        }

        MarginLayoutParams mParams = (MarginLayoutParams) titleTv.getLayoutParams();
        if (JStringKit.isNotBlank(msg)) {
            final int margin = DisplayUtil.dip2px(context, 21.33f);
            mParams.topMargin = margin;
            mParams.bottomMargin = 0;
            titleTv.setLayoutParams(mParams);
            setDialogTextViewContent(context, msg, contentTv);
            contentTv.setVisibility(View.VISIBLE);
        } else if (JStringKit.isNotBlank(title)) {
            final int margin = DisplayUtil.dip2px(context, 38.67f);
            mParams.topMargin = margin;
            mParams.bottomMargin = margin;
            titleTv.setLayoutParams(mParams);
            contentTv.setVisibility(View.GONE);
        }

        contentView.requestLayout();

        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                if (null != callBack) {
                    callBack.onCancelDialog((Dialog) dialog, tag);
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        // 设置对话框宽度
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        WindowManager.LayoutParams aWmLp = window.getAttributes();
        aWmLp.width = LocalApplication.getInstance().screenW - 200;
        aWmLp.height = LocalApplication.getInstance().screenH / 2;//设置高度
        aWmLp.gravity = Gravity.CENTER;
        window.setAttributes(aWmLp);
        dialog.show();
        //LogUtils.e("登录成功"+aWmLp.width);
        return dialog;
    }

    // 设置文字信息
    public static void setDialogTextViewContent(Context context, String content, TextView tView) {
        if (null == tView || TextUtils.isEmpty(content)) {
            return;
        }

        String NEW_LINE = System.getProperty("line.separator");
        if (content.contains(NEW_LINE) || content.contains("\n")) {
//			tView.setGravity(Gravity.CENTER);
        } else {
            float destWidth = JStringKit.getContentWidth(content, tView);
            float maxWidth = DisplayUtil.dip2px(context, 235.33f);
            if (destWidth >= Math.round(maxWidth - 0.5f)) {
                tView.setGravity(Gravity.LEFT);
            } else {
                tView.setGravity(Gravity.CENTER);
            }
        }

        tView.setText(content);
    }

    /**
     * 显示统一风格的等待对话框。没有标题
     *
     * @param msg             对话框内容
     * @param callBack        对话框回调
     * @param isCanCancelabel 是否可以取消
     * @param tag
     */
    public static Dialog showCommenWaitDialog(Context context, String msg, final DialogCallBack callBack, boolean isCanCancelabel, final Object tag) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleRoundCornerStyle);
        dialog.setOwnerActivity((Activity) context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_wait_common_layout, null);
        TextView contentTv = (TextView) contentView.findViewById(R.id.dialog_content_tv);
        if (JStringKit.isNotBlank(msg)) {
            contentTv.setText(msg);
        }
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                if (null != callBack) {
                    callBack.onCancelDialog((Dialog) dialog, tag);
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.show();
        return dialog;

    }

    /**
     * 显示预约相关的对话框
     *
     * @param msg             对话框内容
     * @param imgId           图片Id  默认为0
     * @param callBack        对话框回调
     * @param isCanCancelabel 是否可以取消
     * @param isShowBtn       是否显示下方按钮
     * @param tag
     */
    public static Dialog showYuyueDialog(Context context, int imgId, String msg, final DialogCallBack callBack, boolean isCanCancelabel, boolean isShowBtn, final Object tag) {
        final Dialog dialog = new Dialog(context, R.style.Dialog_Fullscreen);
        dialog.setOwnerActivity((Activity) context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_yuyue, null);
        ImageView zuoIv = (ImageView) contentView.findViewById(R.id.yuyue_zuo_iv);
        TextView contentIv = (TextView) contentView.findViewById(R.id.yuyue_you_tv);
        ImageView cancelIv = (ImageView) contentView.findViewById(R.id.cancel_iv);
        Button yuyueBtn = (Button) contentView.findViewById(R.id.yuyue_btn);

        if (!isShowBtn) {
            yuyueBtn.setVisibility(View.GONE);
        }
        if (0 != imgId) {
            zuoIv.setImageResource(imgId);
        }

        cancelIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                callBack.onButtonClicked(dialog, 1, tag);
            }
        });

        yuyueBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                callBack.onButtonClicked(dialog, 2, tag);
            }
        });
        if (JStringKit.isNotBlank(msg)) {
            contentIv.setText(msg);
        }
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                if (null != callBack) {
                    callBack.onCancelDialog((Dialog) dialog, tag);
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.show();
        return dialog;

    }


    /**
     * 显示拨打客服电话的对话框
     */
    public static void showKufuPhoneDialog(final Context context) {
        SharedPreferences preferences = LocalApplication.getInstance().sharereferences;

        final String telPhone = preferences.getString("telPhone", "400-******");
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_find_kefuphone, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        TextView tv_telPhone = (TextView) contentView.findViewById(R.id.tv_telPhone);
        tv_telPhone.setText(telPhone);
        TextView tv_cacel = (TextView) contentView.findViewById(R.id.tv_cacel);
        TextView tv_boda = (TextView) contentView.findViewById(R.id.tv_boda);
        tv_cacel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_boda.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                // 检查是否获得了权限（Android6.0运行时权限）
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.CALL_PHONE)) {
                        // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                        // 弹窗需要解释为何需要该权限，再次请求授权
                        Toast.makeText(context, "请授权！", Toast.LENGTH_LONG).show();

                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    } else {
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);
                    }
                } else {
                    // 已经获得授权，可以打电话
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telPhone.replaceAll("-", "")));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    context.startActivity(intent);
                }

            }
        });
    }

    /**
     * 显示续投对话框
     */
    public static void showXutouDialog(final Context context, String tile, String content, String sureStr, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_btone_xutou, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) contentView.findViewById(R.id.tv_content);
        TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);
        TextView tv_close = (TextView) contentView.findViewById(R.id.tv_close);
        tv_title.setText(tile);
        tv_content.setText(content);
        tv_sure.setText(sureStr);
        tv_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 0, obj);
                dialog.dismiss();
            }
        });
        tv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示一个按钮对话框
     */
    public static void showOneBtDialog(final Context context, String tile, String content, String sureStr, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_bt_one, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) contentView.findViewById(R.id.tv_content);
        TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);
        tv_title.setText(tile);
        tv_content.setText(content);
        tv_sure.setText(sureStr);
        tv_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 0, obj);
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示两个个按钮对话框  支付 短信验证码
     */
    public static void showInvestDialog(final Context context, String tile, String content, String sureStr, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_invest_sms, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) contentView.findViewById(R.id.tv_content);
        TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);
        final EditText et_sms = (EditText) contentView.findViewById(R.id.et_sms);
        TextView tv_send_sms = (TextView) contentView.findViewById(R.id.tv_send_sms);
        TextView tv_cacel = (TextView) contentView.findViewById(R.id.tv_cacel);

        tv_title.setText(tile);
        tv_content.setText(content);
        tv_sure.setText(sureStr);

        tv_send_sms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 1, obj);
                //dialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et_sms.getText().toString();
                callBack.onButtonClicked(dialog, 0, s);
                //dialog.dismiss();
            }
        });
        tv_cacel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onCancelDialog(dialog, null);
                dialog.dismiss();
            }
        });
    }


    /**
     * 显示红色确认按钮的dialog
     * 确定 取消
     *
     * @param context
     * @param tile
     * @param content
     * @param cancelStr
     * @param sureStr
     * @param callBack
     * @param obj
     */
    public static void showRedSureDialog(final Context context, String tile, String content, String cancelStr, String sureStr, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_sure_red, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) contentView.findViewById(R.id.tv_content);
        TextView tv_cacel = (TextView) contentView.findViewById(R.id.tv_cacel);
        TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);
        tv_title.setText(tile);
        tv_content.setText(content);
        tv_cacel.setText(cancelStr);
        tv_sure.setText(sureStr);
        tv_cacel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onCancelDialog(dialog, null);
                dialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 0, obj);
                dialog.dismiss();
            }
        });

    }

    /**
     * 请确认您要添加的油卡卡号
     * 确定 取消
     *
     * @param context
     * @param tile
     * @param content
     * @param cancelStr
     * @param sureStr
     * @param callBack
     * @param obj
     */
    public static void showAddOilCardSureDialog(final Context context, String tile, String content, String cancelStr, String sureStr, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_sure_add_olicard, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) contentView.findViewById(R.id.tv_content);
        TextView tv_cacel = (TextView) contentView.findViewById(R.id.tv_cacel);
        TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);
        tv_title.setText(tile);
        String s = StringCut.dealOilCard(content);

        //tv_content.setText(content);
        tv_content.setText(s);

        tv_cacel.setText(cancelStr);
        tv_sure.setText(sureStr);
        tv_cacel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onCancelDialog(dialog, null);
                dialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 0, obj);
                dialog.dismiss();
            }
        });

    }

    /**
     * 开通存管2个btn
     *
     * @param context
     * @param callBack
     * @param obj
     */
    public static void showCunguanDialogTwo(final Context context, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_cunguan, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.show();
        TextView tv_close = (TextView) contentView.findViewById(R.id.tv_close);
        Button bt_cancal = (Button) contentView.findViewById(R.id.bt_cancal);
        Button bt_ok = (Button) contentView.findViewById(R.id.bt_ok);
        tv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bt_cancal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 0, obj);
                dialog.dismiss();
            }
        });

    }


    /**
     * 开通存管1个btn
     *
     * @param context
     * @param callBack
     * @param obj
     */
    public static void showCunguanDialogOne(final Context context, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_cunguan_cash, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.show();
        TextView tv_close = (TextView) contentView.findViewById(R.id.tv_close);
        Button bt_ok = (Button) contentView.findViewById(R.id.bt_ok);
        tv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 0, obj);
                dialog.dismiss();
            }
        });

    }

    /**
     * 充值计划
     */
    public static void showMonthDialog(final Context context, List<OilOrderDetailBean.RechargeListBean> list) {
        SharedPreferences preferences = LocalApplication.getInstance().sharereferences;


        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_order_details, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        tvTitle.setText("充值计划-" + list.size() + "个月");

        ListView lvCycle = (ListView) contentView.findViewById(R.id.lv_cycle);

        lvCycle.setAdapter(new listAdapter(context, list));

      /*  tv_boda.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });*/
    }


    static class listAdapter extends BaseAdapter {

        List<OilOrderDetailBean.RechargeListBean> list;
        Context context;

        public listAdapter(Context context, List<OilOrderDetailBean.RechargeListBean> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // View inflate = LayoutInflater.from(context).inflate(R.layout.item_order_details_dialog, parent, false);
            ViewHolder vh = null;
            if (convertView == null) {
                //convertView = inflater.inflate(R.layout.item_project, null);
                convertView = LayoutInflater.from(context).inflate(R.layout.item_order_details_dialog, parent, false);

                vh = new ViewHolder(convertView);

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            OilOrderDetailBean.RechargeListBean rechargeListBean = list.get(position);

            if (rechargeListBean.getStatus() == 1) {
                vh.ivSelect.setVisibility(View.VISIBLE);
            } else {
                vh.ivSelect.setVisibility(View.GONE);
            }


            vh.tvMoney.setText(rechargeListBean.getShouldPrincipal() + "");
            vh.tvNumber.setText((position + 1) + "");

            vh.tvTime.setText(rechargeListBean.getDate());
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tv_number)
            TextView tvNumber;
            @BindView(R.id.tv_time)
            TextView tvTime;
            @BindView(R.id.tv_money)
            TextView tvMoney;
            @BindView(R.id.iv_select)
            ImageView ivSelect;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    /**
     * 充值计划
     */
    public static void showRuleDialog(final Context context, List<String> list) {
        SharedPreferences preferences = LocalApplication.getInstance().sharereferences;


        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_rule, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        tvTitle.setText(" 油卡领取规则 ");

        ListView lvCycle = (ListView) contentView.findViewById(R.id.lv_cycle);

        lvCycle.setAdapter(new RulelistAdapter(context, list));

      /*  tv_boda.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });*/
    }

    /**
     * 好友充值记录
     */
    public static void showInviteDialog(final Context context, List<String> list) {
        SharedPreferences preferences = LocalApplication.getInstance().sharereferences;


        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_invite, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        //  dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        ImageButton ivclose = (ImageButton) contentView.findViewById(R.id.ib_close);

        TextView tv_no_data = (TextView) contentView.findViewById(R.id.tv_no_data);
        ListView lvCycle = (ListView) contentView.findViewById(R.id.lv_cycle);

        if (list.size() > 0) {
            lvCycle.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.GONE);
        } else {
            lvCycle.setVisibility(View.GONE);
            tv_no_data.setVisibility(View.VISIBLE);
        }

        ivclose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        lvCycle.setAdapter(new RulelistAdapter(context, list));

      /*  tv_boda.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });*/
    }


    static class RulelistAdapter extends BaseAdapter {

        List<String> list;
        Context context;

        public RulelistAdapter(Context context, List<String> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // View inflate = LayoutInflater.from(context).inflate(R.layout.item_order_details_dialog, parent, false);
            ViewHolder vh = null;
            if (convertView == null) {
                //convertView = inflater.inflate(R.layout.item_project, null);
                convertView = LayoutInflater.from(context).inflate(R.layout.item_textview, parent, false);

                vh = new ViewHolder(convertView);

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            String rechargeListBean = list.get(position);
            vh.tvNumber.setText(rechargeListBean);
            // vh.tvNumber.setGravity(Gravity.CENTER_HORIZONTAL);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tv_number)
            TextView tvNumber;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


    /**
     * 支付成功 。没有标题
     * 打开订单详情页
     */
    public static Dialog showPaySuccessDialog(final Context context, final DialogCallBack callBack, final int activitytype, final String orderId) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleRoundCornerStyle);
        dialog.setOwnerActivity((Activity) context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_pay_success, null);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.show();

       /* dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                if (null != callBack) {
                    callBack.onCancelDialog((Dialog) dialog, activitytype);
                }
            }
        });*/

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != callBack) {
                    callBack.onCancelDialog((Dialog) dialog, activitytype);
                }
            }
        });
        //设置了一个定时器来显示成功dialog然后自动关闭
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss();
                //结束本界面并跳转到收派员列表的界面
                startActivity(context, activitytype, orderId);

                t.cancel();
            }


        }, 3000);

        return dialog;
    }

    private static void startActivity(Context context, int activitytype, String orderId) {

        // 1：油卡 2：手机 3：直购
        switch (activitytype) {
            case 5: //油卡再支付
            case 1:
                context.startActivity(new Intent(context, MyOrderDetailsActivity.class)
                        .putExtra("orderId", orderId)
                        .putExtra("type", activitytype)
                );
                break;
            case 2:
                context.startActivity(new Intent(context, MyOrderDetailsActivity.class)
                        .putExtra("orderId", orderId)
                        .putExtra("type", activitytype)
                );
                break;
            case 3:
                context.startActivity(new Intent(context, MyOilCardBuyDetailsActivity.class)
                        .putExtra("orderId", orderId)
                        .putExtra("type", activitytype)
                );
                break;
            case 4:
                context.startActivity(new Intent(context, MallOrderDetailsActivity.class)
                        .putExtra("orderId", Integer.parseInt(orderId))
                        .putExtra("type", activitytype)
                );

                break;
              /*  default:

                    break;*/

        }


    }


    /**
     * 显示套餐退订的dialog
     * 确定 取消
     *
     * @param context
     * @param sureStr
     * @param callBack
     * @param obj
     */
    public static void showrefundDialog(final Context context, double factAmount, int shouldPrincipal, int num, double reFundAmount, double interestRate, String sureStr, final DialogCallBack callBack, final Object obj) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_sure_refund, null);
        dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) contentView.findViewById(R.id.tv_content);
        TextView tv_cacel = (TextView) contentView.findViewById(R.id.tv_cacel);
        TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);


        double mul = Arith.sub(factAmount, (shouldPrincipal * num));

        LogUtils.e(mul + "可退金额 factAmount" + factAmount + "(shouldPrincipal * num)" + (shouldPrincipal * num));


        tv_title.setText(Html.fromHtml("可退金额<font color='#FF623D'>" + StringCut.getDoubleKb(reFundAmount) + "</font>元"));

        if (interestRate > 0) {
            tv_content.setText("可退金额=支付金额-月充值额*已充月数-手续费 (" + factAmount + " - " + shouldPrincipal + "*" + num + " - " + factAmount + "*" + (interestRate * 100) + "%)");

        } else {
            tv_content.setText("可退金额=支付金额-月充值额*已充月数 (" + factAmount + " - " + shouldPrincipal + "*" + num + ")");
        }


        tv_sure.setText(sureStr);
        tv_cacel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onCancelDialog(dialog, null);
                dialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onButtonClicked(dialog, 0, obj);
                dialog.dismiss();
            }
        });

    }
}
