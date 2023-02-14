package BoardGame.powers;

import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGTheDiePower extends AbstractPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:TheDiePower");
    public static final String POWER_ID = "BGTheDiePower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture("BoardGameResources/images/powers/loadeddie_power84.png");
    private static final Texture tex32 = TextureLoader.getTexture("BoardGameResources/images/powers/loadeddie_power32.png");


    public BGTheDiePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGTheDiePower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        this.type = AbstractPower.PowerType.BUFF;

        this.isTurnBased = false;

    }



    public void stackPower(int stackAmount) {
        this.amount=stackAmount;
        this.fontScale = 8.0F;
    }


    public void reducePower(int reduceAmount) {
        this.amount = reduceAmount;
        this.fontScale = 8.0F;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
