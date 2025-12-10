package ir.vahidhoseini.gmc.object.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ir.vahidhoseini.gmc.assets.Assets;
import ir.vahidhoseini.gmc.object.GameObject;
import ir.vahidhoseini.gmc.object.World;
import ir.vahidhoseini.gmc.utility.Constants;
import ir.vahidhoseini.gmc.utility.GameSave;
import ir.vahidhoseini.gmc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Static extends Enemy
{
    public static final float POS_Z = 0.094f;
    private TextureRegion texture;

    public Static(World world, Vector2 size, Vector3 position, float speed, int fireResistance, float iceResistance)
    {
        super(world, size, position);
        mSpeed = speed;
        mCanBeHitFromShell = 0;
        mFireResistant = fireResistance;
        mIceResistance = iceResistance;
        position.z = POS_Z;
        ppEnabled = false;
    }

    @Override
    public void initAssets() {
        texture = world.screen.game.assets.manager.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class)
                .findRegion(textureName);
    }

    @Override
    public void dispose()
    {
        texture = null;
    }

    @Override
    public boolean isBullet()
    {
        return true;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if (texture != null)
        {
            float width = Utility.getWidth(texture, mDrawRect.height);
            float originX = width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(texture, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height,
                    1, 1, -mRotationZ);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop()
    {
        return false;
    }

    private float getRotation()
    {
        float circumference = (float) Math.PI * (mColRect.width);
        float deltaVelocity = mSpeed * Gdx.graphics.getDeltaTime();

        float step = circumference / deltaVelocity;

        float frameRotation = 360 / step;//degrees
        mRotationZ += frameRotation;
        if (mRotationZ > 360) mRotationZ = mRotationZ - 360;

        return mRotationZ;
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        mRotationZ = getRotation();
		if(deadByBullet)
        {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(deltaTime);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(deltaTime, false, false);
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return texture;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
    {
        if(object instanceof Enemy && ((Enemy)object).handleCollision)
        {
            GameSave.addScore(((Enemy)object).mKillPoints);
            ((Enemy)object).downgradeOrDie(this, true, false);
        }
        return true;
    }
}
