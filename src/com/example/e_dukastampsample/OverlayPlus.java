package com.example.e_dukastampsample;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class OverlayPlus extends Overlay implements GestureDetector.OnGestureListener{
	

	private MainActivity main;
	private ArrayList<GeoPoint> gp = new ArrayList<GeoPoint>();
	
	public OverlayPlus(MainActivity _main){
		this.main = _main;
		this.gp = main.destinationGP;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO 自動生成されたメソッド・スタブ
				
		if(!shadow){
			
		}
		
		super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTap(GeoPoint p, MapView map) {
		// TODO 自動生成されたメソッド・スタブ
		
		Projection projection = map.getProjection();
		Point point = new Point();
		projection.toPixels(p, point);
		Toast.makeText(this.main, p.toString(), Toast.LENGTH_SHORT).show();
		
		
		return super.onTap(p, map);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		// TODO 自動生成されたメソッド・スタブ
		
		Toast.makeText(this.main, "TTT", Toast.LENGTH_SHORT).show();
		this.main.ctrl.setCenter(new GeoPoint((int)(33.641491*1e6),(int)(130.689182*1e6)));
		
		return true;
		//return super.onTouchEvent(e, mapView);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
