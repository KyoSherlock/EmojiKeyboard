package com.kyo.emoji.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

	private ListView listView;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) this.findViewById(R.id.listView);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
		adapter.add("emoji keyboard sample");
		adapter.add("custom keyboard sample");
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					startActivity(EmojiKeyboardActivity.createIntent(MainActivity.this));
				} else {
					startActivity(SimpleKeyboardActivity.createIntent(MainActivity.this));
				}
			}
		});
	}


}
