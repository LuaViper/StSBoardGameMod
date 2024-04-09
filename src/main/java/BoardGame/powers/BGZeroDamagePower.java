package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

//TODO NEXT: softlock if End Turn button is clicked after die rolls a 6 but before activating relics or playing a card
//TODO NEXT: SurroundedPower still activates twice if die rolls a 6 (eg with gamblers brew)
//TODO NEXT: spear and shield are in separate rows, so all AOE attacks except Bomb will miss the other one!
//TODO: also yeah they do stuff on their turn
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


