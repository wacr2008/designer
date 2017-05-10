package com.fr.design.mainframe.alphafine;

import com.fr.general.SiteCenter;

import java.awt.*;

/**
 * Created by XiaXiang on 2017/5/10.
 */
public class AlphaFineConstants {
    public static final String SAVE_FILE_NAME = "alpha.coco";

    public static final int SHOW_SIZE = 5;

    public static final int LATEST_SHOW_SIZE = 3;

    public static final int HEIGHT = 680;

    public static final int WIDTH = 460;

    public static final int LEFT_WIDTH = 300;

    public static final int RIGHT_WIDTH = 380;

    public static final int FIELD_HEIGHT = 55;

    public static final int CONTENT_HEIGHT = 405;

    public static final int CELL_HEIGHT = 32;


    public static final Dimension FULL_SIZE = new Dimension(680, 460);

    public static final Dimension CONTENT_SIZE = new Dimension(680, 405);

    public static final Dimension FIELD_SIZE = new Dimension(680, 55);

    public static final Dimension ICON_LABEL_SIZE = new Dimension(64, 64);

    public static final Dimension CLOSE_BUTTON_SIZE = new Dimension(40, 40);

    public static final Color GRAY = new Color(0xd2d2d2);

    public static final Color BLUE = new Color(0x3394f0);

    public static final Color BLACK = new Color(0x222222);



    public static final String PLUGIN_SEARCH_URL = SiteCenter.getInstance().acquireUrlByKind("plugin.searchAPI");

    public static final String PLUGIN_IMAGE_URL = "http://shop.finereport.com/plugin/";

    public static final String REUSE_IMAGE_URL = "http://shop.finereport.com/reuse/";

    public static final String DOCUMENT_SEARCH_URL = "http://help.finereport.com/doc-view-";


}
