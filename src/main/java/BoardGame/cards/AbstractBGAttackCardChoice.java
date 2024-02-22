package BoardGame.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class AbstractBGAttackCardChoice extends AbstractBGCard {
    protected AbstractPlayer p;
    protected AbstractMonster m;

    public AbstractBGAttackCardChoice(final String id,
                                      final String name,
                                      final String img,
                                      final int cost,
                                      final String rawDescription,
                                      final CardType type,
                                      final CardColor color,
                                      final CardRarity rarity,
                                      final CardTarget target) {

        super(id, name, img, cost, rawDescription, type, color, rarity, target);

        // Set all the things to their default values.
        isCostModified = false;
        isCostModifiedForTurn = false;
        isDamageModified = false;
        isBlockModified = false;
        isMagicNumberModified = false;
    }


    public void setTargets(AbstractPlayer p, AbstractMonster m){
        this.p=p;
        this.m=m;
    }
}
