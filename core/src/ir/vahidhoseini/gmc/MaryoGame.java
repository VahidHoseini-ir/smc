package ir.vahidhoseini.gmc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.golfgl.gdxgamesvcs.IGameServiceListener;
import ir.vahidhoseini.gmc.assets.Assets;
import ir.vahidhoseini.gmc.screen.LoadingScreen;
import ir.vahidhoseini.gmc.screen.MainMenuScreen;
import ir.vahidhoseini.gmc.shader.Shader;
import ir.vahidhoseini.gmc.utility.GameSave;
import ir.vahidhoseini.gmc.utility.MyControllerMapping;
import ir.vahidhoseini.gmc.utility.PrefsManager;

public class MaryoGame extends Game implements IGameServiceListener {
    public static final int NATIVE_WIDTH = 1024;
    public static final int NATIVE_HEIGHT = 576;

    public static final String GAME_VERSION = "1.0.1";
    public static final boolean GAME_DEVMODE = false;
    public static final String GAME_STOREURL = "https://play.google.com/store/apps/details?id=ir.vahidhoseini.game.mfl";
    public static final String GAME_WEBURL = "https://mfl.vahidhoseini.ir";

    public static final String GAMESOURCECODE_URL = "https://mfl.vahidhoseini.ir";


    public MyControllerMapping controllerMappings;
    public String isRunningOn = "";
    public Assets assets;
    private Event event;


    public static AdHandler handler;
    public static PrefHandler prefHandler;


    public MaryoGame(Event event, AdHandler handler, PrefHandler prefHandler) {
        this.prefHandler = prefHandler;
        this.handler = handler;
        this.event = event;
    }


    @Override
    public void create() {

        if (!GAME_DEVMODE)
            Gdx.app.setLogLevel(Application.LOG_ERROR);

        assets = new Assets();
        Shader.init();
        GameSave.init();
        assets.manager.load(Assets.SKIN_HUD, Skin.class);

        try {
            controllerMappings = new MyControllerMapping();
            Controllers.addListener(controllerMappings.controllerToInputAdapter);
        } catch (Throwable t) {
            Gdx.app.error("Application", "Controllers not instantiated", t);
        }

        setScreen(new LoadingScreen(new MainMenuScreen(this), false));
    }

    @Override
    public void pause() {
        super.pause();
        // kann null sein wenn preloader versteckt wird
        if (Gdx.app != null) {
            PrefsManager.flush();
        }
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        assets = null;
        Shader.dispose();
    }

    public void exit() {
        Gdx.app.exit();
    }


    public void levelStart(String levelName) {
        if (event != null)
            event.levelStart(levelName);
    }

    public void levelEnd(String levelName, boolean success) {
        if (event != null)
            event.levelEnd(levelName, success);
    }


    @Override
    public void gsOnSessionActive() {

    }

    @Override
    public void gsOnSessionInactive() {

    }

    @Override
    public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
        Gdx.app.error("GS", msg, t);
    }

    public interface Event {

        void levelStart(String levelName);

        void levelEnd(String levelName, boolean success);
    }
}
