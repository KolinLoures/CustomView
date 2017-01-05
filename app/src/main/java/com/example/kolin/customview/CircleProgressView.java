package com.example.kolin.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.ValueCallback;

/**
 * Created by kolin on 04.01.2017.
 */

public class CircleProgressView extends View {

    private static final int ANGEL_OFFSET = -270;

    private float lineWidth;

    private int colorProgress;
    private int colorDefault;

    private float targetAmount;
    private float currentAmount;

    private RectF rectF = new RectF();

    private Paint paint;

    private ValueAnimator currentAmountAnimator;


    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            final TypedArray typedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView, 0, 0);

            lineWidth = typedArray.getDimension(R.styleable.CircleProgressView_lineWidth, 2.5f);

            targetAmount = typedArray.getFloat(R.styleable.CircleProgressView_targetAmount, 100);
            currentAmount = typedArray.getFloat(R.styleable.CircleProgressView_currentAmount, 25);

            colorProgress = typedArray.getColor(R.styleable.CircleProgressView_colorProgress,
                    ContextCompat.getColor(context, R.color.colorAccent));
            colorDefault = typedArray.getColor(R.styleable.CircleProgressView_colorDefault,
                    ContextCompat.getColor(context, R.color.colorGrey));


            typedArray.recycle();
        } else {
            colorProgress = ContextCompat.getColor(context, R.color.colorAccent);
            colorDefault = ContextCompat.getColor(context, R.color.colorGrey);

            lineWidth = 2.5f;
            targetAmount = 100;
            currentAmount = 25;
        }

        setLineWidth(lineWidth);
        setColorProgress(colorProgress);
        setColorDefault(colorDefault);
        setTargetAmount(targetAmount);
        setCurrentAmount(currentAmount);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int min = Math.min(width, height);

        int arcDiameter = min - getPaddingStart() - getPaddingEnd();
        float top = height / 2 - (arcDiameter / 2);
        float left = width / 2 - (arcDiameter / 2);

        rectF.set(left, top, left + arcDiameter, top + arcDiameter);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(colorDefault);
        canvas.drawArc(rectF, ANGEL_OFFSET, 360, false, paint);

        int progress = (int) ((360 * currentAmount) / targetAmount);

        paint.setColor(colorProgress);
        canvas.drawArc(rectF, ANGEL_OFFSET, progress, false, paint);
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    public void setColorProgress(int colorProgress) {
        this.colorProgress = colorProgress;
        invalidate();
    }

    public void setColorDefault(int colorDefault) {
        this.colorDefault = colorDefault;
        invalidate();
    }

    public void setTargetAmount(float targetAmount) {
        this.targetAmount = targetAmount;
        invalidate();
    }

    public void setCurrentAmount(float currentAmount) {
        setAnimator(currentAmount, true);
    }

    private void setAnimator(final float currentAmount, boolean animate) {
        if (animate) {
            currentAmountAnimator = ValueAnimator.ofFloat(0, 1);
            currentAmountAnimator.setDuration(700);

            setAnimator(0, false);

            currentAmountAnimator.setInterpolator(new DecelerateInterpolator());

            currentAmountAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float inter = (float) animation.getAnimatedValue();
                    setAnimator(inter * currentAmount, false);
                }
            });

            if (!currentAmountAnimator.isStarted()) {
                currentAmountAnimator.start();
            }
        } else {
            this.currentAmount = currentAmount;
            postInvalidate();
        }
    }


}
