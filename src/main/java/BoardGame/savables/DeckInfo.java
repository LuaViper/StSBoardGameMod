package BoardGame.savables;

//TODO NEXT: when a card reward is generated at end of combat, the top three cards of reward deck are moved to bottom of deck,
// then the game is saved.  when the game is loaded, those three cards remain on the bottom of the deck, and a new (different) card reward
// is generated from the new top three cards, this time without saving.
// find in files CardRewardScreen.rewardMetrics ("SKIP")
// also note that events that do not use CardReward DO preserve card order correctly


import BoardGame.BoardGame;
import BoardGame.actions.BGCopyCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGGoldenTicket;
import BoardGame.characters.*;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.potions.PotionHelperPatch;
import BoardGame.relics.AbstractBGRelic;
import basemod.AutoAdd;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.ShowCardAndPoofAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.clapper.util.classutil.ClassInfo;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import static BoardGame.savables.DeckInfo.DeckType.*;

public class DeckInfo implements CustomSavable<HashMap<DeckInfo.DeckType,ArrayList<String>>> {
    public DeckInfo(){}

    public static HashMap<DeckType,ArrayList<String>> deckInfoBeforeCardReward=null;
    public static boolean cardAbbreviationsAreInitialized=false;


    public enum DeckType {
        RED_REWARDS,RED_RARES,GREEN_REWARDS,GREEN_RARES,BLUE_REWARDS,BLUE_RARES,PURPLE_REWARDS,PURPLE_RARES,COLORLESS,CURSES,
        POTIONS,RELICS,BOSS_RELICS
    }

    public static ArrayList<String> getCardIDs(ArrayList<AbstractCard> deck){
        ArrayList<String> result=new ArrayList<>();
        for(AbstractCard c : deck){
            result.add(c.cardID);
        }
        return result;
    }



    public static ArrayList<String> getRelicIDs(ArrayList<AbstractRelic> deck){
        ArrayList<String> result=new ArrayList<>();
        for(AbstractRelic c : deck){
            result.add(c.relicId);
        }
        return result;
    }

    public static void populateCardDeck(ArrayList<AbstractCard> destination, ArrayList<String> rewardDeck){
        destination.clear();
        for(String id : rewardDeck){
            destination.add(CardLibrary.getCopy(id));
        }
    }

    public static void populateRelicDeck(ArrayList<AbstractRelic> destination, ArrayList<String> rewardDeck){
        destination.clear();
        for(String id : rewardDeck){
            destination.add(RelicLibrary.getRelic(id));
        }
    }


    @Override
    public HashMap<DeckType, ArrayList<String>> onSave() {
        if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)){
            return null;
        }
        BoardGame.logger.info("Saving BG REWARD DECKS");
        HashMap<DeckType,ArrayList<String>> decks;
        if(deckInfoBeforeCardReward==null) {
            decks = getCurrentDeckInfo();
            BoardGame.logger.info("Save deck info as it is currently.");
        }else{
            decks = deckInfoBeforeCardReward;
            deckInfoBeforeCardReward=null;
            BoardGame.logger.info("Save previously stored deck info before drawing card reward.");
        }
        ArrayList<String>red=decks.get(RED_REWARDS);
        BoardGame.logger.info("Top cards: "+red.get(red.size()-1)+" "+red.get(red.size()-2)+" "+red.get(red.size()-3));
        BoardGame.logger.info("Bottom cards: "+red.get(0)+" "+red.get(1)+" "+red.get(2));

        return decks;
    }

    public static HashMap<DeckType,ArrayList<String>> getCurrentDeckInfo(){
        HashMap<DeckType,ArrayList<String>> decks=new HashMap<>();
        decks.put(RED_REWARDS,getCardIDs(AbstractBGDungeon.physicalRewardDecks.get(0).group));
        decks.put(RED_RARES,getCardIDs(AbstractBGDungeon.physicalRareRewardDecks.get(0).group));
        decks.put(GREEN_REWARDS,getCardIDs(AbstractBGDungeon.physicalRewardDecks.get(1).group));
        decks.put(GREEN_RARES,getCardIDs(AbstractBGDungeon.physicalRareRewardDecks.get(1).group));
        decks.put(BLUE_REWARDS,getCardIDs(AbstractBGDungeon.physicalRewardDecks.get(2).group));
        decks.put(BLUE_RARES,getCardIDs(AbstractBGDungeon.physicalRareRewardDecks.get(2).group));
        decks.put(PURPLE_REWARDS,getCardIDs(AbstractBGDungeon.physicalRewardDecks.get(3).group));
        decks.put(PURPLE_RARES,getCardIDs(AbstractBGDungeon.physicalRareRewardDecks.get(3).group));
        decks.put(COLORLESS,getCardIDs(AbstractBGDungeon.colorlessRewardDeck.group));
        decks.put(CURSES,getCardIDs(AbstractBGDungeon.cursesRewardDeck.group));
        decks.put(POTIONS,PotionHelperPatch.potionDeck);
        decks.put(RELICS,getRelicIDs(AbstractBGRelic.relicDeck));
        decks.put(BOSS_RELICS,getRelicIDs(AbstractBGRelic.bossRelicDeck));

        return decks;
    }

    @Override
    public void onLoad(HashMap<DeckType,ArrayList<String>> decks) {
        if(decks==null){
            BoardGame.logger.info("Saved decks is null (not a BG save?)");
            return;
        }
        AbstractBGDungeon.physicalRewardDecks=new ArrayList<>();
        AbstractBGDungeon.physicalRareRewardDecks=new ArrayList<>();
        for(int i=0;i<4;i+=1){
            AbstractBGDungeon.physicalRewardDecks.add(new CardGroup(CardGroup.CardGroupType.CARD_POOL));
            AbstractBGDungeon.physicalRareRewardDecks.add(new CardGroup(CardGroup.CardGroupType.CARD_POOL));
        }
        AbstractBGDungeon.colorlessRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractBGDungeon.cursesRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        populateCardDeck(AbstractBGDungeon.physicalRewardDecks.get(0).group,decks.get(RED_REWARDS));
        populateCardDeck(AbstractBGDungeon.physicalRareRewardDecks.get(0).group,decks.get(RED_RARES));
        populateCardDeck(AbstractBGDungeon.physicalRewardDecks.get(1).group,decks.get(GREEN_REWARDS));
        populateCardDeck(AbstractBGDungeon.physicalRareRewardDecks.get(1).group,decks.get(GREEN_RARES));
        populateCardDeck(AbstractBGDungeon.physicalRewardDecks.get(2).group,decks.get(BLUE_REWARDS));
        populateCardDeck(AbstractBGDungeon.physicalRareRewardDecks.get(2).group,decks.get(BLUE_RARES));
        populateCardDeck(AbstractBGDungeon.physicalRewardDecks.get(3).group,decks.get(PURPLE_REWARDS));
        populateCardDeck(AbstractBGDungeon.physicalRareRewardDecks.get(3).group,decks.get(PURPLE_RARES));
        populateCardDeck(AbstractBGDungeon.colorlessRewardDeck.group,decks.get(COLORLESS));
        populateCardDeck(AbstractBGDungeon.cursesRewardDeck.group,decks.get(CURSES));
        PotionHelperPatch.potionDeck=decks.get(POTIONS);
        populateRelicDeck(AbstractBGRelic.relicDeck,decks.get(RELICS));
        populateRelicDeck(AbstractBGRelic.bossRelicDeck,decks.get(BOSS_RELICS));


        int whoAmI=0;
        if(AbstractDungeon.player instanceof BGIronclad) whoAmI=0;
        else if(AbstractDungeon.player instanceof BGSilent) whoAmI=1;
        else if(AbstractDungeon.player instanceof BGDefect) whoAmI=2;
        else if(AbstractDungeon.player instanceof BGWatcher) whoAmI=3;
        AbstractBGDungeon.rewardDeck = AbstractBGDungeon.physicalRewardDecks.get(whoAmI);
        AbstractBGDungeon.rareRewardDeck = AbstractBGDungeon.physicalRareRewardDecks.get(whoAmI);

        AbstractBGDungeon.initializedCardPools=true;
    }


    @SpirePatch2(clz=AbstractRoom.class, method="update")
    public static class PreserveDeckBeforeCardRewardPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Foo() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                //The game saves AFTER card rewards are generated, so preserve the current reward deck state.
                deckInfoBeforeCardReward = getCurrentDeckInfo();
                //Preserved reward deck state is automatically saved then cleared upon saving.
                //TODO: or maybe it isn't
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "cardBlizzRandomizer");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class UnpreserveDeckPatch {
        @SpirePostfixPatch
        public static void update() {
            if(true || CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                deckInfoBeforeCardReward=null;
            }
        }
    }

}
