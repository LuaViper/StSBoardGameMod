package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.multicharacter.patches.ActionPatches;
import BoardGame.multicharacter.patches.ContextPatches;
import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class GainEnergyAndEnableControlsMultiAction extends AbstractGameAction {

    public GainEnergyAndEnableControlsMultiAction() {
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 0);
    }

    public void update() {
        if (this.duration == 0.5F) {
            for(AbstractPlayer s : BGMultiCharacter.getSubcharacters()) {
                ContextPatches.pushContext(s);

                int energyGain = AbstractDungeon.player.energy.energyMaster;
                AbstractDungeon.player.gainEnergy(energyGain);
                AbstractDungeon.actionManager.updateEnergyGain(energyGain);
                for (AbstractCard c : AbstractDungeon.player.hand.group)
                    c.triggerOnGainEnergy(energyGain, false);
                for (AbstractRelic r : AbstractDungeon.player.relics)
                    r.onEnergyRecharge();
                for (AbstractPower p : AbstractDungeon.player.powers)
                    p.onEnergyRecharge();

                ContextPatches.popContext();
            }

            AbstractDungeon.actionManager.turnHasEnded = false;

        }
        tickDuration();
    }
}

