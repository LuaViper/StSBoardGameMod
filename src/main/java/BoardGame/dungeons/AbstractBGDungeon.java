package BoardGame.dungeons;

import BoardGame.cards.BGGoldenTicket;
import BoardGame.cards.BGCurse.*;
import BoardGame.characters.BGColorless;
import BoardGame.characters.BGIronclad;
import BoardGame.monsters.MonsterGroupRewardsList;
import BoardGame.monsters.bgbeyond.*;
import BoardGame.monsters.bgcity.*;
import BoardGame.monsters.bgexordium.*;
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
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

public abstract class AbstractBGDungeon extends AbstractDungeon {
    public static boolean initializedCardPools=false;
    public static CardGroup rewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    public static CardGroup rareRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    public static CardGroup cursesRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    public static CardGroup colorlessRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);

    public static boolean forceRareRewards=false;

    public AbstractBGDungeon(String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
        super(name, levelId, p, newSpecialOneTimeEventList);

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
            if(p instanceof BGIronclad) {
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
            if(p instanceof BGIronclad) {
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
            //TODO: the correct solution here is to load the card pools when loading a savefile, which unfortunately involves saving the card pools first
            if (CardCrawlGame.dungeon instanceof BGExordium || !initializedCardPools) {
                logger.info("BoardGame mod is resetting ALL reward decks");
                initializedCardPools=true;
                AbstractBGDungeon.rewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
                AbstractBGDungeon.rareRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
                AbstractBGDungeon.cursesRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
                AbstractBGDungeon.colorlessRewardDeck = new CardGroup(CardGroup.CardGroupType.CARD_POOL);

                ArrayList<AbstractCard> tmpPool = new ArrayList<>();
                player.getCardPool(tmpPool);
                for (AbstractCard c : tmpPool) {
                    switch (c.rarity) {
                        case COMMON:
                            rewardDeck.addToTop(c.makeCopy());
                            rewardDeck.addToTop(c.makeCopy());
                            break;
                        case UNCOMMON:
                            rewardDeck.addToTop(c.makeCopy());
                            break;
                        case RARE:
                            rareRewardDeck.addToTop(c.makeCopy());
                            break;
                    }
                }
                rewardDeck.shuffle(cardRng);
                rareRewardDeck.shuffle(cardRng);

                cursesRewardDeck.addToTop(new BGInjury());
                cursesRewardDeck.addToTop(new BGInjury());
                cursesRewardDeck.addToTop(new BGClumsy());
                cursesRewardDeck.addToTop(new BGClumsy());
                cursesRewardDeck.addToTop(new BGRegret());
                cursesRewardDeck.addToTop(new BGRegret());
                cursesRewardDeck.addToTop(new BGDecay());
                cursesRewardDeck.addToTop(new BGDecay());
                cursesRewardDeck.addToTop(new BGParasite());
                cursesRewardDeck.addToTop(new BGParasite());
                cursesRewardDeck.addToTop(new BGWrithe());
                //cursesRewardDeck.addToTop(new BGDoubt());
                //cursesRewardDeck.addToTop(new BGShame());
                cursesRewardDeck.shuffle(cardRng);


                tmpPool = new ArrayList<>();
                new BGColorless().getCardPool(tmpPool);
                //logger.info("Adding colorless cards to reward deck?:");
                for (AbstractCard c : tmpPool) {
                    //logger.info("Add "+c);
                    colorlessRewardDeck.addToTop(c.makeCopy());
                }
                colorlessRewardDeck.shuffle(cardRng);
            }
        }
    }

    public static AbstractCard DrawFromRewardDeck(){
        AbstractCard card = null;
        int tempsize=rewardDeck.size();
        if(rewardDeck.size()>0) {
            card = rewardDeck.getTopCard();
            rewardDeck.removeTopCard();
            rewardDeck.addToBottom(card.makeCopy());  //card gets copied here because we may have upgraded card after drawing it.
        }
        AbstractCard tempcard=card;
        if(card instanceof BGGoldenTicket){
            card = DrawFromRareRewardDeck();
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
            colorlessRewardDeck.addToBottom(card);
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
                                && !(CardCrawlGame.dungeon instanceof BGExordium)
                                && getCurrRoom() instanceof MonsterRoomElite){
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
//        AbstractCard target=null;
//        for(AbstractCard c : deck.group){
//            if(c.cardID==cardname){
//                target=c;
//                break;
//            }
//        }
//        if(target!=null){
//
//        }
        if(deck.removeCard(cardname)){
           logger.info("Successfully removed "+cardname+" from a reward deck");
        }

    }

    public static void removeCardFromRewardDeck(AbstractCard card){
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.rewardDeck);
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.rareRewardDeck);
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.colorlessRewardDeck);
        removeOneCardFromOneDeck(card.cardID, AbstractBGDungeon.cursesRewardDeck);
    }


    @SpirePatch(clz = AbstractDungeon.class, method = "transformCard",
            paramtypez = {AbstractCard.class, boolean.class, Random.class})
    public static class AbstractDungeonTransformCardPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> transformCardPatch(AbstractCard c, boolean autoUpgrade, Random rng) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                transformedCard = DrawFromRewardDeck();
                //TODO: not allowed to transform BGCurses
                UnlockTracker.markCardAsSeen(transformedCard.cardID);
                if (autoUpgrade && transformedCard.canUpgrade()) {
                    transformedCard.upgrade();
                }
                // don't remove card from reward deck yet.
                // it will be removed during the subsequent call to getTransformedCard.
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
                return SpireReturn.Return(EventHelper.RoomResult.EVENT);

            }
            return SpireReturn.Continue();

        }
    }


    @SpirePatch2(clz = AbstractRoom.class, method = "addGoldToRewards",
            paramtypez = {int.class})
    public static class addGoldToRewardsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> addGoldToRewards(AbstractRoom __instance, int gold) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                //logger.info("Encounter: "+AbstractDungeon.monsterList.get(0));  // <-- works as expected
                String encounter="";

                if(__instance instanceof MonsterRoomBoss) {
                    encounter = AbstractDungeon.bossKey;
                    logger.info("Boss key: "+AbstractDungeon.bossKey);
                }else if(__instance instanceof MonsterRoomElite){
                    encounter = AbstractDungeon.eliteMonsterList.get(0);
                }else if(__instance instanceof MonsterRoom){
                    encounter = AbstractDungeon.monsterList.get(0);
                }else if(__instance instanceof TreasureRoom){
                    gold=0;
                }
                if(MonsterGroupRewardsList.rewards.containsKey(encounter)){
                    gold=MonsterGroupRewardsList.rewards.get(encounter).gold;
                }
                if(gold>0) {
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
        public static SpireReturn<Void> addGoldToRewards(AbstractRoom __instance) {
            boolean potion=false;
            String encounter="";
            if(__instance instanceof MonsterRoomBoss){
                encounter="NO POTION";
            }else if(__instance instanceof MonsterRoomElite){
                encounter = AbstractDungeon.eliteMonsterList.get(0);
            }else if(__instance instanceof MonsterRoom) {
                encounter = AbstractDungeon.monsterList.get(0);
            }
            if(MonsterGroupRewardsList.rewards.containsKey(encounter)){
                potion=MonsterGroupRewardsList.rewards.get(encounter).potion;
            }
            if(potion){
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

                }
            }
            return SpireReturn.Continue();
        }
    }


}

