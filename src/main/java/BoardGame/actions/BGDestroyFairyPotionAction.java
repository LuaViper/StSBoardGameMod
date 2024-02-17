package BoardGame.actions;

import BoardGame.monsters.bgexordium.BGGremlinAngry;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AngryPower;

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
