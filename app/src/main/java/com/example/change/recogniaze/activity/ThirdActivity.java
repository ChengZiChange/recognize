package com.example.change.recogniaze.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.change.recogniaze.R;

public class ThirdActivity extends Activity {
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        delete= (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name","activity3");
                int delete=1;
                bundle.putInt("delete",delete);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}
