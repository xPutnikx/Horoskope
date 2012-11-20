package com.hudnitsky;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
    ListAdapter listAdapter;
    private static final int NOTIFY_ID = 101;
    private NotificationManager myNotifyMgr;
    private static final String[] PENS = new String[] { "Овен", "Телец", "Близнецы",
            "Рак", "Лев", "Дева", "Весы", "Скорпион", "Стрелец", "Козерог",
            "Водолей", "Рыбы" };
    private static final String[] DATE = { "21 марта - 20 апреля",
            "21 апреля - 20 мая", "21 мая - 21 июня", "22 июня - 22 июля",
            "23 июля - 23 августа", "24 августа - 23 сентября",
            "24 сентября - 23 октября", "24 октября - 22 ноября",
            "23 ноября - 21 декабря", "22 декабря - 20 января",
            "21 января - 20 февраля", "21 февраля - 20 марта" };
    private static final Integer[] mImage =  {R.drawable.aries, R.drawable.taurus, R.drawable.gemini, R.drawable.cancer,
            R.drawable.lion, R.drawable.virgo, R.drawable.libra, R.drawable.scorpio,
            R.drawable.sagittarius, R.drawable.capricorn, R.drawable.aquarius, R.drawable.pisces};
    private static final String[] mHoro = {"aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra",
            "scorpio", "sagittarius", "capricorn", "aquarius", "pisces"};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new ListAdapter(this);
        setListAdapter(listAdapter);
        myNotifyMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Object o = this.getListAdapter().getItem(position);
        String pens = o.toString();
        int pen = Integer.parseInt(pens);
        Intent intent = new Intent();
        intent.setClass(this, Preview.class);
        intent.putExtra(Preview.EXT_PAGE, pen);
        startActivity(intent);
        //finish();
    }

    /**
     * Custom adapter for list activity
     * @author Putnik
     *
     */
    public class ListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        public ListAdapter (Context ctx) {
            mLayoutInflater = LayoutInflater.from(ctx);
        }

        public int getCount () {
            return PENS.length;
        }

        public Object getItem (int position) {
            return position;
        }

        public long getItemId (int position) {
            return position;
        }

        public String getString (int position) {
            return PENS[position] + " (" + DATE[position] + ")";
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.activity_main, null);

            ImageView image = (ImageView)convertView.findViewById(R.id.Image);
            image.setImageResource(mImage[position]);

            TextView sign = (TextView)convertView.findViewById(R.id.Sign);
            sign.setText(PENS[position]);

            TextView date = (TextView)convertView.findViewById(R.id.Date);
            date.setText(DATE[position]);
            return convertView;
        }
    }
}
