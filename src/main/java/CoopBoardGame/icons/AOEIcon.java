package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import java.util.ArrayList;
import java.util.List;

//TODO: consider adding a CoopBoardGame:SummonIcon for bestiary/multicharintent graphics
public class AOEIcon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:AOE"; //reminder: "Icon" is automatically added
    private static AOEIcon singleton;

    public AOEIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/aoe.png"));
    }

    public static AOEIcon get() {
        if (singleton == null) {
            singleton = new AOEIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> list = new ArrayList<>();
        list.add(
            new TooltipInfo(
                "[CoopBoardGame:AOEIcon] Area of Effect",
                "Affects all enemies in a row and the boss."
            )
        );
        return list;
    }
}
