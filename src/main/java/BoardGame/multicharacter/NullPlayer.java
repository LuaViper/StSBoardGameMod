package BoardGame.multicharacter;

import BoardGame.characters.AbstractBGPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbRed;

import java.util.ArrayList;

//TODO: is this actually used?  we don't have to push null into the player context queue, unlike monster target
public class NullPlayer extends AbstractBGPlayer {

    public NullPlayer() {
        super("NULL", PlayerClass.IRONCLAD, new EnergyOrbRed(), "null", "null");
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        return null;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        return null;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return null;
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return null;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return null;
    }

    @Override
    public Color getCardRenderColor() {
        return null;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return null;
    }

    @Override
    public Color getCardTrailColor() {
        return null;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 0;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return null;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {

    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return null;
    }

    @Override
    public String getLocalizedCharacterName() {
        return null;
    }

    @Override
    public AbstractPlayer newInstance() {
        return null;
    }

    @Override
    public String getSpireHeartText() {
        return null;
    }

    @Override
    public Color getSlashAttackColor() {
        return null;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[0];
    }

    @Override
    public String getVampireText() {
        return null;
    }
}
