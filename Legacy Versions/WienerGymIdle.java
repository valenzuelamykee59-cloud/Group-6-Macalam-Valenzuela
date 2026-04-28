import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Random;

/**
 * Wiener Gym Idle
 *
 * A single-file Swing idle game inspired by the layout and feature mix of
 * "How to Train Your Cock", adapted into a dachshund strength fantasy.
 */
public class WienerGymIdle extends JFrame {

    private static final int WINDOW_WIDTH = 1380;
    private static final int WINDOW_HEIGHT = 860;

    private static final Color APP_BG = new Color(22, 31, 48);
    private static final Color PANEL_BG = new Color(248, 242, 231);
    private static final Color PANEL_BG_ALT = new Color(255, 249, 241);
    private static final Color TEXT_DARK = new Color(45, 41, 36);
    private static final Color TEXT_SOFT = new Color(110, 101, 90);
    private static final Color GAIN_COLOR = new Color(245, 147, 55);
    private static final Color EGG_COLOR = new Color(246, 207, 92);
    private static final Color SIZE_COLOR = new Color(91, 188, 255);
    private static final Color RANK_COLOR = new Color(230, 120, 154);
    private static final Color CARDIO_COLOR = new Color(76, 185, 119);

    private static final DecimalFormat ONE_DECIMAL = new DecimalFormat("0.0");
    private static final String[] RANK_NAMES = {
        "Beach Pup",
        "Sand Strider",
        "Rack Hound",
        "Pier Bruiser",
        "Plate Pirate",
        "Island Beast",
        "Tide Titan",
        "Sunset Legend"
    };
    private static final int[] RANK_THRESHOLDS = {
        110, 220, 360, 520, 760, 1050, 1420, 1850
    };

    private enum LiftType {
        SQUAT("Squat", "Bar on upper back, descend to depth, then drive up to lockout.", new Color(96, 178, 255)),
        BENCH("Bench", "Lower the bar to the chest, pause, then press to straight arms.", new Color(255, 145, 99)),
        DEADLIFT("Deadlift", "Pull from the floor to a tall lockout, then lower under control.", new Color(236, 104, 199));

        final String label;
        final String hint;
        final Color accent;

        LiftType(String label, String hint, Color accent) {
            this.label = label;
            this.hint = hint;
            this.accent = accent;
        }
    }

    private enum UpgradeType {
        POWER("Power", "More gains and heavier plates."),
        ENERGY("Endurance", "More energy for longer sets."),
        RECOVERY("Recovery", "Faster refill between reps."),
        SPEED("Tempo", "Faster training pace and more gains.");

        final String label;
        final String desc;

        UpgradeType(String label, String desc) {
            this.label = label;
            this.desc = desc;
        }
    }

    private enum HatStyle {
        NONE("None", 0),
        HEADBAND("Red Headband", 3),
        CAP("Lifeguard Cap", 5),
        CROWN("Beach Crown", 8);

        final String label;
        final int eggCost;

        HatStyle(String label, int eggCost) {
            this.label = label;
            this.eggCost = eggCost;
        }
    }

    private enum IslandStyle {
        SUNSET("Power Gym", 0),
        GYM_PIER("Beach Pier", 6),
        NEON_FEST("Neon Festival", 10);

        final String label;
        final int eggCost;

        IslandStyle(String label, int eggCost) {
            this.label = label;
            this.eggCost = eggCost;
        }
    }

    private enum BuddyType {
        NONE("No Buddy", 0, "Pure solo lifting.", 0.00, 1.00, 1.00),
        CRAB("Spotter Crab", 4, "+0.35 recovery to every lift.", 0.35, 1.00, 1.00),
        GULL("Seagull Coach", 7, "+12% gains from every rep.", 0.00, 1.12, 1.00),
        OTTER("Otter Cameraman", 9, "+15% size growth and better photo ops.", 0.00, 1.00, 1.15);

        final String label;
        final int eggCost;
        final String desc;
        final double recoveryBonus;
        final double gainsMultiplier;
        final double sizeMultiplier;

        BuddyType(String label, int eggCost, String desc, double recoveryBonus, double gainsMultiplier, double sizeMultiplier) {
            this.label = label;
            this.eggCost = eggCost;
            this.desc = desc;
            this.recoveryBonus = recoveryBonus;
            this.gainsMultiplier = gainsMultiplier;
            this.sizeMultiplier = sizeMultiplier;
        }
    }

    private enum MeetTier {
        LOCAL("Local Meet", 2.2, 2.8, 8),
        CITY("City Open", 4.5, 4.0, 8),
        REGIONAL("Regional Showdown", 8.0, 5.6, 9),
        NATIONAL("National Championships", 13.0, 7.4, 9),
        CONTINENTAL("Continental Cup", 18.0, 9.4, 10),
        IPF("IPF Worlds", 24.0, 12.0, 10);

        final String label;
        final double minStrength;
        final double clickMultiplier;
        final int durationSeconds;

        MeetTier(String label, double minStrength, double clickMultiplier, int durationSeconds) {
            this.label = label;
            this.minStrength = minStrength;
            this.clickMultiplier = clickMultiplier;
            this.durationSeconds = durationSeconds;
        }
    }

    private final EnumMap<LiftType, Integer> liftPower = new EnumMap<LiftType, Integer>(LiftType.class);
    private final EnumMap<LiftType, Double> liftEnergy = new EnumMap<LiftType, Double>(LiftType.class);
    private final EnumMap<LiftType, Double> maxLiftEnergy = new EnumMap<LiftType, Double>(LiftType.class);
    private final EnumMap<LiftType, Double> recoveryRate = new EnumMap<LiftType, Double>(LiftType.class);
    private final EnumMap<LiftType, Double> liftSpeed = new EnumMap<LiftType, Double>(LiftType.class);
    private final EnumMap<LiftType, Long> totalReps = new EnumMap<LiftType, Long>(LiftType.class);

    private final EnumMap<LiftType, Integer> powerLevel = new EnumMap<LiftType, Integer>(LiftType.class);
    private final EnumMap<LiftType, Integer> energyLevel = new EnumMap<LiftType, Integer>(LiftType.class);
    private final EnumMap<LiftType, Integer> recoveryLevel = new EnumMap<LiftType, Integer>(LiftType.class);
    private final EnumMap<LiftType, Integer> speedLevel = new EnumMap<LiftType, Integer>(LiftType.class);

    private final EnumMap<LiftType, JProgressBar> energyBars = new EnumMap<LiftType, JProgressBar>(LiftType.class);
    private final EnumMap<LiftType, JLabel> statsLabels = new EnumMap<LiftType, JLabel>(LiftType.class);
    private final EnumMap<UpgradeType, JButton> upgradeButtons = new EnumMap<UpgradeType, JButton>(UpgradeType.class);
    private final EnumMap<UpgradeType, JLabel> upgradeTextLabels = new EnumMap<UpgradeType, JLabel>(UpgradeType.class);

    private final EnumSet<HatStyle> ownedHats = EnumSet.of(HatStyle.NONE);
    private final EnumSet<IslandStyle> ownedIslands = EnumSet.of(IslandStyle.SUNSET);
    private final EnumSet<BuddyType> ownedBuddies = EnumSet.of(BuddyType.NONE);
    private final Deque<String> eventLog = new ArrayDeque<String>();
    private final Random random = new Random();

    private LiftType selectedLift = LiftType.SQUAT;
    private LiftType animationLift = LiftType.SQUAT;
    private boolean cardioSelected = false;
    private HatStyle equippedHat = HatStyle.NONE;
    private IslandStyle equippedIsland = IslandStyle.SUNSET;
    private BuddyType equippedBuddy = BuddyType.NONE;

    private long totalGains = 0L;
    private long lifetimeGains = 0L;
    private long eggs = 0L;
    private double dogSizeCm = 110.0;
    private int nextEggSizeThreshold = 220;

    private boolean hasProteinShaker = false;
    private boolean hasGymMembership = false;
    private boolean hasOlympicBar = false;
    private boolean hasMassageTable = false;
    private boolean hasChalkBucket = false;
    private boolean hasParasol = false;
    private boolean hasLanterns = false;
    private boolean hasTrophyRack = false;
    private boolean hasPoolFloat = false;
    private boolean highContrastMode = false;
    private boolean largeTextMode = false;
    private boolean reduceMotionMode = false;
    private boolean simplifiedStageMode = false;
    private boolean autoRotateWhenEmpty = true;

    private String dogName = "Wiener";
    private long meetsCompleted = 0L;

    private boolean isAnimating = false;
    private double animationPhase = 0.0;
    private double ambientTime = 0.0;
    private double autoTrainAccumulator = 0.0;
    private double cardioPace = 0.30;
    private double cardioGainAccumulator = 0.0;
    private long cardioClicks = 0L;
    private int cardioEngineLevel = 0;
    private int cardioPaceLevel = 0;
    private int cardioConditioningLevel = 0;
    private int cardioStrideLevel = 0;
    private double uiAccumulator = 0.0;

    private String toastMessage = "";
    private long toastExpiresAt = 0L;
    private String floatingGainText = "";
    private long floatingGainStartedAt = 0L;

    private Timer frameTimer;
    private long lastFrameNanos = 0L;

    private JLabel gainsLabel;
    private JLabel eggsLabel;
    private JLabel sizeLabel;
    private JLabel rankLabel;
    private JLabel selectedLiftLabel;
    private JLabel selectedLiftInfoLabel;
    private JLabel bonusLabel;
    private JLabel cardioLabel;
    private JLabel milestoneLabel;
    private JLabel cardioStatsBoardLabel;
    private JProgressBar cardioPaceBar;
    private JTextArea eventArea;
    private JButton squatButton;
    private JButton benchButton;
    private JButton deadliftButton;
    private JButton cardioButton;
    private JButton trainButton;
    private JButton tradeEggButton;

    private StagePanel stagePanel;

    public WienerGymIdle() {
        initializeGame();
        setupUI();
        updateUI();
        startLoop();
    }

    private void initializeGame() {
        for (LiftType lift : LiftType.values()) {
            liftPower.put(lift, Integer.valueOf(1));
            liftEnergy.put(lift, Double.valueOf(100.0));
            maxLiftEnergy.put(lift, Double.valueOf(100.0));
            recoveryRate.put(lift, Double.valueOf(1.0));
            liftSpeed.put(lift, Double.valueOf(1.0));
            totalReps.put(lift, Long.valueOf(0L));

            powerLevel.put(lift, Integer.valueOf(0));
            energyLevel.put(lift, Integer.valueOf(0));
            recoveryLevel.put(lift, Integer.valueOf(0));
            speedLevel.put(lift, Integer.valueOf(0));
        }

        addEvent(dogName + " rolled into the power gym.");
        addEvent("Each lift has its own energy pool. Rotate lifts to stay moving.");
    }

    private void setupUI() {
        setTitle("Wiener Gym Idle");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        root.add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setOpaque(false);
        center.add(createStatusColumn(), BorderLayout.WEST);
        center.add(createStageColumn(), BorderLayout.CENTER);
        center.add(createUpgradeColumn(), BorderLayout.EAST);

        root.add(center, BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        gainsLabel = createMetricCard(panel, "GAINS", GAIN_COLOR);
        eggsLabel = createMetricCard(panel, "EGGS", EGG_COLOR);
        sizeLabel = createMetricCard(panel, "SIZE", SIZE_COLOR);
        rankLabel = createMetricCard(panel, "RANK", RANK_COLOR);

        return panel;
    }

    private JLabel createMetricCard(JPanel parent, String title, Color accent) {
        JPanel card = createCard(new BorderLayout(0, 8), accent, PANEL_BG_ALT);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_SOFT);
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 13));

        JLabel valueLabel = new JLabel("--");
        valueLabel.setForeground(TEXT_DARK);
        valueLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 26));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        parent.add(card);
        return valueLabel;
    }

    private JPanel createStatusColumn() {
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setPreferredSize(new Dimension(300, 100));

        JPanel overview = createCard(new BorderLayout(0, 12), new Color(100, 86, 71), PANEL_BG);
        JLabel overviewTitle = new JLabel("Lift Board");
        overviewTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        overviewTitle.setForeground(TEXT_DARK);
        overview.add(overviewTitle, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (LiftType lift : LiftType.values()) {
            list.add(createLiftCard(lift));
            list.add(Box.createVerticalStrut(10));
        }
        list.add(createCardioCard());
        overview.add(list, BorderLayout.CENTER);
        column.add(overview);
        column.add(Box.createVerticalStrut(16));

        JPanel events = createCard(new BorderLayout(0, 10), new Color(86, 106, 90), PANEL_BG_ALT);
        JLabel eventTitle = new JLabel("Training Log");
        eventTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        eventTitle.setForeground(TEXT_DARK);
        events.add(eventTitle, BorderLayout.NORTH);

        eventArea = new JTextArea(10, 18);
        eventArea.setEditable(false);
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        eventArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        eventArea.setForeground(TEXT_DARK);
        eventArea.setBackground(new Color(255, 252, 247));
        eventArea.setBorder(new CompoundBorder(new LineBorder(new Color(220, 210, 197), 1, true), new EmptyBorder(8, 8, 8, 8)));

        JScrollPane scroll = new JScrollPane(eventArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        events.add(scroll, BorderLayout.CENTER);

        column.add(events);
        return column;
    }

    private JPanel createLiftCard(final LiftType lift) {
        final JPanel card = createCard(new BorderLayout(0, 8), lift.accent, new Color(255, 251, 245));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel name = new JLabel(lift.label.toUpperCase(Locale.US));
        name.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        name.setForeground(TEXT_DARK);

        JLabel stats = new JLabel();
        stats.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        stats.setForeground(TEXT_SOFT);
        statsLabels.put(lift, stats);

        JProgressBar bar = new JProgressBar();
        bar.setStringPainted(true);
        bar.setForeground(lift.accent);
        bar.setBackground(new Color(223, 218, 210));
        bar.setFont(new Font(Font.DIALOG, Font.BOLD, 11));
        energyBars.put(lift, bar);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(name, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(bar, BorderLayout.SOUTH);

        MouseAdapter selector = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectLift(lift);
            }
        };

        card.addMouseListener(selector);
        name.addMouseListener(selector);
        stats.addMouseListener(selector);
        bar.addMouseListener(selector);

        return card;
    }

    private JPanel createCardioCard() {
        final JPanel card = createCard(new BorderLayout(0, 8), CARDIO_COLOR, new Color(248, 255, 249));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel name = new JLabel("CARDIO");
        name.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        name.setForeground(TEXT_DARK);

        cardioStatsBoardLabel = new JLabel();
        cardioStatsBoardLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        cardioStatsBoardLabel.setForeground(TEXT_SOFT);

        cardioPaceBar = new JProgressBar();
        cardioPaceBar.setStringPainted(true);
        cardioPaceBar.setForeground(CARDIO_COLOR);
        cardioPaceBar.setBackground(new Color(223, 218, 210));
        cardioPaceBar.setFont(new Font(Font.DIALOG, Font.BOLD, 11));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(name, BorderLayout.NORTH);
        top.add(cardioStatsBoardLabel, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(cardioPaceBar, BorderLayout.SOUTH);

        MouseAdapter selector = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCardio();
            }
        };

        card.addMouseListener(selector);
        name.addMouseListener(selector);
        cardioStatsBoardLabel.addMouseListener(selector);
        cardioPaceBar.addMouseListener(selector);

        return card;
    }

    private JPanel createStageColumn() {
        JPanel card = createCard(new BorderLayout(0, 12), new Color(101, 127, 168), PANEL_BG);

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);

        selectedLiftLabel = new JLabel();
        selectedLiftLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
        selectedLiftLabel.setForeground(TEXT_DARK);

        selectedLiftInfoLabel = new JLabel();
        selectedLiftInfoLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        selectedLiftInfoLabel.setForeground(TEXT_SOFT);

        JPanel topText = new JPanel();
        topText.setOpaque(false);
        topText.setLayout(new BoxLayout(topText, BoxLayout.Y_AXIS));
        topText.add(selectedLiftLabel);
        topText.add(Box.createVerticalStrut(4));
        topText.add(selectedLiftInfoLabel);

        top.add(topText, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH);

        stagePanel = new StagePanel();
        stagePanel.setPreferredSize(new Dimension(720, 550));
        stagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        stagePanel.setToolTipText("Click the stage to train the selected lift.");
        stagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                trainSelectedLift(true);
            }
        });
        card.add(stagePanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(3, 1, 0, 6));
        footer.setOpaque(false);
        bonusLabel = new JLabel();
        bonusLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        bonusLabel.setForeground(TEXT_DARK);

        cardioLabel = new JLabel();
        cardioLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        cardioLabel.setForeground(TEXT_SOFT);

        milestoneLabel = new JLabel();
        milestoneLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        milestoneLabel.setForeground(TEXT_SOFT);

        footer.add(bonusLabel);
        footer.add(cardioLabel);
        footer.add(milestoneLabel);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createUpgradeColumn() {
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setPreferredSize(new Dimension(340, 100));

        JPanel trainingCard = createCard(new BorderLayout(0, 12), getSelectedAccent(), PANEL_BG);
        JLabel trainingTitle = new JLabel("Selected Lift");
        trainingTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        trainingTitle.setForeground(TEXT_DARK);
        trainingCard.add(trainingTitle, BorderLayout.NORTH);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        trainButton = createActionButton("Train Selected Lift", selectedLift.accent, new Runnable() {
            @Override
            public void run() {
                trainSelectedLift(true);
            }
        });
        tradeEggButton = createActionButton("Trade 500 Gains for 1 Egg", new Color(184, 144, 54), new Runnable() {
            @Override
            public void run() {
                tradeForEgg();
            }
        });

        actions.add(trainButton);
        actions.add(Box.createVerticalStrut(10));
        actions.add(tradeEggButton);
        actions.add(Box.createVerticalStrut(12));

        for (UpgradeType type : UpgradeType.values()) {
            actions.add(createUpgradeRow(type));
            actions.add(Box.createVerticalStrut(10));
        }

        trainingCard.add(actions, BorderLayout.CENTER);
        column.add(trainingCard);
        column.add(Box.createVerticalStrut(16));

        JPanel featureCard = createCard(new BorderLayout(0, 10), new Color(119, 100, 145), PANEL_BG_ALT);
        JLabel featureTitle = new JLabel("Gym Features");
        featureTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        featureTitle.setForeground(TEXT_DARK);
        featureCard.add(featureTitle, BorderLayout.NORTH);

        JPanel featureButtons = new JPanel();
        featureButtons.setOpaque(false);
        featureButtons.setLayout(new BoxLayout(featureButtons, BoxLayout.Y_AXIS));

        featureButtons.add(createActionButton("Gym Shop", new Color(121, 158, 224), new Runnable() {
            @Override
            public void run() {
                openGymShop();
            }
        }));
        featureButtons.add(Box.createVerticalStrut(8));
        featureButtons.add(createActionButton("Style Studio", new Color(240, 122, 130), new Runnable() {
            @Override
            public void run() {
                openStyleStudio();
            }
        }));
        featureButtons.add(Box.createVerticalStrut(8));
        featureButtons.add(createActionButton("Scenery Shop", new Color(91, 171, 131), new Runnable() {
            @Override
            public void run() {
                openIslandBuilder();
            }
        }));
        featureButtons.add(Box.createVerticalStrut(8));
        featureButtons.add(createActionButton("The Pounder", new Color(179, 105, 208), new Runnable() {
            @Override
            public void run() {
                openPounder();
            }
        }));
        featureButtons.add(Box.createVerticalStrut(8));
        featureButtons.add(createActionButton("Power Meet", new Color(203, 111, 55), new Runnable() {
            @Override
            public void run() {
                openMeetMenu();
            }
        }));
        featureButtons.add(Box.createVerticalStrut(8));
        featureButtons.add(createActionButton("Settings", new Color(106, 121, 141), new Runnable() {
            @Override
            public void run() {
                openSettingsMenu();
            }
        }));
        featureButtons.add(Box.createVerticalStrut(8));
        featureButtons.add(createActionButton("Snap Photo", new Color(86, 120, 180), new Runnable() {
            @Override
            public void run() {
                saveStageSnapshot();
            }
        }));

        featureCard.add(featureButtons, BorderLayout.CENTER);
        column.add(featureCard);
        column.add(Box.createVerticalGlue());

        JPanel liftButtons = createCard(new GridLayout(1, 4, 8, 0), new Color(90, 94, 112), PANEL_BG);
        squatButton = createLiftSelectButton(LiftType.SQUAT);
        benchButton = createLiftSelectButton(LiftType.BENCH);
        deadliftButton = createLiftSelectButton(LiftType.DEADLIFT);
        cardioButton = createCardioSelectButton();
        liftButtons.add(squatButton);
        liftButtons.add(benchButton);
        liftButtons.add(deadliftButton);
        liftButtons.add(cardioButton);

        column.add(liftButtons);
        return column;
    }

    private JPanel createUpgradeRow(final UpgradeType type) {
        JPanel row = createCard(new BorderLayout(8, 0), new Color(221, 215, 203), new Color(253, 248, 241));

        JLabel text = new JLabel("<html><b>" + type.label + "</b><br/>" + type.desc + "</html>");
        text.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        text.setForeground(TEXT_DARK);
        upgradeTextLabels.put(type, text);

        JButton button = new JButton();
        button.setFocusPainted(false);
        button.setFont(new Font(Font.DIALOG, Font.BOLD, 11));
        button.setBackground(new Color(243, 238, 232));
        button.setForeground(TEXT_DARK);
        button.setBorder(new CompoundBorder(new LineBorder(new Color(185, 178, 166), 1, true), new EmptyBorder(8, 8, 8, 8)));
        button.addActionListener(e -> upgradeSelectedLift(type));
        upgradeButtons.put(type, button);

        row.add(text, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private JButton createLiftSelectButton(final LiftType lift) {
        JButton button = new JButton(lift.label);
        button.setFocusPainted(false);
        button.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        button.setBorder(new CompoundBorder(new LineBorder(Color.WHITE, 2, true), new EmptyBorder(10, 10, 10, 10)));
        button.setForeground(Color.WHITE);
        button.addActionListener(e -> selectLift(lift));
        return button;
    }

    private JButton createCardioSelectButton() {
        JButton button = new JButton("Cardio");
        button.setFocusPainted(false);
        button.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        button.setBorder(new CompoundBorder(new LineBorder(Color.WHITE, 2, true), new EmptyBorder(10, 10, 10, 10)));
        button.setForeground(Color.WHITE);
        button.addActionListener(e -> selectCardio());
        return button;
    }

    private JButton createActionButton(String label, Color color, Runnable action) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new CompoundBorder(new LineBorder(color.darker(), 1, true), new EmptyBorder(10, 12, 10, 12)));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JPanel createCard(LayoutManager layout, Color borderColor, Color fill) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(fill);
        panel.setBorder(new CompoundBorder(new LineBorder(borderColor, 2, true), new EmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    private void startLoop() {
        frameTimer = new Timer(16, e -> onFrame());
        frameTimer.start();
    }

    private void onFrame() {
        long now = System.nanoTime();
        if (lastFrameNanos == 0L) {
            lastFrameNanos = now;
            return;
        }

        double dt = (now - lastFrameNanos) / 1_000_000_000.0;
        if (dt > 0.05) {
            dt = 0.05;
        }
        lastFrameNanos = now;

        ambientTime += dt;
        uiAccumulator += dt;

        recoverEnergy(dt);
        tickCardio(dt);
        tickAnimation(dt);
        tickAutoTraining(dt);

        if (uiAccumulator >= 0.15) {
            uiAccumulator = 0.0;
            updateUI();
        }

        stagePanel.repaint();
    }

    private void recoverEnergy(double dt) {
        for (LiftType lift : LiftType.values()) {
            double current = liftEnergy.get(lift).doubleValue();
            double max = maxLiftEnergy.get(lift).doubleValue();
            double gain = getRecoveryPerSecond(lift) * dt;
            if (current < max) {
                current = Math.min(max, current + gain);
                liftEnergy.put(lift, Double.valueOf(current));
            }
        }
    }

    private void tickCardio(double dt) {
        double targetPace = getCardioBaselinePace();
        double settleRate = 0.50 + cardioConditioningLevel * 0.10;
        cardioPace += (targetPace - cardioPace) * Math.min(1.0, dt * settleRate);
        cardioPace = Math.max(targetPace, Math.min(getCardioPaceCap(), cardioPace));

        cardioGainAccumulator += getCardioGainRate() * dt;
        if (cardioGainAccumulator >= 1.0) {
            long passiveGains = (long) cardioGainAccumulator;
            cardioGainAccumulator -= passiveGains;
            totalGains += passiveGains;
            lifetimeGains += passiveGains;
        }
    }

    private void tickAnimation(double dt) {
        if (!isAnimating) {
            return;
        }

        double duration = getAnimationDuration(animationLift);
        animationPhase += dt / duration;
        if (animationPhase >= 1.0) {
            animationPhase = 0.0;
            isAnimating = false;
        }
    }

    private void tickAutoTraining(double dt) {
        if (!hasGymMembership) {
            return;
        }

        autoTrainAccumulator += dt;
        double interval = Math.max(0.55, 1.0 - (getAverageSpeed() - 1.0) * 0.10);

        while (autoTrainAccumulator >= interval) {
            autoTrainAccumulator -= interval;
            for (LiftType lift : LiftType.values()) {
                boolean animate = lift == selectedLift && !isAnimating;
                performLift(lift, animate, false);
            }
        }
    }

    private void selectLift(LiftType lift) {
        cardioSelected = false;
        selectedLift = lift;
        updateUI();
        stagePanel.repaint();
    }

    private void selectCardio() {
        cardioSelected = true;
        updateUI();
        stagePanel.repaint();
    }

    private void trainSelectedLift(boolean fromUser) {
        if (cardioSelected) {
            performCardioClick(fromUser);
            updateUI();
            return;
        }

        if (fromUser) {
            boostCardioFromAction(0.04);
        }

        if (performLift(selectedLift, true, fromUser)) {
            updateUI();
            return;
        }

        if (autoRotateWhenEmpty) {
            LiftType fallbackLift = findNextAvailableLift(selectedLift);
            if (fallbackLift != null) {
                selectedLift = fallbackLift;
                cardioSelected = false;
                performLift(fallbackLift, true, fromUser);
                showToast("Switched to " + fallbackLift.label + ".");
            } else {
                cardioSelected = true;
                performCardioClick(fromUser);
                showToast("All lifts are gassed. Switched to cardio.");
            }
        } else {
            showToast(selectedLift.label + " is out of energy.");
        }
        updateUI();
    }

    private boolean performLift(LiftType lift, boolean animate, boolean fromUser) {
        double currentEnergy = liftEnergy.get(lift).doubleValue();
        if (currentEnergy < 10.0) {
            return false;
        }

        liftEnergy.put(lift, Double.valueOf(currentEnergy - 10.0));

        long gainsEarned = calculateLiftGains(lift);
        totalGains += gainsEarned;
        lifetimeGains += gainsEarned;
        totalReps.put(lift, Long.valueOf(totalReps.get(lift).longValue() + 1L));
        growDog(lift, gainsEarned);
        boostCardioFromAction(fromUser ? 0.16 : 0.05);

        if (animate) {
            animationLift = lift;
            isAnimating = true;
            animationPhase = 0.0;
        }

        if (fromUser) {
            floatingGainText = "+" + formatCompact(gainsEarned) + " gains";
            floatingGainStartedAt = System.currentTimeMillis();
        }

        long reps = totalReps.get(lift).longValue();
        if (reps == 25 || reps == 100 || reps == 250 || reps == 500) {
            addEvent(lift.label + " hit " + reps + " reps.");
            showToast(lift.label + " milestone reached.");
        }

        return true;
    }

    private void boostCardioFromAction(double amount) {
        cardioPace = Math.min(getCardioPaceCap(), cardioPace + amount);
    }

    private LiftType findNextAvailableLift(LiftType current) {
        LiftType[] order = LiftType.values();
        int start = 0;
        for (int i = 0; i < order.length; i++) {
            if (order[i] == current) {
                start = i;
                break;
            }
        }

        for (int step = 1; step <= order.length; step++) {
            LiftType candidate = order[(start + step) % order.length];
            if (liftEnergy.get(candidate).doubleValue() >= 10.0) {
                return candidate;
            }
        }
        return null;
    }

    private long calculateLiftGains(LiftType lift) {
        double power = liftPower.get(lift).intValue();
        double speed = liftSpeed.get(lift).doubleValue();
        double mastery = 1.0 + Math.min(1.8, totalReps.get(lift).longValue() / 180.0);
        double sizeBonus = 1.0 + dogSizeCm / 420.0;
        double multiplier = mastery * sizeBonus * getGlobalGainMultiplier();
        if (lift == selectedLift) {
            multiplier *= 1.08;
        }

        double base = (1.8 + power * 1.65) * (0.95 + speed * 0.82);
        double liftIdentityBonus = lift == LiftType.DEADLIFT ? 1.10 : (lift == LiftType.BENCH ? 0.92 : 1.00);
        double value = base * multiplier * liftIdentityBonus;
        return Math.max(1L, Math.round(value));
    }

    private double getCardioGainRate() {
        double baseRate = 0.020 + cardioEngineLevel * 0.026 + cardioStrideLevel * 0.009 + dogSizeCm / 22000.0;
        double paceBonus = 0.36 + cardioPace * (0.10 + cardioPaceLevel * 0.008);
        double multiplier = 1.0;
        if (equippedIsland == IslandStyle.SUNSET) {
            multiplier *= 1.05;
        }
        if (hasGymMembership) {
            multiplier *= 1.04;
        }
        if (equippedBuddy == BuddyType.CRAB) {
            multiplier *= 0.96;
        }
        return baseRate * paceBonus * multiplier;
    }

    private long calculateCardioClickGains() {
        double base = 0.55 + cardioStrideLevel * 0.18 + cardioEngineLevel * 0.08;
        double paceBonus = 0.52 + cardioPace * (0.18 + cardioPaceLevel * 0.012);
        double multiplier = 0.48 * getGlobalGainMultiplier();
        return Math.max(1L, Math.round(base * paceBonus * multiplier));
    }

    private void performCardioClick(boolean fromUser) {
        long gainsEarned = calculateCardioClickGains();
        totalGains += gainsEarned;
        lifetimeGains += gainsEarned;
        cardioClicks++;
        dogSizeCm += 0.08 + cardioStrideLevel * 0.01;
        boostCardioFromAction(0.10 + cardioPaceLevel * 0.025 + (fromUser ? 0.03 : 0.0));

        if (fromUser) {
            floatingGainText = "+" + formatCompact(gainsEarned) + " gains";
            floatingGainStartedAt = System.currentTimeMillis();
        }

        if (cardioClicks == 25 || cardioClicks == 100 || cardioClicks == 250 || cardioClicks == 500) {
            addEvent("Cardio hit " + cardioClicks + " clicks.");
            showToast("Cardio milestone reached.");
        }
    }

    private double getCardioBaselinePace() {
        return 0.18 + cardioConditioningLevel * 0.05 + Math.min(0.16, getAverageSpeed() * 0.02);
    }

    private double getCardioPaceCap() {
        return 1.65 + cardioPaceLevel * 0.18;
    }

    private void growDog(LiftType lift, long gainsEarned) {
        double growth = 0.18 + (liftPower.get(lift).intValue() * 0.045) + (gainsEarned / 1100.0);
        growth *= equippedBuddy.sizeMultiplier;
        if (hasTrophyRack) {
            growth *= 1.05;
        }
        dogSizeCm += growth;

        while (dogSizeCm >= nextEggSizeThreshold) {
            eggs++;
            addEvent("Size milestone hit: " + nextEggSizeThreshold + " cm laid an egg.");
            showToast("Milestone egg earned.");
            nextEggSizeThreshold += 180 + Math.max(40, nextEggSizeThreshold / 7);
        }
    }

    private void upgradeSelectedLift(UpgradeType type) {
        long cost = getUpgradeCost(type, selectedLift);
        if (totalGains < cost) {
            showToast("Need " + formatCompact(cost) + " gains.");
            return;
        }

        totalGains -= cost;

        if (cardioSelected) {
            switch (type) {
                case POWER:
                    cardioEngineLevel++;
                    showToast("Cardio engine increased.");
                    break;
                case ENERGY:
                    cardioPaceLevel++;
                    showToast("Cardio pace cap increased.");
                    break;
                case RECOVERY:
                    cardioConditioningLevel++;
                    showToast("Cardio conditioning increased.");
                    break;
                case SPEED:
                    cardioStrideLevel++;
                    showToast("Cardio stride increased.");
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case POWER:
                    liftPower.put(selectedLift, Integer.valueOf(liftPower.get(selectedLift).intValue() + 1));
                    powerLevel.put(selectedLift, Integer.valueOf(powerLevel.get(selectedLift).intValue() + 1));
                    showToast(selectedLift.label + " power increased.");
                    break;
                case ENERGY:
                    maxLiftEnergy.put(selectedLift, Double.valueOf(maxLiftEnergy.get(selectedLift).doubleValue() + 15.0));
                    liftEnergy.put(selectedLift, Double.valueOf(liftEnergy.get(selectedLift).doubleValue() + 15.0));
                    energyLevel.put(selectedLift, Integer.valueOf(energyLevel.get(selectedLift).intValue() + 1));
                    showToast(selectedLift.label + " endurance increased.");
                    break;
                case RECOVERY:
                    recoveryRate.put(selectedLift, Double.valueOf(recoveryRate.get(selectedLift).doubleValue() + 0.45));
                    recoveryLevel.put(selectedLift, Integer.valueOf(recoveryLevel.get(selectedLift).intValue() + 1));
                    showToast(selectedLift.label + " recovery increased.");
                    break;
                case SPEED:
                    liftSpeed.put(selectedLift, Double.valueOf(liftSpeed.get(selectedLift).doubleValue() + 0.18));
                    speedLevel.put(selectedLift, Integer.valueOf(speedLevel.get(selectedLift).intValue() + 1));
                    showToast(selectedLift.label + " tempo increased.");
                    break;
                default:
                    break;
            }
        }

        dogSizeCm += 1.6;
        updateUI();
        stagePanel.repaint();
    }

    private long getUpgradeCost(UpgradeType type, LiftType lift) {
        if (cardioSelected) {
            switch (type) {
                case POWER:
                    return 60L + cardioEngineLevel * 50L;
                case ENERGY:
                    return 90L + cardioPaceLevel * 65L;
                case RECOVERY:
                    return 115L + cardioConditioningLevel * 80L;
                case SPEED:
                    return 130L + cardioStrideLevel * 90L;
                default:
                    return 100L;
            }
        }

        switch (type) {
            case POWER:
                return 75L + powerLevel.get(lift).intValue() * 55L;
            case ENERGY:
                return 95L + energyLevel.get(lift).intValue() * 65L;
            case RECOVERY:
                return 120L + recoveryLevel.get(lift).intValue() * 80L;
            case SPEED:
                return 145L + speedLevel.get(lift).intValue() * 95L;
            default:
                return 100L;
        }
    }

    private void tradeForEgg() {
        if (totalGains < 500L) {
            showToast("Need 500 gains for an egg.");
            return;
        }

        totalGains -= 500L;
        eggs++;
        addEvent("Traded 500 gains for an egg.");
        showToast("Fresh egg collected.");
        updateUI();
    }

    private void openGymShop() {
        final JDialog dialog = createDialog("Gym Shop", 520, 360);
        JPanel content = new JPanel();
        content.setBackground(PANEL_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createShopRow(
            "Protein Shaker",
            "6 eggs - 2x gains from all lifting.",
            hasProteinShaker,
            new Runnable() {
                @Override
                public void run() {
                    if (buyEggUpgrade(6, hasProteinShaker)) {
                        hasProteinShaker = true;
                        addEvent("Protein shaker purchased.");
                        showToast("Protein shaker online.");
                        dialog.dispose();
                    }
                }
            }
        ));
        content.add(Box.createVerticalStrut(10));

        content.add(createShopRow(
            "Gym Membership",
            "10 eggs - automatic training across all three lifts.",
            hasGymMembership,
            new Runnable() {
                @Override
                public void run() {
                    if (buyEggUpgrade(10, hasGymMembership)) {
                        hasGymMembership = true;
                        addEvent("Gym membership unlocked. Auto-training active.");
                        showToast("Gym membership unlocked.");
                        dialog.dispose();
                    }
                }
            }
        ));
        content.add(Box.createVerticalStrut(10));

        content.add(createShopRow(
            "Olympic Bar",
            "16 eggs - absurd long barbell and more plates per side.",
            hasOlympicBar,
            new Runnable() {
                @Override
                public void run() {
                    if (buyEggUpgrade(16, hasOlympicBar)) {
                        hasOlympicBar = true;
                        addEvent("Olympic bar purchased. Plate spam unlocked.");
                        showToast("Olympic bar equipped.");
                        dialog.dispose();
                    }
                }
            }
        ));
        content.add(Box.createVerticalStrut(10));

        content.add(createShopRow(
            "Massage Table",
            "8 eggs - +0.55 recovery per second to every lift.",
            hasMassageTable,
            new Runnable() {
                @Override
                public void run() {
                    if (buyEggUpgrade(8, hasMassageTable)) {
                        hasMassageTable = true;
                        addEvent("Massage table installed on the island.");
                        showToast("Recovery improved.");
                        dialog.dispose();
                    }
                }
            }
        ));
        content.add(Box.createVerticalStrut(10));

        content.add(createShopRow(
            "Chalk Bucket",
            "9 eggs - +15% total gains from every rep.",
            hasChalkBucket,
            new Runnable() {
                @Override
                public void run() {
                    if (buyEggUpgrade(9, hasChalkBucket)) {
                        hasChalkBucket = true;
                        addEvent("Chalk bucket purchased.");
                        showToast("Grip and gains improved.");
                        dialog.dispose();
                    }
                }
            }
        ));

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void openStyleStudio() {
        final JDialog dialog = createDialog("Style Studio", 440, 320);
        JPanel content = new JPanel();
        content.setBackground(PANEL_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createStyleRow(HatStyle.NONE, dialog));
        content.add(Box.createVerticalStrut(10));
        content.add(createStyleRow(HatStyle.HEADBAND, dialog));
        content.add(Box.createVerticalStrut(10));
        content.add(createStyleRow(HatStyle.CAP, dialog));
        content.add(Box.createVerticalStrut(10));
        content.add(createStyleRow(HatStyle.CROWN, dialog));

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private JPanel createStyleRow(final HatStyle style, final JDialog dialog) {
        String desc;
        if (style == HatStyle.NONE) {
            desc = "Unequip hats and keep the raw dachshund look.";
        } else {
            desc = style.eggCost + " eggs - cosmetic unlock.";
        }

        boolean owned = ownedHats.contains(style);
        boolean equipped = equippedHat == style;
        String buttonText = equipped ? "Equipped" : (owned ? "Equip" : "Buy");

        JPanel row = createCard(new BorderLayout(8, 0), new Color(236, 191, 198), new Color(255, 248, 249));
        JLabel label = new JLabel("<html><b>" + style.label + "</b><br/>" + desc + "</html>");
        label.setForeground(TEXT_DARK);

        JButton button = new JButton(buttonText);
        button.setFocusPainted(false);
        button.setEnabled(!equipped);
        button.addActionListener(e -> {
            if (!ownedHats.contains(style)) {
                if (eggs < style.eggCost) {
                    showToast("Need " + style.eggCost + " eggs.");
                    return;
                }
                eggs -= style.eggCost;
                ownedHats.add(style);
                addEvent(style.label + " purchased.");
            }

            equippedHat = style;
            showToast(style.label + " equipped.");
            dialog.dispose();
            updateUI();
            stagePanel.repaint();
        });

        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private void openIslandBuilder() {
        final JDialog dialog = createDialog("Scenery Shop", 560, 420);
        JPanel content = new JPanel();
        content.setBackground(PANEL_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createIslandRow(IslandStyle.SUNSET, dialog));
        content.add(Box.createVerticalStrut(10));
        content.add(createIslandRow(IslandStyle.GYM_PIER, dialog));
        content.add(Box.createVerticalStrut(10));
        content.add(createIslandRow(IslandStyle.NEON_FEST, dialog));
        content.add(Box.createVerticalStrut(14));

        content.add(createToggleRow("Beach Parasol", "2 eggs - decorative shade on the island.", hasParasol, new Runnable() {
            @Override
            public void run() {
                if (buyEggUpgrade(2, hasParasol)) {
                    hasParasol = true;
                    addEvent("Parasol added to the island.");
                    dialog.dispose();
                }
            }
        }));
        content.add(Box.createVerticalStrut(8));
        content.add(createToggleRow("Lantern String", "4 eggs - festival lights and +5% island vibe.", hasLanterns, new Runnable() {
            @Override
            public void run() {
                if (buyEggUpgrade(4, hasLanterns)) {
                    hasLanterns = true;
                    addEvent("Lantern string installed.");
                    dialog.dispose();
                }
            }
        }));
        content.add(Box.createVerticalStrut(8));
        content.add(createToggleRow("Trophy Rack", "5 eggs - trophy decor and +5% size growth.", hasTrophyRack, new Runnable() {
            @Override
            public void run() {
                if (buyEggUpgrade(5, hasTrophyRack)) {
                    hasTrophyRack = true;
                    addEvent("Trophy rack installed.");
                    dialog.dispose();
                }
            }
        }));
        content.add(Box.createVerticalStrut(8));
        content.add(createToggleRow("Pool Float", "3 eggs - goofy beach prop floating offshore.", hasPoolFloat, new Runnable() {
            @Override
            public void run() {
                if (buyEggUpgrade(3, hasPoolFloat)) {
                    hasPoolFloat = true;
                    addEvent("Pool float drifted into place.");
                    dialog.dispose();
                }
            }
        }));
        content.add(Box.createVerticalStrut(14));

        content.add(createBuddyRow(BuddyType.NONE, dialog));
        content.add(Box.createVerticalStrut(8));
        content.add(createBuddyRow(BuddyType.CRAB, dialog));
        content.add(Box.createVerticalStrut(8));
        content.add(createBuddyRow(BuddyType.GULL, dialog));
        content.add(Box.createVerticalStrut(8));
        content.add(createBuddyRow(BuddyType.OTTER, dialog));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        dialog.setContentPane(scroll);
        dialog.setVisible(true);
    }

    private JPanel createIslandRow(final IslandStyle style, final JDialog dialog) {
        boolean owned = ownedIslands.contains(style);
        boolean equipped = equippedIsland == style;
        String desc = style == IslandStyle.SUNSET
            ? "Default powerlifting gym."
            : style.eggCost + " eggs - unlock a new stage theme.";

        JPanel row = createCard(new BorderLayout(8, 0), new Color(167, 205, 184), new Color(248, 255, 250));
        JLabel label = new JLabel("<html><b>" + style.label + "</b><br/>" + desc + "</html>");
        label.setForeground(TEXT_DARK);

        JButton button = new JButton(equipped ? "Equipped" : (owned ? "Equip" : "Buy"));
        button.setEnabled(!equipped);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            if (!ownedIslands.contains(style)) {
                if (eggs < style.eggCost) {
                    showToast("Need " + style.eggCost + " eggs.");
                    return;
                }
                eggs -= style.eggCost;
                ownedIslands.add(style);
                addEvent(style.label + " purchased.");
            }
            equippedIsland = style;
            showToast(style.label + " equipped.");
            dialog.dispose();
            updateUI();
            stagePanel.repaint();
        });

        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private JPanel createBuddyRow(final BuddyType buddy, final JDialog dialog) {
        boolean owned = ownedBuddies.contains(buddy);
        boolean equipped = equippedBuddy == buddy;

        String desc = buddy == BuddyType.NONE
            ? buddy.desc
            : buddy.eggCost + " eggs - " + buddy.desc;

        JPanel row = createCard(new BorderLayout(8, 0), new Color(193, 183, 226), new Color(250, 247, 255));
        JLabel label = new JLabel("<html><b>" + buddy.label + "</b><br/>" + desc + "</html>");
        label.setForeground(TEXT_DARK);

        JButton button = new JButton(equipped ? "Equipped" : (owned ? "Equip" : "Buy"));
        button.setEnabled(!equipped);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            if (!ownedBuddies.contains(buddy)) {
                if (eggs < buddy.eggCost) {
                    showToast("Need " + buddy.eggCost + " eggs.");
                    return;
                }
                eggs -= buddy.eggCost;
                ownedBuddies.add(buddy);
                addEvent(buddy.label + " joined the island.");
            }
            equippedBuddy = buddy;
            showToast(buddy.label + " equipped.");
            dialog.dispose();
            updateUI();
            stagePanel.repaint();
        });

        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private JPanel createToggleRow(String title, String desc, boolean owned, Runnable onBuy) {
        JPanel row = createCard(new BorderLayout(8, 0), new Color(214, 206, 164), new Color(255, 252, 239));
        JLabel label = new JLabel("<html><b>" + title + "</b><br/>" + desc + "</html>");
        label.setForeground(TEXT_DARK);

        JButton button = new JButton(owned ? "Owned" : "Buy");
        button.setEnabled(!owned);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            onBuy.run();
            updateUI();
            stagePanel.repaint();
        });

        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private void openMeetMenu() {
        final JDialog dialog = createDialog("Power Meets", 520, 420);
        JPanel content = new JPanel();
        content.setBackground(PANEL_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel intro = new JLabel(
            "<html><b>Competition ladder.</b><br/>" +
            "Higher meets need more total strength and reward far bigger click multipliers.</html>"
        );
        intro.setForeground(TEXT_DARK);
        intro.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(intro);
        content.add(Box.createVerticalStrut(12));

        for (final MeetTier tier : MeetTier.values()) {
            content.add(createMeetRow(tier, dialog));
            content.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        dialog.setContentPane(scroll);
        dialog.setVisible(true);
    }

    private JPanel createMeetRow(final MeetTier tier, final JDialog dialog) {
        final boolean unlocked = getStrengthScore() >= tier.minStrength;
        JPanel row = createCard(new BorderLayout(8, 0), new Color(210, 176, 142), new Color(255, 248, 241));
        JLabel label = new JLabel(
            "<html><b>" + tier.label + "</b><br/>" +
            "Need " + ONE_DECIMAL.format(tier.minStrength) + " strength | " +
            ONE_DECIMAL.format(tier.clickMultiplier) + "x click multiplier | " +
            tier.durationSeconds + "s round</html>"
        );
        label.setForeground(TEXT_DARK);

        JButton button = new JButton(unlocked ? "Compete" : "Locked");
        button.setEnabled(unlocked);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            dialog.dispose();
            startMeet(tier);
        });

        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private void startMeet(final MeetTier tier) {
        final JDialog dialog = createDialog(tier.label, 470, 310);
        final JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(PANEL_BG);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        final JLabel timerLabel = new JLabel("Time Left: " + tier.durationSeconds + "s");
        timerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        timerLabel.setForeground(TEXT_DARK);

        final JLabel scoreLabel = new JLabel("Clicks: 0");
        scoreLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        scoreLabel.setForeground(TEXT_SOFT);

        final JLabel payoutLabel = new JLabel("Projected payout: 0 gains");
        payoutLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        payoutLabel.setForeground(TEXT_SOFT);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(timerLabel);
        top.add(Box.createVerticalStrut(6));
        top.add(scoreLabel);
        top.add(Box.createVerticalStrut(4));
        top.add(payoutLabel);

        final JButton spamButton = new JButton("SPAM FOR TOTAL");
        spamButton.setFocusPainted(false);
        spamButton.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
        spamButton.setForeground(Color.WHITE);
        spamButton.setBackground(new Color(210, 92, 55));
        spamButton.setBorder(new CompoundBorder(new LineBorder(new Color(130, 58, 34), 1, true), new EmptyBorder(20, 20, 20, 20)));

        final int[] clicks = {0};
        spamButton.addActionListener(e -> {
            clicks[0]++;
            scoreLabel.setText("Clicks: " + clicks[0]);
            payoutLabel.setText("Projected payout: " + formatCompact(calculateMeetPayout(tier, clicks[0])) + " gains");
        });

        JLabel tip = new JLabel(
            "<html>Meet score is pure click spam. Bigger meets multiply each click harder.<br/>" +
            "Use this as a burst mechanic on top of regular SBD training.</html>"
        );
        tip.setForeground(TEXT_SOFT);
        tip.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));

        content.add(top, BorderLayout.NORTH);
        content.add(spamButton, BorderLayout.CENTER);
        content.add(tip, BorderLayout.SOUTH);

        dialog.setContentPane(content);

        final int[] timeLeft = {tier.durationSeconds};
        final Timer meetTimer = new Timer(1000, null);
        meetTimer.addActionListener(e -> {
            timeLeft[0]--;
            timerLabel.setText("Time Left: " + Math.max(0, timeLeft[0]) + "s");
            if (timeLeft[0] <= 0) {
                meetTimer.stop();
                spamButton.setEnabled(false);
                long payout = calculateMeetPayout(tier, clicks[0]);
                totalGains += payout;
                lifetimeGains += payout;
                meetsCompleted++;
                addEvent(tier.label + " finished with " + clicks[0] + " clicks.");
                showToast("Meet payout: " + formatCompact(payout) + " gains.");
                JOptionPane.showMessageDialog(
                    dialog,
                    tier.label + " complete.\nClicks: " + clicks[0] + "\nPayout: " + formatCompact(payout) + " gains",
                    "Meet Results",
                    JOptionPane.INFORMATION_MESSAGE
                );
                dialog.dispose();
                updateUI();
            }
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                meetTimer.stop();
            }
        });

        meetTimer.start();
        dialog.setVisible(true);
    }

    private long calculateMeetPayout(MeetTier tier, int clicks) {
        double basePerClick = 4.0 + getStrengthScore() * 0.45 + getAverageSpeed() * 0.75;
        return Math.max(0L, Math.round(clicks * basePerClick * tier.clickMultiplier));
    }

    private void openSettingsMenu() {
        final JDialog dialog = createDialog("Settings", 500, 420);
        JPanel content = new JPanel();
        content.setBackground(PANEL_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel intro = new JLabel(
            "<html><b>Accessibility and quality-of-life.</b><br/>" +
            "Inspired by common accessibility options like larger text, stronger contrast, and reduced motion.</html>"
        );
        intro.setForeground(TEXT_DARK);
        intro.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));

        final JTextField nameField = new JTextField(dogName);
        final JCheckBox highContrastBox = new JCheckBox("High Contrast UI", highContrastMode);
        final JCheckBox largeTextBox = new JCheckBox("Large Text", largeTextMode);
        final JCheckBox reduceMotionBox = new JCheckBox("Reduce Motion", reduceMotionMode);
        final JCheckBox simplifiedStageBox = new JCheckBox("Simplified Background", simplifiedStageMode);
        final JCheckBox autoRotateBox = new JCheckBox("Auto Rotate To Ready Lift", autoRotateWhenEmpty);

        JCheckBox[] boxes = {highContrastBox, largeTextBox, reduceMotionBox, simplifiedStageBox, autoRotateBox};
        for (JCheckBox box : boxes) {
            box.setOpaque(false);
            box.setForeground(TEXT_DARK);
            box.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        }

        JLabel nameLabel = new JLabel("Dog Name");
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

        JLabel help = new JLabel(
            "<html>Large text and contrast are based on common accessibility guidance. " +
            "Reduced motion cuts stage movement, and simplified background removes extra visual clutter.</html>"
        );
        help.setForeground(TEXT_SOFT);
        help.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));

        JButton applyButton = new JButton("Apply");
        applyButton.setFocusPainted(false);
        applyButton.addActionListener(e -> {
            dogName = nameField.getText().trim().length() == 0 ? "Wiener" : nameField.getText().trim();
            highContrastMode = highContrastBox.isSelected();
            largeTextMode = largeTextBox.isSelected();
            reduceMotionMode = reduceMotionBox.isSelected();
            simplifiedStageMode = simplifiedStageBox.isSelected();
            autoRotateWhenEmpty = autoRotateBox.isSelected();
            applyAccessibilitySettings();
            addEvent("Settings updated for " + dogName + ".");
            showToast("Settings applied.");
            dialog.dispose();
            updateUI();
            stagePanel.repaint();
        });

        content.add(intro);
        content.add(Box.createVerticalStrut(12));
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(nameField);
        content.add(Box.createVerticalStrut(12));
        content.add(highContrastBox);
        content.add(Box.createVerticalStrut(8));
        content.add(largeTextBox);
        content.add(Box.createVerticalStrut(8));
        content.add(reduceMotionBox);
        content.add(Box.createVerticalStrut(8));
        content.add(simplifiedStageBox);
        content.add(Box.createVerticalStrut(8));
        content.add(autoRotateBox);
        content.add(Box.createVerticalStrut(12));
        content.add(help);
        content.add(Box.createVerticalStrut(14));
        content.add(applyButton);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void applyAccessibilitySettings() {
        applyFontScaleToComponent(getContentPane(), largeTextMode ? 1.15f : 1.0f);
        updateUI();
        revalidate();
        repaint();
    }

    private void applyFontScaleToComponent(Component component, float scale) {
        if (component instanceof JComponent) {
            JComponent jc = (JComponent) component;
            Font font = jc.getFont();
            if (font != null) {
                Object baseSize = jc.getClientProperty("baseFontSize");
                float base = baseSize instanceof Float ? ((Float) baseSize).floatValue() : font.getSize2D();
                if (!(baseSize instanceof Float)) {
                    jc.putClientProperty("baseFontSize", Float.valueOf(base));
                }
                jc.setFont(font.deriveFont(base * scale));
            }
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyFontScaleToComponent(child, scale);
            }
        }
    }

    private void openPounder() {
        final JDialog dialog = createDialog("The Pounder", 420, 260);
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(PANEL_BG);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel intro = new JLabel(
            "<html><b>Place a gains bet on The Pounder.</b><br/>" +
            "Higher strength improves the odds of cracking the machine.</html>"
        );
        intro.setForeground(TEXT_DARK);
        intro.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        content.add(intro, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));
        buttons.setOpaque(false);
        buttons.add(createBetButton(100, dialog));
        buttons.add(createBetButton(250, dialog));
        buttons.add(createBetButton(500, dialog));
        content.add(buttons, BorderLayout.CENTER);

        JLabel footer = new JLabel("Current strength score: " + ONE_DECIMAL.format(getStrengthScore()));
        footer.setForeground(TEXT_SOFT);
        footer.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        content.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private JButton createBetButton(final int bet, final JDialog dialog) {
        JButton button = new JButton("Bet " + bet);
        button.setFocusPainted(false);
        button.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        button.addActionListener(e -> {
            if (totalGains < bet) {
                showToast("Need " + bet + " gains to bet.");
                return;
            }
            totalGains -= bet;

            double roll = random.nextDouble() + getStrengthScore() * 0.06;
            String result;
            long payout;

            if (roll > 2.05) {
                payout = bet * 4L;
                eggs++;
                result = "Jackpot smash. +" + payout + " gains and 1 egg.";
            } else if (roll > 1.55) {
                payout = bet * 2L;
                result = "Huge hit. +" + payout + " gains.";
            } else if (roll > 1.10) {
                payout = Math.round(bet * 1.35);
                result = "Solid hit. +" + payout + " gains.";
            } else if (roll > 0.75) {
                payout = Math.round(bet * 0.65);
                result = "Weak thud. +" + payout + " gains back.";
            } else {
                payout = 0L;
                result = "Missed the sweet spot. Bet lost.";
            }

            totalGains += payout;
            addEvent("The Pounder: " + result);
            showToast(result);
            dialog.dispose();
            updateUI();
        });
        return button;
    }

    private void saveStageSnapshot() {
        BufferedImage image = new BufferedImage(stagePanel.getWidth(), stagePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        stagePanel.paint(g2);
        g2.dispose();

        File file = new File(System.getProperty("user.dir"), "wiener_snapshot_" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(image, "png", file);
            addEvent("Snapshot saved to " + file.getName() + ".");
            showToast("Saved photo: " + file.getName());
        } catch (IOException ex) {
            showToast("Snapshot failed.");
        }
    }

    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private JPanel createShopRow(String title, String desc, boolean owned, Runnable onBuy) {
        JPanel row = createCard(new BorderLayout(8, 0), new Color(183, 198, 234), new Color(247, 250, 255));
        JLabel label = new JLabel("<html><b>" + title + "</b><br/>" + desc + "</html>");
        label.setForeground(TEXT_DARK);

        JButton button = new JButton(owned ? "Owned" : "Buy");
        button.setEnabled(!owned);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            onBuy.run();
            updateUI();
        });

        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private boolean buyEggUpgrade(int cost, boolean alreadyOwned) {
        if (alreadyOwned) {
            showToast("Already owned.");
            return false;
        }
        if (eggs < cost) {
            showToast("Need " + cost + " eggs.");
            return false;
        }

        eggs -= cost;
        return true;
    }

    private void updateUI() {
        gainsLabel.setText(formatCompact(totalGains));
        eggsLabel.setText(String.valueOf(eggs));
        sizeLabel.setText((int) dogSizeCm + " cm");
        rankLabel.setText(getCurrentRankName());

        if (cardioSelected) {
            selectedLiftLabel.setText(dogName + " Cardio Focus");
            selectedLiftInfoLabel.setText(
                "<html>Always-on idle gains. Clicking builds pace and adds light extra gains.<br/>" +
                "Click gains " + formatCompact(calculateCardioClickGains()) +
                " | Passive " + ONE_DECIMAL.format(getCardioGainRate()) + "/s" +
                " | Pace cap " + ONE_DECIMAL.format(getCardioPaceCap()) + "x" +
                "</html>"
            );
        } else {
            selectedLiftLabel.setText(dogName + " " + selectedLift.label + " Focus");
            selectedLiftInfoLabel.setText(
                "<html>" +
                selectedLift.hint + "<br/>" +
                "Power " + liftPower.get(selectedLift) +
                " | Speed " + ONE_DECIMAL.format(liftSpeed.get(selectedLift).doubleValue()) + "x" +
                " | Recovery " + ONE_DECIMAL.format(getRecoveryPerSecond(selectedLift)) + "/s" +
                " | Gains/rep " + formatCompact(calculateLiftGains(selectedLift)) +
                "</html>"
            );
        }

        bonusLabel.setText(
            "Boosts: " + ONE_DECIMAL.format(getGlobalGainMultiplier()) + "x gains, " +
            ONE_DECIMAL.format(getIslandVibeMultiplier()) + "x scene bonus, " +
            ONE_DECIMAL.format(equippedBuddy.recoveryBonus) + "/s buddy recovery"
        );

        cardioLabel.setText(
            "Cardio: pace " + ONE_DECIMAL.format(cardioPace) + "x, passive " +
            ONE_DECIMAL.format(getCardioGainRate()) + " gains/s"
        );

        cardioStatsBoardLabel.setText(
            "<html>Clicks " + cardioClicks +
            " | Click " + formatCompact(calculateCardioClickGains()) +
            "<br/>Passive " + ONE_DECIMAL.format(getCardioGainRate()) + "/s | Pace cap " + ONE_DECIMAL.format(getCardioPaceCap()) + "x</html>"
        );
        cardioPaceBar.setMaximum((int) Math.round(getCardioPaceCap() * 100.0));
        cardioPaceBar.setValue((int) Math.round(cardioPace * 100.0));
        cardioPaceBar.setString("Pace " + ONE_DECIMAL.format(cardioPace) + "x");
        cardioPaceBar.setBackground(highContrastMode ? Color.BLACK : new Color(223, 218, 210));
        cardioPaceBar.setForeground(highContrastMode ? Color.WHITE : CARDIO_COLOR);

        int nextRank = getNextRankThreshold();
        milestoneLabel.setText(
            nextRank > 0
                ? "Next rank at " + nextRank + " cm. Next auto egg at " + nextEggSizeThreshold + " cm. Meets done: " + meetsCompleted
                : "Top rank reached. Keep growing the beach monster. Meets done: " + meetsCompleted
        );

        for (LiftType lift : LiftType.values()) {
            JProgressBar bar = energyBars.get(lift);
            double current = liftEnergy.get(lift).doubleValue();
            double max = maxLiftEnergy.get(lift).doubleValue();
            bar.setMaximum((int) Math.round(max));
            bar.setValue((int) Math.round(current));
            bar.setString((int) current + " / " + (int) max + " energy");
            bar.setBackground(highContrastMode ? Color.BLACK : new Color(223, 218, 210));
            bar.setForeground(highContrastMode ? Color.WHITE : lift.accent);

            statsLabels.get(lift).setText(
                "<html>Power " + liftPower.get(lift) +
                " | Speed " + ONE_DECIMAL.format(liftSpeed.get(lift).doubleValue()) + "x<br/>" +
                "Recovery " + ONE_DECIMAL.format(getRecoveryPerSecond(lift)) + "/s | Reps " + totalReps.get(lift) +
                "</html>"
            );
        }

        for (UpgradeType type : UpgradeType.values()) {
            JButton button = upgradeButtons.get(type);
            if (button != null) {
                button.setText("<html><center>Upgrade<br/>" + formatCompact(getUpgradeCost(type, selectedLift)) + " gains</center></html>");
            }
            JLabel label = upgradeTextLabels.get(type);
            if (label != null) {
                label.setText(getUpgradeDescription(type));
            }
        }

        trainButton.setText(cardioSelected ? "Run Cardio" : "Train Selected Lift");
        trainButton.setBackground(getSelectedAccent());
        trainButton.setBorder(new CompoundBorder(new LineBorder(getSelectedAccent().darker(), 1, true), new EmptyBorder(10, 12, 10, 12)));
        tradeEggButton.setBackground(highContrastMode ? Color.BLACK : new Color(184, 144, 54));
        tradeEggButton.setForeground(Color.WHITE);
        eventArea.setBackground(highContrastMode ? Color.WHITE : new Color(255, 252, 247));
        eventArea.setForeground(Color.BLACK);

        updateLiftButtons();
        updateEventArea();
    }

    private void updateLiftButtons() {
        updateLiftButton(squatButton, LiftType.SQUAT);
        updateLiftButton(benchButton, LiftType.BENCH);
        updateLiftButton(deadliftButton, LiftType.DEADLIFT);
        if (cardioSelected) {
            cardioButton.setBackground(highContrastMode ? Color.BLACK : CARDIO_COLOR);
        } else {
            cardioButton.setBackground(new Color(94, 100, 120));
        }
    }

    private void updateLiftButton(JButton button, LiftType lift) {
        if (!cardioSelected && selectedLift == lift) {
            button.setBackground(highContrastMode ? Color.BLACK : lift.accent);
        } else {
            button.setBackground(new Color(94, 100, 120));
        }
    }

    private void updateEventArea() {
        StringBuilder sb = new StringBuilder();
        for (String entry : eventLog) {
            sb.append("- ").append(entry).append('\n');
        }
        eventArea.setText(sb.toString());
        eventArea.setCaretPosition(0);
    }

    private void addEvent(String message) {
        eventLog.addFirst(message);
        while (eventLog.size() > 9) {
            eventLog.removeLast();
        }
    }

    private void showToast(String message) {
        toastMessage = message;
        toastExpiresAt = System.currentTimeMillis() + 2200L;
    }

    private double getGlobalGainMultiplier() {
        double multiplier = 1.0;
        if (hasProteinShaker) {
            multiplier *= 2.0;
        }
        if (hasChalkBucket) {
            multiplier *= 1.15;
        }
        multiplier *= equippedBuddy.gainsMultiplier;
        multiplier *= getIslandVibeMultiplier();
        return multiplier;
    }

    private double getIslandVibeMultiplier() {
        double multiplier = 1.0;
        if (hasParasol) {
            multiplier += 0.05;
        }
        if (hasLanterns) {
            multiplier += 0.05;
        }
        if (equippedIsland == IslandStyle.NEON_FEST) {
            multiplier += 0.05;
        }
        if (equippedIsland == IslandStyle.GYM_PIER) {
            multiplier += 0.03;
        }
        return multiplier;
    }

    private double getRecoveryPerSecond(LiftType lift) {
        double recovery = recoveryRate.get(lift).doubleValue();
        if (hasMassageTable) {
            recovery += 0.55;
        }
        if (equippedIsland == IslandStyle.GYM_PIER) {
            recovery += 0.15;
        }
        recovery += equippedBuddy.recoveryBonus;
        return recovery;
    }

    private double getAverageSpeed() {
        double total = 0.0;
        for (LiftType lift : LiftType.values()) {
            total += liftSpeed.get(lift).doubleValue();
        }
        return total / LiftType.values().length;
    }

    private double getStrengthScore() {
        double total = 0.0;
        for (LiftType lift : LiftType.values()) {
            total += liftPower.get(lift).intValue();
        }
        return total / 3.0 + dogSizeCm / 180.0;
    }

    private Color getSelectedAccent() {
        if (highContrastMode) {
            return Color.BLACK;
        }
        return cardioSelected ? CARDIO_COLOR : selectedLift.accent;
    }

    private String getUpgradeDescription(UpgradeType type) {
        if (!cardioSelected) {
            return "<html><b>" + type.label + "</b><br/>" + type.desc + "</html>";
        }

        switch (type) {
            case POWER:
                return "<html><b>Engine</b><br/>Raise passive cardio gains.</html>";
            case ENERGY:
                return "<html><b>Pace Cap</b><br/>Let clicks build to higher pace.</html>";
            case RECOVERY:
                return "<html><b>Conditioning</b><br/>Keep a stronger idle cardio baseline.</html>";
            case SPEED:
                return "<html><b>Stride</b><br/>Raise click gains and pace scaling.</html>";
            default:
                return "<html><b>Upgrade</b><br/>Improve cardio.</html>";
        }
    }

    private double getDogScale() {
        return 0.92 + Math.min(0.95, dogSizeCm / 900.0);
    }

    private double getAnimationDuration(LiftType lift) {
        return Math.max(0.36, 0.82 / Math.max(1.0, liftSpeed.get(lift).doubleValue()));
    }

    private double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private double easeInOut(double value) {
        double t = clamp01(value);
        return t * t * (3.0 - 2.0 * t);
    }

    private double easeOut(double value) {
        double t = clamp01(value);
        return 1.0 - (1.0 - t) * (1.0 - t);
    }

    private double getAnimatedLiftPosition(LiftType lift) {
        if (!isAnimating || animationLift != lift) {
            return 0.0;
        }

        double phase = animationPhase;
        switch (lift) {
            case SQUAT:
                if (phase < 0.58) {
                    return easeInOut(phase / 0.58);
                }
                return 1.0 - easeInOut((phase - 0.58) / 0.42);
            case BENCH:
                if (phase < 0.42) {
                    return easeInOut(phase / 0.42);
                }
                if (phase < 0.55) {
                    return 1.0;
                }
                return 1.0 - easeOut((phase - 0.55) / 0.45);
            case DEADLIFT:
                if (phase < 0.56) {
                    return easeOut(phase / 0.56);
                }
                if (phase < 0.64) {
                    return 1.0;
                }
                return 1.0 - easeInOut((phase - 0.64) / 0.36);
            default:
                return 0.0;
        }
    }

    private double getCardioStride() {
        double motionScale = reduceMotionMode ? 0.30 : 1.0;
        return Math.sin(ambientTime * (3.0 + cardioPace * 2.2 * motionScale)) * (1.0 + cardioPace * 0.08) * motionScale;
    }

    private String getCurrentRankName() {
        int index = 0;
        for (int i = 0; i < RANK_THRESHOLDS.length; i++) {
            if (dogSizeCm >= RANK_THRESHOLDS[i]) {
                index = i;
            }
        }
        return RANK_NAMES[index];
    }

    private int getNextRankThreshold() {
        for (int i = 0; i < RANK_THRESHOLDS.length; i++) {
            if (dogSizeCm < RANK_THRESHOLDS[i]) {
                return RANK_THRESHOLDS[i];
            }
        }
        return -1;
    }

    private String formatCompact(long value) {
        return formatCompact((double) value);
    }

    private String formatCompact(double value) {
        double abs = Math.abs(value);
        if (abs >= 1_000_000_000.0) {
            return ONE_DECIMAL.format(value / 1_000_000_000.0) + "B";
        }
        if (abs >= 1_000_000.0) {
            return ONE_DECIMAL.format(value / 1_000_000.0) + "M";
        }
        if (abs >= 1_000.0) {
            return ONE_DECIMAL.format(value / 1_000.0) + "K";
        }
        return String.valueOf(Math.round(value));
    }

    private Point2D.Double point(double x, double y) {
        return new Point2D.Double(x, y);
    }

    private Point2D.Double polar(Point2D.Double start, double length, double degrees) {
        double radians = Math.toRadians(degrees);
        return new Point2D.Double(
            start.x + Math.cos(radians) * length,
            start.y + Math.sin(radians) * length
        );
    }

    private void drawLimb(Graphics2D g2, Point2D.Double start, Point2D.Double joint, Point2D.Double end, float thickness, Color color) {
        Stroke old = g2.getStroke();

        g2.setStroke(new BasicStroke(thickness + 5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(66, 45, 27, 80));
        g2.draw(new Line2D.Double(start.x + 2, start.y + 2, joint.x + 2, joint.y + 2));
        g2.draw(new Line2D.Double(joint.x + 2, joint.y + 2, end.x + 2, end.y + 2));

        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);
        g2.draw(new Line2D.Double(start, joint));
        g2.draw(new Line2D.Double(joint, end));

        g2.setColor(new Color(248, 220, 176));
        g2.fill(new Ellipse2D.Double(end.x - thickness * 0.35, end.y - thickness * 0.25, thickness * 0.7, thickness * 0.5));

        g2.setStroke(old);
    }

    private void drawTorso(Graphics2D g2, Point2D.Double hip, Point2D.Double shoulder, double thickness, Color coat, Color highlight) {
        Graphics2D body = (Graphics2D) g2.create();
        double dx = shoulder.x - hip.x;
        double dy = shoulder.y - hip.y;
        double length = Math.hypot(dx, dy) + thickness * 0.9;
        double angle = Math.atan2(dy, dx);
        double centerX = (hip.x + shoulder.x) / 2.0;
        double centerY = (hip.y + shoulder.y) / 2.0;
        double bodyHeight = thickness * 1.15;

        body.translate(centerX, centerY);
        body.rotate(angle);

        body.setColor(new Color(62, 42, 26, 70));
        body.fill(new RoundRectangle2D.Double(-length / 2.0 + 3, -bodyHeight / 2.0 + 4, length, bodyHeight, bodyHeight, bodyHeight));

        body.setColor(coat);
        body.fill(new RoundRectangle2D.Double(-length / 2.0, -bodyHeight / 2.0, length, bodyHeight, bodyHeight, bodyHeight));

        body.setColor(new Color(144, 92, 52));
        body.fill(new Ellipse2D.Double(-length * 0.56, -bodyHeight * 0.48, bodyHeight * 0.92, bodyHeight * 0.88));

        body.setColor(new Color(168, 104, 59));
        body.fill(new Ellipse2D.Double(length * 0.13, -bodyHeight * 0.54, bodyHeight * 1.02, bodyHeight * 0.96));

        body.setColor(highlight);
        body.fill(new RoundRectangle2D.Double(-length * 0.10, -bodyHeight * 0.42, length * 0.52, bodyHeight * 0.28, bodyHeight * 0.3, bodyHeight * 0.3));

        body.setColor(new Color(241, 214, 172));
        body.fill(new Ellipse2D.Double(-length * 0.20, bodyHeight * 0.02, length * 0.36, bodyHeight * 0.30));

        body.dispose();
    }

    private void drawNeck(Graphics2D g2, Point2D.Double shoulder, Point2D.Double headCenter, double scale, Color coat) {
        Stroke old = g2.getStroke();
        g2.setStroke(new BasicStroke((float) (16 * scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(62, 42, 26, 70));
        g2.draw(new Line2D.Double(shoulder.x + 2, shoulder.y + 2, headCenter.x - 6 * scale + 2, headCenter.y + 12 * scale + 2));
        g2.setColor(coat);
        g2.draw(new Line2D.Double(shoulder.x, shoulder.y, headCenter.x - 6 * scale, headCenter.y + 12 * scale));
        g2.setStroke(old);
    }

    private void drawTail(Graphics2D g2, Point2D.Double hip, double angle, double scale) {
        Stroke old = g2.getStroke();
        g2.setStroke(new BasicStroke((float) (7 * scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(158, 102, 57));
        Point2D.Double tailTip = polar(hip, 42 * scale, angle);
        g2.draw(new Line2D.Double(hip, tailTip));
        g2.setStroke(old);
    }

    private void drawHead(Graphics2D g2, Point2D.Double center, double angle, double scale) {
        Graphics2D head = (Graphics2D) g2.create();
        head.translate(center.x, center.y);
        head.rotate(Math.toRadians(angle));
        head.scale(scale, scale);

        Color coat = new Color(182, 116, 66);
        Color coatDark = new Color(127, 78, 43);
        Color cream = new Color(244, 216, 168);

        Path2D ear = new Path2D.Double();
        ear.moveTo(-26, -8);
        ear.curveTo(-40, -16, -42, 10, -22, 26);
        ear.curveTo(-8, 18, -8, -6, -26, -8);
        head.setColor(coatDark);
        head.fill(ear);
        head.fillOval(-6, -24, 14, 18);

        head.setColor(coatDark);
        head.fillOval(-18, -24, 44, 34);

        head.setColor(coat);
        head.fillOval(-28, -22, 58, 40);

        Path2D muzzle = new Path2D.Double();
        muzzle.moveTo(10, -8);
        muzzle.curveTo(30, -16, 50, -12, 62, -1);
        muzzle.curveTo(55, 13, 33, 18, 12, 12);
        muzzle.closePath();
        head.fill(muzzle);

        head.setColor(cream);
        head.fillOval(8, -1, 34, 14);
        head.fillOval(-6, 6, 18, 10);

        head.setColor(coatDark);
        head.fillOval(46, -2, 9, 9);
        head.drawArc(-4, -16, 16, 8, 180, 160);

        head.setColor(Color.BLACK);
        head.fillOval(-3, -4, 6, 6);
        head.fillOval(-1, -2, 2, 2);
        head.drawArc(18, 4, 16, 10, 205, 105);
        head.drawLine(24, 7, 40, 9);
        head.drawLine(24, 9, 38, 13);

        head.setColor(new Color(255, 183, 176));
        head.fillOval(-7, 7, 10, 7);

        head.setColor(new Color(46, 108, 185));
        head.fillRoundRect(-24, 14, 18, 8, 6, 6);
        head.setColor(new Color(244, 214, 88));
        head.fillOval(-10, 15, 7, 7);

        drawHat(head);
        head.dispose();
    }

    private void drawHat(Graphics2D g2) {
        switch (equippedHat) {
            case HEADBAND:
                g2.setColor(new Color(204, 56, 74));
                g2.fillRoundRect(-30, -16, 44, 8, 8, 8);
                g2.fillRect(8, -11, 10, 16);
                break;
            case CAP:
                g2.setColor(new Color(246, 96, 94));
                g2.fillArc(-28, -34, 48, 28, 0, 180);
                g2.fillRoundRect(-16, -14, 26, 7, 7, 7);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(-11, -27, 14, 7, 5, 5);
                break;
            case CROWN:
                Path2D crown = new Path2D.Double();
                crown.moveTo(-22, -12);
                crown.lineTo(-16, -34);
                crown.lineTo(-4, -18);
                crown.lineTo(6, -36);
                crown.lineTo(18, -12);
                crown.closePath();
                g2.setColor(new Color(244, 197, 57));
                g2.fill(crown);
                g2.fillRoundRect(-22, -14, 40, 8, 5, 5);
                g2.setColor(new Color(245, 91, 113));
                g2.fillOval(-8, -23, 8, 8);
                break;
            default:
                break;
        }
    }

    private void drawBarbell(Graphics2D g2, double centerX, double centerY, double width, int power, double gripGap, int stageWidth) {
        Stroke old = g2.getStroke();

        double clampedWidth = Math.min(stageWidth * (hasOlympicBar ? 0.95 : 0.62), width);
        double left = centerX - clampedWidth / 2.0;
        double right = centerX + clampedWidth / 2.0;

        g2.setColor(new Color(70, 74, 85, 70));
        g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Line2D.Double(left + 2, centerY + 4, right + 2, centerY + 4));

        g2.setColor(new Color(189, 195, 205));
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Line2D.Double(left, centerY, right, centerY));

        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(145, 150, 160));
        g2.draw(new Line2D.Double(centerX - gripGap, centerY, centerX - gripGap + 10, centerY));
        g2.draw(new Line2D.Double(centerX + gripGap - 10, centerY, centerX + gripGap, centerY));

        int plates = Math.max(1, power / 2);
        if (hasOlympicBar) {
            plates += power / 3;
        }

        double plateSpan = Math.max(0, (clampedWidth / 2.0) - gripGap - 20);
        int maxPlatesByWidth = Math.max(1, (int) (plateSpan / 10.0));
        plates = Math.min(plates, maxPlatesByWidth);

        for (int i = 0; i < plates; i++) {
            double plateHeight = 34 + (i % 3) * 12;
            double plateWidth = 8;
            double offset = i * 9;
            Color plateColor = i % 2 == 0 ? new Color(67, 81, 116) : new Color(221, 85, 105);

            g2.setColor(new Color(44, 45, 55, 60));
            g2.fill(new RoundRectangle2D.Double(left - 6 - offset + 1, centerY - plateHeight / 2 + 3, plateWidth, plateHeight, 4, 4));
            g2.fill(new RoundRectangle2D.Double(right - 2 + offset + 1, centerY - plateHeight / 2 + 3, plateWidth, plateHeight, 4, 4));

            g2.setColor(plateColor);
            g2.fill(new RoundRectangle2D.Double(left - 6 - offset, centerY - plateHeight / 2, plateWidth, plateHeight, 4, 4));
            g2.fill(new RoundRectangle2D.Double(right - 2 + offset, centerY - plateHeight / 2, plateWidth, plateHeight, 4, 4));
        }

        g2.setStroke(old);
    }

    private class StagePanel extends JPanel {
        StagePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();

            drawBackdrop(g2, w, h);
            drawIsland(g2, w, h);
            drawDecor(g2, w, h);
            drawLiftPose(g2, w, h);
            drawOverlay(g2, w, h);

            g2.dispose();
        }

        private void drawBackdrop(Graphics2D g2, int w, int h) {
            Paint old = g2.getPaint();

            if (highContrastMode) {
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h, new Color(220, 220, 220)));
                g2.fillRect(0, 0, w, h);
                g2.setPaint(old);
                return;
            }

            switch (equippedIsland) {
                case SUNSET:
                    g2.setPaint(new GradientPaint(0, 0, new Color(32, 34, 40), 0, h, new Color(58, 42, 36)));
                    break;
                case GYM_PIER:
                    g2.setPaint(new GradientPaint(0, 0, new Color(159, 204, 255), 0, h, new Color(243, 229, 195)));
                    break;
                case NEON_FEST:
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 131, 113), 0, h, new Color(109, 84, 189)));
                    break;
            }
            g2.fillRect(0, 0, w, h);

            if (equippedIsland == IslandStyle.SUNSET) {
                g2.setColor(new Color(235, 235, 232, 46));
                g2.fillRoundRect(0, 78, w, 150, 0, 0);
                g2.setColor(new Color(220, 54, 74));
                g2.fillRoundRect(0, 88, w, 16, 0, 0);
                g2.setColor(new Color(42, 42, 48));
                g2.fillRoundRect(70, 110, 160, 96, 12, 12);
                g2.fillRoundRect(w - 230, 110, 160, 96, 12, 12);
                g2.setColor(new Color(248, 239, 190));
                g2.fillOval(118, 132, 18, 18);
                g2.fillOval(160, 132, 18, 18);
                g2.fillOval(w - 182, 132, 18, 18);
                g2.fillOval(w - 140, 132, 18, 18);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(0, 0, w, 72, 0, 0);
            } else if (equippedIsland == IslandStyle.NEON_FEST) {
                g2.setColor(new Color(255, 244, 164, 170));
                g2.fillOval((int) (w * 0.72), 30, 95, 95);
                drawLightBeam(g2, w * 0.18, 0, w * 0.45, h * 0.54, new Color(255, 120, 173, 45));
                drawLightBeam(g2, w * 0.88, 0, w * 0.60, h * 0.56, new Color(83, 243, 255, 45));
            }

            if (equippedIsland != IslandStyle.SUNSET && !simplifiedStageMode) {
                drawCloud(g2, 90 + Math.sin(ambientTime * 0.5) * 18, 68, 1.0);
                drawCloud(g2, 290 + Math.sin(ambientTime * 0.6 + 0.8) * 24, 120, 1.25);
                drawCloud(g2, 540 + Math.sin(ambientTime * 0.45 + 1.7) * 14, 84, 0.9);
            }

            g2.setPaint(old);
        }

        private void drawLightBeam(Graphics2D g2, double x1, double y1, double x2, double y2, Color color) {
            Path2D beam = new Path2D.Double();
            beam.moveTo(x1 - 18, y1);
            beam.lineTo(x1 + 18, y1);
            beam.lineTo(x2 + 50, y2);
            beam.lineTo(x2 - 50, y2);
            beam.closePath();
            g2.setColor(color);
            g2.fill(beam);
        }

        private void drawCloud(Graphics2D g2, double x, double y, double scale) {
            g2.setColor(new Color(255, 255, 255, 215));
            g2.fillOval((int) x, (int) y, (int) (60 * scale), (int) (28 * scale));
            g2.fillOval((int) (x + 24 * scale), (int) (y - 14 * scale), (int) (40 * scale), (int) (34 * scale));
            g2.fillOval((int) (x + 48 * scale), (int) y, (int) (52 * scale), (int) (28 * scale));
        }

        private void drawIsland(Graphics2D g2, int w, int h) {
            int waterTop = (int) (h * 0.65);

            if (equippedIsland == IslandStyle.SUNSET) {
                int platformTop = (int) (h * 0.73);
                g2.setColor(highContrastMode ? Color.WHITE : new Color(25, 26, 31));
                g2.fillRect(0, platformTop - 28, w, h - platformTop + 28);

                g2.setColor(highContrastMode ? Color.BLACK : new Color(52, 56, 66));
                g2.fillRect(0, platformTop - 28, w, 22);

                g2.setColor(highContrastMode ? new Color(230, 230, 230) : new Color(119, 84, 55));
                g2.fillRoundRect(72, platformTop - 8, w - 144, 128, 24, 24);
                g2.setColor(highContrastMode ? new Color(120, 120, 120) : new Color(151, 109, 72));
                int boardCount = simplifiedStageMode ? 6 : 12;
                for (int i = 0; i < boardCount; i++) {
                    g2.fillRoundRect(86 + i * 48, platformTop + 6, 32, 102, 8, 8);
                }

                g2.setColor(highContrastMode ? Color.BLACK : new Color(38, 40, 47));
                g2.fillRoundRect(120, platformTop + 26, w - 240, 72, 18, 18);
                g2.setColor(highContrastMode ? Color.WHITE : new Color(87, 92, 104));
                g2.fillRoundRect(150, platformTop + 42, w - 300, 10, 8, 8);
                return;
            }

            g2.setColor(new Color(78, 185, 227));
            g2.fillRect(0, waterTop, w, h - waterTop);
            g2.setColor(new Color(149, 234, 255, 130));
            g2.fillRect(0, waterTop + 15, w, 24);

            if (hasPoolFloat) {
                double drift = Math.sin(ambientTime * 0.8) * 10.0;
                g2.setColor(new Color(245, 115, 148));
                g2.fillOval((int) (w * 0.14 + drift), waterTop + 26, 60, 24);
                g2.setColor(new Color(254, 232, 240));
                g2.fillOval((int) (w * 0.14 + 10 + drift), waterTop + 32, 40, 12);
            }

            if (equippedIsland == IslandStyle.GYM_PIER) {
                g2.setColor(new Color(132, 92, 56));
                g2.fillRoundRect(70, waterTop - 36, w - 140, 145, 30, 30);
                g2.setColor(new Color(163, 118, 72));
                for (int i = 0; i < 14; i++) {
                    g2.fillRoundRect(85 + i * 44, waterTop - 28, 30, 129, 8, 8);
                }
            } else {
                g2.setColor(new Color(235, 212, 145));
                g2.fillOval(60, waterTop - 46, w - 120, 170);
                g2.setColor(new Color(242, 225, 167));
                g2.fillOval(98, waterTop - 16, w - 196, 112);
            }

            g2.setColor(new Color(111, 172, 94));
            for (int i = 0; i < 10; i++) {
                int x = 90 + i * 58;
                g2.fillRect(x, waterTop + 30, 4, 18);
                g2.fillOval(x - 4, waterTop + 22, 12, 12);
            }
        }

        private void drawDecor(Graphics2D g2, int w, int h) {
            int groundY = (int) (h * 0.73);

            if (simplifiedStageMode) {
                if (equippedBuddy == BuddyType.CRAB) {
                    drawCrab(g2, 126, groundY + 18);
                } else if (equippedBuddy == BuddyType.GULL) {
                    drawSeagull(g2, w - 126, groundY + 4);
                } else if (equippedBuddy == BuddyType.OTTER) {
                    drawOtter(g2, 132, groundY + 10);
                }
                return;
            }

            if (equippedIsland == IslandStyle.SUNSET) {
                drawPlateStack(g2, 92, groundY + 6);
                drawPlateStack(g2, w - 142, groundY + 6);
                g2.setColor(new Color(226, 63, 84));
                g2.fillRoundRect(88, 118, 140, 18, 10, 10);
                g2.fillRoundRect(w - 228, 118, 140, 18, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
                g2.drawString("SBD CLUB", 122, 131);
                g2.drawString("POWER HOUSE", w - 208, 131);
            }

            if (hasParasol) {
                g2.setColor(new Color(253, 119, 131));
                g2.fillArc(90, groundY - 110, 120, 74, 0, 180);
                g2.setColor(new Color(245, 239, 221));
                g2.fillArc(102, groundY - 104, 96, 58, 0, 180);
                g2.setColor(new Color(129, 89, 61));
                g2.fillRect(147, groundY - 76, 6, 92);
            }

            if (hasLanterns) {
                g2.setColor(new Color(103, 74, 51));
                g2.drawArc(120, 50, w - 240, 34, 0, -180);
                int lanternCount = 7;
                for (int i = 0; i < lanternCount; i++) {
                    int x = 140 + i * ((w - 280) / (lanternCount - 1));
                    g2.setColor(new Color(250, 235, 174, 180));
                    g2.fillOval(x - 12, 66, 24, 24);
                    g2.setColor(new Color(250, 190, 76));
                    g2.fillOval(x - 8, 70, 16, 16);
                }
            }

            if (hasTrophyRack) {
                int x = w - 175;
                int y = groundY - 70;
                g2.setColor(new Color(129, 89, 52));
                g2.fillRoundRect(x, y, 95, 72, 14, 14);
                g2.setColor(new Color(244, 199, 71));
                g2.fillOval(x + 14, y + 12, 20, 20);
                g2.fillOval(x + 38, y + 8, 24, 24);
                g2.fillOval(x + 68, y + 16, 14, 14);
            }

            switch (equippedBuddy) {
                case CRAB:
                    drawCrab(g2, 126, groundY + 18);
                    break;
                case GULL:
                    drawSeagull(g2, w - 126, groundY + 4);
                    break;
                case OTTER:
                    drawOtter(g2, 132, groundY + 10);
                    break;
                default:
                    break;
            }
        }

        private void drawPlateStack(Graphics2D g2, int x, int y) {
            g2.setColor(new Color(62, 66, 76));
            g2.fillRoundRect(x, y - 58, 10, 64, 8, 8);
            Color[] plates = {
                new Color(74, 88, 121),
                new Color(219, 85, 104),
                new Color(244, 190, 73)
            };
            for (int i = 0; i < plates.length; i++) {
                g2.setColor(plates[i]);
                g2.fillRoundRect(x - 18 + i * 2, y - 16 - i * 16, 42, 10, 8, 8);
            }
        }

        private void drawCrab(Graphics2D g2, int x, int y) {
            g2.setColor(new Color(237, 96, 83));
            g2.fillOval(x, y, 32, 22);
            g2.fillOval(x - 6, y + 8, 10, 8);
            g2.fillOval(x + 28, y + 8, 10, 8);
            g2.fillOval(x + 6, y - 7, 6, 6);
            g2.fillOval(x + 20, y - 7, 6, 6);
            g2.setColor(Color.BLACK);
            g2.fillOval(x + 8, y - 5, 2, 2);
            g2.fillOval(x + 22, y - 5, 2, 2);
        }

        private void drawSeagull(Graphics2D g2, int x, int y) {
            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, 34, 22);
            g2.fillOval(x + 20, y - 10, 18, 18);
            g2.setColor(new Color(255, 174, 61));
            g2.fillPolygon(new int[]{x + 35, x + 46, x + 35}, new int[]{y - 1, y + 3, y + 7}, 3);
            g2.setColor(Color.BLACK);
            g2.fillOval(x + 28, y - 4, 3, 3);
            g2.setColor(new Color(186, 133, 61));
            g2.fillRect(x + 7, y + 18, 2, 10);
            g2.fillRect(x + 18, y + 18, 2, 10);
        }

        private void drawOtter(Graphics2D g2, int x, int y) {
            g2.setColor(new Color(154, 102, 59));
            g2.fillRoundRect(x, y, 58, 26, 18, 18);
            g2.fillOval(x + 42, y - 8, 24, 24);
            g2.setColor(new Color(242, 220, 191));
            g2.fillOval(x + 47, y - 1, 14, 10);
            g2.setColor(Color.BLACK);
            g2.fillOval(x + 54, y + 1, 3, 3);
            g2.fillRoundRect(x + 12, y - 10, 18, 10, 5, 5);
            g2.setColor(new Color(70, 70, 78));
            g2.fillRoundRect(x + 10, y - 18, 24, 12, 4, 4);
        }

        private void drawLiftPose(Graphics2D g2, int w, int h) {
            double scale = getDogScale();
            double stride = getCardioStride();
            if (cardioSelected) {
                drawCardioPose(g2, w, h, stride, scale);
                return;
            }
            double rep = getAnimatedLiftPosition(selectedLift) * (reduceMotionMode ? 0.38 : 1.0);

            switch (selectedLift) {
                case BENCH:
                    drawBenchPose(g2, w, h, rep, stride, scale);
                    break;
                case DEADLIFT:
                    drawDeadliftPose(g2, w, h, rep, stride, scale);
                    break;
                case SQUAT:
                default:
                    drawSquatPose(g2, w, h, rep, stride, scale);
                    break;
            }
        }

        private void drawCardioPose(Graphics2D g2, int w, int h, double stride, double scale) {
            double baseX = w * 0.49;
            double groundY = h * 0.79;
            double deckY = groundY - 8 * scale;

            drawTreadmill(g2, baseX, deckY, scale);

            double strideA = stride * 11.0 * scale;
            double strideB = -stride * 11.0 * scale;
            double bounce = Math.abs(stride) * 4.0 * scale;

            Point2D.Double hip = point(baseX - 38 * scale, groundY - 92 * scale + bounce);
            Point2D.Double shoulder = point(baseX + 32 * scale, groundY - 106 * scale + bounce);
            Point2D.Double headCenter = point(shoulder.x + 60 * scale, shoulder.y - 14 * scale + stride * 2.0 * scale);

            Point2D.Double backKnee = point(baseX - 64 * scale + strideB * 0.35, hip.y + 34 * scale);
            Point2D.Double backFoot = point(baseX - 72 * scale + strideB, deckY);
            Point2D.Double frontKnee = point(baseX + 6 * scale + strideA * 0.28, hip.y + 34 * scale);
            Point2D.Double frontFoot = point(baseX + 60 * scale + strideA, deckY);

            Point2D.Double backElbow = point(shoulder.x - 8 * scale + strideA * 0.22, shoulder.y + 26 * scale);
            Point2D.Double backPaw = point(baseX + 2 * scale + strideA, deckY - 2 * scale);
            Point2D.Double frontElbow = point(shoulder.x + 28 * scale + strideB * 0.22, shoulder.y + 28 * scale);
            Point2D.Double frontPaw = point(baseX + 86 * scale + strideB, deckY - 2 * scale);

            g2.setColor(new Color(55, 61, 70, 70));
            g2.fillOval((int) (baseX - 120 * scale), (int) (groundY - 10), (int) (240 * scale), (int) (26 * scale));

            Color coat = new Color(182, 118, 67);
            Color highlight = new Color(229, 169, 114);
            drawTail(g2, point(hip.x - 32 * scale, hip.y - 4 * scale), 198 + stride * 12.0, scale);
            drawLimb(g2, hip, backKnee, backFoot, (float) (15 * scale), coat);
            drawLimb(g2, hip, frontKnee, frontFoot, (float) (15 * scale), coat);
            drawTorso(g2, hip, shoulder, 36 * scale, coat, highlight);
            drawNeck(g2, shoulder, headCenter, scale, coat);
            drawLimb(g2, shoulder, backElbow, backPaw, (float) (12 * scale), coat);
            drawLimb(g2, shoulder, frontElbow, frontPaw, (float) (12 * scale), coat);
            drawHead(g2, headCenter, -6 + stride * 6.0, scale);

            g2.setColor(new Color(255, 255, 255, 85));
            for (int i = 0; i < 3; i++) {
                int x = (int) (baseX - 120 * scale - i * 22);
                int y = (int) (groundY - 118 * scale + i * 14);
                g2.drawLine(x, y, x + 20, y);
            }
        }

        private void drawSquatPose(Graphics2D g2, int w, int h, double rep, double stride, double scale) {
            double baseX = w * 0.50;
            double groundY = h * 0.79;
            double idle = stride * 1.3;

            double hipY = groundY - (118 - rep * 50) * scale + idle;
            Point2D.Double hip = point(baseX - 42 * scale, hipY);
            Point2D.Double shoulder = point(baseX + 30 * scale, hipY - (30 - rep * 12) * scale);
            Point2D.Double headCenter = point(shoulder.x + 62 * scale, shoulder.y - 16 * scale + rep * 4 * scale);
            Point2D.Double backKnee = point(baseX - 66 * scale, hipY + (38 + rep * 18) * scale);
            Point2D.Double frontKnee = point(baseX + 4 * scale + rep * 14 * scale, hipY + (40 + rep * 18) * scale);
            Point2D.Double backFoot = point(baseX - 70 * scale, groundY);
            Point2D.Double frontFoot = point(baseX + 56 * scale, groundY);

            double barCenterX = baseX + 8 * scale;
            double barY = shoulder.y - 18 * scale + rep * 24 * scale;
            double barWidth = 170 + liftPower.get(selectedLift) * (hasOlympicBar ? 16 : 9);
            drawRack(g2, baseX, groundY, groundY - 230 * scale, groundY - 148 * scale, scale, false);
            drawBarbell(g2, barCenterX, barY, barWidth, liftPower.get(selectedLift), 48 * scale, w);

            Point2D.Double backElbow = point(shoulder.x - 18 * scale, shoulder.y + 14 * scale);
            Point2D.Double backHand = point(barCenterX - 26 * scale, barY + 1);
            Point2D.Double frontElbow = point(shoulder.x + 10 * scale, shoulder.y + 10 * scale);
            Point2D.Double frontHand = point(barCenterX + 18 * scale, barY + 1);

            g2.setColor(new Color(66, 42, 24, 60));
            g2.fillOval((int) (baseX - 128 * scale), (int) (groundY - 10), (int) (256 * scale), (int) (34 * scale));

            Color coat = new Color(182, 118, 67);
            Color highlight = new Color(229, 169, 114);
            drawTail(g2, point(hip.x - 34 * scale, hip.y - 2 * scale), 196 + stride * 8.0, scale);
            drawLimb(g2, hip, backKnee, backFoot, (float) (16 * scale), coat);
            drawLimb(g2, hip, frontKnee, frontFoot, (float) (18 * scale), coat);
            drawLimb(g2, shoulder, backElbow, backHand, (float) (12 * scale), coat);
            drawTorso(g2, hip, shoulder, 38 * scale, coat, highlight);
            drawNeck(g2, shoulder, headCenter, scale, coat);
            drawLimb(g2, shoulder, frontElbow, frontHand, (float) (14 * scale), coat);
            drawHead(g2, headCenter, -6 + rep * 10, scale);
        }

        private void drawBenchPose(Graphics2D g2, int w, int h, double rep, double stride, double scale) {
            double baseX = w * 0.50;
            double groundY = h * 0.79;
            double idle = stride * 0.5;

            double benchY = groundY - 76 * scale;
            drawBench(g2, baseX, benchY, groundY, scale);

            Point2D.Double shoulder = point(baseX - 14 * scale, benchY - 9 * scale + idle);
            Point2D.Double hip = point(baseX + 94 * scale, benchY - 2 * scale + idle);
            Point2D.Double headCenter = point(baseX - 54 * scale, benchY - 42 * scale + idle);

            double barY = benchY - 82 * scale + rep * 54 * scale;
            double barWidth = 180 + liftPower.get(selectedLift) * (hasOlympicBar ? 14 : 8);
            drawRack(g2, baseX, groundY, benchY - 150 * scale, benchY - 82 * scale, scale, true);
            drawBarbell(g2, baseX + 12 * scale, barY, barWidth, liftPower.get(selectedLift), 34 * scale, w);

            Point2D.Double leftShoulder = point(baseX - 6 * scale, benchY - 14 * scale + idle);
            Point2D.Double rightShoulder = point(baseX + 26 * scale, benchY - 14 * scale + idle);
            Point2D.Double leftElbow = point(baseX - 20 * scale, benchY - 54 * scale + rep * 38 * scale);
            Point2D.Double rightElbow = point(baseX + 42 * scale, benchY - 52 * scale + rep * 38 * scale);
            Point2D.Double leftHand = point(baseX - 18 * scale, barY + 2);
            Point2D.Double rightHand = point(baseX + 38 * scale, barY + 2);

            Point2D.Double leftKnee = point(baseX + 44 * scale, benchY + 30 * scale);
            Point2D.Double rightKnee = point(baseX + 96 * scale, benchY + 24 * scale);
            Point2D.Double leftFoot = point(baseX + 24 * scale, groundY);
            Point2D.Double rightFoot = point(baseX + 102 * scale, groundY);

            g2.setColor(new Color(66, 42, 24, 50));
            g2.fillOval((int) (baseX - 128 * scale), (int) (groundY - 5), (int) (244 * scale), (int) (28 * scale));

            Color coat = new Color(182, 118, 67);
            Color highlight = new Color(229, 169, 114);
            drawTail(g2, point(hip.x + 34 * scale, hip.y + 3 * scale), 18 + stride * 6.0, scale);
            drawLimb(g2, hip, leftKnee, leftFoot, (float) (13 * scale), coat);
            drawLimb(g2, hip, rightKnee, rightFoot, (float) (13 * scale), coat);
            drawTorso(g2, hip, shoulder, 34 * scale, coat, highlight);
            drawNeck(g2, shoulder, headCenter, scale, coat);
            drawLimb(g2, leftShoulder, leftElbow, leftHand, (float) (13 * scale), coat);
            drawLimb(g2, rightShoulder, rightElbow, rightHand, (float) (13 * scale), coat);
            drawHead(g2, headCenter, -82, scale);
        }

        private void drawDeadliftPose(Graphics2D g2, int w, int h, double rep, double stride, double scale) {
            double baseX = w * 0.50;
            double groundY = h * 0.79;
            double idle = stride * 0.6;

            double hipHeight = groundY - (92 + rep * 42) * scale + idle;
            Point2D.Double hip = point(baseX - 32 * scale, hipHeight);
            Point2D.Double shoulder = point(baseX + 36 * scale, hipHeight - (18 + rep * 20) * scale);
            Point2D.Double headCenter = point(shoulder.x + 60 * scale, shoulder.y - 6 * scale - rep * 2 * scale);

            double barY = groundY - 8 * scale - rep * 88 * scale;
            double barWidth = 184 + liftPower.get(selectedLift) * (hasOlympicBar ? 18 : 10);
            drawBarbell(g2, baseX + 10 * scale, barY, barWidth, liftPower.get(selectedLift), 44 * scale, w);

            Point2D.Double leftHand = point(baseX - 12 * scale, barY + 1);
            Point2D.Double rightHand = point(baseX + 26 * scale, barY + 1);
            Point2D.Double leftElbow = point(baseX - 8 * scale, hip.y + (18 - rep * 4) * scale);
            Point2D.Double rightElbow = point(baseX + 24 * scale, hip.y + (16 - rep * 4) * scale);

            Point2D.Double backKnee = point(baseX - 54 * scale, hip.y + (34 - rep * 16) * scale);
            Point2D.Double frontKnee = point(baseX + 4 * scale, hip.y + (38 - rep * 16) * scale);
            Point2D.Double backFoot = point(baseX - 62 * scale, groundY);
            Point2D.Double frontFoot = point(baseX + 16 * scale, groundY);

            g2.setColor(new Color(66, 42, 24, 55));
            g2.fillOval((int) (baseX - 126 * scale), (int) (groundY - 8), (int) (248 * scale), (int) (30 * scale));

            Color coat = new Color(182, 118, 67);
            Color highlight = new Color(229, 169, 114);
            drawTail(g2, point(hip.x - 36 * scale, hip.y - 3 * scale), 198 + stride * 6.0, scale);
            drawLimb(g2, hip, backKnee, backFoot, (float) (16 * scale), coat);
            drawLimb(g2, hip, frontKnee, frontFoot, (float) (16 * scale), coat);
            drawTorso(g2, hip, shoulder, 36 * scale, coat, highlight);
            drawNeck(g2, shoulder, headCenter, scale, coat);
            drawLimb(g2, shoulder, leftElbow, leftHand, (float) (12 * scale), coat);
            drawLimb(g2, shoulder, rightElbow, rightHand, (float) (12 * scale), coat);
            drawHead(g2, headCenter, 10 - rep * 14, scale);
        }

        private void drawTreadmill(Graphics2D g2, double baseX, double deckY, double scale) {
            g2.setColor(new Color(49, 54, 61));
            g2.fillRoundRect((int) (baseX - 118 * scale), (int) deckY, (int) (236 * scale), (int) (22 * scale), 14, 14);
            g2.setColor(new Color(84, 91, 102));
            g2.fillRoundRect((int) (baseX - 104 * scale), (int) (deckY + 4 * scale), (int) (208 * scale), (int) (12 * scale), 12, 12);
            g2.setColor(new Color(128, 134, 148));
            g2.fillRoundRect((int) (baseX - 98 * scale), (int) (deckY - 86 * scale), (int) (10 * scale), (int) (88 * scale), 8, 8);
            g2.fillRoundRect((int) (baseX + 86 * scale), (int) (deckY - 86 * scale), (int) (10 * scale), (int) (88 * scale), 8, 8);
            g2.fillRoundRect((int) (baseX - 92 * scale), (int) (deckY - 90 * scale), (int) (182 * scale), (int) (8 * scale), 8, 8);
            g2.setColor(new Color(42, 46, 55));
            g2.fillRoundRect((int) (baseX + 52 * scale), (int) (deckY - 124 * scale), (int) (48 * scale), (int) (36 * scale), 8, 8);
            g2.setColor(new Color(83, 243, 255));
            g2.fillRoundRect((int) (baseX + 58 * scale), (int) (deckY - 116 * scale), (int) (36 * scale), (int) (10 * scale), 4, 4);
        }

        private void drawBench(Graphics2D g2, double baseX, double benchY, double groundY, double scale) {
            g2.setColor(new Color(97, 67, 49));
            g2.fillRoundRect((int) (baseX - 110 * scale), (int) benchY, (int) (180 * scale), (int) (20 * scale), 18, 18);
            g2.fillRoundRect((int) (baseX - 82 * scale), (int) (benchY + 16 * scale), (int) (10 * scale), (int) (groundY - benchY - 2 * scale), 10, 10);
            g2.fillRoundRect((int) (baseX + 32 * scale), (int) (benchY + 16 * scale), (int) (10 * scale), (int) (groundY - benchY - 2 * scale), 10, 10);

            g2.setColor(new Color(196, 77, 87));
            g2.fillRoundRect((int) (baseX - 104 * scale), (int) (benchY - 12 * scale), (int) (168 * scale), (int) (16 * scale), 12, 12);
        }

        private void drawRack(Graphics2D g2, double baseX, double groundY, double topY, double cupY, double scale, boolean benchRack) {
            int leftX = (int) (baseX - 138 * scale);
            int rightX = (int) (baseX + 116 * scale);
            int upperY = (int) topY;
            int bottomY = (int) groundY;
            int cup = (int) cupY;

            g2.setColor(new Color(58, 62, 72, 80));
            g2.fillRoundRect(leftX + 3, upperY + 4, (int) (14 * scale), bottomY - upperY + 10, 10, 10);
            g2.fillRoundRect(rightX + 3, upperY + 4, (int) (14 * scale), bottomY - upperY + 10, 10, 10);

            g2.setColor(new Color(142, 149, 165));
            g2.fillRoundRect(leftX, upperY, (int) (14 * scale), bottomY - upperY + 10, 10, 10);
            g2.fillRoundRect(rightX, upperY, (int) (14 * scale), bottomY - upperY + 10, 10, 10);

            int armWidth = (int) (34 * scale);
            int armHeight = (int) (8 * scale);
            g2.setColor(new Color(124, 131, 148));
            g2.fillRoundRect(leftX - armWidth + 10, cup, armWidth, armHeight, 8, 8);
            g2.fillRoundRect(rightX + 4, cup, armWidth, armHeight, 8, 8);

            if (!benchRack) {
                g2.setColor(new Color(92, 99, 114));
                int safetyY = cup + (int) (34 * scale);
                g2.fillRoundRect(leftX + 4, safetyY, (int) (96 * scale), armHeight, 8, 8);
                g2.fillRoundRect(rightX - (int) (82 * scale), safetyY, (int) (96 * scale), armHeight, 8, 8);
            }
        }

        private void drawOverlay(Graphics2D g2, int w, int h) {
            g2.setColor(highContrastMode ? new Color(0, 0, 0, 210) : new Color(22, 28, 40, 35));
            g2.fillRoundRect(16, 14, 220, 72, 18, 18);
            g2.setColor(highContrastMode ? Color.WHITE : Color.WHITE);
            g2.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            g2.drawString((cardioSelected ? "CARDIO" : selectedLift.label.toUpperCase(Locale.US)), 32, 37);
            g2.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
            if (cardioSelected) {
                g2.drawString("Click " + formatCompact(calculateCardioClickGains()) + " gains", 32, 56);
            } else {
                g2.drawString("Load " + formatCompact(calculateLiftGains(selectedLift)) + " gains/rep", 32, 56);
            }
            g2.drawString("Cardio " + ONE_DECIMAL.format(getCardioGainRate()) + " gains/s", 32, 72);

            if (System.currentTimeMillis() < floatingGainStartedAt + 900L) {
                double progress = (System.currentTimeMillis() - floatingGainStartedAt) / 900.0;
                int alpha = (int) (255 * Math.max(0.0, 1.0 - progress));
                g2.setColor(new Color(255, 255, 255, alpha));
                g2.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
                g2.drawString(floatingGainText, (int) (w * 0.50 - 70), (int) (h * 0.22 - progress * 28));
            }

            if (toastMessage.length() > 0 && System.currentTimeMillis() < toastExpiresAt) {
                FontMetrics metrics = g2.getFontMetrics(new Font(Font.DIALOG, Font.BOLD, 14));
                int toastWidth = metrics.stringWidth(toastMessage) + 28;
                int x = (w - toastWidth) / 2;
                int y = h - 64;
                g2.setColor(highContrastMode ? Color.BLACK : new Color(22, 31, 48, 180));
                g2.fillRoundRect(x, y, toastWidth, 34, 18, 18);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
                g2.drawString(toastMessage, x + 14, y + 22);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WienerGymIdle());
    }
}
