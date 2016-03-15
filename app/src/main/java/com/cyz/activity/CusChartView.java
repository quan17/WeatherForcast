package com.cyz.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CusChartView extends View {
	private Context mContext;
	private List<PointItem> mItemList;
	private String unit;
	private String yFormat = "0.#";

	private Paint mPaint1;
	private Paint mPaint2;
	int width;
	int height;

	public CusChartView(Context context, int w, int h) {
		super(context);
		this.mContext = context;
		this.width = w;
		this.height = h;
		mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint1.setColor(Color.parseColor("#7fffffff"));
		mPaint1.setStrokeWidth(4);
		mPaint1.setStyle(Style.STROKE);
		mPaint2 = new Paint();
		mPaint2.setAlpha(0x0000ff);
		mPaint2.setTextSize(sp2px(mContext, 10));
		mPaint2.setColor(Color.parseColor("#28bbff"));
	}

	public CusChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public CusChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
	}

	public void setData(List<PointItem> items, String unit) {
		this.mItemList = items;
		this.unit = unit;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (mItemList == null || mItemList.isEmpty()) {
			return;
		}

		int split = dp2px(mContext, 8);
		int marginLeft = width / 12;
		int marginTop = dp2px(mContext, 20);
		int marginTop2 = dp2px(mContext, 25);
		int bheight = height - marginTop - 2 * split;
		// 设置抗锯齿
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		canvas.drawLine(split, 0, width - split, 0, mPaint1);
		// canvas.drawLine(split, height - split, width - split, height - split,
		// mPaint1);

		// 画单位
		canvas.drawText(unit, split, marginTop2 + split * 2, mPaint2);

		// 画X坐标
		ArrayList<Integer> xlist = new ArrayList<Integer>();
		mPaint1.setColor(Color.GRAY);
		for (int i = 0; i < mItemList.size(); i++) {
			int span = (width - 2 * marginLeft) / mItemList.size();
			int x = marginLeft + span / 2 + span * i;
			xlist.add(x);
			drawText(mItemList.get(i).getxAlis(), x, height + split * 2, canvas);
		}

		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		for (int i = 0; i < mItemList.size(); i++) {
			float y = mItemList.get(i).getyAlis();
			if (y > max) {
				max = y;
			}
			if (y < min) {
				min = y;
			}
		}

		float span = max - min;
		if (span == 0) {
			span = 6.0f;
		}
		max = max + span / 6.0f;
		min = min - span / 6.0f;

		// 获取每日最高温度点集合
		Point[] mPoints = getPoints(xlist, max, min, bheight / 2, marginTop, 0);

		// 获取每日最低温度点集合
		Point[] mPoints1 = getPoints(xlist, max, min, bheight / 2, marginTop
				+ bheight / 2, 1);
		// 画线
		mPaint1.setColor(Color.parseColor("#7fffffff"));
		mPaint1.setStyle(Style.STROKE);
		mPaint1.setStrokeWidth(8);
		drawLine(mPoints, canvas, mPaint1);
		drawLine(mPoints1, canvas, mPaint1);
		// 画点
		mPaint1.setColor(Color.parseColor("#28bbff"));
		mPaint1.setStyle(Style.FILL);
		for (int i = 0; i < mPoints.length; i++) {
			// 最高温度
			canvas.drawCircle(mPoints[i].x, mPoints[i].y, 12, mPaint1);
			String yText = new java.text.DecimalFormat(yFormat)
					.format(mItemList.get(i).getyAlis());
			drawText(yText, mPoints[i].x, mPoints[i].y - dp2px(mContext, 12),
					canvas);
			// 最低温度
			canvas.drawCircle(mPoints[i].x, mPoints1[i].y, 12, mPaint1);
			String yText1 = new java.text.DecimalFormat(yFormat)
					.format(mItemList.get(i).getyMinAlis());
			drawText(yText1, mPoints1[i].x,
					mPoints1[i].y - dp2px(mContext, 12), canvas);
		}

	}

	// 计算温度点得关键代码
	private Point[] getPoints(List<Integer> xList, float max, float min, int h,
			int top, int type) {
		Point[] points = new Point[mItemList.size()];
		for (int i = 0; i < mItemList.size(); i++) {
			int ph;
			if (type == 0) {
				ph = top
						+ h
						- (int) (h * ((mItemList.get(i).getyAlis() - min) / (max - min)));
			} else {
				ph = top
						+ h
						- (int) (h * ((mItemList.get(i).getyMinAlis() - min) / (max - min)));
			}
			points[i] = new Point(xList.get(i), ph);
		}
		return points;
	}

	//
	private void drawLine(Point[] ps, Canvas canvas, Paint paint) {
		Point startp = new Point();
		Point endp = new Point();
		for (int i = 0; i < ps.length - 1; i++) {
			startp = ps[i];
			endp = ps[i + 1];
			canvas.drawLine(startp.x, startp.y, endp.x, endp.y, paint);
		}
	}

	private void drawText(String text, int x, int y, Canvas canvas) {
		Paint p = new Paint();
		p.setAlpha(0x0000ff);
		p.setTextSize(sp2px(mContext, 14));
		p.setTextAlign(Paint.Align.CENTER);
		p.setColor(Color.WHITE);
		canvas.drawText(text, x, y, p);
	}

	public String getFormat() {
		return yFormat;
	}

	public void setFormat(String format) {
		this.yFormat = format;
	}

	private int sp2px(Context context, int spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (fontScale * spValue + 0.5f);
	}

	private int dp2px(Context context, int dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
