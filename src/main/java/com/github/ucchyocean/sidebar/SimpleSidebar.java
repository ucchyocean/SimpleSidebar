package com.github.ucchyocean.sidebar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * シンプルサイドバープラグイン
 * @author ucchy
 */
public class SimpleSidebar extends JavaPlugin {

    private static final String SIDEBAR_NAME = "simplesidebar";
    
    private Scoreboard scoreboard;
    private Objective objective;

    /**
     * サーバー開始時に呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        scoreboard = getServer().getScoreboardManager().getMainScoreboard();
        objective = scoreboard.registerNewObjective(SIDEBAR_NAME, "dummy");
        objective.setDisplayName("Sidebar");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    /**
     * サーバー終了時に呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {
        objective.unregister();
    }

    /**
     * プラグインのコマンドが実行されたときに呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command,
            String label, String[] args) {

        if ( args.length >= 2 && args[0].equalsIgnoreCase("title") ) {
            
            String title = replaceColorCode(args[1]);
            
            if ( title.length() > 32 ) {
                sender.sendMessage("タイトルは32文字以下にしてください。");
                return true;
            }
            
            setTitle(title);
            sender.sendMessage("タイトルを\"" + title + "\"に設定しました。");
            return true;
            
        } else if ( args.length >= 3 && args[0].equalsIgnoreCase("set") ) {
            
            String name = replaceColorCode(args[1]);
            
            if ( name.length() > 16 ) {
                sender.sendMessage("項目名は16文字以下にしてください。");
                return true;
            }
            
            if ( !tryParseInt(args[2]) ) {
                sender.sendMessage("コマンドの指定形式が正しくありません。");
                sender.sendMessage("Usage: /" + label + " set (name) (point)");
                return true;
            }
            
            int point = Integer.parseInt(args[2]);
            setScore(name, point);
            sender.sendMessage("項目\"" + name + "\"のスコアを設定しました。");
            return true;
            
        } else if ( args.length >= 3 && args[0].equalsIgnoreCase("add") ) {
            
            String name = replaceColorCode(args[1]);
            
            if ( name.length() > 16 ) {
                sender.sendMessage("項目名は16文字以下にしてください。");
                return true;
            }
            
            if ( !tryParseInt(args[2]) ) {
                sender.sendMessage("コマンドの指定形式が正しくありません。");
                sender.sendMessage("Usage: /" + label + " add (name) (point)");
                return true;
            }
            
            int amount = Integer.parseInt(args[2]);
            addScore(name, amount);
            sender.sendMessage("項目\"" + name + "\"のスコアを設定しました。");
            return true;
            
        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("remove") ) {
            
            String name = replaceColorCode(args[1]);
            
            removeScore(name);
            sender.sendMessage("項目\"" + name + "\"のスコアを削除しました。");
            return true;
            
        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("removeall") ) {

            removeAllScores();
            sender.sendMessage("全ての項目のスコアを削除しました。");
            return true;
            
        }
        
        return false;
    }
    
    /**
     * サイドバーのタイトルを設定する。
     * @param title タイトル（必ず32文字以下にすること）
     */
    public void setTitle(String title) {
        
        objective.setDisplayName(title);
    }
    
    /**
     * サイドバーの項目のスコアを設定する。
     * 項目が無い場合は、項目を追加して設定する。
     * ただし、全ての項目に0ポイントを設定すると、サイドバーが非表示になってしまうので注意。
     * @param name 項目名（必ず16文字以下にすること）
     * @param point ポイント
     */
    public void setScore(String name, int point) {
        
        OfflinePlayer item = Bukkit.getOfflinePlayer(name);
        objective.getScore(item).setScore(point);
    }
    
    /**
     * サイドバーの項目のスコアを取得する。
     * 項目が無い場合は、0が返される。
     * @param name 項目名
     * @return ポイント
     */
    public int getScore(String name) {
        
        OfflinePlayer item = Bukkit.getOfflinePlayer(name);
        return objective.getScore(item).getScore();
    }
    
    /**
     * サイドバーの項目のスコアを増減する。
     * 項目が無い場合は、項目を追加して設定する。
     * ただし、全ての項目に0ポイントを設定すると、サイドバーが非表示になってしまうので注意。
     * @param name 項目名（必ず16文字以下にすること）
     * @param amount 増減するポイント
     */
    public void addScore(String name, int amount) {
        
        int point = getScore(name);
        setScore(name, point + amount);
    }
    
    /**
     * サイドバーの項目のスコアを削除する。
     * @param name 項目名
     */
    public void removeScore(String name) {
        
        OfflinePlayer item = Bukkit.getOfflinePlayer(name);
        objective.getScore(item).setScore(0);
        scoreboard.resetScores(item);
    }
    
    /**
     * 全ての得点をリセットして、サイドバーを非表示にする。
     */
    public void removeAllScores() {
        
        for ( OfflinePlayer item : scoreboard.getPlayers() ) {
            objective.getScore(item).setScore(0);
            scoreboard.resetScores(item);
        }
    }

    /**
     * 文字列内のカラーコードを置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    private static String replaceColorCode(String source) {

        if ( source == null ) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', source);
    }

    /**
     * 指定された文字列が int型に変換可能かどうかをチェックする
     * @param source 文字列
     * @return 変換可能かどうか
     */
    private static boolean tryParseInt(String source) {
        
        if ( source == null ) {
            return false;
        }
        return source.matches("-?[0-9]{1,9}");
    }
}
