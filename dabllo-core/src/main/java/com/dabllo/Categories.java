package com.dabllo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Apr 29, 2014
 */
public class Categories {
  private static final Map<Integer, String> map = new HashMap<Integer, String>();

  static {
    map.put(1, "计算机、互联网");
    map.put(2, "商务谈判、商务陪同");
    map.put(3, "生活服务");
    map.put(4, "医疗、制药、健康");
    map.put(5, "制造业、机械工业");
    map.put(6, "通信、电子");
    map.put(7, "汽车");
    map.put(8, "服装、服饰、化妆品");
    map.put(9, "能源、原材料、化工、矿产");
    map.put(10, "贸易、消费");
    map.put(11, "影视、剧作");
    map.put(12, "书籍、出版、翻译");
    map.put(13, "科研、论文");
    map.put(14, "教育、培训、留学");
    map.put(15, "金融、会计、证券、投资、银行、保险");
    map.put(16, "文化、传媒、艺术");
    map.put(17, "展会、交流会、博览会");
    map.put(18, "生物工程、生物科技");
    map.put(19, "咨询、法律、管理");
    map.put(20, "建筑工程、房地产、装饰");
    map.put(21, "销售、营运");
    map.put(22, "公关、会展");
    map.put(23, "政府、非盈利性服务");
    map.put(24, "物流、运输、交通");
    map.put(25, "体育、赛事、运动会");
    map.put(100, "其他");
  }

  public static String get(int categoryId) {
    String category = map.get(categoryId);
    if (StringUtils.isEmpty(category)) {
      return map.get(100);
    }

    return category;
  }

}
