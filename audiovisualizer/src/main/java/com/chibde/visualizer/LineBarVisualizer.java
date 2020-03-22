/*
* Copyright (C) 2017 Gautam Chibde
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.chibde.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.chibde.BaseVisualizer;

/**
 * Custom view that creates a Line and Bar visualizer effect for
 * the android {@link android.media.MediaPlayer}
 * <p>
 * Created by gautam chibde on 22/11/17.
 */

public class LineBarVisualizer extends BaseVisualizer {
    private Paint middleLine;
    private float density;
    private int gap;

    public LineBarVisualizer(Context context) {
        super(context);
    }

    public LineBarVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LineBarVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        density = 50;
        gap = 4;
        middleLine = new Paint();
        middleLine.setColor(Color.parseColor("#FD1476"));
    }

    /**
     * Sets the density to the Bar visualizer i.e the number of bars
     * to be displayed. Density can vary from 10 to 256.
     * by default the value is set to 50.
     *
     * @param density density of the bar visualizer
     */
    public void setDensity(float density) {
        if (this.density > 180) {
            this.middleLine.setStrokeWidth(1);
            this.gap = 1;
        } else {
            this.gap = 4;
        }
        this.density = density;
        if (density > 256) {
            this.density = 250;
            this.gap = 0;
        } else if (density <= 10) {
            this.density = 10;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if (middleLine.getColor() != Color.BLUE) {
//            middleLine.setColor(color);
//        }
        if (bytes != null) {
            float barWidth = getWidth() / density;
            float div = bytes.length / density;
            canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, middleLine);
            paint.setStrokeWidth(barWidth - gap);

            float amp = 2;
            float ampvar = 50;
            for (int i = 0; i < density; i++) {
                int bytePosition = (int) Math.ceil(i * div);
                float top =  (getHeight() / amp
                                        + (128 - Math.abs(bytes[bytePosition]))
                                        * (getHeight() / amp) / 128);

                float bottom = getHeight() / amp
                        - (128 - Math.abs(bytes[bytePosition]))
                        * (getHeight() / amp) / 128;

                float barX = (i * barWidth) + (barWidth / 2);
                Log.e("visulizers","top "+top+" bottom "+bottom +" barx "+top+" height "+getHeight());

//                barX -= (2-amp);

//                canvas.drawRect(barX,getHeight()/2,barWidth,bottom,paint);

                float offset = getHeight() /ampvar;


                canvas.drawLine(barX, bottom, barX, getHeight()/amp, paint);
                canvas.drawLine(barX, top, barX, getHeight() / amp, paint);
//                ampvar *=0.998;
            }
            super.onDraw(canvas);
        }
    }
}
