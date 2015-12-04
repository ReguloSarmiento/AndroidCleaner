package com.clear.helpers;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.provider.CallLog;

public class InfoLlamadas {

	private Activity activity;
	private String id          = "";
	private String numero      = "";
	private String duracion    = "";
	private ArrayList<InfoLlamadas> llamadasRecibidas = new ArrayList<InfoLlamadas>();
	private ArrayList<InfoLlamadas> llamadasSalientes = new ArrayList<InfoLlamadas>();
	private ArrayList<InfoLlamadas> llamadasPerdidas  = new ArrayList<InfoLlamadas>();


	public InfoLlamadas(Activity activity){
		this.activity = activity;
	}
	
	public InfoLlamadas(String id, String numero,String duracion){
		this.id       = id;
		this.numero   = numero;
		this.duracion = duracion;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public String getDuracion() {
		return duracion;
	}
	
	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	public ArrayList<InfoLlamadas> getLlamadasRecibidas() {
		return llamadasRecibidas;
	}

	public void setLlamadasRecibidas(ArrayList<InfoLlamadas> llamadasRecibidas) {
		this.llamadasRecibidas = llamadasRecibidas;
	}

	public ArrayList<InfoLlamadas> getLlamadasSalientes() {
		return llamadasSalientes;
	}

	public void setLlamadasSalientes(ArrayList<InfoLlamadas> llamadasSalientes) {
		this.llamadasSalientes = llamadasSalientes;
	}

	public ArrayList<InfoLlamadas> getLlamadasPerdidas() {
		return llamadasPerdidas;
	}

	public void setLlamadasPerdidas(ArrayList<InfoLlamadas> llamadasPerdidas) {
		this.llamadasPerdidas = llamadasPerdidas;
	}
	
	public void obtenerDetallesLlamadas(){
		@SuppressWarnings("deprecation")
		Cursor managedCursor = activity.managedQuery(CallLog.Calls.CONTENT_URI, null,null, null, null);
		
		if(managedCursor != null){
			  int number   = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
			  int type     = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
			  int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
			  int id       = managedCursor.getColumnIndex(CallLog.Calls._ID);
			  
			  while (managedCursor.moveToNext()) {
				   
				    String callType   = managedCursor.getString(type);
				    String ID         = managedCursor.getString(id);
				    String numero     = managedCursor.getString(number);
				    String duracion   = managedCursor.getString(duration);
				    
				        InfoLlamadas infoCall = new InfoLlamadas(ID,numero,duracion);
					    if(Integer.parseInt(callType) == CallLog.Calls.INCOMING_TYPE){
					    	this.llamadasRecibidas.add(infoCall);
					    }else if(Integer.parseInt(callType) == CallLog.Calls.OUTGOING_TYPE){
					    	this.llamadasSalientes.add(infoCall);
					    }else if(Integer.parseInt(callType) == CallLog.Calls.MISSED_TYPE){
					    	this.llamadasPerdidas.add(infoCall);
					    }    
			  }
			  //managedCursor.close();
			  
			  this.setLlamadasRecibidas(llamadasRecibidas);
			  this.setLlamadasSalientes(llamadasSalientes);
			  this.setLlamadasPerdidas(llamadasPerdidas);
	   }  
	}
	
	public void eliminarRegistrosLlamadas( ArrayList<InfoLlamadas> llamadas ){
		  for(int i=0; i < llamadas.size(); i++){
			  activity.getContentResolver().delete(android.provider.CallLog.Calls.CONTENT_URI,"_ID = "+ llamadas.get(i).getId(), null);
		  }
	}
	
}
