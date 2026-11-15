
package com.alife.game2048;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends Activity {
    private GameView gameView;
    private TextView scoreView;
    private float sx, sy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        gameView = new GameView(this);
        root.addView(gameView, new FrameLayout.LayoutParams(-1, -1));

        scoreView = new TextView(this);
        scoreView.setTextSize(22);
        scoreView.setTextColor(0xFF776E65);
        scoreView.setPadding(40, 40, 40, 40);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = Gravity.TOP | Gravity.END;
        root.addView(scoreView, lp);

        setContentView(root);
        updateScore();
    }

    private void updateScore() {
        scoreView.setText("\\u5f97\\u5206 " + gameView.score);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            sx = e.getX(); sy = e.getY();
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float dx = e.getX() - sx, dy = e.getY() - sy;
            if (Math.abs(dx) < 40 && Math.abs(dy) < 40) return true;
            if (Math.abs(dx) > Math.abs(dy)) {
                gameView.move(dx > 0 ? 1 : 3);
            } else {
                gameView.move(dy > 0 ? 2 : 0);
            }
            gameView.invalidate();
            updateScore();
            return true;
        }
        return super.onTouchEvent(e);
    }

    static class GameView extends View {
        int[][] board = new int[4][4];
        int score = 0;
        Random rnd = new Random();
        android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        android.graphics.Paint textPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

        GameView(android.content.Context c) {
            super(c);
            textPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
            addTile();
            addTile();
        }

        void addTile() {
            java.util.ArrayList<int[]> empty = new java.util.ArrayList<>();
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    if (board[i][j] == 0) empty.add(new int[]{i, j});
            if (empty.isEmpty()) return;
            int[] p = empty.get(rnd.nextInt(empty.size()));
            board[p[0]][p[1]] = rnd.nextInt(10) == 0 ? 4 : 2;
        }

        void move(int dir) {
            boolean moved = false;
            for (int k = 0; k < 4; k++) {
                int[] line = new int[4];
                for (int i = 0; i < 4; i++) {
                    int r = dir == 0 ? i : dir == 2 ? 3 - i : k;
                    int c = dir == 1 ? 3 - i : dir == 3 ? i : k;
                    line[i] = board[r][c];
                }
                int[] res = merge(line);
                for (int i = 0; i < 4; i++) {
                    int r = dir == 0 ? i : dir == 2 ? 3 - i : k;
                    int c = dir == 1 ? 3 - i : dir == 3 ? i : k;
                    if (board[r][c] != res[i]) moved = true;
                    board[r][c] = res[i];
                }
            }
            if (moved) addTile();
        }

        int[] merge(int[] line) {
            int[] t = new int[4];
            int n = 0;
            for (int v : line) if (v != 0) t[n++] = v;
            for (int i = 0; i < 3; i++) {
                if (t[i] != 0 && t[i] == t[i + 1]) {
                    t[i] *= 2;
                    score += t[i];
                    t[i + 1] = 0;
                }
            }
            int[] r = new int[4];
            n = 0;
            for (int v : t) if (v != 0) r[n++] = v;
            return r;
        }

        static final int[] COLORS = {
            0xFFCDC1B4, 0xFFEEE4DA, 0xFFEDE0C8, 0xFFF2B179,
            0xFFF59563, 0xFFF67C5F, 0xFFF65E3B, 0xFFEDCF72,
            0xFFEDCC61, 0xFFEDC850, 0xFFEDC53F, 0xFFEDC22E
        };

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h);
            int pad = size / 40;
            int cell = (size - pad * 5) / 4;
            int left = (w - size) / 2 + pad;
            int top = (h - size) / 2 + pad;
            canvas.drawColor(0xFFFAF8EF);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int x = left + j * (cell + pad);
                    int y = top + i * (cell + pad);
                    int v = board[i][j];
                    int idx = v == 0 ? 0 : (int) (Math.log(v) / Math.log(2));
                    paint.setColor(COLORS[Math.min(idx, COLORS.length - 1)]);
                    canvas.drawRoundRect(x, y, x + cell, y + cell, 8, 8, paint);
                    if (v != 0) {
                        textPaint.setColor(v <= 4 ? 0xFF776E65 : 0xFFF9F6F2);
                        textPaint.setTextSize(cell * (v < 100 ? 0.42f : v < 1000 ? 0.34f : 0.26f));
                        canvas.drawText(String.valueOf(v), x + cell / 2f,
                                y + cell / 2f - (textPaint.descent() + textPaint.ascent()) / 2f, textPaint);
                    }
                }
            }
        }
    }
}
