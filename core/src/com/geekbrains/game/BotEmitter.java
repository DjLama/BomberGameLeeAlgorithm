package com.geekbrains.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class BotEmitter extends ObjectPool<Bot> {
    private Map map;
    private Route route;
    private Bomberman bomberman;
    float time;

    public BotEmitter(GameScreen screen){
        this.bomberman = screen.getPlayer();
        this.map = screen.getMap();
        this.route = new Route(map);
    }

    @Override
    protected Bot newObject() {
        return new Bot(BotType.values()[MathUtils.random(0, 1)], route, map, bomberman, MathUtils.random(100, 200));
    }

    public void update(float dt) {
        if (activeList.size() < 5) {
            time += dt;
            if(time > 1) {
                getActiveElement().activate();
                time = 0;
            }
        }
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
