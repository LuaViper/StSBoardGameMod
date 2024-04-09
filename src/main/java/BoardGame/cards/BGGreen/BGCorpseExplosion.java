package BoardGame.cards.BGGreen;

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

public class BGCorpseExplosion extends AbstractBGCard implements CardDoesNotDiscardWhenPlayed {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGCorpseExplosion");
    public static final String ID = "BGCorpseExplosion";

    public BGCorpseExplosion() {
        super("BGCorpseExplosion", cardStrings.NAME, "green/skill/corpse_explosion", 2, cardStrings.DESCRIPTION, CardType.SKILL, BGSilent.Enums.BG_GREEN, CardRarity.RARE, CardTarget.ENEMY);

        this.baseMagicNumber=2;
        this.magicNumber=this.baseMagicNumber;
        this.defaultBaseSecondMagicNumber=6;
        this.defaultSecondMagicNumber=this.defaultBaseSecondMagicNumber;

    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGPoisonPower((AbstractCreature)m, (AbstractCreature)p, this.magicNumber), this.magicNumber));

        //TODO: the current check isn't good enough -- need to actually check if this card Is A Copy
            //not only because we might be running more than one physical copy of CE,
            //but there are some shenanigans which allow a Copy Of CE (or the original, whichever comes 2nd) to be played while no enemy has the debuff
        for(AbstractMonster m2 : (AbstractDungeon.getCurrRoom()).monsters.monsters){
            AbstractPower pw=m2.getPower("BoardGame:BGCorpseExplosionPower");
            if(pw!=null){
                return;
            }
        }

        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGCorpseExplosionPower((AbstractCreature)m, (AbstractCreature)p, this.defaultSecondMagicNumber,this), this.defaultSecondMagicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            upgradeDefaultSecondMagicNumber(4);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGCorpseExplosion();
    }





}


