package BoardGame.actions;

import BoardGame.orbs.BGDark;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class BGChannelAction extends AbstractGameAction {

    public static int MAX_DARK=3;
    private AbstractOrb orbType;
    private boolean autoEvoke;

    public BGChannelAction(AbstractOrb newOrbType) {
        this(newOrbType, true);
    }

    public BGChannelAction(AbstractOrb newOrbType, boolean autoEvoke) {
        this.autoEvoke = false;
        this.duration = Settings.ACTION_DUR_FAST;
        this.orbType = newOrbType;
        this.autoEvoke = autoEvoke;
    }

    public static boolean playerIsDarkOrbCapped(){
        int darkCount=0;
        boolean anyEmptySlots=false;
        for(AbstractOrb o : AbstractDungeon.player.orbs){
            if(o instanceof BGDark){
                darkCount+=1;
            }

        }
        return(darkCount>=MAX_DARK);
    }
    public static boolean playerHasEmptyOrbSlots(){
        for(AbstractOrb o : AbstractDungeon.player.orbs) {
            if (o instanceof EmptyOrbSlot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update() {
        if(!(orbType instanceof BGDark)){
            addToTop(new ChannelAction(orbType,autoEvoke));
        }else{
            if(playerIsDarkOrbCapped() && playerHasEmptyOrbSlots()){
                //TODO: localization
                //TODO: only display message once per game, maybe
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, "I can't have more than 3 Dark Orbs.", true));
            }else{
                addToTop(new ChannelAction(orbType,autoEvoke));
            }
        }
        this.isDone=true;
    }
}
