package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

public class Die3Icon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:Die3"; //reminder: "Icon" is automatically added
    private static Die3Icon singleton;

    public Die3Icon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/die3.png"));
    }

    public static Die3Icon get() {
        if (singleton == null) {
            singleton = new Die3Icon();
        }
        return singleton;
    }
}
