package BoardGame.cards.BGGreen;

import BoardGame.BoardGame;
import BoardGame.actions.BGDoppelgangerAction;
import BoardGame.actions.BGSkewerAction;
import BoardGame.actions.BGWhirlwindAction;
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
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class BGSkewer extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSkewer");
    public static final String ID = "BGSkewer";

    public BGSkewer() {
        super("BGSkewer", cardStrings.NAME, "green/attack/skewer", -1, cardStrings.DESCRIPTION, CardType.ATTACK, BGSilent.Enums.BG_GREEN, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage=1;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if(this.freeToPlay()){
            this.energyOnUse=0;
        }
        if(this.ignoreEnergyOnUse){
            this.energyOnUse=0;
        }
        if(this.copiedCardEnergyOnUse!=-99){
            this.energyOnUse=this.copiedCardEnergyOnUse;
        }
        addToTop((AbstractGameAction)new BGXCostCardAction(this, this.energyOnUse,
                (e)->addToTop((AbstractGameAction)new BGSkewerAction(AbstractDungeon.player, m, this.damage, this.damageTypeForTurn, this.freeToPlayOnce, e, this.magicNumber))));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.upgradeDamage(1);
            this.upgradeMagicNumber(-1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGSkewer();
    }







}


