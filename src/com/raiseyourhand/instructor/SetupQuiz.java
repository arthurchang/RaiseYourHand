package com.raiseyourhand.instructor;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.raiseyourhand.R;
import com.ws.Request;
import com.ws.RequestType;
import com.ws.local.SendRequest;
import com.ws.local.ServerResponseListener;
/**
 * P33, 34
 * @author Hanrui Zhang
 *
 */
public class SetupQuiz extends Activity {

	private boolean choose_bluetooth;
	private boolean choose_builtin;
	private Button begin_quiz_button;
	private Button upload_screenshot_button;
	private ImageView quiz_image;
	private TextView time_set;
	private int count;
	private Uri imageUri;
	protected static final int REQUEST_SAVE = 0;
	protected static final int REQUEST_LOAD = 1;
	protected static final int SHARE_PICTURE_REQUEST = 2;
	protected static final int START_QUIZ = 3;
	private boolean timer_set;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructor_quiz);
		// Show the Up button in the action bar.
		setupActionBar();

		// Set up the start_quiz_button
		begin_quiz_button = (Button) findViewById(R.id.instructor_quiz_button_begin);
		begin_quiz_button.setOnClickListener(new BeginQuizOnClickListener());

		// Set up the take_screenshot_button
		upload_screenshot_button = (Button) findViewById(R.id.instructor_quiz_button_upload);
		upload_screenshot_button.setOnClickListener(new UploadScreenshotOnClickListener());

		// Set up the ImageView
		quiz_image = (ImageView) findViewById(R.id.instructor_quiz_imageView);

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.instructor_quiz, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_back:
			//finishActivity(0);
			onBackPressed();
			return true;
		case R.id.action_mic:
			final Dialog mic_setting = new Dialog(SetupQuiz.this);
			mic_setting.setContentView(R.layout.dialog_instructor_set_mic);
			Button mic_set = (Button) mic_setting.findViewById(R.id.instructor_set_mic_btn);
			TextView bluetooth = (TextView) mic_setting.findViewById(R.id.instructor_set_mic_bluetooth_text);
			TextView builtin = (TextView) mic_setting.findViewById(R.id.instructor_set_mic_builtin_text);



			bluetooth.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					choose_bluetooth = !choose_bluetooth;
					choose_builtin = !choose_builtin;
				}
			});

			builtin.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					choose_bluetooth = !choose_bluetooth;
					choose_builtin = !choose_builtin;
				}
			});

			mic_set.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//set microphone
					if(choose_bluetooth){
						BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
						if (mBluetoothAdapter == null) {
							//it's actually quite complicated to connect to the Bluetooth server
						}
					}else if(choose_builtin){
						//looks like we need another dialog to record?
					}
					mic_setting.dismiss();
				}

			});
			mic_setting.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * OnClickListener for when the begin quiz button is clicked
	 */
	private class BeginQuizOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0) {
			if(timer_set){
				// Pass the quiz screenshot into the intent
				//Do not pass bitmap bytearray here, too much memory and guaranteed not working
				if(imageUri != null){
					Intent beginQuizIntent = new Intent(SetupQuiz.this, OngoingQuiz.class);
					beginQuizIntent.putExtra("Quiz ImageUri", imageUri);
					beginQuizIntent.putExtra("Quiz Time", time_set.getText().toString());

					//TODO in what form do we send those options for quiz
					// Tell server that this quiz has started
					Object[] args = new Object[1]; // TODO: probably lecture id?
					SendStartQuizServerResponseListener listener = new SendStartQuizServerResponseListener();
					SendRequest sendStartQuizRequest = new SendRequest(RequestType.SEND_START_QUIZ, listener, args);
					sendStartQuizRequest.execute((Void)null);

					// TODO Send image to server (in bytearray), and the time/options for the quiz
					startActivityForResult(beginQuizIntent, START_QUIZ);
				}else{
					Toast.makeText(SetupQuiz.this.getBaseContext(), "No quiz image available",
							Toast.LENGTH_SHORT).show();
				}
			}else{
				final Dialog set_time = new Dialog(SetupQuiz.this);
				set_time.setContentView(R.layout.dialog_set_time);

				Button set = (Button) set_time.findViewById(R.id.set_time_button);
				Button cancel = (Button) set_time.findViewById(R.id.cancel_time_button);
				final NumberPicker minutes = (NumberPicker) set_time.findViewById(R.id.NumberPicker_minute);
				final NumberPicker seconds = (NumberPicker) set_time.findViewById(R.id.NumberPicker_second);

				minutes.setMaxValue(59);
				minutes.setMinValue(0);

				seconds.setMaxValue(59);
				seconds.setMinValue(0);

				set.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						int set_min = minutes.getValue();
						int set_sec = seconds.getValue();
						if(set_min + set_sec != 0){
							time_set.setText(String.format("%02d", set_min) + ":" + String.format("%02d", set_sec));
							timer_set = true;
							begin_quiz_button.setText(R.string.instructor_quiz_begin_button);
						}else{
							time_set.setText("Please set time again");
						}
						set_time.dismiss();		
					}

				});

				cancel.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						set_time.dismiss();				
					}
				});

				set_time.show();
			}
		}
	}

	/**
	 * OnClickListener for when the upload screenshot button is clicked
	 */
	private class UploadScreenshotOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Go to an activity that chooses an image or use the camera?

			// TODO Change quiz_image to the screenshot taken, if it's been taken

		}

	}

	/**
	 * Private sub-class to respond to server's response when telling server to start quiz
	 */
	private class SendStartQuizServerResponseListener implements ServerResponseListener {

		@Override
		public boolean onResponse(Request r) {
			// TODO Make sure server got message correctly?
			return false;
		}
	}

}
