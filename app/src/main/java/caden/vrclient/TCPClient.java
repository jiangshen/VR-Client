package caden.vrclient;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by caden on 10/14/17.
 */

public class TCPClient {
    private static final String TAG = "TCPClient";
    private Handler mHandler;
    private String ip, incomingMsg, cmd;

    BufferedReader in;
    PrintWriter out;
    private MessageCallback listener = null;
    private boolean mRun = false;

    public TCPClient(Handler mHandler, String command, String ipNumber, MessageCallback listener) {
        this.listener = listener;
        this.ip = ipNumber;
        this.cmd = command ;
        this.mHandler = mHandler;
    }

    /**
     * Public method for sending the message via OutputStream object.
     * @param message Message passed as an argument and sent via OutputStream object.
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
//            mHandler.sendEmptyMessageDelayed(MainActivity.SENDING, 1000);
            Log.d(TAG, "Sent Message: " + message);

        }
    }

    /**
     * Public method for stopping the TCPClient object ( and finalizing it after that ) from AsyncTask
     */
    public void stopClient(){
        Log.d(TAG, "Client stopped!");
        mRun = false;
    }
}