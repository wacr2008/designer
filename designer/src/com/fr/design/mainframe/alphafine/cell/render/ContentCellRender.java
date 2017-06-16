package com.fr.design.mainframe.alphafine.cell.render;

import com.fr.design.gui.ilable.UILabel;
import com.fr.design.mainframe.alphafine.AlphaFineConstants;
import com.fr.design.mainframe.alphafine.CellType;
import com.fr.design.mainframe.alphafine.cell.model.AlphaCellModel;
import com.fr.design.mainframe.alphafine.cell.model.MoreModel;
import com.fr.stable.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by XiaXiang on 2017/4/20.
 */
public class ContentCellRender implements ListCellRenderer<Object> {
    private UILabel name;
    private UILabel content;

    public ContentCellRender() {
        this.name = new UILabel();
        this.content = new UILabel();
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof MoreModel) {
            return new TitleCellRender().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);
        if (isSelected) {
            panel.setBackground(AlphaFineConstants.BLUE);
        }
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        AlphaCellModel model = (AlphaCellModel) value;
        name.setText(model.getName());
        String iconUrl = "/com/fr/design/mainframe/alphafine/images/alphafine" + model.getType().getTypeValue() + ".png";
        if (model.getType() == CellType.NO_RESULT) {
            name.setIcon(null);
            name.setForeground(AlphaFineConstants.MEDIUM_GRAY);
        } else {
            name.setIcon(new ImageIcon(getClass().getResource(iconUrl)));
            name.setForeground(AlphaFineConstants.BLACK);
        }
        name.setFont(AlphaFineConstants.MEDIUM_FONT);
        name.setVerticalTextPosition(SwingConstants.CENTER);
        name.setHorizontalTextPosition(SwingConstants.RIGHT);
        String description = model.getDescription();
        if (StringUtils.isNotBlank(description)) {
            content.setText("-" + description);
            content.setForeground(AlphaFineConstants.LIGHT_GRAY);
            panel.add(content, BorderLayout.CENTER);
        }
        panel.add(name, BorderLayout.WEST);
        return panel;
    }
}