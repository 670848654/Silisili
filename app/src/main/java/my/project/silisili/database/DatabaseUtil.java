package my.project.silisili.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeDescHeaderBean;

public class DatabaseUtil {
    public static SQLiteDatabase db;
    public static String DB_PATH = Environment.getExternalStorageDirectory() + "/SilisiliAnime/Database/silisili.db";

    /**
     * 创建tables
     */
    public static void CREATE_TABLES() {
        db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        db.execSQL("create table if not exists t_favorite(id integer primary key autoincrement, f_title text, f_url text, f_img text, f_region text, f_year text, f_tag text, f_desc text, f_show text, f_state text)");
        db.execSQL("create table if not exists t_anime(id integer primary key autoincrement, f_id text, f_title text)");
        db.execSQL("create table if not exists t_index(id integer primary key autoincrement, f_pid text, f_url text)");
    }

    /**
     * 关闭数据库连接
     */
    public static void closeDB(){
        db.close();
    }

    /**
     * 新增点击过的番剧名称
     * @param title
     */
    public static void addAnime(String title){
        if (!checkAnime(title))
            db.execSQL("insert into t_anime values(?,?,?)",
                new Object[] { null, UUID.randomUUID().toString(), title});
    }

    /**
     * 检查番剧名称是否存在
     * @param title
     * @return
     */
    public static boolean checkAnime(String title){
        String Query = "select * from t_anime where f_title =?";
        Cursor cursor = db.rawQuery(Query, new String[] { title });
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * 获取番剧fid
     * @param title
     * @return
     */
    public static String getAnimeID(String title){
        String Query = "select * from t_anime where f_title =?";
        Cursor cursor = db.rawQuery(Query, new String[] { title });
        cursor.moveToNext();
        return cursor.getString(1);
    }

    /**
     * 新增点击过的剧集名称
     * @param fid 父id
     * @param url 播放地址
     */
    public static void addIndex(String fid, String url){
        if (!checkIndex(fid, url))
            db.execSQL("insert into t_index values(?,?,?)",
                    new Object[] { null, fid, url});
    }

    /**
     * 检查剧集名称是否存在
     * @param fid 父id
     * @param url 播放地址
     * @return
     */
    private static boolean checkIndex(String fid, String url){
        String Query = "select * from t_index where f_pid =? and f_url like ?";
        Cursor cursor = db.rawQuery(Query, new String[] { fid, "%" + url + "%" });
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * 检查当前fid所有剧集
     * @param fid 番剧ID
     * @return
     */
    public static String queryAllIndex(String fid){
        StringBuffer buffer = new StringBuffer();
        String Query = "select * from t_index where f_pid =?";
        Cursor c = db.rawQuery(Query, new String[] { fid });
        while (c.moveToNext()) {
            buffer.append(c.getString(2));
        }
        c.close();
        return buffer.toString();
    }

    /**
     * 查询用户收藏的番剧
     */
    public static List<AnimeDescHeaderBean> queryAllFavorite() {
        List<AnimeDescHeaderBean> list = new ArrayList<>();
        Cursor c = db.rawQuery("select * from t_favorite order by id desc", null);
        while (c.moveToNext()) {
            AnimeDescHeaderBean bean = new AnimeDescHeaderBean();
            bean.setName(c.getString(1));
            bean.setUrl(c.getString(2));
            bean.setImg(c.getString(3));
            bean.setRegion(c.getString(4));
            bean.setYear(c.getString(5));
            bean.setTag(c.getString(6));
            bean.setDesc(c.getString(7));
            bean.setShow(c.getString(8));
            bean.setState(c.getString(9));
            list.add(bean);
        }
        c.close();
        return list;
    }

    /**
     * 收藏or删除收藏
     * @param bean
     * @return true 收藏成功 false 移除收藏
     */
    public static boolean favorite(AnimeDescHeaderBean bean){
        if (checkFavorite(bean.getName())){
            deleteFavorite(bean.getName());
            return false;
        }else {
            addFavorite(bean);
            return true;
        }
    }

    /**
     * 添加到收藏
     * @param bean
     */
    private static void addFavorite(AnimeDescHeaderBean bean){
        db.execSQL("insert into t_favorite values(?,?,?,?,?,?,?,?,?,?)",
                new Object[] { null,
                bean.getName(),
                bean.getUrl().substring(Silisili.DOMAIN.length()),
                bean.getImg().contains(Silisili.DOMAIN) ? bean.getImg().substring(Silisili.DOMAIN.length()) : bean.getImg(),
                bean.getRegion(),
                bean.getYear(),
                bean.getTag(),
                bean.getDesc(),
                bean.getShow(),
                bean.getState()});
    }

    /**
     * 删除收藏
     * @param title
     */
    public static void deleteFavorite(String title){
        db.execSQL("delete from t_favorite where f_title=?", new String[]{title});
    }

    /**
     * 检查番剧是否收藏
     * @param title
     * @return
     */
    public static boolean checkFavorite(String title){
        String Query = "select * from t_favorite where f_title =?";
        Cursor cursor = db.rawQuery(Query, new String[] { title });
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}