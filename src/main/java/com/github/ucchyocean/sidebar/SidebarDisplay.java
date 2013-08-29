/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.sidebar;

import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * サイドバー表示をするためのAPIクラス
 * @author ucchy
 */
public class SidebarDisplay {

    private static final String SIDEBAR_NAME = "simplesidebar";
    
    private Server server;
    private Scoreboard scoreboard;
    private Objective objective;
    private ArrayList<SidebarItem> items;
    private boolean isInitialized = false;

    /**
     * コンストラクタ。
     */
    public SidebarDisplay(Server server) {
        this.server = server;
    }
    
    /**
     * 表示の初期化
     * @return 初期化が完了したかどうか
     */
    public boolean init() {

        if ( isInitialized ) {
            return true;
        }
        
        if ( server.getScoreboardManager() == null ) {
            return false;
        } else if ( server.getScoreboardManager().getMainScoreboard() == null ) {
            return false;
        }

        scoreboard = server.getScoreboardManager().getMainScoreboard();
        if ( scoreboard.getObjective(SIDEBAR_NAME) != null ) {
            scoreboard.getObjective(SIDEBAR_NAME).unregister();
        }
        objective = scoreboard.registerNewObjective(SIDEBAR_NAME, "");
        objective.setDisplayName("");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        items = new ArrayList<SidebarItem>();
        
        isInitialized = true;
        
        return true;
    }
    
    /**
     * サイドバーのタイトルを設定する。
     * @param title タイトル（必ず32文字以下にすること）
     */
    public void setTitle(String title) {
        
        if ( !init() ) {
            return;
        }
        
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
        
        if ( !init() ) {
            return;
        }
        
        SidebarItem item = getItemFromName(name);
        if ( item == null ) {
            item = new SidebarItem(name);
            items.add(item);
        }
        
        objective.getScore(item).setScore(point);
    }
    
    /**
     * サイドバーの項目のスコアを取得する。
     * 項目が無い場合は、0が返される。
     * @param name 項目名
     * @return ポイント
     */
    public int getScore(String name) {
        
        if ( !init() ) {
            return 0;
        }
        
        SidebarItem item = getItemFromName(name);
        if ( item == null ) {
            return 0;
        }
        
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
        
        if ( !init() ) {
            return;
        }
        
        SidebarItem item = getItemFromName(name);
        if ( item == null ) {
            return;
        }
        
        objective.getScore(item).setScore(0);
        scoreboard.resetScores(item);
        
        items.remove(item);
    }
    
    /**
     * サイドバーを消去する。
     */
    public void unregister() {
        
        if ( !isInitialized ) {
            return;
        }
        
        for ( SidebarItem item : items ) {
            objective.getScore(item).setScore(0);
            scoreboard.resetScores(item);
        }
        
        objective.unregister();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        
        items = null;
        isInitialized = false;
    }
    
    /**
     * 指定した名前のサイドバー項目を取得する。
     * @param name 項目名
     * @return 項目
     */
    private SidebarItem getItemFromName(String name) {
        
        for ( SidebarItem item : items ) {
            if ( item.getName().equals(name) ) {
                return item;
            }
        }
        
        return null;
    }
}
