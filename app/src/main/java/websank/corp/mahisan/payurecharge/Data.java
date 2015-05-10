package websank.corp.mahisan.payurecharge;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 09-05-2015.
 */
public class Data extends Activity {
    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);
        ActionBar mActionBar = getActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#007cd2")));
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        textView = (TextView) findViewById(R.id.textView);
        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }

        // listView1

        final ListView lisView1 = (ListView)findViewById(R.id.list);

        String url = "http://sanchitgoel.net78.net/indata.php";

        try {

            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> map;

            for(int i = 0; i < data.length(); i++){

                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();

                map.put("tit", c.getString("TAG_TITLE"));

                map.put("con", c.getString("TAG_CONTENT"));

                map.put("sec",c.getString("Sec"));
                MyArrList.add(map);

            }



            SimpleAdapter sAdap;

            sAdap = new SimpleAdapter(Data.this, MyArrList, R.layout.feed_item,

                    new String[] {"tit","con"}, new int[] {R.id.title,R.id.desc});

            lisView1.setAdapter(sAdap);

            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);

// OnClick Item

            lisView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> myAdapter, View myView,

                                        int position, long mylng) {

                    //  String sMemberID = MyArrList.get(position).get("tit")

                    //        .toString();

                    String sName = MyArrList.get(position).get("con")

                            .toString();

//String sMemberID = ((TextView) myView.findViewById(R.id.ColMemberID)).getText().toString();

// String sName = ((TextView) myView.findViewById(R.id.ColName)).getText().toString();

// String sTel = ((TextView) myView.findViewById(R.id.ColTel)).getText().toString();

                    viewDetail.setIcon(android.R.drawable.btn_star_big_on);

                    viewDetail.setTitle("Member Detail");

                    viewDetail.setMessage(sName);

                    viewDetail.setPositiveButton("OK",

                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,

                                                    int which) {

// TODO Auto-generated method stub

                                    dialog.dismiss();

                                }

                            });

                    viewDetail.show();
                }

            });


        } catch (JSONException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {

            long rxBytes = TrafficStats.getTotalRxBytes()- mStartRX;
            //RX.setText(Long.toString(rxBytes));
            long txBytes = TrafficStats.getTotalTxBytes()- mStartTX;
           // TX.setText(Long.toString(txBytes));
            long addition = rxBytes + txBytes;
            textView.setText("The Change in the Data Capacity Speed ="+Long.toString(addition)+"kb");

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    public String getJSONUrl(String url) {

        StringBuilder str = new StringBuilder();

        HttpClient client = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);

        try {

            HttpResponse response = client.execute(httpGet);

            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) { // Download OK

                HttpEntity entity = response.getEntity();

                InputStream content = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                String line;

                while ((line = reader.readLine()) != null) {

                    str.append(line);

                }

            } else {

                Log.e("Log", "Failed to download result..");

            }

        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return str.toString();

    }
}
