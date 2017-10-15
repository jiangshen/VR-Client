package caden.vrclient;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelerometer;
    private Sensor magnometer;

    private Sensor gyrometer;

    private float[] accelOutput;
    private float[] magOutput;

    private float[] orientation = new float[3];
    private float[] startOrientation = null;


    WebView wv1;
    WebView wv2;
    TextView tvGyro;

    String url;

    ServerConnect svc;

    private final String UDPIP = "192.168.8.160";

    

    private final int SEND_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);;
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyrometer = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_GAME);

        tvGyro = (TextView) findViewById(R.id.tv_gyro);

        url = "http://192.168.8.174:8080/stream_simple.html";

        wv1 = (WebView) findViewById(R.id.web_view1);
        wv2 = (WebView) findViewById(R.id.web_view2);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv2.getSettings().setJavaScriptEnabled(true);

        wv1.setWebViewClient(new WebViewClient());
        wv2.setWebViewClient(new WebViewClient());

        wv1.setInitialScale(145);
        wv2.setInitialScale(145);

//        wv1.getSettings().setLoadWithOverviewMode(true);
//        wv1.getSettings().setUseWideViewPort(true);
//
//        wv2.getSettings().setLoadWithOverviewMode(true);
//        wv2.getSettings().setUseWideViewPort(true);

        wv1.loadUrl(url);
        wv2.loadUrl(url);

        startOrientation = null;

//        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensor = sm.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
//
//        final float[] rotationMatrix = new float[9];
//
//        sm.getRotationMatrix(rotationMatrix, null,
//                accelerometerReading, magnetometerReading);
//
//        final float[] orientationAngles = new float[3];
//        sm.getOrientation(rotationMatrix, orientationAngles);
//
//        gyro = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
//        sm.registerListener(this, gyro, SensorManager.SENSOR_DELAY_GAME);

        svc = new ServerConnect();

        sendOrientation();
    }

    public void sendOrientation() {

       final JSONObject data = new JSONObject();
        try {
            data.put("yaw", 30);
            data.put("pitch", -120);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                svc.sendMessage(UDPIP, 4000, data.toString());

                handler.postDelayed(this, SEND_INTERVAL);
            }
        }, SEND_INTERVAL);

    }


    public void pause() {
        manager.unregisterListener(this);
    }

    public float radToDeg(float f) {
        return f / (float)Math.PI * 180;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelOutput = event.values;
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magOutput = event.values;
        if(accelOutput != null && magOutput != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput);
            if(success) {
                SensorManager.getOrientation(R, orientation);
                if(startOrientation == null) {
                    startOrientation = new float[orientation.length];
                    System.arraycopy(orientation, 0, startOrientation, 0, orientation.length);
                }
            }
        }

        String output = "";

        if(orientation != null && startOrientation != null) {

            float yall = orientation[0] - startOrientation[0];
            float pitch = orientation[1] - startOrientation[1];
            float roll = orientation[2] - startOrientation[2];

            float xSpeed = 2 * roll * getWindowManager().getDefaultDisplay().getWidth() / 1000f;
            float ySpeed = pitch * getWindowManager().getDefaultDisplay().getHeight() / 1000f;

            output = String.format("Pitch: %.2f, Roll: %.2f, Yall: %.2f, xSpeed: %.2f, ySpeed: %.2f", radToDeg(pitch), radToDeg(roll), radToDeg(yall), xSpeed, ySpeed);

        }

        tvGyro.setText(output);

//        final float alpha = 0.8f;
//
//        // Isolate the force of gravity with the low-pass filter.
//        double[] gravity = new double[3];
//
//        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//        // Remove the gravity contribution with the high-pass filter.
//        double xd, yd , zd;
//        xd = event.values[0] - gravity[0];
//        yd = event.values[1] - gravity[1];
//        zd = event.values[2] - gravity[2];
//
//        String x = String.format("%.3f", xd);
//        String y = String.format("%.3f", yd);
//        String z = String.format("%.3f", zd);
//
//        tvGyro.setText("x: " + x + " y: " + y + " z: " + z);
//
//        Log.d("X", x);
//        Log.d("Y", y);
//        Log.d("Z", z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
