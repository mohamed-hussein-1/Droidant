package com.escobar.pable.snake;

/**
 * Created by Professor on 4/29/2017.
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothArduinoHelper extends Thread {
    private BluetoothAdapter mBlueAdapter = null;
    private BluetoothSocket mBlueSocket = null;
    private BluetoothDevice mBlueRobo = null;

    OutputStream mOut;
    InputStream mIn;

    private boolean robotFound = false;
    private boolean connected = false;

    private String robotName;
    private List<String> mMessages = new ArrayList<String>();
    private final String TAG = "BluetoothConnector";
    private char DELIMITER = '\n';

    private static BluetoothArduinoHelper __blue = null;

    /**
     * Get instance of the BluetoothArduinoHelper
     *
     * @param n name of the robot
     * @return BluetoothArduinoHelper instance
     */
    public static BluetoothArduinoHelper getInstance(String n) {
        return __blue == null ? new BluetoothArduinoHelper(n) : __blue;
    }

    /**
     * Create a static copy of the helper
     *
     * @param Name
     */
    private BluetoothArduinoHelper(String Name) {
        __blue = this;
        try {
            robotName = Name;
            mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e) {
            LogError("\t\t[#]Erro creating Bluetooth! : " + e.getMessage());

        }
    }

    /**
     * Check if the bluetooth is enabled
     *
     * @return boolean
     */
    public boolean isBluetoothEnabled() {
        return mBlueAdapter.isEnabled();
    }

    /**
     * Connect to the Arduino
     * @return boolean to show if the connection was successfully
     * @throws Exception
     */
    public boolean Connect() throws Exception {
        if (mBlueAdapter == null)
            throw new Exception("[#]Phone does not support bluetooth!!");

        if (!isBluetoothEnabled())
            throw new Exception("[#]Bluetooth is not activated!!");


        Set<BluetoothDevice> paired = mBlueAdapter.getBondedDevices();
        if (paired.size() > 0) {
            for (BluetoothDevice d : paired) {
                Log.i("Snake","name : " + d.getName());
                if (d.getName().equals(robotName)) {
                    mBlueRobo = d;
                    robotFound = true;
                    break;
                }
            }
        }

        if (!robotFound)
            throw new Exception("\t\t[#]There is not paired robot with the name " + robotName);

        LogMessage("\t\tConncting to the robot...");

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try{
            mBlueSocket = mBlueRobo.createRfcommSocketToServiceRecord(uuid);
            mBlueSocket.connect();
        }
        catch (Exception e){
            return false;
        }
//        mBlueSocket = mBlueRobo.createRfcommSocketToServiceRecord(uuid);
//        mBlueSocket.connect();
        mOut = mBlueSocket.getOutputStream();
        mIn = mBlueSocket.getInputStream();

        connected = true;
        this.start();

        LogMessage("Connected! " + mBlueAdapter.getName());
        return true;
    }

    /**
     * Run a thread to read and receive messages
     */
    public void run() {
        while (true) {
            if (connected) {
                Log.i("Snake Loop","Conection is on");
                try {

                    byte ch, buffer[] = new byte[1024];
                    int i = 0;

                    String s = "";
                    while ((ch = (byte) mIn.read()) != DELIMITER) {
                        Log.i("Snake Loop","Buffer on " + buffer[i]);
                        buffer[i++] = ch;
                    }
                    buffer[i] = '\0';
                    Log.i("Snake Loop","Buffer is " + Arrays.toString(buffer));
                    final String msg = new String(buffer);

                    messageReceived(msg.trim());
                    LogMessage("[Blue]:" + msg);

                } catch (IOException e) {
                    LogError("->[#]Failed to receive message: " + e.getMessage());
                }
            }
        }
    }

    /**
     * A message was received. Add it to the list
     * @param msg received messaged
     */
    private void messageReceived(String msg) {
        mMessages.add(msg);
    }

    /**
     * Get a message by the position in the List
     * @param i position of the message
     * @return "" if the position is not valid. Otherwise the message
     */
    public String getMenssage(int i) {
        if (i >= 0 && i < mMessages.size())
            return mMessages.get(i);
        return "";
    }

    /**
     * Clear the list of received messages
     */
    public void clearMessages() {
        mMessages.clear();
    }

    /**
     * Count the received messages
     * @return int
     */
    public int countMessages() {
        return mMessages.size();
    }

    /**
     * Returns the last message received
     * @return
     */
    public String getLastMessage() {
        if (countMessages() == 0)
            return "";
        return mMessages.get(countMessages() - 1);
    }

    /**
     * Send a message to the connected Arduino
     * @param msg String with the message
     */
    public void sendMessage(String msg) {
        try {
            if (connected) {
                mOut.write(msg.getBytes());
                Log.i("Snake","inside send Message Connected");
            }

        } catch (IOException e) {
            LogError("->[#]Error while sending message: " + e.getMessage());
        }
    }

    /**
     * Log an message
     * @param msg
     */
    private void LogMessage(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * Log an error
     * @param msg
     */
    private void LogError(String msg) {
        Log.e(TAG, msg);
    }

    /**
     * Set the end of message char delimiter. Defaults to '\n'
     * @param d char delimiter
     */
    public void setDelimiter(char d) {
        DELIMITER = d;
    }

    /**
     * Get the end of message char delimier
     * @return char
     */
    public char getDelimiter() {
        return DELIMITER;
    }
}