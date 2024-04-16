# StSBoardGameMod
Video game adaptation of the board game adaptation of the video game.
Based on the StS-Default Mod Base, because we didn't find BasicMod until it was too late.

# Disclaimer
This mod was thrown together haphazardly with no regard whatsoever for proper coding practices or internal consistency. Contribute at your own risk.

# Technical faults
- Quick Start interface is hilariously broken in several ways -- in many cases, the proceed button will not appear. You can usually escape a softlock by opening and closing the map screen. Additionally, it's possible to escape Quick Start completely and begin Act 1 with all of your bonuses
- Some events can softlock if you don't have enough cards
- Some events don't check if you can afford to pay for them
- "Low health" effect only plays at 1HP (2-3HP would be better)
- Golden Ticket is visible during the Transform animation
- Cards use the wrong cardUI graphics in some situations
- Unupgradeable cards are displayed as upgradeable in the compendium
- Score bonuses are calculated incorrectly (game doesn't know which floors are boss floors)
- Strange things can happen if an Egg relic is used up on the shop screen

# Board Game inaccuracies
- Physical token limits are not yet implemented
- Non-card-reward decks (monsters, potions, relics etc) can produce duplicates of unique cards
- If Ironclad plays an Exhausting Skill card vs Gremlin Nob while Feel No Pain is active, Gremlin Nob's Anger procs before Feel No Pain adds Block

# Glaring omissions
- Non-vanilla events (and even some vanilla events) do not log results to run history
- Some strings are currently hardcoded instead of placed in a localization json
``