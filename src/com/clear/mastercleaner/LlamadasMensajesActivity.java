package com.clear.mastercleaner;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import com.clear.adapters.AdapterLlamadasMensajes;
import com.clear.helpers.EntryItem;
import com.clear.helpers.InfoLlamadas;
import com.clear.helpers.InfoMensajes;
import com.clear.helpers.Item;
import com.clear.helpers.SectionItem;
import com.shauvinoni.mastercleaner.R;

public class LlamadasMensajesActivity extends ActionBarActivity implements OnItemClickListener{
    private InfoLlamadas llamadas;
    private InfoMensajes mensajes;
    private ArrayList<Item> items = new ArrayList<Item>();
    private ListView listview = null;
    private Button   limpiar = null;
    private Activity activity;
    private AdapterLlamadasMensajes adapter;
    private AlertDialog.Builder builder;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_llamadas_mensajes);
		
		activity = this;
        llamadas = new InfoLlamadas(this);
		mensajes = new InfoMensajes(this);
		limpiar  = (Button)   findViewById(R.id.limpiar);
        listview = (ListView) findViewById(R.id.listView_main);
        barraTitulo();
        informacionParaListview();
        botonlimpiar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.llamadas_mensajes, menu);
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
		actionbar.setIcon(R.drawable.llamadas_mensajes);
		actionbar.setTitle(R.string.callandmessage);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_actionbar));
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		  EntryItem item = (EntryItem) items.get(position);
		  CheckBox box   = (CheckBox)view.findViewById(R.id.checkBox1);
		  
		  if(!item.isSelected()){
		      item.setSelected(true);
		      box.setChecked(item.isSelected());
		   }else{
			  item.setSelected(false);
			  box.setChecked(item.isSelected());
		  }
	}
	
	
	public void informacionParaListview(){
		llamadas.obtenerDetallesLlamadas();
		mensajes.obtenerMensajesRecibidos();
		mensajes.obtenerMensajesEnviados();
		
		items.add(new SectionItem(getResources().getString(R.string.calls)));
        items.add(new EntryItem(getResources().getString(R.string.incomming), String.valueOf(llamadas.getLlamadasRecibidas().size()), R.drawable.call_incoming ));
        items.add(new EntryItem(getResources().getString(R.string.outgoing), String.valueOf(llamadas.getLlamadasSalientes().size()), R.drawable.call_outgoing));
        items.add(new EntryItem(getResources().getString(R.string.missed),  String.valueOf(llamadas.getLlamadasPerdidas().size()),  R.drawable.call_missed));
                
        items.add(new SectionItem(getResources().getString(R.string.messages)));
        items.add(new EntryItem(getResources().getString(R.string.received), String.valueOf(mensajes.getMensajesRecibidos().size()), R.drawable.msj_inbox));
        items.add(new EntryItem(getResources().getString(R.string.sent),  String.valueOf(mensajes.getMensajesEnviados().size()),  R.drawable.msj_sent));
        
        adapter = new AdapterLlamadasMensajes(this, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
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
		private int totalRegistros;
		private int registrosLlamadas;
		private int registrosMensajes;
		ProgressDialog	dialogo;
		
		public LimpiarDatos(Activity activity, ArrayList<Item> items){
			this.activity = activity;
			this.items = items;
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
			int e = 0, s = 0, p = 0,  r = 0, en = 0;
			for(int i=0; i<items.size(); i++){
				if(!items.get(i).isSection()){
					EntryItem ite = (EntryItem) items.get(i);
					if(ite.isSelected()){
		                 if(ite.title.equals(getResources().getString(R.string.incomming))){
		                	e = llamadas.getLlamadasRecibidas().size();
		                	llamadas.eliminarRegistrosLlamadas(llamadas.getLlamadasRecibidas());
		                	ite.setCantidad("0");
		                	ite.setSelected(false);
		                 }else if(ite.title.equals(getResources().getString(R.string.outgoing))){
		                	s = llamadas.getLlamadasSalientes().size();
		                	llamadas.eliminarRegistrosLlamadas(llamadas.getLlamadasSalientes());
		                	ite.setCantidad("0");
		                	ite.setSelected(false);
		                 }else if(ite.title.equals(getResources().getString(R.string.missed))){
		                	p = llamadas.getLlamadasPerdidas().size();
		                	llamadas.eliminarRegistrosLlamadas(llamadas.getLlamadasPerdidas());
		                	ite.setCantidad("0");
		                	ite.setSelected(false);
		                 }else if(ite.title.equals(getResources().getString(R.string.received))){
		                	r = mensajes.getMensajesRecibidos().size();
		                	mensajes.eliminarMensajes(mensajes.getMensajesRecibidos());
		                	ite.setCantidad("0");
		                	ite.setSelected(false);
		                 }else if(ite.title.equals(getResources().getString(R.string.sent))){
		                	en = mensajes.getMensajesEnviados().size();
		                	mensajes.eliminarMensajes(mensajes.getMensajesEnviados());
		                	ite.setCantidad("0");
		                	ite.setSelected(false);
		                 }
					}
				}
			}
			this.registrosMensajes = (r + en);
			this.registrosLlamadas = (e + s + p);
			this.totalRegistros    = (e + s + p + r + en);
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
			      builder =  new AlertDialog.Builder(activity,R.style.DialogCustomTheme);
			    }catch (NoSuchMethodError e) {
			      builder =  new AlertDialog.Builder(activity);
			}
			
			builder.setTitle(getResources().getString(R.string.Yourdevice));
			builder.setIcon(R.drawable.dialog_clean_icon);
			
			if( this.registrosLlamadas != 0 ){
			   builder.setMessage(msjRegistroLlamadas(registrosLlamadas) + getResources().getString(R.string.totallogs)+" "+String.valueOf(this.totalRegistros));
			}
			
			if( this.registrosMensajes != 0 ){
			   builder.setMessage(msjRegistroMensajes(registrosMensajes) + getResources().getString(R.string.totallogs)+" "+String.valueOf(this.totalRegistros));
		    }
			
			if( (this.registrosLlamadas != 0) && (this.registrosMensajes != 0) ){
			   builder.setMessage(msjRegistroLlamadas(registrosLlamadas) + msjRegistroMensajes(registrosMensajes) + getResources().getString(R.string.totallogs)+" "+String.valueOf(this.totalRegistros));
			}
			
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
		
		
		public String msjRegistroLlamadas(int registros){
			return "✔ "+getResources().getString(R.string.deleted)+" "+String.valueOf(registros)+ " " +getResources().getString(R.string.ofcalls)+"\n\n";
		}
		
		public String msjRegistroMensajes(int registros){
			return "✔ "+getResources().getString(R.string.deleted)+" "+String.valueOf(registros)+ " " + getResources().getString(R.string.ofmessages)+"\n\n";
		}
	}
	
}
