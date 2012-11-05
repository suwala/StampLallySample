package com.example.e_dukastampsample;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
	//private Integer lat_span,lon_span,north,south,west,east;
	private final float RADISU = 50;
	private Map<Orientations,Integer> mapViewGP = new HashMap<Orientations,Integer>();
	
	
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
			//タッチしたスタンプが取得済みがチェック　観光ガイドを描画
			if(this.main.getStamp(this.main.touchIndex)){
				
				Bitmap guideBmp = BitmapFactory.decodeResource(this.main.getResources(), R.drawable.ic_launcher);
				Rect src = new Rect(0,0,guideBmp.getWidth(),guideBmp.getHeight());
				Rect dst = new Rect(0,0,400,200);
				
				//src=読み込み画像の範囲？ dst= 出力先の範囲
				canvas.drawBitmap(guideBmp, src,dst, null);

				
				//未取得ならアイコンなどを描画
			}else{

				//サークルの描画
				for(GeoPoint g:this.main.destinationGP){
					Projection projection = this.map.getProjection();
					Point point = projection.toPixels(g, null);
					//Location#distanceBetween(A.Latitude,A.Longitude,B.Latitude,B.Longitude,float[])で二点の距離をfloatに代入してくれる

					//緯度経度でラジアンを求め　cos　何やってるか不明
					double radius = Math.cos(Math.toRadians(g.getLatitudeE6()/this.main.destinationGP.get(0).getLongitudeE6()));
					//距離をピクセルに換算？ 50がメートル部分　結果として半径？50mの距離をピクセルで返してくれる？
					float pixel = projection.metersToEquatorPixels(Math.round(this.RADISU/radius));

					Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
					paint.setStyle(Paint.Style.FILL);
					paint.setColor(Color.argb(0x22, 0x33, 0x99, 0xFF));
					canvas.drawCircle(point.x, point.y,pixel, paint);


				}			

				//円の中に居ない場合 居る場合はスタンプが押されてます
				if(!this.collsion()){
					//描画すべきGPを取得
					this.myGPtoViwe();
				}

				//補正したGPに、位置アイコンの描画
				if(this.myGp != null){
					Projection projection = this.map.getProjection();
					Point point = new Point();
					Bitmap icon,balloon=null;
					icon = BitmapFactory.decodeResource(this.main.getResources(), R.drawable.icon03);

					if(this.inMap()){

						projection.toPixels(myGp, point);
						int halfWidth = icon.getWidth();
						int mapWidth = mapView.getWidth();

						//Map外にiconを描画したときの補正
						if(point.x >= mapWidth-halfWidth )
							point.x = mapWidth-halfWidth;
						if(point.y >= mapView.getHeight()-icon.getHeight()/2)
							point.y = mapView.getHeight()-icon.getHeight()/2;

						canvas.drawBitmap(icon, point.x, point.y, null);

					}else{
						balloon = BitmapFactory.decodeResource(this.main.getResources(), R.drawable.balloon);

						projection.toPixels(this.drawGP(), point);



						Location l = new Location("l");
						l.setLatitude(this.myGp.getLatitudeE6()/1e6);
						l.setLongitude(this.myGp.getLongitudeE6()/1e6);

						Location l2 = new Location("l2");
						l2.setLatitude(this.drawGP().getLatitudeE6()/1e6);
						l2.setLongitude(this.drawGP().getLongitudeE6()/1e6);

						//2地点の方位を求め,角度に適応
						float flo = l.bearingTo(l2);
						Log.d("daw",String.valueOf(flo));


						Matrix matrix = new Matrix();
						matrix.postRotate(flo + 180);
						matrix.postScale(0.5f, 0.5f);

						balloon = Bitmap.createBitmap(balloon,0,0,balloon.getWidth(),balloon.getHeight(),matrix,true);

						int balloonWidth = balloon.getWidth();

						//Map外にiconを描画したときの補正
						if(point.x >= mapView.getWidth()-balloonWidth )
							point.x = mapView.getWidth()-balloonWidth;
						if(point.y >= mapView.getHeight()-balloon.getHeight())
							point.y = mapView.getHeight()-balloon.getHeight();


						canvas.drawBitmap(balloon, point.x, point.y,null);


					}


					//				
					//				Log.d("draw","p2"+String.valueOf(p2.x)+":"+String.valueOf(p2.y)+":point"+String.valueOf(point.x)+":"+String.valueOf(point.y));
					//				Log.d("draw",String.valueOf(this.deg(point, p2)));



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
	public boolean inMap(){

		//画面の端同士の緯度経度差を取得

		//Log.d("画面上でのGP","北："+String.valueOf(north)+"南："+String.valueOf(south)+"東："+String.valueOf(west)+"西："+String.valueOf(east));

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
			if(this.myGp.getLatitudeE6() <= this.mapViewGP.get(Orientations.north))
				if(this.myGp.getLatitudeE6() >= this.mapViewGP.get(Orientations.south))
					if(this.myGp.getLongitudeE6() <= this.mapViewGP.get(Orientations.west))
						if(this.myGp.getLongitudeE6() >= this.mapViewGP.get(Orientations.east)){
							Log.d("bool","true");
							return true;
						}
		}
		return false;
	}
	
	//MapViewの各端のGPを求めるメソッド　数値を求めmapVireGPにputする
	public void mapViewGP(){
		
		int lat_span = this.map.getLatitudeSpan();
		int lon_span = this.map.getLongitudeSpan();
		
		//画面の端同士の緯度経度差を取得
		this.mapViewGP.put(Orientations.lan_span, lat_span);
		this.mapViewGP.put(Orientations.lon_span, lon_span);
		
		//MapViewの東西南北を取得　画面中央の緯度　+　表示緯度/2　＝　MapView上端の緯度
		GeoPoint mapGp = this.map.getMapCenter();//491,010,512,599,63
		
		this.mapViewGP.put(Orientations.north,mapGp.getLatitudeE6() + lat_span/2);
		this.mapViewGP.put(Orientations.south,mapGp.getLatitudeE6() - lat_span/2);
		this.mapViewGP.put(Orientations.west,mapGp.getLongitudeE6() + lon_span/2);
		this.mapViewGP.put(Orientations.east,mapGp.getLongitudeE6() - lon_span/2);
		
	}
	
	//描画すべきGPを返すメソッド
	public GeoPoint drawGP(){
		

		int lat,lon;
		lat = this.myGp.getLatitudeE6();
		lon = this.myGp.getLongitudeE6();

		if(this.myGp.getLatitudeE6() > this.mapViewGP.get(Orientations.north))
			lat = this.mapViewGP.get(Orientations.north);
		if(this.myGp.getLatitudeE6() < this.mapViewGP.get(Orientations.south))
			lat = this.mapViewGP.get(Orientations.south);
		if(this.myGp.getLongitudeE6() > this.mapViewGP.get(Orientations.west))
			lon = this.mapViewGP.get(Orientations.west);
		if(this.myGp.getLongitudeE6() < this.mapViewGP.get(Orientations.east))
			lon = this.mapViewGP.get(Orientations.east);


		return new GeoPoint(lat, lon);

	}
	
	public void myGPtoViwe(){
		
		if(this.myGp!=null){
			//ViewのGPを求める＞描画するGPを返す＞描画
			this.mapViewGP();
			this.drawGP();
		}
	}
	
	public GeoPoint getMyGP(){
		return this.myGp;
	}
	
	public void setGeoPoint(GeoPoint p){
		
		this.myGp = p;
	}
	
	//衝突判定に関するメソッド　円の範囲内ならtrueを返す
	public boolean collsion(){
		
		//端末自身のGPを持っていたら処理する
		if(this.myGp!=null){
			/*
			 * このアプリではマップのドラグ？が出来ないため中央値が一定である(?)
			 * そのためMainActiにあるdestinationGPを利用する
			 
			//View上での中央のGP
			GeoPoint mapGp = this.map.getMapCenter();
			
			
			*/
			
			//GP2点の距離を求める（ｍ）　今回はView中央と端末のGPの距離　結果が配列に入る点に注意
			int i = 0;
			for(GeoPoint mapGp:this.main.destinationGP){
				
				float[] result = new float[1];			
				Location.distanceBetween((double)mapGp.getLatitudeE6()/1e6, (double)mapGp.getLongitudeE6()/1e6, (double)this.myGp.getLatitudeE6()/1e6, (double)this.myGp.getLongitudeE6()/1e6,result);
			
				//距離が50m以下ならスタンプをOnにする
				if(result[0] <= this.RADISU && !this.main.getStamp(i)){
					Log.d("MyOver","円の中にいます");
					Toast.makeText(main, "スタンプを取得しました", Toast.LENGTH_SHORT).show();
					main.setStamp(i);
					return true;
				}				
				i++;
			}
		}
		return false;
	}
	
	
}
