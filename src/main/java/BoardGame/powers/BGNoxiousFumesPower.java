package BoardGame.powers;

import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGNoxiousFumesPower extends AbstractBGPower {
    public static final String POWER_ID = "BGNoxiousFumes";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGNoxiousFumes");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGNoxiousFumesPower(AbstractCreature owner, int poisonAmount) {
        this.name = NAME;
        this.ID = "BGNoxiousFumes";
        this.owner = owner;
        this.amount = poisonAmount;
        updateDescription();
        loadRegion("fumes");
    }

    public void atStartOfTurnPostDraw() {
        //TODO: player should be able to delay poison application until other relics activate
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flash();
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                if(target==null)return;
                addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)target, this.owner, new BGPoisonPower((AbstractCreature)target, this.owner, this.amount), this.amount));
            };
            //logger.info("DoubleTap addToTop");
            addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for Noxious Fumes."));
        }
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}


