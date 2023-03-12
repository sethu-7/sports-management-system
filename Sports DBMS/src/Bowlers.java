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

public class Bowlers{
    private JPanel Main;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JTextField idfield;
    private JTextField namefield;
    private JTextField matchfield;
    private JTextField overfield;
    private JTextField wicketfield;
    private JTextField fivewicketfield;
    private JComboBox<String> comparebox;
    private Bowlers_v bowlers;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bowlers");
        frame.setContentPane(new Bowlers().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Bowlers() {
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

    public void setBowlers(int id, String name, int matches, int overs, int wickets, int fivewickets){
        this.bowlers = new Bowlers_v();
        this.bowlers.player = new Player_v();
        this.bowlers.player.setId(id);
        this.bowlers.player.setName(name);
        this.bowlers.setMatches(matches);
        this.bowlers.setOvers(overs);
        this.bowlers.setWickets(wickets);
        this.bowlers.setFivewickets(fivewickets);
    }

    public void save_func(){
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name;
                int id = 0, matches = 0, overs = 0, wickets = 0, fivewickets = 0;

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
                    overs = Integer.parseInt(overfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                try {
                    wickets = Integer.parseInt(wicketfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                try {
                    fivewickets = Integer.parseInt(fivewicketfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }

                setBowlers(id, name, matches, overs, wickets, fivewickets);

                try {
                    PreparedStatement check = con.prepareStatement("select 1 from bowlers where id = ?");
                    check.setInt(1, bowlers.player.getId());

                    try (ResultSet check_result = check.executeQuery()){
                        if (check_result.next()) {
                            try {

                                prep = con.prepareStatement("update bowlers set name = ?, matches = ?, overs = ?, " +
                                        "wickets = ?, fivewickets = ? where id = ?");
                                prep.setString(1, bowlers.player.getName());
                                prep.setInt(2, bowlers.getMatches());
                                prep.setInt(3, bowlers.getOvers());
                                prep.setInt(4, bowlers.getWickets());
                                prep.setInt(5, bowlers.getFivewickets());
                                prep.setInt(6, bowlers.player.getId());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Record updated.");

                                namefield.setText("");
                                matchfield.setText("");
                                overfield.setText("");
                                wicketfield.setText("");
                                fivewicketfield.setText("");
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
                                prep = con.prepareStatement("insert into bowlers(id, name, matches, overs, " +
                                        "wickets, fivewickets) values(?,?,?,?,?,?)");
                                prep.setInt(1, bowlers.player.getId());
                                prep.setString(2, bowlers.player.getName());
                                prep.setInt(3, bowlers.getMatches());
                                prep.setInt(4, bowlers.getOvers());
                                prep.setInt(5, bowlers.getWickets());
                                prep.setInt(6, bowlers.getFivewickets());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null,"Record added.");

                                idfield.setText("");
                                namefield.setText("");
                                matchfield.setText("");
                                overfield.setText("");
                                wicketfield.setText("");
                                fivewicketfield.setText("");
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
                            prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                    "bowlers where id = ?");
                            prep.setInt(1, search_id);
                            ResultSet rs = prep.executeQuery();
                            int i = 0;

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                    model, result);

                            if (rs.next()) {
                                setBowlers(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5), rs.getInt(6));
                                model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(), bowlers.getFivewickets()});
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
                            String search_name = idfield.getText();
                            prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                    "bowlers where name like ?");
                            prep.setString(1, "%" + search_name + "%");
                            ResultSet rs = prep.executeQuery();
                            int i = 0;

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                    model, result);

                            while (rs.next()) {
                                setBowlers(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5), rs.getInt(6));
                                model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                        bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(), bowlers.getFivewickets()});
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
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where matches > ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs1.next()) {
                                    setBowlers(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    i++;
                                }
                                if (i == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where matches < ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs2.next()) {
                                    setBowlers(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    j++;
                                }
                                if (j == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where matches = ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs3.next()) {
                                    setBowlers(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    k++;
                                }
                                if (k == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(overfield.getText())){
                        int search_overs = Integer.parseInt(overfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where overs > ?");
                                prep.setInt(1, search_overs);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs1.next()) {
                                    setBowlers(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    i++;
                                }
                                if (i == 0) {
                                    overfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where overs < ?");
                                prep.setInt(1, search_overs);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs2.next()) {
                                    setBowlers(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    j++;
                                }
                                if (j == 0) {
                                    overfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where overs = ?");
                                prep.setInt(1, search_overs);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs3.next()) {
                                    setBowlers(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    k++;
                                }
                                if (k == 0) {
                                    overfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(wicketfield.getText())){
                        int search_wickets = Integer.parseInt(wicketfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where wickets > ?");
                                prep.setInt(1, search_wickets);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs1.next()) {
                                    setBowlers(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    i++;
                                }
                                if (i == 0) {
                                    wicketfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where wickets < ?");
                                prep.setInt(1, search_wickets);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs2.next()) {
                                    setBowlers(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    j++;
                                }
                                if (j == 0) {
                                    wicketfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where wickets = ?");
                                prep.setInt(1, search_wickets);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs3.next()) {
                                    setBowlers(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    k++;
                                }
                                if (k == 0) {
                                    wicketfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(fivewicketfield.getText())){
                        int search_fivewickets = Integer.parseInt(fivewicketfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where fivewickets > ?");
                                prep.setInt(1, search_fivewickets);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs1.next()) {
                                    setBowlers(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5), rs1.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    i++;
                                }
                                if (i == 0) {
                                    fivewicketfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where fivewickets < ?");
                                prep.setInt(1, search_fivewickets);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs2.next()) {
                                    setBowlers(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5), rs2.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    j++;
                                }
                                if (j == 0) {
                                    fivewicketfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets from " +
                                        "bowlers where fivewickets = ?");
                                prep.setInt(1, search_fivewickets);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Overs", "Wickets", "Five Wickets Hauls"},
                                        model, result);

                                while (rs3.next()) {
                                    setBowlers(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5), rs3.getInt(6));
                                    model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                            bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(),
                                            bowlers.getFivewickets()});
                                    k++;
                                }
                                if (k == 0) {
                                    fivewicketfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else {
                        try {
                            prep = con.prepareStatement("select id,name,matches,overs,wickets,fivewickets " +
                                    "from bowlers");
                            ResultSet rs = prep.executeQuery();

                            int i = 0;
                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[] {"ID", "Name", "Matches", "Overs", "Wickets", "Five Wicket Hauls"},
                                    model, result);

                            while (rs.next()) {
                                setBowlers(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5), rs.getInt(6));
                                model.addRow(new Object[]{bowlers.player.getId(), bowlers.player.getName(),
                                        bowlers.getMatches(), bowlers.getOvers(), bowlers.getWickets(), bowlers.getFivewickets()});
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
                    prep = con.prepareStatement("delete from bowlers where id = ?");
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
