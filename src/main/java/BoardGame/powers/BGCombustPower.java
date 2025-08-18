package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGCombustPower extends AbstractBGPower {
    public static final String POWER_ID = "BGCombust";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Combust");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int hpLoss;

    public BGCombustPower(AbstractCreature owner, int hpLoss, int damageAmount) {
        super();
        this.name = NAME;
        this.ID = "BGCombust";
        this.owner = owner;
        this.amount = damageAmount;
        this.hpLoss = hpLoss;
        this.clickable=true;
        this.autoActivate=true;
        updateDescription();
        loadRegion("combust");
    }

    public void onRightClick(){
        logger.info("BGCombustPower.onRightClick");
        if(!onCooldown) {
            onCooldown=true;
            if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                flash();
                //addToBot((AbstractGameAction)new LoseHPAction(this.owner, this.owner, this.hpLoss, AbstractGameAction.AttackEffect.FIRE));
                addToBot((AbstractGameAction) new DamageAllEnemiesAction(AbstractDungeon.player,
                        DamageInfo.createDamageMatrix(this.amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));
            }
        }
    }





    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        //this.hpLoss++;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2] + getRightClickDescriptionText();

    }
}


