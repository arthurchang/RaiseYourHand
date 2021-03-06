package com.raiseyourhand.instructor;

import java.util.ArrayList;

import ws.Request;
import ws.RequestType;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.raiseyourhand.Login;
import com.raiseyourhand.R;
import com.raiseyourhand.RaiseYourHandApp;
import com.ws.local.SendRequest;
import com.ws.local.ServerResponseListener;

import entities.Roster;

public class ViewLecture extends Activity {
	private SearchView searchView;
	private Button startButton;
	private ListView rosterListView;
	private ArrayAdapter<String> rosterAdapter;
	private String[] students = {"Alex Yoo","Arthur Chang", "Hanrui Zhang"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructor_view_lecture);

		// Check for logged in users.
		if (RaiseYourHandApp.getUsername() == null) {
			RaiseYourHandApp.logout();
			Intent login = new Intent(this, Login.class);
			startActivity(login);
		}

		// Show the Up button in the action bar.
		setupActionBar();

		// Set up Search Bar
		searchView = (SearchView)findViewById(R.id.instructor_view_lecture_search);
		searchView.setOnQueryTextListener(new ViewLectureOnQueryListener());


		// Set up lecture roster
		rosterListView = (ListView)findViewById(R.id.instructor_view_lecture_listview);     
		rosterAdapter = new ArrayAdapter<String>(this, R.layout.roster_item, students);
		rosterListView.setAdapter(rosterAdapter);

		// Setup Start button
		startButton = (Button)findViewById(R.id.instructor_view_lecture_button);
		startButton.setOnClickListener(new StartLectureOnClickListener());
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
		getMenuInflater().inflate(R.menu.view_lecture, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}

	public class StartLectureOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// Send SEND_START_LECTURE to server
			//Object[] args = new Object[1];
			//args[0] = RaiseYourHandApp.getCourseNum();
			//SendStartLectureServerResponseListener listener = new SendStartLectureServerResponseListener();
			//SendRequest SendStartLectureRequest = new SendRequest(new Request(RequestType.SEND_START_LECTURE, args), listener);
			//SendStartLectureRequest.execute((Void)null);

			// Create an Intent to launch the Lecture Activity
			Intent lecture = new Intent(ViewLecture.this, Lecture.class);
			startActivity(lecture);
		}
	}

	public class ViewLectureOnQueryListener implements OnQueryTextListener {
		public boolean onQueryTextChange(String newText) {
			if (TextUtils.isEmpty(newText)) {
				rosterAdapter.getFilter().filter("");
			} else {
				rosterAdapter.getFilter().filter(newText.toString());
			}
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			return false;
		}
	}

	/**
	 * Private method to tell server to fetch the roster
	 * 
	 * Not called by the demo, since it makes a connection to the server.
	 */
	private void getRosterFromServer() {
		Object[] args = new Object[]{RaiseYourHandApp.getCourseNum()};
		GetRosterServerResponseListener listener = new GetRosterServerResponseListener();
		SendRequest getRosterRequest = new SendRequest(new Request(RequestType.GET_ROSTER, args), listener);
		getRosterRequest.execute((Void)null);
	}

	/**
	 * Private method to tell server to start lecture
	 * 
	 * Not called by the demo, since it makes a connection to the server.
	 */
	private void startLectureToServer() {
		Object[] args = new Object[1];
		args[0] = RaiseYourHandApp.getCourseNum();
		SendStartLectureServerResponseListener listener = new SendStartLectureServerResponseListener();
		SendRequest SendStartLectureRequest = new SendRequest(new Request(RequestType.SEND_START_LECTURE, args), listener);
		SendStartLectureRequest.execute((Void)null);
	}

	/**
	 * Private sub-class to respond to server's response when requesting for the roster list for this lecture
	 * 
	 * Not called by the demo, since it makes a connection to the server.
	 */
	private class GetRosterServerResponseListener implements ServerResponseListener {

		@Override
		public boolean onResponse(Request r) {

			Object[] args = r.getArgs();

			try{
				if(((String)args[0]).equals(Request.FAILURE))
				{
					return false;
				}
				else if(((String)args[0]).equals(Request.SUCCESS))
				{
					ArrayList<Roster> server_roster_list = (ArrayList<Roster>)args[1];
					students = new String[server_roster_list.size()];

					// Populate roster list with usernames by what was received from server
					for(int i = 0 ; i < server_roster_list.size() ; i++)
					{
						students[i] = server_roster_list.get(i).getUsername();
					}

					rosterListView = (ListView)findViewById(R.id.instructor_view_lecture_listview);     
					rosterAdapter = new ArrayAdapter<String>(com.raiseyourhand.instructor.ViewLecture.this, R.layout.roster_item, students);
					rosterListView.setAdapter(rosterAdapter);

					return true;
				}
			} catch(ClassCastException e) {
				return false;
			}

			return false;
		}
	}

	/**
	 * Private sub-class to respond to server's response when telling the server to start lecture 
	 * 
	 * Not called by the demo, since it makes a connection to the server.
	 */
	private class SendStartLectureServerResponseListener implements ServerResponseListener {

		@Override
		public boolean onResponse(Request r) {
			Object[] args = r.getArgs();

			// Just make sure server got the message correctly
			try{
				if(((String)args[0]).equals(Request.FAILURE))
				{
					return false;
				}
				else if(((String)args[0]).equals(Request.SUCCESS))
				{
					return true;
				}
			} catch(ClassCastException e) {
				return false;
			}
			return false;
		}
	}
}
