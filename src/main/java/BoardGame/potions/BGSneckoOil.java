package BoardGame.potions;

import BoardGame.cards.BGStatus.BGDazed;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGSneckoOil extends AbstractPotion {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGSneckoOil");

    public static final String POTION_ID = "BGSneckoOil";


    public BGSneckoOil() {
        super(potionStrings.NAME, "BGSneckoOil", AbstractPotion.PotionRarity.RARE, AbstractPotion.PotionSize.SNECKO, AbstractPotion.PotionColor.SNECKO);
        this.isThrown = false;
    }

    public int getPrice() {return 3;}

    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0] + this.potency + potionStrings.DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, this.potency));
            //addToBot((AbstractGameAction)new RandomizeHandCostAction());
            addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 2, false, true));
        }
    }


    public int getPotency(int ascensionLevel) {
        return 5;
    }


    public AbstractPotion makeCopy() {
        return new BGSneckoOil();
    }
}


