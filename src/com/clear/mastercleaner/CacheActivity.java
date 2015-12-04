package com.clear.mastercleaner;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clear.adapters.AdapterCacheApp;
import com.clear.helpers.InfoApp;
import com.clear.helpers.InfoCache;
import com.shauvinoni.mastercleaner.R;

@SuppressLint("HandlerLeak")
public class CacheActivity extends ActionBarActivity implements OnItemClickListener{
    private ListView lista;
    private InfoCache app;
    private AdapterCacheApp adaptador;
    private List<InfoApp> appsList;
    private InfoApp infoapp;
    private Handler  handler;
    private static Activity activity;
    private Button limpiarCache;
    private AlertDialog.Builder builder;
    private String  cache;
    private TextView nohaycache;
    private TextView numapp; 
    private TextView espacioOcupado; 
    private int contador;
	private static final String SCHEME = "package";
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	private static final String APP_PKG_NAME_22 = "pkg";
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	private static final String APP_DETAILS_CLASS_NAME   = "com.android.settings.InstalledAppDetails";

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache);
	
		activity     = this;
		lista        = (ListView) findViewById(R.id.list);
		limpiarCache = (Button) findViewById(R.id.button1);
		nohaycache   = (TextView) findViewById(R.id.cache);
		numapp       = (TextView) findViewById(R.id.numeroapp);
		espacioOcupado = (TextView) findViewById(R.id.cantidad);	
		lista.setOnItemClickListener(this);
		
		barraTitulo();
		cargarAplicaciones();
		iniciarHilo();
		limpiarCache();
		setProgressBarIndeterminateVisibility(true);
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cache, menu);
		return true;
	}
	

		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		     case android.R.id.home:
		    	     onBackPressed();
					 startActivity(new Intent(this,MainActivity.class));
		     default:
		             return super.onOptionsItemSelected(item);    
		}
		//return true;
	}
	
	public void barraTitulo(){
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setIcon(R.drawable.cache_icon);
		actionbar.setTitle(R.string.tituloclearcache);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_actionbar));
	}
	
	public void limpiarCache(){
		limpiarCache.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(appsList.size() > 0){
				    new LiberarCache().execute();
				  }else{
					Toast.makeText(getApplicationContext(), R.string.Thereisnot, Toast.LENGTH_LONG).show();
				}
		    }
		 });
	}
	
	public void iniciarHilo(){
		appsList = new ArrayList<InfoApp>();
		app      = new InfoCache(this, getPackageManager().getInstalledPackages(0));
		app.setHandler(handler);
		app.start();
	}
	
	public void cargarAplicaciones(){
		handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	contador += 1;
                Bundle bundle;
                bundle    = msg.getData();
                infoapp   = new InfoApp();
                InfoApp i = bundle.getParcelable("info");
                
                infoapp.setIcon(i.getIcon());
                infoapp.setTotal(i.getTotal());
                infoapp.setPname(i.getPname());
                infoapp.setAppname(i.getAppname());
                infoapp.setDataApp(i.getDataApp());
                infoapp.setCacheApp(i.getCacheApp());
                infoapp.setInstallSize(i.getInstallSize());

                appsList.add(infoapp);
                cache     = new InfoCache().calculateSize(InfoCache.totalCacheALiberar);
    			adaptador = new AdapterCacheApp( activity, 0, appsList );
    			lista.setAdapter(adaptador);

    			nohaycache.setVisibility(View.GONE);
    			numapp.setText(String.valueOf(contador)); 
    			espacioOcupado.setText(cache);
            }
        };   
         
	}
	
	public class LiberarCache extends AsyncTask<Void,Void,Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			 PackageManager  pm = getPackageManager();
             Method[] methods   = pm.getClass().getDeclaredMethods();
             for (Method m : methods) {
                 if (m.getName().equals("freeStorageAndNotify")) {
                     try {
                            long desiredFreeStorage = Long.MAX_VALUE;
                            m.invoke(pm, desiredFreeStorage , null);
                        } catch (Exception e) { }
                     break;
                 }
              }
             
 			  for(int i = 0; i < appsList.size(); i++){
 				appsList.get(i).setCacheApp("0 Bytes");	
			  }
			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			actulizarLista();
			nohaycache.setVisibility(View.VISIBLE);
			numapp.setText("0");
			espacioOcupado.setText("0 Bytes");
			try 
			{
			   builder =  new AlertDialog.Builder(activity,R.style.DialogCustomTheme);
			  }catch (NoSuchMethodError e) {
			   builder =  new AlertDialog.Builder(activity);
			}
			
			builder.setTitle(R.string.Yourdevice);
			builder.setIcon(R.drawable.dialog_clean_icon);
			builder.setMessage(getResources().getString(R.string.removido)+"  "+ cache + "  " +getResources().getString(R.string.ofCache));
			builder
			 .setCancelable(false)
			 .setNeutralButton(R.string.aceptar,
					 new DialogInterface.OnClickListener() {
			     		public void onClick(DialogInterface dialog,int id) {
			     			dialog.cancel();
			     		}
			   		 });
			 AlertDialog alertDialog = builder.create();
			 alertDialog.show();
		 }
	}
	
	public void actulizarLista(){
		for(int i = 0; i < appsList.size(); i++){
			if(appsList.get(i).getCacheApp().equals("0 Bytes")){
				appsList.remove(i);
				i--;
				adaptador.notifyDataSetChanged();
			}
		}
	}

	@SuppressLint("InlinedApi")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		showInstalledAppDetails(this, appsList.get(position).getPname());
	}
	

	@SuppressLint("InlinedApi")
	public static void showInstalledAppDetails(Context contexto,String packageName) {
	    Intent intent = new Intent();
	    final int apiLevel = Build.VERSION.SDK_INT;
	    if (apiLevel >= 9) { // above 2.3
	        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
	        Uri uri = Uri.fromParts(SCHEME, packageName, null);
	        intent.setData(uri);
	    } else { // below 2.3
	        final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
	        intent.putExtra(appPkgName, packageName);
	    }
	    contexto.startActivity(intent);
	}

}
