package com.tweetco.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.onefortybytes.R;
import com.tweetco.activities.fragments.TrendingFragment.TrendingTag;
import com.tweetco.activities.helper.Helper;
import com.tweetco.activities.progress.AsyncTaskEventHandler;
import com.tweetco.activities.progress.AsyncTaskEventSinks.AsyncTaskCancelCallback;
import com.tweetco.activities.progress.AsyncTaskEventSinks.UIEventSink;
import com.tweetco.asynctasks.PostTweetTask;
import com.tweetco.asynctasks.PostTweetTask.PostTweetTaskCompletionCallback;
import com.tweetco.asynctasks.PostTweetTaskParams;
import com.tweetco.clients.TweetsClient;
import com.tweetco.clients.TwitterAppClient;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.datastore.TrendingListSingleton;
import com.tweetco.datastore.UsersListSigleton;
import com.tweetco.models.tweets.HomeFeedTweetsModel;
import com.tweetco.tweets.TweetCommonData;
import com.tweetco.twitter.TwitterApp;
import com.tweetco.twitter.TwitterApp.TwDialogListener;
import com.tweetco.utility.AlertDialogUtility;
import com.tweetco.utility.ImageUtility;
import com.tweetco.utility.UiUtility;



public class PostTweetActivity extends TweetCoBaseActivity 
{
	private final static String TAG = "PostTweetActivity";
	private static final int TWEET_MAX_CHARS = 140;

	private static final int REQUEST_CODE_IMAGE_SELECT = 100;
	private static final int REQUEST_CODE_IMAGE_CAPTURE = 101;
	private static final Pattern HASH_TAG_PATTERN = Pattern.compile("(^|\\W)(#[a-z\\d][\\w-]*)", Pattern.CASE_INSENSITIVE);

	private MultiAutoCompleteTextView mTweetContent;
	private TextView mCharCount;
	private EditText mContentTags;		//This is for posting as TweetBot, not used in production builds
	private Button mSendButton;
	private Button mImageGalleryButton;
	private Button mImageCameraButton;
	private ImageView mTweetImage;
	private Uri mTweetImageUri;
	private CheckBox mAnonymousCheckBox = null;
	private CheckBox mPostToTwitterCheckBox = null;

	private int replySourceTweetIterator = -1;
	private String replySourceTweetUsername = null;

	private int mCharCountInt = TWEET_MAX_CHARS;

	private HomeFeedTweetsModel model;
	private TwitterAppClient mTwitterAppClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posttweet);
		
	    
		ActionBar actionbar = getSupportActionBar();
		if(actionbar!=null)
		{
			actionbar.setHomeButtonEnabled(true);
			actionbar.setDisplayHomeAsUpEnabled(true);
		}

		model = new HomeFeedTweetsModel();
		mTwitterAppClient = new TwitterAppClient(mTwLoginDialogListener);

		Intent intent = getIntent();
		replySourceTweetIterator = intent.getIntExtra(Constants.INTENT_EXTRA_REPLY_SOURCE_TWEET_ITERATOR, -1);
		replySourceTweetUsername = intent.getStringExtra(Constants.INTENT_EXTRA_REPLY_SOURCE_TWEET_USERNAME);

		mTweetContent = UiUtility.getView(this, R.id.tweetContent);
		mContentTags = UiUtility.getView(this, R.id.contentTags);
		mCharCount = UiUtility.getView(this, R.id.charCount);
		mSendButton = UiUtility.getView(this, R.id.sendTweetButton);
		mImageGalleryButton = UiUtility.getView(this, R.id.imageGalleryButton);
		mImageCameraButton = UiUtility.getView(this, R.id.imageCameraButton);
		mTweetImage = UiUtility.getView(this, R.id.tweetImaage);
		mAnonymousCheckBox = UiUtility.getView(this, R.id.anonymousCheckBox);
		mPostToTwitterCheckBox = UiUtility.getView(this, R.id.postToTwitterCheckBox);
		mTweetContent.setAdapter(new ArrayAdapter<String>(PostTweetActivity.this,
				android.R.layout.simple_dropdown_item_1line, getUsernamesAndHashtags(UsersListSigleton.INSTANCE.getUsersList().iterator(), TrendingListSingleton.INSTANCE.getTrendingList().iterator())));
		mTweetContent.setThreshold(1);

		if(savedInstanceState != null) {
			mTweetImageUri = (Uri)savedInstanceState.getParcelable("imageuri");
			mTweetImage.setImageURI(mTweetImageUri);
		}
		
		mPostToTwitterCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onTwitterClick();
			}
		});

		if (mTwitterAppClient.mApp.hasAccessToken()) {
			mPostToTwitterCheckBox.setChecked(true);
			
			String username = mTwitterAppClient.mApp.getUsername();
			username		= (username.equals("")) ? "Unknown" : username;
			
			mPostToTwitterCheckBox.setText("Post to Twitter (" + username + ")");
		}
		
		//From http://stackoverflow.com/questions/12691679/android-autocomplete-textview-similar-to-the-facebook-app
		//Create a new Tokenizer which will get text after '@' and terminate on ' '
		mTweetContent.setTokenizer(new Tokenizer() {

			@Override
			public CharSequence terminateToken(CharSequence text) {
				int i = text.length();

				while (i > 0 && text.charAt(i - 1) == ' ') {
					i--;
				}

				if (i > 0 && text.charAt(i - 1) == ' ') {
					return text;
				} else {
					SpannableString sp = new SpannableString(text + " ");
					sp.setSpan(new ForegroundColorSpan(Color.BLUE), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					return sp;

				}
			}

			@Override
			public int findTokenStart(CharSequence text, int cursor) {
				int i = cursor;

				while (i > 0 && text.charAt(i - 1) != '@' && text.charAt(i - 1) != '#') {
					i--;
				}

				//Check if token really started with @, else we don't have a valid token
				if (i < 1 || (text.charAt(i - 1) != '@' && text.charAt(i - 1) != '#') ) {
					return cursor;
				}

				return i - 1;
			}

			@Override
			public int findTokenEnd(CharSequence text, int cursor) {
				int i = cursor;
				int len = text.length();

				while (i < len) {
					if (text.charAt(i) == ' ') {
						return i;
					} else {
						i++;
					}
				}

				return len;
			}
		});

		mCharCount.setText(String.valueOf(mCharCountInt));

		mTweetContent.addTextChangedListener(new TextWatcher() 
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				int decrementValue = (before > 0)? -before: count;
				mCharCountInt = mCharCountInt - decrementValue;
				mCharCount.setText(String.valueOf(mCharCountInt));
				if(mCharCountInt < 0)
				{
					mSendButton.setEnabled(false);
					mCharCount.setTextColor(Color.RED);
				}
				else
				{
					mSendButton.setEnabled(true);
					mCharCount.setTextColor(Color.BLACK);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{

			}

			@Override
			public void afterTextChanged(Editable tweetContent) 
			{
				mCharCountInt = TWEET_MAX_CHARS - tweetContent.length();
				mCharCount.setText(String.valueOf(mCharCountInt));
				Linkify.addLinks(tweetContent, Linkify.WEB_URLS);
				URLSpan[] spansList = tweetContent.getSpans(0, tweetContent.length()-1, URLSpan.class);
				for(URLSpan span:spansList)
				{
					Log.i(TAG,"Shortening URL");
					int start = tweetContent.getSpanStart(span);
					int end = tweetContent.getSpanEnd(span);
					if((end - start) > 21)
					{
						(new URLShortenerTask(tweetContent, span)).execute();
					}
				}
			}
		});

		mSendButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				String tweetContent = mTweetContent.getEditableText().toString();
				if(!TextUtils.isEmpty(tweetContent))
				{
					final boolean bAnonymous = mAnonymousCheckBox.isChecked();
					boolean bPostTweet = true;
					if(bAnonymous)
					{
						Matcher matcher = HASH_TAG_PATTERN.matcher(tweetContent);
						if(!matcher.find())
						{
							bPostTweet = false;
							AlertDialogUtility.getAlertDialogOK(PostTweetActivity.this, "Your anonymous post should have #hashtag for it to be searchable by others. Please add a suitable #hashtag to your post.", null).show();
						}
					}
					
					if(bPostTweet)
					{
						new AsyncTask<Void, Void, Boolean>() {

							@Override
							protected Boolean doInBackground(Void... voids) {
								Boolean bSuccess = false;
								try {
									String content = mTweetContent.getEditableText().toString();
									model.postTweet(content, (BitmapDrawable) mTweetImage.getDrawable(),
											replySourceTweetIterator, replySourceTweetUsername, bAnonymous);

									if(mPostToTwitterCheckBox.isChecked()) {
										mTwitterAppClient.postTweet(content);
									}
									bSuccess = true;
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}
								return  bSuccess;
							}

							@Override
							protected void onPostExecute(Boolean bSuccess) {
								if(bSuccess) {
									PostTweetActivity.this.finish();
								}
								else {
									AlertDialogUtility.getAlertDialogOK(PostTweetActivity.this, "Posting tweet failed", null).show();
								}
							}
						}.execute();
					}
				}
				else
				{
					AlertDialogUtility.getAlertDialogOK(PostTweetActivity.this, "Post cannot be empty", null).show();
				}
				

			}
		});

		mImageGalleryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				Intent intent = ImageUtility.getImageChooserIntent(getApplicationContext());
				startActivityForResult(intent, REQUEST_CODE_IMAGE_SELECT);
			}
		});

		mImageCameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				Intent intent = ImageUtility.getImageCaptureIntent(getApplicationContext());
				if(intent!=null)
				{
					startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
				}
				else
				{
					Toast.makeText(PostTweetActivity.this.getApplicationContext(), "Your device doesn't support this", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		// Get intent, action and MIME type
	    String action = intent.getAction();
	    String type = intent.getType();
	    String inputText = null;
	    if (Intent.ACTION_SEND.equals(action) && type != null) 
	    {
	        if ("text/plain".equals(type)) 
	        {
	        	inputText = intent.getStringExtra(Intent.EXTRA_TEXT);
	        }
	        else if (type.startsWith("image/")) 
	        {
	            handleSendImage(intent); // Handle single image being sent
	        }
	    }
	    else
	    {
	    	inputText = intent.getStringExtra(Constants.EXISTING_STRING);
	    }
	    
	    handleInputText(inputText);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {


		if(mTweetImageUri != null) {
			outState.putParcelable("imageuri", mTweetImageUri);
		}

		super.onSaveInstanceState(outState);
	}

	void handleSendImage(Intent intent) 
	{
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    if (imageUri != null) 
	    {
	        Uri correctedImageUri = ImageUtility.getCorrectedImageUri(getApplicationContext(), imageUri, false);
	        if(correctedImageUri != null)
			{
				mTweetImage.setImageURI(correctedImageUri);
			}
	    }
	}
	
	private void onTwitterClick() {
		if (!mTwitterAppClient.mApp.hasAccessToken() && mPostToTwitterCheckBox.isChecked())
		{
			mTwitterAppClient.mApp.authorize();
		}
	}
	
	public void handleInputText(String inputText)
	{
		if(!TextUtils.isEmpty(inputText))
		{
			mTweetContent.setText(inputText);
			mTweetContent.setSelection(inputText.length());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode == REQUEST_CODE_IMAGE_SELECT || requestCode == REQUEST_CODE_IMAGE_CAPTURE)
			{
				Uri fileUri = null;
				try
				{
					fileUri = ImageUtility.onImageAttachmentReceived(getApplicationContext(), data);

					if(fileUri != null)
					{
						mTweetImageUri = fileUri;
						mTweetImage.setImageURI(fileUri);
					}
				}
				catch (FileNotFoundException e)
				{
					Log.e("PostTweet", "onActivityResult onImageAttachmentReceived FileNotFoundException");
				}
				catch (IOException e)
				{
					Log.e("PostTweet", "onActivityResult onImageAttachmentReceived IOException");
				}
				catch (IllegalArgumentException e)
				{
					Log.e("PostTweet", "onActivityResult onImageAttachmentReceived IllegalArgumentException, "+e.getMessage());					
				}

				if(fileUri != null)
				{

				}
				else
				{
					Log.e("PostTweet", "Attachment error");
				}

			}

		}
		else
		{
			if (requestCode == REQUEST_CODE_IMAGE_SELECT || requestCode == REQUEST_CODE_IMAGE_CAPTURE)
			{
				ImageUtility.onImageAttachmentCancelled();
			}
			Log.i("PostTweet","onActivityResult result code: " + resultCode + " for request code: " + requestCode);
		}

	}

	public static String[] getUsernamesAndHashtags(Iterator<TweetUser> tweetUsers, Iterator<TrendingTag> hashTags)
	{
		List<String> usernamesAndHashtags = new ArrayList<String>();
		usernamesAndHashtags.add("@feedback");

		for (TweetUser user; tweetUsers.hasNext(); ) 
		{
			user = tweetUsers.next();
			usernamesAndHashtags.add("@"+user.username);
		}

		//usernamesAndHashtags.add("#feedback");
		for (TrendingTag tag; hashTags.hasNext(); ) 
		{
			tag = hashTags.next();
			usernamesAndHashtags.add("#"+tag.hashtag);
		}
		
		String[] usernamesList = new String[usernamesAndHashtags.size()];

		return usernamesAndHashtags.toArray(usernamesList);
	}

	public class URLShortenerTask extends AsyncTask<Void, Void, String> 
	{
		private final static String TAG = "URLShortenerTask";
		private UIEventSink m_uicallback;
		private Editable mEditable;
		private URLSpan mUrlSpan = null;
		AsyncTaskEventHandler eventHandler;

		public URLShortenerTask(Editable editable, URLSpan urlSpan)
		{
			mEditable = editable;
			mUrlSpan = urlSpan;
		}

		@Override
		protected void onPreExecute()
		{
			eventHandler = new AsyncTaskEventHandler(PostTweetActivity.this, "Shortening..");
			m_uicallback = eventHandler;
			Log.d("tag","onPreExecute");
			if(m_uicallback!=null)
			{
				m_uicallback.onAysncTaskPreExecute(this, new AsyncTaskCancelCallback()
				{
					@Override
					public void onCancelled()
					{
						cancel(true);
					}
				}, true);
			}
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			int start = mEditable.getSpanStart(mUrlSpan);
			int end = mEditable.getSpanEnd(mUrlSpan);
			String str = mEditable.toString();
			String actualUrl = str.substring(start, end);

			try {
				return Helper.shortenUrl(actualUrl);
			} catch (IOException e) {
				return actualUrl;
			}
		}

		@Override
		protected void onPostExecute(String  shortUrl)
		{
			eventHandler.dismiss();
			if(shortUrl != null)
			{
				int start = mEditable.getSpanStart(mUrlSpan);
				int end = mEditable.getSpanEnd(mUrlSpan);
				mEditable.replace(start, end, shortUrl);

				mTweetContent.setText(mEditable);
				mTweetContent.setSelection(mEditable.length());
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			String username = mTwitterAppClient.mApp.getUsername();
			username		= (username.equals("")) ? "No Name" : username;
		
			mPostToTwitterCheckBox.setText(" Post to Twitter  (" + username + ")");
			mPostToTwitterCheckBox.setChecked(true);
			
			Toast.makeText(PostTweetActivity.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onError(String value) {
			mPostToTwitterCheckBox.setChecked(false);
			
			Toast.makeText(PostTweetActivity.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
}
