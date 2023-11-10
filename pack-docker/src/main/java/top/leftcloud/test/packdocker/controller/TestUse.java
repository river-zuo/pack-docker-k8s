package top.leftcloud.test.packdocker.controller;

public class TestUse {

    public static void main(String[] args) {

        String s = "{\\\"query\\\":{\\\"bool\\\":{\\\"should\\\":[{\\\"range\\\":{\\\"@timestamp\\\":{\\\"gte\\\":\\\"1698394200000\\\",\\\"lte\\\":\\\"1698394260000\\\"}}},{\\\"range\\\":{\\\"lastJudgeStateTime\\\":{\\\"gte\\\":\\\"2023-10-27 16:10:00\\\",\\\"lte\\\":\\\"2023-10-27 16:11:00\\\"}}}]}}}";

        System.out.println(s);
        String replace = s.replace("\\", "");
        System.out.println(replace);

    }
}
