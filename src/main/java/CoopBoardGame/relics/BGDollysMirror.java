package CoopBoardGame.relics;

import CoopBoardGame.powers.BGTriggerAnyDieAbilityPower;
import CoopBoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGDollysMirror extends AbstractBGRelic implements DieControlledRelic {

    public static final String ID = "BGDollysMirror";

    public BGDollysMirror() {
        super(
            "BGDollysMirror",
            "mirror.png",
            AbstractRelic.RelicTier.SHOP,
            AbstractRelic.LandingSound.SOLID
        );
    }

    public int getPrice() {
        return 7;
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public String getQuickSummary() {
        if (TheDie.monsterRoll == 1) return "Copy any die relic";
        return "";
    }

    public void checkDieAbility() {
        if (TheDie.finalRelicRoll == 1) {
            activateDieAbility();
        }
    }

    public void activateDieAbility() {
        flash();
        addToBot(
            (AbstractGameAction) new RelicAboveCreatureAction(
                (AbstractCreature) AbstractDungeon.player,
                this
            )
        );
        addToBot(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) AbstractDungeon.player,
                (AbstractCreature) AbstractDungeon.player,
                (AbstractPower) new BGTriggerAnyDieAbilityPower(
                    (AbstractCreature) AbstractDungeon.player
                )
            )
        );
    }


    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
    }

    public AbstractRelic makeCopy() {
        return new BGDollysMirror();
    }

    @Override
    public void onRightClick() {
    }
}
