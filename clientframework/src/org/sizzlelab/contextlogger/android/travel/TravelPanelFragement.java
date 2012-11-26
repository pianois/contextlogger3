/**
 * Copyright (c) 2012 Aalto University and the authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 *  
 * Authors:
 * Chao Wei (chao.wei@aalto.fi)
 */

package org.sizzlelab.contextlogger.android.travel;

import java.util.Arrays;
import java.util.List;

import org.sizzlelab.contextlogger.android.R;
import org.sizzlelab.contextlogger.android.io.MainPipeline;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;
import org.sizzlelab.contextlogger.android.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.widget.Spinner;
import com.WazaBe.HoloEverywhere.widget.Switch;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import fi.aalto.chaow.android.app.BaseAlertDialog;
import fi.aalto.chaow.android.app.BaseAlertDialog.AlertDialogListener;
import fi.aalto.chaow.android.app.BaseFragmentActivity.OnSupportFragmentListener;

public class TravelPanelFragement extends SherlockFragment implements OnClickListener, Constants, OnCheckedChangeListener, 
																	com.WazaBe.HoloEverywhere.widget.AdapterView.OnItemSelectedListener{

	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {

			mHandler.postDelayed(mTimedTask , 6000);
		}
	};
	
	private OnSupportFragmentListener mListener = null;
	
	private boolean mIsRunning = false;
	
	private Spinner mSpinnerMode = null;
	private Spinner mSpinnerPurpose = null;
	private Spinner mSpinnerDestination = null;
	private Spinner mSpinnerReason = null;
	
	private ImageButton mButtonPlay = null;
	private ImageButton mButtonPause = null;
	private ImageButton mButtonStop = null;
	
	private View mViewNoTrip = null;
	private View mViewTripContainer = null;
	private TextView mTextViewDuration = null;
	private TextView mTextViewStatus = null;
	
	private Switch mLoggerSwitch = null;
	
	public TravelPanelFragement(){
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (OnSupportFragmentListener) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
		getSherlockActivity().startService(archiveIntent);	
		mIsRunning = true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.travel_diary, container, false); 
		mSpinnerMode = (Spinner)view.findViewById(R.id.spinner_travel_transport_mode_list);
		mSpinnerMode.setOnItemSelectedListener(this);
		mSpinnerPurpose = (Spinner)view.findViewById(R.id.spinner_travel_purpose_list);
		mSpinnerPurpose.setOnItemSelectedListener(this);
		mSpinnerDestination = (Spinner)view.findViewById(R.id.spinner_travel_destination_list);
		mSpinnerDestination.setOnItemSelectedListener(this);
		mSpinnerReason = (Spinner)view.findViewById(R.id.spinner_travel_reason_list);
		mSpinnerReason.setOnItemSelectedListener(this);
		mButtonPlay = (ImageButton)view.findViewById(R.id.image_button_travel_play);
		mButtonPlay.setOnClickListener(this);
		mButtonPause = (ImageButton)view.findViewById(R.id.image_button_travel_pause);
		mButtonPause.setOnClickListener(this);
		mButtonStop = (ImageButton)view.findViewById(R.id.image_button_travel_stop);
		mButtonStop.setOnClickListener(this);
		mViewNoTrip = view.findViewById(R.id.text_view_no_trip);
		mViewTripContainer = view.findViewById(R.id.layout_trip_duration_container);
		mTextViewDuration = (TextView)view.findViewById(R.id.text_view_travel_duration_value);
		mLoggerSwitch = (Switch)getSherlockActivity().getSupportActionBar().getCustomView().findViewById(R.id.logger_switcher);
		mTextViewStatus = (TextView)view.findViewById(R.id.text_view_travel_service_state);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
//		mHandler.post(mTimedTask);
	}
	
	@Override
	public void onStop() {
		super.onStop();
//		mHandler.removeCallbacks(mTimedTask);
	}
	
	private void refreshUI(){
		if(mIsRunning){
			mTextViewStatus.setText(R.string.travel_service_running);
		} else {
			// disable all the components
			mTextViewStatus.setText(R.string.service_stopped);
		}
		refreshSpinners();		
	}

	private void refreshSpinners(){

		String[] arrayMode = getResources().getStringArray(R.array.travel_mode_array);
		List<String> listMode = Arrays.asList(arrayMode);
		ArrayAdapter<String> dataAdapterMode = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listMode);
		mSpinnerMode.setAdapter(dataAdapterMode);
		
		String[] arrayPurpose = getResources().getStringArray(R.array.travel_purpose_array);
		List<String> listPurpose = Arrays.asList(arrayPurpose);		
		ArrayAdapter<String> dataAdapterPurpose = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listPurpose);
		mSpinnerPurpose.setAdapter(dataAdapterPurpose);
		
		String[] arrayDestination = getResources().getStringArray(R.array.travel_destination_array);
		List<String> listDestination = Arrays.asList(arrayDestination);
		ArrayAdapter<String> dataAdapterDestination = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listDestination);
		mSpinnerDestination.setAdapter(dataAdapterDestination);		
		
		String[] arrayReason = getResources().getStringArray(R.array.travel_reason_array);
		List<String> listReason = Arrays.asList(arrayReason);
		ArrayAdapter<String> dataAdapterReason = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listReason);
		mSpinnerReason.setAdapter(dataAdapterReason);		
	}
	
	@Override
	public void onItemSelected(com.WazaBe.HoloEverywhere.widget.AdapterView<?> parent, View view, int pos, long id) {
		if(view != null){
			final int viewId = view.getId();
			if(viewId == R.id.spinner_travel_transport_mode_list){
//				parent.getCount();
//				= parent.getItemAtPosition(pos).toString();
			} else if(viewId == R.id.spinner_travel_purpose_list){
				
			}else if(viewId == R.id.spinner_travel_destination_list){
				
			}else if(viewId == R.id.spinner_travel_reason_list){
				
			}			
		}
	}

	@Override
	public void onNothingSelected(com.WazaBe.HoloEverywhere.widget.AdapterView<?> parent) {
	}

	@Override
	public void onClick(View view) {
		if(view != null){
			final int viewId = view.getId();
			if(viewId == R.id.image_button_travel_pause){
				
			}else if(viewId == R.id.image_button_travel_play){
				
			}else if(viewId == R.id.image_button_travel_stop){
				
			}			
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.travel_menu, menu);
		updateMenuItem(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		updateMenuItem(menu);
	}

	private void updateMenuItem(Menu menu){
		MenuItem item = menu.findItem(R.id.menu_travel_toggle_service);
		if(MainPipeline.isEnabled(getSherlockActivity().getApplicationContext())){
			item.setTitle(R.string.travel_stop_logging);			
		}else {
			item.setTitle(R.string.travel_start_logging);
		}
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		if (itemId == R.id.menu_travel_export_data) {
			exportData();
			return true;
		} else if (itemId == R.id.menu_travel_history) {
			if(mListener != null){
				mListener.onFragmentChanged(R.layout.logger_history, null);
				return true;
			}
		} else if (itemId == R.id.menu_travel_shutdown) {
			new QuitAppDialog(new AlertDialogListener(){
				@Override
				public void onPositiveClick() {
					quitApp();
				}
				@Override
				public void onNegativeClick() {
				}
				@Override
				public void onCancel() {
				}
			}).show(getFragmentManager(), "QuitApp");
			return true;
		}else if(itemId == R.id.menu_travel_toggle_service){
			if(mLoggerSwitch != null){
				mLoggerSwitch.setChecked(!mIsRunning);
			} 
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
   
	private void exportData(){
		Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
		getSherlockActivity().startService(archiveIntent);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() == R.id.logger_switcher){
			if(isChecked){
				startService();
			}else{
				stopService();
			}
		}
	}	
	
	private void quitApp(){
		if(mLoggerSwitch != null){
			mLoggerSwitch.setChecked(false);
		} 
		stopService();
		getSherlockActivity().finish();
	}
	
	private void startService(){
		if(!mIsRunning){
			Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
			archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
			getSherlockActivity().startService(archiveIntent);	
			mIsRunning = true;
		}
		refreshUI();
	}
	
	private void stopService(){
		Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_DISABLE);
		getSherlockActivity().startService(archiveIntent);
		mIsRunning  = false;
		refreshUI();
	}
	
	
	private class QuitAppDialog extends BaseAlertDialog{

		public QuitAppDialog(AlertDialogListener listener) {
			super(listener);
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    	builder.setTitle(R.string.app_quit_title);
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
	    	builder.setMessage(R.string.app_quit_content);
	    	builder.setPositiveButton(R.string.ok, 
	    			new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getDialogListener().onPositiveClick();
						}
					});
	    	builder.setNegativeButton(R.string.cancel,     			
	    			new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getDialogListener().onNegativeClick();
						}
					});				
			return builder.create(); 
		}
	}


}
