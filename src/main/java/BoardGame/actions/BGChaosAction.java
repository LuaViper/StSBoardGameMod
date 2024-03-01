package BoardGame.actions;

import BoardGame.orbs.BGDark;
import BoardGame.orbs.BGFrost;
import BoardGame.orbs.BGLightning;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.util.ArrayList;

public class BGChaosAction extends AbstractGameAction {


    public BGChaosAction() {
        this.actionType = ActionType.SPECIAL;
    }



    public void update() {
        if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2)
            addToTop((AbstractGameAction)new ChannelAction((AbstractOrb)new BGLightning()));
        if(TheDie.monsterRoll==3 || TheDie.monsterRoll==4)
            addToTop((AbstractGameAction)new ChannelAction((AbstractOrb)new BGFrost()));
        if(TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
            addToTop((AbstractGameAction)new ChannelAction((AbstractOrb)new BGDark()));



        this.isDone = true;
    }
}
