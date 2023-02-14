//package BoardGame.relics;
//import com.megacrit.cardcrawl.relics.AbstractRelic;
//
//
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//
//public class BGWingBoots extends AbstractBGRelic {
//    public static final String ID = "BGWingedGreaves";
//
//    public BGWingBoots() {
//        super("BGWingedGreaves", "winged.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
//        this.counter = 3;
//    }
//    public int getPrice() {return 7;}
//
//    public String getUpdatedDescription() {
//        return this.DESCRIPTIONS[0];
//    }
//
//
//    public void setCounter(int setCounter) {
//        this.counter = setCounter;
//        if (this.counter == -2) {
//            usedUp();
//            AbstractDungeon.player.loseRelic("BGWingedGreaves");
//            this.counter = -2;
//        }
//    }
//
//
////    public boolean canSpawn() {
////        return (Settings.isEndless || AbstractDungeon.floorNum <= 40);
////    }
//
//
//    public AbstractRelic makeCopy() {
//        return new BGWingBoots();
//    }
//}
//
//
