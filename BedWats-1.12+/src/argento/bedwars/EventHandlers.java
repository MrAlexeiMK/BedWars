package argento.bedwars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class EventHandlers implements Listener {
	@EventHandler
	public void breakBlocks(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(BedWars.getStatus().equals("wait")) {
			e.setCancelled(true);
		}
		else if(BedWars.getStatus().equals("play")) {
			Location loc = e.getBlock().getLocation();
			String name = BedWars.getPlayerTeam().get(p.getName());
			if(e.getBlock().getType().toString().contains("BED") && e.getBlock().getType() != Material.BEDROCK) {
				String team = BedWars.getTeamWithBed(loc);
				if(!name.equals(team)) {
					if(BedWars.getBedAlive().contains(team)) {
						String c = BedWars.getConfig().getString("arena.commands."+team+".color");
						BedWars.sendAll(p.getDisplayName()+BedWars.getLang().getString("Common.5")+c+team);
						BedWars.getDatabase().addBed(p);
						if(BedWars.getConfig().getBoolean("sounds.bed_break.enabled")) {
							for(Player ppp : Bukkit.getOnlinePlayers()) {
								ppp.playSound(ppp.getLocation(), Sound.valueOf(BedWars.getConfig().getString("sounds.bed_break.sound")), 1, 1);
							}
						}
						if(BedWars.isGiveMoney() && BedWars.getBedBreakMoney() > 0) {
							try {
								BedWars.getEconomy().depositPlayer(p, BedWars.getBedBreakMoney());
								BedWars.send(p, BedWars.getLang().getString("Common.6").replaceAll("%money%", String.valueOf(BedWars.getBedBreakMoney())));
							} catch(Exception ee) {};
						}
						BedWars.getBedAlive().remove(team);
						e.setDropItems(false);
						for(String p_name : BedWars.getTeams().get(team)) {
							Player pl = Bukkit.getPlayer(p_name);
							if(BedWars.getConfig().getBoolean("slow_and_blind_after_bed_break")) {
								pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*3, 1));
								pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*3, 1));
							}
							BedWars.send(pl, BedWars.getLang().getString("Common.7"));
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p_name+" title {\"text\":\""+BedWars.getLang().getString("Common.8")+"\", \"bold\":true, \"color\":\"red\"}");
						}
					}
					else e.setCancelled(true);
				}
				else e.setCancelled(true);
			}
			else if(!BedWars.getBlocks().contains(loc)) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void place(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(BedWars.getStatus().equals("wait")) {
			e.setCancelled(true);
		}
		else if(BedWars.getStatus().equals("play")) {
			if(e.getBlock().getLocation().getBlockY() > BedWars.getConfig().getInt("height_limit")) {
				e.setCancelled(true);
				return;
			}
			if(e.getBlock().getType() == Material.TNT) {
				Entity tnt = p.getWorld().spawn(e.getBlockPlaced().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
                tnt = (TNTPrimed) tnt;
                tnt.setCustomName(p.getName());
                tnt.setCustomNameVisible(false);
				Bukkit.getServer().getWorld(p.getWorld().getName()).getBlockAt(e.getBlockPlaced().getLocation()).setType(Material.AIR);
			}
			else {
				String team = BedWars.getPlayerTeam().get(p.getName());
				Location bl = (Location) BedWars.getConfig().get("arena.commands."+team+".bed");
				if((e.getBlock().getType() == Material.getMaterial(BedWars.getConfig().getString("specific_blocks.bell.material")) && BedWars.isBellEnabled()) || (e.getBlock().getType() == Material.getMaterial(BedWars.getConfig().getString("specific_blocks.campfire.material"))) && BedWars.isCampfireEnabled()) {
					if(BedWars.getBedAlive().contains(team)) {
						Location loc = e.getBlockPlaced().getLocation();
						int x1 = bl.getBlockX(), z1 = bl.getBlockZ();
						int x2 = loc.getBlockX(), z2 = loc.getBlockZ();
						int dis = BedWars.getConfig().getInt("specific_blocks.distance_to_bed_need");
						if(Math.abs(x1-x2) > dis || Math.abs(z1-z2) > dis) {
							BedWars.send(p, BedWars.getLang().getString("Common.1"));
							e.setCancelled(true);
						}
						else {
							BedWars.getBlocks().add(e.getBlock().getLocation());
							BedWars.send(p, BedWars.getLang().getString("Common.2"));
							for(String p_name : BedWars.getTeams().get(team)) {
								Player pl = Bukkit.getPlayer(p_name);
								BedWars.send(pl, p.getDisplayName()+BedWars.getLang().getString("Common.3"));
							}
						}
					}
					else {
						BedWars.send(p, BedWars.getLang().getString("Common.4"));
						e.setCancelled(true);
					}
				}
				else BedWars.getBlocks().add(e.getBlock().getLocation());
			}
		}
	}
	
	@EventHandler
	public void projHit(ProjectileHitEvent e) {
		if(!BedWars.getStatus().equals("play")) {
			if(e.getEntity() instanceof Arrow) {
				Location loc = e.getEntity().getLocation();
				loc.getWorld().createExplosion(loc, 15, false);
			}
		}
	}
	
	@EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
		if(BedWars.getStatus().equals("play")) {
	        List<Block> destroyed = e.blockList();
	        Iterator<Block> it = destroyed.iterator();
	        while (it.hasNext()) {
	            Block block = it.next();
	            if(BedWars.getConfig().getBoolean("glass_unexplosion") && block.getType() == Material.GLASS) it.remove();
	            else if (!BedWars.getBlocks().contains(block.getLocation())) {
	            	it.remove();
	            }
	        }
		}
		else {
			e.setYield((float) 0.0);
		}
    }
	
	@EventHandler
	public void deathpp(PlayerDeathEvent e) {
		e.setDeathMessage(null);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(BedWars.getStatus().equals("wait")) {
			e.setCancelled(true);
		}
		String team = BedWars.getPlayerTeam().get(p.getName());
		if (!BedWars.getStatus().equals("reload") && e.getSlotType().equals(SlotType.ARMOR) && !e.getCurrentItem().getType().equals(Material.AIR)) {
            e.setCancelled(true);
		}
		Inventory in = e.getInventory();
		if(e.getInventory().equals(BedWars.getTeamInv())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.getItemMeta() != null) {
					ItemMeta meta = it.getItemMeta();
					String t = meta.getDisplayName().substring(2);
					if(BedWars.getTeams().containsKey(t)) {
						if(!team.equals(t)) {
							if(BedWars.getTeams().get(t).size() < BedWars.getPlayersInTeam()) {
								BedWars.removePlayerFromTeam(p);
								BedWars.addPlayerToTeam(p, t);
								BedWars.setColor(p, t);
								Location loc = (Location) BedWars.getConfig().get("arena.commands."+t+".spawn");
								String color = BedWars.getConfig().getString("arena.commands."+t+".color");
								p.teleport(loc);
								BedWars.send(p, BedWars.getLang().getString("Common.9")+color+t);
								p.closeInventory();
								
								ItemStack now = BedWars.getConfig().getItemStack("arena.commands."+t+".block");
								meta = now.getItemMeta();
								meta.setDisplayName(BedWars.getLang().getString("hotbar.team_select"));
								now.setItemMeta(meta);
								p.getInventory().setItem(0, now);
								int min_players = BedWars.getPlayersInTeam()*BedWars.getMinTeams();
								if(!BedWars.isTimer() && BedWars.getCountPlayers() >= min_players && !BedWars.isEnd()) {
									BedWars.startTimer();
								}
								if(BedWars.isTimer() && (BedWars.getCountPlayers() < min_players || BedWars.isEnd())) {
									BedWars.stopTimer();
								}
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.10"));
						}
					}
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(BedWars.getSavedUp().get(team))) {
			ItemStack it = e.getCurrentItem();
			if(it != null && e.getRawSlot() <= 44) {
				ItemMeta meta = it.getItemMeta();
				if(it.getType() == Material.FURNACE) {
					if(BedWars.getStage().get(team) >= 5) {
						BedWars.send(p, BedWars.getLang().getString("Common.15"));
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(BedWars.hasItem(p.getInventory(), Material.DIAMOND, price)) {
							BedWars.removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							BedWars.getStage().replace(team, BedWars.getStage().get(team)+1);
							
							Inventory inv = BedWars.getSavedUp().get(team);
							List<String> lore2 = new ArrayList<String>();
							if(BedWars.getStage().get(team) < 5) { //    String.valueOf(stage.get(team))
								lore2.add(BedWars.getLang().getString("GUI.upgrades.3").replaceAll("%price%", 
										String.valueOf(BedWars.getUpgradeGenerator().get(BedWars.getStage().get(team)+1))));
								lore2.add(BedWars.getLang().getString("GUI.upgrades.10").replaceAll("%before%", 
										String.valueOf(BedWars.getStage().get(team))).replaceAll("%after%", String.valueOf(BedWars.getStage().get(team)+1)));
							}
							else lore2.add(BedWars.getLang().getString("GUI.upgrades.11"));
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							BedWars.getSavedUp().replace(team, inv);
							
							for(String p_name : BedWars.getTeams().get(team)) {
								BedWars.send(Bukkit.getPlayer(p_name), p.getDisplayName()+" "+BedWars.getLang().getString("Common.16")+" &7[&c"+String.valueOf(BedWars.getStage().get(team)-1)+"&e ⟶ &c"+String.valueOf(BedWars.getStage().get(team))+"&7]");
							}
						}
						else BedWars.send(p, BedWars.getLang().getString("Common.11"));
					}
				}
				else if(it.getType() == Material.IRON_SWORD) {
					if(BedWars.getTeamSwords().get(team) >= 4) {
						BedWars.send(p, BedWars.getLang().getString("Common.12"));
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(BedWars.hasItem(p.getInventory(), Material.DIAMOND, price)) {
							BedWars.removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							BedWars.getTeamSwords().replace(team, BedWars.getTeamSwords().get(team)+1);
							
							Inventory inv = BedWars.getSavedUp().get(team);
							List<String> lore2 = new ArrayList<String>();
							if(BedWars.getTeamSwords().get(team) < 4) {
								lore2.add(BedWars.getLang().getString("GUI.upgrades.3").replaceAll("%price%", String.valueOf(BedWars.getUpgradeSword().get(BedWars.getTeamSwords().get(team)+1))));
								lore2.add(BedWars.getLang().getString("GUI.upgrades.10").replaceAll("%before%", String.valueOf(BedWars.getTeamSwords().get(team))).replaceAll("%after%", String.valueOf(BedWars.getTeamSwords().get(team)+1)));
							}
							else lore2.add(BedWars.getLang().getString("GUI.upgrades.11"));
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							BedWars.getSavedUp().replace(team, inv);
							
							for(String p_name : BedWars.getTeams().get(team)) {
								Player pl = Bukkit.getPlayer(p_name);
								ItemStack sw = BedWars.getPlayerSword().get(pl.getName());
								sw.addEnchantment(Enchantment.DAMAGE_ALL, BedWars.getTeamSwords().get(team)-1);
								BedWars.getPlayerSword().replace(pl.getName(), sw);
								Inventory pinv = p.getInventory();
								int raw = 0;
								for(ItemStack sk : pinv.getContents()) {
									if(sk == null) continue;
									if(sk.getType().toString().contains("SWORD")) {
										sk.addEnchantment(Enchantment.DAMAGE_ALL, BedWars.getTeamSwords().get(team)-1);
										p.getInventory().setItem(raw, sk);
									}
									raw++;
								}
								BedWars.send(Bukkit.getPlayer(p_name), p.getDisplayName()+" "+BedWars.getLang().getString("Common.11")+" &7[&c"+String.valueOf(BedWars.getTeamSwords().get(team)-1)+"&e ⟶ &c"+String.valueOf(BedWars.getTeamSwords().get(team))+"&7]");
							}
						}
						else BedWars.send(p, BedWars.getLang().getString("Common.11"));
					}
				}
				else if(it.getType() == Material.LEATHER_CHESTPLATE) {
					if(BedWars.getTeamArmor().get(team) >= 5) {
						BedWars.send(p, BedWars.getLang().getString("Common.13"));
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(BedWars.hasItem(p.getInventory(), Material.DIAMOND, price)) {
							BedWars.removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							BedWars.getTeamArmor().replace(team, BedWars.getTeamArmor().get(team)+1);
							
							Inventory inv = BedWars.getSavedUp().get(team);
							List<String> lore2 = new ArrayList<String>();
							if(BedWars.getTeamArmor().get(team) < 5) {
								lore2.add(BedWars.getLang().getString("GUI.upgrades.3").replaceAll("%price%", 
										String.valueOf(BedWars.getUpgradeArmor().get(BedWars.getUpgradeArmor().get(team)+1))));
								lore2.add(BedWars.getLang().getString("GUI.upgrades.10").replaceAll("%before%", 
										String.valueOf(BedWars.getTeamArmor().get(team))).replaceAll("%after%",
												String.valueOf(BedWars.getTeamArmor().get(team)+1)));
							}
							else lore2.add(BedWars.getLang().getString("GUI.upgrades.11"));
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							BedWars.getSavedUp().replace(team, inv);
							
							for(String p_name : BedWars.getTeams().get(team)) {
								Player pl = Bukkit.getPlayer(p_name);
								ItemStack helmet = BedWars.getPlayerHelmet().get(pl.getName());
								ItemStack chestpate = BedWars.getPlayerChestplate().get(pl.getName());
								ItemStack leggings = BedWars.getPlayerLeggings().get(pl.getName());
								ItemStack boots = BedWars.getPlayerBoots().get(pl.getName());
								helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, BedWars.getTeamArmor().get(team)-1);
								chestpate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, BedWars.getTeamArmor().get(team)-1);
								leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, BedWars.getTeamArmor().get(team)-1);
								boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, BedWars.getTeamArmor().get(team)-1);
								BedWars.getPlayerHelmet().replace(pl.getName(), helmet);
								BedWars.getPlayerChestplate().replace(pl.getName(), chestpate);
								BedWars.getPlayerLeggings().replace(pl.getName(), leggings);
								BedWars.getPlayerBoots().replace(pl.getName(), boots);
								
								p.getInventory().setHelmet(helmet);
								p.getInventory().setChestplate(chestpate);
								p.getInventory().setLeggings(leggings);
								p.getInventory().setBoots(boots);
								BedWars.send(Bukkit.getPlayer(p_name), p.getDisplayName()+" "+
								BedWars.getLang().getString("Common.18")+" &7[&c"+String.valueOf(BedWars.getTeamArmor().get(team)-1)+
								"&e ⟶ &c"+String.valueOf(BedWars.getTeamArmor().get(team))+"&7]");
							}
						}
						else BedWars.send(p, BedWars.getLang().getString("Common.11"));
					}
				}
				else if(it.getType() == Material.GOLD_PICKAXE) {
					if(BedWars.getTeamSpeed().get(team) >= 4) {
						BedWars.send(p, BedWars.getLang().getString("Common.14"));
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(BedWars.hasItem(p.getInventory(), Material.DIAMOND, price)) {
							BedWars.removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							BedWars.getTeamSpeed().replace(team, BedWars.getTeamSpeed().get(team)+1);
							
							Inventory inv = BedWars.getSavedUp().get(team);
							List<String> lore2 = new ArrayList<String>();
							if(BedWars.getTeamSpeed().get(team) < 4) {
								lore2.add(BedWars.getLang().getString("GUI.upgrades.3").replaceAll("%price%", 
										String.valueOf(BedWars.getUpgradeSpeed().get(BedWars.getTeamSpeed().get(team)+1))));
								lore2.add(BedWars.getLang().getString("GUI.upgrades.10").replaceAll("%before%", 
										String.valueOf(BedWars.getTeamSpeed().get(team))).replaceAll("%after%", 
												String.valueOf(BedWars.getTeamSpeed().get(team)+1)));
							}
							else lore2.add(BedWars.getLang().getString("GUI.upgrades.11"));
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							BedWars.getSavedUp().replace(team, inv);
							
							for(String p_name : BedWars.getTeams().get(team)) {
								BedWars.send(Bukkit.getPlayer(p_name), p.getDisplayName()+" "+
							BedWars.getLang().getString("Common.19")+" &7[&c"+String.valueOf(BedWars.getTeamSpeed().get(team)-1)+
							"&e ⟶ &c"+String.valueOf(BedWars.getTeamSpeed().get(team))+"&7]");
							}
						}
						else BedWars.send(p, BedWars.getLang().getString("Common.11"));
					}
				}
				else if(it.getType() == Material.TRIPWIRE_HOOK) {
					if(BedWars.getTeamTrap().get(team)) {
						BedWars.send(p, BedWars.getLang().getString("Common.20"));
					}
					else {
						List<String> lore = meta.getLore();
						String l = lore.get(0);
						String[] parse = l.split(" ");
						int price = Integer.valueOf(parse[1].substring(2));
						if(BedWars.hasItem(p.getInventory(), Material.DIAMOND, price)) {
							BedWars.removeItem(p.getInventory(), Material.DIAMOND, price);
							p.updateInventory();
							BedWars.getTeamTrap().replace(team, true);
							
							Inventory inv = BedWars.getSavedUp().get(team);
							List<String> lore2 = new ArrayList<String>();
							lore2.add(BedWars.getLang().getString("GUI.upgrades.12"));
							meta.setLore(lore2);
							it.setItemMeta(meta);
							inv.setItem(e.getRawSlot(), it);
							BedWars.getSavedUp().replace(team, inv);
							
							for(String p_name : BedWars.getTeams().get(team)) {
								BedWars.send(Bukkit.getPlayer(p_name), p.getDisplayName()+" "+BedWars.getLang().getString("Common.21"));
							}
						}
						else BedWars.send(p, BedWars.getLang().getString("Common.11"));
					}
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(BedWars.getShop())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getShopBlocks())) {
					p.openInventory(BedWars.getSavedInv().get(team));
				}
				else if(it.isSimilar(BedWars.getShopSwords())) {
					p.openInventory(BedWars.getInvSwords());
				}
				else if(it.isSimilar(BedWars.getShopBows())) {
					p.openInventory(BedWars.getInvBows());
				}
				else if(it.isSimilar(BedWars.getShopTools())) {
					p.openInventory(BedWars.getInvTools());
				}
				else if(it.isSimilar(BedWars.getShopUseful())) {
					p.openInventory(BedWars.getInvUseful());
				}
				else if(it.isSimilar(BedWars.getShopPotions())) {
					p.openInventory(BedWars.getInvPotions());
				}
				else if(it.isSimilar(BedWars.getShopArmor())) {
					p.openInventory(BedWars.getInvArmor());
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(BedWars.getSavedInv().get(team))) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getBack())) {
					p.openInventory(BedWars.getShop());
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
						if(type.contains(BedWars.getLang().getString("GUI.common.iron"))) {
							if(BedWars.hasItem(inv, Material.IRON_INGOT, price)) {
								BedWars.removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ItemStack res = new ItemStack(it.getType(), it.getAmount());
								if(it.getType() == Material.WOOL) {
									ItemStack cp = it.clone();
									ItemMeta mm = cp.getItemMeta();
									mm.setLore(null);
									cp.setItemMeta(mm);
									p.getInventory().addItem(cp);
								}
								else {
									p.getInventory().addItem(res);
								}
								p.updateInventory();
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.gold"))) {
							if(BedWars.hasItem(inv, Material.GOLD_INGOT, price)) {
								BedWars.removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ItemStack res = new ItemStack(it.getType(), it.getAmount());
								if(it.getType() == Material.WOOL) {
									ItemStack cp = it.clone();
									ItemMeta mm = cp.getItemMeta();
									mm.setLore(null);
									cp.setItemMeta(mm);
									p.getInventory().addItem(cp);
								}
								else {
									p.getInventory().addItem(res);
								}
								p.updateInventory();
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.emeralds"))) {
							if(BedWars.hasItem(inv, Material.EMERALD, price)) {
								BedWars.removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ItemStack res = new ItemStack(it.getType(), it.getAmount());
								if(it.getType() == Material.WOOL) {
									ItemStack cp = it.clone();
									ItemMeta mm = cp.getItemMeta();
									mm.setLore(null);
									cp.setItemMeta(mm);
									p.getInventory().addItem(cp);
								}
								else {
									p.getInventory().addItem(res);
								}
								p.updateInventory();
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
					}
				} catch (Exception ex) {
					e.setCancelled(true);
				}
			}
			e.setCancelled(true);
		}
		else if(e.getInventory().equals(BedWars.getInvSwords())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getBack())) {
					p.openInventory(BedWars.getShop());
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
						if(type.contains(BedWars.getLang().getString("GUI.common.iron"))) {
							if(BedWars.hasItem(inv, Material.IRON_INGOT, price)) {
								BedWars.removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.gold"))) {
							if(BedWars.hasItem(inv, Material.GOLD_INGOT, price)) {
								BedWars.removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.emeralds"))) {
							if(BedWars.hasItem(inv, Material.EMERALD, price)) {
								BedWars.removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						if(ch) {
							boolean isSword = false;
							if(it.getType() == Material.STONE_SWORD) {
								isSword = true;
								if(BedWars.hasItem(inv, Material.WOOD_SWORD, 1)) {
									BedWars.removeItem(inv, Material.WOOD_SWORD, 1);
								}
							}
							else if(it.getType() == Material.IRON_SWORD) {
								isSword = true;
								if(BedWars.hasItem(inv, Material.STONE_SWORD, 1)) {
									BedWars.removeItem(inv, Material.STONE_SWORD, 1);
								}
								if(BedWars.hasItem(inv, Material.WOOD_SWORD, 1)) {
									BedWars.removeItem(inv, Material.WOOD_SWORD, 1);
								}
							}
							else if(it.getType() == Material.DIAMOND_SWORD) {
								isSword = true;
								if(BedWars.hasItem(inv, Material.STONE_SWORD, 1)) {
									BedWars.removeItem(inv, Material.STONE_SWORD, 1);
								}
								if(BedWars.hasItem(inv, Material.WOOD_SWORD, 1)) {
									BedWars.removeItem(inv, Material.WOOD_SWORD, 1);
								}
								if(BedWars.hasItem(inv, Material.IRON_SWORD, 1)) {
									BedWars.removeItem(inv, Material.IRON_SWORD, 1);
								}
							}
							ItemStack res = new ItemStack(it.getType(), it.getAmount());
							if(isSword && BedWars.getTeamSwords().get(team) > 1) res.addEnchantment(Enchantment.DAMAGE_ALL, BedWars.getTeamSwords().get(team)-1);
							ItemMeta mm = res.getItemMeta();
							mm.setUnbreakable(true);
							res.setItemMeta(mm);
							if(isSword) {
								ItemStack bef_sword = BedWars.getPlayerSword().get(p.getName());
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
		else if(e.getInventory().equals(BedWars.getInvArmor())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getBack())) {
					p.openInventory(BedWars.getShop());
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
						if(type.contains(BedWars.getLang().getString("GUI.common.iron"))) {
							if(BedWars.hasItem(inv, Material.IRON_INGOT, price)) {
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.gold"))) {
							if(BedWars.hasItem(inv, Material.GOLD_INGOT, price)) {
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.emeralds"))) {
							if(BedWars.hasItem(inv, Material.EMERALD, price)) {
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						if(ch) {
							if(it.getType() == Material.CHAINMAIL_BOOTS) {
								if(BedWars.hasItem(inv, Material.CHAINMAIL_BOOTS, 1) || BedWars.hasItem(inv, Material.IRON_BOOTS, 1) 
										|| BedWars.hasItem(inv, Material.DIAMOND_BOOTS, 1)) {
									BedWars.send(p, BedWars.getLang().getString("Common.22"));
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
									
									ItemStack bef_leg = BedWars.getPlayerLeggings().get(p.getName());
									ItemStack bef_boots = BedWars.getPlayerBoots().get(p.getName());
									leggings.addEnchantments(bef_leg.getEnchantments());
									boots.addEnchantments(bef_boots.getEnchantments());
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
									p.updateInventory();
									
									BedWars.getPlayerLeggings().replace(p.getName(), leggings);
									BedWars.getPlayerBoots().replace(p.getName(), boots);
									BedWars.removeItem(inv, Material.IRON_INGOT, price);
									p.updateInventory();
									p.closeInventory();
								}
							}
							else if(it.getType() == Material.IRON_BOOTS) {
								if(BedWars.hasItem(inv, Material.IRON_BOOTS, 1) || BedWars.hasItem(inv, Material.DIAMOND_BOOTS, 1)) {
									BedWars.send(p, BedWars.getLang().getString("Common.22"));
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
									
									ItemStack bef_leg = BedWars.getPlayerLeggings().get(p.getName());
									ItemStack bef_boots = BedWars.getPlayerBoots().get(p.getName());
									leggings.addEnchantments(bef_leg.getEnchantments());
									boots.addEnchantments(bef_boots.getEnchantments());
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
									p.updateInventory();
									
									BedWars.getPlayerLeggings().replace(p.getName(), leggings);
									BedWars.getPlayerBoots().replace(p.getName(), boots);
									BedWars.removeItem(inv, Material.GOLD_INGOT, price);
									p.updateInventory();
									p.closeInventory();
								}
							}
							else if(it.getType() == Material.DIAMOND_BOOTS) {
								if(BedWars.hasItem(inv, Material.DIAMOND_BOOTS, 1)) {
									BedWars.send(p, BedWars.getLang().getString("Common.22"));
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
									
									ItemStack bef_leg = BedWars.getPlayerLeggings().get(p.getName());
									ItemStack bef_boots = BedWars.getPlayerBoots().get(p.getName());
									leggings.addEnchantments(bef_leg.getEnchantments());
									boots.addEnchantments(bef_boots.getEnchantments());
									p.getInventory().setLeggings(leggings);
									p.getInventory().setBoots(boots);
									p.updateInventory();
									
									BedWars.getPlayerLeggings().replace(p.getName(), leggings);
									BedWars.getPlayerBoots().replace(p.getName(), boots);
									BedWars.removeItem(inv, Material.EMERALD, price);
									p.updateInventory();
									p.closeInventory();
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
		else if(e.getInventory().equals(BedWars.getInvTools())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getBack())) {
					p.openInventory(BedWars.getShop());
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
						if(type.contains(BedWars.getLang().getString("GUI.common.iron"))) {
							if(BedWars.hasItem(inv, Material.IRON_INGOT, price)) {
								BedWars.removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.gold"))) {
							if(BedWars.hasItem(inv, Material.GOLD_INGOT, price)) {
								BedWars.removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.emeralds"))) {
							if(BedWars.hasItem(inv, Material.EMERALD, price)) {
								BedWars.removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						if(ch) {
							if(it.getType() == Material.IRON_PICKAXE) {
								if(BedWars.hasItem(inv, Material.STONE_PICKAXE, 1)) {
									BedWars.removeItem(inv, Material.STONE_PICKAXE, 1);
								}
							}
							else if(it.getType() == Material.IRON_AXE) {
								if(BedWars.hasItem(inv, Material.STONE_AXE, 1)) {
									BedWars.removeItem(inv, Material.STONE_AXE, 1);
								}
							}
							else if(it.getType() == Material.DIAMOND_PICKAXE) {
								if(BedWars.hasItem(inv, Material.STONE_PICKAXE, 1)) {
									BedWars.removeItem(inv, Material.STONE_PICKAXE, 1);
								}
								if(BedWars.hasItem(inv, Material.IRON_PICKAXE, 1)) {
									BedWars.removeItem(inv, Material.IRON_PICKAXE, 1);
								}
							}
							else if(it.getType() == Material.DIAMOND_AXE) {
								if(BedWars.hasItem(inv, Material.STONE_AXE, 1)) {
									BedWars.removeItem(inv, Material.STONE_AXE, 1);
								}
								if(BedWars.hasItem(inv, Material.IRON_AXE, 1)) {
									BedWars.removeItem(inv, Material.IRON_AXE, 1);
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
		else if(e.getInventory().equals(BedWars.getInvBows())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getBack())) {
					p.openInventory(BedWars.getShop());
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
						if(type.contains(BedWars.getLang().getString("GUI.common.iron"))) {
							if(BedWars.hasItem(inv, Material.IRON_INGOT, price)) {
								BedWars.removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.gold"))) {
							if(BedWars.hasItem(inv, Material.GOLD_INGOT, price)) {
								BedWars.removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.emeralds"))) {
							if(BedWars.hasItem(inv, Material.EMERALD, price)) {
								BedWars.removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
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
		else if(e.getInventory().equals(BedWars.getInvUseful())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getBack())) {
					p.openInventory(BedWars.getShop());
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
						if(type.contains(BedWars.getLang().getString("GUI.common.iron"))) {
							if(BedWars.hasItem(inv, Material.IRON_INGOT, price)) {
								BedWars.removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.gold"))) {
							if(BedWars.hasItem(inv, Material.GOLD_INGOT, price)) {
								BedWars.removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.emeralds"))) {
							if(BedWars.hasItem(inv, Material.EMERALD, price)) {
								BedWars.removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
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
		else if(e.getInventory().equals(BedWars.getInvPotions())) {
			ItemStack it = e.getCurrentItem();
			if(it != null) {
				if(it.isSimilar(BedWars.getBack())) {
					p.openInventory(BedWars.getShop());
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
						if(type.contains(BedWars.getLang().getString("GUI.common.iron"))) {
							if(BedWars.hasItem(inv, Material.IRON_INGOT, price)) {
								BedWars.removeItem(inv, Material.IRON_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.gold"))) {
							if(BedWars.hasItem(inv, Material.GOLD_INGOT, price)) {
								BedWars.removeItem(inv, Material.GOLD_INGOT, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
						}
						else if(type.contains(BedWars.getLang().getString("GUI.common.emeralds"))) {
							if(BedWars.hasItem(inv, Material.EMERALD, price)) {
								BedWars.removeItem(inv, Material.EMERALD, price);
								p.updateInventory();
								ch = true;
							}
							else BedWars.send(p, BedWars.getLang().getString("Common.11"));
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
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		ItemStack item2 = e.getItemDrop().getItemStack();
		if(BedWars.getStatus().equals("wait") && (item2.isSimilar(BedWars.getItem()) || item2.isSimilar(BedWars.getLobby()) || 
				item2.getType().toString().contains("WOOL"))) e.setCancelled(true);
		if(BedWars.getStatus().equals("play") && item2.getType().toString().contains("SWORD")) e.setCancelled(true);
	}
	
	
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(BedWars.getStatus().equals("wait")) e.setCancelled(true);
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.ANVIL || e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE)) e.setCancelled(true);
		if(p.getItemInHand().isSimilar(BedWars.getItem())) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				p.openInventory(BedWars.getInv());
			}
		}
		else if(p.getItemInHand().isSimilar(BedWars.getLobby())) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				BedWars.toLobby(p);
			}
		}
		else if(BedWars.getStatus().equals("wait") && p.getItemInHand().getType().toString().contains("WOOL")) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				p.openInventory(BedWars.getTeamInv());
			}
		}
		if(BedWars.getStatus().equals("play")) {
			String name = BedWars.getPlayerTeam().get(p.getName());
			if(e.getAction() == Action.LEFT_CLICK_BLOCK && BedWars.getTeamSpeed().get(name) >= 2) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20*15, BedWars.getTeamSpeed().get(name)-1));
			}
			else if(p.getItemInHand().getType() == Material.FIREBALL && (e.getAction() == Action.RIGHT_CLICK_BLOCK || 
					e.getAction() == Action.RIGHT_CLICK_AIR)) {
                if(!BedWars.getFireballCooldowns().contains(p.getName())) {
                	try {
                		if(p.getItemInHand().getAmount() <= 1) p.setItemInHand(null);
                		if(p.getItemInHand().getAmount() <= 1) p.getItemInHand().setType(Material.AIR);
                	} catch(Exception ee) {};
                	p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
                	
                    float angle = (float) ((-p.getLocation().getYaw())*Math.PI/180.0);
                    float dz = (float) (BedWars.getConfig().getDouble("fireball_distance_from_player")*Math.cos(angle));
                    float dx = (float) (BedWars.getConfig().getDouble("fireball_distance_from_player")*Math.sin(angle));
                    
                    Location start = p.getLocation().add(dx, 1.0, dz);
                    Fireball fireball = p.getWorld().spawn(start, Fireball.class);
                    fireball.setCustomName(p.getName());
                    fireball.setCustomNameVisible(false);
                    fireball.setYield(BedWars.getConfig().getInt("fireball_radius_explosion"));
                    
                    BedWars.getFireballCooldowns().add(p.getName());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                    	public void run() {
                    		BedWars.getFireballCooldowns().remove(p.getName());
                    	}
                    }, BedWars.getConfig().getLong("fireball_cooldown"));
                }
                e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		String msg = e.getMessage();
		Player p = e.getPlayer();
		if(!BedWars.getPlayerTeam().containsKey(p.getName())) {
			e.setMessage(BedWars.getLang().getString("chat.spec")+msg);
			for(Player pl : Bukkit.getOnlinePlayers()){
	            e.getRecipients().remove(pl);
	            if(!BedWars.getPlayerTeam().containsKey(pl.getName())) e.getRecipients().add(pl);
	        }
		}
		else {
			String team = BedWars.getPlayerTeam().get(p.getName());
			String c = BedWars.getConfig().getString("arena.commands."+team+".color");
			if(msg.charAt(0) == '!') {
				e.setMessage(BedWars.getLang().getString("chat.global")+msg.substring(1));
			}
			else {
				e.setMessage(BedWars.getLang().getString("chat.team").replaceAll("%team%", "§"+c+team)+msg);
				for(Player pl : Bukkit.getOnlinePlayers()){
		            e.getRecipients().remove(pl);
		        }
				for(String p_name : BedWars.getTeams().get(team)) {
					e.getRecipients().add(Bukkit.getPlayer(p_name));
				}
			}
		}
	}
	
	@EventHandler
	public void interactEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(BedWars.getStatus().equals("play")) {
			if(e.getRightClicked() instanceof Villager) {
				Villager vil = (Villager) e.getRightClicked();
				if(vil.getCustomName().equals(BedWars.getLang().getString("GUI.shop.1"))) {
					p.openInventory(BedWars.getShop());
				}
				else if(vil.getCustomName().equals(BedWars.getLang().getString("GUI.upgrades.1"))) {
					p.openInventory(BedWars.getSavedUp().get(BedWars.getPlayerTeam().get(p.getName())));
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void death(EntityDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player killer = p.getKiller();
			if(BedWars.getInvis().contains(p.getName())) {
				BedWars.getInvis().remove(p.getName());
				ItemStack helmet = BedWars.getPlayerHelmet().get(p.getName());
				ItemStack chestpate = BedWars.getPlayerChestplate().get(p.getName());
				ItemStack leggings = BedWars.getPlayerLeggings().get(p.getName());
				ItemStack boots = BedWars.getPlayerBoots().get(p.getName());
				
				p.getInventory().setHelmet(helmet);
				p.getInventory().setChestplate(chestpate);
				p.getInventory().setLeggings(leggings);
				p.getInventory().setBoots(boots);
				BedWars.send(p, BedWars.getLang().getString("Common.26"));
				if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) p.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			if(killer != null && killer instanceof Player) {
				BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.25")+" "+killer.getDisplayName());
				BedWars.getDatabase().addKill(killer);
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
					BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.27"));
				}
				else {
					DamageCause deathCause = p.getLastDamageCause().getCause();
					if (deathCause == DamageCause.DROWNING) {
						BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.28"));
					}
					else if (deathCause == DamageCause.VOID) {
						if(e.getDrops().contains(new ItemStack(Material.ENDER_PEARL))) {
							BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.29"));
						}
						else BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.30"));
					}
					else if (deathCause == DamageCause.BLOCK_EXPLOSION || deathCause == DamageCause.ENTITY_EXPLOSION) {
						BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.31"));
					}
					else if (deathCause == DamageCause.FALL) {
						if(e.getDrops().contains(new ItemStack(Material.WATER_BUCKET))) {
							BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.32"));
						}
						else BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.33"));
					}
					else if (deathCause == DamageCause.FIRE) {
						BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.35"));
					}
					else if (deathCause == DamageCause.FALLING_BLOCK) {
						BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.36"));
					}
					else {
						BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.27"));
					}
				}
			}
			e.getDrops().clear();
			String team = BedWars.getPlayerTeam().get(p.getName());
			if(!BedWars.getBedAlive().contains(team)) {
				BedWars.setCountPlayers(BedWars.getCountPlayers()-1);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title {\"text\":\""+BedWars.getLang().getString("Common.37")+"\", \"bold\":true, \"color\":\"red\"}");
				if(killer != null && killer instanceof Player) {
					if(BedWars.isMysqlEnabled() && BedWars.getFinalKillMoney() > 0) {
						try {
							BedWars.getEconomy().depositPlayer(killer, BedWars.getFinalKillMoney());
							BedWars.send(killer, BedWars.getLang().getString("Common.38").replaceAll("%money%", 
									String.valueOf(BedWars.getFinalKillMoney())));
						} catch(Exception ee) {};
					}
					BedWars.getDatabase().addFinalKill(killer);
				}
				BedWars.removePlayerFromTeam(p);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> p.spigot().respawn(), 1);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
					public void run() {
						BedWars.toSpec(p);
					}
				}, 20);
				if(BedWars.isEnd()) {
					BedWars.endGame();
				}
			}
			else {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> p.spigot().respawn(), 1);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title {\"text\":\""+BedWars.getLang().getString("Common.37")+"\", \"bold\":true, \"color\":\"red\"}");
						p.teleport(BedWars.getSpec());
						p.setGameMode(GameMode.SPECTATOR);
						BedWars.send(p, BedWars.getLang().getString("Common.39").replaceAll("%N%", String.valueOf(BedWars.getConfig().getInt("respawn_time_seconds"))));
					}
				}, 2);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
					public void run() {
						BedWars.tpToSpawn(p);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title {\"text\":\""+BedWars.getLang().getString("Common.41")+"\", \"bold\":true, \"color\":\"green\"}");
						BedWars.send(p, BedWars.getLang().getString("Common.42"));
						p.setMaxHealth(20);
						BedWars.returnItems(p);
						p.setGameMode(GameMode.SURVIVAL);
					}
				}, 20*5);
			}
		}
	}
	
	@EventHandler
	public void hunger(FoodLevelChangeEvent  e) {
		if(!BedWars.getConfig().getBoolean("players_need_food")) e.setCancelled(true);
	}
	
	@EventHandler
	public void move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(BedWars.getStatus().equals("play")) {
			if(p.getLocation().getBlockY() <= BedWars.getConfig().getInt("autodeath_y_coord")) {
				p.setHealth(1);
			}
			if(BedWars.isCompass()) {
				Location loc = p.getLocation();
				Location min_lc = loc;
				int min_dis = 100000000;
				for(String p_name : BedWars.getPlayerTeam().keySet()) {
					Player pl = Bukkit.getPlayer(p_name);
					if(!BedWars.getPlayerTeam().get(p.getName()).equals(BedWars.getPlayerTeam().get(p_name))) {
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
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" actionbar \""+BedWars.getLang().getString("Common.43").replaceAll("%distance%", String.valueOf(min_dis))+" \"");
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage(null);
		BedWars.getDatabase().addToDB(p);
		if(BedWars.getStatus().equals("wait")) {
			int min_players = BedWars.getPlayersInTeam()*BedWars.getMinTeams();
			if(BedWars.getMaxTeams() == 2 && BedWars.getMinTeams() == 2) min_players = (int) ((int) BedWars.getPlayersInTeam()*1.5);
			else p.setMaxHealth(20);
			BedWars.setCountPlayers(BedWars.getCountPlayers()+1);
			p.getInventory().setItem(8, BedWars.getLobby());
			BedWars.addToTeam(p);

			ItemStack now = BedWars.getConfig().getItemStack("arena.commands."+BedWars.getPlayerTeam().get(p.getName())+".block");
			ItemMeta meta = now.getItemMeta();
			meta.setDisplayName(BedWars.getLang().getString("hotbar.team_select"));
			now.setItemMeta(meta);
			
			p.getInventory().setItem(0, now);

			BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.46").replaceAll("%players%", 
					String.valueOf(BedWars.getCountPlayers())).replaceAll("%max_players%", 
							String.valueOf(BedWars.getMaxTeams()*BedWars.getPlayersInTeam())));
			if(!BedWars.isTimer() && BedWars.getCountPlayers() >= min_players && !BedWars.isEnd()) {
				BedWars.startTimer();
			}
		}
		else if(BedWars.getStatus().equals("play")) {
			BedWars.send(p, BedWars.getLang().getString("Common.45"));
			BedWars.toSpec(p);
			BedWars.clearColor(p);
		}
		else {
			if(!p.hasPermission("API.glav")) BedWars.toLobby(p);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(null);
		if(BedWars.isTabEnabled()) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtabname");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player "+p.getName()+" customtagname");
		}
		if(BedWars.getStatus().equals("wait")) {
			int min_players = BedWars.getPlayersInTeam()*BedWars.getMinTeams();
			BedWars.setCountPlayers(BedWars.getCountPlayers()-1);
			BedWars.sendAll(p.getDisplayName()+" "+BedWars.getLang().getString("Common.47").replaceAll("%players%", 
					String.valueOf(BedWars.getCountPlayers())).replaceAll("%max_players%", String.valueOf(BedWars.getMaxTeams()*BedWars.getPlayersInTeam())));
			BedWars.removePlayerFromTeam(p);
			if(BedWars.isTimer() && (BedWars.getCountPlayers() < min_players || BedWars.isEnd())) {
				BedWars.stopTimer();
			}
		}
		else if(BedWars.getStatus().equals("play") && BedWars.getPlayerTeam().containsKey(p.getName())) {
			BedWars.setCountPlayers(BedWars.getCountPlayers()-1);
			String team = BedWars.getPlayerTeam().get(p.getName());
			String c = BedWars.getConfig().getString("arena.commands."+team+".color");
			BedWars.sendAll(p.getDisplayName()+BedWars.getLang().getString("Common.48"));
			BedWars.removePlayerFromTeam(p);
			if(BedWars.getBedAlive().contains(team)) {
				if(BedWars.getTeams().get(team).isEmpty()) {
					BedWars.sendAll(BedWars.getLang().getString("Common.49").replaceAll("%team%", "§"+c+team));
					BedWars.getBedAlive().remove(team);
				}
			}
			if(BedWars.isEnd()) {
				BedWars.endGame();
			}
		}
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
  			  e.getEntity().remove();
              while (iterator.hasNext()) {
                  hitBlock = iterator.next();
                  if(!ch) {
                      Location loc = new Location(hitBlock.getWorld(), hitBlock.getLocation().getBlockX(), hitBlock.getLocation().getBlockY()-1, hitBlock.getLocation().getBlockZ());
	                  if (loc.getBlock().getType() == Material.AIR) {
	                      if(loc.getBlockY() <= BedWars.getConfig().getInt("height_limit")) {
	                    	  loc.getBlock().setType(Material.SANDSTONE);
	                    	  BedWars.getBlocks().add(loc);
		                      Location loc1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()+1);
		                      Location loc2 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()-1);
		                      Location loc3 = new Location(loc.getWorld(), loc.getBlockX()+1, loc.getBlockY(), loc.getBlockZ());
		                      Location loc4 = new Location(loc.getWorld(), loc.getBlockX()-1, loc.getBlockY(), loc.getBlockZ());
		                      if(loc1.getBlock().getType() == Material.AIR) {
		                    	  loc1.getBlock().setType(Material.SANDSTONE);
		                    	  BedWars.getBlocks().add(loc1);
		                      }
		                      if(loc2.getBlock().getType() == Material.AIR) {
		                    	  loc2.getBlock().setType(Material.SANDSTONE);
		                    	  BedWars.getBlocks().add(loc2);
		                      }
		                      if(loc3.getBlock().getType() == Material.AIR) {
		                    	  loc3.getBlock().setType(Material.SANDSTONE);
		                    	  BedWars.getBlocks().add(loc3);
		                      }
		                      if(loc4.getBlock().getType() == Material.AIR) {
		                    	  loc4.getBlock().setType(Material.SANDSTONE);
		                    	  BedWars.getBlocks().add(loc4);
		                      }
	                      }
	                  }
	                  else if(!BedWars.getBlocks().contains(loc)) {
	                	  break;
	                  }
                  }
                  ch = false;
              }
		}
	}
	
	@EventHandler
	public void onPlayerCraft(CraftItemEvent e) {
		if(!BedWars.getStatus().equals("reload")) e.setCancelled(true);
	}
	
	@EventHandler
	public void dam(EntityDamageEvent e) {
		if(BedWars.getStatus().equals("wait")) e.setCancelled(true);
		if(e.getEntity() instanceof Item) e.setCancelled(true);
		if(BedWars.getStatus().equals("reload")) {
			if(e.getEntity() instanceof Player) e.setCancelled(true);
		}
		if(e.getEntity() instanceof Villager) {
			if(!BedWars.getStatus().equals("reload")) e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void bloccck(PlayerSwapHandItemsEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if(BedWars.getStatus().equals("wait")) e.setCancelled(true);
		if(e.getEntity() instanceof Villager) {
			if(!BedWars.getStatus().equals("reload")) {
				Villager vil = (Villager) e.getEntity();
				if(vil.getCustomName() != null) e.setCancelled(true);
			}
		}
		else if(e.getDamager() instanceof TNTPrimed && e.getEntity() instanceof Player) {
			if(BedWars.getConfig().getBoolean("disable_tnt_damage_others_players")) e.setDamage(0.0);
			else {
				TNTPrimed tnt = (TNTPrimed) e.getDamager();
				Player p = (Player) e.getEntity();
				String name = tnt.getCustomName();
				String team = BedWars.getPlayerTeam().get(p.getName());
				if(BedWars.getTeams().get(team).contains(name)) {
					e.setDamage(0.0);
				}
			}
		}
		else if(e.getDamager() instanceof Fireball && e.getEntity() instanceof Player) {
			Fireball fireball = (Fireball) e.getDamager();
			Player p = (Player) e.getEntity();
			String name = fireball.getCustomName();
			String team = BedWars.getPlayerTeam().get(p.getName());
			if(BedWars.getTeams().get(team).contains(name)) {
				e.setDamage(0.0);
			}
		}
		else if(e.getEntity() instanceof Player && e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			Player p = (Player) e.getEntity();
			if(e.getDamager() instanceof Arrow) {
				Arrow a = (Arrow) e.getDamager();
				Player damager = (Player) a.getShooter();
				String team = BedWars.getPlayerTeam().get(p.getName());
				String team2 = BedWars.getPlayerTeam().get(damager.getName());
				if(team.equals(team2)) e.setCancelled(true);
			}
		}
		else if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			String p_name = p.getName();
			String team = BedWars.getPlayerTeam().get(p_name);
			String team2 = BedWars.getPlayerTeam().get(damager.getName());
			if(team.equals(team2)) e.setCancelled(true);
			if(BedWars.getInvis().contains(p_name)) {
				BedWars.getInvis().remove(p_name);
				ItemStack helmet = BedWars.getPlayerHelmet().get(p_name);
				ItemStack chestpate = BedWars.getPlayerChestplate().get(p_name);
				ItemStack leggings = BedWars.getPlayerLeggings().get(p_name);
				ItemStack boots = BedWars.getPlayerBoots().get(p_name);
				
				p.getInventory().setHelmet(helmet);
				p.getInventory().setChestplate(chestpate);
				p.getInventory().setLeggings(leggings);
				p.getInventory().setBoots(boots);
				BedWars.send(p, BedWars.getLang().getString("Common.26"));
				if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) p.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}
	}
}
