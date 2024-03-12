package BoardGame.events;

import BoardGame.potions.PlaceholderPotion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;

//TODO: Colosseum encounters need to draw from the physical decks
// additionally, since vanilla Colosseum isn't random, this event doesn't even cycle through the monster list
public class BGColosseum extends Colosseum {    //game is hardcoded to check for instanceof Colosseum when clicking Proceed button
    public static final String ID = "BGColosseum";

    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGColosseum");

    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    public String encounterID="";

    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO, FIGHT, LEAVE, POST_COMBAT;
    }

    public BGColosseum() {
        super();
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1] + DESCRIPTIONS[2]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.setDialogOption(OPTIONS[2]);
                        this.screen = CurScreen.FIGHT;
                        break;
                }
                return;
            case FIGHT:
                this.screen = CurScreen.LEAVE;
                switch (buttonPressed) {
                    case 0:
                        (AbstractDungeon.getCurrRoom()).monsters = CardCrawlGame.dungeon.getMonsterForRoomCreation();
                        encounterID=AbstractDungeon.monsterList.get(0);
                        AbstractDungeon.monsterList.remove(0);
                        (AbstractDungeon.getCurrRoom()).rewards.clear();
                        //TODO: for reasons that are still unclear, addPotionToRewards is called automatically by AbstractRoom under certain conditions at intellij line 369
                        AbstractDungeon.getCurrRoom().addGoldToRewards(0);
                        enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = "Standard Encounter";
                        break;
                    case 1:
                        (AbstractDungeon.getCurrRoom()).monsters = CardCrawlGame.dungeon.getEliteMonsterForRoomCreation();
                        encounterID=AbstractDungeon.eliteMonsterList.get(0);
                        AbstractDungeon.eliteMonsterList.remove(0);
                        (AbstractDungeon.getCurrRoom()).rewards.clear();

                        (AbstractDungeon.getCurrRoom()).eliteTrigger = true;
                        AbstractDungeon.getCurrRoom().addGoldToRewards(0);
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);

                        enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = "Elite Encounter";
                        break;
                }
                return;
            case LEAVE:
                openMap();
                return;
        }
        openMap();
    }

    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric("Colosseum", actionTaken);
    }

    public void reopen() {
        //do nothing; override Colosseum
    }
//        if (this.screen != CurScreen.LEAVE) {
//            AbstractDungeon.resetPlayer();
//            AbstractDungeon.player.drawX = Settings.WIDTH * 0.25F;
//            AbstractDungeon.player.preBattlePrep();
//            enterImageFromCombat();
//            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
//            this.imageEventText.updateDialogOption(0, OPTIONS[2]);
//            this.imageEventText.setDialogOption(OPTIONS[3]);
//        }
//    }
}

