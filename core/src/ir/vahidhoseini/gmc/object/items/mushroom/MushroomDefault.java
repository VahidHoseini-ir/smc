package ir.vahidhoseini.gmc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ir.vahidhoseini.gmc.object.World;
import ir.vahidhoseini.gmc.object.maryo.Maryo;
import ir.vahidhoseini.gmc.utility.GameSave;


public class MushroomDefault extends Mushroom
{

    public static final String TEXTURE_NAME = "game_items_mushroom_red";

    public MushroomDefault(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = TEXTURE_NAME;
        mPickPoints = 500;
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_DEFAULT;
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        world.maryo.upgrade(Maryo.MaryoState.big, this, false);
        world.trashObjects.add(this);
        GameSave.addScore(mPickPoints);
    }
}
