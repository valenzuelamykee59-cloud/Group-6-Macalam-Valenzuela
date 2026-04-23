import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class HowToTrainYourWiener {

    // === CORE RESOURCES ===
    private static int treats = 200;
    private static int strength = 1;
    private static int speed = 1;
    private static int obedience = 1;
    private static int charm = 1;
    private static int wienersFound = 0;
    private static int clickPower = 1;
    private static int treatsPerSecond = 0;
    private static int dogHappiness = 60;
    private static int dogMessiness = 0;
    private static int trophies = 0;
    private static int totalTreatsEarned = 0;

    // === SHOP UPGRADES ===
    private static boolean hasClickMultiplier = false;
    private static boolean hasAutoCollector = false;
    private static boolean hasGoldenLeash = false;
    private static boolean hasTreatFactory = false;
    private static boolean hasDogWhisperer = false;
    private static boolean hasSpeedBoots = false;
    private static int prestigeLevel = 0;

    // === COOLDOWNS (ms) ===
    private static long lastStrengthTime = 0;
    private static long lastSpeedTime = 0;
    private static long lastObedienceTime = 0;
    private static long lastCharmTime = 0;
    private static long lastHuntTime = 0;
    private static long lastCompeteTime = 0;
    private static final int TRAIN_COOLDOWN = 3000;
    private static final int COMPETE_COOLDOWN = 15000;

    // === GAMBLING ===
    private static int gamblesWon = 0;
    private static int gamblesLost = 0;

    // === UI ===
    private static JLabel treatsLabel;
    private static JLabel tpsLabel;
    private static JLabel strengthValLabel, speedValLabel, obeValLabel, charmValLabel;
    private static JLabel happinessValLabel, messValLabel, prestigeValLabel, trophiesValLabel, wienersValLabel;
    private static JLabel dogLabel;
    private static JLabel stageLabel;
    private static JTextArea eventLog;
    private static JProgressBar trainingBar;
    private static JPanel dogPanel;
    private static Random random = new Random();
    private static String currentStage = "PUPPY";

    // === ACHIEVEMENT FLAGS ===
    private static boolean firstTrophy = false;
    private static boolean champion = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("Panel.background", new Color(18, 18, 24));
                UIManager.put("Button.background", new Color(35, 35, 45));
                UIManager.put("Button.foreground", new Color(255, 80, 40));
                UIManager.put("Label.foreground", new Color(220, 220, 220));
                UIManager.put("Button.font", new Font("Courier New", Font.BOLD, 11));
                UIManager.put("Label.font", new Font("Courier New", Font.PLAIN, 11));
            } catch (Exception ignored) {}

            buildUI();
        });
    }

    private static void buildUI() {
        JFrame frame = new JFrame("> HOW TO TRAIN YOUR WIENER.exe");
        frame.setSize(1000, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(18, 18, 24));

        // === TITLE ===
        JLabel titleLabel = new JLabel("🌭 HOW TO TRAIN YOUR WIENER 🌭", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255, 80, 40));
        titleLabel.setBounds(0, 10, 1000, 30);
        mainPanel.add(titleLabel);

        JLabel subLabel = new JLabel(">> WIENER DOG CHAMPIONSHIP SIMULATOR v3.0 <<", SwingConstants.CENTER);
        subLabel.setFont(new Font("Courier New", Font.ITALIC, 11));
        subLabel.setForeground(new Color(130, 130, 140));
        subLabel.setBounds(0, 40, 1000, 18);
        mainPanel.add(subLabel);

        // === DOG DISPLAY ===
        dogPanel = new JPanel(new BorderLayout(5, 5));
        dogPanel.setBounds(355, 65, 290, 185);
        dogPanel.setBackground(new Color(12, 12, 18));
        dogPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 80, 40), 3),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        dogLabel = new JLabel("🐶", SwingConstants.CENTER);
        dogLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 90));
        dogPanel.add(dogLabel, BorderLayout.CENTER);

        stageLabel = new JLabel("[ STAGE: PUPPY ]", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        stageLabel.setForeground(new Color(255, 200, 0));
        dogPanel.add(stageLabel, BorderLayout.SOUTH);
        mainPanel.add(dogPanel);

        // === LEFT STATS PANEL ===
        JPanel statsPanel = new JPanel(new GridLayout(9, 2, 3, 6));
        statsPanel.setBounds(15, 65, 330, 230);
        statsPanel.setBackground(new Color(12, 12, 18));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 85), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        statsPanel.add(makeStatKey("💪 STRENGTH:"));  strengthValLabel = makeStatVal("1", new Color(100, 255, 100));   statsPanel.add(strengthValLabel);
        statsPanel.add(makeStatKey("⚡ SPEED:"));     speedValLabel    = makeStatVal("1", new Color(100, 200, 255));   statsPanel.add(speedValLabel);
        statsPanel.add(makeStatKey("🎓 OBEDIENCE:")); obeValLabel      = makeStatVal("1", new Color(255, 200, 100));   statsPanel.add(obeValLabel);
        statsPanel.add(makeStatKey("✨ CHARM:"));     charmValLabel    = makeStatVal("1", new Color(255, 150, 220));   statsPanel.add(charmValLabel);
        statsPanel.add(makeStatKey("😊 HAPPINESS:")); happinessValLabel= makeStatVal("60%", new Color(100, 255, 180));statsPanel.add(happinessValLabel);
        statsPanel.add(makeStatKey("🗑 MESS:"));      messValLabel     = makeStatVal("0", new Color(255, 100, 100));   statsPanel.add(messValLabel);
        statsPanel.add(makeStatKey("🏆 TROPHIES:"));  trophiesValLabel = makeStatVal("0", new Color(255, 215, 0));     statsPanel.add(trophiesValLabel);
        statsPanel.add(makeStatKey("⭐ PRESTIGE:"));  prestigeValLabel = makeStatVal("0", new Color(255, 80, 200));    statsPanel.add(prestigeValLabel);
        statsPanel.add(makeStatKey("🌭 WIENERS:"));   wienersValLabel  = makeStatVal("0", new Color(255, 140, 50));    statsPanel.add(wienersValLabel);
        mainPanel.add(statsPanel);

        // === RIGHT INFO PANEL ===
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 4, 8));
        infoPanel.setBounds(655, 65, 330, 185);
        infoPanel.setBackground(new Color(12, 12, 18));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 85), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel infoTitle = new JLabel("📋 HOW TO WIN:", SwingConstants.LEFT);
        infoTitle.setFont(new Font("Courier New", Font.BOLD, 11));
        infoTitle.setForeground(new Color(255, 80, 40));
        infoPanel.add(infoTitle);

        String[] tips = {
            "• Train stats, collect treats",
            "• Enter Competitions for 🏆 trophies",
            "• 10 trophies = WIENER CHAMPION!",
            "• Prestige to unlock power boosts"
        };
        for (String tip : tips) {
            JLabel l = new JLabel(tip);
            l.setFont(new Font("Courier New", Font.PLAIN, 10));
            l.setForeground(new Color(160, 160, 170));
            infoPanel.add(l);
        }
        mainPanel.add(infoPanel);

        // === PROGRESS BAR ===
        trainingBar = new JProgressBar(0, 100);
        trainingBar.setBounds(355, 258, 290, 22);
        trainingBar.setBackground(Color.BLACK);
        trainingBar.setForeground(new Color(255, 80, 40));
        trainingBar.setStringPainted(true);
        trainingBar.setFont(new Font("Courier New", Font.BOLD, 9));
        mainPanel.add(trainingBar);

        // === EVENT LOG ===
        eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setBackground(new Color(5, 5, 8));
        eventLog.setForeground(new Color(0, 230, 0));
        eventLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane scrollPane = new JScrollPane(eventLog);
        scrollPane.setBounds(15, 290, 968, 135);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 80, 40)),
            ">> SYSTEM LOG <<",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Courier New", Font.BOLD, 10),
            new Color(255, 80, 40)
        ));
        mainPanel.add(scrollPane);

        // === BUTTONS ROW 1 ===
        JButton petBtn = makeBtn("🐕 [PET WIENER]", new Color(255, 80, 40));
        petBtn.setBounds(15, 435, 185, 42);
        petBtn.addActionListener(e -> {
            int gain = clickPower * (hasClickMultiplier ? 2 : 1) * (hasGoldenLeash ? 2 : 1);
            treats += gain;
            totalTreatsEarned += gain;
            dogHappiness = Math.min(100, dogHappiness + 1);
            addLog("> PET: +" + gain + " treats  [HAPPINESS +1]");
            updateGame();
        });
        mainPanel.add(petBtn);

        JButton strengthBtn = makeBtn("💪 STRENGTH TRAIN [30]", new Color(100, 230, 100));
        strengthBtn.setBounds(210, 435, 185, 42);
        strengthBtn.addActionListener(e -> trainStat("strength"));
        mainPanel.add(strengthBtn);

        JButton speedBtn = makeBtn("⚡ ZOOMIES TRAIN [25]", new Color(100, 200, 255));
        speedBtn.setBounds(405, 435, 185, 42);
        speedBtn.addActionListener(e -> trainStat("speed"));
        mainPanel.add(speedBtn);

        JButton obeBtn = makeBtn("🎓 OBEDIENCE CLASS [40]", new Color(255, 200, 100));
        obeBtn.setBounds(600, 435, 185, 42);
        obeBtn.addActionListener(e -> trainStat("obedience"));
        mainPanel.add(obeBtn);

        JButton charmBtn = makeBtn("✨ CHARM LESSONS [35]", new Color(255, 150, 220));
        charmBtn.setBounds(795, 435, 185, 42);
        charmBtn.addActionListener(e -> trainStat("charm"));
        mainPanel.add(charmBtn);

        // === BUTTONS ROW 2 ===
        JButton wienerBtn = makeBtn("🌭 HUNT WIENER [15]", new Color(255, 140, 50));
        wienerBtn.setBounds(15, 487, 185, 42);
        wienerBtn.addActionListener(e -> huntWiener());
        mainPanel.add(wienerBtn);

        JButton competeBtn = makeBtn("🏆 COMPETITION [50]", new Color(255, 215, 0));
        competeBtn.setBounds(210, 487, 185, 42);
        competeBtn.addActionListener(e -> enterCompetition());
        mainPanel.add(competeBtn);

        JButton shopBtn = makeBtn("🛒 SHOP UPGRADES", new Color(160, 100, 255));
        shopBtn.setBounds(405, 487, 185, 42);
        shopBtn.addActionListener(e -> openShop());
        mainPanel.add(shopBtn);

        JButton gambleBtn = makeBtn("🎲 CASINO RISK IT", new Color(255, 215, 50));
        gambleBtn.setBounds(600, 487, 185, 42);
        gambleBtn.addActionListener(e -> openGamble());
        mainPanel.add(gambleBtn);

        JButton prestigeBtn = makeBtn("🌟 PRESTIGE [RESET]", new Color(255, 80, 200));
        prestigeBtn.setBounds(795, 487, 185, 42);
        prestigeBtn.addActionListener(e -> prestigeReset());
        mainPanel.add(prestigeBtn);

        // === BUTTONS ROW 3 ===
        JButton cleanBtn = makeBtn("🧹 CLEAN MESS [5]", new Color(100, 200, 200));
        cleanBtn.setBounds(15, 539, 185, 35);
        cleanBtn.addActionListener(e -> {
            if (dogMessiness > 0 && treats >= 5) {
                treats -= 5;
                dogMessiness = Math.max(0, dogMessiness - 20);
                addLog("> CLEAN: Mess reduced to " + dogMessiness);
                updateGame();
            } else if (treats < 5) {
                addLog("! INSUFFICIENT TREATS !");
            }
        });
        mainPanel.add(cleanBtn);

        JButton feedBtn = makeBtn("🍗 FEED TREAT [10]", new Color(255, 180, 80));
        feedBtn.setBounds(210, 539, 185, 35);
        feedBtn.addActionListener(e -> {
            if (treats >= 10) {
                treats -= 10;
                dogHappiness = Math.min(100, dogHappiness + 15);
                dogMessiness += 3;
                addLog("> FEED: Happiness +15! [MESS +3]");
                updateGame();
            } else {
                addLog("! INSUFFICIENT TREATS !");
            }
        });
        mainPanel.add(feedBtn);

        // === BOTTOM RESOURCE BAR ===
        treatsLabel = new JLabel("", SwingConstants.CENTER);
        treatsLabel.setBounds(0, 585, 700, 28);
        treatsLabel.setFont(new Font("Courier New", Font.BOLD, 13));
        treatsLabel.setForeground(new Color(255, 215, 0));
        treatsLabel.setBackground(new Color(8, 8, 12));
        treatsLabel.setOpaque(true);
        treatsLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 40), 1));
        mainPanel.add(treatsLabel);

        tpsLabel = new JLabel("", SwingConstants.CENTER);
        tpsLabel.setBounds(700, 585, 298, 28);
        tpsLabel.setFont(new Font("Courier New", Font.BOLD, 11));
        tpsLabel.setForeground(new Color(0, 230, 180));
        tpsLabel.setBackground(new Color(8, 8, 12));
        tpsLabel.setOpaque(true);
        tpsLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 40), 1));
        mainPanel.add(tpsLabel);

        frame.add(mainPanel);
        frame.setVisible(true);

        startIdle();
        updateGame();
        addLog("> SYSTEM: How to Train Your Wiener v3.0 ONLINE");
        addLog("> TIP: Train all stats and enter Competitions to earn Trophies!");
        addLog("> WARNING: High MESS reduces treat income. Keep it clean!");
    }

    // === TRAINING ===
    private static void trainStat(String stat) {
        long now = System.currentTimeMillis();
        int cost, cooldown;
        long lastTime;
        String label;

        switch (stat) {
            case "strength": cost = 30; lastTime = lastStrengthTime; label = "STRENGTH"; break;
            case "speed":    cost = 25; lastTime = lastSpeedTime;    label = "SPEED";    break;
            case "obedience":cost = 40; lastTime = lastObedienceTime;label = "OBEDIENCE";break;
            case "charm":    cost = 35; lastTime = lastCharmTime;    label = "CHARM";    break;
            default: return;
        }

        if (now - lastTime < TRAIN_COOLDOWN) {
            int remaining = (int)((TRAIN_COOLDOWN - (now - lastTime)) / 1000) + 1;
            addLog("! " + label + " training on cooldown: " + remaining + "s remaining");
            return;
        }
        if (treats < cost) { addLog("! INSUFFICIENT TREATS !"); return; }

        treats -= cost;
        int gain = 1 + prestigeLevel;
        int happBonus = 0;

        switch (stat) {
            case "strength":
                strength += gain; clickPower += gain; dogMessiness += 4;
                lastStrengthTime = now;
                addLog("> TRAIN: Strength +" + gain + " → " + strength + " | Click Power → " + clickPower + " [MESS +4]");
                break;
            case "speed":
                speed += gain; treatsPerSecond += (hasTreatFactory ? 4 : 2);
                lastSpeedTime = now;
                addLog("> TRAIN: Speed +" + gain + " → " + speed + " | +2 treats/sec");
                break;
            case "obedience":
                obedience += gain; happBonus = 8 + (hasDogWhisperer ? 8 : 0);
                dogHappiness = Math.min(100, dogHappiness + happBonus);
                lastObedienceTime = now;
                addLog("> SCHOOL: Obedience +" + gain + " → " + obedience + " | Happiness +" + happBonus);
                break;
            case "charm":
                charm += gain; happBonus = 5; dogHappiness = Math.min(100, dogHappiness + happBonus);
                lastCharmTime = now;
                addLog("> LESSONS: Charm +" + gain + " → " + charm + " | Happiness +" + happBonus);
                break;
        }
        updateGame();
    }

    // === HUNT WIENER ===
    private static void huntWiener() {
        long now = System.currentTimeMillis();
        if (now - lastHuntTime < 5000) {
            addLog("! Hunt on cooldown: " + ((5000 - (now - lastHuntTime)) / 1000 + 1) + "s");
            return;
        }
        if (treats < 15) { addLog("! INSUFFICIENT TREATS !"); return; }
        treats -= 15;
        lastHuntTime = now;
        wienersFound++;
        int bonus = random.nextInt(40) + 20 + speed * 2;
        treats += bonus;
        totalTreatsEarned += bonus;
        String[] finds = {
            "a legendary golden wiener", "a glittery sparkle wiener",
            "a suspiciously shaped stick", "a rare wiener relic", "a wiener of pure chaos"
        };
        addLog("> HUNT: Found " + finds[random.nextInt(finds.length)] + "! +" + bonus + " treats [TOTAL: " + wienersFound + "]");
        updateGame();
    }

    // === COMPETITION ===
    private static void enterCompetition() {
        long now = System.currentTimeMillis();
        if (now - lastCompeteTime < COMPETE_COOLDOWN) {
            int s = (int)((COMPETE_COOLDOWN - (now - lastCompeteTime)) / 1000) + 1;
            addLog("! Competition cooldown: " + s + "s remaining");
            return;
        }
        if (treats < 50) { addLog("! INSUFFICIENT TREATS !"); return; }
        treats -= 50;
        lastCompeteTime = now;

        // Win chance based on stats + happiness
        int totalStats = strength + speed + obedience + charm;
        int happBonus = dogHappiness > 70 ? 15 : (dogHappiness > 40 ? 5 : -10);
        int winChance = Math.min(85, 20 + totalStats * 2 + happBonus - dogMessiness / 5);
        winChance = Math.max(10, winChance);

        String[] events = {
            "🐾 Agility Course", "🌭 Longest Wiener Contest", "📣 Bark-Off Championship",
            "🎀 Best in Show", "🏃 Wiener Derby"
        };
        String eventName = events[random.nextInt(events.length)];

        if (random.nextInt(100) < winChance) {
            trophies++;
            int prize = 100 + totalStats * 5;
            treats += prize;
            totalTreatsEarned += prize;
            addLog("🏆 COMPETE [" + eventName + "]: WON! +" + prize + " treats | Total Trophies: " + trophies);
            checkAchievements();
        } else {
            int consolation = 20;
            treats += consolation;
            addLog("😔 COMPETE [" + eventName + "]: Lost... +" + consolation + " treats consolation | Win chance was " + winChance + "%");
        }
        updateGame();
    }

    private static void checkAchievements() {
        if (!firstTrophy && trophies >= 1) {
            firstTrophy = true;
            addLog("🎉 ACHIEVEMENT: First Trophy! Your wiener is officially competitive!");
        }
        if (!champion && trophies >= 10) {
            champion = true;
            addLog("👑 ACHIEVEMENT: WIENER CHAMPION! You have mastered the art of training!");
            addLog("══════════════════════════════════════════════════════════");
            addLog("🌭🏆  C O N G R A T U L A T I O N S  🏆🌭");
            addLog("Your Wiener is the Ultimate Champion! Prestige for more glory!");
            addLog("══════════════════════════════════════════════════════════");
        }
    }

    // === IDLE SYSTEM ===
    private static void startIdle() {
        new Timer(1000, e -> {
            // Passive treats
            int passiveGain = treatsPerSecond;
            if (dogHappiness > 70) passiveGain += 3 + charm;
            if (dogHappiness < 20) passiveGain = Math.max(0, passiveGain - 3);
            if (dogMessiness > 30) passiveGain = Math.max(0, passiveGain - dogMessiness / 10);
            if (hasGoldenLeash) passiveGain = (int)(passiveGain * 1.5);

            if (passiveGain > 0) {
                treats += passiveGain;
                totalTreatsEarned += passiveGain;
            }

            // Happiness decay
            if (random.nextInt(10) < 2) dogHappiness = Math.max(0, dogHappiness - 1);

            // Random events (10% chance per second)
            if (random.nextInt(100) < 10) triggerRandomEvent();

            updateGame();
        }).start();
    }

    private static void triggerRandomEvent() {
        int roll = random.nextInt(12);
        switch (roll) {
            case 0:  addLog("🐿 Wiener chased a squirrel! +15 treats"); treats += 15; totalTreatsEarned += 15; break;
            case 1:  addLog("💨 Wiener made a HUGE mess! MESS +10"); dogMessiness += 10; break;
            case 2:  addLog("😨 Wiener saw the vacuum cleaner! HAPPINESS -8"); dogHappiness = Math.max(0, dogHappiness - 8); break;
            case 3:  addLog("🎁 Mysterious treat package arrived! +35 treats"); treats += 35; totalTreatsEarned += 35; break;
            case 4:  addLog("🌧️ Rainy day... Wiener refuses to train. No income for 3s"); treatsPerSecond = Math.max(0, treatsPerSecond - 1); break;
            case 5:  addLog("🌟 Wiener did a backflip! Everyone loved it. HAPPINESS +10"); dogHappiness = Math.min(100, dogHappiness + 10); break;
            case 6:  addLog("🦴 Found a bone in the yard! HAPPINESS +5, +8 treats"); dogHappiness = Math.min(100, dogHappiness + 5); treats += 8; break;
            case 7:  addLog("🎉 Local kids loved your Wiener! CHARM +1"); charm++; break;
            case 8:  addLog("🍕 Wiener stole a slice of pizza! MESS +5, HAPPINESS +5"); dogMessiness += 5; dogHappiness = Math.min(100, dogHappiness + 5); break;
            case 9:  addLog("📸 Wiener went viral online! +50 treats!"); treats += 50; totalTreatsEarned += 50; break;
            case 10: addLog("😴 Wiener took a nap. Nothing happened."); break;
            case 11: addLog("🐕 Neighbor's dog visited! Happiness +5"); dogHappiness = Math.min(100, dogHappiness + 5); break;
        }
    }

    // === PRESTIGE ===
    private static void prestigeReset() {
        if (trophies < 3 && prestigeLevel == 0) {
            addLog("! You need at least 3 Trophies to Prestige!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null,
            "⚠️ PRESTIGE RESET ⚠️\n\n" +
            "• All stats, treats and upgrades reset\n" +
            "• Trophies are KEPT\n" +
            "• Permanent bonus: click power +" + (prestigeLevel + 1) + "\n" +
            "• Current prestige: " + prestigeLevel + "\n\n" +
            "PROCEED?", "PRESTIGE", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            prestigeLevel++;
            treats = 300;
            strength = 1; speed = 1; obedience = 1; charm = 1;
            clickPower = 1 + prestigeLevel;
            treatsPerSecond = 0;
            dogHappiness = 60;
            dogMessiness = 0;
            hasClickMultiplier = false;
            hasAutoCollector = false;
            hasGoldenLeash = false;
            hasTreatFactory = false;
            hasDogWhisperer = false;
            hasSpeedBoots = false;
            addLog("🌟 PRESTIGE LEVEL " + prestigeLevel + "! Click Power starts at " + clickPower + "!");
            addLog("   Your trophies remain: " + trophies);
            updateGame();
        }
    }

    // === SHOP ===
    private static void openShop() {
        JDialog d = new JDialog();
        d.setTitle("> WIENER UPGRADE SHOP <");
        d.setSize(500, 560);
        d.setModal(true);
        d.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBackground(new Color(18, 18, 24));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("══ UPGRADE SHOP ══", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 14));
        title.setForeground(new Color(255, 80, 40));
        panel.add(title);

        // Shop items: name, desc, cost, owned
        Object[][] items = {
            {"🔁 CLICK MULTIPLIER", "2x click rewards", 500, hasClickMultiplier},
            {"🤖 AUTO-PETTER", "Auto-pets every 8s", 1000, hasAutoCollector},
            {"✨ GOLDEN LEASH", "+50% all income", 2000, hasGoldenLeash},
            {"🏭 TREAT FACTORY", "Double speed bonus", 3500, hasTreatFactory},
            {"🧙 DOG WHISPERER", "Double obedience bonus", 2500, hasDogWhisperer},
            {"👟 SPEED BOOTS", "+5 treats/sec instantly", 1500, hasSpeedBoots},
            {"😊 HAPPINESS POTION", "+50 happiness [100]", 100, null},
            {"🍖 TREAT PACK", "200 → 500 treats", 200, null},
        };

        for (Object[] item : items) {
            String name = (String) item[0];
            String desc = (String) item[1];
            int cost = (int) item[2];
            Boolean owned = (Boolean) item[3];

            JButton btn;
            if (owned != null && owned) {
                btn = makeShopBtn("✓ " + name + " — OWNED", Color.GREEN, false);
            } else {
                btn = makeShopBtn(name + "  [" + cost + " treats]  — " + desc, new Color(200, 200, 210), true);
                btn.addActionListener(e -> handleShopBuy(name, cost, d));
            }
            panel.add(btn);
        }

        JLabel bal = new JLabel("💰 TREATS: " + treats, SwingConstants.CENTER);
        bal.setFont(new Font("Courier New", Font.BOLD, 13));
        bal.setForeground(Color.YELLOW);
        panel.add(bal);

        JScrollPane sp = new JScrollPane(panel);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        d.add(sp);
        d.setVisible(true);
    }

    private static void handleShopBuy(String name, int cost, JDialog d) {
        if (treats < cost) { addLog("! INSUFFICIENT TREATS !"); return; }
        treats -= cost;
        switch (name) {
            case "🔁 CLICK MULTIPLIER": hasClickMultiplier = true; addLog("> SHOP: CLICK MULTIPLIER purchased!"); break;
            case "🤖 AUTO-PETTER":      hasAutoCollector = true; startAutoCollector(); addLog("> SHOP: AUTO-PETTER activated!"); break;
            case "✨ GOLDEN LEASH":     hasGoldenLeash = true; addLog("> SHOP: GOLDEN LEASH equipped! +50% income"); break;
            case "🏭 TREAT FACTORY":    hasTreatFactory = true; treatsPerSecond += 10; addLog("> SHOP: TREAT FACTORY online! +10 treats/sec base"); break;
            case "🧙 DOG WHISPERER":    hasDogWhisperer = true; addLog("> SHOP: DOG WHISPERER hired! Obedience training is more powerful"); break;
            case "👟 SPEED BOOTS":      hasSpeedBoots = true; treatsPerSecond += 5; addLog("> SHOP: SPEED BOOTS! +5 treats/sec"); break;
            case "😊 HAPPINESS POTION": dogHappiness = Math.min(100, dogHappiness + 50); addLog("> SHOP: Happiness +50!"); break;
            case "🍖 TREAT PACK":       treats += 500; totalTreatsEarned += 500; addLog("> SHOP: TREAT PACK — net +300 treats!"); break;
        }
        updateGame();
        d.dispose();
        openShop();
    }

    private static void startAutoCollector() {
        new Timer(8000, e -> {
            if (hasAutoCollector) {
                int gain = clickPower * 4 * (hasGoldenLeash ? 2 : 1);
                treats += gain;
                totalTreatsEarned += gain;
                dogHappiness = Math.min(100, dogHappiness + 2);
                addLog("🤖 AUTO-PETTER: +" + gain + " treats [HAPPINESS +2]");
                updateGame();
            }
        }).start();
    }

    // === CASINO ===
    private static void openGamble() {
        JDialog d = new JDialog();
        d.setTitle("> UNDERGROUND WIENER CASINO <");
        d.setSize(480, 420);
        d.setModal(true);
        d.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBackground(new Color(18, 18, 24));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("🎲 WIENER CASINO 🎲", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 15));
        title.setForeground(new Color(255, 215, 0));
        panel.add(title);

        JLabel warn = new JLabel("⚠️ High risk — you could lose it all! ⚠️", SwingConstants.CENTER);
        warn.setForeground(Color.RED);
        warn.setFont(new Font("Courier New", Font.BOLD, 11));
        panel.add(warn);

        JTextField betField = new JTextField("100");
        betField.setHorizontalAlignment(JTextField.CENTER);
        betField.setBackground(Color.BLACK);
        betField.setForeground(Color.GREEN);
        betField.setFont(new Font("Courier New", Font.BOLD, 13));
        panel.add(new JLabel("BET AMOUNT (you have: " + treats + " treats):"));
        panel.add(betField);

        // Coin Flip: 50/50 2x
        JButton coinFlip = makeBtn("🪙 COIN FLIP  [50/50 → 2x]", new Color(255, 200, 100));
        coinFlip.addActionListener(e -> {
            try {
                int bet = Integer.parseInt(betField.getText().trim());
                if (bet <= 0 || bet > treats) { addLog("! Invalid bet amount!"); return; }
                treats -= bet;
                if (random.nextBoolean()) {
                    treats += bet * 2; gamblesWon++;
                    addLog("🎲 CASINO: COIN FLIP WIN! +" + bet + " treats!");
                } else {
                    gamblesLost++;
                    addLog("💀 CASINO: COIN FLIP LOSS! -" + bet + " treats");
                }
                updateGame(); d.dispose();
            } catch (NumberFormatException ex) { addLog("! Enter a valid number!"); }
        });
        panel.add(coinFlip);

        // Dog Race: 33% → 3x
        JButton dogRace = makeBtn("🐕 DOG RACE  [33% → 3x]", new Color(255, 140, 80));
        dogRace.addActionListener(e -> {
            try {
                int bet = Integer.parseInt(betField.getText().trim());
                if (bet <= 0 || bet > treats) { addLog("! Invalid bet amount!"); return; }
                treats -= bet;
                if (random.nextInt(3) == 0) {
                    treats += bet * 3; gamblesWon++;
                    addLog("🏁 CASINO: DOG RACE WIN! +" + (bet * 2) + " profit!");
                } else {
                    gamblesLost++;
                    addLog("🐕 CASINO: DOG RACE LOSS! -" + bet + " treats");
                }
                updateGame(); d.dispose();
            } catch (NumberFormatException ex) { addLog("! Enter a valid number!"); }
        });
        panel.add(dogRace);

        // Jackpot: 10% → 10x
        JButton jackpot = makeBtn("💥 JACKPOT  [10% → 10x]", new Color(255, 80, 80));
        jackpot.addActionListener(e -> {
            try {
                int bet = Integer.parseInt(betField.getText().trim());
                if (bet <= 0 || bet > treats) { addLog("! Invalid bet amount!"); return; }
                treats -= bet;
                if (random.nextInt(10) == 0) {
                    treats += bet * 10; gamblesWon++;
                    addLog("💥 CASINO: JACKPOT!!!! +" + (bet * 9) + " treats profit!");
                } else {
                    gamblesLost++;
                    addLog("💸 CASINO: JACKPOT FAIL. -" + bet + " treats. Ouch.");
                }
                updateGame(); d.dispose();
            } catch (NumberFormatException ex) { addLog("! Enter a valid number!"); }
        });
        panel.add(jackpot);

        JLabel stats = new JLabel(String.format("📊 Wins: %d  |  Losses: %d  |  W/L: %.0f%%",
            gamblesWon, gamblesLost, gamblesWon + gamblesLost == 0 ? 0.0 :
            gamblesWon * 100.0 / (gamblesWon + gamblesLost)), SwingConstants.CENTER);
        stats.setForeground(Color.CYAN);
        stats.setFont(new Font("Courier New", Font.PLAIN, 10));
        panel.add(stats);

        d.add(panel);
        d.setVisible(true);
    }

    // === UPDATE LOOP ===
    private static void updateGame() {
        updateStage();
        updateDogDisplay();
        updateLabels();
        updateProgressBar();
    }

    private static void updateStage() {
        int total = strength + speed + obedience + charm;
        String old = currentStage;
        if      (total > 60) currentStage = "LEGENDARY";
        else if (total > 35) currentStage = "CHAMPION";
        else if (total > 18) currentStage = "ADULT";
        else if (total > 7)  currentStage = "YOUNG";
        else                 currentStage = "PUPPY";

        if (!old.equals(currentStage)) {
            int bonus = 60 + total * 6;
            treats += bonus;
            totalTreatsEarned += bonus;
            addLog("═══ STAGE UP! " + old + " → " + currentStage + " +" + bonus + " treats! ═══");
        }
    }

    private static void updateDogDisplay() {
        String[] emojis  = {"🐶", "🐕", "🐕‍🦺", "🏆🐕", "⭐🐕⭐"};
        String[] stages  = {"PUPPY", "YOUNG", "ADULT", "CHAMPION", "LEGENDARY"};
        for (int i = 0; i < stages.length; i++) {
            if (currentStage.equals(stages[i])) { dogLabel.setText(emojis[i]); break; }
        }

        Color border = dogHappiness > 70 ? new Color(80, 240, 80) :
                       dogHappiness > 35 ? new Color(240, 200, 0) : new Color(240, 50, 50);
        dogPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 3),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        stageLabel.setText("[ STAGE: " + currentStage + " ]");
    }

    private static void updateLabels() {
        strengthValLabel.setText(String.valueOf(strength));
        speedValLabel.setText(String.valueOf(speed));
        obeValLabel.setText(String.valueOf(obedience));
        charmValLabel.setText(String.valueOf(charm));
        happinessValLabel.setText(dogHappiness + "%");
        happinessValLabel.setForeground(dogHappiness > 70 ? new Color(100, 255, 180) :
                                        dogHappiness > 35 ? new Color(255, 200, 0)   : new Color(255, 80, 80));
        messValLabel.setText(String.valueOf(dogMessiness));
        trophiesValLabel.setText(String.valueOf(trophies));
        prestigeValLabel.setText(String.valueOf(prestigeLevel));
        wienersValLabel.setText(String.valueOf(wienersFound));

        treatsLabel.setText(String.format("🌭  TREATS: %,d", treats));
        tpsLabel.setText(String.format("+%d/sec  |  CLICK: %d  |  TOTAL: %,d", treatsPerSecond, clickPower, totalTreatsEarned));
    }

    private static void updateProgressBar() {
        int total = strength + speed + obedience + charm;
        int max = 60;
        trainingBar.setValue(Math.min(total, max));
        trainingBar.setMaximum(max);
        trainingBar.setString(String.format("TRAINING: %d/%d | TROPHIES: %d/10", Math.min(total, max), max, trophies));
        trainingBar.setForeground(total >= max ? new Color(255, 215, 0) : new Color(255, 80, 40));
    }

    // === HELPERS ===
    private static void addLog(String msg) {
        eventLog.append(msg + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
        if (eventLog.getLineCount() > 40) {
            try { eventLog.replaceRange("", 0, eventLog.getLineEndOffset(10)); } catch (Exception ignored) {}
        }
    }

    private static JButton makeBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Courier New", Font.BOLD, 10));
        btn.setBackground(new Color(28, 28, 36));
        btn.setForeground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return btn;
    }

    private static JButton makeShopBtn(String text, Color color, boolean enabled) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Courier New", Font.PLAIN, 10));
        btn.setBackground(new Color(28, 28, 36));
        btn.setForeground(color);
        btn.setFocusPainted(false);
        btn.setEnabled(enabled);
        return btn;
    }

    private static JLabel makeStatKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Courier New", Font.PLAIN, 10));
        l.setForeground(new Color(160, 160, 170));
        return l;
    }

    private static JLabel makeStatVal(String val, Color color) {
        JLabel l = new JLabel(val);
        l.setFont(new Font("Courier New", Font.BOLD, 11));
        l.setForeground(color);
        return l;
    }
}
