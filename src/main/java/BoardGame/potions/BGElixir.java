package BoardGame.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGElixir extends AbstractPotion {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGElixirPotion");
    public static final String POTION_ID = "BGElixirPotion";

    public BGElixir() {
        super(potionStrings.NAME, "BGElixirPotion", AbstractPotion.PotionRarity.UNCOMMON, AbstractPotion.PotionSize.S, AbstractPotion.PotionColor.GREEN);
        this.labOutlineColor = Settings.RED_RELIC_COLOR;
        this.liquidColor= CardHelper.getColor(145,204,100);
        this.hybridColor = new Color(0,0,0,0);
        this.spotsColor = new Color(0,0,0,0);



        this.isThrown = false;
    }

    public int getPrice() {return 2;}


    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.tips.add(new PowerTip(

                TipHelper.capitalize(GameDictionary.EXHAUST.NAMES[0]), (String)GameDictionary.keywords
                .get(GameDictionary.EXHAUST.NAMES[0])));
    }


    public void use(AbstractCreature target) {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot((AbstractGameAction)new ExhaustAction(3, false, true, true));
        }
    }


    public int getPotency(int ascensionLevel) {
        return 0;
    }


    public AbstractPotion makeCopy() {
        return new BGElixir();
    }
}


