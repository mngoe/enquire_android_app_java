package com.exact.general;


import java.util.Locale;

import com.exact.CallSoap.CallSoap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;

public class General {
	// IP address of openIMIS demo server
	// todo: please update with your own Web Services base URL
	private String _Domain = "http://demo.openimis.org/";
	public String getDomain(){
		return _Domain;
	}
	
	public int isSDCardAvailable(){
		String State = Environment.getExternalStorageState();
		if (State.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
			return 0;
		}else if(!State.equals(Environment.MEDIA_MOUNTED)){
			return -1;
		}else{
			return 1;
		}
	}
	
	public boolean isNetworkAvailable(Context ctx){
    	ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo ni = cm.getActiveNetworkInfo();
    	
    	return (ni != null && ni.isConnected());
    	
    		
    }

	public void ChangeLanguage(Context ctx,String Language){
		Resources res = ctx.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration config = res.getConfiguration();
        config.locale = new Locale(Language.toLowerCase());
        res.updateConfiguration(config, dm);
	}
	
	public String getVersion(Context ctx, String PackageName){
		String VersionName = "";
		
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(PackageName, 0);
			//int Code = info.versionCode;
			VersionName = info.versionName;
			
						
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			
		}
		return VersionName;
		
	}
	
	public boolean isNewVersionAvailable(String Field,Context ctx, String PackageName){

		CallSoap cs = new CallSoap();
		cs.setFunctionName("GetCurrentVersion");
		String result = cs.GetCurrentVersion(Field);
		if(result.contains(",")) {
			result = result.replaceAll("(\\d+)\\,(\\d+)", "$1.$2");
		}
		return result == ""?false:Float.parseFloat(this.getVersion(ctx, PackageName).toString()) < Float.parseFloat(result);
		
	}
	
	
	}
	
	

