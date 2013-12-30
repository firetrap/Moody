package model;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

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
