package BoardGame.actions;


import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ClawEffect;

public class BGScrapeAction extends AbstractGameAction {


    public BGScrapeAction() {
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 0);
        this.duration = Settings.ACTION_DUR_FAST;

    }

    public void update() {
        if(!AbstractDungeon.player.discardPile.group.isEmpty()) {
            AbstractCard c = AbstractDungeon.player.discardPile.group.get(AbstractDungeon.player.discardPile.group.size() - 1);
            if (c.cost == 0) {
                addToBot((AbstractGameAction)new DiscardToHandAction(c));
            }
        }

        this.isDone = true;
    }
}
