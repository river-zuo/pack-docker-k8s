package top.leftcloud.packdockerk8s.batchAndStream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class BatchProcess {

    public static void main(String[] args) throws Exception {

        // 加载上下文环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // source 加载数据
        DataSource<String> source = env.fromElements(
                "aa hadoop flink",
                "aa hadoop flink",
                "aa hadoop",
                "aa");



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
        FlatMapOperator<String, String> words = source.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] words = s.split(" ");
                for (String word : words) {
                    collector.collect(word);
                }
            }
        });


        // 标记
        MapOperator<String, Tuple2<String, Integer>> marks = words.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return Tuple2.of(s, 1);
            }
        });

        AggregateOperator<Tuple2<String, Integer>> out = marks.sum(1);

        // 分组
//        KeyedStream<Tuple2<String, Integer>, String> group = marks.keyBy(key -> key.f0);

        // 聚合
//        SingleOutputStreamOperator<Tuple2<String, Integer>> out = group.sum(1);

        out.print();

        env.execute();

    }
}
