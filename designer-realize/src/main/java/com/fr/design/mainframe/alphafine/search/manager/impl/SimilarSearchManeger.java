package com.fr.design.mainframe.alphafine.search.manager.impl;

import com.fr.design.DesignerEnvManager;
import com.fr.design.mainframe.alphafine.AlphaFineConstants;
import com.fr.design.mainframe.alphafine.AlphaFineHelper;
import com.fr.design.mainframe.alphafine.CellType;
import com.fr.design.mainframe.alphafine.cell.model.MoreModel;
import com.fr.design.mainframe.alphafine.cell.model.RobotModel;
import com.fr.design.mainframe.alphafine.component.AlphaFineDialog;
import com.fr.design.mainframe.alphafine.model.SearchResult;
import com.fr.design.mainframe.alphafine.search.manager.fun.AlphaFineSearchProvider;
import com.fr.general.http.HttpToolbox;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.log.FineLoggerFactory;
import com.fr.stable.ArrayUtils;
import com.fr.third.org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;

/**
 * Created by alex.sung on 2018/8/3.
 */
public class SimilarSearchManeger implements AlphaFineSearchProvider {
    private static volatile SimilarSearchManeger instance;
    private SearchResult lessModelList;
    private SearchResult moreModelList = new SearchResult();

    public static SimilarSearchManeger getInstance() {
        if (instance == null) {
            synchronized (SimilarSearchManeger.class){
                if (instance == null) {
                    instance = new SimilarSearchManeger();
                }
            }
        }
        return instance;
    }

    @Override
    public SearchResult getLessSearchResult(String[] searchText) {
        if (ArrayUtils.isEmpty(searchText)) {
            return new SearchResult();
        } else if (AlphaFineDialog.data == null) {
            return AlphaFineHelper.getNoConnectList(instance);
        }
        lessModelList = new SearchResult();
        if (DesignerEnvManager.getEnvManager().getAlphaFineConfigManager().isNeedIntelligentCustomerService()) {
            SearchResult allModelList = new SearchResult();
            for (int j = 0; j < searchText.length; j++) {
                String token = DigestUtils.md5Hex(AlphaFineConstants.ALPHA_ROBOT_SEARCH_TOKEN + searchText[j]);
                String url = AlphaFineConstants.SIMILAR_SEARCH_URL_PREFIX + "msg=" + searchText[j] + "&token=" + token;
                try {
                    String result = HttpToolbox.get(url);
                    AlphaFineHelper.checkCancel();
                    allModelList = AlphaFineHelper.getModelListFromJSONArray(result,"title");
                } catch (ClassCastException | JSONException e) {
                    FineLoggerFactory.getLogger().error("similar search error: " + e.getMessage());
                } catch (IOException e) {
                    FineLoggerFactory.getLogger().error("similar search get result error: " + e.getMessage());
                }
            }
            moreModelList.clear();
            if (allModelList.isEmpty()) {
                return lessModelList;
            } else if (allModelList.size() < AlphaFineConstants.SHOW_SIZE + 1) {
                lessModelList.add(0, new MoreModel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_AlphaFine_Relation_Item")));
                lessModelList.addAll(allModelList);
            } else {
                lessModelList.add(0, new MoreModel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_AlphaFine_Relation_Item"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_AlphaFine_ShowAll"), true, CellType.ROBOT));
                lessModelList.addAll(allModelList.subList(0, AlphaFineConstants.SHOW_SIZE));
                moreModelList.addAll(allModelList.subList(AlphaFineConstants.SHOW_SIZE, allModelList.size()));
            }
        }
        return lessModelList;
    }

    @Override
    public SearchResult getMoreSearchResult(String searchText) {
        return moreModelList;
    }

    /**
     * 根据json信息获取RobotModel
     *
     * @param object
     * @return
     */
    public static RobotModel getModelFromCloud(JSONObject object) {
        String name = object.optString("title");
        String content = object.optString("content");
        return new RobotModel(name, content);
    }
}