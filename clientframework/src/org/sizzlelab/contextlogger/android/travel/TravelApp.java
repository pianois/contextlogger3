package org.sizzlelab.contextlogger.android.travel;

import java.util.ArrayList;
import java.util.Arrays;

import org.sizzlelab.contextlogger.android.ClientApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class TravelApp extends ClientApp {

	private static TravelApp mInstance = null;
	private static final String PREFS_INDICATOR = "TravelPrefs";
	
	
	public static TravelApp getInstance(){
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public synchronized String getTravelModes(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelMode", null);
	}
	
	public synchronized void saveTravelMode(String mode) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelMode", mode);
		editor.commit();
	}

	public synchronized String getTravelPurposes(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelPurpose", null);
	}
	
	public synchronized void saveTravelPurpose(String purpose) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelPurpose", purpose);
		editor.commit();
	}
	
	public synchronized String getTravelDestinations(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelDestination", null);
	}
	
	public synchronized void saveTravelDestination(String destination) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelDestination", destination);
		editor.commit();
	}
	
	public synchronized String getTravelReasons(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelReason", null);
	}
	
	public synchronized void saveTravelReason(String reason) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelReason", reason);
		editor.commit();
	}

	
	public synchronized int getTravelStatus(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getInt("travelStatus", -1);
	}
	
	public synchronized void saveTravelStauts(int travelStatusEnumIndex) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("travelStatus", travelStatusEnumIndex);
		editor.commit();
	}	

	public static ArrayList<String> getStringToList(final String array){
		ArrayList<String> list = null;
		if(TextUtils.isEmpty(array)){
			return null;
		}
		try{
			String[] strArray = array.split(";");
			list = new ArrayList<String>(Arrays.asList(strArray));
		}catch(Exception e){
			return list;
		}
		return list;
	}
	
}
