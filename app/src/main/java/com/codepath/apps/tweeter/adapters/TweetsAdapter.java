package com.codepath.apps.tweeter.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter  extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private List<Tweet> tweets;
    private Context context;

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View articleView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profilePicture;
        public TextView screenName;
        public TextView tweetText;

        public ViewHolder(View itemView) {
            super(itemView);

            profilePicture = (ImageView) itemView.findViewById(R.id.profilePicture);
            screenName = (TextView) itemView.findViewById(R.id.screenName);
            tweetText = (TextView) itemView.findViewById(R.id.tweetText);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Tweet tweet = tweets.get(position);

        // Set item views based on your views and data model
        Glide.with(getContext())
                .load(tweet.user.profileImageUrl)
                .placeholder(new ColorDrawable(getContext().getResources().getColor(R.color.imageLoadingBackground)))
                .centerCrop()
                .crossFade()
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 0))
                .into(viewHolder.profilePicture);
        viewHolder.screenName.setText(tweet.user.getFullScreenName());
        viewHolder.tweetText.setText(tweet.text);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public TweetsAdapter(Context ctx, List<Tweet> tweets) {
        this.tweets = tweets;
        context = ctx;
    }

    private Context getContext() {
        return context;
    }
}
