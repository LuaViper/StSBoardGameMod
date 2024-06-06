package BoardGame.actions;

import BoardGame.cards.BGBlue.BGClaw2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGCoreSurgeAttackAction extends AbstractGameAction {
    private AbstractCard card;
    private AbstractPlayer p;
    private AbstractMonster m;

    public BGCoreSurgeAttackAction(AbstractCard card, AbstractPlayer p, AbstractMonster m) {
        this.card = card;
        this.p=p;
        this.m = m;
    }

    public void update() {
        card.applyPowers();
        card.calculateCardDamage(m);
        addToBot(new DamageAction(m, new DamageInfo(p, card.damage, card.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        this.isDone = true;
    }
}


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\SlayTheSpire\desktop-1.0.jar!\com\megacrit\cardcrawl\actions\defect\GashAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */