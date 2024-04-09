package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class VulnIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Vuln";    //reminder: "Icon" is automatically added
    private static VulnIcon singleton;

    public VulnIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/vuln.png"));
    }

    public static VulnIcon get()
    {
        if (singleton == null) {
            singleton = new VulnIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:VulnIcon] Vulnerable","Deal 2x damage on each [BoardGame:HitIcon] in the next Attack, then remove a [BoardGame:VulnIcon] token."));
        return list;
    }
}