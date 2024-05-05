package BoardGame.cards.BGPurple;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.AbstractBGPlayer;
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

public class BGFlurryOfBlows extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGFlurryOfBlows");
    public static final String ID = "BGFlurryOfBlows";

    public BGFlurryOfBlows() {
        super("BGFlurryOfBlows", cardStrings.NAME, "purple/attack/flurry_of_blows", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage=1;
    }


    //stanceChangedThisTurn patch is in BGIndignation
    public void triggerOnGlowCheck() {
        this
                .glowColor = ((AbstractBGPlayer)AbstractDungeon.player).stanceChangedThisTurn ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        if(((AbstractBGPlayer)AbstractDungeon.player).stanceChangedThisTurn){
            addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
            if(upgraded){
                addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
            }
        }
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGFlurryOfBlows();
    }




}


