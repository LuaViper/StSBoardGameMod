package BoardGame.powers;

import BoardGame.actions.OrbSelectScreenAction;
import BoardGame.orbs.BGDark;
import BoardGame.screen.OrbSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class BGLikeWaterPower extends AbstractBGPower {
    public static final String POWER_ID = "BGLikeWaterPower";

    //use vanilla strings for this
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("LikeWaterPower");

    public BGLikeWaterPower(AbstractCreature owner, int amt) {
        this.name = powerStrings.NAME;
        this.ID = "BGLikeWaterPower";
        this.owner = owner;
        this.amount = amt;
        updateDescription();
        loadRegion("like_water");
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount > 999)
            this.amount = 999;
        updateDescription();
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }

    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (isPlayer) {
            AbstractPlayer p = (AbstractPlayer)this.owner;
            if (p.stance.ID.equals("BGCalm")) {
                flash();
                addToBot((AbstractGameAction)new GainBlockAction(this.owner, this.owner, this.amount));
            }
        }
    }
}