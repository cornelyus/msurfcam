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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;


@ReportsCrashes(formKey = "dHdMZ21hSF9ZLXotUF9mY29OckVYTlE6MQ") 
public class ShowCam extends SherlockActivity {
	
	private URLConnection _connection = null;
	private InputStream _inStream = null;
	private Vibrator _myVib = null;
	private String _url = null;
	private VideoView _videoView = null;
	private MediaController mediaController = null;
	private static final int MSG_NETWORK_ERROR = 1;
	private static final int MSG_SHOW_VIDEO = 2;
	private static final int MSG_NIGHT_VIDEO = 3;
	private static final int MSG_INIT_PROG = 4;
	private static final int MSG_END_PROG = 5;
	private static final int MSG_NO_VIDEO = 6;
	Context mContext = null; 
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		//This has to be called before setContentView and you must use the
        //class in com.actionbarsherlock.view and NOT android.view
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.showcam);
				
		mediaController = new MediaController(this);
		
//		try
//		{
//			//ACRA.init(this);
//			_myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
//        	ShowStart();
//		}
//		catch(Exception ex)
//		{
//		 Log.e("Surf", ex.toString());
//		}
    }
	@Override
	public void onStart()
	{
		super.onStart();
		try
		{
			//ACRA.init(this);
			_myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
			Log.e("Surf", "onStart");
        	
			new Thread( new Runnable()
			{
			    @Override
			    public void run()
			    {
			        // Do something
			    	try {
						ShowStart();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			} ).start();
			
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

		if(total.toString().contains(this.getString(R.string.videonoite)))
		{
			Log.i("Surf", "Video Noite");
			someHandler.sendMessage(Message.obtain(someHandler,	MSG_NIGHT_VIDEO));
			//Toast.makeText(ShowCam.this, this.getString(R.string.errovideonoite), Toast.LENGTH_LONG).show();
			return null;
		}
		else if(total.toString().contains(this.getString(R.string.videoindisponivel)))
		{
			Log.i("Surf", "Video Indisponivel");
			someHandler.sendMessage(Message.obtain(someHandler,	MSG_NO_VIDEO));
			//Toast.makeText(ShowCam.this, this.getString(R.string.errovideonoite), Toast.LENGTH_LONG).show();
			return null;
				
		}
		
		
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

	
    private void ShowStart() throws URISyntaxException, MalformedURLException, IOException {
    	    	
    	//URI uri = new URI(this.getString(R.string.urlAveiro));
    	URI uri = new URI(getCamUrl());
    	
    	if(isNetworkConnected()){
			//setContentView(R.layout.videoview);
		    try {
		    	
		    	someHandler.sendMessage(Message.obtain(someHandler,
						MSG_INIT_PROG));
				_videoView = (VideoView) findViewById(R.id.videoView);
//				MediaController mediaController = new MediaController(mContext);
				mediaController.setAnchorView(_videoView);
				String auxStr = GetMp4FromUrl(uri.toString());
	    	    if(auxStr != null)
	    	    {
//		    	    Uri video = Uri.parse(auxStr);
//		    	    _videoView.setMediaController(mediaController);
//		    	    _videoView.setVideoURI(video);
		    	    someHandler.sendMessage(Message.obtain(someHandler,
							MSG_SHOW_VIDEO, auxStr));
		    	    //_videoView.start();
//		    	    someHandler.sendMessage(Message.obtain(someHandler,
//							MSG_END_PROG));
		    	       	    	    
	    	    }	    
	    	    else
	    	    {
	    	    	someHandler.sendMessage(Message.obtain(someHandler,
							MSG_END_PROG));
	    	    	ShowCam.this.finish();
	    	    }
	    	    	
			} catch (Exception e) {
				Log.e("Surf", e.getMessage());
				e.printStackTrace();
			}  
    	}
    	else
    	{
    		someHandler.sendMessage(Message.obtain(someHandler,
					MSG_END_PROG));
    		someHandler.sendMessage(Message.obtain(someHandler,
					MSG_NETWORK_ERROR));
    		ShowCam.this.finish();
    	}
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
	        	
	    		//setContentView(R.layout.videoview);
	    	    _videoView = (VideoView) findViewById(R.id.videoView);
	    	    MediaController mediaController = new MediaController(this);
	    	    mediaController.setAnchorView(_videoView);
	    	    String auxStr = GetMp4FromUrl(uri.toString());
	    	    if(auxStr != null)
	    	    {
		    	    Uri video = Uri.parse(auxStr);
		    	    _videoView.setMediaController(mediaController);
		    	    _videoView.setVideoURI(video);
		    	    _videoView.start();   	    	    
	    	    }
	    	    else
	    	    	this.finish();
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
    			case MSG_SHOW_VIDEO:
    				if(_videoView != null)
    				{
    					Uri video = Uri.parse(msg.obj.toString());
    					_videoView.setMediaController(mediaController);
    		    	    _videoView.setVideoURI(video);
    					_videoView.start();
    					setSupportProgressBarIndeterminateVisibility(false);
    				}
    				break;
    			case MSG_NIGHT_VIDEO:
    				Log.i("Surf", "someHandler MSG_NIGHT_VIDEO");
    				Toast.makeText(ShowCam.this, getApplicationContext().getString(R.string.errovideonoite), Toast.LENGTH_LONG).show();
    				break;
    			case MSG_NO_VIDEO:
    				Log.i("Surf", "someHandler MSG_NO_VIDEO");
    				Toast.makeText(ShowCam.this, getApplicationContext().getString(R.string.errovideoindisponivel), Toast.LENGTH_LONG).show();
    				break;
    			case MSG_INIT_PROG:
    				setSupportProgressBarIndeterminateVisibility(true);
    				break;
    			case MSG_END_PROG:
    				setSupportProgressBarIndeterminateVisibility(false);
    				break;

    			}
    		}
    	};
    	
    	private String getCamUrl()
    	{
    		//values = new String[] { "Aveiro", "Carcavelos", "Costa da Caparica" ,"Ericeira", "Espinho"
    	    //		,"Estoril", "Guincho", "Leca da Palmeira", "Peniche", "Praia Grande", "Praia da Luz", "Sines" };
    		
    		String url = "";
    		switch(getIntent().getExtras().getInt(ListCams.thisCam))
        	{
        	case 0:
        		url = this.getString(R.string.urlAveiro);
        		getSupportActionBar().setSubtitle(R.string.cam_aveiro);
        		break;
        	case 1:
        		url = this.getString(R.string.urlCarcavelos);
        		getSupportActionBar().setSubtitle(R.string.cam_carcavelos);
        		break;
        	case 2:
        		url = this.getString(R.string.urlCosta);
        		getSupportActionBar().setSubtitle(R.string.cam_costa);
        		break;
        	case 3:
        		url = this.getString(R.string.urlEriceira);
        		getSupportActionBar().setSubtitle(R.string.cam_ericeira);
        		break;
        	case 4:
        		url = this.getString(R.string.urlEspinho);
        		getSupportActionBar().setSubtitle(R.string.cam_espinho);
        		break;
        	case 5:
        		url = this.getString(R.string.urlEstoril);
        		getSupportActionBar().setSubtitle(R.string.cam_estoril);
        		break;
        	case 6:
        		url = this.getString(R.string.urlGuincho);
        		getSupportActionBar().setSubtitle(R.string.cam_guincho);
        		break;
        	case 7:
        		url = this.getString(R.string.urlLecaPalmeira);
        		getSupportActionBar().setSubtitle(R.string.cam_lecapalmeira);
        		break;
        	case 8:
        		url = this.getString(R.string.urlPeniche);
        		getSupportActionBar().setSubtitle(R.string.cam_peniche);
        		break;
        	case 9:
        		url = this.getString(R.string.urlPraiaGrande);
        		getSupportActionBar().setSubtitle(R.string.cam_praiagrande);
        		break;
        	case 10:
        		url = this.getString(R.string.urlPraiaLuz);
        		getSupportActionBar().setSubtitle(R.string.cam_praialuz);
        		break;
        	case 11:
        		url = this.getString(R.string.urlSines);
        		getSupportActionBar().setSubtitle(R.string.cam_sines);
        		break;
        	}

    		Log.d("Surf", "url "+ url);
    		return url;
    		
    	}
}
	