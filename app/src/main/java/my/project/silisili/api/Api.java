package my.project.silisili.api;

import my.project.silisili.application.Silisili;

public class Api {
    //搜索
    public static String SEARCH_API = Silisili.DOMAIN + "/e/search/index.php";
    public static String SEARCH_GET_API = Silisili.DOMAIN + "/e/search/result/index.php?searchid=%s&page=%s";
    //检测更新
    public final static String CHECK_UPDATE = "https://api.github.com/repos/670848654/Silisili/releases/latest";
}
