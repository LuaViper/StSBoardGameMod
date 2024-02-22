package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class StrIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Str";    //reminder: "Icon" is automatically added
    private static StrIcon singleton;

    public StrIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/str.png"));
    }

    public static StrIcon get()
    {
        if (singleton == null) {
            singleton = new StrIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:StrIcon] Strength","Deal +1 damage on each [BoardGame:HitIcon] ."));
        return list;
    }
}