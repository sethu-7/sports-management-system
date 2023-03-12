import com.mysql.cj.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Objects;
import base.*;
import base.player.*;

public class Batsmen{

    private JPanel Main;
    private JTextField namefield;
    private JTextField matchfield;
    private JTextField runfield;
    private JTextField halfcenturyfield;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JComboBox<String> comparebox;
    private JTextField centuryfield;
    private JTextField idfield;
    private Batsmen_v batsmen;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Batsmen");
        frame.setContentPane(new Batsmen().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Batsmen() {
        Connect();
        save_func();
        search_func();
        delete_func();
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

    public void setBatsman(int id, String name, int matches, int runs, int halfcenturies, int centuries){
        this.batsmen = new Batsmen_v();
        this.batsmen.player = new Player_v();
        this.batsmen.player.setId(id);
        this.batsmen.player.setName(name);
        this.batsmen.setMatches(matches);
        this.batsmen.setRuns(runs);
        this.batsmen.setHalfcenturies(halfcenturies);
        this.batsmen.setCenturies(centuries);
    }

    public void save_func(){
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name;
                int id=0, matches=0, runs=0, halfcenturies=0, centuries=0;
                try {
                    id = Integer.parseInt(idfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid ID");
                }
                try {
                    matches = Integer.parseInt(matchfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                name = namefield.getText();
                try {
                    runs = Integer.parseInt(runfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                try {
                    halfcenturies = Integer.parseInt(halfcenturyfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                try {
                    centuries = Integer.parseInt(centuryfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                setBatsman(id, name, matches, runs, halfcenturies, centuries);

                try {
                    PreparedStatement check = con.prepareStatement("select 1 from batsmen where id = ?");
                    check.setInt(1, batsmen.player.getId());

                    try (ResultSet check_result = check.executeQuery()){
                        if (check_result.next()) {
                            try {

                                prep = con.prepareStatement("update batsmen set name = ?, matches = ?, runs = ?, " +
                                        "half_centuries = ?, centuries = ? where id = ?");
                                prep.setString(1, batsmen.player.getName());
                                prep.setInt(2, batsmen.getMatches());
                                prep.setInt(3, batsmen.getRuns());
                                prep.setInt(4, batsmen.getHalfcenturies());
                                prep.setInt(5, batsmen.getCenturies());
                                prep.setInt(6, batsmen.player.getId());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Record updated.");

                                namefield.setText("");
                                matchfield.setText("");
                                runfield.setText("");
                                halfcenturyfield.setText("");
                                centuryfield.setText("");
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
                                prep = con.prepareStatement("insert into batsmen(id, name, matches, runs, " +
                                        "half_centuries, centuries) values(?,?,?,?,?,?)");
                                prep.setInt(1, batsmen.player.getId());
                                prep.setString(2, batsmen.player.getName());
                                prep.setInt(3, batsmen.getMatches());
                                prep.setInt(4, batsmen.getRuns());
                                prep.setInt(5, batsmen.getHalfcenturies());
                                prep.setInt(6, batsmen.getCenturies());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null,"Record added.");

                                idfield.setText("");
                                namefield.setText("");
                                matchfield.setText("");
                                runfield.setText("");
                                halfcenturyfield.setText("");
                                centuryfield.setText("");
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
                            prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                    "batsmen where id = ?");
                            prep.setInt(1, search_id);
                            ResultSet rs = prep.executeQuery();
                            int i = 0;

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                    model, result);

                            while (rs.next()) {
                                setBatsman(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5), rs.getInt(6));
                                model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                        batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                        batsmen.getCenturies()});
                                i++;
                            }
                            if (i == 0) {
                                idfield.setText("");
                                JOptionPane.showMessageDialog(null, "Invalid ID");
                            }
                        } catch (SQLException exception) {
                            throw new RuntimeException(exception);
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(namefield.getText())) {
                        try {
                            String search_name = idfield.getText();
                            prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                    "batsmen where name like ?");
                            prep.setString(1, "%" + search_name + "%");
                            ResultSet rs = prep.executeQuery();
                            int i = 0;

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                    model, result);

                            while (rs.next()) {
                                setBatsman(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5), rs.getInt(6));
                                model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                        batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                        batsmen.getCenturies()});
                                i++;
                            }
                            if (i == 0) {
                                idfield.setText("");
                                JOptionPane.showMessageDialog(null, "Invalid Product1 ID");
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(matchfield.getText())){
                        int search_matches = Integer.parseInt(matchfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where matches > ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs1.next()) {
                                    setBatsman(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    i++;
                                }
                                if (i == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where matches < ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs2.next()) {
                                    setBatsman(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    j++;
                                }
                                if (j == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where matches = ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs3.next()) {
                                    setBatsman(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    k++;
                                }
                                if (k == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(runfield.getText())){
                        int search_runs = Integer.parseInt(runfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where runs > ?");
                                prep.setInt(1, search_runs);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs1.next()) {
                                    setBatsman(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    i++;
                                }
                                if (i == 0) {
                                    runfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where runs < ?");
                                prep.setInt(1, search_runs);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs2.next()) {
                                    setBatsman(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    j++;
                                }
                                if (j == 0) {
                                    runfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where runs = ?");
                                prep.setInt(1, search_runs);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs3.next()) {
                                    setBatsman(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    k++;
                                }
                                if (k == 0) {
                                    runfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(halfcenturyfield.getText())){
                        int search_halfcenturies = Integer.parseInt(halfcenturyfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where half_centuries > ?");
                                prep.setInt(1, search_halfcenturies);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs1.next()) {
                                    setBatsman(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    i++;
                                }
                                if (i == 0) {
                                    halfcenturyfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where half_centuries < ?");
                                prep.setInt(1, search_halfcenturies);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs2.next()) {
                                    setBatsman(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    j++;
                                }
                                if (j == 0) {
                                    halfcenturyfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where half_centuries = ?");
                                prep.setInt(1, search_halfcenturies);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs3.next()) {
                                    setBatsman(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    k++;
                                }
                                if (k == 0) {
                                    halfcenturyfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(centuryfield.getText())){
                        int search_centuries = Integer.parseInt(centuryfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where centuries > ?");
                                prep.setInt(1, search_centuries);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs1.next()) {
                                    setBatsman(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    i++;
                                }
                                if (i == 0) {
                                    centuryfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where centuries < ?");
                                prep.setInt(1, search_centuries);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs2.next()) {
                                    setBatsman(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    j++;
                                }
                                if (j == 0) {
                                    centuryfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries from " +
                                        "batsmen where centuries = ?");
                                prep.setInt(1, search_centuries);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Runs", "Half-centuries", "Centuries"},
                                        model, result);

                                while (rs3.next()) {
                                    setBatsman(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                            batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                            batsmen.getCenturies()});
                                    k++;
                                }
                                if (k == 0) {
                                    centuryfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else {
                        try {
                            prep = con.prepareStatement("select id,name,matches,runs,half_centuries,centuries " +
                                    "from batsmen");
                            ResultSet rs = prep.executeQuery();

                            int i = 0;
                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[] {"ID", "Name", "Matches", "Runs", "Half-centuries",
                                    "Centuries"}, model, result);

                            while (rs.next()) {
                                setBatsman(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5), rs.getInt(6));
                                model.addRow(new Object[]{batsmen.player.getId(), batsmen.player.getName(),
                                        batsmen.getMatches(), batsmen.getRuns(), batsmen.getHalfcenturies(),
                                        batsmen.getCenturies()});
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
                    prep = con.prepareStatement("delete from batsmen where id = ?");
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
}


