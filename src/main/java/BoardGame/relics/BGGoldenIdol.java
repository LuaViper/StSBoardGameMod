package BoardGame.relics;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;


public class BGGoldenIdol
        extends AbstractBGRelic {
    public static final String ID = "BGGolden Idol";

    public BGGoldenIdol() {
        super("BGGolden Idol", "goldenIdolRelic.png", AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.HEAVY);
    }
    public int getPrice() {return 4;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    //TODO: might be able to add +1 gold directly to combat rewards from RewardItem.applyGoldBonus
    public void onVictory() {
        flash();
        AbstractPlayer p = AbstractDungeon.player;
        addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)p, this));
        AbstractDungeon.player.gainGold(1);
    }



    public AbstractRelic makeCopy() {
        return new BGGoldenIdol();
    }
}


