package com.hudnitsky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Preview extends Activity {

	public static final String EXT_PAGE = "page";
    private static final String[] mHoro = {"aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra",
            "scorpio", "sagittarius", "capricorn", "aquarius", "pisces"};
	private ProgressDialog progressDialog;	
	private Bitmap bitmap = null;
	private String text = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run);
        Toast.makeText(super.getBaseContext(), "The day is " +
                super.getBaseContext().getResources(), Toast.LENGTH_LONG).show();
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		    this, R.array.planets_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        Bundle extras = getIntent().getExtras();
        int pageNumber = extras.getInt(EXT_PAGE);
		downloadText("http://horoscope.up2date.by/index.html",mHoro[pageNumber]);


	}
	
	private void downloadText(String urlStr,String pageName) {
		progressDialog = ProgressDialog.show(this, "", "Fetching Text...");
		final String url = urlStr;
		new Thread() {
			public void run() {
				int BUFFER_SIZE = 2000;
				InputStream in = null;
				Message message = Message.obtain();
				message.what=2;
				try {
					in = openHttpConnection(url);
					InputStreamReader isr = new InputStreamReader(in);
					int charRead;
					text = "";
					char[] inputBuffer = new char[BUFFER_SIZE];
					while ((charRead = isr.read(inputBuffer))>0)
					{                    
						String readString =
								String.copyValueOf(inputBuffer, 0, charRead);
						text += readString;
						inputBuffer = new char[BUFFER_SIZE];
					}
					Bundle bundle = new Bundle();
					bundle.putString("text", text);
					message.setData(bundle);
					in.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
				//add html parser
                //java regexp "<div id=\"taurus\">+(\\w*|\\s*)(.+|\\w+)\\s*\\w+\\s*</div>"
				messageHandler.sendMessage(message);
			}
		}.start();
	}
	private InputStream openHttpConnection(String urlStr) {
		InputStream in = null;
		int resCode = -1;
		try {
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();
			if (!(urlConn instanceof HttpURLConnection)) {
				throw new IOException("URL is not an Http URL");
			}
			HttpURLConnection httpConn = (HttpURLConnection)urlConn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect(); 
			resCode = httpConn.getResponseCode();                 
			if (resCode == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();                                 
			}         
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
	private Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ImageView img = (ImageView) findViewById(R.id.imageview01);
				img.setImageBitmap((Bitmap)(msg.getData().getParcelable("bitmap")));
				break;
			case 2:
				TextView text = (TextView) findViewById(R.id.textview01);
				text.setText(msg.getData().getString("text"));
				break;
			}
			progressDialog.dismiss();
		}
	};
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		 
	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	      Toast.makeText(parent.getContext(), "The day is " +
	          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	    }
	 
	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
}
