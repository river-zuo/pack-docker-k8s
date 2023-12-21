package top.leftcloud.packdockerk8s.batchAndStream;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.DynamicProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class WordCountByStreamWithDataStreamApi {

    public static void main(String[] args) throws Exception {


        Configuration conf = new Configuration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
//        DataStreamSource<String> source = env.socketTextStream("your IP", 10086);
//
//        System.out.println("********************************************程序开始执行********************************************");
//
//        source.print();
//
//        env.execute("FirstFlinkApp");

        // 加载上下文环境
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // source 加载数据
        DataStreamSource<String> source = env.socketTextStream(
                "127.0.0.1", 18888, "\n"
        );

        // transform 数据转换
        /**
         * 分隔、标记、分组、聚合
         * 算子: map、flatmap、
         * Tuple
         * map算子: 将输入转换为另一种数据
         * flatMap算子: 将一个输入转换为0-N条s数据输出
         * Tuple内置元组类型
         */

        // 分隔
        DataStream<String> words = source
                .filter(StringUtils::isNotBlank)
                .flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] words = s.split(" ");
                for (String word : words) {
                    collector.collect(word);
                }
            }
        });

        // 标记
        DataStream<Tuple2<String, Integer>> marks = words.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return Tuple2.of(s, 1);
            }
        });

        // 分组
        KeyedStream<Tuple2<String, Integer>, String> group = marks.keyBy(key -> key.f0);


        // 聚合
        SingleOutputStreamOperator<Tuple2<String, Integer>> out1 = group.sum(1);

        out1.print();

        // 全局窗口
        AllWindowedStream<Tuple2<String, Integer>, TimeWindow> secondWindow = group.windowAll(TumblingProcessingTimeWindows.of(Time.of(10, TimeUnit.SECONDS)));
        SingleOutputStreamOperator<Tuple2<String, Integer>> out = secondWindow.process(new ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            @Override
            public void process(ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>.Context context, Iterable<Tuple2<String, Integer>> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                TimeWindow window = context.window();
                long start = window.getStart();
                long end = window.getEnd();
                Integer sum = 0;
                String key = "";
                HashSet<String> keySets = Sets.newHashSet();
                for (Tuple2<String, Integer> element : elements) {
                    sum += element.f1;
                    keySets.add(element.f0);
                }
                String startStr = DateFormatUtils.format(start, "HH:mm:ss.SSS");
                String endStr = DateFormatUtils.format(end, "HH:mm:ss.SSS");
                String keySet = keySets.toString();
                String re = String.format("tumblingWindowAll_[%s]-[%s]_%s", startStr, endStr, keySet);
                Tuple2<String, Integer> of = Tuple2.of(re, sum);
                out.collect(of);
            }
        });
        out.print();

        // keyBy窗口
        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> keyByWindow = group.window(TumblingProcessingTimeWindows.of(Time.of(10, TimeUnit.SECONDS)));
        SingleOutputStreamOperator<Tuple2<String, Integer>> keyByWindowProcess = keyByWindow.process(new ProcessWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>() {
            @Override
            public void process(String s, ProcessWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>.Context context, Iterable<Tuple2<String, Integer>> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                TimeWindow window = context.window();
                long start = window.getStart();
                long end = window.getEnd();
                Integer sum = 0;
                for (Tuple2<String, Integer> element : elements) {
                    sum += element.f1;
                }
                String startStr = DateFormatUtils.format(start, "HH:mm:ss.SSS");
                String endStr = DateFormatUtils.format(end, "HH:mm:ss.SSS");
                String re = String.format("tumbling_[%s]-[%s]_%s", startStr, endStr, s);
                Tuple2<String, Integer> of = Tuple2.of(re, sum);
                out.collect(of);
            }
        });
        keyByWindowProcess.print();

        // session窗口-keyBy
        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> spw = group.window(ProcessingTimeSessionWindows.withGap(Time.of(3, TimeUnit.SECONDS)));
        SingleOutputStreamOperator<Tuple2<String, Integer>> process = spw.process(new ProcessWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>() {
            @Override
            public void process(String s, ProcessWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>.Context context, Iterable<Tuple2<String, Integer>> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                TimeWindow window = context.window();
                long start = window.getStart();
                long end = window.getEnd();
                Integer sum = 0;
                for (Tuple2<String, Integer> element : elements) {
                    sum += element.f1;
                }
                String startStr = DateFormatUtils.format(start, "HH:mm:ss.SSS");
                String endStr = DateFormatUtils.format(end, "HH:mm:ss.SSS");
                String re = String.format("session_[%s]-[%s]_%s", startStr, endStr, s);
                Tuple2<String, Integer> of = Tuple2.of(re, sum);
                out.collect(of);
            }
        });
        process.print();

        // session窗口-全局
        AllWindowedStream<Tuple2<String, Integer>, TimeWindow> sessionAll = group.windowAll(ProcessingTimeSessionWindows.withGap(Time.of(5, TimeUnit.SECONDS)));
        SingleOutputStreamOperator<Tuple2<String, Integer>> sessionWindowAll = sessionAll.process(new ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            @Override
            public void process(ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>.Context context, Iterable<Tuple2<String, Integer>> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                TimeWindow window = context.window();
                long start = window.getStart();
                long end = window.getEnd();
                Integer sum = 0;
                String key = "";
                HashSet<String> keySets = Sets.newHashSet();
                for (Tuple2<String, Integer> element : elements) {
                    sum += element.f1;
                    keySets.add(element.f0);
                }
                String startStr = DateFormatUtils.format(start, "HH:mm:ss.SSS");
                String endStr = DateFormatUtils.format(end, "HH:mm:ss.SSS");
                String keySet = keySets.toString();
                String re = String.format("sessionWindowAll_[%s]-[%s]_%s", startStr, endStr, keySet);
                Tuple2<String, Integer> of = Tuple2.of(re, sum);
                out.collect(of);
            }
        });
        sessionWindowAll.print();

//        group.window()



        env.execute();

    }

    static class MyWindowAssigner<T> extends WindowAssigner<T, TimeWindow> {

        @Override
        public Collection<TimeWindow> assignWindows(T element, long timestamp, WindowAssignerContext context) {
            return null;
        }

        @Override
        public Trigger<T, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
            return new MyCountTrigger<T>();
        }

        @Override
        public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
            return new TimeWindow.Serializer();
        }

        @Override
        public boolean isEventTime() {
            return false;
        }
    }


    static class MyCountTrigger<T> extends Trigger<T, TimeWindow> {

        private final ReducingStateDescriptor<Long> stateDesc =
                new ReducingStateDescriptor<>("count", new MyCountTrigger.Sum(), LongSerializer.INSTANCE);

        private static class Sum implements ReduceFunction<Long> {
            private static final long serialVersionUID = 1L;

            @Override
            public Long reduce(Long value1, Long value2) throws Exception {
                return value1 + value2;
            }
        }

        private int max;

        private MyCountTrigger() {
            max = 3;
        }

        @Override
        public TriggerResult onElement(T element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
            ReducingState<Long> partitionedState = ctx.getPartitionedState(stateDesc);
            partitionedState.add(1L);
            if (partitionedState.get() >= max) {
                partitionedState.clear();
            }
            return TriggerResult.CONTINUE;
        }

        @Override
        public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
            return TriggerResult.CONTINUE;
        }

        @Override
        public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
            return TriggerResult.CONTINUE;
        }

        @Override
        public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
            ctx.getPartitionedState(stateDesc).clear();
        }
    }



}
