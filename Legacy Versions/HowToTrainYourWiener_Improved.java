import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;

public class HowToTrainYourWiener_Improved {

    // === CORE RESOURCES ===
    private static int protein = 300;
    private static int squatPower = 1;
    private static int benchPower = 1;
    private static int deadliftPower = 1;
    private static int totalReps = 0;
    private static int clickPower = 1;
    private static int proteinPerSecond = 0;
    private static int morale = 80;
    private static int sweat = 0;
    private static int trophies = 0;
    private static int energy = 100;
    private static final int MAX_ENERGY = 100;
    private static boolean isResting = false;

    // === WEIGHT PROGRESSION ===
    private static int totalWeight = 0; // total lifted weight across all exercises
    private static int currentLiftWeight = 45; // current barbell weight in lbs

    // === SHOP UPGRADES ===
    private static boolean hasProteinShaker = false;
    private static boolean hasGymMembership = false;
    private static boolean hasPersonalTrainer = false;
    private static boolean hasPowerRack = false;
    private static boolean hasSteroids = false;
    private static boolean hasOlympicBar = false;
    private static int prestigeLevel = 0;

    // === COOLDOWNS (ms) ===
    private static long lastSquatTime = 0;
    private static long lastBenchTime = 0;
    private static long lastDeadliftTime = 0;
    private static long lastCompeteTime = 0;
    private static final int LIFT_COOLDOWN = 2500;
    private static final int COMPETE_COOLDOWN = 20000;

    // === GAME STATE ===
    private static boolean gameOver = false;
    private static String currentLift = "IDLE"; // SQUAT, BENCH, DEADLIFT, IDLE
    private static int liftAnimationFrame = 0;

    // === UI REFERENCES ===
    private static JLabel proteinLabel;
    private static JLabel ppsLabel;
    private static JLabel squatLabel, benchLabel, deadliftLabel;
    private static JLabel moraleLabel, sweatLabel, energyLabel, trophiesLabel;
    private static JLabel stageLabel;
    private static JLabel weightLabel;
    private static JTextArea eventLog;
    private static JProgressBar moraleBar, energyBar, sweatBar;
    private static JPanel gymPanel;
    private static JLabel dogLiftLabel;
    private static Random random = new Random();
    private static String currentStage = "PUPPY";
    private static JFrame mainFrame;

    // === COMBO SYSTEM ===
    private static int comboCount = 0;
    private static long lastClickTime = 0;
    private static final int COMBO_WINDOW = 700;
    private static final int MAX_COMBO = 30;
    private static JLabel comboLabel;
    private static JProgressBar comboBar;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            buildUI();
        });
    }

    private static void buildUI() {
        mainFrame = new JFrame("💪 HOW TO TRAIN YOUR WIENER - GYM EDITION 🏋️");
        mainFrame.setSize(1200, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(25, 25, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === TOP BANNER ===
        JPanel topBanner = new JPanel(new BorderLayout());
        topBanner.setBackground(new Color(40, 40, 50));
        topBanner.setBorder(createStyledBorder("GYM STATUS", new Color(255, 100, 50)));

        JPanel resourcePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        resourcePanel.setOpaque(false);

        proteinLabel = createLargeStatLabel("🥤 PROTEIN: 300", new Color(100, 200, 255));
        ppsLabel = createLargeStatLabel("⚡ +0/sec", new Color(255, 215, 0));
        resourcePanel.add(proteinLabel);
        resourcePanel.add(ppsLabel);

        topBanner.add(resourcePanel, BorderLayout.CENTER);
        mainPanel.add(topBanner, BorderLayout.NORTH);

        // === CENTER PANEL (3 columns) ===
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        centerPanel.setOpaque(false);

        // LEFT: GYM VISUALIZATION
        gymPanel = createGymPanel();
        centerPanel.add(gymPanel);

        // MIDDLE: STATS
        JPanel statsPanel = createStatsPanel();
        centerPanel.add(statsPanel);

        // RIGHT: ACTIONS
        JPanel actionsPanel = createActionsPanel();
        centerPanel.add(actionsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // === BOTTOM PANEL ===
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.setOpaque(false);

        // Event log
        eventLog = new JTextArea(5, 20);
        eventLog.setEditable(false);
        eventLog.setFont(new Font("Courier New", Font.PLAIN, 11));
        eventLog.setBackground(new Color(30, 30, 35));
        eventLog.setForeground(new Color(180, 180, 190));
        eventLog.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 2));
        JScrollPane logScroll = new JScrollPane(eventLog);
        logScroll.setBorder(createStyledBorder("GYM LOG", new Color(100, 100, 120)));
        bottomPanel.add(logScroll, BorderLayout.CENTER);

        // Combo display
        JPanel comboPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        comboPanel.setOpaque(false);
        comboLabel = createStatLabel("COMBO: x1", new Color(255, 255, 255));
        comboLabel.setFont(new Font("Arial Black", Font.BOLD, 14));
        comboLabel.setHorizontalAlignment(SwingConstants.CENTER);
        comboBar = new JProgressBar(0, MAX_COMBO);
        comboBar.setValue(1);
        comboBar.setStringPainted(true);
        comboBar.setForeground(new Color(255, 200, 0));
        comboBar.setBackground(new Color(40, 40, 50));
        comboPanel.add(comboLabel);
        comboPanel.add(comboBar);
        bottomPanel.add(comboPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        addLog("🏋️ Welcome to the GYM! Time to get SWOLE!");
        addLog("💪 Click the wiener to earn protein and start lifting!");

        // Start game loop
        startIdleTimer();
    }

    private static JPanel createGymPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(35, 35, 45));
        panel.setBorder(createStyledBorder("💪 THE GYM 💪", new Color(255, 100, 50)));

        // Main lifting area
        JPanel liftArea = new JPanel(null);
        liftArea.setBackground(new Color(45, 45, 55));
        liftArea.setPreferredSize(new Dimension(380, 400));

        // Weight label at top
        weightLabel = new JLabel("CURRENT LIFT: 45 lbs", SwingConstants.CENTER);
        weightLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
        weightLabel.setForeground(new Color(255, 200, 50));
        weightLabel.setBounds(0, 10, 380, 30);
        liftArea.add(weightLabel);

        // Stage display
        stageLabel = new JLabel("STAGE: PUPPY", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial Black", Font.BOLD, 20));
        stageLabel.setForeground(new Color(255, 100, 100));
        stageLabel.setBounds(0, 45, 380, 30);
        liftArea.add(stageLabel);

        // Dog lifting visualization
        dogLiftLabel = new JLabel("") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawDogLifting(g2);
                g2.dispose();
            }
        };
        dogLiftLabel.setBounds(0, 80, 380, 300);
        dogLiftLabel.setOpaque(false);
        liftArea.add(dogLiftLabel);

        // Clickable button overlay
        JButton clickBtn = new JButton("💪 CLICK TO LIFT! 💪");
        clickBtn.setFont(new Font("Arial Black", Font.BOLD, 14));
        clickBtn.setBackground(new Color(255, 100, 50));
        clickBtn.setForeground(Color.WHITE);
        clickBtn.setFocusPainted(false);
        clickBtn.setBorderPainted(false);
        clickBtn.setBounds(90, 320, 200, 50);
        clickBtn.addActionListener(e -> performClick());
        liftArea.add(clickBtn);

        panel.add(liftArea, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setOpaque(false);

        // Lifting stats
        JPanel liftStats = new JPanel(new GridLayout(4, 1, 0, 10));
        liftStats.setBackground(new Color(35, 35, 45));
        liftStats.setBorder(createStyledBorder("💪 LIFTING STATS", new Color(100, 200, 255)));

        squatLabel = createStatLabel("🏋️ SQUAT POWER: 1", new Color(100, 200, 255));
        benchLabel = createStatLabel("💪 BENCH POWER: 1", new Color(255, 150, 100));
        deadliftLabel = createStatLabel("⚡ DEADLIFT POWER: 1", new Color(255, 100, 255));
        JLabel totalLabel = createStatLabel("📊 TOTAL REPS: 0", new Color(255, 215, 0));

        liftStats.add(squatLabel);
        liftStats.add(benchLabel);
        liftStats.add(deadliftLabel);
        liftStats.add(totalLabel);

        // Condition stats
        JPanel condStats = new JPanel(new GridLayout(5, 1, 0, 8));
        condStats.setBackground(new Color(35, 35, 45));
        condStats.setBorder(createStyledBorder("🎯 CONDITION", new Color(150, 255, 150)));

        // Morale
        JPanel moralePanel = new JPanel(new BorderLayout(5, 0));
        moralePanel.setOpaque(false);
        moraleLabel = createStatLabel("😊 MORALE: 80%", new Color(100, 255, 100));
        moraleBar = createProgressBar(80, new Color(100, 255, 100));
        moralePanel.add(moraleLabel, BorderLayout.NORTH);
        moralePanel.add(moraleBar, BorderLayout.CENTER);

        // Energy
        JPanel energyPanel = new JPanel(new BorderLayout(5, 0));
        energyPanel.setOpaque(false);
        energyLabel = createStatLabel("⚡ ENERGY: 100%", new Color(255, 215, 0));
        energyBar = createProgressBar(100, new Color(255, 215, 0));
        energyPanel.add(energyLabel, BorderLayout.NORTH);
        energyPanel.add(energyBar, BorderLayout.CENTER);

        // Sweat
        JPanel sweatPanel = new JPanel(new BorderLayout(5, 0));
        sweatPanel.setOpaque(false);
        sweatLabel = createStatLabel("💦 SWEAT: 0", new Color(100, 200, 255));
        sweatBar = createProgressBar(0, new Color(100, 200, 255));
        sweatPanel.add(sweatLabel, BorderLayout.NORTH);
        sweatPanel.add(sweatBar, BorderLayout.CENTER);

        trophiesLabel = createStatLabel("🏆 TROPHIES: 0/10", new Color(255, 215, 0));

        condStats.add(moralePanel);
        condStats.add(energyPanel);
        condStats.add(sweatPanel);
        condStats.add(trophiesLabel);

        panel.add(liftStats);
        panel.add(condStats);
        return panel;
    }

    private static JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 10));
        panel.setOpaque(false);

        // Training buttons
        JPanel trainPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        trainPanel.setBackground(new Color(35, 35, 45));
        trainPanel.setBorder(createStyledBorder("🏋️ TRAINING", new Color(255, 100, 50)));

        JButton squatBtn = createActionButton("🏋️ SQUAT TRAINING", new Color(100, 200, 255));
        squatBtn.addActionListener(e -> trainLift("SQUAT"));
        squatBtn.setToolTipText("Cost: " + getSquatCost() + " protein | +1 Squat Power | +5 Sweat");

        JButton benchBtn = createActionButton("💪 BENCH PRESS", new Color(255, 150, 100));
        benchBtn.addActionListener(e -> trainLift("BENCH"));
        benchBtn.setToolTipText("Cost: " + getBenchCost() + " protein | +1 Bench Power | +4 Sweat");

        JButton deadliftBtn = createActionButton("⚡ DEADLIFT", new Color(255, 100, 255));
        deadliftBtn.addActionListener(e -> trainLift("DEADLIFT"));
        deadliftBtn.setToolTipText("Cost: " + getDeadliftCost() + " protein | +1 Deadlift Power | +6 Sweat");

        trainPanel.add(squatBtn);
        trainPanel.add(benchBtn);
        trainPanel.add(deadliftBtn);

        // Competition & Shop
        JPanel actPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        actPanel.setBackground(new Color(35, 35, 45));
        actPanel.setBorder(createStyledBorder("🎯 ACTIONS", new Color(255, 215, 0)));

        JButton compBtn = createActionButton("🏆 STRONGDOG CHAMPIONSHIP", new Color(255, 215, 0));
        compBtn.addActionListener(e -> enterCompetition());
        compBtn.setToolTipText("Cost: 150 protein | Win trophies and big rewards!");

        JButton shopBtn = createActionButton("🛒 GYM EQUIPMENT SHOP", new Color(100, 255, 100));
        shopBtn.addActionListener(e -> openShop());
        shopBtn.setToolTipText("Buy permanent upgrades to boost your gains!");

        JButton prestigeBtn = createActionButton("🌟 PRESTIGE RESET", new Color(255, 50, 255));
        prestigeBtn.addActionListener(e -> prestigeReset());
        prestigeBtn.setToolTipText("Reset for permanent bonuses (requires 3+ trophies)");

        actPanel.add(compBtn);
        actPanel.add(shopBtn);
        actPanel.add(prestigeBtn);

        // Care buttons
        JPanel carePanel = new JPanel(new GridLayout(2, 1, 0, 8));
        carePanel.setBackground(new Color(35, 35, 45));
        carePanel.setBorder(createStyledBorder("🧘 RECOVERY", new Color(150, 255, 200)));

        JButton showerBtn = createActionButton("🚿 TAKE SHOWER", new Color(100, 200, 255));
        showerBtn.addActionListener(e -> takeShower());
        showerBtn.setToolTipText("Cost: 25 protein | Reduces sweat by 30");

        JButton proteinBtn = createActionButton("🥤 PROTEIN SHAKE", new Color(255, 200, 100));
        proteinBtn.addActionListener(e -> drinkProtein());
        proteinBtn.setToolTipText("Cost: 40 protein | +20 Morale, +4 Sweat");

        carePanel.add(showerBtn);
        carePanel.add(proteinBtn);

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 1));
        infoPanel.setBackground(new Color(35, 35, 45));
        infoPanel.setBorder(createStyledBorder("ℹ️ INFO", new Color(180, 180, 200)));

        JTextArea infoText = new JTextArea(
            "💡 TIP: Keep sweat low!\n" +
            "Sweat drains morale fast.\n" +
            "Train hard, shower often,\n" +
            "and drink protein shakes!"
        );
        infoText.setEditable(false);
        infoText.setFont(new Font("Arial", Font.PLAIN, 11));
        infoText.setBackground(new Color(40, 40, 50));
        infoText.setForeground(new Color(200, 200, 210));
        infoText.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        infoPanel.add(infoText);

        panel.add(trainPanel);
        panel.add(actPanel);
        panel.add(carePanel);
        panel.add(infoPanel);
        return panel;
    }

    private static void drawDogLifting(Graphics2D g2) {
        int w = 380;
        int h = 300;

        // Calculate barbell width based on total stats
        int totalPower = squatPower + benchPower + deadliftPower;
        int baseBarWidth = 120;
        int plateWidth = Math.min(totalPower * 3, 250); // caps at screen edge
        int barWidth = baseBarWidth + plateWidth;

        int centerX = w / 2;
        int barY = 100;

        Color plateColor = new Color(80, 80, 100);
        Color barColor = new Color(150, 150, 160);

        // Draw barbell based on current lift
        if (currentLift.equals("SQUAT")) {
            // Barbell on shoulders
            barY = 80;
            // Left plates
            for (int i = 0; i < Math.min(squatPower, 20); i++) {
                g2.setColor(plateColor);
                g2.fillRect(centerX - barWidth/2 + i * 5, barY - 15, 8, 30);
            }
            // Right plates
            for (int i = 0; i < Math.min(squatPower, 20); i++) {
                g2.setColor(plateColor);
                g2.fillRect(centerX + barWidth/2 - i * 5 - 8, barY - 15, 8, 30);
            }
            // Bar
            g2.setColor(barColor);
            g2.fillRect(centerX - barWidth/2, barY - 3, barWidth, 6);
        } else if (currentLift.equals("BENCH")) {
            // Barbell above dog
            barY = 120;
            int liftOffset = (liftAnimationFrame % 2 == 0) ? -5 : 5;
            barY += liftOffset;
            
            // Left plates
            for (int i = 0; i < Math.min(benchPower, 20); i++) {
                g2.setColor(plateColor);
                g2.fillRect(centerX - barWidth/2 + i * 5, barY - 15, 8, 30);
            }
            // Right plates
            for (int i = 0; i < Math.min(benchPower, 20); i++) {
                g2.setColor(plateColor);
                g2.fillRect(centerX + barWidth/2 - i * 5 - 8, barY - 15, 8, 30);
            }
            // Bar
            g2.setColor(barColor);
            g2.fillRect(centerX - barWidth/2, barY - 3, barWidth, 6);
        } else if (currentLift.equals("DEADLIFT")) {
            // Barbell at ground level
            barY = 200;
            int liftOffset = (liftAnimationFrame < 10) ? -(liftAnimationFrame * 8) : -80;
            barY += liftOffset;
            
            // Left plates (bigger for deadlift)
            for (int i = 0; i < Math.min(deadliftPower, 20); i++) {
                g2.setColor(plateColor);
                g2.fillRect(centerX - barWidth/2 + i * 6, barY - 20, 10, 40);
            }
            // Right plates
            for (int i = 0; i < Math.min(deadliftPower, 20); i++) {
                g2.setColor(plateColor);
                g2.fillRect(centerX + barWidth/2 - i * 6 - 10, barY - 20, 10, 40);
            }
            // Bar
            g2.setColor(barColor);
            g2.fillRect(centerX - barWidth/2, barY - 4, barWidth, 8);
        }

        // Draw wiener dog
        int dogY = 160;
        if (currentLift.equals("SQUAT")) dogY = 180;
        if (currentLift.equals("BENCH")) dogY = 190;
        
        drawWienerDog(g2, centerX - 40, dogY);

        // Draw silly weight indicators if very strong
        if (totalPower > 50) {
            g2.setColor(new Color(255, 50, 50));
            g2.setFont(new Font("Arial Black", Font.BOLD, 20));
            String warn = "⚠️ DANGEROUSLY SWOLE ⚠️";
            g2.drawString(warn, centerX - 130, 250);
        }
    }

    private static void drawWienerDog(Graphics2D g2, int x, int y) {
        Color bodyColor;
        if (currentStage.equals("LEGENDARY")) bodyColor = new Color(255, 215, 0);
        else if (currentStage.equals("CHAMPION")) bodyColor = new Color(200, 100, 255);
        else if (currentStage.equals("ADULT")) bodyColor = new Color(139, 90, 43);
        else if (currentStage.equals("YOUNG")) bodyColor = new Color(160, 110, 60);
        else bodyColor = new Color(180, 140, 100);

        // Body (elongated)
        g2.setColor(bodyColor);
        g2.fillRoundRect(x, y, 80, 30, 15, 15);
        
        // Head
        g2.fillOval(x + 70, y - 5, 30, 30);
        
        // Ears
        g2.setColor(bodyColor.darker());
        g2.fillOval(x + 72, y - 3, 8, 15);
        g2.fillOval(x + 90, y - 3, 8, 15);
        
        // Snout
        g2.setColor(bodyColor.darker());
        g2.fillOval(x + 90, y + 8, 15, 12);
        
        // Nose
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 98, y + 12, 5, 5);
        
        // Legs (stubby)
        g2.setColor(bodyColor);
        g2.fillRect(x + 10, y + 28, 8, 15);
        g2.fillRect(x + 30, y + 28, 8, 15);
        g2.fillRect(x + 50, y + 28, 8, 15);
        g2.fillRect(x + 70, y + 28, 8, 15);
        
        // Tail
        g2.setStroke(new BasicStroke(3));
        g2.drawArc(x - 15, y + 5, 20, 20, 0, 180);
        
        // Eye
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 85, y + 5, 4, 4);
        
        // Muscles if strong
        if (squatPower + benchPower + deadliftPower > 20) {
            g2.setColor(new Color(255, 100, 100, 150));
            g2.fillOval(x + 15, y + 8, 15, 15); // shoulder muscle
            g2.fillOval(x + 50, y + 8, 15, 15); // back muscle
        }
    }

    private static void performClick() {
        if (gameOver || isResting) return;

        long now = System.currentTimeMillis();
        if (now - lastClickTime < COMBO_WINDOW) {
            comboCount = Math.min(comboCount + 1, MAX_COMBO);
        } else {
            comboCount = 1;
        }
        lastClickTime = now;

        int baseGain = clickPower;
        if (hasProteinShaker) baseGain *= 2;
        
        int gain = baseGain * comboCount;
        protein += gain;
        totalWeight += gain;

        updateComboDisplay();
        addLog("💪 Lifted! +" + gain + " protein (x" + comboCount + " combo)");
        
        // Random lift animation
        String[] lifts = {"SQUAT", "BENCH", "DEADLIFT"};
        currentLift = lifts[random.nextInt(3)];
        liftAnimationFrame = 0;
        animateLift();
        
        updateGame();
    }

    private static void animateLift() {
        Timer liftTimer = new Timer(80, null);
        liftTimer.addActionListener(new ActionListener() {
            int frame = 0;
            public void actionPerformed(ActionEvent e) {
                liftAnimationFrame = frame++;
                dogLiftLabel.repaint();
                if (frame > 15) {
                    ((Timer)e.getSource()).stop();
                    currentLift = "IDLE";
                    dogLiftLabel.repaint();
                }
            }
        });
        liftTimer.start();
    }

    private static void trainLift(String type) {
        if (gameOver || isResting) return;

        long now = System.currentTimeMillis();
        int cost = 0;
        long lastTime = 0;

        if (type.equals("SQUAT")) {
            cost = getSquatCost();
            lastTime = lastSquatTime;
        } else if (type.equals("BENCH")) {
            cost = getBenchCost();
            lastTime = lastBenchTime;
        } else if (type.equals("DEADLIFT")) {
            cost = getDeadliftCost();
            lastTime = lastDeadliftTime;
        }

        if (now - lastTime < LIFT_COOLDOWN) {
            addLog("⏳ Still recovering from last lift!");
            return;
        }

        if (energy < 25) {
            addLog("⚡ Not enough energy!");
            return;
        }

        if (protein < cost) {
            addLog("🥤 Not enough protein! Need " + cost);
            return;
        }

        protein -= cost;
        energy -= 25;

        if (type.equals("SQUAT")) {
            squatPower++;
            clickPower++;
            sweat += 5;
            totalReps++;
            lastSquatTime = now;
            addLog("🏋️ Squat training complete! +1 Squat Power");
        } else if (type.equals("BENCH")) {
            benchPower++;
            proteinPerSecond += 2;
            sweat += 4;
            totalReps++;
            lastBenchTime = now;
            addLog("💪 Bench press complete! +2 protein/sec");
        } else if (type.equals("DEADLIFT")) {
            deadliftPower++;
            clickPower += 2;
            sweat += 6;
            totalReps++;
            lastDeadliftTime = now;
            addLog("⚡ Deadlift complete! +2 Click Power");
        }

        currentLift = type;
        liftAnimationFrame = 0;
        animateLift();

        checkEnergy();
        updateGame();
    }

    private static void enterCompetition() {
        if (gameOver) return;

        long now = System.currentTimeMillis();
        if (now - lastCompeteTime < COMPETE_COOLDOWN) {
            addLog("⏳ Competition cooldown active!");
            return;
        }

        if (protein < 150) {
            addLog("🥤 Need 150 protein to enter!");
            return;
        }

        if (energy < 40) {
            addLog("⚡ Need 40 energy to compete!");
            return;
        }

        protein -= 150;
        energy -= 40;
        lastCompeteTime = now;

        int totalPower = squatPower + benchPower + deadliftPower;
        int winChance = Math.min(85, 30 + totalPower);
        
        if (hasPersonalTrainer) winChance += 15;

        boolean won = random.nextInt(100) < winChance;

        if (won) {
            int reward = 300 + (totalPower * 20);
            protein += reward;
            trophies++;
            morale = Math.min(100, morale + 10);
            addLog("🏆 WON CHAMPIONSHIP! +" + reward + " protein, +1 trophy!");
            
            if (trophies >= 10) {
                addLog("🎉 WIENER CHAMPION! You've won 10 trophies!");
            }
        } else {
            int consolation = 50;
            protein += consolation;
            morale -= 15;
            addLog("😔 Lost competition. +" + consolation + " protein, -15 morale");
        }

        updateGame();
    }

    private static void takeShower() {
        if (protein < 25) {
            addLog("🥤 Need 25 protein for a shower!");
            return;
        }

        protein -= 25;
        sweat = Math.max(0, sweat - 30);
        addLog("🚿 Took a refreshing shower! -30 sweat");
        updateGame();
    }

    private static void drinkProtein() {
        if (protein < 40) {
            addLog("🥤 Need 40 protein for a shake!");
            return;
        }

        protein -= 40;
        morale = Math.min(100, morale + 20);
        sweat += 4;
        addLog("🥤 Protein shake consumed! +20 morale");
        updateGame();
    }

    private static void openShop() {
        JDialog shop = new JDialog(mainFrame, "🛒 GYM EQUIPMENT SHOP", true);
        shop.setSize(500, 500);
        shop.setLocationRelativeTo(mainFrame);
        shop.setLayout(new GridLayout(8, 1, 10, 10));
        shop.getContentPane().setBackground(new Color(30, 30, 40));

        JLabel title = new JLabel("💪 UPGRADE YOUR GYM 💪", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 18));
        title.setForeground(new Color(255, 200, 50));
        shop.add(title);

        addShopItem(shop, "🥤 Protein Shaker", 2000, hasProteinShaker, "2x click power");
        addShopItem(shop, "🏋️ Gym Membership", 5000, hasGymMembership, "+10 protein/sec");
        addShopItem(shop, "👟 Personal Trainer", 8000, hasPersonalTrainer, "+15% competition win rate");
        addShopItem(shop, "💪 Power Rack", 12000, hasPowerRack, "Training costs 20% less");
        addShopItem(shop, "🌟 Olympic Barbell", 18000, hasOlympicBar, "Lift heavier weights");
        addShopItem(shop, "💉 Steroids (Legal)", 25000, hasSteroids, "2x all stat gains");

        JButton closeBtn = createActionButton("❌ CLOSE", new Color(200, 50, 50));
        closeBtn.addActionListener(e -> shop.dispose());
        shop.add(closeBtn);

        shop.setVisible(true);
    }

    private static void addShopItem(JDialog shop, String name, int cost, boolean owned, String desc) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(40, 40, 50));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 2));

        JLabel label = new JLabel(name + " - " + cost + " protein");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(owned ? new Color(100, 200, 100) : new Color(200, 200, 210));
        
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(150, 150, 160));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(label);
        textPanel.add(descLabel);

        JButton buyBtn = new JButton(owned ? "✓ OWNED" : "BUY");
        buyBtn.setEnabled(!owned && protein >= cost);
        buyBtn.setBackground(owned ? new Color(50, 100, 50) : new Color(100, 150, 255));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);

        if (!owned) {
            buyBtn.addActionListener(e -> {
                if (protein >= cost) {
                    protein -= cost;
                    handleShopPurchase(name);
                    shop.dispose();
                    openShop();
                }
            });
        }

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buyBtn, BorderLayout.EAST);
        shop.add(panel);
    }

    private static void handleShopPurchase(String item) {
        if (item.contains("Protein Shaker")) {
            hasProteinShaker = true;
            addLog("🥤 Bought Protein Shaker! 2x click power!");
        } else if (item.contains("Gym Membership")) {
            hasGymMembership = true;
            proteinPerSecond += 10;
            addLog("🏋️ Bought Gym Membership! +10 protein/sec!");
        } else if (item.contains("Personal Trainer")) {
            hasPersonalTrainer = true;
            addLog("👟 Hired Personal Trainer! Better competition wins!");
        } else if (item.contains("Power Rack")) {
            hasPowerRack = true;
            addLog("💪 Bought Power Rack! Cheaper training!");
        } else if (item.contains("Olympic Barbell")) {
            hasOlympicBar = true;
            addLog("🌟 Bought Olympic Barbell! Max gains unlocked!");
        } else if (item.contains("Steroids")) {
            hasSteroids = true;
            addLog("💉 Taking steroids! Double stat gains! (Legal, we promise)");
        }
        updateGame();
    }

    private static void prestigeReset() {
        if (trophies < 3) {
            addLog("🏆 Need 3+ trophies to prestige!");
            JOptionPane.showMessageDialog(mainFrame, 
                "You need at least 3 trophies to prestige!\nKeep competing to earn more.",
                "Not Ready", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(mainFrame,
            "Prestige will reset all stats and upgrades\nbut you keep your trophies and gain:\n\n" +
            "• +2 base click power permanently\n" +
            "• Faster progression\n\n" +
            "Are you sure?",
            "Prestige Reset",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            prestigeLevel++;
            int keepTrophies = trophies;
            
            // Reset everything
            protein = 300;
            squatPower = 1;
            benchPower = 1;
            deadliftPower = 1;
            totalReps = 0;
            clickPower = 1 + (prestigeLevel * 2);
            proteinPerSecond = 0;
            morale = 80;
            sweat = 0;
            energy = 100;
            totalWeight = 0;
            
            hasProteinShaker = false;
            hasGymMembership = false;
            hasPersonalTrainer = false;
            hasPowerRack = false;
            hasOlympicBar = false;
            hasSteroids = false;
            
            trophies = keepTrophies;
            
            addLog("🌟 PRESTIGE " + prestigeLevel + "! Starting over with +" + (prestigeLevel * 2) + " base power!");
            updateGame();
        }
    }

    private static void checkEnergy() {
        if (!isResting && energy <= 25) {
            isResting = true;
            addLog("💤 WIENER IS RESTING! Energy recharging...");
        }
    }

    private static void startIdleTimer() {
        Timer idleTimer = new Timer(1000, e -> {
            if (gameOver) return;

            // Energy recovery
            if (isResting) {
                energy = Math.min(MAX_ENERGY, energy + 15);
                if (energy >= MAX_ENERGY) {
                    isResting = false;
                    addLog("⚡ Fully rested! Ready to lift again!");
                }
                updateGame();
                return;
            }

            // Passive protein
            int gain = proteinPerSecond;
            if (hasGymMembership) gain += 10;
            protein += gain;

            // Sweat drains morale
            if (sweat > 0) {
                int drain = Math.max(1, sweat / 6);
                morale -= drain;
                if (morale < 0) morale = 0;
            }

            // Random morale decay
            if (random.nextInt(10) < 2) {
                morale = Math.max(0, morale - 1);
            }

            // Random events
            if (random.nextInt(100) < 5) {
                triggerRandomEvent();
            }

            // Combo decay
            long now = System.currentTimeMillis();
            if (now - lastClickTime > COMBO_WINDOW + 500) {
                if (comboCount > 1) {
                    comboCount = 1;
                    updateComboDisplay();
                }
            }

            updateGame();

            if (morale <= 0) {
                triggerGameOver();
            }
        });
        idleTimer.start();
    }

    private static void triggerRandomEvent() {
        String[] events = {
            "💪 Gym bro spotted you! +50 protein",
            "💦 Accidentally dropped weights! +10 sweat",
            "🎵 Great gym playlist! +5 morale",
            "🏃 Quick cardio session! -8 sweat",
            "📸 Instagram post went viral! +100 protein",
            "😰 Gym crush walked by! +15 sweat, -3 morale",
            "🥤 Found protein shake! +75 protein",
            "💪 New PR! +1 to random stat",
            "🧘 Meditation break! +10 morale",
            "⚡ Pre-workout kicked in! +20 energy"
        };

        String event = events[random.nextInt(events.length)];
        addLog("🎲 " + event);

        if (event.contains("spotted you") || event.contains("viral") || event.contains("protein shake")) {
            int bonus = event.contains("viral") ? 100 : event.contains("shake") ? 75 : 50;
            protein += bonus;
        } else if (event.contains("dropped weights") || event.contains("walked by")) {
            int sweatAdd = event.contains("dropped") ? 10 : 15;
            sweat += sweatAdd;
            if (event.contains("walked by")) morale -= 3;
        } else if (event.contains("playlist") || event.contains("Meditation")) {
            morale = Math.min(100, morale + (event.contains("playlist") ? 5 : 10));
        } else if (event.contains("cardio")) {
            sweat = Math.max(0, sweat - 8);
        } else if (event.contains("New PR")) {
            int choice = random.nextInt(3);
            if (choice == 0) { squatPower++; addLog("   +1 Squat Power!"); }
            else if (choice == 1) { benchPower++; addLog("   +1 Bench Power!"); }
            else { deadliftPower++; addLog("   +1 Deadlift Power!"); }
        } else if (event.contains("Pre-workout")) {
            energy = Math.min(MAX_ENERGY, energy + 20);
        }

        updateGame();
    }

    private static void triggerGameOver() {
        gameOver = true;
        
        JDialog gameOverDialog = new JDialog(mainFrame, "💀 GAME OVER", true);
        gameOverDialog.setSize(400, 250);
        gameOverDialog.setLocationRelativeTo(mainFrame);
        gameOverDialog.setLayout(new GridLayout(4, 1, 10, 10));
        gameOverDialog.getContentPane().setBackground(new Color(60, 20, 20));

        JLabel title = new JLabel("💀 YOUR WIENER QUIT THE GYM 💀", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 18));
        title.setForeground(new Color(255, 100, 100));

        JLabel reason = new JLabel("Morale hit 0% - Your dog gave up!", SwingConstants.CENTER);
        reason.setFont(new Font("Arial", Font.PLAIN, 14));
        reason.setForeground(new Color(200, 200, 210));

        JLabel stats = new JLabel(String.format("Final Stats: %d Squat, %d Bench, %d Deadlift | %d Trophies",
            squatPower, benchPower, deadliftPower, trophies), SwingConstants.CENTER);
        stats.setFont(new Font("Arial", Font.PLAIN, 12));
        stats.setForeground(new Color(180, 180, 190));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        JButton restartBtn = createActionButton("🔄 RESTART", new Color(100, 200, 100));
        restartBtn.addActionListener(e -> {
            gameOverDialog.dispose();
            hardReset();
        });

        JButton quitBtn = createActionButton("😭 QUIT", new Color(200, 50, 50));
        quitBtn.addActionListener(e -> System.exit(0));

        btnPanel.add(restartBtn);
        btnPanel.add(quitBtn);

        gameOverDialog.add(title);
        gameOverDialog.add(reason);
        gameOverDialog.add(stats);
        gameOverDialog.add(btnPanel);

        gameOverDialog.setVisible(true);
    }

    private static void hardReset() {
        gameOver = false;
        protein = 300;
        squatPower = 1;
        benchPower = 1;
        deadliftPower = 1;
        totalReps = 0;
        clickPower = 1;
        proteinPerSecond = 0;
        morale = 80;
        sweat = 0;
        trophies = 0;
        energy = 100;
        totalWeight = 0;
        isResting = false;
        prestigeLevel = 0;
        comboCount = 1;
        
        hasProteinShaker = false;
        hasGymMembership = false;
        hasPersonalTrainer = false;
        hasPowerRack = false;
        hasOlympicBar = false;
        hasSteroids = false;
        
        eventLog.setText("");
        addLog("🏋️ Fresh start! Time to get SWOLE!");
        updateGame();
    }

    private static void updateGame() {
        if (gameOver) return;

        // Update stage
        int totalPower = squatPower + benchPower + deadliftPower;
        String oldStage = currentStage;
        if (totalPower > 60) currentStage = "LEGENDARY";
        else if (totalPower > 40) currentStage = "CHAMPION";
        else if (totalPower > 25) currentStage = "ADULT";
        else if (totalPower > 12) currentStage = "YOUNG";
        else currentStage = "PUPPY";

        if (!currentStage.equals(oldStage)) {
            addLog("⭐ STAGE UP! Now " + currentStage + "!");
            protein += 200;
        }

        // Update weight display
        currentLiftWeight = 45 + (totalPower * 10);
        
        // Update labels
        proteinLabel.setText("🥤 PROTEIN: " + protein);
        ppsLabel.setText("⚡ +" + proteinPerSecond + "/sec");
        squatLabel.setText("🏋️ SQUAT POWER: " + squatPower);
        benchLabel.setText("💪 BENCH POWER: " + benchPower);
        deadliftLabel.setText("⚡ DEADLIFT POWER: " + deadliftPower);
        moraleLabel.setText("😊 MORALE: " + morale + "%");
        energyLabel.setText("⚡ ENERGY: " + energy + "%");
        sweatLabel.setText("💦 SWEAT: " + sweat);
        trophiesLabel.setText("🏆 TROPHIES: " + trophies + "/10");
        stageLabel.setText("STAGE: " + currentStage);
        weightLabel.setText("CURRENT LIFT: " + currentLiftWeight + " lbs");

        // Update progress bars
        moraleBar.setValue(morale);
        moraleBar.setForeground(morale > 50 ? new Color(100, 255, 100) : morale > 25 ? new Color(255, 200, 0) : new Color(255, 50, 50));
        
        energyBar.setValue(energy);
        energyBar.setForeground(energy > 50 ? new Color(255, 215, 0) : new Color(200, 100, 0));
        
        sweatBar.setValue(Math.min(100, sweat));
        sweatBar.setForeground(sweat < 30 ? new Color(100, 200, 255) : sweat < 60 ? new Color(255, 200, 0) : new Color(255, 100, 50));

        dogLiftLabel.repaint();
    }

    private static void updateComboDisplay() {
        comboLabel.setText("COMBO: x" + comboCount);
        comboBar.setValue(comboCount);
        
        Color comboColor;
        if (comboCount >= 25) comboColor = new Color(255, 50, 255);
        else if (comboCount >= 20) comboColor = new Color(255, 50, 50);
        else if (comboCount >= 15) comboColor = new Color(255, 120, 0);
        else if (comboCount >= 10) comboColor = new Color(255, 200, 0);
        else comboColor = new Color(200, 200, 200);
        
        comboLabel.setForeground(comboColor);
        comboBar.setForeground(comboColor);
    }

    // === HELPER METHODS ===

    private static int getSquatCost() {
        int base = 100 + (squatPower * 20);
        return hasPowerRack ? (int)(base * 0.8) : base;
    }

    private static int getBenchCost() {
        int base = 85 + (benchPower * 18);
        return hasPowerRack ? (int)(base * 0.8) : base;
    }

    private static int getDeadliftCost() {
        int base = 120 + (deadliftPower * 25);
        return hasPowerRack ? (int)(base * 0.8) : base;
    }

    private static void addLog(String msg) {
        eventLog.append(msg + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
        if (eventLog.getLineCount() > 50) {
            try {
                eventLog.replaceRange("", 0, eventLog.getLineEndOffset(15));
            } catch (Exception ignored) {}
        }
    }

    private static JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }

    private static JLabel createLargeStatLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial Black", Font.BOLD, 16));
        label.setForeground(color);
        return label;
    }

    private static JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(color);
        return label;
    }

    private static JProgressBar createProgressBar(int value, Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(new Color(40, 40, 50));
        bar.setPreferredSize(new Dimension(0, 20));
        return bar;
    }

    private static Border createStyledBorder(String title, Color color) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial Black", Font.BOLD, 12),
            color
        );
        return BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
    }
}
