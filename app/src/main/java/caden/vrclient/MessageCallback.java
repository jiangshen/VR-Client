package caden.vrclient;

/**
 * Created by caden on 10/14/17.
 */


/**
 * Callback Interface for sending received messages to 'onPublishProgress' method in AsyncTask.
 *
 */
public interface MessageCallback {
    /**
     * Method overridden in AsyncTask 'doInBackground' method while creating the TCPClient object.
     * @param message Received message from server app.
     */
    public void callbackMessageReceiver(String message);
}
