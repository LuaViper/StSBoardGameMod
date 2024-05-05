package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import BoardGame.powers.BGWeakPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGClothesline extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGClothesline");
    public static final String ID = "BGClothesline";

    public BGClothesline() {
        super("BGClothesline", cardStrings.NAME, "red/attack/clothesline", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.ENEMY);










        this.baseDamage = 3;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGWeakPower((AbstractCreature)m, this.magicNumber, false), this.magicNumber));

        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)m, (AbstractPower)new BGVulnerablePower((AbstractCreature)m, 3, false), 3));
        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGWeakPower((AbstractCreature)p, 3, false), 3));
        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)m, (AbstractPower)new BGVulnerablePower((AbstractCreature)m, 3, false), 3));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            //upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGClothesline();
    }
}


