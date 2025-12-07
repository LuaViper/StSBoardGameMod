package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import java.util.ArrayList;
import java.util.List;

public class BurnIcon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:Burn"; //reminder: "Icon" is automatically added
    private static BurnIcon singleton;

    public BurnIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/burn.png"));
    }

    public static BurnIcon get() {
        if (singleton == null) {
            singleton = new BurnIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> list = new ArrayList<>();
        list.add(
            new TooltipInfo(
                "[CoopBoardGame:BurnIcon] Burn",
                "Put in your discard pile. NL Unplayable. NL End of turn: Take 1 damage if this is in your hand."
            )
        );
        return list;
    }
}
