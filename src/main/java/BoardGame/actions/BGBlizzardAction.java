
package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.orbs.BGFrost;
import BoardGame.powers.WeakVulnCancel;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.BlizzardEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGBlizzardAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());
    private int[] multiDamage;


    private AbstractPlayer p;


    public BGBlizzardAction(AbstractPlayer p, int[] multiDamage) {
        this.multiDamage = multiDamage;
        this.damageType= DamageInfo.DamageType.NORMAL;
        this.p = p;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.DAMAGE;
    }


    public void update() {
        int effect = 0;
        for(AbstractOrb o : AbstractDungeon.player.orbs) {
            //TODO: do we want to include, or exclude, vanilla orbs?
            if(o instanceof BGFrost || o instanceof Frost){
                effect+=1;
            }
        }

        if (effect > 0) {
            for (int i = effect - 1; i >= 0; i--) {
                //damage needs to be addToTop instead of addToBot, otherwise weakvuln checks will fail
                //as a consequence, actions are added in reverse order from the original card
                addToTop((AbstractGameAction) new DamageAllEnemiesAction( p,
                        this.multiDamage, this.damageType, AttackEffect.BLUNT_HEAVY, true));
                if (Settings.FAST_MODE) {
                    addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new BlizzardEffect(effect,
                            AbstractDungeon.getMonsters().shouldFlipVfx()), 0.25F));
                } else {
                    addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new BlizzardEffect(effect, AbstractDungeon.getMonsters().shouldFlipVfx()), 1.0F));
                }
            }
        }else{
            addToTop((AbstractGameAction)new DamageAllEnemiesAction(p,
                    0, WeakVulnCancel.WEAKVULN_ZEROHITS, AttackEffect.NONE));
        }
        this.isDone = true;
    }
}


