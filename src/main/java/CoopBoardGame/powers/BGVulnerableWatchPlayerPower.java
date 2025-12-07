package CoopBoardGame.powers;

import CoopBoardGame.CoopBoardGame;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGVulnerableWatchPlayerPower extends AbstractBGPower implements InvisiblePower {

    public static final String POWER_ID = CoopBoardGame.makeID("VulnerableWatchPlayer");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(
        POWER_ID
    );
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGVulnerableWatchPlayerPower(
        AbstractCreature owner,
        int amount,
        boolean isSourceMonster
    ) {
        this.name = NAME;
        this.ID = "BGVulnerableWatchPlayer";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("lockon");

        this.type = AbstractPower.PowerType.BUFF;
        this.isTurnBased = false;

        this.priority = 99;
    }

    public void duringTurn() {
        AbstractMonster m = (AbstractMonster) this.owner;
        if (m.getIntentBaseDmg() >= 0) {
            addToBot(
                (AbstractGameAction) new ReducePowerAction(
                    AbstractDungeon.player,
                    AbstractDungeon.player,
                    "BGVulnerable",
                    1
                )
            );
        }
        addToBot(
            (AbstractGameAction) new RemoveSpecificPowerAction(
                this.owner,
                this.owner,
                "BGVulnerableWatchPlayer"
            )
        );
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public void stackPower(int stackAmount) {
        this.amount = 1;
    }
}
