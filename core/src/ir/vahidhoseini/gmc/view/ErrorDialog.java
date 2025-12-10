package ir.vahidhoseini.gmc.view;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ir.vahidhoseini.gmc.assets.Assets;
import ir.vahidhoseini.gmc.assets.FontAwesome;

/**
 * Created by  vahid hoseini on 25.11.2017.
 */

public class ErrorDialog extends ScrollDialog {
    public ErrorDialog(String msg, Skin skin, float percentWidth, float percentHeight) {
        super(skin, percentWidth, percentHeight);

        Label textLabel = new Label(msg, skin, Assets.LABEL_SIMPLE25);
        textLabel.setWrap(true);

        setScrollActor(textLabel);
        button(new ColorableTextButton(FontAwesome.CIRCLE_CHECK, skin, Assets.BUTTON_FA_FRAMELESS));
    }
}
