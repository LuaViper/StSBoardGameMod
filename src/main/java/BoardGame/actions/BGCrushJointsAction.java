package BoardGame.actions;

import BoardGame.patches.DiscardInOrderOfEnergyCostPatch;
import BoardGame.powers.BGAfterImagePower;
import BoardGame.powers.BGVulnerablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;


public class BGCrushJointsAction extends AbstractGameAction {
    private AbstractMonster m;

    private int magicNumber;


    public BGCrushJointsAction(AbstractMonster monster, int vulnAmount) {
        this.m = monster;
        this.magicNumber = vulnAmount;
    }

    public void update() {
        if (AbstractDungeon.player.stance.ID.equals("BGWrath")) {
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.m, (AbstractCreature) AbstractDungeon.player, (AbstractPower) new BGVulnerablePower((AbstractCreature) this.m, this.magicNumber, false), this.magicNumber));
        }
        this.isDone = true;
    }
}

