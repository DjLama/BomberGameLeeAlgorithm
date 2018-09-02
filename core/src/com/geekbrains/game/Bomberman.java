package com.geekbrains.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Bomberman {
    public enum State {
        IDLE(0), MOVE(1);

        private int animationIndex;

        State(int animationIndex) {
            this.animationIndex = animationIndex;
        }
    }

    private Map map;
    private Animation[] animations;
    private Vector2 position;
    private Vector2 velocity;
    private float pathCounter;
    private float speed;
    private State currentState;
    private BombEmitter bombEmitter;
    private boolean wasted;
    private float time;
    private boolean blink;
    private StringBuilder tmpStringBuilder;
    private int score;
    private int scoreToShow;

    public int getCellX() {
        return (int) (position.x / Rules.CELL_SIZE);
    }

    public int getCellY() {
        return (int) (position.y / Rules.CELL_SIZE);
    }

    public boolean isWasted() {
        return wasted;
    }

    public void setWasted() {
        if(!wasted) {
            wasted = true;
            time = 0;
            pathCounter = -1.0f;
            addScore(-250);
        }
    }

    public void setBombEmitter(BombEmitter bombEmitter) {
        this.bombEmitter = bombEmitter;
    }

    public Bomberman(GameScreen screen) {
        this.map = screen.getMap();
        this.position = new Vector2(600.0f, 360.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.speed = 230.0f;
        this.pathCounter = -1;
        this.animations = new Animation[State.values().length];
        for (int i = 0; i < State.values().length; i++) {
            this.animations[i] = new Animation();
            this.animations[i].activate(0, 0, 1, new TextureRegion(Assets.getInstance().getAtlas().findRegion("bomber")).split(Rules.CELL_SIZE, Rules.CELL_SIZE)[i], 0.1f, true);
        }
        this.currentState = State.IDLE;
        this.score = 0;
        this.scoreToShow = 0;
        this.tmpStringBuilder = new StringBuilder(32);
    }

    public void render(SpriteBatch batch) {
        if (!wasted)
            batch.draw(animations[currentState.animationIndex].getCurrentRegion(), position.x - Rules.CELL_HALF_SIZE, position.y - Rules.CELL_HALF_SIZE);
        else if (blink)
            batch.draw(animations[currentState.animationIndex].getCurrentRegion(), position.x - Rules.CELL_HALF_SIZE, position.y - Rules.CELL_HALF_SIZE);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        tmpStringBuilder.setLength(0);
        tmpStringBuilder.append("Score: ").append(scoreToShow);
        font.draw(batch, tmpStringBuilder, 20, 700);
    }

    public void addScore(int amount) {
        score += amount;
        if(score < 0)
            score = 0;
    }

    public void update(float dt) {
        if (scoreToShow < score) {
            scoreToShow += 4;
            if (scoreToShow > score) {
                scoreToShow = score;
            }
        }

        if (scoreToShow > score) {
            scoreToShow -= 4;
            if (scoreToShow < score) {
                scoreToShow = score;
            }
        }

        if (wasted) {
            time += dt;
            if ((int) time % 2 == 0)
                blink = true;
            else
                blink = false;
            if (time > 7f) {
                int cellX;
                int cellY;
                do { cellX = MathUtils.random(0, 15); cellY = MathUtils.random(0, 8); }
                while (!map.isCellEmpty(cellX, cellY, CheckType.BaseMapObjectsBombBotCollision));
                position.x = (cellX * 80) + 40;
                position.y = (cellY * 80) + 40;
                wasted = false;
            }
            return;
        }

        animations[currentState.animationIndex].update(dt);

        if (Gdx.input.isKeyPressed(Input.Keys.D) && pathCounter < 0.0f && map.isCellEmpty(getCellX() + 1, getCellY(), CheckType.BaseMapObjectsBombCollision)) {
            velocity.set(speed, 0.0f);
            pathCounter = 0.1f;
            currentState = State.MOVE;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && pathCounter < 0.0f && map.isCellEmpty(getCellX() - 1, getCellY(), CheckType.BaseMapObjectsBombCollision)) {
            velocity.set(-speed, 0.0f);
            pathCounter = 0.1f;
            currentState = State.MOVE;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) && pathCounter < 0.0f && map.isCellEmpty(getCellX(), getCellY() + 1, CheckType.BaseMapObjectsBombCollision)) {
            velocity.set(0.0f, speed);
            pathCounter = 0.1f;
            currentState = State.MOVE;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && pathCounter < 0.0f && map.isCellEmpty(getCellX(), getCellY() - 1, CheckType.BaseMapObjectsBombCollision)) {
            velocity.set(0.0f, -speed);
            pathCounter = 0.1f;
            currentState = State.MOVE;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Bomb b = bombEmitter.getActiveElement();
            b.activate(getCellX(), getCellY(), 2.0f, 5);
        }

        if (pathCounter > 0.0f) {
            position.mulAdd(velocity, dt);
            pathCounter += velocity.len() * dt;
            if (pathCounter >= Rules.CELL_SIZE) {
                position.x = getCellX() * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE;
                position.y = getCellY() * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE;
                pathCounter = -1.0f;
                currentState = State.IDLE;
            }
        }
    }
}
