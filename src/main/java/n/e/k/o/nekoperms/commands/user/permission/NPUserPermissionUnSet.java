package n.e.k.o.nekoperms.commands.user.permission;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import n.e.k.o.nekoperms.api.NekoPermsAPI;
import n.e.k.o.nekoperms.utils.Config;
import n.e.k.o.nekoperms.utils.StringColorUtils;
import n.e.k.o.nekoperms.utils.Utils;
import net.minecraft.command.CommandSource;
import n.e.k.o.nekoperms.api.NekoUser;
import org.apache.logging.log4j.Logger;

public class NPUserPermissionUnSet implements Command<CommandSource>
{

    private final NekoPermsAPI api;
    private final Config config;
    private final Logger logger;

    public NPUserPermissionUnSet(NekoPermsAPI api, Config config, Logger logger)
    {
        this.api = api;
        this.config = config;
        this.logger = logger;
    }

    @Override
    public int run(CommandContext<CommandSource> ctx) throws CommandSyntaxException
    {
        CommandSource source = ctx.getSource();
        if (!Utils.hasPermissionForCommand(source, api, config))
            return SINGLE_SUCCESS;

        String username = ctx.getArgument("username", String.class);
        NekoUser user = Utils.getNekoUserByUsername(username, api);
        if (user == null || !user.loadBlock())
        {
            source.sendFeedback(StringColorUtils.getColoredString("Username not found (not even using the Mojang API)."), true);
            return SINGLE_SUCCESS;
        }

        String node = ctx.getArgument("node", String.class).toLowerCase();

        boolean hasNode = user.hasPermission(node);
        if (!hasNode)
        {
            source.sendFeedback(StringColorUtils.getColoredString("NPUserPermissionUnSet. Username: " + user.getUsernameOrUUID() + ", node: '" + node + "', hasNode: false"), true);
            return SINGLE_SUCCESS;
        }

        user.unsetPermission(node);

        source.sendFeedback(StringColorUtils.getColoredString("NPUserPermissionUnSet. Username: " + user.getUsernameOrUUID() + ", node: '" + node + "'."), true);
        source.sendFeedback(StringColorUtils.getColoredString("Removed permission node, saving user data - please wait..."), true);
        user.saveAsync().thenAcceptAsync(success ->
        {
            if (success)
                source.sendFeedback(StringColorUtils.getColoredString("Saving succeeded :)"), true);
            else
                source.sendFeedback(StringColorUtils.getColoredString("Failed saving data..."), true);
        });

        return SINGLE_SUCCESS;
    }

    public static class NoArgument implements Command<CommandSource>
    {

        private final NekoPermsAPI api;
        private final Config config;
        private final Logger logger;

        public NoArgument(NekoPermsAPI api, Config config, Logger logger)
        {
            this.api = api;
            this.config = config;
            this.logger = logger;
        }

        @Override
        public int run(CommandContext<CommandSource> ctx) throws CommandSyntaxException
        {
            CommandSource source = ctx.getSource();
            if (!Utils.hasPermissionForCommand(source, api, config))
                return SINGLE_SUCCESS;

            String username = ctx.getArgument("username", String.class);
            NekoUser user = Utils.getNekoUserByUsername(username, api);
            if (user == null || !user.loadBlock())
            {
                source.sendFeedback(StringColorUtils.getColoredString("Username not found (not even using the Mojang API)."), true);
                return SINGLE_SUCCESS;
            }

            source.sendFeedback(StringColorUtils.getColoredString("User.Permission.Unset: " + user), true);

            source.sendFeedback(StringColorUtils.getColoredString("[NekoPerms User] Permission Unset commands:"), true);
            source.sendFeedback(StringColorUtils.getColoredString("/np user <username> permission unset <node>"), true);

            return SINGLE_SUCCESS;
        }

    }

}
