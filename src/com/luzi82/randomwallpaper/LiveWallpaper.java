package com.luzi82.randomwallpaper;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class LiveWallpaper extends WallpaperService {

	private static final String LOG_TAG = "LiveWallpaper";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		// Create the live wallpaper engine
		return new LiveWallpaperEngine();
	}

	static final Matrix mi = new Matrix();
	static final Paint paint = new Paint();

	static int oldWidth = -1;
	static int oldHeight = -1;

	static Timer timer;

	static byte[] byteAry;
	static ByteBuffer byteBuffer;
	static Bitmap bitmap;
	static int size = -1;

	static synchronized void clean() {
		Log.d(LOG_TAG, "static synchronized void clean()");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		size = -1;
		oldWidth = -1;
		oldHeight = -1;
		byteAry = null;
		byteBuffer = null;
		if (bitmap != null)
			bitmap.recycle();
		bitmap = null;
		cleanBuf();
	}

	static synchronized void drawCanvas(Canvas c) {
		Log.d(LOG_TAG, "static void drawCanvas(Canvas c) start");
		int nowWidth = c.getWidth();
		int nowHeight = c.getHeight();
		int s = nowWidth * nowHeight;
		if ((oldHeight != nowHeight) || (oldWidth != nowWidth)) {
			if (bitmap != null)
				bitmap.recycle();
			bitmap = Bitmap.createBitmap(nowWidth, nowHeight,
					Bitmap.Config.ARGB_8888);
			oldHeight = nowHeight;
			oldWidth = nowWidth;
		}
		if (size != s) {
			byteAry = new byte[s << 2];
			byteBuffer = ByteBuffer.wrap(byteAry);
			setSize(s);
			size = s;
		}
		genRandom(byteAry);
		bitmap.copyPixelsFromBuffer(byteBuffer);
		c.drawBitmap(bitmap, mi, paint);
		Log.d(LOG_TAG, "static void drawCanvas(Canvas c) end");
	}

	static synchronized void createTimer(final LiveWallpaperEngine engine) {
		Log
				.d(LOG_TAG,
						"static void createTimer(final LiveWallpaperEngine engine) start");
		if (timer == null) {
			Log.d(LOG_TAG, "create timer");
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					engine.updateCanvas();
				}
			}, 100, 100);
		}
		Log
				.d(LOG_TAG,
						"static void createTimer(final LiveWallpaperEngine engine) end");
	}

	// static long time = System.currentTimeMillis();

	// static {
	// paint.setColor(Color.RED);
	// paint.setTextSize(20);
	// }

	class LiveWallpaperEngine extends Engine {

		@Override
		public void onCreate(SurfaceHolder holder) {
			super.onCreate(holder);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		// Become false when switching to an app or put phone to sleep
		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			// Log.d(LOG_TAG, "onVisibilityChanged=" + visible);
			if (visible) {
				createTimer(this);
			} else {
				clean();
			}

		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			// Log.d(LOG_TAG, "onSurfaceChanged");
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			// Log.d(LOG_TAG, "onSurfaceCreated");
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			// Log.d(LOG_TAG, "onSurfaceDestroyed");
		}

		private void updateCanvas() {
			Log.d(LOG_TAG, "updateCanvas start");
			// long now = System.currentTimeMillis();
			// int diff = (int) (now - time);
			// time = now;
			SurfaceHolder holder = getSurfaceHolder();
			if (holder != null) {
				Canvas c = null;
				try {
					c = holder.lockCanvas();
					if (c != null) {
						drawCanvas(c);
					}
				} finally {
					if (c != null)
						holder.unlockCanvasAndPost(c);
				}
			}
			Log.d(LOG_TAG, "updateCanvas end");
		}
	}

	static {
		System.loadLibrary("randompixel");
		setSeed(System.currentTimeMillis());
	}

	public static native void setSeed(long seed);

	public static native void setSize(int imgSize);

	public static native void cleanBuf();

	public static native void genRandom(byte[] out);

}
