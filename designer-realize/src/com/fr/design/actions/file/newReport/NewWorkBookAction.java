package com.fr.design.actions.file.newReport;

import com.fr.base.BaseUtils;
import com.fr.design.actions.UpdateAction;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.mainframe.JWorkBook;
import com.fr.design.menu.MenuKeySet;
import com.fr.general.Inter;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;

public class NewWorkBookAction extends UpdateAction {

    public NewWorkBookAction() {
        this.setMenuKeySet(NEW_WORK_BOOK);
        this.setName(getMenuKeySet().getMenuKeySetName());
        this.setMnemonic(getMenuKeySet().getMnemonic());
        this.setSmallIcon(icon());
        this.setAccelerator(getMenuKeySet().getKeyStroke());
    }

    protected Icon icon() {
        return BaseUtils.readIcon("/com/fr/design/images/buttonicon/newcpts.png");
    }

    /**
     * 动作
     *
     * @param e 事件
     */
    public void actionPerformed(ActionEvent e) {
        DesignerContext.getDesignerFrame().addAndActivateJTemplate(new JWorkBook());
    }

    public static final MenuKeySet NEW_WORK_BOOK = new MenuKeySet() {
        @Override
        public char getMnemonic() {
            return 'N';
        }

        @Override
        public String getMenuName() {
            return Inter.getLocText("M-New_WorkBook");
        }

        @Override
        public KeyStroke getKeyStroke() {
            return null;
        }
    };

}