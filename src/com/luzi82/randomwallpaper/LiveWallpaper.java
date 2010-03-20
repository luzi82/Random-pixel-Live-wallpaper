package com.luzi82.randomwallpaper;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
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

	static float xOffset = 0;
	static float yOffset = 0;

	static int oldWidth = -1;
	static int oldHeight = -1;

	static Timer timer;

	static byte[] byteAry;
	static ByteBuffer byteBuffer;
	static Bitmap bitmap;
	static IntBuffer intBuffer;
	static int[] intAry;
	static int size = -1;
	static Paint paint = new Paint();
	static Random random = new Random();
	static long time = System.currentTimeMillis();

	static {
		paint.setColor(Color.RED);
		paint.setTextSize(20);
	}

	class LiveWallpaperEngine extends Engine {

		@Override
		public void onCreate(SurfaceHolder holder) {
			super.onCreate(holder);
			setSeed(System.currentTimeMillis());
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		// Become false when switching to an app or put phone to sleep
		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			Log.d(LOG_TAG, "onVisibilityChanged=" + visible);
			if (visible) {
				// updateCanvas();
				if (timer == null) {
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							updateCanvas();
						}
					}, 100, 100);
				}
			} else {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
			}

		}

//		// 0 when on the first home screen, -0.5/-160px on the center
//		// home screen (assume 3 screens in total).
//		@Override
//		public void onOffsetsChanged(float xOffset, float yOffset,
//				float xOffsetStep, float yOffsetStep, int xPixelOffset,
//				int yPixelOffset) {
//			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
//					xPixelOffset, yPixelOffset);
//			Log.d(LOG_TAG, "xOffset=" + xOffset + " yOffset=" + yOffset
//					+ "xOffseStep=" + xOffsetStep + " yOffsetStep="
//					+ yOffsetStep + "xPixelOffset=" + xPixelOffset
//					+ " yPixelOffset=" + yPixelOffset);
//			LiveWallpaper.xOffset = xOffset;
//			LiveWallpaper.yOffset = yOffset;
//			// updateCanvas();
//		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			Log.d(LOG_TAG, "onSurfaceChanged");
			setSize(width*height);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			Log.d(LOG_TAG, "onSurfaceCreated");
			Rect rect=holder.getSurfaceFrame();
			setSize(rect.width()*rect.height());
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			Log.d(LOG_TAG, "onSurfaceDestroyed");
			Rect rect=holder.getSurfaceFrame();
			setSize(rect.width()*rect.height());
		}

		private void updateCanvas() {
			Log.d(LOG_TAG, "updateCanvas start");
			long now = System.currentTimeMillis();
			int diff = (int) (now - time);
			time = now;
			SurfaceHolder holder = getSurfaceHolder();
			if (holder != null) {
				Canvas c = null;
				try {
					c = holder.lockCanvas();
					if (c != null) {
						int nowWidth = c.getWidth();
						int nowHeight = c.getHeight();
						int s = nowWidth * nowHeight;
						if (size != s) {
							byteAry = new byte[s << 2];
							byteBuffer = ByteBuffer.wrap(byteAry);
							// intBuffer = byteBuffer.asIntBuffer();
							// intAry = new int[s];

							// random.nextBytes(byteAry);
							// intBuffer.rewind();
							// intBuffer.get(intAry);

							bitmap = Bitmap.createBitmap(nowWidth, nowHeight,
									Bitmap.Config.ARGB_8888);

							size = s;
						}
//						Arrays.fill(byteAry, (byte) 0x00);
						genRandom(byteAry);
						// random.nextBytes(byteAry);
						// intBuffer.rewind();
						// intBuffer.get(intAry);
						Log.d(LOG_TAG, "updateCanvas copy");
						bitmap.copyPixelsFromBuffer(byteBuffer);
						Log.d(LOG_TAG, "updateCanvas draw");
						c.drawBitmap(bitmap, mi, paint);
//						c.drawText("" + diff, 100, 100, paint);
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
	}
	public static native void setSeed(long seed);
	public static native void setSize(int imgSize);
	public static native void genRandom(byte[] out);

}
