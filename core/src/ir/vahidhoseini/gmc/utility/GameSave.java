package ir.vahidhoseini.gmc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.gamestate.ILoadGameStateResponseListener;
import de.golfgl.gdxgamesvcs.gamestate.ISaveGameStateResponseListener;
import ir.vahidhoseini.gmc.MaryoGame;
import ir.vahidhoseini.gmc.assets.Assets;
import ir.vahidhoseini.gmc.audio.SoundManager;
import ir.vahidhoseini.gmc.object.maryo.Maryo;
import ir.vahidhoseini.gmc.screen.AbstractScreen;

public class GameSave {
    private static int levelScore;
    private static long levelPlaytime;
    private static int levelStartedNum;
    private static int maryoState;
    private static int persistentMaryoState;
    private static int lifes;
    private static long totalPlaytime;
    private static int coins;
    private static int item;
    private static int persistentItem;
    private static int totalScore;
    private static int bestTotal;
    private static int gameOverNum;


    public static long getLevelPlaytime() {
        return levelPlaytime;
    }

    public static void addLevelPlaytime(long timeToAdd) {
        levelPlaytime += timeToAdd;
        totalPlaytime += timeToAdd;
    }

    public static void init() {
        // gibt es bereits einen gespeicherten Stand?
        boolean didRead = false;
        try {
            String savedGame = PrefsManager.getSaveGame();
            if (savedGame != null) {
                readFromJson(savedGame);
                didRead = true;
            }
        } catch (Throwable t) {
            Gdx.app.error("GameSave", "Error loading saved state", t);
        }

        if (!didRead || lifes <= 0)
            resetGameOver();
    }

    /**
     * read saved game, if gameovernum and levelstarts are greater than current state
     *
     * @param savedgame json
     * @return true if savegame was read, false otherwise
     */
    private static boolean readFromJson(String savedgame) {
        JsonValue savegame = new JsonReader().parse(savedgame);
        int readLevelStartedNum = savegame.getInt("levelstarts", 0);
        int readGameOverNum = savegame.getInt("gameovers", 1);

        boolean readStateIsNewer = (readGameOverNum > gameOverNum
                || readGameOverNum == gameOverNum && readLevelStartedNum > levelStartedNum);

        if (readStateIsNewer) {
            lifes = savegame.getInt("lifes");
            coins = savegame.getInt("coins");
            persistentItem = savegame.getInt("item");
            persistentMaryoState = savegame.getInt("state");
            totalPlaytime = savegame.getLong("playtime");
            levelStartedNum = readLevelStartedNum;
            gameOverNum = readGameOverNum;
            item = persistentItem;
            maryoState = persistentMaryoState;
        }

        // following
        bestTotal = Math.max(bestTotal, savegame.getInt("bestTotal", 0));

        JsonValue levelList = savegame.get("levels");
        for (JsonValue jsonlevel = levelList.child; jsonlevel != null; jsonlevel = jsonlevel.next) {
            String levelId = jsonlevel.getString("id");
            Level level = Level.getLevel(levelId);
            if (level != null) {
                if (readStateIsNewer)
                    level.currentScore = jsonlevel.getInt("score");
                level.bestScore = Math.max(level.bestScore, jsonlevel.getInt("best"));
            }
        }

        recalcTotalScore();

        return readStateIsNewer;
    }

    private static JsonValue toJson() {
        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("lifes", new JsonValue(lifes));
        json.addChild("coins", new JsonValue(coins));
        json.addChild("item", new JsonValue(persistentItem));
        json.addChild("state", new JsonValue(persistentMaryoState));
        json.addChild("playtime", new JsonValue(totalPlaytime));
        json.addChild("levelstarts", new JsonValue(levelStartedNum));
        json.addChild("gameovers", new JsonValue(gameOverNum));
        json.addChild("bestTotal", new JsonValue(bestTotal));

        JsonValue levelArray = new JsonValue(JsonValue.ValueType.array);
        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            if (level.bestScore > 0) {
                JsonValue levelJson = new JsonValue(JsonValue.ValueType.object);
                levelJson.addChild("id", new JsonValue(levelId));
                levelJson.addChild("score", new JsonValue(level.currentScore));
                levelJson.addChild("best", new JsonValue(level.bestScore));
                levelArray.addChild(levelJson);
            }
        }
        json.addChild("levels", levelArray);

        return json;
    }

    public static void resetGameOver() {
        lifes = 4;
        item = 0;
        coins = 0;
        persistentMaryoState = 0;
        maryoState = 0;
        totalPlaytime = 0;
        totalScore = 0;
        gameOverNum++;

        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);
            level.currentScore = 0;
        }
    }


    public static void addCoins(AbstractScreen screen, int addCoins) {
        coins += addCoins;
        if (coins >= 100) {
            coins -= 100;
            lifes++;
            AssetManager manager = screen.game.assets.manager;
            if (manager.isLoaded(Assets.SOUND_ITEM_LIVE_UP_2))
                SoundManager.play(manager.get(Assets.SOUND_ITEM_LIVE_UP_2, Sound.class));
        }
    }

    public static int getCoins() {
        return coins;
    }

    public static int getItem() {
        return item;
    }

    public static void setItem(int itemType) {
        item = itemType;
    }

    public static Maryo.MaryoState getMaryoState() {
        return Maryo.MaryoState.fromInt(maryoState);
    }

    public static void setMaryoState(Maryo.MaryoState newState) {
        maryoState = Maryo.MaryoState.toInt(newState);
    }

    public static Maryo.MaryoState getPersistentMaryoState() {
        return Maryo.MaryoState.fromInt(persistentMaryoState);
    }

    public static int getPersistentItem() {
        return persistentItem;
    }

    public static int getLifes() {
        return lifes;
    }

    public static void addScore(int score) {
        levelScore += score;
    }

    public static void addLifes(int addedLifes) {
        lifes += addedLifes;
    }

    public static int getScore() {
        return levelScore;
    }

    public static int getTotalScore() {
        return totalScore;
    }

    /**
     * Starten eines Spiels aus dem Menü heraus
     */
    public static boolean startLevelFresh() {
        item = persistentItem;
        maryoState = persistentMaryoState;
        persistentMaryoState = 0;
        persistentItem = 0;
        levelPlaytime = 0;
        lifes--;
        levelScore = 0;
        levelStartedNum++;
        return lifes >= 0;
    }

    /**
     * Level erfolgreich beendet => Punkte etc übernehmen
     */
    public static void levelCleared(String levelName) {
        persistentItem = item;
        persistentMaryoState = maryoState;
        lifes++;

        Level level = Level.getLevel(levelName);
        level.currentScore = levelScore;
        if (level.currentScore > level.bestScore)
            level.bestScore = levelScore;

        recalcTotalScore();
    }

    private static void recalcTotalScore() {
        totalScore = 0;

        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            totalScore += level.currentScore;
        }

        if (totalScore > bestTotal)
            bestTotal = totalScore;
    }

}
