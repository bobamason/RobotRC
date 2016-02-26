package org.masonapps.robotcontrol.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.masonapps.robotcontrol.R;


public class D_PadView extends View {

    private static float PI = (float) Math.PI;
    private static float QTR_PI = PI / 4f;
    public static final float THREE_QTR_PI = QTR_PI * 3f;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int width;
    private int height;
    private int padColor;
    private int touchedColor;
    private static final PointF point = new PointF();
    private static final PolarCoordinate polar = new PolarCoordinate();
    private static final PolarCoordinate polar2 = new PolarCoordinate();
    private static final PolarCoordinate polar3 = new PolarCoordinate();
    private float radius;
    private PointF center;
    private OnDirectionPressedListener listener = null;
    public DirectionButton pressedButton = DirectionButton.NONE;
    private Paint arcPaint;
    private RectF ovalRect;
    private Path arrowPath;
    private Paint arrowPaint;
    private int arrowColor;
    private float arrowDimen;
    private float density;
    private RectF arrowRect;
    private float spacing;

    public enum DirectionButton {
        NONE, UP, DOWN, LEFT, RIGHT
    }

    public D_PadView(Context context) {
        super(context);
        init(null, 0);
    }

    public D_PadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public D_PadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.D_PadView, defStyle, 0);

        padColor = a.getColor(R.styleable.D_PadView_padColor, Color.GRAY);
        arrowColor = a.getColor(R.styleable.D_PadView_arrowColor, Color.WHITE);
        touchedColor = a.getColor(R.styleable.D_PadView_touchedColor, Color.rgb(Math.min(Color.red(padColor) + 40, 255), Math.min(Color.green(padColor) + 40, 255), Math.min(Color.blue(padColor) + 40, 255)));

        a.recycle();

        center = new PointF();
        ovalRect = new RectF();
        arrowRect = new RectF();
        arrowPath = new Path();
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.FILL);
        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setColor(arrowColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateMeasurements();
    }

    private void invalidateMeasurements() {
        density = getResources().getDisplayMetrics().density;
        spacing = 2f * density;
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        width = getWidth() - paddingLeft - paddingRight;
        height = getHeight() - paddingTop - paddingBottom;
        center.set(width * 0.5f + paddingLeft, height * 0.5f + paddingTop);
        radius = Math.min(width, height) * 0.5f - spacing * 4f;
        arcPaint.setStrokeWidth(radius * 0.5f);
        arrowDimen = radius * 0.125f;
        ovalRect.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        final float r = radius * 0.75f;
        arrowRect.set(center.x - r, center.y - r, center.x + r, center.y + r);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRightButton(canvas);
        drawUpButton(canvas);
        drawLeftButton(canvas);
        drawDownButton(canvas);
    }

    private void drawDownButton(Canvas canvas) {
        if(pressedButton == DirectionButton.DOWN) {
            ovalRect.offset(0, spacing * 2f);
            arcPaint.setColor(touchedColor);
        } else{
            ovalRect.offset(0, spacing);
            arcPaint.setColor(padColor);
        }
        canvas.drawArc(ovalRect, 45, 90, true, arcPaint);
        arrowPath.reset();
        arrowPath.moveTo(center.x, arrowRect.bottom + arrowDimen);
        arrowPath.lineTo(center.x + arrowDimen, arrowRect.bottom - arrowDimen);
        arrowPath.lineTo(center.x - arrowDimen, arrowRect.bottom - arrowDimen);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);
        ovalRect.offset(0, pressedButton == DirectionButton.DOWN ? -spacing * 2f : -spacing);
    }

    private void drawUpButton(Canvas canvas) {
        if(pressedButton == DirectionButton.UP) {
            ovalRect.offset(0, -spacing * 2f);
            arcPaint.setColor(touchedColor);
        } else{
            ovalRect.offset(0, -spacing);
            arcPaint.setColor(padColor);
        }
        canvas.drawArc(ovalRect, 225, 90, true, arcPaint);
        arrowPath.reset();
        arrowPath.moveTo(center.x, arrowRect.top - arrowDimen);
        arrowPath.lineTo(center.x - arrowDimen, arrowRect.top + arrowDimen);
        arrowPath.lineTo(center.x + arrowDimen, arrowRect.top + arrowDimen);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);
        ovalRect.offset(0, pressedButton == DirectionButton.UP ? spacing * 2f : spacing);
    }

    private void drawLeftButton(Canvas canvas) {
        if(pressedButton == DirectionButton.LEFT) {
            ovalRect.offset(-spacing * 2f, 0);
            arcPaint.setColor(touchedColor);
        } else{
            ovalRect.offset(-spacing, 0);
            arcPaint.setColor(padColor);
        }
        canvas.drawArc(ovalRect, 135, 90, true, arcPaint);
        arrowPath.reset();
        arrowPath.moveTo(arrowRect.left - arrowDimen, center.y);
        arrowPath.lineTo(arrowRect.left + arrowDimen, center.y + arrowDimen);
        arrowPath.lineTo(arrowRect.left + arrowDimen, center.y - arrowDimen);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);
        ovalRect.offset(pressedButton == DirectionButton.LEFT ? spacing * 2f : spacing, 0);
    }

    private void drawRightButton(Canvas canvas) {
        if(pressedButton == DirectionButton.RIGHT) {
            ovalRect.offset(spacing * 2f, 0);
            arcPaint.setColor(touchedColor);
        } else{
            ovalRect.offset(spacing, 0);
            arcPaint.setColor(padColor);
        }
        canvas.drawArc(ovalRect, 315, 90, true, arcPaint);
        arrowPath.reset();
        arrowPath.moveTo(arrowRect.right + arrowDimen, center.y);
        arrowPath.lineTo(arrowRect.right - arrowDimen, center.y - arrowDimen);
        arrowPath.lineTo(arrowRect.right - arrowDimen, center.y + arrowDimen);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);
        ovalRect.offset(pressedButton == DirectionButton.RIGHT ? -spacing * 2f : -spacing, 0);
    }

    private DirectionButton lastButton = DirectionButton.NONE;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            point.set(event.getX(), event.getY());
            //center as origin
            point.x -= center.x;
            point.y -= center.y;
            polar.setCartesian(point);
            if (polar.r < radius) {
                // angle is clockwise because -y is up
                if (polar.a > QTR_PI && polar.a < THREE_QTR_PI) {
                    pressedButton = DirectionButton.DOWN;
                } else if (polar.a > -QTR_PI && polar.a < QTR_PI) {
                    pressedButton = DirectionButton.RIGHT;
                } else if (polar.a > -THREE_QTR_PI && polar.a < -QTR_PI) {
                    pressedButton = DirectionButton.UP;
                } else {
                    pressedButton = DirectionButton.LEFT;
                }
                if (listener != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        listener.touchDown(pressedButton);
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        // if touch moves to different button call listener as if a new touch occurred
                        if(lastButton != pressedButton) {
                            listener.touchUp();
                            listener.touchDown(pressedButton);
                        }
                        else listener.touchMove(pressedButton);
                    }
                }
                invalidate();
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null) listener.touchUp();
            pressedButton = DirectionButton.NONE;
            invalidate();
            return true;
        } else {
            pressedButton = DirectionButton.NONE;
        }
        lastButton = pressedButton;
        return super.onTouchEvent(event);
    }

    public void setOnDirectionPressedListener(OnDirectionPressedListener listener) {
        this.listener = listener;
    }

    public interface OnDirectionPressedListener {
        void touchDown(DirectionButton directionButton);

        void touchMove(DirectionButton directionButton);

        void touchUp();
    }
}
