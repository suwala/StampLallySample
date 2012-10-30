package com.example.e_dukastampsample;

import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

public class MyOverlay extends MyLocationOverlay {
	
	private MainActivity main;
	private MapView map;
	private GeoPoint myGp=null;
	
	public MyOverlay(Context context, MapView mapView) {
		super(context, mapView);
		// TODO 自動生成されたコンストラクター・スタブ
		this.main = (MainActivity)context;
		this.map = mapView;
		
		//暫定処理
		//this.myGp = new GeoPoint(33555169, 130308480);
	}
	
	
	@Override
	public synchronized boolean draw(Canvas canvas, MapView mapView,
			boolean shadow,long when) {
		// TODO 自動生成されたメソッド・スタブ
		
		super.draw(canvas, mapView, shadow);
		
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
			
			if(this.myGp != null){
				Projection projection = this.map.getProjection();
				Point point = new Point();
				projection.toPixels(myGp, point);
				Bitmap icon = BitmapFactory.decodeResource(this.main.getResources(), R.drawable.icon03);
								
				int halfWidth = icon.getWidth()/2;
				Rect bound = new Rect(point.x-halfWidth,point.x+halfWidth,point.y-icon.getHeight(),point.y);
				
				canvas.drawBitmap(icon, point.x, point.y, null);
				
				
			}
			//画面に表示されてる範囲（高さ）452-1804<zoomLV22-19>単位が不明　緯度（経度）の差?
			Integer top = mapView.getLatitudeSpan();
			Log.d("draw",top.toString());
			GeoPoint gp = map.getMapCenter();
			float[] result=new float[1];
			if(this.myGp!=null)
				Location.distanceBetween((double)gp.getLatitudeE6()/1e6, (double)gp.getLongitudeE6()/1e6, (double)this.myGp.getLatitudeE6()/1e6, (double)this.myGp.getLongitudeE6()/1e6,result);
			//家との距離27434.605
			Log.d("draw",String.valueOf(result[0]));
			int lat_span = map.getLatitudeSpan();
			int lng_span = map.getLongitudeSpan();
			
			//画面上端の緯度　myGP<=north myGP>=south myGP<=east myGP>=west　を満たせば画面内にいる
			int north = (gp.getLongitudeE6()+(lng_span/2));
			int south = gp.getLongitudeE6() - (lng_span/2);
			int west = gp.getLatitudeE6() + lat_span/2;
			int east = gp.getLatitudeE6() - lat_span/2;
			
			Log.d("darw","北："+String.valueOf(north)+"南："+String.valueOf(south)+"東："+String.valueOf(west)+"西："+String.valueOf(east));
			
			if(this.myGp!=null){
			if(this.myGp.getLongitudeE6() >= north){
				Log.d("draw","北にいます");
			}
			if(this.myGp.getLongitudeE6() <= south){
				Log.d("draw","南にいます");
			}
			if(this.myGp.getLatitudeE6() >= west){
				Log.d("draw","東にいます");
			}
			if(this.myGp.getLatitudeE6() <= east){
				Log.d("draw","西にいます");
			}
			}
			Log.d("draw_span",String.valueOf(north));
		}
		
		return super.draw(canvas, mapView, shadow, when);
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ
		super.onLocationChanged(location);
		
		this.myGp = new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()*1e6));
	
	}

	public GeoPoint getMyGeoPoint(){
		return this.myGp;
	}

}
