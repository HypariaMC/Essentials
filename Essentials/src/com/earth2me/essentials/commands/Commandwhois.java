package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Server;
import org.bukkit.Statistic;

import java.util.Locale;
import java.util.Collections;
import java.util.List;


public class Commandwhois extends EssentialsCommand {
    public Commandwhois() {
        super("whois");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User user = getPlayer(server, sender, args, 0);

        sender.sendTl("whoisTop", user.getName());
        user.setDisplayNick();
        sender.sendTl("whoisNick", user.getDisplayName());
        sender.sendTl("whoisUuid", user.getBase().getUniqueId().toString());
        sender.sendTl("whoisHealth", user.getBase().getHealth());
        sender.sendTl("whoisHunger", user.getBase().getFoodLevel(), user.getBase().getSaturation());
        sender.sendTl("whoisExp", SetExpFix.getTotalExperience(user.getBase()), user.getBase().getLevel());
        sender.sendTl("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ());
        long playtimeMs = System.currentTimeMillis() - (user.getBase().getStatistic(Statistic.PLAY_ONE_TICK) * 50);
        sender.sendTl("whoisPlaytime", DateUtil.formatDateDiff(sender, playtimeMs));
        if (!ess.getSettings().isEcoDisabled()) {
            sender.sendTl("whoisMoney", NumberUtil.displayCurrency(user.getMoney(), ess));
        }
        if (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.whois.ip")) {
            sender.sendTl("whoisIPAddress", user.getBase().getAddress().getAddress().toString());
        }
        final String location = user.getGeoLocation();
        if (location != null && (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendTl("whoisGeoLocation", location);
        }
        sender.sendTl("whoisGamemode", sender.tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH)));
        sender.sendTl("whoisGod", (user.isGodModeEnabled() ? sender.tl("true") : sender.tl("false")));
        sender.sendTl("whoisOp", (user.getBase().isOp() ? sender.tl("true") : sender.tl("false")));
        sender.sendTl("whoisFly", user.getBase().getAllowFlight() ? sender.tl("true") : sender.tl("false"), user.getBase().isFlying() ? sender.tl("flying") : sender.tl("notFlying"));
        if (user.isAfk()) {
            sender.sendTl("whoisAFKSince", sender.tl("true"), DateUtil.formatDateDiff(sender, user.getAfkSince()));
        } else {
            sender.sendTl("whoisAFK", sender.tl("false"));
        }
        sender.sendTl("whoisJail", (user.isJailed() ? user.getJailTimeout() > 0 ? DateUtil.formatDateDiff(sender, user.getJailTimeout()) : sender.tl("true") : sender.tl("false")));
        sender.sendTl("whoisMuted", (user.isMuted() ? user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(sender, user.getMuteTimeout()) : sender.tl("true") : sender.tl("false")));

    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
