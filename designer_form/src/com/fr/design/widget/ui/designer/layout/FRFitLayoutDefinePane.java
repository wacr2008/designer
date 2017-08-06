package com.fr.design.widget.ui.designer.layout;

import com.fr.design.data.DataCreatorUI;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.designer.creator.XWAbsoluteBodyLayout;
import com.fr.design.designer.creator.XWFitLayout;
import com.fr.design.designer.creator.XWScaleLayout;
import com.fr.design.designer.properties.items.FRFitConstraintsItems;
import com.fr.design.designer.properties.items.FRLayoutTypeItems;
import com.fr.design.designer.properties.items.Item;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.FormDesigner;
import com.fr.design.mainframe.FormSelectionUtils;
import com.fr.design.mainframe.WidgetPropertyPane;
import com.fr.design.widget.ui.designer.AbstractDataModify;
import com.fr.design.widget.ui.designer.component.PaddingBoundPane;
import com.fr.form.ui.Widget;
import com.fr.form.ui.container.WAbsoluteBodyLayout;
import com.fr.form.ui.container.WAbsoluteLayout;
import com.fr.form.ui.container.WBodyLayoutType;
import com.fr.form.ui.container.WFitLayout;
import com.fr.general.FRLogger;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ibm on 2017/8/2.
 */
public class FRFitLayoutDefinePane extends AbstractDataModify<WFitLayout> {
    private XWFitLayout xWFitLayout;
    private WFitLayout wFitLayout;
    private UIComboBox layoutComboBox;
    private UIComboBox adaptComboBox;
    private UISpinner componentIntervel;
    private PaddingBoundPane paddingBound;
    private UITextField background;

    public FRFitLayoutDefinePane(XCreator xCreator) {
        super(xCreator);
        this.xWFitLayout = (XWFitLayout) xCreator;
        wFitLayout = xWFitLayout.toData();
        initComponent();
    }


    public void initComponent() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        JPanel advancePane = createAdvancePane();
        UIExpandablePane advanceExpandablePane = new UIExpandablePane(Inter.getLocText("FR-Designer_Advanced"), 280, 20, advancePane);
        this.add(advanceExpandablePane, BorderLayout.NORTH);
        UIExpandablePane layoutExpandablePane = new UIExpandablePane(Inter.getLocText("FR-Designer_Layout"), 280, 20, createLayoutPane());
        this.add(layoutExpandablePane, BorderLayout.CENTER);
    }

    public JPanel createAdvancePane() {
        JPanel jPanel = FRGUIPaneFactory.createBorderLayout_S_Pane();
        background = new UITextField();
        paddingBound = new PaddingBoundPane();
        JPanel jp2 = TableLayoutHelper.createGapTableLayoutPane(new Component[][]{new Component[]{new UILabel(Inter.getLocText("FR-Designer_Background")), background}}, TableLayoutHelper.FILL_LASTCOLUMN, 18, 7);
        jp2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel.add(paddingBound, BorderLayout.CENTER);
        jPanel.add(jp2, BorderLayout.NORTH);
        return jPanel;
    }

    public JPanel createLayoutPane() {
        JPanel jPanel = FRGUIPaneFactory.createBorderLayout_S_Pane();
        layoutComboBox = initUIComboBox(FRLayoutTypeItems.ITEMS);
        adaptComboBox = initUIComboBox(FRFitConstraintsItems.ITEMS);
        componentIntervel = new UISpinner(0, 100, 1, 0);
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p, p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 1}, {1, 1}, {1, 1}};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Attr_Layout_Type")), layoutComboBox},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Component_Scale")), adaptComboBox},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Component_Interval")), componentIntervel}
        };
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 20, 7);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel.add(panel, BorderLayout.CENTER);

        return jPanel;
    }


    public UIComboBox initUIComboBox(Item[] items) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Item item : items) {
            model.addElement(item);
        }
        return new UIComboBox(model);
    }


    @Override
    public String title4PopupWindow() {
        return "fitLayout";
    }

    @Override
    public void populateBean(WFitLayout ob) {
        background.setText("test");
        paddingBound.populate(ob);
        layoutComboBox.setSelectedIndex(ob.getBodyLayoutType().getTypeValue());
        adaptComboBox.setSelectedIndex(ob.getCompState());
        componentIntervel.setValue(ob.getCompInterval());
    }


    @Override
    public WFitLayout updateBean() {
        WFitLayout layout = (WFitLayout) creator.toData();
        paddingBound.update(layout);
        Item item = (Item) layoutComboBox.getSelectedItem();
        Object value = item.getValue();
        int state = 0;
        if (value instanceof Integer) {
            state = (Integer) value;
        }
        try {
            if (state == WBodyLayoutType.ABSOLUTE.getTypeValue()) {
                WAbsoluteBodyLayout wAbsoluteBodyLayout = new WAbsoluteBodyLayout("body");
                wAbsoluteBodyLayout.setCompState(WAbsoluteLayout.STATE_FIXED);
                Component[] components = xWFitLayout.getComponents();
                xWFitLayout.removeAll();
                XWAbsoluteBodyLayout xwAbsoluteBodyLayout = new XWAbsoluteBodyLayout(wAbsoluteBodyLayout, new Dimension(0, 0));
                xWFitLayout.getLayoutAdapter().addBean(xwAbsoluteBodyLayout, 0, 0);
                for (Component component : components) {
                    XCreator xCreator = (XCreator) component;
                    //部分控件被ScaleLayout包裹着，绝对布局里面要放出来
                    if (xCreator.acceptType(XWScaleLayout.class)) {
                        if (xCreator.getComponentCount() > 0 && ((XCreator) xCreator.getComponent(0)).shouldScaleCreator()) {
                            component = xCreator.getComponent(0);
                            component.setBounds(xCreator.getBounds());
                        }
                    }
                    xwAbsoluteBodyLayout.add(component);
                }
                FormDesigner formDesigner = WidgetPropertyPane.getInstance().getEditingFormDesigner();
                formDesigner.getSelectionModel().setSelectedCreators(
                        FormSelectionUtils.rebuildSelection(xWFitLayout, new Widget[]{wAbsoluteBodyLayout}));
            }
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage());

        }
        //todo 验证下
        layout.setLayoutType(WBodyLayoutType.parse(state));
        layout.setCompState(adaptComboBox.getSelectedIndex());
        layout.setCompInterval((int)componentIntervel.getValue());
        return layout;
    }

    @Override
    public DataCreatorUI dataUI() {
        return null;
    }

}
