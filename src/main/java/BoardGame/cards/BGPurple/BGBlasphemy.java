package BoardGame.cards.BGPurple;

import BoardGame.actions.BGExhaustDrawPileAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.powers.BGTripleAttackPower;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

public class BGBlasphemy extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBlasphemy");
    public static final String ID = "BGBlasphemy";

    public BGBlasphemy() {
        super("BGBlasphemy", cardStrings.NAME, "purple/skill/blasphemy", 2, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.RARE, CardTarget.SELF);
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

        addToBot(new VFXAction(new LightningEffect(p.hb.cX, p.hb.cY)));
        addToBot(new VFXAction (new DamageNumberEffect(p, p.hb.cX, p.hb.cY, 99999)));
        addToBot(new BGExhaustDrawPileAction(p));
        addToBot(new ApplyPowerAction(p, p, new BGTripleAttackPower(p,1),1));

    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.selfRetain=true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGBlasphemy();
    }
}
