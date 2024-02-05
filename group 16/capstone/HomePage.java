package group16.capstone;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class HomePage extends javax.swing.JFrame {

    private boolean login;
    private String logintype;
    private List<team> teams = new ArrayList<>();
    private List<player> playerz = new ArrayList<>();
    private match live;
    private String onstrike;//keep track of batting team
    private int BMA;//batsman batting A
    private int BMB;//batsman batting B
    private int CurrentB;//Currently on strike
    private int CurrentBowl;
    private int PrevBowl;
    private int balls;
    private int runs;
    private int runsfs;
    private int runsxs;

    public void addplayers() {
        int t = 0;
        while (teams.size() > t) {
            teams.get(t).addplayer();
            t++;
        }
    }

    public void bowlinglimit() {
        //limit the bowler to 10 per innings Add to overs
    }

    public void setTeams() {
        int tId, gamewon, gamelost, gamedrawn, gplayed, counter;
        String name, coachname;
        counter = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/capstone", "root", "");
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select * from teams");
            while (rs.next()) {
                tId = rs.getInt(1);
                name = rs.getString(2);
                coachname = rs.getString(3);
                gplayed = rs.getInt(4);
                gamewon = rs.getInt(5);
                gamedrawn = rs.getInt(6);
                gamelost = rs.getInt(7);
                teams.add(new team(tId, name, coachname, gplayed, gamewon, gamedrawn, gamelost));
            }
            System.out.println("teams added");
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to connect and add Teams!", "DB Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setteamlists() {
        DefaultListModel listmodel = new DefaultListModel();
        TeamListadmin.setModel(listmodel);

        for (int i = 0; i < teams.size(); i++) {
            listmodel.addElement(teams.get(i).getTeamname());
        }

    }

    public void setplayerlists(int index) {
        teams.get(index).addplayer();
        playerz = teams.get(index).getPlayers();
        DefaultListModel listmodel = new DefaultListModel();
        Playerslistadmin.setModel(listmodel);
        Playeradminlbl.setText(" Players for Team :  " + teams.get(index).getTeamname());
        for (int i = 0; i < playerz.size(); i++) {
            listmodel.addElement(playerz.get(i).getName());
        }

    }

    public void setbatiing() {
        BatsmanOutCombo.addItem((String) Battingtable.getModel().getValueAt(BMA, 0));
        BatsmanOutCombo.addItem((String) Battingtable.getModel().getValueAt(BMB, 0));
    }

    public void setbattingtable(int index) {
        teams.get(index).addplayer();
        playerz = teams.get(index).getPlayers();
        DefaultTableModel model = new DefaultTableModel();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        Battingtable.setModel(model);
        model.addColumn("Batsman Name");
        model.addColumn("Runs");
        model.addColumn("4s");
        model.addColumn("6s");
        model.addColumn("How Out");
        model.addColumn("Bowler");
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        Battingtable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        Battingtable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        Battingtable.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );
        Battingtable.getColumnModel().getColumn(4).setCellRenderer( centerRenderer );
        Battingtable.getColumnModel().getColumn(5).setCellRenderer( centerRenderer );
        for (int i = 0; i < 11; i++) {
            model.addRow(new Object[]{playerz.get(i).getName(), 0, 0, 0});
        }
    }

    public void setbowlingtable(int index) {
        teams.get(index).addplayer();
        playerz = teams.get(index).getPlayers();
        DefaultTableModel model = new DefaultTableModel();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        Bowlingtable.setModel(model);
        model.addColumn("Bowler Name");
        model.addColumn("Runs");
        model.addColumn("Overs");
        model.addColumn("Wickets");
        model.addColumn("Extra");
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        Bowlingtable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        Bowlingtable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        Bowlingtable.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );
        Bowlingtable.getColumnModel().getColumn(4).setCellRenderer( centerRenderer );
      
       
        for (int i = 0; i < 12; i++) {
            model.addRow(new Object[]{playerz.get(i).getName(), 0, 0, 0, 0});
        }
    }

    public void overs() {
        int r;
        if ("A".equals(onstrike)) {
            if ((balls % 6) == 0) {
                r = live.getTeamAovers() + 1;
                live.setTeamAovers(r);
                Overstxt.setText(String.valueOf(r));
                Bowlingtable.clearSelection();
                PrevBowl = CurrentBowl;
                r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 2);
                r++;
                Bowlingtable.getModel().setValueAt(r, CurrentBowl, 2);
                
                }
        } else {
            if ((balls % 6) == 0) {
                r = live.getTeambovers() + 1;
                live.setTeambovers(r);
                Overstxt.setText(String.valueOf(r));
                Bowlingtable.clearSelection();
                PrevBowl = CurrentBowl;
                r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 2);
                r++;
                Bowlingtable.getModel().setValueAt(r, CurrentBowl, 2);

            }
        }
    }

    public boolean isLogin() {
        return login;
    }

    private void setLogin(boolean login) {
        this.login = login;
    }

    public String getLogintype() {
        return logintype;
    }

    public void setLogintype(String logintype) {
        this.logintype = logintype;
    }
    private CardLayout card;//home screen card layout

    public HomePage() {
        initComponents();//initialize componenets
        setLogin(false);
        card = (CardLayout) Mainpanel.getLayout();
        setLogintype("user");
        setTeams();//adding teams 
        addplayers();
        setteamlists();
        //DB connection         
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        Mainpanel = new javax.swing.JPanel();
        HomePage = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        LiveMatch = new javax.swing.JPanel();
        BattingTeam = new javax.swing.JLabel();
        BatsmenPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Battingtable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        BatsmenOnStrikelbl = new javax.swing.JLabel();
        addrun1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        BatsmanOutCombo = new javax.swing.JComboBox<>();
        DismissalCombo1 = new javax.swing.JComboBox<>();
        Confirmoutbtn = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        BowlingPane = new javax.swing.JPanel();
        BowlingTeam = new javax.swing.JLabel();
        bowlinglbl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Bowlingtable = new javax.swing.JTable();
        NBButton = new javax.swing.JButton();
        lblbowler = new javax.swing.JLabel();
        lblBowlername = new javax.swing.JLabel();
        Overcountlbl = new javax.swing.JLabel();
        Ballcountlbl = new javax.swing.JLabel();
        Overstxt = new javax.swing.JTextField();
        ballstxt = new javax.swing.JTextField();
        DotButton = new javax.swing.JButton();
        WideButton = new javax.swing.JButton();
        TeamBpanel = new javax.swing.JPanel();
        TeamBlbl = new javax.swing.JLabel();
        teamBruns = new javax.swing.JTextField();
        teamBExtra = new javax.swing.JTextField();
        teamBWickets = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        TeamApanel = new javax.swing.JPanel();
        TeamAlbl = new javax.swing.JLabel();
        teamAruns = new javax.swing.JTextField();
        teamAExtra = new javax.swing.JTextField();
        teamAWickets = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        Statistics = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        TeamSelect = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Selectteamlist = new javax.swing.JList<>();
        BattingTeam1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        Selectteamlist1 = new javax.swing.JList<>();
        BattingTeam2 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Selectedteam = new javax.swing.JList<>();
        Battingorderup = new javax.swing.JButton();
        Battingorderdown = new javax.swing.JButton();
        LeagueAdministration = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        LMButton = new javax.swing.JButton();
        BattingTeam3 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        BattingTeam4 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        TeamListadmin = new javax.swing.JList<>();
        jScrollPane12 = new javax.swing.JScrollPane();
        Playerslistadmin = new javax.swing.JList<>();
        BattingTeam6 = new javax.swing.JLabel();
        Playeradminlbl = new javax.swing.JLabel();
        addplayer = new javax.swing.JButton();
        Addteam = new javax.swing.JButton();
        HPButton = new javax.swing.JButton();
        StButton = new javax.swing.JButton();
        TSButton = new javax.swing.JButton();
        AdminButton = new javax.swing.JButton();

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        jCheckBoxMenuItem2.setSelected(true);
        jCheckBoxMenuItem2.setText("jCheckBoxMenuItem2");

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 700));
        setMinimumSize(new java.awt.Dimension(1000, 700));
        setPreferredSize(new java.awt.Dimension(1000, 700));
        setSize(new java.awt.Dimension(1000, 700));

        Mainpanel.setMaximumSize(new java.awt.Dimension(980, 600));
        Mainpanel.setMinimumSize(new java.awt.Dimension(980, 600));
        Mainpanel.setPreferredSize(new java.awt.Dimension(980, 600));
        Mainpanel.setLayout(new java.awt.CardLayout());

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setText("Fixtures");
        jScrollPane3.setViewportView(jTextArea2);

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jTextArea3.setText("Best bowlers\n");
        jScrollPane4.setViewportView(jTextArea3);

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jTextArea4.setText("Pos\tTeam\tPoints\n1)\tUWC\t12\n2)\tUCT\t11\n3)\tMarties\t9");
        jScrollPane5.setViewportView(jTextArea4);

        jLabel1.setText("UWC WINS NAILBITING GAME  AGAINST  UCT!");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(17, 17, 17))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jTextArea5.setText("Best Batsmen\n");
        jScrollPane6.setViewportView(jTextArea5);

        javax.swing.GroupLayout HomePageLayout = new javax.swing.GroupLayout(HomePage);
        HomePage.setLayout(HomePageLayout);
        HomePageLayout.setHorizontalGroup(
            HomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePageLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(HomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                .addGroup(HomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HomePageLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(216, 216, 216))
                    .addGroup(HomePageLayout.createSequentialGroup()
                        .addGroup(HomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        HomePageLayout.setVerticalGroup(
            HomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HomePageLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(HomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HomePageLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE))
                    .addGroup(HomePageLayout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addGap(13, 13, 13)))
                .addGroup(HomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HomePageLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(109, 109, 109))
        );

        Mainpanel.add(HomePage, "HP");

        BattingTeam.setBackground(new java.awt.Color(204, 204, 204));
        BattingTeam.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BattingTeam.setForeground(new java.awt.Color(255, 255, 255));
        BattingTeam.setText("Team On Strike");

        BatsmenPanel.setBackground(new java.awt.Color(102, 102, 102));

        Battingtable.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        Battingtable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BattingtableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Battingtable);

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(" On Strike");

        BatsmenOnStrikelbl.setBackground(new java.awt.Color(204, 204, 204));
        BatsmenOnStrikelbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BatsmenOnStrikelbl.setForeground(new java.awt.Color(255, 255, 255));
        BatsmenOnStrikelbl.setText("X player");

        addrun1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addrun1.setForeground(new java.awt.Color(2, 2, 2));
        addrun1.setText("+1");
        addrun1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addrun1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(2, 2, 2));
        jButton2.setText("+2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(2, 2, 2));
        jButton3.setText("+3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(2, 2, 2));
        jButton4.setText("+4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(2, 2, 2));
        jButton5.setText("+6");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Dismissal:");

        BatsmanOutCombo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        BatsmanOutCombo.setForeground(new java.awt.Color(255, 255, 255));
        BatsmanOutCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Batsman:" }));
        BatsmanOutCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BatsmanOutComboActionPerformed(evt);
            }
        });

        DismissalCombo1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        DismissalCombo1.setForeground(new java.awt.Color(255, 255, 255));
        DismissalCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select here:", "Bowled", "Caught", "Leg Before Wicket", "Run-out", "Stumped", "Other" }));
        DismissalCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DismissalCombo1ActionPerformed(evt);
            }
        });

        Confirmoutbtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Confirmoutbtn.setForeground(new java.awt.Color(255, 255, 255));
        Confirmoutbtn.setText("Confirm Out");
        Confirmoutbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfirmoutbtnActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Runs scored:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Select Name:");

        javax.swing.GroupLayout BatsmenPanelLayout = new javax.swing.GroupLayout(BatsmenPanel);
        BatsmenPanel.setLayout(BatsmenPanelLayout);
        BatsmenPanelLayout.setHorizontalGroup(
            BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BatsmenPanelLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BatsmenPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(25, 25, 25)
                        .addComponent(BatsmenOnStrikelbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(BatsmenPanelLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(29, 29, 29)
                                .addComponent(addrun1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(BatsmenPanelLayout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24))
                    .addGroup(BatsmenPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 22, Short.MAX_VALUE)))
                .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(DismissalCombo1, 0, 203, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BatsmanOutCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Confirmoutbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(112, 112, 112))
        );
        BatsmenPanelLayout.setVerticalGroup(
            BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BatsmenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(BatsmenPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DismissalCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BatsmanOutCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Confirmoutbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BatsmenPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton3)
                                .addComponent(addrun1)
                                .addComponent(jButton4))
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton5)
                            .addComponent(jButton2)))
                    .addGroup(BatsmenPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(BatsmenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(BatsmenOnStrikelbl))))
                .addGap(20, 20, 20))
        );

        BowlingPane.setBackground(new java.awt.Color(102, 102, 102));

        BowlingTeam.setBackground(new java.awt.Color(204, 204, 204));
        BowlingTeam.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BowlingTeam.setForeground(new java.awt.Color(255, 255, 255));
        BowlingTeam.setText("X");

        bowlinglbl.setBackground(new java.awt.Color(204, 204, 204));
        bowlinglbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        bowlinglbl.setForeground(new java.awt.Color(255, 255, 255));
        bowlinglbl.setText("Team : ");

        Bowlingtable.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        Bowlingtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(Bowlingtable);
        if (Bowlingtable.getColumnModel().getColumnCount() > 0) {
            Bowlingtable.getColumnModel().getColumn(0).setResizable(false);
            Bowlingtable.getColumnModel().getColumn(1).setResizable(false);
            Bowlingtable.getColumnModel().getColumn(2).setResizable(false);
            Bowlingtable.getColumnModel().getColumn(3).setResizable(false);
        }

        NBButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        NBButton.setForeground(new java.awt.Color(2, 2, 2));
        NBButton.setText("No Ball");
        NBButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NBButtonActionPerformed(evt);
            }
        });

        lblbowler.setBackground(new java.awt.Color(204, 204, 204));
        lblbowler.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        lblbowler.setForeground(new java.awt.Color(255, 255, 255));
        lblbowler.setText("Bowler On Strike:");

        lblBowlername.setBackground(new java.awt.Color(204, 204, 204));
        lblBowlername.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        lblBowlername.setForeground(new java.awt.Color(255, 255, 255));
        lblBowlername.setText("X player");

        Overcountlbl.setBackground(new java.awt.Color(204, 204, 204));
        Overcountlbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Overcountlbl.setForeground(new java.awt.Color(255, 255, 255));
        Overcountlbl.setText("Overs");

        Ballcountlbl.setBackground(new java.awt.Color(204, 204, 204));
        Ballcountlbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Ballcountlbl.setForeground(new java.awt.Color(255, 255, 255));
        Ballcountlbl.setText("Balls");

        Overstxt.setEditable(false);
        Overstxt.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Overstxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ballstxt.setEditable(false);
        ballstxt.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        ballstxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        DotButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        DotButton.setForeground(new java.awt.Color(2, 2, 2));
        DotButton.setText("Dot Ball");
        DotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DotButtonActionPerformed(evt);
            }
        });

        WideButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        WideButton.setForeground(new java.awt.Color(2, 2, 2));
        WideButton.setText("Wide");
        WideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WideButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BowlingPaneLayout = new javax.swing.GroupLayout(BowlingPane);
        BowlingPane.setLayout(BowlingPaneLayout);
        BowlingPaneLayout.setHorizontalGroup(
            BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BowlingPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(BowlingPaneLayout.createSequentialGroup()
                        .addComponent(bowlinglbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BowlingTeam)
                        .addGap(156, 156, 156)
                        .addComponent(Overcountlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(Overstxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Ballcountlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBowlername)
                    .addComponent(lblbowler)
                    .addComponent(NBButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ballstxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(WideButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        BowlingPaneLayout.setVerticalGroup(
            BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BowlingPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bowlinglbl)
                        .addComponent(BowlingTeam))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Overcountlbl)
                        .addComponent(Overstxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Ballcountlbl)
                        .addComponent(ballstxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(BowlingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BowlingPaneLayout.createSequentialGroup()
                        .addComponent(DotButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                        .addComponent(WideButton)
                        .addGap(18, 18, 18)
                        .addComponent(NBButton)
                        .addGap(26, 26, 26)
                        .addComponent(lblbowler)
                        .addGap(18, 18, 18)
                        .addComponent(lblBowlername)
                        .addGap(21, 21, 21))
                    .addGroup(BowlingPaneLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        TeamBpanel.setBackground(new java.awt.Color(102, 102, 102));
        TeamBpanel.setMinimumSize(new java.awt.Dimension(310, 110));
        TeamBpanel.setPreferredSize(new java.awt.Dimension(310, 110));

        TeamBlbl.setBackground(new java.awt.Color(204, 204, 204));
        TeamBlbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        TeamBlbl.setForeground(new java.awt.Color(255, 255, 255));
        TeamBlbl.setText("Team : Y");

        teamBruns.setEditable(false);
        teamBruns.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        teamBExtra.setEditable(false);
        teamBExtra.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        teamBWickets.setEditable(false);
        teamBWickets.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Runs");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Wickets");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Extras");

        javax.swing.GroupLayout TeamBpanelLayout = new javax.swing.GroupLayout(TeamBpanel);
        TeamBpanel.setLayout(TeamBpanelLayout);
        TeamBpanelLayout.setHorizontalGroup(
            TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TeamBpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TeamBpanelLayout.createSequentialGroup()
                        .addComponent(TeamBlbl)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(TeamBpanelLayout.createSequentialGroup()
                        .addGap(0, 14, Short.MAX_VALUE)
                        .addGroup(TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(teamBruns, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(teamBWickets, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(teamBExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGap(21, 21, 21))))
        );
        TeamBpanelLayout.setVerticalGroup(
            TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TeamBpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TeamBlbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGap(12, 12, 12)
                .addGroup(TeamBpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teamBExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teamBWickets, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teamBruns, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        TeamApanel.setBackground(new java.awt.Color(102, 102, 102));
        TeamApanel.setMinimumSize(new java.awt.Dimension(310, 110));

        TeamAlbl.setBackground(new java.awt.Color(204, 204, 204));
        TeamAlbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        TeamAlbl.setForeground(new java.awt.Color(255, 255, 255));
        TeamAlbl.setText("Team : X");

        teamAruns.setEditable(false);
        teamAruns.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        teamAExtra.setEditable(false);
        teamAExtra.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        teamAWickets.setEditable(false);
        teamAWickets.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Runs");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Wickets");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Extras");

        javax.swing.GroupLayout TeamApanelLayout = new javax.swing.GroupLayout(TeamApanel);
        TeamApanel.setLayout(TeamApanelLayout);
        TeamApanelLayout.setHorizontalGroup(
            TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TeamApanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TeamApanelLayout.createSequentialGroup()
                        .addComponent(TeamAlbl)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(TeamApanelLayout.createSequentialGroup()
                        .addGap(0, 14, Short.MAX_VALUE)
                        .addGroup(TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(teamAruns, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addGroup(TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(teamAWickets, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addGap(18, 18, 18)
                        .addGroup(TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(teamAExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addGap(21, 21, 21))))
        );
        TeamApanelLayout.setVerticalGroup(
            TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TeamApanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TeamAlbl)
                .addGap(18, 18, 18)
                .addGroup(TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TeamApanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teamAWickets, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teamAExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teamAruns, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout LiveMatchLayout = new javax.swing.GroupLayout(LiveMatch);
        LiveMatch.setLayout(LiveMatchLayout);
        LiveMatchLayout.setHorizontalGroup(
            LiveMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LiveMatchLayout.createSequentialGroup()
                .addGroup(LiveMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LiveMatchLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(BattingTeam))
                    .addGroup(LiveMatchLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(LiveMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BatsmenPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(LiveMatchLayout.createSequentialGroup()
                                .addComponent(BowlingPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(LiveMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(TeamApanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TeamBpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))))))
                .addGap(14, 14, 14))
        );
        LiveMatchLayout.setVerticalGroup(
            LiveMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LiveMatchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BattingTeam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BatsmenPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(LiveMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(LiveMatchLayout.createSequentialGroup()
                        .addComponent(TeamApanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(TeamBpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BowlingPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Mainpanel.add(LiveMatch, "LM");

        jPanel6.setLayout(new java.awt.CardLayout());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane11.setViewportView(jTable3);

        jPanel6.add(jScrollPane11, "card2");

        jButton8.setText("Team");

        javax.swing.GroupLayout StatisticsLayout = new javax.swing.GroupLayout(Statistics);
        Statistics.setLayout(StatisticsLayout);
        StatisticsLayout.setHorizontalGroup(
            StatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
            .addGroup(StatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        StatisticsLayout.setVerticalGroup(
            StatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, StatisticsLayout.createSequentialGroup()
                .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Mainpanel.add(Statistics, "ST");

        Selectteamlist.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "For this need to add buttons for sorting batting order ", " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane7.setViewportView(Selectteamlist);

        BattingTeam1.setBackground(new java.awt.Color(204, 204, 204));
        BattingTeam1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BattingTeam1.setForeground(new java.awt.Color(255, 255, 255));
        BattingTeam1.setText("Select Team:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BattingTeam1)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(BattingTeam1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Selectteamlist1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane9.setViewportView(Selectteamlist1);

        BattingTeam2.setBackground(new java.awt.Color(204, 204, 204));
        BattingTeam2.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BattingTeam2.setForeground(new java.awt.Color(255, 255, 255));
        BattingTeam2.setText("Seleted Full Team");

        Selectedteam.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane8.setViewportView(Selectedteam);

        Battingorderup.setText("Up");

        Battingorderdown.setText("Down");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(BattingTeam2)
                        .addContainerGap(812, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(189, 189, 189)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Battingorderup)
                            .addComponent(Battingorderdown))
                        .addGap(108, 108, 108))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(BattingTeam2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(Battingorderup)
                        .addGap(29, 29, 29)
                        .addComponent(Battingorderdown)))
                .addContainerGap())
        );

        javax.swing.GroupLayout TeamSelectLayout = new javax.swing.GroupLayout(TeamSelect);
        TeamSelect.setLayout(TeamSelectLayout);
        TeamSelectLayout.setHorizontalGroup(
            TeamSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(TeamSelectLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        TeamSelectLayout.setVerticalGroup(
            TeamSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TeamSelectLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Mainpanel.add(TeamSelect, "TS");

        jPanel4.setBackground(new java.awt.Color(102, 102, 102));

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "UCT VS UWC" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane10.setViewportView(jList1);

        LMButton.setBackground(new java.awt.Color(153, 153, 153));
        LMButton.setForeground(new java.awt.Color(2, 2, 2));
        LMButton.setText("Live Match");
        LMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LMButtonActionPerformed(evt);
            }
        });

        BattingTeam3.setBackground(new java.awt.Color(204, 204, 204));
        BattingTeam3.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BattingTeam3.setForeground(new java.awt.Color(255, 255, 255));
        BattingTeam3.setText("Select Match :");

        jButton7.setBackground(new java.awt.Color(153, 153, 153));
        jButton7.setForeground(new java.awt.Color(2, 2, 2));
        jButton7.setText("Reschedule ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(BattingTeam3, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                            .addComponent(jScrollPane10)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(LMButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))))
                .addContainerGap(268, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BattingTeam3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LMButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));

        BattingTeam4.setBackground(new java.awt.Color(204, 204, 204));
        BattingTeam4.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BattingTeam4.setForeground(new java.awt.Color(255, 255, 255));
        BattingTeam4.setText("League Administration: ");

        TeamListadmin.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        TeamListadmin.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        TeamListadmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TeamListadminMouseClicked(evt);
            }
        });
        jScrollPane13.setViewportView(TeamListadmin);

        Playerslistadmin.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        Playerslistadmin.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Select Team", " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane12.setViewportView(Playerslistadmin);

        BattingTeam6.setBackground(new java.awt.Color(204, 204, 204));
        BattingTeam6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        BattingTeam6.setForeground(new java.awt.Color(255, 255, 255));
        BattingTeam6.setText("Teams:");

        Playeradminlbl.setBackground(new java.awt.Color(204, 204, 204));
        Playeradminlbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        Playeradminlbl.setForeground(new java.awt.Color(255, 255, 255));
        Playeradminlbl.setText("Players for Team:");

        addplayer.setBackground(new java.awt.Color(153, 153, 153));
        addplayer.setForeground(new java.awt.Color(2, 2, 2));
        addplayer.setText("Add player");

        Addteam.setBackground(new java.awt.Color(153, 153, 153));
        Addteam.setForeground(new java.awt.Color(2, 2, 2));
        Addteam.setText("Add Team");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(Addteam)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(87, 87, 87)
                                .addComponent(addplayer)))
                        .addGap(39, 107, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(BattingTeam6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Playeradminlbl)
                        .addGap(111, 111, 111))))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(BattingTeam4)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BattingTeam4)
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BattingTeam6)
                    .addComponent(Playeradminlbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                    .addComponent(jScrollPane13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addplayer)
                    .addComponent(Addteam))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout LeagueAdministrationLayout = new javax.swing.GroupLayout(LeagueAdministration);
        LeagueAdministration.setLayout(LeagueAdministrationLayout);
        LeagueAdministrationLayout.setHorizontalGroup(
            LeagueAdministrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeagueAdministrationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        LeagueAdministrationLayout.setVerticalGroup(
            LeagueAdministrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeagueAdministrationLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(LeagueAdministrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(268, 268, 268))
        );

        Mainpanel.add(LeagueAdministration, "LA");

        HPButton.setBackground(new java.awt.Color(102, 102, 102));
        HPButton.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        HPButton.setForeground(new java.awt.Color(5, 5, 5));
        HPButton.setText("Home");
        HPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HPButtonActionPerformed(evt);
            }
        });

        StButton.setBackground(new java.awt.Color(102, 102, 102));
        StButton.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        StButton.setForeground(new java.awt.Color(5, 5, 5));
        StButton.setText("Statistics");
        StButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StButtonActionPerformed(evt);
            }
        });

        TSButton.setBackground(new java.awt.Color(102, 102, 102));
        TSButton.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        TSButton.setForeground(new java.awt.Color(5, 5, 5));
        TSButton.setText("Team Select");
        TSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TSButtonActionPerformed(evt);
            }
        });

        AdminButton.setBackground(new java.awt.Color(102, 102, 102));
        AdminButton.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        AdminButton.setForeground(new java.awt.Color(5, 5, 5));
        AdminButton.setText("AdminButton");
        AdminButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Mainpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(HPButton)
                        .addGap(40, 40, 40)
                        .addComponent(AdminButton)
                        .addGap(40, 40, 40)
                        .addComponent(StButton)
                        .addGap(40, 40, 40)
                        .addComponent(TSButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(HPButton, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addComponent(AdminButton, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                    .addComponent(TSButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(StButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(Mainpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void HPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HPButtonActionPerformed
        // TODO add your handling code here:
        card.show(Mainpanel, "HP");
    }//GEN-LAST:event_HPButtonActionPerformed

    private void LMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LMButtonActionPerformed
        // TODO add your handling code here:
        card.show(Mainpanel, "LM");
        BattingTeam.setText("Team on Strike: " + teams.get(1).getTeamname());
        TeamAlbl.setText(teams.get(1).getTeamname());
        onstrike = "A";
        TeamBlbl.setText(teams.get(2).getTeamname());
        bowlinglbl.setText(teams.get(2).getTeamname());
        setbattingtable(1);
        setbowlingtable(2);
        teamAruns.setText("0");
        teamAExtra.setText("0");
        teamAWickets.setText("0");
        teamBruns.setText("0");
        teamBExtra.setText("0");
        teamBWickets.setText("0");
        Overstxt.setText("0");
        ballstxt.setText("0");
        balls = 0;
        BMA = 0;
        BMB = 1;
        CurrentB = -1;
        CurrentBowl = -1;
        PrevBowl=-1;
        live = new match(0, 0, 0, 0, 0, 0, 0, 0);
        setbatiing();
    }//GEN-LAST:event_LMButtonActionPerformed

    private void StButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StButtonActionPerformed
        // TODO add your handling code here:
        card.show(Mainpanel, "ST");
    }//GEN-LAST:event_StButtonActionPerformed

    private void TSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSButtonActionPerformed
        // TODO add your handling code here:
        card.show(Mainpanel, "TS");
    }//GEN-LAST:event_TSButtonActionPerformed

    private void AdminButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminButtonActionPerformed
        // TODO add your handling code here:
        card.show(Mainpanel, "LA");
    }//GEN-LAST:event_AdminButtonActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {
            CurrentBowl = Bowlingtable.getSelectedRow();
            CurrentB = Battingtable.getSelectedRow();
            int r;
            if ("A".equals(onstrike)) {
                r = live.getTeamAscore() + 4;
                live.setTeamAscore(r);
                teamAruns.setText(String.valueOf(r));
            } else {
                r = live.getTeamBscore() + 4;
                live.setTeamBscore(r);
                teamBruns.setText(String.valueOf(r));
            }
            balls++;
            ballstxt.setText(String.valueOf(balls));
            runs = (int) Battingtable.getModel().getValueAt(Battingtable.getSelectedRow(), 1) + 4;
            Battingtable.getModel().setValueAt(runs, Battingtable.getSelectedRow(), 1);
            r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 1);
            r++;
            Bowlingtable.getModel().setValueAt(r, CurrentBowl, 1);
            overs();
        }else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {
            CurrentBowl = Bowlingtable.getSelectedRow();
            CurrentB = Battingtable.getSelectedRow();
            int r;
            if ("A".equals(onstrike)) {
                r = live.getTeamAscore() + 6;
                live.setTeamAscore(r);
                teamAruns.setText(String.valueOf(r));
            } else {
                r = live.getTeamBscore() + 6;
                live.setTeamBscore(r);
                teamBruns.setText(String.valueOf(r));
            }
            balls++;
            ballstxt.setText(String.valueOf(balls));
            runs = (int) Battingtable.getModel().getValueAt(Battingtable.getSelectedRow(), 1) + 6;
            Battingtable.getModel().setValueAt(runs, Battingtable.getSelectedRow(), 1);
            r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 1);
            r++;
            Bowlingtable.getModel().setValueAt(r, CurrentBowl, 1);
            overs();
        }else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
       // TODO add your handling code here:
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {
            CurrentBowl = Bowlingtable.getSelectedRow();
            CurrentB = Battingtable.getSelectedRow();
            int r;
            if ("A".equals(onstrike)) {
                r = live.getTeamAscore() + 3;
                live.setTeamAscore(r);
                teamAruns.setText(String.valueOf(r));
            } else {
                r = live.getTeamBscore() + 3;
                live.setTeamBscore(r);
                teamBruns.setText(String.valueOf(r));
            }
            balls++;
            ballstxt.setText(String.valueOf(balls));
            runs = (int) Battingtable.getModel().getValueAt(Battingtable.getSelectedRow(), 1) + 3;
            Battingtable.getModel().setValueAt(runs, Battingtable.getSelectedRow(), 1);
            r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 1);
            r++;
            Bowlingtable.getModel().setValueAt(r, CurrentBowl, 1);
            overs();
        }else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}


    
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        // TODO add your handling code here:
       // TODO add your handling code here:
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {
            CurrentBowl = Bowlingtable.getSelectedRow();
            CurrentB = Battingtable.getSelectedRow();
            int r;
            if ("A".equals(onstrike)) {
                r = live.getTeamAscore() + 2;
                live.setTeamAscore(r);
                teamAruns.setText(String.valueOf(r));
            } else {
                r = live.getTeamBscore() + 2;
                live.setTeamBscore(r);
                teamBruns.setText(String.valueOf(r));
            }
            balls++;
            ballstxt.setText(String.valueOf(balls));
            runs = (int) Battingtable.getModel().getValueAt(Battingtable.getSelectedRow(), 1) + 2;
            Battingtable.getModel().setValueAt(runs, Battingtable.getSelectedRow(), 1);
            r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 1);
            r++;
            Bowlingtable.getModel().setValueAt(r, CurrentBowl, 1);
            
            if (BMA == CurrentB) {
                Battingtable.setRowSelectionInterval(BMB, 1);
                CurrentB = BMB;
            } else {
                Battingtable.setRowSelectionInterval(BMA, 1);
                CurrentB = BMA;
            }
            overs();
        }else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}


    
    }//GEN-LAST:event_jButton2ActionPerformed

    private void addrun1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrun1ActionPerformed
        // TODO add your handling code here:
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {
            System.out.println("Current before :"+CurrentB);
            CurrentBowl = Bowlingtable.getSelectedRow();
            if(CurrentB==-1){CurrentB = Battingtable.getSelectedRow();}
            int r;
            if ("A".equals(onstrike)) {
                r = live.getTeamAscore() + 1;
                live.setTeamAscore(r);
                teamAruns.setText(String.valueOf(r));
            } else {
                r = live.getTeamBscore() + 1;
                live.setTeamBscore(r);
                teamBruns.setText(String.valueOf(r));
            }
            balls++;
            ballstxt.setText(String.valueOf(balls));
            runs = (int) Battingtable.getModel().getValueAt(Battingtable.getSelectedRow(), 1) + 1;
            Battingtable.getModel().setValueAt(runs, CurrentB, 1);
            r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 1);
            r++;
            Bowlingtable.getModel().setValueAt(r, CurrentBowl, 1);
            System.out.println("group16.capstone.HomePage.addrun1ActionPerformed()");
            System.out.println("BMA "+BMA);
            System.out.println("BMB "+BMB);
            System.out.println("selected "+Battingtable.getSelectedRow());
            System.out.println("Current "+CurrentB);
            if (BMA == CurrentB) {
                Battingtable.setRowSelectionInterval(BMB, 1);
                CurrentB = BMB;
            } else {
                Battingtable.setRowSelectionInterval(BMA, 1);
                CurrentB = BMA;
            }
            overs();
        }else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}


    }//GEN-LAST:event_addrun1ActionPerformed

    private void TeamListadminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TeamListadminMouseClicked
        // TODO add your handling code here:
        int i = TeamListadmin.getSelectedIndex();
        setplayerlists(i);
    }//GEN-LAST:event_TeamListadminMouseClicked
private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed
private void BatsmanOutComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BatsmanOutComboActionPerformed
    // TODO add your handling code here:
    }//GEN-LAST:event_BatsmanOutComboActionPerformed
private void DismissalCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DismissalCombo1ActionPerformed
    // TODO add your handling code here:
    }//GEN-LAST:event_DismissalCombo1ActionPerformed
private void ConfirmoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmoutbtnActionPerformed
    // TODO add your handling code here:
    int k,r;
    String i;
    if(DismissalCombo1.getSelectedIndex()==0){JOptionPane.showMessageDialog(null, "Please select the Dismisal method in the drop down.", "Dismissal", JOptionPane.ERROR_MESSAGE);}else
     if(BatsmanOutCombo.getSelectedIndex()==0){JOptionPane.showMessageDialog(null, "Please select the Player  in the drop down.", "Player", JOptionPane.ERROR_MESSAGE);}else{
        if(BatsmanOutCombo.getSelectedIndex()==1){
         Battingtable.getModel().setValueAt(DismissalCombo1.getSelectedItem(), BMA, 4);
         i =(String)Bowlingtable.getModel().getValueAt(CurrentBowl, 0);
         k =(int) Bowlingtable.getModel().getValueAt(CurrentBowl, 3);
         k++;
         Bowlingtable.getModel().setValueAt(k, CurrentBowl, 3);
         Battingtable.getModel().setValueAt(i, BMA, 5); 
         if(CurrentB==BMA){
         while(i!="K"){
             i = (String)Battingtable.getModel().getValueAt(BMA, 4);
             if(BMA==BMB){
                BMA++;
             }else 
                 if(i==null){break;}
                 else{BMA++;}
         }
         CurrentB=BMA;
         Battingtable.clearSelection();
         Battingtable.setRowSelectionInterval(BMA, 1);
         //Battingtable.setR
        }else{
        while(i!="K"){
             i = (String)Battingtable.getModel().getValueAt(BMA, 4);
             if(BMA==BMB){
                BMA++;
             }else 
                 if(i==null){break;}
                 else{BMA++;}
         }
         }
        }
        
        if(BatsmanOutCombo.getSelectedIndex()==2){

         Battingtable.getModel().setValueAt(DismissalCombo1.getSelectedItem(), BMB, 4);
         i =(String)Bowlingtable.getModel().getValueAt(CurrentBowl, 0);
         k =(int) Bowlingtable.getModel().getValueAt(CurrentBowl, 3);
         k++;
         Bowlingtable.getModel().setValueAt(k, CurrentBowl, 3);
         Battingtable.getModel().setValueAt(i, BMB, 5); 
         if(CurrentB==BMB){
         while(i!="K"){
             i = (String)Battingtable.getModel().getValueAt(BMB, 4);
             if(BMA==BMB){
                BMB++;
             }else 
                 if(i==null){break;}
                 else{BMB++;}
         }
         CurrentB=BMB;
         Battingtable.clearSelection();
         Battingtable.setRowSelectionInterval(BMB, 1);
         //Battingtable.setR
        }else{
        while(i!="K"){
             i = (String)Battingtable.getModel().getValueAt(BMB, 4);
             if(BMA==BMB){
                BMA++;
             }else 
                 if(i==null){break;}
                 else{BMB++;}
         }
         }
        
     }
    balls++;
    ballstxt.setText(String.valueOf(balls));
            r = (int) Bowlingtable.getModel().getValueAt(CurrentBowl, 1);
            r++;
            Bowlingtable.getModel().setValueAt(r, CurrentBowl, 1);
            overs();
     }
    }//GEN-LAST:event_ConfirmoutbtnActionPerformed
private void NBButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NBButtonActionPerformed
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {
            CurrentBowl = Bowlingtable.getSelectedRow();
            CurrentB = Battingtable.getSelectedRow();    // TODO add your handling code here:
            int r;
            int E;
            if ("A".equals(onstrike)) {
                r = live.getTeamAscore() + 1;
                E=live.getTeamAextra()+1;
                live.setTeamAscore(r);
                live.setTeamAextra(E);
                teamAruns.setText(String.valueOf(r));
                teamAExtra.setText(String.valueOf(E));
            } else {
                r = live.getTeamBscore() + 1;
                E=live.getTeamBextra()+1;
                live.setTeamBscore(r);
                live.setTeamBextra(E);
                teamBruns.setText(String.valueOf(r));
                teamBExtra.setText(String.valueOf(E));
            }}else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}
    }//GEN-LAST:event_NBButtonActionPerformed

    private void BattingtableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BattingtableMouseClicked
        // TODO add your handling code here:

        BatsmenOnStrikelbl.setText((String) Battingtable.getModel().getValueAt(Battingtable.getSelectedRow(), 0));
    }//GEN-LAST:event_BattingtableMouseClicked

    private void DotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DotButtonActionPerformed
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {
            CurrentBowl = Bowlingtable.getSelectedRow();
            CurrentB = Battingtable.getSelectedRow();
            balls++;
            ballstxt.setText(String.valueOf(balls));
            overs();
        }else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}
              // TODO add your handling code here:

    }//GEN-LAST:event_DotButtonActionPerformed

    private void WideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WideButtonActionPerformed
        if ((Battingtable.getSelectedRow() < 0)) {
            JOptionPane.showMessageDialog(null, "Please select a Batsman", "Batsman Error", JOptionPane.ERROR_MESSAGE);
        }else
        if ((Bowlingtable.getSelectedRow()<0)) {
            JOptionPane.showMessageDialog(null, "Please select a Bowler.", "Bowler Error", JOptionPane.ERROR_MESSAGE);
        } else if(PrevBowl !=(int)Bowlingtable.getSelectedRow()) {        // TODO add your handling code here:
            CurrentBowl = Bowlingtable.getSelectedRow();
            CurrentB = Battingtable.getSelectedRow();
            int r;
        int E;
            if ("A".equals(onstrike)) {
                r = live.getTeamAscore() + 1;
                E=live.getTeamAextra()+1;
                live.setTeamAscore(r);
                live.setTeamAextra(E);
                teamAruns.setText(String.valueOf(r));
                teamAExtra.setText(String.valueOf(E));
            } else {
                r = live.getTeamBscore() + 1;
                E=live.getTeamBextra()+1;
                live.setTeamBscore(r);
                live.setTeamBextra(E);
                teamBruns.setText(String.valueOf(r));
                teamBExtra.setText(String.valueOf(E));
            }}else{JOptionPane.showMessageDialog(null, "Please select a New  Bowler.", "NEW Bowler", JOptionPane.ERROR_MESSAGE);}
        
    }//GEN-LAST:event_WideButtonActionPerformed

    public static void main(String args[]) {
        try {//adding theme to the layout
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 999);

            UIManager.put("Component.arc", 999);
            UIManager.put("ProgressBar.arc", 999);
            UIManager.put("TextComponent.arc", 999);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new HomePage().setVisible(true);
                }
            });
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Addteam;
    private javax.swing.JButton AdminButton;
    private javax.swing.JLabel Ballcountlbl;
    private javax.swing.JComboBox<String> BatsmanOutCombo;
    private javax.swing.JLabel BatsmenOnStrikelbl;
    private javax.swing.JPanel BatsmenPanel;
    private javax.swing.JLabel BattingTeam;
    private javax.swing.JLabel BattingTeam1;
    private javax.swing.JLabel BattingTeam2;
    private javax.swing.JLabel BattingTeam3;
    private javax.swing.JLabel BattingTeam4;
    private javax.swing.JLabel BattingTeam6;
    private javax.swing.JButton Battingorderdown;
    private javax.swing.JButton Battingorderup;
    private javax.swing.JTable Battingtable;
    private javax.swing.JPanel BowlingPane;
    private javax.swing.JLabel BowlingTeam;
    private javax.swing.JTable Bowlingtable;
    private javax.swing.JButton Confirmoutbtn;
    private javax.swing.JComboBox<String> DismissalCombo1;
    private javax.swing.JButton DotButton;
    private javax.swing.JButton HPButton;
    private javax.swing.JPanel HomePage;
    private javax.swing.JButton LMButton;
    private javax.swing.JPanel LeagueAdministration;
    private javax.swing.JPanel LiveMatch;
    private javax.swing.JPanel Mainpanel;
    private javax.swing.JButton NBButton;
    private javax.swing.JLabel Overcountlbl;
    private javax.swing.JTextField Overstxt;
    private javax.swing.JLabel Playeradminlbl;
    private javax.swing.JList<String> Playerslistadmin;
    private javax.swing.JList<String> Selectedteam;
    private javax.swing.JList<String> Selectteamlist;
    private javax.swing.JList<String> Selectteamlist1;
    private javax.swing.JButton StButton;
    private javax.swing.JPanel Statistics;
    private javax.swing.JButton TSButton;
    private javax.swing.JLabel TeamAlbl;
    private javax.swing.JPanel TeamApanel;
    private javax.swing.JLabel TeamBlbl;
    private javax.swing.JPanel TeamBpanel;
    private javax.swing.JList<String> TeamListadmin;
    private javax.swing.JPanel TeamSelect;
    private javax.swing.JButton WideButton;
    private javax.swing.JButton addplayer;
    private javax.swing.JButton addrun1;
    private javax.swing.JTextField ballstxt;
    private javax.swing.JLabel bowlinglbl;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JLabel lblBowlername;
    private javax.swing.JLabel lblbowler;
    private javax.swing.JTextField teamAExtra;
    private javax.swing.JTextField teamAWickets;
    private javax.swing.JTextField teamAruns;
    private javax.swing.JTextField teamBExtra;
    private javax.swing.JTextField teamBWickets;
    private javax.swing.JTextField teamBruns;
    // End of variables declaration//GEN-END:variables

}