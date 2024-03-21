package BoardGame.rewards;

import BoardGame.screen.GridCardSelectScreenCallback;
import BoardGame.util.TextureLoader;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import static basemod.BaseMod.openCustomScreen;

public class TinyHouseUpgrade1Card extends CustomReward {


    private static final Texture ICON = TextureLoader.getTexture("BoardGameResources/images/rewards/upgradeTongs.png");

    public int amount;
    public TinyHouseUpgrade1Card(int amount) {
        super(ICON, "Upgrade a card", TinyHouseUpgrade1CardTypePatch.BOARDGAME_UPGRADEREWARD);
    }

    @Override
    public boolean claimReward() {
        //TODO: localization
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                .getUpgradableCards(), 1, "Select a Card to Upgrade.", true, false, false, false);

        GridCardSelectScreenCallback.CallbackField.callback.set(AbstractDungeon.gridSelectScreen,
                ()-> {
                    for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                        AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        c.upgrade();
                        AbstractDungeon.player.bottledCardUpgradeCheck(c);
                        AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                    }
                });

        return true;
    }

}
