//package BoardGame.cards.BGStatus;
//
//import BoardGame.actions.AttemptAutoplayCardAction;
//import BoardGame.cards.AbstractBGCard;
//import BoardGame.cards.CardDisappearsOnExhaust;
//import BoardGame.characters.BGColorless;
//import BoardGame.characters.BGIronclad;
//import com.megacrit.cardcrawl.actions.AbstractGameAction;
//import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.characters.AbstractPlayer;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.localization.CardStrings;
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//public class BGDesync extends AbstractBGCard  {
//    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDesync");
//        static final String ID = "BGDesync";
//
//    public BGDesync() {
//        super("BGDesync", cardStrings.NAME, "status/void", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.NONE);
//
//    }
//
//
//    public void triggerWhenDrawn() {
//        AbstractDungeon.player.maxHealth=999;
//        AbstractDungeon.player.heal(999,false);
//    }
//
//
//
//    public void use(AbstractPlayer p, AbstractMonster m) {}
//
//
//
//    public void upgrade() {}
//
//
//    public AbstractCard makeCopy() {
//        return new BGDesync();
//    }
//}
//
//
