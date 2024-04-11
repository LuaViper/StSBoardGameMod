package BoardGame.multicharacter;

import BoardGame.multicharacter.patches.ContextPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class GainEnergyAndEnableControlsMultiAction extends AbstractGameAction {

    public GainEnergyAndEnableControlsMultiAction() {
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 0);
    }

    public void update() {
        if (this.duration == 0.5F) {
            for(AbstractPlayer s : BGMultiCharacter.getSubcharacters()) {
                ContextPatches.pushPlayerContext(s);

                int energyGain = AbstractDungeon.player.energy.energyMaster;
                AbstractDungeon.player.gainEnergy(energyGain);
                AbstractDungeon.actionManager.updateEnergyGain(energyGain);
                for (AbstractCard c : AbstractDungeon.player.hand.group)
                    c.triggerOnGainEnergy(energyGain, false);
                for (AbstractRelic r : AbstractDungeon.player.relics)
                    r.onEnergyRecharge();
                for (AbstractPower p : AbstractDungeon.player.powers)
                    p.onEnergyRecharge();

                ContextPatches.popPlayerContext();
            }

            AbstractDungeon.actionManager.turnHasEnded = false;

        }
        tickDuration();
    }
}

