package com.example.kolin.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by kolin on 04.01.2017.
 */

public class CircleProgressView extends View {

    private static final int START_OFFSET = 0;

    //dimensions
    private float progressWidth = 12;
    private float arcWidth = 10;
    private float textSize = 18;

    //progresses
    private int goalProgress = 360;
    private int currentProgress = 180;

    //colors
    private int progressColor;
    private int arcColor;

    private Paint paintProgress;
    private Paint paintArc;
    private Paint paintText;

    private RectF arcRectF = new RectF();
    private Rect textRect = new Rect();

    private ValueAnimator currentAmountAnimator;


    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        float density = getDensity();

        progressColor = ContextCompat.getColor(context, R.color.colorAccent);
        arcColor = ContextCompat.getColor(context, R.color.colorGrey);
        progressWidth = (int) (progressWidth * density);
        arcWidth = (int) (arcWidth * density);
        textSize = (int) (textSize * density);


        if (attrs != null) {
            final TypedArray a =
                    context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView, 0, 0);

            progressWidth = (int) a.getDimension(R.styleable.CircleProgressView_progressWidth, progressWidth);
            arcWidth = (int) a.getDimension(R.styleable.CircleProgressView_arcWidth, arcWidth);
            textSize = a.getDimension(R.styleable.CircleProgressView_textSize, textSize);

            progressColor = a.getColor(R.styleable.CircleProgressView_progressColor, progressColor);
            arcColor = a.getColor(R.styleable.CircleProgressView_arcColor, arcColor);

            goalProgress = (int) a.getFloat(R.styleable.CircleProgressView_goalProgress, goalProgress);
            currentProgress = (int) a.getFloat(R.styleable.CircleProgressView_currentProgress, currentProgress);

            a.recycle();
        }

        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArc.setColor(arcColor);
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(arcWidth);

        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintProgress.setColor(progressColor);
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setStrokeWidth(progressWidth);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(progressColor);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(textSize);
        paintText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if ((width / getDensity()) < 100 || (height / getDensity()) < 100){
            throw new IllegalArgumentException("Width or Height must be more than 100dp");
        }


        final int min = Math.min(width, height);

        int arcDiameter = min - getPaddingStart() - getPaddingEnd();
        float top = min / 2 - (arcDiameter / 2);
        float left = min / 2 - (arcDiameter / 2);

        arcRectF.set(left, top, left + arcDiameter, top + arcDiameter);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        String text = String.valueOf(currentProgress);
        paintText.getTextBounds(text,
                0,
                text.length(),
                textRect);

        int xPos = canvas.getWidth() / 2 - textRect.width() / 2;
        int yPos = (int) (arcRectF.centerY() - (paintText.descent() + paintText.ascent()) / 2);

        int progress = (360 * currentProgress) / goalProgress;

        canvas.drawText(String.valueOf(currentProgress), xPos, yPos, paintText);
        canvas.drawArc(arcRectF, START_OFFSET, 360, false, paintArc);
        canvas.drawArc(arcRectF, START_OFFSET, progress, false, paintProgress);
    }

    public float getProgressWidth() {
        return progressWidth;
    }

    private float getDensity(){
        return getResources().getDisplayMetrics().density;
    }

    public void setProgressWidth(float progressWidth) {
        if (progressWidth > 0) {
            this.progressWidth = progressWidth;
            paintProgress.setStrokeWidth(progressWidth);
            invalidate();
        } else {
            throw new IllegalArgumentException("Progress width must be more than 0");
        }
    }

    public float getArcWidth() {
        return arcWidth;
    }

    public void setArcWidth(float arcWidth) {
        if (arcWidth > 0) {
            this.arcWidth = arcWidth;
            paintArc.setStrokeWidth(arcWidth);
            invalidate();
        } else {
            throw new IllegalArgumentException("Arc width must be more than 0");
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public int getGoalProgress() {
        return goalProgress;
    }

    public void setGoalProgress(int goalProgress) {
        if (goalProgress > 1) {
            this.goalProgress = goalProgress;
            invalidate();
        } else {
            throw new IllegalArgumentException("Goal must be more than 1");
        }
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        paintProgress.setColor(progressColor);
        invalidate();
    }

    public int getArcColor() {
        return arcColor;
    }

    public void setArcColor(int arcColor) {
        this.arcColor = arcColor;
        paintArc.setColor(arcColor);
        invalidate();
    }

    public void setCurrentProgress(int currentProgress) {
        if (currentProgress > 0 && currentProgress <= this.goalProgress) {
            setAnimator(currentProgress, true);
        } else {
            if (currentProgress < 0)
                throw new IllegalStateException("current progress must be more than 0 and");
            if (currentProgress > 0 && currentProgress > this.goalProgress)
                throw new IllegalStateException("current progress must be smaller then goal");
        }
    }

    private void setAnimator(final int currentProgress, boolean animate) {

        if (animate) {
            currentAmountAnimator = ValueAnimator.ofFloat(0, 1);
            currentAmountAnimator.setDuration(1000);

            setAnimator(0, false);

            currentAmountAnimator.setInterpolator(new DecelerateInterpolator());

            currentAmountAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float inter = (float) animation.getAnimatedValue();
                    setAnimator((int) (inter * currentProgress), false);
                }
            });

            if (!currentAmountAnimator.isStarted()) {
                currentAmountAnimator.start();
            }
        } else {
            this.currentProgress = currentProgress;
            postInvalidate();
        }
    }
}
