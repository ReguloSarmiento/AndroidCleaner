package com.clear.mastercleaner;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.clear.adapters.ApplicationAdapter;
import com.clear.helpers.InfoAplicaciones;
import com.clear.helpers.InfoApp;
import com.clear.helpers.InfoCache;
import com.shauvinoni.mastercleaner.R;

public class ListAppActivity extends ActionBarActivity implements OnItemClickListener{
	static   ArrayList<InfoApp> apps;
	private  ListView lista;
	private  ApplicationAdapter adaptador;
	private  Button desintalar;
	private  AppDeleted detectarAppDelete;
	private  IntentFilter filter;
	private  IntentFilter intentFilter;
	private  InfoAplicaciones app;
	private  Handler  handler;
	private  InfoApp  infoapp;
	private  Activity activity;
	private  TextView numapp; 
	private  TextView espacioOcupado; 
	private  int contador;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_app);
	
		activity       = this;
		lista          = (ListView)   findViewById(R.id.list);
		desintalar 	   = (Button)     findViewById(R.id.button1);
		numapp     	   = (TextView)   findViewById(R.id.numeroapp);
		espacioOcupado = (TextView) findViewById(R.id.cantidad);
		setProgressBarIndeterminateVisibility(true);
		
		lista.setOnItemClickListener(this);
		barraTitulo();
		cargarAplicaciones();
		iniciarHilo();
		desintalarApps();
		
		// Detecta si una aplicacion ha sido desintalada.
		detectarAppDelete = new AppDeleted();
		filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_DELETE);
		filter.addDataScheme("package"); 
		registerReceiver(detectarAppDelete, filter); 
		
		// Permite remover de la lista la aplicacion que ha sido desintalada.
		intentFilter = new IntentFilter();
        intentFilter.addAction("QUITAR");
        this.registerReceiver(intentReceiver, intentFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_app, menu);
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
		actionbar.setIcon(R.drawable.app_manager_icon);
		actionbar.setTitle(R.string.manager);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_actionbar));
	}
	
	
	public void desintalarApps(){
		desintalar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			   boolean flag = false;
               for(int i = 0; i < apps.size(); i++){
            	   if(apps.get(i).isSelected()){
            		   Intent intent = new Intent(Intent.ACTION_DELETE);
             	       intent.setData(Uri.parse("package:" + apps.get(i).getPname()));
             	       startActivity(intent);
             	       flag=true;
            	   }
               }
               
               if(!flag){
            	  Toast.makeText(getApplicationContext(), R.string.needselect, Toast.LENGTH_LONG).show();
               }
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
		  AlertDialog.Builder builder =  new AlertDialog.Builder(this);
		   try 
			{
			      builder =  new AlertDialog.Builder(activity,R.style.DialogCustomTheme);
			    }catch (NoSuchMethodError e) {
			      builder =  new AlertDialog.Builder(activity);
			}
			
		   builder.setTitle(apps.get(position).getAppname());
		   builder.setIcon(apps.get(position).getIcon()); 
		     
	       String msg = 
	    	           "Version: "+ apps.get(position).getVersionName() +"\n"+
	    	           "Date:    "+ apps.get(position).getFechaInstalled() +"\n"+ 
	    	           "Occupied:"+ apps.get(position).getInstallSize();
	    	 
	    	      
		   builder.setMessage(msg)
	       .setPositiveButton(getResources().getString(R.string.uninstall), new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	      Intent intent = new Intent(Intent.ACTION_DELETE);
            	      intent.setData(Uri.parse("package:" + apps.get(position).getPname()));
            	      startActivity(intent);
               }
            })
            
            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   dialog.dismiss();
                }
             });
		
		AlertDialog alert = builder.create();
        alert.show();
	}
	
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			actualizarLista();
		}
	};
	
	public void actualizarLista(){
		for(int i=0; i<apps.size(); i++){
			if(apps.get(i).isDelete()){
				apps.remove(i);
				adaptador.notifyDataSetChanged();
				i--;
			}
		}
	}
	
	public void iniciarHilo(){
		apps = new ArrayList<InfoApp>();
		app  = new InfoAplicaciones(this, getPackageManager().getInstalledPackages(0));
		app.setHandler(handler);
		app.start();
	}
	
	@SuppressLint("HandlerLeak")
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
                infoapp.setPname(i.getPname());
                infoapp.setAppname(i.getAppname());
                infoapp.setDataApp(i.getDataApp());
                infoapp.setCacheApp(i.getCacheApp());
                infoapp.setVersionName(i.getVersionName());
                infoapp.setInstallSize(i.getInstallSize());
                infoapp.setFechaInstalled(i.getFechaInstalled());
                infoapp.setEspacioOcupadoCacheCodigo(i.getEspacioOcupadoCacheCodigo());
                
                apps.add(infoapp);
    			adaptador = new ApplicationAdapter( activity, 0, apps );
    			lista.setAdapter(adaptador); 
    			numapp.setText(String.valueOf(contador)); 
    			espacioOcupado.setText(new InfoCache().calculateSize(InfoAplicaciones.totalespacioOcupado));
            }
        }; 
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(intentReceiver);
		unregisterReceiver(detectarAppDelete);
		super.onDestroy();
	}
	
}
