package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import java.util.ArrayList;
import java.util.List;

//TODO: Miracle icon should be scaled up if possible
public class MiracleIcon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:Miracle"; //reminder: "Icon" is automatically added
    private static MiracleIcon singleton;

    public MiracleIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/miracle.png"));
    }

    public static MiracleIcon get() {
        if (singleton == null) {
            singleton = new MiracleIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> list = new ArrayList<>();
        list.add(new TooltipInfo("[CoopBoardGame:MiracleIcon] Miracle", "Gain [W] ."));
        return list;
    }
}
