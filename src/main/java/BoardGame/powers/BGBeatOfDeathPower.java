package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//TODO: BGBeatOfDeathPower currently applies before BGPoison; both are "end of turn" so poison can go first
public class BGBeatOfDeathPower extends AbstractBGPower {
    public static final String POWER_ID = "BGBeatOfDeathPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGBeatOfDeathPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGBeatOfDeathPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGBeatOfDeathPower";
        this.owner = owner;
        this.amount = amount;
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
        loadRegion("beat");
        this.type = AbstractPower.PowerType.BUFF;
    }

    public void stackPower(int stackAmount) {
        if (this.amount == -1) {
            //logger.info(this.name + " does not stack");
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        int maxStacks=(AbstractDungeon.ascensionLevel<11) ? 100 : 120;
        if(this.amount>maxStacks)this.amount=maxStacks; //TODO: max stacks is 5 on hardmode
    }

    public void atStartOfTurn(){
        //start of heart's turn, not player's turn
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}


