package com.clear.helpers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

public class InfoAplicaciones extends Thread{
	private Handler handler;
	private List<PackageInfo> packs;
	private Activity activity;
	public static double totalespacioOcupado;
	
	public InfoAplicaciones(Activity activity, List<PackageInfo> packs){
		this.packs    = packs;
		this.activity = activity;
	}
	
	@Override
	public void run() {
	    Message msg        = null;
	    Bundle bundle      = null;
	    PackageManager pm  = activity.getPackageManager();
	    
	    try{
	       for(int i=0;i<packs.size();i++) {
	        	final PackageInfo p = packs.get(i);
	        	Method getPackageSizeInfo = null;
	        	
	        	if ((packs.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1){
	        		   continue ;
	        	   } else{
	        		   
	        		   if(p.packageName.equals(getPackageName())){
	            		    continue;
	            	   }
	        		  
	      	           msg    = new Message();
	                   bundle = new Bundle();
	                   enviarDatosApp(bundle, msg, p.packageName, pm, getPackageSizeInfo, p);
	            }  
	        }
	    }finally{
	    	activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					activity.setProgressBarIndeterminateVisibility(false);
			    } 
	    	 });
	    }
	        
		super.run();
	}

	public void enviarDatosApp(final Bundle bundle, final Message msg, final String paquete, PackageManager pm , Method getPackageSizeInfo, final PackageInfo p){
		try {
				getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
				getPackageSizeInfo.invoke(pm, paquete,
				    new IPackageStatsObserver.Stub() {
				        @Override
				        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				            throws RemoteException {
				        
				        	InfoApp app = new InfoApp();
				        	
				        	totalespacioOcupado += ((double)pStats.codeSize + (double)pStats.cacheSize);
				        	double espacio       = ((double)pStats.codeSize + (double)pStats.cacheSize);
				        	app.setIcon(p.applicationInfo.loadIcon(activity.getPackageManager()));
				        	app.setAppname(p.applicationInfo.loadLabel(activity.getPackageManager()).toString());
				        	app.setPname(p.packageName);
				        	app.setVersionName(p.versionName);
				        	app.setFechaInstalled(fechaAppInstalada(p.applicationInfo.sourceDir));
				        	app.setInstallSize(calculateSize((double) pStats.codeSize));
				        	app.setCacheApp(calculateSize((double) pStats.cacheSize));
				        	app.setDataApp(calculateSize((double) pStats.dataSize));
				        	app.setEspacioOcupadoCacheCodigo(calculateSize(espacio));
			
				        	bundle.putParcelable("info", app);
			                msg.setData(bundle);
			                handler.sendMessage(msg);
     
				        }});
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	@SuppressLint("SimpleDateFormat")
	public String fechaAppInstalada(String ruta){
			File file = new File(ruta);
			long inst = file.lastModified();
			Date fec  = new Date(inst);
			
			SimpleDateFormat formato  = new SimpleDateFormat("dd/MM/yyyy");
		
			Calendar c = Calendar.getInstance();
			String fechaSistema = formato.format(c.getTime());
			String fechaApp     = formato.format(fec);
			
			if(fechaSistema.equals(fechaApp)){
			   return "Hoy";	
			}
			
		return formato.format(fec);
	}
	
	public String calculateSize(double size) {
		
		 String unit = "Bytes";
		 double sizeInUnit = 0d;

	         if (size > 1024 * 1024 * 1024) { // Gigabyte
	            sizeInUnit = size / (1024 * 1024 * 1024);
	            unit = "GB";
	         } else if (size > 1024 * 1024) { // Megabyte
	            sizeInUnit = size / (1024 * 1024);
	            unit = "MB";
	         } else if (size > 1024) { // Kilobyte
	            sizeInUnit = size / 1024;
	            unit = "KB"; 
	         } else { // Byte
	            sizeInUnit = size;
	         }
		   
		 // only show two digits after the comma
		 return new DecimalFormat("###.##").format(sizeInUnit) + " " + unit;
	}
	
    private String getPackageName(){
        String pkgName = "";
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            pkgName = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pkgName;
    }

	     
	
}
