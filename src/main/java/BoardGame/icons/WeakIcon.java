package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class WeakIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Weak";    //reminder: "Icon" is automatically added
    private static WeakIcon singleton;

    public WeakIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/weak.png"));
    }

    public static WeakIcon get()
    {
        if (singleton == null) {
            singleton = new WeakIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:WeakIcon] Weak","Deal -1 damage on each [BoardGame:HitIcon] in the next Attack, then remove a [BoardGame:WeakIcon] token."));
        return list;
    }
}