package ir.vahidhoseini.gmc.object.items.mushroom;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ir.vahidhoseini.gmc.assets.Assets;
import ir.vahidhoseini.gmc.audio.SoundManager;
import ir.vahidhoseini.gmc.object.World;
import ir.vahidhoseini.gmc.utility.GameSave;


public class MushroomLive1 extends Mushroom
{

    public MushroomLive1(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = "game_items_mushroom_green";
        mPickPoints = 1000;
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_LIVE_1;
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        GameSave.addLifes(1);
        Sound sound = world.screen.game.assets.manager.get(Assets.SOUND_ITEM_LIVE_UP);
        SoundManager.play(sound);
        world.trashObjects.add(this);
        GameSave.addScore(mPickPoints);
    }
}
