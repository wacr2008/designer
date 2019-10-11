package com.fr.van.chart.bubble;

import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.Plot;
import com.fr.design.beans.BasicBeanPane;
import com.fr.design.condition.ConditionAttributesPane;
import com.fr.design.gui.frpane.AttributeChangeListener;
import com.fr.design.i18n.Toolkit;
import com.fr.design.mainframe.chart.AbstractChartAttrPane;
import com.fr.design.mainframe.chart.gui.ChartDataPane;
import com.fr.design.mainframe.chart.gui.ChartStylePane;
import com.fr.design.mainframe.chart.gui.data.report.AbstractReportDataContentPane;
import com.fr.design.mainframe.chart.gui.data.report.BubblePlotReportDataContentPane;
import com.fr.design.mainframe.chart.gui.data.table.AbstractTableDataContentPane;
import com.fr.design.mainframe.chart.gui.type.AbstractChartTypePane;
import com.fr.plugin.chart.bubble.VanChartBubblePlot;
import com.fr.van.chart.bubble.data.VanChartBubblePlotTableDataContentPane;
import com.fr.van.chart.designer.other.VanChartInteractivePaneWithOutSort;
import com.fr.van.chart.designer.other.VanChartOtherPane;
import com.fr.van.chart.designer.other.zoom.ZoomPane;
import com.fr.van.chart.designer.style.VanChartStylePane;
import com.fr.van.chart.vanchart.AbstractIndependentVanChartUI;

/**
 * Created by Mitisky on 16/3/31.
 */
public class BubbleIndependentVanChartInterface extends AbstractIndependentVanChartUI {
    @Override
    public String getName() {
        return Toolkit.i18nText("Fine-Design_Chart_New_Bubble");
    }

    @Override
    public String[] getSubName() {
        return new String[]{
                Toolkit.i18nText("Fine-Design_Chart_Bubble_Chart"),
                Toolkit.i18nText("Fine-Design_Chart_New_Force_Bubble")
        };
    }

    @Override
    public String[] getDemoImagePath() {
        return new String[]{
                "com/fr/plugin/chart/demo/image/26.png",
                "com/fr/plugin/chart/demo/image/27.png"
        };
    }

    /**
     * 图表的类型定义界面类型，就是属性表的第一个界面
     *
     * @return 图表的类型定义界面类型
     */
    @Override
    public AbstractChartTypePane getPlotTypePane() {
        return new VanChartBubblePlotPane();
    }

    /**
     * 图标路径
     *
     * @return 图标路径
     */
    @Override
    public String getIconPath() {
        return "com/fr/design/images/form/toolbar/bubble.png";
    }

    @Override
    public BasicBeanPane<Plot> getPlotSeriesPane(ChartStylePane parent, Plot plot) {
        return new VanChartBubbleSeriesPane(parent, plot);
    }

    @Override
    public AbstractTableDataContentPane getTableDataSourcePane(Plot plot, ChartDataPane parent) {
        if (((VanChartBubblePlot) plot).isForceBubble()) {
            return super.getTableDataSourcePane(plot, parent);
        }
        return new VanChartBubblePlotTableDataContentPane(parent);
    }

    @Override
    public AbstractReportDataContentPane getReportDataSourcePane(Plot plot, ChartDataPane parent) {
        if (((VanChartBubblePlot) plot).isForceBubble()) {
            return super.getReportDataSourcePane(plot, parent);
        }
        return new BubblePlotReportDataContentPane(parent);
    }

    /**
     * 图表的属性界面数组
     *
     * @return 属性界面
     */
    public AbstractChartAttrPane[] getAttrPaneArray(AttributeChangeListener listener) {
        VanChartStylePane stylePane = new VanChartBubbleStylePane(listener);
        VanChartOtherPane otherPane = new VanChartOtherPane() {
            protected BasicBeanPane<Chart> createInteractivePane() {
                return new VanChartInteractivePaneWithOutSort() {
                    @Override
                    protected ZoomPane createZoomPane() {
                        return new ZoomPane();
                    }

                    @Override
                    protected boolean isCurrentChartSupportLargeDataMode() {
                        return true;
                    }
                };
            }
        };
        return new AbstractChartAttrPane[]{stylePane, otherPane};
    }

    public ConditionAttributesPane getPlotConditionPane(Plot plot) {
        return new VanChartBubbleConditionPane(plot);
    }
}
