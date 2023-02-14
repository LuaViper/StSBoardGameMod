package BoardGame.damagemodifiers;

import BoardGame.BoardGame;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

public class DamageModBGVulnerable extends AbstractDamageModifier {
    public static final String ID = BoardGame.makeID("BGVulnerableDamage");
    public final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    TooltipInfo leechTooltip = null;

    public DamageModBGVulnerable() {}

    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if(info.owner.hasPower("BGWeakened") )
            return damageAmount;

        return damageAmount*2;

    }

//    //This hook allows up to add a custom tooltip to any cards it is added to.
//    //In this case, we are using cardstrings to get the localized data
//    public TooltipInfo getCustomTooltip() {
//        if (leechTooltip == null) {
//            leechTooltip = new TooltipInfo(cardStrings.DESCRIPTION, cardStrings.EXTENDED_DESCRIPTION[0]);
//        }
//        return leechTooltip;
//    }

//    //This allows us to add an stslib descriptor to the card
//    // If it was originally an Attack, it will now read "Attack | Leech"
//    @Override
//    public String getCardDescriptor() {
//        return cardStrings.NAME;
//    }

    //Overriding this to true tells us that this damage mod is considered part of the card and not just something added on to the card later.
    //If you ever add a damage modifier during the initialization of a card, it should be inherent.
    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new DamageModBGVulnerable();
    }

}