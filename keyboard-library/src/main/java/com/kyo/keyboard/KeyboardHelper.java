package com.kyo.keyboard;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by jianghui on 4/12/16.
 */
public class KeyboardHelper {

	private static final long DELAY_LISTVIEW_SCROLL_TO_BOTTOM = 200;
	private Activity activity;
	private View customKeyboardLayout;
	private EditText editText;
	private View emojiToggleView;
	private KeyboardManager keyboardManager;
	private ListView listView;

	private KeyboardHelper(Activity activity, EditText editText, View customKeyboardLayout, View emojiToggleView, ListView listView) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.activity = activity;
		this.keyboardManager = new KeyboardManager(activity);
		this.keyboardManager.setOnKeyboardVisibilityListener(onKeyboardVisibilityListener);
		if (customKeyboardLayout == null) {
			throw new IllegalArgumentException("customKeyboardLayout can not be null!");
		}
		if (emojiToggleView == null) {
			throw new IllegalArgumentException("emojiToggleView can not be null!");
		}
		if (editText == null) {
			throw new IllegalArgumentException("editText can not be null!");
		}
		this.customKeyboardLayout = customKeyboardLayout;
		this.editText = editText;
		this.emojiToggleView = emojiToggleView;
		this.emojiToggleView.setOnClickListener(onClickListener);
		this.listView = listView;
	}

	public static KeyboardHelper setup(Activity activity, EditText editText, View customKeyboardLayout, View emojiToggleView) {
		return new KeyboardHelper(activity, editText, customKeyboardLayout, emojiToggleView, null);
	}

	public static KeyboardHelper setup(Activity activity, EditText editText, View customKeyboardLayout, View emojiToggleView, ListView listView) {
		return new KeyboardHelper(activity, editText, customKeyboardLayout, emojiToggleView, listView);
	}

	public boolean onBackPressed() {
		if (isCustomKeyboardVisible()) {
			hideCustomKeyboard();
			return true;
		}
		return false;
	}

	private KeyboardManager.OnKeyboardVisibilityListener onKeyboardVisibilityListener = new KeyboardManager.OnKeyboardVisibilityListener() {
		@Override
		public void onKeyboardVisibilityChanged(boolean visible, int height) {
			if (visible == true) {
				// After system keyboard is open, reset SoftInputMode to resize
				activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				if (isCustomKeyboardVisible()) {
					// Emoji keyboard is being displayed
					if (emojiToggleView != null) {
						emojiToggleView.setSelected(false);
					}
					customKeyboardLayout.getLayoutParams().height = 0;
					customKeyboardLayout.requestLayout();
				} else {
					// No keyboard is being displayed
					if (listView != null) {
						listView.postDelayed(new Runnable() {
							@Override
							public void run() {
								ListAdapter adapter = listView.getAdapter();
								if (adapter != null && adapter.getCount() > 0) {
									listView.setSelection(adapter.getCount() - 1);
								}
							}
						}, DELAY_LISTVIEW_SCROLL_TO_BOTTOM);
					}
				}
			} else {
				// After the keyboard is closed
				if (isCustomKeyboardVisible()) {
					// Emoji keyboard is being displayed, reset SoftInputMode to adjust_pan.
					activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				} else {
					// No keyboard is being displayed ,reset SoftInputMode to resize.
					activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
					if (emojiToggleView != null) {
						emojiToggleView.setSelected(false);
					}
					if (listView != null) {
						listView.postDelayed(new Runnable() {
							@Override
							public void run() {
								ListAdapter adapter = listView.getAdapter();
								if (adapter != null && adapter.getCount() > 0) {
									listView.setSelection(adapter.getCount() - 1);
								}
							}
						}, DELAY_LISTVIEW_SCROLL_TO_BOTTOM);
					}
				}
			}
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == emojiToggleView) {
				if (v.isSelected()) {
					showSystemKeyboard();
					if(listView != null){
						listView.postDelayed(new Runnable() {
							@Override
							public void run() {
								ListAdapter adapter = listView.getAdapter();
								if (adapter != null && adapter.getCount() > 0) {
									listView.setSelection(adapter.getCount() - 1);
								}
							}
						}, DELAY_LISTVIEW_SCROLL_TO_BOTTOM);
					}
				} else {
					showCustomKeyboard();
				}
			}
		}
	};


	private boolean isCustomKeyboardVisible(){
		return customKeyboardLayout.getLayoutParams().height != 0;
	}

	private void showSystemKeyboard() {
		keyboardManager.showSoftInput(editText);
	}

	private void showCustomKeyboard() {
		if (keyboardManager.isShowingKeyboard()) {
			// system keyboard is being displayed
			emojiToggleView.setSelected(true);
			customKeyboardLayout.getLayoutParams().height = keyboardManager.getLastKnowKeyboardHeight(activity);
			customKeyboardLayout.requestLayout();
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			if (keyboardManager.isShowingKeyboard()) {
				keyboardManager.hideSoftInput(editText);
			}
		} else if (!isCustomKeyboardVisible()) {
			// no keyboard is being displayed
			emojiToggleView.setSelected(true);
			customKeyboardLayout.getLayoutParams().height = keyboardManager.getLastKnowKeyboardHeight(activity);
			customKeyboardLayout.requestLayout();
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		} else {
			// do nothing
		}
	}

	private void hideCustomKeyboard() {
		if (isCustomKeyboardVisible()) {
			emojiToggleView.setSelected(false);
			customKeyboardLayout.getLayoutParams().height = 0;
			customKeyboardLayout.requestLayout();
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		}
	}
}
