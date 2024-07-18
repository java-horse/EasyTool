package easy.handler.sign;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import easy.base.Constants;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.handler.ding.DingBotModel;
import easy.handler.ding.DingBotParam;
import easy.handler.ding.DingTypeEnum;
import easy.helper.ServiceHelper;
import easy.util.JsonUtil;
import easy.util.NotifyUtil;

import java.util.*;

public class JueJinSignService {

    private final WidgetConfig.SignConfig signConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState().getSignConfig();

    /**
     * 自动签到
     *
     * @author mabin
     * @date 2024/07/16 10:33
     */
    public void autoSign() {
        StopWatch watch = new StopWatch();
        watch.start();
        Map<String, Object> resultMap = new HashMap<>(16);
        resultMap.put("startTime", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_FORMAT));
        // 签到
        if (checkAndSignIn(resultMap)) {
            // 抽奖
            draw(resultMap);
            // 签到总天数, 连续签到天数
            signDays(resultMap);
            // 尝试收集BUG
            collectBug(resultMap);
            // 我的道具
            displayMyTool(resultMap);
        }
        // 发送钉钉消息
        watch.stop();
        resultMap.put("endTime", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_FORMAT));
        sendDingMsg(resultMap, watch.getTotalTimeMillis());
    }

    /**
     * 签到处理
     *
     * @param
     * @param resultMap
     * @return void
     * @author mabin
     * @date 2023/3/9 9:50
     **/
    private boolean checkAndSignIn(Map<String, Object> resultMap) {
        // [{"err_no":0,"err_msg":"success","data":false}]
        // [{"err_no":403,"err_msg":"must login","data":null}]
        String checkResponse = getRequest(JueJinConstants.TODAY_STATUS);
        JsonObject checkObject = JsonUtil.fromObject(checkResponse);
        Integer errNo = checkObject.get(JueJinConstants.ERR_NO).getAsInt();
        if (ObjectUtil.notEqual(Constants.NUM.ZERO, errNo)) {
            resultMap.put("cookie", false);
            return false;
        } else if (!checkObject.get(JueJinConstants.DATA).getAsBoolean()) {
            // {"err_no":0,"err_msg":"success","data":{"incr_point":700,"sum_point":734}}
            String signResponse = postRequest(JueJinConstants.CHECK, null);
            JsonObject signObject = JsonUtil.fromObject(signResponse);
            if (ObjectUtil.equal(Constants.NUM.ZERO, signObject.get(JueJinConstants.ERR_NO).getAsInt())) {
                JsonObject dataObject = signObject.get(JueJinConstants.DATA).getAsJsonObject();
                resultMap.put("incrPoint", dataObject.get("incr_point").getAsLong());
                resultMap.put("sumPoint", dataObject.get("sum_point").getAsLong());
            }
        }
        return true;
    }

    /**
     * 抽奖
     *
     * @param
     * @param resultMap
     * @return void
     * @author mabin
     * @date 2023/3/9 10:29
     **/
    private void draw(Map<String, Object> resultMap) {
        List<String> lotteryNameList = new ArrayList<>();
        // 免费抽奖
        String lotteryConfigResponse = getRequest(JueJinConstants.LOTTERY_CONFIG);
        JsonObject lotteryConfigObject = JsonUtil.fromObject(lotteryConfigResponse);
        int freeDrawCount = Constants.NUM.ZERO;
        if (ObjectUtil.equal(Constants.NUM.ZERO, lotteryConfigObject.get(JueJinConstants.ERR_NO).getAsInt())) {
            int freeCount = lotteryConfigObject.get(JueJinConstants.DATA).getAsJsonObject().get("free_count").getAsInt();
            if (freeCount > Constants.NUM.ZERO) {
                // 免费抽奖一次
                // {"err_no":0,"err_msg":"success","data":{"id":19,"lottery_id":"6981716980386496552","lottery_name":"63矿石","lottery_type":1,"lottery_image":"https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/32ed6a7619934144882d841761b63d3c~tplv-k3u1fbpfcp-no-mark:0:0:0:0.image","lottery_desc":"","lottery_cost":0,"lottery_reality":2,"recycle_point":0,"donate_point":0,"draw_lucky_value":10,"total_lucky_value":63,"history_id":"7392116210889916456"}}
                String drawResponse = postRequest(JueJinConstants.DRAW, null);
                JsonObject drawObject = JsonUtil.fromObject(drawResponse);
                if (ObjectUtil.equal(Constants.NUM.ZERO, drawObject.get(JueJinConstants.ERR_NO).getAsInt())) {
                    String lotteryName = drawObject.get(JueJinConstants.DATA).getAsJsonObject().get("lottery_name").getAsString();
                    lotteryNameList.add(lotteryName);
                    freeDrawCount++;
                }
            }
        }
        // 无限积分抽奖
        String curPointResponse = getRequest(JueJinConstants.CUR_POINT);
        JsonObject curPointObject = JsonUtil.fromObject(curPointResponse);
        // 抽奖过程中统计的矿石数量
        int originPoint = curPointObject.get(JueJinConstants.DATA).getAsInt();
        Integer summaryPoint = originPoint;
        int rouletteDrawCount = Constants.NUM.ZERO;
        if (Opt.ofNullable(signConfig.getDrawSwitch()).orElse(Boolean.FALSE)) {
            while (summaryPoint >= Opt.ofNullable(signConfig.getReserved()).orElse(120000)) {
                if (rouletteDrawCount > Constants.NUM.ZERO) {
                    ThreadUtil.sleep(Opt.ofNullable(signConfig.getDrawInternal()).orElse(1000L));
                }
                String drawResponse = postRequest(JueJinConstants.DRAW, null);
                JsonObject drawObject = JsonUtil.fromObject(drawResponse);
                if (ObjectUtil.equal(Constants.NUM.ZERO, drawObject.get(JueJinConstants.ERR_NO).getAsInt())) {
                    // 抽奖成功, 积分减200
                    summaryPoint -= Constants.NUM.TWO_HUNDRED;
                    String lotteryName = drawObject.get(JueJinConstants.DATA).getAsJsonObject().get("lottery_name").getAsString();
                    // 如果奖品是积分, 累加到总积分
                    if (StrUtil.contains(lotteryName, JueJinConstants.MINERAL)) {
                        summaryPoint += Convert.toInt(StrUtil.replace(lotteryName, JueJinConstants.MINERAL, StrUtil.EMPTY));
                    }
                    lotteryNameList.add(lotteryName);
                    rouletteDrawCount++;
                } else {
                    // 抽奖请求异常时, 睡眠后重试
                    ThreadUtil.sleep(Opt.ofNullable(signConfig.getDrawInternal()).orElse(2000L));
                }
            }
        }
        resultMap.put("surplusPoint", summaryPoint);
        resultMap.put("lotteryNameList", lotteryNameList);
        resultMap.put("drawCount", freeDrawCount + rouletteDrawCount);
        resultMap.put("originPoint", originPoint);
    }

    /**
     * 签到总天数, 连续签到天数
     *
     * @param resultMap
     * @return void
     * @author mabin
     * @date 2023/3/9 11:14
     **/
    private void signDays(Map<String, Object> resultMap) {
        String daysResponse = getRequest(JueJinConstants.COUNTS);
        JsonObject resObject = JsonUtil.fromObject(daysResponse).get(JueJinConstants.DATA).getAsJsonObject();
        resultMap.put("contCount", resObject.get("cont_count").getAsInt());
        resultMap.put("sumCount", resObject.get("sum_count").getAsInt());
    }

    /**
     * 尝试收集BUG
     *
     * @param resultMap
     * @return void
     * @author mabin
     * @date 2023/3/10 14:33
     **/
    private void collectBug(Map<String, Object> resultMap) {
        String notResponse = postRequest(JueJinConstants.NOT_COLLECT_BUG, null);
        JsonObject notObject = JsonUtil.fromObject(notResponse);
        int bugCount = 0;
        if (ObjectUtil.equal(Constants.NUM.ZERO, notObject.get(JueJinConstants.ERR_NO).getAsInt())) {
            JsonArray bugArray = notObject.get(JueJinConstants.DATA).getAsJsonArray();
            if (ArrayUtil.isNotEmpty(bugArray)) {
                for (JsonElement lottery : bugArray) {
                    JsonObject bugObject = lottery.getAsJsonObject();
                    bugObject.remove("bug_show_type");
                    bugObject.remove("is_first");
                    String response = postRequest(JueJinConstants.COLLECT_BUG, JsonUtil.fromMap(JsonUtil.toJson(bugObject)));
                    if (ObjectUtil.equal(Constants.NUM.ZERO, JsonUtil.fromObject(response).get(JueJinConstants.ERR_NO).getAsInt())) {
                        bugCount++;
                    }
                }
            }
        }
        resultMap.put("collectBug", bugCount);
    }

    /**
     * 获取我的道具
     *
     * @param resultMap
     * @return void
     * @author mabin
     * @date 2023/3/10 14:59
     **/
    private void displayMyTool(Map<String, Object> resultMap) {
        String response = postRequest(JueJinConstants.MY_TOOL, null);
        JsonObject resObject = JsonUtil.fromObject(response);
        if (ObjectUtil.equal(Constants.NUM.ZERO, resObject.get(JueJinConstants.ERR_NO).getAsInt())) {
            JsonObject dataObject = resObject.get(JueJinConstants.DATA).getAsJsonObject();
            if (ObjectUtil.isNotNull(dataObject)) {
                JsonArray lotteryArray = dataObject.get("lottery_histories").getAsJsonArray();
                if (ArrayUtil.isNotEmpty(lotteryArray)) {
                    List<String> lotteryList = new ArrayList<>();
                    for (JsonElement lottery : lotteryArray) {
                        JsonObject lotteryObject = lottery.getAsJsonObject();
                        lotteryList.add(lotteryObject.get("lottery_name").getAsString() + " x " + lotteryObject.get("lottery_count").getAsInt());
                        resultMap.put("myTool", CollUtil.isNotEmpty(lotteryList) ? CollUtil.join(lotteryList, "，") : "暂无道具");
                    }
                }
            }
        }
    }

    /**
     * 发送钉钉消息
     *
     * @param resultMap
     * @param totalTimeMillis
     * @return void
     * @author mabin
     * @date 2023/3/9 14:21
     **/
    private void sendDingMsg(Map<String, Object> resultMap, long totalTimeMillis) {
        if (MapUtil.isEmpty(resultMap)) {
            return;
        }
        DingBotModel model = new DingBotModel();
        model.setTitle("掘金签到");
        model.setReceiver(Set.of("@all"));
        model.setSendType(DingTypeEnum.ACTION_CARD.getType());
        model.setBtnOrientation(Integer.toString(Constants.NUM.ONE));
        List<DingBotParam.ActionCardVO.BtnVO> btnVOList = new ArrayList<>();
        DingBotParam.ActionCardVO.BtnVO btnVO = new DingBotParam.ActionCardVO.BtnVO();
        btnVO.setTitle(Constants.PLUGIN_NAME);
        btnVO.setActionURL(Constants.JETBRAINS_URL);
        btnVOList.add(btnVO);
        model.setBtns(JsonUtil.toJson(btnVOList));
        // 账号是否正常
        if (ObjectUtil.isNotNull(resultMap.get("cookie"))) {
            model.setContent("![1](https://horse-blog.oss-cn-hangzhou.aliyuncs.com/202303/NMZlf0piTA.png) \n" +
                    "#### **掘金** \n" +
                    "---  \n" +
                    "**Cookie状态：** Cookie已失效，请重新配置！");
        } else {
            String msgTemplate = "![1](https://horse-blog.oss-cn-hangzhou.aliyuncs.com/202303/NMZlf0piTA.png) \n" +
                    "#### **掘金** \n" +
                    "---  \n" +
                    "**Cookie状态：** Cookie正常 \n\n" +
                    "**签到：** 总签到 {} 天，连续签到 {} 天 \n\n" +
                    "**轮盘抽奖：** 抽奖 {} 次，矿石 x {}，BUG x {}，其他 x {} \n\n" +
                    "**收集BUG：** BUG x {} \n\n" +
                    "**矿石变化：** 初始 {} 矿石，剩余 {} 矿石 \n\n" +
                    "**我的道具：** {} \n\n" +
                    "**签到耗时：** 起始 {}-{}，共耗时 {}ms";
            JsonObject resultObject = JsonUtil.fromObject(JsonUtil.toJson(resultMap));
            List<String> lotteryNameList = JsonUtil.fromJson(JsonUtil.toJson(resultObject.get("lotteryNameList")), new TypeToken<List<String>>() {
            }.getType());
            long mineralCount = 0;
            long bugCount = 0;
            long otherCount = 0;
            if (ArrayUtil.isNotEmpty(lotteryNameList)) {
                mineralCount = lotteryNameList.stream().filter(lottery -> StrUtil.containsIgnoreCase(lottery, JueJinConstants.MINERAL))
                        .map(lottery -> StrUtil.replace(lottery, JueJinConstants.MINERAL, StrUtil.EMPTY))
                        .mapToLong(Long::parseLong).sum();
                bugCount = lotteryNameList.stream().filter(lottery -> StrUtil.containsIgnoreCase(lottery, JueJinConstants.BUG)).count();
                otherCount = lotteryNameList.size() - lotteryNameList.stream().filter(lottery -> StrUtil.containsIgnoreCase(lottery, JueJinConstants.MINERAL)).count() - bugCount;
            }
            model.setContent(StrUtil.format(msgTemplate, resultObject.get("sumCount").getAsString(), resultObject.get("contCount").getAsString(),
                    resultObject.get("drawCount").getAsString(), mineralCount, bugCount, otherCount, resultObject.get("collectBug").getAsString(),
                    resultObject.get("originPoint").getAsString(), resultObject.get("surplusPoint").getAsString(), resultObject.get("myTool").getAsString(),
                    resultObject.get("startTime").getAsString(), resultObject.get("endTime").getAsString(), totalTimeMillis));
        }
        NotifyUtil.notify(model);
    }

    /**
     * GET请求
     *
     * @param url
     * @return java.lang.String
     * @author mabin
     * @date 2023/3/9 15:27
     **/
    private String getRequest(String url) {
        return HttpRequest.get(url)
                .timeout(10000)
                .setFollowRedirects(false)
                .cookie(Opt.ofNullable(signConfig.getCookie()).get())
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36 Edg/95.0.1020.53")
                .execute().body();
    }

    /**
     * POST请求
     *
     * @param url
     * @param paramMap
     * @return java.lang.String
     * @author mabin
     * @date 2023/3/9 15:27
     **/
    private String postRequest(String url, Map<String, Object> paramMap) {
        HttpRequest httpRequest = HttpRequest.post(url)
                .timeout(10000)
                .setFollowRedirects(false)
                .cookie(Opt.ofNullable(signConfig.getCookie()).get());
        if (MapUtil.isNotEmpty(paramMap)) {
            httpRequest.form(paramMap);
        }
        return httpRequest.execute().body();
    }

}
