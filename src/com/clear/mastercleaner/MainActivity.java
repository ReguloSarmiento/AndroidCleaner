package com.clear.mastercleaner;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import com.clear.adapters.AdapterGridviewMain;
import com.shauvinoni.mastercleaner.R;




public class MainActivity extends ActionBarActivity implements OnItemClickListener{
	private GridView        gridview;
	private Button          size;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		String[]  title = { getResources().getString(R.string.appmanager),
				            getResources().getString(R.string.call), 
				            getResources().getString(R.string.cache),
                            getResources().getString(R.string.histories)};
		//Componetes UI
		gridview    = (GridView) findViewById(R.id.gridview);
		size        = (Button) findViewById(R.id.size);
		gridview.setOnItemClickListener(this);
		gridview.setAdapter(new AdapterGridviewMain(this, title));
		barraTitulo();
		size();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void barraTitulo(){
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setIcon(R.drawable.ic_launcher);
		actionbar.setTitle("Android Cleaner");
		actionbar.setDisplayUseLogoEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_actionbar));
	}
	
	public void size(){
        File path = Environment.getDataDirectory();
        espacio(path.getPath());
	}
	
	public void espacio(String path){
	    StatFs stat = new StatFs(path);
	    @SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize();
	    @SuppressWarnings("deprecation")
		long availableBlocks = stat.getAvailableBlocks();
	    @SuppressWarnings("deprecation")
		long countBlocks = stat.getBlockCount();
	    String fileSize = Formatter.formatFileSize(this, availableBlocks * blockSize);
	    String maxSize = Formatter.formatFileSize(this, countBlocks * blockSize);
	    size.setText("  "+fileSize + " / " + maxSize);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        switch(position){
               case 0:
            	   startActivity(new Intent(this, ListAppActivity.class));
            	   break;
               case 1:
            	   startActivity(new Intent(this, LlamadasMensajesActivity.class));
            	   break;
               case 2:
            	   startActivity(new Intent(this, CacheActivity.class));
            	   break;
               case 3:
            	   startActivity(new Intent(this, HistoryActivity.class));
                   break;
        }
	}	
}
