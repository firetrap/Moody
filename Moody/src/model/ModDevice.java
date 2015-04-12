package model;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

public class ModDevice {
	private WindowManager wm;
	private Display display;
	static float baseX = 720;
	static float baseY = 1184;

	public ModDevice(Context ctx) {
		wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
	}

	public float getX() {

		Point size = new Point();
		display.getSize(size);
		return size.x;

	}

	public int getY() {

		Point size = new Point();
		display.getSize(size);
		return size.y;

	}

	public float getXdiff() {

		return getX() - baseX;

	}

	public float getYdiff() {

		return getY() - baseY;
	}
}
