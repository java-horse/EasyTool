package easy.handler.sign;

public class JueJinConstants {

    public static final String DATA = "data";
    public static final String ERR_NO = "err_no";
    public static final String MINERAL = "矿石";
    public static final String BUG = "Bug";

    private static final String BASE_URL = "https://api.juejin.cn/";

    /**
     * 查询本日签到状态
     */
    public static final String TODAY_STATUS = BASE_URL + "growth_api/v1/get_today_status";

    /**
     * 查询免费抽奖次数
     */
    public static final String LOTTERY_CONFIG = BASE_URL + "growth_api/v1/lottery_config/get";

    /**
     * 签到
     */
    public static final String CHECK = BASE_URL + "growth_api/v1/check_in";

    /**
     * 轮盘抽奖
     */
    public static final String DRAW = BASE_URL + "growth_api/v1/lottery/draw";

    /**
     * 查询剩余积分
     */
    public static final String CUR_POINT = BASE_URL + "growth_api/v1/get_cur_point";

    /**
     * 查询签到天数
     */
    public static final String COUNTS = BASE_URL + "growth_api/v1/get_counts";

    /**
     * 沾喜气
     */
    public static final String DIP_LUCKY = BASE_URL + "growth_api/v1/lottery_lucky/dip_lucky";

    /**
     * 查询可以沾喜气的用户列表
     */
    public static final String GLOBAL_BIG = BASE_URL + "growth_api/v1/lottery_history/global_big";

    /**
     * 查询未收集的BUG
     */
    public static final String NOT_COLLECT_BUG = BASE_URL + "user_api/v1/bugfix/not_collect";

    /**
     * 开始收集BUG
     */
    public static final String COLLECT_BUG = BASE_URL + "user_api/v1/bugfix/collect";

    /**
     * 查询我的道具
     */
    public static final String MY_TOOL = BASE_URL + "growth_api/v1/lottery_history/items_by_page";

}
