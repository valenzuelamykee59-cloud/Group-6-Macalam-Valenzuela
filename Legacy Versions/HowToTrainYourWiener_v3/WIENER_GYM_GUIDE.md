# WIENER GYM IDLE

Single-file Java idle game inspired by the cozy desktop layout and feature mix of "How to Train Your Cock", reworked around a dachshund training in a powerlifting gym with switchable scenery.

## Run

```bash
javac WienerGymIdle.java
java WienerGymIdle
```

Java 8 or newer is enough.

## Core Loop

- Pick one lift: `Squat`, `Bench`, or `Deadlift`.
- Or switch to the dedicated `Cardio` mode.
- Click the stage or use `Train Selected Lift`.
- Each lift spends its own energy pool.
- Energy recovers independently, so rotating lifts matters.
- `Cardio` always trickles in passive gains, even when lift energy is low.
- Clicking and lifting increases cardio pace, so idle gains speed up when you stay active.
- `Squat`, `Bench`, and `Deadlift` are still the main money makers. Cardio is intentionally much weaker.
- Bigger stats, cosmetics, scene bonuses, and egg shop upgrades push rep value higher over time.

## Visual Improvements In This Version

- The dog now uses more detailed dachshund-style body shapes instead of a generic stick figure.
- Squat uses a fixed rack, a shoulder-bar position, and an actual down-then-up squat path.
- Bench uses a fixed bench rack, lowers to the chest, pauses, then presses back up.
- Deadlift starts on the floor, rises to lockout, then returns under control.
- Cardio has its own treadmill-style stage view and click loop.
- The barbell width and plate count scale with strength, and the `Olympic Bar` unlock lets it stretch into absurd late-game proportions.
- The default stage is now a powerlifting gym with platform flooring, banners, plate stacks, props, companions, shadows, and animated ambience.

## Progression

Each lift tracks:

- `Power`
- `Endurance`
- `Recovery`
- `Tempo`
- `Total Reps`

Resources:

- `Gains`: main upgrade currency.
- `Eggs`: premium progression currency.
- `Size`: dog growth in centimeters.
- `Rank`: title based on dog size.

## Eggs

Eggs come from two places:

- Trade `500 gains` for `1 egg`.
- Hit size milestones. Once the dog gets big enough, milestone growth lays eggs automatically.

## New Feature Set

### Gym Shop

Bought with eggs:

- `Protein Shaker`: doubles all gains.
- `Gym Membership`: auto-trains all three lifts.
- `Olympic Bar`: unlocks the extra-long silly barbell.
- `Massage Table`: global recovery bonus.
- `Chalk Bucket`: global gains multiplier.

### Style Studio

Cosmetic hats:

- `Red Headband`
- `Lifeguard Cap`
- `Beach Crown`
- `None`

### Island Builder

Themes:

- `Power Gym`
- `Beach Pier`
- `Neon Festival`

Decor:

- `Beach Parasol`
- `Lantern String`
- `Trophy Rack`
- `Pool Float`

Companions:

- `Spotter Crab`: recovery bonus.
- `Seagull Coach`: gains bonus.
- `Otter Cameraman`: faster size growth.

### The Pounder

- Bet gains in a quick luck-based minigame.
- Stronger dogs get better odds.
- High rolls can return big gains and even bonus eggs.

### Photo Mode

- `Snap Photo` exports a PNG of the current stage to the project folder.

### Passive Cardio

- Cardio is always active, so the game never fully stalls when lift energy is empty.
- Cardio gains are intentionally much slower than `Squat`, `Bench`, and `Deadlift`.
- Cardio is also a dedicated selectable mode with its own clicks and upgrades.
- Pace rises from clicking and successful reps, then settles back down over time.
- If a selected SBD lift is empty, the game now hops to the next lift with energy. If none are available, it falls back to cardio automatically.
- The current cardio pace and passive gains per second are shown in the stage info footer and the cardio board.
- Early-game cardio is deliberately small so SBD stays the main progression path.

### Power Meets

- `Power Meet` opens a competition ladder from local meets up to `IPF Worlds`.
- Each meet needs a minimum strength score before it unlocks.
- Meet gameplay is a short spam-click segment.
- More clicks means more gains.
- Higher-tier meets apply bigger click multipliers and give larger burst payouts.

## Controls

- Click the stage to train.
- Use the bottom lift buttons to switch between `Squat`, `Bench`, and `Deadlift`.
- Use the `Cardio` button to enter the idle/clicker fallback mode directly.
- Use the right panel to upgrade the selected lift.
- Use the feature buttons for shop, cosmetics, scenery, meets, settings, The Pounder, and snapshots.

## Accessibility And QoL

The `Settings` button now includes:

- `High Contrast UI`
- `Large Text`
- `Reduce Motion`
- `Simplified Background`
- `Auto Rotate To Ready Lift`
- Custom dog name

## Notes

- Auto-training only activates after buying `Gym Membership`.
- Scenery, decorations, and buddies provide small but real progression bonuses.
- The event log on the left is meant for major milestones, not every single rep.
- The game is intentionally exaggerated late game, especially the barbell width and dog scale.
