package BoardGame.actions;

import BoardGame.powers.BGSpikerProccedPower;
import BoardGame.screen.TargetSelectScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TargetSelectScreenAction extends AbstractGameAction {
    private TargetSelectScreen.TargetSelectAction action;
    private String description;
    private boolean allowCancel;
    private TargetSelectScreen.TargetSelectAction cancelAction;

    public TargetSelectScreenAction(TargetSelectScreen.TargetSelectAction action,String description) {
        this.action=action;
        this.description=description;
    }

    //TODO: make a ForcedWaitAction which applies even if settings are set to fastmode, but don't wait if there are no targets available

    public void update() {
        final Logger logger = LogManager.getLogger(TargetSelectScreenAction.class.getName());
       // logger.info("TSSAction update");
        BaseMod.openCustomScreen(TargetSelectScreen.Enum.TARGET_SELECT,action, description);

        this.isDone = true;
    }
}


