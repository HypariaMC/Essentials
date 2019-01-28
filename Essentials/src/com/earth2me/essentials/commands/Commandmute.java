package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import net.ess3.api.events.MuteStatusChangeEvent;
import org.bukkit.Server;

import java.util.List;
import java.util.logging.Level;


public class Commandmute extends EssentialsCommand {
    public Commandmute() {
        super("mute");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        boolean nomatch = false;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        User user;
        try {
            user = getPlayer(server, args, 0, true, true);
        } catch (PlayerNotFoundException e) {
            nomatch = true;
            user = ess.getUser(new OfflinePlayer(args[0], ess.getServer()));
        }
        if (!user.getBase().isOnline()) {
            if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.mute.offline")) {
                throw new Exception(sender.tl("muteExemptOffline"));
            }
        } else {
            if (user.isAuthorized("essentials.mute.exempt") && sender.isPlayer()) {
                throw new Exception(sender.tl("muteExempt"));
            }
        }
        
        final boolean willMute = (args.length > 1) ? true : !user.getMuted();
        final User controller = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
        final MuteStatusChangeEvent event = new MuteStatusChangeEvent(user, controller, willMute);
        ess.getServer().getPluginManager().callEvent(event);
        
        if (!event.isCancelled()) {
            long muteTimestamp = 0;

            if (args.length > 1) {
                final String time = getFinalArg(args, 1);
                muteTimestamp = DateUtil.parseDateDiff(time, true);
                user.setMuted(true);
            } else {
                user.setMuted(!user.getMuted());
            }
            user.setMuteTimeout(muteTimestamp);
            final boolean muted = user.getMuted();
            String muteTime = DateUtil.formatDateDiff(sender, muteTimestamp);

            if (nomatch) {
                sender.sendTl("userUnknown", user.getName());
            }

            if (muted) {
                if (muteTimestamp > 0) {
                    sender.sendTl("mutedPlayerFor", user.getDisplayName(), muteTime);
                    user.sendTl("playerMutedFor", muteTime);
                } else {
                    sender.sendTl("mutedPlayer", user.getDisplayName());
                    user.sendTl("playerMuted");
                }
                final String message;
                if (muteTimestamp > 0) {
                    message = user.tl("muteNotifyFor", sender.getSender().getName(), user.getName(), muteTime);
                } else {
                    message = user.tl("muteNotify", sender.getSender().getName(), user.getName());
                }
                server.getLogger().log(Level.INFO, message);
                ess.broadcastMessage("essentials.mute.notify", message);
            } else {
                sender.sendTl("unmutedPlayer", user.getDisplayName());
                user.sendTl("playerUnmuted");
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return COMMON_DATE_DIFFS; // Date diff can span multiple words
        }
    }
}
