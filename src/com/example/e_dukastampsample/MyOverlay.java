package com.example.e_dukastampsample;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
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
	private Integer lat_span,lon_span,north,south,west,east;
	
	public MyOverlay(Context context, MapView mapView) {
		super(context, mapView);
		// TODO 自動生成されたコンストラクター・スタブ
		this.main = (MainActivity)context;
		this.map = mapView;
		
		//暫定処理
		this.myGp =new GeoPoint((int)(33.641491*1e6),(int)(130.689900*1e6));
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
								
				//リサイズせずに描画してる Rect boundを使おう
				int halfWidth = icon.getWidth()/2;
				Rect bound = new Rect(point.x-halfWidth,point.x+halfWidth,point.y-icon.getHeight(),point.y);
				
				canvas.drawBitmap(icon, point.x, point.y, null);
				
				
			}
			//画面に表示されてる範囲（高さ）452-1804<zoomLV22-19>　単位は緯度（経度）の差?
			Integer top = mapView.getLatitudeSpan();
			Log.d("draw",top.toString());
			//表示MAP中央のGP
			GeoPoint gp = map.getMapCenter();
			float[] result=new float[1];
			if(this.myGp!=null){
				Location.distanceBetween((double)gp.getLatitudeE6()/1e6, (double)gp.getLongitudeE6()/1e6, (double)this.myGp.getLatitudeE6()/1e6, (double)this.myGp.getLongitudeE6()/1e6,result);
				//家との距離27434.605m
				Log.d("draw",String.valueOf(result[0]));
				if(result[0] <= 50 && !this.main.getStamp(0)){
					Log.d("MyOver","円の中にいます");
					Toast.makeText(main, "スタンプを取得しました", Toast.LENGTH_SHORT).show();
					main.setStamp(0);
					//押すスタンプの判定をわかりやすく
					//スタンプがfalseの時のみ判定すべき
				}

				if(!this.isHereMap()){

					Projection projection = this.map.getProjection();
					Point point = new Point();
					Log.d("mapout",this.mapOutDrawGp().toString());
					projection.toPixels(this.mapOutDrawGp(), point);
										
					Bitmap icon = BitmapFactory.decodeResource(this.main.getResources(), R.drawable.ic_launcher);
					Rect bound = new Rect(point.x-icon.getWidth()/2,point.x+icon.getWidth()/2,point.y-icon.getHeight(),point.y);
					
					//
					canvas.drawBitmap(icon, point.x, point.y-icon.getHeight(), null);
					Log.d("mapOut","icon");

				}
			}
			
		}
				
		return super.draw(canvas, mapView, shadow, when);
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ
		super.onLocationChanged(location);
		
		//試験的にあうと
		//this.myGp = new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()*1e6));
	
	}

	public GeoPoint getMyGeoPoint(){
		return this.myGp;
	}

	//画面内にいるか判定するメソッド　居るならtrue いないならfalse
	public boolean isHereMap(){
		
		//画面の端同士の緯度経度差を取得
		
		this.setMapGPs();
		
		Log.d("画面上でのGP","北："+String.valueOf(north)+"南："+String.valueOf(south)+"東："+String.valueOf(west)+"西："+String.valueOf(east));
		
		if(this.myGp!=null){/*
			Log.d("mygp",String.valueOf(this.myGp.getLatitudeE6())+":"+String.valueOf(this.myGp.getLongitudeE6()));
			
			if(this.myGp.getLatitudeE6() >= north){
				Log.d("draw","北にいます");
			}
			if(this.myGp.getLatitudeE6() <= south){
				Log.d("draw","南にいます");
			}
			if(this.myGp.getLongitudeE6() >= west){
				Log.d("draw","東にいます");
			}
			if(this.myGp.getLongitudeE6() <= east){
				Log.d("draw","西にいます");
			}
			*/
			
			//自身が画面内にいるか判定する　北に行くほど緯度は+（北緯線のみか）　東に行くほど経度は+
			if(this.myGp.getLatitudeE6() <= north)
				if(this.myGp.getLatitudeE6() >= south)
					if(this.myGp.getLongitudeE6() <= west)
						if(this.myGp.getLongitudeE6() >= east){
							return true;
						}
		}
		return false;
	}
	
	//画面外にいるとき　描画するべきGPを返すメソッド
	public GeoPoint mapOutDrawGp(){
		
		//画面の端同士の緯度経度差を取得
		int lat_span = map.getLatitudeSpan();
		int lon_span = map.getLongitudeSpan();
		
		//画面表示上での東西南北を取得　画面中央の緯度　+　表示緯度/2　＝　画面上端の緯度　
		GeoPoint mapGp = this.map.getMapCenter();
		int north = mapGp.getLatitudeE6()+lat_span/2;
		int south = mapGp.getLatitudeE6() - lat_span/2;
		int west = mapGp.getLongitudeE6() + lon_span/2;
		int east = mapGp.getLongitudeE6() - lon_span/2;
		
		int lat,lon;
		lat = this.myGp.getLatitudeE6();
		lon = this.myGp.getLongitudeE6();
		
		if(this.myGp.getLatitudeE6() > north)
			lat = north;
		if(this.myGp.getLatitudeE6() < south)
			lat = south;
		if(this.myGp.getLongitudeE6() > west)
			lon = west;
		if(this.myGp.getLongitudeE6() < east)
			lon = east;
		
		
		return new GeoPoint(lat, lon);
		
	}
	
	//画面の緯度経度スパンや東西南北のGPをセットするメソッド
	public void setMapGPs(){
		
		//画面の端同士の緯度経度差を取得
		this.lat_span = map.getLatitudeSpan();
		this.lon_span = map.getLongitudeSpan();
		
		//画面表示上での東西南北を取得　画面中央の緯度　+　表示緯度/2　＝　画面上端の緯度　
		GeoPoint mapGp = this.map.getMapCenter();
		this.north = mapGp.getLatitudeE6()+lat_span/2;
		this.south = mapGp.getLatitudeE6() - lat_span/2;
		this.west = mapGp.getLongitudeE6() + lon_span/2;
		this.east = mapGp.getLongitudeE6() - lon_span/2;
		
	}
	
	public GeoPoint getMyGP(){
		return this.myGp;
	}
	
	public void setGeoPoint(GeoPoint p){
		
		this.myGp = p;
	}
}
