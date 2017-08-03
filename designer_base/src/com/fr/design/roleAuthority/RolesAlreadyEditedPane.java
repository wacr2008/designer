package com.fr.design.roleAuthority;

import com.fr.base.BaseUtils;
import com.fr.design.constants.UIConstants;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itree.refreshabletree.ExpandMutableTreeNode;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.mainframe.DockingView;
import com.fr.general.Inter;
import com.fr.general.NameObject;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Enumeration;

/**
 * Author : daisy
 * Date: 13-9-25
 * Time: 下午4:34
 */
public class RolesAlreadyEditedPane extends JPanel {
    private static final int TITLE_HEIGHT = 19;
    private static final int LEFT_GAP = 8;
    private static final int TOP_GAP = -1;
    private static RolesAlreadyEditedPane THIS;
    private RolesEditedPane rolesEditedPane;

    /**
     * 得到实例
     *
     * @return
     */
    public static final RolesAlreadyEditedPane getInstance() {
        if (THIS == null) {
            THIS = new RolesAlreadyEditedPane();
        }
        return THIS;
    }


    public RolesAlreadyEditedPane() {
        this.setLayout(new BorderLayout());
        this.setBorder(null);
        UILabel authorityTitle = new UILabel(Inter.getLocText("roles_already_authority_edited")) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, TITLE_HEIGHT);
            }
        };
        authorityTitle.setHorizontalAlignment(SwingConstants.CENTER);
        authorityTitle.setVerticalAlignment(SwingConstants.CENTER);
        JPanel northPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        northPane.add(authorityTitle, BorderLayout.CENTER);
        northPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.LINE_COLOR));
//        this.add(northPane, BorderLayout.NORTH);
        rolesEditedPane = new RolesEditedPane();
        this.add(rolesEditedPane, BorderLayout.CENTER);

    }

    public RoleTree getRoleTree() {
        return rolesEditedPane.roleTree;
    }

    public void refreshDockingView() {
        rolesEditedPane.refreshDockingView();
    }

    public void setReportAndFSSelectedRoles() {
        rolesEditedPane.setSelectedRole();
    }


    private class RolesEditedPane extends DockingView {
        private RoleTree roleTree;
        private RolesEditedSourceOP op;
        private DefaultTreeCellRenderer roleTreeRenderer = new DefaultTreeCellRenderer() {
            private static final long serialVersionUID = 2L;


            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                ExpandMutableTreeNode treeNode = (ExpandMutableTreeNode) value;
                Object userObj = treeNode.getUserObject();
                if (userObj instanceof String) {
                    // p:这个是column field.
                    this.setIcon(null);
                    this.setText((String) userObj);
                } else if (userObj instanceof NameObject) {
                    NameObject nameObject = (NameObject) userObj;
                    this.setText(nameObject.getName());
                    if (nameObject.getName() == Inter.getLocText("M_Server-Platform_Manager")) {
                        this.setIcon(BaseUtils.readIcon("/com/fr/web/images/platform/platform_16_16.png"));
                    } else {
                        this.setIcon(BaseUtils.readIcon("/com/fr/web/images/platform/demo.png"));
                    }
                }


                // 这里新建一个Label作为render是因为JTree在动态刷新的时候，节点上render画布的的宽度不会变，会使得一部分比较长的数据显示为"..."
                this.setBackgroundNonSelectionColor(UIConstants.NORMAL_BACKGROUND);
                this.setForeground(UIConstants.FONT_COLOR);
                this.setBackgroundSelectionColor(UIConstants.FLESH_BLUE);
                return this;
            }
        };


        public RolesEditedPane() {
            roleTree = new RoleTree() {
                public void refreshRoleTree(String selectedRole) {
                    super.refreshRoleTree(selectedRole);
                    RoleTree roleTree = ReportAndFSManagePane.getInstance().getRoleTree();
                    TreeNode root = (TreeNode) roleTree.getModel().getRoot();
                    TreePath parent = new TreePath(root);
                    roleTree.setSelectedRole(selectedRole, parent);
                }
            };
            roleTree.setCellRenderer(roleTreeRenderer);
            roleTree.setEditable(false);
            op = new RolesEditedSourceOP();
            UIScrollPane scrollPane = new UIScrollPane(roleTree) {
                public Dimension getPreferredSize() {
                    return new Dimension(RolesEditedPane.this.getWidth(), RolesEditedPane.this.getHeight());
                }
            };
            scrollPane.setBorder(BorderFactory.createEmptyBorder(TOP_GAP, LEFT_GAP, 0, 0));
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            this.add(scrollPane, BorderLayout.CENTER);
        }

        public void refreshDockingView() {
            this.op = new RolesEditedSourceOP();
            roleTree.populate(op);
            expandTree(roleTree, true);
        }

        private void setSelectedRole() {
            String name = ReportAndFSManagePane.getInstance().getRoleTree().getSelectedRoleName();
            TreeNode root = (TreeNode) roleTree.getModel().getRoot();
            TreePath parent = new TreePath(root);
            roleTree.setSelectedRole(name, parent);
        }


        public String getViewTitle() {
            return null;
        }

        public Icon getViewIcon() {
            return null;
        }

        public Location preferredLocation() {
            return null;
        }

        public void expandTree(JTree tree, boolean isExpand) {
            TreeNode root = (TreeNode) tree.getModel().getRoot();
            expandAll(tree, new TreePath(root), isExpand);
        }


        private void expandAll(JTree tree, TreePath parent, boolean expand) {
            TreeNode node = (TreeNode) parent.getLastPathComponent();
            if (node.getChildCount() >= 0) {
                for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                    TreeNode n = (TreeNode) e.nextElement();
                    TreePath path = parent.pathByAddingChild(n);
                    expandAll(tree, path, expand);
                }
            }
            if (expand) {
                tree.expandPath(parent);
            } else {
                tree.collapsePath(parent);
            }
        }
    }
}