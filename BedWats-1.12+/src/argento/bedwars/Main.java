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
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {
	public static Main instance;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	
	public void onEnable() {
		instance = this;
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		this.getServer().getPluginManager().registerEvents(new EventHandlers(), this);
		
		Bukkit.getServer().getWorld("world").setGameRuleValue("doDaylightCycle", "false");
		Bukkit.getServer().getWorld("world").setGameRuleValue("doWeatherCycle", "false");
		Bukkit.getServer().getWorld("world").setGameRuleValue("doMobSpawning", "false");
		Bukkit.getServer().getWorld("world").setGameRuleValue("doFireTick", "false");
		Bukkit.getServer().getWorld("world").setGameRuleValue("announceAdvancements", "false");
		Bukkit.getServer().getWorld("world").setStorm(false);
		
		BedWars.init();
		
        boolean bungee_enabled = BedWars.getConfig().getBoolean("BungeeCord.enabled");
        
        if(bungee_enabled) {
        	Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }
        
        List<Entity> entList = Bukkit.getWorld("world").getEntities();
 
        for(Entity current : entList) {
            if (current instanceof Villager) {
            	current.remove();
            }
        }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equals("stat")) {
				if(BedWars.isMysqlEnabled()) {
					if(!BedWars.getStatCd().containsKey(p.getName()) || BedWars.getStatCd().get(p.getName()) == null) {
						BedWars.getStatCd().put(p.getName(), (long)(System.currentTimeMillis()/1000));
					}
					if((long)(System.currentTimeMillis()/1000) - BedWars.getStatCd().get(p.getName()) >= BedWars.getConfig().getLong("command_stat_cooldown")) {
						if(args.length == 0) {
							BedWars.getDatabase().checkStat(p, p.getName());
							BedWars.getStatCd().replace(p.getName(), (long)(System.currentTimeMillis()/1000));
						}
						else {
							String check = args[0];
							BedWars.getDatabase().checkStat(p, check);
							BedWars.getStatCd().replace(p.getName(), (long)(System.currentTimeMillis()/1000));
						}
					}
					else {
						BedWars.send(p, BedWars.getLang().getString("Common.67"));
					}
				}
				else {
					BedWars.send(p, BedWars.getLang().getString("Common.65"));
				}
			}
			else if(cmd.getName().equals("bedwars")) {
				if(p.hasPermission("API.glav")) {
					if(args.length == 0) {
						BedWars.send(p, "/bw create [name]");
						BedWars.send(p, "/bw setminteams [count]");
						BedWars.send(p, "/bw setmaxteams [count]");
						BedWars.send(p, "/bw setplayersinteam [count]");
						BedWars.send(p, "/bw setspawn [new command name]");
						BedWars.send(p, "/bw setbedloc [command name]");
						BedWars.send(p, "/bw setblock [command name]");
						BedWars.send(p, "/bw setcolor [command name] [color]");
						BedWars.send(p, "/bw addironspawn [command name]");
						BedWars.send(p, "/bw addgoldspawn [command name]");
						BedWars.send(p, "/bw setarmor [command name]");
						BedWars.send(p, "/bw adddiamondsspawn");
						BedWars.send(p, "/bw addemeraldsspawn");
						BedWars.send(p, "/bw addshop");
						BedWars.send(p, "/bw addupgrade");
						BedWars.send(p, "/bw setspec");
						BedWars.send(p, "/bw edit");
						BedWars.send(p, "/bw save");
						BedWars.send(p, "/bw additem [shop type] [price] [iron/gold/emerald] (name)");
						BedWars.send(p, "/bw finish");
						BedWars.send(p, "/bw start");
						BedWars.send(p, "/bw faststart");
					}
					else {
						if(args[0].equals("create")) {
							if(args.length == 2) {
								String name = args[1];
								BedWars.getConfig().set("arena.name", name);
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setminteams")) {
							if(args.length == 2) {
								int count = Integer.valueOf(args[1]);
								BedWars.getConfig().set("arena.min_teams", count);
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setmaxteams")) {
							if(args.length == 2) {
								int count = Integer.valueOf(args[1]);
								BedWars.getConfig().set("arena.max_teams", count);
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("additem")) {
							if(args.length >= 4) {
								String sh = args[1];
								int price = Integer.valueOf(args[2]);
								String type = args[3];
								String typ = "";
								String name = "";
								if(args.length > 4) {
									for(int i = 4; i < args.length; ++i) {
										name += args[i]+" ";
									}
								}
								name = name.replaceAll("&", "§");
								if(type.equals("iron")) typ = "§f"+BedWars.getLang().getString("GUI.common.iron");
								else if(type.equals("gold")) typ = "§6"+BedWars.getLang().getString("GUI.common.gold");
								else if(type.equals("emeralds")) typ = "§2"+BedWars.getLang().getString("GUI.common.emeralds");
								
								ItemStack it = p.getItemInHand();
								ItemMeta meta = it.getItemMeta();
								List<String> lore = new ArrayList<String>();
								lore.add(BedWars.getLang().getString("GUI.upgrades.3").split(" ")[0]+" §c"+args[2]+" "+typ);
								meta.setLore(lore);
								if(args.length > 4) meta.setDisplayName(name);
								it.setItemMeta(meta);
								
								List<ItemStack> list = (List<ItemStack>) BedWars.getConfig().get("shops."+sh);
								list.add(it);
								BedWars.getConfig().set("shops."+sh, list);
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setplayersinteam")) {
							if(args.length == 2) {
								int count = Integer.valueOf(args[1]);
								BedWars.getConfig().set("arena.players_in_team", count);
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setspawn")) {
							if(args.length == 2) {
								String name = args[1];
								BedWars.getConfig().set("arena.commands."+name+".spawn", p.getLocation());
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setblock")) {
							if(args.length == 2) {
								String name = args[1];
								BedWars.getConfig().set("arena.commands."+name+".block", p.getItemInHand());
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setarmor")) {
							if(args.length == 2) {
								String name = args[1];
								ItemStack helmet = p.getInventory().getHelmet();
								ItemStack chestplate = p.getInventory().getChestplate();
								ItemStack leggings = p.getInventory().getLeggings();
								ItemStack boots = p.getInventory().getBoots();
								BedWars.getConfig().set("arena.commands."+name+".armor.helmet", helmet);
								BedWars.getConfig().set("arena.commands."+name+".armor.chestplate", chestplate);
								BedWars.getConfig().set("arena.commands."+name+".armor.leggings", leggings);
								BedWars.getConfig().set("arena.commands."+name+".armor.boots", boots);
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setbedloc")) {
							if(args.length == 2) {
								String name = args[1];
								BedWars.getConfig().set("arena.commands."+name+".bed", p.getTargetBlock(null, 6).getLocation());
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setcolor")) {
							if(args.length == 3) {
								String name = args[1];
								String c = args[2];
								BedWars.getConfig().set("arena.commands."+name+".color", c);
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("setspec")) {
							BedWars.getConfig().set("arena.spec_spawn", p.getLocation());
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							BedWars.getConfigFile().accept();
						}
						else if(args[0].equals("addironspawn")) {
							if(args.length == 2) {
								String name = args[1];
								try {
									List<Location> list = (List<Location>) BedWars.getConfig().get("arena.commands."+name+".iron_gens");
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									BedWars.getConfig().set("arena.commands."+name+".iron_gens", list);
								} catch(Exception ee) {
									List<Location> list = new ArrayList<Location>();
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									BedWars.getConfig().set("arena.commands."+name+".iron_gens", list);
								}
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("addgoldspawn")) {
							if(args.length == 2) {
								String name = args[1];
								try {
									List<Location> list = (List<Location>) BedWars.getConfig().get("arena.commands."+name+".gold_gens");
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									BedWars.getConfig().set("arena.commands."+name+".gold_gens", list);
								} catch (Exception ee) {
									List<Location> list = new ArrayList<Location>();
									Location ll = p.getTargetBlock(null, 6).getLocation();
									ll.setY(ll.getY()+1);
									list.add(ll);
									BedWars.getConfig().set("arena.commands."+name+".gold_gens", list);
								}
								BedWars.send(p, BedWars.getLang().getString("Common.64"));
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
								BedWars.getConfigFile().accept();
							}
						}
						else if(args[0].equals("adddiamondsspawn")) {
							try {
								List<Location> list = (List<Location>) BedWars.getConfig().get("arena.diamonds_gen");
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								BedWars.getConfig().set("arena.diamonds_gen", list);
							} catch(Exception ee) {
								List<Location> list = new ArrayList<Location>();
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								BedWars.getConfig().set("arena.diamonds_gen", list);
							}
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							BedWars.getConfigFile().accept();
						}
						else if(args[0].equals("addemeraldsspawn")) {
							try {
								List<Location> list = (List<Location>) BedWars.getConfig().get("arena.emeralds_gen");
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								BedWars.getConfig().set("arena.emeralds_gen", list);
							} catch(Exception ee) {
								List<Location> list = new ArrayList<Location>();
								Location ll = p.getTargetBlock(null, 6).getLocation();
								ll.setY(ll.getY()+1);
								list.add(ll);
								BedWars.getConfig().set("arena.emeralds_gen", list);
							}
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							BedWars.getConfigFile().accept();
						}
						else if(args[0].equals("addshop")) {
							List<Location> list = (List<Location>) BedWars.getConfig().get("arena.shops");
							list.add(p.getLocation());
							BedWars.getConfig().set("arena.shops", list);
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							BedWars.getConfigFile().accept();
						}
						else if(args[0].equals("addupgrade")) {
							List<Location> list = (List<Location>) BedWars.getConfig().get("arena.upgrades");
							list.add(p.getLocation());
							BedWars.getConfig().set("arena.upgrades", list);
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							BedWars.getConfigFile().accept();
						}
						else if(args[0].equals("finish")) {
							BedWars.toLobby(p);
							BedWars.getConfig().set("arena.status", "wait");
							BedWars.setStatus("wait");
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							BedWars.getConfigFile().accept();
						}
						else if(args[0].equals("save")) {
							BedWars.getConfigFile().accept();
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
						}
						else if(args[0].equals("edit")) {
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							BedWars.send(p, BedWars.getLang().getString("Common.64"));
							BedWars.setStatus("reload");
							BedWars.getConfigFile().accept();
						}
						else if(args[0].equals("start")) {
							BedWars.startTimer();
						}
						else if(args[0].equals("faststart")) {
							BedWars.setLobbyTime(5);
							BedWars.startTimer();
						}
						else if(args[0].equals("end")) {
							BedWars.endGame();
						}
					}
				}
			}
		}
		return true;
	}
}
