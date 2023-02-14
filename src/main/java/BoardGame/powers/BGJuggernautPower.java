package BoardGame.powers;


import BoardGame.actions.DamageSpecificEnemyOrRandomIfDeadAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGJuggernautPower extends AbstractPower {
    public static final String POWER_ID = "BGJuggernaut";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Juggernaut");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private AbstractCreature target;

    public BGJuggernautPower(AbstractCreature owner, int newAmount, AbstractCreature target) {
        this.name = NAME;
        this.ID = "BGJuggernaut";
        this.owner = owner;
        this.amount = newAmount;
        this.target=target;
        updateDescription();
        loadRegion("juggernaut");
    }


    public void onGainedBlock(float blockAmount) {
        if (blockAmount > 0.0F) {
            flash();
//            if(target==null || target.halfDead || target.isDead || target.isDying || target.isEscaping) {
//                //this.target = (AbstractCreature) AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
//                AbstractCreature randommonster=(AbstractCreature)AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
//                addToBot((AbstractGameAction) new DamageAction(randommonster, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
//
//
//            }else {
//                addToBot((AbstractGameAction) new DamageAction(this.target, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
//            }
                //TODO: ...but to be completely accurate, we need to ask the player for a target every time
            addToBot((AbstractGameAction) new DamageSpecificEnemyOrRandomIfDeadAction(this.target, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

        }
    }





    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}


