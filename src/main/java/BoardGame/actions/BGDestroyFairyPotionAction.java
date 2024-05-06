package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class BGDestroyFairyPotionAction extends AbstractGameAction {
    @Override
    public void update() {
        for (AbstractPotion p : AbstractDungeon.player.potions) {
            if (p.ID.equals("BGFairyPotion")) {
                AbstractDungeon.player.currentHealth = 0;
                p.use(AbstractDungeon.player);
                AbstractDungeon.topPanel.destroyPotion(p.slot);
            }
        }

        this.isDone = true;
    }
}
