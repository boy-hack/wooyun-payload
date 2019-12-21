package wooyun;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.String.join;
import static java.lang.String.valueOf;

public class Wooyun {
    public StringBuilder sb;
    private String shuimugan;
    private TableStruct struct;

    public Wooyun() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("wooyun.json");
        if (is==null) {
            throw new NullPointerException();
        }
//        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("wooyun.json");
        sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        int ch;
        while ((ch = br.read()) != -1) {
            sb.append((char) ch);
        }
        shuimugan = "https://shuimugan.com/bug/view?bug_no=";
        struct = new TableStruct();
    }

    private static Object[] concat(Object[] a, Object[] b) {
        Object[] c = new Object[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }


    /*
        输入:一段url，host，get参数，cookie参数，post参数
        返回: path统计，host统计，各种参数统计
               漏洞类型占比(path,host)
     */
    public TableStruct Search(URL url, String[] GetParams, String[] CookieParams, String[] PostParams) {
        JSONArray jobj;
        jobj = JSONArray.parseArray(sb.toString());
        String[] path_array = url.getPath().split("/");
        String path = path_array[path_array.length - 1];
        String host = url.getHost();

        Map<String, Integer> pathTJMap = new TreeMap<String, Integer>();
        List<Object[]> pathList = new ArrayList<Object[]>();

        Map<String, Integer> hostTJMap = new TreeMap<String, Integer>();
        List<Object[]> hostList = new ArrayList<Object[]>();

        Map<String, Integer> getMap = new TreeMap<String, Integer>();
        Map<String, Integer> cookieMap = new TreeMap<String, Integer>();
        Map<String, Integer> postMap = new TreeMap<String, Integer>();

        for (Object object : jobj) {
            JSONObject jsonObject = (JSONObject) object;
            String BugType = jsonObject.getString("type");
            if (BugType.equals("")) {
                BugType = "不知名的漏洞类型";
            }
            String BugID = jsonObject.getString("bugid");
            String Title = jsonObject.getString("title");

            JSONArray urls = jsonObject.getJSONArray("url");
            JSONArray targets = jsonObject.getJSONArray("target");
            JSONArray get_params = jsonObject.getJSONArray("params");
            JSONArray cookie_params = jsonObject.getJSONArray("cookie_params");
            JSONArray post_params = jsonObject.getJSONArray("post_params");
            for (Object obj_url : urls)
                try {
                    URL _url = new URL((String) obj_url);
                    String[] path_array2 = _url.getPath().split("/");
                    if (path_array2.length > 0) {
                        String _path = path_array2[path_array2.length - 1];
                        if (!_path.equals("") && _path.equalsIgnoreCase(path)) {
                            int count = 1;
                            if (pathTJMap.containsKey(BugType)) {
                                count = pathTJMap.get(BugType) + 1;
                            }
                            pathTJMap.put(BugType, count);
                            pathList.add(new Object[]{BugType, Title, shuimugan + BugID, _url.toString()});
                            break;
                        }
                    }
                } catch (MalformedURLException | ArrayIndexOutOfBoundsException ignored) {
                }
            for (Object _target : targets) {
                String target = (String) _target;
                if (target.equalsIgnoreCase(host)) {
                    int count = 1;
                    if (hostTJMap.containsKey(BugType)) {
                        count = hostTJMap.get(BugType) + 1;
                    }
                    hostTJMap.put(BugType, count);
//                    hostList.add("[" + BugType + "] " + Title + " 漏洞链接:" + shuimugan + BugID);
                    hostList.add(new Object[]{BugType, Title, shuimugan + BugID});
                }
            }

            String flag = "!!flag{aabbc}!!";
            if (GetParams.length > 0) {
                Set<String> param = new HashSet<String>();
                for (Object _get : get_params) {
                    String target = (String) _get;
                    for (String get2 : GetParams) {
                        if (target.equalsIgnoreCase(get2)) {
                            param.add(get2);
                        }
                    }
                }
                if (param.size() > 0) {
                    String[] ret = {BugType, Title, join(",", param), shuimugan + BugID};

                    getMap.put(join(flag, ret), param.size());
                }

            }

            if (CookieParams.length > 0) {
                Set<String> param = new HashSet<String>();
                for (Object _get : cookie_params) {
                    String target = (String) _get;
                    for (String get2 : CookieParams) {
                        if (target.equalsIgnoreCase(get2)) {
                            param.add(get2);
                        }
                    }
                }
                if (param.size() > 0) {
                    String[] ret = {BugType, Title, join(",", param), shuimugan + BugID};
                    cookieMap.put(join(flag, ret), param.size());
                }

            }

            if (PostParams.length > 0) {
                Set<String> param = new HashSet<String>();
                for (Object _get : post_params) {
                    String target = (String) _get;
                    for (String get2 : PostParams) {
                        if (target.equalsIgnoreCase(get2)) {
                            param.add(get2);
                        }
                    }
                }
                if (param.size() > 0) {
                    String[] ret = {BugType, Title, join(",", param), shuimugan + BugID};
                    postMap.put(join(flag, ret), param.size());
                }

            }

        }


        List<Map.Entry<String, Integer>> patTJlist = new ArrayList<Map.Entry<String, Integer>>(pathTJMap.entrySet());

        Collections.sort(patTJlist, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        List<Map.Entry<String, Integer>> hostTJlist = new ArrayList<Map.Entry<String, Integer>>(hostTJMap.entrySet());

        Collections.sort(hostTJlist, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        List<Map.Entry<String, Integer>> getList = new ArrayList<Map.Entry<String, Integer>>(getMap.entrySet());

        getList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        List<Map.Entry<String, Integer>> cookieList = new ArrayList<Map.Entry<String, Integer>>(cookieMap.entrySet());

        cookieList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        List<Map.Entry<String, Integer>> postList = new ArrayList<Map.Entry<String, Integer>>(postMap.entrySet());

        postList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });


        // 输出 path 部分
        if (pathList.size() > 0) {
            StringBuilder ret = new StringBuilder(String.format("相同路径:%s 下历史漏洞及漏洞类型:\n", path));
            int index = 0;
            for (Map.Entry<String, Integer> entry : patTJlist) {
                ret.append(entry.getKey()).append(":").append(entry.getValue()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
                index++;
                if (index % 3 == 0) {
                    ret.append("\n");
                }
                if (index >= 6) {
                    break;
                }
            }
            struct.SetExtraInfo("Path", ret.toString());
            struct.AddColumnNames("Path", new Object[]{"ID", "漏洞类型", "漏洞标题", "乌云镜像地址", "漏洞页面"});
//            struct.AddRowDatas();
            Object[][] rows = new Object[pathList.size()][];
            for (int i = 0; i < pathList.size(); i++) {
                Object[] obj = concat(new Object[]{valueOf(i)}, pathList.get(i));
                rows[i] = obj;
            }
            struct.AddRowDatas("Path", rows);
        }

        // 输出host部分
        if (hostList.size() > 0) {
            StringBuilder ret = new StringBuilder(String.format("相同Host:%s 下历史漏洞及漏洞类型:\n", host));
            int index = 0;
            for (Map.Entry<String, Integer> entry : hostTJlist) {
                ret.append(entry.getKey()).append(":").append(entry.getValue()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
                index++;
                if (index % 3 == 0) {
                    ret.append("\n");
                }
                if (index >= 6) {
                    break;
                }
            }
            struct.SetExtraInfo("Host", ret.toString());
            struct.AddColumnNames("Host", new Object[]{"ID", "漏洞类型", "漏洞标题", "乌云镜像地址"});
            Object[][] rows = new Object[hostList.size()][];
            for (int i = 0; i < hostList.size(); i++) {
                Object[] obj = concat(new Object[]{valueOf(i)}, hostList.get(i));
                rows[i] = obj;
            }
            struct.AddRowDatas("Host", rows);

        }

        // 输出Get参数部分
        String flag_split = "!!flag\\{aabbc\\}!!";
        if (getList.size() > 0) {
            struct.SetExtraInfo("Get Params", "Get参数历史漏洞:");
            struct.AddColumnNames("Get Params", new Object[]{"ID", "漏洞类型", "漏洞标题", "影响参数", "漏洞链接"});
            Object[][] data = new Object[getList.size()][5];
            for (int i = 0; i < getList.size(); i++) {
                Map.Entry<String, Integer> entry = getList.get(i);
                data[i] = concat(new Object[]{String.valueOf(i)}, (Object[]) entry.getKey().split(flag_split));
            }
            struct.AddRowDatas("Get Params", data);
        }

        if (cookieList.size() > 0) {
            struct.SetExtraInfo("Cookie Params", "Cookie参数历史漏洞:");
            struct.AddColumnNames("Cookie Params", new Object[]{"ID", "漏洞类型", "漏洞标题", "影响参数", "漏洞链接"});
            Object[][] data = new Object[cookieList.size()][5];
            for (int i = 0; i < cookieList.size(); i++) {
                Map.Entry<String, Integer> entry = cookieList.get(i);
                data[i] = concat(new Object[]{String.valueOf(i)}, (Object[]) entry.getKey().split(flag_split));
            }
            struct.AddRowDatas("Cookie Params", data);

        }

        if (postList.size() > 0) {
            struct.SetExtraInfo("Post Params", "Post参数历史漏洞:");
            struct.AddColumnNames("Post Params", new Object[]{"ID", "漏洞类型", "漏洞标题", "影响参数", "漏洞链接"});
            Object[][] data = new Object[postList.size()][5];
            for (int i = 0; i < postList.size(); i++) {
                Map.Entry<String, Integer> entry = postList.get(i);
                data[i] = concat(new Object[]{String.valueOf(i)}, (Object[]) entry.getKey().split(flag_split));
            }
            struct.AddRowDatas("Post Params", data);

        }
        return struct;
    }

}