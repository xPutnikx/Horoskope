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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final int SUBPARSER=6;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run);
        Bundle extras = getIntent().getExtras();
        int pageNumber = extras.getInt(EXT_PAGE);
        String pageName=mHoro[pageNumber];
        ImageView image = (ImageView) findViewById(R.id.imageview01);
        image.setImageResource(imageParser(pageName));
        buttonYesterday = (Button)findViewById(R.id.button1);
        buttonToday = (Button)findViewById(R.id.button2);
        buttonTomorrow = (Button) findViewById(R.id.button3);

        buttonYesterday.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                downloadText("http://horoscope.up2date.by/index.html","yesterday");
            }
        });
        buttonToday.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                downloadText("http://horoscope.up2date.by/index.html","today");
            }
        });
        buttonTomorrow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                downloadText("http://horoscope.up2date.by/index.html","tomorrow");
            }
        });
		downloadText("http://horoscope.up2date.by/index.html","today");


	}
	
	private void downloadText(final String urlStr, final String day) {
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
                    bundle.putString("day", day);
					message.setData(bundle);
					in.close();
				}catch (IOException e) {
					e.printStackTrace();
				}

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
                Bundle bundle=msg.getData();
                String day=bundle.getString("day");
				TextView text = (TextView) findViewById(R.id.textview01);
                String result=textParse(msg.getData().getString("text"),day);
                text.clearComposingText();
				text.setText(result);
				break;
			}
			progressDialog.dismiss();
		}
	};
    private String textParse(String sourceString,String day){
        Bundle extras = getIntent().getExtras();
        int pageNumber = extras.getInt(EXT_PAGE);
        String pageName=mHoro[pageNumber];
        Pattern patternDay = Pattern.compile("<div id=\\\""+day+"\\\">+((\\w*|\\s*)(.+|\\w+)\\s*.+\\s*\\w*\\s*</div>)*\\s*</div>");
        Matcher matcherDay = patternDay.matcher(sourceString);
        String result="";
        while (matcherDay.find()){
            result+= matcherDay.group(0);
        }
        Pattern pattern0 = Pattern.compile("<div id=\\\""+pageName+"\\\">+(\\w*|\\s*)(.+|\\w+)\\s*\\w+\\s*</div>");
        Matcher matcher0 = pattern0.matcher(result);
        String resultString="";
        while (matcher0.find()){
            resultString+= matcher0.group(0);
        }
        Pattern pattern1 = Pattern.compile("<div id=\\\""+pageName+"\\\">");
        Matcher matcher1 = pattern1.matcher(resultString);
        resultString = matcher1.replaceAll("");
        return resultString.substring(0,resultString.length()-SUBPARSER);
    }
    private int imageParser(String image){
        if(image.equals("aries")){
            return R.drawable.aries_big;
        }
        if(image.equals("taurus")){
            return R.drawable.taurus_big;
        }
        if(image.equals("gemini")){
            return R.drawable.gemini_big;
        }
        if(image.equals("cancer")){
            return R.drawable.cancer_big;
        }
        if(image.equals("leo")){
            return R.drawable.lion_big;
        }
        if(image.equals("virgo")){
            return R.drawable.virgo_big;
        }
        if(image.equals("libra")){
            return R.drawable.libra_big;
        }
        if(image.equals("scorpio")){
            return R.drawable.scorpio_big;
        }
        if(image.equals("sagittarius")){
            return R.drawable.sagittarius_big;
        }
        if(image.equals("capricorn")){
            return R.drawable.capricorn_big;
        }
        if(image.equals("aquarius")){
            return R.drawable.aquarius_big;
        }
        if(image.equals("pisces")){
            return R.drawable.pisces_big;
        }
        return 0;
    }
}
