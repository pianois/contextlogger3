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

import java.util.ArrayList;
import java.util.Arrays;

import org.sizzlelab.contextlogger.android.R;
import org.sizzlelab.contextlogger.android.io.MainPipeline;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;
import org.sizzlelab.contextlogger.android.travel.TravelPanelFragement.CustomSubjectItemDialog.CustomSubjectItemListener;
import org.sizzlelab.contextlogger.android.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.widget.AdapterView;
import com.WazaBe.HoloEverywhere.widget.AdapterView.OnItemSelectedListener;
import com.WazaBe.HoloEverywhere.widget.EditText;
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
																						OnItemSelectedListener{

	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {
			ArrayList<ActionEvent> travelList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
			for(ActionEvent ae : travelList){
				if(TravelApp.getInstance().getString(R.string.travel).equals(ae.getActionEventName())){
					if(mTextViewDuration != null){
						 mTextViewDuration.setText(ae.getDuration(true));						
					}
				}
			}
			mHandler.postDelayed(mTimedTask , 500);				
		}
	};
	
	private enum CustomSubject{
		UNKONWN, MODE, REASON, PURPOSE, DESTINATION
	}
	
	private enum TravelStatus{
		IDLE, MOVING, PAUSE, STOP;
		public static TravelStatus getTravelStatus(final int index) throws IndexOutOfBoundsException{
			if(index < 0 || index > (TravelStatus.values()).length){
				throw new IndexOutOfBoundsException();
			}
			return TravelStatus.values()[index];
		}
	}
	
	private OnSupportFragmentListener mListener = null;
	
	private boolean mIsRunning = false;
	
	private Spinner mSpinnerMode = null;
	private Spinner mSpinnerModePerson = null;
	private Spinner mSpinnerPurpose = null;
	private Spinner mSpinnerDestination = null;
	private Spinner mSpinnerReason = null;
	
	private ImageButton mButtonPlay = null;
	private ImageButton mButtonPause = null;
	private ImageButton mButtonStop = null;
	private ImageButton mButtonParking = null;
	
	private View mViewNoTrip = null;
	private View mViewTripContainer = null;
	private View mViewParkingAnimation = null;
	private TextView mTextViewDuration = null;
	private TextView mTextViewStatus = null;
	
	private Switch mLoggerSwitch = null;
	
	private String mNewTravelMode = null;
	private String mNewTravelReason = null;
	private String mNewTravelPurpose = null;
	private String mNewTravelDestination = null;
	private TravelApp mApp = null;
	
	private TravelStatus mStatus = TravelStatus.IDLE;
	
	private String mCurrentMode = null;
	private String mCurrentPersonNumber = null;
	private String mCurrentReason = null;
	private String mCurrentPurpose = null;
	private String mCurrentDestination = null;
	
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
		mApp = TravelApp.getInstance();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.travel_diary, container, false); 
		mSpinnerMode = (Spinner)view.findViewById(R.id.spinner_travel_transport_mode_list);
		mSpinnerMode.setOnItemSelectedListener(this);
		mSpinnerModePerson = (Spinner)view.findViewById(R.id.spinner_travel_transport_mode_person_list);
		mSpinnerModePerson.setOnItemSelectedListener(this);
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
		mButtonParking = (ImageButton)view.findViewById(R.id.image_button_travel_parking);
		mButtonParking.setOnClickListener(this);
		mViewNoTrip = view.findViewById(R.id.text_view_no_trip);
		mViewTripContainer = view.findViewById(R.id.layout_trip_duration_container);
		mTextViewDuration = (TextView)view.findViewById(R.id.text_view_travel_duration_value);
		mLoggerSwitch = (Switch)getSherlockActivity().getSupportActionBar().getCustomView().findViewById(R.id.logger_switcher);
		mTextViewStatus = (TextView)view.findViewById(R.id.text_view_travel_service_state);
		mViewParkingAnimation = view.findViewById(R.id.view_travel_parking_animation);
		mViewParkingAnimation.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
		mHandler.post(mTimedTask);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mHandler.removeCallbacks(mTimedTask);
	}

	private void invalidateUIComponents(){
		switch(mStatus){
			case IDLE:
			case STOP:
				toggleUIComponent(true);
				mButtonPause.setEnabled(false);
				mButtonStop.setEnabled(false);
				break;
			case PAUSE:
				toggleUIComponent(true);
				mSpinnerDestination.setEnabled(false);
				mButtonPause.setEnabled(false);
				mSpinnerPurpose.setEnabled(false);
				break;
			case MOVING:
				toggleUIComponent(false);
				mButtonPause.setEnabled(true);
				mButtonStop.setEnabled(true);
				break;
		}
	}
	
	private void refreshUI(){
		// load travel status
		try{
			mStatus = TravelStatus.getTravelStatus(mApp.getTravelStatus()); 			
		}catch(IndexOutOfBoundsException e){
			mStatus = TravelStatus.IDLE;
		}

		if(mIsRunning){
			mTextViewStatus.setText(R.string.travel_service_running);
			// enable all the components
			toggleUIComponent(true);
			invalidateUIComponents();
			if(mStatus == TravelStatus.PAUSE){
				mButtonParking.setVisibility(View.VISIBLE);
			}else{
				mButtonParking.setVisibility(View.GONE);
			}
			if(mStatus == TravelStatus.MOVING){
				mButtonPlay.setVisibility(View.GONE);
				mButtonPause.setVisibility(View.VISIBLE);
			}else if(mStatus == TravelStatus.PAUSE){
				mButtonPlay.setVisibility(View.VISIBLE);
				mButtonPause.setVisibility(View.GONE);
			}
			if((mStatus == TravelStatus.MOVING) || (mStatus == TravelStatus.PAUSE)){
				mViewNoTrip.setVisibility(View.GONE);
				mViewTripContainer.setVisibility(View.VISIBLE);
			}else{
				mViewNoTrip.setVisibility(View.VISIBLE);
				mViewTripContainer.setVisibility(View.GONE);
			}
		} else {
			mTextViewStatus.setText(R.string.service_stopped);
			// disable all the components
			toggleUIComponent(false);
			mCurrentMode = null;
			mCurrentReason = null;
			mCurrentPurpose = null;
			mCurrentDestination = null;
			mViewNoTrip.setVisibility(View.VISIBLE);
			mViewTripContainer.setVisibility(View.GONE);
			mButtonParking.setVisibility(View.GONE);
		}
		refreshSpinners();		
	}

	private void toggleUIComponent(final boolean enable){
		mButtonPause.setEnabled(enable);
		mButtonStop.setEnabled(enable);
		mButtonPlay.setEnabled(enable);
		mSpinnerMode.setEnabled(enable);
		mSpinnerModePerson.setEnabled(enable);
		mSpinnerDestination.setEnabled(enable);
		mSpinnerPurpose.setEnabled(enable);
		mSpinnerReason.setEnabled(enable);		
	}
	
	private void refershModeSpinner(){
		// mode
		String[] arrayMode = getResources().getStringArray(R.array.travel_mode_array);
		ArrayList<String> listMode = new ArrayList<String>(Arrays.asList(arrayMode)); 
		// load the saved data, if any
		ArrayList<String> modeTempList = TravelApp.getStringToList(mApp.getTravelModes());
		if((modeTempList != null) && (!modeTempList.isEmpty())){
			for(String m : modeTempList){
				listMode.add(listMode.size() - 1, m);
			}
		}
		ArrayAdapter<String> dataAdapterMode = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listMode);
		int modeListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewTravelMode)) && (listMode != null) && (!listMode.isEmpty())){
			for(int i = 0; i < listMode.size(); i++){
				String s = listMode.get(i);
				if(s.equals(mNewTravelMode)){
					modeListSelectionOffset = i;
					mNewTravelMode = null;
					break;
				}
			}
		}
		mSpinnerMode.setAdapter(dataAdapterMode);
		if(modeListSelectionOffset > 0){
			mSpinnerMode.setSelection(modeListSelectionOffset, false);
		} else {
			mSpinnerMode.setSelection(0);
		}
		mSpinnerMode.invalidate();
		
		// hard-code for person
		String[] arrayPerson = {"1", "2", "3", "4", "5", "6", "7", "8"};
		ArrayList<String> listPerson = new ArrayList<String>(Arrays.asList(arrayPerson));
		ArrayAdapter<String> dataAdapterPerson = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listPerson);
		mSpinnerModePerson.setAdapter(dataAdapterPerson);
	}
	
	private void refershReasonSpinner(){
		// reason
		String[] arrayReason = getResources().getStringArray(R.array.travel_reason_array);
		ArrayList<String> listReason = new ArrayList<String>(Arrays.asList(arrayReason));
		// load the saved data, if any
		ArrayList<String> reasonTempList = TravelApp.getStringToList(mApp.getTravelReasons());
		if((reasonTempList != null) && (!reasonTempList.isEmpty())){
			for(String m : reasonTempList){
				listReason.add(listReason.size() - 1, m);
			}
		}
		int reasonListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewTravelReason)) && (listReason != null) && (!listReason.isEmpty())){
			for(int i = 0; i < listReason.size(); i++){
				String s = listReason.get(i);
				if(s.equals(mNewTravelReason)){
					reasonListSelectionOffset = i;
					mNewTravelReason = null;
					break;
				}
			}
		}
		ArrayAdapter<String> dataAdapterReason = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listReason);
		mSpinnerReason.setAdapter(dataAdapterReason);
		if(reasonListSelectionOffset > 0){
			mSpinnerReason.setSelection(reasonListSelectionOffset, false);
		} else {
			mSpinnerReason.setSelection(0);
		}
		mSpinnerReason.invalidate();
	}
	
	private void refershDestinationSpinner(){
		// destination
		String[] arrayDestination = getResources().getStringArray(R.array.travel_destination_array);
		ArrayList<String> listDestination = new ArrayList<String>(Arrays.asList(arrayDestination));
		// load the saved data, if any
		ArrayList<String> destinationTempList = TravelApp.getStringToList(mApp.getTravelDestinations());
		if((destinationTempList != null) && (!destinationTempList.isEmpty())){
			for(String m : destinationTempList){
				listDestination.add(listDestination.size() - 1, m);
			}
		}
		int desListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewTravelDestination)) && (listDestination != null) && (!listDestination.isEmpty())){
			for(int i = 0; i < listDestination.size(); i++){
				String s = listDestination.get(i);
				if(s.equals(mNewTravelDestination)){
					desListSelectionOffset = i;
					mNewTravelDestination = null;
					break;
				}
			}
		}
		ArrayAdapter<String> dataAdapterDestination = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listDestination);
		mSpinnerDestination.setAdapter(dataAdapterDestination);		
		if(desListSelectionOffset > 0){
			mSpinnerDestination.setSelection(desListSelectionOffset, false);
		}else{
			mSpinnerDestination.setSelection(0);
		}
		mSpinnerDestination.invalidate();
	}
	
	private void refershPurposeSpinner(){
		// purpose
		String[] arrayPurpose = getResources().getStringArray(R.array.travel_purpose_array);
		ArrayList<String> listPurpose = new ArrayList<String>(Arrays.asList(arrayPurpose));		
		// load the saved data, if any
		ArrayList<String> purposeTempList = TravelApp.getStringToList(mApp.getTravelPurposes());
		if((purposeTempList != null) && (!purposeTempList.isEmpty())){
			for(String m : purposeTempList){
				listPurpose.add(listPurpose.size() - 1, m);
			}
		}
		ArrayAdapter<String> dataAdapterPurpose = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listPurpose);
		int purposeListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewTravelPurpose)) && (listPurpose != null) && (!listPurpose.isEmpty())){
			for(int i = 0; i < listPurpose.size(); i++){
				String s = listPurpose.get(i);
				if(s.equals(mNewTravelPurpose)){
					purposeListSelectionOffset = i;
					mNewTravelPurpose = null;
					break;
				}
			}
		}
		mSpinnerPurpose.setAdapter(dataAdapterPurpose);
		if(purposeListSelectionOffset > 0){
			mSpinnerPurpose.setSelection(purposeListSelectionOffset, false);
		}else {
			mSpinnerPurpose.setSelection(0);
		}
		mSpinnerPurpose.invalidate();
	}
	
	private void refreshSpinners(){
		refershModeSpinner();
		refershPurposeSpinner();
		refershDestinationSpinner();
		refershReasonSpinner();
	}
	
	private void handleCustomeSubjectItem(final String itemName, final CustomSubject subject){	
		switch(subject){
			case MODE:
				mNewTravelMode = itemName;
				ArrayList<String> modeList = TravelApp.getStringToList(mApp.getTravelModes());
				if(modeList == null){
					mApp.saveTravelMode(itemName);
				}else{
					modeList.add(itemName);
					String strMode = TextUtils.join(";", modeList.toArray());
					mApp.saveTravelMode(strMode);
				}
				mCurrentMode = itemName;
				refershModeSpinner();
				break;
			case PURPOSE:
				mNewTravelPurpose = itemName;
				ArrayList<String> purposeList = TravelApp.getStringToList(mApp.getTravelPurposes());
				if(purposeList == null){
					mApp.saveTravelPurpose(itemName);
				}else{
					purposeList.add(itemName);
					String strPurpose = TextUtils.join(";", purposeList.toArray());
					mApp.saveTravelPurpose(strPurpose);
				}
				mCurrentPurpose = itemName;
				refershPurposeSpinner();
				break;
			case REASON:
				mNewTravelReason = itemName;
				ArrayList<String> reasonList = TravelApp.getStringToList(mApp.getTravelReasons());
				if(reasonList == null){
					mApp.saveTravelReason(itemName);
				}else{
					reasonList.add(itemName);
					String strReason = TextUtils.join(";", reasonList.toArray());
					mApp.saveTravelReason(strReason);
				}
				mCurrentReason = itemName;
				refershReasonSpinner();
				break;
			case DESTINATION:
				mNewTravelDestination = itemName;
				ArrayList<String> desList = TravelApp.getStringToList(mApp.getTravelDestinations());
				if(desList == null){
					mApp.saveTravelDestination(itemName);
				}else{
					desList.add(itemName);
					String strDes = TextUtils.join(";", desList.toArray());
					mApp.saveTravelMode(strDes);
				}
				mCurrentDestination = itemName;
				refershDestinationSpinner();
				break;					
			default:
				return;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		final AdapterView<?> arg = parent;
		if(parent != null){
			final String itemName = parent.getItemAtPosition(pos).toString();
			boolean isShown = false;
			CustomSubject cs = CustomSubject.UNKONWN;
			final int viewId = parent.getId();
			if(viewId == R.id.spinner_travel_transport_mode_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					cs = CustomSubject.MODE;
				}
				mCurrentMode = itemName;
			}else if(viewId == R.id.spinner_travel_purpose_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					cs = CustomSubject.PURPOSE;
				}
				mCurrentPurpose = itemName;
			}else if(viewId == R.id.spinner_travel_destination_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					cs = CustomSubject.DESTINATION;
				}
				mCurrentDestination = itemName;
			}else if(viewId == R.id.spinner_travel_reason_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					cs = CustomSubject.REASON;
				}
				mCurrentReason = itemName;
			}else if(viewId == R.id.spinner_travel_transport_mode_person_list){
				mCurrentPersonNumber = itemName;
			}
			if(isShown){
				CustomSubjectItemDialog csid = new CustomSubjectItemDialog();
				csid.config(new CustomSubjectItemListener (){
					@Override
					public void OnTagNameInputCompleted(String itemName, CustomSubject subject) {
						handleCustomeSubjectItem(itemName, subject);
					}
				}, cs);
				csid.setAlertDialogListener(new AlertDialogListener(){
					@Override
					public void onPositiveClick() { }
					@Override
					public void onNegativeClick() {
						resetSpinner(arg);
					}
					@Override
					public void onCancel() { 
						resetSpinner(arg);
					}
				});
				csid.show(getFragmentManager(), "CustomSubjectItem");				
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	private void resetSpinner(AdapterView<?> parent){
		if(parent != null){
			final int viewId = parent.getId();
			if(viewId == R.id.spinner_travel_transport_mode_list){
				mSpinnerMode.setSelection(0, false);
			}else if(viewId == R.id.spinner_travel_purpose_list){
				mSpinnerPurpose.setSelection(0, false);
			}else if(viewId == R.id.spinner_travel_destination_list){
				mSpinnerDestination.setSelection(0, false);
			}else if(viewId == R.id.spinner_travel_reason_list){
				mSpinnerReason.setSelection(0, false);
			}
		}
	}

	@Override
	public void onClick(View view) {
		if(view != null){
			final int viewId = view.getId();
			if(viewId == R.id.view_travel_parking_animation){
				mListener.onFragmentChanged(R.layout.travel_parking_panel, null);
				return;
			}
			if(viewId == R.id.image_button_travel_parking){
				mListener.onFragmentChanged(R.layout.travel_parking_panel, null);
				return;
			}
			if(viewId == R.id.image_button_travel_pause){
				notifyTravelingEvent(TravelStatus.PAUSE);
				mButtonPlay.setVisibility(View.VISIBLE);
				mButtonPause.setVisibility(View.GONE);
				mButtonParking.setVisibility(View.VISIBLE);
			}else if(viewId == R.id.image_button_travel_play){
				notifyTravelingEvent(TravelStatus.MOVING);
				mViewNoTrip.setVisibility(View.GONE);
				mViewTripContainer.setVisibility(View.VISIBLE);
				mButtonPlay.setVisibility(View.GONE);
				mButtonPause.setVisibility(View.VISIBLE);
				mButtonParking.setVisibility(View.GONE);
			}else if(viewId == R.id.image_button_travel_stop){
				notifyTravelingEvent(TravelStatus.STOP);
				mViewNoTrip.setVisibility(View.VISIBLE);
				mViewTripContainer.setVisibility(View.GONE);
				mButtonPlay.setVisibility(View.VISIBLE);
				mButtonPause.setVisibility(View.GONE);
				mButtonParking.setVisibility(View.GONE);
			}			
			mApp.saveTravelStauts(mStatus.ordinal());
			invalidateUIComponents();
		}
	}

	private void notifyTravelingEvent(final TravelStatus ts){
		if(mStatus == TravelStatus.IDLE){
			ActionEvent ae = new ActionEvent(getString(R.string.travel), System.currentTimeMillis());
			ae.setState(EventState.START);
			ActionEventHandler.getInstance().insert(getSherlockActivity().getApplicationContext(), ae);	
			final String payload = ae.getMessagePayload();
			if(!TextUtils.isEmpty(payload)){
				Intent i = new Intent();
				i.setAction(CUSTOM_INTENT_ACTION);
				i.putExtra("APPLICATION_ACTION", payload);
				getSherlockActivity().sendBroadcast(i);
			} else {
				mApp.showToastMessage(R.string.client_error);
			}
		}
		switch(ts){
			case IDLE:
				break;
			case STOP:
				// mode
				ArrayList<ActionEvent> stopList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
				for(ActionEvent ae : stopList){
					ae.confirmBreakTimestamp();
					ae.setState(EventState.STOP);
					ActionEventHandler.getInstance().update(getSherlockActivity().getApplicationContext(), ae);
					notifyEvent(ae.getMessagePayload());
				} 
				mStatus = TravelStatus.IDLE;
				mTextViewDuration.setText("");
				break;
			case PAUSE:
				// mode
				ArrayList<ActionEvent> pauseList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
				for(ActionEvent ae : pauseList){
					if(!getString(R.string.travel).equals(ae.getActionEventName())){
						ae.confirmBreakTimestamp();
						ae.setState(EventState.STOP);
						ActionEventHandler.getInstance().update(getSherlockActivity().getApplicationContext(), ae);
						notifyEvent(ae.getMessagePayload());
					}
				} 					
				mStatus = TravelStatus.PAUSE;
				break;
			case MOVING:
				// mode
				if(!TextUtils.isEmpty(mCurrentMode)){
					ActionEvent ae = new ActionEvent(mCurrentMode, System.currentTimeMillis());
					ae.setState(EventState.START);
					ActionEventHandler.getInstance().insert(getSherlockActivity().getApplicationContext(), ae);	
					notifyEvent(ae.getMessagePayload());
					mCurrentMode = null;
				}
				if(!TextUtils.isEmpty(mCurrentPersonNumber)){
					handleNote("Person(s): " + mCurrentPersonNumber);
					mCurrentPersonNumber = null;
				}
				// purpose
				if(!TextUtils.isEmpty(mCurrentPurpose)){
					handleNote("Purpose: " + mCurrentPurpose);
					mCurrentPurpose = null;
				}
				// destination
				if(!TextUtils.isEmpty(mCurrentDestination)){
					handleNote("Destination: " + mCurrentDestination);
					mCurrentDestination = null;
				}
				// reason
				if(!TextUtils.isEmpty(mCurrentReason)){
					handleNote("Reason: " + mCurrentReason);
					mCurrentReason = null;
				}
				mStatus = TravelStatus.MOVING;
				break;
		}
		
	}
	
	private void notifyEvent(final String payload){
		if(!TextUtils.isEmpty(payload)){
			Intent i = new Intent();
			i.setAction(CUSTOM_INTENT_ACTION);
			i.putExtra("APPLICATION_ACTION", payload);
			getSherlockActivity().sendBroadcast(i);
		} else {
			mApp.showToastMessage(R.string.client_error);
		}
	}
	
	private void handleNote(final String note){
		ActionEventHandler.getInstance().insert(getSherlockActivity().getApplicationContext(), 
				new ActionEvent("USER_NOTE", System.currentTimeMillis(), note, true));
		Intent intent = new Intent();
		intent.setAction(CUSTOM_INTENT_ACTION);
		intent.putExtra("APPLICATION_ACTION", "USER_NOTE");
		intent.putExtra("APPLICATION_DATA", note);
		getSherlockActivity().sendBroadcast(intent);
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
			QuitAppDialog aqd = new QuitAppDialog();
			aqd.setAlertDialogListener(new AlertDialogListener(){
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
			});
			aqd.show(getFragmentManager(), "QuitApp");
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
				mStatus = TravelStatus.IDLE;
				mApp.saveTravelStauts(mStatus.ordinal());
				startService();
			}else{
				notifyTravelingEvent(TravelStatus.STOP);
				stopService();
				mApp.saveTravelStauts(mStatus.ordinal());
				mViewNoTrip.setVisibility(View.VISIBLE);
				mViewTripContainer.setVisibility(View.GONE);
			}
		}
	}	
	
	private void quitApp(){
		if(mLoggerSwitch != null){
			mLoggerSwitch.setChecked(false);
		} 
		notifyTravelingEvent(TravelStatus.STOP);
		stopService();
		mApp.saveTravelStauts(mStatus.ordinal());
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
	
	
	public static class QuitAppDialog extends BaseAlertDialog{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    	builder.setTitle(R.string.app_travel_quit_title);
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
	    	builder.setMessage(R.string.app_travel_quit_content);
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

	public static class CustomSubjectItemDialog extends BaseAlertDialog {

		private CustomSubjectItemListener mCustomSubjectItemListener = null;
		private CustomSubject mCustomSubject = CustomSubject.UNKONWN;
		
		public void config(final CustomSubjectItemListener listener, CustomSubject subject){
			mCustomSubjectItemListener = listener;
			mCustomSubject = subject;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
			switch(mCustomSubject){
				case MODE:
			    	builder.setTitle(R.string.travel_mode_dialog);
					break;
				case PURPOSE:
			    	builder.setTitle(R.string.travel_purpose_dialog);
					break;
				case REASON:
			    	builder.setTitle(R.string.travel_reason_dialog);
					break;
				case DESTINATION:
			    	builder.setTitle(R.string.travel_destination_dialog);					
					break;					
				default:
					return null;
			}
	    	LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
	    	final View noteView = inflater.inflate(R.layout.custom_item_dialog, null);
	    	final EditText travelItemContent = (EditText)noteView.findViewById(R.id.edit_text_travel_item);
	    	builder.setView(noteView);
	    	
	    	builder.setPositiveButton(R.string.travel_save, 
	    			new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final String itemName = travelItemContent.getEditableText().toString();
							if(!TextUtils.isEmpty(itemName)){
								if(mCustomSubjectItemListener != null){
									mCustomSubjectItemListener.OnTagNameInputCompleted(itemName, mCustomSubject);
								}
							}else{
								TravelApp.getInstance().showToastMessage(R.string.travel_custome_item_input_error);
							}
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
		
		public static interface CustomSubjectItemListener{
			void OnTagNameInputCompleted(final String itemName, final CustomSubject subject);
		}
		
	}
}
