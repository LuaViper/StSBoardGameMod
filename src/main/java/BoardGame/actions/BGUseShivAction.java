package BoardGame.actions;
import BoardGame.BoardGame;
import BoardGame.cards.AbstractBGCard;
import BoardGame.monsters.MixedAttacks;
import BoardGame.powers.BGPoisonPower;
import BoardGame.relics.BGShivs;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import BoardGame.cards.BGColorless.BGFakeShiv;

public class BGUseShivAction extends AbstractGameAction {
    private boolean isARealShiv;        //false if we play Cunning Potion
    private int bonusDamage;
    private int baseDamage;
    private int damage;
    private boolean canDiscard;
    private AbstractPlayer player;

    public BGUseShivAction(boolean isARealShiv, boolean canDiscard, int bonusDamage) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.isARealShiv=isARealShiv;
        this.canDiscard=canDiscard;
        this.bonusDamage=bonusDamage;
    }

    public void update() {
        AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        if(relic!=null) {
            if(relic.counter>0) {
                TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                    //double-check that we actually have a Shiv to spend (but Cunning Potion doesn't count)
                    if(relic.counter>0 || !isARealShiv) {
                        ((BGShivs)relic).shivsPlayedThisTurn+=1;
                        if(isARealShiv) relic.counter = relic.counter - 1;  //don't decrement Shivs if we throw a Cunning Potion!
                        BGFakeShiv fakeShiv=new BGFakeShiv();
                        UseCardAction fakeShivAction=new UseCardAction(fakeShiv,target);

                        fakeShiv.calculateCardDamage(target);
                        addToTop((AbstractGameAction) new CheckAfterUseCardAction(fakeShiv,fakeShivAction));
                        addToTop((AbstractGameAction) new DamageAction((AbstractCreature) target, new DamageInfo((AbstractCreature) AbstractDungeon.player, fakeShiv.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                        //reminder: order of operations is now DamageAction -> (proc weak/vuln) -> CheckAfterUseCardAction

                    }
                };
                //logger.info("DoubleTap addToTop");
                String message = "Choose a target for Shiv.";
                if (relic.counter > 5) {
                    //TODO: also check global token cap (5*number_of_Silents) (only relevant if prismatic shard)
                    message = "Too many Shivs! Use one now or discard it.";
                }
                addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, message));
            }
        }




        this.isDone = true;


    }


}


