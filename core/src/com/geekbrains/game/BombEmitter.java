package com.geekbrains.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BombEmitter extends ObjectPool<Bomb> {
    private TextureRegion textureRegion;
    private GameScreen screen;
    private BotEmitter botEmitter;

    @Override
    protected Bomb newObject() {
        return new Bomb(screen.getAnimationEmitter(), this, botEmitter, screen.getPlayer(), screen.getMap(),  textureRegion);
    }

    public BombEmitter(GameScreen screen) {
        this.textureRegion = Assets.getInstance().getAtlas().findRegion("bomb");
        this.screen = screen;
        this.botEmitter = screen.getBotEmitter();
        this.addObjectsToFreeList(10);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
