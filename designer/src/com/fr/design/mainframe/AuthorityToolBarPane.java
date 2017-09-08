package com.fr.design.mainframe;

import com.fr.base.ConfigManager;
import com.fr.base.ConfigManagerProvider;
import com.fr.base.FRContext;
import com.fr.common.inputevent.InputEventBaseOnOS;
import com.fr.design.beans.BasicBeanPane;
import com.fr.design.file.HistoryTemplateListPane;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.mainframe.toolbar.AuthorityEditToolBarComponent;
import com.fr.design.mainframe.toolbar.ToolBarMenuDock;
import com.fr.design.roleAuthority.RolesAlreadyEditedPane;
import com.fr.design.webattr.ReportWebWidgetConstants;
import com.fr.design.webattr.ToolBarButton;
import com.fr.design.webattr.ToolBarPane;
import com.fr.form.ui.Button;
import com.fr.form.ui.ToolBar;
import com.fr.form.ui.Widget;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.main.TemplateWorkBook;
import com.fr.report.web.Location;
import com.fr.report.web.ToolBarManager;
import com.fr.report.web.WebContent;
import com.fr.stable.ArrayUtils;
import com.fr.web.attr.ReportWebAttr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Author : daisy
 * Date: 13-9-9
 * Time: 下午4:58
 */
public class AuthorityToolBarPane<T extends WebContent> extends BasicBeanPane<ReportWebAttr> implements AuthorityEditToolBarComponent {
    private static final int SMALL_GAP = 13;
    private static final int GAP = 25;
    private static final int PRE_GAP = 9;
    private static final int COMBOX_WIDTH = 144;

    private static final String[] CHOOSEITEM = new String[]{Inter.getLocText("M-Page_Preview"), Inter.getLocText(new String[]{"Face_Write", "PageSetup-Page"}), Inter.getLocText("M-Data_Analysis")};
    private UIComboBox choseComboBox;
    private ToolBarPane toolBarPane;
    private AuthorityEditToolBarPane authorityEditToolBarPane = null;
    private int selectedIndex = -1;
    private UILabel title = null;
    private MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (!toolBarPane.isEnabled()) {
                return;
            }
            java.util.List<ToolBarButton> buttonlists = toolBarPane.getToolBarButtons();
            int oldIndex = selectedIndex;
            selectedIndex = pressButtonIndex(e, buttonlists);
            //实现shift多选
            if (e.isShiftDown()) {
                if (oldIndex == -1) {
                    removeSelection();
                    ((ToolBarButton) e.getSource()).setSelected(true);
                } else {
                    int max = oldIndex >= selectedIndex ? oldIndex : selectedIndex;
                    int min = oldIndex <= selectedIndex ? oldIndex : selectedIndex;
                    for (int i = min; i <= max; i++) {
                        buttonlists.get(i).setSelected(true);
                    }
                }
            } else if (!InputEventBaseOnOS.isControlDown(e)) {
                //实现单选
                removeSelection();
                if (selectedIndex != -1) {
                    ((ToolBarButton) e.getSource()).setSelected(true);
                }
            }
            authorityEditToolBarPane.populate();
            EastRegionContainerPane.getInstance().switchMode(EastRegionContainerPane.PropertyMode.AUTHORITY_EDITION);
            EastRegionContainerPane.getInstance().replaceAuthorityEditionPane(authorityEditToolBarPane);

        }

    };


    private int pressButtonIndex(MouseEvent e, java.util.List<ToolBarButton> buttonlists) {
        if (!(e.getSource() instanceof ToolBarButton)) {
            return -1;
        }
        ToolBarButton button = (ToolBarButton) e.getSource();
        for (int i = 0; i < buttonlists.size(); i++) {
            if (ComparatorUtils.equals(button, buttonlists.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 去掉选择
     */
    public void removeSelection() {
        for (ToolBarButton button : toolBarPane.getToolBarButtons()) {
            button.setSelected(false);
        }
    }


    private ItemListener itemListener = new ItemListener() {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                selectedIndex = -1;
                populateToolBarPane();
                authorityEditToolBarPane = new AuthorityEditToolBarPane(toolBarPane.getToolBarButtons());
                authorityEditToolBarPane.setAuthorityToolBarPane(AuthorityToolBarPane.this);
                EastRegionContainerPane.getInstance().replaceAuthorityEditionPane(authorityEditToolBarPane);
                EastRegionContainerPane.getInstance().replaceConfiguredRolesPane(RolesAlreadyEditedPane.getInstance());
            }
        }
    };

    public AuthorityToolBarPane() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.setBorder(BorderFactory.createEmptyBorder(0, PRE_GAP, 0, 0));
        title = new UILabel(Inter.getLocText(new String[]{"ReportServerP-Toolbar", "Choose_Role"}));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title, 0);
        choseComboBox = new UIComboBox(CHOOSEITEM) {
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                dim.width = COMBOX_WIDTH;
                return dim;
            }
        };
        choseComboBox.addItemListener(itemListener);
        //默认选择第一个
        choseComboBox.setSelectedIndex(0);
        this.add(createGapPanel(SMALL_GAP));
        this.add(choseComboBox);
        toolBarPane = new ToolBarPane();
        toolBarPane.setBorder(null);
        toolBarPane.removeDefaultMouseListener();
        this.add(createGapPanel(GAP));
        this.add(toolBarPane);
        populateDefaultToolBarWidgets();
        populateBean(getReportWebAttr());
        toolBarPane.addAuthorityListener(mouseListener);
        authorityEditToolBarPane = new AuthorityEditToolBarPane(toolBarPane.getToolBarButtons());
        authorityEditToolBarPane.setAuthorityToolBarPane(this);
        checkToolBarPaneEnable();
    }

    private JPanel createGapPanel(final int gap) {
        return new JPanel() {
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                dim.width = gap;
                return dim;
            }
        };
    }


    private void populateToolBarPane() {
        toolBarPane.removeAll();
        populateDefaultToolBarWidgets();
        populateBean(getReportWebAttr());
        toolBarPane.addAuthorityListener(mouseListener);
        toolBarPane.repaint();
        authorityEditToolBarPane = new AuthorityEditToolBarPane(toolBarPane.getToolBarButtons());
        checkToolBarPaneEnable();
    }

    /**
     * 使用普通用户远程设计时，如果工具栏使用的是“采用服务器设置”，则工具栏按钮为灰不可用
     */
    private void checkToolBarPaneEnable() {
        List<ToolBarButton> toolBarButtons = toolBarPane.getToolBarButtons();
        boolean isnotEnable = ComparatorUtils.equals(title.getText(), Inter.getLocText(new String[]{"Server", "ReportServerP-Toolbar", "Choose_Role"}))
                && !FRContext.getCurrentEnv().isRoot();
        for (ToolBarButton button : toolBarButtons) {
            button.setEnabled(!isnotEnable);
        }
        toolBarPane.setEnabled(!isnotEnable);
    }

    /**
     * 更新权限工具栏面板
     */
    public void populateAuthority() {
        toolBarPane.repaint();
    }


    private ReportWebAttr getReportWebAttr() {
        JTemplate editingTemplate = HistoryTemplateListPane.getInstance().getCurrentEditingTemplate();
        if (!editingTemplate.isJWorkBook()) {
            return null;
        }
        JWorkBook editingWorkBook = (JWorkBook) editingTemplate;
        TemplateWorkBook wbTpl = editingWorkBook.getTarget();
        return wbTpl.getReportWebAttr();
    }


    //将该报表的设置过权限的属性记录一下
    public void setAuthorityWebAttr(Widget widget, boolean isSelected, String selectedRole) {
        JTemplate editingTemplate = HistoryTemplateListPane.getInstance().getCurrentEditingTemplate();
        if (!editingTemplate.isJWorkBook()) {
            return;
        }
        JWorkBook editingWorkBook = (JWorkBook) editingTemplate;
        TemplateWorkBook wbTpl = editingWorkBook.getTarget();

        ReportWebAttr rw = wbTpl.getReportWebAttr();
        ConfigManagerProvider cm = ConfigManager.getProviderInstance();
        ReportWebAttr webAttr = ((ReportWebAttr) cm.getGlobalAttribute(ReportWebAttr.class));
        if (webAttr == null || rw == null || rw.getWebPage() == null) {
            return;
        }

        //wbTpl.clear先清空
        //再将所有的保存进去
        //看是存在服务器还存在模板里面
        if (choseComboBox.getSelectedIndex() == 0) {
            //分页
            dealWithWebContent(webAttr.getWebPage(), widget, isSelected, selectedRole);
        } else if (choseComboBox.getSelectedIndex() == 1) {
            //填报
            dealWithWebContent(webAttr.getWebWrite(), widget, isSelected, selectedRole);
        } else {
            //view
            dealWithWebContent(webAttr.getWebView(), widget, isSelected, selectedRole);
        }
    }

    private void dealWithWebContent(WebContent wc, Widget widget, boolean isSelected, String selectedRole) {
        ToolBarManager[] managers = wc.getToolBarManagers();
        if (managers == null) {
            return;
        }
        for (int i = 0; i < managers.length; i++) {
            ToolBar tb = managers[i].getToolBar();
            for (int j = 0; j < tb.getWidgetSize(); j++) {
                if (widget instanceof Button && tb.getWidget(j) instanceof Button && ComparatorUtils.equals(((Button) widget).getIconName(), ((Button) tb.getWidget(j)).getIconName())) {
                    if (!isSelected) {
                        tb.getWidget(j).getWidgetPrivilegeControl().addInvisibleRole(selectedRole);
                    } else {
                        tb.getWidget(j).getWidgetPrivilegeControl().removeInvisibleRole(selectedRole);
                    }
                }
            }
        }
        wc.setToolBarManagers(managers);
    }


    public void populateBean(ReportWebAttr reportWebAttr) {
        this.remove(title);
        // 如果是空值就说明采用服务器配置了
        if (reportWebAttr == null || this.getWebContent(reportWebAttr) == null) {
            title = new UILabel(Inter.getLocText(new String[]{"Server", "ReportServerP-Toolbar", "Choose_Role"}));
            populateServerSettings();
            this.add(title, 0);
            return;
        }
        // 模板设置
        T webContent = this.getWebContent(reportWebAttr);
        title = new UILabel(Inter.getLocText(new String[]{"the_template", "ReportServerP-Toolbar", "Choose_Role"}));
        this.add(title, 0);
        populate(webContent.getToolBarManagers());
    }

    public ReportWebAttr updateBean() {
        return null;
    }

    public void populate(ToolBarManager[] toolBarManager) {
        if (ArrayUtils.isEmpty(toolBarManager)) {
            return;
        }
        if (toolBarManager.length == 0) {
            return;
        }
        for (int i = 0; i < toolBarManager.length; i++) {
            toolBarPane.populateBean(toolBarManager[i].getToolBar());
        }
    }


    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.height = ToolBarMenuDock.PANLE_HEIGNT;
        return dim;
    }


    public void populateBean(ToolBarManager[] toolBarManager) {
        if (ArrayUtils.isEmpty(toolBarManager)) {
            return;
        }
        for (int i = 0; i < toolBarManager.length; i++) {
            Location location = toolBarManager[i].getToolBarLocation();
            if (location instanceof Location.Embed) {
                toolBarPane.populateBean(toolBarManager[i].getToolBar());
            }
        }
    }


    private void populateServerSettings() {
        ConfigManagerProvider cm = ConfigManager.getProviderInstance();
        ReportWebAttr webAttr = ((ReportWebAttr) cm.getGlobalAttribute(ReportWebAttr.class));
        if (this.getWebContent(webAttr) != null) {
            populate(this.getWebContent(webAttr).getToolBarManagers());
        }
    }

    protected String title4PopupWindow() {
        return null;
    }


    private T getWebContent(ReportWebAttr reportWebAttr) {
        if (choseComboBox.getSelectedIndex() == 0) {
            return reportWebAttr == null ? null : (T) reportWebAttr.getWebPage();
        } else if (choseComboBox.getSelectedIndex() == 1) {
            return reportWebAttr == null ? null : (T) reportWebAttr.getWebWrite();
        } else {
            return reportWebAttr == null ? null : (T) reportWebAttr.getWebView();
        }

    }

    private void populateDefaultToolBarWidgets() {
        if (choseComboBox.getSelectedIndex() == 0) {
            ReportWebWidgetConstants.getPageToolBarInstance();
        } else if (choseComboBox.getSelectedIndex() == 1) {
            ReportWebWidgetConstants.getWriteToolBarInstance();
        } else {
            ReportWebWidgetConstants.getViewToolBarInstance();
        }
    }

    private ToolBarManager getDefaultToolBarManager() {
        if (choseComboBox.getSelectedIndex() == 0) {
            return ToolBarManager.createDefaultToolBar();
        } else if (choseComboBox.getSelectedIndex() == 1) {
            return ToolBarManager.createDefaultWriteToolBar();
        } else {
            return ToolBarManager.createDefaultViewToolBar();
        }

    }


}