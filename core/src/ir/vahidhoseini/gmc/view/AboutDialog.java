package ir.vahidhoseini.gmc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import ir.vahidhoseini.gmc.MaryoGame;
import ir.vahidhoseini.gmc.assets.Assets;
import ir.vahidhoseini.gmc.assets.FontAwesome;

/**
 * Created by  vahid hoseini on 24.10.2017.
 */

public class AboutDialog extends ScrollDialog {
    public final Label.LabelStyle simpleLabel = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    private final ColorableTextButton scrollDownButton;
    private final ColorableTextButton scrollUpButton;
    private Stage stage;
    private float sinceLastScroll;


    public AboutDialog(Skin skin) {
        super(skin, .8f, .5f);

        button(new ColorableTextButton(FontAwesome.CIRCLE_CHECK, skin, Assets.BUTTON_FA_FRAMELESS));

        Runnable gpl3runnable = getLicenseBoxRunnable("data/about/license_gpl3.txt");

        Table aboutTable = new Table();
        aboutTable.defaults().pad(5).align(Align.center);

        aboutTable.add(new Label("Magical Fungus Legend", skin, Assets.LABEL_BORDER60));
        aboutTable.row();
        aboutTable.add(new Label("Version " + MaryoGame.GAME_VERSION, skin, Assets.LABEL_SIMPLE25));
        aboutTable.row();
        aboutTable.add(new Label("brought to you by simplocity", skin, Assets.LABEL_SIMPLE25));
        aboutTable.row().padBottom(40);
        aboutTable.add(getButtonsTable(new String[]{"Website", "License"},
                new Runnable[]{getWebRunnable(MaryoGame.GAME_WEBURL),
                        gpl3runnable}, Assets.BUTTON_SMALL));

        aboutTable.row();
        aboutTable.add(new Label("This game is based on the following projects:", skin, Assets.LABEL_SIMPLE25));

        aboutTable.row().padTop(40);
        aboutTable.add(getCenteredSmallLabel("Graphics, levels, sounds:\nMagical Fungus Legend by Vahid Hoseini, AmirhoseinMohseni, HamidHaghdoost and special thanks to farzad stoode" +
                " and others")).fill();
        aboutTable.row();

        aboutTable.row().padTop(40);
        aboutTable.row();
        aboutTable.row().padTop(40);
        aboutTable.add(getCenteredSmallLabel("Game service connection:\ngdx-gamesvcs by vahid hoseini")).fill();
        aboutTable.row();


        scrollActor = aboutTable;

        scrollDownButton = new ColorableTextButton(FontAwesome.CIRCLE_DOWN, skin, Assets.BUTTON_FA_FRAMELESS);
        scrollUpButton = new ColorableTextButton(FontAwesome.CIRCLE_UP, skin, Assets.BUTTON_FA_FRAMELESS);
        getButtonTable().add(scrollDownButton);
        buttonsToAdd.add(scrollDownButton);
        getButtonTable().add(scrollUpButton);
        buttonsToAdd.add(scrollUpButton);
    }

    private Runnable getLicenseBoxRunnable(final String file) {
        return new Runnable() {
            @Override
            public void run() {
                String license = Gdx.files.internal(file).readString();
                Label textLabel = new Label(license, simpleLabel);
                textLabel.setWrap(true);

                ScrollDialog licenseBox = new ScrollDialog(skin, .5f, .75f);
                licenseBox.setScrollActor(textLabel);
                licenseBox.button(new ColorableTextButton(FontAwesome.CIRCLE_CHECK, skin, Assets.BUTTON_FA_FRAMELESS));
                licenseBox.show(stage);
            }
        };
    }

    private Runnable getWebRunnable(final String url) {
        return new Runnable() {
            @Override
            public void run() {
                Gdx.net.openURI(url);
            }
        };
    }

    private Table getButtonsTable(String[] label, Runnable[] run) {
        return getButtonsTable(label, run, Assets.BUTTON_SMALL_FRAMELESS);
    }

    private Table getButtonsTable(String[] label, final Runnable[] run, String styleName) {
        Table storebuttons = new Table();
        for (int i = 0; i < label.length; i++) {
            TextButton actor = new ColorableTextButton(label[i], skin, styleName);
            final Runnable runnable = run[i];
            if (runnable != null)
                actor.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        runnable.run();
                    }

                });
            storebuttons.add(actor).uniform().fill().pad(5);
            buttonsToAdd.add(actor);
        }
        return storebuttons;
    }

    private Label getCenteredSmallLabel(String text) {
        Label smLabel = new Label(text, skin, Assets.LABEL_SIMPLE25);
        smLabel.setWrap(true);
        smLabel.setAlignment(Align.center);
        return smLabel;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        sinceLastScroll -= delta;
        sinceLastScroll = Math.max(-1, sinceLastScroll);
        int forceScroll = (scrollDownButton.isPressed() ? 1 : scrollUpButton.isPressed() ? -1 : 0);

        if (forceScroll != 0 && sinceLastScroll <= 0) {
            getStage().setScrollFocus(scrollPane);
            getStage().scrolled(forceScroll);
            sinceLastScroll = .1f;
        }
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        this.stage = stage;
        return super.show(stage, action);
    }
}
