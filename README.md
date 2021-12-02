# BedWars
 Minecraft plugin on BedWars game on spigot 1.8-1.17.1  
 SpigotMC: https://www.spigotmc.org/resources/bedwarsmeow.95820/  
 
<b>This plugin support only 1 arena with 1 server</b>

<b>Features:</b>
- MySQL support to collect players stats
- BungeeCord support to leave from game
- Many configurable parameters
- Configurable messages
- Configurable shops
- Configurable upgrades
- Configurable generators

<b>Requirements:</b>
- Spigot/Paperspigot 1.8-1.17.1
- Optionally, Vault, any Economy plugin and TAB

<b>Installation:</b>
1) Put this plugin into your 'plugins' folder.
2) Duplicate your 'world' folder as 'map' folder and write in your 'start.sh/.bat' script to delete 'world' folder and duplicate 'map' folder as 'world' folder (server example at github). It is needed for map regeneration.
3) Start the server and edit config.yml in plugin folder, then restart.
 
<b>Demostration:</b>
<a href="https://www.youtube.com/watch?v=krtnFcqwpVo&feature=emb_imp_woyt">link</a>

Commands:
- /bw create [name]
- /bw setminteams [count]
- /bw setmaxteams [count]
- /bw setplayersinteam [count]
- /bw setspawn [new team name]
- /bw setbedloc [team]
- /bw setblock [team] - set wool team block (you should hold it in hand)
- /bw setcolor [team] [color code] - set color code to team without &
- /bw addironspawn [team]
- /bw addgoldspawn [team]
- /bw adddiamondsspawn
- /bw addemeraldsspawn
- /bw addupgrade - add villager with upgrades
- /bw addshop - add shop villager
- /bw setarmor [team] - set colored armor to team (you need to take it)
- /bw setspec - set spectator spawn
- /bw edit - set arena status to "editing"
- /bw save - save changes
- /bw finish - finish editing
- /bw start - start game
- /bw faststart - fast start game
- /bw additem [shop type] [price] [iron/gold/emeralds] (name) - add item in your hand into shop (shop types: blocks/armor/tools/useful/potions/bows)
- /stat (player) - check stat (need MySQL connection)
