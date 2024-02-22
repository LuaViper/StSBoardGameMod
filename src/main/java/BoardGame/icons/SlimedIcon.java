package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class SlimedIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Slimed";    //reminder: "Icon" is automatically added
    private static SlimedIcon singleton;

    public SlimedIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/slime.png"));
    }

    public static SlimedIcon get()
    {
        if (singleton == null) {
            singleton = new SlimedIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:SlimedIcon] Slimed","Put in your discard pile. NL Play for [E] to Exhaust."));
        return list;
    }
}