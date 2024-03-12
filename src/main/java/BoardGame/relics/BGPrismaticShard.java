package BoardGame.relics;

import BoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGPrismaticShard extends AbstractBGRelic {
    public static final String ID = "BGPrismaticShard";

    public BGPrismaticShard() {
        super("BGPrismaticShard", "prism.png", AbstractRelic.RelicTier.SHOP, AbstractRelic.LandingSound.MAGICAL);
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
