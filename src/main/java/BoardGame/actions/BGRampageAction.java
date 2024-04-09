package BoardGame.actions;

import BoardGame.cards.BGRed.BGRampage;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGRampageAction extends AbstractGameAction {

    private AbstractMonster m;
    private AbstractPlayer p;
    private BGRampage card;
    public BGRampageAction(AbstractMonster m, AbstractPlayer p, BGRampage card){
        this.m=m;
        this.p=p;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.DAMAGE;
        this.card=card;
    }

    @Override
    public void update() {
        card.calculateCardDamage(m);
        addToTop((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, card.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        this.isDone=true;
    }
}
