package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.unique.EscapePlanAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGEscapePlan extends AbstractBGCard {
    public static final String ID = "BGEscapePlan";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGEscapePlan");

    public BGEscapePlan() {
        super("BGEscapePlan", cardStrings.NAME, "green/skill/escape_plan", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);
        this.baseBlock = 1;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if(!this.upgraded) {
            addToBot((AbstractGameAction) new DrawCardAction(1, (AbstractGameAction) new EscapePlanAction(this.block)));
        }else{
            addToBot((AbstractGameAction) new GainBlockAction(AbstractDungeon.player,this.block));
            addToBot((AbstractGameAction) new DrawCardAction(1));
        }
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGEscapePlan();
    }
}

