package BoardGame.actions;

import BoardGame.BoardGame;
import BoardGame.cards.BGColorless.BGFakeShiv;
import BoardGame.powers.BGTrigger2DieAbilityPower;
import BoardGame.powers.BGTriggerAnyDieAbilityPower;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.relics.DieControlledRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckAfterUseCardAction
            extends AbstractGameAction {
    private DamageInfo info;
    private DieControlledRelic relic = null;
    private BGFakeShiv fakeShiv = new BGFakeShiv();
    private UseCardAction fakeShivAction = new UseCardAction(fakeShiv, target);

    public CheckAfterUseCardAction(BGFakeShiv fakeShiv, UseCardAction fakeShivAction) {
        this.fakeShiv = fakeShiv;
        this.fakeShivAction = fakeShivAction;
    }

    public void update() {
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("CheckAfterUseCardAction activates");

        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (!fakeShiv.dontTriggerOnUseCard)
                p.onAfterUseCard(fakeShiv, fakeShivAction);
        }
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            for (AbstractPower p : m.powers) {
                if (!fakeShiv.dontTriggerOnUseCard)
                    p.onAfterUseCard(fakeShiv, fakeShivAction);
            }
        }
        this.isDone = true;
    }


}


