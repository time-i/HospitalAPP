package com.hospital.app.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.hospital.app.entity.ChartEntity;

import java.util.ArrayList;
import java.util.List;

public class LineChartView extends CharterBase {
    private onSelectClick onSelectClick;

    public void setOnSelectClick(onSelectClick onSelectClick) {
        this.onSelectClick = onSelectClick;
    }
    private int xo;//圆点x坐标
    private int yo;//圆点y坐标
    private int topBound;//上边界的高度
    private int firstX;//第一个点x坐标
    private int minSlideX;//在移动时，第一个点允许最小的x坐标
    private int maxSlideX;//在移动时，第一个点允许最大的x坐标
    private int interval;//坐标间的间隔
    float startX=0;//滑动时候，上一次手指的x坐标

    //单位文字
    private String unitText="";

    public String getUnitText() {
        return unitText;
    }

    public void setUnitText(String unitText) {
        this.unitText = unitText;
    }

    //是否有阴影
    private boolean IsShadow=false;
    //控件宽度
    private int width;
    //控件高度
    private int height;
    //X轴刻度数
//    private int XScaleNumber=8;
    private int XScaleNumber=600;

    //虚线的画笔
    private Paint effectPaint;

    //日期的画笔
    private Paint datePaint;
    //日期线的宽度
    private float dateLineWith=dipToPx(0.6f);
    //日期背景的颜色
    private int dateLineColor = Color.parseColor("#f2f2f2");
//    private int dateLineColor = Color.parseColor("#000000");

    //折线颜色
    private int baseLineColor;
    //折线路径
    private Path baseLinePath;
    //折线画笔
    private Paint baseLinePaint;
    //折线的宽度
    private float baseLineWith = dipToPx(0.8f);
    //折线下的阴影的画笔
    private Paint baseShadow;

    //圆圈点的画笔
    private Paint circlePaint;

    //折线的弯曲率
    private float smoothness=0.35f;
    //坐标点的集合
    private List<PointF> points=new ArrayList<>();
    //选中的坐标点
    private int selectPoint = -1;

    //文字的画笔
    private Paint textPaint;
    //文字的大小
    private float textSize=dipToPx(12);
    //文字的颜色
//    private int textColor= Color.parseColor("#000000");
    private int textColor= Color.parseColor("#FFFFFF");

    //背景颜色
//    private int bg=Color.parseColor("#fa6049");
    private int bg=Color.parseColor("#666666");

    public int getSelectPoint() {
        return selectPoint;
    }

    public void setSelectPoint(int selectPoint) {
        this.selectPoint = selectPoint;
        invalidate();
    }

    public int getXScaleNumber() {
        return XScaleNumber;
    }

    public void setXScaleNumber(int XScaleNumber) {
        this.XScaleNumber = XScaleNumber;
    }

    public boolean isShadow() {
        return IsShadow;
    }

    public void setShadow(boolean shadow) {
        IsShadow = shadow;
    }

    public LineChartView(Context context) {
        super(context);
        init(context);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
        init(context);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
        init(context);
    }
    private void initAttr(Context context, AttributeSet attrs)
    {
    }
    //初始化各类参数
    private void init(Context context) {
        setBackgroundColor(bg);
        datePaint=new Paint();
        datePaint.setAntiAlias(true);
        datePaint.setStyle(Paint.Style.FILL);
        datePaint.setColor(dateLineColor);
        datePaint.setStrokeWidth(dateLineWith);
        datePaint.setStrokeCap(Paint.Cap.ROUND);

        effectPaint=new Paint();
        effectPaint.setAntiAlias(true);
        effectPaint.setStyle(Paint.Style.STROKE);
        effectPaint.setColor(dateLineColor);
        effectPaint.setStrokeWidth(dateLineWith);
        effectPaint.setStrokeCap(Paint.Cap.ROUND);

        baseLineColor=Color.WHITE;
        baseLinePath = new Path();
        baseLinePaint = new Paint();
        baseLinePaint.setAntiAlias(true);
        baseLinePaint.setStyle(Paint.Style.STROKE);
        baseLinePaint.setStrokeWidth(baseLineWith);
        baseLinePaint.setColor(baseLineColor);
        baseLinePaint.setStrokeCap(Paint.Cap.ROUND);

        baseShadow = new Paint();
        baseShadow.setAntiAlias(true);
        baseShadow.setColor((baseLineColor & 0x40FFFFFF) | 0x10000000);
        baseShadow.setStyle(Paint.Style.FILL);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(baseLineWith);
        circlePaint.setColor(baseLineColor);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
        xo=0;
        yo=height-dipToPx(30);
        topBound=dipToPx(12);
        if(values!=null){
            setParam(values);
        }
    }
    private void setParam(ArrayList<ChartEntity> values){
        if(values.size()<XScaleNumber){
            interval=width/(values.size()+1);
            firstX=interval;
        }else{
            interval=width/XScaleNumber;
            firstX=width-values.size()*interval;
        }

//        minSlideX=width-values.size()*interval;
//        maxSlideX=interval;
        minSlideX=150;
        maxSlideX=150;
        selectPoint=values.size()-1;
    }

    public void setDataChart(ArrayList<ChartEntity> values){
        setValues(values);
        setParam(values);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawShell(canvas);
        drawEffect(canvas);
        getCoordinatePoint(canvas);
        if (!animFinished && !animator.isRunning()&&values!=null&&values.size()>0) {
            playAnimation();
        }
        drawEffectText(canvas);
    }
    //画日期背景
    private void drawShell(Canvas canvas) {
        datePaint.setColor(dateLineColor);
        canvas.drawRect(0,yo,width,height,datePaint);
    }
    //画虚线
    private void drawEffect(Canvas canvas) {
        effectPaint.setPathEffect(new DashPathEffect(new float[]{8,10,8,10},0));
        float interval=(yo-topBound)/4;
        for(int i=0;i<4;i++){
            Path path = new Path();
            path.moveTo(0, topBound+(i*interval));
            path.lineTo(width, topBound+(i*interval));
            canvas.drawPath(path,effectPaint);
        }
    }
    //文字
    private void drawEffectText(Canvas canvas) {
        textPaint.setTextSize(dipToPx(12));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(dateLineColor);
        float interval=(yo-topBound)/4;
        for(int i=0;i<4;i++){
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
            canvas.drawText(df.format((maxY-i*((maxY-minY)/4))),interval*0.8f,topBound+(i*interval)+dipToPx(15),textPaint);
        }
    }
    //得到动态的坐标点
    private void getCoordinatePoint(Canvas canvas){
        if (values == null || values.size() == 0) {
            return;
        }

        final int valuesLength = valuesTransition.length;

        final float dY = maxY - minY > 0 ? maxY - minY : 1;
        // calculate point coordinates
        points = new ArrayList<>(valuesLength);
        for (int i = 0; i < valuesLength; i++) {
            float x = i*interval+firstX;
            float y = topBound + ((yo-topBound) - (valuesTransition[i] - minY) * (yo-topBound) / dY);
            Log.e("GXL_Chart","yo="+yo+"-----valuesTransition["+i+"]="+valuesTransition[i]+"-----minY="+minY+"-----dY="+dY+"-----y="+y);
            points.add(new PointF(x, y));

        }
        drawBendLine(canvas,points);
        //drawWeightPoint(canvas,points);
        drawText(canvas,points);
    }

    //绘制X坐标轴文本
    private void drawText(Canvas canvas, List<PointF> Points) {
        if (Points.size() == 0) {
            return;
        }
        for (int i = 0; i < Points.size(); i++) {
            if (i == selectPoint) {
                //绘制日期
                textPaint.setTextSize(dipToPx(12));
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setColor(textColor);
                canvas.drawText(valuesText[i], Points.get(i).x, yo + (height - yo) / 2 + dipToPx(12) / 3, textPaint);

                //绘制提示文字
                textPaint.setTextSize(dipToPx(11));
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setColor(textColor);

                String hintText = String.valueOf(valuesData[i]) + unitText;
                Rect rect = new Rect();
                textPaint.getTextBounds(hintText, 0, hintText.length() - 1, rect);
                float textHeight = rect.height();
                float textWidth = rect.width();

                float textX=Points.get(i).x;
                float textY=Points.get(i).y-textHeight;
                datePaint.setColor(Color.WHITE);
                RectF textBg=new RectF();
                textBg.left=textX-textWidth*0.8f;
                textBg.top=textY-textHeight*0.8f-textHeight/2;
                textBg.right=textX+textWidth*0.8f;
                textBg.bottom=textY+textHeight*0.8f-textHeight/2;
                canvas.drawRoundRect(textBg,dipToPx(2),dipToPx(2),datePaint);
                textPaint.setColor(Color.parseColor("#F75044"));
                canvas.drawText(hintText, textX, textY, textPaint);
            } else {
                textPaint.setTextSize(dipToPx(12));
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setColor(textColor);
                canvas.drawText(valuesText[i], Points.get(i).x, yo + (height - yo) / 2 + dipToPx(12) / 3, textPaint);
            }
        }

    }
    //画弯曲的折线
    private void drawBendLine(Canvas canvas,List<PointF> Points){
        baseLinePath.reset();
        if(Points.size() == 0)
        {
            return;
        }
        List<PointF> NewPoints=new ArrayList<>();
        NewPoints.addAll(Points);


        float lX = 0;
        float lY = 0;
        baseLinePath.moveTo(NewPoints.get(0).x, NewPoints.get(0).y);
        for (int i = 1; i < NewPoints.size(); i++) {
            PointF p = NewPoints.get(i);

            PointF firstPointF = NewPoints.get(i - 1);
            float x1 = firstPointF.x + lX;
            float y1 = firstPointF.y + lY;

            PointF secondPointF = NewPoints.get(i + 1 < NewPoints.size() ? i + 1 : i);
            lX = (secondPointF.x - firstPointF.x) / 2 * smoothness;
            lY = (secondPointF.y - firstPointF.y) / 2 * smoothness;
            float x2 = p.x - lX;
            float y2 = p.y - lY;
            if (y1 == p.y) {
                y2 = y1;
            }
            baseLinePath.cubicTo(x1, y1, x2, y2, p.x, p.y);
        }
        baseLinePaint.setStyle(Paint.Style.STROKE);
        baseLinePaint.setColor(baseLineColor);
        canvas.drawPath(baseLinePath, baseLinePaint);
        //绘制线下面的阴影
        if(IsShadow){
            drawArea(canvas,points);
        }
    }
    //绘制线下面的阴影
    private void drawArea(Canvas canvas,List<PointF> Points){
        // fill area
        LinearGradient mShader = new LinearGradient(0,0,0,getMeasuredHeight(),new int[] {Color.WHITE,Color.parseColor("#f85646")},new float[]{0.5f,0.85f},Shader.TileMode.REPEAT);
        baseShadow.setShader(mShader);
        if (Points.size() > 0) {
            baseLinePath.lineTo(points.get(Points.size() - 1).x , yo);
            baseLinePath.lineTo(points.get(0).x, yo);
            baseLinePath.close();
            canvas.drawPath(baseLinePath, baseShadow);
        }
    }
    //绘制折线穿过的点
    protected void drawWeightPoint(Canvas canvas,List<PointF> Points)
    {
        if(Points.size()== 0)
        {
            return;
        }
        for(int i = 0; i < Points.size(); i++)
        {
            if(i==selectPoint){
                circlePaint.setColor(Color.WHITE);
                canvas.drawCircle(Points.get(i).x, Points.get(i).y, dipToPx(3.5f), circlePaint);
            }else {
                circlePaint.setColor(Color.WHITE);
                canvas.drawCircle(Points.get(i).x, Points.get(i).y, dipToPx(3f), circlePaint);
                circlePaint.setColor(Color.parseColor("#F75044"));
                canvas.drawCircle(Points.get(i).x, Points.get(i).y, dipToPx(2f), circlePaint);
            }
        }
    }
    private int mLastX;
    private int mLastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 要求父控件拦截事件
                this.getParent().requestDisallowInterceptTouchEvent(false);
                startX=event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                // 如果是左右滑动
                if(Math.abs(deltaX) > Math.abs(deltaY)){
                    // 要求父控件不拦截事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                //如果不用滑动就可以展示所有数据，就不让滑动
                if(values.size()<XScaleNumber){
                    return false;
                }
                float dis=event.getX()-startX;
                startX=event.getX();
                if(firstX+dis>maxSlideX){
                    firstX=maxSlideX;
                    Log.e("GXL_firstX","maxSlideX="+firstX);
                }else if(firstX+dis<minSlideX){
                    firstX=minSlideX;
                    Log.e("GXL_firstX","minSlideX="+firstX);
                }else{
                    firstX=(int) (firstX+dis);
                    Log.e("GXL_firstX","firstX+dis="+firstX+"-------dis="+dis);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onActionUpEvent(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    private void onActionUpEvent(MotionEvent event)
    {
        boolean isValidTouch = validateTouch(event.getX(), event.getY());
        Log.e("GXL_Event","isValidTouch="+isValidTouch);

        if(isValidTouch)
        {
            invalidate();
            Log.e("GXL_Event","selectPoint="+selectPoint);
            if(onSelectClick!=null){
                onSelectClick.onSelect(selectPoint);
            }
        }
    }

    //是否是有效的触摸范围
    private boolean validateTouch(float x, float y)
    {
        //曲线触摸区域
        for(int i = 0; i < points.size(); i++)
        {
            // dipToPx(8)乘以2为了适当增大触摸面积
            if(x > (points.get(i).x - dipToPx(8) * 2) && x < (points.get(i).x + dipToPx(8) * 2))
            {
                if(y > (points.get(i).y - dipToPx(8) * 2) && y < (points.get(i).y + dipToPx(8) * 2))
                {
                    selectPoint = i;
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip)
    {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }
    public interface onSelectClick{
        void onSelect(int select);
    }
}


