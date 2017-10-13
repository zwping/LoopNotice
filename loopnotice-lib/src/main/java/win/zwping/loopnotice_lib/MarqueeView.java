package win.zwping.loopnotice_lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * <p>describe：绘制View实现textView跑马灯效果
 * <p>    note：
 * #attrs
 * 文字
 * 文字大小
 * 文字颜色
 * 滑动速度
 * 滑动方向
 * 起始位置
 * #public
 * 设置text{@link #setText(CharSequence)}
 * 开始跑马灯{@link #start()}
 * 暂定跑马灯{@link #pause()}
 * 运动完成监听{@link #setOnCompleteListener(OnMarqueeTextViewListener)}
 * 获取其运行状态{@link #getResources()}
 * <p>  author：zwp on 2017/7/3 0003 mail：1101558280@qq.com web: http://www.zwping.win </p>
 */

public class MarqueeView extends View implements Runnable {

    private static final int CHANGE_VIEW_WITCH = 5;

    /*attr*/
    private CharSequence text;
    private int textSize;
    private int textColor;
    private int speed; //速度，默认等于2
    private int direction; /*滑动方向*/
    private int originPosition; //起始位置

    private Paint paint; //准备一支笔
    private Rect rect;  //准备测量文字的长宽

    private int layoutWidth; //文本布局宽度
    private int textWidth; //文字宽度

    private float textYPosition; //y position，居中显示

    private OnMarqueeTextViewListener listener;

    private int xPosition; //当前text的位置
    private boolean textFlag; //当前文字标识，每串文字均只有一次为true，运行完成会改为false
    private boolean runningState; //运行状态

    public MarqueeView(Context context) {
        super(context);
        initView(null);
    }

    public MarqueeView(Context context, OnMarqueeTextViewListener listener) {
        super(context);
        this.listener = listener;
        initView(null);
    }

    public MarqueeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public MarqueeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarqueeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attr) {
        if (null != attr) {
            TypedArray array = getContext().obtainStyledAttributes(attr, R.styleable.MarqueeView);
            try {
                text = array.getText(R.styleable.MarqueeView_text);
                textSize = array.getInteger(R.styleable.MarqueeView_textSize, spToPx(18)); //默认值为18sp
                textColor = array.getInteger(R.styleable.MarqueeView_textColor, Color.BLACK); //默认黑色
                speed = array.getInteger(R.styleable.MarqueeView_speed, 2); //默认速度为2
                direction = array.getInteger(R.styleable.MarqueeView_direction, 0); //默认滑动方向为：左滑
                originPosition = array.getInteger(R.styleable.MarqueeView_originPosition, 0); //默认起始位置为：控件内
            } finally {
                array.recycle();
            }
        }

        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTextAlign(0 == direction ? Paint.Align.LEFT : Paint.Align.RIGHT);
        paint.setColor(textColor);
        paint.setAntiAlias(true);
        paint.setDither(true);
        rect = new Rect();

        setText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == text) return;
        if (layoutWidth == 0) {
            this.layoutWidth = getMeasuredWidth();
            xPosition = 0 == originPosition ? 0 : layoutWidth;
        }
        if (rect.height() != 0 && textYPosition == 0) {
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            textYPosition = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        }
        if (getRunState()) xPosition = xPosition - CHANGE_VIEW_WITCH;
        if (textWidth <= layoutWidth && xPosition < 0) xPosition = 0;
        canvas.drawText(text, 0, text.length(), xPosition, textYPosition, paint);
        if (textWidth <= layoutWidth && xPosition == 0) {
            complete();
        } else if (textWidth <= layoutWidth) {
            postDelayed(this, speed);
        } else {
            if (xPosition > -(textWidth - layoutWidth))
                postDelayed(this, speed);
            else complete();
        }
    }

    private void complete() {
        if (textFlag && runningState) {
            textFlag = false;
            if (null != listener) listener.marqueeComplete();
        }
        pause(); //onDraw执行两次，设置执行监听标识
    }

    @Override
    public void run() {
        if (runningState) invalidate();
    }

    public boolean getRunState() {
        return textFlag && runningState;
    }

    public void pause() {
        runningState = false;
    }

    public void start() {
        runningState = true;
        invalidate();
    }

    public void setText(CharSequence text) {
        if (null != text) {
            this.text = text;
            paint.getTextBounds(this.text.toString(), 0, this.text.length(), rect);
            textWidth = rect.width();
            layoutWidth = 0;
            textFlag = true;
            pause();
            invalidate();
        }
    }

    public void setOnCompleteListener(OnMarqueeTextViewListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getRunState()) start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pause();
    }

    private int spToPx(float sp) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    public interface OnMarqueeTextViewListener {
        void marqueeComplete();
    }
}