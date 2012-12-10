package org.sizzlelab.contextlogger.android.travel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.sizzlelab.contextlogger.android.R;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;
import org.sizzlelab.contextlogger.android.utils.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.widget.AdapterView;
import com.WazaBe.HoloEverywhere.widget.AdapterView.OnItemSelectedListener;
import com.WazaBe.HoloEverywhere.widget.SeekBar;
import com.WazaBe.HoloEverywhere.widget.SeekBar.OnSeekBarChangeListener;
import com.WazaBe.HoloEverywhere.widget.Spinner;
import com.actionbarsherlock.app.SherlockFragment;

import fi.aalto.chaow.android.app.BaseFragmentActivity.OnSupportFragmentListener;

public class TravelParkingPanelFragement extends SherlockFragment implements OnClickListener,OnCheckedChangeListener, OnItemSelectedListener, Constants{

	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {
			ArrayList<ActionEvent> parkingList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
			for(ActionEvent ae : parkingList){
				if(TravelApp.getInstance().getString(R.string.travel_parking).equals(ae.getActionEventName())){
					if(mTextViewParkingDuration != null){
						mTextViewParkingDuration.setText(ae.getDuration(true));						
					}
				}
			}
			mHandler.postDelayed(mTimedTask , 500);				
		}
	};
	
	private OnSupportFragmentListener mListener = null;
	
	private Spinner mSpinnerParkingPlace = null;
	private Spinner mSpinnerPayment = null;
	private Spinner mSpinnerMode = null;
	private Spinner mSpinnerModePerson = null;
	
	private ImageButton mButtonPlay = null;
	private ImageButton mButtonStop = null;
	
	private CheckBox mCheckBoxFree = null;
	
	private SeekBar mSeekBarPrice = null;

	private TravelApp mApp = null;
	
	private View mViewNoParking = null;
	private View mViewParkingDurationContainer = null;
	private TextView mTextViewParkingDuration = null;
	private TextView mTextViewParkingPrice = null;
	
	private String mCurrentMode = null;
	private String mCurrentPersonNumber = null;
	private String mParkingPlace = null;
	private String mPaymentMethod = null;
	
	public TravelParkingPanelFragement(){
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
		mApp = TravelApp.getInstance();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.travel_parking_panel, container, false); 
		mSpinnerParkingPlace = (Spinner)view.findViewById(R.id.spinner_travel_parking_place_list);
		mSpinnerParkingPlace.setOnItemSelectedListener(this);
		mSpinnerPayment = (Spinner)view.findViewById(R.id.spinner_travel_parking_payment_list);
		mSpinnerPayment.setOnItemSelectedListener(this);
		mSpinnerMode = (Spinner)view.findViewById(R.id.spinner_travel_transport_mode_list);
		mSpinnerMode.setOnItemSelectedListener(this);
		mSpinnerModePerson = (Spinner)view.findViewById(R.id.spinner_travel_transport_mode_person_list);
		mSpinnerModePerson.setOnItemSelectedListener(this);
		mButtonPlay = (ImageButton) view.findViewById(R.id.image_button_travel_parking_play);
		mButtonPlay.setOnClickListener(this);
		mButtonStop = (ImageButton) view.findViewById(R.id.image_button_travel_parking_stop);
		mButtonStop.setOnClickListener(this);
		mSeekBarPrice = (SeekBar)view.findViewById(R.id.seek_bar_travel_parking_price);
		mSeekBarPrice.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setParkingPrice(seekBar);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setParkingPrice(seekBar);
			}			
		});
		mTextViewParkingPrice = (TextView)view.findViewById(R.id.text_view_travel_parking_price);
		mViewNoParking = (TextView) view.findViewById(R.id.text_view_no_parking);
		mViewParkingDurationContainer = view.findViewById(R.id.layout_parking_duration_container);
		mTextViewParkingDuration = (TextView)view.findViewById(R.id.text_view_travel_parking_duration_value);
		mCheckBoxFree = (CheckBox)view.findViewById(R.id.checkbox_travel_parking_free_charge);
		mCheckBoxFree.setOnCheckedChangeListener(this);
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

	private void toggleUIComponent(final boolean enable){
		mSpinnerPayment.setEnabled(enable);
		mSpinnerParkingPlace.setEnabled(enable);
		mButtonStop.setEnabled(enable);
		mButtonPlay.setEnabled(enable);
		mSpinnerMode.setEnabled(enable);
		mSpinnerModePerson.setEnabled(enable);
		mSeekBarPrice.setEnabled(enable);
		mCheckBoxFree.setEnabled(enable);
	}
	
	private void refreshUI(){
		ActionEvent parking = null;
		ArrayList<ActionEvent> parkingList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
		for(ActionEvent ae : parkingList){
			if(getString(R.string.travel_parking).equals(ae.getActionEventName())){
				parking = ae;
				break;
			}
		} 
		refreshSpinners();	
		if(parking == null){
			setParkingPrice(mSeekBarPrice);
			mViewNoParking.setVisibility(View.VISIBLE);
			mViewParkingDurationContainer.setVisibility(View.GONE);
		}else{
			toggleUIComponent(false);
			final String parkingInfo = mApp.getParkingInfo();
			if(!TextUtils.isEmpty(parkingInfo)){
				try {
					JSONObject object = new JSONObject(parkingInfo);
					if(object.isNull("message")){
						fillValue(object);
					}else{
						fillValue(object.getJSONObject("message")); 
					}
				} catch (JSONException e) {
				}
			}
			mButtonStop.setEnabled(true);
			mViewNoParking.setVisibility(View.GONE);
			mViewParkingDurationContainer.setVisibility(View.VISIBLE);
		}
	}

	private void fillValue(JSONObject data)throws JSONException{
		if(!data.isNull("mode")){
			fillSpinnerValue(data.getString("mode"), mSpinnerMode);
		}
		if(!data.isNull("persons")){
			fillSpinnerValue(data.getString("persons"), mSpinnerModePerson);
		}
		if(!data.isNull("place")){
			fillSpinnerValue(data.getString("place"), mSpinnerParkingPlace);
		}
		
		if(!data.isNull("price")){
			String price = data.getString("price");
			try{
				final int p = Integer.valueOf(price);
				mSeekBarPrice.setProgress(p);
				if(p <= 0){
					mCheckBoxFree.setChecked(true);
					return;
				}				
			}catch(Exception e){
			}
		}
		if(!data.isNull("payment")){
			fillSpinnerValue(data.getString("payment"), mSpinnerPayment);
		}
	}

	private void fillSpinnerValue(final String value, final Spinner s){
		if(TextUtils.isEmpty(value) || (s == null)) return;
		for(int i = 0; i < s.getCount(); i++){
			String item = s.getItemAtPosition(i).toString();
			if((!TextUtils.isEmpty(item)) && value.equals(item)){
				s.setSelection(i, false);
				break;
			}
		}		
	}
	
	private void refreshSpinners(){
		refreshModeSpinner();
		refreshParkingPlaceSpinner();
		refreshPaymentMethodSpinner();
	}
	
	private void refreshParkingPlaceSpinner(){
		String[] arrayPlace = getResources().getStringArray(R.array.travel_parking_place);
		ArrayList<String> listPlace = new ArrayList<String>(Arrays.asList(arrayPlace));
		ArrayAdapter<String> dataAdapterPlace = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listPlace);
		mSpinnerParkingPlace.setAdapter(dataAdapterPlace);
	}
	
	private void refreshPaymentMethodSpinner(){
		String[] arrayPayment = getResources().getStringArray(R.array.travel_parking_paymeny_method);
		ArrayList<String> listPayment = new ArrayList<String>(Arrays.asList(arrayPayment));
		ArrayAdapter<String> dataAdapterPayment = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listPayment);
		mSpinnerPayment.setAdapter(dataAdapterPayment);
	}
	
	private void refreshModeSpinner(){
		// mode
		String[] arrayMode = getResources().getStringArray(R.array.travel_mode_array);
		ArrayList<String> listMode = new ArrayList<String>(Arrays.asList(arrayMode));
		// remove the last one (add custom item)
		listMode.remove(listMode.size() - 1); 
		// load the saved data, if any
		ArrayList<String> modeTempList = TravelApp.getStringToList(mApp.getTravelModes());
		if((modeTempList != null) && (!modeTempList.isEmpty())){
			for(String m : modeTempList){
				listMode.add(listMode.size() - 1, m);
			}
		}
		ArrayAdapter<String> dataAdapterMode = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listMode);
		mSpinnerMode.setAdapter(dataAdapterMode);
		
		// hard-code for person
		String[] arrayPerson = {"1", "2", "3", "4", "5", "6", "7", "8"};
		ArrayList<String> listPerson = new ArrayList<String>(Arrays.asList(arrayPerson));
		ArrayAdapter<String> dataAdapterPerson = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
				R.layout.spinner_item, listPerson);
		mSpinnerModePerson.setAdapter(dataAdapterPerson);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if(parent != null){
			final String itemName = parent.getItemAtPosition(position).toString();
			final int viewId = parent.getId();
			if(viewId == R.id.spinner_travel_transport_mode_list){
				mCurrentMode = itemName;
			}else if(viewId == R.id.spinner_travel_parking_payment_list){
				mPaymentMethod = itemName;
			}else if(viewId == R.id.spinner_travel_parking_place_list){
				mParkingPlace = itemName;
			}else if(viewId == R.id.spinner_travel_transport_mode_person_list){
				mCurrentPersonNumber = itemName;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onClick(View v) {
		if(v != null){
			final int viewId = v.getId();
			if(viewId == R.id.image_button_travel_parking_play){
				startParkingEvent();
			}else if(viewId == R.id.image_button_travel_parking_stop){
				stopParkingEvent();
			}			
		}
	}
	
	private void startParkingEvent(){
		toggleUIComponent(false);
		ActionEvent ae = new ActionEvent(getString(R.string.travel_parking), System.currentTimeMillis());
		ae.setState(EventState.START);
		ActionEventHandler.getInstance().insert(getSherlockActivity().getApplicationContext(), ae);	

		HashMap<String, String> userMsg = new HashMap<String,String>();
		
		if(!TextUtils.isEmpty(mCurrentMode)){
			userMsg.put("mode", mCurrentMode);
			mCurrentMode = null;
		}else{
			userMsg.put("mode", mSpinnerMode.getItemAtPosition(0).toString());
		}
		
		if(!TextUtils.isEmpty(mCurrentPersonNumber)){
			userMsg.put("persons", mCurrentPersonNumber);
			mCurrentPersonNumber = null;
		}else{
			userMsg.put("persons", mSpinnerModePerson.getItemAtPosition(0).toString());
		}

		if(!TextUtils.isEmpty(mParkingPlace)){
			userMsg.put("place", mParkingPlace);
			mParkingPlace = null;
		}else{
			userMsg.put("place", mSpinnerParkingPlace.getItemAtPosition(0).toString());
		}			

		if(mCheckBoxFree.isChecked()){
			userMsg.put("price", "0");			
		}else{
			if(!TextUtils.isEmpty(mPaymentMethod)){
				userMsg.put("payment", mPaymentMethod);
				mPaymentMethod = null;
			}else{
				userMsg.put("payment", mSpinnerPayment.getItemAtPosition(0).toString());
			}
			userMsg.put("price", String.valueOf(mSeekBarPrice.getProgress()));			
		}
		notifyEvent(ae.getMessagePayload(), userMsg);	
		refreshUI();
	}
	
	private void notifyEvent(final String payload, HashMap<String, String> msg){
		if(!TextUtils.isEmpty(payload)){
			Intent intent = new Intent();
			intent.setAction(CUSTOM_INTENT_ACTION);
			intent.putExtra("APPLICATION_ACTION", payload);
			if((msg != null) && (!msg.isEmpty())){
				String appData = null;
				try {
					appData = TravelApp.getFormattedJsonObject(msg, "message").toString();	
				}catch(Exception e){
				}
				if(!TextUtils.isEmpty(appData)){
					intent.putExtra("APPLICATION_DATA", appData); 
					mApp.saveParkingInfo(appData);
				}
			}
			getSherlockActivity().sendBroadcast(intent);
		} else {
			mApp.showToastMessage(R.string.client_error);
		}
	}
	
	private void stopParkingEvent(){
		toggleUIComponent(false);
		ArrayList<ActionEvent> stopList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
		for(ActionEvent ae : stopList){
			if(getString(R.string.travel_parking).equals(ae.getActionEventName())){
				ae.confirmBreakTimestamp();
				ae.setState(EventState.STOP);
				ActionEventHandler.getInstance().update(getSherlockActivity().getApplicationContext(), ae);
				notifyEvent(ae.getMessagePayload(), null);
				mTextViewParkingDuration.setText("");
				break;
			}
		} 
		mListener.onFragmentChanged(R.layout.travel_diary, null);
	}
	
	private void setParkingPrice(final SeekBar seekBar){
		if((seekBar != null) && (mTextViewParkingPrice != null)){
			mTextViewParkingPrice.setText(String.format(Locale.getDefault(), 
					getString(R.string.travel_parking_price_value), String.valueOf(seekBar.getProgress())));
		}		
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mSeekBarPrice.setEnabled(!isChecked);
		mSpinnerPayment.setEnabled(!isChecked);
		if(isChecked){
			mSeekBarPrice.setProgress(0);
		}
	}

}
