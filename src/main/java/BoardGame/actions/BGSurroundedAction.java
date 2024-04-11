package BoardGame.actions;

import BoardGame.cards.BGColorless.BGShieldSpear_Shield;
import BoardGame.cards.BGColorless.BGShieldSpear_Spear;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

//TODO: consider shield/spear animations for Facing abilities (might have to be different for multicharacter games)

public class BGSurroundedAction extends AbstractGameAction {

    private static final CardStrings SPEAR = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShieldSpear_Spear");
    private static final CardStrings SHIELD = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShieldSpear_Shield");

    @Override
    public void update() {
        isDone=true;
        ArrayList<AbstractCard> choices = new ArrayList<>();
        {
            AbstractCard c = new BGShieldSpear_Shield();
            AbstractMonster shield=AbstractDungeon.getMonsters().monsters.get(1);
            //AbstractMonster m2=AbstractDungeon.getMonsters().monsters.get(0);
            c.name="Dodge Spear"; //TODO: localization
            if(shield.isDying || shield.isDead || shield.halfDead){
                c.rawDescription=SHIELD.EXTENDED_DESCRIPTION[3];
            }else {
                if (shield.nextMove == 0) {
                    c.rawDescription=SHIELD.EXTENDED_DESCRIPTION[0];
                } else if (shield.nextMove == 1) {
                    c.rawDescription=SHIELD.EXTENDED_DESCRIPTION[1];
                } else if (shield.nextMove == 2) {
                    c.rawDescription=SHIELD.EXTENDED_DESCRIPTION[2];
                }else{
                    c.rawDescription=SHIELD.EXTENDED_DESCRIPTION[0];
                }
            }
            c.initializeDescription();
            choices.add(c);
        }
        {
            AbstractCard c = new BGShieldSpear_Spear();
            AbstractMonster spear=AbstractDungeon.getMonsters().monsters.get(0);
            c.name="Dodge Shield"; //TODO: localization
            if(spear.isDying || spear.isDead || spear.halfDead){
                return;
            }else {
                c.rawDescription=SPEAR.EXTENDED_DESCRIPTION[0];
            }
            c.initializeDescription();
            choices.add(c);
        }
        AbstractDungeon.cardRewardScreen.chooseOneOpen(choices);
    }
}
