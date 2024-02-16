//TODO: currently, copied cards are played after the original card (VG) instead of before the original card (BG)
//TODO: if Doppelganger is forced to copy Burst via e.g. Havoc, is Doppelganger Unplayable or does it merely have no effect?

package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.powers.BGBurstPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.AdrenalineEffect;

public class BGAdrenaline extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Adrenaline");
    public static final String ID = "BGAdrenaline";

    public BGAdrenaline() {
        super("BGAdrenaline", cardStrings.NAME, "green/skill/adrenaline", 0, cardStrings.DESCRIPTION, CardType.SKILL, BGSilent.Enums.BG_GREEN, CardRarity.RARE, CardTarget.SELF);

        this.exhaust=true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new AdrenalineEffect(), 0.15F));
        if (this.upgraded) {
            addToBot((AbstractGameAction)new GainEnergyAction(2));
        } else {
            addToBot((AbstractGameAction)new GainEnergyAction(1));
        }
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)p, 2));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
    public AbstractCard makeCopy() {
        return new BGAdrenaline();
    }
}


