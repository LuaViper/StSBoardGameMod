package BoardGame.potions;

import BoardGame.events.BGNloth;
import BoardGame.events.BGTheJoust;
import BoardGame.events.BGWeMeetAgain;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameTips;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;

public abstract class AbstractBGPotion {

    public AbstractBGPotion(){
        super();
    }
    //public int getPrice() {return 9999;}  //not actually used.  potions currently extend AbstractPotion, not AbstractBGPotion
                                                                //(getPrice is a vanilla method)

    //TODO: the player should be allowed to use Entropic Brew during the event introduction, up until the player clicks lose-a-potion and the buttons change to potion selection
    @SpirePatch2(clz = PotionPopUp.class, method = "open", paramtypez = {int.class, AbstractPotion.class})
    public static class LockPotionsDuringEventsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix() {
            if (AbstractDungeon.getCurrRoom() != null) {
                if (AbstractDungeon.getCurrRoom().event instanceof BGNloth || AbstractDungeon.getCurrRoom().event instanceof BGTheJoust) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
}
