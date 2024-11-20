package com.example.imageslider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private ArrayList<String> imageSources = new ArrayList<>(); // List to hold image URLs
    private int currentPage = 0;
    private Handler handler = new Handler();
    private boolean isAutoScrollEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        // Fetch image links from the server
        fetchImageLinks();
    }

    // Fetch image URLs from the server in a separate thread
    private void fetchImageLinks() {
        if (isNetworkAvailable()) {
            new Thread(() -> {
                try {
                    // Server URL to fetch image links
                    URL url = new URL("https://admissonjourney.xyz/PracticeApp/ImageLinks.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    // Read server response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse the JSON response
                    JSONArray jsonArray = new JSONArray(response.toString());
                    imageSources.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String imageUrl = jsonObject.optString("imageUrl");
                        if (!imageUrl.isEmpty()) {
                            imageSources.add(imageUrl);
                        }
                    }

                    // Update UI on the main thread after fetching data
                    runOnUiThread(() -> {
                        if (!imageSources.isEmpty()) {
                            setupViewPagerAndDots();
                        } else {
                            Toast.makeText(MainActivity.this, "No images found.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Failed to load images.", Toast.LENGTH_SHORT).show();
                        showNoInternetImage(); // Show the placeholder image
                    });
                }
            }).start();
        } else {
            // No internet connection, show a placeholder image
            showNoInternetImage();
        }
    }

    // Show a placeholder image when there is no internet connection
    private void showNoInternetImage() {
        imageSources.clear();
        imageSources.add("drawable/wait"); // Add the placeholder image resource
        setupViewPagerAndDots();
    }

    // Set up the ViewPager and dots based on the fetched image URLs
    private void setupViewPagerAndDots() {
        // Set up the adapter for the ViewPager
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageSources);
        viewPager.setAdapter(adapter);

        // Add dots to indicate page position
        addDots(imageSources.size());
        updateDots(currentPage);

        // ViewPager page change listener
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

        // Start auto-scroll if enabled
        if (isAutoScrollEnabled) {
            startAutoScroll();
        }
    }

    // Add dots to indicate the number of pages
    private void addDots(int count) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.dot_inactive); // Default inactive dot image
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dotsLayout.addView(dot);
        }
    }

    // Update dots when page changes
    private void updateDots(int position) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            dot.setImageResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    // Start the auto-scroll feature
    private void startAutoScroll() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!imageSources.isEmpty()) {
                    currentPage = (currentPage + 1) % imageSources.size();
                    viewPager.setCurrentItem(currentPage, true);
                    handler.postDelayed(this, 3000); // Auto-scroll every 3 seconds
                }
            }
        }, 3000);
    }

    // Cleanup when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Remove any pending auto-scroll tasks
    }

    // Check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
