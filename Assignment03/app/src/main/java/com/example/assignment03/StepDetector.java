package com.example.assignment03;

/*
StepDetector code from:
http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.XeTg9Oj7Q2w
 */

//This class will take values from the accelerometer and use our Sensor Filter to determine if the
//change is worth counting as a step
public class StepDetector {

    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;

    //sensitivity threshold.
    private static final float STEP_THRESHOLD = 50f;
    //put a delay between each step or else 1 step may be counted as 12 steps
    private static final int STEP_DELAY_NS = 250000000;

    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private long lastStepTimeNs = 0;
    private float oldVelocityEstimate = 0;

    private StepListener listener;

    public void registerListener(StepListener listener){
        this.listener = listener;
    }

    //As mentioned in the stepCounterActivity, when a change is detected, we take these values x,y,x
    //and add them to them a float array
    public void updateAccel(long timeNs, float x, float y, float z){
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        //First step is to update global z vector
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        //Using our SensorFilters sum,min and norm functions to determine our global z values
        float[] worldZ = new float[3];
        worldZ[0] = SensorFilter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = SensorFilter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = SensorFilter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalization_factor = SensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        //get the z value captured by our sensorfilter minus the normalized value calculated above
        float currentZ = SensorFilter.dot(worldZ, currentAccel) - normalization_factor;
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;


        float velocityEstimate = SensorFilter.sum(velRing);

        //HERE is specifically where we determine if the change detected by the sensor filter is
        //worth counting as a step
        if(velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                &&(timeNs - lastStepTimeNs > STEP_DELAY_NS)){
            //call the step function of the listener interface
            listener.step(timeNs);
            lastStepTimeNs = timeNs;
        }
        oldVelocityEstimate = velocityEstimate;
    }
}
