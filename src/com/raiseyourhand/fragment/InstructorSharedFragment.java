package com.raiseyourhand.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raiseyourhand.R;


/**
 * A instructor shared fragment representing a section of the app, where
 * the notes shared by the instructor are listed.
 */
public class InstructorSharedFragment extends Fragment{
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_instructor_shared, container, false);

		
		return rootView;
	}
}
