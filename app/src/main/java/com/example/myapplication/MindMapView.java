package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MindMapView extends View {
    private Node rootNode;
    private Node selectedNode;
    private float touchX, touchY;

    // 三个必要的构造函数
    public MindMapView(Context context) {
        super(context);
        init();
    }

    public MindMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MindMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 延迟初始化节点，因为onCreate时可能还没有尺寸
        post(() -> {
            if (rootNode == null) {
                rootNode = new Node("中心主题", getWidth()/2, getHeight()/2);
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rootNode != null) {
            drawNode(canvas, rootNode);
        }
    }

    private void drawNode(Canvas canvas, Node node) {
        // 绘制节点圆角矩形背景
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFEB3B"));
        canvas.drawRoundRect(node.x-100, node.y-50, node.x+100, node.y+50, 10, 10, paint);

        // 绘制节点文本
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24);
        canvas.drawText(node.text, node.x-80, node.y+10, textPaint);

        // 递归绘制子节点
        for(Node child : node.children) {
            drawConnection(canvas, node, child);
            drawNode(canvas, child);
        }
    }

    private void drawConnection(Canvas canvas, Node from, Node to) {
        Paint linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(3);
        canvas.drawLine(from.x, from.y, to.x, to.y, linePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selectedNode = findNodeAtPosition(event.getX(), event.getY());
                touchX = event.getX();
                touchY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - touchX) < 10 &&
                        Math.abs(event.getY() - touchY) < 10) {
                    // 点击添加新节点
                   
                }
                break;
        }
        return true;
    }

    // 查找点击位置所在的节点
    private Node findNodeAtPosition(float x, float y) {
        return findNodeAtPosition(rootNode, x, y);
    }

    private Node findNodeAtPosition(Node node, float x, float y) {
        // 检查当前节点
        if (node.x-100 <= x && x <= node.x+100 &&
                node.y-50 <= y && y <= node.y+50) {
            return node;
        }

        // 递归检查子节点
        for (Node child : node.children) {
            Node found = findNodeAtPosition(child, x, y);
            if (found != null) return found;
        }

        return null;
    }

    // 节点类定义
    public static class Node {
        String text;
        float x, y;
        List<Node> children = new ArrayList<>();
        Node parent; // 父节点引用

        Node(String text, float x, float y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }
}