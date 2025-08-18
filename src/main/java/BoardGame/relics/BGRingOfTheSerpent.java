package BoardGame.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGRingOfTheSerpent
        extends AbstractBGRelic  {
    public static final String ID = "BGRing of the Serpent";
    private static final int NUM_CARDS = 1;

    public BGRingOfTheSerpent() {
        super("BGRing of the Serpent", "serpent_ring.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
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