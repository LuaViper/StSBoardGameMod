package BoardGame.cards.BGPurple;
import BoardGame.actions.BGSashWhipAction;
import BoardGame.actions.BGTalkToTheHandAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
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

public class BGTalkToTheHand extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTalkToTheHand");
    public static final String ID = "BGTalkToTheHand";

    public BGTalkToTheHand() {
        super("BGTalkToTheHand", cardStrings.NAME, "purple/attack/talk_to_the_hand", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.ENEMY);

        this.baseDamage=2;
        this.baseBlock=1;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot((AbstractGameAction)new BGTalkToTheHandAction(p,block));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGTalkToTheHand();
    }
}



