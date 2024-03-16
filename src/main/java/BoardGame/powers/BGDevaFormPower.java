package BoardGame.powers;

import BoardGame.actions.BGGainMiracleAction;
import basemod.ReflectionHacks;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGDevaFormPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGDevaFormPower");
    public static final String POWER_ID = "BGDevaFormPower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGDevaFormPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGDevaFormPower";
        this.owner = owner;
        this.amount = amount;

        updateDescription();
        loadRegion("deva2");
    }


    public void atStartOfTurn() {
        flash();
        addToBot(new BGGainMiracleAction(amount));


    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }


}


