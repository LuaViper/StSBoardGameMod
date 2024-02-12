package BoardGame.cards.BGGreen;

import BoardGame.actions.BGDamagePerAttackPlayedAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.relics.BGShivs;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DamagePerAttackPlayedAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGFinisher extends AbstractBGCard {
    public static final String ID = "BGFinisher";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGFinisher");

    public BGFinisher() {
        super("BGFinisher", cardStrings.NAME, "green/attack/finisher", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);
        this.baseDamage = 1;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGDamagePerAttackPlayedAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    public void applyPowers() {
        super.applyPowers();
        int count = 0;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c.type == AbstractCard.CardType.ATTACK)
                count++;
        }
        BGShivs relic = (BGShivs)AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        if(relic!=null){
            count+=relic.shivsPlayedThisTurn;
        }
        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0] + count;
        if (count == 1) {
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[1];
        } else {
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[2];
        }
        initializeDescription();
    }

    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGFinisher();
    }
}


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\SlayTheSpire\desktop-1.0.jar!\com\megacrit\cardcrawl\cards\green\Finisher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */