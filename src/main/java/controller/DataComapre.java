/*
package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataComapre {

    public static void main(String[] args) {
        String dateStart = "2019-05-14 10:00:00";
        String dateEnd = "2019-05-14 10:21:28";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date date2 = format.parse(dateStart);
            Date date = format.parse(dateEnd);

            //System.out.println("两个日期的差距：" + differentDays(date,date2));
            System.out.println("两个日期的差距：" + getIntevalSecond(dateEnd,dateStart));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    */
/**
     * 求两个时间相差的秒数
     *
     * @param startDate 开始时间("yyyy-MM-dd HH:mm:ss")
     * @param endDate   结束时间("yyyy-MM-dd HH:mm:ss")
     * @return
     * @throws ParseException
     *//*

    public static long getIntevalSecond(String startDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = sdf.parse(startDate);
        Date d2 = sdf.parse(endDate);
        long stsartDate = d1.getTime()/1000;
        long endsDate = d2.getTime()/1000;
        long second = (d2.getTime() - d1.getTime()) / 1000;
        return second;
    }

    */
/**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     *//*

    public static int differentDaysByMillisecond(Date date1, Date date2)
    {        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));        return days;
    }
}
*/
