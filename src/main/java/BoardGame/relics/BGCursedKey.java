package BoardGame.relics;

import BoardGame.dungeons.AbstractBGDungeon;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGCursedKey
        extends AbstractBGRelic  {
    public BGCursedKey() {
        super("BGCursed Key", "cursedKey.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
    }
    public static final String ID = "BGCursed Key";

    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return setDescription(AbstractDungeon.player.chosenClass);
        }
        return setDescription((AbstractPlayer.PlayerClass)null);
    }





    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[1] + this.DESCRIPTIONS[0];
    }





    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }


    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster++;
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for(int i=0;i<2;i+=1) {
            AbstractCard randomcurse = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
            AbstractBGDungeon.removeCardFromRewardDeck(randomcurse);
            UnlockTracker.markCardAsSeen(((AbstractCard) randomcurse).cardID);
            group.addToBottom(randomcurse.makeCopy());
        }

        AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, this.DESCRIPTIONS[2]);
        CardCrawlGame.sound.playA("BELL", MathUtils.random(-0.2F, -0.3F));

    }


    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster--;
    }


    public AbstractRelic makeCopy() {
        return new BGCursedKey();
    }
}


