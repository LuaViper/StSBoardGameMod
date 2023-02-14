package BoardGame.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.DamageInfo;

public class WeakVulnCancel
{
    // An eccentric damage type which is used when an attack card hits 0 times.
    // Procs Weak+Vulnerable but nothing else.  Preferably deals 0 damage.
    @SpireEnum
    public static DamageInfo.DamageType WEAKVULN_ZEROHITS;

}