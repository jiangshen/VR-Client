package caden.vrclient;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.io.IOException;

/**
 * Created by caden on 10/14/17.
 */

public class ServerConnect {

//    Socket socket;
//    InputStream in;
//    OutputStream out;

    private final String TAG = "ServerConnect";


    public ServerConnect() {
    }

    public void sendMessage(String iAddr, int port, String msg) {

        try {
            DatagramSocket s = new DatagramSocket();
            InetAddress local = InetAddress.getByName(iAddr);
            DatagramPacket p = new DatagramPacket(msg.getBytes(), msg.length(), local, port);
            Log.d(TAG, "Sending Message through UDP");
            s.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
