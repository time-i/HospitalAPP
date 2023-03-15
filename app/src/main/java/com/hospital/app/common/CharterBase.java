package com.hospital.app.common;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.hospital.app.entity.ChartEntity;
import java.util.ArrayList;

public class CharterBase extends View {
    //Y轴的最大最小值
    float minY=10.0f;
    float maxY=20.0f;

    //数据的最大最小值
    float minData;
    float maxData;
    //差值
    float disparity;

    ArrayList<ChartEntity> values = new ArrayList<>();
    float[] valuesData;
    String[] valuesText;

    float[] valuesTransition;

    private long animDuration;
    boolean animFinished;
    ValueAnimator animator;
    private Interpolator animInterpolator;

    private CharterAnimListener animListener;

    protected CharterBase(Context context) {
        this(context, null);
    }

    protected CharterBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected CharterBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected CharterBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        animDuration = 1;
        animFinished = false;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animInterpolator = new DecelerateInterpolator();
    }

    private long checkAnimDuration(long animDuration) {
//        int minAnimDuration = 300;
        int minAnimDuration = 1;
        if (animDuration < minAnimDuration) {
            animDuration = minAnimDuration;
        }
        return animDuration;
    }

    public void show() {
        setWillNotDraw(false);
        invalidate();
    }

    public ArrayList<ChartEntity> getValues() {
        return values;
    }

    public void setValues(ArrayList<ChartEntity> values) {
        this.values = values;
        getMaxMinValues(values);
        getValues(values);
        replayAnim();
    }

    private void getValues(ArrayList<ChartEntity> values) {
        if (values != null && values.size() > 0) {
            this.valuesText = new String[values.size()];
            this.valuesData = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                this.valuesText[i] = values.get(i).getText();
                this.valuesData[i] = values.get(i).getValue();
            }
        }
    }

    private void getMaxMinValues(ArrayList<ChartEntity> values) {
        if (values != null && values.size() > 0) {
            maxData = values.get(0).getValue();
            minData = values.get(0).getValue();
            for (int y = 0; y < values.size(); y++) {
                if (values.get(y).getValue() > maxData) {
                    maxData = values.get(y).getValue();
                }
                if (values.get(y).getValue() < minData) {
                    minData = values.get(y).getValue();
                }
            }
            disparity = maxData - minData;
            if (disparity == 0) {
                //maxY = maxData + 1;
                //minY = minData - 1;
            } else {
                //maxY = maxData + (disparity / 6);
                //minY = minData - (disparity / 6);
            }
        }
    }

    private void initValuesTarget(ArrayList<ChartEntity> values) {
        this.valuesTransition = new float[values.size()];
        for (int i = 0; i < valuesTransition.length; i++) {
            valuesTransition[i] = minY;
        }
    }

    private void calculateNextAnimStep(float animationProgress) {
        float step = (animationProgress * maxY);
        for (int i = 0; i < valuesTransition.length; i++) {
            valuesTransition[i] = (step >= values.get(i).getValue()) ? values.get(i).getValue() : ((step >= minY) ? step : minY);
        }

        if (animListener != null) {
            animListener.onAnimProgress(animationProgress);
        }
    }

    public void replayAnim() {
        if (values == null || values.size() == 0) {
            return;
        }

        initValuesTarget(values);
        animator.cancel();
        animFinished = false;
        invalidate();
    }

    public long getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(long animDuration) {
        this.animDuration = checkAnimDuration(animDuration);
        replayAnim();
    }

    public void setAnimListener(CharterAnimListener animListener) {
        this.animListener = animListener;
    }

    public void setAnimInterpolator(Interpolator animInterpolator) {
        this.animInterpolator = animInterpolator;
    }

    void playAnimation() {
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                calculateNextAnimStep((float) animation.getAnimatedValue());
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (animListener != null) {
                    animListener.onAnimStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animFinished = true;
                if (animListener != null) {
                    animListener.onAnimFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (animListener != null) {
                    animListener.onAnimCancel();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(animDuration);
        animator.setInterpolator(animInterpolator);
        animator.start();
    }
    public interface CharterAnimListener {
        void onAnimStart();

        void onAnimFinish();

        void onAnimCancel();

        void onAnimProgress(float progress);
    }
}