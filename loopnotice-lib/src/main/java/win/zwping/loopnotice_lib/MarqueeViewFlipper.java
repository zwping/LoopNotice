package win.zwping.loopnotice_lib;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
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

public class MarqueeViewFlipper extends ViewFlipper implements MarqueeView.OnMarqueeTextViewListener {

    //<editor-fold desc="内部参数">

    private MarqueeView mText1;
    private MarqueeView mText2;

    private List<String> list;
    private int maxNumber;

    /**
     * 跑马灯完成，停留时间
     */
    private long marqueeCompletePassTime = 2000;
    /**
     * 文字显示时间，之后开始跑马灯- -
     */
    private long textShowTime = 1500;

    /**
     * marquee运行完成为true(viewFlipper准备滑动了)
     */
    private boolean slideFlipperState;

    /**
     * 当前播放位置
     */
    private int currentPlayPosition = -1;
    //</editor-fold>
    //<editor-fold desc="构造函数">

    public MarqueeViewFlipper(Context context) {
        super(context);
        initView(null);
    }

    public MarqueeViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }
    //</editor-fold>
    //<editor-fold desc="内部方法">

    private void initView(AttributeSet attrs) {
        mText1 = new MarqueeView(getContext());
        mText2 = new MarqueeView(getContext());
        mText1.setTextSize(18).setTextColor(Color.BLACK).setSpeed(2);
        mText2.setTextSize(18).setTextColor(Color.BLACK).setSpeed(2);
        addView(mText1);
        addView(mText2);

        mText1.setOnCompleteListener(this);
        mText2.setOnCompleteListener(this);

        this.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom));
        this.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_top));

        list = new ArrayList<>();
    }

    /**
     * 第一次添加数据，主动开发运行
     */
    private void firstStartRun() {
        if (!getRunState()) {
            slideFlipperState = true;
            postDelayed(completeRunnable, 0);
        }
    }

    /**
     * cut list from max number
     */
    private void cutListFromMaxNumber() {
        if (0 != maxNumber && list.size() > maxNumber) {
            List<String> lists = new ArrayList<>();
            for (int i = list.size() - maxNumber; i < maxNumber + 1; i++) {
                lists.add(list.get(i));
            }
            list = lists;
        }
    }
    //</editor-fold>
    //<editor-fold desc="API">

    /**
     * set max number
     *
     * @param maxNumber max number from constraint list
     * @return 链式
     */
    public MarqueeViewFlipper setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
        return this;
    }


    /**
     * 设置加载文字后，文字等待时间，等待时间完成后，判断是否需要“跑马灯”
     *
     * @param time
     * @return
     */
    public MarqueeViewFlipper setTextShowTime(long time) {
        this.textShowTime = time;
        return this;
    }

    /**
     * 设置未进行跑马灯/跑马灯完成后，停留时时间
     *
     * @param time
     * @return
     */
    public MarqueeViewFlipper setMarqueeCompletePassTime(long time) {
        this.marqueeCompletePassTime = time;
        return this;
    }

    /**
     * 设置默认文字
     *
     * @param msg
     * @return
     */
    public MarqueeViewFlipper setDefaultDisplayText(String msg) {
        mText1.setText(msg);
        return this;
    }

    /**
     * 增加跑马灯文字集合
     *
     * @param list
     * @return
     */
    public MarqueeViewFlipper addList(List<String> list) {
        this.list.addAll(list);
        cutListFromMaxNumber();
        firstStartRun();
        return this;
    }

    /**
     * 增加单个跑马灯文字
     *
     * @param string
     * @return
     */
    public MarqueeViewFlipper addText(String string) {
        this.list.add(getNextPlayPosition() == 0 ? list.size() : getNextPlayPosition(), string); //优先播放最新插入的string
        cutListFromMaxNumber();
        firstStartRun();
        return this;
    }

    /**
     * 获取下一个待播放的位置
     *
     * @return
     */
    public int getNextPlayPosition() {
        return currentPlayPosition < list.size() - 1 ? currentPlayPosition + 1 : 0;
    }

    /**
     * 设置字体大小 {会导致当前界面不上下对齐，请设置在所有的设置之前}
     *
     * @param sp
     */
    public MarqueeViewFlipper setTextSize(int sp) {
        mText1.setTextSize(mText1.spToPx(sp));
        mText2.setTextSize(mText1.spToPx(sp));
        invalidate();
        return this;
    }

    /**
     * 设置文字颜色
     *
     * @param color
     * @return
     */
    public MarqueeViewFlipper setTextColor(int color) {
        mText1.setTextColor(color);
        mText2.setTextColor(color);
        return this;
    }

    /**
     * 设置跑马灯速度
     *
     * @param speed
     * @return
     */
    public MarqueeViewFlipper setSpeed(int speed) {
        mText1.setSpeed(speed);
        mText2.setSpeed(speed);
        return this;
    }

    /**
     * 获取当前执行状态
     *
     * @return
     */
    public boolean getRunState() {
        return mText1.getRunState() || mText2.getRunState() || slideFlipperState;
    }

    @Override
    public void marqueeComplete() {
        slideFlipperState = true;
        postDelayed(completeRunnable, marqueeCompletePassTime);
    }

    //</editor-fold>
    //<editor-fold desc="runnable">

    Runnable completeRunnable = new Runnable() {
        @Override
        public void run() {
            if (0 == getDisplayedChild())
                mText2.setText(list.get(currentPlayPosition = getNextPlayPosition()));
            else
                mText1.setText(list.get(currentPlayPosition = getNextPlayPosition()));
            showNext();
            postDelayed(flipperNextCompleteRunnable, textShowTime + 500); //1.5s后运行
        }
    };
    Runnable flipperNextCompleteRunnable = new Runnable() {
        @Override
        public void run() {
            slideFlipperState = false;
            if (0 == getDisplayedChild()) mText1.start();
            else mText2.start();
        }
    };
    //</editor-fold>
}
