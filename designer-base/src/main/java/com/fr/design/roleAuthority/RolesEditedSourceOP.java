package com.fr.design.roleAuthority;import java.util.ArrayList;import java.util.Arrays;import java.util.Collections;import java.util.Iterator;import java.util.List;import java.util.Map;import com.fr.design.file.HistoryTemplateListPane;import com.fr.design.gui.itree.refreshabletree.ExpandMutableTreeNode;import com.fr.design.mainframe.JTemplate;import com.fr.general.NameObject;import com.fr.privilege.PrivilegeEditedRoleProvider;/** * Author : daisy * Date: 13-9-25 * Time: 下午4:57 */public class RolesEditedSourceOP extends RoleSourceOP {	/**	 * 获取报表平台的角色	 */	protected void addReportRoles(Map<String, RoleDataWrapper> report_roles) {		RoleDataWrapper tdw = new RoleDataWrapper(com.fr.design.i18n.Toolkit.i18nText("M_Server-Platform_Manager"));		report_roles.put(com.fr.design.i18n.Toolkit.i18nText("M_Server-Platform_Manager"), tdw);	}	/**	 * 获取数据决策系统的角色	 */	protected void addFSRoles(Map<String, RoleDataWrapper> FS_roles) {		RoleDataWrapper tdw = new RoleDataWrapper(com.fr.design.i18n.Toolkit.i18nText("FS_Name"));		FS_roles.put(com.fr.design.i18n.Toolkit.i18nText("FS_Name"), tdw);	}	/**	 * 生成子节点	 *	 * @return	 */	@Override	public ExpandMutableTreeNode[] load() {		Map<String, RoleDataWrapper> report_roles = null;//		Map<String, RoleDataWrapper> FS_roles = null;		if (this != null) {			report_roles = this.init().get(0);//			FS_roles = this.init().get(1);		} else {			report_roles = Collections.emptyMap();//			FS_roles = Collections.emptyMap();		}		List<ExpandMutableTreeNode> list = new ArrayList<ExpandMutableTreeNode>(); //所有的角色		List<ExpandMutableTreeNode> reportlist = new ArrayList<ExpandMutableTreeNode>(); //报表平台橘色//		List<ExpandMutableTreeNode> FSlist = new ArrayList<ExpandMutableTreeNode>();   //数据决策系统角色		list.add(initReportRolseNode(report_roles));		addNodeToList(report_roles, reportlist);//		list.add(initFSRolseNode(FS_roles));//		addNodeToList(FS_roles, FSlist);		return list.toArray(new ExpandMutableTreeNode[list.size()]);	}		protected ExpandMutableTreeNode initReportRolseNode(Map<String, RoleDataWrapper> report_roles) {		ExpandMutableTreeNode templateNode = new ExpandMutableTreeNode(new NameObject(com.fr.design.i18n.Toolkit.i18nText("roles_already_authority_edited"), 0), true);		templateNode.addChildTreeNodes(getNodeArrayFromMap(report_roles));		return templateNode;	}	protected ExpandMutableTreeNode[] getNodeArrayFromMap(Map<String, RoleDataWrapper> map) {		List<ExpandMutableTreeNode> roleList = new ArrayList<ExpandMutableTreeNode>();		Iterator<Map.Entry<String, RoleDataWrapper>> entryIt = map.entrySet().iterator();		while (entryIt.hasNext()) {			Map.Entry<String, RoleDataWrapper> entry = entryIt.next();			String name = entry.getKey();			RoleDataWrapper t = entry.getValue();						JTemplate jt = HistoryTemplateListPane.getInstance().getCurrentEditingTemplate();			PrivilegeEditedRoleProvider pe = (PrivilegeEditedRoleProvider) jt.getTarget();						ExpandMutableTreeNode[] expand = t.load(Arrays.asList(pe.getAllEditedRoleSet()));			for (ExpandMutableTreeNode expandMutableTreeNode : expand) {				roleList.add(expandMutableTreeNode);			}		}		return roleList.toArray(new ExpandMutableTreeNode[roleList.size()]);	}}