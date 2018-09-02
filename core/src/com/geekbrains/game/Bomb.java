package com.geekbrains.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bomb implements Poolable {
    private TextureRegion texture;
    private AnimationEmitter animationEmitter;
    private BombEmitter bombEmitter;
    private BotEmitter botEmitter;
    private Bomberman bomberman;
    private  Map map;
    private int radius;
    private int cellX, cellY;
    private float time;
    private float maxTime;
    private boolean active;

    private boolean[] activeDirections = new boolean[4];

    @Override
    public boolean isActive() {
        return active;
    }

    public int getCellX() {
        return cellX;
    }

    public int getCellY() {
        return cellY;
    }

    public Bomb(AnimationEmitter animationEmitter, BombEmitter bombEmitter, BotEmitter botEmitter, Bomberman bomberman, Map map, TextureRegion texture) {
        this.texture = texture;
        this.animationEmitter = animationEmitter;
        this.bombEmitter = bombEmitter;
        this.botEmitter = botEmitter;
        this.map = map;
        this.bomberman = bomberman;
    }

    public void update(float dt) {
        time += dt;
        if (time >= maxTime) {
            boom();
        }
    }

    private void boomWave(int direction, int cellX, int cellY) {
        if (activeDirections[direction])
            if (map.isCellEmpty(cellX, cellY, CheckType.BaseMapObjects)) {
                animationEmitter.createAnimation(cellX * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE, cellY * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE, 4.0f, AnimationEmitter.AnimationType.EXPLOSION);
                boomCollision(cellX, cellY);
                return;
            }
            else if (map.isCellDestructable(cellX, cellY)) {
                map.clearCell(cellX, cellY);
                animationEmitter.createAnimation(cellX * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE, cellY * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE, 4.0f, AnimationEmitter.AnimationType.EXPLOSION);
                bomberman.addScore(100);
            }
        activeDirections[direction] = false;
    }

    private void boomCollision(int cellX, int cellY){
        for(Bot bot : botEmitter.getActiveList())
            if(bot.isActive() && bot.getCellX() == cellX && bot.getCellY() == cellY) {
                bot.blow();
                bomberman.addScore(250);
            }
        for(Bomb bomb : bombEmitter.getActiveList()) {
            if (bomb.isActive() && bomb.cellX == cellX && bomb.cellY == cellY)
                bomb.boom();
        }
        if(bomberman.getCellX() == cellX && bomberman.getCellY() == cellY) {
            bomberman.setWasted();
        }
    }

    public void boom() {
        active = false;
        for (int i = 0; i < activeDirections.length; i++)
            activeDirections[i] = true;
        animationEmitter.createAnimation(cellX * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE, cellY * Rules.CELL_SIZE + Rules.CELL_HALF_SIZE, 4.0f, AnimationEmitter.AnimationType.EXPLOSION);
        boomCollision(cellX, cellY);
        for (int i = 1; i <= radius; i++) {
            boomWave(0, (cellX + i), cellY);
            boomWave(1, (cellX - i), cellY);
            boomWave(2, cellX, (cellY + i));
            boomWave(3, cellX, (cellY - i));
        }
    }

    public void activate(int cellX, int cellY, float maxTime, int radius) {
        this.cellX = cellX;
        this.cellY = cellY;
        this.maxTime = maxTime;
        this.radius = radius;
        this.time = 0.0f;
        this.active = true;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, cellX * Rules.CELL_SIZE, cellY * Rules.CELL_SIZE);
    }
}
