package com.fr.design.actions.help.AlphaFine;

import com.fr.stable.OperatingSystem;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import javax.swing.*;

/**
 * Created by XiaXiang on 2017/4/5.
 */
public class AlphafineConfigManager implements XMLable {

    /**
     * 是否开启alphafine
     */
    private boolean isEnabled = true;

    /**
     * 是否联网搜索
     */
    private boolean isSearchOnLine = true;

    /**
     * 快捷键设置
     */
    private String shortcuts;

    /**
     * 搜索范围
     */

    /**
     * 猜您需要
     */
    private boolean isContainRecommend;

    /**
     * 设置
     */
    private boolean isContainAction = true;

    /**
     * 帮助文档
     */
    private boolean isContainDocument = true;

    /**
     * 模板
     */
    private boolean isContainTemplate = true;

    /**
     * 模板内容
     */
    private boolean isContainFileContent;

    /**
     * 应用中心
     */
    private boolean isContainPlugin = true;

    /**
     * 快捷键
     */
    private KeyStroke shortCutKeyStore;

    /**
     * 是否提醒
     */
    private boolean isNeedRemind = true;

    /**
     * 直接操作菜单次数
     */
    private int operateCount;




    private static AlphafineConfigManager alphafineConfigManager = new AlphafineConfigManager();

    public static AlphafineConfigManager getInstance() {
        return alphafineConfigManager;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.setEnabled(reader.getAttrAsBoolean("isEnabled", true));
            this.setContainPlugin(reader.getAttrAsBoolean("isContainDocument", true));
            this.setContainDocument(reader.getAttrAsBoolean("isContainDocument", true));
            this.setContainRecommend(reader.getAttrAsBoolean("isContainRecommend", true));
            this.setContainAction(reader.getAttrAsBoolean("isContainAction", true));
            this.setContainTemplate(reader.getAttrAsBoolean("isContainTemplate", true));
            this.setContainFileContent(reader.getAttrAsBoolean("isContainFileContent", false));
            this.setShortcuts(reader.getAttrAsString("shortcuts", getDefaultShortCuts()));
            this.setNeedRemind(reader.getAttrAsBoolean("isNeedRemind", true));
            this.setOperateCount(reader.getAttrAsInt("operateCount", 0));

        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG("AlphaFineConfigManager");
        writer.attr("isEnabled", this.isEnabled())
                .attr("isSearchOnline", this.isSearchOnLine())
                .attr("shortcuts", this.getShortcuts())
                .attr("isContainRecommend", this.isContainRecommend())
                .attr("isContainAction", this.isContainAction())
                .attr("isContainDocument", this.isContainDocument())
                .attr("isContainTemplate", this.isContainTemplate())
                .attr("isContainPlugin", this.isContainPlugin())
                .attr("isContainFileContent", this.isContainFileContent())
                .attr("isNeedRemind", this.isNeedRemind())
                .attr("operateCount", this.getOperateCount());
        writer.end();
    }



    public boolean isSearchOnLine() {
        return isSearchOnLine;
    }

    public void setSearchOnLine(boolean searchOnLine) {
        isSearchOnLine = searchOnLine;
    }

    public String getShortcuts() {
        if (StringUtils.isBlank(shortcuts)) {
            return getDefaultShortCuts();
        }
        return shortcuts;
    }

    /**
     * 返回默认快捷键
     * @return
     */
    private String getDefaultShortCuts() {
        return OperatingSystem.isMacOS()? "meta + F" : "ctrl + F";
    }

    public void setShortcuts(String shortcuts) {
        this.shortcuts = shortcuts;
        this.shortCutKeyStore = convert2KeyStroke(this.shortcuts);
    }


    public boolean isContainAction() {
        return isContainAction;
    }

    public void setContainAction(boolean containAction) {
        this.isContainAction = containAction;
    }

    public boolean isContainDocument() {
        return isContainDocument;
    }

    public void setContainDocument(boolean containDocument) {
        this.isContainDocument = containDocument;
    }

    public boolean isContainTemplate() {
        return isContainTemplate;
    }

    public void setContainTemplate(boolean containTemplate) {
        this.isContainTemplate = containTemplate;
    }

    public boolean isContainPlugin() {
        return isContainPlugin;
    }

    public void setContainPlugin(boolean containPlugin) {
        this.isContainPlugin = containPlugin;
    }

    public boolean isContainRecommend() {
        return isContainRecommend;
    }

    public void setContainRecommend(boolean containConclude) {
        isContainRecommend = containConclude;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public KeyStroke getShortCutKeyStore() {
        if (shortCutKeyStore == null) {
            shortCutKeyStore = convert2KeyStroke(this.getShortcuts());
        }
        return shortCutKeyStore;
    }

    public void setShortCutKeyStore(KeyStroke shortCutKeyStore) {
        this.shortCutKeyStore = shortCutKeyStore;
    }

    private KeyStroke convert2KeyStroke(String ks) {
        return KeyStroke.getKeyStroke(ks.replace("+", "pressed"));
    }

    public boolean isContainFileContent() {
        return isContainFileContent;
    }

    public void setContainFileContent(boolean containFileContent) {
        isContainFileContent = containFileContent;
    }

    public boolean isNeedRemind() {
        return isNeedRemind;
    }

    public void setNeedRemind(boolean needRemind) {
        isNeedRemind = needRemind;
    }

    public int getOperateCount() {
        return operateCount;
    }

    public void setOperateCount(int operateCount) {
        this.operateCount = operateCount;
    }
}
