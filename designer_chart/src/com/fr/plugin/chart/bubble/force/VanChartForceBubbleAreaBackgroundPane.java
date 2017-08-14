package com.fr.plugin.chart.bubble.force;

import com.fr.design.gui.frpane.AbstractAttrNoScrollPane;
import com.fr.plugin.chart.designer.style.background.VanChartAreaBackgroundPane;

import java.awt.*;

//图表区|绘图区 边框和背景
public class VanChartForceBubbleAreaBackgroundPane extends VanChartAreaBackgroundPane {


    public VanChartForceBubbleAreaBackgroundPane(boolean isPlot, AbstractAttrNoScrollPane parent) {
        super(isPlot, parent);
    }

    @Override
    protected Component[][] initComponents() {
        return new Component[][]{
                new Component[]{chartBorderPane},
                new Component[]{chartBackgroundPane},
        };
    }
}