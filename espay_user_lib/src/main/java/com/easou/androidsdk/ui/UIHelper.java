package com.easou.androidsdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.data.PayItem;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.UnitUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class UIHelper {
    public static String TAG = "UIHelper";
    static View convertView;
    public static int screen_Width = 320, screen_Height = 480;
    private static String needChannels;
    public static EditText ed_1, ed_2, ed_3;
    public static ImageView name_check_img;


    private static int getLastVisiblePosition = 0, lastVisiblePositionY = 0;
    private static TextView userID; // 用户id
    private static TextView titleBuyInfo; // 商品名称
    private static TextView tittleInfo; // 商品数量
    private static TextView tittleAmt; // 支付金额

    private static View moreLayout, wxLayout, ylLayout, aliLayout, webLayout, jfLayout;
    private static ImageView moreImageViewid, wxImageViewid, ylImageViewid, aliImageViewid, webImageViewid,
            moreLineImageView, weixinLineImageView, unipayLineImageView, jfImageViewId,
            alipayLineImageView;

    private static Button button, tradeHistory;
    private static final int backgroundColor = 0xffdcdcdc, TEXT_COLOR_GRAY = 0xff9b9797, TEXT_COLOR_RED = 0xffff632c,
            TITLE_HIGHT = 48, HORIZONTAL_MARGIN = 15, VERTICAL_MARGIN = 15, MARGIN = 10,
            BTN_HEIGHT = 40, BTN_WIDTH = 100;

    private static View navBar;
    private static TextView titleText, backBtn, tv_title, tv_left, tv_right, payFailInfo, tv_morePayment;

    public static boolean isShowingMore, isClicked;

    private static final String navBarTextColor = "#f4f4f4", deliverLineColor = "#CCCCCC", buttonColor = "#464646",
            textViewBGColor = "#ffffffff";

    private static final int LEFT_BUTTON_ID = 0x0001, TITLE_VIEW_ID = 0x0002, CLOSE_VIEW_ID = 0x0003;


    /**
     * 创建一个全局通用的bar，主要有三个按钮：最左的按钮（这个按钮多变，比如点击事件和文字），
     * 中间的view不能点击，文字可变，最右的按钮式关闭当前页面的按钮。
     */
    public static LinearLayout createBar(final Context context, final Handler handler) {

        final int margin = UnitUtils.dp2px(context, 10);

        // ============ Back Button 返回位置的按钮=============
        LinearLayout leftLayout = newLayout(context, UnitUtils.dp2px(context, BTN_WIDTH), UnitUtils.dp2px(context, 40));
        final TextView left_pos_Btn = new TextView(context);
        left_pos_Btn.setTextColor(Color.parseColor(navBarTextColor));
        left_pos_Btn.setGravity(Gravity.CENTER);
        left_pos_Btn.setId(LEFT_BUTTON_ID);
        left_pos_Btn.setTextSize(16);

        LayoutParams leftLayoutParams = new LayoutParams(UnitUtils.dp2px(context, 50), UnitUtils.dp2px(context, 32));
        leftLayout.setOrientation(LinearLayout.VERTICAL);
        leftLayoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        leftLayoutParams.leftMargin = UnitUtils.dp2px(context, 5);
        leftLayout.addView(left_pos_Btn, leftLayoutParams);

        // ========= Image title View =========
        titleText = new TextView(context);
        titleText.setId(TITLE_VIEW_ID);
        titleText.setTextColor(Color.parseColor(navBarTextColor));
        titleText.setTextSize(18);
        titleText.setSingleLine();
        titleText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.FILL_PARENT);
        imageViewParams.weight = 1;

        // ====== Close Button =====
        LinearLayout closeLayout = newLayout(context, UnitUtils.dp2px(context, 100), UnitUtils.dp2px(context, 40));
        closeLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams closeImageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        closeImageParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        closeImageParams.rightMargin = UnitUtils.dp2px(context, 5);

        final TextView closeBtn = new TextView(context);
        closeBtn.setTextSize(16);
        closeBtn.setText("关闭");
        closeBtn.setTextColor(Color.parseColor(navBarTextColor));
        closeBtn.setPadding(0, 0, margin, 0);
        closeBtn.setId(CLOSE_VIEW_ID);
        closeLayout.addView(closeBtn, closeImageParams);
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // System.out.println(TAG + "：关闭账户中心");
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_CLOSE_ACCOUNT_CENTER;
                msg.sendToTarget();
            }
        });

        // ===== 整条navigation容器 ======
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                UnitUtils.dp2px(context, TITLE_HIGHT));
        layout.setBackgroundResource(context.getResources().getIdentifier("es_alertview_title_bg", "drawable",
                context.getPackageName()));
//		layout.setBackgroundDrawable(CommonUtils.getNinePatchDrawable(context, "es_alertview_title_bg.9.png"));

        layout.setLayoutParams(layoutParams);
        layout.addView(leftLayout);
        layout.addView(titleText, imageViewParams);
        layout.addView(closeLayout);
        return layout;
    }
    // TODO createNavigateBar与createCusBar的布局重复。或者说，将全局的导航bar抽取为一条。方便后期的修改与维护。

    /**
     * 创建导航条View
     */
    public static View createNavigateBar(final Context context, final Handler handler, boolean isPos, final String needBack) {
        // navigation bar
        LinearLayout bar = createBar(context, handler);
        backBtn = (TextView) bar.findViewById(LEFT_BUTTON_ID);

        // Back Button
        if (isPos)
            backBtn.setVisibility(View.VISIBLE);
        else
            backBtn.setVisibility(View.INVISIBLE);

        backBtn.setText("返回");
        backBtn.setTextSize(16);
        backBtn.setTextColor(Color.parseColor(navBarTextColor));
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // System.out.println(TAG + "：返回的点击事件");
                Message msg = handler.obtainMessage();
                hideKeyBoard(context);
                msg.what = Constant.HANDLER_GOBACK;

                msg.sendToTarget();
            }
        });
        return bar;
    }

    /**
     * 获取一个自定义的LinearLayout布局
     */
    public static LinearLayout newLayout(Context context, int width, int height) {
        LinearLayout back_layout = new LinearLayout(context);
        LayoutParams left_right_params = new LayoutParams(width, height);
        left_right_params.gravity = Gravity.CENTER;
        back_layout.setGravity(Gravity.CENTER);
        back_layout.setOrientation(LinearLayout.HORIZONTAL);
        back_layout.setLayoutParams(left_right_params);

        return back_layout;
    }

    /**
     * 创建标题导航View
     *
     * @throws Exception
     */
    public static View createCusBar(final Context context, final Handler handler, int type) {
        LinearLayout bar = createBar(context, handler);
        // titleText = (TextView) bar.findViewById(TITLE_VIEW_ID); // TODO
        // 为什么将他自己find出来就一句话，界面就发生变化了呢？
        final TextView left_pos_Btn = (TextView) bar.findViewById(LEFT_BUTTON_ID);
        TextView right_pos_Btn = (TextView) bar.findViewById(CLOSE_VIEW_ID);
        left_pos_Btn.setVisibility(View.VISIBLE);
        right_pos_Btn.setVisibility(View.VISIBLE);
        left_pos_Btn.setTextSize(16);
        // final ImageView closeBtn = (ImageView)
        // bar.findViewById(CLOSE_VIEW_ID);

        // left Button
        if (type == 3) {
            left_pos_Btn.setText("购买记录");
            left_pos_Btn.setTextSize(16);
            left_pos_Btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 购买记录
                    Message msg = handler.obtainMessage();
                    msg.what = Constant.HANDLER_PAYLIST_SHOW_VIEW;
                    msg.sendToTarget();
                }
            });
        } else if (type == 6) {
            left_pos_Btn.setText("返回");
            left_pos_Btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard(context);
                    // 返回
                    Message msg = handler.obtainMessage();
                    msg.what = Constant.HANDLER_LOAD_USERCENTER_VIEW;
                    msg.sendToTarget();
                }
            });
        } else {
            right_pos_Btn.setVisibility(View.GONE);
            left_pos_Btn.setVisibility(View.GONE);
        }
        return bar;
    }


    /**
     * 创建一个Toast用的提示view
     */
    public static View createToastView(Context context, String msg) {
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        // layout.setBackgroundResource(R.drawable.es_welcome_back_toast_bg);
//		Bitmap toastLayoutImg = CommonUtils.getBitmap(context, "es_welcome_back_toast_bg.png");
//		layout.setBackgroundDrawable(new BitmapDrawable(context.getResources(), toastLayoutImg));
        layout.setBackgroundResource(context.getResources().getIdentifier("es_welcome_back_toast_bg", "drawable",
                context.getPackageName()));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setLayoutParams(layoutParams);

        ImageView image = new ImageView(context);
        Bitmap luncherImg = BitmapFactory.decodeResource(context.getResources(),
                context.getResources().getIdentifier("es_logo", "drawable", context.getPackageName()));
//		Bitmap luncherImg = CommonUtils.getBitmap(context, "es_logo.png");
//		image.setBackgroundDrawable(new BitmapDrawable(context.getResources(), luncherImg));
        image.setImageBitmap(luncherImg);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        imageParams.setMargins(14, 14, 14, 14);
        layout.addView(image, imageParams);

        TextView tostText = new TextView(context);
        tostText.setText(msg);
        tostText.setTextColor(TEXT_COLOR_GRAY);
        tostText.setTextSize(16);
        LinearLayout.LayoutParams toastTextParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        toastTextParams.gravity = Gravity.CENTER_VERTICAL;
        toastTextParams.rightMargin = 50;
        layout.addView(tostText, toastTextParams);
        return layout;
    }


    /**
     * 创建一个按钮
     */
    public static View createButtonView(final Context context, String msg, final String btn_bg,
                                        final String btn_pressed, View.OnClickListener listener) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams btn_Params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                UnitUtils.dp2px(context, 48));
        btn_Params.weight = 1;

        final Button btn_1 = new Button(context);
//		btn_1.setBackgroundDrawable(CommonUtils.getNinePatchDrawable(context, btn_bg));
        btn_1.setBackgroundResource(context.getResources().getIdentifier(btn_bg, "drawable",
                context.getPackageName()));
        btn_1.setLines(1);
        btn_1.setTextSize(20);
        btn_1.setOnClickListener(listener);
        btn_1.setText(msg);
        btn_1.setTextColor(Color.WHITE);
        btn_1.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn_1.setBackgroundResource(context.getResources().getIdentifier(btn_bg, "drawable",
                            context.getPackageName()));
//					btn_1.setBackgroundDrawable(CommonUtils.getNinePatchDrawable(context, btn_bg));
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_1.setBackgroundResource(context.getResources().getIdentifier(btn_pressed, "drawable",
                            context.getPackageName()));
//					btn_1.setBackgroundDrawable(CommonUtils.getNinePatchDrawable(context, btn_pressed));
                }
                return false;
            }

        });
        layout.addView(btn_1, btn_Params);

        return layout;
    }

    /**
     * 创建ScrollView
     */
    public static View createScrollView(final Context context) {
        ScrollView mScroll_View = new ScrollView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, (screen_Height / 20) * 17);
        mScroll_View.setLayoutParams(layoutParams);
        mScroll_View.setFadingEdgeLength(0);
        return mScroll_View;

    }

    /**
     * 创建分割线的View
     */
    public static View createDeliverView(final Context context) {
        // 分割线
        View deliverView = new View(context);
        deliverView.setBackgroundColor(Color.parseColor(deliverLineColor));
        LinearLayout.LayoutParams deliverParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                UnitUtils.dp2px(context, 1));
        deliverParams.gravity = Gravity.CENTER;
        deliverParams.setMargins(40, 0, 40, 0);
        deliverView.setLayoutParams(deliverParams);

        return deliverView;
    }

    /**
     * 支付失败的view
     */
    public static View createPayFailView(Context context, final Handler handler, String msg) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(
                context.getResources().getIdentifier("easou_payfail", "layout", context.getPackageName()), null);

        navBar = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_HeadLayout", "id", context.getPackageName()));

        tv_title = (TextView) navBar.findViewById(
                context.getResources().getIdentifier("espay_navbar_title", "id", context.getPackageName()));
        tv_title.setText("购买失败");

        tv_right = (TextView) navBar.findViewById(
                context.getResources().getIdentifier("espay_navbar_right", "id", context.getPackageName()));
        tv_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_CLOSE_ACCOUNT_CENTER;
                msg.sendToTarget();
            }
        });

        payFailInfo = (TextView) convertView.findViewById(
                context.getResources().getIdentifier("espay_payfail_info", "id", context.getPackageName()));
        payFailInfo.setText(msg);

        tradeHistory = (Button) convertView.findViewById(
                context.getResources().getIdentifier("espay_payfail_tradeHistory", "id", context.getPackageName()));
        tradeHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_PAYLIST_SHOW_VIEW;
                msg.sendToTarget();
            }
        });
        return convertView;
    }

    /**
     * 购买记录列表
     */
    public static View createMainPayListView(final Context context, final Handler handler, View titleView,
                                             LinkedList<PayItem> datalist) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, 0);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setLayoutParams(layoutParams);
//		Bitmap main_bg_Img = CommonUtils.getBitmap(context, "es_main_bg.png");
//		layout.setBackgroundDrawable(new BitmapDrawable(context.getResources(), main_bg_Img));
        layout.setBackgroundResource(context.getResources().getIdentifier("es_main_bg", "drawable",
                context.getPackageName()));

        // 添加导航条
        layout.addView(titleView);

        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
        rightParams.weight = 1;
        rightParams.setMargins(20, 20, 20, 10);
        HashMap<String, Object> lay = createPayListView(context, handler, datalist);
        layout.addView((View) lay.get("View"), rightParams);

        return layout;

    }

    /**
     * 支付界面
     */
    public static View createPayView(final Context context, final Handler handler, Map<String, String> map) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(
                context.getResources().getIdentifier("easou_opt_pannel", "layout", context.getPackageName()),
                null);

        navBar = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_HeadLayout", "id", context.getPackageName()));

        tv_title = (TextView) navBar.findViewById(
                context.getResources().getIdentifier("espay_navbar_title", "id", context.getPackageName()));
        tv_title.setText("宜支付收银台");
        tv_title.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ESToast.getInstance().ToastShow(context, "version:" + Constant.SDK_VERSION);
                return false;
            }
        });

        tv_left = (TextView) navBar.findViewById(
                context.getResources().getIdentifier("espay_navbar_left", "id", context.getPackageName()));
        tv_left.setText("购买记录");
        tv_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 购买记录
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_PAYLIST_SHOW_VIEW;
                msg.sendToTarget();
            }
        });

        tv_right = (TextView) navBar.findViewById(
                context.getResources().getIdentifier("espay_navbar_right", "id", context.getPackageName()));
        tv_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_CLOSE_ACCOUNT_CENTER;
                msg.sendToTarget();
            }
        });


        // 微信
        wxLayout = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_wxChannelLayout", "id", context.getPackageName()));
        wxLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    ESToast.getInstance().ToastShow(context, "请不要频繁操作！");
                    return;
                }
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_WECHAT;
                msg.sendToTarget();
                isClicked = true;
            }
        });

        // 支付宝
        aliLayout = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_aliChannelLayout", "id", context.getPackageName()));
        aliLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    ESToast.getInstance().ToastShow(context, "请不要频繁操作！");
                    return;
                }
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_ALIPAY;
                msg.sendToTarget();
                isClicked = true;
            }
        });

        //积分兑换
       /* jfLayout = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_jfChannelLayout", "id", context.getPackageName()));
        jfLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    ESToast.getInstance().ToastShow(context, "请不要频繁操作！");
                    return;
                }
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_JFPAY;
                msg.sendToTarget();
                isClicked = true;
            }
        });*/

        // 银联
        ylLayout = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_ylChannelLayout", "id", context.getPackageName()));
        ylLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    ESToast.getInstance().ToastShow(context, "请不要频繁操作！");
                    return;
                }
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_UNIPAY;
                msg.sendToTarget();
                isClicked = true;
            }
        });

        // 更多支付方式
        moreLayout = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_moreChannelLayout", "id", context.getPackageName()));
        tv_morePayment = (TextView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_moreChannelText", "id", context.getPackageName()));
        moreLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowingMore) {
                    hideMorePayment();
                } else {
                    showMorePayment();
                }
            }
        });


        // 网页
        // String needWeb = mBundle.getString(Constant.NEEDWEB);
        webLayout = convertView.findViewById(
                context.getResources().getIdentifier("easou_id_webChannelLayout", "id", context.getPackageName()));
        webLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    ESToast.getInstance().ToastShow(context, "请不要频繁操作！");
                    return;
                }
                Message msg = handler.obtainMessage();
                msg.what = Constant.HANDLER_WEBPAY;
                msg.sendToTarget();
                isClicked = true;
            }
        });

        if (webLayout.getVisibility() == View.GONE) {
            webLayout.setVisibility(View.VISIBLE);
        }
        wxImageViewid = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_wxChannelid", "id", context.getPackageName()));
        ylImageViewid = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_ylChannelid", "id", context.getPackageName()));
        aliImageViewid = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_aliChannelid", "id", context.getPackageName()));
        webImageViewid = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_webChannelid", "id", context.getPackageName()));
        moreImageViewid = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_moreChannelid", "id", context.getPackageName()));
        jfImageViewId = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_jfChannelid", "id", context.getPackageName()));
        wxLayout.setVisibility(View.GONE);
        wxImageViewid.setVisibility(View.GONE);
//        ylLayout.setVisibility(View.GONE);
//        ylImageViewid.setVisibility(View.GONE);
        aliLayout.setVisibility(View.GONE);
        aliImageViewid.setVisibility(View.GONE);
        webLayout.setVisibility(View.GONE);
        webImageViewid.setVisibility(View.GONE);
        moreLayout.setVisibility(View.GONE);
        moreImageViewid.setVisibility(View.GONE);

        weixinLineImageView = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_wxChannelid", "id", context.getPackageName()));
        weixinLineImageView.setVisibility(View.GONE);

        unipayLineImageView = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_ylChannelid", "id", context.getPackageName()));
        unipayLineImageView.setVisibility(View.GONE);

        alipayLineImageView = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_aliChannelid", "id", context.getPackageName()));
        alipayLineImageView.setVisibility(View.GONE);

        moreLineImageView = (ImageView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_moreChannelid", "id", context.getPackageName()));
        moreLineImageView.setVisibility(View.GONE);

        needChannels = map.get(ESConstant.NEED_CHANNELS);

        if (TextUtils.isEmpty(needChannels)) {
            wxLayout.setVisibility(View.VISIBLE);
            wxImageViewid.setVisibility(View.VISIBLE);
//			ylLayout.setVisibility(View.VISIBLE);
//			ylImageViewid.setVisibility(View.VISIBLE);
            aliLayout.setVisibility(View.VISIBLE);
            aliImageViewid.setVisibility(View.VISIBLE);
            webLayout.setVisibility(View.VISIBLE);
            webImageViewid.setVisibility(View.VISIBLE);
            moreLayout.setVisibility(View.GONE);
            moreImageViewid.setVisibility(View.GONE);

            weixinLineImageView.setVisibility(View.VISIBLE);
            unipayLineImageView.setVisibility(View.VISIBLE);
            alipayLineImageView.setVisibility(View.VISIBLE);
            moreLineImageView.setVisibility(View.GONE);

        } else {
            if (needChannels.contains("WECHAT")) {
                wxLayout.setVisibility(View.VISIBLE);
                wxImageViewid.setVisibility(View.VISIBLE);
            }
            if (needChannels.contains("ALIPAY2")) {
                aliLayout.setVisibility(View.VISIBLE);
                aliImageViewid.setVisibility(View.VISIBLE);
            }
            if (needChannels.contains("UNIONPAY2")) {
//				ylLayout.setVisibility(View.VISIBLE);
//				ylImageViewid.setVisibility(View.VISIBLE);
            }

            if (needChannels.contains("WEB")) {
                moreLayout.setVisibility(View.VISIBLE);
                moreImageViewid.setVisibility(View.VISIBLE);
            }

            if (!isShowingMore) {
                moreLineImageView.setVisibility(View.GONE);
            }

            if (!needChannels.contains("WFTQQWALLET") && !needChannels.contains("WECHAT")
                    && !needChannels.contains("ALIPAY2") && !needChannels.contains("ALIPAY2")) {
                moreLayout.setVisibility(View.GONE);
                moreImageViewid.setVisibility(View.GONE);

                if (needChannels.contains("WEB")) {
                    webLayout.setVisibility(View.VISIBLE);
                    webImageViewid.setVisibility(View.VISIBLE);
                }
            }

            if (!needChannels.contains("WEB") && !needChannels.contains("CARD_GAME")
                    && !needChannels.contains("CARD_PHONE") && !needChannels.contains("WFTQQWALLET")) {
                unipayLineImageView.setVisibility(View.GONE);
            }
            if (!needChannels.contains("WEB") && !needChannels.contains("CARD_GAME")
                    && !needChannels.contains("CARD_PHONE") && !needChannels.contains("WFTQQWALLET")
                    && !needChannels.contains("UNIONPAY2")) {
                alipayLineImageView.setVisibility(View.GONE);
            }
            if (!needChannels.contains("WEB") && !needChannels.contains("CARD_GAME")
                    && !needChannels.contains("CARD_PHONE") && !needChannels.contains("WFTQQWALLET")
                    && !needChannels.contains("UNIONPAY2") && !needChannels.contains("ALIPAY2")) {
                weixinLineImageView.setVisibility(View.GONE);
            }

		/*	if (Constant.USE_DHT) {
				ylLayout.setVisibility(View.GONE);
				unipayLineImageView.setVisibility(View.GONE);
			}*/

            if (Constant.PAY_CHANNEl == 1 || Constant.PAY_CHANNEl == 2 || Constant.PAY_CHANNEl == 3 || Constant.PAY_CHANNEl == 4
                    || Constant.PAY_CHANNEl == 0 || Constant.PAY_CHANNEl == 5 || Constant.PAY_CHANNEl == 6 || Constant.PAY_CHANNEl == 7) {
//                ylLayout.setVisibility(View.GONE);
//                unipayLineImageView.setVisibility(View.GONE);
            }
        }


        // 用户id
        userID = (TextView) convertView
                .findViewById(context.getResources().getIdentifier("easou_userID", "id", context.getPackageName()));
        userID.setText(String.valueOf(Constant.ESDK_USERID));

        // 购买商品
        titleBuyInfo = (TextView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_tittleOfferName", "id", context.getPackageName()));
        titleBuyInfo.setText(map.get(Constant.PRODUCT_NAME));
        tittleInfo = (TextView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_tittleMount", "id", context.getPackageName()));
        tittleInfo.setText(map.get(Constant.AMOUNT));
        if (titleBuyInfo.length() > 4) {
            titleBuyInfo.setTextSize(14);
        }

        // 支付金额
        String money = map.get(ESConstant.MONEY);
        tittleAmt = (TextView) convertView.findViewById(
                context.getResources().getIdentifier("easou_id_tittleAmt", "id", context.getPackageName()));
        tittleAmt.setText("￥" + money);

        ESdkLog.d("支付的金额---->" + money);
      /*  String[] strs = money.split("\\.");
        int payMoney = 0;
        try {
            payMoney = Integer.parseInt(strs[0]);
            if (payMoney == 6) {
                ESdkLog.d("支付的金额符合要求,显示移动积分兑换");
                jfLayout.setVisibility(View.VISIBLE);
                jfImageViewId.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){

		}*/
        return convertView;
    }

    public static void showMorePayment() {

        isShowingMore = true;

        if (needChannels.contains("WEB")) {
            webLayout.setVisibility(View.VISIBLE);
            webImageViewid.setVisibility(View.VISIBLE);
        }

        tv_morePayment.setText("收起");
        moreLineImageView.setVisibility(View.VISIBLE);
    }

    public static void hideMorePayment() {

        isShowingMore = false;

        if (needChannels.contains("WEB")) {
            webLayout.setVisibility(View.GONE);
            webImageViewid.setVisibility(View.GONE);
        }

        tv_morePayment.setText("展开");
        moreLineImageView.setVisibility(View.GONE);
    }

    /**
     * 购买记录列表
     */
    public static HashMap<String, Object> createPayListView(final Context context, final Handler handler,
                                                            final LinkedList<PayItem> data_list) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(layoutParams);

        LayoutParams listlayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        ListView lv = new ListView(context);
        // 为ListView设置适配器
        lv.setLayoutParams(listlayoutParams);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setCacheColorHint(Color.TRANSPARENT);
        lv.setVerticalScrollBarEnabled(true);
        lv.setFadingEdgeLength(0);
        Bitmap btn_bg_Img;
        btn_bg_Img = BitmapFactory.decodeResource(context.getResources(),
                context.getResources().getIdentifier("es_list_line", "drawable", context.getPackageName()));
//		btn_bg_Img = CommonUtils.getBitmap(context, "es_list_line.png");
        lv.setDivider(new BitmapDrawable(context.getResources(), btn_bg_Img));
        final Pay_List_Adapter adapter = new Pay_List_Adapter(context, data_list);
        lv.setAdapter(adapter);
        // 注册监听事件
        lv.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            }
        });

        if (data_list.size() == 0) {
            Toast.makeText(context, "暂无数据！", Toast.LENGTH_SHORT).show();
        }

        if (data_list.size() >= 10) {
            lv.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // TODO Auto-generated method stub
                    if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                        // 滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            View v = (View) view.getChildAt(view.getChildCount() - 1);
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
                            int y = location[1];
                            if (view.getLastVisiblePosition() != getLastVisiblePosition && lastVisiblePositionY != y)// 第一次拖至底部
                            {
                                Toast.makeText(view.getContext(), "再次上拉加载更多数据...", Toast.LENGTH_SHORT).show();
                                getLastVisiblePosition = view.getLastVisiblePosition();
                                lastVisiblePositionY = y;
                                return;
                            } else if (view.getLastVisiblePosition() == getLastVisiblePosition
                                    && lastVisiblePositionY == y)// 第二次拖至底部
                            {
                                for (PayItem payItem : data_list) {
                                    if (payItem.isHasNext() == false) {
                                        Toast.makeText(view.getContext(), "数据已全部加载完毕", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                ESPayCenterActivity.page++;
                                Message msg = handler.obtainMessage();
                                msg.what = Constant.HANDLER_PAYLIST_SHOW_VIEW;
                                msg.sendToTarget();
                            }
                        }
                        // 未滚动到底部，第二次拖至底部都初始化
                        getLastVisiblePosition = 0;
                        lastVisiblePositionY = 0;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub

                }
            });
        }

        layout.addView(lv);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("View", layout);
        map.put("Pay_List_Adapter", adapter);
        return map;
    }

    // 自定义适配器
    public static class Pay_List_Adapter extends BaseAdapter {
        // 上下文对象
        private Context context;
        private LinkedList<PayItem> list_value;

        Pay_List_Adapter(Context context, LinkedList<PayItem> _list_value) {
            this.context = context;
            this.list_value = _list_value;
        }

        public void set_list_value(LinkedList<PayItem> _list_value) {
            list_value = _list_value;
        }

        public int getCount() {
            if (list_value != null && list_value.size() > 0)
                return list_value.size();
            else
                return 0;
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        // 创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder layout;
            if (convertView == null) {
                layout = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        context.getResources().getIdentifier("easou_paylist_item", "layout", context.getPackageName()), null);
                layout.tradeName = (TextView) convertView.findViewById(context.getResources()
                        .getIdentifier("espay_paylist_tradename", "id", context.getPackageName()));
                layout.tradeWay = (TextView) convertView.findViewById(
                        context.getResources().getIdentifier("espay_paylist_tradeway", "id", context.getPackageName()));
                layout.tradeTime = (TextView) convertView.findViewById(context.getResources()
                        .getIdentifier("espay_paylist_tradetime", "id", context.getPackageName()));
                layout.titleAmt = (TextView) convertView.findViewById(
                        context.getResources().getIdentifier("espay_paylist_amt", "id", context.getPackageName()));
                layout.titlePoint = (TextView) convertView.findViewById(
                        context.getResources().getIdentifier("espay_paylist_point", "id", context.getPackageName()));

                convertView.setTag(layout);
            } else {
                layout = (ViewHolder) convertView.getTag();
            }

//			if (list_value.get(position).getType() == 0) {

            layout.tradeName.setText(list_value.get(position).getTradeName());
            layout.tradeTime.setText(list_value.get(position).getCreateDatetime());

            String channelName = list_value.get(position).getChannelName();
            if (channelName.indexOf("微信") != -1 && channelName.indexOf("组件版") != -1) {
                channelName = "微信支付-组件版";
            } else if (channelName.indexOf("微信") != -1 && channelName.indexOf("网页版") != -1) {
                channelName = "微信支付-网页版";
            } else if (channelName.indexOf("易联") != -1 && channelName.indexOf("组件版") != -1) {
                channelName = "银联支付-组件版";
            } else if (channelName.indexOf("易联") != -1 && channelName.indexOf("网页版") != -1) {
                channelName = "银联支付-网页版";
            } else if (channelName.indexOf("QQ钱包") != -1 && channelName.indexOf("组件版") != -1) {
                channelName = "QQ钱包-组件版";
            } else if (channelName.indexOf("QQ钱包") != -1 && channelName.indexOf("网页版") != -1) {
                channelName = "QQ钱包-网页版";
            }
            layout.tradeWay.setText(channelName);

            String paidFeeMoney = list_value.get(position).getPaidFeeMoney();
            if (paidFeeMoney.indexOf("-") != -1) {
                paidFeeMoney = paidFeeMoney.replace("-", "");
            }
            layout.titleAmt.setText(paidFeeMoney);
//			}

            return convertView;
        }
    }

    static class ViewHolder {
        private TextView tradeName;
        private TextView tradeWay;
        private TextView tradeTime;
        private TextView titleAmt;
        private TextView titlePoint;
    }

    /**
     * 获取标题TextView
     */
    public static TextView getTitle() {
        return titleText;
    }

    /**
     * 获取返回按钮view
     */
    public static TextView getBackButton() {
        return backBtn;
    }

    /**
     * 判断屏幕方向
     */
    public static boolean isOrientation(Context context) {
        Configuration c = context.getResources().getConfiguration();
        int orientation = c.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            return true;
        return false;
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            final View v = ((Activity) context).getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
