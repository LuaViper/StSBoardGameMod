
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.orbs.BGFrost;
import BoardGame.powers.WeakVulnCancel;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.BlizzardEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGBarrageAction
        extends AbstractGameAction {

    private DamageInfo info = null;

    private AbstractCreature target;
    private int extrahits;
    public BGBarrageAction(AbstractCreature m, DamageInfo info, int extrahits) {
        this.info = info;
        this.target = m;
        this.extrahits=extrahits;
    }

    public void update() {
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); i++) {
            if (!(AbstractDungeon.player.orbs.get(i) instanceof com.megacrit.cardcrawl.orbs.EmptyOrbSlot))
                addToTop((AbstractGameAction)new DamageAction(this.target, this.info, AbstractGameAction.AttackEffect.BLUNT_LIGHT, true));
        }
        for(int i=0;i<extrahits;i+=1){
            addToTop((AbstractGameAction)new DamageAction(this.target, this.info, AbstractGameAction.AttackEffect.BLUNT_LIGHT, true));
        }
        this.isDone = true;
    }
}


