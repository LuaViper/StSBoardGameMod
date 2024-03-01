package BoardGame.powers;

import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGElectroPower extends AbstractBGPower {
    public static final String POWER_ID = "BGElectroPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGElectroPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGElectroPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGElectroPower";
        this.owner = owner;
        updateDescription();
        loadRegion("mastery");
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}



