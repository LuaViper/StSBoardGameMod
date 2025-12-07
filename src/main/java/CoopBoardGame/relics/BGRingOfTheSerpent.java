package CoopBoardGame.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGRingOfTheSerpent extends AbstractBGRelic {

    public static final String ID = "BGRing of the Serpent";

    public BGRingOfTheSerpent() {
        super(
            "BGRing of the Serpent",
            "serpent_ring.png",
            AbstractRelic.RelicTier.BOSS,
            AbstractRelic.LandingSound.CLINK
        );
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1];
    }

    public void onEquip() {
        AbstractDungeon.player.masterHandSize++;
    }

    public void onUnequip() {
        AbstractDungeon.player.masterHandSize--;
    }

    public void atTurnStart() {
        flash();
    }

    public AbstractRelic makeCopy() {
        return new BGRingOfTheSerpent();
    }
}
