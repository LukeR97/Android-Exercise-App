package com.example.assignment03;



//class to filter out unwanted values that may simulate a step
//When our accelerometer detects a change, the value of the change is fed into this class.
//we then determine if the change detected was great enough to warrant counting as a step
public class SensorFilter {

    //required constructor. Is kept empty as we only need the functions of SensorFilter
    private SensorFilter(){
    }

    //Float arrays are required as canvas.drawline, drawLines, drawPath etc require floats

    //Function to sum an array of floats
    public static float sum(float[] array){
        float retval = 0;
        for(int i = 0; i < array.length; i++){
            retval += array[i];
        }
        return retval;
    }

    //This returns the normalization of an array of floats
    public static float norm(float[] array){
        float retval = 0;
        for(int i = 0; i < array.length; i++){
            retval += array[i] * array[i];
        }
        return (float) Math.sqrt(retval);
    }

    //This function returns the Dot product of 2 float arrays
    public static float dot(float[] a, float[] b){
        float retval = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return retval;
    }
}
