package BoardGame.actions;

import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class BGDarkShacklesAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("OpeningAction");
    public static final String[] TEXT = uiStrings.TEXT;

    private int blockAmount;
    private AbstractPlayer player;

    public BGDarkShacklesAction(int blockAmount, AbstractPlayer player) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.blockAmount = blockAmount;
        this.player=player;
        //this.targetMonster = m;
    }


    public void update() {
        for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            //avoid attacking dead monsters (and corresponding visual fx)
            if (!mo.isDying && !mo.isDead && !mo.halfDead ) {
                if (mo.getIntentBaseDmg() >= 0) {
                    EnemyMoveInfo move = AbstractBGMonster.PublicMoveField.publicmove.get(mo);
                    if (move != null) {
                        addToBot((AbstractGameAction) new GainBlockAction((AbstractCreature) player, this.blockAmount));
                    }
                }
            }
        }
        this.isDone = true;


    }
}


