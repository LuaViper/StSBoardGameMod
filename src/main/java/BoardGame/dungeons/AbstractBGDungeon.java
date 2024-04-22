package BoardGame.dungeons;

import BoardGame.cards.*;
import BoardGame.cards.BGBlue.BGClaw;
import BoardGame.cards.BGBlue.BGClaw2;
import BoardGame.cards.BGCurse.*;
import BoardGame.characters.*;
import BoardGame.events.BGColosseum;
import BoardGame.events.BGDeadAdventurer;
import BoardGame.events.BGHallwayEncounter;
import BoardGame.monsters.MonsterGroupRewardsList;
import BoardGame.monsters.bgbeyond.*;
import BoardGame.monsters.bgcity.*;
import BoardGame.monsters.bgending.BGCorruptHeart;
import BoardGame.monsters.bgexordium.*;
import BoardGame.multicharacter.MultiCharacter;
import BoardGame.ui.EntropicBrewPotionButton;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;
import java.util.Collections;

//TODO: decks are not getting shuffled upon starting a new game (initializedCardPools never gets set back to true) (can we just change it to non-static??)

public abstract class AbstractBGDungeon extends AbstractDungeon {
    public static boolean initializedCardPools=false;
    public static CardGroup rewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    public static CardGroup rareRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    public static CardGroup cursesRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    public static CardGroup colorlessRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);


    public static ArrayList<CardGroup> physicalRewardDecks=new ArrayList<>();
    public static ArrayList<CardGroup> physicalRareRewardDecks=new ArrayList<>();


    public static boolean forceRareRewards=false;
    public static int forceSpecificColor=-999;

    public AbstractBGDungeon(String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
        super(name, levelId, p, newSpecialOneTimeEventList);

        forceRareRewards=false;
        forceSpecificColor=-999;
        //Settings.isFinalActAvailable=false;
    }

    public AbstractBGDungeon(String name, AbstractPlayer p, SaveFile saveFile) {
        super(name, p, saveFile);
    }



    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon",
            paramtypez={String.class, AbstractPlayer.class})
    public static class getDungeonPatch {
        @SpirePrefixPatch
        public static SpireReturn<AbstractDungeon> Prefix(@ByRef String[] key, AbstractPlayer p) {
            if(p instanceof AbstractBGPlayer) {
                if (key[0].equals("BoardGameSetupDungeon")){
//                    //logger.info("BOARDGAME SETUPDUNGEON DETECTED");
//                    ArrayList<String>emptyList = new ArrayList<>();
//                    return SpireReturn.Return((AbstractDungeon)new BGSetupDungeon(p, emptyList));
                }else if (key[0].equals("Exordium")) {
                    //logger.info("BOARDGAME EXORDIUM DETECTED");
                    //do not change the key itself.  game is hard-coded in several places to check for "Exordium"
                    ArrayList<String>emptyList = new ArrayList<>();
                    return SpireReturn.Return((AbstractDungeon)new BGExordium(p, emptyList));
                }else if(key[0].equals("TheCity")){
                    ArrayList<String>emptyList = new ArrayList<>();
                    return SpireReturn.Return((AbstractDungeon)new BGTheCity(p, emptyList));
                }else if(key[0].equals("TheBeyond")){
                    ArrayList<String>emptyList = new ArrayList<>();
                    return SpireReturn.Return((AbstractDungeon)new BGTheBeyond(p, emptyList));
                }else if(key[0].equals("TheEnding")){
                    ArrayList<String>emptyList = new ArrayList<>();
                    return SpireReturn.Return((AbstractDungeon)new BGTheEnding(p, emptyList));

                }
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon",
            paramtypez={String.class, AbstractPlayer.class, SaveFile.class})
    public static class getDungeonPatch2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractDungeon> Prefix(@ByRef String[] key, AbstractPlayer p, SaveFile saveFile) {
            //logger.info("SAVEFILE CHECK GOES HERE "+key[0]+" "+p);
            if(p instanceof AbstractBGPlayer) {
                if (key[0].equals("BoardGameSetupDungeon")) {
//                    return SpireReturn.Return((AbstractDungeon)new BGSetupDungeon(p, saveFile));
                }else if (key[0].equals("Exordium")) {
                    //logger.info("BOARDGAME EXORDIUM DETECTED (savefile)");
                    //do not change key.  game is hard-coded to check for "Exordium"
                    return SpireReturn.Return((AbstractDungeon)new BGExordium(p, saveFile));
                }else if(key[0].equals("TheCity")){
                    ArrayList<String>emptyList = new ArrayList<>();
                    return SpireReturn.Return((AbstractDungeon)new BGTheCity(p, saveFile));
                }else if(key[0].equals("TheBeyond")) {
                    ArrayList<String> emptyList = new ArrayList<>();
                    return SpireReturn.Return((AbstractDungeon) new BGTheBeyond(p, saveFile));
                }else if(key[0].equals("TheEnding")) {
                    ArrayList<String> emptyList = new ArrayList<>();
                    return SpireReturn.Return((AbstractDungeon) new BGTheEnding(p, saveFile));
                }
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(clz = AbstractDungeon.class, method = "initializeCardPools",
            paramtypez = {})
    public static class initializeCardPoolsPatch {
        @SpirePrefixPatch
        public static void initializeCardPools() {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;
            //TODO: the correct solution here is to load the card pools when loading a savefile, which unfortunately involves saving the card pools first
            if (!initializedCardPools) {
                logger.info("----------BoardGame mod is resetting ALL reward decks----------");
                //TODO: there is an extraordinary amount of Logger spam between here and Results.  should we be concerned about things loading out of order?
                initializedCardPools=true;
                physicalRewardDecks.clear();
                physicalRareRewardDecks.clear();

                for(int i=0;i<4;i+=1){
                    CardGroup rewards=new CardGroup(CardGroup.CardGroupType.CARD_POOL);
                    CardGroup rares=new CardGroup(CardGroup.CardGroupType.CARD_POOL);
                    AbstractCard gt;
                    if(i==0)gt=new BGGoldenTicket_R();
                    else if(i==1)gt=new BGGoldenTicket_G();
                    else if(i==2)gt=new BGGoldenTicket_B();
                    else gt=new BGGoldenTicket_W();
                    rewards.addToTop(gt.makeCopy());
                    rewards.addToTop(gt.makeCopy());
                    ArrayList<AbstractCard> tmpPool = new ArrayList<>();
                    if(i==0) new BGIronclad("Ironclad physical card pool", BGIronclad.Enums.BG_IRONCLAD).getCardPool(tmpPool);
                    else if(i==1) new BGSilent("Silent physical card pool", BGSilent.Enums.BG_SILENT).getCardPool(tmpPool);
                    else if(i==2) new BGDefect("Defect physical card pool", BGDefect.Enums.BG_DEFECT).getCardPool(tmpPool);
                    else if(i==3) new BGWatcher("Watcher physical card pool", BGWatcher.Enums.BG_WATCHER).getCardPool(tmpPool);
                    for (AbstractCard c : tmpPool) {
                        switch (c.rarity) {
                            case COMMON:
                                if(c instanceof BGClaw2)break; //these get added in later
                                if(c instanceof BGClaw && BGClaw2.getClawPackCount()>0)break;   //if we ARE using claw pack, don't add regular Claws
                                rewards.addToTop(c.makeCopy());
                                rewards.addToTop(c.makeCopy());
                                break;
                            case UNCOMMON:
                                rewards.addToTop(c.makeCopy());
                                break;
                            case RARE:
                                if(!(c instanceof BGGoldenTicket)) rares.addToTop(c.makeCopy());
                                break;
                        }
                    }
                    if(i==2){
                        for(int j=0;j<BGClaw2.getClawPackCount();j+=1){
                            rewards.addToTop(new BGClaw2());
                        }
                    }
                    rewards.shuffle(cardRng);
                    rares.shuffle(cardRng);
                    physicalRewardDecks.add(rewards);
                    physicalRareRewardDecks.add(rares);
                }


                AbstractBGDungeon.cursesRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
                AbstractBGDungeon.colorlessRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);

                int whoAmI=0;
                if(AbstractDungeon.player instanceof BGIronclad) whoAmI=0;
                else if(AbstractDungeon.player instanceof BGSilent) whoAmI=1;
                else if(AbstractDungeon.player instanceof BGDefect) whoAmI=2;
                else if(AbstractDungeon.player instanceof BGWatcher) whoAmI=3;
                AbstractBGDungeon.rewardDeck = physicalRewardDecks.get(whoAmI);
                AbstractBGDungeon.rareRewardDeck = physicalRareRewardDecks.get(whoAmI);

                cursesRewardDeck.addToTop(new BGInjury());
                cursesRewardDeck.addToTop(new BGInjury());
                cursesRewardDeck.addToTop(new BGClumsy());
                cursesRewardDeck.addToTop(new BGClumsy());
                cursesRewardDeck.addToTop(new BGParasite());
                cursesRewardDeck.addToTop(new BGParasite());
                cursesRewardDeck.addToTop(new BGRegret());
                cursesRewardDeck.addToTop(new BGRegret());
                cursesRewardDeck.addToTop(new BGWrithe());
                cursesRewardDeck.addToTop(new BGDoubt());
                cursesRewardDeck.addToTop(new BGPain());
                cursesRewardDeck.addToTop(new BGShame());
                cursesRewardDeck.addToTop(new BGDecay());
                cursesRewardDeck.shuffle(cardRng);


                ArrayList<AbstractCard> tmpPool = new ArrayList<>();
                new BGColorless().getCardPool(tmpPool);
                //logger.info("Adding colorless cards to reward deck?:");
                for (AbstractCard c : tmpPool) {
                    //logger.info("Add "+c);
                    if(c.type!=AbstractCard.CardType.STATUS && c.rarity != AbstractCard.CardRarity.SPECIAL
                    ) {
                        colorlessRewardDeck.addToTop(c.makeCopy());
                    }
                }
                colorlessRewardDeck.shuffle(cardRng);


                logger.info("Results:");
                logger.info("Ironclad physical deck contains "+physicalRewardDecks.get(0).size()+" cards");
                logger.info("Silent physical deck contains "+physicalRewardDecks.get(1).size()+" cards");
                logger.info("Defect physical deck contains "+physicalRewardDecks.get(2).size()+" cards");
                logger.info("Watcher physical deck contains "+physicalRewardDecks.get(3).size()+" cards");
                logger.info("Colorless deck contains "+colorlessRewardDeck.size()+" cards");
                logger.info("Curses deck contains "+cursesRewardDeck.size()+" cards");
                logger.info("Current reward deck "+whoAmI+" is "+physicalRewardDecks.get(whoAmI).size()+" cards");
            }
        }
    }

    public static AbstractCard DrawFromRewardDeck() {
        if(forceSpecificColor<0)
            return DrawFromRewardDeck(rewardDeck,rareRewardDeck);
        else
            return DrawFromRewardDeck(physicalRewardDecks.get(forceSpecificColor),physicalRareRewardDecks.get(forceSpecificColor));
    }
    public static AbstractCard DrawFromRewardDeck(CardGroup deck) {
        return DrawFromRewardDeck(deck,null);
    }
    public static AbstractCard DrawFromRewardDeck(CardGroup deck, CardGroup raredeck){
        AbstractCard card = null;
        int tempsize=deck.size();
        if(deck.size()>0) {
            card = deck.getTopCard();
            deck.removeTopCard();
            deck.addToBottom(card.makeCopy());  //card gets copied here because we may have upgraded card after drawing it.
        }
        AbstractCard tempcard=card;
        if(card instanceof BGGoldenTicket){
            //card = DrawFromRareRewardDeck();
            if(raredeck!=null)card=DrawFromRewardDeck(raredeck);
        }
        logger.info("AbstractBGDungeon: drew reward card "+card);
        if(card==null){
            logger.info("PANIC!  Card was null?!  Originally drew "+tempcard+" from deck of size "+tempsize);
        }
        return card;
    }

    public static AbstractCard DrawFromRareRewardDeck(){
        AbstractCard card=null;
        if(rareRewardDeck.size()>0) {
            card = rareRewardDeck.getTopCard();
            rareRewardDeck.removeTopCard();
            rareRewardDeck.addToBottom(card.makeCopy());
        }
        return card;
    }

    public static AbstractCard DrawFromColorlessRewardDeck(){
        AbstractCard card=null;
        if(colorlessRewardDeck.size()>0) {
            card = colorlessRewardDeck.getTopCard();
            colorlessRewardDeck.removeTopCard();
            colorlessRewardDeck.addToBottom(card.makeCopy());
        }
        return card;
    }
    public static AbstractCard DrawFromCursesRewardDeck(){
        AbstractCard card=null;
        if(cursesRewardDeck.size()>0) {
            card = cursesRewardDeck.getTopCard();
            cursesRewardDeck.removeTopCard();
            cursesRewardDeck.addToBottom(card);
        }
        return card;
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards",
            paramtypez = {})
    public static class getRewardCardsPatch {
        @SpirePrefixPatch
        public static SpireReturn<ArrayList<AbstractCard>> getRewardCards() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                ArrayList<AbstractCard> retVal = new ArrayList<>();
                int numCards = 3;
                for (AbstractRelic r : player.relics) {
                    numCards = r.changeNumberOfCardsInReward(numCards);
                }
                if (ModHelper.isModEnabled("Binary")) {
                    numCards--;
                }
                if(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && CardCrawlGame.dungeon instanceof BGTheBeyond){
                    //no card drops allowed in act3 boss room
                    numCards = 0;
                }
                boolean rare=false;
                if((getCurrRoom() instanceof MonsterRoomBoss) || AbstractBGDungeon.forceRareRewards==true)
                    rare=true;
                for (int i = 0; i < numCards; i++) {
                    AbstractCard card=null;
                    if(!rare)
                        card = DrawFromRewardDeck();
                    else
                        card  = DrawFromRareRewardDeck();

                    if(card!=null) {
                        if(CardCrawlGame.dungeon instanceof AbstractBGDungeon
                                && (!(CardCrawlGame.dungeon instanceof BGExordium) && getCurrRoom() instanceof MonsterRoomElite)
                                || (getCurrRoom() instanceof EventRoom && getCurrRoom().event instanceof BGColosseum && ((BGColosseum)getCurrRoom().event).isElite)){
                            card.upgrade();
                        }
                        for (AbstractRelic r : player.relics) {
                            r.onPreviewObtainCard(card);
                        }
                        retVal.add(card);
                    }
                    //any card which is actually taken should be removed from the reward deck (handled in CardRewardScreenAcquireCardPatch)
                }
                return SpireReturn.Return(retVal);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getColorlessRewardCards",
            paramtypez = {})
    public static class AbstractDungeonGetColorlessRewardCardsPatch {
        @SpirePrefixPatch
        public static SpireReturn<ArrayList<AbstractCard>> getColorlessRewardCards() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                ArrayList<AbstractCard> retVal = new ArrayList<>();
                int numCards = 3;
                for (AbstractRelic r : player.relics) {
                    numCards = r.changeNumberOfCardsInReward(numCards);
                }
                if (ModHelper.isModEnabled("Binary")) {
                    numCards--;
                }
                for (int i = 0; i < numCards; i++) {
                    AbstractCard card = null;
                    card = DrawFromColorlessRewardDeck();
                    if (card != null) {
                        retVal.add(card);
                    }
                }
                return SpireReturn.Return(retVal);
            }
            return SpireReturn.Continue();
        }
    }

    //CardRewardScreen handles "pick 1 of X cards to add to your deck"
        // -- patch removes the chosen card from the rewards deck
    @SpirePatch2(clz = CardRewardScreen.class, method = "acquireCard",
            paramtypez = {AbstractCard.class})
    public static class acquireCardPatch {
        @SpirePostfixPatch
        private static void acquireCard(AbstractCard hoveredCard) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                AbstractBGDungeon.removeCardFromRewardDeck(hoveredCard);
            }
        }
    }

    //AbstractDungeon.getCard handles all other instances of "produce a random card"
        // including cases where the card is not removed from the rewards deck
    @SpirePatch(clz = AbstractDungeon.class, method = "getCard",
            paramtypez = {AbstractCard.CardRarity.class})
    public static class getCardPatch1 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> getCard(AbstractCard.CardRarity rarity) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                switch (rarity) {
                    case COMMON:
                    case UNCOMMON:
                        return SpireReturn.Return(DrawFromRewardDeck());
                    case RARE:
                        return SpireReturn.Return(DrawFromRareRewardDeck());
                    case CURSE:
                        return SpireReturn.Return(DrawFromCursesRewardDeck());
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getCard",
            paramtypez = {AbstractCard.CardRarity.class, Random.class})
    public static class getCardPatch2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> getCard(AbstractCard.CardRarity rarity, Random rng) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon)
                return SpireReturn.Return(AbstractDungeon.getCard(rarity));
            else
                return SpireReturn.Continue();
        }
    }
    @SpirePatch(clz = AbstractDungeon.class, method = "getCardWithoutRng",
            paramtypez = {AbstractCard.CardRarity.class})
    public static class getCardPatch3 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> getCardWithoutRng(AbstractCard.CardRarity rarity) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon)
                return SpireReturn.Return(AbstractDungeon.getCard(rarity));
            else
                return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getColorlessCardFromPool",
            paramtypez = {AbstractCard.CardRarity.class})
    public static class getCardPatch4 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> getColorlessCardFromPool(AbstractCard.CardRarity rarity) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                return SpireReturn.Return(DrawFromColorlessRewardDeck());
            }
            return SpireReturn.Continue();
        }
    }

    private static void removeOneCardFromOneDeck(String cardname, CardGroup deck){

        if(deck.removeCard(cardname)){
           logger.info("Successfully removed "+cardname+" from a reward deck");
        }

    }

    public static void removeCardFromRewardDeck(AbstractCard card){
        //TODO: check if card came from a different color deck too!
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.rewardDeck);
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.rareRewardDeck);
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.colorlessRewardDeck);
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.cursesRewardDeck);
        //TODO: if remove was unsuccessful, complain loudly
    }


    @SpirePatch(clz = AbstractDungeon.class, method = "transformCard",
            paramtypez = {AbstractCard.class, boolean.class, Random.class})
    public static class AbstractDungeonTransformCardPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> transformCardPatch(AbstractCard c, boolean autoUpgrade, Random rng) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                transformedCard = DrawFromRewardDeck();
                //TODO: not allowed to transform BGCurses (also, colorless cards transform into color cards)
                UnlockTracker.markCardAsSeen(transformedCard.cardID);
                if (autoUpgrade && transformedCard.canUpgrade()) {
                    transformedCard.upgrade();
                }
                // don't remove card from reward deck yet.
                // it will be removed during the subsequent call to getTransformedCard.
                // reminder: transformed / removed cards do not return to the reward deck -- they're gone forever
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch(clz = AbstractDungeon.class, method = "getTransformedCard",
            paramtypez = {})
    public static class getTransformedCardPatch {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> getTransformedCard() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                AbstractCard retVal = transformedCard;
                AbstractBGDungeon.removeCardFromRewardDeck(transformedCard);
                transformedCard = null;
                return SpireReturn.Return(retVal);
            }
            return SpireReturn.Continue();
        }
    }



    @SpirePatch2(clz = EventHelper.class, method = "roll",
            paramtypez = {Random.class})
    public static class EventHelperrollPatch {
        @SpirePrefixPatch
        public static SpireReturn<EventHelper.RoomResult> roll (Random eventRng){
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                //TODO: on ascension 3+, check the top event card + AbstractDungeon.floorNum first
                if(true) {
                    return SpireReturn.Return(EventHelper.RoomResult.EVENT);
                }else if(false) {
                    return SpireReturn.Return(EventHelper.RoomResult.MONSTER);
                }else if(false){
                    return SpireReturn.Return(EventHelper.RoomResult.SHOP);
                }
            }
            return SpireReturn.Continue();

        }
    }


    @SpirePatch2(clz = AbstractRoom.class, method = "addGoldToRewards",
            paramtypez = {int.class})
    public static class addGoldToRewardsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> addGoldToRewards(AbstractRoom __instance, int gold) {
            //TODO: if multicharacter, need completely different reward interface
            if(AbstractDungeon.player instanceof MultiCharacter) return SpireReturn.Continue();
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                //logger.info("Encounter: "+AbstractDungeon.monsterList.get(0));  // <-- works as expected
                String encounter="";

                int goldModifier = 0;
                if(__instance instanceof MonsterRoomBoss) {
                    encounter = AbstractDungeon.bossKey;
                    logger.info("Boss key: "+AbstractDungeon.bossKey);
                    if(AbstractDungeon.ascensionLevel>=10) {
                        //TODO: doublecheck this isn't affecting Tiny House
                        goldModifier = -1;
                    }
                }else if(__instance instanceof MonsterRoomElite){
                    encounter = AbstractDungeon.eliteMonsterList.get(0);
                }else if(__instance instanceof MonsterRoom){
                    encounter = AbstractDungeon.monsterList.get(0);
                }else if(__instance instanceof TreasureRoom){
                    gold=0;
                }else if(__instance instanceof EventRoom && __instance.event instanceof BGColosseum){
                    encounter = ((BGColosseum)__instance.event).encounterID;
                }else if(__instance instanceof EventRoom && __instance.event instanceof BGDeadAdventurer){
                    encounter = ((BGDeadAdventurer)__instance.event).encounterID;
                }else if(__instance instanceof EventRoom && __instance.event instanceof BGHallwayEncounter){
                    //note: BGHallwayEncounter currently swaps itself out for a MonsterRoom, so this line shouldn't be reachable
                    encounter = ((BGHallwayEncounter)__instance.event).encounterID;
                }
                if(MonsterGroupRewardsList.rewards.containsKey(encounter)){
                    gold=MonsterGroupRewardsList.rewards.get(encounter).gold;
                }
                if(gold>0) {
                    gold+=goldModifier;
                    for (RewardItem i : __instance.rewards) {
                        if (i.type == RewardItem.RewardType.GOLD) {
                            i.incrementGold(gold);
                            return SpireReturn.Return();
                        }
                    }
                    __instance.rewards.add(new RewardItem(gold));
                }
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "addPotionToRewards",
            paramtypez = {})
    public static class addPotionToRewardsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> addPotionToRewards(AbstractRoom __instance) {
            //TODO: if multicharacter, need completely different reward interface
            if(AbstractDungeon.player instanceof MultiCharacter) return SpireReturn.Continue();
            boolean potion=false;
            String encounter="";
            //BoardGame.logger.info("addPotionToRewardsPatch...");
            if(__instance instanceof MonsterRoomBoss){
                //BoardGame.logger.info("...MonsterRoomBoss...");
                encounter = "NO POTION";
            }else if(__instance instanceof MonsterRoomElite){
                encounter = AbstractDungeon.eliteMonsterList.get(0);
            }else if(__instance instanceof MonsterRoom) {
                encounter = AbstractDungeon.monsterList.get(0);
            }else if(__instance instanceof EventRoom && __instance.event instanceof BGColosseum){
                encounter = ((BGColosseum)__instance.event).encounterID;
            }else if(__instance instanceof EventRoom && __instance.event instanceof BGDeadAdventurer){
                encounter = ((BGDeadAdventurer)__instance.event).encounterID;
            }else if(__instance instanceof EventRoom && __instance.event instanceof BGHallwayEncounter){
                //note: BGHallwayEncounter currently swaps itself out for a MonsterRoom, so this line shouldn't be reachable
                encounter = ((BGHallwayEncounter)__instance.event).encounterID;
            }
            if(MonsterGroupRewardsList.rewards.containsKey(encounter)){
                potion=MonsterGroupRewardsList.rewards.get(encounter).potion;
            }
            if(potion){
                CardCrawlGame.metricData.potions_floor_spawned.add(Integer.valueOf(AbstractDungeon.floorNum));
                __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
            }
            if(AbstractDungeon.player.hasRelic("BGWhite Beast Statue")){
                //BoardGame.logger.info("...BGWhite Beast Statue...");

                CardCrawlGame.metricData.potions_floor_spawned.add(Integer.valueOf(AbstractDungeon.floorNum));
                __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));

            }else if(ReflectionHacks.getPrivate(AbstractDungeon.combatRewardScreen, CombatRewardScreen.class,"labelOverride")
                    ==CardCrawlGame.languagePack.getRelicStrings("BGTiny House").DESCRIPTIONS[3]){
                CardCrawlGame.metricData.potions_floor_spawned.add(Integer.valueOf(AbstractDungeon.floorNum));
                __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
            }
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz = MonsterHelper.class, method = "getEncounter",
            paramtypez = {String.class})
    public static class getEncounterPatch {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> getEncounter(String key) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                switch (key) {
                    case "The Guardian":
                        return SpireReturn.Return(new MonsterGroup((AbstractMonster) new BGTheGuardian()));
                    case "Hexaghost":
                        return SpireReturn.Return(new MonsterGroup((AbstractMonster) new BGHexaghost()));
                    case "Slime Boss":
                        return SpireReturn.Return(new MonsterGroup((AbstractMonster) new BGSlimeBoss()));
                    case "Automaton":
                        return SpireReturn.Return(new MonsterGroup((AbstractMonster) new BGBronzeAutomaton()));
                    case "Collector":
                        return SpireReturn.Return(new MonsterGroup((AbstractMonster) new BGTheCollector()));
                    case "Champ":
                        return SpireReturn.Return(new MonsterGroup((AbstractMonster) new BGChamp()));
                    case "Time Eater":
                        return SpireReturn.Return(new MonsterGroup((AbstractMonster) new BGTimeEater()));
                    case "Awakened One":
                        return SpireReturn.Return(new MonsterGroup(new AbstractMonster[] {
                                (AbstractMonster)new BGCultist(-590.0F, 10.0F, false),
                                (AbstractMonster)new BGCultist(-298.0F, -10.0F, false),
                                (AbstractMonster)new BGAwakenedOne(100.0F, 15.0F) }));
                    case "Donu and Deca":
                        return SpireReturn.Return(new MonsterGroup(new AbstractMonster[] {
                                (AbstractMonster)new BGDeca(),
                                (AbstractMonster)new BGDonu() }));
                    case "The Heart":
                        return SpireReturn.Return(new MonsterGroup(new AbstractMonster[] {
                                (AbstractMonster)new BGCorruptHeart()}));
                }
            }
            return SpireReturn.Continue();
        }
    }


    public void nextRoomTransition(SaveFile saveFile){
        super.nextRoomTransition(saveFile);
        EntropicBrewPotionButton.TopPanelEntropicInterface.entropicBrewPotionButtons.set(AbstractDungeon.topPanel, new ArrayList<>());
    }


    public void populateMonsterList(ArrayList<MonsterInfo> monsters, int numMonsters, boolean elites) {
        //TODO: monsters go to the bottom of the monster deck after they're selected
        // (rather than reshuffling the entire deck when it's depleted)
        // (but First Encounter monsters don't go back into the deck)
        numMonsters=Math.min(numMonsters,monsters.size());
        Collections.shuffle(monsters, new java.util.Random(monsterRng.randomLong()));
        if(!elites) {
            for (int i = 0; i < numMonsters; ++i) {
                monsterList.add(monsters.get(i).name);
            }
        }else{
            for (int i = 0; i < numMonsters; ++i) {
                eliteMonsterList.add(monsters.get(i).name);
            }
        }
    }

}

