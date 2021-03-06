package com.fr.design.javascript;

import com.fr.design.DesignerEnvManager;
import com.fr.design.border.UIRoundedBorder;
import com.fr.design.constants.KeyWords;
import com.fr.design.constants.UIConstants;
import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.autocomplete.AutoCompletion;
import com.fr.design.gui.autocomplete.BasicCompletion;
import com.fr.design.gui.autocomplete.CompletionProvider;
import com.fr.design.gui.autocomplete.DefaultCompletionProvider;
import com.fr.design.gui.autocomplete.ShorthandCompletion;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.syntax.ui.rsyntaxtextarea.RSyntaxTextArea;
import com.fr.design.gui.syntax.ui.rsyntaxtextarea.SyntaxConstants;
import com.fr.design.javascript.beautify.JavaScriptFormatHelper;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.general.IOUtils;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class JSContentPane extends BasicPane {
    private RSyntaxTextArea contentTextArea;
    private UILabel funNameLabel;

    private int titleWidth = 180;

    public JSContentPane(String[] args) {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        funNameLabel = new UILabel();
        this.setFunctionTitle(args);

        UILabel label = new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Format_JavaScript"), IOUtils.readIcon("com/fr/design/images/edit/format.png"), SwingConstants.LEFT);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setToolTipText(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Format_JavaScript"));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        return JavaScriptFormatHelper.beautify(contentTextArea.getText());
                    }

                    @Override
                    protected void done() {
                        try {
                            String text = get();
                            contentTextArea.setText(text);
                        } catch (Exception ignore) {

                        }
                    }
                }.execute();
            }
        });

        //REPORT-10533 用户参数多达25个,导致JS没地方写,增加滚动条显示
        JPanel jsParaPane = new JPanel(new BorderLayout(4, 4));
        jsParaPane.setPreferredSize(new Dimension(300, 80));
        UIScrollPane scrollPane = new UIScrollPane(funNameLabel);
        scrollPane.setPreferredSize(new Dimension(400, 80));
        scrollPane.setBorder(new UIRoundedBorder(UIConstants.TITLED_BORDER_COLOR, 1, UIConstants.ARC));
        jsParaPane.add(scrollPane, BorderLayout.WEST);
        jsParaPane.add(label, BorderLayout.EAST);
        this.add(jsParaPane, BorderLayout.NORTH);

        contentTextArea = new RSyntaxTextArea();
        contentTextArea.setCloseCurlyBraces(true);
        contentTextArea.setLineWrap(true);
        contentTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        contentTextArea.setCodeFoldingEnabled(true);
        contentTextArea.setAntiAliasingEnabled(true);

        CompletionProvider provider = createCompletionProvider();

        AutoCompletion ac = new AutoCompletion(provider);
        String shortCuts = DesignerEnvManager.getEnvManager().getAutoCompleteShortcuts();

        ac.setTriggerKey(convert2KeyStroke(shortCuts));
        ac.install(contentTextArea);

        UIScrollPane sp = new UIScrollPane(contentTextArea);
        this.add(sp, BorderLayout.CENTER);

        UILabel funNameLabel2 = new UILabel();
        funNameLabel2.setText("}");
        this.add(funNameLabel2, BorderLayout.SOUTH);
    }

    private KeyStroke convert2KeyStroke(String ks) {
        return KeyStroke.getKeyStroke(ks.replace("+", "pressed"));
    }

    @Override
    protected String title4PopupWindow() {
        return "JS";
    }

    public void populate(String js) {
        this.contentTextArea.setText(js);
    }

    public String update() {
        return this.contentTextArea.getText();
    }

    public void setFunctionTitle(String[] args) {
        funNameLabel.setText(createFunctionTitle(args));
    }

    public void setFunctionTitle(String[] args, String[] defaultArgs) {
        String[] titles;
        if (defaultArgs == null) {
            titles = args;
        } else if (args == null) {
            titles = defaultArgs;
        } else {
            ArrayList list = new ArrayList();
            for (String s : defaultArgs) {
                list.add(s);
            }
            for (String s : args) {
                list.add(s);
            }
            titles = (String[]) list.toArray(new String[list.size()]);
        }
        setFunctionTitle(titles);
    }

    /**
     * 用html，方便换行
     *
     * @param args
     * @return
     */
    private String createFunctionTitle(String[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html> <body> <div style='height:16px'>function(");
        int width = titleWidth;
        FontMetrics cellFM = this.getFontMetrics(this.getFont());
        int tempwidth = 0;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    continue;
                }
                if (cellFM.stringWidth(args[i]) < width) {
                    tempwidth = tempwidth + cellFM.stringWidth(args[i]);
                    if (tempwidth < width) {
                        sb.append(args[i]);
                        if (i != args.length - 1) {
                            sb.append(",");
                        }
                    } else {
                        tempwidth = 0;
                        i = i - 1;// 后退一步
                        sb.append("</p><p>&nbsp&nbsp&nbsp&nbsp&nbsp;");
                    }
                } else {
                    sb.append("</p><p>&nbsp&nbsp&nbsp&nbsp&nbsp;");
                    sb.append(args[i]);
                    sb.append("</p>");
                }
            }
        }
        sb.append("){</div><body> </html>");
        return sb.toString();
    }

    private CompletionProvider createCompletionProvider() {

        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        for (String key : KeyWords.JAVASCRIPT) {
            provider.addCompletion(new BasicCompletion(provider, key));
        }

        for (String[] key : KeyWords.JAVASCRIPT_SHORT) {
            provider.addCompletion(new ShorthandCompletion(provider, key[0],
                    key[1], key[1]));
        }

        return provider;

    }
}