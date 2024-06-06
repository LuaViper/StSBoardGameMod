package BoardGame.actions;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.orbs.BGDark;
import BoardGame.screen.OrbSelectScreen;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.AnimateOrbAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.defect.EvokeOrbAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

//TODO: showEvokeOrbCount needs a patch
//TODO: prevent AnimateOrbAction until we know WHICH orb is evoking

public class BGEvokeOrbAction extends AbstractGameAction {
    public boolean allOrbs;
    public String description;
    public AbstractOrb newOrbType;
    public BGEvokeOrbAction(boolean allOrbs, String description, AbstractOrb newOrbType) {
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }
        this.duration = this.startDuration;
        this.allOrbs=allOrbs;
        this.description=description;
        this.newOrbType=newOrbType;
        this.actionType = ActionType.DAMAGE;

    }
    public BGEvokeOrbAction(boolean allOrbs, String description) {
        this(allOrbs,description,null);
    }

    public void update() {
        if (this.duration == this.startDuration) {
            if(this.newOrbType instanceof BGDark && BGChannelAction.playerIsDarkOrbCapped()){
                //find the first dark orb and evoke it instead
                for(int i=0;i<AbstractDungeon.player.orbs.size();i+=1){
                    if(AbstractDungeon.player.orbs.get(i) instanceof BGDark){
                        addToTop((AbstractGameAction) new BGEvokeSpecificOrbAction(i));
                        break;
                    }
                }
            }else {
                OrbSelectScreen.OrbSelectAction ossAction = (target) -> {
                    addToTop((AbstractGameAction) new BGEvokeSpecificOrbAction(target));
                };
                //TODO: are we addingToBot any TARGETSelectScreenActions that should be addedToTop instead? (maybe it doesn't matter?? card queue waits until action queue is empty before playing next card)
                addToTop((AbstractGameAction) new OrbSelectScreenAction(ossAction, description, false));
            }
        }
        this.tickDuration();
    }

    @SpirePatch2(clz = EvokeOrbAction.class, method = "update",
            paramtypez = {})
    public static class EvokeOrbActionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(EvokeOrbAction __instance, int ___orbCount, float ___duration, float ___startDuration, boolean ___isDone) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))
                return SpireReturn.Continue();

            String message="Orb slots are full. Choose an Orb to Evoke.";
            if(___orbCount>1)message="Choose Orbs to Evoke.";   //WE SHOULD NEVER SEE THIS

            if(___duration==___startDuration) {
                for (int i = 0; i < ___orbCount; i += 1) {
                    BoardGame.BoardGame.logger.info("NEW BGEVOKEORBACTION");
                    //first check if we got here by channeling a dark orb
                    // -- if we did, remember that in case we're at the dark orb limit
                    AbstractOrb nextOrb=null;
                    for(AbstractGameAction a : AbstractDungeon.actionManager.actions){
                        if(a instanceof ChannelAction){
                            nextOrb=ReflectionHacks.getPrivate(a,ChannelAction.class,"orbType");
                            break;
                        }
                    }
                    AbstractDungeon.actionManager.addToTop(new BGEvokeOrbAction(false, message, nextOrb));
                }
            }
            ReflectionHacks.RMethod tickduration=ReflectionHacks.privateMethod(AbstractGameAction.class,"tickDuration");
            tickduration.invoke(__instance);
            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = AnimateOrbAction.class, method = "update",
            paramtypez = {})
    public static class ChannelActionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AnimateOrbAction __instance) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                //skip all AnimateOrbActions during BG -- we animate them manually before specificEvokes
                __instance.isDone=true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

}
