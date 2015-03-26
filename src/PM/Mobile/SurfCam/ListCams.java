package PM.Mobile.SurfCam;


import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ListCams extends SherlockActivity {
	
	 ListView listView;
	 String[] values;
	 public static final int activityShowCam = 1;
	 public static final String thisCam = "thisCam";
	 int count = 0;
	 boolean demo = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock); //Used for theme switching in samples
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.camslist);
	    
	    checkIfDemo();
	
	    listView = (ListView) findViewById(R.id.mycamslist);
	    values = new String[] { "Aveiro", "Carcavelos", "Costa da Caparica" ,"Ericeira", "Espinho"
	    		,"Estoril", "Guincho", "Leca da Palmeira", "Peniche", "Praia Grande", "Praia da Luz", "Sines" };

	    // First paramenter - Context
	    // Second parameter - Layout for the row
	    // Third parameter - ID of the View to which the data is written
	    // Forth - the Array of data
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	    	android.R.layout.simple_list_item_1, values);

	    // Assign adapter to ListView
	    listView.setAdapter(adapter);
	    
	    // For ListItem Click
	    listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
                String s = values[position].toString();
                //Toast.makeText(ListCams.this, "Selected Beach is " + s, Toast.LENGTH_SHORT).show();                
                
                
                if(isNetworkConnected())
                {
                	if(hasDataNetworkConnection())
                	{
                		final int pos = position;
                		
                		AlertDialog.Builder builder = new AlertDialog.Builder(ListCams.this);
               	     	builder.setMessage(ListCams.this.getString(R.string.alerta_3gactivo))
               	            .setCancelable(true)
               	            .setIcon(R.drawable.alert_dialog_icon)
               	            .setTitle(ListCams.this.getString(R.string.alertatitulo_3gactivo))
               	            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
               	                public void onClick(DialogInterface dialog, int id) {
               	                	Intent i = new Intent(ListCams.this, ShowCam.class);
               		        		i.putExtra(thisCam, pos);
               		        		startActivityForResult(i, activityShowCam);
               	                }
               	            })
               	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
               	                public void onClick(DialogInterface dialog, int id) {
               	                     dialog.cancel();
               	                }
               	            });
               	     	AlertDialog alert = builder.create();
               	     	alert.show();
                	}
                	else
                	{                	
		                Intent i = new Intent(ListCams.this, ShowCam.class);
		        		i.putExtra(thisCam, position);
		        		startActivityForResult(i, activityShowCam);
                	}
                }
                else
                {
                	AlertDialog.Builder builder = new AlertDialog.Builder(ListCams.this);
           	     	builder.setMessage(ListCams.this.getString(R.string.alerta_semligacao))
           	            .setCancelable(true)
           	            .setIcon(R.drawable.alert_dialog_icon)
           	            .setTitle(ListCams.this.getString(R.string.alertatitulo_semligacao))
           	            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
           	                public void onClick(DialogInterface dialog, int id) {
           	                	
           	                }
           	            });
           	     	AlertDialog alert = builder.create();
           	     	alert.show();
                }
                	
                
            }
        });
	    
	    
	}
	
	
	private void checkIfDemo() {
		
		if(demo){
				Calendar cal = new GregorianCalendar();
			cal.set(2012, 5, 14);
			// etc...
	
			Log.d("Surf", "system current " + System.currentTimeMillis());
			Log.d("Surf", "call millis current " + cal.getTimeInMillis());
			
			if (System.currentTimeMillis() > cal.getTimeInMillis()) {
				//Toast.makeText(ListCams.this, "Time has passed", Toast.LENGTH_SHORT).show();
				Toast.makeText(ListCams.this, "Sorry for that. Maybe the swell is too big. Nah, Eddie would go..", Toast.LENGTH_LONG).show();
				this.finish();
			}
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.listcams_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.exitapp:
	    	android.os.Process.killProcess(android.os.Process.myPid());
	        return true;
	    case R.id.donate:
	    	Intent intent = new Intent(Intent.ACTION_VIEW);
	    	intent.setData(Uri.parse(this.getString(R.string.donateUrl)));
	    	startActivity(intent);
	        return true;
	    case R.id.home:
	    	Intent intentHome = new Intent(Intent.ACTION_VIEW);
	    	intentHome.setData(Uri.parse(this.getString(R.string.homeUrl)));
	    	startActivity(intentHome);
	        return true;	    
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
     * Checks if the device is Network Connected, meaning, if it has access to the internet.
     * @return true if connected, false otherwise
     */
    	private boolean isNetworkConnected() {
    		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo network = cm.getActiveNetworkInfo();

    		if (network != null) {
    			return network.isAvailable();
    		}

    		return false;
    	}
    	
    	/**
         * Checks if the device is Network Connected with Data Plan, meaning, if it has access to the internet.
         * @return true if connected, false otherwise
         */
    	private boolean hasDataNetworkConnection() {
    		//http://stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available
    	    boolean haveConnectedWifi = false;
    	    boolean haveConnectedMobile = false;

    	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
    	    for (NetworkInfo ni : netInfo) {
    	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
    	            if (ni.isConnected())
    	                haveConnectedMobile = true;
    	    }
    	    return haveConnectedMobile;
    	}

}
