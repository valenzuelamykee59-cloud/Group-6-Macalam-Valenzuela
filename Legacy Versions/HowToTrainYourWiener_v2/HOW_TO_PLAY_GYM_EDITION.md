# 💪 HOW TO TRAIN YOUR WIENER — GYM EDITION — Player Guide

## Running the Game

Make sure you have Java installed (version 8 or higher). Then compile and run:

```bash
javac HowToTrainYourWiener_Improved.java
java HowToTrainYourWiener_Improved
```

---

## The Goal

Transform your wiener dog from a tiny **PUPPY** into a **LEGENDARY** bodybuilder, collect **10 trophies** to become the Ultimate Strongdog Champion, and most importantly — keep your dog's **morale high** or it's game over!

---

## What's New in Gym Edition?

### 🎨 **Improved UI/UX**
- **3-Column Layout**: Large gym visualization on left, clear stats in middle, organized actions on right
- **Better Visual Hierarchy**: Color-coded sections with clear borders and labels
- **Progress Bars**: Visual indicators for morale, energy, and sweat
- **Tooltips**: Hover over buttons to see costs and effects
- **Combo System**: Enhanced combo bar with visual feedback

### 💪 **Weightlifting Focus**
- **Three Core Lifts**: Squat, Bench Press, and Deadlift
- **Visual Weight Progression**: Watch the barbell grow from 45 lbs to absurdly heavy weights
- **Animated Lifting**: See your wiener actually perform squats, bench presses, and deadlifts
- **Silly Progression**: At high stats, the barbell extends across the entire screen with massive plates

### 🎮 **Streamlined Gameplay**
- Clearer resource names (Treats → Protein, Happiness → Morale, Mess → Sweat)
- Better balanced progression
- Improved shop with meaningful upgrades
- Enhanced random events themed around gym life

---

## The Five Stages

Your wiener evolves as your total lifting power (Squat + Bench + Deadlift) grows:

| Stage     | Total Power Needed | Appearance                    |
|-----------|-------------------|-------------------------------|
| PUPPY     | 0–12              | Light brown, small muscles    |
| YOUNG     | 13–25             | Medium brown, getting stronger|
| ADULT     | 26–40             | Dark brown, visible muscles   |
| CHAMPION  | 41–60             | Purple-ish, very muscular     |
| LEGENDARY | 61+               | Golden, MAXIMUM SWOLE         |

Every stage up gives you a **200 protein bonus** and changes your dog's appearance!

---

## Resources Explained

| Resource   | What It Does                                              |
|------------|-----------------------------------------------------------|
| 🥤 Protein | Main currency - used for training and everything else     |
| ⚡ +X/sec   | Passive protein income (auto-generated every second)      |
| 😊 Morale  | Must stay above 0% or you lose the game                   |
| ⚡ Energy  | Consumed by training - auto-refills when depleted         |
| 💦 Sweat   | Drains morale over time - take showers to reduce it!      |
| 🏆 Trophies| Win these in competitions. Get 10 to become champion!     |

---

## How to Get Protein

### 💪 Click to Lift
- Click the big **"💪 CLICK TO LIFT! 💪"** button in the gym area
- Earns protein based on your click power
- Builds combo multiplier (up to x30) - the faster you click, the bigger the bonus!
- Each click triggers a random lift animation (squat, bench, or deadlift)

### 🏋️ Passive Income
- Bench Press training gives you +2 protein/sec per level
- Gym Membership upgrade adds +10 protein/sec
- Automatic generation every second

### 🏆 Competitions
- Enter the Strongdog Championship for big rewards
- Win = huge protein payout + 1 trophy
- Lose = small consolation prize

### 🎲 Random Events
- Random gym events occur every few seconds
- Can give protein bonuses, stat increases, or penalties

---

## The Three Core Lifts

### 🏋️ SQUAT TRAINING
- **Base Cost**: 100 protein (scales with squat power)
- **Energy Cost**: 25
- **Cooldown**: 2.5 seconds
- **Benefits**: 
  - +1 Squat Power
  - +1 Click Power
  - +5 Sweat
- **Visual**: Dog performs squats with barbell on shoulders
- **Best for**: Building click power for more protein per click

### 💪 BENCH PRESS
- **Base Cost**: 85 protein (scales with bench power)
- **Energy Cost**: 25
- **Cooldown**: 2.5 seconds
- **Benefits**:
  - +1 Bench Power
  - +2 Protein/sec (passive income!)
  - +4 Sweat
- **Visual**: Dog bench presses the barbell up and down
- **Best for**: Building passive income for steady protein flow

### ⚡ DEADLIFT
- **Base Cost**: 120 protein (scales with deadlift power)
- **Energy Cost**: 25
- **Cooldown**: 2.5 seconds
- **Benefits**:
  - +1 Deadlift Power
  - +2 Click Power
  - +6 Sweat (most exhausting!)
- **Visual**: Dog lifts barbell from ground to standing position
- **Best for**: Maximum click power gains (but creates most sweat!)

**💡 Strategy Tip**: Training costs scale up as you get stronger, so the game gets progressively more expensive. Plan your training carefully!

---

## Actions Explained

### 🏆 STRONGDOG CHAMPIONSHIP
- **Cost**: 150 protein, 40 energy
- **Cooldown**: 20 seconds
- **Win Chance**: 30% base + total power + 15% if you have Personal Trainer
- **Win**: Big protein reward (300 + power × 20), +1 trophy, +10 morale
- **Lose**: Small consolation (50 protein), -15 morale

### 🚿 TAKE SHOWER
- **Cost**: 25 protein
- **Effect**: -30 sweat
- **When to use**: When sweat gets above 30-40 to prevent morale drain

### 🥤 PROTEIN SHAKE
- **Cost**: 40 protein
- **Effect**: +20 morale, +4 sweat
- **When to use**: When morale is dropping dangerously low

### 🛒 GYM EQUIPMENT SHOP
- One-time permanent upgrades
- Opens shop dialog with all available purchases
- Can only buy each item once

### 🌟 PRESTIGE RESET
- **Requirement**: 3+ trophies
- **Effect**: Reset all stats and upgrades, keep trophies, gain permanent bonuses
- **Bonus**: +2 base click power per prestige level

---

## The Shop — Permanent Upgrades

| Upgrade              | Cost   | Effect                                    |
|----------------------|--------|-------------------------------------------|
| 🥤 Protein Shaker    | 2,000  | 2x click power                           |
| 🏋️ Gym Membership    | 5,000  | +10 protein/sec                          |
| 👟 Personal Trainer  | 8,000  | +15% competition win rate                |
| 💪 Power Rack        | 12,000 | Training costs 20% less                  |
| 🌟 Olympic Barbell   | 18,000 | Unlock maximum weight progression        |
| 💉 Steroids (Legal)  | 25,000 | 2x all stat gains from training          |

**Recommended Buy Order**: Protein Shaker → Gym Membership → Power Rack → Personal Trainer → Olympic Barbell → Steroids

---

## Morale — The Real Challenge

**Morale is the only stat that can end your game.** If it hits 0%, you lose!

### 💔 What Drains Morale

**💦 Sweat — The Main Threat**
- Every second, the game calculates `sweat / 6` and subtracts from morale
- Drain scales with how sweaty your dog is:

| Sweat Level | Morale Lost Per Second |
|------------|------------------------|
| 12         | 2/sec                  |
| 30         | 5/sec                  |
| 60         | 10/sec                 |

At 60+ sweat, you can go from full morale to zero in just **10 seconds**!

**Sweat Sources**:
- Squat Training: +5 sweat
- Bench Press: +4 sweat
- Deadlift: +6 sweat (most exhausting!)
- Protein Shake: +4 sweat
- Random events: varies

**🎲 Random Decay**
- 20% chance each second to lose 1 morale naturally
- Small but constant pressure

**🏆 Losing Competitions**
- Instant -15 morale penalty on top of the entry fee loss

### 💚 What Raises Morale

**🥤 Protein Shake is the ONLY source of morale recovery**
- Costs 40 protein
- Grants +20 morale
- Also adds +4 sweat (delayed penalty)

**🏆 Winning Competitions**
- +10 morale bonus when you win

### 🔥 The Death Spiral (How You Lose)

1. Heavy training builds up sweat quickly
2. Sweat starts draining morale every second
3. You spend protein on shakes to recover morale
4. Can't afford both shakes AND showers
5. Sweat keeps rising, morale drain accelerates
6. Morale hits 0% → GAME OVER

**The Solution**: 
- **Shower constantly** - don't let sweat get above 30
- **Feed strategically** - only when really needed
- **Always keep 65 protein** - enough for shower (25) + shake (40) in emergencies
- **Stop training when sweat is high** - take a shower break first!

---

## Energy System

Every training action costs **25 energy**. When energy drops to **25 or below**, the wiener automatically enters rest mode:

- **During Rest**: All actions are blocked (buttons still visible but won't work)
- **Recovery Rate**: +15 energy per second
- **Full Recovery**: Takes about 5-6 seconds to go from 25 to 100
- **Energy Bar**: Turns from gold to orange when low

**💡 Tip**: Don't blow all your energy right before a competition! Save at least 40 energy for championship attempts.

---

## The Combo System

The combo meter multiplies your click rewards. It works like this:

- Click within **0.7 seconds** of your last click to build combo
- Combo ranges from **x1 to x30**
- Each combo level increases the protein you earn
- Visual feedback: combo bar fills up and changes color
- Auto-resets to x1 if you stop clicking for 1+ seconds

**Combo Colors**:
- x1-9: White (base)
- x10-14: Gold
- x15-19: Orange
- x20-24: Red
- x25-30: Magenta (maximum!)

**💡 Strategy**: Rapid clicking at high combo levels is your best protein source in early game!

---

## Weight Progression — The Silly Part!

As your lifting stats grow, the barbell gets progressively more absurd:

| Total Power | Barbell Weight | Visual Effect                                    |
|-------------|----------------|--------------------------------------------------|
| 1-10        | 45-145 lbs     | Standard barbell, small plates                   |
| 11-30       | 155-345 lbs    | Barbell with medium plates on both sides        |
| 31-50       | 355-545 lbs    | Large plates extending outward                   |
| 51-70       | 555-745 lbs    | Plates stacking up, looking ridiculous          |
| 71+         | 755+ lbs       | ABSURD - barbell extends across entire screen!  |

At high levels (power > 50), you'll see a **"⚠️ DANGEROUSLY SWOLE ⚠️"** warning because the weights are getting comically large!

---

## Random Gym Events

Every few seconds there's a chance of a random event:

**Good Events** 💪:
- Gym bro spotted you (+50 protein)
- Instagram post went viral (+100 protein)  
- Found protein shake (+75 protein)
- Great gym playlist (+5 morale)
- Quick cardio session (-8 sweat)
- New PR! (+1 to random stat)
- Pre-workout kicked in (+20 energy)
- Meditation break (+10 morale)

**Bad Events** 😰:
- Accidentally dropped weights (+10 sweat)
- Gym crush walked by (+15 sweat, -3 morale)

---

## Prestige System

Once you have **3+ trophies**, you can Prestige:

**What You Lose**:
- All stats reset to 1
- All protein gone (start with 300)
- All shop upgrades lost
- Energy, morale, sweat reset

**What You Keep**:
- **ALL TROPHIES**
- Progress toward 10-trophy goal

**What You Gain**:
- **+2 permanent click power** per prestige level
- Faster progression on future runs
- Pride in your achievements!

**Example**: After 2 prestiges, you start with 5 base click power instead of 1!

---

## Tips & Strategy

### Early Game (Stages 1-2: PUPPY → YOUNG)
1. **Build combo clicking** - get to x20+ combo for massive protein
2. **Prioritize Bench Press** - passive income is crucial
3. **Keep sweat under 25** - shower every 3-4 training sessions
4. **Save for Protein Shaker** (2,000) - doubles your click power!

### Mid Game (Stages 3-4: ADULT → CHAMPION)
1. **Buy Gym Membership** - +10 protein/sec is huge
2. **Balance all three lifts** - need well-rounded stats for competitions
3. **Enter competitions** - you need those trophies!
4. **Buy Power Rack** - 20% cost reduction helps a lot

### Late Game (Stage 5: LEGENDARY)
1. **Max out shop upgrades** - you'll need them all
2. **Competition spam** - win those last trophies
3. **Watch sweat carefully** - late game training creates tons of sweat
4. **Consider Prestige** at 3+ trophies for permanent bonuses

### Universal Tips 🎯:
- **The Golden Rule**: If sweat > 40, SHOWER IMMEDIATELY
- **Emergency Budget**: Always keep 65+ protein for shower + shake
- **Competition Timing**: Enter when morale and energy are both high
- **Upgrade Order**: Protein Shaker → Gym Membership → Power Rack
- **Death Spiral Escape**: If morale < 30 and sweat > 50, focus on recovery over training

---

## Common Mistakes to Avoid

❌ **Training Nonstop** - Creates too much sweat, morale crashes  
✅ **Training in Bursts** - Train, shower, repeat

❌ **Ignoring Sweat** - "I'll shower later" = death spiral  
✅ **Shower at 30+ Sweat** - Prevention > recovery

❌ **Using All Protein** - Can't afford emergency shake  
✅ **Keep Safety Buffer** - Always have 65+ protein

❌ **Competing When Weak** - Wastes protein, loses morale  
✅ **Compete When Ready** - High stats = higher win rate

❌ **Buying Upgrades Randomly** - Inefficient progression  
✅ **Follow Buy Order** - Protein Shaker and Gym Membership first!

---

## Win Condition

**Collect 10 Trophies** from Strongdog Championship victories to become the Ultimate Wiener Champion!

**Estimated Time to Win**: 15-25 minutes depending on how efficiently you manage resources and train your wiener.

---

## Key Differences from Original

### UI/UX Improvements ✨:
- 3-column layout (was scattered all over)
- Clear section borders with labels
- Progress bars for all resources (was just numbers)
- Better color coding (themed sections)
- Hover tooltips (costs and effects visible)
- Larger, more readable fonts
- Better visual hierarchy

### Gameplay Changes 🎮:
- Treats → Protein (fits gym theme)
- Happiness → Morale (more appropriate)
- Mess → Sweat (gym-appropriate)
- 4 training types → 3 core lifts (focused gameplay)
- Simplified progression (clearer stages)
- Better balanced costs and rewards
- More impactful shop upgrades

### New Features 🆕:
- Visual weight progression on barbell
- Animated lifting (squat, bench, deadlift)
- Enhanced combo system with visual bar
- Gym-themed random events
- Absurdly large weights at high stats
- Better game over screen
- Prestige system improvements

---

## Have Fun! 💪

Remember: This is a silly game about a wiener dog getting absolutely JACKED at the gym. Don't take it too seriously, enjoy the absurd weight progression, and may your wiener achieve LEGENDARY status! 🌭💪🏆
