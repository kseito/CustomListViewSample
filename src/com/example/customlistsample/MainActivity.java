package com.example.customlistsample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements AbsListView.OnScrollListener {
	
	//ヘッダ親レイアウト
	LinearLayout headerView;
	int headerHeight;
	
	//スワイプ量によって高さが変わる画像
	ImageView headerImage;
	int headerImageHeight;
	
	int moveDistance = 0;
	int preDistance = 0;
	int newListHeight = 0;
	int newImageHeight = 0;
	
	private ListView listView;
	
	private SwipeRefreshLayout swipeRefreshLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < 100; i++) {
			list.add(""+i);
		}
		
		//プルリフレッシュの実装
		swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				
			}
		});
		swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.GREEN, Color.YELLOW);

		//リストビュー設定
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		listView = (ListView)findViewById(R.id.listView1);
		listView.setOnScrollListener(this);
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		//親ヘッダの高さ取得
		headerView = (LinearLayout)findViewById(R.id.abesi);
		headerHeight = headerView.getHeight();
		
		//画像の高さ取得
		headerImage = (ImageView)findViewById(R.id.imageView1);
		headerImageHeight = headerImage.getHeight();
		
		//リストビューのトップをヘッダの直下へ合わせる
		ViewGroup.MarginLayoutParams listParams = (MarginLayoutParams) swipeRefreshLayout.getLayoutParams();
		listParams.setMargins(0, headerHeight, 0, 0);
		swipeRefreshLayout.setLayoutParams(listParams);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
			View header = (View) view.getChildAt(firstVisibleItem);
			int height = header == null ? 0 : header.getHeight();
			
			//計算に必要なビューの高さがすべて取得できたら
			if(height > 0 && headerHeight > 0 && headerImageHeight > 0) {
				
//				moveDistance = ((firstVisibleItem) * height  + (height * firstVisibleItem - header.getTop()));
				moveDistance = 2 * height * firstVisibleItem - header.getTop();	//スワイプ量を取得する(上記式の展開式)

				//同じ高さが連続して取得できたらはじく
				if(moveDistance == preDistance) {
					return;
				}
				preDistance = moveDistance;
				
				//新しくセットする高さ。等倍だとブレが激しいため倍率3/4にしている
				newListHeight = headerHeight - moveDistance * 3 / 4;
				newImageHeight = headerImageHeight - moveDistance * 3 / 4;
				
				//画像の高さがマイナスの場合
				if(newImageHeight < 0) {
					newImageHeight = 0;
				}
				
				//画像高さ更新
				headerImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, newImageHeight));
				
				//レスとビューのレイアウト更新
				ViewGroup.MarginLayoutParams listParams = (MarginLayoutParams) swipeRefreshLayout.getLayoutParams();
				listParams.setMargins(0, (int) (newListHeight), 0, 0);
				swipeRefreshLayout.setLayoutParams(listParams);
				
			}
			
	}

}
