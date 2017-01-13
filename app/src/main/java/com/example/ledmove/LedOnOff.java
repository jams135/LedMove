package com.example.ledmove;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class LedOnOff extends AppCompatActivity {

    Button btnOn, btnOff, myb;
    TextView myt;
    Handler handler;

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    InputStream inStream=null;

    // Well known SPP UUID
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your server's MAC address
    private static String address = "00:15:83:35:8A:C9";

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_on_off);


        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        myb= (Button) findViewById(R.id.button1);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();


        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("1");
                Toast msg = Toast.makeText(getBaseContext(),
                        "You have clicked On", Toast.LENGTH_SHORT);
                msg.show();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("0");
                Toast msg = Toast.makeText(getBaseContext(),
                        "You have clicked Off", Toast.LENGTH_SHORT);
                msg.show();
            }
        });


        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        myb=(Button)findViewById(R.id.button1);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                String message = bundle.getString("Message");

                myt=(TextView) findViewById(R.id.textView1);
                myt.setText(message);

            }
        };


        myb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Runnable r=new Runnable() {
                    @Override
                    public void run() {
                        byte buffer[]= new byte[1024];
                        int bytes;

                        // while(true) {
                        try {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bytes = inStream.read(buffer);            //read bytes from input buffer
                            String yo = new String(buffer, 0, bytes);

                            bundle.putString("Message", yo);
                            msg.setData(bundle);
                            // send message to the handler
                            handler.sendMessage(msg);
                        } catch (Exception e) {

                        }
                        // }//while
                    }//run
                };

                Thread jamThread=new Thread(r);
                jamThread.start();



            }
        });//onclickListener

    }

    @Override
    public void onResume() {
        super.onResume();

        //Log.d(TAG, "...In onResume - Attempting client connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        //Log.d(TAG, "...Connecting to Remote...");
        try {
            btSocket.connect();
            //Log.d(TAG, "...Connection established and data link opened...");
            Toast.makeText(getBaseContext(),
                    "Connected", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        //Log.d(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
            inStream=btSocket.getInputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {     //USE this for closing bluetooth on pause, it calls errorExit which calls finish()
        super.onPause();

        //Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
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

    private void errorExit(String title, String message){
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();


        //Log.d(TAG, "...Sending data: " + message + "...");

        try {
            outStream.write(msgBuffer);
            //put outstream.flush here
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }

}
