package kd.bettervillagers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/*
 * Contains stuff related with custom command to reload custom trades.
 */
public class RefreshTradesCommand implements ICommand {

	@Override
	public int compareTo(ICommand command) {
		return 0;
	}

	@Override
	public String getName() {
		return "bv reload";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "Reload Better Villagers trades.";
	}

	@Override
	public List<String> getAliases() {
		List<String> list = new ArrayList<String>();
		list.add("bv r");
		return list;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		BVTradesStorage.refreshTrades();
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}
}