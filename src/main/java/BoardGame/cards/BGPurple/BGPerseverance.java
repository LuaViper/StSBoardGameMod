package BoardGame.cards.BGPurple;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

//TODO: activated status does not work with Play Twice effects. should it?
public class BGPerseverance extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGPerseverance");
    public static final String ID = "BGPerseverance";

    public BGPerseverance() {
        super("BGPerseverance", cardStrings.NAME, "purple/skill/perseverance", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);

        this.selfRetain=true;
        this.baseBlock=1;
        this.baseMagicNumber=2;
        this.magicNumber=this.baseMagicNumber;
    }

    public void triggerOnGlowCheck() {
        this
                .glowColor = shouldGlow() ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    private boolean shouldGlow() {
        if(activated)
                return true;

        return false;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
        if(this.activated){
            addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.magicNumber));
        }
        this.activated=false;
    }

    protected void applyPowersToBlock() {
        super.applyPowersToBlock();
        float tmp=this.baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers)
            tmp = p.modifyBlock(tmp, this);
        for (AbstractPower p : AbstractDungeon.player.powers)
            tmp = p.modifyBlockLast(tmp);
        if (this.baseMagicNumber != MathUtils.floor(tmp))
            this.isMagicNumberModified = true;
        if (tmp < 0.0F)
            tmp = 0.0F;
        this.magicNumber = MathUtils.floor(tmp);
    }


    public boolean activated;
    public void onRetained(){
        this.activated=true;
    }

    public void onResetBeforeMoving() {
        //BoardGame.BoardGame.logger.info("Outmaneuver.onMoveToDiscard");
        this.activated=false;
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGPerseverance();
    }
}



