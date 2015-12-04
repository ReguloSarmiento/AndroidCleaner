package com.clear.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.clear.helpers.InfoApp;
import com.shauvinoni.mastercleaner.R;

public class ApplicationAdapter extends ArrayAdapter<InfoApp>{
	private List<InfoApp> appsList = null;
	private Context context;

	public ApplicationAdapter(Context context, int textViewResourceId, List<InfoApp> appsList) {
		super(context, textViewResourceId, appsList);
		this.context    = context;
		this.appsList   = appsList;
	}
	
	@Override
	public int getCount() {
		return ((null != appsList) ? appsList.size() : 0);
	}

	@Override
	public InfoApp getItem(int position) {
		return ((null != appsList) ? appsList.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (null == view) {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.list_app_item, null);
		}
	
		final InfoApp data = appsList.get(position);
		if (null != data) {
			TextView appName           = (TextView)  view.findViewById(R.id.app_name);
			TextView fecha             = (TextView)  view.findViewById(R.id.fechaInstalled);
			TextView espacio           = (TextView)  view.findViewById(R.id.espaciOcupado);
			ImageView iconview         = (ImageView) view.findViewById(R.id.app_icon);
			final CheckBox check       = (CheckBox)  view.findViewById(R.id.check);
			
			check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				    data.setSelected(buttonView.isChecked());
				}
			});  
			
			appName.setText(data.getAppname());
			fecha.setText(data.getFechaInstalled());
			espacio.setText(data.getEspacioOcupadoCacheCodigo());
			iconview.setImageDrawable(data.getIcon());
			check.setChecked(data.isSelected());
		}
		return view;
	}
}
