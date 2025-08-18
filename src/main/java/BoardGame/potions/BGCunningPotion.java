
package BoardGame.potions;

import BoardGame.actions.BGUseShivAction;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGCunningPotion
        extends AbstractPotion {
    public static final String POTION_ID = "BGCunningPotion";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGCunningPotion");

    //TODO: decide whether we want this to proc Curl Up between attacks (currently waiting for all attacks to resolve)

    public BGCunningPotion() {
        super(potionStrings.NAME, "BGCunningPotion", AbstractPotion.PotionRarity.UNCOMMON, AbstractPotion.PotionSize.SPIKY, AbstractPotion.PotionEffect.NONE, Color.GRAY, Color.DARK_GRAY, null);
        this.labOutlineColor = Settings.GREEN_RELIC_COLOR;
        this.isThrown=false;
    }

    public int getPrice() {return 3;}
    public int getPotency(int ascensionLevel) {
        return 3;
    }
    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        AbstractPlayer abstractPlayer = AbstractDungeon.player;
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            for(int i=0;i<potency;i+=1){
                addToBot((AbstractGameAction) new BGUseShivAction(false,false,0,"Choose a target for Cunning Potion."));
            }
        }
    }

    



    public AbstractPotion makeCopy() {
        return new BGCunningPotion();
    }
}


