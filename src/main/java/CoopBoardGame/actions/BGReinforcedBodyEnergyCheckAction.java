package CoopBoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class BGReinforcedBodyEnergyCheckAction extends AbstractGameAction {

    AbstractPlayer p;
    AbstractCard card;
    int minEnergy;
    int energyOnUse;
    int exactEnergyCost;
    boolean freeToPlayOnce;
    boolean upgraded;
    private int blockBonus;

    private BGXCostCardAction.XCostInfo info;

    public BGReinforcedBodyEnergyCheckAction(
        AbstractPlayer p,
        AbstractCard card,
        BGXCostCardAction.XCostInfo info,
        boolean upgraded,
        int blockBonus
    ) {
        this.p = p;
        this.card = card;
        this.info = info;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.upgraded = upgraded;
        this.blockBonus = blockBonus;
    }

    public void update() {
        if (info.maxEnergy > 0 || card.upgraded) {
            addToBot(
                (AbstractGameAction) new BGXCostCardAction(card, info, (e, d) ->
                    addToTop(
                        (AbstractGameAction) new BGReinforcedBodyAction(
                            AbstractDungeon.player,
                            d,
                            e,
                            upgraded,
                            blockBonus
                        )
                    )
                )
            );
        } else {
            //TODO: we shouldn't ever reach this part; BGReinforcedBody.canUse is supposed to catch before then
            //TODO: localization
            AbstractDungeon.effectList.add(
                new ThoughtBubble(
                    AbstractDungeon.player.dialogX,
                    AbstractDungeon.player.dialogY,
                    3.0F,
                    "I can't play that card for 0 Energy.",
                    true
                )
            );
        }

        this.isDone = true;
    }
}
