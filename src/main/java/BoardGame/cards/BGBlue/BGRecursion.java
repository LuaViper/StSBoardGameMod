package BoardGame.cards.BGBlue;

import BoardGame.actions.BGEvokeOrbRecursionAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGRecursion extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGRecursion");
    public static final String ID = "BGRecursion";

    public BGRecursion() {
        super("BGRecursion", cardStrings.NAME, "blue/skill/recursion", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.SELF);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        BoardGame.BoardGame.logger.info(1.0F + Interpolation.pow2Out.apply(0.03F, 0.11F, 1.0F - 1.2F));
        BoardGame.BoardGame.logger.info(1.0F + Interpolation.pow2Out.apply(0.03F, 0.11F, 1.0F - 0.0F));
        addToBot((AbstractGameAction)new BGEvokeOrbRecursionAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    public AbstractCard makeCopy() {
        return new BGRecursion();
    }
}
