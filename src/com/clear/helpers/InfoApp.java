package com.clear.helpers;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class InfoApp implements Parcelable{
	
	private Drawable icon;
	private String   total;
    private String   pname;
    private String   dataApp;
    private String   appname;
    private String   cacheApp;
    private boolean  isDelete;
    private String   installDir;
    private boolean  isSelected;
    private String   versionName;
    private String   installSize;
    private String   fechaInstalled;
    private String   espacioOcupadoCacheCodigo;
    
    public InfoApp(){}
    
	public String getAppname() {
		return appname;
	}
	
	public void setAppname(String appname) {
		this.appname = appname;
	}
	
	public String getPname() {
		return pname;
	}
	
	public void setPname(String pname) {
		this.pname = pname;
	}
	
	public String getVersionName() {
		return versionName;
	}
	
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Drawable getIcon() {
		return icon;
	}
	
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public String getInstallDir() {
		return installDir;
	}

	public void setInstallDir(String installDir) {
		this.installDir = installDir;
	}

	public String getInstallSize() {
		return installSize;
	}

	public void setInstallSize(String installSize) {
		this.installSize = installSize;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	
	public String getFechaInstalled() {
		return fechaInstalled;
	}

	public void setFechaInstalled(String fechaInstalled) {
		this.fechaInstalled = fechaInstalled;
	}
	
	public String getCacheApp() {
		return cacheApp;
	}
	
	public String getEspacioOcupadoCacheCodigo() {
		return espacioOcupadoCacheCodigo;
	}

	public void setEspacioOcupadoCacheCodigo(String espacioOcupadoCacheCodigo) {
		this.espacioOcupadoCacheCodigo = espacioOcupadoCacheCodigo;
	}

	public void setCacheApp(String cacheApp) {
		this.cacheApp = cacheApp;
	}

	public String getDataApp() {
		return dataApp;
	}

	public void setDataApp(String dataApp) {
		this.dataApp = dataApp;
	}

	public int numeroDeAplicaciones(ArrayList<InfoApp> app){
		return app.size();
	}
		
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("deprecation")
	public InfoApp(Parcel in){
		Bitmap bitmap    = (Bitmap)in.readParcelable(getClass().getClassLoader());
		this.icon        = new BitmapDrawable(bitmap);
		this.total       = in.readString();
		this.appname     = in.readString();
		this.pname       = in.readString();
		this.cacheApp    = in.readString();
		this.dataApp     = in.readString();
		this.installSize = in.readString();
		this.versionName    = in.readString();
		this.fechaInstalled = in.readString();
		this.espacioOcupadoCacheCodigo = in.readString();
		
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		 out.writeValue((Bitmap)((BitmapDrawable) this.icon).getBitmap());
		 out.writeString(this.total);
		 out.writeString(this.pname);
		 out.writeString(this.appname);
		 out.writeString(this.versionName);
		 out.writeString(this.fechaInstalled);
		 out.writeString(this.installSize);
		 out.writeString(this.cacheApp);
		 out.writeString(this.dataApp);
		 out.writeString(this.espacioOcupadoCacheCodigo);
	}
	
	public static final Parcelable.Creator<InfoApp> CREATOR = new Parcelable.Creator<InfoApp>() {
		@Override
		public InfoApp createFromParcel(Parcel source) {
			return new InfoApp(source);
		}

		@Override
		public InfoApp[] newArray(int size) {
			return new InfoApp[size];
		}
	 };

}
