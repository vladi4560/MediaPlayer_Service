package com.vladi.karasove.mediaplayerservice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.vladi.karasove.mediaplayerservice.CallBack.CallBack_SensorToMain;

public class SensorMangerACC {
    private SensorManager sensorManager;
    private Sensor sensor;
    private CallBack_SensorToMain callBack_sensorToMain;
    private boolean switchSongOnce= false;
    private boolean sensorWorking= false;
    public SensorMangerACC() {}

    public void setCallBack_sensorToMain(CallBack_SensorToMain cl){
        callBack_sensorToMain = cl;
    }

    public SensorMangerACC(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(gyroListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//        sensorWorking= true;
    }
    public void startSensor(){
        sensorManager.registerListener(gyroListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorWorking= true;
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];

            if((x<-5 || x>5)&& switchSongOnce){
                switchSongOnce = false;
                callBack_sensorToMain.sensorSongChange((int)x);
            }
            if (-0.5<x && x<0.5){
                switchSongOnce= true;
            }
        }
    };
    public void StopSensor(){
        sensorManager.unregisterListener(gyroListener);
        sensorWorking= false;
    }
}
