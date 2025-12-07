package CoopBoardGame.cards.BGRed;

import CoopBoardGame.actions.BGSpotWeaknessAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGIronclad;
import CoopBoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSpotWeakness extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGSpot Weakness"
    );
    public static final String ID = "BGSpot Weakness";

    public BGSpotWeakness() {
        super(
            "BGSpot Weakness",
            cardStrings.NAME,
            "red/skill/spot_weakness",
            1,
            cardStrings.DESCRIPTION,
            AbstractCard.CardType.SKILL,
            BGIronclad.Enums.BG_RED,
            AbstractCard.CardRarity.UNCOMMON,
            AbstractCard.CardTarget.SELF
        );
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        int maxDieNumber = 3;
        if (this.upgraded) maxDieNumber = 4;
        addToBot((AbstractGameAction) new BGSpotWeaknessAction(this.magicNumber, maxDieNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            //upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public void triggerOnGlowCheck() {
        this.glowColor = shouldGlow()
            ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy()
            : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    private boolean shouldGlow() {
        int maxDieNumber = 3;
        if (this.upgraded) maxDieNumber = 4;
        if (TheDie.monsterRoll <= maxDieNumber) return true;

        return false;
    }

    public AbstractCard makeCopy() {
        return new BGSpotWeakness();
    }
}
