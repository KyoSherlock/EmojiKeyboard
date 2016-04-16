package com.kyo.emoji.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kyo.emoji.sample.EmojiKeyboardActivity;
import com.kyo.emoji.sample.R;
import com.kyo.keyboard.KeyboardHelper;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

/**
 * Created by jianghui on 4/12/16.
 */
public class EmojiKeyboardLayout extends LinearLayout {

	private KeyboardHelper keyboardHelper;
	private View emojiKeyboard;
	private EditText input;
	private View emojiToggleView;
	private View sendButton;

	public EmojiKeyboardLayout(Context context) {
		super(context);
		this.init(context);
	}

	public EmojiKeyboardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	public EmojiKeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public EmojiKeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.init(context);
	}

	private void init(Context context) {
		this.setOrientation(VERTICAL);
		LayoutInflater.from(context).inflate(R.layout.layout_keyboard_emoji, this, true);
		emojiKeyboard = this.findViewById(R.id.emoji_keyboard);
		input = (EditText) this.findViewById(R.id.text_input);
		emojiToggleView = this.findViewById(R.id.emoji_btn);
		sendButton = this.findViewById(R.id.send_btn);


	}

	public void setup(FragmentActivity activity) {
		this.setup(activity, null);
	}

	public void setup(FragmentActivity activity, ListView listView) {
		keyboardHelper = KeyboardHelper.setup(activity, input, emojiKeyboard, emojiToggleView, listView);

		Fragment emojiKeyboardFragment = activity.getSupportFragmentManager().findFragmentByTag(EmojiKeyboardActivity.FRAGMENT_TAG_KEYBOARD);
		if (emojiKeyboardFragment == null) {
			emojiKeyboardFragment = MyEmojiconsKeyboardFragment.newInstance();
			activity.getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.emoji_keyboard, emojiKeyboardFragment, EmojiKeyboardActivity.FRAGMENT_TAG_KEYBOARD)
				.commit();
		}
	}

	public boolean onBackPressed() {
		return keyboardHelper.onBackPressed();
	}

	public EditText getEditText() {
		return input;
	}

	public View getSendButton() {
		return sendButton;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (keyboardHelper == null) {
			throw new IllegalArgumentException("Please invoke setup method!");
		}
	}

	public static class MyEmojiconsKeyboardFragment extends Fragment implements EmojiconGridFragment.OnEmojiconClickedListener,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener {

		private EditText input;

		public static MyEmojiconsKeyboardFragment newInstance() {
			MyEmojiconsKeyboardFragment fragment = new MyEmojiconsKeyboardFragment();
			return fragment;
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_container, container, false);

			Fragment emojiFragment = getChildFragmentManager().findFragmentByTag(EmojiKeyboardActivity.FRAGMENT_TAG_EMOJI);
			if (emojiFragment == null) {
				emojiFragment = EmojiconsFragment.newInstance(false);
				getChildFragmentManager()
					.beginTransaction()
					.add(R.id.fragment_container, emojiFragment, EmojiKeyboardActivity.FRAGMENT_TAG_EMOJI)
					.commit();
			}
			return rootView;
		}

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			View view = getView();
			ViewParent parent = null;
			while (true) {
				parent = parent == null ? view.getParent() : parent.getParent();
				if (parent == null) {
					throw new IllegalArgumentException("MyEmojiconsKeyboardFragment must be in EmojiKeyboardLayout.");
				}
				if (parent instanceof EmojiKeyboardLayout) {
					input = ((EmojiKeyboardLayout) parent).getEditText();
					break;
				}
			}

		}

		@Override
		public void onEmojiconBackspaceClicked(View v) {
			EmojiconsFragment.backspace(input);

		}

		@Override
		public void onEmojiconClicked(Emojicon emojicon) {
			EmojiconsFragment.input(input, emojicon);
		}
	}
}
