package BoardGame.cards.BGCurse;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import BoardGame.events.BGSensoryStone;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGParasite extends AbstractBGCard {
    public static final String ID = "BGParasite";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGParasite");

    public BGParasite() {
        super("BGParasite", cardStrings.NAME, "curse/parasite", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.CURSE, BGCurse.Enums.BG_CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.NONE);
    }







    public void use(AbstractPlayer p, AbstractMonster m) {}







    public void onRemoveFromMasterDeck() {
        AbstractDungeon.player.damage(new DamageInfo(null, 2, DamageInfo.DamageType.HP_LOSS));
        CardCrawlGame.sound.play("BLOOD_SWISH");
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGParasite();
    }
}


