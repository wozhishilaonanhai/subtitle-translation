package translation.compression;

import lombok.Data;

/**
 * 一个简单自定义计时时间的类
 */
@Data
public final class Timer {

    int hours;
    int mine;
    int second;
    int ms;

    /**
     *
     * @return 返回秒
     */
    public int toSecond() {
        return second + mine * 60 + hours * 60 * 60;
    }

    /**
     * @return 返回毫秒
     */
    public int toMs() {
        return toSecond() * 1000 + ms;
    }


    private Timer() {
    }

    /**
     * 通过字符串解析
     * @param data 字幕中的时间 HH:MM:SS.SSS
     * @return 返回对象
     */
    public static Timer of(String data) {

        Timer timer = new Timer();
        String[] time = data.split(":");
        timer.hours = Integer.parseInt(time[0].trim());
        timer.mine = Integer.parseInt(time[1].trim());

        String[] secondAndMs = time[2].trim().split("\\.");
        timer.second = Integer.parseInt(secondAndMs[0].trim());
        timer.ms = Integer.parseInt(secondAndMs[1].trim());
        return timer;

    }

    /**
     * 对比两个对象相差的毫秒，顺序无关，返回绝对值
     * @param start 开始
     * @param end 结束
     * @return 相差的毫秒数绝对值
     */
    public static int betweenMs(Timer start, Timer end) {
        return Math.abs(end.toMs() - start.toMs());
    }

    /**
     * 返回两个对象相差后整合的对象
     * @param start 开始
     * @param end 结束
     * @return 对象
     */
    public static Timer between(Timer start, Timer end) {
        return of(Math.abs(end.toMs() - start.toMs()));
    }

    /**
     * 解析毫秒
     * @param _ms 毫秒
     * @return 对象
     */
    public static Timer of(int _ms) {

        int allSecond = _ms / 1000;
        Timer timer = new Timer();
        int innerMs = _ms % 1000;
        int _second = allSecond % 60;
        int _mine = (allSecond / 60) % 60;
        int _hours = (allSecond / (60 * 60)) % 60;
        timer.second = _second;
        timer.mine = _mine;
        timer.hours = _hours;
        timer.ms = innerMs;
        return timer;

    }

    /**
     * 为对象添加毫秒时间 后的对象
     * @param _ms 毫秒
     * @return 对象
     */
    public Timer addMs(int _ms) {

        int myMs = toMs();
        myMs = myMs + _ms;
        return Timer.of(myMs);

    }

    public Timer subMs(int _ms) {

        int myMs = toMs();
        myMs = myMs - _ms;
        assert myMs >= 0;
        return Timer.of(myMs);

    }


    public String toString() {
        return String.format("%02d:%02d:%02d.%03d", hours, mine, second, ms);
    }

    public static void main(String[] args) {
//        10:22:52.373 --> 10:22:55.373
        Timer start = Timer.of("10:22:52.373");
        Timer end = Timer.of("10:22:55.373");
        System.out.println(end);
        System.out.println(Timer.betweenMs(start, end));
        System.out.println(Timer.between(start, end));
    }
}
