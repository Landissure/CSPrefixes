package landissure.mc.csprefixes;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class CSPrefixes extends JavaPlugin implements Listener {
	public HashMap<String, String> prefixes = new HashMap<String, String>();
	public HashMap<String, String> suffixes = new HashMap<String, String>();
	public Path pre = Paths.get("plugins/prefixes.data");

	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		ArrayList<String> input = new ArrayList<String>();
		try {
			String cache = "";
			for(byte b : Files.readAllBytes(pre)){
				if(b == '\n'){
					input.add(cache);
					cache = "";
				}
				else{
					cache += (char) b;
				}
			}

		} catch (IOException e) {
		}
		for(int i = 0; i < input.size(); i = i + 2){
			prefixes.put(input.get(i),input.get(i+1));

		}
	}

	public void onDisable(){
		ArrayList<String> outputs = new ArrayList<String>();
		for(String d : prefixes.keySet()){
			outputs.add(d);
			outputs.add(prefixes.get(d));
		}

		try {
			Files.deleteIfExists(pre);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

			for(int i = 0; i < outputs.size(); i++){
				try {

					Files.write(pre,new byte[]{'\n'}, StandardOpenOption.APPEND);
					Files.write(pre,outputs.get(i).getBytes(), StandardOpenOption.APPEND);
				}
				catch(NoSuchFileException e){
					try {
						Files.write(pre,outputs.get(i).getBytes(), StandardOpenOption.CREATE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
}

public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
	if(sender instanceof Player){
		Player s = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("prefix")){
			switch(args.length){
			case 2:
				prefixes.put(args[0], args[1]);
				s.sendMessage("Prefix for player " + args[0] + " set.");
				break;
			case 1:
				prefixes.put(s.getName(), args[0]);
				s.sendMessage("Prefix for player " + s.getName() + " set.");
				break;
			default: return false;
			}
		}
		if(cmd.getName().equalsIgnoreCase("suffix")){
			switch(args.length){
			case 2:
				suffixes.put(args[0], args[1]);
				s.sendMessage("Suffix for player " + args[0] + " set.");
				break;
			case 1:
				suffixes.put(s.getName(), args[0]);
				s.sendMessage("Suffix for player " + s.getName() + " set.");
				break;
			default: return false;
			}
		}
	}
	return true;
}

public String replaceColor(String tbc){
	while(tbc.indexOf("@") != -1){
		tbc = tbc.substring(0, tbc.indexOf("@")) + "§" + tbc.substring(tbc.indexOf("@")+1, tbc.length());
	}
	return tbc;
}

@EventHandler
public void onChat(AsyncPlayerChatEvent e){
	if(this.prefixes.containsKey(e.getPlayer().getName())){
		e.getPlayer().setPlayerListName(replaceColor(prefixes.get(e.getPlayer().getName())) + e.getPlayer().getName());
		e.getPlayer().setDisplayName(replaceColor(prefixes.get(e.getPlayer().getName())) +  e.getPlayer().getName() + "§r");
	}
	if(this.suffixes.containsKey(e.getPlayer().getName())){
		e.setMessage(replaceColor(suffixes.get(e.getPlayer().getName())) + e.getMessage());
	}
}

@EventHandler
public void onJoin(PlayerJoinEvent e){
	if(this.prefixes.containsKey(e.getPlayer().getName())){
		e.getPlayer().setPlayerListName(replaceColor(prefixes.get(e.getPlayer().getName())) + e.getPlayer().getName());
	}
}

}
