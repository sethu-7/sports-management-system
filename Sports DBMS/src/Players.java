import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Objects;
import com.mysql.cj.util.StringUtils;
import base.*;

public class Players {
    private JPanel Main;
    private JTextField idfield;
    private JTextField namefield;
    private JTextField yearfield;
    private JTextField salaryfield;
    private JTextField rolefield;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JComboBox<String> comparebox;
    private JButton checkStatsButton;
    private Player_v player;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Players");
        frame.setContentPane(new Players().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Players() {
        Connect();
        save_func();
        search_func();
        delete_func();
        check_stats();
    }
    Connection con;
    PreparedStatement prep;
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/dbms", "root", "sunnygoi25");
            System.out.println("Connected.");
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void setup_search(String[] entries, DefaultTableModel model, JFrame result){
        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        result.setLayout(new BorderLayout());
        model.setColumnIdentifiers(entries);
        JTable table = new JTable();
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        result.add(scroll);
        result.setVisible(true);
        result.setSize(400, 300);
    }

    public void setPlayer(int id, String name, int year, int salary, String role)
    {
        this.player = new Player_v();
        this.player.setName(name);
        this.player.setSalary(salary);
        this.player.setRole(role);
        this.player.setId(id);
        this.player.setYear(year);

    }

    public void save_func(){
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name, role;
                int id = 0, year = 0, salary = 0;
                try {
                    id=Integer.parseInt(idfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid ID");
                }
                name = namefield.getText();
                try {
                    year = Integer.parseInt(yearfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid ID");
                }
                try {
                    salary = Integer.parseInt(salaryfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid ID");
                }
                role = rolefield.getText();
                setPlayer(id,name,year,salary,role);

                try {
                    PreparedStatement check = con.prepareStatement("select 1 from players where id = ?");
                    check.setInt(1, player.getId());

                    try (ResultSet check_result = check.executeQuery()){
                        if (check_result.next()) {
                            try {

                                prep = con.prepareStatement("update players set name = ?, year = ?, salary = ?, " +
                                        "role = ? where id = ?");
                                prep.setString(1, player.getName());
                                prep.setInt(2, player.getYear());
                                prep.setInt(3, player.getSalary());
                                prep.setString(4, player.getRole());
                                prep.setInt(5, player.getId());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Record updated.");

                                namefield.setText("");
                                yearfield.setText("");
                                salaryfield.setText("");
                                rolefield.setText("");
                                idfield.setText("");
                                idfield.requestFocus();
                            }
                            catch (SQLException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        else {
                            try {
                                prep = con.prepareStatement("insert into players(id, name, year, salary, role)" +
                                        "values(?,?,?,?,?)");
                                prep.setInt(1, player.getId());
                                prep.setString(2, player.getName());
                                prep.setInt(3, player.getYear());
                                prep.setInt(4, player.getSalary());
                                prep.setString(5, player.getRole());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null,"Record added.");

                                idfield.setText("");
                                namefield.setText("");
                                yearfield.setText("");
                                salaryfield.setText("");
                                rolefield.setText("");
                                idfield.requestFocus();
                            }

                            catch (SQLException e2)
                            {
                                e2.printStackTrace();
                            }
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void search_func(){
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!StringUtils.isEmptyOrWhitespaceOnly(idfield.getText())) {
                        try {
                            int search_id = Integer.parseInt(idfield.getText());
                            prep = con.prepareStatement("select id,name,year,salary,role from players " +
                                    "where id = ?");
                            prep.setInt(1, search_id);
                            ResultSet rs = prep.executeQuery();
                            int i = 0;

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Year", "Salary", "Role"}, model, result);

                            if (rs.next()) {
                                setPlayer(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getString(5));
                                model.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                        player.getSalary(), player.getRole()});
                                i++;
                            }
                            if (i == 0) {
                                idfield.setText("");
                                JOptionPane.showMessageDialog(null, "Invalid Product ID");
                            }
                        } catch (SQLException exception) {
                            throw new RuntimeException(exception);
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(namefield.getText())) {
                        try {
                            String search_name = namefield.getText();
                            prep = con.prepareStatement("select id, name, year, salary, role from players " +
                                    "where name like ?");
                            prep.setString(1, "%" + search_name + "%");
                            ResultSet rs = prep.executeQuery();

                            int i = 0;
                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[] {"ID", "Name", "Year", "Salary", "Role"}, model, result);

                            while (rs.next()) {
                                setPlayer(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getString(5));
                                model.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                        player.getSalary(), player.getRole()});
                                i++;
                            }
                            if (i==0) {
                                namefield.setText("");
                                JOptionPane.showMessageDialog(null, "Invalid name");
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(yearfield.getText())){
                        int search_year = Integer.parseInt(yearfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id, name, year, salary, role from " +
                                        "players where year > ?");
                                prep.setInt(1, search_year);
                                ResultSet rs1 = prep.executeQuery();
                                System.out.println(comparator);
                                int i = 0;
                                DefaultTableModel model1 = new DefaultTableModel();
                                JFrame result1 = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Year", "Salary", "Role"}, model1, result1);
                                while (rs1.next()) {
                                    setPlayer(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getString(5));
                                    model1.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                            player.getSalary(), player.getRole()});
                                    i++;
                                }
                                if (i == 0) {
                                    yearfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid name");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id, name, year, salary, role from" +
                                        "players where year < ?");
                                prep.setInt(1, search_year);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;
                                DefaultTableModel model2 = new DefaultTableModel();
                                JFrame result2 = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Year", "Salary", "Role"}, model2, result2);
                                while (rs2.next()) {
                                    setPlayer(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getString(5));
                                    model2.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                            player.getSalary(), player.getRole()});
                                    j++;
                                }
                                if (j == 0) {
                                    yearfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid name");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id, name, year, salary, role from" +
                                        "players where year = ?");
                                prep.setInt(1, search_year);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;
                                DefaultTableModel model3 = new DefaultTableModel();
                                JFrame result3 = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Year", "Salary", "Role"}, model3, result3);
                                while (rs3.next()) {
                                    setPlayer(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getString(5));
                                    model3.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                            player.getSalary(), player.getRole()});
                                    k++;
                                }
                                if (k == 0) {
                                    yearfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid name");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(salaryfield.getText())){
                        int search_salary = Integer.parseInt(salaryfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id, name, year, salary, role from " +
                                        "players where salary > ?");
                                prep.setInt(1, search_salary);
                                ResultSet rs1 = prep.executeQuery();
                                System.out.println(comparator);
                                int i = 0;
                                DefaultTableModel model1 = new DefaultTableModel();
                                JFrame result1 = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Year", "Salary", "Role"}, model1, result1);
                                while (rs1.next()) {
                                    setPlayer(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getString(5));
                                    model1.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                            player.getSalary(), player.getRole()});
                                    i++;
                                }
                                if (i == 0) {
                                    salaryfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid name");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id, name, year, salary, role from" +
                                        "players where salary < ?");
                                prep.setInt(1, search_salary);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;
                                DefaultTableModel model2 = new DefaultTableModel();
                                JFrame result2 = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Year", "Salary", "Role"}, model2, result2);
                                while (rs2.next()) {
                                    setPlayer(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getString(5));
                                    model2.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                            player.getSalary(), player.getRole()});
                                    j++;
                                }
                                if (j == 0) {
                                    salaryfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid name");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id, name, year, salary, role from " +
                                        "players where salary = ?");
                                prep.setInt(1, search_salary);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;
                                DefaultTableModel model3 = new DefaultTableModel();
                                JFrame result3 = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Year", "Salary", "Role"}, model3, result3);
                                while (rs3.next()) {
                                    setPlayer(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getString(5));
                                    model3.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                            player.getSalary(), player.getRole()});
                                    k++;
                                }
                                if (k == 0) {
                                    salaryfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid name");
                                }
                            }
                        }
                    } else {
                        try {
                            prep = con.prepareStatement("select id, name, year, salary, role from players");
                            ResultSet rs = prep.executeQuery();

                            int i = 0;
                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[] {"ID", "Name", "Year", "Salary", "Role"}, model, result);

                            while (rs.next()) {
                                setPlayer(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getString(5));
                                model.addRow(new Object[]{player.getId(), player.getName(), player.getYear(),
                                        player.getSalary(), player.getRole()});
                                i++;
                            }
                            if (i==0) {
                                JOptionPane.showMessageDialog(null, "Empty database.");
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid ID");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void delete_func(){
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int delete_id;
                delete_id = Integer.parseInt(idfield.getText());
                try {
                    prep = con.prepareStatement("delete from players where id = ?");
                    prep.setInt(1, delete_id);

                    prep.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record deleted.");

                    idfield.setText("");
                    idfield.requestFocus();
                }

                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void check_stats(){
        checkStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int stats_id = Integer.parseInt(idfield.getText());
                try {
                    prep = con.prepareStatement("select role from players where id = ?");
                    prep.setInt(1, stats_id);
                    ResultSet rs = prep.executeQuery();
                    String role = null;

                    while (rs.next()){
                        role = rs.getString(1);
                    }

                    switch (Objects.requireNonNull(role)) {
                        case "batsman" -> {
                            prep = con.prepareStatement("select id, name, matches, runs, half_centuries, " +
                                    "centuries from batsmen where id = ?");
                            prep.setInt(1, stats_id);
                            ResultSet rs1 = prep.executeQuery();

                            DefaultTableModel model1 = new DefaultTableModel();
                            JFrame result1 = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                    model1, result1);
                            while (rs1.next()) {
                                int id = rs1.getInt(1);
                                String name = rs1.getString(2);
                                int matches = rs1.getInt(3);
                                int runs = rs1.getInt(4);
                                int halfcenturies = rs1.getInt(5);
                                int centuries = rs1.getInt(6);

                                model1.addRow(new Object[]{id, name, matches, runs, halfcenturies, centuries});
                            }
                        }
                        case "bowler" -> {
                            prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                    "bowlers where id = ?");
                            prep.setInt(1, stats_id);
                            ResultSet rs2 = prep.executeQuery();

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                    model, result);

                            while (rs2.next()) {
                                int id = rs2.getInt(1);
                                String name = rs2.getString(2);
                                int matches = rs2.getInt(3);
                                int overs = rs2.getInt(4);
                                int wickets = rs2.getInt(5);
                                int fivewickets = rs2.getInt(6);
                                model.addRow(new Object[]{id, name, matches, overs, wickets, fivewickets});
                            }
                        }
                        case "wicketkeeper" -> {
                            prep = con.prepareStatement("select id,name,matches,overs,wickes,fivewickets from " +
                                    "wicketkeepers where id = ?");
                            prep.setInt(1, stats_id);
                            ResultSet rs3 = prep.executeQuery();

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Catches", "Stumpings"},
                                    model, result);

                            while (rs3.next()) {
                                int id = rs3.getInt(1);
                                String name = rs3.getString(2);
                                int matches = rs3.getInt(3);
                                int catches = rs3.getInt(4);
                                int stumpings = rs3.getInt(5);
                                model.addRow(new Object[]{id, name, matches, catches, stumpings});
                            }
                        }
                        default -> System.out.println("No players exists.");
                    }
                }
                catch (SQLException e2)
                {
                    e2.printStackTrace();
                }
                catch (NumberFormatException e3){
                    System.out.println("No ID.");
                }
            }
        });
    }
}


