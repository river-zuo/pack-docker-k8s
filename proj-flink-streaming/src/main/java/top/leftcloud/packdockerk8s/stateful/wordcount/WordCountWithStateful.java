package top.leftcloud.packdockerk8s.stateful.wordcount;

import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * Flink状态: KeyState、OperateState
 * KeyState只能放在KeyStream后
 *
 */
public class WordCountWithStateful {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> source = env.socketTextStream("127.0.0.1", 18888, "\n");

        source.flatMap((String s, Collector<WordCount> out) -> {
                    String[] s1 = s.split(" ");
                    for (String s2 : s1) {
                        WordCount wordCount = new WordCount();
                        wordCount.setWord(s2);
                        wordCount.setCount(1);
                        out.collect(wordCount);
                    }
                }).returns(WordCount.class)
                .keyBy(WordCount::getWord)
                // 状态计算
                .flatMap(new WordCountStateFunc())
//                .windowAll(TumblingProcessingTimeWindows.of(Time.of(10, TimeUnit.SECONDS)))
                // 打印
                .print();

        env.execute();

    }

}
