package com.example.ledmove;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

}
    public void next(View view) {
        //Button button1=(Button)findViewById(R.id.button);
        //startActivity(new Intent(MainActivity.this, LedOnOff.class));
        Intent intent = new Intent(MainActivity.this, LedOnOff.class);
        startActivity(intent);

    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            //errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                //Log.d(TAG, "...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                //Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                btAdapter.enable();
                //btAdapter.disable();
            }
        }
    }

}


