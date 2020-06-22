package com.example.assignment03;

//--------------------------------------
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
//--------------------------------------

//Our CustomView class
public class CustomView extends View {

    //array to store the points to graph of our step counter
    int graphPoints[] = null;
    //variable to store the last X coord
    int topPoint = 0;
    //This variable will adjust the line space sensitivity. Do not adjust
    int lineCount = 130;
    //array to store the points to graph of our altitude monitor
    String altGraphPoints[] = null;
    //This arrayList is used to take our altitudes from the DB and temporarily store them
    ArrayList<Float> altValues = new ArrayList<>();
    //variable used to store altValues values. It has to be a primitive array type as canvas.drawLine
    //does not accept ArrayList element locations
    float[] altGraph;

    //2 colours used
    Paint step_line_colour;
    Paint alt_line_colour;

    //our required constructors for custom view
    public CustomView(Context c){
        super(c);
        init();
    }

    public CustomView(Context c, AttributeSet as){
        super(c, as);
        init();
    }

    public CustomView(Context c, AttributeSet as, int default_style){
        super(c, as, default_style);
        init();
    }

    //initialisation method
    public void init(){

        step_line_colour = new Paint(Paint.ANTI_ALIAS_FLAG);
        step_line_colour.setColor(Color.BLACK);
        step_line_colour.setStrokeWidth(10.f);

        alt_line_colour = new Paint(Paint.ANTI_ALIAS_FLAG);
        alt_line_colour.setColor(Color.BLUE);
        alt_line_colour.setStrokeWidth(10.f);

    }

    //Method for setting out graph points
    public void setGraphPoints(int graphPointsX[], int topPointY){
        graphPoints = graphPointsX;
        topPoint = topPointY;
    }

    //method for first time graph draw. (as there will be no registered top Y coord on init
    //this method will also take our altitude values
    public void setGraphPoints(int graphPointsX[], String graphPointsA[]){
        int topPointY = 0;
        for(int i =0; i < graphPointsX.length; i++){
            if(graphPointsX[i] > topPointY)
                topPointY = graphPointsX[i];
        }
        altGraphPoints = graphPointsA;
        setGraphPoints(graphPointsX, topPointY);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();

        //if either the steps or altitudes are null then don't draw anything
        if(graphPoints == null || altGraphPoints == null){
            return;
        }

        //this will be the value for the last step. IE if there were 10 steps, then topPointX
        //will be 10
        int topPointX = graphPoints.length;

        //get the width and height of the layout
        float x = getWidth() / (float)topPointX;
        float y = getHeight() / (float)topPoint;

        //x0,y0,x1,y1 are used for / steps as they increase only
        //sx,sy,ex,ey = start x, y end x, y
        //used to map the steps linearly
        for(int i = 1; i < graphPoints.length; i++){
            int x0 = i -1;
            int y0 = graphPoints[i-1];
            int x1 = i;
            int y1 = graphPoints[i];

            int sx = (int)(x0 * x);
            int sy = getHeight() - (int)(y0* y);
            int ex = (int)(x1 * x);
            int ey = getHeight() - (int)(y1 * y);
            canvas.drawLine(sx,sy,ex,ey,step_line_colour);
        }
        //need to init array in onDraw as we are now going to be placing altValues values in here
        altGraph = new float[altValues.size()];
        //Adding our altitude values to an arrayList of type Double
        for(int i = 0; i < altGraphPoints.length; i++){
            altValues.add(Float.parseFloat(altGraphPoints[i]));
        }

        //for loop to draw the altitude values. where line 1 ends, line 2 begins etc
        for(int i =0; i < altValues.size()-1; i++){
            canvas.drawLine(lineCount*i,altValues.get(i),
                    lineCount* (i+1),altValues.get(i+1),alt_line_colour);
        }
        canvas.restore();
    }
}
