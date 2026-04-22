package ca.risingvocal.lbpcore.commands;

import ca.risingvocal.lbpcore.LBPCore;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class LBPCoreCommand {
    private final LBPCore plugin;
    public LBPCoreCommand(LBPCore plugin) {this.plugin = plugin;}

    public LiteralArgumentBuilder<CommandSourceStack> getTree() {
        return Commands.literal("lbpcore")
                .then(subReload())
                .executes(ctx -> {
                    ctx.getSource().getSender().sendRichMessage("<green>LBPCore | <dark_green>Version: <yellow>" + plugin.getPluginMeta().getVersion());
                    return 1;
                });
    }

    public LiteralArgumentBuilder<CommandSourceStack> subReload() {
        return Commands.literal("reload")
                .requires(sender -> sender.getSender().hasPermission("lbpcore.command.admin.reload"))
                .executes(ctx -> {
                    plugin.reloadPlugin();
                    ctx.getSource().getSender().sendRichMessage("<green>LBPCore | <dark_green>Reloaded!");
                    return 1;
                });
    }

}
