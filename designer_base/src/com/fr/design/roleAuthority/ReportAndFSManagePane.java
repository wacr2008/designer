package com.fr.design.roleAuthority;

import com.fr.base.BaseUtils;
import com.fr.base.FRCoreContext;
import com.fr.design.actions.UpdateAction;
import com.fr.design.constants.UIConstants;
import com.fr.design.data.DesignTableDataManager;
import com.fr.design.data.tabledata.Prepare4DataSourceChange;
import com.fr.design.gui.ibutton.UIHeadGroup;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.gui.itoolbar.UIToolbar;
import com.fr.design.gui.itree.refreshabletree.ExpandMutableTreeNode;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.mainframe.DockingView;
import com.fr.design.menu.ToolBarDef;
import com.fr.general.Inter;
import com.fr.general.VT4FR;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/**
 * 设计器左下角面板，用于在权限编辑时存放角色
 * Author : daisy
 * Date: 13-8-30
 * Time: 下午2:22
 */
public class ReportAndFSManagePane extends DockingView implements Prepare4DataSourceChange {

    private static final int REPORT_PLATEFORM_MANAGE = 0;
    private static final int FS_MANAGE = 1;
    private static final int LEFT_GAP = -125;
    private static boolean isSupportFS = false;
    private TreePath treePath = null;

    private static ReportAndFSManagePane singleton = new ReportAndFSManagePane();

    private static RoleTree roleTree;
    // carl:我先屏了，现在半拉子，等客户要了再好好做
//	private AddAction addAction = new AddAction();
//	private RemoveAction removeAction = new RemoveAction();
    private RefreshAction refreshAction = new RefreshAction();
    private UIHeadGroup buttonGroup;
    private RoleSourceOP op;
    protected String[] roleNames = new String[2];

    public synchronized static ReportAndFSManagePane getInstance() {
        singleton.op = new RoleSourceOP();
        singleton.op.setDataMode(isSupportFS ? FS_MANAGE : REPORT_PLATEFORM_MANAGE);
        singleton.setDefaultSelectedRole();
        return singleton;

    }

    public ReportAndFSManagePane() {
        initRoleTree();
        this.setLayout(new BorderLayout(4, 0));
        this.setBorder(null);
        this.add(iniToolBarPane(), BorderLayout.NORTH);
        refreshAction.setEnabled(true);
        UIScrollPane scrollPane = new UIScrollPane(roleTree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        initbuttonGroup();
        JPanel jPanel = new JPanel(new BorderLayout(4, 4));
        JPanel buttonPane = new JPanel(new GridLayout());
        buttonPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.LINE_COLOR));
        buttonPane.add(buttonGroup, BorderLayout.CENTER);
        jPanel.add(buttonPane, BorderLayout.NORTH);
        jPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(jPanel, BorderLayout.CENTER);
        registerDSChangeListener();
    }

    private void initRoleTree() {
        roleTree = new RoleTree() {
            public void refreshRoleTree(String selectedRole) {
                super.refreshRoleTree(selectedRole);
                changeAlreadyEditedPaneRole(selectedRole);
            }


            protected void doWithValueChanged(TreeSelectionEvent e) {
                super.doWithValueChanged(e);
                TreeNode root = (TreeNode) roleTree.getModel().getRoot();
                TreePath parent = new TreePath(root);
                setSelectedRole(roleTree.getSelectedRoleName(), parent);

            }

            protected void setTabRoleName(String roleName) {
                roleNames[getMode()] = roleTree.getSelectedRoleName();
            }
        };
        roleTree.setEnabled(true);
        roleTree.setEditable(false);
//		RoleTreeCellEditor treeCellEditor = new RoleTreeCellEditor(new UITextField());
//		treeCellEditor.addCellEditorListener(treeCellEditor);
//		roleTree.setCellEditor(treeCellEditor);
        roleTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                roleTree.setEditable(false);
//                int row = roleTree.getRowForLocation(e.getX(), e.getY());
//                TreePath path = roleTree.getPathForLocation(e.getX(), e.getY());
//                if (e.getClickCount() == 2 && buttonGroup.getSelectedIndex() == REPORT_PLATEFORM_MANAGE) {
//                    PrivilegeManagerProvider pm = PrivilegeManager.getProviderInstance();
//                    AuthenticationProvider ap = pm.getAuthenticationProvider();
//                    if (!(ap instanceof DaoAuthenticationProvider)) {
//                        roleTree.setEditable(true);
//                        roleTree.startEditingAtPath(path);
//                        treePath = path;
//                    }
//                }
            }
        });
    }

    private void changeAlreadyEditedPaneRole(String selectedRole) {
        RolesAlreadyEditedPane.getInstance().refreshDockingView();
        RoleTree roleTree = RolesAlreadyEditedPane.getInstance().getRoleTree();
        TreeNode root = (TreeNode) roleTree.getModel().getRoot();
        TreePath parent = new TreePath(root);
        roleTree.setSelectedRole(selectedRole, parent);
    }

    public void setDefaultSelectedRole() {
        //设置选中的节点
        TreeNode root = (TreeNode) roleTree.getModel().getRoot();
        TreePath parent = new TreePath(root);
        ExpandMutableTreeNode node = (ExpandMutableTreeNode) parent.getLastPathComponent();
        String selectedRole = null;
        if (singleton != null) {
            selectedRole = roleNames[getMode()];
        }
        if (selectedRole == null) {
            if (node.getChildCount() <= 0 || node.getFirstChild().getChildCount() <= 0) {
                return;
            }
            selectedRole = node.getFirstChild().getChildAt(0).toString();
        }
        roleTree.setSelectedRole(selectedRole, parent);
    }


    public RoleTree getRoleTree() {
        return roleTree;
    }

    /**
     * 检查f)	每增删改一个角色信息（及时保存），先对比下privilege下之前的角色信息有没有发生变化（即在此期间有没有在其他途径中修改过）
     */
    private void checkChanges() {
        //如若有变化，则弹出下面的对话框
        int returnVal = JOptionPane.showConfirmDialog(DesignerContext.getDesignerFrame(), Inter.getLocText("FR-Designer_Role_changed_isRefresh") + "?",
                Inter.getLocText("FR-Designer_Refresh"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (returnVal == JOptionPane.OK_OPTION) {
            roleTree.refreshTreeNode();
            expandTree(roleTree, true);
            roleTree.updateUI();
        }

    }

    private JPanel iniToolBarPane() {
        ToolBarDef toolbarDef = new ToolBarDef();
        toolbarDef.addShortCut(refreshAction);
        UIToolbar toolBar = ToolBarDef.createJToolBar();
        toolbarDef.updateToolBar(toolBar);
        JPanel toolbarPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        toolbarPane.add(toolBar, BorderLayout.CENTER);
        return toolbarPane;
    }

    private void initbuttonGroup() {
        Icon[] iconArray = new Icon[]{BaseUtils.readIcon("/com/fr/web/images/platform/demo.png")};
        String[] textArray = new String[]{Inter.getLocText("FR-Designer_FS_Name")};
        buttonGroup = new UIHeadGroup(iconArray, textArray) {
            public void tabChanged(int index) {
                roleTree.setEditable(false);
                if (op != null) {
                    op.setDataMode(getMode());
                    //判断是否可编辑
                    refreshDockingView();
                }
                setDefaultSelectedRole();
                if (singleton != null) {
                    changeAlreadyEditedPaneRole(roleNames[getMode()]);
                }
            }
        };
        buttonGroup.setBorder(BorderFactory.createMatteBorder(1, LEFT_GAP, 0, 0, UIConstants.LINE_COLOR));
        buttonGroup.setNeedLeftRightOutLine(false);
    }


    private int getMode(){
        return isSupportFS?FS_MANAGE: REPORT_PLATEFORM_MANAGE;
    }


//	/**
//	 * 检查看看是否可以增删刷新按钮是都可以编辑,并且检查角色树是不是可以编辑
//	 */
//	public void checkToolButtonsEnabled() {
//		if (buttonGroup.getSelectedIndex() == REPORT_PLATEFORM_MANAGE) {
//			PrivilegeManagerProvider pm = PrivilegeManager.getProviderInstance();
//			AuthenticationProvider ap = pm.getAuthenticationProvider();
//			PrivilegeFilter pf = pm.getPrivilegeFilter();
//			boolean isClickable = !(ap instanceof DaoAuthenticationProvider) 
//					&& pf instanceof AuthorityControlFilter;
//			addAction.setEnabled(isClickable);
//			removeAction.setEnabled(isClickable);
//		} else {
//			addAction.setEnabled(false);
//			removeAction.setEnabled(false);
//		}
//
//	}


    /**
     * 刷新界面
     */
    public void refreshDockingView() {
        populate(new RoleSourceOP());
//		this.checkToolButtonsEnabled();
    }

    private void populate(RoleSourceOP op) {
        this.op = op;
        roleTree.populate(op);
        expandTree(roleTree, true);
    }

    public String getViewTitle() {
        return null;
    }

    public Icon getViewIcon() {
        return null;
    }

    /**
     * 最佳定位
     * @return 定位
     */
    public Location preferredLocation() {
        return null;
    }

    /**
     * 注册数据库改变的响应的Listener
     */
    public void registerDSChangeListener() {
        DesignTableDataManager.addDsChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                roleTree.refreshTreeNode();
                expandTree(roleTree, true);
                roleTree.updateUI();
            }
        });

    }

//	private class AddAction extends UpdateAction {
//		public AddAction() {
//			this.setName(Inter.getLocText("Add"));
//			this.setSmallIcon(BaseUtils.readIcon("/com/fr/base/images/cell/control/add.png"));
//		}
//
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			refreshDockingView();
//			
//			DefaultTreeModel treeModel = (DefaultTreeModel) roleTree.getModel();
//			ExpandMutableTreeNode root = (ExpandMutableTreeNode) treeModel.getRoot();
//			ExpandMutableTreeNode parentNode = (ExpandMutableTreeNode) root.getChildAt(0);
//			String newName = Inter.getLocText("newNode") + (++newIndex);
//			parentNode.add(new ExpandMutableTreeNode(newName));
//			op.addAction(newName);
//			roleTree.updateUI();
//			
//			try {
//				synchronized (AuthorityRoleDAOManager.class) {
//					AuthorityControlFilter pf = AuthorityRoleDAOManager.getAuthControlFilter(true);
//					
//					if (AuthorityRoleDAOManager.getAuthorityAllocation(pf, newName) != null) {
//						newName = Inter.getLocText("newNode") + (++newIndex);
//						roleTree.refreshTreeNode();
//						expandTree(roleTree, true);
//					}
//					
//					AuthorityRoleDAOManager.addAuthorityRole(pf, new Authority(newName), new Allocation(), false, true);
//				
//					AuthorityRoleDAOManager.doEnd(pf);
//				}
//			} catch (Exception e1) {
//				FRContext.getLogger().error(e1.getMessage(), e1);
//			}
//		}
//	}

//	private class RemoveAction extends UpdateAction {
//
//		public RemoveAction() {
//			this.setName(Inter.getLocText("Remove"));
//			this.setSmallIcon(UIConstants.CLEAR_ICON);
//		}
//
//		@Override
//		public void actionPerformed(ActionEvent e) {
//
//
//			NameObject selectedNO = roleTree.getSelectedNameObject();
//
//			if (selectedNO == null) {
//				return;
//			}
//
//			int returnVal = JOptionPane.showConfirmDialog(DesignerContext.getDesignerFrame(), Inter.getLocText("Utils-Are_you_sure_to_remove_the_selected_item") + ":" + selectedNO.getName() + "?",
//					Inter.getLocText("Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
//			if (returnVal == JOptionPane.OK_OPTION) {
//				op.removeAction(selectedNO.getName());
//
//				try {
//					AuthorityControlFilter pf = AuthorityRoleDAOManager.getAuthControlFilter(true);
//					
//					AuthorityRoleDAOManager.removeAuthorityRole(pf, new Authority(selectedNO.getName()), true);
//					AuthorityRoleDAOManager.doEnd(pf);
//				} catch (Exception e1) {
//					FRContext.getLogger().error(e1.getMessage(), e1);
//				}
//
//				roleTree.refreshTreeNode();
//				expandTree(roleTree, true);
//				roleTree.updateUI();
//				roleTree.requestFocus();
//				roleTree.setSelectionRow(roleTree.getRowCount() - 1);
//			}
//		}
//	}


    /*
 * 刷新ReportletsTree
 */
    private class RefreshAction extends UpdateAction {

        public RefreshAction() {
            this.setName(Inter.getLocText("FR-Designer_Refresh"));
            this.setSmallIcon(UIConstants.REFRESH_ICON);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            roleTree.refreshTreeNode();
            expandTree(roleTree, true);
            roleTree.updateUI();
        }
    }


//	private class RoleTreeCellEditor extends DefaultCellEditor implements TreeCellEditor, CellEditorListener {
//
//		private NameObject editingNO;
//		private String oldName;
//		private String newName;
//		private UITextField jTextField;
//
//		public RoleTreeCellEditor(final UITextField textField) {
//			super(textField);
//			this.jTextField = textField;
//			this.jTextField.setPreferredSize(new Dimension(DesignerEnvManager.getEnvManager().getLastWestRegionContainerWidth() - 5, this.jTextField.getHeight()));
//		}
//
//		@Override
//		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
//			editingNO = ReportAndFSManagePane.this.roleTree.getSelectedNameObject();
//			oldName = editingNO.getName();
//
//			delegate.setValue(oldName);
//
//			editorComponent.setPreferredSize(new java.awt.Dimension(ReportAndFSManagePane.this.getPreferredSize().width, editorComponent.getPreferredSize().height));
//
//			return editorComponent;
//		}
//
//		@Override
//
//		public boolean isCellEditable(EventObject anEvent) {
//			NameObject no = ReportAndFSManagePane.this.roleTree.getSelectedNameObject();
//			return !(no.getName() == Inter.getLocText("Role"));
//		}
//
//		@Override
//		public Object getCellEditorValue() {
//			newName = super.getCellEditorValue().toString();
//			editingNO.setName(newName);
//			return editingNO;
//		}
//
//
//		private boolean checkRoleNameNotEmpty() {
//			refreshDockingView();
//			
//			String currentText = delegate.getCellEditorValue().toString();
//			boolean isContained = false;
//			AuthorityControlFilter pf = AuthorityRoleDAOManager.getAuthControlFilter(false);
//			if (pf != null && 
//					!ComparatorUtils.equals(oldName, currentText)) {
//				try {
//					Iterator iterator = AuthorityRoleDAOManager.authorityAllocationIterator(pf);
//					
//					while (iterator.hasNext()) {
//						AuthorityAllocation authorityAllocation = (AuthorityAllocation) ((Map.Entry) iterator.next())
//								.getValue();
//						Authority _authority = authorityAllocation.getAuthority();
//						if(ComparatorUtils.equals(_authority.getName(), currentText)){
//							isContained = true;
//							break;
//						}
//					}
//				} catch (Exception e) {
//				}
//			}
//			
//			if (currentText.isEmpty() || isContained) {
//				JOptionPane.showMessageDialog(DesignerContext.getDesignerFrame(), Inter.getLocText("RoleName_Can_Not_Be_Null") + "！");
//				roleTree.refreshTreeNode();
//				expandTree(roleTree, true);
//				delegate.setValue(oldName);
//				return false;
//			}
//			return true;
//		}
//
//		/*
//		* 下面两个方法是CellEditorListener的
//		*/
//		@Override
//		public void editingCanceled(ChangeEvent e) {
//			if (!checkRoleNameNotEmpty()) {
//				treePath = null;
//				return;
//			}
//			roleTree.stopEditing();
//			if (treePath == null) {
//				return;
//			}
//			changeValue();
//			roleTree.refreshTreeNode();
//			expandTree(roleTree, true);
//		}
//
//		@Override
//		public void editingStopped(ChangeEvent e) {
//			if (!checkRoleNameNotEmpty()) {
//				treePath = null;
//				return;
//			}
//			changeValue();
//			roleTree.refreshTreeNode();
//			expandTree(roleTree, true);
//			roleTree.updateUI();
//		}
//
//		private void changeValue() {
//			newName = delegate.getCellEditorValue().toString();
//			if (!newName.isEmpty() && newName != oldName) {
//				roleTree.setSelectedRoleName(newName);
//				op.rename(oldName, newName);
//
//				AuthorityControlFilter pf = AuthorityRoleDAOManager.getAuthControlFilter(false);
//				if (pf != null) {
//					try {
//						Iterator iterator = AuthorityRoleDAOManager.authorityAllocationIterator(pf);
//						
//						while (iterator.hasNext()) {
//							AuthorityAllocation authorityAllocation = (AuthorityAllocation) ((Map.Entry) iterator.next())
//									.getValue();
//							Authority _authority = authorityAllocation.getAuthority();
//							if (ComparatorUtils.equals(_authority.getName(), oldName)) {
//								_authority.setName(newName);
//							}
//						}
//					} catch (Exception e) {
//					}
//				}
//
//				try {
//					AuthorityRoleDAOManager.doEnd(pf);
//				} catch (Exception e) {
//					FRContext.getLogger().error(e.getMessage(), e);
//				}
//			}
//		}
//	}


    /**
     * 展开树
     * @param tree 树
     * @param isExpand 是否展开
     */
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