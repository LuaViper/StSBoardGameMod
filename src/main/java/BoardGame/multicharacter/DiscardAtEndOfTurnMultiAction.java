package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.dungeons.BGTheCity;
import BoardGame.multicharacter.patches.ActionPatches;
import BoardGame.multicharacter.patches.ContextPatches;
import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.unique.RestoreRetainedCardsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

//REMINDER: followUpAction needs its owner set before calling this

public class DiscardAtEndOfTurnMultiAction extends AbstractGameAction {

    private static final float DURATION = Settings.ACTION_DUR_XFAST;

    public DiscardAtEndOfTurnMultiAction() {
        this.duration = DURATION;
    }

    public void update() {
        if (this.duration == DURATION) {
            //discard from bottom of screen to top (changes depending on currentHand)
            //addToTop, so cycle backwards
            int j=BGMultiCharacter.getSubcharacters().size()-1+BGMultiCharacter.handLayoutHelper.currentHand;
            for(int k=j;k>=j-(BGMultiCharacter.getSubcharacters().size()-1);k-=1) {
                AbstractPlayer p=BGMultiCharacter.getSubcharacters().get(k%BGMultiCharacter.getSubcharacters().size());
                ContextPatches.pushPlayerContext(p);

                for (Iterator<AbstractCard> c = AbstractDungeon.player.hand.group.iterator(); c.hasNext(); ) {
                    AbstractCard e = c.next();
                    if (e.retain || e.selfRetain) {
                        AbstractDungeon.player.limbo.addToTop(e);
                        c.remove();
                    }
                }
                addToTop((AbstractGameAction) new RestoreRetainedCardsAction(AbstractDungeon.player.limbo));
                if (!AbstractDungeon.player.hasRelic("Runic Pyramid") && !AbstractDungeon.player.hasPower("Equilibrium")) {
                    int tempSize = AbstractDungeon.player.hand.size();
                    for (int i = 0; i < tempSize; i++) {
                        DiscardAction action = new DiscardAction( AbstractDungeon.player, null, AbstractDungeon.player.hand
                                .size(), true, true);
                        Field.instant.set(action,true);
                        addToTop(action);
                    }
                }
                ArrayList<AbstractCard> cards = (ArrayList<AbstractCard>) AbstractDungeon.player.hand.group.clone();
                Collections.shuffle(cards);
                for (AbstractCard abstractCard : cards)
                    abstractCard.triggerOnEndOfPlayerTurn();

                ContextPatches.popPlayerContext();
            }
            this.isDone = true;
        }
    }


    @SpirePatch(
            clz= DiscardAction.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<Boolean> instant = new SpireField<>(()->false);
    }

    @SpirePatch2(clz = DiscardAction.class, method = "update")
    public static class InstantDiscardActionPatch {
        @SpirePostfixPatch
        public static void Foo(DiscardAction __instance) {
            if(Field.instant.get(__instance)){
                __instance.isDone=true;
            }
        }
    }

}
