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

public class BGJuggernautPower extends AbstractBGPower {
    public static final String POWER_ID = "BGJuggernaut";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Juggernaut");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public int transientSkipCounter=10;
    public boolean transientFadedOut=false;

    public BGJuggernautPower(AbstractCreature owner, int newAmount, AbstractCreature target) {
        this.name = NAME;
        this.ID = "BGJuggernaut";
        this.owner = owner;
        this.amount = newAmount;
        updateDescription();
        loadRegion("juggernaut");
    }


    public void onGainedBlock(float blockAmount) {
        if(AbstractDungeon.player.currentBlock>=20)
            return;

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

            TargetSelectScreen.TargetSelectAction action=(target)->{
                if(target==null)return;
                addToTop((AbstractGameAction) new DamageAction((AbstractCreature) target, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
//                if(target instanceof BGTransient){
//                    transientSkipCounter--;
//                    if(transientSkipCounter==0){
//                        AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, DESCRIPTIONS[2], true));
//                    }else if(transientSkipCounter==-5){
//                        CardCrawlGame.startOver=false;
//                        CardCrawlGame.fadeToBlack(2.0F);
//                        transientFadedOut=true;
//                    }else if(transientSkipCounter==-10){
//                        if(target.currentHealth>10*this.amount){
//                            int damage=target.currentHealth-10*this.amount;
//                            addToTop((AbstractGameAction) new DamageSpecificEnemyOrRandomIfDeadAction(target, new DamageInfo(this.owner, damage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
//                            //this.target.currentHealth=10*this.amount;
//                        }
//                    }else if(transientSkipCounter==-12){
//                        CardCrawlGame.fadeIn(2.0F);
//                        transientFadedOut=false;
//                    }else if(transientSkipCounter==-15){
//                        AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 2.0F, DESCRIPTIONS[3], true));
//                    }
//                }
            };

            addToTop((AbstractGameAction)new TargetSelectScreenAction(action,"You gained Block.  Choose a target for Juggernaut ("+Integer.toString(this.amount)+" damage)."));




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


