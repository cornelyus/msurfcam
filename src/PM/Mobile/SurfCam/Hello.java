package PM.Mobile.SurfCam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

import org.acra.annotation.ReportsCrashes;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


@ReportsCrashes(formKey = "dHdMZ21hSF9ZLXotUF9mY29OckVYTlE6MQ") 
public class Hello extends Activity {
	
	private URLConnection _connection = null;
	private InputStream _inStream = null;
	private Vibrator _myVib = null;
	private String _url = null;
	private VideoView _videoView = null;
	private static final int MSG_NETWORK_ERROR = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
			//ACRA.init(this);
			_myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        	ShowStart();
		}
		catch(Exception ex)
		{
		 Log.e("Surf", ex.toString());
		}
    }
	
    private String GetMp4FromUrl(String url) throws URISyntaxException, MalformedURLException, IOException
    {
    	try
		{
    	
    	_connection = null;
    	URI uri = new URI(url);
		_connection = uri.toURL().openConnection();
		_connection.connect();
		_inStream = _connection.getInputStream();
		
		BufferedReader r = new BufferedReader(new InputStreamReader(_inStream));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		
		String[] sub= null;

		sub = total.toString().split("<embed wmode=\"transparent\" src=\"");
		if(sub.length >= 2)
		{
			sub = sub[1].split("\" type=\"");
			if(sub.length >= 1)
			{

				_url = sub[0];
			}
			else
			{
				Log.i("Surf1", "SPLIT FAILD");
			}
		}	
		else
		{
			Log.i("Surf2", "SPLIT FAIL");
		}
		
		Log.i("Surf_LASTURL", _url);
		
		_url = _url.substring(_url.indexOf("play?file=")+ 10);
		_url = _url.substring(0, _url.indexOf("&autoStart"));
		Log.i("surf", _url);
		}
		catch (Exception ex)
		{
			Log.i("Surf", "ERROR");
			Log.e("SurfERROR", ex.toString());
		}	
		return _url;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.prog_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		_myVib.vibrate(50);
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
	    case R.id.costa:
	    	ShowCam(Cams.COSTA);
	        return true;
	    case R.id.carcavelos:
	    	ShowCam(Cams.CARCAVELOS);
	        return true;
	    case R.id.ericeira:
	    	ShowCam(Cams.ERICEIRA);
	        return true;
	    case R.id.estoril:
	    	ShowCam(Cams.ESTORIL);
	        return true;
	    case R.id.guincho:
	    	ShowCam(Cams.GUINCHO);
	        return true;
	    case R.id.peniche:
	    	ShowCam(Cams.PENICHE);
	        return true;
	    case R.id.praiagrande:
	    	ShowCam(Cams.PG);
	        return true;
	    case R.id.aveiro:
	    	ShowCam(Cams.AVEIRO);
	    case R.id.praialuz:
	    	ShowCam(Cams.PRAIALUZ);
	    case R.id.sines:
	    	ShowCam(Cams.SINES);
	    case R.id.lecapalmeira:
	    	ShowCam(Cams.LECAPALMEIRA);
	    case R.id.espinho:
	    	ShowCam(Cams.ESPINHO);
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
    private void ShowStart() throws URISyntaxException, MalformedURLException, IOException {
    	URI uri = new URI(this.getString(R.string.urlAveiro));
    	
    	if(isNetworkConnected()){
			setContentView(R.layout.videoview);
		    _videoView = (VideoView) findViewById(R.id.videoView);
		    MediaController mediaController = new MediaController(this);
		    mediaController.setAnchorView(_videoView);
		    Uri video = Uri.parse(GetMp4FromUrl(uri.toString()));
		    _videoView.setMediaController(mediaController);
		    _videoView.setVideoURI(video);
		    _videoView.start();  
    	}
    	else
    		someHandler.sendMessage(Message.obtain(someHandler,
					MSG_NETWORK_ERROR));
	}
    
    public void ShowCam(Cams cam) {
    	URI uri = null;
    	
    	if(isNetworkConnected()){
	        
    		try 
	        {
	        	switch(cam)
	        	{
	        	case AVEIRO:
	        		uri = new URI(this.getString(R.string.urlAveiro));
	        		break;
	        	case PRAIALUZ:
	        		uri = new URI(this.getString(R.string.urlPraiaLuz));
	        		break;
	        	case SINES:
	        		uri = new URI(this.getString(R.string.urlSines));
	        		break;
	        	case LECAPALMEIRA:
	        		uri = new URI(this.getString(R.string.urlLecaPalmeira));
	        		break;
	        	case ESPINHO:
	        		uri = new URI(this.getString(R.string.urlEspinho));
	        		break;
	        	case CARCAVELOS:
	        		uri = new URI(this.getString(R.string.urlCarcavelos));
	        		break;
	        	case ERICEIRA:
	        		uri = new URI(this.getString(R.string.urlEriceira));
	        		break;
	        	case PG:
	        		uri = new URI(this.getString(R.string.urlPraiaGrande));
	        		break;
	        	case GUINCHO:
	        		uri = new URI(this.getString(R.string.urlGuincho));
	        		break;
	        	case COSTA:
	        		uri = new URI(this.getString(R.string.urlCosta));
	        		break;
	        	case ESTORIL:
	        		uri = new URI(this.getString(R.string.urlEstoril));
	        		break;
	        	case PENICHE:
	        		uri = new URI(this.getString(R.string.urlPeniche));
	        		break;
	        	}
	        	
	    		setContentView(R.layout.videoview);
	    	    _videoView = (VideoView) findViewById(R.id.videoView);
	    	    MediaController mediaController = new MediaController(this);
	    	    mediaController.setAnchorView(_videoView);
	    	    Uri video = Uri.parse(GetMp4FromUrl(uri.toString()));
	    	    _videoView.setMediaController(mediaController);
	    	    _videoView.setVideoURI(video);
	    	    _videoView.start();    	
	        }
	        catch (Exception ex)
	        {
	        	Log.e("SurfError", ex.toString());
	        }
    	}
    	else
    		someHandler.sendMessage(Message.obtain(someHandler,
					MSG_NETWORK_ERROR));
    		
    }
    
    /**
     * Checks if the device is Network Connected, meaning, if it has access to the internet.
     * @return true if connected, false otherwise
     */
    	public boolean isNetworkConnected() {
    		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo network = cm.getActiveNetworkInfo();

    		if (network != null) {
    			return network.isAvailable();
    		}

    		return false;
    	}
    	
    	/**
    	 * Main UI Handler that deals with showing errors that can result from background Threads ( Communications, etc)
    	 * 
    	 */
    	Handler someHandler = new Handler() {

    		// this method will handle the calls from other threads.
    		public void handleMessage(Message msg) {

    			switch (msg.what) {
    			
    			case MSG_NETWORK_ERROR:
    				Toast.makeText(getApplicationContext(), "No Network Available",
    						Toast.LENGTH_SHORT).show();
    				break;			

    			}
    		}
    	};
}
	