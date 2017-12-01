package win.zwping.loopnotice;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import win.zwping.loopnotice_lib.MarqueeView;
import win.zwping.loopnotice_lib.MarqueeViewFlipper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mRootLayout;
    private MarqueeView mMarquee;
    /**
     * 开始
     */
    private Button mMarqueeStart;
    /**
     * 暂停
     */
    private Button mMarqueePass;
    /**
     * 设置文字内容
     */
    private Button mSetMarquee;
    /**
     * 获取当前滑动状态
     */
    private Button mMarqueeState;
    private MarqueeViewFlipper mNotice;
    /**
     * 增加list
     */
    private Button mAddList;
    /**
     * +1
     */
    private Button mAddOne;

    private int position = 0;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);
        mNotice = (MarqueeViewFlipper) findViewById(R.id.notice);
        mAddList = (Button) findViewById(R.id.add_list);
        mAddList.setOnClickListener(this);
        mAddOne = (Button) findViewById(R.id.add_one);
        mAddOne.setOnClickListener(this);
        mMarquee = (MarqueeView) findViewById(R.id.marquee);
        mMarqueeStart = (Button) findViewById(R.id.marquee_start);
        mMarqueeStart.setOnClickListener(this);
        mMarqueePass = (Button) findViewById(R.id.marquee_pass);
        mMarqueePass.setOnClickListener(this);
        mSetMarquee = (Button) findViewById(R.id.set_marquee);
        mSetMarquee.setOnClickListener(this);
        mMarqueeState = (Button) findViewById(R.id.marquee_state);
        mMarqueeState.setOnClickListener(this);

        mMarquee.setOnCompleteListener(new MarqueeView.OnMarqueeTextViewListener() {
            @Override
            public void marqueeComplete() {
                showToast("完成了");
            }
        });
        mNotice.setDefaultDisplayText("暂无公告！").setMaxNumber(4);
//        mNotice.addText(returnRandomString());
        //mNotice.setTextShowTime(1500);
        //mNotice.setMarqueeCompletePassTime(2000);

    }

    private void showToast(Object o) {
        if (toast == null) toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        toast.setText("" + o);
        toast.show();
    }

    private Toast toast;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_marquee:
                mMarquee.setText(returnRandomString());
                break;
            case R.id.marquee_start:
                mMarquee.start();
                break;
            case R.id.marquee_pass:
                mMarquee.pause();
                break;
            case R.id.marquee_state:
                showToast(mMarquee.getRunState());
                break;
            case R.id.add_list: //每次增加10条
                list.clear();
                for (int i = 0; i < 10; i++) {
                    list.add(returnRandomString());
                }
                mNotice.addList(list);
                break;
            case R.id.add_one:
                mNotice.addText(returnRandomString());
                break;
            default:
                break;
        }
    }

    private String returnRandomString() {
        return "谷歌Pixel 2和iPhone 8到底谁更厉害?" + ++position;
//        return "谷歌10月4日发布了Pixel 2和Pixel 2 XL两款手机，它们一推出就在拍照上超越了iPhone 8，位列DXO Mark榜单第一。" + ++position;
//        return new Random().nextBoolean() ?
//                "谷歌Pixel 2和iPhone 8到底谁更厉害？" + ++position :
//                "谷歌10月4日发布了Pixel 2和Pixel 2 XL两款手机，它们一推出就在拍照上超越了iPhone 8，位列DXO Mark榜单第一。" + ++position;
    }
}

