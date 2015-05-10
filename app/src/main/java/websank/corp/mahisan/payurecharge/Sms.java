package websank.corp.mahisan.payurecharge;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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


public class Sms extends Activity {
    SimpleCursorAdapter adapter;
    ListView lvMsg,lisView1;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms);
        ActionBar mActionBar = getActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#007cd2")));
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        lvMsg = (ListView) findViewById(R.id.listView);
        lisView1 = (ListView)findViewById(R.id.listView2);
        tv = (TextView) findViewById(R.id.textView);
        String url = "http://sanchitgoel.net78.net/sms.php";
        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/sent");

        // List required columns
        String[] reqCols = new String[] { "_id", "address", "body" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        // Attached Cursor with adapter and display in listview
        adapter = new SimpleCursorAdapter(this, R.layout.feed_item, c,
                new String[] { "body", "address" }, new int[] {
                R.id.title, R.id.desc });
        int length = adapter.getCount();
        tv.setText("Total Messages Sent:-"+ length+"");

        try {

            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> map;

            for(int i = 0; i < data.length(); i++){

                JSONObject ca = data.getJSONObject(i);

                map = new HashMap<String, String>();

                map.put("tit", ca.getString("TAG_TITLE"));

                map.put("con", ca.getString("TAG_CONTENT"));


                MyArrList.add(map);

            }

            SimpleAdapter sAdap;

            sAdap = new SimpleAdapter(Sms.this, MyArrList, R.layout.feed_item,

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

                    viewDetail.setTitle("Message Info");

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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