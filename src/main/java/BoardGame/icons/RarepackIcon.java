package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class RarepackIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Rarepack";    //reminder: "Icon" is automatically added
    private static RarepackIcon singleton;

    public RarepackIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/rarepack.png"));
    }

    public static RarepackIcon get()
    {
        if (singleton == null) {
            singleton = new RarepackIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:RarepackIcon] Rare Reward","Reveal 3 rare rewards. Add 1 to your deck or skip."));
        return list;
    }
}