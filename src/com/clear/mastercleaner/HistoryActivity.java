package com.clear.mastercleaner;


import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Browser;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.clear.adapters.AdapterHistory;
import com.clear.helpers.EntryItem;
import com.clear.helpers.InfoCache;
import com.clear.helpers.Item;
import com.clear.helpers.SectionItem;
import com.shauvinoni.mastercleaner.R;


@SuppressWarnings("deprecation")
public class HistoryActivity extends ActionBarActivity implements OnItemClickListener{
    private ArrayList<Item> items = new ArrayList<Item>();
    private ListView listview     = null;
    private Button   limpiar      = null;
    private AdapterHistory adapter;
    private AlertDialog.Builder builder;
    private AlertDialog.Builder builde;
    private Activity activity;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
	
		activity = this;
		limpiar  = (Button)   findViewById(R.id.limpiar);
        listview = (ListView) findViewById(R.id.listView_main);
        limpiar  = (Button)   findViewById(R.id.limpiar);
        
        barraTitulo();
        botonlimpiar();
        informacionParaListview();

   
   }   

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
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
		actionbar.setIcon(R.drawable.history_icon);
		actionbar.setTitle(R.string.history);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_actionbar));
	}
	
	public void informacionParaListview(){
		items.add(new SectionItem(getResources().getString(R.string.browser)));
		items.add(new EntryItem(getResources().getString(R.string.browser), String.valueOf(numeroPaginasVisitadas())+" Pag", R.drawable.browser ));
		
		items.add(new SectionItem(getResources().getString(R.string.download)));
		items.add(new EntryItem(getResources().getString(R.string.download), espacioDescargas(), R.drawable.downloads ));
		items.add(new EntryItem(getResources().getString(R.string.files), espacioArchivos(), R.drawable.list_icon_media_files ));
		
		items.add(new SectionItem(getResources().getString(R.string.aplicacion)));
		items.add(new EntryItem(getResources().getString(R.string.clipboard), tamanoPortapapeles(), R.drawable.clipboard ));
			
        adapter = new AdapterHistory(this, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
	}

	public int numeroPaginasVisitadas(){
		String[] proj = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
        String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
		Cursor mCur = this.managedQuery(Browser.BOOKMARKS_URI, proj, sel, null, null);
        this.startManagingCursor(mCur);
        mCur.moveToFirst();
        int contador = 0;

        if (mCur.moveToFirst() && mCur.getCount() > 0) {
        	while (mCur.isAfterLast() == false) {
              contador +=1; 
              mCur.moveToNext();
           }
       }
	   return contador;
	}
	
	public String espacioDescargas(){
		File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	    long tamanoDescarga = 0;
		if(file != null){
			if(file.listFiles()!= null){
				for(File f :file.listFiles()){
					if(!f.isDirectory()){
						tamanoDescarga += f.length();
					}	
				}
			}
		}
		return new InfoCache().calculateSize(tamanoDescarga);
	}
	
	public void DeleteEspacioDescargas(File file) {	
       if (!file.isDirectory()){
    	   file.delete();
       }   
    }
	
	public String espacioArchivos(){
		  File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		  long tamanoDescarga = 0;
	      if(file != null){
			if(file.listFiles()!= null){
				for(File f :file.listFiles()){
					if(f.isDirectory()){
						for(File d : f.listFiles()){
							tamanoDescarga += d.length();
						}
					}	
				}
			 }
		  }
		  return new InfoCache().calculateSize(tamanoDescarga);
	}
	
	public void DeleteEspacioArchivos(File file) {
	       if (file.isDirectory()){
	          for (File child : file.listFiles()){
	        	  DeleteEspacioArchivos(child);
	          }
	       }
	       file.delete();
	}
	

	public String tamanoPortapapeles(){
		final ClipboardManager portapapeles = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if(portapapeles != null && portapapeles.hasText()){
			return new InfoCache().calculateSize(portapapeles.getText().length());
		  }else{
			return "0 Bytes";
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		  EntryItem item = (EntryItem) items.get(position);
		  CheckBox box   = (CheckBox)view.findViewById(R.id.checkBox1);
		  
		  if(item.title.equals(getResources().getString(R.string.files))){
			  if(!item.isSelected()){
				  mensajeAlerta(item, box);
			  }else{
				  item.setSelected(false);
				  box.setChecked(item.isSelected());
			  }
		  }else{
			  if(!item.isSelected()){
			      item.setSelected(true);
			      box.setChecked(item.isSelected());
			   }else{
				  item.setSelected(false);
				  box.setChecked(item.isSelected());
			  }
		  }
		  
		
	}
		
	@SuppressLint("NewApi")
	public void mensajeAlerta(final EntryItem item, final CheckBox box){
		try 
		{
		      builder =  new AlertDialog.Builder(this,R.style.DialogCustomTheme);
		    }catch (NoSuchMethodError e) {
		      builder =  new AlertDialog.Builder(this);
		}
		
		builder.setTitle(R.string.doesmedia);
		builder.setIcon(R.drawable.alerta);
		builder.setMessage(getResources().getString(R.string.itwillremove)+"\n\n"+getResources().getString(R.string.cannot))
		
		.setPositiveButton(R.string.incluir, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
  		      item.setSelected(true);
  		      box.setChecked(item.isSelected()); 
            }
         })
                
       .setNegativeButton(R.string.excluir, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
  			   item.setSelected(false);
  			   box.setChecked(item.isSelected());	
               dialog.dismiss();
            }
         });
   
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void botonlimpiar(){
		limpiar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean haySeleccion = false;
				for(int i=0; i<items.size(); i++){
					if(!items.get(i).isSection()){
						EntryItem ite = (EntryItem) items.get(i);
						if(ite.isSelected()){
							haySeleccion = true;
						}
					}
				}
				
				if(haySeleccion){
				     new LimpiarDatos(activity, items).execute();
				  }else{
					 Toast.makeText(getApplicationContext(), R.string.need, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	public class LimpiarDatos extends AsyncTask<Void,Void,Boolean>{
		private Activity activity;
		private ArrayList<Item> items;
		ProgressDialog	dialogo;
		boolean browserSeleccionado;
		boolean descargaSeleccionado;
		boolean archivosSeleccionado;
		boolean portapapelesSele;
		String  espacioArchivos;
		String  espacioDescarga;

		
		public LimpiarDatos(Activity activity, ArrayList<Item> items){
			this.activity = activity;
			this.items = items;
			this.browserSeleccionado = false;
		}
		
		@Override
		protected void onPreExecute() {
	    	try 
	    	{
	    	     dialogo = new ProgressDialog(activity,R.style.DialogCustomTheme);
		       }catch (NoSuchMethodError e) {
		         dialogo = new ProgressDialog(activity);
		    }
	    	
			dialogo.setMessage(getResources().getString(R.string.cleaning)+"...");
			dialogo.show();
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			Looper.prepare();
			for(int i=0; i<items.size(); i++){
				if(!items.get(i).isSection()){
					EntryItem ite = (EntryItem) items.get(i);
					if(ite.isSelected()){
						if(ite.title.equals(getResources().getString(R.string.browser))){
							this.browserSeleccionado = true;
							Browser.clearHistory(getContentResolver());
		                	ite.setCantidad("0 Pag");
		                	ite.setSelected(false);
						}else if(ite.title.equals(getResources().getString(R.string.download))){
							descargaSeleccionado = true;
							espacioDescarga = espacioDescargas();
							File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
							for(File f :file.listFiles()){
							  DeleteEspacioDescargas(f);							
							}
		                	ite.setCantidad("0 Bytes");
		                	ite.setSelected(false);
						}else if(ite.title.equals(getResources().getString(R.string.files))){
							archivosSeleccionado = true;
							espacioArchivos = espacioArchivos();
							File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
							for(File f :file.listFiles()){
							  DeleteEspacioArchivos(f);
							}
		                	ite.setCantidad("0 Bytes");
		                	ite.setSelected(false);
						}else if(ite.title.equals(getResources().getString(R.string.clipboard))){
							portapapelesSele = true;
							final ClipboardManager portapapeles = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							portapapeles.setText("");
		                	ite.setCantidad("0 Bytes");
		                	ite.setSelected(false);
						}
					}
				}
			}
			return true;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialogo.dismiss();
			adapter.notifyDataSetChanged();
			
			try 
			{
			      builde =  new AlertDialog.Builder(activity,R.style.DialogCustomTheme);
			    }catch (NoSuchMethodError e) {
			      builde =  new AlertDialog.Builder(activity);
			}
			
			builde.setTitle(R.string.Yourdevice);
			builde.setIcon(R.drawable.dialog_clean_icon);
			
			String mensaje="";
			String m1 = "", m2 = "", m3 = "",m4 = "";

			if(this.browserSeleccionado){
				m1 = msjBrowser();
			}
			
			if(this.descargaSeleccionado){
				m2 = msjDescarga(espacioDescarga);
			}
			
			if(this.archivosSeleccionado){
				m3 = msjArchivos(espacioArchivos);
			}
			
			if(this.portapapelesSele){
				m4 = msjPortapales();
			}
			
			mensaje = (m1 + m2 + m3 + m4);
		    builde.setMessage(mensaje);
			builde
			 .setCancelable(false)
			 .setNeutralButton(R.string.aceptar,
					 new DialogInterface.OnClickListener() {
			     		public void onClick(DialogInterface dialog,int id) {
			     			dialog.cancel();
			     		}
			   		 });
			 AlertDialog alertDialog = builde.create();
			 alertDialog.show();
		}	
	}
	
	public String msjBrowser(){
		return "✔ "+getResources().getString(R.string.itcleared)+"\n\n";
	}
	
	public String msjDescarga(String espacio){
		return "✔ "+getResources().getString(R.string.removido)+" " +espacio+ " "+getResources().getString(R.string.ofd)+"\n\n";
	}
	
	public String msjArchivos(String espacio){
		return "✔ "+getResources().getString(R.string.removido)+" " +espacio+ " "+getResources().getString(R.string.omf)+"\n\n";
	}
	
	public String msjPortapales(){
		return "✔ "+getResources().getString(R.string.itclearedclipboard);
	}

	
	
}
