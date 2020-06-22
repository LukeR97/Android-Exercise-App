package com.example.assignment03;

//Interface to listen for step alerts from device
public interface StepListener {

    void step(long timeNs);
}
