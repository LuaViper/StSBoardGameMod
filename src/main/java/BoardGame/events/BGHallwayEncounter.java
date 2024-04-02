package BoardGame.events;

import BoardGame.relics.BGDiscardedHallwayEvent;
import BoardGame.relics.BGDiscardedOldCoin;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.events.exordium.DeadAdventurer;
import com.megacrit.cardcrawl.events.exordium.Mushrooms;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.vfx.MapCircleEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class BGHallwayEncounter extends AbstractImageEvent {
    public static final String ID = "BGHallwayEncounter";

    private static final Logger logger = LogManager.getLogger(BGHallwayEncounter.class.getName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGPlaceholderEvent");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public String encounterID="";

    public BGHallwayEncounter() {
        super(ID,"DNT: Hallway Encounter placeholder","");
    }

    boolean isDone=false;
    public void update(){
        super.update();
        if(!isDone){
            isDone=true;
            if(AbstractDungeon.floorNum==2){
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
                AbstractDungeon.floorNum-=1;
                encounterID=AbstractDungeon.monsterList.get(0);

                MapRoomNode cur = AbstractDungeon.currMapNode;
                cur.taken=true;
                MapRoomNode node = new MapRoomNode(cur.x, cur.y);
                node.room = new MonsterRoom();
                ArrayList<MapEdge> curEdges = cur.getEdges();
                Iterator var8 = curEdges.iterator();
                while(var8.hasNext()) {
                    MapEdge edge = (MapEdge)var8.next();
                    node.addEdge(edge);
                }
                AbstractDungeon.nextRoom = node;
                ReflectionHacks.setPrivateStatic(AbstractDungeon.class,"fadeTimer",0F);
                AbstractDungeon.nextRoomTransitionStart();
                node.taken=true;
//                AbstractDungeon.topLevelEffects.add(new MapCircleEffect(node.x *
//                        (float)ReflectionHacks.getPrivateStatic(MapRoomNode.class,"SPACING_X") +
//                        MapRoomNode.OFFSET_X + node.offsetX, node.y * Settings.MAP_DST_Y +
//                        (float)ReflectionHacks.getPrivateStatic(MapRoomNode.class,"OFFSET_Y") +
//                        DungeonMapScreen.offsetY + node.offsetY,
//                        (float)ReflectionHacks.getPrivate(node,MapRoomNode.class,"angle")));
            }
        }
    }
    protected void buttonEffect(int buttonPressed){}


    public void enterCombat() {
        //this.roomEventText.clear();
        //AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMBAT;
        //AbstractDungeon.getCurrRoom().monsters.init();
        //AbstractRoom.waitTimer = 0.1F;
        //AbstractDungeon.player.preBattlePrep();
        //this.hasFocus = false;
        //this.roomEventText.hide();
    }
}


