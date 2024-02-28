package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.powers.BGPoisonPower;
import BoardGame.screen.OrbSelectScreen;
import BoardGame.screen.TargetSelectScreen;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces.BranchingUpgradesPatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.defect.EvokeOrbAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.Collections;

//TODO: showEvokeOrbCount needs a patch
//TODO: prevent AnimateOrbAction until we know WHICH orb is evoking

public class BGEvokeSpecificOrbAction extends AbstractGameAction {
    int orbSlot;
    public BGEvokeSpecificOrbAction(int orbSlot) {
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }
        this.orbSlot=orbSlot;
        this.duration = this.startDuration;
        this.actionType = ActionType.DAMAGE;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            AbstractPlayer player = AbstractDungeon.player;
            if (player != null) {
                if (player.orbs.size() > orbSlot && !(player.orbs.get(orbSlot) instanceof EmptyOrbSlot)) {
                    AbstractDungeon.player.triggerEvokeAnimation(orbSlot);
                    ((AbstractOrb) player.orbs.get(orbSlot)).onEvoke();
                    EmptyOrbSlot emptyOrbSlot = new EmptyOrbSlot();
                    int i;
                    for(i=orbSlot+1; i<player.orbs.size(); i++)
                        Collections.swap(player.orbs, i, i - 1);
                    player.orbs.set(player.orbs.size() - 1, emptyOrbSlot);
                    for (i = 0; i < player.orbs.size(); i++)
                        ((AbstractOrb)player.orbs.get(i)).setSlot(i, player.maxOrbs);
                }
            }
        }
        this.tickDuration();
    }





}
