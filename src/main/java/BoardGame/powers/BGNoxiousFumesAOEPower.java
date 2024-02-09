package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGNoxiousFumesAOEPower extends AbstractBGPower {
    public static final String POWER_ID = "BGNoxiousFumesAOE";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGNoxiousFumes");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGNoxiousFumesAOEPower(AbstractCreature owner, int poisonAmount) {
        this.name = NAME;
        this.ID = "BGNoxiousFumesAOE";
        this.owner = owner;
        this.amount = poisonAmount;
        updateDescription();
        loadRegion("fumes");
    }

    public void atStartOfTurnPostDraw() {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flash();
            //TODO: "to any row"
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (!m.isDead && !m.isDying)
                    addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, this.owner, new BGPoisonPower((AbstractCreature)m, this.owner, this.amount), this.amount));
            }
        }
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
    }
}


