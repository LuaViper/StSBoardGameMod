package BoardGame.powers;

import BoardGame.monsters.bgbeyond.BGDarkling;
import BoardGame.monsters.bgexordium.BGSlimeBoss;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGRegrowPower extends AbstractPower {
    public static final String POWER_ID = "BGRegrowPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGRegrowPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGRegrowPower(AbstractCreature owner) {
        Logger logger = LogManager.getLogger(BGRegrowPower.class.getName());
        if(!(owner instanceof BGDarkling)){
            logger.info("WARNING: tried to apply BGRegrowPower to something other than a Darkling; things will probably break");
        }
        this.name = NAME;
        this.ID = "BGRegrowPower";
        this.owner = owner;
        updateDescription();
        loadRegion("regrow");
        this.type = AbstractPower.PowerType.BUFF;
    }





    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}


