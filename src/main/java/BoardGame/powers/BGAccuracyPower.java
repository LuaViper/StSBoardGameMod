package BoardGame.powers;

import BoardGame.BoardGame;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGShivs;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGAccuracyPower extends AbstractBGPower {
    public static final String POWER_ID = "BGAccuracy";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGAccuracyPower");

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGAccuracyPower(AbstractCreature owner, int amt) {
        this.name = powerStrings.NAME;
        this.ID = "BGAccuracy";
        this.owner = owner;
        this.amount = amt;
        updateDescription();
        loadRegion("accuracy");
        updateExistingShivs();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateExistingShivs();
    }

    private void updateExistingShivs() {
        if(AbstractDungeon.player!=null) {
            AbstractRelic shivs = AbstractDungeon.player.getRelic("BoardGame:BGShivs");
            if (shivs != null) {
                ((BGShivs)shivs).updateDescription(this.amount);
            }
        }
    }

    public void onVictory() {
        if(AbstractDungeon.player!=null) {
            AbstractRelic shivs = AbstractDungeon.player.getRelic("BoardGame:BGShivs");
            if (shivs != null) {
                ((BGShivs)shivs).updateDescription(0);
            }
        }
    }

}
