package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class MiracleIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Miracle";    //reminder: "Icon" is automatically added
    private static MiracleIcon singleton;

    public MiracleIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/miracle.png"));
    }

    public static MiracleIcon get()
    {
        if (singleton == null) {
            singleton = new MiracleIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:MiracleIcon] Miracle","Gain [W] ."));
        return list;
    }

}