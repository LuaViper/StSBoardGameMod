package BoardGame.relics;
import BoardGame.dungeons.BGTheCity;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGSecretPortalRelic extends AbstractBGRelic {
    public static final String ID = "BGSecretPortalRelic";

    private static final Logger logger = LogManager.getLogger(BGSecretPortalRelic.class.getName());

    public BGSecretPortalRelic() {
        super("BGSecretPortalRelic", "winged.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
        this.counter = -2;
    }
    public int getPrice() {return 9999;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void setCounter(int setCounter) {
        this.counter = -2;
    }



    public AbstractRelic makeCopy() {
        return new BGSecretPortalRelic();
    }


}


