package me.darkolythe.shulkerpacks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReload implements CommandExecutor {

    private ShulkerPacks main = ShulkerPacks.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(main.prefix + "이 명령어를 사용할 권한이 없습니다.");
            return true;
        }
        if (args.length != 1 || !(args[0].equals("새로고침") || args[0].equalsIgnoreCase("reload"))) {
            sender.sendMessage(main.prefix + "사용법: /편의기능 새로고침");
            return true;
        }
        sender.sendMessage(main.prefix + "내장 설정을 사용 중입니다. 설정 변경은 플러그인을 다시 빌드한 뒤 서버를 재시작하면 적용됩니다.");
        return true;
    }
}
