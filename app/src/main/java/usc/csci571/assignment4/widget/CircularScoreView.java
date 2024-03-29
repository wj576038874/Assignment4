package usc.csci571.assignment4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import usc.csci571.assignment4.R;

/**
 * Created by stark on 13/03/17.
 */

public class CircularScoreView extends View {

    private int mScore;
    private float mDiameter;
    private float mTextSize;
    private float mCentreX;
    private float mCentreY;
    private RectF mRectF;

    private int mTextColor;
    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mBackgroundColor;

    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private Paint mScorePaint;
    private Paint mBlankPaint;

    public CircularScoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributeArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircularScoreView,
                0,0
        );

        try {
            mScore = attributeArray.getInteger(R.styleable.CircularScoreView_score, 0);

            mTextColor = attributeArray.getColor(R.styleable.CircularScoreView_setTextColor,
                    ResourcesCompat.getColor(getResources(), android.R.color.white,null));

            mPrimaryColor = attributeArray.getColor(R.styleable.CircularScoreView_setPrimaryColor,
                    ResourcesCompat.getColor(getResources(),R.color.user_score_score,null));

            mSecondaryColor = attributeArray.getColor(R.styleable.CircularScoreView_setSecondaryColor,
                    ResourcesCompat.getColor(getResources(),android.R.color.black,null));

            mBackgroundColor = attributeArray.getColor(R.styleable.CircularScoreView_setBackgroundColor,
                    ResourcesCompat.getColor(getResources(),R.color.user_score_background, null));
        } finally {
            //TypedArray objects are a shared resource and must be recycled after use.
            attributeArray.recycle();
        }

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCentreX, mCentreY,mDiameter/2f, mBackgroundPaint);

        mScore = Math.min(100, mScore);
        float sweepAngle = (mScore/100f)*360f;

        canvas.drawArc(mRectF, 270f + sweepAngle, 360f - sweepAngle, false, mBlankPaint);

        canvas.drawArc(mRectF, 270f, sweepAngle, false, mScorePaint);

        String text = Integer.toString(mScore) + "%";
        float xPos =  (mCentreX ) - (mTextPaint.measureText(text)/2);
        float yPos =  ((mCentreY) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)) ;
        canvas.drawText(text, xPos, yPos, mTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());

        // Account for the label

        float ww = (float)w - xpad;
        float hh = (float)h - ypad;

        // Setting dimensions for the circle.
        mDiameter = Math.min(ww, hh);
        float leftArc = 0.07f*mDiameter + xpad/2f;
        float topArc =  0.07f*mDiameter + ypad/2f;
        float rightArc = 0.93f*mDiameter + xpad/2f;
        float bottomArc = 0.93f*mDiameter + ypad/2f;
        mRectF = new RectF(leftArc,topArc,rightArc,bottomArc);

        mCentreX = (mDiameter + xpad)/2f;
        mCentreY = (mDiameter + ypad)/2f;

        mTextSize = mDiameter/3f;
        mTextPaint.setTextSize(mTextSize);

        mScorePaint.setStrokeWidth(0.07f*mDiameter);
        mBlankPaint.setStrokeWidth(0.07f*mDiameter);
    }

    private void init(){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);

        mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScorePaint.setStyle(Paint.Style.STROKE);
        mScorePaint.setStrokeCap(Paint.Cap.ROUND);
        mScorePaint.setColor(mPrimaryColor);

        mBlankPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlankPaint.setStyle(Paint.Style.STROKE);
        mBlankPaint.setColor(mSecondaryColor);

    }



    public void setScore(int score){
        mScore = score;

//        These calls are crucial to ensure that the view behaves reliably
        invalidate();
        requestLayout();
    }

    public int getScore(){
        return mScore;
    }

    public void setPrimaryColor(int color){
        mScorePaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public void setSecondaryColor(int color){
        mBlankPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public void setBackgroundColor(int color){
        mBackgroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public void setTextColor(int color){
        mTextPaint.setColor(color);
        invalidate();
        requestLayout();
    }
}