package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import java.util.ArrayList;
import java.util.List;

public class StrIcon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:Str"; //reminder: "Icon" is automatically added
    private static StrIcon singleton;

    public StrIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/str.png"));
    }

    public static StrIcon get() {
        if (singleton == null) {
            singleton = new StrIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> list = new ArrayList<>();
        list.add(
            new TooltipInfo(
                "[CoopBoardGame:StrIcon] Strength",
                "Deal +1 damage on each [CoopBoardGame:HitIcon] ."
            )
        );
        return list;
    }
}
