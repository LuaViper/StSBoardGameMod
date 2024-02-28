package BoardGame.relics;

import BoardGame.orbs.BGLightning;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.RestOption;

public class BGCrackedCore extends AbstractBGRelic  {
    public static final String ID = "BGCrackedCore";

    public BGCrackedCore() {
        super("BGCrackedCore", "crackedOrb.png", RelicTier.STARTER, LandingSound.CLINK);
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void atPreBattle() {
        AbstractDungeon.player.channelOrb((AbstractOrb)new BGLightning());
    }



    public AbstractRelic makeCopy() {
        return new BGCrackedCore();
    }
}


