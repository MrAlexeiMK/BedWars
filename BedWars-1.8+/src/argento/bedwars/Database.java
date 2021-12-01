package argento.bedwars;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Database {
	private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String url, user, password;
    
    public Database() {
    	url = "";
    	user = "";
    	password = "";
    }
    
    public Database(String url, String user, String password) {
    	this.url = url;
    	this.user = user;
    	this.password = password;
    	
    	if(!connect()) {
    		Main.instance.getLogger().warning("Cannot connect to mysql database! You can disable MySQL in config.yml");
			Bukkit.getPluginManager().disablePlugin(Main.instance);
			return;
    	}
    	createTable();
    }
    
    public boolean connect() {
		Main.instance.getLogger().info("Connecting");
		try {
			con = DriverManager.getConnection(url, user, password);;
			stmt = con.createStatement();
		} catch (SQLException e) {
			try {
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return false;
			};
		};
		return true;
	}
    
    public void addToDB(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			String query = "INSERT INTO bw_stats (name, games, kills, final_kills, beds, wins) VALUES ('"+p.getName()+"', 0, 0, 0, 0, 0)";
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e) {};
		}
	}
	
	public void createTable() {
		if(BedWars.isMysqlEnabled()) {
			String query = "CREATE TABLE bw_stats (name VARCHAR(512) UNIQUE, games INTEGER, kills INTEGER, final_kills INTEGER, beds INTEGER, wins INTEGER)";
			try {
				stmt.execute(query);
			}
			catch(SQLException e) {};
		}
	}
	
	int getGames(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int games = 0;
			String query = "SELECT games FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				games = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return games;
		}
		return 0;
	}
	
	int getGames(String name) {
		if(BedWars.isMysqlEnabled()) {
			int games = 0;
			String query = "SELECT games FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				games = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return games;
		}
		return 0;
	}
	
	public boolean playerExist(String p_name) {
		try {
			int games = getGames(p_name);
			if(games > 0) return true;
		} catch(Exception ee) {};
		return false;
	}
	
	public void checkStat(Player p, String check) {
		if(playerExist(check)) {
			int games = getGames(check);
			int wins = getWins(check);
			int beds = getBeds(check);
			int kills = getKills(check);
			int final_kills = getFinalKills(check);
			BedWars.send(p, BedWars.getConfig().getString("stat.1")+check);
			BedWars.send(p, BedWars.getConfig().getString("stat.2")+String.valueOf(games));
			BedWars.send(p, BedWars.getConfig().getString("stat.3")+String.valueOf(wins));
			BedWars.send(p, BedWars.getConfig().getString("stat.4")+String.valueOf(beds));
			BedWars.send(p, BedWars.getConfig().getString("stat.5")+String.valueOf(kills));
			BedWars.send(p, BedWars.getConfig().getString("stat.6")+String.valueOf(final_kills));
		}
		else {
			BedWars.send(p, BedWars.getLang().getString("Common.66"));
		}
	}
	
	public void addGame(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int games = getGames(p);
			String query = "UPDATE bw_stats SET games = '"+(games+1)+"' WHERE name = '"+name+"'";
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e) {};
		}
	}
	
	public void addWin(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int wins = getWins(p);
			String query = "UPDATE bw_stats SET wins = '"+(wins+1)+"' WHERE name = '"+name+"'";
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e) {};
		}
	}
	
	public void addKill(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int kills = getKills(p);
			String query = "UPDATE bw_stats SET kills = '"+(kills+1)+"' WHERE name = '"+name+"'";
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e) {};
		}
	}
	
	public void addFinalKill(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int final_kills = getFinalKills(p);
			String query = "UPDATE bw_stats SET final_kills = '"+(final_kills+1)+"' WHERE name = '"+name+"'";
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e) {};
		}
	}
	
	public void addBed(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int beds = getBeds(p);
			String query = "UPDATE bw_stats SET beds = '"+(beds+1)+"' WHERE name = '"+name+"'";
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e) {};
		}
	}
	
	int getWins(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int wins = 0;
			String query = "SELECT wins FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				wins = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return wins;
		}
		return 0;
	}
	
	int getWins(String name) {
		if(BedWars.isMysqlEnabled()) {
			int wins = 0;
			String query = "SELECT wins FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				wins = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return wins;
		}
		return 0;
	}
	
	int getKills(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int kills = 0;
			String query = "SELECT kills FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				kills = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return kills;
		}
		return 0;
	}
	
	int getKills(String name) {
		if(BedWars.isMysqlEnabled()) {
			int kills = 0;
			String query = "SELECT kills FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				kills = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return kills;
		}
		return 0;
	}

	int getFinalKills(Player p) {
		if(BedWars.isMysqlEnabled()) {
			String name = p.getName();
			int final_kills = 0;
			String query = "SELECT final_kills FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				final_kills = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return final_kills;
		}
		return 0;
	}
	
	int getFinalKills(String name) {
		if(BedWars.isMysqlEnabled()) {
			int final_kills = 0;
			String query = "SELECT final_kills FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				final_kills = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return final_kills;
		}
		return 0;
	}
	
	int getBeds(Player p) {
		if(BedWars.isMysqlEnabled()) {
			int beds = 0;
			String name = p.getName();
			String query = "SELECT beds FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				beds = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return beds;
		}
		return 0;
	}
	
	int getBeds(String name) {
		if(BedWars.isMysqlEnabled()) {
			int beds = 0;
			String query = "SELECT beds FROM bw_stats WHERE name = '"+name+"'";
			try {
				rs = stmt.executeQuery(query);
				rs.next();
				beds = rs.getInt(1);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			return beds;
		}
		return 0;
	}
}
