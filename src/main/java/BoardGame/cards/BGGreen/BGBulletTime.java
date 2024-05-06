package BoardGame.cards.BGGreen;

//TODO: what happens if you Exhume or Hologram a card? and how does the VG handle it?

import BoardGame.actions.BGApplyBulletTimeAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.NoDrawPower;

public class BGBulletTime extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Bullet Time");
    public static final String ID = "BGBulletTime";

    public BGBulletTime() {
        super("BGBulletTime", cardStrings.NAME, "green/skill/bullet_time", 3, cardStrings.DESCRIPTION, CardType.SKILL, BGSilent.Enums.BG_GREEN, CardRarity.RARE, CardTarget.NONE);

    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new NoDrawPower((AbstractCreature)p), 1));
        //addToBot((AbstractGameAction)new ApplyBulletTimeAction());
        addToBot((AbstractGameAction)new BGApplyBulletTimeAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(2);
        }
    }


    public AbstractCard makeCopy() {
        return new BGBulletTime();
    }
}


