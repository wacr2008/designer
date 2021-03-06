package com.fr.van.chart.designer.component;

import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.mainframe.chart.gui.style.ChartFillStylePane;

import com.fr.van.chart.designer.TableLayout4VanChartHelper;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;

/**
 * Created by mengao on 2017/8/17.
 */
public class VanChartFillStylePane extends ChartFillStylePane {

    @Override
    protected JPanel getContentPane () {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double e = TableLayout4VanChartHelper.EDIT_AREA_WIDTH;
        double[] columnSize = {f, e};
        double[] rowSize = {p, p};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Color_Match")),styleSelectBox},
                new Component[]{null,customPane},

        };
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components,rowSize,columnSize);
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,0,0));
        return panel;
    }
    @Override
    public Dimension getPreferredSize() {
        if(styleSelectBox.getSelectedIndex() != styleSelectBox.getItemCount() - 1) {
            return new Dimension(styleSelectBox.getPreferredSize().width, 30);
        }
        return super.getPreferredSize();
    }
}
