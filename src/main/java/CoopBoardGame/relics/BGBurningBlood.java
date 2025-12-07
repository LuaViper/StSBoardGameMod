package CoopBoardGame.relics;

import CoopBoardGame.CoopBoardGame;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGBurningBlood extends AbstractBGRelic {

    // ID, images, text.
    public static final String ID = CoopBoardGame.makeID("BurningBlood");

    public BGBurningBlood() {
        super(ID, "burningBlood.png", RelicTier.STARTER, LandingSound.MAGICAL);
    }

    private static final int HEALTH_AMT = 1;

    public AbstractRelic makeCopy() {
        return new BGBurningBlood();
    }

    public void onEquip() {
        BaseMod.MAX_HAND_SIZE = 999;
    }

    @Override
    public void onVictory() {
        flash();
        addToTop(
            (AbstractGameAction) new RelicAboveCreatureAction(
                (AbstractCreature) AbstractDungeon.player,
                this
            )
        );
        AbstractPlayer p = AbstractDungeon.player;
        if (p.currentHealth > 0) {
            p.heal(HEALTH_AMT);
        }
    }

    // Description
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
