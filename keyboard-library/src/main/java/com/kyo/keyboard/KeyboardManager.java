package com.kyo.keyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jianghui on 4/12/16.
 */
public class KeyboardManager {

	private static final String TAG = "KeyboardManager";
	private static final boolean debug = true;
	private static final String PREFERENCES_NAME = "keyboard";
	private static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";
	private static final int DEFAULT_KEYBOARD_HEIGHT = 300;
	private final Activity activity;
	private final InputMethodManager inputMethodManager;
	private final OnKeyboardVisibilityListener internalListener; // internal
	private final SharedPreferences sharedPreferences;
	private OnKeyboardVisibilityListener listener;

	public KeyboardManager(Activity activity) {
		this.activity = activity;
		this.inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		this.sharedPreferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		this.internalListener = new MyOnKeyboardVisibilityListener();
		this.monitorKeyboard();
	}

	/**
	 * 设置键盘监听
	 *
	 * @param onKeyboardVisibilityListener
	 */
	public void setOnKeyboardVisibilityListener(OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
		this.listener = onKeyboardVisibilityListener;
	}

	public InputMethodManager getInputMethodManager() {
		return inputMethodManager;
	}

	public void showSoftInput(View view) {
		inputMethodManager.showSoftInput(view, 0);
	}

	public void hideSoftInput(View view) {
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 获取最后一次键盘高度
	 *
	 * @param activity
	 * @return
	 */
	public static int getLastKnowKeyboardHeight(Activity activity) {
		final SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		float scale = activity.getResources().getDisplayMetrics().density;
		int defaultHeight = (int) (DEFAULT_KEYBOARD_HEIGHT * scale + 0.5f);
		int height = sharedPreferences.getInt(KEY_KEYBOARD_HEIGHT, 1011);
		return height;
//		return 1011;
	}

	/**
	 * 获取键盘高度
	 *
	 * @return
	 */
	public int getCurrentKeyboardHeight() {
		Rect r = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
		int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
		int softInputHeight = screenHeight - r.bottom;
		if (Build.VERSION.SDK_INT >= 18) {
			// When SDK Level >= 18, the softInputHeight will contain the softKeyboardHeight of softButtonsBar (if has)
			softInputHeight = softInputHeight - getSoftButtonsBarHeight(activity);
		}
		return softInputHeight;
	}

	public boolean isShowingKeyboard() {
		return getCurrentKeyboardHeight() > 0;
	}

	/**
	 * 监控键盘
	 */
	private void monitorKeyboard() {
		View activityRootView = activity.getWindow().getDecorView().getRootView();
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			private boolean wasOpened;
			private final Rect r = new Rect();

			@Override
			public void onGlobalLayout() {
				if (inputMethodManager.isFullscreenMode()) {
					if (debug) {
						Log.i(TAG, "monitorKeyboard, fullscreen mode");
					}
					return;
				}
				int keyboardHeight = getCurrentKeyboardHeight();
				if (debug) {
					Log.i(TAG, "monitorKeyboard, keyboard height:" + keyboardHeight);
				}
				boolean isOpen;
				if (keyboardHeight > 0) {
					isOpen = true;
				} else {
					isOpen = false;
				}
				if (isOpen == wasOpened) {
					return;
				}
				wasOpened = isOpen;
				keyboardHeight = isOpen ? keyboardHeight : 0;
				internalListener.onKeyboardVisibilityChanged(isOpen, keyboardHeight);
				if (listener != null) {
					listener.onKeyboardVisibilityChanged(isOpen, keyboardHeight);
				}
			}
		});
	}

	/**
	 * 获取虚拟键高度（返回，HOME，MENU）
	 *
	 * @param context
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static int getSoftButtonsBarHeight(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		int usableHeight = metrics.heightPixels;
		windowManager.getDefaultDisplay().getRealMetrics(metrics);
		int realHeight = metrics.heightPixels;
		if (realHeight > usableHeight) {
			return realHeight - usableHeight;
		} else {
			return 0;
		}
	}

	private class MyOnKeyboardVisibilityListener implements OnKeyboardVisibilityListener {
		@Override
		public void onKeyboardVisibilityChanged(boolean visible, int height) {
			if (visible) {
				sharedPreferences.edit().putInt(KEY_KEYBOARD_HEIGHT, height).apply();
			}
		}
	}

	/**
	 * 键盘高度监听
	 */
	public interface OnKeyboardVisibilityListener {
		void onKeyboardVisibilityChanged(boolean visible, int height);
	}
}
