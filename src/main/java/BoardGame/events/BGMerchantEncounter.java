package BoardGame.events;

import BoardGame.dungeons.BGExordium;
import BoardGame.relics.BGDiscardedMerchantEvent;
import BoardGame.relics.BGSsserpentHead;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class BGMerchantEncounter extends AbstractImageEvent {
    public static final String ID = "BGMerchantEncounter";

    private static final Logger logger = LogManager.getLogger(BGHallwayEncounter.class.getName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGPlaceholderEvent");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public String encounterID="";

    public BGMerchantEncounter() {
        super(ID,"DNT: Merchant Encounter placeholder","");
    }

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
                AbstractRelic r = new BGDiscardedMerchantEvent();
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

                MapRoomNode cur = AbstractDungeon.currMapNode;
                cur.taken=true;
                MapRoomNode node = new MapRoomNode(cur.x, cur.y);
                node.room = new ShopRoom();
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
            }
        }
    }
    protected void buttonEffect(int buttonPressed){}



}


