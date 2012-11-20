package com.hudnitsky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Button buttonYesterday;
    private Button buttonToday;
    private Button buttonTomorrow;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run);
        buttonYesterday = (Button)findViewById(R.id.button1);
        buttonToday = (Button)findViewById(R.id.button2);
        buttonTomorrow = (Button) findViewById(R.id.button3);
        buttonYesterday.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //yesterday
            }
        });
        buttonToday.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //today
            }
        });
        buttonTomorrow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //tomorrow
            }
        });
		downloadText("http://horoscope.up2date.by/index.html");


	}
	
	private void downloadText(String urlStr) {
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
                /**
                 import java.util.regex.Pattern;
                 import java.util.regex.Matcher;
                 class Module1{
                 public static void main(String[] asd){
                 String sourcestring = "source string to match with pattern";
                 Pattern re = Pattern.compile("<div id=\\\"taurus\\\">+(\\w*|\\s*)(.+|\\w+)\\s*\\w+\\s*</div>");
                 Matcher m = re.matcher(sourcestring);
                 int mIdx = 0;
                 while (m.find()){
                 for( int groupIdx = 0; groupIdx < m.groupCount()+1; groupIdx++ ){
                 System.out.println( "[" + mIdx + "][" + groupIdx + "] = " + m.group(groupIdx));
                 }
                 mIdx++;
                 }
                 }
                 }

                 */
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
                Bundle extras = getIntent().getExtras();
                int pageNumber = extras.getInt(EXT_PAGE);
                //add parser
				text.setText(msg.getData().getString("text"));
				break;
			}
			progressDialog.dismiss();
		}
	};
}
