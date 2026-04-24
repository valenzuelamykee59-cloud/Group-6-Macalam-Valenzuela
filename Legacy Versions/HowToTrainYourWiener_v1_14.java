 import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;

public class HowToTrainYourWiener_v1_14 {

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

    // === STAMINA ===
    private static int stamina = 100;
    private static final int MAX_STAMINA = 100;
    private static boolean isResting = false;
    private static JProgressBar staminaBar;
    private static JLabel staminaLabel;

    // === GAME STATE ===
    private static boolean gameOver = false;

    // === GAMBLING ===
    private static int gamblesWon = 0;
    private static int gamblesLost = 0;

    // === UI ===
    private static JLabel treatsLabel;
    private static JLabel tpsLabel;
    private static JLabel strengthValLabel, speedValLabel, obeValLabel, charmValLabel;
    private static JLabel happinessValLabel, messValLabel, prestigeValLabel, trophiesValLabel, wienersValLabel, staminaValLabel;
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

    // === ANIMATION ===
    private static JButton petBtnRef = null;
    private static javax.swing.Timer clickAnimTimer = null;
    private static javax.swing.Timer growAnimTimer = null;
    private static javax.swing.Timer bounceAnimTimer = null;
    private static final int DOG_ORIGIN_X = 45;   // dogLabel's true resting X
    private static final int DOG_ORIGIN_Y = 45;   // dogLabel's true resting Y
    private static int clickAnimStep = 0;
    private static int growAnimStep = 0;
    private static int dogBaseFontSize = 72;
    private static JPanel particleLayer = null;  // transparent overlay for Cookie Clicker particles
    private static JFrame mainFrame = null;
    private static int shakeOffsetX = 0;
    private static int shakeOffsetY = 0;

    // === COMBO / BOOST SYSTEM ===
    private static int comboCount = 0;           // how many rapid clicks in a row
    private static long lastClickTime = 0;        // ms of last click
    private static final int COMBO_WINDOW = 600;  // ms window to chain clicks
    private static final int MAX_COMBO = 20;      // cap combo at 20x
    private static JLabel comboLabel = null;      // displayed in UI
    private static javax.swing.Timer comboDecayTimer = null; // resets combo if idle

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
        mainFrame = frame;
        frame.setSize(1000, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(18, 18, 24));

        // Transparent particle overlay — sits above everything, mouse events pass through
        particleLayer = new JPanel(null) {
            @Override public boolean contains(int x, int y) { return false; } // click-through
        };
        particleLayer.setOpaque(false);

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

        // === DOG DISPLAY — Island Theme ===
        dogPanel = new JPanel(null);
        dogPanel.setBounds(355, 65, 290, 185);
        dogPanel.setBackground(new Color(184, 221, 184));
        dogPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 170, 90), 3),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        // Sky/island background
        JPanel skyPanel = new JPanel(null);
        skyPanel.setBounds(0, 0, 290, 185);
        skyPanel.setBackground(new Color(214, 236, 214));
        skyPanel.setOpaque(true);
        dogPanel.add(skyPanel);

        // Ground oval
        JLabel groundLabel = new JLabel("") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(126, 200, 126));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(90, 170, 90));
                g2.drawOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
            }
        };
        groundLabel.setBounds(20, 135, 240, 60);
        skyPanel.add(groundLabel);

        // Trees
        JLabel treeLeft = new JLabel("🌴");
        treeLeft.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        treeLeft.setBounds(8, 105, 36, 36);
        skyPanel.add(treeLeft);

        JLabel treeRight = new JLabel("🌸");
        treeRight.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        treeRight.setBounds(246, 108, 36, 36);
        skyPanel.add(treeRight);

        // Dumbbells
        JLabel dbLeft = new JLabel("🏋️");
        dbLeft.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        dbLeft.setBounds(40, 118, 30, 28);
        skyPanel.add(dbLeft);

        JLabel dbRight = new JLabel("🏋️");
        dbRight.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        dbRight.setBounds(218, 118, 30, 28);
        skyPanel.add(dbRight);

        // Wiener dog — custom painted, centered in skyPanel (290 wide)
        dogLabel = new JLabel("") {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int ox = shakeOffsetX;
                int oy = shakeOffsetY;

                if ("LEGENDARY".equals(currentStage)) {
                    // ════ LEGENDARY STAGE: TESTEKLE FORM ════
                    int cx = w / 2 + ox;
                    int ballsY = h / 2 + oy + 18;  // balls sit in lower half
                    int bw = 46, bh = 52;           // ball dimensions
                    int gap = 10;                   // gap between centres

                    // --- SHAFT (big beefy shaft rising above the balls) ---
                    int shaftW = 28;                // wide shaft
                    int shaftH = 62;               // tall shaft
                    int shaftX = cx - shaftW / 2 + ox;
                    int shaftY = ballsY - bh/2 - shaftH + 6 + oy; // sits right on top of balls
                    // shaft base colour
                    g2.setColor(new Color(210, 140, 100));
                    g2.fillRoundRect(shaftX, shaftY, shaftW, shaftH, 14, 14);
                    // shaft shading (right side darker)
                    g2.setColor(new Color(180, 105, 65, 160));
                    g2.fillRoundRect(shaftX + shaftW/2, shaftY + 4, shaftW/2 - 2, shaftH - 8, 10, 10);
                    // shaft highlight (left side lighter)
                    g2.setColor(new Color(255, 210, 175, 130));
                    g2.fillRoundRect(shaftX + 4, shaftY + 6, 8, shaftH - 16, 6, 6);
                    // shaft vein (subtle)
                    g2.setColor(new Color(180, 100, 60, 90));
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx + 4 + ox, shaftY + 10, cx + 6 + ox, shaftY + shaftH - 10);
                    g2.setStroke(new BasicStroke(1));
                    // shaft outline
                    g2.setColor(new Color(140, 70, 40, 170));
                    g2.drawRoundRect(shaftX, shaftY, shaftW, shaftH, 14, 14);
                    // glans (rounded tip)
                    int glanW = 34, glanH = 24;
                    int glanX = cx - glanW / 2 + ox;
                    int glanY = shaftY - glanH + 10;
                    g2.setColor(new Color(220, 130, 90));
                    g2.fillOval(glanX, glanY, glanW, glanH);
                    // glans shading
                    g2.setColor(new Color(185, 100, 65, 150));
                    g2.fillOval(glanX + glanW/2, glanY + 4, glanW/2 - 2, glanH - 6);
                    // glans highlight
                    g2.setColor(new Color(255, 220, 190, 140));
                    g2.fillOval(glanX + 4, glanY + 3, 10, 7);
                    // glans outline
                    g2.setColor(new Color(140, 70, 40, 160));
                    g2.drawOval(glanX, glanY, glanW, glanH);
                    // corona ridge (rim line)
                    g2.setColor(new Color(160, 85, 50, 180));
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(glanX, glanY + glanH - 8, glanW, 10, 0, -180);
                    g2.setStroke(new BasicStroke(1));

                    // --- sack skin base (left ball) ---
                    g2.setColor(new Color(210, 140, 100));
                    g2.fillOval(cx - gap - bw + ox, ballsY - bh/2 + oy, bw, bh);
                    // --- sack skin base (right ball) ---
                    g2.fillOval(cx + gap/2 + ox, ballsY - bh/2 + oy, bw, bh);

                    // --- shading / depth (left) ---
                    g2.setColor(new Color(185, 110, 70, 140));
                    g2.fillOval(cx - gap - bw + 6 + ox, ballsY - bh/2 + 8 + oy, bw - 10, bh - 10);
                    // --- shading / depth (right) ---
                    g2.fillOval(cx + gap/2 + 4 + ox, ballsY - bh/2 + 8 + oy, bw - 10, bh - 10);

                    // --- highlight (left) ---
                    g2.setColor(new Color(255, 210, 175, 130));
                    g2.fillOval(cx - gap - bw + 8 + ox, ballsY - bh/2 + 6 + oy, 14, 12);
                    // --- highlight (right) ---
                    g2.fillOval(cx + gap/2 + 10 + ox, ballsY - bh/2 + 6 + oy, 14, 12);

                    // --- centre crease / divot line ---
                    g2.setColor(new Color(160, 85, 50, 180));
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx + ox, ballsY - bh/2 + 4 + oy, cx + ox, ballsY + bh/2 - 4 + oy);
                    g2.setStroke(new BasicStroke(1));

                    // --- wrinkle lines (left ball) ---
                    g2.setColor(new Color(160, 85, 50, 100));
                    g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(cx - gap - bw + 10 + ox, ballsY - 6 + oy, 20, 12, 10, 160);
                    g2.drawArc(cx - gap - bw + 14 + ox, ballsY + 6 + oy, 16, 10, 20, 140);
                    // --- wrinkle lines (right ball) ---
                    g2.drawArc(cx + gap/2 + 8 + ox, ballsY - 6 + oy, 20, 12, 10, 160);
                    g2.drawArc(cx + gap/2 + 12 + ox, ballsY + 6 + oy, 16, 10, 20, 140);
                    g2.setStroke(new BasicStroke(1));

                    // --- outline (left) ---
                    g2.setColor(new Color(140, 70, 40, 160));
                    g2.drawOval(cx - gap - bw + ox, ballsY - bh/2 + oy, bw, bh);
                    // --- outline (right) ---
                    g2.drawOval(cx + gap/2 + ox, ballsY - bh/2 + oy, bw, bh);

                    // --- crown on top of glans (it's LEGENDARY after all) ---
                    g2.setColor(new Color(255, 215, 0));
                    int[] crownX = {cx - 14 + ox, cx - 14 + ox, cx - 7 + ox, cx + ox, cx + 7 + ox, cx + 14 + ox, cx + 14 + ox};
                    int[] crownY = {glanY - 2 + oy, glanY - 14 + oy, glanY - 8 + oy, glanY - 16 + oy, glanY - 8 + oy, glanY - 14 + oy, glanY - 2 + oy};
                    g2.fillPolygon(crownX, crownY, 7);
                    g2.setColor(new Color(200, 160, 0));
                    g2.drawPolygon(crownX, crownY, 7);
                    // crown jewel dots
                    g2.setColor(new Color(255, 80, 40));
                    g2.fillOval(cx - 11 + ox, glanY - 17 + oy, 5, 5);
                    g2.fillOval(cx - 2 + ox,  glanY - 19 + oy, 5, 5);
                    g2.fillOval(cx + 7 + ox,  glanY - 17 + oy, 5, 5);

                    g2.dispose();
                    return;
                }

                // ════ NORMAL STAGES ════
                // ---- body (long sausage) ----
                g2.setColor(new Color(160, 82, 45));
                g2.fillRoundRect(8 + ox, h/2 - 14 + oy, w - 20, 28, 28, 28);
                // belly highlight
                g2.setColor(new Color(205, 133, 63));
                g2.fillRoundRect(18 + ox, h/2 - 7 + oy, w - 45, 14, 14, 14);
                // ---- head ----
                g2.setColor(new Color(160, 82, 45));
                g2.fillOval(w - 36 + ox, h/2 - 18 + oy, 38, 34);
                // ear
                g2.setColor(new Color(101, 50, 20));
                g2.fillOval(w - 22 + ox, h/2 - 14 + oy, 14, 20);
                // snout
                g2.setColor(new Color(205, 133, 63));
                g2.fillOval(w - 12 + ox, h/2 - 4 + oy, 16, 12);
                // nose
                g2.setColor(new Color(40, 20, 10));
                g2.fillOval(w - 3 + ox, h/2 - 1 + oy, 7, 6);
                // eye
                g2.setColor(Color.WHITE);
                g2.fillOval(w - 23 + ox, h/2 - 9 + oy, 8, 8);
                g2.setColor(new Color(30, 15, 5));
                g2.fillOval(w - 21 + ox, h/2 - 7 + oy, 5, 5);
                g2.setColor(Color.WHITE);
                g2.fillOval(w - 20 + ox, h/2 - 8 + oy, 2, 2);
                // ---- tail (curled) ----
                g2.setColor(new Color(160, 82, 45));
                g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(2 + ox, h/2 - 20 + oy, 18, 22, 200, -240);
                g2.setStroke(new BasicStroke(1));
                // ---- legs (4 short stubs) ----
                g2.setColor(new Color(130, 65, 30));
                int[] legX = {28, 44, 68, 84};
                for (int lx : legX) {
                    g2.fillRoundRect(lx + ox, h/2 + 12 + oy, 10, 16, 6, 6);
                    // paw
                    g2.setColor(new Color(180, 100, 60));
                    g2.fillOval(lx - 1 + ox, h/2 + 25 + oy, 12, 7);
                    g2.setColor(new Color(130, 65, 30));
                }
                g2.dispose();
            }
        };
        dogLabel.setBounds(45, 45, 200, 90);  // Back to original centered position
        skyPanel.add(dogLabel);

        // Stage label
        stageLabel = new JLabel("[ STAGE: PUPPY ]", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Courier New", Font.BOLD, 10));
        stageLabel.setForeground(new Color(45, 90, 45));
        stageLabel.setBounds(0, 165, 290, 18);
        dogPanel.add(stageLabel);

        // Click the dog to pet it
        MouseAdapter dogPetListener = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime < COMBO_WINDOW) {
                    comboCount = Math.min(comboCount + 1, MAX_COMBO);
                } else {
                    comboCount = 1;
                }
                lastClickTime = now;
                if (comboDecayTimer != null) comboDecayTimer.stop();
                comboDecayTimer = new javax.swing.Timer(COMBO_WINDOW + 200, ev -> {
                    comboCount = 0;
                    updateComboLabel();
                    comboDecayTimer.stop();
                });
                comboDecayTimer.setRepeats(false);
                comboDecayTimer.start();
                updateComboLabel();
                Point p = SwingUtilities.convertPoint((java.awt.Component)e.getSource(), e.getPoint(), particleLayer);
                int base = clickPower * (hasClickMultiplier ? 2 : 1) * (hasGoldenLeash ? 2 : 1);
                int gain = base * Math.max(1, comboCount);
                spawnTreatParticle(p.x, p.y, "+" + gain + " 🍖", comboCount);
                treats += gain;
                totalTreatsEarned += gain;
                dogHappiness = Math.min(100, dogHappiness + 1);
                if (comboCount >= 10) addLog("> 🔥 COMBO x" + comboCount + "! +" + gain + " treats!");
                else addLog("> PET: +" + gain + " treats  [HAPPINESS +1]" + (comboCount > 1 ? "  COMBO x" + comboCount : ""));
                updateGame();
                animateDogBounce();
                if (comboCount >= 5) shakeFrame(comboCount);
            }
        };
        dogLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dogLabel.addMouseListener(dogPetListener);
        skyPanel.addMouseListener(dogPetListener);

        mainPanel.add(dogPanel);

        // === LEFT STATS PANEL ===
        JPanel statsPanel = new JPanel(new GridLayout(10, 2, 3, 6));
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
        statsPanel.add(makeStatKey("⚡ STAMINA:"));   staminaValLabel  = makeStatVal("100", new Color(0, 200, 255));   statsPanel.add(staminaValLabel);
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
        trainingBar.setBounds(355, 258, 290, 20);
        trainingBar.setBackground(Color.BLACK);
        trainingBar.setForeground(new Color(255, 80, 40));
        trainingBar.setStringPainted(true);
        trainingBar.setFont(new Font("Courier New", Font.BOLD, 9));
        mainPanel.add(trainingBar);

        // === COMBO METER ===
        comboLabel = new JLabel("COMBO: x1", SwingConstants.CENTER);
        comboLabel.setBounds(355, 285, 290, 22);
        comboLabel.setFont(new Font("Courier New", Font.BOLD, 13));
        comboLabel.setForeground(new Color(255, 215, 0));
        comboLabel.setBackground(new Color(8, 8, 12));
        comboLabel.setOpaque(true);
        comboLabel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 100), 1));
        mainPanel.add(comboLabel);

        // === STAMINA BAR ===
        staminaBar = new JProgressBar(0, MAX_STAMINA);
        staminaBar.setBounds(355, 310, 210, 20);
        staminaBar.setBackground(Color.BLACK);
        staminaBar.setForeground(new Color(0, 200, 255));
        staminaBar.setValue(100);
        staminaBar.setStringPainted(true);
        staminaBar.setFont(new Font("Courier New", Font.BOLD, 9));
        staminaBar.setString("STAMINA: 100/100");
        mainPanel.add(staminaBar);

        JButton restBtn = makeBtn("💤 REST", new Color(100, 180, 255));
        restBtn.setBounds(572, 308, 73, 24);
        restBtn.addActionListener(e -> {
            if (!isResting) {
                isResting = true;
                addLog("> RESTING... Stamina recharging (10 sec)");
                updateGame();
                new javax.swing.Timer(10000, ev -> {
                    stamina = MAX_STAMINA;
                    isResting = false;
                    addLog("> STAMINA FULLY RESTORED! Back in action!");
                    ((javax.swing.Timer)ev.getSource()).stop();
                    updateGame();
                }).start();
            } else {
                addLog("! Already resting...");
            }
        });
        mainPanel.add(restBtn);
        eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setBackground(new Color(5, 5, 8));
        eventLog.setForeground(new Color(0, 230, 0));
        eventLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane scrollPane = new JScrollPane(eventLog);
        scrollPane.setBounds(15, 335, 968, 92);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 80, 40)),
            ">> SYSTEM LOG <<",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Courier New", Font.BOLD, 10),
            new Color(255, 80, 40)
        ));
        mainPanel.add(scrollPane);

        // === BUTTONS ROW 1 ===
        JButton strengthBtn = makeBtn("💪 STRENGTH TRAIN [80+]", new Color(100, 230, 100));
        strengthBtn.setBounds(15, 435, 230, 42);
        strengthBtn.addActionListener(e -> trainStat("strength"));
        mainPanel.add(strengthBtn);

        JButton speedBtn = makeBtn("⚡ ZOOMIES TRAIN [65+]", new Color(100, 200, 255));
        speedBtn.setBounds(258, 435, 230, 42);
        speedBtn.addActionListener(e -> trainStat("speed"));
        mainPanel.add(speedBtn);

        JButton obeBtn = makeBtn("🎓 OBEDIENCE CLASS [100+]", new Color(255, 200, 100));
        obeBtn.setBounds(501, 435, 230, 42);
        obeBtn.addActionListener(e -> trainStat("obedience"));
        mainPanel.add(obeBtn);

        JButton charmBtn = makeBtn("✨ CHARM LESSONS [90+]", new Color(255, 150, 220));
        charmBtn.setBounds(744, 435, 240, 42);
        charmBtn.addActionListener(e -> trainStat("charm"));
        mainPanel.add(charmBtn);

        // === BUTTONS ROW 2 ===
        JButton wienerBtn = makeBtn("🌭 HUNT WIENER [40]", new Color(255, 140, 50));
        wienerBtn.setBounds(15, 487, 185, 42);
        wienerBtn.addActionListener(e -> huntWiener());
        mainPanel.add(wienerBtn);

        JButton competeBtn = makeBtn("🏆 COMPETITION [120]", new Color(255, 215, 0));
        competeBtn.setBounds(210, 487, 185, 42);
        competeBtn.addActionListener(e -> enterCompetition());
        mainPanel.add(competeBtn);

        JButton shopBtn = makeBtn("🛒 SHOP UPGRADES", new Color(160, 100, 255));
        shopBtn.setBounds(405, 487, 185, 42);
        shopBtn.addActionListener(e -> openShop());
        mainPanel.add(shopBtn);

        JButton gambleBtn = makeBtn("🪙 CASINO RISK IT", new Color(255, 215, 50));
        gambleBtn.setBounds(600, 487, 185, 42);
        gambleBtn.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(gambleBtn, e.getPoint(), particleLayer);
                spawnCoinBurst(p.x, p.y);
            }
        });
        gambleBtn.addActionListener(e -> {
            animateCoinFlip(gambleBtn, () -> openGamble());
        });
        mainPanel.add(gambleBtn);

        JButton prestigeBtn = makeBtn("🌟 PRESTIGE [RESET]", new Color(255, 80, 200));
        prestigeBtn.setBounds(795, 487, 185, 42);
        prestigeBtn.addActionListener(e -> prestigeReset());
        mainPanel.add(prestigeBtn);

        // === BUTTONS ROW 3 ===
        JButton cleanBtn = makeBtn("🧹 CLEAN MESS [20]", new Color(100, 200, 200));
        cleanBtn.setBounds(15, 539, 185, 35);
        cleanBtn.addActionListener(e -> {
            if (dogMessiness > 0 && treats >= 20) {
                treats -= 20;
                dogMessiness = Math.max(0, dogMessiness - 20);
                addLog("> CLEAN: Mess reduced to " + dogMessiness);
                updateGame();
            } else if (treats < 20) {
                addLog("! INSUFFICIENT TREATS ! Need 20 to clean");
            }
        });
        mainPanel.add(cleanBtn);

        JButton feedBtn = makeBtn("🍗 FEED TREAT [30]", new Color(255, 180, 80));
        feedBtn.setBounds(210, 539, 185, 35);
        feedBtn.addActionListener(e -> {
            if (treats >= 30) {
                treats -= 30;
                dogHappiness = Math.min(100, dogHappiness + 15);
                dogMessiness += 3;
                addLog("> FEED: Happiness +15! [MESS +3]");
                updateGame();
            } else {
                addLog("! INSUFFICIENT TREATS ! Need 30");
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

        // Add particle overlay on top via the layered pane
        JLayeredPane lp = frame.getLayeredPane();
        particleLayer.setBounds(0, 0, 1000, 750);
        lp.add(particleLayer, JLayeredPane.POPUP_LAYER);

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
            case "strength": cost = 80 + strength * 15;  lastTime = lastStrengthTime; label = "STRENGTH"; break;
            case "speed":    cost = 65 + speed * 12;     lastTime = lastSpeedTime;    label = "SPEED";    break;
            case "obedience":cost = 100 + obedience * 18;lastTime = lastObedienceTime;label = "OBEDIENCE";break;
            case "charm":    cost = 90 + charm * 14;     lastTime = lastCharmTime;    label = "CHARM";    break;
            default: return;
        }

        if (now - lastTime < TRAIN_COOLDOWN) {
            int remaining = (int)((TRAIN_COOLDOWN - (now - lastTime)) / 1000) + 1;
            addLog("! " + label + " training on cooldown: " + remaining + "s remaining");
            return;
        }
        if (isResting) { addLog("! Wiener is RESTING — wait for stamina to refill!"); return; }
        if (stamina < 20) { addLog("! STAMINA TOO LOW! Wiener needs to rest!"); return; }
        if (treats < cost) { addLog("! INSUFFICIENT TREATS ! Need " + cost); return; }

        treats -= cost;
        stamina -= 20;
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
        if (now - lastHuntTime < 8000) {
            addLog("! Hunt on cooldown: " + ((8000 - (now - lastHuntTime)) / 1000 + 1) + "s");
            return;
        }
        if (isResting) { addLog("! Wiener is RESTING — wait for stamina to refill!"); return; }
        if (stamina < 15) { addLog("! STAMINA TOO LOW to hunt!"); return; }
        if (treats < 40) { addLog("! INSUFFICIENT TREATS ! Need 40"); return; }
        treats -= 40;
        stamina -= 15;
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
        if (isResting) { addLog("! Wiener is RESTING — can't compete yet!"); return; }
        if (stamina < 30) { addLog("! Not enough STAMINA to compete! Need 30"); return; }
        if (treats < 120) { addLog("! INSUFFICIENT TREATS ! Need 120 to enter competition"); return; }
        treats -= 120;
        stamina -= 30;
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
            int happLoss = 10 + random.nextInt(6); // lose 10-15 happiness on loss
            dogHappiness = Math.max(0, dogHappiness - happLoss);
            addLog("😔 COMPETE [" + eventName + "]: Lost... +" + consolation + " treats | HAPPINESS -" + happLoss + " | Win chance was " + winChance + "%");
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
            if (gameOver) return;
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
            {"🔁 CLICK MULTIPLIER", "2x click rewards", 3500, hasClickMultiplier},
            {"🤖 AUTO-PETTER", "Auto-pets every 8s", 8000, hasAutoCollector},
            {"✨ GOLDEN LEASH", "+50% all income", 18000, hasGoldenLeash},
            {"🏭 TREAT FACTORY", "Double speed bonus", 35000, hasTreatFactory},
            {"🧙 DOG WHISPERER", "Double obedience bonus", 22000, hasDogWhisperer},
            {"👟 SPEED BOOTS", "+5 treats/sec instantly", 12000, hasSpeedBoots},
            {"😊 HAPPINESS POTION", "+50 happiness [100]", 750, null},
            {"🍖 TREAT PACK", "200 → 500 treats", 1500, null},
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
        if (gameOver) return;
        updateStage();
        updateDogDisplay();
        updateLabels();
        updateProgressBar();
        updateStaminaBar();
        if (dogHappiness <= 0) triggerGameOver();
    }

    private static void updateStaminaBar() {
        if (staminaBar == null) return;
        staminaBar.setValue(stamina);
        staminaBar.setString(isResting ? "💤 RESTING... " + stamina + "/100" : "STAMINA: " + stamina + "/100");
        Color stCol = stamina > 60 ? new Color(0, 200, 255) : stamina > 30 ? new Color(255, 200, 0) : new Color(255, 60, 60);
        staminaBar.setForeground(stCol);
    }

    private static void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;

        // Build the game over overlay dialog
        JDialog gd = new JDialog(mainFrame, "💀 GAME OVER 💀", true);
        gd.setSize(480, 320);
        gd.setLocationRelativeTo(mainFrame);
        gd.setResizable(false);
        gd.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel p = new JPanel(null);
        p.setBackground(new Color(10, 5, 5));

        JLabel skull = new JLabel("💀", SwingConstants.CENTER);
        skull.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 54));
        skull.setBounds(0, 15, 480, 65);
        p.add(skull);

        JLabel title = new JLabel("YOUR WIENER HAS GIVEN UP ON LIFE", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 14));
        title.setForeground(new Color(255, 50, 50));
        title.setBounds(0, 85, 480, 22);
        p.add(title);

        JLabel sub = new JLabel("Happiness reached 0% — the wiener is done.", SwingConstants.CENTER);
        sub.setFont(new Font("Courier New", Font.ITALIC, 11));
        sub.setForeground(new Color(160, 100, 100));
        sub.setBounds(0, 112, 480, 18);
        p.add(sub);

        // RESTART button
        JButton restartBtn = new JButton("🔄  RESTART");
        restartBtn.setFont(new Font("Courier New", Font.BOLD, 13));
        restartBtn.setBackground(new Color(20, 80, 20));
        restartBtn.setForeground(new Color(100, 255, 100));
        restartBtn.setFocusPainted(false);
        restartBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 255, 100), 2));
        restartBtn.setBounds(60, 155, 160, 50);
        restartBtn.addActionListener(e -> {
            gd.dispose();
            hardReset();
        });
        p.add(restartBtn);

        // QUIT button
        JButton quitBtn = new JButton("<html><center>😭 I'M A CRY BABY<br>LOSER, I QUIT</center></html>");
        quitBtn.setFont(new Font("Courier New", Font.BOLD, 11));
        quitBtn.setBackground(new Color(40, 10, 10));
        quitBtn.setForeground(new Color(255, 80, 80));
        quitBtn.setFocusPainted(false);
        quitBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 40, 40), 2));
        quitBtn.setBounds(255, 155, 170, 50);
        quitBtn.addActionListener(e -> System.exit(0));
        p.add(quitBtn);

        JLabel tip = new JLabel("TIP: Feed your wiener more treats to keep it happy!", SwingConstants.CENTER);
        tip.setFont(new Font("Courier New", Font.PLAIN, 10));
        tip.setForeground(new Color(100, 80, 80));
        tip.setBounds(0, 225, 480, 16);
        p.add(tip);

        gd.add(p);
        gd.setVisible(true);
    }

    private static void hardReset() {
        gameOver = false;
        treats = 200; strength = 1; speed = 1; obedience = 1; charm = 1;
        wienersFound = 0; clickPower = 1; treatsPerSecond = 0;
        dogHappiness = 60; dogMessiness = 0; trophies = 0;
        totalTreatsEarned = 0; stamina = MAX_STAMINA; isResting = false;
        hasClickMultiplier = false; hasAutoCollector = false;
        hasGoldenLeash = false; hasTreatFactory = false;
        hasDogWhisperer = false; hasSpeedBoots = false;
        prestigeLevel = 0; currentStage = "PUPPY";
        firstTrophy = false; champion = false;
        gamblesWon = 0; gamblesLost = 0; comboCount = 0;
        lastStrengthTime = 0; lastSpeedTime = 0;
        lastObedienceTime = 0; lastCharmTime = 0;
        lastHuntTime = 0; lastCompeteTime = 0;
        addLog("═══ GAME RESET — Train harder this time! ═══");
        updateGame();
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
            animateDogGrow();
        }
    }

    private static void updateDogDisplay() {
        String[] emojis  = {"🐶", "🐕", "🐕‍🦺", "🏆🐕", "⭐🐕⭐"};
        String[] stages  = {"PUPPY", "YOUNG", "ADULT", "CHAMPION", "LEGENDARY"};
        for (int i = 0; i < stages.length; i++) {
            if (currentStage.equals(stages[i])) { dogLabel.setText(emojis[i]); break; }
        }

        // Island panel border glows green/yellow/red with happiness
        Color border = dogHappiness > 70 ? new Color(90, 200, 90) :
                       dogHappiness > 35 ? new Color(200, 200, 0) : new Color(220, 60, 60);
        dogPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 3),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        stageLabel.setText("[ STAGE: " + currentStage + " ]");
        stageLabel.setForeground(dogHappiness > 70 ? new Color(45, 90, 45) :
                                 dogHappiness > 35 ? new Color(130, 100, 0) : new Color(160, 40, 40));
    }

    private static void updateLabels() {
        strengthValLabel.setText(String.valueOf(strength));
        speedValLabel.setText(String.valueOf(speed));
        obeValLabel.setText(String.valueOf(obedience));
        charmValLabel.setText(String.valueOf(charm));
        happinessValLabel.setText(dogHappiness + "%");
        happinessValLabel.setForeground(dogHappiness > 70 ? new Color(100, 255, 180) :
                                        dogHappiness > 35 ? new Color(255, 200, 0)   : new Color(255, 80, 80));
        if (staminaValLabel != null) {
            staminaValLabel.setText(isResting ? "💤 REST" : stamina + "%");
            staminaValLabel.setForeground(stamina > 60 ? new Color(0, 200, 255) :
                                          stamina > 30 ? new Color(255, 200, 0) : new Color(255, 60, 60));
        }
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

    // === ANIMATIONS ===

    /** Flashes the pet button with a bright colour burst on click */
    private static void animateClick(JButton btn) {
        if (clickAnimTimer != null && clickAnimTimer.isRunning()) clickAnimTimer.stop();
        clickAnimStep = 0;
        Rectangle origin = btn.getBounds();
        Color[] frames = {
            new Color(255, 240, 0),
            new Color(255, 160, 30),
            new Color(255, 100, 20),
            new Color(255, 80,  40),
        };
        clickAnimTimer = new javax.swing.Timer(55, null);
        clickAnimTimer.addActionListener(e -> {
            if (clickAnimStep < frames.length) {
                btn.setForeground(frames[clickAnimStep]);
                int jitter = (clickAnimStep % 2 == 0) ? 3 : -3;
                btn.setBounds(origin.x + jitter, origin.y, origin.width, origin.height);
                clickAnimStep++;
            } else {
                btn.setBounds(origin.x, origin.y, origin.width, origin.height);
                btn.setForeground(new Color(255, 80, 40));
                clickAnimTimer.stop();
            }
        });
        clickAnimTimer.start();
    }

    /** Coin-flip animation on the casino button before opening the dialog */
    private static void animateCoinFlip(JButton btn, Runnable onComplete) {
        btn.setEnabled(false);
        // Frames simulate a coin spinning: edge-on → face → edge-on → other face → repeat
        String[] frames = {
            "🪙 CASINO RISK IT",   // full coin
            "| CASINO RISK IT",    // edge-on (thin)
            "🟡 CASINO RISK IT",  // other face (gold circle)
            "| CASINO RISK IT",
            "🪙 CASINO RISK IT",
            "| CASINO RISK IT",
            "🟡 CASINO RISK IT",
            "| CASINO RISK IT",
            "🪙 CASINO RISK IT",
            "| CASINO RISK IT",
            "🎲 CASINO RISK IT",   // lands on dice — result revealed
        };
        Color[] colours = {
            new Color(255, 215, 50), new Color(200, 170, 20),
            new Color(255, 240, 100), new Color(200, 170, 20),
            new Color(255, 215, 50), new Color(200, 170, 20),
            new Color(255, 240, 100), new Color(200, 170, 20),
            new Color(255, 215, 50), new Color(200, 170, 20),
            new Color(255, 80, 40),
        };
        int[] step = {0};
        javax.swing.Timer coinTimer = new javax.swing.Timer(90, null);
        coinTimer.addActionListener(e -> {
            if (step[0] < frames.length) {
                btn.setText(frames[step[0]]);
                btn.setForeground(colours[step[0]]);
                // Vertical squish: shrink font height to simulate coin spinning perspective
                int fontSize = (frames[step[0]].startsWith("|")) ? 8 : 10;
                btn.setFont(new Font("Courier New", Font.BOLD, fontSize));
                step[0]++;
            } else {
                // Restore button
                btn.setText("🪙 CASINO RISK IT");
                btn.setForeground(new Color(255, 215, 50));
                btn.setFont(new Font("Courier New", Font.BOLD, 10));
                btn.setEnabled(true);
                coinTimer.stop();
                onComplete.run();
            }
        });
        coinTimer.start();
    }


    private static void animateDogBounce() {
        if (growAnimTimer != null && growAnimTimer.isRunning()) return;
        // Cancel any in-progress bounce and snap back to true resting position
        if (bounceAnimTimer != null && bounceAnimTimer.isRunning()) {
            bounceAnimTimer.stop();
        }
        dogLabel.setLocation(DOG_ORIGIN_X, DOG_ORIGIN_Y);
        int[] offsets = {4, -4, 3, -3, 2, -2, 1, 0};
        int[] idx = {0};
        bounceAnimTimer = new javax.swing.Timer(40, null);
        bounceAnimTimer.addActionListener(e -> {
            if (idx[0] < offsets.length) {
                dogLabel.setLocation(DOG_ORIGIN_X + offsets[idx[0]], DOG_ORIGIN_Y);
                idx[0]++;
            } else {
                dogLabel.setLocation(DOG_ORIGIN_X, DOG_ORIGIN_Y);
                bounceAnimTimer.stop();
            }
        });
        bounceAnimTimer.start();
    }

    /** Big pulsing grow animation when the wiener stages up */
    private static void animateDogGrow() {
        if (bounceAnimTimer != null && bounceAnimTimer.isRunning()) {
            bounceAnimTimer.stop();
        }
        dogLabel.setLocation(DOG_ORIGIN_X, DOG_ORIGIN_Y);
        if (growAnimTimer != null && growAnimTimer.isRunning()) growAnimTimer.stop();
        growAnimStep = 0;
        int[] offsets = {5, -8, 14, 8, 18, 12, 16, 14, 12, 10, 6, 0};
        Color[] colours = {
            new Color(255, 215, 0), new Color(255, 140, 0), new Color(255, 80, 40),
            new Color(255, 215, 0), new Color(255, 140, 0), new Color(255, 80, 40),
            new Color(255, 215, 0), new Color(255, 140, 0), new Color(255, 80, 40),
            new Color(255, 215, 0), new Color(255, 80, 40), new Color(255, 200, 0)
        };
        growAnimTimer = new javax.swing.Timer(80, null);
        growAnimTimer.addActionListener(e -> {
            if (growAnimStep < offsets.length) {
                dogLabel.setLocation(DOG_ORIGIN_X, DOG_ORIGIN_Y + offsets[growAnimStep]);
                stageLabel.setForeground(colours[growAnimStep]);
                dogPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colours[growAnimStep], 5),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
                growAnimStep++;
            } else {
                dogLabel.setLocation(DOG_ORIGIN_X, DOG_ORIGIN_Y);
                stageLabel.setForeground(new Color(255, 200, 0));
                growAnimTimer.stop();
            }
        });
        growAnimTimer.start();
    }

    // === COMBO LABEL UPDATE ===
    private static void updateComboLabel() {
        if (comboLabel == null) return;
        int c = Math.max(1, comboCount);
        String bar = "▓".repeat(Math.min(c, 20)) + "░".repeat(Math.max(0, 20 - c));
        comboLabel.setText("COMBO x" + c + "  [" + bar + "]");
        // Color ramp: white → yellow → orange → red → magenta at max
        Color col;
        if      (c >= 20) col = new Color(255,  50, 255);
        else if (c >= 15) col = new Color(255,  50,  50);
        else if (c >= 10) col = new Color(255, 120,   0);
        else if (c >=  5) col = new Color(255, 215,   0);
        else              col = new Color(180, 180, 180);
        comboLabel.setForeground(col);
        comboLabel.setBorder(BorderFactory.createLineBorder(col, c >= 5 ? 2 : 1));
    }

    // === SCREEN SHAKE — shakes only the dog label, returns to center ===
    private static void shakeFrame(int comboLevel) {
        if (dogLabel == null) return;
        int magnitude = Math.min(comboLevel / 3, 8);
        int[] shakeX = new int[]{magnitude, -magnitude, magnitude/2, -magnitude/2, 0};
        int[] shakeY = new int[]{2, -2, 1, -1, 0};
        int[] idx = {0};
        javax.swing.Timer shaker = new javax.swing.Timer(40, null);
        shaker.addActionListener(e -> {
            if (idx[0] < shakeX.length) {
                shakeOffsetX = shakeX[idx[0]];
                shakeOffsetY = shakeY[idx[0]];
                dogLabel.repaint();
                idx[0]++;
            } else {
                shakeOffsetX = 0;
                shakeOffsetY = 0;
                dogLabel.repaint();
                shaker.stop();
            }
        });
        shaker.start();
    }

    // === COOKIE CLICKER STYLE PARTICLES ===

    /**
     * Spawns a floating "+N 🍖" label that rises and fades — combo-aware size & colour.
     */
    private static void spawnTreatParticle(int x, int y, String text, int combo) {
        if (particleLayer == null) return;

        int spawnX = x + random.nextInt(60) - 30;
        int spawnY = y + random.nextInt(20) - 10;

        // Scale font with combo
        int fontSize = Math.min(12 + combo * 2, 36);

        // Color ramp
        Color col;
        if      (combo >= 20) col = new Color(255,  50, 255);
        else if (combo >= 15) col = new Color(255,  50,  50);
        else if (combo >= 10) col = new Color(255, 120,   0);
        else if (combo >=  5) col = new Color(255, 215,   0);
        else                  col = new Color(255, 230,   0);

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Courier New", Font.BOLD, fontSize));
        lbl.setForeground(col);
        lbl.setSize(lbl.getPreferredSize());
        lbl.setLocation(spawnX - lbl.getWidth() / 2, spawnY);
        particleLayer.add(lbl);
        particleLayer.repaint();

        // Rise faster at higher combos
        int riseSpeed = 2 + Math.min(combo / 3, 5);
        int totalFrames = 35;
        int[] frame = {0};
        int[] currentY = {spawnY};
        javax.swing.Timer t = new javax.swing.Timer(20, null);
        t.addActionListener(e -> {
            frame[0]++;
            currentY[0] -= riseSpeed;
            int alpha = Math.max(0, 255 - (int)(255.0 * frame[0] / totalFrames));
            lbl.setForeground(new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha));
            lbl.setLocation(spawnX - lbl.getWidth() / 2, currentY[0]);
            particleLayer.repaint();

            if (frame[0] >= totalFrames) {
                t.stop();
                particleLayer.remove(lbl);
                particleLayer.repaint();
            }
        });
        t.start();
    }

    // Legacy overload (used by other callers with no combo info)
    private static void spawnTreatParticle(int x, int y, String text) {
        spawnTreatParticle(x, y, text, 1);
    }

    /**
     * Shoots 6 gold coin emojis outward in a burst — one per direction — for the casino button.
     * Each coin arcs outward and fades, like a jackpot spray.
     */
    private static void spawnCoinBurst(int cx, int cy) {
        if (particleLayer == null) return;

        // 8 directions: angles in degrees
        double[] angles = {0, 45, 90, 135, 180, 225, 270, 315};
        String[] coinEmojis = {"🪙", "💰", "🪙", "🟡", "🪙", "💰", "🪙", "🟡"};

        for (int i = 0; i < angles.length; i++) {
            double angle = Math.toRadians(angles[i]);
            double dx = Math.cos(angle) * 3.5;
            double dy = Math.sin(angle) * 3.5;
            String emoji = coinEmojis[i];

            JLabel coin = new JLabel(emoji);
            coin.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            coin.setSize(coin.getPreferredSize());
            coin.setLocation(cx - coin.getWidth() / 2, cy - coin.getHeight() / 2);
            particleLayer.add(coin);

            int[] frame = {0};
            int totalFrames = 28;
            double[] px = {cx - coin.getWidth() / 2.0};
            double[] py = {cy - coin.getHeight() / 2.0};

            javax.swing.Timer t = new javax.swing.Timer(16, null); // ~60fps
            t.addActionListener(e -> {
                frame[0]++;
                px[0] += dx;
                py[0] += dy + frame[0] * 0.15; // slight gravity arc
                int alpha = Math.max(0, 255 - (int)(255.0 * frame[0] / totalFrames));
                // Use a coloured foreground fade; emoji alpha not fully supported but label fades via opacity trick
                coin.setForeground(new Color(255, 215, 0, alpha));
                coin.setLocation((int) px[0], (int) py[0]);
                particleLayer.repaint();

                if (frame[0] >= totalFrames) {
                    t.stop();
                    particleLayer.remove(coin);
                    particleLayer.repaint();
                }
            });
            t.start();
        }
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
