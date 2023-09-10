package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGPerfectedStrike extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGPerfected Strike"); public static final String ID = "BGPerfected Strike";

    public BGPerfectedStrike() {
        super("BGPerfected Strike", cardStrings.NAME, "red/attack/perfected_strike", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);










        this.baseDamage = 3;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        this.tags.add(AbstractCard.CardTags.STRIKE);
    }

    public static int countCards(AbstractCard self) {
        int count = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (isStrike(c) && c!=self) {
                count++;
            }
        }
//        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
//            if (isStrike(c)) {
//                count++;
//            }
//        }
//        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
//            if (isStrike(c)) {
//                count++;
//            }
//        }
        return count;
    }

    public static boolean isStrike(AbstractCard c) {
        return c.hasTag(AbstractCard.CardTags.STRIKE);
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }






    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * countCards(this);

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;


        this.isDamageModified = (this.damage != this.baseDamage);
    }




    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * countCards(this);

        super.applyPowers();

        this.baseDamage = realBaseDamage;


        this.isDamageModified = (this.damage != this.baseDamage);
    }


    public AbstractCard makeCopy() {
        return new BGPerfectedStrike();
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}


