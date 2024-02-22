package BoardGame.icons;

import BoardGame.util.TextureLoader;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

public class Die4Icon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Die4";    //reminder: "Icon" is automatically added
    private static Die4Icon singleton;

    public Die4Icon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/die4.png"));
    }

    public static Die4Icon get()
    {
        if (singleton == null) {
            singleton = new Die4Icon();
        }
        return singleton;
    }

}