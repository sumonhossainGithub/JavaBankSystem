package bank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

abstract class Account {
    private String accountNumber;
    private String accountHolderName;
    protected double balance;

    public Account(String accountNumber, String accountHolderName, double balance) 
    {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public String getAccountNumber() 
    {
         return accountNumber; 
        }
    public String getAccountHolderName() 
    { 
        return accountHolderName;
     }
    public double getBalance() 
    { 
        return balance;
     }

    public boolean deposit(double amount)
     {
        if (amount <= 0) return false;
        balance += amount;
        return true;
    }

    public boolean withdraw(double amount)
     {
        if (amount <= 0 || amount > balance)
             return false;
        balance -= amount;
        return true;
    }
}

class SavingAccount extends Account
 {
    public SavingAccount(String accNo, String holder, double balance) 
    {
        super(accNo, holder, balance);
    }
}

class CurrentAccount extends Account 
{
    public CurrentAccount(String accNo, String holder, double balance)
     {
        super(accNo, holder, balance);
    }
}

class Bank 
{
    private ArrayList<Account> accounts = new ArrayList<>();

    public boolean addAccount(Account acc)
     {
        if (findAccount(acc.getAccountNumber()) != null) return false;
        accounts.add(acc);
        return true;
    }

    public Account findAccount(String accNo) 
    {
        for (Account a : accounts)
             {
            if (a.getAccountNumber().equals(accNo)) return a;
        }
        return null;
    }

    public ArrayList<Account> getAllAccounts() 
    {
        return accounts;
    }
}

public class BankManagementGUI extends JFrame 
{

    private Bank bank;
    private JTextField txtAccNo, txtName, txtInitialBalance;
    private JTextField txtSearchAcc, txtAmount;
    private JTable tableAccounts;
    private DefaultTableModel model;
    private JLabel lblStatus;

    public BankManagementGUI()
     {
        bank = new Bank();
        initUI();
    }


    private void initUI() 
    {
        setTitle("Bank System");
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10,10));

        // Top Panel - Create account
        JPanel createPanel = new JPanel(new GridLayout(4,2,5,5));
        createPanel.setBorder(BorderFactory.createTitledBorder("Create Account"));

        txtAccNo = new JTextField();
        txtName = new JTextField();
        txtInitialBalance = new JTextField();

        JButton btnSave = new JButton("Create Saving Account");
        JButton btnCurrent = new JButton("Create Current Account");

        createPanel.add(new JLabel("Account No:"));
        createPanel.add(txtAccNo);
        createPanel.add(new JLabel("Holder Name:"));
        createPanel.add(txtName);
        createPanel.add(new JLabel("Balance:"));
        createPanel.add(txtInitialBalance);
        createPanel.add(btnSave);
        createPanel.add(btnCurrent);

        // Middle Panel - operations
        JPanel opPanel = new JPanel(new GridLayout(3,2,5,5));
        opPanel.setBorder(BorderFactory.createTitledBorder("Operations"));

        txtSearchAcc = new JTextField();
        txtAmount = new JTextField();

        JButton btnSearch = new JButton("Search");
        JButton btnDep = new JButton("Deposit");
        JButton btnWit = new JButton("Withdraw");

        opPanel.add(new JLabel("Account No:"));
        opPanel.add(txtSearchAcc);
        opPanel.add(new JLabel("Amount:"));
        opPanel.add(txtAmount);
        opPanel.add(btnDep);
        opPanel.add(btnWit);

        // Table
        model = new DefaultTableModel(new Object[]{"Acc No", "Holder", "Type", "Balance"},0);
        tableAccounts = new JTable(model);

        JScrollPane scroll = new JScrollPane(tableAccounts);

        lblStatus = new JLabel("Ready.");

        JButton refresh = new JButton("Refresh Table");

        panel.add(createPanel, BorderLayout.NORTH);
        panel.add(opPanel, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        panel.add(lblStatus, BorderLayout.PAGE_END);

        add(panel);

        // actions
        btnSave.addActionListener(e -> create(true));
        btnCurrent.addActionListener(e -> create(false));
        btnSearch.addActionListener(e -> search());
        btnDep.addActionListener(e -> deposit());
        btnWit.addActionListener(e -> withdraw());
        refresh.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void create(boolean saving) {
        String acc = txtAccNo.getText();
        String name = txtName.getText();
        double bal;

        try { bal = Double.parseDouble(txtInitialBalance.getText()); }
        catch (Exception ex) { return; }

        Account a = saving ? new SavingAccount(acc,name,bal) : new CurrentAccount(acc,name,bal);

        if (!bank.addAccount(a)) return;

        lblStatus.setText("Account Created.");
        refreshTable();
    }

    private void search()
     {
        Account a = bank.findAccount(txtSearchAcc.getText());
        if (a == null) return;

        JOptionPane.showMessageDialog(this,
                a.getAccountNumber()+"\n"+
                a.getAccountHolderName()+"\n"+
                a.getBalance());
    }

    private void deposit() 
    {
        Account a = bank.findAccount(txtSearchAcc.getText());
        if (a == null) return;

        double amt = Double.parseDouble(txtAmount.getText());
        a.deposit(amt);

        lblStatus.setText("Deposit done.");
        refreshTable();
    }

    private void withdraw()
     {
        Account a = bank.findAccount(txtSearchAcc.getText());
        if (a == null) return;

        double amt = Double.parseDouble(txtAmount.getText());
        a.withdraw(amt);

        lblStatus.setText("Withdraw done.");
        refreshTable();
    }

    private void refreshTable() 
    {
        model.setRowCount(0);
        for(Account a : bank.getAllAccounts()) 
            {
            String type = a instanceof SavingAccount ? "Saving" : "Current";
            model.addRow(new Object[]
                {
                    a.getAccountNumber(),
                    a.getAccountHolderName(),
                    type,
                    a.getBalance()
            });
        }
    }

    public static void main(String[] args) 
    {
        new BankManagementGUI().setVisible(true);
    }
}