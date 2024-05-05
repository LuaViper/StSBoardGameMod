package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import BoardGame.powers.BGVulnerablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGBash extends AbstractBGCard {

    public static final String ID = "BGBash";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:Bash");

    public BGBash() {
        super("BGBash", cardStrings.NAME, "red/attack/bash", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.ENEMY);










        this.baseDamage = 2;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        BoardGame.BoardGame.logger.info("Current act number is "+AbstractDungeon.actNum+"    !!!!!!!!!!!!!!!");
        if (Settings.isDebug) {
            this.multiDamage = new int[(AbstractDungeon.getCurrRoom()).monsters.monsters.size()];
            for (int i = 0; i < (AbstractDungeon.getCurrRoom()).monsters.monsters.size(); i++) {
                this.multiDamage[i] = 100;
            }
            addToBot((AbstractGameAction)new DamageAllEnemiesAction((AbstractCreature)p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        } else {
            addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }

        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGVulnerablePower((AbstractCreature)m, this.magicNumber, false), this.magicNumber));
        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGWeakPower((AbstractCreature)m, this.magicNumber, false), this.magicNumber));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
            //upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGBash();
    }
}


