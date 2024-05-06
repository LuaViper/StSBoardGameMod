package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BGMindBloom
        extends MindBloom //game is hardcoded to check for MindBloom when completing event
{
    public static final String ID = "BGMindBloom";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGMindBloom");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private static final String DIALOG_3 = DESCRIPTIONS[2];

    private CurScreen screen = CurScreen.INTRO;

    private boolean awake=false;
    private enum CurScreen {
        INTRO, FIGHT, LEAVE;
    }

    public BGMindBloom() {
        super();

        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[0]);    //war
        this.imageEventText.setDialogOption(OPTIONS[3]);    //awake
        this.imageEventText.setDialogOption(OPTIONS[1]);    //rich
        this.imageEventText.setDialogOption(OPTIONS[2]);    //healthy


    }

    public void update() {
        super.update();
        if(awake) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i += 1) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                    c.upgrade();
                    //TODO: if there's more than one card, spread them out
                    AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                }
                //TODO: log it
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                awake = false;
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }
    }

    protected void buttonEffect(int buttonPressed) {
    ArrayList<String> list;
    int effectCount;
    List<String> upgradedCards, obtainedRelic;
    Doubt doubt;
    switch (this.screen) {
        case INTRO:
            switch (buttonPressed) {
                case 0: //war
                    this.imageEventText.updateBodyText(DIALOG_2);
                    this.screen = CurScreen.FIGHT;
                    logMetric("MindBloom", "Fight");
                    CardCrawlGame.music.playTempBgmInstantly("MINDBLOOM", true);
                    list = new ArrayList<>();
                    list.add("The Guardian");
                    list.add("Hexaghost");
                    list.add("Slime Boss");
                    Collections.shuffle(list, new Random(AbstractDungeon.miscRng.randomLong()));
                    (AbstractDungeon.getCurrRoom()).monsters = MonsterHelper.getEncounter(list.get(0));
                    (AbstractDungeon.getCurrRoom()).rewards.clear();
                    AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);
                    enterCombatFromImage();
                    AbstractDungeon.lastCombatMetricKey = "Mind Bloom Boss Battle";
                    break;
                case 1: //awake
                    awake=true;
                    this.imageEventText.updateBodyText(DIALOG_3);
                    this.screen = CurScreen.LEAVE;
                    effectCount = 0;

                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                            .getUpgradableCards(), 2, "Choose 2 cards to upgrade.", false, false, false, false);

                    AbstractDungeon.player.damage(new DamageInfo(null, 3, DamageInfo.DamageType.HP_LOSS));


                    this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                    break;
                case 2: //rich
                    this.imageEventText.updateBodyText(DIALOG_2);
                    this.screen = CurScreen.LEAVE;
                    logMetric("MindBloom", "Gold", null, null, null, null, null, null, null, 0, 0, 0, 0, 5, 0);
                    AbstractDungeon.effectList.add(new RainingGoldEffect(5*20));
                    AbstractDungeon.player.gainGold(5);

                    this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                    break;

                case 3: //healthy
                    this.imageEventText.updateBodyText(DIALOG_2);
                    this.screen = CurScreen.LEAVE;
                    AbstractCard curse = AbstractBGDungeon.DrawFromCursesRewardDeck();
                    logMetricObtainCardAndHeal("MindBloom", "Heal", (AbstractCard) curse, AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth);


                    AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) curse, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                    this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                    break;
            }


            this.imageEventText.clearRemainingOptions();
            return;
        case LEAVE:
            openMap();
            return;
    }
    openMap();
}
}


