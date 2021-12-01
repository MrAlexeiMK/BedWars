package argento.bedwars;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import argento.bedwars.configs.Config;
import argento.bedwars.configs.Lang;
import net.milkbowl.vault.economy.Economy;

public class BedWars {
    private static Economy econ;
	private static boolean campfireEnabled = true;
    private static boolean bellEnabled = true;
    private static boolean tabEnabled = true;
    private static boolean bungeeEnabled = false;
    private static boolean mysqlEnabled = false;
    private static int border;
	private static int countPlayers = 0;
    private static HashSet<String> fast = new HashSet<String>();
	private static int maxLengthTeam = 0;
    private static Inventory shop, upgrade;
	private static Inventory inv;
	private static Inventory teamInv;
    private static int diamondStage = 1, emeraldStage = 1;
	private static ItemStack item, lobby;
	
    private static ItemStack p1, p2, p3, p4, p5;
    private static ItemStack back;
    private static boolean compass = false;
    private static boolean is_compass = false;
    private static boolean giveMoney = false;
    private static ItemStack shopBlocks, shopSwords, shopArmor, shopTools, shopBows, shopUseful, shopPotions;
    private static Inventory invBlocks, invSwords, invArmor, invTools, invBows, invUseful, invPotions;

    private static HashSet<String> bedAlive = new HashSet<String>();
    private static HashSet<String> invis = new HashSet<String>();
    private static HashMap<String, String> playerTeams = new HashMap<String, String>();
    private static HashMap<String, Integer> stage = new HashMap<String, Integer>();
    private static HashMap<String, Integer> teamSwords = new HashMap<String, Integer>();
    private static HashMap<String, Integer> teamArmor = new HashMap<String, Integer>();
    private static HashMap<String, Integer> teamSpeed = new HashMap<String, Integer>();
    private static HashMap<String, Boolean> teamTrap = new HashMap<String, Boolean>();
    private static HashMap<String, List<String>> teams = new HashMap<String, List<String>>();
    private static HashMap<String, Inventory> savedInv = new HashMap<String, Inventory>();
    private static HashMap<String, Inventory> savedUp = new HashMap<String, Inventory>();
    private static HashMap<String, ItemStack> playerHelmet = new HashMap<String, ItemStack>();
    private static HashMap<String, ItemStack> playerChestplate = new HashMap<String, ItemStack>();
    private static HashMap<String, ItemStack> playerLeggings = new HashMap<String, ItemStack>();
    private static HashMap<String, ItemStack> playerBoots = new HashMap<String, ItemStack>();
    private static HashMap<String, ItemStack> playerSword = new HashMap<String, ItemStack>();
    private static HashMap<Integer, Integer> upgradeGenerator = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> upgradeSword = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> upgradeArmor = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> upgradeSpeed = new HashMap<Integer, Integer>();
    private static HashMap<String, Long> statCd = new HashMap<String, Long>();
    private static HashSet<String> fireballCooldowns = new HashSet<String>();
    private static HashSet<String> activedTeams = new HashSet<String>();
    private static List<ArmorStand> dArmorstands = new ArrayList<ArmorStand>();
    private static List<ArmorStand> eArmorstands = new ArrayList<ArmorStand>();
    private static HashSet<Location> blocks = new HashSet<Location>();
    
    private static String status;
    private static Location spec;
    private static int minTeams, maxTeams, playersInTeam, bedBreakMoney, winMoney, finalKillMoney;
    private static List<Location> diamondsGen, emeraldsGen, shops, upgrades;
    private static int LOBBY_TIME, GAME_TIME;
    private static String nextEvent;
    private static int time = 0, taskID, up_tier_time_2, up_tier_time_3, bed_gone_time;
    private static boolean timer = false;

	private static Config config = null;
	private static Lang lang = null;
	private static Database db = null;
	
	public static void init() {
		config = new Config();
		lang = new Lang();

		initObjects();
		initDatabase();
		if(!initEconomy()) {
			getLogger().warning("Add Vault plugin to support money system!");
		}
		initGens();
		initBorder();
		initUpgrades();
		
		createShop();
		createUpgrade();
		updateTeamInv();
		addCages();
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					scoreboardUpdate(p);
				}
			}
		}, 0, 19*1);
	}
	
	public static void initObjects() {
		tabEnabled = getConfig().getBoolean("TAB_plugin_support");
        giveMoney = getConfig().getBoolean("give_money");
        campfireEnabled = getConfig().getBoolean("specific_blocks.campfire.enabled");
        bungeeEnabled = BedWars.getConfig().getBoolean("BungeeCord.enabled");
        bellEnabled = getConfig().getBoolean("specific_blocks.bell.enabled");
        border = getConfig().getInt("border");
		bedBreakMoney = getConfig().getInt("bed_break_money");
		winMoney = getConfig().getInt("win_money");
		finalKillMoney = getConfig().getInt("final_kill_money");
		minTeams = getConfig().getInt("arena.min_teams");
		maxTeams = getConfig().getInt("arena.max_teams");
		playersInTeam = getConfig().getInt("arena.players_in_team");
		status = getConfig().getString("arena.status");
		spec = (Location) getConfig().get("arena.spec_spawn");
		diamondsGen = (List<Location>) getConfig().get("arena.diamonds_gen");
		emeraldsGen = (List<Location>) getConfig().get("arena.emeralds_gen");
		shops = (List<Location>) getConfig().get("arena.shops");
		upgrades = (List<Location>) getConfig().get("arena.upgrades");
		LOBBY_TIME = getConfig().getInt("lobby_time");
		GAME_TIME = getConfig().getInt("game_time");
		up_tier_time_2 = getConfig().getInt("up_tier_time.2");
		up_tier_time_3 = getConfig().getInt("up_tier_time.3");
		bed_gone_time = getConfig().getInt("bed_gone_time");
		invBlocks = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.2"));
		invArmor = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.4"));
		invSwords = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.3"));
		invBows = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.6"));
		invTools = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.5"));
		invUseful = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.7"));
		invPotions = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.8"));
		
		back = new ItemStack(Material.SLIME_BALL);
		ItemMeta met2 = back.getItemMeta();
		met2.setDisplayName(getLang().getString("GUI.shop.9"));
		back.setItemMeta(met2);
		
		lobby = new ItemStack(Material.RED_BED);
		ItemMeta meta = lobby.getItemMeta();
		meta.setDisplayName(getLang().getString("hotbar.back"));
		lobby.setItemMeta(meta);
		
		invBlocks.setItem(22, back);
		invArmor.setItem(22, back);
		invSwords.setItem(22, back);
		invBows.setItem(22, back);
		invTools.setItem(22, back);
		invUseful.setItem(22, back);
		invPotions.setItem(22, back);
		
		try {
			ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
	        for(String name : cs.getKeys(false)) {
	        	if(name.length() > maxLengthTeam) {
	        		maxLengthTeam = name.length();
	        	}
	        }
		} catch (Exception ee) {};
	}
	
	public static void initBorder() {
		if(spec != null) {
			WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
			wb.setCenter(spec.getBlockX(), spec.getBlockZ());
			wb.setSize(border);
		}
	}
	
	public static void initUpgrades() {
		try {
			ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
			for(String ar : cs.getKeys(false)) {
				List<String> none = new ArrayList<String>();
				teams.put(ar, none);
			}
			
			cs = getConfig().getConfigurationSection("upgrades.generator");
			for(String ar : cs.getKeys(false)) {
				upgradeGenerator.put(Integer.valueOf(ar), cs.getInt(ar));
			}
			cs = getConfig().getConfigurationSection("upgrades.sword");
			for(String ar : cs.getKeys(false)) {
				upgradeSword.put(Integer.valueOf(ar), cs.getInt(ar));
			}
			cs = getConfig().getConfigurationSection("upgrades.armor");
			for(String ar : cs.getKeys(false)) {
				upgradeArmor.put(Integer.valueOf(ar), cs.getInt(ar));
			}
			cs = getConfig().getConfigurationSection("upgrades.speed");
			for(String ar : cs.getKeys(false)) {
				upgradeSpeed.put(Integer.valueOf(ar), cs.getInt(ar));
			}
		} catch (Exception ee) {};
	}
	
	public static void initGens() {
		if(diamondsGen != null) {
			for(Location loc2 : diamondsGen) {
				Location loc = new Location(loc2.getWorld(), loc2.getBlockX()+0.5, loc2.getBlockY()+0.5, loc2.getBlockZ()+0.5);
				ArmorStand as = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
				as.setVisible(false);
				as.setGravity(false);
		        as.setCanPickupItems(false);
		        as.setCustomNameVisible(true);
		        as.setCustomName(" ");
		        as.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
		        dArmorstands.add(as);
			}
		}
		
		if(emeraldsGen != null) {
			for(Location loc2 : emeraldsGen) {
				Location loc = new Location(loc2.getWorld(), loc2.getBlockX()+0.5, loc2.getBlockY()+0.5, loc2.getBlockZ()+0.5);
				ArmorStand as = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
				as.setVisible(false);
				as.setGravity(false);
		        as.setCanPickupItems(false);
		        as.setCustomNameVisible(true);
		        as.setCustomName(" ");
		        as.setHelmet(new ItemStack(Material.EMERALD_BLOCK));
		        eArmorstands.add(as);
			}
		}
	}

    public static boolean initEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Main.instance.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public static void initDatabase() {
		String url = getConfig().getString("MySQL.url");
        String user = getConfig().getString("MySQL.user");
        String password = getConfig().getString("MySQL.password");
        mysqlEnabled = getConfig().getBoolean("MySQL.enabled");
        
        if(mysqlEnabled) {
        	db = new Database(url, user, password);
        	db.connect();
        }
        else {
        	db = new Database();
        }
	}
	
	public static boolean isCampfireEnabled() {
		return campfireEnabled;
	}

	public static void setCampfireEnabled(boolean campfireEnabled) {
		BedWars.campfireEnabled = campfireEnabled;
	}

	public static boolean isBellEnabled() {
		return bellEnabled;
	}

	public static void setBellEnabled(boolean bellEnabled) {
		BedWars.bellEnabled = bellEnabled;
	}

	public static boolean isTabEnabled() {
		return tabEnabled;
	}

	public static void setTabEnabled(boolean tabEnabled) {
		BedWars.tabEnabled = tabEnabled;
	}

	public static boolean isBungeeEnabled() {
		return bungeeEnabled;
	}

	public static void setBungeeEnabled(boolean bungeeEnabled) {
		BedWars.bungeeEnabled = bungeeEnabled;
	}
	
	public static void setStatus(String status) {
		BedWars.status = status;
	}
	
	public static void setLobbyTime(int time) {
		BedWars.LOBBY_TIME = time;
	}
	
	public static void setGameTime(int time) {
		BedWars.GAME_TIME = time;
	}
	
	public static int getLobbyTime() {
		return LOBBY_TIME;
	}
	
	public static int getGameTime() {
		return GAME_TIME;
	}

	public static boolean isMysqlEnabled() {
		return mysqlEnabled;
	}

	public static void setMysqlEnabled(boolean mysqlEnabled) {
		BedWars.mysqlEnabled = mysqlEnabled;
	}

	public static int getBorder() {
		return border;
	}

	public static void setBorder(int border) {
		BedWars.border = border;
	}

	public static int getCountPlayers() {
		return countPlayers;
	}

	public static void setCountPlayers(int countPlayers) {
		BedWars.countPlayers = countPlayers;
	}

	public static HashSet<String> getFast() {
		return fast;
	}
	
	public static boolean isTimer() {
		return timer;
	}

	public static void setFast(HashSet<String> fast) {
		BedWars.fast = fast;
	}

	public static int getMaxLengthTeam() {
		return maxLengthTeam;
	}

	public static void setMaxLengthTeam(int maxLengthTeam) {
		BedWars.maxLengthTeam = maxLengthTeam;
	}

	public static Inventory getInv() {
		return inv;
	}

	public static void setInv(Inventory inv) {
		BedWars.inv = inv;
	}

	public static Inventory getTeamInv() {
		return teamInv;
	}

	public static void setTeamInv(Inventory teamInv) {
		BedWars.teamInv = teamInv;
	}

	public static int getDiamondStage() {
		return diamondStage;
	}

	public static void setDiamondStage(int diamondStage) {
		BedWars.diamondStage = diamondStage;
	}

	public static int getEmeraldStage() {
		return emeraldStage;
	}

	public static void setEmeraldStage(int emeraldStage) {
		BedWars.emeraldStage = emeraldStage;
	}

	public static ItemStack getBack() {
		return back;
	}

	public static void setBack(ItemStack back) {
		BedWars.back = back;
	}

	public static boolean isCompass() {
		return compass;
	}

	public static void setCompass(boolean compass) {
		BedWars.compass = compass;
	}

	public static boolean isGiveMoney() {
		return giveMoney;
	}

	public static void setGiveMoney(boolean giveMoney) {
		BedWars.giveMoney = giveMoney;
	}

	public static HashSet<String> getBedAlive() {
		return bedAlive;
	}

	public static void setBedAlive(HashSet<String> bedAlive) {
		BedWars.bedAlive = bedAlive;
	}

	public static HashMap<String, String> getPlayerTeam() {
		return playerTeams;
	}

	public static void setPlayerTeams(HashMap<String, String> playerTeams) {
		BedWars.playerTeams = playerTeams;
	}

	public static HashMap<String, Integer> getStage() {
		return stage;
	}

	public static void setStage(HashMap<String, Integer> stage) {
		BedWars.stage = stage;
	}

	public static HashMap<String, Integer> getTeamSwords() {
		return teamSwords;
	}

	public static void setTeamSwords(HashMap<String, Integer> teamSwords) {
		BedWars.teamSwords = teamSwords;
	}

	public static HashMap<String, Integer> getTeamArmor() {
		return teamArmor;
	}

	public static void setTeamArmor(HashMap<String, Integer> teamArmor) {
		BedWars.teamArmor = teamArmor;
	}

	public static HashMap<String, Integer> getTeamSpeed() {
		return teamSpeed;
	}

	public static void setTeamSpeed(HashMap<String, Integer> teamSpeed) {
		BedWars.teamSpeed = teamSpeed;
	}

	public static HashMap<String, Boolean> getTeamTrap() {
		return teamTrap;
	}

	public static void setTeamTrap(HashMap<String, Boolean> teamTrap) {
		BedWars.teamTrap = teamTrap;
	}

	public static HashMap<String, List<String>> getTeams() {
		return teams;
	}

	public static void setTeams(HashMap<String, List<String>> teams) {
		BedWars.teams = teams;
	}

	public static HashMap<String, Inventory> getSavedInv() {
		return savedInv;
	}

	public static void setSavedInv(HashMap<String, Inventory> savedInv) {
		BedWars.savedInv = savedInv;
	}

	public static HashMap<String, Inventory> getSavedUp() {
		return savedUp;
	}

	public static void setSavedUp(HashMap<String, Inventory> savedUp) {
		BedWars.savedUp = savedUp;
	}

	public static HashMap<String, ItemStack> getPlayerHelmet() {
		return playerHelmet;
	}

	public static void setPlayerHelmet(HashMap<String, ItemStack> playerHelmet) {
		BedWars.playerHelmet = playerHelmet;
	}

	public static HashMap<String, ItemStack> getPlayerChestplate() {
		return playerChestplate;
	}

	public static void setPlayerChestplate(HashMap<String, ItemStack> playerChestplate) {
		BedWars.playerChestplate = playerChestplate;
	}

	public static HashMap<String, ItemStack> getPlayerLeggings() {
		return playerLeggings;
	}

	public static void setPlayerLeggings(HashMap<String, ItemStack> playerLeggings) {
		BedWars.playerLeggings = playerLeggings;
	}

	public static HashMap<String, ItemStack> getPlayerBoots() {
		return playerBoots;
	}

	public static void setPlayerBoots(HashMap<String, ItemStack> playerBoots) {
		BedWars.playerBoots = playerBoots;
	}

	public static HashMap<String, ItemStack> getPlayerSword() {
		return playerSword;
	}

	public static void setPlayerSword(HashMap<String, ItemStack> playerSword) {
		BedWars.playerSword = playerSword;
	}

	public static HashMap<Integer, Integer> getUpgradeGenerator() {
		return upgradeGenerator;
	}

	public static void setUpgradeGenerator(HashMap<Integer, Integer> upgradeGenerator) {
		BedWars.upgradeGenerator = upgradeGenerator;
	}

	public static HashMap<Integer, Integer> getUpgradeSword() {
		return upgradeSword;
	}

	public static void setUpgradeSword(HashMap<Integer, Integer> upgradeSword) {
		BedWars.upgradeSword = upgradeSword;
	}

	public static HashMap<Integer, Integer> getUpgradeArmor() {
		return upgradeArmor;
	}

	public static void setUpgradeArmor(HashMap<Integer, Integer> upgradeArmor) {
		BedWars.upgradeArmor = upgradeArmor;
	}

	public static HashMap<Integer, Integer> getUpgradeSpeed() {
		return upgradeSpeed;
	}

	public static void setUpgradeSpeed(HashMap<Integer, Integer> upgradeSpeed) {
		BedWars.upgradeSpeed = upgradeSpeed;
	}

	public static HashMap<String, Long> getStatCd() {
		return statCd;
	}

	public static void setStatCd(HashMap<String, Long> statCd) {
		BedWars.statCd = statCd;
	}

	public static HashSet<String> getFireballCooldowns() {
		return fireballCooldowns;
	}

	public static void setFireballCooldowns(HashSet<String> fireballCooldowns) {
		BedWars.fireballCooldowns = fireballCooldowns;
	}

	public static HashSet<String> getActivedTeams() {
		return activedTeams;
	}

	public static void setActivedTeams(HashSet<String> activedTeams) {
		BedWars.activedTeams = activedTeams;
	}

	public static List<ArmorStand> getdArmorstands() {
		return dArmorstands;
	}

	public static void setdArmorstands(List<ArmorStand> dArmorstands) {
		BedWars.dArmorstands = dArmorstands;
	}

	public static List<ArmorStand> geteArmorstands() {
		return eArmorstands;
	}

	public static void seteArmorstands(List<ArmorStand> eArmorstands) {
		BedWars.eArmorstands = eArmorstands;
	}

	public static HashSet<Location> getBlocks() {
		return blocks;
	}

	public static void setBlocks(HashSet<Location> blocks) {
		BedWars.blocks = blocks;
	}

	public static Location getSpec() {
		return spec;
	}

	public static void setSpec(Location spec) {
		BedWars.spec = spec;
	}

	public static int getMinTeams() {
		return minTeams;
	}

	public static void setMinTeams(int minTeams) {
		BedWars.minTeams = minTeams;
	}

	public static int getMaxTeams() {
		return maxTeams;
	}

	public static void setMaxTeams(int maxTeams) {
		BedWars.maxTeams = maxTeams;
	}

	public static int getPlayersInTeam() {
		return playersInTeam;
	}

	public static void setPlayersInTeam(int playersInTeam) {
		BedWars.playersInTeam = playersInTeam;
	}

	public static int getBedBreakMoney() {
		return bedBreakMoney;
	}

	public static void setBedBreakMoney(int bedBreakMoney) {
		BedWars.bedBreakMoney = bedBreakMoney;
	}

	public static int getWinMoney() {
		return winMoney;
	}

	public static void setWinMoney(int winMoney) {
		BedWars.winMoney = winMoney;
	}

	public static int getFinalKillMoney() {
		return finalKillMoney;
	}

	public static void setFinalKillMoney(int finalKillMoney) {
		BedWars.finalKillMoney = finalKillMoney;
	}

	public static List<Location> getDiamondsGen() {
		return diamondsGen;
	}

	public static void setDiamondsGen(List<Location> diamondsGen) {
		BedWars.diamondsGen = diamondsGen;
	}

	public static List<Location> getEmeraldsGen() {
		return emeraldsGen;
	}

	public static void setEmeraldsGen(List<Location> emeraldsGen) {
		BedWars.emeraldsGen = emeraldsGen;
	}

	public static List<Location> getUpgrades() {
		return upgrades;
	}

	public static void setUpgrades(List<Location> upgrades) {
		BedWars.upgrades = upgrades;
	}

	public static String getNextEvent() {
		return nextEvent;
	}

	public static Inventory getShop() {
		return shop;
	}

	public static void setShop(Inventory shop) {
		BedWars.shop = shop;
	}

	public static ItemStack getShopBlocks() {
		return shopBlocks;
	}

	public static void setShopBlocks(ItemStack shopBlocks) {
		BedWars.shopBlocks = shopBlocks;
	}

	public static ItemStack getShopSwords() {
		return shopSwords;
	}

	public static void setShopSwords(ItemStack shopSwords) {
		BedWars.shopSwords = shopSwords;
	}

	public static ItemStack getShopArmor() {
		return shopArmor;
	}
	
	public static ItemStack getLobby() {
		return lobby;
	}
	
	public static ItemStack getItem() {
		return item;
	}

	public static void setShopArmor(ItemStack shopArmor) {
		BedWars.shopArmor = shopArmor;
	}

	public static ItemStack getShopTools() {
		return shopTools;
	}

	public static void setShopTools(ItemStack shopTools) {
		BedWars.shopTools = shopTools;
	}

	public static ItemStack getShopBows() {
		return shopBows;
	}

	public static void setShopBows(ItemStack shopBows) {
		BedWars.shopBows = shopBows;
	}

	public static ItemStack getShopUseful() {
		return shopUseful;
	}

	public static void setShopUseful(ItemStack shopUseful) {
		BedWars.shopUseful = shopUseful;
	}

	public static ItemStack getShopPotions() {
		return shopPotions;
	}

	public static void setShopPotions(ItemStack shopPotions) {
		BedWars.shopPotions = shopPotions;
	}

	public static Inventory getInvBlocks() {
		return invBlocks;
	}

	public static void setInvBlocks(Inventory invBlocks) {
		BedWars.invBlocks = invBlocks;
	}

	public static Inventory getInvSwords() {
		return invSwords;
	}

	public static void setInvSwords(Inventory invSwords) {
		BedWars.invSwords = invSwords;
	}

	public static Inventory getInvArmor() {
		return invArmor;
	}

	public static void setInvArmor(Inventory invArmor) {
		BedWars.invArmor = invArmor;
	}

	public static Inventory getInvTools() {
		return invTools;
	}

	public static void setInvTools(Inventory invTools) {
		BedWars.invTools = invTools;
	}

	public static Inventory getInvBows() {
		return invBows;
	}

	public static void setInvBows(Inventory invBows) {
		BedWars.invBows = invBows;
	}

	public static Inventory getInvUseful() {
		return invUseful;
	}

	public static void setInvUseful(Inventory invUseful) {
		BedWars.invUseful = invUseful;
	}

	public static Inventory getInvPotions() {
		return invPotions;
	}

	public static void setInvPotions(Inventory invPotions) {
		BedWars.invPotions = invPotions;
	}

	public static HashSet<String> getInvis() {
		return invis;
	}

	public static void setInvis(HashSet<String> invis) {
		BedWars.invis = invis;
	}

	public static void setNextEvent(String nextEvent) {
		BedWars.nextEvent = nextEvent;
	}

	public static Database getDatabase() {
		return db;
	}
	
	public static String getStatus() {
		return status;
	}
	
	public static Logger getLogger() {
		return Main.instance.getLogger();
	}
	
	public static Economy getEconomy() {
		return econ;
	}
	
	public static Config getConfigFile() {
		return config;
	}
	
	public static Lang getLangFile() {
		return lang;
	}
	
	public static FileConfiguration getConfig() {
		return config.getConfig();
	}
	
	public static FileConfiguration getLang() {
		return lang.getConfig();
	}
	
	public static void send(Player p, String msg) {
		p.sendMessage(msg.replaceAll("&", "§"));
	}
	
	public static void createUpgrade() {
		upgrade = Bukkit.createInventory(null, 9*5, getLang().getString("GUI.upgrades.1"));
		ItemStack n = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
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
		meta.setDisplayName(getLang().getString("GUI.upgrades.2"));
		List<String> lore = new ArrayList<String>();
		lore.add(getLang().getString("GUI.upgrades.3").replaceAll("%price%", String.valueOf(upgradeSword.get(2))));
		lore.add(getLang().getString("GUI.upgrades.4"));
		meta.setLore(lore);
		up_sword.setItemMeta(meta);

		ItemStack up_armor = new ItemStack(Material.LEATHER_CHESTPLATE);
		meta = up_armor.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.upgrades.5"));
		lore = new ArrayList<String>();
		lore.add(getLang().getString("GUI.upgrades.3").replaceAll("%price%", String.valueOf(upgradeArmor.get(2))));
		lore.add(getLang().getString("GUI.upgrades.4"));
		meta.setLore(lore);
		up_armor.setItemMeta(meta);

		ItemStack up_gen = new ItemStack(Material.FURNACE);
		meta = up_gen.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.upgrades.6"));
		lore = new ArrayList<String>();
		lore.add(getLang().getString("GUI.upgrades.3").replaceAll("%price%", String.valueOf(upgradeGenerator.get(2))));
		lore.add(getLang().getString("GUI.upgrades.4"));
		meta.setLore(lore);
		up_gen.setItemMeta(meta);

		ItemStack up_speed = new ItemStack(Material.GOLDEN_PICKAXE);
		meta = up_speed.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.upgrades.7"));
		lore = new ArrayList<String>();
		lore.add(getLang().getString("GUI.upgrades.3").replaceAll("%price%", String.valueOf(upgradeSpeed.get(2))));
		lore.add(getLang().getString("GUI.upgrades.4"));
		meta.setLore(lore);
		up_speed.setItemMeta(meta);

		ItemStack trap = new ItemStack(Material.TRIPWIRE_HOOK);
		meta = trap.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.upgrades.8"));
		lore = new ArrayList<String>();
		lore.add(getLang().getString("GUI.upgrades.3").replaceAll("%price%", String.valueOf(getConfig().getString("upgrades.trap"))));
		lore.add(getLang().getString("GUI.upgrades.9"));
		meta.setLore(lore);
		trap.setItemMeta(meta);
		
		upgrade.setItem(10, up_sword);
		upgrade.setItem(12, up_armor);
		upgrade.setItem(14, up_gen);
		upgrade.setItem(16, up_speed);
		upgrade.setItem(31, trap);
	}
	
	public static void createShop() {
		shop = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.1"));
		ItemStack n = new ItemStack(Material.BLACK_STAINED_GLASS);
		ItemMeta meta = n.getItemMeta();
		meta.setDisplayName(" ");
		n.setItemMeta(meta);
		for(int i = 0; i <= 8; ++i) {
			shop.setItem(i, n);
			shop.setItem(i+18, n);
		}
		shop.setItem(9, n);
		shop.setItem(17, n);
		
		shopBlocks = new ItemStack(Material.WHITE_WOOL);
		meta = shopBlocks.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.shop.2"));
		shopBlocks.setItemMeta(meta);
		
		shopSwords = new ItemStack(Material.IRON_SWORD);
		meta = shopSwords.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.shop.3"));
		shopSwords.setItemMeta(meta);
		
		shopArmor = new ItemStack(Material.IRON_BOOTS);
		meta = shopArmor.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.shop.4"));
		shopArmor.setItemMeta(meta);
		
		shopTools = new ItemStack(Material.STONE_PICKAXE);
		meta = shopTools.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.shop.5"));
		shopTools.setItemMeta(meta);
		
		shopBows = new ItemStack(Material.BOW);
		meta = shopBows.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.shop.6"));
		shopBows.setItemMeta(meta);
		
		shopUseful = new ItemStack(Material.TNT);
		meta = shopUseful.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.shop.7"));
		shopUseful.setItemMeta(meta);
		
		shopPotions = new ItemStack(Material.POTION);
		meta = shopPotions.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setDisplayName(getLang().getString("GUI.shop.8"));
		shopPotions.setItemMeta(meta);
		
		shop.setItem(10, shopBlocks);
		shop.setItem(11, shopSwords);
		shop.setItem(12, shopArmor);
		shop.setItem(13, shopTools);
		shop.setItem(14, shopBows);
		shop.setItem(15, shopUseful);
		shop.setItem(16, shopPotions);
	}
	
	public static void scoreboardUpdate(Player p) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getNewScoreboard();
        final Objective objective = board.registerNewObjective("score", "dummy");        
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§c§lBedWars");
        long money = 0;
        try {
        	money = (long) econ.getBalance(p.getName());
        } catch (Exception ee) {};
		int rr = maxTeams*playersInTeam;
		String team = playerTeams.get(p.getName());
        if(status.equals("wait")) {
    		String c = getConfig().getString("arena.commands."+team+".color");
        	Score score = objective.getScore(getLang().getString("scoreboard.1").replaceAll("%money%", String.valueOf(money)));
            score.setScore(6);
            Score score2 = objective.getScore(getLang().getString("scoreboard.2"));
            score2.setScore(5);
            Score score25 = objective.getScore(getLang().getString("scoreboard.3")+c+team);
            score25.setScore(4);
            Score score3 = objective.getScore(getLang().getString("scoreboard.4").replaceAll("%players%", String.valueOf(countPlayers)).replaceAll("%max_players%", String.valueOf(rr)));
            score3.setScore(3);
            Score score8 = objective.getScore(getLang().getString("scoreboard.5")+String.valueOf(LOBBY_TIME));
            score8.setScore(2);
            Score score4 = objective.getScore(getLang().getString("scoreboard.6"));
            score4.setScore(1);
            p.setScoreboard(board);
        }
        else if(status.equals("play")) {
        	ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
    		int count = cs.getKeys(false).size();
        	Score score = objective.getScore(getLang().getString("scoreboard.1").replaceAll("%money%", String.valueOf(money)));
            score.setScore(count+7);
            Score score2 = objective.getScore(getLang().getString("scoreboard.7"));
            score2.setScore(count+6);
            Score score3 = objective.getScore(getLang().getString("scoreboard.8"));
            score3.setScore(count+4);
            int i = 0;
            for(String name : cs.getKeys(false)) {
            	if(name.equals(team)) {
            		String bed = getLang().getString("scoreboard.9");
            		String add = getLang().getString("scoreboard.10");
	            	int w = teams.get(name).size();
	            	if(bedAlive.contains(name)) bed = getLang().getString("scoreboard.11");
	        		String c = getConfig().getString("arena.commands."+name+".color");
	        		String res = "§f·  "+bed+" §7(§f"+String.valueOf(w)+"§7) §"+c+name + " " + add;
	                Score score33 = objective.getScore(res);
	                score33.setScore(count+3-i);
            	}
            	else {
	            	String bed = getLang().getString("scoreboard.9");
	            	int w = teams.get(name).size();
	            	if(bedAlive.contains(name)) bed = getLang().getString("scoreboard.11");
	        		String c = getConfig().getString("arena.commands."+name+".color");
	        		String res = "§f·  "+bed+" §7(§f"+String.valueOf(w)+"§7) §"+c+name;
	                Score score33 = objective.getScore(res);
	                score33.setScore(count+3-i);
            	}
                ++i;
            }
            Score score44 = objective.getScore("        ");
            score44.setScore(3);
            Score score8 = objective.getScore(nextEvent+" §7(§c"+String.valueOf(time)+"§7)");
            score8.setScore(2);
            Score score4 = objective.getScore(getLang().getString("scoreboard.6"));
            score4.setScore(1);
            p.setScoreboard(board);
        }
        else {
        	Score score = objective.getScore(getLang().getString("scoreboard.1").replaceAll("%money%", String.valueOf(money)));
            score.setScore(3);
            Score score2 = objective.getScore(getLang().getString("scoreboard.12"));
            score2.setScore(2);
            Score score4 = objective.getScore(getLang().getString("scoreboard.6"));
            score4.setScore(1);
            p.setScoreboard(board);
        }
	}
	
    public static void toLobby(Player p ) {
    	if(bungeeEnabled) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
	        out.writeUTF("Connect");
	        out.writeUTF(getConfig().getString("BungeeCord.lobby"));
	        p.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
    	}
    	else {
    		p.chat(getConfig().getString("command_at_game_end"));
    	}
	}
    
    public static String getTeamWithBed(Location loc) {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		String team = "";
		for(String name : cs.getKeys(false)) {
			Location bed_lock = (Location) getConfig().get("arena.commands."+name+".bed");
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
	
	public static boolean hasItem(Inventory inv, Material mat, int amount) {
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
	
	public static void removeItem(Inventory inv, Material mat, int amount) {
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
	
	public static void updateTeamInv() {
		teamInv = Bukkit.createInventory(null, 9*3, getLang().getString("hotbar.team_select"));
		
		for(String t : teams.keySet()) {
			ItemStack helmet = getConfig().getItemStack("arena.commands."+t+".armor.helmet");
			String c = getConfig().getString("arena.commands."+t+".color");
			ItemMeta meta = helmet.getItemMeta();
			meta.setDisplayName("§"+c+t);
			helmet.setItemMeta(meta);
			
			teamInv.addItem(helmet);
		}
	}
	
	public static boolean isFull(Inventory inv) {
		for(ItemStack it : inv.getContents()) {
			if(it == null) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean hasBlock(String team, Material mat) {
		Location bl = (Location) getConfig().get("arena.commands."+team+".bed");
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
	
	public static String inBase(Player p) {
		Location loc = p.getLocation();
		String res = "no";
		for(String team : teams.keySet()) {
			if(!teams.get(team).isEmpty()) {
				Location bl = (Location) getConfig().get("arena.commands."+team+".bed");
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
	
	public static void deleteTrap(String team) {
		Inventory inv = savedUp.get(team);
		
		ItemStack trap = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta meta = trap.getItemMeta();
		meta.setDisplayName(getLang().getString("GUI.upgrades.8"));
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(getLang().getString("GUI.upgrades.3").replaceAll("%price%", String.valueOf(getConfig().getString("upgrades.trap"))));
		lore.add(getLang().getString("GUI.upgrades.9"));
		meta.setLore(lore);
		trap.setItemMeta(meta);
		
		inv.setItem(31, trap);
		savedUp.replace(team, inv);
		teamTrap.replace(team, false);
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
	
	public static void remCages() {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		for(String name : cs.getKeys(false)) {
			Location loc = (Location) getConfig().get("arena.commands."+name+".spawn");
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
	
	public static void addCages() {
		try {
			ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
			for(String name : cs.getKeys(false)) {
				Location loc = (Location) getConfig().get("arena.commands."+name+".spawn");
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
		} catch(Exception ee) {};
	}
	
	public static void toSpec(Player p) {
		p.teleport(spec);
		send(p, getLang().getString("Common.44"));
		p.setGameMode(GameMode.SPECTATOR);
		p.getInventory().setItem(8, lobby);
	}
	
	public static void addPlayerToTeam(Player p, String name) {
		if(playerTeams.containsKey(p.getName())) {
			playerTeams.replace(p.getName(), name);
		}
		else playerTeams.put(p.getName(), name);
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
	
	public static void addToTeam(Player p) {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		String command = null;
		for(String name : cs.getKeys(false)) {
			if(teams.isEmpty() || teams.get(name).size() < playersInTeam) {
				command = name;
				break;
			}
		}
		addPlayerToTeam(p, command);
		setColor(p, command);
		Location loc = (Location) getConfig().get("arena.commands."+command+".spawn");
		p.teleport(loc);
	}
	
	public static void tpToSpawn(Player p) {
		String command = playerTeams.get(p.getName());
		Location loc = (Location) getConfig().get("arena.commands."+command+".spawn");
		p.teleport(loc);
	}
	
	public static void sendAll(String msg) {
		for(Player pl : Bukkit.getOnlinePlayers()) {
			send(pl, msg);
		}
	}
	
	public static void removePlayerFromTeam(Player p) {
		if(playerTeams.containsKey(p.getName())) {
			String team = playerTeams.get(p.getName());
			playerTeams.remove(p.getName());
			List<String> list = new ArrayList<String>();
			for(String pl : teams.get(team)) {
				if(!pl.equals(p.getName())) {
					list.add(pl);
				}
			}
			teams.replace(team, list);
		}
	}
	
	public static void setColor(Player p, String team) {
		String c = getConfig().getString("arena.commands."+team+".color");
		String name = p.getDisplayName();
		String nn = "§"+c+p.getName();
		p.setDisplayName(nn+"§f");
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
			public void run() {
				if(tabEnabled) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtabname "+nn);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtagname "+nn);
				}
			}
		}, 20);
	}
	
	public static void clearColor(Player p) {
		String name = p.getDisplayName();
		String nn = "§f"+p.getName();
		p.setDisplayName(nn);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
			public void run() {
				if(tabEnabled) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtabname");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtagname");
				}
			}
		}, 20);
	}
	
	public static boolean isEnd() {
		ConfigurationSection cs = getConfig().getConfigurationSection("arena.commands");
		int alives = 0;
		for(String team : cs.getKeys(false)) {
			if(!teams.get(team).isEmpty()) alives++;
		}
		if(alives <= 1) return true;
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
	
	public static void stopTimer() {
		Bukkit.getScheduler().cancelTask(taskID);
		LOBBY_TIME = getConfig().getInt("lobby_time");
		timer = false;
	}
	
	public static void stopGameTimer() {
		Bukkit.getScheduler().cancelTask(taskID);
	}
	
	public static void endGame() {
		boolean is = false;
		stopGameTimer();
		status = "reload";
		if(!isEnd()) {
			is = true;
			sendAll(getLang().getString("Common.50"));
		}
		int size = 0;
		for(String team : teams.keySet()) {
			if(!teams.get(team).isEmpty()) {
				size++;
			}
		}
		if(size != 0) {
			int each_money = (int) (winMoney/size);
			for(String team : teams.keySet()) {
				if(!teams.get(team).isEmpty()) {
					for(String p_name : teams.get(team)) {
						Player pl = Bukkit.getPlayer(p_name);
						Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
							public void run() {
								spawnFireworks(pl.getLocation(), 3);
							}
						}, 0, 20);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p_name+" title {\"text\":\""+getLang().getString("Common.51")+"\", \"bold\":true, \"color\":\"gold\"}");
						if(is) send(pl, getLang().getString("Common.52"));
						else {
							db.addWin(pl);
						}
						if(giveMoney && each_money > 0) {
							try {
								econ.depositPlayer(pl, each_money);
								send(pl, getLang().getString("Common.40").replaceAll("%money%", String.valueOf(each_money)));
							} catch(Exception ee) {};
						}
					}
				}
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
			public void run() {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					toLobby(pl);
				}
			}
		}, 20*getConfig().getInt("game_end_to_lobby_players_seconds"));
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}
		}, 20*getConfig().getInt("game_end_restart_seconds"));
	}
	
	public static void startTimer() {
		timer = true;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		taskID = scheduler.scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			public void run() {
				if(LOBBY_TIME%5 == 0 || LOBBY_TIME <= 5) {
					sendAll(getLang().getString("Common.53").replaceAll("%time%", String.valueOf(LOBBY_TIME)));
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					}
				}
				LOBBY_TIME--;
				if(LOBBY_TIME <= 0) {
					status = "play";
					sendAll(getLang().getString("Common.54"));
					try {
						int i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.blocks")) {
							invBlocks.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.armor")) {
							invArmor.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.swords")) {
							invSwords.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.bows")) {
							invBows.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.tools")) {
							invTools.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.useful")) {
							invUseful.setItem(i, it);
							i++;
						}
						i = 0;
						for(ItemStack it : (List<ItemStack>) getConfig().get("shops.potions")) {
							invPotions.setItem(i, it);
							i++;
						}
					} catch (Exception e) {};
					startGame();
				}
			}
		}, 0, 20*1);
	}
	
	public static void spawnNPCS() {
		for(Location loc : shops) {
			Villager vil = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
			vil.setAI(false);
			vil.setCustomName(getLang().getString("GUI.shop.1"));
			vil.setCustomNameVisible(true);
		}
		for(Location loc : upgrades) {
			Villager vil = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
			vil.setAI(false);
			vil.setCustomName(getLang().getString("GUI.upgrades.1"));
			vil.setCustomNameVisible(true);
		}
	}
	
	public static void returnItems(Player p) {
		ItemStack helmet = playerHelmet.get(p.getName());
		ItemStack chestplate = playerChestplate.get(p.getName());
		ItemStack leggings = playerLeggings.get(p.getName());
		ItemStack boots = playerBoots.get(p.getName());
		ItemStack sword = playerSword.get(p.getName());
		p.getInventory().setItem(0, sword);
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(chestplate);
		p.getInventory().setLeggings(leggings);
		p.getInventory().setBoots(boots);
		p.updateInventory();
	}
	
	public static void startGame() {
		stopTimer();
		remCages();
		spawnNPCS();
		nextEvent = "§7§eI §7⟶ §eII§7";
		time = GAME_TIME-up_tier_time_2;
		for(String team : teams.keySet()) {
			stage.put(team, 1);
			if(!teams.get(team).isEmpty()) {
				activedTeams.add(team);
				ItemStack now = getConfig().getItemStack("arena.commands."+team+".block");
				try {
					Inventory n = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.2"));
					ItemStack ttt = invBlocks.getItem(0);
					ItemMeta meta = now.getItemMeta();
					ItemMeta meta2 = ttt.getItemMeta();
					meta.setDisplayName(getLang().getString("Common.69"));
					meta.setLore(meta2.getLore());
					now.setItemMeta(meta);
					now.setAmount(ttt.getAmount());
					n.setContents(invBlocks.getContents());
					n.setItem(0, now);
					savedInv.put(team, n);
				} catch(Exception ee) {
					Inventory n = Bukkit.createInventory(null, 9*3, getLang().getString("GUI.shop.2"));
					savedInv.put(team, n);
				};

				Inventory n2 = Bukkit.createInventory(null, 9*5, getLang().getString("GUI.upgrades.1"));
				n2.setContents(upgrade.getContents());
				savedUp.put(team, n2);
				
				teamSwords.put(team, 1);
				teamArmor.put(team, 1);
				teamSpeed.put(team, 1);
				teamTrap.put(team, false);
				
				bedAlive.add(team);
			}
		}
		for(String p_name : playerTeams.keySet()) {
			Player pl = Bukkit.getPlayer(p_name);
			pl.closeInventory();
			db.addGame(pl);
			String team = playerTeams.get(p_name);
			ItemStack helmet = getConfig().getItemStack("arena.commands."+team+".armor.helmet");
			ItemStack chestplate = getConfig().getItemStack("arena.commands."+team+".armor.chestplate");
			ItemStack leggings = getConfig().getItemStack("arena.commands."+team+".armor.leggings");
			ItemStack boots = getConfig().getItemStack("arena.commands."+team+".armor.boots");
			ItemStack itt = new ItemStack(Material.WOODEN_SWORD);
			playerHelmet.put(p_name, helmet);
			playerChestplate.put(p_name, chestplate);
			playerLeggings.put(p_name, leggings);
			playerBoots.put(p_name, boots);
			playerSword.put(p_name, itt);
			ItemMeta meta = itt.getItemMeta();
			meta.setUnbreakable(true);
			itt.setItemMeta(meta);
			pl.getInventory().setItem(0, itt);
			returnItems(Bukkit.getPlayer(p_name));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p_name+" title {\"text\":\""+getLang().getString("Common.55")+"\", \"bold\":true, \"color\":\"green\"}");
		}
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			public void run() {
				if(status.equals("play")) {
					for(String p_name : playerTeams.keySet()) {
						Player p = Bukkit.getPlayer(p_name);
						String base = inBase(p);
						String team = playerTeams.get(p_name);
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
								send(p, getLang().getString("Common.56"));
							}
						}
						else {
							if(invis.contains(p_name)) {
								invis.remove(p_name);
								ItemStack helmet = playerHelmet.get(p_name);
								ItemStack chestpate = playerChestplate.get(p_name);
								ItemStack leggings = playerLeggings.get(p_name);
								ItemStack boots = playerBoots.get(p_name);
								
								p.getInventory().setHelmet(helmet);
								p.getInventory().setChestplate(chestpate);
								p.getInventory().setLeggings(leggings);
								p.getInventory().setBoots(boots);
							}
						}
						if(!base.equals("no") && !base.equals(team) && !teams.get(base).isEmpty() && bedAlive.contains(base)) {
							if(hasBlock(base, Material.getMaterial(getConfig().getString("specific_blocks.bell.material")))) {
								for(String pp : teams.get(base)) {
									Player ppp = Bukkit.getPlayer(pp);
									send(ppp, getLang().getString("Common.57"));
									ppp.playSound(ppp.getLocation(), Sound.valueOf(getConfig().getString("specific_blocks.bell.sound")), 1, 1);
								}
							}
							if(teamTrap.get(base)) {
								deleteTrap(base);
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*10, 1));
								send(p, getLang().getString("Common.58"));
							}
							if(hasBlock(base, Material.getMaterial(getConfig().getString("specific_blocks.campfire.material")))) {
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
								if(!playerHelmet.get(p_name).isSimilar(p.getInventory().getHelmet()) && !invis.contains(p_name) && status.equals("play")) {
									ItemStack helmet = playerHelmet.get(p_name);
									ItemStack chestpate = playerChestplate.get(p_name);
									ItemStack leggings = playerLeggings.get(p_name);
									ItemStack boots = playerBoots.get(p_name);
									
									p.getInventory().setHelmet(helmet);
									p.getInventory().setChestplate(chestpate);
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
								}
								int raw = 0;
								for(ItemStack sk : p.getInventory().getContents()) {
									if(sk!= null && sk.getType().toString().contains("SWORD")) {
										if(!sk.getEnchantments().equals(playerSword.get(p_name).getEnchantments())) {
											sk.addEnchantments(playerSword.get(p_name).getEnchantments());
											p.getInventory().setItem(raw, sk);
										}
									}
									raw++;
								}
							}
						}
						else {
							if(!playerHelmet.get(p_name).isSimilar(p.getInventory().getHelmet()) && !invis.contains(p_name)  && status.equals("play")) {
								ItemStack helmet = playerHelmet.get(p_name);
								ItemStack chestpate = playerChestplate.get(p_name);
								ItemStack leggings = playerLeggings.get(p_name);
								ItemStack boots = playerBoots.get(p_name);
								
								p.getInventory().setHelmet(helmet);
								p.getInventory().setChestplate(chestpate);
								p.getInventory().setLeggings(leggings);
								p.getInventory().setBoots(boots);
							}
							int raw = 0;
							for(ItemStack sk : p.getInventory().getContents()) {
								if(sk!= null && sk.getType().toString().contains("SWORD")) {
									if(!sk.getEnchantments().equals(playerSword.get(p_name).getEnchantments())) {
										sk.addEnchantments(playerSword.get(p_name).getEnchantments());
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
		taskID = scheduler.scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			public void run() {
				int d_time = getConfig().getInt("diamond_tier."+String.valueOf(diamondStage)+".time");
				int d_amount = getConfig().getInt("diamond_tier."+String.valueOf(diamondStage)+".amount");
				int e_time = getConfig().getInt("emerald_tier."+String.valueOf(emeraldStage)+".time");
				int e_amount = getConfig().getInt("emerald_tier."+String.valueOf(emeraldStage)+".amount");
				
				try {
					if(GAME_TIME%d_time == 0) {
						for(Location loc : diamondsGen) {
							loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND, d_amount));
						}
					}
					if(GAME_TIME%e_time == 0) {
						for(Location loc : emeraldsGen) {
							loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.EMERALD, e_amount));
						}
					}
				} catch(Exception ee) {};
				
				for(ArmorStand as : dArmorstands) {
					as.setCustomName("§b"+GAME_TIME%d_time);
				}
				
				for(ArmorStand as : eArmorstands) {
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
					sendAll(getLang().getString("Common.60"));
					nextEvent = "§7§eII §7⟶ §eIII§7";
					time = GAME_TIME-up_tier_time_3;
					diamondStage = 2;
					emeraldStage = 2;
				}
				else if(GAME_TIME == up_tier_time_3) {
					sendAll(getLang().getString("Common.61"));
					nextEvent = getLang().getString("scoreboard.13");
					time = GAME_TIME-bed_gone_time;
					diamondStage = 3;
					emeraldStage = 3;
				}
				else if(GAME_TIME == bed_gone_time) {
					sendAll(getLang().getString("Common.59"));
					compass = true;
					nextEvent = getLang().getString("Common.62");
					time = GAME_TIME;
					bedAlive.clear();
				}
				if(GAME_TIME == 100 || GAME_TIME == 50 || GAME_TIME == 20 || GAME_TIME <= 10) {
					sendAll(getLang().getString("Common.63").replaceAll("%time%", String.valueOf(GAME_TIME)));
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					}
				}
				time--;
				GAME_TIME--;
				if(GAME_TIME <= 0) {
					endGame();
				}
			}
		}, 0, 20*1);
		
		for(String p_name : playerTeams.keySet()) {
			Player p = Bukkit.getPlayer(p_name);
			p.getInventory().remove(item);
			p.getInventory().remove(lobby);
			p.updateInventory();
		}
	}
	

	public boolean isFinished(Player p) {
		if(!getConfig().contains("arena.name")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw create [name]");
			return false;
		}
		if(!getConfig().contains("arena.max_teams")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw setmaxteams [count]");
			return false;
		}
		if(!getConfig().contains("arena.min_teams")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw setminteams [count]");
			return false;
		}
		if(!getConfig().contains("arena.players_in_team")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw setplayersinteam [count]");
			return false;
		}
		if(!getConfig().contains("arena.shops")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw addshop");
			return false;
		}
		if(!getConfig().contains("arena.upgrades")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw addupgrade");
			return false;
		}
		if(!getConfig().contains("arena.commands")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw setspawn [command name]");
			send(p, "/bw setbedloc [command]");
			send(p, "/bw setblock [command]");
			send(p, "/bw addironspawn [command]");
			send(p, "/bw setarmor [command]");
			return false;
		}
		if(!getConfig().contains("arena.spec_spawn")) {
			send(p, getLang().getString("Common.68"));
			send(p, "/bw setspec");
		}
		return true;
	}
}
