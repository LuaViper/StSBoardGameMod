//package BoardGame.shop;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.MathUtils;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.helpers.ImageMaster;
//import com.megacrit.cardcrawl.relics.AbstractRelic;
//import com.megacrit.cardcrawl.shop.StoreRelic;
//
//public class OnSaleTagForRelics
//{
//    public AbstractRelic relic;
//    private static final float W = 128.0F * Settings.scale;
//    public static Texture img = null;
//
//    public OnSaleTagForRelics(AbstractRelic r) {
//        this.relic = r;
//        if (img == null) {
//            switch (Settings.language) {
//                case DEU:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/deu.png");
//                    return;
//                case EPO:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/epo.png");
//                    return;
//                case FIN:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/fin.png");
//                    return;
//                case FRA:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/fra.png");
//                    return;
//                case ITA:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/ita.png");
//                    return;
//                case JPN:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/jpn.png");
//                    return;
//                case KOR:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/kor.png");
//                    return;
//                case RUS:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/rus.png");
//                    return;
//                case THA:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/tha.png");
//                    return;
//                case UKR:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/ukr.png");
//                    return;
//                case ZHS:
//                    img = ImageMaster.loadImage("images/npcs/sale_tag/zhs.png");
//                    return;
//            }
//            img = ImageMaster.loadImage("images/npcs/sale_tag/eng.png");
//        }
//    }
//
//
//
//    public void render(SpriteBatch sb) {
//        sb.setColor(Color.WHITE);
//        sb.draw(img, this.relic.currentX + 30.0F * Settings.scale + (this.relic.scale - 0.75F) * 60.0F * Settings.scale, this.relic.currentY + 70.0F * Settings.scale + (this.relic.scale - 0.75F) * 90.0F * Settings.scale, W * this.relic.scale, W * this.relic.scale);
//
//
//
//
//
//        sb.setBlendFunction(770, 1);
//        sb.setColor(new Color(1.0F, 1.0F, 1.0F, (MathUtils.cosDeg((float)(System.currentTimeMillis() / 5L % 360L)) + 1.25F) / 3.0F));
//        sb.draw(img, this.relic.currentX + 30.0F * Settings.scale + (this.relic.scale - 0.75F) * 60.0F * Settings.scale, this.relic.currentY + 70.0F * Settings.scale + (this.relic.scale - 0.75F) * 90.0F * Settings.scale, W * this.relic.scale, W * this.relic.scale);
//
//
//
//
//
//        sb.setBlendFunction(770, 771);
//    }
//}
//
//
