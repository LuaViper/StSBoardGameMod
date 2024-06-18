//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGFiendFireAction extends AbstractGameAction {
    private DamageInfo info;
    private float startingDuration;

    public BGFiendFireAction(AbstractCreature target, DamageInfo info) {
        this.info = info;
        this.setValues(target, info);
        this.actionType = ActionType.WAIT;
        this.attackEffect = AttackEffect.FIRE;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startingDuration;
    }

    public void update() {
        int count = AbstractDungeon.player.hand.size();

        int i;
        for(i = 0; i < count; ++i) {
            this.addToTop(new DamageAction(this.target, this.info, AttackEffect.FIRE));
        }

        for(i = 0; i < count; ++i) {
            if (Settings.FAST_MODE) {
                //reminder -- all getNCardFromTops are being called now before any exhaust actions kick in, so they will still refer to the correct cards
                //TODO: randomize index order in which cards are exhausted to match VG animation
                // (or maybe don't, because the addtotop draw-card process messes with the animation anyway)
                this.addToTop(new ExhaustSpecificCardAction(AbstractDungeon.player.hand.getNCardFromTop(i),AbstractDungeon.player.hand,true));
            } else {
                this.addToTop(new ExhaustSpecificCardAction(AbstractDungeon.player.hand.getNCardFromTop(i),AbstractDungeon.player.hand));
            }
        }

        this.isDone = true;
    }
}
