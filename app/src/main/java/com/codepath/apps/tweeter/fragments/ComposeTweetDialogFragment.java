package com.codepath.apps.tweeter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.TweeterApplication;
import com.codepath.apps.tweeter.models.Tweet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetDialogFragment extends DialogFragment {
    private ComposeTweetDialogListener listener;
    private TextView tvCharsRemaining;
    private Button btnTweet;
    private EditText etComposeTweet;
    private CharSequence tweetText = "";

    public interface ComposeTweetDialogListener {
        void onPostTweet(Tweet tweet);
    }

    public void setListener(ComposeTweetDialogListener listener) {
        this.listener = listener;
    }

    public static ComposeTweetDialogFragment newInstance() {
        ComposeTweetDialogFragment fragment = new ComposeTweetDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();

        tvCharsRemaining = (TextView) view.findViewById(R.id.charsRemaining);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        etComposeTweet = (EditText) view.findViewById(R.id.composeTweet);

        getDialog().setTitle("Edit Filters");
        setupCharsRemaining();
        setupTweetButton();
        focusComposeTweet();
    }

    private void setupCharsRemaining() {
        etComposeTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tweetText = charSequence;
                int numCharsLeft = 140 - tweetText.length();
                tvCharsRemaining.setText(String.valueOf(numCharsLeft));
                if (numCharsLeft < 0) {
                    tvCharsRemaining.setTextColor(0xDDFF0000);
                    btnTweet.setAlpha(0.2F);
                } else {
                    tvCharsRemaining.setTextColor(0x88777777);
                    btnTweet.setAlpha(1F);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setupTweetButton() {
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweetText.length() > 140 || tweetText.length() < 1) {
                    return;
                }
                TweeterApplication.getRestClient()
                        .postTweet(tweetText.toString(), new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  String responseString, Throwable throwable) {
                                throwable.printStackTrace();
                                // TODO: toast or snack bar
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  String responseString) {
                                Gson gson = new GsonBuilder().create();
                                Tweet tweet = gson.fromJson(responseString, Tweet.class);
                                listener.onPostTweet(tweet);
                            }
                        });
            }
        });
    }

    private void focusComposeTweet() {
        etComposeTweet.requestFocus();
    }


}
