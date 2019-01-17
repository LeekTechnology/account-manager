package com.wx.account.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 公共数据转换或者数据校验的转换类
 * @author changjinlin
 * @date 2016-11-04
 */
public class CommonUtils {

    public static <T> boolean isNull(T... objects) {
        for (T obj : objects)
            if (null == obj) {
                return true;
            }
        return false;
    }

    public static <T> boolean notNull(T... objects) {
        for (T obj : objects)
            if (null == obj) {
                return false;
            }
        return true;
    }

    public static boolean isBlank(String... objects) {

        for (String obj : objects) {
            if (StringUtils.isNotEmpty(obj)) {
                return false;
            }
        }
        return true;
    }

    public static boolean notBlank(String... objects) {
        for (String obj : objects) {
            if (StringUtils.isBlank(obj)) {
                return false;
            }
        }
        return true;
    }

    public static boolean notEmpty(Collection collection) {

        if (isNull(collection)) {
            return false;
        }
        if (collection.size() <= 0) {
            return false;
        }

        return true;
    }

    public static boolean isEmpty(Collection collection) {
        if (isNull(collection)) {
            return true;
        }
        if (collection.size() <= 0) {
            return true;
        }
        return false;
    }

    public static <T extends Collection> boolean hasTheSameItem(T main, T second) {
        if (CommonUtils.notEmpty(main) && CommonUtils.notEmpty(second)) {
            for (Object objects : main) {
                if (second.contains(objects)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static List<String> trimAndSplitBySemicolon(String s) {
        return (ArrayList) Arrays.asList(s.trim().split("\\s*;\\s*"));
    }


    /**
     * 对BigDecimal做非null处理,如果参数为null,默认值返回0
     * @param value
     * @return
     */
    public static BigDecimal requireNonNull(BigDecimal value){

        if (isNull(value)){
            return BigDecimal.ZERO;
        }
        return value;
    }

    /**
     * 用户生日转换成yyyy-MM-dd格式
     *
     * @param userBirthday
     * @return
     */
    public static String formatUserBirthday(String userBirthday) {

        if (isBlank(userBirthday) || userBirthday.matches("^\\s*$")) {
            return "";
        }
        if (userBirthday.contains("-")) {
            userBirthday = userBirthday.replace("-", "");
        }
        if (userBirthday.length() != 8) {
            return "";
        }

        return DateUtils.formatDateToDB(userBirthday);
    }

    /**
     * 获取卡类型动态密码的方法
     * 交易等级不为2 ,直接返回原动态交易密码
     * 动态密码为空或者长度不足四位,重新生成四位动态密码,否则直接返回
     * @param transSafeLevel
     * @param dynamicPWD
     * @return
     */
    public static String getDynamicPWD(int transSafeLevel,String dynamicPWD){

        if (transSafeLevel != 2){
            return dynamicPWD;
        }

        if (isBlank(dynamicPWD) && dynamicPWD.length() < 4){
            String dtwd = "0000" + new Random().nextInt(10000);
            return dtwd.substring(dtwd.length() - 4);
        }

        return dynamicPWD;
    }

    /**
     * 对bigdecimal类型的数据做非空判断
     * @param value
     * @return
     */
    public static BigDecimal getAndSetDefaultBigDecimal(BigDecimal value){

        return Optional.ofNullable(value)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 对bigdecimal类型的数据做非空判断,如果值小于0,则返回0
     * @param value
     * @return
     */
    public static BigDecimal getAndSetBigThenZero(BigDecimal value){

        return Optional.ofNullable(value)
                .filter(s -> s.compareTo(BigDecimal.ZERO) > 0)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 检测传递的值是否为空或者小于0,如果为空或者小于0 则返回false
     * @param value
     * @return
     */
    public static boolean verifyBigThanZero(BigDecimal value){

        return Optional.ofNullable(value)
                .map(s -> s.compareTo(BigDecimal.ZERO) > 0)
                .orElse(false);
    }

    /**
     * 检测传递的值是否为空或者小于0,如果为空或者小于0 则返回false
     * @param value
     * @return
     */
    public static boolean verifyLittleThanZero(BigDecimal value){

        return Optional.ofNullable(value)
                .map(s -> s.compareTo(BigDecimal.ZERO) < 0)
                .orElse(false);
    }

    /**
     * 判断输入的验证码是否为空,如果不为空随机生成验证码返回
     * @param authCode
     * @return
     */
    public static String generateAuthCode(String authCode){

        return Optional.ofNullable(authCode)
                .orElseGet(() -> {
                    DecimalFormat df = new DecimalFormat("######");
                    return df.format(Math.random() * 9999);});

    }

    /**
     * 验证手机号码是否合法
     * @param mobile
     * @return
     */
    public static boolean isMobileNO(String mobile){
        return Optional.ofNullable(mobile)
                .map(s -> {
                    Pattern p = Pattern.compile("^(1)\\d{10}$");
                    Matcher m = p.matcher(s);
                    return m.matches();})
                .orElse(false);
    }

    /**
     * 生成四位随机动态验证码
     * @param length
     * @return
     */
    public static String genDynamicPWD(int length) {
        if(length < 1) {
            length = 4;
        }

        int max = (int)Math.pow(10.0D, (double)length) - 1;
        StringBuilder sb = new StringBuilder();

        for(int value = 0; value < length; ++value) {
            sb.append("#");
        }

        String var5 = (new DecimalFormat(sb.toString())).format(Math.random() * (double)max);

        for(int i = var5.length(); i < length; ++i) {
            var5 = "0" + var5;
        }

        return var5;
    }

    public static Integer string2Int(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    public static Long string2Long(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return -1l;
        }
    }

    public static BigDecimal string2BigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public static String repairLengthValue(String value, int len) {
        if (value.length() == len) {
            return value;
        }
        for (int i = 0; i < len - value.length(); i++) {
            value = "0" + value;
        }
        return value;
    }

    public static boolean validateNumber(String value) {
        try {
            new BigDecimal(value);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateBigDecimal(BigDecimal value) {
        if (value != null && value.compareTo(BigDecimal.ZERO.ZERO) >0) {
            return true;
        }
        return false;
    }

    /**
     * 给map的key值累加和.
     * @param id
     * @param step
     * @param map
     */
    public static void calIntegerMap(Long id, int step, Map<Long, Integer> map) {
        Integer num = map.get(id);
        if (num == null) {
            num = 0;
        }
        map.put(id, num + step);
    }

    /**
     * 给map的key值累加和.
     * @param id
     * @param step
     * @param map
     */
    public static void calBigdecimalMap(Long id, BigDecimal step, Map<Long, BigDecimal> map) {
        BigDecimal num = map.get(id);
        if (num == null) {
            num = BigDecimal.ZERO;
        }
        map.put(id, num.add(step));
    }

    /**
     * 计算浮点数总和
     *
     * @param id
     */
    public static void calBigdecimalValues(Long id, BigDecimal value, Map<Long, BigDecimal> map) {
        BigDecimal num = map.get(id);
        if (num == null) {
            num = BigDecimal.ZERO;
        }
        map.put(id, num.add(value));
    }

    public static void calStringMap(String key, BigDecimal step, Map<String, BigDecimal> map) {
        BigDecimal num = map.get(key);
        if (num == null) {
            num = BigDecimal.ZERO;
        }
        map.put(key, num.add(step));
    }

    /**
     * 把指定值添加到map对象列表中.
     *
     * @param dataMap
     * @param key
     * @param value
     */
    public static void addData2Map(Map<String, List<String>> dataMap, String key, String value) {
        if (CommonUtils.isBlank(value)) {
            return;
        }
        List<String> dataList = dataMap.get(key);
        if (CommonUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
            dataMap.put(key, dataList);
        }
        if (!dataList.contains(value)) {
            dataList.add(value);
        }
    }

    /**
     * 把字符串转成list结构.
     * @param values
     * @return
     */
    public static List<Long> transString2LongList(String values) {
        List<Long> result = new ArrayList<>();
        if (CommonUtils.isBlank(values)) {
            return result;
        }
        String[] arr = values.split(",");
        for (String s : arr) {
            Long val = string2Long(s);
            if (val != null && val > 0) result.add(val);
        }
        return result;
    }


    /**
     * 把字符串转成list结构.
     * @param values
     * @return
     */
    public static List<String> transString2List(String values) {
        List<String> result = new ArrayList<>();
        if (CommonUtils.isBlank(values)) {
            return result;
        }
        String[] arr = values.split(",");
        for (String s : arr) {
            if (isBlank(s)) {
                continue;
            }
            result.add(s);
        }
        return result;
    }

    public static BigDecimal transInt2Bigdecimal100(Integer value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 把json个数的数据解析成指定格式数据
     *
     * @param jsonData
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseJSON2ObjectList(String jsonData, Class<T> clz) {
        try {
            return JSONObject.parseArray(jsonData, clz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证是否是数字.
     *
     * @param value
     * @return
     */
    public static boolean validateBigdecimalVal(String value) {
        try {
            new BigDecimal(value);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查金钱的长度.
     *
     * @param value
     * @return
     */
    public static boolean validateSalaryLength(BigDecimal value) {
        if (value == null) {
            return true;
        }
        if (value.compareTo(BigDecimal.valueOf(100000)) > 0) {
            return false;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        return true;
    }

    /**
     * 把long型list转成字符串.
     *
     * @param list
     * @return
     */
    public static String transList2String(List<Long> list) {
        if (isEmpty(list)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Long obj : list) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(obj);
        }

        return builder.toString();
    }
}
