package tw.edu.pu.tcyang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder surfaceHolder;
    Bitmap Road, Boy;

    int GreenLightSec, YellowLightSec, RedLightSec; //各燈號秒數
    Boolean BoyMoving = false; //小男孩是否移動
    int BGmoveX = 0; //背景圖片往左捲動像素

    Paint paint; //畫筆
    Rect SrcRect, DestRect; //繪圖所需長方形
    float ratio, w, h; //比例及寬度與長度

    int step = 1; //步數


    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        Road = BitmapFactory.decodeResource(getResources(), R.drawable.road);
        Boy = BitmapFactory.decodeResource(getResources(), R.drawable.boy1);
        paint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = surfaceHolder.lockCanvas(null);
          drawSomething(canvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void drawSomething(Canvas canvas) {
        DrawRoad(canvas);  //道路背景圖繪製
        DrawBoy(canvas);  //小男孩繪製
        DrawLight(canvas);  //紅綠燈繪製
    }

    //道路背景圖繪製
    public void DrawRoad(Canvas canvas){
        //調整圖片高度為裝置解析度高度的95%
        SrcRect = new Rect(0, 0, Road.getWidth(), Road.getHeight());
        ratio = Road.getHeight() / (canvas.getHeight() * 0.95f);
        w = Road.getWidth() / ratio;
        h = Road.getHeight() / ratio;

        //處理背景捲動
        if (BoyMoving){
            BGmoveX -= 5;
        }
        int BGnewX = (int)w + BGmoveX;

        // 如果已捲動整張圖則重新開始，否則用兩張圖拼裝
        if (BGnewX <= 0) {
            BGmoveX = 0;
            // only need one draw
            DestRect = new Rect(0, 0, (int) w, (int) h);
            canvas.drawBitmap(Road , SrcRect, DestRect, null);
        } else {
            // need to draw original and wrap
            DestRect = new Rect(BGmoveX, 0, (int) (w+BGmoveX), (int) h);
            canvas.drawBitmap(Road , SrcRect, DestRect, null);
            DestRect = new Rect(BGnewX, 0, (int) (w+BGnewX), (int) h);
            canvas.drawBitmap(Road , SrcRect, DestRect, null);
        }
    }

    //小男孩繪製
    public void DrawBoy(Canvas canvas){
        if (BoyMoving){  //分數加1，並改變小男孩走路圖示
            step++;
            if (step>8){
                step = 1;
            }
            int res = getResources().getIdentifier("boy" + (step), "drawable", getContext().getPackageName());
            Boy = BitmapFactory.decodeResource(getResources(), res);
        }

        //根據裝置解析度比例調整小男孩位置及大小
        SrcRect = new Rect(0, 0, Boy.getWidth(), Boy.getHeight());
        float w = Boy.getWidth() / ratio;
        float h = Boy.getHeight() / ratio;
        float w0 = canvas.getHeight()* 0.2f;
        float h0 = canvas.getHeight()* 0.93f - h;
        DestRect = new Rect((int) w0, (int) h0, (int) (w0+w), (int) (h0+h));
        canvas.drawBitmap(Boy , SrcRect, DestRect, null);

        //目前步數繪製
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize((int) 60 * canvas.getHeight() / 1080);
        paint.setAntiAlias(true);
        canvas.drawText("圖片編號：" + String.valueOf(step), (int) (0), (int) (canvas.getHeight()*0.1) ,paint);
    }

    //紅綠燈繪製
    public void DrawLight(Canvas canvas) {
        //長方形區域黑色背景
        paint.setColor(Color.BLACK);
        int r = (int) 100 * canvas.getHeight() / 1080;
        canvas.drawRect(canvas.getWidth()-2*r-16, 0,
                canvas.getWidth(), 6*r+30, paint);

        //空心圓形
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);

        //紅綠燈三個圓
        paint.setColor(Color.GREEN);
        canvas.drawCircle(canvas.getWidth() - r -8, 5*r+20, r, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(canvas.getWidth() - r -8, 3*r+10, r, paint);
        paint.setColor(Color.RED);
        canvas.drawCircle(canvas.getWidth() - r -8, r, r, paint);


        //以綠燈為例，畫出實心圓
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(canvas.getWidth() - r -8, 5*r+20, r, paint);

        //顯示各燈號秒數
        paint.setColor(Color.BLUE);
        paint.setTextSize(r);
        canvas.drawText(String.valueOf(GreenLightSec), canvas.getWidth() - 1.5f *r, 5.5f * r + 10 , paint);
        canvas.drawText(String.valueOf(YellowLightSec), canvas.getWidth() - 1.5f *r, 3.5f * r + 5 , paint);
        canvas.drawText(String.valueOf(RedLightSec), canvas.getWidth() - 1.5f *r, 1.5f * r  , paint);
    }


    //初始設定各燈號秒數
    public void SetLightSec(int GreenSec, int YellowSec, int RedSec){
        GreenLightSec = GreenSec;
        YellowLightSec = YellowSec;
        RedLightSec = RedSec;
    }


}
