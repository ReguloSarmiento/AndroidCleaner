package com.clear.helpers;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

public class InfoMensajes {
   private int id      = 0;
   private String body = "";
   private Activity activity;
   private ArrayList<InfoMensajes> mensajesRecibidos = new ArrayList<InfoMensajes>();
   private ArrayList<InfoMensajes> mensajesEnviados  = new ArrayList<InfoMensajes>();
   
   public InfoMensajes(Activity activity){
	   this.activity = activity;
   }
   
   public InfoMensajes(int id, String body){
	   this.id   = id;
	   this.body = body;
   }

	public long getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public ArrayList<InfoMensajes> getMensajesRecibidos() {
		return mensajesRecibidos;
	}

	public void setMensajesRecibidos(ArrayList<InfoMensajes> mensajesRecibidos) {
		this.mensajesRecibidos = mensajesRecibidos;
	}

	public ArrayList<InfoMensajes> getMensajesEnviados() {
		return mensajesEnviados;
	}

	public void setMensajesEnviados(ArrayList<InfoMensajes> mensajesEnviados) {
		this.mensajesEnviados = mensajesEnviados;
	}
	
	public void obtenerMensajesRecibidos(){
		Uri SMS_INBOX = Uri.parse("content://sms/inbox");
		Cursor mensajes = activity.getContentResolver().query(SMS_INBOX,null,null, null,"DATE desc");
		
		if(mensajes != null ){
				while(mensajes.moveToNext()){
					int threadId = mensajes.getInt(mensajes.getColumnIndex("thread_id"));
					String body = mensajes.getString(5);

					InfoMensajes msj = new InfoMensajes( threadId , body ); 
					this.mensajesRecibidos.add(msj);
				}
				//mensajes.close();
				this.setMensajesRecibidos(mensajesRecibidos);
		}
	}
	
	public void obtenerMensajesEnviados(){
		Uri SMS_INBOX = Uri.parse("content://sms/sent");
		Cursor mensajes = activity.getContentResolver().query(SMS_INBOX,null,null, null,"DATE desc");

		if(mensajes != null ){
				while(mensajes.moveToNext()){
				    int threadId = mensajes.getInt(mensajes.getColumnIndex("thread_id"));
					String body  = mensajes.getString(5);

					InfoMensajes msj = new InfoMensajes( threadId , body ); 
					this.mensajesEnviados.add(msj);
				}
				//mensajes.close();
				this.setMensajesEnviados(mensajesEnviados);
		}
	}
	
	public void eliminarMensajes(ArrayList<InfoMensajes> msj){
		for(int i=0; i < msj.size(); i++){
			activity.getContentResolver().delete(Uri.parse("content://sms/conversations/" + msj.get(i).getId()), null, null);
		}
	}
}

