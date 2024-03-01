
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGReinforcedBodyEnergyCheckAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());
    AbstractPlayer p;
    AbstractCard card;
    int minEnergy;
    int energyOnUse;
    boolean freeToPlayOnce;
    boolean upgraded;
    private int blockBonus;
    private DamageInfo.DamageType damageTypeForTurn;


    public BGReinforcedBodyEnergyCheckAction(AbstractPlayer p, AbstractCard card, int minEnergy, int energyOnUse, boolean freeToPlayOnce, boolean upgraded, int blockBonus) {
        this.p = p;
        this.card=card;
        this.minEnergy=minEnergy;
        this.freeToPlayOnce = freeToPlayOnce;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
        this.upgraded=upgraded;
        this.blockBonus=blockBonus;
    }


    public void update() {
        if(energyOnUse>0 || card.upgraded) {
            addToTop((AbstractGameAction) new BGXCostCardAction(card, minEnergy, this.energyOnUse,
                    (e) -> addToTop((AbstractGameAction) new BGReinforcedBodyAction(AbstractDungeon.player, this.freeToPlayOnce, e, this.upgraded, blockBonus))));
        }else{
            //TODO: localization
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, "I can't play that card for 0 Energy.", true));
        }

        this.isDone = true;
    }
}


