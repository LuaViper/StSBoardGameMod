//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.Iterator;

public class BGApplyBulletTimeAction extends AbstractGameAction {
    public BGApplyBulletTimeAction() {
        this.duration = Settings.ACTION_DUR_XFAST;
    }

    public void update() {
        Iterator var1 = AbstractDungeon.player.hand.group.iterator();

        while(var1.hasNext()) {
            AbstractCard c = (AbstractCard)var1.next();
            c.setCostForTurn(-9);
            if(c instanceof AbstractBGCard){
                ((AbstractBGCard)c).temporarilyCostsZero =true;
            }
        }

        this.isDone = true;
    }
}
