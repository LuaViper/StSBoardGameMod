package BoardGame.powers;

import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

//TODO: use Smite effects instead of Combust effects

public class BGBattleHymnPower extends AbstractBGPower {
    public static final String POWER_ID = "BGBattleHymn";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGBattleHymnPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int hpLoss;

    public BGBattleHymnPower(AbstractCreature owner, int damageAmount) {
        super();
        this.name = NAME;
        this.ID = "BGBattleHymnPower";
        this.owner = owner;
        this.amount = damageAmount;
        this.clickable=true;
        this.autoActivate=true;
        updateDescription();
        loadRegion("hymn");
    }

    public void onRightClick(){
        logger.info("BGBattleHymnPower.onRightClick");
        if(!onCooldown) {
            onCooldown=true;
            if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                flash();
                int damage=amount;
                if (AbstractDungeon.player.stance.ID.equals("BGWrath")) {
                    damage+=amount;
                }
                int finalDamage = damage;
                TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                    if(target==null)return;
                    addToTop((AbstractGameAction) new DamageAction((AbstractCreature) target, new DamageInfo((AbstractCreature) AbstractDungeon.player, finalDamage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                };
                addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for Battle Hymn."));
            }
        }
    }





    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        //this.hpLoss++;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[3] + getRightClickDescriptionText();

    }
}


