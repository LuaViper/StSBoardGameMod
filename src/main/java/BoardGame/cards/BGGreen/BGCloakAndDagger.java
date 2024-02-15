package BoardGame.cards.BGGreen;

import BoardGame.actions.BGGainBlockIfShivAction;
import BoardGame.actions.BGGainShivAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGCloakAndDagger extends AbstractBGCard {
    public static final String ID = "BGCloakAndDagger";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Cloak And Dagger");

    public BGCloakAndDagger() {
        super("BGDeflect", cardStrings.NAME, "green/skill/cloak_and_dagger", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGSilent.Enums.BG_GREEN, CardRarity.COMMON, CardTarget.SELF);
        this.baseBlock = 1;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGGainShivAction(this.magicNumber));
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));

    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGCloakAndDagger();
    }
}


