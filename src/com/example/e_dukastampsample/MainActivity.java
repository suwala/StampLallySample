package com.example.e_dukastampsample;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends MapActivity {
/*
 * android:clickable="false"でユーザーによる地図の操作のON/OFFができる
 * ZoomLV22が最大
 * ZoomLV19-22辺りで使用？
 */
	
	public MapController ctrl;
	public MapView map;
	public int weight,height;
	public ArrayList<Rect> rectList = new ArrayList<Rect>();
	public boolean[] getStamp = {false,false,false,false,false};
	public ArrayList<GeoPoint> gp = new ArrayList<GeoPoint>();
	
	public MyLocationOverlay myLocationOverlay;
	public MyOverlay myOverlay;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Display disp = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        this.weight = disp.getWidth();
        this.height = disp.getHeight();
        
        this.map = (MapView)this.findViewById(R.id.mapview);
        this.ctrl = map.getController();
        
        this.gp.add(new GeoPoint((int)(33.641491*1e6),(int)(130.689182*1e6)));
        this.gp.add(new GeoPoint((int)(33.644912*1e6),(int)(130.688476*1e6)));
        this.gp.add(new GeoPoint((int)(33.647413*1e6),(int)(130.689903*1e6)));
        this.gp.add(new GeoPoint((int)(33.644501*1e6),(int)(130.694001*1e6)));
        this.gp.add(new GeoPoint((int)(33.643536*1e6),(int)(130.691459*1e6)));
        
        ctrl.setCenter(this.gp.get(0));
        ctrl.setZoom(2);
               
        this.myOverlay = new MyOverlay(this, this.map);
        //使用する位置情報プロバイダを指定
        this.myOverlay.onProviderDisabled(LocationManager.GPS_PROVIDER);
        //自位置の追跡を有効に　マーカー付き！
        this.myOverlay.enableMyLocation();
        //最初に位置情報が確定したときに走る
        this.myOverlay.runOnFirstFix(new Runnable() {

        	@Override
        	public void run() {
        		// TODO 自動生成されたメソッド・スタブ
        		//GPS情報を元に現在地へ移動今回使わない
        		//ctrl.animateTo(myOverlay.getMyLocation());

        		//自位置追跡の無効化　マーカも消える！　
                myOverlay.disableMyLocation();
        		Log.d("main","main");
        	}
        });
        //コンパスの有効化　描画もされる
        //this.myOverlay.enableCompass();
        
        
        //Overlayの追加と再描画
        this.map.getOverlays().add(this.myOverlay);
        this.map.invalidate();

    }
    

    @Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		
		//OverlayPlus plus = new OverlayPlus(this);
		//this.map.getOverlays().add(plus);
		

        this.drawStamp();
	}

	public void mapZoom(View v){
    	int zoomLevel =	this.map.getZoomLevel();
    	zoomLevel =	this.map.getZoomLevel();
    	if(v.getId() == R.id.zoomin){
    		if(zoomLevel < 22){
    			zoomLevel++;
    			this.ctrl.setZoom(zoomLevel);  
    		}
    	}else{
    		if(zoomLevel > 19){
    			zoomLevel--;
    			this.ctrl.setZoom(zoomLevel);   
    		}
    	}
    	
    	TextView tv = (TextView)this.findViewById(R.id.textView1);
    	tv.setText(""+zoomLevel);
    }
    
    @Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
		
		//サービス停止を忘れずに
		this.myOverlay.disableMyLocation();
		this.myOverlay.disableCompass();
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public void drawStamp(){      
		
		this.rectList.clear();
		Bitmap bmp = Bitmap.createBitmap(this.weight,this.height/3,Bitmap.Config.ARGB_8888);
		ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
		
		
		Canvas canvas = new Canvas(bmp);
		
        //スタンプのフレームを描画
        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(),R.drawable.stampfream);
        bmp2 = Bitmap.createScaledBitmap(bmp2,this.weight,this.height/3,true);
        canvas.drawBitmap(bmp2, 0, 0, null);
		
        int x=0,y=0;
        //スタンプを5つ描画
        for(int i = 0;i<this.getStamp.length;i++){
        	
        	//状態により描画するスタンプを取得
        	if(this.getStamp[i])
        		bmp2 = BitmapFactory.decodeResource(getResources(),R.drawable.stampon);
        	else
        		bmp2 = BitmapFactory.decodeResource(getResources(),R.drawable.stampoff);
        
        	//描画する位置の指定
        	Rect rect = new Rect(x, y, x+this.height/3/2, y+this.height/3/2);
        	//
        	canvas.drawBitmap(bmp2, new Rect(0,0,bmp2.getWidth(),bmp2.getHeight()), rect,null);
        	Log.d("draw",rect.toString());
        	
        	this.rectList.add(rect);
        	x+=this.weight/3;
        	if(i==2){
        		x=0;
        		y+=this.height/3/2;
        	}
        		
        }
        iv.setImageBitmap(bmp);
		
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		
		Log.d("touch",String.valueOf(event.getY()));
		Float x=event.getX();
		Float y=event.getY();
		Integer i=0;
		
		for(Rect r:rectList){
			//保持したRectとタッチした点との判定
			if(r.contains(x.intValue(), y.intValue())){
				
				this.getStamp[i]=true;
				this.ctrl.setCenter(this.gp.get(i));
			}
			i++;
		}

		this.drawStamp();
		GeoPoint gp = this.myOverlay.getMyGeoPoint();
		/*if(gp!=null)
			this.ctrl.setCenter(gp);
		*/
		return super.onTouchEvent(event);
	}

    
}
