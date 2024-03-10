package BoardGame.neow;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
















//BGNeowReward does NOT extend NeowReward, otherwise the enums start fighting and overriding the button labels
public class BGNeowReward
{
    public static class NeowRewardDef
    {
        public BGNeowReward.NeowRewardType type;
        public String desc;

        public NeowRewardDef(BGNeowReward.NeowRewardType type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }

    public static class NeowRewardDrawbackDef
    {
        public BGNeowReward.NeowRewardDrawback type;
        public String desc;

        public NeowRewardDrawbackDef(BGNeowReward.NeowRewardDrawback type, String desc) {
            this.type = type;
            this.desc = desc;
        } }



    public BGNeowReward(String details) {
        char drawbackchar=details.charAt(0);
        char rewardchar=details.charAt(1);

        //logger.info("BGNeowReward: "+details+" "+drawbackchar+" "+rewardchar);

        NeowRewardDrawbackDef drawbackDef=getDrawbackFromChar(drawbackchar);
        //logger.info("Type: "+drawbackDef.type);
        //logger.info("Desc: "+drawbackDef.desc);
        NeowRewardDef reward=getRewardFromChar(rewardchar);

        this.optionLabel = "";


        this.drawbackDef = drawbackDef;
        this.drawback = drawbackDef.type;

        this.activated = false;
        this.hp_bonus = 0;
        this.cursed = false;
        this.hp_bonus = (int)(AbstractDungeon.player.maxHealth * 0.1F);


        if (this.drawback != NeowRewardDrawback.NONE && this.drawbackDef != null) {
            this.optionLabel += this.drawbackDef.desc;
        }
        this.optionLabel += reward.desc;
        this.type = reward.type;
        //logger.info("BGNeowReward: "+this.optionLabel);
    }


    private NeowRewardDrawbackDef getDrawbackFromChar(char drawback) {
        switch(drawback) {
            case '-':
                return new NeowRewardDrawbackDef(NeowRewardDrawback.NONE, "");
            case 'D':
                return new NeowRewardDrawbackDef(NeowRewardDrawback.LOSE_HP, TEXT[21] + 2 + TEXT[29] + " ");
            case '3':
                return new NeowRewardDrawbackDef(NeowRewardDrawback.LOSE_3_HP, TEXT[21] + 3 + TEXT[29] + " ");
            case 'G':
                return new NeowRewardDrawbackDef(NeowRewardDrawback.LOSE_GOLD, TEXT[19]);
            case 'C':
                return new NeowRewardDrawbackDef(NeowRewardDrawback.CURSE, TEXT[20]);
            default:
                logger.info("Unrecognized BGNeow drawback char: "+drawback);
        }
        return new NeowRewardDrawbackDef(NeowRewardDrawback.NONE, "");
    }
    private NeowRewardDef getRewardFromChar(char reward) {
        switch(reward) {
            case '4':
                return new NeowRewardDef(NeowRewardType.FOUR_GOLD, TEXT[32]);
            case '5':
                return new NeowRewardDef(NeowRewardType.FIVE_GOLD, "[ #gObtain #g5 #gGold ]"); //TODO: localization
            case '8':
                return new NeowRewardDef(NeowRewardType.EIGHT_GOLD, TEXT[37]);
            case 'X':
                return new NeowRewardDef(NeowRewardType.TEN_GOLD, TEXT[33]);
            case 'r':
                return new NeowRewardDef(NeowRewardType.REMOVE_CARD, TEXT[2]);
            case 'R':
                return new NeowRewardDef(NeowRewardType.REMOVE_TWO, TEXT[10]);
            case 't':
                return new NeowRewardDef(NeowRewardType.TRANSFORM_CARD, TEXT[4]);
            case 'T':
                return new NeowRewardDef(NeowRewardType.TRANSFORM_TWO_CARDS, TEXT[15]);
            case 'u':
                return new NeowRewardDef(NeowRewardType.UPGRADE_CARD, TEXT[3]);
            case 'U':
                return new NeowRewardDef(NeowRewardType.UPGRADE_TWO_RANDOM, TEXT[34]);
            case 'c':
                return new NeowRewardDef(NeowRewardType.CHOOSE_A_CARD, "[ #gChoose #ga #gCard #gto #gobtain ]"); //TODO: localization
            case 'C':
                return new NeowRewardDef(NeowRewardType.GET_TWO_RANDOM_CARDS, TEXT[35]);
            case 'P':
                return new NeowRewardDef(NeowRewardType.THREE_POTIONS, TEXT[5]);
            case '?':
                return new NeowRewardDef(NeowRewardType.RANDOM_RARE_CARD, TEXT[1]);
            case '!':
                return new NeowRewardDef(NeowRewardType.CHOOSE_RARE_CARD, TEXT[12]);
            case '%':
                return new NeowRewardDef(NeowRewardType.RELIC, TEXT[6]);
            case '=':
                return new NeowRewardDef(NeowRewardType.CHOOSE_COLORLESS_CARD, TEXT[30]);
            case '+':
                return new NeowRewardDef(NeowRewardType.GET_TWO_RANDOM_COLORLESS_CARDS, TEXT[36]);
            case 'V':
                return new NeowRewardDef(NeowRewardType.CHOOSE_TWO_CARDS, " #gChoose #g2 #gCards #gto #gobtain ]"); //TODO: localization
            case 'W':
                return new NeowRewardDef(NeowRewardType.CARD_GOLD_COMBO, " #gChoose #ga #gCard #gto #gobtain. #gObtain #g5 #gGold ]"); //TODO: localization and punctuation
            case ')':
                return new NeowRewardDef(NeowRewardType.CHOOSE_TWO_COLORLESS_CARDS, " #gChoose #g2 #gcolorless #gCards #gto #gobtain ]"); //TODO: localization
            default:
                logger.info("Unrecognized BGNeow reward char: "+reward);
        }
        return null;
    }




    public void update() {
        if (this.activated) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c, c2, c3, t1, t2;
                switch (this.type) {
                    case UPGRADE_CARD:
                        c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                        c.upgrade();
                        AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        break;

                    case REMOVE_CARD:
                        CardCrawlGame.sound.play("CARD_EXHAUST");
                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards

                                .get(0), (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));


                        AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards
                                .get(0));
                        break;
                    case REMOVE_TWO:
                        CardCrawlGame.sound.play("CARD_EXHAUST");
                        c2 = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                        c3 = AbstractDungeon.gridSelectScreen.selectedCards.get(1);
                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c2, Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, (Settings.HEIGHT / 2)));




                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c3, Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));




                        AbstractDungeon.player.masterDeck.removeCard(c2);
                        AbstractDungeon.player.masterDeck.removeCard(c3);
                        break;
                    case TRANSFORM_CARD:
                        AbstractDungeon.transformCard(AbstractDungeon.gridSelectScreen.selectedCards
                                .get(0), false, NeowEvent.rng);


                        AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards
                                .get(0));
                        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(

                                AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        break;
                    case TRANSFORM_TWO_CARDS:
                        t1 = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                        t2 = AbstractDungeon.gridSelectScreen.selectedCards.get(1);
                        AbstractDungeon.player.masterDeck.removeCard(t1);
                        AbstractDungeon.player.masterDeck.removeCard(t2);
                        AbstractDungeon.transformCard(t1, false, NeowEvent.rng);
                        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(

                                AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));


                        AbstractDungeon.transformCard(t2, false, NeowEvent.rng);
                        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(

                                AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
                        break;


                    default:
                        logger.info("[ERROR] Missing Neow Reward Type: " + this.type.name());
                        break;
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.overlayMenu.cancelButton.hide();
                SaveHelper.saveIfAppropriate(SaveFile.SaveType.POST_NEOW);
                this.activated = false;
            }
            if (this.cursed) {
                this.cursed = !this.cursed;
                AbstractCard card = AbstractDungeon.getCardWithoutRng(AbstractCard.CardRarity.CURSE);
                AbstractBGDungeon.removeCardFromRewardDeck(card);

                AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                        card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                AbstractBGDungeon.removeCardFromRewardDeck(card);
            }
        }
    }


    public void activate() {
        int i, remove, j;
        this.activated = true;
        if(this.drawback!=null) {
            switch (this.drawback) {
                case NONE:
                    break;
                case CURSE:
                    this.cursed = true;
                    break;
                case LOSE_GOLD:
                    AbstractDungeon.player.loseGold(3);
                    break;
                case LOSE_HP:
                    AbstractDungeon.player.damage(new DamageInfo(null, 2, DamageInfo.DamageType.HP_LOSS));
                    break;
                case LOSE_3_HP:
                    AbstractDungeon.player.damage(new DamageInfo(null, 3, DamageInfo.DamageType.HP_LOSS));
                    break;
                default:
                    logger.info("[ERROR] Missing Neow Reward Drawback: " + this.drawback.name());
                    break;
            }
        }
        if(this.type!=null) {
            AbstractCard.CardRarity rarity;
            AbstractCard card1,card2;
            switch (this.type) {
                case FOUR_GOLD:
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                    AbstractDungeon.player.gainGold(4);
                    break;
                case FIVE_GOLD:
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                    AbstractDungeon.player.gainGold(5);
                    break;

                case EIGHT_GOLD:
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                    AbstractDungeon.player.gainGold(8);
                    break;

                case TEN_GOLD:
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                    AbstractDungeon.player.gainGold(10);
                    break;

                case REMOVE_CARD:
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                            .getPurgeableCards(), 1, TEXT[23], false, false, false, true);
                    break;

                case REMOVE_TWO:
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                            .getPurgeableCards(), 2, TEXT[24], false, false, false, false);
                    break;


                case TRANSFORM_CARD:
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                            .getPurgeableCards(), 1, TEXT[25], false, true, false, false);
                    break;

                case TRANSFORM_TWO_CARDS:
                    //(forTransform=false apparently disables the slot machine animation so it works with 2 cards)
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                            .getPurgeableCards(), 2, TEXT[26], false, false, false, false);
                    break;


                case UPGRADE_CARD:
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                            .getUpgradableCards(), 1, TEXT[27], true, false, false, false);
                    break;
                case UPGRADE_TWO_RANDOM:
                    upgradeTwoCards();
                    break;
                case GET_TWO_RANDOM_CARDS:
                    card1 = AbstractBGDungeon.DrawFromRewardDeck();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card1, Settings.WIDTH / 2.0F - 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card1);
                    card2 = AbstractBGDungeon.DrawFromRewardDeck();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card2, Settings.WIDTH / 2.0F + 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card2);



                    //TODO: logmetricobtaincards

                    break;

                case THREE_POTIONS:
                    CardCrawlGame.sound.play("POTION_1");
                    for (i = 0; i < 3; i++) {
                        AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getRandomPotion());
                    }
                    AbstractDungeon.combatRewardScreen.open();
                    (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
                    remove = -1;
                    for (j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
                        if (((RewardItem) AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
                            remove = j;
                            break;
                        }
                    }
                    if (remove != -1) {
                        AbstractDungeon.combatRewardScreen.rewards.remove(remove);
                    }
                    break;

                case RANDOM_RARE_CARD:
                    AbstractCard card=AbstractDungeon.getCard(AbstractCard.CardRarity.RARE, NeowEvent.rng);
                    AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                            card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                            //card.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card);
                    break;

                case CHOOSE_RARE_CARD:
                    AbstractBGDungeon.forceRareRewards=true;
                    AbstractDungeon.cardRewardScreen.open(
                            AbstractDungeon.getRewardCards(), null,

                            (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
                    AbstractBGDungeon.forceRareRewards=false;
                    break;

                case RELIC:
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2),
                            AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON));
                    break;

                case CHOOSE_A_CARD:
                    AbstractDungeon.cardRewardScreen.open(
                            AbstractDungeon.getRewardCards(), null,

                            (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
                    break;

                case CHOOSE_COLORLESS_CARD:
                    AbstractDungeon.cardRewardScreen.open(
                            AbstractDungeon.getColorlessRewardCards(), null,

                            (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
                    break;

                case GET_TWO_RANDOM_COLORLESS_CARDS:
                    logger.info("GET_TWO_RANDOM_COLORLESS_CARDS is not yet implemented!");
                    card1=AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.COMMON);
                    AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                            card1, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    //card1.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card1);
                    card2=AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.COMMON);
                    if(card2!=null) {
                        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                                card2, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    }
                    //card2.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card2);


                    break;

                case CHOOSE_TWO_COLORLESS_CARDS:
                    AbstractDungeon.getCurrRoom().event.noCardsInRewards=true;  //prevent extra regular card from appearing when reward screen is opened
                    (AbstractDungeon.getCurrRoom()).rewards.clear();
                    for (i = 0; i < 2; i++) {
                        AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(AbstractCard.CardColor.COLORLESS));
                    }
                    AbstractDungeon.combatRewardScreen.open();
                    AbstractDungeon.getCurrRoom().event.noCardsInRewards=false;
                    break;

                case CARD_GOLD_COMBO:
                    AbstractDungeon.getCurrRoom().event.noCardsInRewards=true;  //prevent extra regular card from appearing when reward screen is opened
                    (AbstractDungeon.getCurrRoom()).rewards.clear();
                    for (i = 0; i < 1; i++) {
                        AbstractDungeon.getCurrRoom().addCardReward(new RewardItem());
                    }
                    AbstractDungeon.combatRewardScreen.open();
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                    AbstractDungeon.player.gainGold(5);
                    AbstractDungeon.getCurrRoom().event.noCardsInRewards=false;
                    break;

                case CHOOSE_TWO_CARDS:
                    AbstractDungeon.getCurrRoom().event.noCardsInRewards=true;
                    (AbstractDungeon.getCurrRoom()).rewards.clear();
                    for (i = 0; i < 2; i++) {
                        //AbstractDungeon.getCurrRoom().addCardToRewards();
                        AbstractDungeon.getCurrRoom().addCardReward(new RewardItem());
                    }
                    AbstractDungeon.combatRewardScreen.open();
                    AbstractDungeon.getCurrRoom().event.noCardsInRewards=false;
                    break;
            }
        }






        CardCrawlGame.metricData.addNeowData(this.type.name(), this.drawback.name());
    }





    public AbstractCard.CardRarity rollRarity() {
        if (NeowEvent.rng.randomBoolean(0.33F)) {
            return AbstractCard.CardRarity.UNCOMMON;
        }
        return AbstractCard.CardRarity.COMMON;
    }

//    public AbstractCard getCard(AbstractCard.CardRarity rarity) {
//        switch (rarity) {
//            case RARE:
//                return AbstractDungeon.rareCardPool.getRandomCard(NeowEvent.rng);
//            case UNCOMMON:
//                return AbstractDungeon.uncommonCardPool.getRandomCard(NeowEvent.rng);
//            case COMMON:
//                return AbstractDungeon.commonCardPool.getRandomCard(NeowEvent.rng);
//        }
//        logger.info("Error in getCard in Neow Reward");
//        return null;
//    }



    private static final Logger logger = LogManager.getLogger(BGNeowReward.class.getName());
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("BoardGame:BGNeow Reward");
    public static final String[] NAMES = characterStrings.NAMES;
    public static final String[] TEXT = characterStrings.TEXT; public String optionLabel; public NeowRewardType type; public NeowRewardDrawback drawback; private boolean activated; private int hp_bonus;
    public static final String[] UNIQUE_REWARDS = characterStrings.UNIQUE_REWARDS; private boolean cursed; private static final int GOLD_BONUS = 4;
    private static final int LARGE_GOLD_BONUS = 10;
    private NeowRewardDrawbackDef drawbackDef;

    public enum NeowRewardType
    {   FOUR_GOLD, FIVE_GOLD, EIGHT_GOLD, TEN_GOLD, REMOVE_CARD, REMOVE_TWO, TRANSFORM_CARD, TRANSFORM_TWO_CARDS,
    UPGRADE_CARD, UPGRADE_TWO_RANDOM, CHOOSE_A_CARD, GET_TWO_RANDOM_CARDS, THREE_POTIONS,
        RANDOM_RARE_CARD, CHOOSE_RARE_CARD, RELIC, CHOOSE_COLORLESS_CARD, GET_TWO_RANDOM_COLORLESS_CARDS,
        CHOOSE_TWO_CARDS, CARD_GOLD_COMBO, CHOOSE_TWO_COLORLESS_CARDS}


    public enum NeowRewardDrawback {
        NONE, LOSE_HP, LOSE_3_HP, LOSE_GOLD, CURSE;
    }





    //from ShiningLight event
    private void upgradeTwoCards() {
        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade()) {
                upgradableCards.add(c);
            }
        }

        List<String> cardMetrics = new ArrayList<>();

        Collections.shuffle(upgradableCards, new java.util.Random(AbstractDungeon.miscRng.randomLong()));

        if (!upgradableCards.isEmpty())
        {
            if (upgradableCards.size() == 1) {
                ((AbstractCard)upgradableCards.get(0)).upgrade();
                cardMetrics.add(((AbstractCard)upgradableCards.get(0)).cardID);
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards.get(0)).makeStatEquivalentCopy()));
            } else {
                ((AbstractCard)upgradableCards.get(0)).upgrade();
                ((AbstractCard)upgradableCards.get(1)).upgrade();
                cardMetrics.add(((AbstractCard)upgradableCards.get(0)).cardID);
                cardMetrics.add(((AbstractCard)upgradableCards.get(1)).cardID);
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards

                        .get(0)).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F - 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));


                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards

                        .get(1)).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F + 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));
            }
        }
        //TODO: log

        //AbstractEvent.logMetric("Shining Light", "Entered Light", null, null, null, cardMetrics, null, null, null, this.damage, 0, 0, 0, 0, 0);
    }




}


