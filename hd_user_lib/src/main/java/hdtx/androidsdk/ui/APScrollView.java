package hdtx.androidsdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class APScrollView extends ScrollView {
    public APScrollView(Context paramContext) {
        super(paramContext);
    }

    public APScrollView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public APScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    public void fling(int paramInt) {
        super.fling(paramInt / 40);
    }
}
