package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.multicharacter.patches.ContextPatches;
import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class DrawCardMultiAction_PostShuffleFollowUp extends AbstractGameAction {

    private ArrayList<Integer> amountLeftToDraw;
    private static final Logger logger = LogManager.getLogger(DrawCardAction.class.getName());
    public static ArrayList<AbstractCard> drawnCards = new ArrayList();
    private boolean clearDrawHistory;
    private AbstractGameAction followUpAction;

    public DrawCardMultiAction_PostShuffleFollowUp(ArrayList<Integer>amountLeftToDraw) {
        this.clearDrawHistory = true;
        this.followUpAction = null;

        this.setValues(AbstractDungeon.player, AbstractDungeon.player, 0);
        this.actionType = AbstractGameAction.ActionType.DRAW;
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FASTER;
        }

        this.amountLeftToDraw=amountLeftToDraw;
    }
    public void update() {
        int endActionCounter = 0;
        if (ContextPatches.originalBGMultiCharacter==null) {
            BoardGame.logger.info("WARNING: DrawCardMultiAction_PostShuffleFollowUp was updated while ContextPatches.originalBGMultiCharacter==null, time to panic!");
            this.endActionWithFollowUp();
        }else if (false && AbstractDungeon.player.hasPower("No Draw")) {
            AbstractDungeon.player.getPower("No Draw").flash();
            this.endActionWithFollowUp();
        } else if (false && this.amount <= 0) {
            this.endActionWithFollowUp();
        }else {
            this.duration -= Gdx.graphics.getDeltaTime();
            if(this.duration<0.0F) {
                for (int i = 0; i< MultiCharacter.getSubcharacters().size(); i+=1) {
                    AbstractPlayer p = MultiCharacter.getSubcharacters().get(i);
                    int deckSize = p.drawPile.size();
                    int discardSize = p.discardPile.size();
                    if (!SoulGroup.isActive()) {
                        if (deckSize + discardSize == 0) {
                            endActionCounter += 1;
                        } else if (false && p.hand.size() == BaseMod.MAX_HAND_SIZE) {
                            p.createHandIsFullDialog();
                            endActionCounter += 1;
                        } else if (deckSize == 0) {
                            endActionCounter += 1;
                        }else{
                            //if (this.amount != 0 && this.duration < 0.0F) {
                            if (this.amountLeftToDraw.get(i) > 0) {
                                if (!p.drawPile.isEmpty()) {
                                    drawnCards.add(p.drawPile.getTopCard());
                                    p.draw();
                                    p.hand.refreshHandLayout();
                                    this.amountLeftToDraw.set(i,this.amountLeftToDraw.get(i)-1);
                                } else {
                                    logger.warn("Player attempted to draw from an empty drawpile mid-DrawAction?MASTER DECK: " + p.masterDeck.getCardNames());
                                    this.endActionWithFollowUp();
                                }
                            }
                            if(this.amountLeftToDraw.get(i)<=0){
                                endActionCounter += 1;
                            }
                        }
                    }
                }
                if (Settings.FAST_MODE) {
                    this.duration = Settings.ACTION_DUR_XFAST;
                } else {
                    this.duration = Settings.ACTION_DUR_FASTER;
                }
            }


            if(endActionCounter>= MultiCharacter.getSubcharacters().size()){
                this.endActionWithFollowUp();
            }
        }
    }

    private void endActionWithFollowUp() {
        this.isDone = true;
        if (this.followUpAction != null) {
            this.addToTop(this.followUpAction);
        }
    }
}
