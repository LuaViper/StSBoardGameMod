package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.powers.BGPoisonPower;
import BoardGame.powers.BGVulnerablePower;
import BoardGame.powers.BGWeakPower;
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

public class BGCripplingCloud extends AbstractBGCard {
    public static final String ID = "BGCripplingCloud";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGCripplingCloud");

    public BGCripplingCloud() {
        super("BGCripplingCloud", cardStrings.NAME, "green/skill/crippling_poison", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, AbstractCard.CardColor.GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ALL_ENEMY);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flash();
            for (AbstractMonster monster : (AbstractDungeon.getMonsters()).monsters) {
                if (!monster.isDead && !monster.isDying) {
                    addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)monster, (AbstractCreature)p, (AbstractPower)new BGPoisonPower((AbstractCreature)monster, (AbstractCreature)p, this.magicNumber), this.magicNumber));
                    addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)monster, (AbstractCreature)p, (AbstractPower)new BGWeakPower((AbstractCreature)monster, 1, false), 1));
                }
            }
        }
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGCripplingCloud();
    }
}

