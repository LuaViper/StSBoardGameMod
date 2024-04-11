package BoardGame.relics;

import BoardGame.orbs.BGLightning;
import basemod.BaseMod;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGCrackedCore extends AbstractBGRelic  {
    public static final String ID = "BGCrackedCore";

    public BGCrackedCore() {
        super("BGCrackedCore", "crackedOrb.png", RelicTier.STARTER, LandingSound.CLINK);
    }

    public void onEquip() {
        BaseMod.MAX_HAND_SIZE=999;
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


