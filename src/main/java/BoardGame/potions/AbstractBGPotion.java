package BoardGame.potions;

import BoardGame.events.BGNloth;
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
    public int getPrice() {return 9999;}


    //TODO: also disable clickable relics somehow (good luck, we need clickable relics for some events)
    @SpirePatch2(clz = PotionPopUp.class, method = "open",
            paramtypez = {int.class, AbstractPotion.class})
    public static class LockPotionsDuringEventsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix() {
            if (AbstractDungeon.getCurrRoom() != null) {
                if (AbstractDungeon.getCurrRoom().event instanceof BGNloth) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
}
