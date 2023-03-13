package BoardGame.powers;


import BoardGame.actions.DamageSpecificEnemyOrRandomIfDeadAction;
import BoardGame.monsters.bgbeyond.BGTransient;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class BGJuggernautPower extends AbstractPower {
    public static final String POWER_ID = "BGJuggernaut";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Juggernaut");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private AbstractCreature target;

    public int transientSkipCounter=10;
    public boolean transientFadedOut=false;

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

            if(this.target instanceof BGTransient){
                transientSkipCounter--;
                if(transientSkipCounter==0){
                    AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, DESCRIPTIONS[2], true));
                }else if(transientSkipCounter==-5){
                    CardCrawlGame.startOver=false;
                    CardCrawlGame.fadeToBlack(2.0F);
                    transientFadedOut=true;
                }else if(transientSkipCounter==-10){
                    if(this.target.currentHealth>10*this.amount){
                        int damage=this.target.currentHealth-10*this.amount;
                        addToTop((AbstractGameAction) new DamageSpecificEnemyOrRandomIfDeadAction(this.target, new DamageInfo(this.owner, damage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                        //this.target.currentHealth=10*this.amount;
                    }
                }else if(transientSkipCounter==-12){
                    CardCrawlGame.fadeIn(2.0F);
                    transientFadedOut=false;
                }else if(transientSkipCounter==-15){
                    AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 2.0F, DESCRIPTIONS[3], true));
                }
            }
        }
    }

    public void onVictory(){
        if(transientFadedOut){
            CardCrawlGame.fadeIn(2.0F);
            transientFadedOut=true;
        }
    }





    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}


