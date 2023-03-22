package BoardGame.cards.BGRed;

import BoardGame.BoardGame;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGBloodForBlood extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBlood for Blood");
    public static final String ID = "BGBlood for Blood";

    public BGBloodForBlood() {
        super("BGBlood for Blood", cardStrings.NAME, "red/attack/blood_for_blood", 4, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);



        this.baseDamage = 4;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }


    public void tookDamage() {
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("BGBloodForBlood tookDamage: "+this.magicNumber);
        updateCost(-this.cost+this.magicNumber);
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(-1);
            if (this.cost < 3) {
                updateCost(-this.cost+this.magicNumber);
            }
        }
    }


    public AbstractCard makeCopy() {
        AbstractCard tmp = new BGBloodForBlood();
        if (AbstractDungeon.player != null) {
            if(AbstractDungeon.player.damagedThisCombat>0){
                updateCost(-this.cost+this.magicNumber);
            }
        }
        return tmp;
    }
}


