package ir.vahidhoseini.gmc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ir.vahidhoseini.gmc.object.World;

public class MushroomPoison extends Mushroom
{
    public MushroomPoison(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = "game_items_mushroom_poison";
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_POISON;
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        if (!world.maryo.mInvincibleStar)
            world.maryo.downgradeOrDie(false);
        world.trashObjects.add(this);
    }
}
