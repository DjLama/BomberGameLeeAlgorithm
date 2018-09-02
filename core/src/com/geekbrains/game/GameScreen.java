package com.geekbrains.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private Map map;
    private Bomberman player;
    private AnimationEmitter animationEmitter;
    private BombEmitter bombEmitter;
    private BotEmitter botEmitter;
    private BitmapFont font32;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    public BombEmitter getBombEmitter() {
        return bombEmitter;
    }

    public AnimationEmitter getAnimationEmitter() {
        return animationEmitter;
    }

    public BotEmitter getBotEmitter() {
        return botEmitter;
    }

    public Map getMap() {
        return map;
    }

    public Bomberman getPlayer() {
        return player;
    }

    @Override
    public void show() {
        animationEmitter = new AnimationEmitter();
        map = new Map(this);
        player = new Bomberman(this);
        botEmitter = new BotEmitter(this);
        bombEmitter = new BombEmitter(this);
        player.setBombEmitter(bombEmitter);
        font32 = Assets.getInstance().getAssetManager().get("gomarice32.ttf", BitmapFont.class);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        map.render(batch);
        player.render(batch);
        bombEmitter.render(batch);
        botEmitter.render(batch);
        animationEmitter.render(batch);
        player.renderGUI(batch, font32);
        batch.end();
    }

    public void update(float dt) {
        map.update(dt);
        player.update(dt);
        bombEmitter.update(dt);
        botEmitter.update(dt);
        animationEmitter.update(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
