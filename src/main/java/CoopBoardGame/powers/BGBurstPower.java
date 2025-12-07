package CoopBoardGame.powers;

import CoopBoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import java.util.ArrayList;
import java.util.Collections;

public class BGBurstPower extends AbstractBGPower {

    public static final String POWER_ID = "CoopBoardGame:BGBurstPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(
        "CoopBoardGame:BGBurstPower"
    );
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGBurstPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "CoopBoardGame:BGBurstPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("burst");
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }

    public void onAboutToUseCard(AbstractCard originalCard, AbstractCreature originalTarget) {
        boolean copyOK = true;
        if (originalCard instanceof AbstractBGCard) {
            if (((AbstractBGCard) originalCard).cannotBeCopied) copyOK = false;
            if (((AbstractBGCard) originalCard).ignoreFurtherCopies) copyOK = false;
        }

        //TODO: depending on ruling, maybe preserve burst and wait until next card?
        if (
            !originalCard.purgeOnUse &&
            originalCard.type == AbstractCard.CardType.SKILL &&
            this.amount > 0 &&
            copyOK
        ) {
            flash();

            AbstractCard copiedCard = originalCard.makeSameInstanceOf();
            if (copiedCard instanceof AbstractBGCard) {
                ((AbstractBGCard) originalCard).ignoreFurtherCopies = true;
                ((AbstractBGCard) copiedCard).ignoreFurtherCopies = true;
            }
            BGDoubleAttackPower.swapOutQueueCard(copiedCard);

            AbstractDungeon.player.limbo.addToTop(copiedCard);
            copiedCard.current_x = originalCard.current_x;
            copiedCard.current_y = originalCard.current_y;
            copiedCard.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            copiedCard.target_y = Settings.HEIGHT / 2.0F;

            copiedCard.purgeOnUse = true;

            if (originalCard instanceof AbstractBGCard) {
                ((AbstractBGCard) originalCard).copiedCard = (AbstractBGCard) copiedCard;
            }

            ((AbstractBGCard) copiedCard).followUpCardChain = new ArrayList<>(
                Collections.singletonList(originalCard)
            );

            this.amount--;
            if (this.amount == 0) {
                addToBot(
                    (AbstractGameAction) new RemoveSpecificPowerAction(
                        this.owner,
                        this.owner,
                        "CoopBoardGame:BGBurstPower"
                    )
                );
            }
        }
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) addToBot(
            (AbstractGameAction) new RemoveSpecificPowerAction(
                this.owner,
                this.owner,
                "CoopBoardGame:BGBurstPower"
            )
        );
    }
}
