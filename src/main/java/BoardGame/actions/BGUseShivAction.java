package BoardGame.actions;

import BoardGame.cards.BGColorless.BGShivSurrogate;
import BoardGame.characters.AbstractBGPlayer;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGUseShivAction extends AbstractGameAction {
    private boolean isARealShiv;        //false if we play Cunning Potion or Ninja Scroll
    private int bonusDamage;
    private int baseDamage;
    private int damage;
    private boolean canDiscard;     //currently unused.
    private AbstractPlayer player;
    private String message;

    public BGUseShivAction(boolean isARealShiv, boolean canDiscard, int bonusDamage, String message) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.isARealShiv=isARealShiv;
        this.canDiscard=canDiscard;
        this.bonusDamage=bonusDamage;
        this.message=message;
    }

    public void update() {
        AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        int reliccounter=0;

        if(relic!=null) reliccounter=relic.counter;

        if(reliccounter>0 || !isARealShiv) {
            int finalReliccounter = reliccounter; //because lambdas
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                //double-check that we actually have a Shiv to spend (but Cunning Potion doesn't count)
                if (finalReliccounter > 0 || !isARealShiv) {
                    if (AbstractDungeon.player instanceof AbstractBGPlayer)
                        ((AbstractBGPlayer) AbstractDungeon.player).shivsPlayedThisTurn += 1;
                    if (isARealShiv && relic != null) {
                        relic.counter = relic.counter - 1;  //don't decrement Shivs if we throw a Cunning Potion!
                    }
                    BGShivSurrogate fakeShiv = new BGShivSurrogate();
                    fakeShiv.isARealShiv = isARealShiv;
                    if (isARealShiv) {
                        //similarly don't apply Accuracy to Cunning Potions
                        AbstractPower accuracy = AbstractDungeon.player.getPower("BGAccuracy");
                        if (accuracy != null) {
                            fakeShiv.baseDamage += accuracy.amount;
                        }
                    }
                    fakeShiv.baseDamage += bonusDamage;
                    UseCardAction fakeShivAction = new UseCardAction(fakeShiv, target);
                    fakeShiv.calculateCardDamage(target);
                    addToTop((AbstractGameAction) new CheckAfterUseCardAction(fakeShiv, fakeShivAction));
                    addToTop((AbstractGameAction) new DamageAction((AbstractCreature) target, new DamageInfo((AbstractCreature) AbstractDungeon.player, fakeShiv.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                    //reminder: order of operations is now DamageAction -> (proc weak/vuln) -> CheckAfterUseCardAction
                }
            };
            addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, message));


        }



        this.isDone = true;


    }


}


