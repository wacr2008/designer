package com.fr.design.preview;

import com.fr.design.fun.impl.AbstractPreviewProvider;
import com.fr.locale.InterProviderFactory;


/**
 * @author kerry
 * @date 2018/5/22
 */
public class FormPreview extends AbstractPreviewProvider {
    private static final int PREVIEW_TYPE = 5;

    @Override
    public String nameForPopupItem() {
        return InterProviderFactory.getProvider().getLocText("Fine-Design_Form_M_Form_Preview");
    }

    @Override
    public String iconPathForPopupItem() {
        return  "com/fr/design/images/buttonicon/runs.png";
    }

    @Override
    public String iconPathForLarge() {
        return  "com/fr/design/images/buttonicon/run24.png";
    }

    @Override
    public int previewTypeCode() {
        return PREVIEW_TYPE;
    }

}
