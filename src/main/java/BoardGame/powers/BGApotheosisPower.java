package BoardGame.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGApotheosisPower
        extends AbstractPower {
    public static final String POWER_ID = "BGApotheosisPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGApotheosisPower");

    public BGApotheosisPower(AbstractCreature owner, int amt) {
        this.name = powerStrings.NAME;
        this.ID = "BGApotheosisPower";
        this.owner = owner;
        this.amount = amt;
        updateDescription();
        loadRegion("accuracy");
        updateExistingStrikes();
        updateExistingDefends();
    }


    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }


    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateExistingStrikes();
        updateExistingDefends();
    }

    private void updateExistingStrikes() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                if(!c.upgraded){c.baseDamage = 1+this.amount;}else{c.baseDamage = 2+this.amount;}

            }
        }

        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                if(!c.upgraded){c.baseDamage = 1+this.amount;}else{c.baseDamage = 2+this.amount;}
            }
        }

        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                if(!c.upgraded){c.baseDamage = 1+this.amount;}else{c.baseDamage = 2+this.amount;}
            }
        }

        for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                if(!c.upgraded){c.baseDamage = 1+this.amount;}else{c.baseDamage = 2+this.amount;}
            }
        }
    }

    private void updateExistingDefends() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_DEFEND)) {
                if(!c.upgraded){c.baseBlock = 1+this.amount;}else{c.baseBlock = 2+this.amount;}
            }
        }

        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_DEFEND)) {
                if(!c.upgraded){c.baseBlock = 1+this.amount;}else{c.baseBlock = 2+this.amount;}
            }
        }

        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_DEFEND)) {
                if(!c.upgraded){c.baseBlock = 1+this.amount;}else{c.baseBlock = 2+this.amount;}
            }
        }

        for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_DEFEND)) {
                if(!c.upgraded){c.baseBlock = 1+this.amount;}else{c.baseBlock = 2+this.amount;}
            }
        }
    }

    public void onDrawOrDiscard() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE))
                if(!c.upgraded){c.baseDamage = 1+this.amount;}else{c.baseDamage = 2+this.amount;}
            if (c.hasTag(AbstractCard.CardTags.STARTER_DEFEND))
                if(!c.upgraded){c.baseBlock = 1+this.amount;}else{c.baseBlock = 2+this.amount;}
        }
    }
}



