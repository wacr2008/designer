package com.fr.design.mainframe.chart.gui.style.axis;

import com.fr.chart.chartattr.Axis;
import com.fr.chart.chartattr.ValueAxis;
import com.fr.design.gui.icheckbox.UICheckBox;


import javax.swing.*;
import java.awt.*;

/**
 * 属性表, 坐标轴. 第二值轴界面.
 * @author kunsnat E-mail:kunsnat@gmail.com
 * @version 创建时间：2013-1-4 下午05:41:11
 */
public class ChartSecondValuePane extends ChartValuePane {

	private UICheckBox isAlignZeroValue;

	protected JPanel aliagnZero4Second() {// 添加 0值对齐
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane.add(isAlignZeroValue = new UICheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Align_Origin"), false));
		return pane;
	}

    public void populateBean(Axis axis) {
        if (axis instanceof ValueAxis) {
            super.populateBean(axis);
            isAlignZeroValue.setSelected(((ValueAxis)axis).isAlignZeroValue());
        }
    }

    public void updateBean(Axis axis) {
    	if(axis instanceof ValueAxis) {
    		ValueAxis valueAxis = (ValueAxis)axis;
    		super.updateBean(valueAxis);
    		valueAxis.setAlignZeroValue(isAlignZeroValue.isSelected());
    	}
    }

    /**
     * 界面标题 第二值轴
     * @return 第二值轴
     */
	public String title4PopupWindow() {
		return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Second_Value_Axis");
	}
}