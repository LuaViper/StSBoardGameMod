# StSBoardGameMod
Video game adaptation of the board game adaptation of the video game.
Based on the StS-Default Mod Base, because we didn't find BasicMod until it was too late.

# Disclaimer
This mod was thrown together haphazardly with no regard whatsoever for proper coding practices or internal consistency. Contribute at your own risk.

# Technical faults
- Savefiles are not supported yet.  Attempting to load a saved game will, in the best possible case, reroll dice RNG, reset the card reward decks (allowing you to receive duplicate cards), and replace all of your potions with vanilla 20-damage Fire Potions.
- Quick Start interface is hilariously broken in several ways -- in many cases, the proceed button will not appear. You can usually escape a softlock by opening and closing the map screen. Additionally, it's possible to escape Quick Start completely and begin Act 1 with all of your bonuses
- Sever Soul+'s confirm button is unavailable until you select 2 cards.  (You can still unselect one card, then confirm.)
- Lab doesn't open the map screen after taking potions (can escape softlock by opening map screen yourself)
- Some events can softlock if you don't have enough cards
- Some events don't check if you can afford to pay for them
- "Low health" effect only plays at 1HP (2-3HP would be better)
- The Guardian, during phase transition, will occasionally display its intent text overhead as if it were an acquired buff 
- Golden Ticket is visible during the Transform animation
- Cards use the wrong cardUI graphics in some situations
- Potions aren't registered to the compendium / debug console
- Intent damage icons break (revert to swords) at very high numbers
- Unupgradeable cards are displayed as upgradeable in the compendium
- Status cards are listed on the compendium's vanilla-colorless page
- Energy symbol in relic tooltips is the wrong color
- Score bonuses are calculated incorrectly (game doesn't know which floors are boss floors)

# Board Game inaccuracies
- Several cards are marked with the "Approximate" keyword to denote they don't behave *exactly* like the board game
- Physical token limits are not yet implemented
- Non-card-reward decks (monsters, potions, relics etc) can produce duplicates of unique cards
- Some potion-granting events still try to add a potion directly to your inventory (prevents you from first discarding one if you're full)
- Curses are still transformable
- Merchant card removal tooltip still says "the cost of this service increases by 25 for the rest of the run"
- If Ironclad plays an Exhausting Skill card vs Gremlin Nob while Feel No Pain is active, Gremlin Nob's Anger procs before Feel No Pain adds Block


# Glaring omissions
- A few colorless cards have not been implemented -- most have effects which do not quite match vanilla
- Several potions have not been implemented
- About half of the relics have not been implemented (hardcoded effects, effects which target enemies, die-changing relics)
- Several events are missing
- Non-vanilla events (and even some vanilla events) do not log results to run history
- Some strings are currently hardcoded instead of placed in a localization json
