//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package CoopBoardGame.actions;

import CoopBoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGApplyBulletTimeAction extends AbstractGameAction {

    public BGApplyBulletTimeAction() {
        this.duration = Settings.ACTION_DUR_XFAST;
    }

    public void update() {
        for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
            card.setCostForTurn(0);
            if (card instanceof AbstractBGCard) {
                ((AbstractBGCard) card).temporarilyCostsZero = true;
            }
        }

        this.isDone = true;
    }
}
