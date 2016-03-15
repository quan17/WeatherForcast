package com.cyz.activity;

public class PointItem {
	private String xAlis;
	// highest temperature
	private int yAlis;
	// lowhest temperature
	private int yMinAlis;

	public int getyMinAlis() {
		return yMinAlis;
	}

	public void setyMinAlis(int yMinAlis) {
		this.yMinAlis = yMinAlis;
	}

	public PointItem(String x, int y, int yLow) {
		this.xAlis = x;
		this.yAlis = y;
		this.yMinAlis = yLow;
	}

	public String getxAlis() {
		return xAlis;
	}

	public void setxAlis(String xAlis) {
		this.xAlis = xAlis;
	}

	public int getyAlis() {
		return yAlis;
	}

	public void setyAlis(int yAlis) {
		this.yAlis = yAlis;
	}

}
