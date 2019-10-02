package com.zonar.zonarapp.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.zonar.zonarapp.R;
import com.zonar.zonarapp.utils.ArrayUtils;
import com.zonar.zonarapp.utils.ZonarUtils;

import java.util.ArrayList;

public class CircleWaveView extends View {
    private static final String TAG = CircleWaveView.class.getSimpleName();

    private Bitmap gradientBitmap; // 漸層圖
    private Matrix matrix = new Matrix();

    private Path touchPath = new Path();
    private PathMeasure pathMeasure = new PathMeasure(); // 用於計算圈圈在波形上的位置
    private float[] pos = new float[2];
    private float[] tan = new float[2];

    private Path path1 = new Path(); // 畫波形最外圈的 path
    private Path path2 = new Path(); // 畫波形中間的 path
    private Path path3 = new Path(); // 畫波形最內圈的 path
    private Paint paint = new Paint();

    private ArrayList<Float> drawValues = ArrayUtils.newArrayList(10);
    private ArrayList<PointF> peeks1 = new ArrayList<>(); // 用來畫外圈貝氏曲線的參考點
    private ArrayList<PointF> peeks2 = new ArrayList<>(); // 用來畫中圈貝氏曲線的參考點
    private ArrayList<PointF> peeks3 = new ArrayList<>(); // 用來畫內圈貝氏曲線的參考點
    private PointF touchPoint = new PointF(); // 手指觸碰的點

    private float currentNumero = 1f;
    private int currentNumeroInt = 1;

    private boolean isTouchDown = false;

    private OnAngleChangedListener mOnAngleChangedListener;

    public static interface OnAngleChangedListener {
        public void onAngleChanged(float angle);
    }

    public CircleWaveView(Context context) {
        super(context);

        gradientBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rectangle_gradient);

        double[] values = ZonarUtils.getEQData(true, currentNumeroInt, 1);

        for (int i = 0; i < values.length; i++) {
            this.drawValues.set(i, (float) values[i]);
        }

        setListener();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        drawByTouchPoint(canvas, touchPoint.x, touchPoint.y);
    }

    private void drawByTouchPoint(Canvas canvas, float touchX, float touchY) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float distance = calculateDistance(touchX, touchY);
        float radius = getWidth() / 2f;

        // 把 EQ 的 10 個值，每前後兩點中間多差一個靠近中心的值，拓展成 20 個點，讓波形看起來更加起伏
        peeks1.clear();
        peeks2.clear();
        peeks3.clear();
        for (int i = 0; i < drawValues.size(); i++) {
            float dist = (distance * 3 / 4f * drawValues.get(i) / 10f) + (distance / 4f);
            double degree = Math.PI * 2 / 10 * i;

            int ni = (i + 1) % drawValues.size();
            float n_dist = (distance * 3 / 4f * drawValues.get(ni) / 10f) + (distance / 4f);
            double n_degree = Math.PI * 2 / 10 * ni;

            //
            float x1 = (float) (Math.cos(degree) * dist) + centerX;
            float y1 = (float) (Math.sin(degree) * dist) + centerY;
            float n_x1 = (float) (Math.cos(n_degree) * n_dist) + centerX;
            float n_y1 = (float) (Math.sin(n_degree) * n_dist) + centerY;
            float i_x1 = ((x1 + n_x1) / 2 - centerX) * 95 / 100 + centerX;
            float i_y1 = ((y1 + n_y1) / 2 - centerY) * 95 / 100 + centerY;
            peeks1.add(new PointF(x1, y1));
            peeks1.add(new PointF(i_x1, i_y1));

            //
            float x2 = (float) (Math.cos(degree) * dist) * 8 / 10 + centerX;
            float y2 = (float) (Math.sin(degree) * dist) * 8 / 10 + centerY;
            float n_x2 = (float) (Math.cos(n_degree) * n_dist) * 8 / 10 + centerX;
            float n_y2 = (float) (Math.sin(n_degree) * n_dist) * 8 / 10 + centerY;
            float i_x2 = ((x2 + n_x2) / 2 - centerX) * 95 / 100 + centerX;
            float i_y2 = ((y2 + n_y2) / 2 - centerY) * 95 / 100 + centerY;
            peeks2.add(new PointF(x2, y2));
            peeks2.add(new PointF(i_x2, i_y2));

            //
            float x3 = (float) (Math.cos(degree) * dist) * 7 / 10 + centerX;
            float y3 = (float) (Math.sin(degree) * dist) * 7 / 10 + centerY;
            float n_x3 = (float) (Math.cos(n_degree) * n_dist) * 7 / 10 + centerX;
            float n_y3 = (float) (Math.sin(n_degree) * n_dist) * 7 / 10 + centerY;
            float i_x3 = ((x3 + n_x3) / 2 - centerX) * 95 / 100 + centerX;
            float i_y3 = ((y3 + n_y3) / 2 - centerY) * 95 / 100 + centerY;
            peeks3.add(new PointF(x3, y3));
            peeks3.add(new PointF(i_x3, i_y3));
        }

        // 畫三個等高線圖
        addPathByPeek(path1, peeks1);
        addPathByPeek(path2, peeks2);
        addPathByPeek(path3, peeks3);

        paint.reset();
        paint.setColor(0xff946b4a);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path1, paint);

        paint.reset();
        paint.setColor(0xffb8845c);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path2, paint);

        paint.reset();
        paint.setColor(0xffc28b61);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path3, paint);

        // 背景漸層圖，並利用 PorterDuff.Mode.DST_OUT，讓漸層效果只呈現在波形內部
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        double angle = Math.atan2(touchY - centerY, touchX - centerX) * 180 / Math.PI;
        matrix.reset();
        matrix.postScale(rect.width() / (float) gradientBitmap.getWidth(), rect.height() / (float) gradientBitmap.getHeight());
        matrix.postRotate((float) angle + 90, centerX, centerY);
        paint.reset();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawBitmap(gradientBitmap, matrix, paint);

        // 手指頭放開之後，利用 pathMeasure 去決定圈圈位置
        if (angle < 0) {
            angle += 360;
        }
        touchPath.reset();
        touchPath.addCircle(0, 0, radius / 13f, Path.Direction.CW);
        pathMeasure.setPath(path1, false);
        float length = pathMeasure.getLength(); // 整個波形的邊長為多少
        double progress = -1;
        float slope1 = 1;
        float slope2 = 1;
        try {
            // 尋找手指頭的角度對應到波形上 progress 是多少
            for (int i = 0; i < 360; i++) {
                pathMeasure.getPosTan(length * i / 360f, pos, tan);
                slope1 = (pos[1] - centerY) / (pos[0] - centerX);
                slope2 = (touchY - centerY) / (touchX - centerX);
                if (slope1 / slope2 >= 0.95 &&
                        slope1 / slope2 <= 1.05 && // 最佳應該是 slope1 / slope2 == 1，但實際上可能很難剛好，所以取一點buffer，在 0.95 與 1.05 之間就當作找到角度了
                        ((pos[1] - centerY) / (touchY - centerY)) > 0 &&
                        ((pos[0] - centerX) / (touchX - centerX)) > 0) { // 這裡是確保手指頭到中心的向量確實跟找到的角度向量一致，不然可能會偏差 180 度
                    progress = i;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 以防萬一，沒找到角度，那就以手指頭角度為 progress，這裡可能會問，為什麼不乾脆用手指頭角度當 progress
        // 因為波形不是一個完美的圓，角度假設為 30 度，並不代表 progress 也會是 30，所以要先用上面的迴圈去找出最相近的 progress
        // 也因為是相近值，所以有時會需要一點額外校正，如下 (可能可以優化)
        if (progress == -1) {
            progress = angle;
            pathMeasure.getPosTan(length * ((float) progress + 10f) / 360f, pos, tan);
        } else {
            pathMeasure.getPosTan(length * ((float) progress + 5f) / 360f, pos, tan);
        }

        // 手指頭位置
        paint.reset();
        paint.setColor(0xffa47b5a);
        paint.setStyle(Paint.Style.FILL);
        if (isTouchDown) {
            canvas.drawCircle(touchX, touchY, radius / 13f, paint);
        } else {
            canvas.drawCircle(pos[0], pos[1], radius / 13f, paint);
        }
    }

    // 畫波形，把 peek 透過 quadTo，加到 path 裡
    private void addPathByPeek(Path path, ArrayList<PointF> peeks) {
        path.reset();
        float firstMiddleX = (peeks.get(peeks.size() - 1).x + peeks.get(0).x) / 2;
        float firstMiddleY = (peeks.get(peeks.size() - 1).y + peeks.get(0).y) / 2;
        path.moveTo(firstMiddleX, firstMiddleY);
        for (int i = 0; i < peeks.size(); i++) {
            int ni = (i + 1) % peeks.size();

            float middleX = (peeks.get(i).x + peeks.get(ni).x) / 2;
            float middleY = (peeks.get(i).y + peeks.get(ni).y) / 2;

            path.quadTo(peeks.get(i).x, peeks.get(i).y, middleX, middleY);
        }
        path.close();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        touchPoint.x = w * 5 / 5f;
        touchPoint.y = h / 2f;

        postInvalidate();
    }

    private void setListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isTouchDown = true;
                    modifyValues(x, y);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    isTouchDown = true;
                    modifyValues(x, y);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    isTouchDown = false;
                    modifyValues(x, y);
                }

                return true;
            }
        });
    }

    // 手指頭在移動時，為了讓波形流暢，所以會抓取前後 EQ 值做內插
    private void modifyValues(float x, float y) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = getWidth() / 2f;

        touchPoint.x = x;
        touchPoint.y = y;

        // 計算手指頭離中心點多遠，其距離會影響 mode 值
        float min_distance = radius / 3f;
        float max_distance = radius;
        float distance = calculateDistance(touchPoint.x, touchPoint.y);
        if (distance < min_distance) {
            float ratio = min_distance / distance;
            touchPoint.x = (touchPoint.x - centerX) * ratio + centerX;
            touchPoint.y = (touchPoint.y - centerY) * ratio + centerY;
        }
        if (distance > max_distance) {
            float ratio = max_distance / distance;
            touchPoint.x = (touchPoint.x - centerX) * ratio + centerX;
            touchPoint.y = (touchPoint.y - centerY) * ratio + centerY;
        }
        float final_distance = calculateDistance(touchPoint.x, touchPoint.y);

        postInvalidate();

        // 計算手指頭的位置角度，其角度在 360 度裡是第幾等分，會影響 numero 值
        float angle = (float) (Math.atan2(y - centerY, x - centerX) * 180 / Math.PI);
        if (angle <= 0) {
            angle += 360;
        }

        // 將角度回傳給 ZaMajorLayout.java
        if (mOnAngleChangedListener != null) {
            mOnAngleChangedListener.onAngleChanged(angle);
        }

        // 計算 numero
        float numero = angle / 18f;

        if (currentNumero == numero) {
            return;
        }
        currentNumero = numero;

        // 這裡決定前後 numero，在後面會依照 numeroInt 與 n_numeroInt 來取前後 EQ 做內差
        int numeroInt = (int) Math.ceil(numero);
        int n_numeroInt = numeroInt == 20 ? 1 : numeroInt + 1;

        if (currentNumeroInt != numeroInt) {
            currentNumeroInt = numeroInt;
        }

        // 決定 mode
        int mode = 0;
        if (final_distance >= radius / 3f + (radius * 2f / 3f) * 2f / 3f) {
            mode = 2;
        } else if (final_distance >= radius / 3f + (radius * 2f / 3f) * 1f / 3f) {
            mode = 1;
        }

        // 做內差，取得要畫圖的內差 EQ
        double[] values = ZonarUtils.getEQData(true, numeroInt, mode); // 會寫入 SDK，並取值
        double[] n_values = ZonarUtils.getEQData(false, n_numeroInt, mode); // 不會寫入 SDK，單純取值
        for (int i = 0; i < values.length; i++) {

            double value = (n_values[i] - values[i]) * (numero - (int) numero) + values[i];

            this.drawValues.set(i, (float) value);
        }

        postInvalidate();
    }

    // 計算與中心距離
    private float calculateDistance(float x, float y) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        return (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
    }

    public void setOnAngleChangedListener(OnAngleChangedListener onAngleChangedListener) {
        mOnAngleChangedListener = onAngleChangedListener;
    }

}
