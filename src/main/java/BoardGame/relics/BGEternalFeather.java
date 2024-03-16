//package BoardGame.relics;
//
//import com.megacrit.cardcrawl.helpers.PowerTip;
//import com.megacrit.cardcrawl.relics.AbstractRelic;
//
//
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.rooms.AbstractRoom;
//
//public class BGEternalFeather
//        extends AbstractBGRelic  {
//    public static final String ID = "BGEternal Feather";
//
//    public BGEternalFeather() {
//        super("BGEternal Feather", "eternal_feather.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.MAGICAL);
//    }
//
//
//    public String getUpdatedDescription() {
//        return this.DESCRIPTIONS[0];
//    }
//
//
//    public void onEnterRoom(AbstractRoom room) {
//        if (room instanceof com.megacrit.cardcrawl.rooms.RestRoom) {
//            flash();
//            int amountToGain = 4;
//            AbstractDungeon.player.heal(amountToGain);
//        }
//    }
//
//
//    public AbstractRelic makeCopy() {
//        return new BGEternalFeather();
//    }
//}
//
//
