package org.sizzlelab.contextlogger.android.travel;

import java.util.Locale;

import org.sizzlelab.contextlogger.android.R;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.widget.AdapterView;
import com.WazaBe.HoloEverywhere.widget.AdapterView.OnItemSelectedListener;
import com.WazaBe.HoloEverywhere.widget.SeekBar;
import com.WazaBe.HoloEverywhere.widget.SeekBar.OnSeekBarChangeListener;
import com.WazaBe.HoloEverywhere.widget.Spinner;
import com.actionbarsherlock.app.SherlockFragment;

public class TravelParkingPanelFragement extends SherlockFragment implements OnClickListener, OnItemSelectedListener, OnSeekBarChangeListener{

	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {
			mHandler.postDelayed(mTimedTask , 500);				
		}
	};
	
	private Spinner mSpinnerParkingPlace = null;
	private Spinner mSpinnerPayment = null;
	private Spinner mSpinnerMode = null;
	private Spinner mSpinnerModePerson = null;
	
	private ImageButton mButtonPlay = null;
	private ImageButton mButtonStop = null;
	
	private SeekBar mSeekBarPrice = null;

	private TravelApp mApp = null;
	
	private View mViewNoParking = null;
	private View mViewParkingDurationContainer = null;
	private TextView mTextViewParkingDuration = null;
	private TextView mTextViewParkingPrice = null;
	
	public TravelParkingPanelFragement(){
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
		mSeekBarPrice.setOnSeekBarChangeListener(this);
		mTextViewParkingPrice = (TextView)view.findViewById(R.id.text_view_travel_parking_price);
		mViewNoParking = (TextView) view.findViewById(R.id.text_view_no_parking);
		mViewParkingDurationContainer = view.findViewById(R.id.layout_parking_duration_container);
		mTextViewParkingDuration = (TextView)view.findViewById(R.id.text_view_travel_parking_duration_value);
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
		setParkingPrice(mSeekBarPrice);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if(parent != null){
			final String itemName = parent.getItemAtPosition(position).toString();
			final int viewId = parent.getId();
			if(viewId == R.id.spinner_travel_transport_mode_list){
				
			}else if(viewId == R.id.spinner_travel_parking_payment_list){
				
			}else if(viewId == R.id.spinner_travel_parking_place_list){
				
			}else if(viewId == R.id.spinner_travel_transport_mode_person_list){
				
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
				
			}else if(viewId == R.id.image_button_travel_parking_stop){
				
			}			
		}
	}

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
	
	private void setParkingPrice(final SeekBar seekBar){
		if((seekBar != null) && (mTextViewParkingPrice != null)){
			mTextViewParkingPrice.setText(String.format(Locale.getDefault(), 
					getString(R.string.travel_parking_price_value), String.valueOf(seekBar.getProgress())));
		}		
	}

}
