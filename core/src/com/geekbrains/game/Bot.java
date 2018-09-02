package com.geekbrains.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

enum BotType { Walker, Hunter }

public class Bot implements Poolable {
    private Animation[] animations;
    private Route route;
    private Bomberman bomberman;
    private State currentState;
    private BotType botType;
    private Map map;

    private Vector2 position;
    private Vector2 velocity;
    private float pathCounter;
    private float speed;

    private int[][] arr = new int[Map.MAP_CELLS_WIDTH * Map.MAP_CELLS_HEIGHT][2];

    private boolean active;

    @Override
    public boolean isActive() {
        return active;
    }

    public void activate(){
        getNewPosition();
        active = true;
    }

    public enum State {
        IDLE(0), MOVE(1);

        private int animationIndex;

        State(int animationIndex) {
            this.animationIndex = animationIndex;
        }
    }

    public Bot(BotType botType, Route route, Map map, Bomberman bomberman, float speed) {
        this.botType = botType;
        this.animations = new Animation[State.values().length];
        for (int i = 0; i < Bomberman.State.values().length; i++) {
            this.animations[i] = new Animation();
            this.animations[i].activate(0, 0, 1, new TextureRegion(Assets.getInstance().getAtlas().findRegion("bot")).split(Rules.CELL_SIZE, Rules.CELL_SIZE)[i], 0.1f, true);
        }
        this.currentState = State.IDLE;
        this.route = route;
        this.map = map;
        this.bomberman = bomberman;
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.speed = speed;
        this.pathCounter = -1;
        getNewPosition();
    }

    public int getCellX() {
        return (int) (position.x / Rules.CELL_SIZE);
    }

    public int getCellY() {
        return (int) (position.y / Rules.CELL_SIZE);
    }

    public void render(SpriteBatch batch) {
        if (active)
            batch.draw(animations[currentState.animationIndex].getCurrentRegion(), position.x - Rules.CELL_HALF_SIZE, position.y - Rules.CELL_HALF_SIZE);
    }

    public void blow() {
        active = false;
        pathCounter = -1.0f;
        currentState = State.IDLE;
    }

    private void getNewPosition(){
        int cellX;
        int cellY;
        do { cellX = MathUtils.random(0, 15); cellY = MathUtils.random(0, 8); }
        while (!map.isCellEmpty(cellX, cellY, CheckType.AllMapObjects));
        position.x = (cellX * 80) + 40;
        position.y = (cellY * 80) + 40;
    }

    public void update(float dt) {
        if(bomberman.isWasted())
            return;

        animations[currentState.animationIndex].update(dt);

        if (getCellX() == bomberman.getCellX() && getCellY() == bomberman.getCellY())
            bomberman.setWasted();

        if (currentState == State.IDLE && !bomberman.isWasted()) {
            if (botType == BotType.Hunter && route.getRoute(arr, getCellX(), getCellY(), bomberman.getCellX(), bomberman.getCellY())) {
                currentState = State.MOVE;
            } else {
                route.getRandomRoute(arr, getCellX(), getCellY());
                currentState = State.MOVE;
            }
        }

        if (pathCounter < 0.0f && currentState == State.MOVE) {
            if (getCellX() < arr[0][0]) {
                velocity.set(speed, 0.0f);
                pathCounter = 0.1f;
                currentState = State.MOVE;
            }
            if (getCellX() > arr[0][0]) {
                velocity.set(-speed, 0.0f);
                pathCounter = 0.1f;
                currentState = State.MOVE;
            }
            if (getCellY() < arr[0][1]) {
                velocity.set(0.0f, speed);
                pathCounter = 0.1f;
                currentState = State.MOVE;
            }
            if (getCellY() > arr[0][1]) {
                velocity.set(0.0f, -speed);
                pathCounter = 0.1f;
                currentState = State.MOVE;
            }
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