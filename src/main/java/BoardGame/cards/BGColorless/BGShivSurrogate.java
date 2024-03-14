package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGShivSurrogate extends AbstractBGCard {
    public static final String ID = "BGShivSurrogate";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShivSurrogate");

    public static final int ATTACK_DMG = 4;

    public static final int UPG_ATTACK_DMG = 2;

    public boolean isARealShiv;
    public BGShivSurrogate() {
        super("BGShivSurrogate", cardStrings.NAME, "colorless/attack/shiv", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.ENEMY);
        this.baseDamage = 1;
        this.exhaust = true;
        this.cannotBeCopied=true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    public AbstractCard makeCopy() {
        return new BGShivSurrogate();
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}