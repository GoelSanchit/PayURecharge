package websank.corp.mahisan.payurecharge;

import android.app.
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by user on 09-05-2015.
 */
public class Call extends Activity implements View.OnClickListener{
    Button b1,b2,b3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        ActionBar mActionBar = getActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#007cd2")));
        b1 = (Button)findViewById(R.id.button);
        b2 = (Button)findViewById(R.id.button2);
        b3 = (Button)findViewById(R.id.button3);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
    }

    public void onClick(View v){

        if(v.getId() == R.id.button){
            Intent intent = new Intent(Call.this, Daily.class);
            startActivity(intent);

        }else if(v.getId() == R.id.button2){
            Intent intent = new Intent(Call.this, Weekly.class);
            startActivity(intent);
        }else if(v.getId() == R.id.button3){
            Intent intent = new Intent(Call.this, Monthly.class);
            startActivity(intent);
        }

    }

}
