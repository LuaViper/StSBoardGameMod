//package BoardGame.relics;
//
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.characters.AbstractPlayer;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.helpers.PotionHelper;
//import com.megacrit.cardcrawl.helpers.PowerTip;
//import com.megacrit.cardcrawl.relics.AbstractRelic;
//import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
//import com.megacrit.cardcrawl.ui.campfire.SmithOption;
//import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
//import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Random;
//
//public class BGUpgrade1Card extends AbstractBGRelic  {
//    public static final String ID = "BGUpgrade1Card";
//
//
//
//    public BGUpgrade1Card() {
//        super("BGUpgrade1Card", "tinyHouse.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
//    }
//
//    public String getUpdatedDescription() {
//        return this.DESCRIPTIONS[0];
//    }
//
//    public void onEquip() {
//        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
//        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
//            if (c.canUpgrade())
//                upgradableCards.add(c);
//        }
//        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));
//        if (!upgradableCards.isEmpty())
//            if (upgradableCards.size() == 1) {
//                ((AbstractCard)upgradableCards.get(0)).upgrade();
//                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
//                AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards
//                        .get(0)).makeStatEquivalentCopy()));
//                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
//            } else {
//                //TODO: open card select screen
//                ((AbstractCard)upgradableCards.get(0)).upgrade();
//                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
//                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));
//                AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards
//
//                        .get(0)).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
//                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
//            }
//        this.usedUp=true;
//    }
//
//    public AbstractRelic makeCopy() {
//        return new BGUpgrade1Card();
//    }
//}
//
