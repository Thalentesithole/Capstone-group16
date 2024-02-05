package group16.capstone;
import com.formdev.flatlaf.FlatLightLaf;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;
public class Capstone {
// Test connect to DB
   public static HomePage Splash = new HomePage();
    
    public static void main(String[] args) {
        Splash.main(args);  
    }
    }
 

 class player{

 private String name;
 private int playerID;
 private int age;
 
    public player(String name, int playerID, int age) {
        this.name = name;
        this.playerID = playerID;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
 }

class team {
    private int teamID;
    private String teamname;
    private String couch;
    private int gamesplayed;
    private int gameswon;
    private int gamesdrawn;
    private int gameslost;
    private static List<player>  players;
    
    public team(int teamID, String teamname, String couch, int gamesplayed, int gameswon, int gamesdrawn, int gameslost) {
        this.teamID = teamID;
        this.teamname = teamname;
        this.couch = couch;
        this.gamesplayed = gamesplayed;
        this.gameswon = gameswon;
        this.gamesdrawn = gamesdrawn;
        this.gameslost = gameslost;
    }

    public  List<player> getPlayers() {
        return players;
    }

    public static void setPlayers(List<player> players) {
        team.players = players;
    }
    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    
    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public String getCouch() {
        return couch;
    }

    public void setCouch(String couch) {
        this.couch = couch;
    }

    public int getGamesplayed() {
        return gamesplayed;
    }

    public void setGamesplayed(int gamesplayed) {
        this.gamesplayed = gamesplayed;
    }

    public int getGameswon() {
        return gameswon;
    }

    public void setGameswon(int gameswon) {
        this.gameswon = gameswon;
    }

    public int getGamesdrawn() {
        return gamesdrawn;
    }

    public void setGamesdrawn(int gamesdrawn) {
        this.gamesdrawn = gamesdrawn;
    }

    public int getGameslost() {
        return gameslost;
    }

    public void setGameslost(int gameslost) {
        this.gameslost = gameslost;
    }
     
public void addplayer(){
    players =  new ArrayList<player>();
    //Connect to db
try{
    Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/capstone","root","");
    Statement stm  = con.createStatement();
    ResultSet rs=stm.executeQuery("select * from players where `Team ID` ="+Integer.toString(teamID));
    while(rs.next()){
    players.add(new player(rs.getString(2),rs.getInt(1),rs.getInt(3)));
        //System.out.println(rs.getString(2));
    }
    }catch(Exception e){
           JOptionPane.showMessageDialog(null, "Unable to connect to add player!","DB Error!", JOptionPane.ERROR_MESSAGE);
    } 
    }
}

class match{
private int TeamAscore;
private int TeamBscore;
private int TeamAextra;
private int TeamBextra;
private int TeamAovers;
private int Teambovers;
private int TeamAwickets;
private int TeamBwickets;
    public int getTeamAscore() {
        return TeamAscore;
    }

    public void setTeamAscore(int TeamAscore) {
        this.TeamAscore = TeamAscore;
    }

    public int getTeamBscore() {
        return TeamBscore;
    }

    public void setTeamBscore(int TeamBscore) {
        this.TeamBscore = TeamBscore;
    }

    public int getTeamAextra() {
        return TeamAextra;
    }

    public void setTeamAextra(int TeamAextra) {
        this.TeamAextra = TeamAextra;
    }

    public int getTeamBextra() {
        return TeamBextra;
    }

    public void setTeamBextra(int TeamBextra) {
        this.TeamBextra = TeamBextra;
    }

    public int getTeamAovers() {
        return TeamAovers;
    }

    public void setTeamAovers(int TeamAovers) {
        this.TeamAovers = TeamAovers;
    }

    public int getTeambovers() {
        return Teambovers;
    }

    public void setTeambovers(int Teambovers) {
        this.Teambovers = Teambovers;
    }

    public int getTeamAwickets() {
        return TeamAwickets;
    }

    public void setTeamAwickets(int TeamAwickets) {
        this.TeamAwickets = TeamAwickets;
    }

    public int getTeamBwickets() {
        return TeamBwickets;
    }

    public void setTeamBwickets(int TeamBwickets) {
        this.TeamBwickets = TeamBwickets;
    }

    public match(int TeamAscore, int TeamBscore, int TeamAextra, int TeamBextra, int TeamAovers, int Teambovers, int TeamAwickets, int TeamBwickets) {
        this.TeamAscore = TeamAscore;
        this.TeamBscore = TeamBscore;
        this.TeamAextra = TeamAextra;
        this.TeamBextra = TeamBextra;
        this.TeamAovers = TeamAovers;
        this.Teambovers = Teambovers;
        this.TeamAwickets = TeamAwickets;
        this.TeamBwickets = TeamBwickets;
    }




}