package BoardGame.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;

public interface DieControlledRelic extends ClickableRelic {
    //public void dieProposal(int roll);
    //public void dieLockAndActivate(int roll);

    public void checkDieAbility();
    public void activateDieAbility();

    public String getQuickSummary();


    public static String RIGHT_CLICK_TO_ACTIVATE = " NL #bRight-Click to activate.";
    public static String USED_THIS_COMBAT = " NL This relic is on cooldown.";
    public static String USED_THIS_TURN = " NL This relic is on cooldown.";
    public static String DIE_LOCKED = " NL The die roll is already locked in.";






}
