package com.diyicai.service;

import com.diyicai.dao.TlBonusTopDao;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhangyong
 * Date: 13-12-30
 * Time: 下午5:59
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TlBonusTopService {

    private static final Logger logger = Logger.getLogger(TlBonusTopService.class);

    private static final Integer RECENT_90_DAYS = 90;

    private static final Integer BATCH_STEP = 5000;

    @Autowired
    private TlBonusTopDao tlBonusTopDao;

    @Value("${STORE_PATH}")
    private String STORE_PATH;

    public void executeTlBonusTop() throws IOException {
        long startTime = System.currentTimeMillis();
        FileUtils.deleteDirectory(new File(STORE_PATH));
        Map rangeInfo = tlBonusTopDao.getRangeInfo(RECENT_90_DAYS);
        logger.info("rangeInfo:" + rangeInfo);
        int MIN_ID = ((BigDecimal) rangeInfo.get("MIN_ID")).intValue();
        int MAX_ID = ((BigDecimal) rangeInfo.get("MAX_ID")).intValue();
        int COUNT_NUMBER = ((BigDecimal) rangeInfo.get("COUNT_NUMBER")).intValue();
        int pageCount = COUNT_NUMBER % BATCH_STEP == 0 ? COUNT_NUMBER / BATCH_STEP : COUNT_NUMBER / BATCH_STEP + 1;
        logger.info("pageCount==" + pageCount);
        int startId = MIN_ID;
        for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
            int endId = startId + BATCH_STEP;
            if (endId > MAX_ID) {
                endId = MAX_ID;
            }
            Map param = new HashMap();
            param.put("startId", startId);
            param.put("endId", endId);
            logger.info("param==" + param);
            List<Map<String, String>> list = tlBonusTopDao.getTlBonusTopList(param);
            startId = endId + 1;
            //将相同的用户名的中奖信息 放入到一个文件里
            for (int j = 0; j < list.size(); j++) {
                Map<String, String> temp = list.get(j);
                String lotteryClassCode = temp.get("LOTTERY_CLASS_CODE");
                String userName = temp.get("USER_NAME");
                String bonusAmount = temp.get("BONUS_AMOUNT");
                int location = Math.abs(userName.hashCode()) % pageCount;
                String line = userName + "^" + lotteryClassCode + "^" + bonusAmount + "\r\n";
                try {
                    FileUtils.writeStringToFile(new File(STORE_PATH, location + ".txt"), line, "UTF-8", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] lotteryClassCodeArr = new String[]{
                "300"
        };

        //取前200条记录
        String lotteryClassCode = "300";
        Integer topNumber = 200;

        //按照由大到小的顺序排序
        Object[] sortedList = new Object[topNumber];

        //遍历 每一个文件
        for (int location = 0; location < pageCount; location++) {
            Map<String, Map<String, String>> userMap = new HashMap<String, Map<String, String>>();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(STORE_PATH, location + ".txt")));
                String str = "";
                while ((str = bufferedReader.readLine()) != null) {
                    String[] temparr = StringUtils.split(str, '^');
                    if (!temparr[1].equals(lotteryClassCode)) {
                        continue;
                    }
                    String userName = temparr[0];
                    String bonus = temparr[2];
                    if (userMap.containsKey(userName)) {
                        Map<String, String> map = userMap.get(userName);
                        BigDecimal totalMoney = new BigDecimal(map.get("BONUS")).add(new BigDecimal(bonus));
                        map.put("BONUS", totalMoney.toString());
                        userMap.put(userName, map);
                    } else {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("BONUS", bonus);
                        map.put("USERNAME", userName);
                        map.put("WINCOUNT", "1");
                        userMap.put(userName, map);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Map.Entry entry : userMap.entrySet()) {
                Map map = (Map) entry.getValue();
                sort(sortedList, map);
            }

        }

        for (int i = 0; i < sortedList.length; i++) {
            FileUtils.writeStringToFile(new File(STORE_PATH, "rank.txt"), ((Map) sortedList[i]).toString() + "\r\n", "UTF-8", true);
        }

        logger.info("总耗时 ：" + (System.currentTimeMillis() - startTime));

    }

    public static void sort(Object[] sortedList, Map<String, String> map) {
        int length = sortedList.length;
        int index = -1;
        for (int i = 0; i < length; i++) {
            Map object = (HashMap) sortedList[i];
            if (object != null) {
                BigDecimal bonus = new BigDecimal(object.get("BONUS").toString());
                BigDecimal currentBonus = new BigDecimal(map.get("BONUS").toString());
                if (currentBonus.doubleValue() > bonus.doubleValue()) {
                    index = i;
                    break;
                }
            } else {
                index = i;
                break;
            }
        }
        if (index > -1) {
            //将索引后的数据(包括索引数据）后移
            Object replaceObject = map;
            Object temp = null;
            for (int i = index; i < length; i++) {
                if (replaceObject == null) {
                    replaceObject = temp;
                }
                if (i + 1 <= length) {
                    temp = sortedList[i];
                    sortedList[i] = replaceObject;
                    replaceObject = null;
                } else {
                    temp = null;
                    replaceObject = null;
                }
            }
        }


    }

    public static void main(String[] args) {
        Object[] arr = new Object[5];
        Map map1 = new HashMap();
        map1.put("BONUS", "123");

        Map map2 = new HashMap();
        map2.put("BONUS", "12");

        Map map3 = new HashMap();
        map3.put("BONUS", "10000");

        Map map4 = new HashMap();
        map4.put("BONUS", "1");

        sort(arr, map1);
        sort(arr, map3);
        sort(arr, map2);
        sort(arr, map4);

        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }

    }

}
