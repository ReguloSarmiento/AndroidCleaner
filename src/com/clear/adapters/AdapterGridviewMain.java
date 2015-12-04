package com.clear.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shauvinoni.mastercleaner.R;

public class AdapterGridviewMain extends BaseAdapter{
    private Context context; 
    private String[]  title;
	
	public AdapterGridviewMain(Context context, String[]  title){
		this.context = context;
		this.title = title;
	}
	
	@Override
	public int getCount() {	return title.length; }

	@Override
	public Object getItem(int position) { return null; }

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		
		if (convertView == null) { 
        	holder = new Holder(); 
        	LayoutInflater inflate = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE ); 
        	convertView =  inflate.inflate(R.layout.gridview_main_item, null);
        	holder.setImage((ImageView) convertView.findViewById(R.id.imagen));
        	holder.setTexto((TextView) convertView.findViewById(R.id.texto));
        	convertView.setTag(holder); 
        } else {
        	holder = (Holder) convertView.getTag();
        }
		
		 holder.getImage().setImageResource(icons[position]);
	     holder.getTextView().setText(title[position]);
	  
		return convertView;
		
		
	}
	
	
	public class Holder{
		  public ImageView imagen;
	      public TextView  texto;
	      
	      public void setImage(ImageView image){
	            this.imagen = image;
	      }
	      
	      public void setTexto(TextView texto){
	    	    this.texto = texto;
	      }
	      
	      public ImageView getImage(){
	            return imagen;
	      }
	      
	      public TextView getTextView(){
	            return texto;
	      }
     }
		
	private Integer[] icons = {R.drawable.app_manager_icon, R.drawable.llamadas_mensajes, R.drawable.cache_icon, R.drawable.history_icon};
}
