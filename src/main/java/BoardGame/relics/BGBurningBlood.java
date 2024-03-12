package BoardGame.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import BoardGame.BoardGame;
import BoardGame.util.TextureLoader;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static BoardGame.BoardGame.makeRelicOutlinePath;
import static BoardGame.BoardGame.makeRelicPath;

public class BGBurningBlood extends AbstractBGRelic {



    // ID, images, text.
    public static final String ID = BoardGame.makeID("BurningBlood");

    //private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BGburningBlood.png"));
    //private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("BGburningBlood.png"));

    public BGBurningBlood() {
        super(ID,
                "burningBlood.png",
                RelicTier.STARTER, LandingSound.MAGICAL);
    }
    private static final int HEALTH_AMT = 1;

    public AbstractRelic makeCopy() {
        return new BGBurningBlood();
    }


    @Override
    public void onVictory() {
        flash();
        addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        AbstractPlayer p = AbstractDungeon.player;
        if (p.currentHealth > 0) {
            p.heal(HEALTH_AMT);
        }
    }

    // Description
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
