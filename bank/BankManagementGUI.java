import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class BankManagementSystem extends JFrame {

    public static final Color COLOR_SIDEBAR     = new Color(15, 23, 42);     // Slate 900
    public static final Color COLOR_SIDEBAR_ACT = new Color(30, 41, 59);     // Slate 800
    public static final Color COLOR_PRIMARY     = new Color(37, 99, 235);    // Royal Blue
    public static final Color COLOR_SECONDARY   = new Color(16, 185, 129);   // Emerald Green
    public static final Color COLOR_DANGER      = new Color(225, 29, 72);    // Rose Red
    public static final Color COLOR_BACKGROUND  = new Color(241, 245, 249);  // Slate 100
    public static final Color COLOR_SURFACE     = Color.WHITE;
    public static final Color COLOR_TEXT_MAIN   = new Color(15, 23, 42);
    public static final Color COLOR_TEXT_MUTED  = new Color(100, 116, 139);

    public static final Color CYBER_BG          = new Color(5, 10, 20);      // Deep Space Blue
    public static final Color CYBER_CARD        = new Color(15, 23, 42);     // Dark Slate
    public static final Color CYBER_ACCENT      = new Color(6, 182, 212);    // Neon Cyan
    public static final Color CYBER_TEXT        = new Color(226, 232, 240);  // Off White

    // --- Typography ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    // --- State ---
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private BankService bankService;
    private User currentUser;

    // --- Panels ---
    private LoginPanel loginPanel;
    private SignupPanel signupPanel;
    private DashboardPanel dashboardPanel;

    public BankManagementSystem() {
        setTitle("Osryn Sovereign Bank - Cyber Enterprise");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        bankService = new BankService();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        signupPanel = new SignupPanel(this);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(signupPanel, "SIGNUP");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // --- Navigation Methods ---

    public void showLogin() {
        currentUser = null;
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showSignup() {
        cardLayout.show(mainPanel, "SIGNUP");
    }

    public void login(String username, String password) {
        User user = bankService.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            dashboardPanel = new DashboardPanel(this, currentUser);
            mainPanel.add(dashboardPanel, "DASHBOARD");
            cardLayout.show(mainPanel, "DASHBOARD");
        } else {
            showToast("Invalid Credentials", true);
        }
    }

    public void register(String name, String username, String password, String email, String phone) {
        User newUser = bankService.createAccount(name, username, password, email, phone);
        if (newUser != null) {
            JOptionPane.showMessageDialog(this, 
                "Identity Verified. Access Granted.\nAccount ID: " + newUser.getAccountId(), 
                "System Notification", JOptionPane.INFORMATION_MESSAGE);
            showLogin();
        } else {
            showToast("Username Unavailable", true);
        }
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Terminate Session?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            showLogin();
        }
    }
    
    public void showToast(String message, boolean isError) {
        JOptionPane.showMessageDialog(this, message, isError ? "System Error" : "Success", isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    public BankService getBankService() { return bankService; }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
            catch (Exception ignored) {}
            new BankManagementSystem().setVisible(true);
        });
    }

    // ==========================================
    //              DATA LAYER
    // ==========================================

    static class User {
        private String accountId, username, name, password, email, phone;
        private double balance;
        private List<Transaction> transactions;
        private List<Double> balanceHistory;
        private Date dateCreated;

        public User(String accountId, String username, String name, String password, String email, String phone) {
            this.accountId = accountId;
            this.username = username;
            this.name = name;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.balance = 0.0;
            this.transactions = new ArrayList<>();
            this.balanceHistory = new ArrayList<>();
            this.balanceHistory.add(0.0);
            this.dateCreated = new Date();
        }

        // Getters
        public String getAccountId() { return accountId; }
        public String getUsername() { return username; }
        public String getName() { return name; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
        public double getBalance() { return balance; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public Date getDateCreated() { return dateCreated; }
        public List<Transaction> getTransactions() { return transactions; }
        public List<Double> getBalanceHistory() { return balanceHistory; }

        public void updateBalance(double newBalance) {
            this.balance = newBalance;
            if (balanceHistory.size() > 20) balanceHistory.remove(0);
            balanceHistory.add(newBalance);
        }
        
        public void addTransaction(Transaction t) {
            transactions.add(0, t);
        }
    }

    static class Transaction {
        private String id, type, description;
        private double amount;
        private Date date;

        public Transaction(String type, double amount, String description) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.type = type;
            this.amount = amount;
            this.description = description;
            this.date = new Date();
        }
        public String getId() { return id; }
        public String getType() { return type; }
        public double getAmount() { return amount; }
        public String getDescription() { return description; }
        public Date getDate() { return date; }
    }

    static class BankService {
        private Map<String, User> users = new HashMap<>();

        public BankService() {
            User admin = createAccount("Admin User", "admin", "admin", "admin@osryn.bank", "000-0000");
            if(admin != null) {
                admin.updateBalance(12500.50);
                admin.addTransaction(new Transaction("Deposit", 12500.50, "Initial Funding"));
                admin.getBalanceHistory().clear();
                admin.getBalanceHistory().addAll(Arrays.asList(5000.0, 5200.0, 4800.0, 6000.0, 8500.0, 7000.0, 12500.50));
            }
        }

        public User createAccount(String name, String username, String pass, String email, String phone) {
            for (User u : users.values()) if (u.getUsername().equalsIgnoreCase(username)) return null;
            String accId = String.format("%09d", new Random().nextInt(1000000000));
            User u = new User(accId, username, name, pass, email, phone);
            users.put(accId, u);
            return u;
        }

        public User authenticate(String user, String pass) {
            return users.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(user) && u.getPassword().equals(pass))
                .findFirst().orElse(null);
        }

        public boolean transfer(User sender, String targetId, double amount) {
            User target = users.get(targetId);
            if (target == null || sender.getBalance() < amount) return false;
            
            sender.updateBalance(sender.getBalance() - amount);
            target.updateBalance(target.getBalance() + amount);
            
            sender.addTransaction(new Transaction("Transfer Out", -amount, "To: " + target.getName()));
            target.addTransaction(new Transaction("Transfer In", amount, "From: " + sender.getName()));
            return true;
        }

        public void deposit(User u, double amt) {
            u.updateBalance(u.getBalance() + amt);
            u.addTransaction(new Transaction("Deposit", amt, "ATM / Cash"));
        }

        public boolean withdraw(User u, double amt) {
            if (u.getBalance() < amt) return false;
            u.updateBalance(u.getBalance() - amt);
            u.addTransaction(new Transaction("Withdrawal", -amt, "ATM Withdrawal"));
            return true;
        }
        
        public boolean payBill(User u, String biller, double amt) {
            if (u.getBalance() < amt) return false;
            u.updateBalance(u.getBalance() - amt);
            u.addTransaction(new Transaction("Bill Payment", -amt, "To: " + biller));
            return true;
        }
    }

    // ==========================================
    //            CUSTOM UI COMPONENTS
    // ==========================================

    // Cyber Button for Auth Pages
    static class CyberButton extends JButton {
        public CyberButton(String text) {
            super(text);
            setFont(FONT_BOLD);
            setForeground(CYBER_BG);
            setBackground(CYBER_ACCENT);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(CYBER_ACCENT.brighter()); }
                public void mouseExited(MouseEvent e) { setBackground(CYBER_ACCENT); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0); // Sharp corners for cyber look
            super.paintComponent(g);
        }
    }

    static class CyberTextField extends JTextField {
        private String placeholder;
        public CyberTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setOpaque(false);
            setForeground(CYBER_TEXT);
            setCaretColor(CYBER_ACCENT);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(45, 55, 75), 1),
                new EmptyBorder(10, 15, 10, 15)
            ));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(20, 25, 40));
            g2.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(100, 116, 139));
                g2.setFont(getFont());
                g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        }
    }

    static class ModernButton extends JButton {
        private Color normalColor, hoverColor;
        private boolean isOutline = false;
        public ModernButton(String text, Color bg) { this(text, bg, false); }
        public ModernButton(String text, Color bg, boolean outline) {
            super(text);
            this.isOutline = outline;
            this.normalColor = bg;
            this.hoverColor = bg.brighter();
            setFont(FONT_BOLD);
            setForeground(outline ? bg : Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(normalColor); repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isOutline) {
                g2.setColor(getBackground() == null ? normalColor : getBackground());
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                setForeground(getBackground() == null ? normalColor : getBackground());
            } else {
                g2.setColor(getBackground() == null ? normalColor : getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                setForeground(Color.WHITE);
            }
            super.paintComponent(g);
        }
    }

    static class ModernTextField extends JTextField {
        private String placeholder;
        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_SURFACE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(COLOR_TEXT_MUTED);
                g2.setFont(getFont());
                g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        }
    }
    
    // ==========================================
    //            DASHBOARD GRAPH
    // ==========================================
    
    static class BalanceGraphPanel extends JPanel {
        private List<Double> history;
        public BalanceGraphPanel(List<Double> history) {
            this.history = history;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 180));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (history == null || history.size() < 2) return;
            int w = getWidth(), h = getHeight(), pad = 30;
            double min = Collections.min(history), max = Collections.max(history);
            if(min == max) { max += 100; min -= 100; }
            
            Path2D path = new Path2D.Double();
            double xStep = (double)(w - 2 * pad) / (history.size() - 1);
            
            for (int i = 0; i < history.size(); i++) {
                double val = history.get(i);
                double x = pad + i * xStep;
                double y = h - pad - ((val - min) / (max - min)) * (h - 2 * pad);
                if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
            }
            
            g2.setColor(COLOR_PRIMARY);
            g2.setStroke(new BasicStroke(3f));
            g2.draw(path);
            
            for (int i = 0; i < history.size(); i++) {
                double val = history.get(i);
                double x = pad + i * xStep;
                double y = h - pad - ((val - min) / (max - min)) * (h - 2 * pad);
                g2.setColor(COLOR_SURFACE);
                g2.fillOval((int)x-5, (int)y-5, 10, 10);
                g2.setColor(COLOR_PRIMARY);
                g2.drawOval((int)x-5, (int)y-5, 10, 10);
            }
        }
    }

    // ==========================================
    //            MAIN PANELS
    // ==========================================

    class LoginPanel extends JPanel {
        public LoginPanel(BankManagementSystem frame) {
            setLayout(new GridBagLayout());
            setBackground(CYBER_BG);

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CYBER_CARD);
            card.setBorder(new EmptyBorder(60, 60, 60, 60));
            // Cyber Border
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(40, 50, 70)),
                new EmptyBorder(50, 50, 50, 50)
            ));
            
            JLabel brand = new JLabel("OSRYN // SYSTEMS");
            brand.setFont(new Font("Consolas", Font.BOLD, 32));
            brand.setForeground(CYBER_ACCENT);
            brand.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sub = new JLabel("SECURE ACCESS TERMINAL");
            sub.setFont(new Font("Consolas", Font.PLAIN, 14));
            sub.setForeground(new Color(148, 163, 184));
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            CyberTextField userField = new CyberTextField("USERNAME");
            JPasswordField passField = new JPasswordField();
            passField.setBackground(new Color(20, 25, 40));
            passField.setForeground(CYBER_TEXT);
            passField.setCaretColor(CYBER_ACCENT);
            passField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(45, 55, 75), 1), new EmptyBorder(10, 15, 10, 15)));
            
            CyberButton loginBtn = new CyberButton("INITIATE LOGIN");
            JButton signupBtn = new JButton("NEW USER REGISTRATION");
            signupBtn.setFont(new Font("Consolas", Font.PLAIN, 12));
            signupBtn.setBorderPainted(false); 
            signupBtn.setContentAreaFilled(false);
            signupBtn.setForeground(CYBER_ACCENT);
            signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            Dimension dim = new Dimension(350, 45);
            userField.setMaximumSize(dim); passField.setMaximumSize(dim); loginBtn.setMaximumSize(dim);

            loginBtn.addActionListener(e -> frame.login(userField.getText(), new String(passField.getPassword())));
            signupBtn.addActionListener(e -> frame.showSignup());

            card.add(brand);
            card.add(Box.createVerticalStrut(5));
            card.add(sub);
            card.add(Box.createVerticalStrut(50));
            card.add(new JLabel("ID_KEY") {{ setForeground(CYBER_TEXT); setFont(new Font("Consolas", Font.PLAIN, 12)); }});
            card.add(Box.createVerticalStrut(5));
            card.add(userField);
            card.add(Box.createVerticalStrut(20));
            card.add(new JLabel("PASS_KEY") {{ setForeground(CYBER_TEXT); setFont(new Font("Consolas", Font.PLAIN, 12)); }});
            card.add(Box.createVerticalStrut(5));
            card.add(passField);
            card.add(Box.createVerticalStrut(40));
            card.add(loginBtn);
            card.add(Box.createVerticalStrut(15));
            card.add(signupBtn);

            add(card);
        }
        
        // Cyber Grid Background
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(15, 23, 42));
            for(int i=0; i<getWidth(); i+=40) g.drawLine(i, 0, i, getHeight());
            for(int i=0; i<getHeight(); i+=40) g.drawLine(0, i, getWidth(), i);
        }
    }

    class SignupPanel extends JPanel {
        public SignupPanel(BankManagementSystem frame) {
            setLayout(new GridBagLayout());
            setBackground(CYBER_BG);

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CYBER_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(40, 50, 70)),
                new EmptyBorder(40, 60, 40, 60)
            ));

            JLabel title = new JLabel("NEW IDENTITY");
            title.setFont(new Font("Consolas", Font.BOLD, 28));
            title.setForeground(CYBER_ACCENT);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            CyberTextField nameF = new CyberTextField("FULL NAME");
            CyberTextField userF = new CyberTextField("USERNAME");
            CyberTextField emailF = new CyberTextField("EMAIL ADDRESS");
            CyberTextField phoneF = new CyberTextField("CONTACT REF");
            JPasswordField passF = new JPasswordField();
            passF.setBackground(new Color(20, 25, 40));
            passF.setForeground(CYBER_TEXT);
            passF.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(45, 55, 75), 1), new EmptyBorder(10, 15, 10, 15)));

            Dimension d = new Dimension(350, 40);
            nameF.setMaximumSize(d); userF.setMaximumSize(d);
            emailF.setMaximumSize(d); phoneF.setMaximumSize(d); passF.setMaximumSize(d);

            CyberButton regBtn = new CyberButton("CREATE IDENTITY");
            regBtn.setMaximumSize(new Dimension(350, 45));
            regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton backBtn = new JButton("ABORT / RETURN");
            backBtn.setFont(new Font("Consolas", Font.PLAIN, 12));
            backBtn.setContentAreaFilled(false); backBtn.setBorderPainted(false);
            backBtn.setForeground(new Color(100, 116, 139));
            backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            regBtn.addActionListener(e -> {
                if(userF.getText().isEmpty() || new String(passF.getPassword()).isEmpty()) {
                    frame.showToast("Data Incomplete", true); return;
                }
                frame.register(nameF.getText(), userF.getText(), new String(passF.getPassword()), emailF.getText(), phoneF.getText());
            });
            backBtn.addActionListener(e -> frame.showLogin());

            card.add(title);
            card.add(Box.createVerticalStrut(30));
            card.add(nameF); card.add(Box.createVerticalStrut(10));
            card.add(userF); card.add(Box.createVerticalStrut(10));
            card.add(emailF); card.add(Box.createVerticalStrut(10));
            card.add(phoneF); card.add(Box.createVerticalStrut(10));
            card.add(passF); card.add(Box.createVerticalStrut(30));
            card.add(regBtn);
            card.add(Box.createVerticalStrut(10));
            card.add(backBtn);

            add(card);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(15, 23, 42));
            for(int i=0; i<getWidth(); i+=40) g.drawLine(i, 0, i, getHeight());
            for(int i=0; i<getHeight(); i+=40) g.drawLine(0, i, getWidth(), i);
        }
    }

    class DashboardPanel extends JPanel {
        private BankManagementSystem frame;
        private User user;
        private JPanel contentArea;
        private CardLayout contentLayout;
        private Map<String, JButton> navButtons = new HashMap<>();
        private String currentCard = "HOME";

        public DashboardPanel(BankManagementSystem frame, User user) {
            this.frame = frame;
            this.user = user;
            setLayout(new BorderLayout());

            // --- Sidebar ---
            JPanel sidebar = new JPanel();
            sidebar.setPreferredSize(new Dimension(280, getHeight()));
            sidebar.setBackground(COLOR_SIDEBAR);
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

            // Logo
            JPanel logoP = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
            logoP.setOpaque(false);
            JLabel logo = new JLabel("OSRYN BANK");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
            logo.setForeground(Color.WHITE);
            logoP.add(logo);
            sidebar.add(logoP);
            sidebar.add(Box.createVerticalStrut(10));

            // Expanded Nav Items - SEPARATE PAGES
            addNav(sidebar, "Dashboard", "HOME");
            sidebar.add(Box.createVerticalStrut(10));
            addLabel(sidebar, "OPERATIONS");
            addNav(sidebar, "Deposit Funds", "DEPOSIT");
            addNav(sidebar, "Withdraw Funds", "WITHDRAW");
            addNav(sidebar, "Transfer Money", "TRANSFER");
            addNav(sidebar, "Pay Bills", "BILLS");
            sidebar.add(Box.createVerticalStrut(10));
            addLabel(sidebar, "ACCOUNT");
            addNav(sidebar, "Security Settings", "SETTINGS");

            sidebar.add(Box.createVerticalGlue());
            
            JButton logout = new JButton("  Sign Out");
            logout.setForeground(COLOR_DANGER);
            logout.setFont(FONT_BOLD);
            logout.setBorder(new EmptyBorder(0, 30, 40, 0));
            logout.setContentAreaFilled(false);
            logout.setFocusPainted(false);
            logout.setAlignmentX(Component.LEFT_ALIGNMENT);
            logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logout.addActionListener(e -> frame.logout());
            sidebar.add(logout);

            // --- Content ---
            contentLayout = new CardLayout();
            contentArea = new JPanel(contentLayout);
            contentArea.setBackground(COLOR_BACKGROUND);

            // Init Views
            contentArea.add(new HomeView(user, contentLayout, contentArea), "HOME");
            contentArea.add(new OperationView(user, "DEPOSIT"), "DEPOSIT");
            contentArea.add(new OperationView(user, "WITHDRAW"), "WITHDRAW");
            contentArea.add(new TransferView(user), "TRANSFER");
            contentArea.add(new BillPayView(user), "BILLS");
            contentArea.add(new SettingsView(user), "SETTINGS");

            add(sidebar, BorderLayout.WEST);
            add(contentArea, BorderLayout.CENTER);
            
            highlightNav("HOME");
        }
        
        private void addLabel(JPanel p, String text) {
            JLabel l = new JLabel(text);
            l.setForeground(new Color(71, 85, 105));
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setBorder(new EmptyBorder(5, 30, 5, 0));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(l);
        }

        private void addNav(JPanel panel, String label, String cardName) {
            JButton btn = new JButton("  " + label);
            btn.setMaximumSize(new Dimension(280, 50));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setFont(FONT_BODY);
            btn.setForeground(new Color(148, 163, 184));
            btn.setBackground(COLOR_SIDEBAR);
            btn.setBorder(new EmptyBorder(12, 30, 12, 0));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btn.addActionListener(e -> {
                contentLayout.show(contentArea, cardName);
                highlightNav(cardName);
            });
            
            navButtons.put(cardName, btn);
            panel.add(btn);
        }
        
        private void highlightNav(String cardName) {
            if(navButtons.containsKey(currentCard)) {
                JButton old = navButtons.get(currentCard);
                old.setBackground(COLOR_SIDEBAR);
                old.setForeground(new Color(148, 163, 184));
                old.setOpaque(true);
            }
            currentCard = cardName;
            if(navButtons.containsKey(currentCard)) {
                JButton curr = navButtons.get(currentCard);
                curr.setBackground(COLOR_SIDEBAR_ACT);
                curr.setForeground(Color.WHITE);
                curr.setOpaque(true);
            }
        }
    }
    
    // --- Dashboard Views ---

    class HomeView extends JPanel {
        private JLabel balanceLbl, idLbl, statusLbl;
        private BalanceGraphPanel graphPanel;
        private User user;
        private DefaultTableModel transModel;

        public HomeView(User user, CardLayout cl, JPanel container) {
            this.user = user;
            setLayout(new BorderLayout());
            setBackground(COLOR_BACKGROUND);
            setBorder(new EmptyBorder(40, 50, 40, 50));
            
            // Add listener to auto-refresh when shown
            this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    updateData();
                }
            });

            // Top
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            JLabel greet = new JLabel("Overview");
            greet.setFont(FONT_TITLE);
            top.add(greet, BorderLayout.WEST);
            top.add(new JLabel(new SimpleDateFormat("MMMM dd, yyyy").format(new Date())), BorderLayout.EAST);

            // Stats
            JPanel stats = new JPanel(new GridLayout(1, 3, 30, 0));
            stats.setOpaque(false);
            stats.setPreferredSize(new Dimension(0, 150));
            
            // Create Labels References
            balanceLbl = new JLabel(); balanceLbl.setForeground(Color.WHITE); balanceLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
            idLbl = new JLabel(); idLbl.setForeground(Color.WHITE); idLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
            statusLbl = new JLabel("Active"); statusLbl.setForeground(Color.WHITE); statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));

            stats.add(createCard("Available Balance", balanceLbl, COLOR_PRIMARY));
            stats.add(createCard("Account ID", idLbl, COLOR_SIDEBAR));
            stats.add(createCard("Status", statusLbl, COLOR_SECONDARY));

            // Graph
            JPanel graphSection = new JPanel(new BorderLayout());
            graphSection.setBackground(COLOR_SURFACE);
            graphSection.setBorder(new EmptyBorder(20, 20, 20, 20));
            JLabel trendLbl = new JLabel("Financial Analytics");
            trendLbl.setFont(FONT_HEADER);
            
            graphPanel = new BalanceGraphPanel(user.getBalanceHistory());
            graphSection.add(trendLbl, BorderLayout.NORTH);
            graphSection.add(graphPanel, BorderLayout.CENTER);

            // Recent Transactions (Auto Refreshing)
            JPanel transPanel = new JPanel(new BorderLayout());
            transPanel.setBackground(COLOR_SURFACE);
            transPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
            
            String[] cols = {"Type", "Amount", "Description"};
            transModel = new DefaultTableModel(cols, 0);
            JTable table = new JTable(transModel);
            styleTable(table);
            
            JLabel transTitle = new JLabel("Recent Activity");
            transTitle.setFont(FONT_HEADER);
            transTitle.setBorder(new EmptyBorder(0,0,10,0));
            
            transPanel.add(transTitle, BorderLayout.NORTH);
            transPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Layout
            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setOpaque(false);
            center.add(stats);
            center.add(Box.createVerticalStrut(25));
            center.add(graphSection);
            center.add(Box.createVerticalStrut(25));
            center.add(transPanel);

            add(top, BorderLayout.NORTH);
            add(new JScrollPane(center), BorderLayout.CENTER);
            
            // Initial data load
            updateData();
        }

        private void updateData() {
            balanceLbl.setText(formatMoney(user.getBalance()));
            idLbl.setText(user.getAccountId());
            graphPanel.repaint();
            
            // Reload Table
            transModel.setRowCount(0);
            for(Transaction t : user.getTransactions()) {
                transModel.addRow(new Object[]{ t.getType(), formatMoney(t.getAmount()), t.getDescription() });
            }
        }

        private JPanel createCard(String title, JLabel valLbl, Color c) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(c);
            p.setBorder(new EmptyBorder(25, 25, 25, 25));
            
            JLabel t = new JLabel(title);
            t.setForeground(new Color(255,255,255,200));
            t.setFont(FONT_BODY);
            
            p.add(t, BorderLayout.NORTH);
            p.add(valLbl, BorderLayout.CENTER);
            return p;
        }
        
        private void styleTable(JTable table) {
            table.setRowHeight(35);
            table.setShowVerticalLines(false);
            table.setFont(FONT_BODY);
            table.setSelectionBackground(new Color(230, 240, 255));
            table.setSelectionForeground(COLOR_TEXT_MAIN);
            table.setGridColor(new Color(240,240,240));
            
            JTableHeader header = table.getTableHeader();
            header.setBackground(COLOR_SURFACE);
            header.setFont(FONT_BOLD);
            header.setForeground(COLOR_TEXT_MUTED);
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        }
    }

    // Generic View for simple Deposit/Withdraw
    class OperationView extends JPanel {
        public OperationView(User user, String type) {
            setLayout(new GridBagLayout());
            setBackground(COLOR_BACKGROUND);
            
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(COLOR_SURFACE);
            card.setBorder(new EmptyBorder(40, 40, 40, 40));
            card.setPreferredSize(new Dimension(500, 400));
            
            JLabel title = new JLabel(type.equals("DEPOSIT") ? "Deposit Funds" : "Withdraw Funds");
            title.setFont(FONT_TITLE);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel desc = new JLabel(type.equals("DEPOSIT") ? "Add money to your balance safely." : "Withdraw money from your account.");
            desc.setForeground(COLOR_TEXT_MUTED);
            desc.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            ModernTextField amountF = new ModernTextField("Enter Amount (BDT)");
            amountF.setMaximumSize(new Dimension(400, 45));
            
            ModernButton btn = new ModernButton(type.equals("DEPOSIT") ? "CONFIRM DEPOSIT" : "CONFIRM WITHDRAWAL", 
                type.equals("DEPOSIT") ? COLOR_SECONDARY : COLOR_DANGER);
            btn.setMaximumSize(new Dimension(400, 50));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            btn.addActionListener(e -> {
                try {
                    double val = Double.parseDouble(amountF.getText());
                    if(type.equals("DEPOSIT")) {
                        bankService.deposit(user, val);
                        showToast("Successfully Deposited " + formatMoney(val), false);
                    } else {
                        if(bankService.withdraw(user, val)) showToast("Successfully Withdrew " + formatMoney(val), false);
                        else showToast("Insufficient Funds", true);
                    }
                    amountF.setText("");
                } catch(Exception ex) { showToast("Invalid Amount", true); }
            });
            
            card.add(title);
            card.add(Box.createVerticalStrut(10));
            card.add(desc);
            card.add(Box.createVerticalStrut(40));
            card.add(new JLabel("Amount") {{ setFont(FONT_BOLD); }});
            card.add(Box.createVerticalStrut(5));
            card.add(amountF);
            card.add(Box.createVerticalStrut(40));
            card.add(btn);
            
            add(card);
        }
    }

    class TransferView extends JPanel {
        public TransferView(User user) {
            setLayout(new GridBagLayout());
            setBackground(COLOR_BACKGROUND);
            
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(COLOR_SURFACE);
            card.setBorder(new EmptyBorder(40, 40, 40, 40));
            card.setPreferredSize(new Dimension(500, 450));
            
            JLabel title = new JLabel("Wire Transfer");
            title.setFont(FONT_TITLE);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            ModernTextField recF = new ModernTextField("Recipient Account ID");
            ModernTextField amtF = new ModernTextField("Amount to Send (BDT)");
            recF.setMaximumSize(new Dimension(400, 45));
            amtF.setMaximumSize(new Dimension(400, 45));
            
            ModernButton sendBtn = new ModernButton("SEND FUNDS", COLOR_PRIMARY);
            sendBtn.setMaximumSize(new Dimension(400, 50));
            sendBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            sendBtn.addActionListener(e -> {
                try {
                    double amt = Double.parseDouble(amtF.getText());
                    if(bankService.transfer(user, recF.getText(), amt)) {
                        showToast("Transfer Complete", false);
                        recF.setText(""); amtF.setText("");
                    } else showToast("Transaction Failed (Check ID/Balance)", true);
                } catch(Exception ex) { showToast("Invalid Input", true); }
            });

            card.add(title);
            card.add(Box.createVerticalStrut(40));
            card.add(new JLabel("Beneficiary Details") {{ setFont(FONT_BOLD); }});
            card.add(Box.createVerticalStrut(5));
            card.add(recF);
            card.add(Box.createVerticalStrut(20));
            card.add(new JLabel("Transaction Amount") {{ setFont(FONT_BOLD); }});
            card.add(Box.createVerticalStrut(5));
            card.add(amtF);
            card.add(Box.createVerticalStrut(40));
            card.add(sendBtn);
            
            add(card);
        }
    }

    class BillPayView extends JPanel {
        public BillPayView(User user) {
            setLayout(new BorderLayout());
            setBackground(COLOR_BACKGROUND);
            setBorder(new EmptyBorder(40, 50, 40, 50));

            JLabel title = new JLabel("Pay Bills");
            title.setFont(FONT_TITLE);
            
            JPanel grid = new JPanel(new GridLayout(2, 2, 25, 25));
            grid.setOpaque(false);
            
            grid.add(createBillerCard(user, "Electricity Corp", "Power", COLOR_SIDEBAR));
            grid.add(createBillerCard(user, "Global Internet", "Internet", COLOR_PRIMARY));
            grid.add(createBillerCard(user, "City Water Dept", "Water", new Color(14, 165, 233)));
            grid.add(createBillerCard(user, "Mobile Services", "Phone", new Color(139, 92, 246)));

            add(title, BorderLayout.NORTH);
            add(new JScrollPane(grid), BorderLayout.CENTER);
        }
        
        private JPanel createBillerCard(User u, String name, String type, Color iconColor) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(COLOR_SURFACE);
            p.setBorder(new EmptyBorder(25, 25, 25, 25));
            
            JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
            JLabel n = new JLabel(name); n.setFont(new Font("Segoe UI", Font.BOLD, 16));
            JLabel ty = new JLabel(type); ty.setForeground(COLOR_TEXT_MUTED);
            top.add(n, BorderLayout.NORTH); top.add(ty, BorderLayout.CENTER);
            
            JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10)); form.setOpaque(false);
            ModernTextField amt = new ModernTextField("Amount due");
            amt.setPreferredSize(new Dimension(180, 40));
            ModernButton pay = new ModernButton("PAY NOW", iconColor);
            pay.setPreferredSize(new Dimension(100, 40));
            
            pay.addActionListener(e -> {
                try {
                    double val = Double.parseDouble(amt.getText());
                    if(bankService.payBill(u, name, val)) showToast("Payment to " + name + " Successful", false);
                    else showToast("Insufficient Funds", true);
                } catch(Exception ex) { showToast("Invalid Amount", true); }
            });
            
            form.add(amt); form.add(Box.createHorizontalStrut(10)); form.add(pay);
            
            p.add(top, BorderLayout.NORTH);
            p.add(form, BorderLayout.SOUTH);
            return p;
        }
    }

    class SettingsView extends JPanel {
        public SettingsView(User user) {
            setLayout(new GridBagLayout()); // Center the content
            setBackground(COLOR_BACKGROUND);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(COLOR_SURFACE);
            panel.setBorder(new EmptyBorder(40, 40, 40, 40));
            panel.setPreferredSize(new Dimension(500, 400));
            
            JLabel t = new JLabel("Security Settings");
            t.setFont(FONT_TITLE);
            t.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JPasswordField curr = new JPasswordField();
            JPasswordField newP = new JPasswordField();
            LineBorder b = new LineBorder(new Color(200,200,200));
            curr.setBorder(BorderFactory.createCompoundBorder(b, new EmptyBorder(10,10,10,10)));
            newP.setBorder(BorderFactory.createCompoundBorder(b, new EmptyBorder(10,10,10,10)));
            Dimension d = new Dimension(400, 45);
            curr.setMaximumSize(d); newP.setMaximumSize(d);
            
            ModernButton save = new ModernButton("UPDATE PASSWORD", COLOR_SIDEBAR);
            save.setMaximumSize(d);
            save.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            save.addActionListener(e -> {
                if(new String(curr.getPassword()).equals(user.getPassword())) {
                    user.setPassword(new String(newP.getPassword()));
                    showToast("Security Credentials Updated", false);
                    curr.setText(""); newP.setText("");
                } else showToast("Authentication Failed", true);
            });

            panel.add(t);
            panel.add(Box.createVerticalStrut(40));
            panel.add(new JLabel("Current Password") {{ setFont(FONT_BOLD); }});
            panel.add(Box.createVerticalStrut(5));
            panel.add(curr);
            panel.add(Box.createVerticalStrut(20));
            panel.add(new JLabel("New Password") {{ setFont(FONT_BOLD); }});
            panel.add(Box.createVerticalStrut(5));
            panel.add(newP);
            panel.add(Box.createVerticalStrut(40));
            panel.add(save);
            
            add(panel);
        }
    }

    // --- Helpers ---
    
    private String formatMoney(double amt) {
        // Manually format BDT for consistency across all systems
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "৳" + df.format(amt);
    }
}
