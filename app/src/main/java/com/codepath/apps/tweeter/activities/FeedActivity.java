package com.codepath.apps.tweeter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.TweeterApplication;
import com.codepath.apps.tweeter.adapters.TweetsAdapter;
import com.codepath.apps.tweeter.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.tweeter.models.Tweet;
import com.codepath.apps.tweeter.utils.EndlessRecyclerViewScrollListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class FeedActivity extends AppCompatActivity {
    Toolbar toolbar;
    ArrayList<Tweet> tweets = new ArrayList<>();
    TweetsAdapter tweetsAdapter;
    RecyclerView recyclerView;
    ImageView loadingIndicator;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setupViews();
        populateTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void composeTweet(MenuItem mi) {
        final FragmentManager fm = getSupportFragmentManager();
        final ComposeTweetDialogFragment frag = ComposeTweetDialogFragment.newInstance();
        frag.setListener(new ComposeTweetDialogFragment.ComposeTweetDialogListener() {
            @Override
            public void onPostTweet(Tweet tweet) {
                frag.dismiss();
                tweets.add(0, tweet);
                tweetsAdapter.notifyItemInserted(0);
            }
        });
        frag.show(fm, "fragment_compose_tweet");
    }

    private void setupViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tweetsAdapter = new TweetsAdapter(this, tweets);
        loadingIndicator = (ImageView) findViewById(R.id.loadingIndicator);

        setupRecyclerView();

    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.rvTweets);
        recyclerView.setAdapter(tweetsAdapter);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
//        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
//                new ItemClickSupport.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                        launchArticleWebView(position);
//                    }
//                }
//        );
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getNextTweets();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

    }

    private void getNextTweets() {
        showLoadingIndicator();
        long lastId = tweets.isEmpty() ? -1 : tweets.get(tweets.size() - 1).id;
        TweeterApplication.getRestClient().getHomeTimeline(lastId, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                throwable.printStackTrace();
                // TODO: toast or snack bar
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweetsArr = gson.fromJson(responseString, Tweet[].class);
                int curSize = tweetsAdapter.getItemCount();
                tweets.addAll(Arrays.asList(tweetsArr));
                tweetsAdapter.notifyItemRangeInserted(curSize, tweetsArr.length);
                hideLoadingIndicator();
            }
        });
    }

    private void populateTimeline() {
        if (!tweets.isEmpty()) {
            clearTimeline();
        }
        getNextTweets();
    }

    private void clearTimeline() {
        tweets.clear();
        tweetsAdapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    private void showLoadingIndicator() {
        loadingIndicator.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.raw.loading_dots)
                .into(new GlideDrawableImageViewTarget(loadingIndicator));
    }
    private void hideLoadingIndicator() { loadingIndicator.setVisibility(View.GONE); }
}
