package BoardGame.icons;

import BoardGame.util.TextureLoader;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

public class Die6Icon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Die6";    //reminder: "Icon" is automatically added
    private static Die6Icon singleton;

    public Die6Icon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/die6.png"));
    }

    public static Die6Icon get()
    {
        if (singleton == null) {
            singleton = new Die6Icon();
        }
        return singleton;
    }

}