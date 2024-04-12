package BoardGame.savables;

//TODO NEXT: when a card reward is generated at end of combat, the top three cards of reward deck are moved to bottom of deck,
// then the game is saved.  when the game is loaded, those three cards remain on the bottom of the deck, and a new (different) card reward
// is generated from the new top three cards, this time without saving.
// find in files CardRewardScreen.rewardMetrics ("SKIP")
// also note that events that do not use CardReward DO preserve card order correctly


import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.*;
import BoardGame.dungeons.AbstractBGDungeon;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static BoardGame.savables.DeckInfo.DeckType.*;

public class DeckInfo implements CustomSavable<HashMap<DeckInfo.DeckType,ArrayList<String>>> {
    public DeckInfo(){}
    public static boolean cardAbbreviationsAreInitialized=false;


    public enum DeckType {
        RED_REWARDS,RED_RARES,GREEN_REWARDS,GREEN_RARES,BLUE_REWARDS,BLUE_RARES,PURPLE_REWARDS,PURPLE_RARES,COLORLESS,CURSES
    }

    public static ArrayList<String> getCardIDs(ArrayList<AbstractCard> deck){
        ArrayList<String> result=new ArrayList<>();
        for(AbstractCard c : deck){
            result.add(c.cardID);
        }
        return result;
    }

    public static void populateDeck(ArrayList<AbstractCard> destination, ArrayList<String> rewardDeck){
        destination.clear();
        for(String id : rewardDeck){
            destination.add(CardLibrary.getCopy(id));
        }
    }

    @Override
    public HashMap<DeckType, ArrayList<String>> onSave() {
        if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)){
            return null;
        }
        BoardGame.BoardGame.logger.info("Saving BG reward decks");
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
        return decks;
    }


    @Override
    public void onLoad(HashMap<DeckType,ArrayList<String>> decks) {
        if(decks==null){
            BoardGame.BoardGame.logger.info("Saved decks is null (not a BG save?)");
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
        populateDeck(AbstractBGDungeon.physicalRewardDecks.get(0).group,decks.get(RED_REWARDS));
        populateDeck(AbstractBGDungeon.physicalRareRewardDecks.get(0).group,decks.get(RED_RARES));
        populateDeck(AbstractBGDungeon.physicalRewardDecks.get(1).group,decks.get(GREEN_REWARDS));
        populateDeck(AbstractBGDungeon.physicalRareRewardDecks.get(1).group,decks.get(GREEN_RARES));
        populateDeck(AbstractBGDungeon.physicalRewardDecks.get(2).group,decks.get(BLUE_REWARDS));
        populateDeck(AbstractBGDungeon.physicalRareRewardDecks.get(2).group,decks.get(BLUE_RARES));
        populateDeck(AbstractBGDungeon.physicalRewardDecks.get(3).group,decks.get(PURPLE_REWARDS));
        populateDeck(AbstractBGDungeon.physicalRareRewardDecks.get(3).group,decks.get(PURPLE_RARES));
        populateDeck(AbstractBGDungeon.colorlessRewardDeck.group,decks.get(COLORLESS));
        populateDeck(AbstractBGDungeon.cursesRewardDeck.group,decks.get(CURSES));

        int whoAmI=0;
        if(AbstractDungeon.player instanceof BGIronclad) whoAmI=0;
        else if(AbstractDungeon.player instanceof BGSilent) whoAmI=1;
        else if(AbstractDungeon.player instanceof BGDefect) whoAmI=2;
        else if(AbstractDungeon.player instanceof BGWatcher) whoAmI=3;
        AbstractBGDungeon.rewardDeck = AbstractBGDungeon.physicalRewardDecks.get(whoAmI);
        AbstractBGDungeon.rareRewardDeck = AbstractBGDungeon.physicalRareRewardDecks.get(whoAmI);

        AbstractBGDungeon.initializedCardPools=true;
    }
}
