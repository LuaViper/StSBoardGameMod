package BoardGame.cards.BGGreen;

import BoardGame.actions.BGStormOfSteelAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.CardDoesNotDiscardWhenPlayed;
import BoardGame.characters.BGSilent;
import BoardGame.powers.BGCorpseExplosionPower;
import BoardGame.powers.BGPoisonPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGStormOfSteel extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGStormOfSteel");
    public static final String ID = "BGStormOfSteel";

    public BGStormOfSteel() {
        super("BGStormOfSteel", cardStrings.NAME, "green/skill/storm_of_steel", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGSilent.Enums.BG_GREEN, CardRarity.RARE, CardTarget.SELF);

        this.baseMagicNumber=0;
        this.magicNumber=this.baseMagicNumber;

    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGStormOfSteelAction(AbstractDungeon.player,this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGStormOfSteel();
    }





}


