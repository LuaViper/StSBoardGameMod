package BoardGame.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGBlackBlood extends AbstractBGRelic  {
    public BGBlackBlood() {
        super("BGBlack Blood", "blackBlood.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + 2 + this.DESCRIPTIONS[1];
    }
    public static final String ID = "BGBlack Blood";



    public void onVictory() {
        flash();
        AbstractPlayer p = AbstractDungeon.player;
        addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)p, this));
        if (p.currentHealth > 0) {
            p.heal(2);
        }
    }





    public AbstractRelic makeCopy() {
        return new BGBlackBlood();
    }
}


