package com.clear.adapters;

import java.util.ArrayList;

import com.clear.helpers.EntryItem;
import com.clear.helpers.Item;
import com.clear.helpers.SectionItem;
import com.shauvinoni.mastercleaner.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterLlamadasMensajes extends ArrayAdapter<Item>{
	 private ArrayList<Item> items;
	 private LayoutInflater vi;

	public AdapterLlamadasMensajes(Context context, ArrayList<Item> items) {
		super(context, 0, items);
		this.items   = items;
		this.vi      = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		final Item i = items.get(position);
		  if (i != null) {
			  if(i.isSection()){
				  SectionItem si = (SectionItem)i;
				  v = vi.inflate(R.layout.divisor, null);
				  v.setBackgroundColor(Color.parseColor("#142241"));
				  v.setOnClickListener(null);
				  v.setOnLongClickListener(null);
				  v.setLongClickable(false);
		     
				  final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
				  sectionView.setTextColor(Color.parseColor("#FFFFFF"));
				  sectionView.setText(si.getTitle());
		     
			  }else{
				  final EntryItem ei = (EntryItem)i;
				  v = vi.inflate(R.layout.items_call_msj, null);
				  final ImageView icono   = (ImageView) v.findViewById(R.id.list_item_entry_drawable);
				  final TextView title    = (TextView)  v.findViewById(R.id.list_item_entry_title);
				  final TextView cantidad = (TextView)  v.findViewById(R.id.list_item_entry_summary);
				  final CheckBox check    = (CheckBox)  v.findViewById(R.id.checkBox1);
		     
				  check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					  @Override
					  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						  ei.setSelected(buttonView.isChecked());
						  Log.d("Metodo ApplicationAdapter------>", ei.title);
					  }
				  }); 
		  
				  title.setText(ei.title);
				  cantidad.setText(ei.cantidad);
				  icono.setImageResource(ei.icono);
				  check.setChecked(ei.isSelected());
		   }
		  }
		  
		return v;
	}
}
