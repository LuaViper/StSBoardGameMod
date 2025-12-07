package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

//TODO: icon needs some serious contrast/readability work

public class GoldIcon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:Gold"; //reminder: "Icon" is automatically added
    private static GoldIcon singleton;

    public GoldIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/gold.png"));
    }

    public static GoldIcon get() {
        if (singleton == null) {
            singleton = new GoldIcon();
        }
        return singleton;
    }
}
