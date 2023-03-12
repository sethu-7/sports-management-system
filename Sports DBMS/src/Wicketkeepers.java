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

public class Wicketkeepers{
    private JPanel Main;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JTextField idfield;
    private JTextField namefield;
    private JTextField matchfield;
    private JTextField catchfield;
    private JComboBox<String> comparebox;
    private JTextField stumpfield;
    private Wicketkeepers_v wicketkeepers;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Wicketkeepers");
        frame.setContentPane(new Wicketkeepers().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Wicketkeepers() {
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

    public void setWicketkeepers(int id, String name, int matches, int catches, int stumpings){
        this.wicketkeepers = new Wicketkeepers_v();
        this.wicketkeepers.player = new Player_v();
        this.wicketkeepers.player.setId(id);
        this.wicketkeepers.player.setName(name);
        this.wicketkeepers.setMatches(matches);
        this.wicketkeepers.setCatches(catches);
        this.wicketkeepers.setStumpings(stumpings);
    }
    public void save_func(){
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name;
                int id = 0, matches = 0, catches = 0, stumpings = 0;

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
                    catches = Integer.parseInt(catchfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                try {
                    stumpings = Integer.parseInt(stumpfield.getText());
                } catch (NumberFormatException nfe){
                    System.out.println("Invalid number");
                }
                setWicketkeepers(id, name, matches, catches, stumpings);

                try {
                    PreparedStatement check = con.prepareStatement("select 1 from wicketkeepers where id = ?");
                    check.setInt(1, wicketkeepers.player.getId());

                    try (ResultSet check_result = check.executeQuery()){
                        if (check_result.next()) {
                            try {

                                prep = con.prepareStatement("update wicketkeepers set name = ?, matches = ?, " +
                                        "catches = ?, stumpings = ? where id = ?");
                                prep.setString(1, wicketkeepers.player.getName());
                                prep.setInt(2, wicketkeepers.getMatches());
                                prep.setInt(3, wicketkeepers.getCatches());
                                prep.setInt(4, wicketkeepers.getStumpings());
                                prep.setInt(5, wicketkeepers.player.getId());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Record updated.");

                                namefield.setText("");
                                matchfield.setText("");
                                catchfield.setText("");
                                stumpfield.setText("");
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
                                prep = con.prepareStatement("insert into wicketkeepers(id, name, matches, catches, " +
                                        "stumpings) values(?,?,?,?,?)");
                                prep.setInt(1, wicketkeepers.player.getId());
                                prep.setString(2, wicketkeepers.player.getName());
                                prep.setInt(3, wicketkeepers.getMatches());
                                prep.setInt(4, wicketkeepers.getCatches());
                                prep.setInt(5, wicketkeepers.getStumpings());

                                prep.executeUpdate();
                                JOptionPane.showMessageDialog(null,"Record added.");

                                idfield.setText("");
                                namefield.setText("");
                                matchfield.setText("");
                                catchfield.setText("");
                                stumpfield.setText("");
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
                            prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                    "wicketkeepers where id = ?");
                            prep.setInt(1, search_id);
                            ResultSet rs = prep.executeQuery();
                            int i = 0;

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name", "Matches", "Catches", "Stumpings"},
                                    model, result);

                            if (rs.next()) {
                                setWicketkeepers(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5));
                                model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
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
                            prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                    "wicketkeepers where name like ?");
                            prep.setString(1, "%" + search_name + "%");
                            ResultSet rs = prep.executeQuery();
                            int i = 0;

                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[]{"ID", "Name","Matches", "Catches", "Stumpings"},
                                    model, result);

                            while (rs.next()) {
                                setWicketkeepers(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5));
                                model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                        wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
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
                                prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                        "wicketkeepers where matches > ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name","Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs1.next()) {
                                    setWicketkeepers(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    i++;
                                }
                                if (i == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                        "wicketkeepers where matches < ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name","Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs2.next()) {
                                    setWicketkeepers(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    j++;
                                }
                                if (j == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                        "wicketkeepers where matches = ?");
                                prep.setInt(1, search_matches);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name","Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs3.next()) {
                                    setWicketkeepers(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    k++;
                                }
                                if (k == 0) {
                                    matchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(catchfield.getText())){
                        int search_catches = Integer.parseInt(catchfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                        "wicketkeepers where catches > ?");
                                prep.setInt(1, search_catches);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name","Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs1.next()) {
                                    setWicketkeepers(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    i++;
                                }
                                if (i == 0) {
                                    catchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                        "wicketkeepers where catches < ?");
                                prep.setInt(1, search_catches);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name","Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs2.next()) {
                                    setWicketkeepers(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    j++;
                                }
                                if (j == 0) {
                                    catchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,catches,stumpings from " +
                                        "wicketkeepers where catches = ?");
                                prep.setInt(1, search_catches);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name","Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs3.next()) {
                                    setWicketkeepers(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    k++;
                                }
                                if (k == 0) {
                                    catchfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else if (!StringUtils.isEmptyOrWhitespaceOnly(stumpfield.getText())){
                        int search_stumpings = Integer.parseInt(stumpfield.getText());
                        String comparator = Objects.requireNonNull(comparebox.getSelectedItem()).toString();
                        switch (comparator) {
                            case ">" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickes,fivewickets from " +
                                        "wicketkeepers where stumpings > ?");
                                prep.setInt(1, search_stumpings);
                                ResultSet rs1 = prep.executeQuery();
                                int i = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs1.next()) {
                                    setWicketkeepers(rs1.getInt(1), rs1.getString(2), rs1.getInt(3),
                                            rs1.getInt(4), rs1.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    i++;
                                }
                                if (i == 0) {
                                    stumpfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "<" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickes,fivewickets from " +
                                        "wicketkeepers where stumpings < ?");
                                prep.setInt(1, search_stumpings);
                                ResultSet rs2 = prep.executeQuery();
                                int j = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs2.next()) {
                                    setWicketkeepers(rs2.getInt(1), rs2.getString(2), rs2.getInt(3),
                                            rs2.getInt(4), rs2.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    j++;
                                }
                                if (j == 0) {
                                    stumpfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                            case "=" -> {
                                prep = con.prepareStatement("select id,name,matches,overs,wickes,fivewickets from " +
                                        "wicketkeepers where wickets = ?");
                                prep.setInt(1, search_stumpings);
                                ResultSet rs3 = prep.executeQuery();
                                int k = 0;

                                DefaultTableModel model = new DefaultTableModel();
                                JFrame result = new JFrame("Search Result");
                                setup_search(new String[]{"ID", "Name", "Matches", "Catches", "Stumpings"},
                                        model, result);

                                while (rs3.next()) {
                                    setWicketkeepers(rs3.getInt(1), rs3.getString(2), rs3.getInt(3),
                                            rs3.getInt(4), rs3.getInt(5));
                                    model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                            wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
                                    k++;
                                }
                                if (k == 0) {
                                    stumpfield.setText("");
                                    JOptionPane.showMessageDialog(null, "Invalid Product ID");
                                }
                            }
                        }
                    } else {
                        try {
                            prep = con.prepareStatement("select id,name,matches,catches,stumpings from wicketkeepers");
                            ResultSet rs = prep.executeQuery();

                            int i = 0;
                            DefaultTableModel model = new DefaultTableModel();
                            JFrame result = new JFrame("Search Result");
                            setup_search(new String[] {"ID", "Name", "Matches", "Catches", "Stumpings"},
                                    model, result);

                            while (rs.next()) {
                                setWicketkeepers(rs.getInt(1), rs.getString(2), rs.getInt(3),
                                        rs.getInt(4), rs.getInt(5));
                                model.addRow(new Object[]{wicketkeepers.player.getId(), wicketkeepers.player.getName(),
                                        wicketkeepers.getMatches(), wicketkeepers.getCatches(), wicketkeepers.getStumpings()});
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
                    prep = con.prepareStatement("delete from wicketkeepers where id = ?");
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
