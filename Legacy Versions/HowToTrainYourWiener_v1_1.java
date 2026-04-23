import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.Random;

public class HowToTrainYourWiener {
    // Game variables
    private static int treats = 500;
    private static int strength = 1;
    private static int speed = 1;
    private static int obedience = 1;
    private static int wienersFound = 0;
    private static int clickPower = 1;
    private static int treatsPerSecond = 0;
    private static int dogHappiness = 50;
    private static int dogMessiness = 0;
    
    // Shop upgrades
    private static boolean hasClickMultiplier = false;
    private static boolean hasAutoCollector = false;
    private static boolean hasGoldenLeash = false;
    private static int prestigeLevel = 0;
    
    // Gambling stats
    private static int gamblesWon = 0;
    private static int gamblesLost = 0;
    private static int currentStreak = 0;
    
    // UI Components
    private static JLabel treatsLabel;
    private static JLabel statsLabel;
    private static JLabel dogLabel;
    private static JLabel happinessLabel;
    private static JTextArea eventLog;
    private static JProgressBar trainingBar;
    private static Random random = new Random();
    private static String currentStage = "PUPPY";
    
    public static void main(String[] args) {
        // Set dark arcade look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Custom dark colors
            UIManager.put("Panel.background", new Color(20, 20, 25));
            UIManager.put("Button.background", new Color(40, 40, 48));
            UIManager.put("Button.foreground", new Color(255, 80, 40));
            UIManager.put("Label.foreground", new Color(220, 220, 220));
            UIManager.put("Button.font", new Font("Courier New", Font.BOLD, 12));
            UIManager.put("Label.font", new Font("Courier New", Font.PLAIN, 12));
            UIManager.put("TitledBorder.titleColor", new Color(255, 80, 40));
            
        } catch (Exception e) {}
        
        JFrame frame = new JFrame("> HOW TO TRAIN YOUR WIENER.exe");
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        // Main panel with dark background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(20, 20, 25));
        
        // ASCII style title
        JLabel titleLabel = new JLabel("══════════════ HOW TO TRAIN YOUR WIENER ══════════════", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 80, 40));
        titleLabel.setBounds(50, 15, 800, 30);
        mainPanel.add(titleLabel);
        
        JLabel subLabel = new JLabel(">> DOG TRAINING SIMULATOR v2.0 <<", SwingConstants.CENTER);
        subLabel.setFont(new Font("Courier New", Font.ITALIC, 11));
        subLabel.setForeground(new Color(150, 150, 150));
        subLabel.setBounds(300, 45, 300, 20);
        mainPanel.add(subLabel);
        
        // DOG DISPLAY PANEL (CHUNKY BORDER)
        JPanel dogPanel = new JPanel();
        dogPanel.setBounds(300, 80, 300, 200);
        dogPanel.setBackground(new Color(15, 15, 20));
        dogPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 80, 40), 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        dogPanel.setLayout(new BorderLayout());
        
        dogLabel = new JLabel("🐕", SwingConstants.CENTER);
        dogLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        dogPanel.add(dogLabel, BorderLayout.CENTER);
        
        JLabel stageLabel = new JLabel("[ STAGE: PUPPY ]", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        stageLabel.setForeground(new Color(255, 200, 0));
        dogPanel.add(stageLabel, BorderLayout.SOUTH);
        
        mainPanel.add(dogPanel);
        
        // STATS PANEL (LEFT SIDE)
        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(20, 80, 250, 220);
        statsPanel.setBackground(new Color(15, 15, 20));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 90), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statsPanel.setLayout(new GridLayout(7, 1, 5, 8));
        
        statsLabel = new JLabel("> STRENGTH: " + strength);
        statsLabel.setForeground(new Color(100, 255, 100));
        JLabel speedLabel = new JLabel("> SPEED: " + speed);
        speedLabel.setForeground(new Color(100, 200, 255));
        JLabel obeLabel = new JLabel("> OBEDIENCE: " + obedience);
        obeLabel.setForeground(new Color(255, 200, 100));
        happinessLabel = new JLabel("> HAPPINESS: " + dogHappiness + "%");
        JLabel messLabel = new JLabel("> MESS: " + dogMessiness);
        messLabel.setForeground(new Color(255, 100, 100));
        JLabel prestigeLabel = new JLabel("> PRESTIGE: " + prestigeLevel);
        prestigeLabel.setForeground(new Color(255, 80, 200));
        JLabel wienerLabel = new JLabel("> WIENERS: " + wienersFound);
        wienerLabel.setForeground(new Color(255, 150, 50));
        
        statsPanel.add(statsLabel);
        statsPanel.add(speedLabel);
        statsPanel.add(obeLabel);
        statsPanel.add(happinessLabel);
        statsPanel.add(messLabel);
        statsPanel.add(prestigeLabel);
        statsPanel.add(wienerLabel);
        mainPanel.add(statsPanel);
        
        // TRAINING PROGRESS BAR
        trainingBar = new JProgressBar(0, 100);
        trainingBar.setBounds(300, 290, 300, 20);
        trainingBar.setBackground(Color.BLACK);
        trainingBar.setForeground(new Color(255, 80, 40));
        trainingBar.setStringPainted(true);
        trainingBar.setFont(new Font("Courier New", Font.BOLD, 10));
        mainPanel.add(trainingBar);
        
        // EVENT LOG (TERMINAL STYLE)
        eventLog = new JTextArea(8, 40);
        eventLog.setEditable(false);
        eventLog.setBackground(Color.BLACK);
        eventLog.setForeground(new Color(0, 255, 0));
        eventLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        eventLog.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 40), 1));
        JScrollPane scrollPane = new JScrollPane(eventLog);
        scrollPane.setBounds(20, 320, 850, 150);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 80, 40)), 
            ">> SYSTEM LOG <<",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Courier New", Font.BOLD, 11),
            new Color(255, 80, 40)
        ));
        mainPanel.add(scrollPane);
        
        // BUTTON PANEL (CHUNKY BUTTONS)
        // Row 1
        JButton petBtn = createArcadeButton("🐕 [PET WIENER] +" + clickPower, new Color(255, 80, 40));
        petBtn.setBounds(20, 490, 200, 45);
        petBtn.addActionListener(e -> {
            int gain = clickPower * (hasClickMultiplier ? 2 : 1);
            treats += gain;
            dogHappiness = Math.min(100, dogHappiness + 2);
            addLog("> PET ACTION: +" + gain + " treats [HAPPINESS +2]");
            updateGame();
        });
        
        JButton strengthBtn = createArcadeButton("💪 FORCE PUSH-UPS [30]", new Color(100, 200, 100));
        strengthBtn.setBounds(230, 490, 200, 45);
        strengthBtn.addActionListener(e -> {
            if (treats >= 30) {
                treats -= 30;
                strength++;
                clickPower++;
                dogMessiness += 5;
                addLog("> TRAINING: Strength increased to " + strength + "! +1 click power [MESS +5]");
                updateGame();
            } else { addLog("! INSUFFICIENT TREATS !"); }
        });
        
        JButton speedBtn = createArcadeButton("⚡ ZOOMIES TRAINING [25]", new Color(100, 200, 255));
        speedBtn.setBounds(440, 490, 200, 45);
        speedBtn.addActionListener(e -> {
            if (treats >= 25) {
                treats -= 25;
                speed++;
                treatsPerSecond += 2;
                addLog("> TRAINING: Speed increased to " + speed + "! +2 treats/sec");
                updateGame();
            } else { addLog("! INSUFFICIENT TREATS !"); }
        });
        
        JButton wienerBtn = createArcadeButton("🌭 HUNT FOR WIENER [15]", new Color(255, 150, 50));
        wienerBtn.setBounds(650, 490, 200, 45);
        wienerBtn.addActionListener(e -> {
            if (treats >= 15) {
                treats -= 15;
                wienersFound++;
                int bonus = random.nextInt(30) + 15;
                treats += bonus;
                addLog("> DISCOVERY: Mythical wiener found! +" + bonus + " treats [TOTAL: " + wienersFound + "]");
                updateGame();
            } else { addLog("! INSUFFICIENT TREATS !"); }
        });
        
        // Row 2
        JButton obedienceBtn = createArcadeButton("🎓 OBEDIENCE SCHOOL [40]", new Color(255, 200, 100));
        obedienceBtn.setBounds(20, 545, 200, 45);
        obedienceBtn.addActionListener(e -> {
            if (treats >= 40) {
                treats -= 40;
                obedience++;
                dogHappiness = Math.min(100, dogHappiness + 10);
                addLog("> SCHOOL: Obedience increased to " + obedience + "! +10 happiness");
                updateGame();
            } else { addLog("! INSUFFICIENT TREATS !"); }
        });
        
        JButton shopBtn = createArcadeButton("🛒 [SHOP] UPGRADES", new Color(150, 100, 255));
        shopBtn.setBounds(230, 545, 200, 45);
        shopBtn.addActionListener(e -> openShop());
        
        JButton gambleBtn = createArcadeButton("🎲 [CASINO] RISK IT", new Color(255, 215, 0));
        gambleBtn.setBounds(440, 545, 200, 45);
        gambleBtn.addActionListener(e -> openGambleWindow());
        
        JButton prestigeBtn = createArcadeButton("🌟 PRESTIGE [RESET]", new Color(255, 80, 200));
        prestigeBtn.setBounds(650, 545, 200, 45);
        prestigeBtn.addActionListener(e -> prestigeReset());
        
        // Clean up button
        JButton cleanBtn = createArcadeButton("🧹 CLEAN MESS [5]", new Color(100, 200, 200));
        cleanBtn.setBounds(20, 600, 200, 35);
        cleanBtn.addActionListener(e -> {
            if (dogMessiness > 0 && treats >= 5) {
                treats -= 5;
                dogMessiness = Math.max(0, dogMessiness - 15);
                addLog("> CLEANING: Mess reduced to " + dogMessiness);
                updateGame();
            }
        });
        
        mainPanel.add(petBtn);
        mainPanel.add(strengthBtn);
        mainPanel.add(speedBtn);
        mainPanel.add(wienerBtn);
        mainPanel.add(obedienceBtn);
        mainPanel.add(shopBtn);
        mainPanel.add(gambleBtn);
        mainPanel.add(prestigeBtn);
        mainPanel.add(cleanBtn);
        
        // TREATS DISPLAY (BOTTOM BAR)
        treatsLabel = new JLabel();
        treatsLabel.setBounds(20, 645, 860, 25);
        treatsLabel.setFont(new Font("Courier New", Font.BOLD, 14));
        treatsLabel.setForeground(new Color(255, 215, 0));
        treatsLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 40), 1));
        treatsLabel.setOpaque(true);
        treatsLabel.setBackground(new Color(10, 10, 15));
        treatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(treatsLabel);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        startIdle();
        updateGame();
        addLog("> SYSTEM: Wiener Training Simulator v2.0 ONLINE");
        addLog("> TIP: Visit the SHOP for permanent upgrades");
        addLog("> WARNING: Gambling may cause treat addiction");
    }
    
    private static JButton createArcadeButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Courier New", Font.BOLD, 11));
        btn.setBackground(new Color(30, 30, 35));
        btn.setForeground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return btn;
    }
    
    private static void openShop() {
        JDialog shopDialog = new JDialog();
        shopDialog.setTitle("> WIENER UPGRADE SHOP <");
        shopDialog.setSize(450, 500);
        shopDialog.setModal(true);
        shopDialog.setBackground(new Color(20, 20, 25));
        shopDialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 20, 25));
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("══ PERMANENT UPGRADES ══", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 14));
        title.setForeground(new Color(255, 80, 40));
        panel.add(title);
        
        // Shop items
        JButton multiBtn = createShopButton("🔁 CLICK MULTIPLIER", "2x click rewards", 500, !hasClickMultiplier);
        if (!hasClickMultiplier) {
            multiBtn.addActionListener(e -> {
                if (treats >= 500) {
                    treats -= 500;
                    hasClickMultiplier = true;
                    addLog("> SHOP: Purchased CLICK MULTIPLIER!");
                    updateGame();
                    shopDialog.dispose();
                    openShop();
                }
            });
        }
        
        JButton autoBtn = createShopButton("🤖 AUTO-PETTER", "Pets every 10 sec", 1000, !hasAutoCollector);
        if (!hasAutoCollector) {
            autoBtn.addActionListener(e -> {
                if (treats >= 1000) {
                    treats -= 1000;
                    hasAutoCollector = true;
                    startAutoCollector();
                    addLog("> SHOP: Purchased AUTO-PETTER!");
                    updateGame();
                    shopDialog.dispose();
                    openShop();
                }
            });
        }
        
        JButton goldenBtn = createShopButton("✨ GOLDEN LEASH", "+50% all income", 2000, !hasGoldenLeash);
        if (!hasGoldenLeash) {
            goldenBtn.addActionListener(e -> {
                if (treats >= 2000) {
                    treats -= 2000;
                    hasGoldenLeash = true;
                    treatsPerSecond *= 1.5;
                    clickPower *= 1.5;
                    addLog("> SHOP: Purchased GOLDEN LEASH! All stats +50%");
                    updateGame();
                    shopDialog.dispose();
                    openShop();
                }
            });
        }
        
        JButton happinessBtn = createShopButton("😊 HAPPINESS POTION", "+50 happiness", 100, true);
        happinessBtn.addActionListener(e -> {
            if (treats >= 100) {
                treats -= 100;
                dogHappiness = Math.min(100, dogHappiness + 50);
                addLog("> SHOP: Used HAPPINESS POTION!");
                updateGame();
                shopDialog.dispose();
                openShop();
            }
        });
        
        JButton treatsBtn = createShopButton("🍖 TREAT PACK", "200 → 500 treats", 200, true);
        treatsBtn.addActionListener(e -> {
            if (treats >= 200) {
                treats -= 200;
                treats += 500;
                addLog("> SHOP: Bought TREAT PACK! +300 net treats");
                updateGame();
                shopDialog.dispose();
                openShop();
            }
        });
        
        panel.add(multiBtn);
        panel.add(autoBtn);
        panel.add(goldenBtn);
        panel.add(happinessBtn);
        panel.add(treatsBtn);
        
        JLabel balance = new JLabel("💰 CURRENT TREATS: " + treats, SwingConstants.CENTER);
        balance.setFont(new Font("Courier New", Font.BOLD, 12));
        balance.setForeground(Color.YELLOW);
        panel.add(balance);
        
        shopDialog.add(panel);
        shopDialog.setVisible(true);
    }
    
    private static JButton createShopButton(String name, String desc, int cost, boolean enabled) {
        JButton btn = new JButton(String.format("%s [%d] - %s", name, cost, desc));
        btn.setFont(new Font("Courier New", Font.PLAIN, 11));
        btn.setEnabled(enabled);
        if (!enabled) {
            btn.setText("✓ " + name + " - OWNED");
            btn.setForeground(Color.GREEN);
        }
        return btn;
    }
    
    private static void openGambleWindow() {
        JDialog gambleDialog = new JDialog();
        gambleDialog.setTitle("> UNDERGROUND DOG FIGHT CLUB <");
        gambleDialog.setSize(500, 450);
        gambleDialog.setModal(true);
        gambleDialog.setBackground(new Color(20, 20, 25));
        gambleDialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 20, 25));
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel warning = new JLabel("⚠️ 50/50 CHANCE - YOU MIGHT LOSE EVERYTHING ⚠️", SwingConstants.CENTER);
        warning.setForeground(Color.RED);
        warning.setFont(new Font("Courier New", Font.BOLD, 12));
        panel.add(warning);
        
        JTextField betField = new JTextField("100");
        betField.setHorizontalAlignment(JTextField.CENTER);
        betField.setBackground(Color.BLACK);
        betField.setForeground(Color.GREEN);
        betField.setFont(new Font("Courier New", Font.BOLD, 14));
        
        panel.add(new JLabel("BET AMOUNT:"));
        panel.add(betField);
        
        JButton coinFlip = createArcadeButton("🪙 COIN FLIP [2x WIN]", new Color(255, 200, 100));
        coinFlip.addActionListener(e -> {
            int bet = Integer.parseInt(betField.getText());
            if (bet > 0 && bet <= treats) {
                boolean win = random.nextBoolean();
                if (win) {
                    treats += bet;
                    gamblesWon++;
                    addLog("🎲 CASINO: COIN FLIP WIN! +" + bet + " treats!");
                } else {
                    treats -= bet;
                    gamblesLost++;
                    addLog("💀 CASINO: COIN FLIP LOSS! -" + bet + " treats!");
                }
                updateGame();
                gambleDialog.dispose();
            }
        });
        
        JButton doubleOrNothing = createArcadeButton("⚠️ DOUBLE OR NOTHING [2x or 0x]", new Color(255, 100, 100));
        doubleOrNothing.addActionListener(e -> {
            int bet = Integer.parseInt(betField.getText());
            if (bet > 0 && bet <= treats) {
                boolean win = random.nextBoolean();
                if (win) {
                    treats += bet;
                    gamblesWon++;
                    addLog("🎲 CASINO: DOUBLE OR NOTHING WIN! +" + bet + " treats!");
                } else {
                    treats -= bet;
                    gamblesLost++;
                    addLog("💀 CASINO: DOUBLE OR NOTHING LOSS! -" + bet + " treats!");
                }
                updateGame();
                gambleDialog.dispose();
            }
        });
        
        panel.add(coinFlip);
        panel.add(doubleOrNothing);
        
        JLabel stats = new JLabel(String.format("STATS: Wins: %d | Losses: %d", gamblesWon, gamblesLost));
        stats.setForeground(Color.CYAN);
        panel.add(stats);
        
        gambleDialog.add(panel);
        gambleDialog.setVisible(true);
    }
    
    private static void startAutoCollector() {
        if (hasAutoCollector) {
            new Timer(10000, e -> {
                int gain = clickPower * 5;
                treats += gain;
                addLog("🤖 AUTO-PETTER: +" + gain + " treats!");
                updateGame();
            }).start();
        }
    }
    
    private static void prestigeReset() {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "⚠️ PRESTIGE WILL RESET ALL PROGRESS ⚠️\n" +
            "Permanent bonus: +50% to all stats\n" +
            "Current prestige level: " + prestigeLevel + "\n\n" +
            "PROCEED?",
            "PRESTIGE", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            prestigeLevel++;
            treats = 100;
            strength = 1;
            speed = 1;
            obedience = 1;
            clickPower = (int)(1 * (1 + prestigeLevel * 0.5));
            treatsPerSecond = 0;
            dogHappiness = 50;
            dogMessiness = 0;
            hasClickMultiplier = false;
            hasAutoCollector = false;
            hasGoldenLeash = false;
            addLog("🌟 PRESTIGE LEVEL " + prestigeLevel + "! All stats +" + (prestigeLevel * 50) + "% 🌟");
            updateGame();
        }
    }
    
    private static void startIdle() {
        new Timer(1000, e -> {
            treats += treatsPerSecond;
            if (dogHappiness > 70) treats += 2;
            
            if (random.nextInt(100) < 8) {
                String[] events = {
                    "🐿️ Wiener chased a squirrel! +10 treats",
                    "💨 Wiener made a mess! MESS +5",
                    "😢 Wiener saw a vacuum! HAPPINESS -5",
                    "🎁 Found a mysterious wiener! +20 treats"
                };
                int idx = random.nextInt(4);
                addLog("> RANDOM EVENT: " + events[idx]);
                if (idx == 1) dogMessiness += 5;
                if (idx == 2) dogHappiness = Math.max(0, dogHappiness - 5);
                if (idx == 0 || idx == 3) treats += (idx == 0 ? 10 : 20);
            }
            updateGame();
        }).start();
    }
    
    private static void updateGame() {
        updateStage();
        updateDogDisplay();
        updateLabels();
        updateProgressBar();
    }
    
    private static void updateStage() {
        int total = strength + speed + obedience;
        String oldStage = currentStage;
        
        if (total > 50) currentStage = "LEGENDARY";
        else if (total > 30) currentStage = "CHAMPION";
        else if (total > 15) currentStage = "ADULT";
        else if (total > 5) currentStage = "YOUNG";
        else currentStage = "PUPPY";
        
        if (!oldStage.equals(currentStage)) {
            int bonus = 50 + total * 5;
            treats += bonus;
            addLog("═══ STAGE UP! " + oldStage + " → " + currentStage + "! +" + bonus + " treats ═══");
        }
    }
    
    private static void updateDogDisplay() {
        String[] emojis = {"🐶", "🐕", "🐕‍🦺", "🏆🐕", "⭐🐕⭐"};
        String[] stages = {"PUPPY", "YOUNG", "ADULT", "CHAMPION", "LEGENDARY"};
        for (int i = 0; i < stages.length; i++) {
            if (currentStage.equals(stages[i])) {
                dogLabel.setText(emojis[i]);
                break;
            }
        }
        
        Color borderColor = dogHappiness > 70 ? new Color(100, 255, 100) : 
                           (dogHappiness > 30 ? new Color(255, 200, 0) : new Color(255, 50, 50));
        ((JPanel)dogLabel.getParent()).setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel stageLabel = (JLabel)((JPanel)dogLabel.getParent()).getComponent(1);
        stageLabel.setText("[ STAGE: " + currentStage + " ]");
    }
    
    private static void updateProgressBar() {
        int progress = strength + speed + obedience;
        trainingBar.setValue(Math.min(progress, 100));
        trainingBar.setString(String.format("TRAINING PROGRESS: %d/100", Math.min(progress, 100)));
        if (progress >= 100) trainingBar.setForeground(new Color(255, 215, 0));
    }
    
    private static void updateLabels() {
        treatsLabel.setText(String.format("🌭 TREATS: %,d  |  +%d/sec  |  WIENERS: %d  |  CLICK: %d  |  MESS: %d",
                           treats, treatsPerSecond, wienersFound, clickPower, dogMessiness));
        statsLabel.setText("> STRENGTH: " + strength);
        happinessLabel.setText("> HAPPINESS: " + dogHappiness + "%");
    }
    
    private static void addLog(String message) {
        eventLog.append(message + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
        if (eventLog.getLineCount() > 30) {
            try {
                eventLog.replaceRange("", 0, eventLog.getLineEndOffset(10));
            } catch(Exception e) {}
        }
    }
}