package CoopBoardGame.potions;

import CoopBoardGame.powers.BGWeakPower;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGWeakenPotion extends AbstractPotion {

    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(
        "CoopBoardGame:BGWeak Potion"
    );

    public static final String POTION_ID = "BGWeak_Potion";

    public BGWeakenPotion() {
        super(
            potionStrings.NAME,
            "BGWeak_Potion",
            AbstractPotion.PotionRarity.COMMON,
            AbstractPotion.PotionSize.H,
            AbstractPotion.PotionColor.WEAK
        );
        this.isThrown = true;
        this.targetRequired = true;
    }

    public int getPrice() {
        return 2;
    }

    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.tips.add(
            new PowerTip(
                BaseMod.getKeywordTitle("coopboardgame:weak"),
                BaseMod.getKeywordDescription("coopboardgame:weak")
            )
        );
    }

    public void use(AbstractCreature target) {
        addToBot(
            (AbstractGameAction) new ApplyPowerAction(
                target,
                (AbstractCreature) AbstractDungeon.player,
                (AbstractPower) new BGWeakPower(target, this.potency, false),
                this.potency
            )
        );
    }

    public int getPotency(int ascensionLevel) {
        return 2;
    }

    public AbstractPotion makeCopy() {
        return new BGWeakenPotion();
    }
}
