/*
 * Copyright(c) 2001-2010, FineReport Inc, All Rights Reserved.
 */
package com.fr.design.designer.properties;

import com.fr.design.designer.creator.XLayoutContainer;
import com.fr.design.designer.creator.XWVerticalBoxLayout;
import com.fr.form.ui.container.WVerticalBoxLayout;

import java.awt.Component;

/**
 * @author richer
 * @since 6.5.3
 */
public class VerticalLayoutConstraints extends HVLayoutConstraints {
	WVerticalBoxLayout layout;
	
    public VerticalLayoutConstraints(XLayoutContainer parent, Component comp) {
		super(parent, comp);
		this.layout = ((XWVerticalBoxLayout) parent).toData();
        this.editor1 = new LayoutConstraintsEditor(layout);
	}

	@Override
    public Object getValue(int row, int column) {
        if (column == 0) {
            switch (row) {
                case 0:
                    return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Layout_Index");
                default:
                    return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Tree_Height");
            }
        } else {
            switch (row) {
                case 0:
                    return layout.getWidgetIndex(widget) + 1;
                default:
                    return layout.getHeightAtWidget(widget);
            }
        }
    }

    @Override
	public boolean setValue(Object value, int row, int column) {
		if (column == 1) {
			XWVerticalBoxLayout wLayout = (XWVerticalBoxLayout) parent;
			switch (row) {
			case 0:
				layout.setWidgetIndex(widget, value == null ? 0 : (((Number) value).intValue() - 1));
				wLayout.convert();
				//TODO
				return true;
			default:
				layout.setHeightAtWidget(widget, value == null ? 0 : ((Number) value).intValue());
				wLayout.recalculateChildrenPreferredSize();
				return true;
			}
		}
		return true;
	}
}
