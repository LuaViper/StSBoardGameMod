package BoardGame.powers;

import BoardGame.actions.BGEvokeSpecificOrbAction;
import BoardGame.actions.OrbSelectScreenAction;
import BoardGame.orbs.BGDark;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.screen.OrbSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGLoopPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGLoopPower");
    public static final String POWER_ID = "BoardGame:BGLoopPower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static int loopIdOffset;

    public BGLoopPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BoardGame:BGLoopPower" + loopIdOffset;
        loopIdOffset++;
        this.owner = owner;
        this.amount = amount;

        updateDescription();
        loadRegion("loop");
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_INTANGIBLE", 0.05F);
    }

    public void updateDescription() {
        if (this.amount <= 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }


    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        boolean isEmpty=true;
        for(AbstractOrb o : AbstractDungeon.player.orbs){
            if(!(o instanceof EmptyOrbSlot) && !(o instanceof BGDark)) {
                isEmpty = false;
            }
        }
        if (!isEmpty) {
            flash();
            BoardGame.BoardGame.logger.info("BGLoopPower.atEndOfTurn");
            OrbSelectScreen.OrbSelectAction ossAction = (target) -> {
                //TODO LATER: this code currently runs even if we pick EmptyOrbSlot or BGDark, so don't add any side effects
                //TODO LATER: onEndOfTurn currently uses addToBot, so some events will be out of order
                AbstractPlayer player = AbstractDungeon.player;
                AbstractOrb orb = player.orbs.get(target);
                for (int i = 0; i < this.amount; i++) {
                    ((AbstractOrb) AbstractDungeon.player.orbs.get(target)).onEndOfTurn();
                }
            };
            addToTop((AbstractGameAction) new OrbSelectScreenAction(ossAction, "Choose an Orb to Loop.", true));
        }
    }




}


