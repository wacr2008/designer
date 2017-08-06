package com.fr.design.widget.ui.designer;

import com.fr.design.designer.creator.XCreator;
import com.fr.design.gui.frpane.RegPane;
import com.fr.form.ui.TextArea;
import com.fr.form.ui.TextEditor;


public class TextAreaDefinePane extends TextFieldEditorDefinePane {
	public TextAreaDefinePane(XCreator xCreator) {
		super(xCreator);
	}
	@Override
	protected TextEditor newTextEditorInstance() {
		return new TextArea();
	}

	protected RegPane createRegPane() {
		return new RegPane(RegPane.TEXTAREA_REG_TYPE);
	}
}