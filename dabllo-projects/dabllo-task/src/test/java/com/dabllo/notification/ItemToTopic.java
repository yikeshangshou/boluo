package com.dabllo.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dabllo.dao.EntityDao;
import com.dabllo.dao.mapper.ItemRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Item;

/**
 * @author mixueqiang
 * @since Aug 6, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ApplicationContext.xml" })
public class ItemToTopic {

	@Resource
	EntityDao entityDao;

	static List<Item> items;
	public static String ORDER_OPTION_ASC = "ASC";
	public static String ORDER_OPTION_DESC = "DESC";

	@Test
	public void Change() {
		// "苹果", "iphone", "iPhone", "ipad", "iPad","appWatch", "库克"
		// "Facebook", "扎克伯格", "fb","脸书"

		// 亚马逊
		 items = entityDao.get("item", 1, 99999, ItemRowMapper.getInstance());
		//items = entityDao.get("item", 1, 100, ItemRowMapper.getInstance(), "createTime", ORDER_OPTION_DESC);

//		test(100001, "Facebook", "扎克伯格", "fb", "脸书");
//		test(100002, "airbnb");
//		test(100003, "亚马逊");
//		test(100004, "苹果", "iphone", "iPhone", "ipad", "iPad", "appWatch", "库克");
//		test(100005, "诺基亚");
//		test(100006, "百度", "李彦宏");
//		test(100007, "京东", "刘强东");
//		test(100008, "魅族");
//		test(100009, "微软", "window", "比尔盖茨");
//		test(100010, "window");
//		test(100011, "直播", "斗鱼", "映客", "花椒");
//		test(100012, "游戏直播", "斗鱼", "全民直播", "熊猫tv");
//		test(100014, "谷歌", "google", "gmail");
//		test(100015, "阿里", "马云", "淘宝", "天猫", "支付宝");
//		test(100016, "安卓", "android");
//		test(100017, "陌陌", "唐岩", "哈尼直播");
//		test(100018, "小米", "雷军", "红米");
//		test(100019, "人人");
//		test(100020, "扎克伯格");
//		test(100021, "库克");
//		test(100022, "腾讯", "微信", "qq", "马化腾");
//		test(100023, "微信", "wechat");
//		test(100024, "英特尔", "inter");
//		test(100025, "锤子", "老罗", "坚果");
//		test(100026, "财报", "Q2", "第二季度");
//		test(100027, "雷军");
//		test(100028, "三星", "SAMSUNG", "galaxy");
//		test(100029, "Uber");
//		test(100030, "乐视");
//		test(100031, "滴滴", "程维");
//		test(100033, "融资", "投资");
//		test(100034, "360", "周鸿祎");
//		test(100035, "无人机");
//		test(100036, "vr", "虚拟现实");
//		test(100037, "显卡", "cpu");
//		test(100038, "3d打印");
//		test(100039, "iPhone");
		test(100042, "特斯拉", "马斯克", "tesla", "solarcity");

	}

	private void test(long topicId, String... keys) {
		// List<Item> items = entityDao.get("item", 1, 999999999,
		// ItemRowMapper.getInstance());
		test(topicId, items, keys);
	}

	private void test(long topicId, List<Item> items, String... keys) {
		int a = 0;
		int b = 0;
		List<Item> ch = new ArrayList<Item>();
		// List<Item> items = entityDao.get("item", 1, 999999999,
		// ItemRowMapper.getInstance());
		for (Item item : items) {
			b++;
			if (StringUtils.isNotEmpty(item.getTitle()) && isContanis(item.getTitle(), keys)) {
				a++;
				ch.add(item);
				Entity topic_item = new Entity("topic_item");
				topic_item.set("topicId", topicId).set("itemId", item.getId());
				topic_item.set("status", 1).set("createTime", item.getCreateTime());
				Map<String, Object> valus = new HashMap<String, Object>();
				valus.put("itemId", item.getId());
				valus.put("status", 1);
				valus.put("createTime", item.getCreateTime());

				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("itemId", item.getId());
				condition.put("topicId", topicId);

				if (entityDao.exists("topic_item", condition)) {
					entityDao.update("topic_item", condition, valus);
				} else {
					entityDao.save(topic_item);
				}
			}
		}

		for (Item item : ch) {
			System.out.println(item.getTitle());
		}
		System.out.println(a + " " + b);
	}

	private boolean isContanis(String title, String... keys) {
		for (String key : keys) {
			if (StringUtils.containsIgnoreCase(title, key)) {
				return true;
			}
		}
		return false;
	}

}
