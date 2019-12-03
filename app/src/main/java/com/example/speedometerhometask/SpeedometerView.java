package com.example.speedometerhometask;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpeedometerView extends View {
    private int mProgress;
    private static final Paint SPEEDOMETER_ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint CENTER_ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARROW_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float FONT_SIZE = 112f;
    private Rect mTextBounds = new Rect();
    private static final Path ARROW_PATH = new Path();
    private static final float STROKE_WIDTH = 48f;
    private static final float STROKE_WIDTH_CENTER = 40f;
    private static final float RADIUS = 400f;
    private static final float RADIUS_CENTER = 40f;
    private static final RectF ARC_RECT = new RectF(STROKE_WIDTH / 2,
            STROKE_WIDTH / 2,
            2 * RADIUS,
            2 * RADIUS);
    private static final RectF ARC_RECT_CENTER = new RectF(RADIUS + (STROKE_WIDTH / 2) - STROKE_WIDTH_CENTER,
            RADIUS + (STROKE_WIDTH / 2) - STROKE_WIDTH_CENTER,
            RADIUS + (STROKE_WIDTH / 2) + STROKE_WIDTH_CENTER,
            RADIUS + (STROKE_WIDTH / 2) + STROKE_WIDTH_CENTER);
    private static final int MAX_PROGRESS = 220;
    private static final float MAX_ANGLE = 270f;
    private static final float START_ANGLE = 135f;
    private static final float MAX_ANGLE_CENTER = 360f;
    private static final float START_ANGLE_CENTER = 0f;
    final float ARROW_LENGTH = RADIUS - 64f;
    final float ARROW_RADIUS = RADIUS_CENTER - 20f;
    float CENTER_X = RADIUS + STROKE_WIDTH / 2;
    float CENTER_Y = RADIUS + STROKE_WIDTH / 2;
    @ColorInt
    private int mFirstColor;
    @ColorInt
    private int mSecondColor;
    @ColorInt
    private int mThirdColor;
    @ColorInt
    private int mFourthColor;
    @ColorInt
    private int mFifthColor;
    @ColorInt
    private int mTextColor;
    @ColorInt
    private int mArrowColor;


    public SpeedometerView(Context context) {
        this(context, null);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(ARC_RECT, START_ANGLE, MAX_ANGLE, false, SPEEDOMETER_ARC_PAINT);
        canvas.drawArc(ARC_RECT_CENTER, START_ANGLE_CENTER, MAX_ANGLE_CENTER, false, CENTER_ARC_PAINT);

        drawText(canvas);
        drawArrow(canvas);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        extractAttributes(context, attrs);
        configureGradientArc();
        configureCenterDot();
        configureText();
        configureArrow();
    }

    private void extractAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            final Resources.Theme theme = context.getTheme();
            final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.SpeedometerView, 0, R.style.SpeedometerDefault);
            try {
                mProgress = typedArray.getInt(R.styleable.SpeedometerView_progress, 0);
                mFirstColor = typedArray.getColor(R.styleable.SpeedometerView_first_color, Color.GREEN);
                mSecondColor = typedArray.getColor(R.styleable.SpeedometerView_second_color, Color.YELLOW);
                mThirdColor = typedArray.getColor(R.styleable.SpeedometerView_third_color, Color.argb(255, 255, 165, 0));
                mFourthColor = typedArray.getColor(R.styleable.SpeedometerView_fourth_color, Color.argb(255, 255, 140, 0));
                mFifthColor = typedArray.getColor(R.styleable.SpeedometerView_fifth_color, Color.RED);
                mTextColor = typedArray.getColor(R.styleable.SpeedometerView_text_color, Color.BLACK);
                mArrowColor = typedArray.getColor(R.styleable.SpeedometerView_arrow_color, Color.RED);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void configureGradientArc() {
        SweepGradient gradient = new SweepGradient(RADIUS + STROKE_WIDTH / 2,
                RADIUS + STROKE_WIDTH / 2,
                new int[]{mFirstColor, mSecondColor, mThirdColor, mFourthColor, mFifthColor},
                new float[]{0.2f, 0.4f, 0.45f, 0.5f, 1f});
        Matrix gradientMatrix = new Matrix();
        gradientMatrix.preRotate(START_ANGLE,
                RADIUS + STROKE_WIDTH / 2,
                RADIUS + STROKE_WIDTH / 2);
        gradient.setLocalMatrix(gradientMatrix);
        SPEEDOMETER_ARC_PAINT.setShader(gradient);
        SPEEDOMETER_ARC_PAINT.setStyle(Paint.Style.STROKE);
        SPEEDOMETER_ARC_PAINT.setStrokeWidth(STROKE_WIDTH);
    }

    private void configureCenterDot() {
        CENTER_ARC_PAINT.setColor(mFifthColor);
        CENTER_ARC_PAINT.setStyle(Paint.Style.FILL);
        CENTER_ARC_PAINT.setStrokeWidth(STROKE_WIDTH_CENTER);
    }

    private void configureArrow() {
        ARROW_PAINT.setColor(mFifthColor);
        ARROW_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);


    }

    private void drawArrow(Canvas canvas) {
        ARROW_PATH.reset();
        float angle = (START_ANGLE + mProgress * MAX_ANGLE / MAX_PROGRESS) * (float) Math.PI / 180f;
        float angle_for_line = (START_ANGLE + 90f + mProgress * MAX_ANGLE / MAX_PROGRESS) * (float) Math.PI / 180f;
        ARROW_PATH.moveTo(CENTER_X, CENTER_Y);
        ARROW_PATH.lineTo(CENTER_X + (float) Math.cos(angle) * ARROW_LENGTH,
                CENTER_Y + (float) Math.sin(angle) * ARROW_LENGTH);
        ARROW_PATH.lineTo(CENTER_X + (float) Math.cos(angle_for_line) * ARROW_RADIUS,
                CENTER_Y + (float) Math.sin(angle_for_line) * ARROW_RADIUS);
        ARROW_PATH.lineTo(CENTER_X - (float) Math.cos(angle_for_line) * ARROW_RADIUS,
                CENTER_Y - (float) Math.sin(angle_for_line) * ARROW_RADIUS);
        ARROW_PATH.lineTo(CENTER_X + (float) Math.cos(angle) * ARROW_LENGTH,
                CENTER_Y + (float) Math.sin(angle) * ARROW_LENGTH);
        ARROW_PATH.close();
        canvas.drawPath(ARROW_PATH, ARROW_PAINT);
    }

    private void configureText() {
        TEXT_PAINT.setColor(mTextColor);
        TEXT_PAINT.setStyle(Paint.Style.FILL);
        TEXT_PAINT.setTextSize(FONT_SIZE);
    }

    private void getTextBounds(@NonNull String progressString) {
        TEXT_PAINT.getTextBounds(progressString, 0, progressString.length(), mTextBounds);
    }

    private String formatString(int progress) {
        return String.format(getContext().getString(R.string.speed_format), progress);
    }


    private void drawText(Canvas canvas) {
        final String progressString = formatString(mProgress);
        getTextBounds(progressString);
        float x = ARC_RECT.width() / 2f - mTextBounds.width() / 2f - mTextBounds.left + ARC_RECT.left;
        float y = ARC_RECT.height();
        canvas.drawText(progressString, x, y, TEXT_PAINT);
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }
}
