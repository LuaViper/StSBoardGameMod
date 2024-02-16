package BoardGame.cards.BGGreen;

import BoardGame.BoardGame;
import BoardGame.actions.BGDoppelgangerAction;
import BoardGame.actions.BGMalaiseAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class BGMalaise extends AbstractBGCard {
    public static final String ID = "BGMalaise";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGMalaise");

    public BGMalaise() {
        super("BGMalaise", cardStrings.NAME, "green/skill/malaise", -1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.ENEMY);
        this.baseMagicNumber=0;
        this.magicNumber=this.baseMagicNumber;
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        int minEnergy=0;
        if(this.isCostModifiedForTurn){
            minEnergy=this.costForTurn;
            this.energyOnUse=this.costForTurn;
        }
        if(this.freeToPlay()){
            this.energyOnUse=0;
        }
        if(this.ignoreEnergyOnUse){
            this.energyOnUse=0;
        }
        if(this.copiedCardEnergyOnUse!=-99){
            this.energyOnUse=this.copiedCardEnergyOnUse;
        }
        addToTop((AbstractGameAction)new BGXCostCardAction(this, minEnergy, this.energyOnUse,
                (e)->addToTop((AbstractGameAction)new BGMalaiseAction(p, m, this.freeToPlayOnce, e, this.magicNumber))));
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
        return new BGMalaise();
    }
}
