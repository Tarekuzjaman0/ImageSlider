package com.example.imageslider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    private final Context context;
    private final List<String> imageSources; // List to hold image URLs (use String for URLs)

    public ImageSliderAdapter(Context context, List<String> imageSources) {
        this.context = context;
        this.imageSources = imageSources;
    }

    @Override
    public int getCount() {
        return imageSources.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // Inflate the layout for the slider item
        View view = LayoutInflater.from(context).inflate(R.layout.slider_item, container, false);
        ImageView imageView = view.findViewById(R.id.sliderImage);

        // Check internet connectivity
        if (NetworkUtils.isInternetAvailable(context)) {
            // Load image from URL if internet is available
            String imageUrl = imageSources.get(position);
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.wait) // Placeholder image while loading
                    .error(R.drawable.load) // Fallback image if loading fails
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Optional: add logging or UI changes on success
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error, e.g., log or show a message
                            e.printStackTrace();
                        }
                    });
        } else {
            // Show fallback image if there's no internet
            imageView.setImageResource(R.drawable.aaaas); // Replace with your fallback image
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object); // Remove the view from the container when no longer needed
    }
}
