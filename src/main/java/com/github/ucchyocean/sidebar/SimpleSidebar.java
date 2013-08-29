/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.sidebar;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * シンプルサイドバープラグイン
 * @author ucchy
 */
public class SimpleSidebar extends JavaPlugin {

    private static final String PREFIX = "[SS]";
    private static final String PREERR = ChatColor.RED + "[SS]";
    
    private static final String REGEX_PARSEINT_CHECK = "-?[0-9]{1,9}";
    
    private SidebarDisplay display;

    /**
     * サーバー開始時に呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        display = new SidebarDisplay(getServer());
    }
    
    /**
     * サーバー終了時に呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {
        display.unregister();
    }

    /**
     * プラグインのコマンドが実行されたときに呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command,
            String label, String[] args) {

        // 引数0はここで終わる
        if ( args.length == 0 ) {
            return false;
        }
        
        // removeall が指定されている場合
        if ( args[0].equalsIgnoreCase("removeall") ) {

            display.unregister();
            sender.sendMessage(PREFIX + 
                    "サイドバーを削除しました。");
            return true;
            
        }
        
        // 以降は引数2つ以上必須なので、引数1つの場合はここで終わる。
        if ( args.length <= 1 ) {
            return false;
        }
        
        if ( args[0].equalsIgnoreCase("title") ) {
            
            String title = replaceColorCode(args[1]);
            
            if ( title.length() > 32 ) {
                sender.sendMessage(PREERR + 
                        "タイトルは32文字以下にしてください。");
                return true;
            }
            
            display.setTitle(title);
            sender.sendMessage(PREFIX + 
                    "タイトルを\"" + title + "\"に設定しました。");
            return true;
            
        } else if ( args[0].equalsIgnoreCase("set") ) {
            
            String name = replaceColorCode(args[1]);
            
            if ( name.length() > 16 ) {
                sender.sendMessage(PREERR + 
                        "項目名は16文字以下にしてください。");
                return true;
            }
            
            int point = 0;
            if ( args.length >= 3 && args[2].matches(REGEX_PARSEINT_CHECK) ) {
                point = Integer.parseInt(args[2]);
            }
            display.setScore(name, point);
            sender.sendMessage(PREFIX + 
                    "項目\"" + name + "\"のスコアを設定しました。");
            return true;
            
        } else if ( args[0].equalsIgnoreCase("add") ) {
            
            String name = replaceColorCode(args[1]);
            
            if ( name.length() > 16 ) {
                sender.sendMessage(PREERR + 
                        "項目名は16文字以下にしてください。");
                return true;
            }
            
            int amount = 0;
            if ( args.length >= 3 && args[2].matches(REGEX_PARSEINT_CHECK) ) {
                amount = Integer.parseInt(args[2]);
            }
            display.addScore(name, amount);
            sender.sendMessage(PREFIX + 
                    "項目\"" + name + "\"のスコアを設定しました。");
            return true;
            
        } else if ( args[0].equalsIgnoreCase("remove") ) {
            
            String name = replaceColorCode(args[1]);
            
            display.removeScore(name);
            sender.sendMessage(PREFIX + 
                    "項目\"" + name + "\"のスコアを削除しました。");
            return true;
            
        }
        
        return false;
    }

    /**
     * 文字列内のカラーコードを置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    private static String replaceColorCode(String source) {

        return source.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }
}
