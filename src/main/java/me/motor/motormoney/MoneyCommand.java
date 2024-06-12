package me.motor.motormoney;

import org.bukkit.command.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.motor.motormoney.MotorMoney;
import me.motor.motormoney.VaultHook;

public class MoneyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§f§l[ §e§oMONEY §f§l] §c유저에 의해서만 사용 가능한 명령어입니다.");
            return false;
        }
        Player player = (Player) sender;
        String namePlayer = player.getName();

        if (args.length == 0) {
            sender.sendMessage("§f§l[ §e§oMONEY §f§l] §f돈 명령어 모음");
            sender.sendMessage("§a/돈 확인 [닉네임]");
            sender.sendMessage("§a/돈 송금 [닉네임] [돈]");
            if (sender.hasPermission("motor.money.op")) {
                sender.sendMessage(" ");
                sender.sendMessage("§f§l[ §e§oMONEY §f§l] §c돈 관리자 명령어 모음");
                sender.sendMessage("§c/돈 설정 [닉네임] [돈]");
                sender.sendMessage("§c/돈 추가 [닉네임] [돈]");
                sender.sendMessage("§c/돈 삭감 [닉네임] [돈]");
            }
            int balance = (int) MotorMoney.getEconomy().getBalance(player);
            sender.sendMessage("§f§l[ §e§oMONEY §f§l] §f현재 보유 금액은 §e" + balance + "§f원입니다.");
            return false;
        }
        if (args.length == 1) {
            sender.sendMessage("§c§l× §c플레이어 이름을 입력해주세요.");
            return false;
        }
        double amount;
        int amountint;
        if (args.length >= 2) {
            String nameRecipient = args[1];
            Player recipient = Bukkit.getPlayer(nameRecipient);
            if (recipient == null) {
                sender.sendMessage("§c§l× §c플레이어 이름을 잘못 입력했거나 플레이어는 존재하지 않습니다.");
                return false;
            }
            int recipientbalance = (int) MotorMoney.getEconomy().getBalance(recipient);
            int payerBalance = (int) MotorMoney.getEconomy().getBalance(player);
            switch (args[0]) {
                case "확인":
                    if (args.length < 2) {
                        sender.sendMessage("§c§l× §c/돈 확인 [닉네임]");
                        return false;
                    }

                    sender.sendMessage("§f§l[ §e§oMONEY §f§l] §a" + nameRecipient + "§f님의 보유 금액은 §e" + recipientbalance + "§f원입니다.");
                    return true;
                case "송금":
                    if (args.length != 3) {
                        sender.sendMessage("§c§l× §c/돈 송금 [닉네임] [돈]");
                        return false;
                    }
                    try {
                        amount = Double.parseDouble(args[2]);
                        amountint = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§c§l× §c정수만 입력해주세요.");
                        return false;
                    }
                    if (amount > payerBalance) {
                        sender.sendMessage("§c§l× §c보유 금액이 부족합니다.");
                        return false;
                    }
                    MotorMoney.getEconomy().withdrawPlayer(player, amount);
                    MotorMoney.getEconomy().depositPlayer(recipient, amount);
                    sender.sendMessage("§f§l[ §e§oMONEY §f§l] §a" + nameRecipient + "§f님께 §e" + amountint + "§f원을 송금했습니다.");
                    recipient.sendMessage("§f§l[ §e§oMONEY §f§l] §a" + namePlayer + "§f님으로부터 §e" + amountint + "§f원을 송금받았습니다.");
                    return true;
                case "설정":
                    if (!(sender.hasPermission("motor.money.op"))) {
                        sender.sendMessage("§c§l× §c권한이 없습니다.");
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage("§c§l× §c/돈 설정 [닉네임] [돈]");
                        return false;
                    }
                    try {
                        amount = Double.parseDouble(args[2]);
                        amountint = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§c§l× §c정수만 입력해주세요.");
                        return false;
                    }
                    MotorMoney.getEconomy().withdrawPlayer(recipient, MotorMoney.getEconomy().getBalance(recipient));
                    MotorMoney.getEconomy().depositPlayer(recipient, amount);
                    sender.sendMessage("§f§l[ §e§oMONEY §f§l] §a" + nameRecipient + "§f님의 보유 금액을 §e" + amountint + "§f원으로 설정했습니다.");
                    return true;
                case "삭감":
                    if (!(sender.hasPermission("motor.money.op"))) {
                        sender.sendMessage("§c§l× §c권한이 없습니다.");
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage("§c§l× §c/돈 삭감 [닉네임] [돈]");
                        return false;
                    }
                    try {
                        amount = Double.parseDouble(args[2]);
                        amountint = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§c§l× §c정수만 입력해주세요.");
                        return false;
                    }
                    if (amount > recipientbalance) {
                        sender.sendMessage("§c§l× §c삭감할 금액이 해당 유저가 가진 금액보다 많습니다.");
                        return false;
                    }
                    MotorMoney.getEconomy().withdrawPlayer(recipient, amount);
                    sender.sendMessage("§f§l[ §e§oMONEY §f§l] §a" + nameRecipient + "§f님의 보유 금액에서 §e" + amountint + "§f원을 삭감했습니다.");
                    return true;
                case "추가":
                    if (!(sender.hasPermission("motor.money.op"))) {
                        sender.sendMessage("§c§l× §c권한이 없습니다.");
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage("§c§l× §c/돈 추가 [닉네임] [돈]");
                        return false;
                    }
                    try {
                        amount = Double.parseDouble(args[2]);
                        amountint = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§c§l× §c정수만 입력해주세요.");
                        return false;
                    }
                    MotorMoney.getEconomy().depositPlayer(recipient, amount);
                    sender.sendMessage("§f§l[ §e§oMONEY §f§l] §a" + nameRecipient + "§f님의 보유 금액에 §e" + amountint + "§f원을 추가했습니다.");
                    return true;
                default:
                    sender.sendMessage("§f§l[ §e§oMONEY §f§l] §c명령어 인수가 부족합니다. /돈");
                    return false;
            }
        }


        return false;
    }
}
