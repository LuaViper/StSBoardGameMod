package BoardGame.events;

import BoardGame.dungeons.BGExordium;
import BoardGame.relics.BGDiscardedHallwayEvent;
import BoardGame.relics.BGSsserpentHead;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class BGHallwayEncounter extends AbstractEvent {
    public static final String ID = "BGHallwayEncounter";

    private static final Logger logger = LogManager.getLogger(BGHallwayEncounter.class.getName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGPlaceholderEvent");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public String encounterID="";

    boolean isDone=false;
    public void update(){
        super.update();
        if(!isDone){
            isDone=true;
            if(AbstractDungeon.floorNum==2 && CardCrawlGame.dungeon instanceof BGExordium){
                //make sure ssserpenthead happens only ONCE
                if(AbstractDungeon.player.hasRelic("BGSsserpentHead")){
                    AbstractDungeon.player.loseGold(BGSsserpentHead.GOLD_AMT);
                }
                AbstractRelic r = new BGDiscardedHallwayEvent();
                r.instantObtain(AbstractDungeon.player, AbstractDungeon.player.relics.size(), true);
                r.flash();
                AbstractDungeon.nextRoom=AbstractDungeon.getCurrMapNode();
                AbstractDungeon.nextRoom.room=AbstractDungeon.getCurrRoom();
                ReflectionHacks.setPrivateStatic(AbstractDungeon.class,"fadeTimer",0F);
                AbstractDungeon.nextRoomTransitionStart();
                //TODO LATER: this will probably break something if the player S&Qs immediately
                AbstractDungeon.floorNum-=1;
            }else {
                //TODO NEXT NEXT: "?" that turns into a combat changes after a S&Q
                // (same for merchants)
                (AbstractDungeon.getCurrRoom()).monsters = CardCrawlGame.dungeon.getMonsterForRoomCreation();
                encounterID=AbstractDungeon.monsterList.get(0);
                this.enterCombat();
            }
        }
    }
    protected void buttonEffect(int buttonPressed){}


    public void enterCombat() {
        this.roomEventText.clear();// 92
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMBAT;// 93
        AbstractDungeon.getCurrRoom().monsters.init();// 94
        AbstractRoom.waitTimer = 0.1F;// 95
        AbstractDungeon.player.preBattlePrep();// 96
        this.hasFocus = false;// 97
        this.roomEventText.hide();// 98
    }// 99
}


