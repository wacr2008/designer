package com.fr.design.actions.cell.style;

import com.fr.base.BaseUtils;
import com.fr.base.Style;
import com.fr.base.chart.BaseChartCollection;
import com.fr.design.actions.ButtonGroupAction;
import com.fr.design.actions.utils.ReportActionUtils;
import com.fr.design.constants.UIConstants;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ibutton.UIToggleButton;
import com.fr.design.mainframe.ElementCasePane;
import com.fr.general.Inter;
import com.fr.grid.selection.FloatSelection;
import com.fr.grid.selection.Selection;
import com.fr.report.cell.FloatElement;
import com.fr.report.elementcase.TemplateElementCase;
import com.fr.stable.Constants;

import javax.swing.*;


public class AlignmentAction extends ButtonGroupAction implements StyleActionInterface {

    private static final Icon[] normalBlackIcon = new Icon[]{
            BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_left_normal.png"),
            BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_center_normal.png"),
            BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_right_normal.png")};

    private static final Icon[] toggleWhiteIcon = new Icon[]{
            BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_left_normal_white.png"),
            BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_center_normal_white.png"),
            BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_right_normal_white.png")};

    private static final Integer[] valueArray = new Integer[]{Constants.LEFT, Constants.CENTER, Constants.RIGHT};

    public AlignmentAction(ElementCasePane t) {
        super(t, normalBlackIcon, valueArray);
    }


    /**
     * executeStyle
     *
     * @param style
     * @param selectedStyle
     * @return style
     */
    public Style executeStyle(Style style, Style selectedStyle) {
        return style.deriveHorizontalAlignment(getSelectedValue());
    }

    /**
     * 更新Style
     *
     * @param style style
     */
    public void updateStyle(Style style) {
        UIButtonGroup<Integer> buttonGroup = createToolBarComponent();
        int selected = BaseUtils.getAlignment4Horizontal(style);
        for (int i = 0; i < normalBlackIcon.length; i++) {
            UIToggleButton button = buttonGroup.getButton(i);
            Icon currentIcon = button.getIcon();
            Icon newIcon = selected == valueArray[i] ? toggleWhiteIcon[i] : normalBlackIcon[i];
            if (newIcon != currentIcon) {
                button.setIcon(newIcon);
            }
        }
        setSelectedIndex(selected);
    }

    /**
     * executeActionReturnUndoRecordNeeded
     *
     * @return
     */
    public boolean executeActionReturnUndoRecordNeeded() {
        ElementCasePane reportPane = this.getEditingComponent();
        if (reportPane == null) {
            return false;
        }

        return ReportActionUtils.executeAction(this, reportPane);
    }

    /**
     * update
     */
    public void update() {
        super.update();

        //peter:如果当前没有ReportFrame,不需要继续.
        if (!this.isEnabled()) {
            return;
        }

        //got simple cell element from row and column
        ElementCasePane reportPane = this.getEditingComponent();
        if (reportPane == null) {
            this.setEnabled(false);
            return;
        }
        Selection cs = reportPane.getSelection();
        TemplateElementCase tplEC = reportPane.getEditingElementCase();

        if (tplEC != null && cs instanceof FloatSelection) {
            FloatElement selectedFloat = tplEC.getFloatElement(((FloatSelection) cs).getSelectedFloatName());
            Object value = selectedFloat.getValue();
            if (value instanceof BaseChartCollection) {
                this.setEnabled(false);
                return;
            }
        }
        this.updateStyle(ReportActionUtils.getCurrentStyle(reportPane));
    }

    /**
     * 创建工具条，且有提示
     *
     * @return
     */
    public UIButtonGroup<Integer> createToolBarComponent() {
        UIButtonGroup<Integer> group = super.createToolBarComponent();
        if (group != null) {
            group.setForToolBarButtonGroup(true);
            group.setAllToolTips(new String[]{Inter.getLocText("StyleAlignment-Left"), Inter.getLocText("Center"), Inter.getLocText("StyleAlignment-Right")});
        }
        for (int i = 0; i < 3; i++) {
            group.getButton(i).setRoundBorder(true, UIConstants.ARC);
            group.getButton(i).setBorderPainted(true);
        }
        return group;
    }

}