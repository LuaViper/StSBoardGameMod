//TODO: it should be possible to use the Amount field to disable Painful Stabs, rather than removing it and adding it again (which is visually noisy)

package BoardGame.powers;

import BoardGame.cards.BGStatus.BGDazed;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGPainfulStabsPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGPainful Stabs");
    public static final String POWER_ID = "BGPainful Stabs";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGPainfulStabsPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGPainful Stabs";
        this.owner = owner;
        this.amount = -1;
        updateDescription();
        loadRegion("painfulStabs");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type != DamageInfo.DamageType.THORNS)
            addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
    }
}


