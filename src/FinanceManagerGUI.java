import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FinanceManagerGUI extends JFrame {
    private final DefaultTableModel tableModel;
    private final JTable transactionTable;
    private final DefaultTableModel reportTableModel;
    private final JTable reportTable;

    private final JComboBox<String> addTypeBox;
    private final JTextField addAmountField;
    private final JComboBox<String> addAccountBox;
    private final JComboBox<String> addCategoryBox;
    private final JComboBox<String> addDescriptionBox;
    private final JComboBox<String> addModeBox;

    private final JComboBox<String> editTypeBox;
    private final JTextField editAmountField;
    private final JComboBox<String> editAccountBox;
    private final JComboBox<String> editCategoryBox;
    private final JComboBox<String> editDescriptionBox;
    private final JComboBox<String> editModeBox;

    private final JTextField searchField;
    private final JComboBox<String> filterTypeBox;
    private final JComboBox<String> filterAccountBox;
    private final JComboBox<String> filterCategoryBox;
    private final JComboBox<String> filterDescriptionBox;
    private final JComboBox<String> filterModeBox;

    private final JComboBox<String> reportTypeBox;
    private final JComboBox<String> reportAccountBox;
    private final JComboBox<String> reportCategoryBox;
    private final JComboBox<String> reportDescriptionBox;
    private final JComboBox<String> reportModeBox;
    private final JLabel reportTotalLabel;

    public FinanceManagerGUI() {
        super("Finance Manager");

        tableModel = new DefaultTableModel(
                new String[] { "ID", "Date Time", "Type", "Amount", "Account", "Category", "Description", "Mode" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillEditFormFromSelectedRow();
            }
        });

        reportTableModel = new DefaultTableModel(
                new String[] { "ID", "Date Time", "Type", "Amount", "Account", "Category", "Description", "Mode" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(reportTableModel);

        addTypeBox = typeBox();
        addAmountField = new JTextField(14);
        addAccountBox = accountBox(false);
        addCategoryBox = categoryBox(false);
        addDescriptionBox = descriptionBox(false);
        addModeBox = modeBox();

        editTypeBox = typeBox();
        editAmountField = new JTextField(14);
        editAccountBox = accountBox(false);
        editCategoryBox = categoryBox(false);
        editDescriptionBox = descriptionBox(false);
        editModeBox = modeBox();

        searchField = new JTextField(20);
        filterTypeBox = allTypeBox();
        filterAccountBox = accountBox(true);
        filterCategoryBox = categoryBox(true);
        filterDescriptionBox = descriptionBox(true);
        filterModeBox = allModeBox();

        reportTypeBox = allTypeBox();
        reportAccountBox = accountBox(true);
        reportCategoryBox = categoryBox(true);
        reportDescriptionBox = descriptionBox(true);
        reportModeBox = allModeBox();
        reportTotalLabel = new JLabel("Total: Rs 0.00", SwingConstants.LEFT);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Transaction", buildAddTab());
        tabs.addTab("View and Edit", buildViewTab());
        tabs.addTab("Reports", buildReportsTab());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        loadTransactions();
        refreshReportTotal();
    }

    private JPanel buildAddTab() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New Transaction"));
        addFormRow(form, 0, "Type", addTypeBox, "Amount", addAmountField);
        addFormRow(form, 1, "Account", addAccountBox, "Category", addCategoryBox);
        addFormRow(form, 2, "Description", addDescriptionBox, "Mode", addModeBox);

        JButton addButton = new JButton("Add Transaction");
        addButton.addActionListener(event -> addTransaction());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearAddForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(clearButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 8, 8, 8);
        form.add(buttons, gbc);

        outer.add(form, BorderLayout.NORTH);
        return outer;
    }

    private JPanel buildViewTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        panel.add(buildFilterPanel(), BorderLayout.NORTH);
        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        panel.add(buildEditPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFilterPanel() {
        JPanel container = new JPanel(new BorderLayout(6, 6));
        container.setBorder(BorderFactory.createTitledBorder("Find Transactions"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(event -> searchTransactions());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> loadTransactions());

        searchPanel.add(new JLabel("Search"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.addActionListener(event -> applyFilters());

        JButton clearFilterButton = new JButton("Clear Filters");
        clearFilterButton.addActionListener(event -> clearFilters());

        filterPanel.add(new JLabel("Type"));
        filterPanel.add(filterTypeBox);
        filterPanel.add(new JLabel("Account"));
        filterPanel.add(filterAccountBox);
        filterPanel.add(new JLabel("Category"));
        filterPanel.add(filterCategoryBox);
        filterPanel.add(new JLabel("Description"));
        filterPanel.add(filterDescriptionBox);
        filterPanel.add(new JLabel("Mode"));
        filterPanel.add(filterModeBox);
        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);

        container.add(searchPanel, BorderLayout.NORTH);
        container.add(filterPanel, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Edit Selected Transaction"));

        addFormRow(panel, 0, "Type", editTypeBox, "Amount", editAmountField);
        addFormRow(panel, 1, "Account", editAccountBox, "Category", editCategoryBox);
        addFormRow(panel, 2, "Description", editDescriptionBox, "Mode", editModeBox);

        JButton updateButton = new JButton("Update Selected");
        updateButton.addActionListener(event -> updateSelectedTransaction());

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(event -> deleteSelectedTransaction());

        JButton clearButton = new JButton("Clear Selection");
        clearButton.addActionListener(event -> clearEditForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 8, 8);
        panel.add(buttons, gbc);

        return panel;
    }

    private JPanel buildReportsTab() {
        JPanel outer = new JPanel(new BorderLayout(10, 10));
        outer.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel reportPanel = new JPanel(new GridBagLayout());
        reportPanel.setBorder(BorderFactory.createTitledBorder("Calculate Total"));
        addFormRow(reportPanel, 0, "Type", reportTypeBox, "Account", reportAccountBox);
        addFormRow(reportPanel, 1, "Category", reportCategoryBox, "Description", reportDescriptionBox);
        addSingleFieldRow(reportPanel, 2, "Mode", reportModeBox);

        JButton calculateButton = new JButton("Calculate Total");
        calculateButton.addActionListener(event -> refreshReportTotal());

        JButton showMatchingButton = new JButton("Show Matching Transactions");
        showMatchingButton.addActionListener(event -> showReportMatchesInTable());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(calculateButton);
        buttons.add(showMatchingButton);
        buttons.add(reportTotalLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 8, 8, 8);
        reportPanel.add(buttons, gbc);

        outer.add(reportPanel, BorderLayout.NORTH);
        outer.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        return outer;
    }

    private void addFormRow(JPanel panel, int row, String firstLabel, Component firstField,
            String secondLabel, Component secondField) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(firstLabel), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(firstField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel(secondLabel), gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(secondField, gbc);
    }

    private void addSingleFieldRow(JPanel panel, int row, String label, Component field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JComboBox<String> typeBox() {
        return new JComboBox<>(new String[] { "expense", "income" });
    }

    private JComboBox<String> allTypeBox() {
        return new JComboBox<>(new String[] { "All", "expense", "income" });
    }

    private JComboBox<String> modeBox() {
        return new JComboBox<>(new String[] { "online", "offline" });
    }

    private JComboBox<String> allModeBox() {
        return new JComboBox<>(new String[] { "All", "online", "offline" });
    }

    private JComboBox<String> accountBox(boolean includeAll) {
        return editableBox(includeAll
                ? new String[] { "All", "BOB", "DCB", "Cash", "UPI" }
                : new String[] { "BOB", "DCB", "Cash", "UPI" });
    }

    private JComboBox<String> categoryBox(boolean includeAll) {
        return editableBox(includeAll
                ? new String[] { "All", "Food", "Shopping", "Travel", "Rent", "Salary", "Bills", "Other" }
                : new String[] { "Food", "Shopping", "Travel", "Rent", "Salary", "Bills", "Other" });
    }

    private JComboBox<String> descriptionBox(boolean includeAll) {
        return editableBox(includeAll
                ? new String[] { "All", "Blinkit", "Amazon", "Zomato", "Swiggy", "College", "Other" }
                : new String[] { "Blinkit", "Amazon", "Zomato", "Swiggy", "College", "Other" });
    }

    private JComboBox<String> editableBox(String[] values) {
        JComboBox<String> box = new JComboBox<>(values);
        box.setEditable(true);
        return box;
    }

    private void addTransaction() {
        try {
            Transaction.addTransaction(
                    selectedValue(addTypeBox),
                    parseAmount(addAmountField),
                    selectedValue(addAccountBox),
                    selectedValue(addCategoryBox),
                    selectedValue(addDescriptionBox),
                    selectedValue(addModeBox));

            showMessage("Transaction added successfully.");
            clearAddForm();
            loadTransactions();
            refreshReportTotal();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateSelectedTransaction() {
        int id = selectedId();
        if (id == -1) {
            showMessage("Select a transaction from the table first.");
            return;
        }

        try {
            Transaction.updateTransaction(
                    id,
                    selectedValue(editTypeBox),
                    parseAmount(editAmountField),
                    selectedValue(editAccountBox),
                    selectedValue(editCategoryBox),
                    selectedValue(editDescriptionBox),
                    selectedValue(editModeBox));

            showMessage("Transaction updated successfully.");
            loadTransactions();
            refreshReportTotal();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteSelectedTransaction() {
        int id = selectedId();
        if (id == -1) {
            showMessage("Select a transaction from the table first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Delete transaction ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Transaction.deleteTransaction(id);
            showMessage("Transaction deleted successfully.");
            clearEditForm();
            loadTransactions();
            refreshReportTotal();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void searchTransactions() {
        try {
            fillTable(Transaction.searchTransactions(searchField.getText()));
        } catch (Exception e) {
            showError(e);
        }
    }

    private void applyFilters() {
        try {
            fillTable(Transaction.filterTransactions(
                    selectedValue(filterTypeBox),
                    selectedValue(filterAccountBox),
                    selectedValue(filterCategoryBox),
                    selectedValue(filterDescriptionBox),
                    selectedValue(filterModeBox)));
        } catch (Exception e) {
            showError(e);
        }
    }

    private void clearFilters() {
        filterTypeBox.setSelectedIndex(0);
        filterAccountBox.setSelectedItem("All");
        filterCategoryBox.setSelectedItem("All");
        filterDescriptionBox.setSelectedItem("All");
        filterModeBox.setSelectedIndex(0);
        searchField.setText("");
        loadTransactions();
    }

    private void loadTransactions() {
        try {
            fillTable(Transaction.getAllTransactions());
        } catch (Exception e) {
            showError(e);
        }
    }

    private void refreshReportTotal() {
        try {
            double total = Transaction.getFilteredTotal(
                    selectedValue(reportTypeBox),
                    selectedValue(reportAccountBox),
                    selectedValue(reportCategoryBox),
                    selectedValue(reportDescriptionBox),
                    selectedValue(reportModeBox));
            reportTotalLabel.setText(String.format("Total: Rs %.2f", total));
            fillReportTable(Transaction.filterTransactions(
                    selectedValue(reportTypeBox),
                    selectedValue(reportAccountBox),
                    selectedValue(reportCategoryBox),
                    selectedValue(reportDescriptionBox),
                    selectedValue(reportModeBox)));
        } catch (Exception e) {
            reportTotalLabel.setText("Total: unavailable");
        }
    }

    private void showReportMatchesInTable() {
        try {
            filterTypeBox.setSelectedItem(selectedValue(reportTypeBox));
            filterAccountBox.setSelectedItem(selectedValue(reportAccountBox));
            filterCategoryBox.setSelectedItem(selectedValue(reportCategoryBox));
            filterDescriptionBox.setSelectedItem(selectedValue(reportDescriptionBox));
            filterModeBox.setSelectedItem(selectedValue(reportModeBox));
            applyFilters();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void fillTable(List<Transaction.TransactionRecord> transactions) {
        tableModel.setRowCount(0);

        for (Transaction.TransactionRecord transaction : transactions) {
            tableModel.addRow(new Object[] {
                    transaction.getId(),
                    transaction.getDateTime(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getAccount(),
                    transaction.getCategory(),
                    transaction.getDescription(),
                    transaction.getMode()
            });
        }
    }

    private void fillReportTable(List<Transaction.TransactionRecord> transactions) {
        reportTableModel.setRowCount(0);

        for (Transaction.TransactionRecord transaction : transactions) {
            reportTableModel.addRow(new Object[] {
                    transaction.getId(),
                    transaction.getDateTime(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getAccount(),
                    transaction.getCategory(),
                    transaction.getDescription(),
                    transaction.getMode()
            });
        }
    }

    private void fillEditFormFromSelectedRow() {
        int row = transactionTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        editTypeBox.setSelectedItem(valueAt(row, 2));
        editAmountField.setText(valueAt(row, 3));
        editAccountBox.setSelectedItem(valueAt(row, 4));
        editCategoryBox.setSelectedItem(valueAt(row, 5));
        editDescriptionBox.setSelectedItem(valueAt(row, 6));
        editModeBox.setSelectedItem(valueAt(row, 7));
    }

    private int selectedId() {
        int row = transactionTable.getSelectedRow();
        if (row == -1) {
            return -1;
        }

        return Integer.parseInt(valueAt(row, 0));
    }

    private String valueAt(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private String selectedValue(JComboBox<String> box) {
        Object value = box.getSelectedItem();
        return value == null ? "" : value.toString().trim();
    }

    private double parseAmount(JTextField amountField) {
        String value = amountField.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Amount is required.");
        }

        return Double.parseDouble(value);
    }

    private void clearAddForm() {
        addTypeBox.setSelectedIndex(0);
        addAmountField.setText("");
        addAccountBox.setSelectedItem("");
        addCategoryBox.setSelectedItem("");
        addDescriptionBox.setSelectedItem("");
        addModeBox.setSelectedIndex(0);
    }

    private void clearEditForm() {
        editTypeBox.setSelectedIndex(0);
        editAmountField.setText("");
        editAccountBox.setSelectedItem("");
        editCategoryBox.setSelectedItem("");
        editDescriptionBox.setSelectedItem("");
        editModeBox.setSelectedIndex(0);
        transactionTable.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinanceManagerGUI().setVisible(true));
    }
}
