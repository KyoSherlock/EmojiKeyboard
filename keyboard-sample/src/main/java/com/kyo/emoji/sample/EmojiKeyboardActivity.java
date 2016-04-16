package com.kyo.emoji.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kyo.emoji.sample.view.EmojiKeyboardLayout;
import com.kyo.emoji.sample.view.KeyboardLayout;

/**
 * Created by jianghui on 4/13/16.
 */
public class EmojiKeyboardActivity extends AppCompatActivity {

	private static final long DELAY_LISTVIEW_SCROLL_TO_BOTTOM = 200;
	public static final String FRAGMENT_TAG_EMOJI = "emoji";
	public static final String FRAGMENT_TAG_KEYBOARD = "custom_keyboard";
	private ListView listView;
	private EmojiKeyboardLayout keyboardLayout;
	private EditText editText;
	private View sendButton;
	private ArrayAdapter<String> adapter;

	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, EmojiKeyboardActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard_emoji);

		listView = (ListView) this.findViewById(R.id.listView);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		adapter = new ArrayAdapter<>(this, R.layout.item_emoji_textview);
		listView.setAdapter(adapter);
		keyboardLayout = (EmojiKeyboardLayout) this.findViewById(R.id.keyboardLayout);
		keyboardLayout.setup(this, listView);
		editText = keyboardLayout.getEditText();
		sendButton = keyboardLayout.getSendButton();
		sendButton.setOnClickListener(onClickListener);
	}

	@Override
	public void onBackPressed() {
		if (!keyboardLayout.onBackPressed()) {
			super.onBackPressed();
		}
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == sendButton) {
				String input = editText.getText().toString();
				if (!TextUtils.isEmpty(input)) {
					adapter.add(input);
					adapter.notifyDataSetChanged();
					editText.setText("");
				}
			}
		}
	};
}