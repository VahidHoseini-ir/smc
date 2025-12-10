package ir.vahidhoseini.gmc.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ir.vahidhoseini.gmc.MaryoGame;
import ir.vahidhoseini.gmc.view.MenuStage;

/**
 * @author Mats Svensson
 */
public abstract class AbstractScreen implements Screen {
    public  MaryoGame game;
    protected MenuStage stage;


    public AbstractScreen(MaryoGame game) {
        this.game = game;
        stage = new MenuStage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void exitToMenu() {
        //TODO direkt in Levelauswahl springen bei Gamescreen
        game.setScreen(new LoadingScreen(new MainMenuScreen(game), false));
    }

    public void quit() {
        game.exit();
    }

    /**
     * Override this method to add assets to loading queue using AssetManager.load()
     */
    public abstract void loadAssets();

    /**
     * Called after after all assets has been loaded, use it to find regions from atlases for example
     */
    public abstract void onAssetsLoaded();
}
