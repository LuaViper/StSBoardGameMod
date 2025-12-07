package CoopBoardGame.cards.BGRed;

import CoopBoardGame.actions.BGFeedAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGIronclad;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class BGFeed extends AbstractBGCard {

    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGFeed"
    );
    public static final String ID = "BGFeed";

    private static final int DAMAGE = 3;
    private static final int STRENGTH_GAIN = 1;
    private static final int UPGRADE_STRENGTH_BONUS = 1;

    private static final float BITE_EFFECT_DURATION = 0.3F;
    private static final float BITE_EFFECT_Y_OFFSET = 40.0F;

    public BGFeed() {
        super(
            ID,
            CARD_STRINGS.NAME,
            "red/attack/feed",
            1,
            CARD_STRINGS.DESCRIPTION,
            CardType.ATTACK,
            BGIronclad.Enums.BG_RED,
            CardRarity.RARE,
            CardTarget.ENEMY
        );
        this.baseDamage = DAMAGE;
        this.exhaust = true;
        this.baseMagicNumber = STRENGTH_GAIN;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer player, AbstractMonster target) {
        playBiteEffect(target);
        dealFeedDamage(player, target);
    }

    private void playBiteEffect(AbstractMonster target) {
        if (target != null) {
            addToBot(
                new VFXAction(
                    new BiteEffect(
                        target.hb.cX,
                        target.hb.cY - BITE_EFFECT_Y_OFFSET * Settings.scale,
                        Color.SCARLET.cpy()
                    ),
                    BITE_EFFECT_DURATION
                )
            );
        }
    }

    private void dealFeedDamage(AbstractPlayer player, AbstractMonster target) {
        addToBot(
            new BGFeedAction(
                target,
                new DamageInfo(player, this.damage, this.damageTypeForTurn),
                this.magicNumber
            )
        );
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_STRENGTH_BONUS);
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGFeed();
    }
}
