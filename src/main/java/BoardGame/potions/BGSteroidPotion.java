package BoardGame.potions;
import BoardGame.actions.GainTemporaryStrengthIfNotCappedAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGSteroidPotion extends AbstractPotion {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGFlexPotion");

    public static final String POTION_ID = "BGSteroidPotion";


    public BGSteroidPotion() {
        super(potionStrings.NAME, "BGSteroidPotion", AbstractPotion.PotionRarity.COMMON, AbstractPotion.PotionSize.FAIRY, AbstractPotion.PotionColor.STEROID);
        this.isThrown = false;
    }

    public int getPrice() {return 2;}

    public void initializeData() {
        this.potency = getPotency();
        //this.description = potionStrings.DESCRIPTIONS[0] + this.potency + potionStrings.DESCRIPTIONS[1] + this.potency + potionStrings.DESCRIPTIONS[2];
        this.description = potionStrings.DESCRIPTIONS[0];


        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.tips.add(new PowerTip(

                TipHelper.capitalize(GameDictionary.STRENGTH.NAMES[0]), (String)GameDictionary.keywords
                .get(GameDictionary.STRENGTH.NAMES[0])));
    }


    public void use(AbstractCreature target) {
        AbstractPlayer abstractPlayer = AbstractDungeon.player;
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot(new GainTemporaryStrengthIfNotCappedAction(abstractPlayer,potency));
        }
    }



    public int getPotency(int ascensionLevel) {
        return 1;
    }


    public AbstractPotion makeCopy() {
        return new BGSteroidPotion();
    }
}


