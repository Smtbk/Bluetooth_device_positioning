package com.example.test10;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {

    public static String hostIP = "KEK";
    public static String hostPORT = "KEK";
    EditText Text1;
    EditText Text4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Text1 = findViewById(R.id.editText);
        Text4 = findViewById(R.id.editText4);

        Button saveB = (Button) findViewById(R.id.setting_saveButton);
        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                if (Text1.length() != 0 && Text4.length() != 0) {
                    hostIP = ((EditText) findViewById(R.id.editText)).getText().toString();
                    hostPORT = ((EditText) findViewById(R.id.editText4)).getText().toString();
                }
                startActivity(intent);
            }
        });
    }


}
