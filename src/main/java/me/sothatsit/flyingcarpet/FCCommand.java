package me.sothatsit.flyingcarpet;

import me.sothatsit.flyingcarpet.message.Messages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class FCCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0 && args[0].equalsIgnoreCase("worldguard")) {
            if(!sender.hasPermission("flyingcarpet.worldguard")) {
                Messages.get("error.no-permissions.worldguard").send(sender);
                return true;
            }

            if(!FlyingCarpet.isWorldGuardHooked()) {
                Messages.get("error.worldguard-not-hooked").send(sender);
                return true;
            }

            if(args.length == 1) {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc worldguard <add:remove:list>").send(sender);
                return true;
            }

            if(args[1].equalsIgnoreCase("add")) {
                if(args.length != 3) {
                    Messages.get("error.invalid-arguments").argument("%valid%", "/mc worldguard add <region>").send(sender);
                    return true;
                }

                FlyingCarpet.getMainConfig().addWorldguardBlacklistedRegion(args[2]);
                Messages.get("message.wg.add").argument("%region%", args[2]).send(sender);
                return true;
            }

            if(args[1].equalsIgnoreCase("remove")) {
                if(args.length != 3) {
                    Messages.get("error.invalid-arguments").argument("%valid%", "/mc worldguard remove <region>").send(sender);
                    return true;
                }

                FlyingCarpet.getMainConfig().removeWorldguardBlacklistedRegion(args[2]);
                Messages.get("message.wg.remove").argument("%region%", args[2]).send(sender);
                return true;
            }

            if(args[1].equalsIgnoreCase("list")) {
                if(args.length != 2) {
                    Messages.get("error.invalid-arguments").argument("%valid%", "/mc worldguard list").send(sender);
                    return true;
                }

                Set<String> regions = FlyingCarpet.getMainConfig().getWorldguardBlacklistedRegions();

                if(regions.size() == 0) {
                    Messages.get("message.wg.list-none").send(sender);
                    return true;
                }

                Messages.get("message.wg.list-header").send(sender);

                for(String region : regions) {
                    Messages.get("message.wg.list-line").argument("%region%", region).send(sender);
                }

                return true;
            }

            Messages.get("error.invalid-arguments").argument("%valid%", "/mc worldguard <add:remove:list>").send(sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            Messages.get("error.must-be-player").send(sender);
            return true;
        }
        
        Player p = (Player) sender;
        
        if (!p.hasPermission("flyingcarpet.use")) {
            Messages.get("error.no-permissions.carpet").send(sender);
            return true;
        }
        
        UPlayer up = FlyingCarpet.getInstance().getUPlayer(p);
        
        if (args.length == 0) {
            if(!up.isEnabled() && !FlyingCarpet.getInstance().isCarpetAllowed(p.getLocation())) {
                Messages.get("error.region-blocked").send(sender);
                return true;
            }

            up.setEnabled(!up.isEnabled());
            
            if (up.isEnabled()) {
                sendToggleMessage(p, "message.carpet-on");
            } else {
                sendToggleMessage(p, "message.carpet-off");
            }
            
            return true;
        }
        
        if (args[0].equalsIgnoreCase("on")) {
            if (args.length != 1) {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc on").send(sender);
                return true;
            }

            if(!FlyingCarpet.getInstance().isCarpetAllowed(p.getLocation())) {
                Messages.get("error.region-blocked").send(sender);
                return true;
            }

            up.setEnabled(true);
            
            sendToggleMessage(p, "message.carpet-on");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("off")) {
            if (args.length != 1) {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc off").send(sender);
                return true;
            }
            
            up.setEnabled(false);
            
            sendToggleMessage(p, "message.carpet-off");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            if (!p.hasPermission("flyingcarpet.reload")) {
                Messages.get("error.no-permissions.reload").send(sender);
                return false;
            }
            
            if (args.length != 1) {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc off").send(sender);
                return true;
            }
            
            FlyingCarpet.getMainConfig().reloadConfiguration();
            
            Messages.get("message.reload").send(sender);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("tools")) {
            if (!p.hasPermission("flyingcarpet.tools")) {
                Messages.get("error.no-permissions.tools").send(sender);
                return false;
            }
            
            if (args.length != 1 && args.length != 2) {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc tools [on:off]").send(sender);
                return true;
            }
            
            if (args.length == 1) {
                up.setTools(!up.isTools());
                
                if (up.isTools())
                    Messages.get("message.tools-on").send(sender);
                else
                    Messages.get("message.tools-off").send(sender);
                
                return true;
            }
            
            if (args[1].equalsIgnoreCase("on")) {
                up.setTools(true);
                
                Messages.get("message.tools-on").send(sender);
                return true;
            }
            
            if (args[1].equalsIgnoreCase("off")) {
                up.setTools(false);
                
                Messages.get("message.tools-off").send(sender);
                return true;
            }
            
            Messages.get("error.invalid-arguments").argument("%valid%", "/mc tools [on:off]").send(sender);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("light")) {
            if (!p.hasPermission("flyingcarpet.light")) {
                Messages.get("error.no-permissions.light").send(sender);
                return true;
            }
            
            if (args.length != 1 && args.length != 2) {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc light [on:off]").send(sender);
                return true;
            }
            
            if (args.length == 1) {
                up.setLight(!up.isLight());
                
                if (up.isLight())
                    Messages.get("message.light-on").send(sender);
                else
                    Messages.get("message.light-off").send(sender);
                
                return true;
            }
            
            if (args[1].equalsIgnoreCase("on")) {
                up.setLight(true);
                
                Messages.get("message.light-on").send(sender);
                return true;
            }
            
            if (args[1].equalsIgnoreCase("off")) {
                up.setLight(false);
                
                Messages.get("message.light-off").send(sender);
                return true;
            }
            
            Messages.get("error.invalid-arguments").argument("%valid%", "/mc light [on:off]").send(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("togglemessage")) {
            if (args.length != 1 && args.length != 2) {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc togglemessage [on:off]").send(sender);
                return true;
            }

            MessagePreferences preferences = FlyingCarpet.getMessagePreferences();
            boolean enabled;

            if (args.length == 1) {
                enabled = preferences.toggle(p.getUniqueId());
            } else if (args[1].equalsIgnoreCase("on")) {
                enabled = true;
                preferences.setEnabled(p.getUniqueId(), true);
            } else if (args[1].equalsIgnoreCase("off")) {
                enabled = false;
                preferences.setEnabled(p.getUniqueId(), false);
            } else {
                Messages.get("error.invalid-arguments").argument("%valid%", "/mc togglemessage [on:off]").send(sender);
                return true;
            }

            if (enabled)
                Messages.get("message.togglemessage-on").send(sender);
            else
                Messages.get("message.togglemessage-off").send(sender);

            return true;
        }

        String arguments = "on:off:tools:light:togglemessage";

        if(FlyingCarpet.isWorldGuardHooked() && p.hasPermission("flyingcarpet.worldguard")) {
            arguments += ":worldguard";
        }

        Messages.get("error.invalid-arguments").argument("%valid%", "/mc [" + arguments + "]").send(sender);
        return true;
    }

    private static void sendToggleMessage(Player player, String key) {
        if (!FlyingCarpet.getMessagePreferences().isEnabled(player.getUniqueId()))
            return;

        Messages.get(key).send(player);
    }
}
