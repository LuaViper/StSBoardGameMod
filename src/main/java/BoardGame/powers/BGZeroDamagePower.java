package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

//TODO: if End Turn button is clicked while die abilities are pending, No Draw will be applied after player's turn ends and will persist through next turn
//TODO: also, damage potions can be used before this is applied
//TODO: End Turn can also be used to sneak in The Bomb damage

public class BGZeroDamagePower extends AbstractBGPower {
    public static final String POWER_ID = BoardGame.makeID("BGZeroDamagePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = false;
    private static final int EFFECTIVENESS_STRING = 1;

    public BGZeroDamagePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        updateDescription();
        loadRegion("noattack");

        this.type = PowerType.DEBUFF;
        this.amount=-2;
        this.isTurnBased = true;

    }



    public void updateDescription() {
            this.description = DESCRIPTIONS[0];
    }



    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        //TODO: if info.source==null, this won't get called (game doesn't know whose power list to check)
        // so somewhere in AbstractCreature.damage and/or its overrides, complain loudly if info.source==null
        if(true){
            if (info.type != DamageInfo.DamageType.HP_LOSS) {
                return 0;
            }
        }
        return damageAmount;
    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
        if(true){
            if (type != DamageInfo.DamageType.HP_LOSS) {
                return 0;
            }
        }
        return damage;
    }


    @Override
    public void atEndOfRound() {
        addToBot(new RemoveSpecificPowerAction(owner,owner,this));
    }

}


