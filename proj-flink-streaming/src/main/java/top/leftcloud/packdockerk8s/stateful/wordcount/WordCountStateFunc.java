package top.leftcloud.packdockerk8s.stateful.wordcount;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * 1.继承Rich函数
 * 2.重写Open方法
 * 3.状态计算逻辑s
 *
 * 如果Flink发生异常退出，Checkpoint可以读取保存的状态，进行数据恢复
 *
 *
 */
public class WordCountStateFunc extends RichFlatMapFunction<WordCount, WordCount> {

    /**
     * KeyState的数据类型:
     * ValueState<T>
     * ListState<T>
     *
     */

    private ValueState<WordCount> keyState;

    /**
     * 状态变量初始化
     * @param parameters The configuration containing the parameters attached to the contract.
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<WordCount> valueStateDescriptor = new ValueStateDescriptor<WordCount>(
                // 描述器的名称
                // 描述器的类型
                /**
                 * Flink 有一套数据类型, 包含了Java、Scala所有的类型
                 * TypeInformation
                 * TypeInformation统一了所有类型的序列化实现
                 */
                "wordCountState", TypeInformation.of(WordCount.class)
        );
        keyState = getRuntimeContext().getState(valueStateDescriptor);
    }

    @Override
    public void flatMap(WordCount value, Collector<WordCount> out) throws Exception {
        // 读取状态
        WordCount wordCount = keyState.value();
        // 更新状态
        if (wordCount == null) {
            keyState.update(value);
            out.collect(value);
        } else {
            Integer count = wordCount.getCount() + value.getCount();
            WordCount cc = new WordCount();
            cc.setWord(wordCount.getWord());
            cc.setCount(count);
            keyState.update(cc);
            out.collect(cc);
        }
        // 返回更新后的状态

    }

}
