package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class PatternLockView extends View {
    private static final int POINT_COUNT = 3; // 3x3 九宫格
    private float mDotRadius; // 点的半径
    private Paint mDotPaint, mLinePaint;
    private List<Point> mPoints = new ArrayList<>(); // 九宫格所有点
    private List<Point> mSelectedPoints = new ArrayList<>(); // 已选中的点
    private Path mPath = new Path(); // 手势路径
    private OnPatternListener mListener;

    public PatternLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 初始化点的画笔
        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setColor(Color.GRAY);
        mDotPaint.setStyle(Paint.Style.FILL);

        // 初始化路径的画笔
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.BLUE);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 初始化九宫格点坐标
        initPoints(w, h);
    }

    private void initPoints(int width, int height) {
        mPoints.clear();
        float cellSize = Math.min(width, height) / (POINT_COUNT * 2f);
        mDotRadius = cellSize * 0.4f;

        for (int i = 0; i < POINT_COUNT; i++) {
            for (int j = 0; j < POINT_COUNT; j++) {
                float x = cellSize * (2 * j + 1);
                float y = cellSize * (2 * i + 1);
                mPoints.add(new Point(x, y, i, j));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPoints(canvas);
        drawPath(canvas);
    }

    // 绘制九宫格点
    private void drawPoints(Canvas canvas) {
        for (Point point : mPoints) {
            canvas.drawCircle(point.x, point.y, mDotRadius, mDotPaint);
        }
    }

    // 绘制手势路径
    private void drawPath(Canvas canvas) {
        if (!mSelectedPoints.isEmpty()) {
            mPath.reset();
            Point first = mSelectedPoints.get(0);
            mPath.moveTo(first.x, first.y);
            for (Point point : mSelectedPoints) {
                mPath.lineTo(point.x, point.y);
            }
            canvas.drawPath(mPath, mLinePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkPoint(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                checkPoint(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onPatternComplete();
                break;
        }
        return true;
    }

    // 检查触摸点是否选中九宫格点
    private void checkPoint(float x, float y) {
        for (Point point : mPoints) {
            if (Math.abs(x - point.x) < mDotRadius &&
                    Math.abs(y - point.y) < mDotRadius &&
                    !mSelectedPoints.contains(point)) {
                mSelectedPoints.add(point);
                break;
            }
        }
    }

    // 手势完成回调
    private void onPatternComplete() {
        if (mListener != null) {
            mListener.onPatternDetected(getPatternString());
        }
        reset();
    }

    // 生成图案字符串（例如 "00,01,02"）
    private String getPatternString() {
        StringBuilder pattern = new StringBuilder();
        for (Point point : mSelectedPoints) {
            pattern.append(point.row).append(point.col);
            if (point != mSelectedPoints.get(mSelectedPoints.size() - 1)) {
                pattern.append(",");
            }
        }
        return pattern.toString();
    }

    // 重置视图
    public void reset() {
        mSelectedPoints.clear();
        invalidate();
    }

    // 设置图案监听器
    public void setOnPatternListener(OnPatternListener listener) {
        mListener = listener;
    }

    // 九宫格点内部类
    private static class Point {
        float x, y;
        int row, col;

        Point(float x, float y, int row, int col) {
            this.x = x;
            this.y = y;
            this.row = row;
            this.col = col;
        }
    }

    // 图案监听接口
    public interface OnPatternListener {
        void onPatternDetected(String pattern);
    }
}