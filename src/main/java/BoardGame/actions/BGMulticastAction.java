package BoardGame.actions;

import BoardGame.screen.OrbSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class BGMulticastAction extends AbstractGameAction {
    public String description;
    public int[] multiDamage;
    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private int extrahits=0;
    private AbstractMonster m;

    private DamageInfo.DamageType damageType;
    private int damage;
    private DamageInfo.DamageType damageTypeForTurn;

    private AbstractPlayer p;
    public BGMulticastAction(AbstractPlayer p, AbstractMonster m, int damage, DamageInfo.DamageType damageTypeForTurn, boolean dontExpendResources, int energyOnUse, int extrahits) {
        this.p = p;
        this.m=m;
        this.damage = damage;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.damageTypeForTurn = damageTypeForTurn;
        this.energyOnUse = energyOnUse;
        this.extrahits=extrahits;
    }

    public void update() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }
        effect += this.extrahits;

        if (this.p.hasRelic("Chemical X")) {
            effect += 2;
            this.p.getRelic("Chemical X").flash();
        }

        if (effect > 0) {
            int finalEffect = effect;
            OrbSelectScreen.OrbSelectAction ossAction = (target) -> {
                AbstractPlayer player = AbstractDungeon.player;
                //addToTop -- reverse order
                BoardGame.BoardGame.logger.info("BGEvokeOrbMulticastAction: slot " + target);
                addToTop((AbstractGameAction) new BGEvokeSpecificOrbAction(target));
                for(int i = 0; i<finalEffect-1; i+=1) {
                    addToTop((AbstractGameAction) new BGEvokeWithoutRemovingSpecificOrbAction(target));
                }
            };
            //TODO: localization
            addToTop((AbstractGameAction) new OrbSelectScreenAction(ossAction, "Choose an Orb to Multi-Cast.", false));
            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }else{
            //do nothing; this is not an Attack card so we don't need to WEAKVULN
        }
        this.isDone = true;
    }
}
