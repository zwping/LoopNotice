package win.zwping.loopnotice_lib;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>describe：
 * <p>    note：
 * <p>  author：zwp on 2017/7/5 0005 mail：1101558280@qq.com web: http://www.zwping.win </p>
 */

public class MarqueeViewFlipper extends RelativeLayout implements MarqueeView.OnMarqueeTextViewListener {

    private MarqueeView mText1;
    private MarqueeView mText2;
    private ViewFlipper mFlipper;

    private List<String> list;

    private long marqueeCompletePassTime = 2000;//跑马灯完成，停留时间
    private long textShowTime = 1500; //文字显示时间，之后开始跑马灯- -

    public MarqueeViewFlipper(Context context) {
        super(context);
        initView(null);
    }

    public MarqueeViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public MarqueeViewFlipper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarqueeViewFlipper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        inflate(getContext(), R.layout.child_marquee_layout, this);
        mText1 = (MarqueeView) findViewById(R.id.text1);
        mText2 = (MarqueeView) findViewById(R.id.text2);
        mFlipper = (ViewFlipper) findViewById(R.id.flipper);

        mText1.setOnCompleteListener(this);
        mText2.setOnCompleteListener(this);

        mFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom));
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_top));

        list = new ArrayList<>();
    }

    public void setTextShowTime(long time) {
        this.textShowTime = time;
    }

    public void setMarqueeCompletePassTime(long time) {
        this.marqueeCompletePassTime = time;
    }

    public void setDefaultDisplayText(String msg) {
        mText1.setText(msg);
    }

    public void addList(List<String> list) {
        this.list.addAll(list);
        firstStartRun();
    }

    public void addText(String string) {
        this.list.add(autoRunPosition, string); //优先播放最新插入的string
        firstStartRun();
    }

    public boolean getRunState() {
        return mText1.getRunState() || mText2.getRunState() || slideFlipperState;
    }

    private boolean slideFlipperState; //marquee运行完成为true(viewFlipper准备滑动了)

    private int autoRunPosition; //自动播放的位置

    private void firstStartRun() {
        if (!getRunState()) {
            slideFlipperState = true;
            postDelayed(completeRunnable, 0);
        }
    }

    @Override
    public void marqueeComplete() {
        slideFlipperState = true;
        autoRunPosition = ++autoRunPosition >= list.size() ? 0 : autoRunPosition;
        postDelayed(completeRunnable, marqueeCompletePassTime);
    }

    Runnable completeRunnable = new Runnable() {
        @Override
        public void run() {
            if (0 == mFlipper.getDisplayedChild())
                mText2.setText(list.get(autoRunPosition));
            else
                mText1.setText(list.get(autoRunPosition));
            mFlipper.showNext();
            postDelayed(flipperNextCompleteRunnable, textShowTime + 500); //1.5s后运行
        }
    };
    Runnable flipperNextCompleteRunnable = new Runnable() {
        @Override
        public void run() {
            slideFlipperState = false;
            if (0 == mFlipper.getDisplayedChild()) mText1.start();
            else mText2.start();
        }
    };
}
