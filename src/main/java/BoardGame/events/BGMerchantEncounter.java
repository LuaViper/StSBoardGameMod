package BoardGame.events;

import BoardGame.dungeons.BGExordium;
import BoardGame.relics.BGDiscardedMerchantEvent;
import BoardGame.relics.BGSsserpentHead;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class BGMerchantEncounter extends AbstractEvent {
    public static final String ID = "BGMerchantEncounter";

    private static final Logger logger = LogManager.getLogger(BGHallwayEncounter.class.getName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGPlaceholderEvent");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public String encounterID="";

    public Merchant merchant;

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
                if (!AbstractDungeon.id.equals("TheEnding")) {// 38
                    AbstractDungeon.getCurrRoom().playBGM("SHOP");// 39
                }
                AbstractDungeon.overlayMenu.proceedButton.setLabel(ShopRoom.TEXT[0]);// 41
                this.setMerchant(new Merchant());// 42
                //hide text bar
                this.panelAlpha=0.0f;
                this.hideAlpha=true;
                this.roomEventText.clear();
                this.roomEventText.hide();
                //enable proceedbutton
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                this.hasFocus=false;
            }
        }else{
            if(AbstractDungeon.floorNum==2 && CardCrawlGame.dungeon instanceof BGExordium){
                //do nothing
            }else{
                super.update();// 55
                if (this.merchant != null) {// 56
                    this.merchant.update();// 57
                }

                this.updatePurge();// 59
            }
        }
    }
    protected void buttonEffect(int buttonPressed){}


    public void setMerchant(Merchant merc) {
        this.merchant = merc;// 33
    }// 34


    private void updatePurge() {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {// 63
            ShopScreen.purgeCard();// 64

            for(AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {// 65
                CardCrawlGame.metricData.addPurgedItem(card.getMetricID());// 66
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));// 67
                AbstractDungeon.player.masterDeck.removeCard(card);// 69
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();// 71
            AbstractDungeon.shopScreen.purgeAvailable = false;// 72
        }

    }// 74

    public void render(SpriteBatch sb) {
        if (this.merchant != null) {// 81
            this.merchant.render(sb);// 82
        }

        super.render(sb);// 85
        AbstractDungeon.getCurrRoom().renderTips(sb);// 86
    }// 87

    public void dispose() {
        super.dispose();// 91
        if (this.merchant != null) {// 92
            this.merchant.dispose();// 93
            this.merchant = null;// 94
        }

    }
}


