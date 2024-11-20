// MainActivity.java
package com.example.imageslider;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private ArrayList<String> imageUrls;
    private int currentPage = 0;
    private Handler handler = new Handler();
    private boolean isAutoScrollEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        // সার্ভার থেকে ইমেজ লিঙ্ক লোড করুন
        imageUrls = new ArrayList<>();
        imageUrls.add("https://th.bing.com/th/id/OIP._mYAOR0nOMSRUQ5vFeQUeQHaFU?w=232&h=180&c=7&r=0&o=5&pid=1.7");
        imageUrls.add("https://th.bing.com/th/id/OIP.kJt2704uawJxFxYvZU-nowHaFj?rs=1&pid=ImgDetMain");
        imageUrls.add("https://th.bing.com/th/id/OIP.vDc7AVL8PaIjSSeTixkElAHaE8?w=265&h=180&c=7&r=0&o=5&pid=1.7");

        ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);

        addDots(imageUrls.size());
        updateDots(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        if (isAutoScrollEnabled) {
            startAutoScroll();
        }
    }

    private void addDots(int count) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.dot_inactive); // ডিফল্ট ডট ইমেজ
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dotsLayout.addView(dot);
        }
    }

    private void updateDots(int position) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            dot.setImageResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void startAutoScroll() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage = (currentPage + 1) % imageUrls.size();
                viewPager.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 3000); // প্রতি ৩ সেকেন্ডে স্ক্রল
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
