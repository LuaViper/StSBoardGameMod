package BoardGame.cards.BGBlue;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.relics.BGTheDieRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGForceField extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGForceField");
    public static final String ID = "BGForceField";

    public BGForceField() {
        super("BGForceField", cardStrings.NAME, "blue/skill/forcefield", 3, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseBlock = 3;
    }

    public void applyPowers(){
        super.applyPowers();
        int discount=Math.min(BGTheDieRelic.powersPlayedThisCombat,3);
        this.modifyCostForCombat(-this.cost+3-discount);
        //BoardGame.BoardGame.logger.info("BGForceField: modifyCostForCombat "+(-discount));
    }
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        int discount=Math.min(BGTheDieRelic.powersPlayedThisCombat,3);
        this.modifyCostForCombat(-this.cost+3-discount);
        //BoardGame.BoardGame.logger.info("BGForceField: modifyCostForCombat "+(-discount));

    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGForceField();
    }
}