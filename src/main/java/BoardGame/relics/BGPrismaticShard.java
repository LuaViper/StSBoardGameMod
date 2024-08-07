package BoardGame.relics;

import BoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGPrismaticShard extends AbstractBGRelic {
    public static final String ID = "BGPrismaticShard";

    //TODO: consider changing RelicTier for all(?) relics so they show up in a more appropriate category in the compendium
    public BGPrismaticShard() {
        super("BGPrismaticShard", "prism.png", AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.MAGICAL);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new BGPrismaticShard();
    }

    public void onEquip() {
        if (!(AbstractDungeon.player instanceof BGDefect) && AbstractDungeon.player.masterMaxOrbs < 2)
            AbstractDungeon.player.masterMaxOrbs = 2;
    }
}
