package com.raiseyourhand.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raiseyourhand.R;

/**
 * A student shared fragment representing a section of the app, where
 * notes shared by students are listed.
 */
public class StudentSharedFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_student_shared, container, false);
		return rootView;
	}
}
