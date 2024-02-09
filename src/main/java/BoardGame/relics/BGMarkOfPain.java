package BoardGame.relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.SmithOption;

public class BGMarkOfPain extends AbstractBGRelic  {
    public static final String ID = "BGMarkOfPain";

    public BGMarkOfPain() {
        super("BGMarkOfPain", "mark_of_pain.png", RelicTier.BOSS, LandingSound.MAGICAL);
    }

    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return setDescription(AbstractDungeon.player.chosenClass);
        }
        return setDescription((AbstractPlayer.PlayerClass)null);
    }


    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[1] + this.DESCRIPTIONS[0];
    }


    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }


    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster++;
        AbstractDungeon.player.maxHealth=6;
        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth)
            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
    }


    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster--;
        //TODO LATER: maybe also restore original max HP (not a high priority as BG boss relics shouldn't be unequippable)
    }

    public AbstractRelic makeCopy() {
        return new BGMarkOfPain();
    }
}


