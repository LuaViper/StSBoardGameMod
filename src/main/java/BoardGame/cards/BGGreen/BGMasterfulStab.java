package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import basemod.ReflectionHacks;
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
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGMasterfulStab extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGMasterful Stab");
    public static final String ID = "BGMasterfulStab";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGMasterfulStab.class.getName());
    public BGMasterfulStab() {
        super("BGMasterful Stab", cardStrings.NAME, "green/attack/masterful_stab", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGSilent.Enums.BG_GREEN, CardRarity.UNCOMMON, CardTarget.ENEMY);

        this.baseDamage = 2;
        this.baseMagicNumber=2;
        this.magicNumber=this.baseMagicNumber;
    }

    public void tookDamage() {
        //TODO: multiplayer doppelganger implementation will be Not Fun
        //TODO: we haven't actually tested to make sure this stops at cost 2
        if(this.cost<this.magicNumber) updateCost(this.magicNumber);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            upgradeMagicNumber(-1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGMasterfulStab();
    }
}


