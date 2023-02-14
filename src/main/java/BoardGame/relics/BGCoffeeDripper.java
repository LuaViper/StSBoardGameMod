package BoardGame.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.RestOption;

public class BGCoffeeDripper extends AbstractBGRelic  {
    public static final String ID = "BGCoffee Dripper";

    public BGCoffeeDripper() {
        super("BGCoffee Dripper", "coffeeDripper.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
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
    }


    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster--;
    }



    public boolean canUseCampfireOption(AbstractCampfireOption option) {
        if (option instanceof RestOption && option.getClass().getName().equals(RestOption.class.getName())) {
            ((RestOption)option).updateUsability(false);
            return false;
        }
        return true;
    }


    public AbstractRelic makeCopy() {
        return new BGCoffeeDripper();
    }
}


