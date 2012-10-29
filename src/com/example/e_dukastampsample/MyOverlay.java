package com.example.e_dukastampsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

public class MyOverlay extends MyLocationOverlay {
	
	private MainActivity main;
	private MapView map;
	
	public MyOverlay(Context context, MapView mapView) {
		super(context, mapView);
		// TODO 自動生成されたコンストラクター・スタブ
		this.main = (MainActivity)context;
		this.map = mapView;
	}
	
	
	@Override
	public synchronized boolean draw(Canvas canvas, MapView mapView,
			boolean shadow, long when) {
		// TODO 自動生成されたメソッド・スタブ
		
		if(!shadow){
			for(GeoPoint g:this.main.gp){
				Projection projection = this.map.getProjection();
				Point point = projection.toPixels(g, null);
				//Location#distanceBetween(A.Latitude,A.Longitude,B.Latitude,B.Longitude,float[])で二点の距離をfloatに代入してくれる

				//緯度経度でラジアンを求め　cos　何やってるか不明
				double radius = Math.cos(Math.toRadians(g.getLatitudeE6()/this.main.gp.get(0).getLongitudeE6()));
				//距離をピクセルに換算？ 50がメートル部分　結果として半径？50mの距離をピクセルで返してくれる？
				float pixel = projection.metersToEquatorPixels(Math.round(50/radius));

				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.argb(0x22, 0x33, 0x99, 0xFF));
				canvas.drawCircle(point.x, point.y,pixel, paint);
			}
					
		}
		
		return super.draw(canvas, mapView, shadow, when);
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ
		super.onLocationChanged(location);
	}

	

}
