
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.orbs.BGFrost;
import BoardGame.orbs.BGLightning;
import BoardGame.powers.WeakVulnCancel;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.orbs.Lightning;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.BlizzardEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGThunderStrikeAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());

    private int[] multiDamage;


    private AbstractPlayer p;


    public BGThunderStrikeAction(AbstractPlayer p, int[] multiDamage) {
        this.multiDamage = multiDamage;
        this.p = p;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = AbstractGameAction.AttackEffect.NONE;
    }


    public void update() {
        int effect = 0;
        for(AbstractOrb o : AbstractDungeon.player.orbs) {
            //TODO: do we want to include, or exclude, vanilla orbs?
            if(o instanceof BGLightning || o instanceof Lightning){
                effect+=1;
            }
        }

        if (effect > 0) {
            for (int i = effect - 1; i >= 0; i--) {
                //TODO LATER: this burns RNGs, decide if we want to keep it that way
                //TODO LATER: dead enemies can be targeted by visual effects, do we want to do change that? vanilla solves this by calling ThunderStrikeAction recursively (effort)
                AbstractCreature target = (AbstractCreature)AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);

                //damage needs to be addToTop instead of addToBot, otherwise weakvuln checks will fail
                //as a consequence, actions are added in reverse order from the original card
                if (Settings.FAST_MODE) {
                    addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new LightningEffect(target.drawX, target.drawY),.25F));
                } else {
                    addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new LightningEffect(target.drawX, target.drawY),1.0F));
                }
                addToTop((AbstractGameAction) new DamageAllEnemiesAction((AbstractCreature) this.p, this.multiDamage, this.damageType, AttackEffect.BLUNT_HEAVY, true));
                addToTop((AbstractGameAction)new SFXAction("ORB_LIGHTNING_EVOKE",0.1F));
                addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new FlashAtkImgEffect(target.hb.cX, target.hb.cY, this.attackEffect)));

            }
        }else{
            addToTop((AbstractGameAction)new DamageAllEnemiesAction(this.p,0, WeakVulnCancel.WEAKVULN_ZEROHITS, AttackEffect.NONE));
        }
        this.isDone = true;
    }
}
