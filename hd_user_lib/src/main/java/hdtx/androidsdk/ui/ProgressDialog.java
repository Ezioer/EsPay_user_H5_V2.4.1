package hdtx.androidsdk.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        // TODO Auto-generated constructor stub
    }

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(mContext.getResources().getIdentifier("hd_plugin_wait_loadding", "layout", mContext.getPackageName()));
        Animation animBlink = AnimationUtils.loadAnimation(mContext,
                mContext.getResources().getIdentifier("hd_anim_blink", "anim", mContext.getPackageName()));
        ProgressBar hd_progress = (ProgressBar) findViewById(mContext.getResources().getIdentifier("hd_progress", "id", mContext.getPackageName()));
        hd_progress.startAnimation(animBlink);
    }

}