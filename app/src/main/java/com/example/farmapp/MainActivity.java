package com.example.farmapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //-------------------------------Bluetooth Variables-------------------------------------------------------------//
    //Bluetooth permission code
    final int BLUETOOTH_PERMISSION_CODE = 1;
    //Universally Unique IDentifier of the hc-05
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //BluetoothAdapter object to use the local bluetooth module of the device
    BluetoothAdapter bluetoothAdapter;
    //BluetoothSocket object to use to create a communication link between the device and the hc-05
    BluetoothSocket bluetoothSocket;
    //Set an output stream, to send data to the hc-05
    OutputStream outputStream;
    //Set an input stream, to send data to the hc-05
    InputStream inputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------------------------Home Page Variables (Control Options)--------------------------------------------//
        final String selfControlCommand = "SelfControl";
        final String manualControlCommand = "ManualControl";
        TextView selfControlText = findViewById(R.id.selfcontroltext);
        TextView manualControlText = findViewById(R.id.manualcontroltext);
        ImageButton selfControlImageButton = findViewById(R.id.selfcontrolbutton);
        ImageButton manualControlImageButton = findViewById(R.id.manualcontrolbutton);

        //Get the default bluetooth module of the device to be currently used
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Check to see if we were successful in getting the bluetooth of the current device
        if (bluetoothAdapter == null) {
            //Provide a message saying that current device does not support bluetooth
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Provide a message saying that current does support bluetooth
            Toast.makeText(this, "This device supports bluetooth!", Toast.LENGTH_SHORT).show();
        }
        //Check to see if permission was grant, call method if permission to use bluetooth module is not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
            return;
        }
        //Check for devices/bluetooth enable devices that are connected to the current device using the mobile application
        System.out.println(bluetoothAdapter.getBondedDevices());

        //Create an object of the hc-05 that is connected to your current device using its MAC address
        BluetoothDevice hc05 = bluetoothAdapter.getRemoteDevice("00:14:03:05:18:8E");
        //Check to see if this is the correct device
        System.out.println(hc05.getName());

        //Try to establish a communication socket
        do {
            try {
                bluetoothSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                System.out.println("Socket communication created!: " + bluetoothSocket);
                bluetoothSocket.connect();
                System.out.println(bluetoothSocket.isConnected());
            } catch (IOException e) {
                System.out.println("Socket communication could not be created");
            }
        }while(!bluetoothSocket.isConnected());

            selfControlImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "You clicked for self control mode", Toast.LENGTH_SHORT).show();
                    provideOutputStream(selfControlCommand);
                }
            });

            manualControlImageButton.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "You clicked for manual control mode", Toast.LENGTH_SHORT).show();
                    provideOutputStream(manualControlCommand);
                }
            }));




        //Send sample output stream to the hc-05
        /*
        try {
            outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(48);
            System.out.println("Output stream being sent out");
        } catch (IOException e) {
            System.out.println("Could not send out valid return statement");
        }

         */


        //Close the communication socket
        /*
        try{

            bluetoothSocket.close();
            System.out.println("Socket communication is now closed!");
        } catch (IOException e) {
            System.out.println("Socket communication could not be closed!");
        }

         */



    }

    //Method to send output stream to the FARM
    private void provideOutputStream(String command) {

        try{
            outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(command.getBytes(StandardCharsets.UTF_8));
            System.out.println("Output stream being sent out");
        } catch (IOException e) {
            System.out.println("Could not send out valid output stream");
        }
    }

    //Method to ask for access for the bluetooth on the current device
    private void requestBluetoothPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("This is permission to connect to local bluetooth on this device")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_CODE);
        }
    }

    //Check whether the user accepted permission access or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}