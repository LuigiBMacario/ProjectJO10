package br.com.luigibmacario.projectko10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

import br.com.luigibmacario.projectko10.entities.GameCharacters;
import br.com.luigibmacario.projectko10.helpers.GameConstants;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private float x, y;
    private Random rand = new Random();
    private GameLoop gameLoop;
    private ArrayList<PointF> mushrooms = new ArrayList<>();
    private int playerAniIndexY, playerFaceDir = GameConstants.Face_Dir.RIGHT;
    private int aniTick;
    private int aniSpeed = 10;

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        gameLoop = new GameLoop(this);
        for (int i = 0; i < 12; i++){
            mushrooms.add(new PointF(rand.nextInt(1080), rand.nextInt(1920)));
        }
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);
        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAniIndexY, playerFaceDir), x, y, null);
        for (PointF pos: mushrooms)
            c.drawBitmap(GameCharacters.MUSHROOM.getSprite(0,0), pos.x, pos.y, null);
        holder.unlockCanvasAndPost(c);
    }

    public void updade(double delta) {
        for (PointF pos: mushrooms) {
            pos.y += delta + 10;
            if (pos.y >= 1920)
                pos.y = 0;
        }
        updadeAnimation();
    }

    private void updadeAnimation(){
        aniTick++;
        if(aniTick >= aniSpeed){
            aniTick = 0;
            playerAniIndexY++;
            if (playerAniIndexY >= 4)
                playerAniIndexY = 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float newX = event.getX();
            float newY = event.getY();
            float xDiff = Math.abs(newX - x);
            float yDiff = Math.abs(newY - y);
            if (xDiff > yDiff) {
                if (newX > x) {
                    playerFaceDir = GameConstants.Face_Dir.RIGHT;
                }else{
                    playerFaceDir = GameConstants.Face_Dir.LEFT;
                }
            }else{
                if (newY > y){
                    playerFaceDir = GameConstants.Face_Dir.DOWN;
                }else{
                    playerFaceDir = GameConstants.Face_Dir.UP;
                }
            }
            x = event.getX();
            y = event.getY();
        }
        return true;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startGameLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}