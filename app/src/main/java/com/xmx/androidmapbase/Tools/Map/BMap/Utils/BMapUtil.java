/**
 *
 */
package com.xmx.androidmapbase.Tools.Map.BMap.Utils;

import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.xmx.androidmapbase.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BMapUtil {
    /**
     * 判断edittext是否null
     */
    public static String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }

    public static Spanned stringToSpan(String src) {
        return src == null ? null : Html.fromHtml(src.replace("\n", "<br />"));
    }

    public static String colorFont(String src, String color) {
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("<font color=").append(color).append(">").append(src)
                .append("</font>");
        return strBuf.toString();
    }

    public static String makeHtmlNewLine() {
        return "<br />";
    }

    public static String makeHtmlSpace(int number) {
        final String space = "&nbsp;";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < number; i++) {
            result.append(space);
        }
        return result.toString();
    }

    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + ChString.Kilometer;
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + ChString.Kilometer;
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + ChString.Meter;
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }

        return dis + ChString.Meter;
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
//	public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
//		return new LatLonPoint(latlon.latitude, latlon.longitude);
//	}

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
//	public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
//		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
//	}

    /**
     * 把集合体的LatLonPoint转化为集合体的LatLng
     */
//	public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
//		ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
//		for (LatLonPoint point : shapes) {
//			LatLng latLngTemp = BMapUtil.convertToLatLng(point);
//			lineShapes.add(latLngTemp);
//		}
//		return lineShapes;
//	}

    /**
     * long类型时间格式化
     */
    public static String convertToTime(long time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return df.format(date);
    }

    public static final String HtmlBlack = "#000000";
    public static final String HtmlGray = "#808080";

    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }

    //路径规划方向指示和图片对应
    public static int getDriveActionID(String actionName) {
        if (actionName == null || actionName.equals("")) {
            return R.drawable.dir3;
        }
        if ("左转".equals(actionName)) {
            return R.drawable.dir2;
        }
        if ("右转".equals(actionName)) {
            return R.drawable.dir1;
        }
        if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName)) {
            return R.drawable.dir6;
        }
        if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName)) {
            return R.drawable.dir5;
        }
        if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName)) {
            return R.drawable.dir7;
        }
        if ("向右后方行驶".equals(actionName)) {
            return R.drawable.dir8;
        }
        if ("直行".equals(actionName)) {
            return R.drawable.dir3;
        }
        if ("减速行驶".equals(actionName)) {
            return R.drawable.dir4;
        }
        return R.drawable.dir3;
    }

    public static int getWalkActionID(String actionName) {
        if (actionName == null || actionName.equals("")) {
            return R.drawable.dir13;
        }
        if ("左转".equals(actionName)) {
            return R.drawable.dir2;
        }
        if ("右转".equals(actionName)) {
            return R.drawable.dir1;
        }
        if ("向左前方".equals(actionName) || "靠左".equals(actionName) || actionName.contains("向左前方")) {
            return R.drawable.dir6;
        }
        if ("向右前方".equals(actionName) || "靠右".equals(actionName) || actionName.contains("向右前方")) {
            return R.drawable.dir5;
        }
        if ("向左后方".equals(actionName) || actionName.contains("向左后方")) {
            return R.drawable.dir7;
        }
        if ("向右后方".equals(actionName) || actionName.contains("向右后方")) {
            return R.drawable.dir8;
        }
        if ("直行".equals(actionName)) {
            return R.drawable.dir3;
        }
        if ("通过人行横道".equals(actionName)) {
            return R.drawable.dir9;
        }
        if ("通过过街天桥".equals(actionName)) {
            return R.drawable.dir11;
        }
        if ("通过地下通道".equals(actionName)) {
            return R.drawable.dir10;
        }

        return R.drawable.dir13;
    }

    public static String getBusPathTitle(MassTransitRouteLine busPath) {
        if (busPath == null) {
            return String.valueOf("");
        }
        List<List<MassTransitRouteLine.TransitStep>> busSteps = busPath.getNewSteps();
        if (busSteps == null) {
            return String.valueOf("");
        }
        List<String> sqe = new ArrayList<>();
        for (List<MassTransitRouteLine.TransitStep> stepGroup : busSteps) {
            List<String> group = new ArrayList<>();
            for (MassTransitRouteLine.TransitStep step : stepGroup) {
                switch (step.getVehileType()) {
                    case ESTEP_WALK:
                        break;
                    case ESTEP_DRIVING:
                        group.add(step.getInstructions());
                        break;
                    case ESTEP_TRAIN:
                        group.add(step.getTrainInfo().getName());
                        break;
                    case ESTEP_PLANE:
                        group.add(step.getPlaneInfo().getName());
                        break;
                    case ESTEP_BUS:
                        group.add(step.getBusInfo().getName());
                        break;
                    case ESTEP_COACH:
                        group.add(step.getCoachInfo().getName());
                        break;
                    default:
                        group.add(step.getInstructions());
                        break;
                }
            }
            if (group.size() > 0) {
                sqe.add(join(group, "|"));
            }
        }
        return join(sqe, " > ");
    }

    public static String getBusPathDes(MassTransitRouteLine busPath) {
        if (busPath == null) {
            return String.valueOf("");
        }
        long second = busPath.getDuration();
        String time = getFriendlyTime((int) second);
        float subDistance = busPath.getDistance();
        String subDis = getFriendlyLength((int) subDistance);
        float walkDistance = getWalkDistance(busPath);
        String walkDis = getFriendlyLength((int) walkDistance);
        return String.valueOf(time + " | " + subDis + " | 步行" + walkDis);
    }

    public static int getWalkDistance(MassTransitRouteLine busPath) {
        int dis = 0;
        for (List<MassTransitRouteLine.TransitStep> stepGroup : busPath.getNewSteps()) {
            MassTransitRouteLine.TransitStep step = stepGroup.get(0);
            if (step.getVehileType()
                    == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_WALK) {
                dis += step.getDistance();
            }
        }
        return dis;
    }

    /**
     * 连接字符串
     *
     * @param items     待连接的字符串列表
     * @param separator 分隔字符串
     * @return 格式化后的文本
     */
    public static String join(List<String> items, String separator) {
        StringBuffer sb = new StringBuffer();
        sb.append(items.get(0));
        for (int i = 1; i < items.size(); ++i) {
            sb.append(separator);
            sb.append(items.get(i));
        }
        return new String(sb);
    }
}
