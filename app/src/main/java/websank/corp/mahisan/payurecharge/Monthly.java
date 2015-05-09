package websank.corp.mahisan.payurecharge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CallLog;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 09-05-2015.
 */
public class Monthly extends Activity {
    TextView textView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly);
        textView = (TextView) findViewById(R.id.textview_call);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        // listView1

        final ListView lisView1 = (ListView)findViewById(R.id.list);
        getCallDetails();
        String url = "http://sanchitgoel.net78.net/monthly.php";

        try {

            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> map;

            for(int i = 0; i < data.length(); i++){

                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();

                map.put("tit", c.getString("TAG_TITLE"));

                map.put("con", c.getString("TAG_CONTENT"));


                MyArrList.add(map);

            }

            SimpleAdapter sAdap;

            sAdap = new SimpleAdapter(Monthly.this, MyArrList, R.layout.feed_item,

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

    private void getCallDetails() {
        StringBuffer sb = new StringBuffer();
        // a simple hashMap declaration with default size and load factor
        HashMap<String, String> hashMap = new HashMap<String, String>();

        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        /* Query the CallLog Content Provider */
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        sb.append("Call Log :");
        int netOutgoing = 0;

        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);
            String strcallDate = managedCursor.getString(date);
            Date callDate = new Date(Long.valueOf(strcallDate));

            if(callDate.getTime()<getYesterdayDateString().getTime())
                break;

            String callDuration = managedCursor.getString(duration);
            String callType = null;
            hashMap.put("number", phNum);
            hashMap.put("type",callTypeCode);
            hashMap.put("date", callDate.getTime() + "");
            hashMap.put("duration", callDuration);

            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;

            }


            if(callcode==CallLog.Calls.OUTGOING_TYPE) {
                netOutgoing+=Integer.parseInt(callDuration);  // Net Out going of the call.
            }

        }
        sb.append( "Net Outgoing last Weekday =  :--- " + netOutgoing);
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
//        String strDate = sdf.format(c.getTime());
//


        managedCursor.close();
      //  textView.setText(sb);
    }

    public Date getYesterdayDateString(){
        return new Date(System.currentTimeMillis()-1000L*60L*60L*24L*30L);
    }

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
