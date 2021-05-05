package argento.bedwars;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TNT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {
	private static final String url2 = "jdbc:mysql://localhost:3306/minigames?useSSL=false&autoReConnect=true";
	private static final String user = "root";
    private static final String password = "";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	File file = null;
	FileConfiguration config = null;
    private static Connection con2;
    private static Statement stmt2;
    private static ResultSet rs2;
    private String arena;
    int count_players = 0;
    ItemStack item, lobby;
    Inventory inv;
    Inventory team_inv;
    int max_length_team = 0;
    private List<Pair> perks1 = new ArrayList<Pair>();
    private List<Pair> perks2 = new ArrayList<Pair>();
    private List<Pair> perks3 = new ArrayList<Pair>();
    private List<Pair> perks4 = new ArrayList<Pair>();
    private List<Pair> perks5 = new ArrayList<Pair>();
    private HashMap<String, Integer> diamonds = new HashMap<String, Integer>();
    private HashMap<String, Integer> hp = new HashMap<String, Integer>();
    private HashMap<String, Integer> golds = new HashMap<String, Integer>();
    private HashMap<String, Integer> irons = new HashMap<String, Integer>();
    private HashMap<String, Integer> emeralds = new HashMap<String, Integer>();
    private HashMap<String, Integer> fire = new HashMap<String, Integer>();
    private HashMap<String, Integer> cold = new HashMap<String, Integer>();
    private HashMap<String, Integer> prot = new HashMap<String, Integer>();
    private HashMap<String, Integer> extra_damage = new HashMap<String, Integer>();
    private HashMap<String, Integer> poison = new HashMap<String, Integer>();
    private HashSet<String> infinity = new HashSet<String>();
    private HashSet<String> regen = new HashSet<String>();
    private HashSet<String> fast = new HashSet<String>();
    ItemStack p1, p2, p3, p4, p5;
    private Economy econ;

    HashMap<String, Inventory> player_votes = new HashMap<String, Inventory>();
    HashMap<String, List<ItemStack>> player_kit = new HashMap<String, List<ItemStack>>();

    private HashSet<String> bed_alive = new HashSet<String>();
    private HashSet<String> invis = new HashSet<String>();
    private HashMap<String, String> player_teams = new HashMap<String, String>();
    private HashMap<String, Integer> stage = new HashMap<String, Integer>();
    private HashMap<String, Integer> team_swords = new HashMap<String, Integer>();
    private HashMap<String, Integer> team_armor = new HashMap<String, Integer>();
    private HashMap<String, Integer> team_speed = new HashMap<String, Integer>();
    private HashMap<String, Boolean> team_trap = new HashMap<String, Boolean>();
    private HashMap<String, List<String>> teams = new HashMap<String, List<String>>();
    private HashMap<String, Inventory> saved_inv = new HashMap<String, Inventory>();
    private HashMap<String, Inventory> saved_up = new HashMap<String, Inventory>();
    private HashMap<String, ItemStack> player_helmet = new HashMap<String, ItemStack>();
    private HashMap<String, ItemStack> player_chestplate = new HashMap<String, ItemStack>();
    private HashMap<String, ItemStack> player_leggings = new HashMap<String, ItemStack>();
    private HashMap<String, ItemStack> player_boots = new HashMap<String, ItemStack>();
    private HashMap<String, ItemStack> player_sword = new HashMap<String, ItemStack>();
    private HashMap<Integer, Integer> upgrade_generator = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> upgrade_sword = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> upgrade_armor = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> upgrade_speed = new HashMap<Integer, Integer>();
    private HashSet<String> fireball_cooldowns = new HashSet<String>();
    private HashSet<String> actived_teams = new HashSet<String>();
    private List<ArmorStand> d_armorstands = new ArrayList<ArmorStand>();
    private List<ArmorStand> e_armorstands = new ArrayList<ArmorStand>();
    private HashSet<Location> blocks = new HashSet<Location>();
    private String status;
    private Location spec;
    private int min_teams, max_teams, players_in_team, bed_break_money, win_money, final_kill_money;
    private List<Location> diamonds_gen, emeralds_gen, shops, upgrades;
    private int LOBBY_TIME, GAME_TIME;
    private String next_event;
    private int time = 0, taskID, up_tier_time_2, up_tier_time_3, bed_gone_time;
    private int diamond_stage = 1, emerald_stage = 1;
    private boolean timer = false;
    Inventory shop, upgrade;
    ItemStack shop_blocks, shop_swords, shop_armor, shop_tools, shop_bows, shop_useful, shop_potions;
    Inventory inv_blocks, inv_swords, inv_armor, inv_tools, inv_bows, inv_useful, inv_potions;
    ItemStack back;
    private int border;
	boolean compass;
	boolean is_compass = false;
    public class Pair {
		private String name;
		private int chance;
		public Pair(String a, int b) {
			this.name = a;
			this.chance = b;
		}
	}

	public void saveDefaultConfig() {
		if(file == null) {
			file = new File(getDataFolder(), "config.yml");
		}
		if(!file.exists()) {
			saveResource("config.yml", false);
		}
	}
	
	int getMax() {
		int index = 0;
		String query = "select MAX(index1) from bedwars_arenas;";
		try {
			rs2 = stmt2.executeQuery(query);
			rs2.next();
			index = rs2.getInt(1);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return index;
	}
	
	public void saveConfig() {
		if(config == null || file == null) return;
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reloadConfig() {
		if(file == null) {
			file = new File(getDataFolder(), "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(file);
		Reader defConfigStream = new InputStreamReader(getResource("config.yml"));
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
	}
	
	public FileConfiguration getConfig() {
		if(config == null) {
			reloadConfig();
		}
		return config;
	}
	
	int getGames(Player p) {
		String name = p.getName();
		int games = 0;
		String query = "SELECT games FROM bw_stats WHERE name = '"+name+"'";
		try {
			rs2 = stmt2.executeQuery(query);
			rs2.next();
			games = rs2.getInt(1);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return games;
	}
	
	public void addGame(Player p) {
		String name = p.getName();
		int games = getGames(p);
		String query = "UPDATE bw_stats SET games = '"+(games+1)+"' WHERE name = '"+name+"'";
		try {
			stmt2.executeUpdate(query);
		} catch (SQLException e) {}
	}
	
	public void addWin(Player p) {
		String name = p.getName();
		int wins = getWins(p);
		String query = "UPDATE bw_stats SET wins = '"+(wins+1)+"' WHERE name = '"+name+"'";
		try {
			stmt2.executeUpdate(query);
		} catch (SQLException e) {}
	}
	
	public void addKill(Player p) {
		String name = p.getName();
		int kills = getKills(p);
		String query = "UPDATE bw_stats SET kills = '"+(kills+1)+"' WHERE name = '"+name+"'";
		try {
			stmt2.executeUpdate(query);
		} catch (SQLException e) {}
	}
	
	public void addFinalKill(Player p) {
		String name = p.getName();
		int final_kills = getFinalKills(p);
		String query = "UPDATE bw_stats SET final_kills = '"+(final_kills+1)+"' WHERE name = '"+name+"'";
		try {
			stmt2.executeUpdate(query);
		} catch (SQLException e) {}
	}
	
	public void addBed(Player p) {
		String name = p.getName();
		int beds = getBeds(p);
		String query = "UPDATE bw_stats SET beds = '"+(beds+1)+"' WHERE name = '"+name+"'";
		try {
			stmt2.executeUpdate(query);
		} catch (SQLException e) {}
	}
	
	int getWins(Player p) {
		String name = p.getName();
		int wins = 0;
		String query = "SELECT wins FROM bw_stats WHERE name = '"+name+"'";
		try {
			rs2 = stmt2.executeQuery(query);
			rs2.next();
			wins = rs2.getInt(1);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return wins;
	}
	
	int getKills(Player p) {
		String name = p.getName();
		int kills = 0;
		String query = "SELECT kills FROM bw_stats WHERE name = '"+name+"'";
		try {
			rs2 = stmt2.executeQuery(query);
			rs2.next();
			kills = rs2.getInt(1);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return kills;
	}

	int getFinalKills(Player p) {
		String name = p.getName();
		int final_kills = 0;
		String query = "SELECT final_kills FROM bw_stats WHERE name = '"+name+"'";
		try {
			rs2 = stmt2.executeQuery(query);
			rs2.next();
			final_kills = rs2.getInt(1);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return final_kills;
	}
	
	int getBeds(Player p) {
		String name = p.getName();
		int beds = 0;
		String query = "SELECT beds FROM bw_stats WHERE name = '"+name+"'";
		try {
			rs2 = stmt2.executeQuery(query);
			rs2.next();
			beds = rs2.getInt(1);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return beds;
	}
	
	List<Long> toCoins(long l) {
		ArrayList<Long> list = new ArrayList<Long>();
		long med_money = l%1000;
		long ost = (l-med_money)/1000;
		long gold_money = ost%1000;
		long plat_money = (ost-gold_money)/1000;
		list.add(med_money);
		list.add(gold_money);
		list.add(plat_money);
		return list;
	}
	
	public void createUpgrade() {
		upgrade = Bukkit.createInventory(null, 9*5, "§cПрокачки");
		ItemStack n = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = n.getItemMeta();
		meta.setDisplayName(" ");
		n.setItemMeta(meta);
		for(int i = 0; i <= 8; ++i) {
			upgrade.setItem(i, n);
			upgrade.setItem(i+36, n);
		}
		for(int i = 18; i <= 9*5-1; ++i) {
			upgrade.setItem(i, n);
		}
		upgrade.setItem(9, n);
		upgrade.setItem(11, n);
		upgrade.setItem(13, n);
		upgrade.setItem(15, n);
		upgrade.setItem(17, n);

		ItemStack up_sword = new ItemStack(Material.IRON_SWORD);
		meta = up_sword.getItemMeta();
		meta.setDisplayName("§eЗаострение мечей");
		List<String> lore = new ArrayList<String>();
		lore.add("§7Цена: §c"+upgrade_sword.get(2)+" §bАлмазов");
		lore.add("§7Следующий уровень: §c1§e ⟶ §c2");
		meta.setLore(lore);
		up_sword.setItemMeta(meta);

		ItemStack up_armor = new ItemStack(Material.LEATHER_CHESTPLATE);
		meta = up_armor.getItemMeta();
		meta.setDisplayName("§eУкрепление брони");
		lore = new ArrayList<String>();
		lore.add("§7Цена: §c"+upgrade_armor.get(2)+" §bАлмаз(-а,-ов)");
		lore.add("§7Следующий уровень: §c1§e ⟶ §c2");
		meta.setLore(lore);
		up_armor.setItemMeta(meta);

		ItemStack up_gen = new ItemStack(Material.FURNACE);
		meta = up_gen.getItemMeta();
		meta.setDisplayName("§eПрокачка генератора");
		lore = new ArrayList<String>();
		lore.add("§7Цена: §c"+upgrade_generator.get(2)+" §bАлмаз(-а,-ов)");
		lore.add("§7Следующий уровень: §c1§e ⟶ §c2");
		meta.setLore(lore);
		up_gen.setItemMeta(meta);

		ItemStack up_speed = new ItemStack(Material.GOLDEN_PICKAXE);
		meta = up_speed.getItemMeta();
		meta.setDisplayName("§eСкорость копания");
		lore = new ArrayList<String>();
		lore.add("§7Цена: §c"+upgrade_speed.get(2)+" §bАлмаз(-а,-ов)");
		lore.add("§7Следующий уровень: §c1§e ⟶ §c2");
		meta.setLore(lore);
		up_speed.setItemMeta(meta);

		ItemStack trap = new ItemStack(Material.TRIPWIRE_HOOK);
		meta = trap.getItemMeta();
		meta.setDisplayName("§eЛовушка");
		lore = new ArrayList<String>();
		lore.add("§7Цена: §c2 §bАлмаз(-а,-ов)");
		lore.add("§7Одноразовая ловушка, замедляющая скорость копания врага на вашей базе");
		meta.setLore(lore);
		trap.setItemMeta(meta);
		
		upgrade.setItem(10, up_sword);
		upgrade.setItem(12, up_armor);
		upgrade.setItem(14, up_gen);
		upgrade.setItem(16, up_speed);
		upgrade.setItem(31, trap);
	}
	
	public void createShop() {
		shop = Bukkit.createInventory(null, 9*3, "§aМагазин");
		ItemStack n = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = n.getItemMeta();
		meta.setDisplayName(" ");
		n.setItemMeta(meta);
		for(int i = 0; i <= 8; ++i) {
			shop.setItem(i, n);
			shop.setItem(i+18, n);
		}
		shop.setItem(9, n);
		shop.setItem(17, n);
		
		shop_blocks = new ItemStack(Material.OAK_PLANKS);
		meta = shop_blocks.getItemMeta();
		meta.setDisplayName("§fБлоки");
		shop_blocks.setItemMeta(meta);
		
		shop_swords = new ItemStack(Material.IRON_SWORD);
		meta = shop_swords.getItemMeta();
		meta.setDisplayName("§fОружие");
		shop_swords.setItemMeta(meta);
		
		shop_armor = new ItemStack(Material.IRON_BOOTS);
		meta = shop_armor.getItemMeta();
		meta.setDisplayName("§fБроня");
		shop_armor.setItemMeta(meta);
		
		shop_tools = new ItemStack(Material.STONE_PICKAXE);
		meta = shop_tools.getItemMeta();
		meta.setDisplayName("§fИнструменты");
		shop_tools.setItemMeta(meta);
		
		shop_bows = new ItemStack(Material.BOW);
		meta = shop_bows.getItemMeta();
		meta.setDisplayName("§fЛуки/Арбалеты");
		shop_bows.setItemMeta(meta);
		
		shop_useful = new ItemStack(Material.TNT);
		meta = shop_useful.getItemMeta();
		meta.setDisplayName("§fПолезное");
		shop_useful.setItemMeta(meta);
		
		shop_potions = new ItemStack(Material.POTION);
		meta = shop_potions.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setDisplayName("§fЗелья");
		shop_potions.setItemMeta(meta);
		
		shop.setItem(10, shop_blocks);
		shop.setItem(11, shop_swords);
		shop.setItem(12, shop_armor);
		shop.setItem(13, shop_tools);
		shop.setItem(14, shop_bows);
		shop.setItem(15, shop_useful);
		shop.setItem(16, shop_potions);
	}
	
	public void scoreboardUpdate(Player p) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getNewScoreboard();
        final Objective objective = board.registerNewObjective("score", "dummy");        
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§c§lBedWars");
        ArrayList<Long> list = (ArrayList<Long>) toCoins((long)econ.getBalance(p));
        long med = list.get(0);
        long gold = list.get(1);
        long plat = list.get(2);
		int rr = max_teams*players_in_team;
		String team = player_teams.get(p.getName());
        if(status.equals("wait")) {
    		String c = getConfig().getString("arena.commands."+team+".color");
        	Score score = objective.getScore("§7Баланс: §a"+plat+"§fп §a"+gold+"§6з §a"+med+"§cм");
            score.setScore(6);
            Score score2 = objective.getScore("§7Состояние: §cОжидание...");
            score2.setScore(5);
            Score score25 = objective.getScore("§7Команда: §"+c+team);
            score25.setScore(4);
            Score score3 = objective.getScore("§7Игроков: §c"+String.valueOf(count_players)+"§e/§c"+String.valueOf(rr));
            score3.setScore(3);
            Score score8 = objective.getScore("§7До начала игры: §c"+String.valueOf(LOBBY_TIME));
            score8.setScore(2);
            Score score4 = objective.getScore("§7Донат: §cVaultCommunity.ru");
            score4.setScore(1);
            p.setScoreboard(board);
        }
        else if(status.equals("play")) {
        	ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
    		int count = cs.getKeys(false).size();
        	Score score = objective.getScore("§7Баланс: §a"+plat+"§fп §a"+gold+"§6з §a"+med+"§cм");
            score.setScore(count+7);
            Score score2 = objective.getScore("§7Состояние: §aИдёт игра");
            score2.setScore(count+6);
            Score score3 = objective.getScore("§7Команды:");
            score3.setScore(count+4);
            int i = 0;
            for(String name : cs.getKeys(false)) {
            	if(name.equals(team)) {
            		String bed = "§c❌";
            		String add = "§7(Вы)";
	            	int w = teams.get(name).size();
	            	if(bed_alive.contains(name)) bed = "§a✔";
	        		String c = getConfig().getString("arena.commands."+name+".color");
	        		String res = "§f·  "+bed+" §7(§f"+String.valueOf(w)+"§7) §"+c+name + " " + add;
	                Score score33 = objective.getScore(res);
	                score33.setScore(count+3-i);
            	}
            	else {
	            	String bed = "§c❌";
	            	int w = teams.get(name).size();
	            	if(bed_alive.contains(name)) bed = "§a✔";
	        		String c = getConfig().getString("arena.commands."+name+".color");
	        		String res = "§f·  "+bed+" §7(§f"+String.valueOf(w)+"§7) §"+c+name;
	                Score score33 = objective.getScore(res);
	                score33.setScore(count+3-i);
            	}
                ++i;
            }
            Score score44 = objective.getScore("        ");
            score44.setScore(3);
            Score score8 = objective.getScore(next_event+" §7(§c"+String.valueOf(time)+"§7)");
            score8.setScore(2);
            Score score4 = objective.getScore("§7Донат: §cVaultCommunity.ru");
            score4.setScore(1);
            p.setScoreboard(board);
        }
        else {
        	Score score = objective.getScore("§7Баланс: §a"+plat+"§fп §a"+gold+"§6з §a"+med+"§cм");
            score.setScore(3);
            Score score2 = objective.getScore("§7Состояние: §fРед.");
            score2.setScore(2);
            Score score4 = objective.getScore("§7Донат: §cVaultCommunity.ru");
            score4.setScore(1);
            p.setScoreboard(board);
        }
	}
	
    public void toLobby(Player p ) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("lobby");
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
    
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public void addToDB() {
		String query = "INSERT INTO bedwars_arenas (arena, status, online, max_online, type, index1) VALUES ('"+arena+"', '"+status+"', '"+0+"', '"+players_in_team*max_teams+"', '"+players_in_team+"', '"+getMax()+"')";
		try {
			stmt2.executeUpdate(query);
		} catch(SQLException e) {
			updateDB("wait", 0);
		}
	}
	
	public void updateDB(String status, int online) {
		String query = "UPDATE bedwars_arenas SET status = '"+status+"', online = '"+online+"' WHERE arena = '"+arena+"'";
		try {
			stmt2.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		};
	}
	
	public void exist(Player p) {
		p.sendMessage("§cТак как у вас уже есть подобный перк, данный перк был отменён");
	}
	
	@EventHandler
	public void place(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(status.equals("wait")) {
			e.setCancelled(true);
		}
		else if(status.equals("play")) {
			if(e.getBlock().getType() == Material.TNT) {
				Entity tnt = p.getWorld().spawn(e.getBlockPlaced().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
                tnt = (TNTPrimed) tnt;
                tnt.setCustomName(p.getName());
                tnt.setCustomNameVisible(false);
				Bukkit.getServer().getWorld(p.getWorld().getName()).getBlockAt(e.getBlockPlaced().getLocation()).setType(Material.AIR);
			}
			else {
				String team = player_teams.get(p.getName());
				Location bl = getConfig().getLocation("arena.commands."+team+".bed");
				if(e.getBlock().getType() == Material.BELL || e.getBlock().getType() == Material.CAMPFIRE || e.getBlock().getType() == Material.BEE_NEST) {
					if(bed_alive.contains(team)) {
						Location loc = e.getBlockPlaced().getLocation();
						int x1 = bl.getBlockX(), z1 = bl.getBlockZ();
						int x2 = loc.getBlockX(), z2 = bl.getBlockZ();
						if(Math.abs(x1-x2) > 5 || Math.abs(z1-z2) > 5) {
							send(p, "&cПоставьте ближе к кровати! (на одном уровне с ней)");
							e.setCancelled(true);
						}
						else {
							send(p, "&aУспешно!");
							for(String p_name : teams.get(team)) {
								Player pl = Bukkit.getPlayer(p_name);
								send(pl, p.getDisplayName()+" &7поставил специальный блок около кровати");
							}
							blocks.add(e.getBlock().getLocation());
						}
					}
					else {
						send(p, "&cВаша кровать разрушена");
					}
				}
				else blocks.add(e.getBlock().getLocation());
			}
		}
	}
	
	@EventHandler
	public void projHit(ProjectileHitEvent e) {
		if(!status.equals("play")) {
			if(e.getEntity() instanceof Arrow) {
				Location loc = e.getEntity().getLocation();
				loc.getWorld().createExplosion(loc, 15, false);
			}
		}
	}
	
	@EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
		if(status.equals("play")) {
	        List<Block> destroyed = e.blockList();
	        Iterator<Block> it = destroyed.iterator();
	        while (it.hasNext()) {
	            Block block = it.next();
	            if(block.getType() == Material.GLASS) it.remove();
	            else if (!blocks.contains(block.getLocation())) {
	            	it.remove();
	            }
	        }
		}
		else {
			e.setYield((float) 0.0);
		}
    }
	
	String getTeamWithBed(Location loc) {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		String team = "";
		for(String name : cs.getKeys(false)) {
			Location bed_lock = getConfig().getLocation("arena.commands."+name+".bed");
			if(bed_lock.getBlockY() == loc.getBlockY()) {
				int x1 = loc.getBlockX(), x2 = bed_lock.getBlockX();
				int z1 = loc.getBlockZ(), z2 = bed_lock.getBlockZ();
				if(Math.abs(x1-x2) <= 2 && Math.abs(z1-z2) <= 2) {
					team = name;
					break;
				}
			}
		}
		return team;
	}
	
	@EventHandler
	public void breakBlocks(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(status.equals("wait")) {
			e.setCancelled(true);
		}
		else if(status.equals("play")) {
			Location loc = e.getBlock().getLocation();
			String name = player_teams.get(p.getName());
			if(e.getBlock().getType().toString().contains("BED") && e.getBlock().getType() != Material.BEDROCK) {
				String team = getTeamWithBed(loc);
				if(!name.equals(team)) {
					if(bed_alive.contains(team)) {
						String c = getConfig().getString("arena.commands."+team+".color");
						sendAll(p.getDisplayName()+" &fРАЗРУШИЛ КРОВАТЬ КОМАНДЫ &"+c+team);
						addBed(p);
						for(Player ppp : Bukkit.getOnlinePlayers()) {
							ppp.playSound(ppp.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
						}
						econ.depositPlayer(p, bed_break_money);
						send(p, "&7Вы получили &c"+String.valueOf(bed_break_money)+" медных &7монет за разрушение кровати");
						bed_alive.remove(team);
						e.setDropItems(false);
						for(String p_name : teams.get(team)) {
							Player pl = Bukkit.getPlayer(p_name);
							send(pl, "&cВаша кровать разрушена! Вы больше не возродитесь!");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p_name+" title {\"text\":\"КРОВАТЬ РАЗРУШЕНА\", \"bold\":true, \"color\":\"red\"}");
						}
					}
					else e.setCancelled(true);
				}
				else e.setCancelled(true);
			}
			else if(!blocks.contains(loc)) e.setCancelled(true);
		}
	}
	
	public boolean hasItem(Inventory inv, Material mat, int amount) {
		int sum = 0;
		for(ItemStack it : inv.getContents()) {
			if(it != null) {
				if(it.getType() == mat) {
					sum += it.getAmount();
					if(sum >= amount) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void removeItem(Inventory inv, Material mat, int amount) {
		int sum = amount;
		int raw = 0;
		for(ItemStack it : inv.getContents()) {
			if(it != null) {
				if(it.getType() == mat) {
					if(sum == 0) break;
					if(it.getAmount() <= sum) {
						inv.setItem(raw, null);
						sum -= it.getAmount();
					}
					else {
						it.setAmount(it.getAmount()-sum);
						sum = 0;
					}
				}
			}
			raw++;
		}
	}
	
	public void updateTeamInv() {
		team_inv = Bukkit.createInventory(null, 9*3, "§aВыбор команды");
		
		for(String t : teams.keySet()) {
			ItemStack helmet = getConfig().getItemStack("arena.commands."+t+".armor.helmet");
			String c = getConfig().getString("arena.commands."+t+".color");
			ItemMeta meta = helmet.getItemMeta();
			meta.setDisplayName("§"+c+t);
			helmet.setItemMeta(meta);
			
			team_inv.addItem(helmet);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(status.equals("wait")) {
			e.setCancelled(true);
		}
		String team = player_teams.get(p.getName());
		if (e.getSlotType().equals(SlotType.ARMOR) && !e.getCurrentItem().getType().equals(Material.AIR)) {
            e.setCancelled(true);
		}
		Inventory in = e.getInventory();
		if(e.getInventory().equals(team_inv)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				ItemMeta meta = it.getItemMeta();
				String t = meta.getDisplayName().substring(2);
				if(teams.containsKey(t)) {
					if(!team.equals(t)) {
						if(teams.get(t).size() < players_in_team) {
							removePlayerFromTeam(p);
							addPlayerToTeam(p, t);
							setColor(p, t);
							Location loc = getConfig().getLocation("arena.commands."+t+".spawn");
							String color = getConfig().getString("arena.commands."+t+".color");
							p.teleport(loc);
							send(p, "&7Выбрана команда &"+color+t);
							p.closeInventory();
							
							ItemStack now = getConfig().getItemStack("arena.commands."+t+".block");
							meta = now.getItemMeta();
							meta.setDisplayName("§aВыбор команды");
							now.setItemMeta(meta);
							p.getInventory().setItem(0, now);
						}
						else send(p, "&cКоманда заполнена");
					}
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(saved_up.get(team))) {
			ItemStack it = e.getCurrentItem();
			if(it != null && e.getRawSlot() <= 44) {
				ItemMeta meta = it.getItemMeta();
				if(it.getType() == Material.FURNACE) {
					if(stage.get(team) >= 5) {
						send(p, "&cГенератор уже прокачан на максимум");
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(hasItem(p.getInventory(), Material.DIAMOND, price)) {
							removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							stage.replace(team, stage.get(team)+1);
							
							Inventory inv = saved_up.get(team);
							List<String> lore2 = new ArrayList<String>();
							if(stage.get(team) < 5) {
								lore2.add("§7Цена: §c"+String.valueOf(upgrade_generator.get(stage.get(team)+1))+" §bАлмаз(-а,-ов)");
								lore2.add("§7Следующий уровень: §c"+String.valueOf(stage.get(team))+"§e ⟶ §c"+String.valueOf(stage.get(team)+1));
							}
							else lore2.add("§aМАКСИМУМ");
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							saved_up.replace(team, inv);
							
							for(String p_name : teams.get(team)) {
								send(Bukkit.getPlayer(p_name), p.getDisplayName()+" &7прокачал &cГенератор &7[&c"+String.valueOf(stage.get(team)-1)+"&e ⟶ &c"+String.valueOf(stage.get(team))+"&7]");
							}
						}
						else send(p, "&cНедостаточно ресурсов");
					}
				}
				else if(it.getType() == Material.IRON_SWORD) {
					if(team_swords.get(team) >= 4) {
						send(p, "&cЗаострение мечей уже прокачано на максимум");
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(hasItem(p.getInventory(), Material.DIAMOND, price)) {
							removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							team_swords.replace(team, team_swords.get(team)+1);
							
							Inventory inv = saved_up.get(team);
							List<String> lore2 = new ArrayList<String>();
							if(team_swords.get(team) < 4) {
								lore2.add("§7Цена: §c"+String.valueOf(upgrade_sword.get(team_swords.get(team)+1))+" §bАлмаз(-а,-ов)");
								lore2.add("§7Следующий уровень: §c"+String.valueOf(team_swords.get(team))+"§e ⟶ §c"+String.valueOf(team_swords.get(team)+1));
							}
							else lore2.add("§aМАКСИМУМ");
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							saved_up.replace(team, inv);
							
							for(String p_name : teams.get(team)) {
								Player pl = Bukkit.getPlayer(p_name);
								ItemStack sw = player_sword.get(pl.getName());
								sw.addEnchantment(Enchantment.DAMAGE_ALL, team_swords.get(team)-1);
								player_sword.replace(pl.getName(), sw);
								Inventory pinv = p.getInventory();
								int raw = 0;
								for(ItemStack sk : pinv.getContents()) {
									if(sk == null) continue;
									if(sk.getType().toString().contains("SWORD")) {
										sk.addEnchantment(Enchantment.DAMAGE_ALL, team_swords.get(team)-1);
										p.getInventory().setItem(raw, sk);
									}
									raw++;
								}
								send(Bukkit.getPlayer(p_name), p.getDisplayName()+" &7прокачал &cЗаострение мечей &7[&c"+String.valueOf(team_swords.get(team)-1)+"&e ⟶ &c"+String.valueOf(team_swords.get(team))+"&7]");
							}
						}
						else send(p, "&cНедостаточно ресурсов");
					}
				}
				else if(it.getType() == Material.LEATHER_CHESTPLATE) {
					if(team_armor.get(team) >= 5) {
						send(p, "&cУкрепление брони уже прокачано на максимум");
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(hasItem(p.getInventory(), Material.DIAMOND, price)) {
							removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							team_armor.replace(team, team_armor.get(team)+1);
							
							Inventory inv = saved_up.get(team);
							List<String> lore2 = new ArrayList<String>();
							if(team_armor.get(team) < 5) {
								lore2.add("§7Цена: §c"+String.valueOf(upgrade_armor.get(team_armor.get(team)+1))+" §bАлмаз(-а,-ов)");
								lore2.add("§7Следующий уровень: §c"+String.valueOf(team_armor.get(team))+"§e ⟶ §c"+String.valueOf(team_armor.get(team)+1));
							}
							else lore2.add("§aМАКСИМУМ");
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							saved_up.replace(team, inv);
							
							for(String p_name : teams.get(team)) {
								Player pl = Bukkit.getPlayer(p_name);
								ItemStack helmet = player_helmet.get(pl.getName());
								ItemStack chestpate = player_chestplate.get(pl.getName());
								ItemStack leggings = player_leggings.get(pl.getName());
								ItemStack boots = player_boots.get(pl.getName());
								helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, team_armor.get(team)-1);
								chestpate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, team_armor.get(team)-1);
								leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, team_armor.get(team)-1);
								boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, team_armor.get(team)-1);
								player_helmet.replace(pl.getName(), helmet);
								player_chestplate.replace(pl.getName(), chestpate);
								player_leggings.replace(pl.getName(), leggings);
								player_boots.replace(pl.getName(), boots);
								
								p.getInventory().setHelmet(helmet);
								p.getInventory().setChestplate(chestpate);
								p.getInventory().setLeggings(leggings);
								p.getInventory().setBoots(boots);
								send(Bukkit.getPlayer(p_name), p.getDisplayName()+" &7прокачал &cУкрепление &7[&c"+String.valueOf(team_armor.get(team)-1)+"&e ⟶ &c"+String.valueOf(team_armor.get(team))+"&7]");
							}
						}
						else send(p, "&cНедостаточно ресурсов");
					}
				}
				else if(it.getType() == Material.GOLDEN_PICKAXE) {
					if(team_speed.get(team) >= 4) {
						send(p, "&cУскорение копания уже прокачано на максимум");
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(hasItem(p.getInventory(), Material.DIAMOND, price)) {
							removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							team_speed.replace(team, team_speed.get(team)+1);
							
							Inventory inv = saved_up.get(team);
							List<String> lore2 = new ArrayList<String>();
							if(team_speed.get(team) < 4) {
								lore2.add("§7Цена: §c"+String.valueOf(upgrade_speed.get(team_speed.get(team)+1))+" §bАлмаз(-а,-ов)");
								lore2.add("§7Следующий уровень: §c"+String.valueOf(team_speed.get(team))+"§e ⟶ §c"+String.valueOf(team_speed.get(team)+1));
							}
							else lore2.add("§aМАКСИМУМ");
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							saved_up.replace(team, inv);
							
							for(String p_name : teams.get(team)) {
								send(Bukkit.getPlayer(p_name), p.getDisplayName()+" &7прокачал &cУскорение копания &7[&c"+String.valueOf(team_speed.get(team)-1)+"&e ⟶ &c"+String.valueOf(team_speed.get(team))+"&7]");
							}
						}
						else send(p, "&cНедостаточно ресурсов");
					}
				}
				else if(it.getType() == Material.TRIPWIRE_HOOK) {
					if(team_trap.get(team)) {
						send(p, "&cЛовушка уже куплена!");
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(hasItem(p.getInventory(), Material.DIAMOND, price)) {
							removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							team_trap.replace(team, true);
							
							Inventory inv = saved_up.get(team);
							List<String> lore2 = new ArrayList<String>();
							lore2.add("§aАКТИВНО");
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							saved_up.replace(team, inv);
							
							for(String p_name : teams.get(team)) {
								send(Bukkit.getPlayer(p_name), p.getDisplayName()+" &7купил &cЛовушку");
							}
						}
						else send(p, "&cНедостаточно ресурсов");
					}
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(shop)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(shop_blocks)) {
					p.openInventory(saved_inv.get(team));
				}
				else if(it.isSimilar(shop_swords)) {
					p.openInventory(inv_swords);
				}
				else if(it.isSimilar(shop_bows)) {
					p.openInventory(inv_bows);
				}
				else if(it.isSimilar(shop_tools)) {
					p.openInventory(inv_tools);
				}
				else if(it.isSimilar(shop_useful)) {
					p.openInventory(inv_useful);
				}
				else if(it.isSimilar(shop_potions)) {
					p.openInventory(inv_potions);
				}
				else if(it.isSimilar(shop_armor)) {
					p.openInventory(inv_armor);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(saved_inv.get(team))) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(back)) {
					p.openInventory(shop);
				}
				try {
					ItemMeta meta = it.getItemMeta();
					List<String> lore = meta.getLore();
					if(lore == null || lore.isEmpty()) {
						e.setCancelled(true);
					}
					else {
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						String type = parse[2];
						Inventory inv = p.getInventory();
						if(type.contains("Железа")) {
							if(hasItem(inv, Material.IRON_INGOT, price)) {
								removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ItemStack res = new ItemStack(it.getType(), it.getAmount());
								p.getInventory().addItem(res);
								p.updateInventory();
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Золота")) {
							if(hasItem(inv, Material.GOLD_INGOT, price)) {
								removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ItemStack res = new ItemStack(it.getType(), it.getAmount());
								p.getInventory().addItem(res);
								p.updateInventory();
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Изумруд(-ов,-а)")) {
							if(hasItem(inv, Material.EMERALD, price)) {
								removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ItemStack res = new ItemStack(it.getType(), it.getAmount());
								p.getInventory().addItem(res);
								p.updateInventory();
							}
							else send(p, "&cНедостаточно ресурсов");
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(inv_swords)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(back)) {
					p.openInventory(shop);
				}
				try {
					ItemMeta meta = it.getItemMeta();
					List<String> lore = meta.getLore();
					if(lore == null || lore.isEmpty()) {
						e.setCancelled(true);
					}
					else {
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						String type = parse[2];
						Inventory inv = p.getInventory();
						boolean ch = false;
						if(type.contains("Железа")) {
							if(hasItem(inv, Material.IRON_INGOT, price)) {
								removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Золота")) {
							if(hasItem(inv, Material.GOLD_INGOT, price)) {
								removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Изумруд(-ов,-а)")) {
							if(hasItem(inv, Material.EMERALD, price)) {
								removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						if(ch) {
							boolean isSword = false;
							if(it.getType() == Material.STONE_SWORD) {
								isSword = true;
								if(hasItem(inv, Material.WOODEN_SWORD, 1)) {
									removeItem(inv, Material.WOODEN_SWORD, 1);
								}
							}
							else if(it.getType() == Material.IRON_SWORD) {
								isSword = true;
								if(hasItem(inv, Material.STONE_SWORD, 1)) {
									removeItem(inv, Material.STONE_SWORD, 1);
								}
								if(hasItem(inv, Material.WOODEN_SWORD, 1)) {
									removeItem(inv, Material.WOODEN_SWORD, 1);
								}
							}
							else if(it.getType() == Material.DIAMOND_SWORD) {
								isSword = true;
								if(hasItem(inv, Material.STONE_SWORD, 1)) {
									removeItem(inv, Material.STONE_SWORD, 1);
								}
								if(hasItem(inv, Material.WOODEN_SWORD, 1)) {
									removeItem(inv, Material.WOODEN_SWORD, 1);
								}
								if(hasItem(inv, Material.IRON_SWORD, 1)) {
									removeItem(inv, Material.IRON_SWORD, 1);
								}
							}
							else if(it.getType() == Material.NETHERITE_SWORD) {
								isSword = true;
								if(hasItem(inv, Material.DIAMOND_SWORD, 1)) {
									removeItem(inv, Material.DIAMOND_SWORD, 1);
								}
								if(hasItem(inv, Material.STONE_SWORD, 1)) {
									removeItem(inv, Material.STONE_SWORD, 1);
								}
								if(hasItem(inv, Material.WOODEN_SWORD, 1)) {
									removeItem(inv, Material.WOODEN_SWORD, 1);
								}
								if(hasItem(inv, Material.IRON_SWORD, 1)) {
									removeItem(inv, Material.IRON_SWORD, 1);
								}
							}
							ItemStack res = new ItemStack(it.getType(), it.getAmount());
							if(isSword && team_swords.get(team) > 1) res.addEnchantment(Enchantment.DAMAGE_ALL, team_swords.get(team)-1);
							ItemMeta mm = res.getItemMeta();
							mm.setUnbreakable(true);
							res.setItemMeta(mm);
							if(isSword) {
								ItemStack bef_sword = player_sword.get(p.getName());
								res.addEnchantments(bef_sword.getEnchantments());
							}
							else {
								res.addUnsafeEnchantments(it.getEnchantments());
							}
							p.getInventory().addItem(res);
							p.updateInventory();
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(inv_armor)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(back)) {
					p.openInventory(shop);
				}
				try {
					ItemMeta meta = it.getItemMeta();
					List<String> lore = meta.getLore();
					if(lore == null || lore.isEmpty()) {
						e.setCancelled(true);
					}
					else {
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						String type = parse[2];
						Inventory inv = p.getInventory();
						boolean ch = false;
						if(type.contains("Железа")) {
							if(hasItem(inv, Material.IRON_INGOT, price)) {
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Золота")) {
							if(hasItem(inv, Material.GOLD_INGOT, price)) {
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Изумруд(-ов,-а)")) {
							if(hasItem(inv, Material.EMERALD, price)) {
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						if(ch) {
							if(it.getType() == Material.CHAINMAIL_BOOTS) {
								if(hasItem(inv, Material.CHAINMAIL_BOOTS, 1) || hasItem(inv, Material.IRON_BOOTS, 1) || hasItem(inv, Material.DIAMOND_BOOTS, 1) || hasItem(inv, Material.NETHERITE_BOOTS, 1)) {
									send(p, "&cУ вас уже имеется броня лучше или такая же");
								}
								else {
									ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
									ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
									ItemMeta m = leggings.getItemMeta();
									m.setUnbreakable(true);
									leggings.setItemMeta(m);
									m = boots.getItemMeta();
									m.setUnbreakable(true);
									boots.setItemMeta(m);
									
									ItemStack bef_leg = player_leggings.get(p.getName());
									ItemStack bef_boots = player_boots.get(p.getName());
									leggings.addEnchantments(bef_leg.getEnchantments());
									boots.addEnchantments(bef_boots.getEnchantments());
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
									p.updateInventory();
									
									player_leggings.replace(p.getName(), leggings);
									player_boots.replace(p.getName(), boots);
									removeItem(inv, Material.IRON_INGOT, price);
									p.updateInventory();
								}
							}
							else if(it.getType() == Material.IRON_BOOTS) {
								if(hasItem(inv, Material.IRON_BOOTS, 1) || hasItem(inv, Material.DIAMOND_BOOTS, 1) || hasItem(inv, Material.NETHERITE_BOOTS, 1)) {
									send(p, "&cУ вас уже имеется броня лучше или такая же");
								}
								else {
									ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
									ItemStack boots = new ItemStack(Material.IRON_BOOTS);
									ItemMeta m = leggings.getItemMeta();
									m.setUnbreakable(true);
									leggings.setItemMeta(m);
									m = boots.getItemMeta();
									m.setUnbreakable(true);
									boots.setItemMeta(m);
									
									ItemStack bef_leg = player_leggings.get(p.getName());
									ItemStack bef_boots = player_boots.get(p.getName());
									leggings.addEnchantments(bef_leg.getEnchantments());
									boots.addEnchantments(bef_boots.getEnchantments());
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
									p.updateInventory();
									
									player_leggings.replace(p.getName(), leggings);
									player_boots.replace(p.getName(), boots);
									removeItem(inv, Material.GOLD_INGOT, price);
									p.updateInventory();
								}
							}
							else if(it.getType() == Material.DIAMOND_BOOTS) {
								if(hasItem(inv, Material.DIAMOND_BOOTS, 1) || hasItem(inv, Material.NETHERITE_BOOTS, 1)) {
									send(p, "&cУ вас уже имеется броня лучше или такая же");
								}
								else {
									ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
									ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
									ItemMeta m = leggings.getItemMeta();
									m.setUnbreakable(true);
									leggings.setItemMeta(m);
									m = boots.getItemMeta();
									m.setUnbreakable(true);
									boots.setItemMeta(m);
									
									ItemStack bef_leg = player_leggings.get(p.getName());
									ItemStack bef_boots = player_boots.get(p.getName());
									leggings.addEnchantments(bef_leg.getEnchantments());
									boots.addEnchantments(bef_boots.getEnchantments());
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
									p.updateInventory();
									
									player_leggings.replace(p.getName(), leggings);
									player_boots.replace(p.getName(), boots);
									removeItem(inv, Material.EMERALD, price);
									p.updateInventory();
								}
							}
							else if(it.getType() == Material.NETHERITE_BOOTS) {
								if(hasItem(inv, Material.NETHERITE_BOOTS, 1)) {
									send(p, "&cУ вас уже имеется броня лучше или такая же");
								}
								else {
									ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
									ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
									ItemMeta m = leggings.getItemMeta();
									m.setUnbreakable(true);
									leggings.setItemMeta(m);
									m = boots.getItemMeta();
									m.setUnbreakable(true);
									boots.setItemMeta(m);
									
									ItemStack bef_leg = player_leggings.get(p.getName());
									ItemStack bef_boots = player_boots.get(p.getName());
									leggings.addEnchantments(bef_leg.getEnchantments());
									boots.addEnchantments(bef_boots.getEnchantments());
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
									p.updateInventory();
									
									player_leggings.replace(p.getName(), leggings);
									player_boots.replace(p.getName(), boots);
									removeItem(inv, Material.EMERALD, price);
									p.updateInventory();
								}
							}
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(inv_tools)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(back)) {
					p.openInventory(shop);
				}
				try {
					ItemMeta meta = it.getItemMeta();
					List<String> lore = meta.getLore();
					if(lore == null || lore.isEmpty()) {
						e.setCancelled(true);
					}
					else {
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						String type = parse[2];
						Inventory inv = p.getInventory();
						boolean ch = false;
						if(type.contains("Железа")) {
							if(hasItem(inv, Material.IRON_INGOT, price)) {
								removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Золота")) {
							if(hasItem(inv, Material.GOLD_INGOT, price)) {
								removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Изумруд(-ов,-а)")) {
							if(hasItem(inv, Material.EMERALD, price)) {
								removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						if(ch) {
							if(it.getType() == Material.IRON_PICKAXE) {
								if(hasItem(inv, Material.STONE_PICKAXE, 1)) {
									removeItem(inv, Material.STONE_PICKAXE, 1);
								}
							}
							else if(it.getType() == Material.IRON_AXE) {
								if(hasItem(inv, Material.STONE_AXE, 1)) {
									removeItem(inv, Material.STONE_AXE, 1);
								}
							}
							else if(it.getType() == Material.DIAMOND_PICKAXE) {
								if(hasItem(inv, Material.STONE_PICKAXE, 1)) {
									removeItem(inv, Material.STONE_PICKAXE, 1);
								}
								if(hasItem(inv, Material.IRON_PICKAXE, 1)) {
									removeItem(inv, Material.IRON_PICKAXE, 1);
								}
							}
							else if(it.getType() == Material.DIAMOND_AXE) {
								if(hasItem(inv, Material.STONE_AXE, 1)) {
									removeItem(inv, Material.STONE_AXE, 1);
								}
								if(hasItem(inv, Material.IRON_AXE, 1)) {
									removeItem(inv, Material.IRON_AXE, 1);
								}
							}
							else if(it.getType() == Material.NETHERITE_PICKAXE) {
								if(hasItem(inv, Material.STONE_PICKAXE, 1)) {
									removeItem(inv, Material.STONE_PICKAXE, 1);
								}
								if(hasItem(inv, Material.IRON_PICKAXE, 1)) {
									removeItem(inv, Material.IRON_PICKAXE, 1);
								}
								if(hasItem(inv, Material.DIAMOND_PICKAXE, 1)) {
									removeItem(inv, Material.DIAMOND_PICKAXE, 1);
								}
							}
							else if(it.getType() == Material.NETHERITE_AXE) {
								if(hasItem(inv, Material.STONE_AXE, 1)) {
									removeItem(inv, Material.STONE_AXE, 1);
								}
								if(hasItem(inv, Material.IRON_AXE, 1)) {
									removeItem(inv, Material.IRON_AXE, 1);
								}
								if(hasItem(inv, Material.DIAMOND_AXE, 1)) {
									removeItem(inv, Material.DIAMOND_AXE, 1);
								}
							}
							ItemStack res = new ItemStack(it.getType(), it.getAmount());
							ItemMeta mm = res.getItemMeta();
							mm.setUnbreakable(true);
							res.setItemMeta(mm);
							
							p.getInventory().addItem(res);
							p.updateInventory();
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(inv_bows)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(back)) {
					p.openInventory(shop);
				}
				try {
					ItemMeta meta = it.getItemMeta();
					List<String> lore = meta.getLore();
					if(lore == null || lore.isEmpty()) {
						e.setCancelled(true);
					}
					else {
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						String type = parse[2];
						Inventory inv = p.getInventory();
						boolean ch = false;
						if(type.contains("Железа")) {
							if(hasItem(inv, Material.IRON_INGOT, price)) {
								removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Золота")) {
							if(hasItem(inv, Material.GOLD_INGOT, price)) {
								removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Изумруд(-ов,-а)")) {
							if(hasItem(inv, Material.EMERALD, price)) {
								removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						if(ch) {
							ItemStack res = new ItemStack(it.getType(), it.getAmount());
							ItemMeta mm = res.getItemMeta();
							mm.setUnbreakable(true);
							res.setItemMeta(mm);
							if(!it.getEnchantments().isEmpty()) res.addEnchantments(it.getEnchantments());
							if(res.getType() == Material.TIPPED_ARROW) {
								PotionMeta mu2 = (PotionMeta) it.getItemMeta();
								PotionMeta mu = (PotionMeta) res.getItemMeta();
								mu.setBasePotionData(mu2.getBasePotionData());
								res.setItemMeta(mu);
							}
							
							p.getInventory().addItem(res);
							p.updateInventory();
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(inv_useful)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(back)) {
					p.openInventory(shop);
				}
				try {
					ItemMeta meta = it.getItemMeta();
					List<String> lore = meta.getLore();
					if(lore == null || lore.isEmpty()) {
						e.setCancelled(true);
					}
					else {
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						String type = parse[2];
						Inventory inv = p.getInventory();
						boolean ch = false;
						if(type.contains("Железа")) {
							if(hasItem(inv, Material.IRON_INGOT, price)) {
								removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Золота")) {
							if(hasItem(inv, Material.GOLD_INGOT, price)) {
								removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Изумруд(-ов,-а)")) {
							if(hasItem(inv, Material.EMERALD, price)) {
								removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						if(ch) {
							ItemStack res = new ItemStack(it.getType(), it.getAmount());
							
							p.getInventory().addItem(res);
							p.updateInventory();
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(inv_potions)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(back)) {
					p.openInventory(shop);
				}
				try {
					ItemMeta meta = it.getItemMeta();
					List<String> lore = meta.getLore();
					if(lore == null || lore.isEmpty()) {
						e.setCancelled(true);
					}
					else {
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						String type = parse[2];
						Inventory inv = p.getInventory();
						boolean ch = false;
						if(type.contains("Железа")) {
							if(hasItem(inv, Material.IRON_INGOT, price)) {
								removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Золота")) {
							if(hasItem(inv, Material.GOLD_INGOT, price)) {
								removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						else if(type.contains("Изумруд(-ов,-а)")) {
							if(hasItem(inv, Material.EMERALD, price)) {
								removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else send(p, "&cНедостаточно ресурсов");
						}
						if(ch) {
							ItemStack res = new ItemStack(it.getType(), it.getAmount());
							PotionMeta pm = (PotionMeta) res.getItemMeta();
							PotionMeta pm2 = (PotionMeta) it.getItemMeta();
					        pm.setBasePotionData(pm2.getBasePotionData());
					        res.setItemMeta(pm);
							
							p.getInventory().addItem(res);
							p.updateInventory();
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(inv)) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(p1)) {
					p.closeInventory();
					if(econ.getBalance(p.getName()) < 100) {
						p.sendMessage("§cНе хватает монет!");
						return;
					}
					econ.withdrawPlayer(p.getName(), 100);
					int rand = (int) (Math.random() * 100);
					int sum = 0;
					boolean found = false;
					int i = 0;
					for(Pair pair : perks1) {
						String name = pair.name;
						int chance = pair.chance;
						sum += chance;
						if(sum >= rand) {
							found = true;
							p.sendMessage("§7Вам выпало: "+name + " §7(§cодноразовый§7)");
							if(i == 0) {
								if(p.getMaxHealth() != 20) {
									exist(p);
								}
								else {
									p.setMaxHealth(22);
									if(hp.containsKey(p.getName())) hp.replace(p.getName(), 22);
									else hp.put(p.getName(), 22);
								}
							}
							else if(i == 1) {
								if(!diamonds.containsKey(p.getName())) {
									diamonds.put(p.getName(), 4);
								}
								else {
									exist(p);
								}
							}
							else if(i == 2) {
								if(!golds.containsKey(p.getName())) {
									golds.put(p.getName(), 12);
								}
								else if(golds.get(p.getName()) < 12) golds.replace(p.getName(), 12);
								else {
									exist(p);
								}
							}
							else if(i == 3) {
								if(!golds.containsKey(p.getName())) {
									golds.put(p.getName(), 6);
								}
								else {
									exist(p);
								}
							}
							else if(i == 4) {
								if(!irons.containsKey(p.getName())) {
									irons.put(p.getName(), 16);
								}
								else {
									exist(p);
								}
							}
							else if(i == 5) {
								if(!irons.containsKey(p.getName())) {
									irons.put(p.getName(), 32);
								}
								else if(irons.get(p.getName()) < 32) irons.replace(p.getName(), 32);
								else {
									exist(p);
								}
							}
							else if(i == 6) {
								econ.depositPlayer(p, 50);
							}
							else if(i == 7) {
								econ.depositPlayer(p, 25);
							}
							else if(i == 8) {
								if(!fire.containsKey(p.getName())) {
									fire.put(p.getName(), 5);
								}
								else {
									exist(p);
								}
							}
							else if(i == 9) {
								if(!cold.containsKey(p.getName())) {
									cold.put(p.getName(), 5);
								}
								else {
									exist(p);
								}
							}
							else if(i == 10) {
								if(!prot.containsKey(p.getName())) {
									prot.put(p.getName(), 5);
								}
								else {
									exist(p);
								}
							}
							break;
						}
						i++;
					}
					if(!found) {
						p.sendMessage("§cНе повезло. Вам ничего не выпало");
					}
				}
				else if(it.isSimilar(p2)) {
					p.closeInventory();
					if(econ.getBalance(p.getName()) < 1000) {
						p.sendMessage("§cНе хватает монет!");
						return;
					}
					econ.withdrawPlayer(p.getName(), 1000);
					int rand = (int) (Math.random() * 100);
					int sum = 0;
					boolean found = false;
					int i = 0;
					for(Pair pair : perks2) {
						String name = pair.name;
						int chance = pair.chance;
						sum += chance;
						if(sum >= rand) {
							found = true;
							p.sendMessage("§7Вам выпало: "+name + " §7(§cодноразовый§7)");
							if(i == 0) {
								if(p.getMaxHealth() >= 24) {
									exist(p);
								}
								else {
									p.setMaxHealth(24);
									if(hp.containsKey(p.getName())) hp.replace(p.getName(), 24);
									else hp.put(p.getName(), 24);
								}
							}
							else if(i == 1) {
								if(!diamonds.containsKey(p.getName())) {
									diamonds.put(p.getName(), 8);
								}
								else if(diamonds.get(p.getName()) < 8) diamonds.replace(p.getName(), 8);
								else {
									exist(p);
								}
							}
							else if(i == 2) {
								if(!golds.containsKey(p.getName())) {
									golds.put(p.getName(), 16);
								}
								else if(golds.get(p.getName()) < 16) golds.replace(p.getName(), 16);
								else {
									exist(p);
								}
							}
							else if(i == 3) {
								if(!golds.containsKey(p.getName())) {
									golds.put(p.getName(), 8);
								}
								else if(golds.get(p.getName()) < 8) golds.replace(p.getName(), 8);
								else {
									exist(p);
								}
							}
							else if(i == 4) {
								if(!emeralds.containsKey(p.getName())) {
									emeralds.put(p.getName(), 2);
								}
								else {
									exist(p);
								}
							}
							else if(i == 5) {
								econ.depositPlayer(p, 500);
							}
							else if(i == 6) {
								econ.depositPlayer(p, 250);
							}
							else if(i == 7) {
								if(!fire.containsKey(p.getName())) {
									fire.put(p.getName(), 10);
								}
								else if(fire.get(p.getName()) < 10) fire.replace(p.getName(), 10);
								else {
									exist(p);
								}
							}
							else if(i == 8) {
								if(!cold.containsKey(p.getName())) {
									cold.put(p.getName(), 10);
								}
								else if(cold.get(p.getName()) < 10) cold.replace(p.getName(), 10);
								else {
									exist(p);
								}
							}
							else if(i == 9) {
								if(!prot.containsKey(p.getName())) {
									prot.put(p.getName(), 10);
								}
								else if(prot.get(p.getName()) < 10) prot.replace(p.getName(), 10);
								else {
									exist(p);
								}
							}
							else if(i == 10) {
								if(!extra_damage.containsKey(p.getName())) {
									extra_damage.put(p.getName(), 5);
								}
								else {
									exist(p);
								}
							}
							break;
						}
						i++;
					}
					if(!found) {
						p.sendMessage("§cНе повезло. Вам ничего не выпало");
					}
				}
				else if(it.isSimilar(p3)) {
					p.closeInventory();
					if(econ.getBalance(p.getName()) < 10000) {
						p.sendMessage("§cНе хватает монет!");
						return;
					}
					econ.withdrawPlayer(p.getName(), 10000);
					int rand = (int) (Math.random() * 100);
					int sum = 0;
					boolean found = false;
					int i = 0;
					for(Pair pair : perks3) {
						String name = pair.name;
						int chance = pair.chance;
						sum += chance;
						if(sum >= rand) {
							found = true;
							p.sendMessage("§7Вам выпало: "+name + " §7(§cодноразовый§7)");
							if(i == 0) {
								if(p.getMaxHealth() >= 26) {
									exist(p);
								}
								else {
									p.setMaxHealth(26);
									if(hp.containsKey(p.getName())) hp.replace(p.getName(), 26);
									else hp.put(p.getName(), 26);
								}
							}
							else if(i == 1) {
								if(!diamonds.containsKey(p.getName())) {
									diamonds.put(p.getName(), 16);
								}
								else if(diamonds.get(p.getName()) < 16) diamonds.replace(p.getName(), 16);
								else {
									exist(p);
								}
							}
							else if(i == 2) {
								if(!golds.containsKey(p.getName())) {
									golds.put(p.getName(), 24);
								}
								else if(golds.get(p.getName()) < 24) golds.replace(p.getName(), 24);
								else {
									exist(p);
								}
							}
							else if(i == 3) {
								if(!emeralds.containsKey(p.getName())) {
									emeralds.put(p.getName(), 4);
								}
								else if(emeralds.get(p.getName()) < 4) emeralds.replace(p.getName(), 4);
								else {
									exist(p);
								}
							}
							else if(i == 4) {
								econ.depositPlayer(p, 1000);
							}
							else if(i == 5) {
								econ.depositPlayer(p, 3000);
							}
							else if(i == 6) {
								if(!fire.containsKey(p.getName())) {
									fire.put(p.getName(), 20);
								}
								else if(fire.get(p.getName()) < 20) fire.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 7) {
								if(!cold.containsKey(p.getName())) {
									cold.put(p.getName(), 20);
								}
								else if(cold.get(p.getName()) < 20) cold.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 9) {
								if(!prot.containsKey(p.getName())) {
									prot.put(p.getName(), 20);
								}
								else if(prot.get(p.getName()) < 20) prot.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 10) {
								if(!extra_damage.containsKey(p.getName())) {
									extra_damage.put(p.getName(), 15);
								}
								else if(extra_damage.get(p.getName()) < 15) extra_damage.replace(p.getName(), 15);
								else {
									exist(p);
								}
							}
							else if(i == 11) {
								if(!poison.containsKey(p.getName())) {
									poison.put(p.getName(), 15);
								}
								else {
									exist(p);
								}
							}
							break;
						}
						i++;
					}
					if(!found) {
						p.sendMessage("§cНе повезло. Вам ничего не выпало");
					}
				}
				else if(it.isSimilar(p4)) {
					p.closeInventory();
					if(!p.hasPermission("API.mvp")) {
						p.sendMessage("§cНет прав!");
						return;
					}
					if(econ.getBalance(p.getName()) < 500) {
						p.sendMessage("§cНе хватает монет!");
						return;
					}
					econ.withdrawPlayer(p.getName(), 500);
					int rand = (int) (Math.random() * 100);
					int sum = 0;
					boolean found = false;
					int i = 0;
					for(Pair pair : perks4) {
						String name = pair.name;
						int chance = pair.chance;
						sum += chance;
						if(sum >= rand) {
							found = true;
							p.sendMessage("§7Вам выпало: "+name + " §7(§cодноразовый§7)");
							if(i == 0) {
								if(p.getMaxHealth() >= 26) {
									exist(p);
								}
								else {
									p.setMaxHealth(26);
									if(hp.containsKey(p.getName())) hp.replace(p.getName(), 26);
									else hp.put(p.getName(), 26);
								}
							}
							else if(i == 1) {
								if(!diamonds.containsKey(p.getName())) {
									diamonds.put(p.getName(), 16);
								}
								else if(diamonds.get(p.getName()) < 16) diamonds.replace(p.getName(), 16);
								else {
									exist(p);
								}
							}
							else if(i == 2) {
								if(!golds.containsKey(p.getName())) {
									golds.put(p.getName(), 24);
								}
								else if(golds.get(p.getName()) < 24) golds.replace(p.getName(), 24);
								else {
									exist(p);
								}
							}
							else if(i == 3) {
								if(!emeralds.containsKey(p.getName())) {
									emeralds.put(p.getName(), 4);
								}
								else if(emeralds.get(p.getName()) < 4) emeralds.replace(p.getName(), 4);
								else {
									exist(p);
								}
							}
							else if(i == 4) {
								if(!fire.containsKey(p.getName())) {
									fire.put(p.getName(), 20);
								}
								else if(fire.get(p.getName()) < 20) fire.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 5) {
								if(!cold.containsKey(p.getName())) {
									cold.put(p.getName(), 20);
								}
								else if(cold.get(p.getName()) < 20) cold.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 6) {
								if(!prot.containsKey(p.getName())) {
									prot.put(p.getName(), 20);
								}
								else if(prot.get(p.getName()) < 20) prot.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 7) {
								if(!extra_damage.containsKey(p.getName())) {
									extra_damage.put(p.getName(), 15);
								}
								else if(extra_damage.get(p.getName()) < 15) extra_damage.replace(p.getName(), 15);
								else {
									exist(p);
								}
							}
							else if(i == 8) {
								if(!poison.containsKey(p.getName())) {
									poison.put(p.getName(), 15);
								}
								else if(poison.get(p.getName()) < 15) poison.replace(p.getName(), 15);
								else {
									exist(p);
								}
							}
							else if(i == 9) {
								if(!infinity.contains(p.getName())) {
									infinity.add(p.getName());
								}
								else {
									exist(p);
								}
							}
							break;
						}
						i++;
					}
					if(!found) {
						p.sendMessage("§cНе повезло. Вам ничего не выпало");
					}
				}
				else if(it.isSimilar(p5)) {
					p.closeInventory();
					if(!p.hasPermission("API.premium")) {
						p.sendMessage("§cНет прав!");
						return;
					}
					if(econ.getBalance(p.getName()) < 300) {
						p.sendMessage("§cНе хватает монет!");
						return;
					}
					econ.withdrawPlayer(p.getName(), 300);
					int rand = (int) (Math.random() * 100);
					int sum = 0;
					boolean found = false;
					int i = 0;
					for(Pair pair : perks5) {
						String name = pair.name;
						int chance = pair.chance;
						sum += chance;
						if(sum >= rand) {
							found = true;
							p.sendMessage("§7Вам выпало: "+name + " §7(§cодноразовый§7)");
							if(i == 0) {
								if(p.getMaxHealth() >= 28) {
									exist(p);
								}
								else {
									p.setMaxHealth(28);
									if(hp.containsKey(p.getName())) hp.replace(p.getName(), 28);
									else hp.put(p.getName(), 28);
								}
							}
							else if(i == 1) {
								if(!diamonds.containsKey(p.getName())) {
									diamonds.put(p.getName(), 24);
								}
								else if(diamonds.get(p.getName()) < 24) diamonds.replace(p.getName(), 24);
								else {
									exist(p);
								}
							}
							else if(i == 2) {
								if(!emeralds.containsKey(p.getName())) {
									emeralds.put(p.getName(), 6);
								}
								else if(emeralds.get(p.getName()) < 6) emeralds.replace(p.getName(), 6);
								else {
									exist(p);
								}
							}
							else if(i == 3) {
								if(!fire.containsKey(p.getName())) {
									fire.put(p.getName(), 25);
								}
								else if(fire.get(p.getName()) < 25) fire.replace(p.getName(), 25);
								else {
									exist(p);
								}
							}
							else if(i == 4) {
								if(!cold.containsKey(p.getName())) {
									cold.put(p.getName(), 25);
								}
								else if(cold.get(p.getName()) < 25) cold.replace(p.getName(), 25);
								else {
									exist(p);
								}
							}
							else if(i == 5) {
								if(!prot.containsKey(p.getName())) {
									prot.put(p.getName(), 25);
								}
								else if(prot.get(p.getName()) < 25) prot.replace(p.getName(), 25);
								else {
									exist(p);
								}
							}
							else if(i == 6) {
								if(!extra_damage.containsKey(p.getName())) {
									extra_damage.put(p.getName(), 20);
								}
								else if(extra_damage.get(p.getName()) < 20) extra_damage.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 7) {
								if(!poison.containsKey(p.getName())) {
									poison.put(p.getName(), 20);
								}
								else if(poison.get(p.getName()) < 20) poison.replace(p.getName(), 20);
								else {
									exist(p);
								}
							}
							else if(i == 8) {
								if(!infinity.contains(p.getName())) {
									infinity.add(p.getName());
								}
								else {
									exist(p);
								}
							}
							else if(i == 9) {
								if(!regen.contains(p.getName())) {
									regen.add(p.getName());
								}
								else {
									exist(p);
								}
							}
							else if(i == 10) {
								if(!fast.contains(p.getName())) {
									fast.add(p.getName());
								}
								else {
									exist(p);
								}
							}
							break;
						}
						i++;
					}
					if(!found) {
						p.sendMessage("§cНе повезло. Вам ничего не выпало");
					}
				}
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		ItemStack item2 = e.getItemDrop().getItemStack();
		if(status.equals("wait") && (item2.isSimilar(item) || item2.isSimilar(lobby) || item2.getType().toString().contains("WOOL"))) e.setCancelled(true);
		if(status.equals("play") && item2.getType().toString().contains("SWORD")) e.setCancelled(true);
	}
	
	
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(status.equals("wait")) e.setCancelled(true);
		if(p.getItemInHand().isSimilar(item)) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				p.openInventory(inv);
			}
		}
		else if(p.getItemInHand().isSimilar(lobby)) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				toLobby(p);
			}
		}
		else if(status.equals("wait") && p.getItemInHand().getType().toString().contains("WOOL")) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				p.openInventory(team_inv);
			}
		}
		else if(p.getItemInHand().getType() == Material.FIREWORK_ROCKET && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
			removeItem(p.getInventory(), Material.FIREWORK_ROCKET, 1);
			TNTPrimed tnt = (TNTPrimed) p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
			tnt.setYield(15);
			tnt.setVelocity(p.getLocation().getDirection().normalize().multiply(4));
			tnt.setFuseTicks(15);
		}
		if(status.equals("play")) {
			String name = player_teams.get(p.getName());
			if(e.getAction() == Action.LEFT_CLICK_BLOCK && team_speed.get(name) >= 2) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20*15, team_speed.get(name)-1));
			}
			else if(p.getItemInHand().getType() == Material.FIRE_CHARGE && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
                if(!fireball_cooldowns.contains(p.getName())) {
                	p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
                    Location start = p.getLocation().add(0, 1, 0);
                    Vector facing = p.getEyeLocation().add(p.getLocation().getDirection()).toVector();
                    Vector initialV = facing.subtract(start.toVector()).normalize();
                    Fireball fireball = p.getWorld().spawn(start, Fireball.class);
                    fireball.setYield(2);
                    fireball_cooldowns.add(p.getName());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    	public void run() {
                    		fireball_cooldowns.remove(p.getName());
                    	}
                    }, 40);
                }
                e.setCancelled(true);
			}
		}
		if(infinity.contains(p.getName())) {
			if(status.equals("play") && p.getItemInHand().getType() == Material.BOW) {
				if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if(!p.getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE)) {
						ItemMeta im = p.getItemInHand().getItemMeta();
						im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
						p.getItemInHand().setItemMeta(im);
						p.sendMessage("§7Бесконечные стрелы активированы!");
					}
				}
			}
		}
	}
	
	public boolean isFull(Inventory inv) {
		for(ItemStack it : inv.getContents()) {
			if(it == null) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasBlock(String team, Material mat) {
		Location bl = getConfig().getLocation("arena.commands."+team+".bed");
		int x = bl.getBlockX(), z = bl.getBlockZ(), y = bl.getBlockY();
		for(int xx = x-5; xx <= x+5; ++xx) {
			for(int zz = z-5; zz <= z+5; ++zz) {
				if(Bukkit.getWorld("world").getBlockAt(xx, y, zz).getType() == mat) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String inBase(Player p) {
		Location loc = p.getLocation();
		String res = "no";
		for(String team : teams.keySet()) {
			if(!teams.get(team).isEmpty()) {
				Location bl = getConfig().getLocation("arena.commands."+team+".bed");
				int x1 = loc.getBlockX(), y1 = loc.getBlockY(), z1 = loc.getBlockZ();
				int x2 = bl.getBlockX(), y2 = bl.getBlockY(), z2 = bl.getBlockZ();
				if(Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2)+Math.abs(z1-z2)*Math.abs(z1-z2)) <= 10) {
					res = team;
					break;
				}
			}
		}
		return res;
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		String msg = e.getMessage();
		Player p = e.getPlayer();
		if(!player_teams.containsKey(p.getName())) {
			e.setMessage("§7[§6Зритель§7] "+msg);
			for(Player pl : Bukkit.getOnlinePlayers()){
	            e.getRecipients().remove(pl);
	            if(!player_teams.containsKey(pl.getName())) e.getRecipients().add(pl);
	        }
		}
		else {
			String team = player_teams.get(p.getName());
			String c = getConfig().getString("arena.commands."+team+".color");
			if(msg.charAt(0) == '!') {
				e.setMessage("§7[§fВСЕМ§7] "+msg.substring(1));
			}
			else {
				e.setMessage("§7[§"+c+team+"§7] "+msg);
				for(Player pl : Bukkit.getOnlinePlayers()){
		            e.getRecipients().remove(pl);
		        }
				for(String p_name : teams.get(team)) {
					e.getRecipients().add(Bukkit.getPlayer(p_name));
				}
			}
		}
	}
	
	public void deleteTrap(String team) {
		Inventory inv = saved_up.get(team);
		
		ItemStack trap = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta meta = trap.getItemMeta();
		meta.setDisplayName("§eЛовушка");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Цена: §c2 §bАлмаз(-а,-ов)");
		lore.add("§7Одноразовая ловушка, замедляющая скорость копания врага на вашей базе");
		meta.setLore(lore);
		trap.setItemMeta(meta);
		
		inv.setItem(31, trap);
		saved_up.replace(team, inv);
		team_trap.replace(team, false);
	}
	
	@EventHandler
	public void interactEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(status.equals("play")) {
			if(e.getRightClicked() instanceof Villager) {
				Villager vil = (Villager) e.getRightClicked();
				if(vil.getCustomName().equals("§aМагазин")) {
					p.openInventory(shop);
				}
				else if(vil.getCustomName().equals("§cПрокачки")) {
					p.openInventory(saved_up.get(player_teams.get(p.getName())));
				}
			}
		}
	}
	
	public static void spawnFireworks(Location location, int amount){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
       
        fw.setFireworkMeta(fwm);
        fw.detonate();
       
        for(int i = 0;i < amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }
	
	public void remCages() {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		for(String name : cs.getKeys(false)) {
			Location loc = getConfig().getLocation("arena.commands."+name+".spawn");
			ItemStack cage = new ItemStack(Material.AIR);
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			for(int xx = x-1; xx <= x+1; ++xx) {
				for(int zz = z-1; zz <= z+1; ++zz) {
					Bukkit.getWorld("world").getBlockAt(xx, y+3, zz).setType(cage.getType());
				}
			}
			for(int yy = y; yy <= y+3; ++yy) {
				for(int zz = z-1; zz <= z+1; ++zz) {
					Bukkit.getWorld("world").getBlockAt(x-2, yy, zz).setType(cage.getType());	
					Bukkit.getWorld("world").getBlockAt(x+2, yy, zz).setType(cage.getType());
				}
			}
			for(int yy = y; yy <= y+3; ++yy) {
				for(int xx = x-1; xx <= x+1; ++xx) {
					Bukkit.getWorld("world").getBlockAt(xx, yy, z-2).setType(cage.getType());	
					Bukkit.getWorld("world").getBlockAt(xx, yy, z+2).setType(cage.getType());
				}
			}
		}
	}
	
	public void addCages() {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		for(String name : cs.getKeys(false)) {
			Location loc = getConfig().getLocation("arena.commands."+name+".spawn");
			ItemStack cage = new ItemStack(Material.BARRIER);
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			for(int xx = x-1; xx <= x+1; ++xx) {
				for(int zz = z-1; zz <= z+1; ++zz) {
					Bukkit.getWorld("world").getBlockAt(xx, y+3, zz).setType(cage.getType());
				}
			}
			for(int yy = y; yy <= y+3; ++yy) {
				for(int zz = z-1; zz <= z+1; ++zz) {
					Bukkit.getWorld("world").getBlockAt(x-2, yy, zz).setType(cage.getType());	
					Bukkit.getWorld("world").getBlockAt(x+2, yy, zz).setType(cage.getType());
				}
			}
			for(int yy = y; yy <= y+3; ++yy) {
				for(int xx = x-1; xx <= x+1; ++xx) {
					Bukkit.getWorld("world").getBlockAt(xx, yy, z-2).setType(cage.getType());	
					Bukkit.getWorld("world").getBlockAt(xx, yy, z+2).setType(cage.getType());
				}
			}
		}
	}
	
	@EventHandler
	public void death(EntityDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player killer = p.getKiller();
			if(invis.contains(p.getName())) {
				invis.remove(p.getName());
				ItemStack helmet = player_helmet.get(p.getName());
				ItemStack chestpate = player_chestplate.get(p.getName());
				ItemStack leggings = player_leggings.get(p.getName());
				ItemStack boots = player_boots.get(p.getName());
				
				p.getInventory().setHelmet(helmet);
				p.getInventory().setChestplate(chestpate);
				p.getInventory().setLeggings(leggings);
				p.getInventory().setBoots(boots);
				send(p, "&cВас ударили! Эффект невидимости снят!");
				if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) p.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			if(killer != null && killer instanceof Player) {
				if(regen.contains(killer.getName())) {
					killer.sendMessage("§aРегенерация...");
					killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*3, 3));
				}
				sendAll(p.getDisplayName()+" &7был убит "+killer.getDisplayName());
				addKill(killer);
				for(ItemStack gg : e.getDrops()) {
					if(gg != null) {
						if(gg.getType() == Material.IRON_INGOT || gg.getType() == Material.GOLD_INGOT || gg.getType() == Material.DIAMOND || gg.getType() == Material.EMERALD) {
							killer.getInventory().addItem(gg);
							killer.updateInventory();
						}
					}
				}
			}
			else {
				if(p.getLastDamageCause() == null || p.getLastDamageCause().getCause() == null) {
					sendAll(p.getDisplayName()+" &7погиб");
				}
				else {
					DamageCause deathCause = p.getLastDamageCause().getCause();
					if (deathCause == DamageCause.DROWNING) {
						sendAll(p.getDisplayName()+" &7задохнулся");
					}
					else if (deathCause == DamageCause.VOID) {
						if(e.getDrops().contains(new ItemStack(Material.ENDER_PEARL))) {
							sendAll(p.getDisplayName()+" &7не додумался кинуть &9эндер-пёрл");
						}
						else sendAll(p.getDisplayName()+" &7спрыгнул в бездну");
					}
					else if (deathCause == DamageCause.BLOCK_EXPLOSION || deathCause == DamageCause.ENTITY_EXPLOSION) {
						sendAll(p.getDisplayName()+" &7разорвало на куски");
					}
					else if (deathCause == DamageCause.FALL) {
						if(e.getDrops().contains(new ItemStack(Material.WATER_BUCKET))) {
							sendAll(p.getDisplayName()+" &7не смог сделать &bватер-дроп");
						}
						else sendAll(p.getDisplayName()+" &7разбился");
					}
					else if (killer instanceof Bee) {
						sendAll(p.getDisplayName()+" &7был закусан до смерти");
					}
					else if (deathCause == DamageCause.FIRE) {
						sendAll(p.getDisplayName()+" &7сгорел");
					}
					else if (deathCause == DamageCause.FALLING_BLOCK) {
						sendAll(p.getDisplayName()+" &7получил по башке");
					}
					else {
						sendAll(p.getDisplayName()+" &7погиб");
					}
				}
			}
			e.getDrops().clear();
			String team = player_teams.get(p.getName());
			if(!bed_alive.contains(team)) {
				count_players--;
				updateDB("play", count_players);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title {\"text\":\"ВЫ ПОГИБЛИ\", \"bold\":true, \"color\":\"red\"}");
				if(killer != null && killer instanceof Player) {
					send(killer, "&eВы сделали финальное убийство! Получено: &c"+String.valueOf(final_kill_money)+" медных &eмонет");
					econ.depositPlayer(killer, final_kill_money);
					addFinalKill(killer);
				}
				removePlayerFromTeam(p);
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> p.spigot().respawn(), 1);
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						toSpec(p);
					}
				}, 20);
				if(isEnd()) {
					endGame();
				}
			}
			else {
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> p.spigot().respawn(), 1);
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title {\"text\":\"ВЫ ПОГИБЛИ\", \"bold\":true, \"color\":\"red\"}");
						p.teleport(spec);
						p.setGameMode(GameMode.SPECTATOR);
						if(!fast.contains(p.getName())) send(p, "&eВы будете возрождены через &c5 &eсекунд!");
						else send(p, "&eВы будете возрождены через &c1 &eсекунду!");
					}
				}, 2);
				if(fast.contains(p.getName())) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							tpToSpawn(p);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title {\"text\":\"ВОЗРОЖДЕНИЕ\", \"bold\":true, \"color\":\"green\"}");
							send(p, "&aВы были возрождены!");
							if(hp.containsKey(p.getName())) p.setMaxHealth(hp.get(p.getName()));
							else p.setMaxHealth(20);
							returnItems(p);
							p.setGameMode(GameMode.SURVIVAL);
						}
					}, 20*1);
				}
				else {
					Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							tpToSpawn(p);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title {\"text\":\"ВОЗРОЖДЕНИЕ\", \"bold\":true, \"color\":\"green\"}");
							send(p, "&aВы были возрождены!");
							if(hp.containsKey(p.getName())) p.setMaxHealth(hp.get(p.getName()));
							else p.setMaxHealth(20);
							returnItems(p);
							p.setGameMode(GameMode.SURVIVAL);
						}
					}, 20*5);
				}
			}
		}
	}
	
	@EventHandler
	public void move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(status.equals("play")) {
			if(p.getLocation().getBlockY() <= -80) {
				p.setHealth(0);
			}
			if(compass) {
				Location loc = p.getLocation();
				Location min_lc = loc;
				int min_dis = 100000000;
				for(String p_name : player_teams.keySet()) {
					Player pl = Bukkit.getPlayer(p_name);
					if(!player_teams.get(p.getName()).equals(player_teams.get(p_name))) {
						if(pl != null && pl.isOnline() && !pl.getName().equals(p.getName())) {
							Location lc = pl.getLocation();
							int dis = (int) Math.sqrt(Math.pow(loc.getBlockX()-lc.getBlockX(), 2) + Math.pow(loc.getBlockY()-lc.getBlockY(), 2) + Math.pow(loc.getBlockZ()-lc.getBlockZ(), 2));
							if(dis < min_dis) {
								min_dis = dis;
								min_lc = lc;
							}
						}
					}
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" actionbar \"§eДо ближайшего игрока §c"+String.valueOf(min_dis)+" §eблоков \"");
			}
		}
	}
	
	public void toSpec(Player p) {
		p.teleport(spec);
		send(p, "&6Вы зритель! Введите &c/hub &6чтобы вернуться в лобби");
		p.setGameMode(GameMode.SPECTATOR);
		p.getInventory().setItem(8, lobby);
	}
	
	public void addPlayerToTeam(Player p, String name) {
		if(player_teams.containsKey(p.getName())) {
			player_teams.replace(p.getName(), name);
		}
		else player_teams.put(p.getName(), name);
		List<String> pl = new ArrayList<String>();
		if(!teams.isEmpty()) {
			for(String p_name : teams.get(name)) {
				pl.add(p_name);
			}
		}
		pl.add(p.getName());
		if(teams.containsKey(name)) teams.replace(name, pl);
		else teams.put(name, pl);
	}
	
	public void addToTeam(Player p) {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		String command = null;
		for(String name : cs.getKeys(false)) {
			if(teams.isEmpty() || teams.get(name).size() < players_in_team) {
				command = name;
				break;
			}
		}
		addPlayerToTeam(p, command);
		setColor(p, command);
		Location loc = getConfig().getLocation("arena.commands."+command+".spawn");
		p.teleport(loc);
	}
	
	public void tpToSpawn(Player p) {
		String command = player_teams.get(p.getName());
		Location loc = getConfig().getLocation("arena.commands."+command+".spawn");
		p.teleport(loc);
	}
	
	public void sendAll(String msg) {
		for(Player pl : Bukkit.getOnlinePlayers()) {
			send(pl, msg);
		}
	}
	
	public void removePlayerFromTeam(Player p) {
		if(player_teams.containsKey(p.getName())) {
			String team = player_teams.get(p.getName());
			player_teams.remove(p.getName());
			List<String> list = new ArrayList<String>();
			for(String pl : teams.get(team)) {
				if(!pl.equals(p.getName())) {
					list.add(pl);
				}
			}
			teams.replace(team, list);
		}
	}
	
	public void setColor(Player p, String team) {
		String c = getConfig().getString("arena.commands."+team+".color");
		String name = p.getDisplayName();
		String[] split = name.split(":");
		String nn = "§"+c+p.getName();
		p.setDisplayName(split[0]+": "+nn+"§f");
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtabname "+nn);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtagname "+nn);
			}
		}, 20);
	}
	
	public void clearColor(Player p) {
		String name = p.getDisplayName();
		String[] split = name.split(":");
		String nn = "§f"+p.getName();
		p.setDisplayName(split[0]+": "+nn);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtabname");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtagname");
			}
		}, 20);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				scoreboardUpdate(p);
			}
		}, 0, 19*1);
		if(status.equals("wait")) {
			int min_players = players_in_team*min_teams;
			if(max_teams == 2 && min_teams == 2) min_players = (int) ((int) players_in_team*1.5);
			if(hp.containsKey(p.getName())) p.setMaxHealth(hp.get(p.getName()));
			else p.setMaxHealth(20);
			count_players++;
			updateDB(status, count_players);
			p.getInventory().setItem(4, item);
			p.getInventory().setItem(8, lobby);
			addToTeam(p);
			
			ItemStack now = getConfig().getItemStack("arena.commands."+player_teams.get(p.getName())+".block");
			ItemMeta meta = now.getItemMeta();
			meta.setDisplayName("§aВыбор команды");
			now.setItemMeta(meta);
			
			p.getInventory().setItem(0, now);
			
			sendAll(p.getDisplayName()+" &eприсоединился к игре &c"+String.valueOf(count_players)+"&a/&c"+String.valueOf(max_teams*players_in_team));
			if(!timer && count_players >= min_players) {
				startTimer();
			}
		}
		else if(status.equals("play")) {
			send(p, "&7Идёт игра...");
			toSpec(p);
			clearColor(p);
		}
		else {
			if(!p.hasPermission("API.glav")) toLobby(p);
		}
	}
	
	public boolean isEnd() {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		int alives = 0;
		for(String team : cs.getKeys(false)) {
			if(!teams.get(team).isEmpty()) alives++;
		}
		if(alives == 1) return true;
		return false;
	}
	
	String getWinners() {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		String res = "";
		for(String team : cs.getKeys(false)) {
			if(!teams.get(team).isEmpty()) {
				res = team;
				break;
			}
		}
		return res;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtabname");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtagname");
		updateDB(status, count_players);
		if(status.equals("wait")) {
			int min_players = players_in_team*min_teams;
			count_players--;
			updateDB("wait", count_players);
			sendAll(p.getDisplayName()+" &eпокинул игру &c"+String.valueOf(count_players)+"&a/&c"+String.valueOf(max_teams*players_in_team));
			removePlayerFromTeam(p);
			if(timer && count_players < min_players) {
				stopTimer();
			}
		}
		else if(status.equals("play") && player_teams.containsKey(p.getName())) {
			count_players--;
			updateDB("play", count_players);
			String team = player_teams.get(p.getName());
			String c = getConfig().getString("arena.commands."+team+".color");
			sendAll(p.getDisplayName()+" &7покинул игру");
			removePlayerFromTeam(p);
			if(bed_alive.contains(team)) {
				if(teams.get(team).isEmpty()) {
					sendAll("&cКровать команды &"+c+team+" &cбыла разрушена, так как команда пустая");
					bed_alive.remove(team);
				}
			}
			if(isEnd()) {
				endGame();
			}
		}
	}
	
	public void stopTimer() {
		Bukkit.getScheduler().cancelTask(taskID);
		LOBBY_TIME = getConfig().getInt("lobby_time");
		timer = false;
	}
	
	public void stopGameTimer() {
		Bukkit.getScheduler().cancelTask(taskID);
	}
	
	public void endGame() {
		boolean is = false;
		stopGameTimer();
		status = "reload";
		updateDB(status, 0);
		if(!isEnd()) {
			is = true;
			sendAll("&cОднозначный победитель не был определён. Победные монеты будут распределены оставшимся командам");
		}
		int size = 0;
		for(String team : teams.keySet()) {
			if(!teams.get(team).isEmpty()) {
				size++;
			}
		}
		if(size != 0) {
			int each_money = (int) (win_money/size);
			for(String team : teams.keySet()) {
				if(!teams.get(team).isEmpty()) {
					for(String p_name : teams.get(team)) {
						Player pl = Bukkit.getPlayer(p_name);
						Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
							public void run() {
								spawnFireworks(pl.getLocation(), 3);
							}
						}, 0, 20);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p_name+" title {\"text\":\"ПОБЕДА\", \"bold\":true, \"color\":\"gold\"}");
						if(is) send(pl, "&7Ваша команда осталась в живых, вы получаете часть победных монет");
						else {
							addWin(pl);
						}
						send(pl, "&7Получено: &c"+String.valueOf(each_money)+" медных &7монет");
						pl.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
						pl.getInventory().setItem(0, new ItemStack(Material.FIREWORK_ROCKET, 64));
						econ.depositPlayer(pl, each_money);
					}
				}
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					toLobby(pl);
				}
			}
		}, 20*12);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}
		}, 20*15);
	}
	
	public void startTimer() {
		timer = true;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		taskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if(LOBBY_TIME%5 == 0 || LOBBY_TIME <= 5) {
					sendAll("&7Игра начнётся через &c"+String.valueOf(LOBBY_TIME)+" &7секунд");
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.playSound(pl.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1, 1);
					}
				}
				LOBBY_TIME--;
				if(LOBBY_TIME <= 0) {
					status = "play";
					sendAll("&aИгра началась! Удачи!");
					try {
						int i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.blocks")) {
							inv_blocks.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.armor")) {
							inv_armor.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.swords")) {
							inv_swords.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.bows")) {
							inv_bows.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.tools")) {
							inv_tools.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.useful")) {
							inv_useful.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.potions")) {
							inv_potions.setItem(i, it);
							i++;
						}
					} catch (Exception e) {};
					startGame();
				}
			}
		}, 0, 20*1);
	}
	
	@EventHandler
	public void prrr(ProjectileLaunchEvent e) {
		if(e.getEntity().getType() == EntityType.EGG) {
			Egg egg = (Egg) e.getEntity();
			Vector v = e.getEntity().getVelocity();
			BlockIterator iterator = new BlockIterator(egg.getWorld(), egg
                    .getLocation().toVector(), egg.getVelocity().normalize(), 0.0D,
                    30);
                           Block hitBlock = null;
              boolean ch = true;
              while (iterator.hasNext()) {
                  hitBlock = iterator.next();
                  if(!ch) {
	                  if (hitBlock.getType() == Material.AIR) {
	                      hitBlock.setType(Material.SANDSTONE);
	                      Location loc = hitBlock.getLocation();
	                      Location loc1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()+1);
	                      Location loc2 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()-1);
	                      Location loc3 = new Location(loc.getWorld(), loc.getBlockX()+1, loc.getBlockY(), loc.getBlockZ());
	                      Location loc4 = new Location(loc.getWorld(), loc.getBlockX()-1, loc.getBlockY(), loc.getBlockZ());
	                      loc1.getBlock().setType(Material.SANDSTONE);
	                      loc2.getBlock().setType(Material.SANDSTONE);
	                      loc3.getBlock().setType(Material.SANDSTONE);
	                      loc4.getBlock().setType(Material.SANDSTONE);
	                      blocks.add(loc);
	                      blocks.add(loc1);
	                      blocks.add(loc2);
	                      blocks.add(loc3);
	                      blocks.add(loc4);
	                  }
	                  else if(!blocks.contains(hitBlock.getLocation())) {
	                	  break;
	                  }
                  }
                  ch = false;
              }
		}
	}
	
	public void spawnNPCS() {
		for(Location loc : shops) {
			Villager vil = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
			vil.setAI(false);
			vil.setCustomName("§aМагазин");
			vil.setCustomNameVisible(true);
		}
		for(Location loc : upgrades) {
			Villager vil = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
			vil.setAI(false);
			vil.setCustomName("§cПрокачки");
			vil.setCustomNameVisible(true);
		}
	}
	
	@EventHandler
	public void onPlayerCraft(CraftItemEvent e) {
		e.setCancelled(true);
	}
	
	public void returnItems(Player p) {
		ItemStack helmet = player_helmet.get(p.getName());
		ItemStack chestplate = player_chestplate.get(p.getName());
		ItemStack leggings = player_leggings.get(p.getName());
		ItemStack boots = player_boots.get(p.getName());
		ItemStack sword = player_sword.get(p.getName());
		p.getInventory().setItem(0, sword);
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(chestplate);
		p.getInventory().setLeggings(leggings);
		p.getInventory().setBoots(boots);
		p.updateInventory();
	}
	
	@EventHandler
	public void target(EntityTargetEvent e) {
		if(e.getEntity() instanceof Bee) {
			Bee bee = (Bee) e.getEntity();
			
		}
	}
	
	public void startGame() {
		stopTimer();
		updateDB(status, count_players);
		remCages();
		spawnNPCS();
		next_event = "§7§eI §7⟶ §eII§7";
		time = GAME_TIME-up_tier_time_2;
		for(String team : teams.keySet()) {
			stage.put(team, 1);
			if(!teams.get(team).isEmpty()) {
				actived_teams.add(team);
				ItemStack now = getConfig().getItemStack("arena.commands."+team+".block");
				ItemStack ttt = inv_blocks.getItem(0);
				ItemMeta meta = ttt.getItemMeta();
				Inventory n = Bukkit.createInventory(null, 9*3, "§cБлоки");
				ItemStack ff = new ItemStack(now.getType(), ttt.getAmount());
				ff.setItemMeta(meta);
				n.setContents(inv_blocks.getContents());
				n.setItem(0, ff);
				saved_inv.put(team, n);

				Inventory n2 = Bukkit.createInventory(null, 9*5, "§cПрокачки");
				n2.setContents(upgrade.getContents());
				saved_up.put(team, n2);
				
				team_swords.put(team, 1);
				team_armor.put(team, 1);
				team_speed.put(team, 1);
				team_trap.put(team, false);
				
				bed_alive.add(team);
			}
		}
		for(String p_name : player_teams.keySet()) {
			Player pl = Bukkit.getPlayer(p_name);
			pl.closeInventory();
			addGame(pl);
			String team = player_teams.get(p_name);
			ItemStack helmet = getConfig().getItemStack("arena.commands."+team+".armor.helmet");
			ItemStack chestplate = getConfig().getItemStack("arena.commands."+team+".armor.chestplate");
			ItemStack leggings = getConfig().getItemStack("arena.commands."+team+".armor.leggings");
			ItemStack boots = getConfig().getItemStack("arena.commands."+team+".armor.boots");
			ItemStack itt = new ItemStack(Material.WOODEN_SWORD);
			player_helmet.put(p_name, helmet);
			player_chestplate.put(p_name, chestplate);
			player_leggings.put(p_name, leggings);
			player_boots.put(p_name, boots);
			player_sword.put(p_name, itt);
			ItemMeta meta = itt.getItemMeta();
			meta.setUnbreakable(true);
			itt.setItemMeta(meta);
			pl.getInventory().setItem(0, itt);
			returnItems(Bukkit.getPlayer(p_name));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p_name+" title {\"text\":\"СТАРТ\", \"bold\":true, \"color\":\"green\"}");
		}
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if(status.equals("play")) {
					for(String p_name : player_teams.keySet()) {
						Player p = Bukkit.getPlayer(p_name);
						String base = inBase(p);
						String team = player_teams.get(p_name);
						if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							if(p.getPotionEffect(PotionEffectType.INVISIBILITY).getDuration() > 20*30) {
								p.removePotionEffect(PotionEffectType.INVISIBILITY);
								p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*30, 1));
							}
							if(!invis.contains(p_name)) {
								invis.add(p_name);
								p.getInventory().setHelmet(null);
								p.getInventory().setChestplate(null);
								p.getInventory().setLeggings(null);
								p.getInventory().setBoots(null);
								send(p, "&aВы невидимы!");
							}
						}
						else {
							if(invis.contains(p_name)) {
								invis.remove(p_name);
								ItemStack helmet = player_helmet.get(p_name);
								ItemStack chestpate = player_chestplate.get(p_name);
								ItemStack leggings = player_leggings.get(p_name);
								ItemStack boots = player_boots.get(p_name);
								
								p.getInventory().setHelmet(helmet);
								p.getInventory().setChestplate(chestpate);
								p.getInventory().setLeggings(leggings);
								p.getInventory().setBoots(boots);
							}
						}
						if(!base.equals("no") && !base.equals(team) && !teams.get(base).isEmpty() && bed_alive.contains(base)) {
							if(hasBlock(base, Material.BELL)) {
								for(String pp : teams.get(base)) {
									Player ppp = Bukkit.getPlayer(pp);
									send(ppp, "&cНа вашу базу вторглись враги!");
									ppp.playSound(ppp.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
								}
							}
							if(team_trap.get(base)) {
								deleteTrap(base);
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*10, 1));
								send(p, "&cНа вас подействовала ловушка замедленного копания!");
							}
							if(hasBlock(base, Material.CAMPFIRE)) {
								ItemStack helmet = p.getInventory().getHelmet();
								ItemStack chestplate = p.getInventory().getChestplate();
								ItemStack leggings = p.getInventory().getLeggings();
								ItemStack boots = p.getInventory().getBoots();
								if(helmet.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
									helmet.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
									p.getInventory().setHelmet(helmet);
								}
								if(chestplate.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
									chestplate.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
									p.getInventory().setChestplate(chestplate);
								}
								if(leggings.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
									leggings.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
									p.getInventory().setLeggings(leggings);
								}
								if(boots.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
									boots.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
									p.getInventory().setLeggings(boots);
								}
								int raw = 0;
								for(ItemStack sk : p.getInventory().getContents()) {
									if(sk!= null && sk.getType().toString().contains("SWORD")) {
										if(sk.containsEnchantment(Enchantment.DAMAGE_ALL)) {
											sk.removeEnchantment(Enchantment.DAMAGE_ALL);
											p.getInventory().setItem(raw, sk);
										}
									}
									raw++;
								}
							}
							else {
								if(!player_helmet.get(p_name).isSimilar(p.getInventory().getHelmet()) && !invis.contains(p_name) && status.equals("play")) {
									ItemStack helmet = player_helmet.get(p_name);
									ItemStack chestpate = player_chestplate.get(p_name);
									ItemStack leggings = player_leggings.get(p_name);
									ItemStack boots = player_boots.get(p_name);
									
									p.getInventory().setHelmet(helmet);
									p.getInventory().setChestplate(chestpate);
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
								}
								int raw = 0;
								for(ItemStack sk : p.getInventory().getContents()) {
									if(sk!= null && sk.getType().toString().contains("SWORD")) {
										if(!sk.getEnchantments().equals(player_sword.get(p_name).getEnchantments())) {
											sk.addEnchantments(player_sword.get(p_name).getEnchantments());
											p.getInventory().setItem(raw, sk);
										}
									}
									raw++;
								}
							}
						}
						else {
							if(!player_helmet.get(p_name).isSimilar(p.getInventory().getHelmet()) && !invis.contains(p_name)  && status.equals("play")) {
								ItemStack helmet = player_helmet.get(p_name);
								ItemStack chestpate = player_chestplate.get(p_name);
								ItemStack leggings = player_leggings.get(p_name);
								ItemStack boots = player_boots.get(p_name);
								
								p.getInventory().setHelmet(helmet);
								p.getInventory().setChestplate(chestpate);
								p.getInventory().setLeggings(leggings);
								p.getInventory().setBoots(boots);
							}
							int raw = 0;
							for(ItemStack sk : p.getInventory().getContents()) {
								if(sk!= null && sk.getType().toString().contains("SWORD")) {
									if(!sk.getEnchantments().equals(player_sword.get(p_name).getEnchantments())) {
										sk.addEnchantments(player_sword.get(p_name).getEnchantments());
										p.getInventory().setItem(raw, sk);
									}
								}
								raw++;
							}
						}
					}
				}
			}
		}, 0, 20);
		taskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				int d_time = getConfig().getInt("diamond_tier."+String.valueOf(diamond_stage)+".time");
				int d_amount = getConfig().getInt("diamond_tier."+String.valueOf(diamond_stage)+".amount");
				int e_time = getConfig().getInt("emerald_tier."+String.valueOf(emerald_stage)+".time");
				int e_amount = getConfig().getInt("emerald_tier."+String.valueOf(emerald_stage)+".amount");
				
				if(GAME_TIME%d_time == 0) {
					for(Location loc : diamonds_gen) {
						loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND, d_amount));
					}
				}
				if(GAME_TIME%e_time == 0) {
					for(Location loc : emeralds_gen) {
						loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.EMERALD, e_amount));
					}
				}
				
				for(ArmorStand as : d_armorstands) {
					as.setCustomName("§b"+GAME_TIME%d_time);
				}
				
				for(ArmorStand as : e_armorstands) {
					as.setCustomName("§b"+GAME_TIME%e_time);
				}
				
				ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
				for(String team : cs.getKeys(false)) {
					//if(!actived_teams.contains(team)) break;
					int st = stage.get(team);
					int i_time, i_amount, g_time, g_amount;
					if(st <= 3) {
						i_time = getConfig().getInt("iron_tier."+String.valueOf(st)+".time");
						i_amount = getConfig().getInt("iron_tier."+String.valueOf(st)+".amount");
						g_time = getConfig().getInt("gold_tier."+String.valueOf(st)+".time");
						g_amount = getConfig().getInt("gold_tier."+String.valueOf(st)+".amount");
					}
					else {
						i_time = getConfig().getInt("iron_tier."+String.valueOf(3)+".time");
						i_amount = getConfig().getInt("iron_tier."+String.valueOf(3)+".amount");
						g_time = getConfig().getInt("gold_tier."+String.valueOf(3)+".time");
						g_amount = getConfig().getInt("gold_tier."+String.valueOf(3)+".amount");
						e_time = getConfig().getInt("emerald_tier."+String.valueOf(st-3)+".time");
						e_amount = getConfig().getInt("emerald_tier."+String.valueOf(st-3)+".amount");
						if(GAME_TIME%e_time == 0) {
							for(Location loc : (List<Location>) getConfig().get("arena.commands."+team+".gold_gens")) {
								loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.EMERALD, e_amount));	
							}
						}
					}
					if(GAME_TIME%i_time == 0) {
						for(Location loc : (List<Location>) getConfig().get("arena.commands."+team+".iron_gens")) {
							loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.IRON_INGOT, i_amount));	
						}
					}
					if(GAME_TIME%g_time == 0) {
						for(Location loc : (List<Location>) getConfig().get("arena.commands."+team+".gold_gens")) {
							loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_INGOT, g_amount));	
						}
					}
				}
				if(GAME_TIME == up_tier_time_2) {
					sendAll("&bАлмазные &7и &2изумрудные &7генераторы были улучшены (&eI &7⟶ &eII&7)");
					next_event = "§7§eII §7⟶ §eIII§7";
					time = GAME_TIME-up_tier_time_3;
					diamond_stage = 2;
					emerald_stage = 2;
				}
				else if(GAME_TIME == up_tier_time_3) {
					sendAll("&bАлмазные &7и &2изумрудные &7генераторы были улучшены (&eII &7⟶ &eIII&7)");
					next_event = "§cРазрушение кроватей";
					time = GAME_TIME-bed_gone_time;
					diamond_stage = 3;
					emerald_stage = 3;
				}
				else if(GAME_TIME == bed_gone_time) {
					sendAll("&cКровати были разрушены! Вы больше не возродитесь!");
					compass = true;
					next_event = "§cКонец игры";
					time = GAME_TIME;
					bed_alive.clear();
				}
				if(GAME_TIME == 100 || GAME_TIME == 50 || GAME_TIME == 20 || GAME_TIME <= 10) {
					sendAll("&7Игра закончится через &c"+String.valueOf(GAME_TIME)+" &7секунд");
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.playSound(pl.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1, 1);
					}
				}
				time--;
				GAME_TIME--;
				if(GAME_TIME <= 0) {
					endGame();
				}
			}
		}, 0, 20*1);
		
		for(String p_name : player_teams.keySet()) {
			Player p = Bukkit.getPlayer(p_name);
			p.getInventory().remove(item);
			p.getInventory().remove(lobby);
			p.updateInventory();
			if(irons.containsKey(p.getName())) {
				p.getInventory().setItem(1, new ItemStack(Material.IRON_INGOT, irons.get(p.getName())));
				p.updateInventory();
				irons.remove(p.getName());
			}
			if(golds.containsKey(p.getName())) {
				p.getInventory().setItem(2, new ItemStack(Material.GOLD_INGOT, golds.get(p.getName())));
				p.updateInventory();
				golds.remove(p.getName());
			}
			if(diamonds.containsKey(p.getName())) {
				p.getInventory().setItem(3, new ItemStack(Material.DIAMOND, diamonds.get(p.getName())));
				p.updateInventory();
				diamonds.remove(p.getName());
			}
			if(emeralds.containsKey(p.getName())) {
				p.getInventory().setItem(4, new ItemStack(Material.EMERALD, emeralds.get(p.getName())));
				p.updateInventory();
				emeralds.remove(p.getName());
			}
		}
	}
	
	@EventHandler
	public void dam(EntityDamageEvent e) {
		if(status.equals("wait")) e.setCancelled(true);
		if(status.equals("reload")) {
			if(e.getEntity() instanceof Player) e.setCancelled(true);
		}
		if(e.getEntity() instanceof Villager) {
			if(!status.equals("reload")) e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void bloccck(PlayerSwapHandItemsEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if(status.equals("wait")) e.setCancelled(true);
		if(e.getEntity() instanceof Villager) {
			if(!status.equals("reload")) {
				Villager vil = (Villager) e.getEntity();
				if(vil.getCustomName() != null) e.setCancelled(true);
			}
		}
		else if(e.getDamager() instanceof TNTPrimed && e.getEntity() instanceof Player) {
			TNTPrimed tnt = (TNTPrimed) e.getDamager();
			Player p = (Player) e.getEntity();
			String name = tnt.getCustomName();
			String team = player_teams.get(p.getName());
			if(teams.get(team).contains(name)) {
				e.setDamage(0.0);
			}
		}
		else if(e.getEntity() instanceof Player && e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			Player p = (Player) e.getEntity();
			if(e.getDamager() instanceof Arrow) {
				Arrow a = (Arrow) e.getDamager();
				Player damager = (Player) a.getShooter();
				String team = player_teams.get(p.getName());
				String team2 = player_teams.get(damager.getName());
				if(team.equals(team2)) e.setCancelled(true);
			}
			else if(e.getDamager() instanceof Trident) {
				Trident a = (Trident) e.getDamager();
				Player damager = (Player) a.getShooter();
				String team = player_teams.get(p.getName());
				String team2 = player_teams.get(damager.getName());
				if(team.equals(team2)) e.setCancelled(true);
			}
		}
		else if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			String p_name = p.getName();
			String team = player_teams.get(p_name);
			String team2 = player_teams.get(damager.getName());
			if(team.equals(team2)) e.setCancelled(true);
			if(invis.contains(p_name)) {
				invis.remove(p_name);
				ItemStack helmet = player_helmet.get(p_name);
				ItemStack chestpate = player_chestplate.get(p_name);
				ItemStack leggings = player_leggings.get(p_name);
				ItemStack boots = player_boots.get(p_name);
				
				p.getInventory().setHelmet(helmet);
				p.getInventory().setChestplate(chestpate);
				p.getInventory().setLeggings(leggings);
				p.getInventory().setBoots(boots);
				send(p, "&cВас ударили! Эффект невидимости снят!");
				if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) p.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			if(!e.isCancelled() && !p.isBlocking()) {
				if(fire.containsKey(damager.getName())) {
					int chance = fire.get(damager.getName());
					int rand = (int) (Math.random() * 100);
					if(rand <= chance) {
						damager.sendMessage("§7Вы подожгли противника!");
						p.setFireTicks(40);
					}
				}
				if(cold.containsKey(damager.getName())) {
					int chance = cold.get(damager.getName());
					int rand = (int) (Math.random() * 100);
					if(rand <= chance) {
						damager.sendMessage("§7Вы заморозили противника!");
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*2, 5));
					}
				}
				if(prot.containsKey(p.getName())) {
					int chance = cold.get(p.getName());
					int rand = (int) (Math.random() * 100);
					if(rand <= chance) {
						p.sendMessage("§7Вы отразили половину урона!");
						e.setDamage(e.getDamage()/2);
					}
				}
				if(extra_damage.containsKey(damager.getName())) {
					int chance = extra_damage.get(damager.getName());
					int rand = (int) (Math.random() * 100);
					if(rand <= chance) {
						damager.sendMessage("§7Вы нанесли дополнительный урон!");
						e.setDamage(e.getDamage()+4);
					}
				}
				if(poison.containsKey(damager.getName())) {
					int chance = poison.get(damager.getName());
					int rand = (int) (Math.random() * 100);
					if(rand <= chance) {
						damager.sendMessage("§7Вы отравили противника!");
						p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*4, 1));
					}
				}
			}
		}
	}
	
	public void connect() {
		getLogger().info("Connecting");
		
		try {
			con2 = DriverManager.getConnection(url2, user, password);;
		} catch (SQLException e) {}
		try {
			stmt2 = con2.createStatement();
		} catch (SQLException e) {}
	}
	
	public void onEnable() {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		this.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		connect();
		if (!setupEconomy()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
	    }
		file = new File(getDataFolder(), "config.yml");
		if(!file.exists()) {
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		reloadConfig();
        List<Entity> entList = Bukkit.getWorld("world").getEntities();
 
        for(Entity current : entList) {
            if (current instanceof Villager) {
            	current.remove();
            }
        }
		
        border = getConfig().getInt("border");
		bed_break_money = getConfig().getInt("bed_break_money");
		win_money = getConfig().getInt("win_money");
		final_kill_money = getConfig().getInt("final_kill_money");
		min_teams = getConfig().getInt("arena.min_teams");
		max_teams = getConfig().getInt("arena.max_teams");
		players_in_team = getConfig().getInt("arena.players_in_team");
		status = getConfig().getString("arena.status");
		arena = getConfig().getString("arena.name");
		spec = getConfig().getLocation("arena.spec_spawn");
		diamonds_gen = (List<Location>) getConfig().get("arena.diamonds_gen");
		emeralds_gen = (List<Location>) getConfig().get("arena.emeralds_gen");
		shops = (List<Location>) getConfig().get("arena.shops");
		upgrades = (List<Location>) getConfig().get("arena.upgrades");
		LOBBY_TIME = getConfig().getInt("lobby_time");
		GAME_TIME = getConfig().getInt("game_time");
		up_tier_time_2 = getConfig().getInt("up_tier_time.2");
		up_tier_time_3 = getConfig().getInt("up_tier_time.3");
		bed_gone_time = getConfig().getInt("bed_gone_time");
		
		WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
		wb.setCenter(spec.getBlockX(), spec.getBlockZ());
		wb.setSize(border);
		
		for(Location loc2 : diamonds_gen) {
			Location loc = new Location(loc2.getWorld(), loc2.getBlockX()+0.5, loc2.getBlockY()+0.5, loc2.getBlockZ()+0.5);
			ArmorStand as = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
			as.setVisible(false);
			as.setGravity(false);
	        as.setCanPickupItems(false);
	        as.setCustomNameVisible(true);
	        as.setCustomName(" ");
	        as.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
	        d_armorstands.add(as);
		}

		for(Location loc2 : emeralds_gen) {
			Location loc = new Location(loc2.getWorld(), loc2.getBlockX()+0.5, loc2.getBlockY()+0.5, loc2.getBlockZ()+0.5);
			ArmorStand as = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
			as.setVisible(false);
			as.setGravity(false);
	        as.setCanPickupItems(false);
	        as.setCustomNameVisible(true);
	        as.setCustomName(" ");
	        as.setHelmet(new ItemStack(Material.EMERALD_BLOCK));
	        e_armorstands.add(as);
		}
		
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		for(String ar : cs.getKeys(false)) {
			List<String> none = new ArrayList<String>();
			teams.put(ar, none);
		}
		
		cs = getConfig().getConfigurationSection("upgrades.generator");
		for(String ar : cs.getKeys(false)) {
			upgrade_generator.put(Integer.valueOf(ar), cs.getInt(ar));
		}
		cs = getConfig().getConfigurationSection("upgrades.sword");
		for(String ar : cs.getKeys(false)) {
			upgrade_sword.put(Integer.valueOf(ar), cs.getInt(ar));
		}
		cs = getConfig().getConfigurationSection("upgrades.armor");
		for(String ar : cs.getKeys(false)) {
			upgrade_armor.put(Integer.valueOf(ar), cs.getInt(ar));
		}
		cs = getConfig().getConfigurationSection("upgrades.speed");
		for(String ar : cs.getKeys(false)) {
			upgrade_speed.put(Integer.valueOf(ar), cs.getInt(ar));
		}
		
		createShop();
		createUpgrade();
		inv_blocks = Bukkit.createInventory(null, 9*3, "§cБлоки");
		inv_armor = Bukkit.createInventory(null, 9*3, "§cБроня");
		inv_swords = Bukkit.createInventory(null, 9*3, "§cОружие");
		inv_bows = Bukkit.createInventory(null, 9*3, "§cЛуки/Арбалеты");
		inv_tools = Bukkit.createInventory(null, 9*3, "§cИнструменты");
		inv_useful = Bukkit.createInventory(null, 9*3, "§cПолезное");
		inv_potions = Bukkit.createInventory(null, 9*3, "§cЗелья");
		
		back = new ItemStack(Material.SLIME_BALL);
		ItemMeta met2 = back.getItemMeta();
		met2.setDisplayName("§eНазад");
		back.setItemMeta(met2);
		
		inv_blocks.setItem(22, back);
		inv_armor.setItem(22, back);
		inv_swords.setItem(22, back);
		inv_bows.setItem(22, back);
		inv_tools.setItem(22, back);
		inv_useful.setItem(22, back);
		inv_potions.setItem(22, back);
		
		
		addToDB();
		item = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Перки");
		item.setItemMeta(meta);
		
		lobby = new ItemStack(Material.RED_BED);
		meta = lobby.getItemMeta();
		meta.setDisplayName("§cНазад в лобби");
		lobby.setItemMeta(meta);
		
		inv = Bukkit.createInventory(null, 9*5, "§cПерки");
		Pair p = new Pair("§7Добавление §c+2 ХП", 3);
		perks1.add(p);
		p = new Pair("§b+4 алмаза §7при старте", 5);
		perks1.add(p);
		p = new Pair("§6+12 золота §7при старте", 5);
		perks1.add(p);
		p = new Pair("§6+6 золота §7при старте", 10);
		perks1.add(p);
		p = new Pair("§f+16 железа §7при старте", 5);
		perks1.add(p);
		p = new Pair("§f+32 железа §7при старте", 10);
		perks1.add(p);
		p = new Pair("§c+50 медных §7монет", 5);
		perks1.add(p);
		p = new Pair("§c+25 медных §7монет", 10);
		perks1.add(p);
		p = new Pair("§7Шанс §35% §7поджечь противника", 5);
		perks1.add(p);
		p = new Pair("§7Шанс §35% §7заморозить противника", 5);
		perks1.add(p);
		p = new Pair("§7Шанс §35% §7отразить половину урона", 5);
		perks1.add(p);
		
		p = new Pair("§7Добавление §c+4 ХП", 5);
		perks2.add(p);
		p = new Pair("§b+8 алмазов §7при старте", 5);
		perks2.add(p);
		p = new Pair("§6+16 золота §7при старте", 5);
		perks2.add(p);
		p = new Pair("§6+8 золота §7при старте", 10);
		perks2.add(p);
		p = new Pair("§2+2 изумруда §7при старте", 3);
		perks2.add(p);
		p = new Pair("§c+500 медных §7монет", 5);
		perks2.add(p);
		p = new Pair("§c+250 медных §7монет", 10);
		perks2.add(p);
		p = new Pair("§7Шанс §310% §7поджечь противника", 5);
		perks2.add(p);
		p = new Pair("§7Шанс §310% §7заморозить противника", 5);
		perks2.add(p);
		p = new Pair("§7Шанс §310% §7отразить половину урона", 5);
		perks2.add(p);
		p = new Pair("§7Шанс §35% §7нанести доп. урон", 5);
		perks2.add(p);
		
		p = new Pair("§7Добавление §c+6 ХП", 10);
		perks3.add(p);
		p = new Pair("§b+16 алмазов §7при старте", 10);
		perks3.add(p);
		p = new Pair("§6+24 золота §7при старте", 10);
		perks3.add(p);
		p = new Pair("§2+4 изумруда §7при старте", 5);
		perks3.add(p);
		p = new Pair("§6+1 золотая §7монета", 10);
		perks3.add(p);
		p = new Pair("§6+3 золотых §7монеты", 5);
		perks3.add(p);
		p = new Pair("§7Шанс §320% §7поджечь противника", 10);
		perks3.add(p);
		p = new Pair("§7Шанс §320% §7заморозить противника", 10);
		perks3.add(p);
		p = new Pair("§7Шанс §320% §7отразить половину урона", 10);
		perks3.add(p);
		p = new Pair("§7Шанс §315% §7нанести доп. урон", 10);
		perks3.add(p);
		p = new Pair("§7Шанс §315% §7отравить противника", 10);
		perks3.add(p);
		
		p = new Pair("§7Добавление §c+6 ХП", 5);
		perks4.add(p);
		p = new Pair("§b+16 алмазов §7при старте", 7);
		perks4.add(p);
		p = new Pair("§6+24 золота §7при старте", 7);
		perks4.add(p);
		p = new Pair("§2+4 изумруда §7при старте", 7);
		perks4.add(p);
		p = new Pair("§7Шанс §320% §7поджечь противника", 5);
		perks4.add(p);
		p = new Pair("§7Шанс §320% §7заморозить противника", 5);
		perks4.add(p);
		p = new Pair("§7Шанс §320% §7отразить половину урона", 5);
		perks4.add(p);
		p = new Pair("§7Шанс §315% §7нанести доп. урон", 5);
		perks4.add(p);
		p = new Pair("§7Шанс §315% §7отравить противника", 5);
		perks4.add(p);
		p = new Pair("§7Бесконечный лук", 5);
		perks4.add(p);
		
		p = new Pair("§7Добавление §c+8 ХП", 10);
		perks5.add(p);
		p = new Pair("§b+24 алмаза §7при старте", 10);
		perks5.add(p);
		p = new Pair("§2+6 изумрудов §7при старте", 10);
		perks5.add(p);
		p = new Pair("§7Шанс §325% §7поджечь противника", 10);
		perks5.add(p);
		p = new Pair("§7Шанс §325% §7заморозить противника", 10);
		perks5.add(p);
		p = new Pair("§7Шанс §325% §7отразить половину урона", 10);
		perks5.add(p);
		p = new Pair("§7Шанс §320% §7нанести доп. урон", 10);
		perks5.add(p);
		p = new Pair("§7Шанс §320% §7отравить противника", 10);
		perks5.add(p);
		p = new Pair("§7Бесконечный лук", 10);
		perks5.add(p);
		p = new Pair("§7Регенерация после убийства", 5);
		perks5.add(p);
		p = new Pair("§7Мгновенное возрождение", 5);
		perks5.add(p);
		
		p1 = new ItemStack(Material.IRON_INGOT);
		ItemMeta met = p1.getItemMeta();
		met.setDisplayName("§7Цена: §c100 медных §7монет");
		List<String> lore = new ArrayList<String>();
		lore.add("§eДоступно всем");
		for(Pair pp : perks1) {
			String name = pp.name;
			int chance = pp.chance;
			lore.add("§7 - "+name+" §7(§c"+String.valueOf(chance)+"%§7)");
		}
		met.setLore(lore);
		p1.setItemMeta(met);
		inv.setItem(11, p1);
		
		p2 = new ItemStack(Material.GOLD_INGOT);
		met = p2.getItemMeta();
		met.setDisplayName("§7Цена: §61 золотая §7монета");
		lore = new ArrayList<String>();
		lore.add("§eДоступно всем");
		for(Pair pp : perks2) {
			String name = pp.name;
			int chance = pp.chance;
			lore.add("§7 - "+name+" §7(§c"+String.valueOf(chance)+"%§7)");
		}
		met.setLore(lore);
		p2.setItemMeta(met);
		inv.setItem(13, p2);
		
		p3 = new ItemStack(Material.DIAMOND);
		met = p3.getItemMeta();
		met.setDisplayName("§7Цена: §610 золотых §7монет");
		lore = new ArrayList<String>();
		lore.add("§eДоступно всем");
		for(Pair pp : perks3) {
			String name = pp.name;
			int chance = pp.chance;
			lore.add("§7 - "+name+" §7(§c"+String.valueOf(chance)+"%§7)");
		}
		met.setLore(lore);
		p3.setItemMeta(met);
		inv.setItem(15, p3);
		
		p4 = new ItemStack(Material.EMERALD);
		met = p4.getItemMeta();
		met.setDisplayName("§7Цена: §c500 медных §7монет");
		lore = new ArrayList<String>();
		lore.add("§eДосступно с §7[§bMVP§7] §eи выше");
		for(Pair pp : perks4) {
			String name = pp.name;
			int chance = pp.chance;
			lore.add("§7 - "+name+" §7(§c"+String.valueOf(chance)+"%§7)");
		}
		met.setLore(lore);
		p4.setItemMeta(met);
		inv.setItem(30, p4);
		
		p5 = new ItemStack(Material.FIREWORK_STAR);
		met = p5.getItemMeta();
		met.setDisplayName("§7Цена: §c300 медных §7монет");
		lore = new ArrayList<String>();
		lore.add("§eДосступно с §7[§9Premium§7] §eи выше");
		for(Pair pp : perks5) {
			String name = pp.name;
			int chance = pp.chance;
			lore.add("§7 - "+name+" §7(§c"+String.valueOf(chance)+"%§7)");
		}
		met.setLore(lore);
		p5.setItemMeta(met);
		inv.setItem(32, p5);
		if(status.equals("wait")) {
			addCages();
		}
		
		updateTeamInv();
		
		ConfigurationSection cs2 = getConfig().getConfigurationSection("arena.commands");
        for(String name : cs2.getKeys(false)) {
        	if(name.length() > max_length_team) {
        		max_length_team = name.length();
        	}
        }
	}
	public void onDisable() {
		updateDB("reload", count_players);
		try {
			if(con2 != null && !con2.isClosed()) {
				try {
					con2.close();
					stmt2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(Player p, String msg) {
		p.sendMessage(msg.replaceAll("&", "§"));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equals("bedwars")) {
				if(p.hasPermission("API.glav")) {
					if(args.length == 0) {
						send(p, "/bw create [name]");
						send(p, "/bw setminteams [count]");
						send(p, "/bw setmaxteams [count]");
						send(p, "/bw setplayersinteam [count]");
						send(p, "/bw setspawn [new command name]");
						send(p, "/bw setbedloc [command name]");
						send(p, "/bw setblock [command name]");
						send(p, "/bw setcolor [command name] [color]");
						send(p, "/bw addironspawn [command name]");
						send(p, "/bw addgoldspawn [command name]");
						send(p, "/bw setarmor [command name]");
						send(p, "/bw adddiamondsspawn");
						send(p, "/bw addemeraldsspawn");
						send(p, "/bw addshop");
						send(p, "/bw addupgrade");
						send(p, "/bw setspec");
						send(p, "/bw edit");
						send(p, "/bw save");
						send(p, "/bw additem [shop type] [price] [iron/gold/emerald]");
						send(p, "/bw finish");
						send(p, "/bw start");
					}
					else {
						if(args[0].equals("create")) {
							if(args.length == 2) {
								String name = args[1];
								getConfig().set("arena.name", name);
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setminteams")) {
							if(args.length == 2) {
								int count = Integer.valueOf(args[1]);
								getConfig().set("arena.min_teams", count);
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setmaxteams")) {
							if(args.length == 2) {
								int count = Integer.valueOf(args[1]);
								getConfig().set("arena.max_teams", count);
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("additem")) {
							if(args.length == 4) {
								String sh = args[1];
								int price = Integer.valueOf(args[2]);
								String type = args[3];
								String typ = "";
								if(type.equals("iron")) typ = "§fЖелеза";
								else if(type.equals("gold")) typ = "§6Золота";
								else if(type.equals("emerald")) typ = "§2Изумруд(-ов,-а)";
								
								ItemStack it = p.getItemInHand();
								ItemMeta meta = it.getItemMeta();
								List<String> lore = new ArrayList<String>();
								lore.add("§7Цена: §c"+args[2]+" "+typ);
								meta.setLore(lore);
								it.setItemMeta(meta);
								
								List<ItemStack> list = (List<ItemStack>) getConfig().get("shops."+sh);
								list.add(it);
								getConfig().set("shops."+sh, list);
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setplayersinteam")) {
							if(args.length == 2) {
								int count = Integer.valueOf(args[1]);
								getConfig().set("arena.players_in_team", count);
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setspawn")) {
							if(args.length == 2) {
								String name = args[1];
								getConfig().set("arena.commands."+name+".spawn", p.getLocation());
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setblock")) {
							if(args.length == 2) {
								String name = args[1];
								getConfig().set("arena.commands."+name+".block", p.getItemInHand());
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setarmor")) {
							if(args.length == 2) {
								String name = args[1];
								ItemStack helmet = p.getInventory().getHelmet();
								ItemStack chestplate = p.getInventory().getChestplate();
								ItemStack leggings = p.getInventory().getLeggings();
								ItemStack boots = p.getInventory().getBoots();
								getConfig().set("arena.commands."+name+".armor.helmet", helmet);
								getConfig().set("arena.commands."+name+".armor.chestplate", chestplate);
								getConfig().set("arena.commands."+name+".armor.leggings", leggings);
								getConfig().set("arena.commands."+name+".armor.boots", boots);
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setbedloc")) {
							if(args.length == 2) {
								String name = args[1];
								getConfig().set("arena.commands."+name+".bed", p.getTargetBlock(null, 6).getLocation());
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setcolor")) {
							if(args.length == 3) {
								String name = args[1];
								String c = args[2];
								getConfig().set("arena.commands."+name+".color", c);
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("setspec")) {
							getConfig().set("arena.spec_spawn", p.getLocation());
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("addironspawn")) {
							if(args.length == 2) {
								String name = args[1];
								try {
									List<Location> list = (List<Location>) getConfig().get("arena.commands."+name+".iron_gens");
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									getConfig().set("arena.commands."+name+".iron_gens", list);
								} catch(Exception ee) {
									List<Location> list = new ArrayList<Location>();
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									getConfig().set("arena.commands."+name+".iron_gens", list);
								}
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("addgoldspawn")) {
							if(args.length == 2) {
								String name = args[1];
								try {
									List<Location> list = (List<Location>) getConfig().get("arena.commands."+name+".gold_gens");
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									getConfig().set("arena.commands."+name+".gold_gens", list);
								} catch (Exception ee) {
									List<Location> list = new ArrayList<Location>();
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									getConfig().set("arena.commands."+name+".gold_gens", list);
								}
								saveConfig();
								reloadConfig();
							}
						}
						else if(args[0].equals("adddiamondsspawn")) {
							try {
								List<Location> list = (List<Location>) getConfig().get("arena.diamonds_gen");
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								getConfig().set("arena.diamonds_gen", list);
							} catch(Exception ee) {
								List<Location> list = new ArrayList<Location>();
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								getConfig().set("arena.diamonds_gen", list);
							}
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("addemeraldsspawn")) {
							try {
								List<Location> list = (List<Location>) getConfig().get("arena.emeralds_gen");
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								getConfig().set("arena.emeralds_gen", list);
							} catch(Exception ee) {
								List<Location> list = new ArrayList<Location>();
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								getConfig().set("arena.emeralds_gen", list);
							}
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("addshop")) {
							List<Location> list = (List<Location>) getConfig().get("arena.shops");
							list.add(p.getLocation());
							getConfig().set("arena.shops", list);
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("addupgrade")) {
							List<Location> list = (List<Location>) getConfig().get("arena.upgrades");
							list.add(p.getLocation());
							getConfig().set("arena.upgrades", list);
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("finish")) {
							toLobby(p);
							getConfig().set("arena.status", "wait");
							status = "wait";
							updateDB("wait", count_players);
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("save")) {
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("edit")) {
							getConfig().set("arena.status", "reload");
							status = "reload";
							updateDB("reload", count_players);
							saveConfig();
							reloadConfig();
						}
						else if(args[0].equals("start")) {
							startTimer();
						}
						else if(args[0].equals("faststart")) {
							LOBBY_TIME = 5;
							startTimer();
						}
						else if(args[0].equals("end")) {
							endGame();
						}
					}
				}
			}
		}
		return true;
	}
}
