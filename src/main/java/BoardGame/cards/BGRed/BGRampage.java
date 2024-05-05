package BoardGame.cards.BGRed;

import BoardGame.BoardGame;
import BoardGame.actions.BGRampageAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGRampage extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGRampage");
    public static final String ID = "BGRampage";

    public BGRampage() {
        super("BGRampage", cardStrings.NAME, "red/attack/rampage", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);










        this.baseDamage = 0;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.upgraded) {
            addToBot((AbstractGameAction) new ExhaustAction(1, false, true, false));
        }
        addToBot((AbstractGameAction)new BGRampageAction((AbstractMonster)m, (AbstractPlayer)p,this));

    }

    public void applyPowers() {
        this.baseDamage = AbstractDungeon.player.exhaustPile.size();
        super.applyPowers();
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGRampage applyPowers:"+this.damage);
        if (!this.upgraded) {
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        }else{
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[1];
        }
        initializeDescription();
    }

    public void calculateCardDamage(AbstractMonster mo) {
        this.baseDamage = AbstractDungeon.player.exhaustPile.size();
        super.calculateCardDamage(mo);
        if (!this.upgraded) {
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        }else{
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[1];
        }
        initializeDescription();
    }





    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGRampage();
    }
}


