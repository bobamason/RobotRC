package org.masonapps.robotcontrol.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import org.masonapps.robotcontrol.R;

import java.text.DecimalFormat;
import java.util.HashMap;


public class DepthView extends View {

    private static final PolarCoordinate polar = new PolarCoordinate();
    private TextPaint textPaint;
    private float textWidth;
    private float textHeight;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int width;
    private int height;
    private HashMap<Integer, Float> depthReadings;
    private int lineColor;
    private int depthColor;
    private float density;
    private RectF ovalRect;
    private PointF origin;
    private float radius;
    private Paint linePaint;
    private Paint bgPaint;
    private Paint depthPaint;
    private Path path;
    private int bgColor;
    private static final DecimalFormat formatter = new DecimalFormat("###.#");
    private float max;
    private float unitsToPixels;
    private String units;

    public DepthView(Context context) {
        super(context);
        init(null, 0);
    }

    public DepthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DepthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DepthView, defStyle, 0);
        
        depthReadings = new HashMap<>();

        max = a.getInt(R.styleable.DepthView_max, 150);
        lineColor = a.getColor(R.styleable.DepthView_lineColor, Color.GREEN);
        bgColor = a.getColor(R.styleable.DepthView_backgroundColor, Color.argb(32, Color.red(lineColor), Color.green(lineColor), Color.blue(lineColor)));
        depthColor = a.getColor(R.styleable.DepthView_depthReadingColor, Color.GREEN);
        if(a.hasValue(R.styleable.DepthView_units)){
            units = a.getString(R.styleable.DepthView_units);
        } else{
            units = "";
        }
        a.recycle();
        
        ovalRect = new RectF();
        origin = new PointF();
        path = new Path();
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);
        depthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        depthPaint.setColor(depthColor);
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        invalidateMeasurements();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateMeasurements();
    }

    private void invalidateMeasurements() {
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        width = getWidth() - paddingLeft - paddingRight;
        height = getHeight() - paddingTop - paddingBottom;
        density = getResources().getDisplayMetrics().density;
        
        origin.set(width * 0.5f + paddingLeft, height - paddingBottom);
        radius = Math.min(width, height * 2) * 0.5f;
        //don't divide by zero
        if(max != 0) unitsToPixels = radius / max;
        else unitsToPixels = 1;
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f * density);
        depthPaint.setStyle(Paint.Style.STROKE);
        depthPaint.setStrokeWidth(3f * density);
        
        textPaint.setTextSize(8f * density);
        textPaint.setColor(lineColor);
        textWidth = textPaint.measureText("000.0");
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textHeight = fontMetrics.bottom;
        
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ovalRect.set(origin.x - radius, origin.y - radius, origin.x + radius, origin.y + radius);
        canvas.drawArc(ovalRect, 180, 180, true, bgPaint);
        
        canvas.drawArc(ovalRect, 180, 180, false, linePaint);
        canvas.drawText(formatter.format(max) + units, origin.x, ovalRect.top - textHeight, textPaint);
        
        float r1 = radius * 0.25f;
        ovalRect.set(origin.x - r1, origin.y - r1, origin.x + r1, origin.y + r1);
        canvas.drawArc(ovalRect, 180, 180, false, linePaint);
        canvas.drawText(formatter.format(max * 0.25f) + units, origin.x, ovalRect.top - textHeight, textPaint);
        
        float r2 = radius * 0.5f;
        ovalRect.set(origin.x - r2, origin.y - r2, origin.x + r2, origin.y + r2);
        canvas.drawArc(ovalRect, 180, 180, false, linePaint);
        canvas.drawText(formatter.format(max * 0.5f) + units, origin.x, ovalRect.top - textHeight, textPaint);
        
        float r3 = radius * 0.75f;
        ovalRect.set(origin.x - r3, origin.y - r3, origin.x + r3, origin.y + r3);
        canvas.drawArc(ovalRect, 180, 180, false, linePaint);
        canvas.drawText(formatter.format(max * 0.75f) + units, origin.x, ovalRect.top - textHeight, textPaint);

        path.reset();
        int i = 0;
        for (int a = 0; a <= 180; a++) {
            if(!depthReadings.containsKey(a))continue;
            polar.setDegrees(depthReadings.get(a) * unitsToPixels, a);
            polar.a *= -1f;
            if(i == 0) path.moveTo(polar.getX() + origin.x, polar.getY() + origin.y);
            else path.lineTo(polar.getX() + origin.x, polar.getY() + origin.y);
            i++;
        }
        canvas.drawPath(path, depthPaint);
    }
    
    public void updateDepth(int angle, float depth) {
        depthReadings.put(angle, depth);
    }
}
