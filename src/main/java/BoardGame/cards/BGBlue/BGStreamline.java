package BoardGame.cards.BGBlue;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.AbstractBGCharacter;
import BoardGame.characters.BGDefect;
import BoardGame.relics.BGTheDieRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.defect.FTLAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGStreamline extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGStreamline");
    public static final String ID = "BGStreamline";

    public BGStreamline() {
        super("BGStreamline", cardStrings.NAME, "blue/attack/streamline", 2, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = 3;
    }

    public void applyPowers(){
        super.applyPowers();
        int discount=Math.min(BGTheDieRelic.powersPlayedThisCombat,2);
        this.modifyCostForCombat(-this.cost+2-discount);
        BoardGame.BoardGame.logger.info("BGStreamline: modifyCostForCombat "+(-discount));
    }
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        int discount=Math.min(BGTheDieRelic.powersPlayedThisCombat,2);
        this.modifyCostForCombat(-this.cost+2-discount);
        BoardGame.BoardGame.logger.info("BGStreamline: modifyCostForCombat "+(-discount));

    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGStreamline();
    }
}