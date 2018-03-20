package com.example.change.recogniaze.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.change.recogniaze.R;


public class SecondActivity extends Activity {
    private Button finish;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        finish = (Button) findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                et = (EditText)findViewById(R.id.editText);
                String value=et.getText().toString().trim();
                Log.i("activity2",value);
                Log.i("activity2", Integer.toString(value.length()));
                bundle.putString("name","activity2");//作为判断的标志
                bundle.putString("value",value);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
